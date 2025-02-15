/*
 * Copyright (c) 2015, Garrett Benoit. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */ 

package swipe;

import keyboard.IKeyboard;
import keyboard.renderables.SwipePoint;
import keyboard.renderables.SwipeTrail;
import keyboard.renderables.VirtualKey;
import keyboard.renderables.VirtualKeyboard;
import utilities.MyUtilities;

import com.leapmotion.leap.Vector;

import enums.Attribute;
import enums.Key;
import enums.Renderable;
import experiment.WordObserver;

public class SwipeKeyboard implements WordObserver {
    private static final float AUTO_REPEAT_DELAY = (750 * 1f/3f) + 250; // Windows default
    private static final int AUTO_REPEAT_RATE = 1000 / 31; // Windows default
    private final float MAX_CLOSE_KEY_DISTANCE;
    private final float MAX_EXPECTED_PRESS_RADIUS;
    private final float MAX_CLOSE_EXPECTED_PRESS_RADIUS;
    private final float MAX_DISTANCE_OFF_KEYBOARD;
    private final SwipePoint swipePoint;
    private final SwipeTrail swipeTrail;
    private final VirtualKeyboard virtualKeyboard;
    private VirtualKey virtualKey;
    private VirtualKey previousKey = null;
    private VirtualKey previousPressed = null;
    private VirtualKey firstKey = null;
    private boolean isPressed;
    private boolean isDown;
    private boolean isBackSpaceDown;
    private boolean isBackSpaceRepeating;
    private boolean isShiftDown;
    private boolean isSpecialDown;
    private boolean isSwiping = false;
    private long previousRepeatTime = 0;
    private long elapsedRepeatTime = 0;
    private String currentWord = null;
    private int currentLetter = 0;
    private Key expectedKey = Key.VK_NULL;
    private Key previousExpectedKey = expectedKey;
    private Key previousPreviousExpectedKey = previousExpectedKey;
    private boolean touchPress;
    private boolean touchDown;
    private boolean touchReleased = false;
    private Vector normalizedPoint = Vector.zero();
    
    // 1) Enter, Period, Comma, Space, Numbers:
    //    	- Must only fire on release if it was the first thing pressed.
    //		- If leave key and start swiping, then we start swiping instead
    //		- Don't detect these at all while swiping regardless of angle or overlap
    // 2) Shift:
    //		- Must only fire on down press.
    //		- If leave key, it is still counted as held down, we ignore swiping
    //		- Don't detect shift at all while swiping regardless of angle or overlap
    // 3) Backspace:
    //		- Must start to fire on down press, it is the only repeating key while held down
    //		- If leave key, it is still counted as held down, we ignore swiping
    //		- Don't detect backspace at all while swiping regardless of angle or overlap
    // 4) Letters:
    //		- Can be detected on key press, angle, and release
    //		- If we are swiping, then we ignore all other non letter keys, consider closest alpha keys to us on angle
    
    public SwipeKeyboard(IKeyboard keyboard) {
        float keyWidth = keyboard.getAttributes().getAttributeAsPoint(Attribute.KEY_SIZE).x;
        MAX_EXPECTED_PRESS_RADIUS = keyWidth * 1.20f; // 64; --- was 1.25 -- 80
        MAX_DISTANCE_OFF_KEYBOARD = keyWidth * 1.5f;
        MAX_CLOSE_EXPECTED_PRESS_RADIUS = keyWidth * 0.75f; // 48;
        virtualKeyboard = (VirtualKeyboard) keyboard.getRenderables().getRenderable(Renderable.VIRTUAL_KEYBOARD);
        MAX_CLOSE_KEY_DISTANCE = MyUtilities.MATH_UTILITILES.findDistanceToPoint(
                virtualKeyboard.getVirtualKey(Key.VK_G).getCenter(),
                virtualKeyboard.getVirtualKey(Key.VK_B).getCenter());
        swipePoint = (SwipePoint) keyboard.getRenderables().getRenderable(Renderable.SWIPE_POINT);
        swipeTrail = (SwipeTrail) keyboard.getRenderables().getRenderable(Renderable.SWIPE_TRAIL);
        
        previousPressed = virtualKeyboard.getVirtualKey(Key.VK_ENTER);
        previousKey = previousPressed;
    }

    public void update(boolean isTouching) {
        normalizedPoint = new Vector(swipePoint.getNormalizedPoint());
        normalizedPoint.setZ(0);
        
        VirtualKey nearestAnyKey = null;
        if(isTouching) {
            nearestAnyKey = virtualKeyboard.getNearestKeyNoEnter(normalizedPoint, MAX_DISTANCE_OFF_KEYBOARD);
        }
        if(nearestAnyKey != null) {
            if(!touchPress && !touchDown) {
                touchPress = true;
                touchDown = true;
            } else if (touchPress) {
                touchPress = false;
            }
        } else {
            if(touchPress || touchDown) {
                touchPress = false;
                touchDown = false;
                touchReleased = true;
            }
        }

        if((virtualKey = virtualKeyboard.isHoveringAny(normalizedPoint)) != null && touchDown) {
            // Have to force down to be false or else we run into problems when the leap is on a slow computer.
            if(previousKey != virtualKey) {
                isDown = false;
                //System.out.println("Turning off isDown " + virtualKey.getKey());
            }
            if(touchPress) {
                firstKey = virtualKey;
            }
            //System.out.println("hovering: " + virtualKey.getKey());
            if(/*isBackSpaceDown || */(virtualKey.getKey() == Key.VK_BACK_SPACE /*&& !isSwiping*/ && !isShiftDown)) {
                virtualKey = virtualKeyboard.getVirtualKey(Key.VK_BACK_SPACE);
                if(!isPressed && !isBackSpaceDown) {
                    isPressed = true;
                    isBackSpaceDown = true;
                    previousRepeatTime = System.currentTimeMillis();
                    elapsedRepeatTime = 0;
                } else {
                    long now = System.currentTimeMillis();
                    elapsedRepeatTime += now - previousRepeatTime;
                    previousRepeatTime = now;
                    
                    if(!isBackSpaceRepeating && elapsedRepeatTime > AUTO_REPEAT_DELAY) {
                        isPressed = true;
                        isBackSpaceRepeating = true;
                        elapsedRepeatTime = 0;
                    } else if(isBackSpaceRepeating && elapsedRepeatTime > AUTO_REPEAT_RATE) {
                        isPressed = true;
                        elapsedRepeatTime = 0;
                    }
                }
            } else if(isShiftDown || (virtualKey.getKey() == Key.VK_SHIFT && !isSwiping && !isBackSpaceDown)) {
                virtualKey = virtualKeyboard.getVirtualKey(Key.VK_SHIFT);
                if(!isPressed && !isShiftDown) {
                    isPressed = true;
                    isShiftDown = true;
                }
            }
            // If we're close enough to our expected key when we detect a touch, then that's good enough even if we missed it.
            if(virtualKey.getKey() != expectedKey && expectedKey != Key.VK_ENTER && expectedKey != Key.VK_BACK_SPACE && !isShiftDown && !isBackSpaceDown) {
                VirtualKey expectedVirtualKey = virtualKeyboard.getVirtualKey(expectedKey);
                if(expectedVirtualKey != null &&
                        MyUtilities.MATH_UTILITILES.findDistanceToPoint(normalizedPoint, expectedVirtualKey.getCenter()) <=
                        (swipeTrail.getPathDistance() <= MAX_CLOSE_KEY_DISTANCE ? MAX_CLOSE_EXPECTED_PRESS_RADIUS : MAX_EXPECTED_PRESS_RADIUS)) {
                    virtualKey = expectedVirtualKey;
                    isDown = false; // set to false since we switch our vKey.
                    //System.out.println("Touch is CLOSE ENOUGH, switch to " + virtualKey.getKey());
                }
            }
            if((virtualKey.getKey().isSpecial() || virtualKey.getKey().isNumeric()) && !isSwiping && !isShiftDown && !isBackSpaceDown && firstKey != null) {
                isSpecialDown = true;
            } else if(!isShiftDown && !isBackSpaceDown) {
                if(isSpecialDown) {
                    virtualKey = virtualKeyboard.getNearestAlphaKey(normalizedPoint);
                    isPressed = true;
                    isSpecialDown = false;
                    isSwiping = true;
                }
                if(!isPressed && !isDown && onExpectedLetterDown(virtualKey.getKey())) {
                    isPressed = true;
                    isDown = true;
                    if(!touchPress) {
                        //swipeTrail.setPressedPoint(virtualKey.getCenter());
                    	swipeTrail.setPressedPoint(normalizedPoint);
                    }
                    isSwiping = true;
                    //System.out.println("pressed: touching IS expected key " + virtualKey.getKey());
                } else if(!isPressed && !isDown && touchPress && !onExpectedLetterDown(virtualKey.getKey())) {
                    isPressed = true;
                    isDown = true;
                    isSwiping = true;
                    //System.out.println("pressed: touching NOT expected key " + virtualKey.getKey());
                } else if(!isPressed && !isDown && (onPreviousExpectedLetterDown(virtualKey.getKey()) || virtualKey != previousKey)) {
                    handleTrailPressDetection();
                } else if(swipeTrail.detectPressed()) {
                    isDown = false;
                    //System.out.println("not pressed: detected new swipe trail press " + virtualKey.getKey());
                    handleTrailPressDetection();
                } else {
                    //System.out.println("not pressed: " + virtualKey.getKey() + " Down: " + isDown + " Pressed: " + isPressed);
                }
            }
            previousKey = virtualKey;
            if(previousKey != firstKey) {
                firstKey = null;
            }
            virtualKey.pressed();
        } else if(touchDown) {
            if(isBackSpaceDown) {
                /*virtualKey = virtualKeyboard.getVirtualKey(Key.VK_BACK_SPACE);
                long now = System.currentTimeMillis();
                elapsedRepeatTime += now - previousRepeatTime;
                previousRepeatTime = now;
                
                if(!isBackSpaceRepeating && elapsedRepeatTime > AUTO_REPEAT_DELAY) {
                    isPressed = true;
                    isBackSpaceRepeating = true;
                    elapsedRepeatTime = 0;
                } else if(isBackSpaceRepeating && elapsedRepeatTime > AUTO_REPEAT_RATE) {
                    isPressed = true;
                    elapsedRepeatTime = 0;
                }
                virtualKey.pressed();*/
                isBackSpaceDown = false;
                isBackSpaceRepeating = false;
            } else if(isShiftDown) {
                virtualKey = virtualKeyboard.getVirtualKey(Key.VK_SHIFT);
                virtualKey.pressed();
            } /*else if(isSwiping && swipeTrail.detectPressed()) {
                handleTrailPressDetection();
                previousKey = virtualKey;
                if(previousKey != firstKey) {
                    firstKey = null;
                }
                if(virtualKey != null) {
                    virtualKey.pressed();
                }
            }*/ else {
                VirtualKey nearestKey;
                if((nearestKey = virtualKeyboard.getNearestAlphaKey(normalizedPoint, MAX_DISTANCE_OFF_KEYBOARD)) != null) {
                    virtualKey = nearestKey;
                    // If we're close enough to our expected key when we detect a touch, then that's good enough even if we missed it.
                    if(virtualKey.getKey() != expectedKey && expectedKey != Key.VK_ENTER && expectedKey != Key.VK_BACK_SPACE && !isShiftDown && !isBackSpaceDown) {
                        VirtualKey expectedVirtualKey = virtualKeyboard.getVirtualKey(expectedKey);
                        if(expectedVirtualKey != null &&
                                MyUtilities.MATH_UTILITILES.findDistanceToPoint(normalizedPoint, expectedVirtualKey.getCenter()) <=
                                (swipeTrail.getPathDistance() <= MAX_CLOSE_KEY_DISTANCE ? MAX_CLOSE_EXPECTED_PRESS_RADIUS : MAX_EXPECTED_PRESS_RADIUS)) {
                            virtualKey = expectedVirtualKey;
                            isDown = false; // set to false since we switch our vKey.
                            //System.out.println("Touch is CLOSE ENOUGH, switch to " + virtualKey.getKey());
                        }
                    }
                    if(!isShiftDown && !isBackSpaceDown) {
                        if(!isPressed && !isDown && onExpectedLetterDown(virtualKey.getKey())) {
                            isPressed = true;
                            isDown = true;
                            if(!touchPress) {
                                //swipeTrail.setPressedPoint(virtualKey.getCenter());
                                swipeTrail.setPressedPoint(normalizedPoint);
                            }
                            isSwiping = true;
                            //System.out.println("pressed: touching IS expected key " + virtualKey.getKey());
                        } else if(!isPressed && !isDown && touchPress && !onExpectedLetterDown(virtualKey.getKey())) {
                            isPressed = true;
                            isDown = true;
                            isSwiping = true;
                            //System.out.println("pressed: touching NOT expected key " + virtualKey.getKey());
                        } else if(!isPressed && !isDown && (onPreviousExpectedLetterDown(virtualKey.getKey()) || virtualKey != previousKey)) {
                            handleTrailPressDetection();
                        } else if(swipeTrail.detectPressed()) {
                            isDown = false;
                            //System.out.println("not pressed: detected new swipe trail press " + virtualKey.getKey());
                            handleTrailPressDetection();
                        } else {
                            //System.out.println("not pressed: " + virtualKey.getKey() + " Down: " + isDown + " Pressed: " + isPressed);
                        }
                    }
                    previousKey = virtualKey;
                    if(previousKey != firstKey) {
                        firstKey = null;
                    }
                    if(virtualKey != null) {
                        virtualKey.pressed();
                    }
                }
            }
        } else {
            if(isPressed || isDown) {
                if(!onExpectedLetterRelease()) {
                    // If we're close enough to our previous expected key when we detect a key release, then we shouldn't count it.
                    VirtualKey previousExpectedVirtualKey = virtualKeyboard.getVirtualKey(previousExpectedKey);
                    if(previousExpectedVirtualKey != null && expectedKey != Key.VK_BACK_SPACE /*&& expectedKey != Key.VK_ENTER*/ &&
                            MyUtilities.MATH_UTILITILES.findDistanceToPoint(normalizedPoint, previousExpectedVirtualKey.getCenter()) <= MAX_EXPECTED_PRESS_RADIUS) {
                        isPressed = false;
                        //System.out.println("not pressed: ignoring prev key release " + previousKey.getKey());
                    } else if((previousExpectedVirtualKey = virtualKeyboard.getVirtualKey(previousPreviousExpectedKey)) != null && expectedKey != Key.VK_BACK_SPACE &&
                            MyUtilities.MATH_UTILITILES.findDistanceToPoint(normalizedPoint, previousExpectedVirtualKey.getCenter()) <= MAX_EXPECTED_PRESS_RADIUS) {
                        isPressed = false;
                        //System.out.println("not pressed: ignoring prev x 2 key release " + previousKey.getKey());
                    } else if(/*previousKey != null &&*/ (previousKey.getKey().isSpecial() || previousKey.getKey().isNumeric())) {
                        if(isSwiping) {
                            previousKey = virtualKeyboard.getNearestAlphaKey(normalizedPoint);
                            if(previousKey != previousPressed) {
                                isPressed = true;
                                //System.out.println("pressed: " + previousKey.getKey());
                            } else {
                                isPressed = false;
                                //System.out.println("didnt press: " + previousKey.getKey());
                            }
                        } else {
                            //System.out.println("No pressed dected because we didn't start on the keyboard");
                            isPressed = false;
                        }
                    } else {
                        //if(previousKey != null) {
                            VirtualKey nearestKey;
                            if((nearestKey = virtualKeyboard.getNearestAlphaKey(normalizedPoint, MAX_DISTANCE_OFF_KEYBOARD)) != null) {
                                previousKey = nearestKey;
                            }
                        //}
                        isPressed = true;
                        //System.out.println("pressed: not ignoring key release " + previousKey.getKey());
                    }
                    isDown = false;
                } else {
                    isPressed = false;
                    isDown = false;
                    //System.out.println("For some reason ending up here");
                }
            }
            if(isSpecialDown) {
                isPressed = true;
                isSpecialDown = false;
            }
            if(isBackSpaceDown) {
                isPressed = false;
                isBackSpaceDown = false;
                isBackSpaceRepeating = false;
            }
            if(isShiftDown) {
                isPressed = false;
                isShiftDown = false;
            }
            isSwiping = false;
        }
    }
    
    private void handleTrailPressDetection() {
        //System.out.print("CHECK angle is pressed ");
        Vector pressedPoint = swipeTrail.isPressed();
        //System.out.println("-- point: " + pressedPoint);
        // If we're close enough to our previous expected key when we detect an angle press, then we shouldn't count it.
        VirtualKey previousExpectedVirtualKey = virtualKeyboard.getVirtualKey(previousExpectedKey);
        if(!pressedPoint.equals(Vector.zero()) && previousExpectedVirtualKey != null && expectedKey != Key.VK_BACK_SPACE /*&& expectedKey != Key.VK_ENTER*/ &&
                MyUtilities.MATH_UTILITILES.findDistanceToPoint(normalizedPoint, previousExpectedVirtualKey.getCenter()) <= MAX_EXPECTED_PRESS_RADIUS) {
            pressedPoint = Vector.zero();
            //System.out.println("Angle press TOO CLOSE to prev expected - ignore it");
        } else if(!pressedPoint.equals(Vector.zero()) && (previousExpectedVirtualKey = virtualKeyboard.getVirtualKey(previousPreviousExpectedKey)) != null &&
                expectedKey != Key.VK_BACK_SPACE && MyUtilities.MATH_UTILITILES.findDistanceToPoint(normalizedPoint, previousExpectedVirtualKey.getCenter()) <= MAX_EXPECTED_PRESS_RADIUS) {
            pressedPoint = Vector.zero();
            //System.out.println("Angle press TOO CLOSE to prev x 2 expected - ignore it");
        }
        if(!pressedPoint.equals(Vector.zero())) {
            if(virtualKey != null && virtualKey.isHovering(pressedPoint)) {
                if(virtualKey.getKey().isSpecial() || virtualKey.getKey().isNumeric()) {
                    virtualKey = virtualKeyboard.getNearestAlphaKey(normalizedPoint);
                }
                isPressed = true;
                isDown = true;
                //System.out.println("pressed: angle press curr " + virtualKey.getKey());
            } else if(expectedKey != Key.VK_ENTER && !previousPressed.isHovering(pressedPoint)){ // checking if enter helps us avoid false angle
                VirtualKey nearestKey;
                if((nearestKey = virtualKeyboard.getNearestAlphaKey(normalizedPoint, MAX_DISTANCE_OFF_KEYBOARD)) != null && nearestKey != previousPressed) {
                    virtualKey = nearestKey;
                    isPressed = true;
                    isDown = true;
                    //System.out.println("pressed: angle press prev " + virtualKey.getKey());
                } else {
                    // System.out.println("angle too far from keyboard to press");
                }
            } else {
                //System.out.println("not pressed: ignore the angle press " + virtualKey.getKey());
            }
        } else {
            // We set down here because we are touching but we detected nothing else.
            isDown = true;
            //System.out.println("not pressed: set abitrary DOWN + " + virtualKey.getKey());
        }
    }
    
    public Key isPressed() {
        try {
            if(isPressed) {
                previousPressed = previousKey;
                return previousKey.getKey();
            } else {
                return Key.VK_NULL;
            }
        } finally {
            // Consume the pressed key event.
            isPressed = false;
        }
    }
    
    public boolean isTouchReleased() {
        try {
            return touchReleased;
        } finally {
            // Consume the released touch event.
            touchReleased = false;
        }
    }
    
    private boolean onExpectedLetterDown(Key key) {
        if(touchDown) {
            return key == expectedKey && !key.isSpecial() && !key.isNumeric();
        } else {
            return false;
        }
    }
    
    private boolean onPreviousExpectedLetterDown(Key key) {
        if(touchDown) {
            return key == previousExpectedKey  && !key.isSpecial() && !key.isNumeric();
        } else {
            return false;
        }
    }
    
    private boolean onExpectedLetterRelease() {
        if(!touchDown) {
            if(firstKey != null) {
                return previousKey == firstKey;
            } else {
                return previousKey.getKey() == previousExpectedKey && expectedKey!= Key.VK_BACK_SPACE &&
                        !previousKey.getKey().isSpecial() && !previousKey.getKey().isNumeric();
            }
        } else {
            return true;
        }
    }
    
    @Override
    public void matchEventObserved() {
        // Do nothing
    }
    
    @Override
    public void wordSetEventObserved(String word) {
        currentWord = word;
        currentLetter = 0;
        expectedKey = Key.getByValue(currentWord.charAt(currentLetter));
    }

    @Override
    public void currentLetterIndexChangedEventObservered(int letterIndex, Key key) {
        currentLetter = letterIndex;
        previousPreviousExpectedKey = previousExpectedKey;
        previousExpectedKey = expectedKey;
        expectedKey = key;

        VirtualKey previousExpectedVirtualKey = virtualKeyboard.getVirtualKey(previousExpectedKey);
        VirtualKey previousPreviousExpectedVirtualKey = virtualKeyboard.getVirtualKey(previousPreviousExpectedKey);
        if(previousExpectedVirtualKey != null && previousPreviousExpectedVirtualKey != null && 
                MyUtilities.MATH_UTILITILES.findDistanceToPoint(previousExpectedVirtualKey.getCenter(), previousPreviousExpectedVirtualKey.getCenter()) > MAX_CLOSE_KEY_DISTANCE) {
            previousPreviousExpectedKey = previousExpectedKey;
        }
        
        // This will allow us to deal with double letters without gestures. - No delay in detection but gives recursive call, too much work to fix.
        /*if(currentLetter > 0 && previousExpectedKey == expectedKey && previousExpectedKey != Key.VK_BACK_SPACE && previousExpectedKey != Key.VK_ENTER && previousKey.getKey() != Key.VK_BACK_SPACE) {
            if(shiftOnce) {
                keyPressed = expectedKey.toUpper();
                shiftOnce = shiftTwice;
                if(!shiftTwice) {
                    keyboardRenderables.swapToLowerCaseKeyboard();
                }
            } else {
                keyPressed = expectedKey.getValue();   
            }
            notifyListenersKeyEvent(); // This causes a recursive call and messes up the rendering.
        }*/
        
        // This will allow us to deal with double letters without gestures. - Small delay in detection.
        if(previousExpectedKey == expectedKey) {
            //isDown = false;
            if(currentLetter > 0) {
                previousExpectedKey = previousPreviousExpectedKey;
            }
        }
        isDown = false;
        // Find vector from this expected key to previous expected key
        if(expectedKey == Key.VK_ENTER || expectedKey == Key.VK_BACK_SPACE) {
            swipeTrail.setExpectedPath(Vector.zero(), Vector.zero());
        } else {
            // Use this path to lessen the risk of accidental presses (false positives) when traveling to next key.
            swipeTrail.setExpectedPath(virtualKeyboard.getVirtualKey(previousExpectedKey).getCenter(), virtualKeyboard.getVirtualKey(expectedKey).getCenter());
        }
    }
}

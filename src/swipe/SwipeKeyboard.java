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
    private final int MAX_EXPECTED_PRESS_RADIUS;
    private final int MAX_CLOSE_EXPECTED_PRESS_RADIUS;
    private final SwipePoint swipePoint;
    private final SwipeTrail swipeTrail;
    private final VirtualKeyboard virtualKeyboard;
    private VirtualKey virtualKey;
    private VirtualKey previousKey = null;
    private VirtualKey previousPressed = null;
    private VirtualKey firstKey = null;
    private boolean isPressed;
    private boolean isDown;
    private boolean isBackSpacePressed;
    private boolean isBackSpaceDown;
    private boolean isBackSpaceRepeating;
    private long previousRepeatTime = 0;
    private long elapsedRepeatTime = 0;
    private String currentWord = null;
    private int currentLetter = 0;
    private Key expectedKey = Key.VK_NULL;
    private Key previousExpectedKey = expectedKey;
    private Key previousPreviousExpectedKey = previousExpectedKey;
    private boolean touchPress;
    private boolean touchDown;
    
    public SwipeKeyboard(IKeyboard keyboard) {
        int keyWidth = keyboard.getAttributes().getAttributeAsPoint(Attribute.KEY_SIZE).x;
        MAX_EXPECTED_PRESS_RADIUS = (int) (keyWidth * 1.25f); // 80;
        MAX_CLOSE_EXPECTED_PRESS_RADIUS = (int) (keyWidth * 0.75f); // 48;
        virtualKeyboard = (VirtualKeyboard) keyboard.getRenderables().getRenderable(Renderable.VIRTUAL_KEYBOARD);
        MAX_CLOSE_KEY_DISTANCE = MyUtilities.MATH_UTILITILES.findDistanceToPoint(
                virtualKeyboard.getVirtualKey(Key.VK_G).getCenter(),
                virtualKeyboard.getVirtualKey(Key.VK_B).getCenter());
        swipePoint = (SwipePoint) keyboard.getRenderables().getRenderable(Renderable.SWIPE_POINT);
        swipeTrail = (SwipeTrail) keyboard.getRenderables().getRenderable(Renderable.SWIPE_TRAIL);
    }
    
    public void update(boolean isTouching) {
        if(isTouching) {
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
            }
        }

        if((virtualKey = virtualKeyboard.isHoveringAny(swipePoint.getNormalizedPoint())) != null && touchDown) {
            // Have to force down to be false or else we run into problems when the leap is on a slow computer.
            if(previousKey != virtualKey) {
                isDown = false;
                //System.out.println("Turning off isDown " + virtualKey.getKey());
            }
            virtualKey.pressed();
            if(touchPress) {
                firstKey = virtualKey;
            }
            //System.out.println("hovering: " + virtualKey.getKey());
            if(virtualKey.getKey() == Key.VK_BACK_SPACE) {
                if(!isBackSpacePressed && !isBackSpaceDown) {
                    isBackSpacePressed = true;
                    isBackSpaceDown = true;
                    previousRepeatTime = System.currentTimeMillis();
                    elapsedRepeatTime = 0;
                } else {
                    long now = System.currentTimeMillis();
                    elapsedRepeatTime += now - previousRepeatTime;
                    previousRepeatTime = now;
                    
                    if(!isBackSpaceRepeating && elapsedRepeatTime > AUTO_REPEAT_DELAY) {
                        isBackSpacePressed = true;
                        isBackSpaceRepeating = true;
                        elapsedRepeatTime = 0;
                    } else if(isBackSpaceRepeating && elapsedRepeatTime > AUTO_REPEAT_RATE) {
                        isBackSpacePressed = true;
                        elapsedRepeatTime = 0;
                    }
                }
            } else {
                // If we're close enough to our expected key when we detect a touch, then that's good enough even if we missed it.
                if(virtualKey.getKey() != expectedKey && expectedKey != Key.VK_ENTER && expectedKey != Key.VK_BACK_SPACE) {
                    VirtualKey expectedVirtualKey = virtualKeyboard.getVirtualKey(expectedKey);
                    if(expectedVirtualKey != null &&
                            MyUtilities.MATH_UTILITILES.findDistanceToPoint(swipePoint.getNormalizedPoint(), expectedVirtualKey.getCenter()) <=
                            (swipeTrail.getPathDistance() <= MAX_CLOSE_KEY_DISTANCE ? MAX_CLOSE_EXPECTED_PRESS_RADIUS : MAX_EXPECTED_PRESS_RADIUS)) {
                        virtualKey = expectedVirtualKey;
                        isDown = false; // set to false since we switch our vKey.
                        //System.out.println("Touch is CLOSE ENOUGH, switch to " + virtualKey.getKey());
                    }
                }
                if(!isPressed && !isDown && onExpectedLetterDown(virtualKey.getKey())) {
                    isPressed = true;
                    isDown = true;
                    if(!touchPress) {
                        swipeTrail.setPressedPoint(virtualKey.getCenter());
                    }
                    //System.out.println("pressed: touching IS expected key " + virtualKey.getKey());
                } else if(!isPressed && !isDown && touchPress && !onExpectedLetterDown(virtualKey.getKey())) {
                    isPressed = true;
                    isDown = true;
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
        } else {
            // TODO: The 'l' issue
            // For the leap only, when done pressing any key, the key just above it sometimes gets accidently hit because
            // of the natural motion of pulling away from a surface, as well as dealing with the fact that our touch plane
            // has a specific threshold (may be corrected with improved transformation matrix of leap plane or further calibration
            // of plane flatness based on trying to press corner buttons.
            if(isPressed || isDown) {
                if(!onExpectedLetterRelease()) {
                    // If we're close enough to our previous expected key when we detect a key release, then we shouldn't count it.
                    VirtualKey previousExpectedVirtualKey = virtualKeyboard.getVirtualKey(previousExpectedKey);
                    if(previousExpectedVirtualKey != null && expectedKey != Key.VK_BACK_SPACE /*&& expectedKey != Key.VK_ENTER*/ &&
                            MyUtilities.MATH_UTILITILES.findDistanceToPoint(swipePoint.getNormalizedPoint(), previousExpectedVirtualKey.getCenter()) <= MAX_EXPECTED_PRESS_RADIUS) {
                        isPressed = false;
                        //System.out.println("not pressed: ignoring prev key release " + previousKey.getKey());
                    } else if((previousExpectedVirtualKey = virtualKeyboard.getVirtualKey(previousPreviousExpectedKey)) != null && expectedKey != Key.VK_BACK_SPACE &&
                            MyUtilities.MATH_UTILITILES.findDistanceToPoint(swipePoint.getNormalizedPoint(), previousExpectedVirtualKey.getCenter()) <= MAX_EXPECTED_PRESS_RADIUS) {
                        isPressed = false;
                        //System.out.println("not pressed: ignoring prev x 2 key release " + previousKey.getKey());
                    } else {
                        isPressed = true;
                        //System.out.println("pressed: not ignoring key release " + previousKey.getKey());
                    }
                    isDown = false;
                } else {
                    isPressed = false;
                    isDown = false;
                }
            } else if(isBackSpacePressed || isBackSpaceDown) {
                isBackSpacePressed = false;
                isBackSpaceDown = false;
                isBackSpaceRepeating = false;
            }
        }
    }
    
    private void handleTrailPressDetection() {
        //System.out.print("CHECK angle is pressed ");
        Vector pressedPoint = swipeTrail.isPressed();
        //System.out.println("-- point: " + pressedPoint);
        // If we're close enough to our previous expected key when we detect an angle press, then we shouldn't count it.
        VirtualKey previousExpectedVirtualKey = virtualKeyboard.getVirtualKey(previousExpectedKey);
        if(!pressedPoint.equals(Vector.zero()) && previousExpectedVirtualKey != null && expectedKey != Key.VK_BACK_SPACE /*&& expectedKey != Key.VK_ENTER*/ &&
                MyUtilities.MATH_UTILITILES.findDistanceToPoint(swipePoint.getNormalizedPoint(), previousExpectedVirtualKey.getCenter()) <= MAX_EXPECTED_PRESS_RADIUS) {
            pressedPoint = Vector.zero();
            //System.out.println("Angle press TOO CLOSE to prev expected - ignore it");
        } else if(!pressedPoint.equals(Vector.zero()) && (previousExpectedVirtualKey = virtualKeyboard.getVirtualKey(previousPreviousExpectedKey)) != null &&
                expectedKey != Key.VK_BACK_SPACE && MyUtilities.MATH_UTILITILES.findDistanceToPoint(swipePoint.getNormalizedPoint(), previousExpectedVirtualKey.getCenter()) <= MAX_EXPECTED_PRESS_RADIUS) {
            pressedPoint = Vector.zero();
            //System.out.println("Angle press TOO CLOSE to prev x 2 expected - ignore it");
        }
        if(!pressedPoint.equals(Vector.zero())) {
            if(virtualKey.isHovering(pressedPoint)) {
                isPressed = true;
                isDown = true;
                //System.out.println("pressed: angle press curr " + virtualKey.getKey());
            } else if(expectedKey != Key.VK_ENTER && !previousPressed.isHovering(pressedPoint)){ // checking if enter helps us avoid false angle
                virtualKey = virtualKeyboard.getNearestKey(pressedPoint);
                virtualKey.pressed();
                isPressed = true;
                isDown = true;
                //System.out.println("pressed: angle press prev " + virtualKey.getKey());
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
            if(isPressed || isBackSpacePressed) {
                previousPressed = previousKey;
                return previousKey.getKey();
            } else {
                return Key.VK_NULL;
            }
        } finally {
            // Consume the pressed key event.
            isPressed = false;
            isBackSpacePressed = false;
        }
    }
    
    private boolean onExpectedLetterDown(Key key) {
        if(touchDown) {
            return key == expectedKey;
        } else {
            return false;
        }
    }
    
    private boolean onPreviousExpectedLetterDown(Key key) {
        if(touchDown) {
            return key == previousExpectedKey;
        } else {
            return false;
        }
    }
    
    private boolean onExpectedLetterRelease() {
        if(!touchDown) {
            if(firstKey != null) {
                return previousKey == firstKey;
            } else {
                return previousKey.getKey() == previousExpectedKey;
            }
        } else {
            return true;
        }
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

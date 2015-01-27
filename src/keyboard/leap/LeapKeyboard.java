package keyboard.leap;

import utilities.Point;

import java.util.concurrent.locks.ReentrantLock;

import javax.media.opengl.GL2;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.JPanel;

import utilities.MyUtilities;

import com.leapmotion.leap.InteractionBox;
import com.leapmotion.leap.Vector;

import enums.Attribute;
import enums.FileExt;
import enums.FileName;
import enums.FilePath;
import enums.Gesture;
import enums.Key;
import enums.Renderable;
import enums.Setting;
import experiment.WordManager;
import experiment.WordObserver;
import keyboard.CalibrationObserver;
import keyboard.IKeyboard;
import keyboard.KeyboardGesture;
import keyboard.KeyboardSetting;
import keyboard.renderables.KeyboardGestures;
import keyboard.renderables.LeapPlane;
import keyboard.renderables.LeapPoint;
import keyboard.renderables.LeapTool;
import keyboard.renderables.SwipeTrail;
import keyboard.renderables.VirtualKey;
import keyboard.renderables.VirtualKeyboard;
import leap.LeapData;
import leap.LeapListener;
import leap.LeapObserver;

public class LeapKeyboard extends IKeyboard implements LeapObserver, CalibrationObserver {
    public final int KEYBOARD_ID;
    private final String KEYBOARD_NAME;
    private final String KEYBOARD_FILE_NAME;
    private final ReentrantLock LEAP_LOCK = new ReentrantLock();
    private final float CAMERA_DISTANCE;
    private LeapData leapData;
    private LeapTool leapTool;
    private LeapPoint leapPoint;
    private LeapPlane leapPlane;
    private SwipeTrail swipeTrail;
    private LeapGestures leapGestures;
    private KeyboardGestures keyboardGestures;
    private SwipeKeyboard swipeKeyboard;
    private VirtualKeyboard virtualKeyboard;
    private boolean isCalibrated = false;
    private boolean shiftOnce = false;
    private boolean shiftTwice = false;
    
    public LeapKeyboard(boolean air) {
        super(air ? 2 : 1, air ? "Leap Air Keyboard" : "Leap Surface Keyboard", air ?  FileName.LEAP_AIR.getName() : FileName.LEAP_SURFACE.getName());
        KEYBOARD_ID = air ? 2 : 1;
        KEYBOARD_NAME = air ? "Leap Air Keyboard" : "Leap Surface Keyboard";
        KEYBOARD_FILE_NAME = air ?  FileName.LEAP_AIR.getName() : FileName.LEAP_SURFACE.getName();
        System.out.println(KEYBOARD_NAME + " - Loading Settings from " + FilePath.CONFIG.getPath() + KEYBOARD_FILE_NAME + FileExt.INI.getExt());
        keyboardAttributes = new LeapAttributes(this);
        keyboardSettings = new LeapSettings(this);
        System.out.println("-------------------------------------------------------");
        keyboardRenderables = new LeapRenderables(this, air);
        keyboardSize = keyboardAttributes.getAttributeAsPoint(Attribute.KEYBOARD_SIZE);
        int borderSize = keyboardAttributes.getAttributeAsInteger(Attribute.BORDER_SIZE) * 2;
        imageSize = new Point(keyboardSize.x + borderSize, keyboardSize.y + borderSize);
        CAMERA_DISTANCE = keyboardAttributes.getAttributeAsFloat(Attribute.CAMERA_DISTANCE);
        virtualKeyboard = (VirtualKeyboard) keyboardRenderables.getRenderable(Renderable.VIRTUAL_KEYS);
        leapPoint = (LeapPoint) keyboardRenderables.getRenderable(Renderable.LEAP_POINT);
        leapTool = (LeapTool) keyboardRenderables.getRenderable(Renderable.LEAP_TOOL);
        if(Gesture.ENABLED) {
            keyboardGestures = (KeyboardGestures) keyboardRenderables.getRenderable(Renderable.KEYBOARD_GESTURES);
        }
        swipeTrail = (SwipeTrail) keyboardRenderables.getRenderable(Renderable.SWIPE_TRAIL);
        leapPlane = (LeapPlane) keyboardRenderables.getRenderable(Renderable.LEAP_PLANE);
        leapPlane.registerObserver(this);
        if(!leapPlane.isCalibrated()) {
            leapPoint.blockAccess(true);
            leapTool.blockAccess(true);
            if(Gesture.ENABLED) {
                keyboardGestures.blockAccess(true);
            }
        } else {
            isCalibrated = true;
        }
        if(Gesture.ENABLED) {
            leapGestures = new LeapGestures();
        }
        swipeKeyboard = new SwipeKeyboard();
    }
    
    @Override
    public void render(GL2 gl) {
        MyUtilities.OPEN_GL_UTILITIES.switchToPerspective(gl, this, true);
        gl.glPushMatrix();
        gl.glTranslatef(-imageSize.x/2f, -imageSize.y/2f, -CAMERA_DISTANCE);
        keyboardRenderables.render(gl);
        gl.glPopMatrix();
    }
    
    @Override
    public void update() {
        LEAP_LOCK.lock();
        /*System.out.print("System time: " + System.currentTimeMillis());
        if(leapData != null) {
            System.out.print(" Leap time: " + leapData.getTimeStamp());
        }
        System.out.println(" Local time: " + LocalTime.now().getNano());*/
        try {
            if(Gesture.ENABLED) {
                // Allow leap plane to take over the updates of specific objects that require the plane
                leapPlane.update(leapPoint, leapTool, keyboardGestures, swipeTrail);
                // Update gestures after plane, we need both normalized and non normalized points.
                leapGestures.update();
            } else {
             // Allow leap plane to take over the updates of specific objects that require the plane
                leapPlane.update(leapPoint, leapTool, null, swipeTrail);
            }
            if(leapTool.isValid()) {
                Key key;
                swipeKeyboard.update();
                if((key = swipeKeyboard.isPressed()) != Key.VK_NULL) {
                    if(key != Key.VK_SHIFT) {
                        if(shiftOnce) {
                            keyPressed = key.toUpper();
                            shiftOnce = shiftTwice;
                            if(!shiftTwice) {
                                keyboardRenderables.swapToLowerCaseKeyboard();
                            }
                        } else {
                            keyPressed = key.getValue();   
                        }
                        notifyListenersKeyEvent();
                    } else if(!shiftOnce && !shiftTwice) {
                        shiftOnce = true;
                        keyboardRenderables.swapToUpperCaseKeyboard();
                    } else if(shiftOnce && !shiftTwice) {
                        shiftTwice = true;
                    } else {
                        shiftTwice = false;
                        shiftOnce = false;
                        keyboardRenderables.swapToLowerCaseKeyboard();
                    }
                }
                if(shiftTwice) {
                    virtualKeyboard.locked(Key.VK_SHIFT);
                } else if(shiftOnce) {
                    virtualKeyboard.pressed(Key.VK_SHIFT);
                }
            }/* else {
                virtualKeyboard.clearAll();
            }*/
        } finally {
            LEAP_LOCK.unlock();
        }
    }
    
    @Override
    public void addToUI(JPanel panel, GLCanvas canvas) {
        LeapListener.registerObserver(this);
        LeapListener.startListening();
        WordManager.registerObserver(swipeKeyboard);
    }

    @Override
    public void removeFromUI(JPanel panel, GLCanvas canvas) {
        LeapListener.stopListening();
        LeapListener.removeObserver(this);
        leapTool.deleteQuadric();
        if(Gesture.ENABLED) {
            keyboardGestures.deleteQuadric();
        }
        WordManager.removeObserver(swipeKeyboard);
    }

    @Override
    public void beginCalibration(JPanel textPanel) {
        leapPoint.blockAccess(true);
        leapTool.blockAccess(true);
        if(Gesture.ENABLED) {
            keyboardGestures.blockAccess(true);
        }
        virtualKeyboard.blockAccess(true);
        keyboardRenderables.getRenderable(Renderable.KEYBOARD_IMAGE).blockAccess(true);
        leapPlane.beginCalibration(textPanel);
    }

    @Override
    protected void finishCalibration() {
        leapPoint.grantAccess(true);
        leapTool.grantAccess(true);
        if(Gesture.ENABLED) {
            keyboardGestures.grantAccess(true);
        }
        virtualKeyboard.grantAccess(true);
        keyboardRenderables.getRenderable(Renderable.KEYBOARD_IMAGE).grantAccess(true);
        isCalibrated = true;
        notifyListenersCalibrationFinished();
    }
    
    @Override
    public boolean isCalibrated() {
        return isCalibrated;
    }
    
    @Override
    public void leapEventObserved(LeapData leapData) {
        LEAP_LOCK.lock();
        try {
            this.leapData = leapData;
            this.leapData.populateData(leapPoint, leapTool);
        } finally {
            LEAP_LOCK.unlock();
        }
    }

    @Override
    public void leapInteractionBoxSet(InteractionBox iBox) {
        leapPoint.setInteractionBox(iBox);
        leapPlane.setInteractionBox(iBox);
    }

    @Override
    public void keyboardCalibrationFinishedEventObserved() {
        finishCalibration();
    }
    
    private class LeapGestures {
        private final KeyboardSetting GESTURE_SWIPE_MIN_LENGTH;
        private final KeyboardSetting GESTURE_SWIPE_MIN_VELOCITY;
        private boolean detectingSwipeGesture = false;
        private KeyboardGesture gesture;
        
        public LeapGestures() {
            if(Gesture.ENABLED) {
                GESTURE_SWIPE_MIN_LENGTH = keyboardSettings.getSetting(Setting.GESTURE_SWIPE_MIN_LENGTH);
                GESTURE_SWIPE_MIN_VELOCITY = keyboardSettings.getSetting(Setting.GESTURE_SWIPE_MIN_VELOCITY);
            } else {
                GESTURE_SWIPE_MIN_LENGTH = null;
                GESTURE_SWIPE_MIN_VELOCITY = null;
            }
        }
        
        public void update() {
            if(Gesture.ENABLED) {
                // Remove completed gestures.
                keyboardGestures.removeFinishedGestures();
                
                // We have already detected a swipe that was long enough.
                if(detectingSwipeGesture && gesture != null && keyboardGestures.containsGesture(gesture)) {
                    if(leapTool.getVelocity().magnitude() < GESTURE_SWIPE_MIN_VELOCITY.getValue()) {
                        gesture.gestureFinshed();
                        gesture = null;
                    } // else continue detecting it
                }
                // We have already detected a swipe but it's not long enough yet.
                else if (detectingSwipeGesture && gesture != null){
                    // Update the gesture since it's not added to the renderables yet.
                    gesture.update(leapPoint.getNormalizedPoint());
                    // Check that we're still maintaining velocity.
                    if(leapTool.getVelocity().magnitude() >= GESTURE_SWIPE_MIN_VELOCITY.getValue()) {
                        // Check if we've met minimum length requirement.
                        if(gesture.getLength() >= GESTURE_SWIPE_MIN_LENGTH.getValue()) {
                            // Gesture meets all minimum requirements, so add to renderable gestures.
                            keyboardGestures.addGesture(gesture);
                        } // else continue detecting it
                    } else if(gesture.getLength() >= GESTURE_SWIPE_MIN_LENGTH.getValue()) {
                        // We were long enough at the time we lost minimum velocity.
                        keyboardGestures.addGesture(gesture);
                    } else {
                        // We lost velocity before reaching minimum length.
                        detectingSwipeGesture = false;
                    }
                }
                // We aren't currently detecting a swipe.
                else {
                    if(leapTool.getVelocity().magnitude() >= GESTURE_SWIPE_MIN_VELOCITY.getValue()) {
                        detectingSwipeGesture = true;
                        gesture = new KeyboardGesture(leapPoint.getNormalizedPoint(), Gesture.SWIPE);
                    }
                }
            }
        }
    }
    
    private class SwipeKeyboard implements WordObserver {
        private static final float AUTO_REPEAT_DELAY = (750 * 1f/3f) + 250; // Windows default
        private static final int AUTO_REPEAT_RATE = 1000 / 31; // Windows default
        private final int MIN_EXPECTED_PRESS_RADIUS = (int) (keyboardAttributes.getAttributeAsPoint(Attribute.KEY_SIZE).x * 1.25f); // 80;
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
        private boolean touchPress;
        private boolean touchDown;
        
        public void update() {
            // TODO: Implement it so that we only record presses of the correct keys
            // calculate a path based on expected word -- record deviations from path as key presses, also perhaps significant angles
            // only detect press on expected letter for now
            
            // 1) Determing the vector from either current point to dest key center or from previous key center to next key center.
            // 2) If deviate by min Arc or Angle then we count keys in our path as pressed even if they are on the path to the next key. This also helps us determine accuracy.
            // 3) If we touch a key, we considered it pressed only once.
            // 4) If Swipe is used, then we generate a free space before (if doesn't exit) and after word we swiped.
            // 5) If key taps are used, then we generate no spaces.
            // 6) The only key that can be held down is backspace. Just implement it like backspace of other keyboards.
            if(leapPlane.isTouching()) {
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

            if((virtualKey = virtualKeyboard.isHoveringAny(leapPoint.getNormalizedPoint())) != null && touchDown) {
                // Have to force down to be false or else we run into problems when the leap is on a slow computer.
                if(previousKey != virtualKey) {
                    isDown = false;
                }
                virtualKey.pressed();
                if(touchPress) {
                    firstKey = virtualKey;
                }
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
                                MyUtilities.MATH_UTILITILES.findDistanceToPoint(leapPoint.getNormalizedPoint(), expectedVirtualKey.getCenter()) <= MIN_EXPECTED_PRESS_RADIUS) {
                            virtualKey = expectedVirtualKey;
                        }
                    }
                    if(!isPressed && !isDown && !touchPress && onExpectedLetterDown(virtualKey.getKey())) {
                        isPressed = true;
                        isDown = true;
                    } else if(!isPressed && !isDown && touchPress && !onExpectedLetterDown(virtualKey.getKey())) {
                        isPressed = true;
                        isDown = true;
                    } else if(!isPressed && !isDown && !touchPress) {
                        isDown = true;
                    } else if(!isPressed && !onPreviousExpectedLetterDown(virtualKey.getKey())) {
                        Vector pressedPoint = swipeTrail.isPressed();
                        // If we're close enough to our previous expected key when we detect an angle press, then we shouldn't count it.
                        VirtualKey previousExpectedVirtualKey = virtualKeyboard.getVirtualKey(previousExpectedKey);
                        if(previousExpectedVirtualKey != null &&
                                MyUtilities.MATH_UTILITILES.findDistanceToPoint(leapPoint.getNormalizedPoint(), previousExpectedVirtualKey.getCenter()) <= MIN_EXPECTED_PRESS_RADIUS) {
                            pressedPoint = Vector.zero();
                        }
                        if(!pressedPoint.equals(Vector.zero())) {
                            if(virtualKey.isHovering(pressedPoint)) {
                                isPressed = true;
                                isDown = true;
                            } else if(!previousPressed.isHovering(pressedPoint)){
                                virtualKey = virtualKeyboard.getNearestKey(pressedPoint);
                                virtualKey.pressed();
                                isPressed = true;
                            }   
                        }
                    }
                }
                previousKey = virtualKey;
                if(previousKey != firstKey) {
                    firstKey = null;
                }
            } else {
                // TODO: Fix the hitting 'l' when releasing backspace problem.
                if(isPressed || isDown) {
                    if(!onExpectedLetterRelease()) {
                        // If we're close enough to our previous expected key when we detect a key release, then we shouldn't count it.
                        VirtualKey previousExpectedVirtualKey = virtualKeyboard.getVirtualKey(previousExpectedKey);
                        if(previousExpectedVirtualKey != null &&
                                MyUtilities.MATH_UTILITILES.findDistanceToPoint(leapPoint.getNormalizedPoint(), previousExpectedVirtualKey.getCenter()) <= MIN_EXPECTED_PRESS_RADIUS) {
                            isPressed = false;
                        } else {
                            isPressed = true;
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
            previousExpectedKey = expectedKey;
            expectedKey = key;
            // Find vector from this expected key to previous expected key
            if(expectedKey == Key.VK_ENTER || expectedKey == Key.VK_BACK_SPACE) {
                swipeTrail.setExpectedPath(Vector.zero(), Vector.zero());
            } else {
                // Use this path to lessen the risk of accident presses in a straight line.
                swipeTrail.setExpectedPath(virtualKeyboard.getVirtualKey(previousExpectedKey).getCenter(), virtualKeyboard.getVirtualKey(expectedKey).getCenter());
            }
        }
    }
}

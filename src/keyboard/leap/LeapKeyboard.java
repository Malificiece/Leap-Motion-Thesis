package keyboard.leap;

import utilities.Point;

import java.util.concurrent.locks.ReentrantLock;

import javax.media.opengl.GL2;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.JPanel;

import utilities.MyUtilities;

import com.leapmotion.leap.InteractionBox;

import enums.Attribute;
import enums.FileExt;
import enums.FileName;
import enums.FilePath;
import enums.Gesture;
import enums.Key;
import enums.Renderable;
import enums.Setting;
import keyboard.CalibrationObserver;
import keyboard.IKeyboard;
import keyboard.KeyboardGesture;
import keyboard.KeyboardSetting;
import keyboard.renderables.KeyboardGestures;
import keyboard.renderables.LeapPlane;
import keyboard.renderables.LeapPoint;
import keyboard.renderables.LeapTool;
import keyboard.renderables.LeapTrail;
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
    private LeapTrail leapTrail;
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
        keyboardGestures = (KeyboardGestures) keyboardRenderables.getRenderable(Renderable.KEYBOARD_GESTURES);
        leapTrail = (LeapTrail) keyboardRenderables.getRenderable(Renderable.LEAP_TRAIL);
        leapPlane = (LeapPlane) keyboardRenderables.getRenderable(Renderable.LEAP_PLANE);
        leapPlane.registerObserver(this);
        if(!leapPlane.isCalibrated()) {
            leapPoint.blockAccess(true);
            leapTool.blockAccess(true);
            keyboardGestures.blockAccess(true);
        } else {
            isCalibrated = true;
        }
        leapGestures = new LeapGestures();
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
            // Allow leap plane to take over the updates of specific objects that require the plane
            leapPlane.update(leapPoint, leapTool, keyboardGestures, leapTrail);
            // Update gestures after plane, we need both normalized and non normalized points.
            leapGestures.update();
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
    }

    @Override
    public void removeFromUI(JPanel panel, GLCanvas canvas) {
        LeapListener.stopListening();
        LeapListener.removeObserver(this);
        leapTool.deleteQuadric();
        keyboardGestures.deleteQuadric();
    }

    @Override
    public void beginCalibration(JPanel textPanel) {
        leapPoint.blockAccess(true);
        leapTool.blockAccess(true);
        keyboardGestures.blockAccess(true);
        virtualKeyboard.blockAccess(true);
        keyboardRenderables.getRenderable(Renderable.KEYBOARD_IMAGE).blockAccess(true);
        leapPlane.beginCalibration(textPanel);
    }

    @Override
    protected void finishCalibration() {
        leapPoint.grantAccess(true);
        leapTool.grantAccess(true);
        keyboardGestures.grantAccess(true);
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
            GESTURE_SWIPE_MIN_LENGTH = keyboardSettings.getSetting(Setting.GESTURE_SWIPE_MIN_LENGTH);
            GESTURE_SWIPE_MIN_VELOCITY = keyboardSettings.getSetting(Setting.GESTURE_SWIPE_MIN_VELOCITY);
        }
        
        public void update() {
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
    
    private class SwipeKeyboard {
        private static final float AUTO_REPEAT_DELAY = (750 * 1f/3f) + 250; // Windows default
        private static final int AUTO_REPEAT_RATE = 1000 / 31; // Windows default
        private VirtualKey virtualKey;
        private boolean isPressed;
        private boolean isDown;
        private boolean isRepeating;
        private long previousRepeatTime = 0;
        private long elapsedRepeatTime = 0;
        
        public void update() {
            // 1) Determing the vector from either current point to dest key center or from previous key center to next key center.
            // 2) If deviate by min Arc or Angle then we count keys in our path as pressed even if they are on the path to the next key. This also helps us determine accuracy.
            // 3) If we touch a key, we considered it pressed only once.
            // 4) If Swipe is used, then we generate a free space before (if doesn't exit) and after word we swiped.
            // 5) If key taps are used, then we generate no spaces.
            // 6) The only key that can be held down is backspace. Just implement it like backspace of other keyboards.
            
            if((virtualKey = virtualKeyboard.isHoveringAny(leapPoint.getNormalizedPoint())) != null && leapPlane.isTouching()) {
                virtualKey.pressed();
                if(!isPressed && !isDown) {
                    isPressed = true;
                    isDown = true;
                    if(virtualKey.getKey() == Key.VK_BACK_SPACE) {
                        previousRepeatTime = System.currentTimeMillis();
                        elapsedRepeatTime = 0;
                    }
                } else if(virtualKey.getKey() == Key.VK_BACK_SPACE) {
                    long now = System.currentTimeMillis();
                    elapsedRepeatTime += now - previousRepeatTime;
                    previousRepeatTime = now;
                    
                    if(!isRepeating && elapsedRepeatTime > AUTO_REPEAT_DELAY) {
                        isPressed = true;
                        isRepeating = true;
                        elapsedRepeatTime = 0;
                    } else if(isRepeating && elapsedRepeatTime > AUTO_REPEAT_RATE) {
                        isPressed = true;
                        elapsedRepeatTime = 0;
                    }
                }
            } else {
                if(isPressed || isDown) {
                    isPressed = false;
                    isDown = false;
                    isRepeating = false;
                }
            }
        }

        public Key isPressed() {
            try {
                if(isPressed) {
                    return virtualKey.getKey();
                } else {
                    return Key.VK_NULL;
                }
            } finally {
                // Consume the pressed key event.
                isPressed = false;
            }
        }
    }
}

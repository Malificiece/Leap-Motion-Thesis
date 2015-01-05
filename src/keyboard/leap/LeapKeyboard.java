package keyboard.leap;

import java.util.concurrent.locks.ReentrantLock;

import javax.media.opengl.GL2;
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
import leap.LeapObserver;


public class LeapKeyboard extends IKeyboard implements LeapObserver, CalibrationObserver {
    public static final int KEYBOARD_ID = 1;
    private static final String KEYBOARD_NAME = "Leap Keyboard";
    private static final String KEYBOARD_FILE_NAME = FileName.LEAP.getName();
    private static final ReentrantLock LEAP_LOCK = new ReentrantLock();
    private final int DIST_TO_CAMERA;
    private LeapData leapData;
    private LeapTool leapTool;
    private LeapPoint leapPoint;
    private KeyboardGestures keyboardGestures;
    private LeapPlane leapPlane;
    private LeapTrail leapTrail;
    private LeapGestures leapGestures;
    private SwipeKeyboard swipeKeyboard;
    private VirtualKeyboard virtualKeyboard;
    private boolean isCalibrated = false;
    private boolean shiftDown = false;
    
    public LeapKeyboard() {
        super(KEYBOARD_ID, KEYBOARD_NAME, KEYBOARD_FILE_NAME);
        System.out.println(KEYBOARD_NAME + " - Loading Settings from " + FilePath.CONFIG.getPath() + KEYBOARD_FILE_NAME + FileExt.INI.getExt());
        keyboardAttributes = new LeapAttributes(this);
        keyboardSettings = new LeapSettings(this);
        System.out.println("-------------------------------------------------------");
        keyboardRenderables = new LeapRenderables(this);
        keyboardWidth = keyboardAttributes.getAttribute(Attribute.KEYBOARD_WIDTH);
        keyboardHeight = keyboardAttributes.getAttribute(Attribute.KEYBOARD_HEIGHT);
        DIST_TO_CAMERA = keyboardAttributes.getAttribute(Attribute.DIST_TO_CAMERA).getValueAsInteger();
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
    public void render(GL2 gl) {
        MyUtilities.OPEN_GL_UTILITIES.switchToPerspective(gl, this, true);
        gl.glPushMatrix();
        gl.glTranslatef(-keyboardWidth.getValueAsInteger()/2f, -keyboardHeight.getValueAsInteger()/2f, -DIST_TO_CAMERA); // 465 is the magic number that somehow centers the 2D with the 3D
        keyboardRenderables.render(gl);
        gl.glPopMatrix();
    }
    
    @Override
    public void update() {
        LEAP_LOCK.lock();
        try {
            // Allow leap plane to take over the updates of specific objects that require the plane
            leapPlane.update(leapPoint, leapTool, keyboardGestures, leapTrail);
            // Update gestures after plane, we need both normalized and non normalized points.
            leapGestures.update();
            if(leapTool.isValid()) {
                VirtualKey vKey;
                if((vKey = virtualKeyboard.isHoveringAny(leapPoint.getNormalizedPoint())) != null && leapPlane.isTouching()) {
                    vKey.pressed();
                    if(vKey.getKey() != Key.VK_SHIFT) {
                        if(shiftDown) {
                            keyPressed = vKey.getKey().toUpper();
                            shiftDown = false;
                        } else {
                            keyPressed = vKey.getKey().getValue();   
                        }
                        notifyListenersKeyEvent();
                    } else {
                        shiftDown = true;
                    }
                }
                if(shiftDown) {
                    virtualKeyboard.pressed(Key.VK_SHIFT);
                }
            } else {
                virtualKeyboard.clearAll();
            }
        } finally {
            LEAP_LOCK.unlock();
        }
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
        leapTool.createQuadric();
        keyboardGestures.createQuadric();
    }

    @Override
    public void keyboardCalibrationFinishedEventObserved() {
        finishCalibration();
    }
    
    protected class LeapGestures {
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
        
    }
}

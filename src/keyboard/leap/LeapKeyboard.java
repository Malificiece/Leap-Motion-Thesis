package keyboard.leap;

import java.util.concurrent.locks.ReentrantLock;

import javax.media.opengl.GL2;

import utilities.MyUtilities;

import com.leapmotion.leap.InteractionBox;

import enums.AttributeName;
import enums.FilePath;
import enums.RenderableName;
import keyboard.IKeyboard;
import keyboard.renderables.LeapGestures;
import keyboard.renderables.LeapPlane;
import keyboard.renderables.LeapPoint;
import keyboard.renderables.LeapTool;
import keyboard.renderables.VirtualKey;
import keyboard.renderables.VirtualKeyboard;
import leap.LeapData;
import leap.LeapObserver;


public class LeapKeyboard extends IKeyboard implements LeapObserver {
    public static final int KEYBOARD_ID = 1;
    private static final String KEYBOARD_FILE_PATH = FilePath.LEAP_PATH.getPath();
    private static final ReentrantLock LEAP_LOCK = new ReentrantLock();
    private final int DIST_TO_CAMERA;
    private LeapData leapData;
    private LeapTool leapTool;
    private LeapPoint leapPoint;
    private LeapGestures leapGestures;
    private LeapPlane leapPlane;
    private VirtualKeyboard virtualKeyboard;
    
    public LeapKeyboard() {
        super(KEYBOARD_ID, KEYBOARD_FILE_PATH);
        keyboardAttributes = new LeapAttributes(this);
        keyboardSettings = new LeapSettings(this);
        keyboardRenderables = new LeapRenderables(this);
        keyboardWidth = keyboardAttributes.getAttributeByName(AttributeName.KEYBOARD_WIDTH.toString());
        keyboardHeight = keyboardAttributes.getAttributeByName(AttributeName.KEYBOARD_HEIGHT.toString());
        DIST_TO_CAMERA = keyboardAttributes.getAttributeByName(AttributeName.DIST_TO_CAMERA.toString()).getValueAsInteger();
        virtualKeyboard = (VirtualKeyboard) keyboardRenderables.getRenderableByName(RenderableName.VIRTUAL_KEYS.toString());
        leapPoint = (LeapPoint) keyboardRenderables.getRenderableByName(RenderableName.LEAP_POINT.toString());
        leapTool = (LeapTool) keyboardRenderables.getRenderableByName(RenderableName.LEAP_TOOL.toString());
        leapGestures = (LeapGestures) keyboardRenderables.getRenderableByName(RenderableName.LEAP_GESTURES.toString());
        leapPlane = (LeapPlane) keyboardRenderables.getRenderableByName(RenderableName.LEAP_PLANE.toString());
    }
    
    @Override
    public void render(GL2 gl) {
        MyUtilities.OPEN_GL_UTILITIES.switchToPerspective(gl, this);
        gl.glPushMatrix();
        gl.glTranslatef(-keyboardWidth.getValueAsInteger()/2f, -keyboardHeight.getValueAsInteger()/2f, -DIST_TO_CAMERA); // 465 is the magic number that somehow centers the 2D with the 3D
        keyboardRenderables.render(gl);
        gl.glPopMatrix();
    }
    
    @Override
    public void update() {
        LEAP_LOCK.lock();
        try {
            if(leapTool.isValid()) {
                //keyPressed = 'l';
                //notifyListeners();
                // Allow leap plane to take over the updates of specific objects that require the plane
                leapPlane.update(leapPoint, leapTool);
                VirtualKey vKey;
                if((vKey = virtualKeyboard.isHoveringAny(leapPoint.getNormalizedPoint())) != null && leapPlane.isTouching()) {
                    vKey.pressed();
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
            this.leapData.populateData(leapPoint, leapTool, leapGestures);
        } finally {
            LEAP_LOCK.unlock();
        }
    }

    @Override
    public void leapInteractionBoxSet(InteractionBox iBox) {
        leapPoint.setInteractionBox(iBox);
        leapPlane.setInteractionBox(iBox);
        leapGestures.setInteractionBox(iBox);
        leapTool.setInteractionBox(iBox);

        // Add this also to calibration I suppose
        leapTool.createCylinder();
        
        // temporary - move this to section that fires when calibration button is pushed
        leapPlane.calibrate();
    }
}

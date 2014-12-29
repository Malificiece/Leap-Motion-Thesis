package keyboard.leap;

import java.util.concurrent.locks.ReentrantLock;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

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
    protected GLU glu  = new GLU();
    
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
        MyUtilities.OPEN_GL_UTILITIES.switchToPerspective(gl, glu, this);
        gl.glPushMatrix();
        // TODO: Might need to add translates to individual render functions depending on the ortho vs perspective thing
        gl.glTranslatef(-keyboardWidth.getValueAsInteger()/2.0f, -keyboardHeight.getValueAsInteger()/2.0f, -DIST_TO_CAMERA); // 465 is the magic number that somehow centers the 2D with the 3D
        keyboardRenderables.render(gl);
        gl.glPopMatrix();
    }
    
    @Override
    public void update() {
        // do calculations/normalization etc here?
        // render functions will only render the object/shape I want based off of what I set them to here
        // can also make it so that the render function takes care of their explicit 3D/2D, depth, light preferences
        // also make to switch from ortho to perspective here
        
        // Last but not least once we calibrate the leap plane (however/wherever we do that) we'll need to call
        // virtualKeyboard.rebuildKeys(leapPlane);
        LEAP_LOCK.lock();
        try {
            if(leapTool.isValid()) {
                //keyPressed = 'l';
                //notifyListeners();
                //System.out.println("point: " + leapPoint.getPoint() + "  distance: " + leapPlane.distToPlane(leapPoint.getPoint()));
                leapPlane.update(leapPoint);
                VirtualKey vKey;
                if((vKey = virtualKeyboard.isHoveringAny(leapPoint.getNormalizedPoint())) != null && leapPlane.isTouching(leapPoint.getPoint())) {
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

        // temporary - call this after we get points from leapPlane.calibrate()
        leapPlane.calibrate();
    }
}

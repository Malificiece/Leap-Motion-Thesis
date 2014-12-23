package keyboard.leap;

import javax.media.opengl.GL2;

import com.leapmotion.leap.Tool;

import enums.AttributeName;
import enums.FilePath;
import keyboard.IKeyboard;
import leap.LeapData;
import leap.LeapObserver;

public class LeapKeyboard extends IKeyboard implements LeapObserver {
    public static final int KEYBOARD_ID = 1;
    private static final String KEYBOARD_FILE_PATH = FilePath.LEAP_PATH.getPath();
    private Tool myTestTool = new Tool();
    private boolean once = false;
    
    public LeapKeyboard() {
        super(KEYBOARD_ID, KEYBOARD_FILE_PATH);
        keyboardAttributes = new LeapAttributes(this);
        keyboardSettings = new LeapSettings(this);
        keyboardRenderables = new LeapRenderables(this);
        width = keyboardAttributes.getAttributeByName(AttributeName.KEYBOARD_WIDTH.toString());
        height = keyboardAttributes.getAttributeByName(AttributeName.KEYBOARD_HEIGHT.toString());
    }
    
    @Override
    public void render(GL2 gl) {
        gl.glPushMatrix();
        gl.glTranslatef(0.0f, 0.0f, -100.0f);
        // TODO: Figure out what order is best for drawing. Image on top of colors or colors on top of image etc.
        //drawBackground(); // convert to drawing the leap plane in order to determine if leap plane is correct
        keyboardRenderables.render(gl);
        gl.glPopMatrix();
        
        //gl.glTranslatef(-323.5f, -192.5f, -1000.0f); // figure out what to do here in order to do perspective if we use texture
        //gl.GL_TEXTURE_RECTANGLE_ARB --- use this for exact texturing if imaging attempt fails.
    }
    
    /*private void drawBackground() {
        //gl.glColor4f(0.5f, 0.5f, 0.5f, 0.5f);
        //gl.glRecti(0, 0, WIDTH, HEIGHT);
        
        
        /* Use this for leap plane
        gl.glBegin(GL_QUADS);
        gl.glColor3f(1.0f, 0.0f, 0.0f); // red
        gl.glVertex3f(300, 300, 0);
        gl.glVertex3f(0, 300, -100);
        gl.glVertex3f(0, 0, -100);
        gl.glVertex3f(300, 0, 0);
        gl.glEnd();
    }*/
    
    @Override
    public void update() {
        // do calculations/normalization etc here?
        // render functions will only render the object/shape I want based off of what I set them to here
        // can also make it so that the render function takes care of their explicit 3D/2D, depth, light preferences
        // also make to switch from ortho to perspective here
        
        // Last but not least once we calibrate the leap plane (however/wherever we do that) we'll need to call
        // virtualKeyboard.rebuildKeys(leapPlane);
        keyPressed = 'l';
        notifyListeners();
        System.out.println("keyboard: " + "Tip Position: " + myTestTool.tipPosition() + " Stabilized Tip Position: " + myTestTool.stabilizedTipPosition());
    }

    /*@Override
    public void leapEventObserved(LeapData leapObject) {
        System.out.println("receiving leap data: " + leapObject);
    }*/

    @Override
    public void leapEventObserved(Tool testTool) {
        if(!once) {
            once = true;
            myTestTool = testTool;
            System.out.println("Receiving leap data!");
        }
    }
}

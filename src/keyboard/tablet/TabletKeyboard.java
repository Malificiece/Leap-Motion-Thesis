package keyboard.tablet;

import static javax.media.opengl.fixedfunc.GLMatrixFunc.GL_MODELVIEW;
import static javax.media.opengl.fixedfunc.GLMatrixFunc.GL_PROJECTION;

import javax.media.opengl.GL2;

import keyboard.IKeyboard;
import enums.Attribute;
import enums.FileName;

public class TabletKeyboard extends IKeyboard {
    public static final int KEYBOARD_ID = 2;
    private static final String KEYBOARD_FILE_NAME = FileName.TABLET_NAME.getName();
    //private VirtualKeyboard virtualKeyboard;
    
    public TabletKeyboard() {
        super(KEYBOARD_ID, KEYBOARD_FILE_NAME);
        keyboardAttributes = new TabletAttributes(this);
        keyboardSettings = new TabletSettings(this);
        keyboardRenderables = new TabletRenderables(this);
        keyboardWidth = keyboardAttributes.getAttributeByName(Attribute.KEYBOARD_WIDTH.toString());
        keyboardHeight = keyboardAttributes.getAttributeByName(Attribute.KEYBOARD_HEIGHT.toString());
        //virtualKeyboard = (VirtualKeyboard) keyboardRenderables.getRenderableByName(RenderableName.VIRTUAL_KEYS.toString());
    }
    
    @Override
    public void render(GL2 gl) {
        // Setup ortho projection, with aspect ratio matches viewport
        gl.glMatrixMode(GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrtho(0, keyboardWidth.getValueAsInteger(), 0, keyboardHeight.getValueAsInteger(), 0.1, 1000);
   
        // Enable the model-view transform
        gl.glMatrixMode(GL_MODELVIEW);
        gl.glLoadIdentity();
        
        gl.glPushMatrix();
        gl.glTranslatef(0.0f, 0.0f, -0.1f);
        
        gl.glPushMatrix();
        gl.glTranslatef(0.0f, 0.0f, -100.0f);
        // TODO: Figure out what order is best for drawing. Image on top of colors or colors on top of image etc.
        //drawBackground(); // convert to drawing the leap plane in order to determine if leap plane is correct
        keyboardRenderables.render(gl);
        gl.glPopMatrix();
        
        //gl.glTranslatef(-323.5f, -192.5f, -1000.0f); // figure out what to do here in order to do perspective if we use texture
        //gl.GL_TEXTURE_RECTANGLE_ARB --- use this for exact texturing if imaging attempt fails.
    }
    
    @Override
    public void update() {
        // TODO Add the key listener stuff in here maybe?
        // notifyListeners(); when they fire
        // What other point does update serve if not for that?
        // Possibly for leap we can give it the leap object
        // We'll have to tract that in a different way then rather than passing
        // it to Calibration control/experiment control which don't really care about the leap
    }
}

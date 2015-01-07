package keyboard.tablet;

import utilities.Point;

import javax.media.opengl.GL2;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.JPanel;

import utilities.MyUtilities;
import keyboard.IKeyboard;
import enums.Attribute;
import enums.FileExt;
import enums.FileName;
import enums.FilePath;

public class TabletKeyboard extends IKeyboard {
    public static final int KEYBOARD_ID = 2;
    private static final String KEYBOARD_NAME = "Tablet Keyboard";
    private static final String KEYBOARD_FILE_NAME = FileName.TABLET.getName();
    private boolean isCalibrated = false;
    //private VirtualKeyboard virtualKeyboard;
    
    public TabletKeyboard() {
        super(KEYBOARD_ID, KEYBOARD_NAME, KEYBOARD_FILE_NAME);
        System.out.println(KEYBOARD_NAME + " - Loading Settings from " + FilePath.CONFIG.getPath() + KEYBOARD_FILE_NAME + FileExt.INI.getExt());
        keyboardAttributes = new TabletAttributes(this);
        keyboardSettings = new TabletSettings(this);
        System.out.println("-------------------------------------------------------");
        keyboardRenderables = new TabletRenderables(this);
        keyboardSize = keyboardAttributes.getAttributeAsPoint(Attribute.KEYBOARD_SIZE);
        int borderSize = keyboardAttributes.getAttributeAsInteger(Attribute.BORDER_SIZE) * 2;
        imageSize = new Point(keyboardSize.x + borderSize, keyboardSize.y + borderSize);
        //virtualKeyboard = (VirtualKeyboard) keyboardRenderables.getRenderable(RenderableName.VIRTUAL_KEYS);
    }
    
    @Override
    public void render(GL2 gl) {
        MyUtilities.OPEN_GL_UTILITIES.switchToOrthogonal(gl, this, true);
        gl.glPushMatrix();
        gl.glTranslatef(0.0f, 0.0f, -0.1f);
        keyboardRenderables.render(gl);
        gl.glPopMatrix();
    }
    
    @Override
    public void update() {
        // TODO: Implement Tablet Keyboard
    }

    @Override
    public void beginCalibration(JPanel textPanel) {
        finishCalibration();
    }

    @Override
    protected void finishCalibration() {
        isCalibrated = true;
        notifyListenersCalibrationFinished();
    }

    @Override
    public boolean isCalibrated() {
        return isCalibrated;
    }

    @Override
    public void addToUI(JPanel panel, GLCanvas canvas) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void removeFromUI(JPanel panel, GLCanvas canvas) {
        // TODO Auto-generated method stub
        
    }
}

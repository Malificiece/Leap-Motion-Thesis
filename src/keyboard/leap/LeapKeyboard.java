package keyboard.leap;

import javax.media.opengl.GL2;

import enums.AttributeName;
import keyboard.IKeyboard;

public class LeapKeyboard extends IKeyboard {
    
    public LeapKeyboard() {
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
        key = 'l';
        notifyListeners();
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
        // TODO Auto-generated method stub
    }
}

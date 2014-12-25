package keyboard.renderables;

import static javax.media.opengl.GL2GL3.GL_TRIANGLE_FAN;

import javax.media.opengl.GL2;

import com.leapmotion.leap.InteractionBox;
import com.leapmotion.leap.Vector;

import enums.AttributeName;
import enums.RenderableName;
import keyboard.KeyboardAttributes;
import keyboard.KeyboardRenderable;

public class LeapPoint extends KeyboardRenderable {
    private static final String RENDER_NAME = RenderableName.LEAP_POINT.toString();
    private static final int NUM_VERTICIES = 32;
    private static final float DELTA_ANGLE = (float) (2.0f * Math.PI / NUM_VERTICIES);
    private static final float RADIUS = 10f;
    private Vector A = new Vector(-57.324f, 138.28f, -32.742f);
    private final int KEYBOARD_WIDTH;
    private final int KEYBOARD_HEIGHT;
    private Vector point = new Vector();
    private InteractionBox iBox;
    
    public LeapPoint(KeyboardAttributes keyboardAttributes) {
        super(RENDER_NAME);
        KEYBOARD_WIDTH = keyboardAttributes.getAttributeByName(AttributeName.KEYBOARD_WIDTH.toString()).getValueAsInteger();
        KEYBOARD_HEIGHT = keyboardAttributes.getAttributeByName(AttributeName.KEYBOARD_HEIGHT.toString()).getValueAsInteger();
    }
    
    public void setPoint(Vector point) {
        this.point = point;
    }
    
    public Vector getPoint() {
        return point;
    }
    
    public void setInteractionBox(InteractionBox iBox) {
        this.iBox = iBox;
    }
    
    public Vector normalize() {
        return iBox.normalizePoint(point);
    }

    @Override
    public void render(GL2 gl) {
        if(isEnabled()) {
            //gl.glLoadIdentity();
            gl.glPushMatrix();

            Vector norm = normalize();
            float x = norm.getX() * KEYBOARD_WIDTH;
            float y = KEYBOARD_HEIGHT - norm.getY() * KEYBOARD_HEIGHT;
             y = KEYBOARD_HEIGHT - y;
            //gl.glTranslatef(x, y, norm.getZ());
            Vector tmpP = point.minus(A);
            gl.glTranslatef(tmpP.getX(), tmpP.getY(), tmpP.getZ());

            //System.out.println("Leap Point: " + point  + "   Normalized: " + norm);
            
            drawPoint(gl);
            
            gl.glPopMatrix();
        }
    }
    
    private void drawPoint(GL2 gl) {
        gl.glColor3f(0f, 0f, 0f);
        gl.glBegin(GL_TRIANGLE_FAN);

        //draw the vertex at the center of the circle      
        gl.glVertex3f(0f, 0f, 0f);
       
        for(int i = 0; i < NUM_VERTICIES ; i++)
        {
          gl.glVertex3d(Math.cos(DELTA_ANGLE * i) * RADIUS, Math.sin(DELTA_ANGLE * i) * RADIUS, 0.0);
        }

        gl.glVertex3f(1f * RADIUS, 0f, 0f);
        
        gl.glEnd();
    }
}

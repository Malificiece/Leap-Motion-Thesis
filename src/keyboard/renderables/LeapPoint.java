package keyboard.renderables;

import static javax.media.opengl.GL2GL3.GL_TRIANGLE_FAN;

import javax.media.opengl.GL2;

import utilities.MyUtilities;

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
    private final int DIST_TO_CAMERA;
    private Vector point = new Vector();
    private Vector normalizedPoint = point;
    private InteractionBox iBox;
    
    public LeapPoint(KeyboardAttributes keyboardAttributes) {
        super(RENDER_NAME);
        KEYBOARD_WIDTH = keyboardAttributes.getAttributeByName(AttributeName.KEYBOARD_WIDTH.toString()).getValueAsInteger();
        KEYBOARD_HEIGHT = keyboardAttributes.getAttributeByName(AttributeName.KEYBOARD_HEIGHT.toString()).getValueAsInteger();
        DIST_TO_CAMERA = keyboardAttributes.getAttributeByName(AttributeName.DIST_TO_CAMERA.toString()).getValueAsInteger();
    }
    
    public void setPoint(Vector point) {
        this.point = point;
    }
    
    public Vector getPoint() {
        return point;
    }
    
    public Vector getNormalizedPoint() {
        return normalizedPoint;
    }
    
    public void setInteractionBox(InteractionBox iBox) {
        this.iBox = iBox;
    }
    
    private void normalizePoint() {
        normalizedPoint = iBox.normalizePoint(normalizedPoint);
    }
    
    public void applyPlaneRotationAndNormalizePoint(Vector axis, float angle) {
        normalizedPoint = MyUtilities.MATH_UTILITILES.rotateVector(point, axis, angle);
        normalizePoint();
    }
    
    public void applyPlaneNormalization() {
        normalizedPoint.setX(normalizedPoint.getX() * KEYBOARD_WIDTH);
        normalizedPoint.setY(normalizedPoint.getY() * KEYBOARD_HEIGHT);
        normalizedPoint.setZ(normalizedPoint.getZ() * DIST_TO_CAMERA);
    }

    @Override
    public void render(GL2 gl) {
        if(isEnabled()) {
            gl.glPushMatrix();
            gl.glTranslatef(normalizedPoint.getX(), normalizedPoint.getY(), normalizedPoint.getZ());
            drawPoint(gl);
            gl.glPopMatrix();
        }
    }
    
    private void drawPoint(GL2 gl) {
        gl.glColor4f(0f, 0f, 1f, (DIST_TO_CAMERA-normalizedPoint.getZ())/DIST_TO_CAMERA);
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

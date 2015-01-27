package keyboard.renderables;

import static javax.media.opengl.GL.GL_LINES;
import static javax.media.opengl.GL2.GL_ENABLE_BIT;
import static javax.media.opengl.GL2.GL_LINE_STIPPLE;
import static javax.media.opengl.GL2GL3.GL_TRIANGLE_FAN;
import utilities.GLColor;
import utilities.Point;

import javax.media.opengl.GL2;

import utilities.MyUtilities;

import com.leapmotion.leap.InteractionBox;
import com.leapmotion.leap.Vector;

import enums.Attribute;
import enums.Color;
import enums.Renderable;
import keyboard.KeyboardAttributes;
import keyboard.KeyboardRenderable;

public class SwipePoint extends KeyboardRenderable {
    private static final Renderable TYPE = Renderable.SWIPE_POINT;
    private static final GLColor COLOR = new GLColor(Color.BLUE);
    private static final int NUM_VERTICIES = 32;
    private static final float DELTA_ANGLE = (float) (2.0f * Math.PI / NUM_VERTICIES);
    private static final float RADIUS = 10f;
    private final Point KEYBOARD_SIZE;
    private final int BORDER_SIZE;
    private final float CAMERA_DISTANCE;
    private Vector point = new Vector();
    private Vector normalizedPoint = Vector.zero();
    private InteractionBox iBox;
    
    public SwipePoint(KeyboardAttributes keyboardAttributes) {
        super(TYPE);
        KEYBOARD_SIZE = keyboardAttributes.getAttributeAsPoint(Attribute.KEYBOARD_SIZE);
        BORDER_SIZE = keyboardAttributes.getAttributeAsInteger(Attribute.BORDER_SIZE);
        CAMERA_DISTANCE = keyboardAttributes.getAttributeAsFloat(Attribute.CAMERA_DISTANCE);
    }
    
    public void setPoint(Vector point) {
        this.point = point;
    }
    
    public void setNormalizedPoint(Vector point) {
        this.normalizedPoint = point;
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
    
    private Vector normalizePoint() {
        if(iBox != null) {
            return iBox.normalizePoint(normalizedPoint);
        }
        return normalizedPoint;
    }
    
    public void applyPlaneRotationAndNormalizePoint(Vector axis, float angle) {
        if(axis.isValid()) {
            normalizedPoint = MyUtilities.MATH_UTILITILES.rotateVector(point, axis, angle);
        }
        normalizedPoint = normalizePoint();
    }
    
    public void scaleTo3DSpace() {
        normalizedPoint.setX((normalizedPoint.getX() * KEYBOARD_SIZE.x) + BORDER_SIZE);
        normalizedPoint.setY((normalizedPoint.getY() * KEYBOARD_SIZE.y) + BORDER_SIZE);
        normalizedPoint.setZ(normalizedPoint.getZ() * CAMERA_DISTANCE);
    }

    @Override
    public void render(GL2 gl) {
        if(isEnabled()) {
            gl.glPushMatrix();
            gl.glNormal3f(0, 0, 1);
            drawDottedLine(gl);
            gl.glTranslatef(normalizedPoint.getX(), normalizedPoint.getY(), normalizedPoint.getZ());
            drawPoint(gl);
            gl.glPopMatrix();
        }
    }
    
    private void drawDottedLine(GL2 gl) {
        COLOR.setAlpha(1f);
        COLOR.glColor(gl);
        gl.glPushAttrib(GL_ENABLE_BIT);
        gl.glLineWidth(2);
        gl.glLineStipple(1, (short) 0xAAAA);
        gl.glEnable(GL_LINE_STIPPLE);
        gl.glBegin(GL_LINES);
        gl.glVertex3f(normalizedPoint.getX(), normalizedPoint.getY(), normalizedPoint.getZ());
        gl.glVertex3f(normalizedPoint.getX(), normalizedPoint.getY(), 0f);
        gl.glEnd();
        gl.glDisable(GL_LINE_STIPPLE);
        gl.glPopAttrib();
    }
    
    private void drawPoint(GL2 gl) {
        COLOR.setAlpha((CAMERA_DISTANCE-normalizedPoint.getZ())/CAMERA_DISTANCE);
        COLOR.glColor(gl);
        gl.glBegin(GL_TRIANGLE_FAN);
        // Draw the vertex at the center of the circle
        gl.glVertex3f(0f, 0f, 0f);
        for(int i = 0; i < NUM_VERTICIES; i++)
        {
          gl.glVertex3d(Math.cos(DELTA_ANGLE * i) * RADIUS, Math.sin(DELTA_ANGLE * i) * RADIUS, 0.0);
        }
        gl.glVertex3f(1f * RADIUS, 0f, 0f);
        gl.glEnd();
    }
}

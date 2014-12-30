package keyboard.renderables;

import static javax.media.opengl.GL2.*; // GL2 constants

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLUquadric;

import ui.GraphicsController;
import utilities.MyUtilities;

import com.leapmotion.leap.InteractionBox;
import com.leapmotion.leap.Tool;
import com.leapmotion.leap.Vector;

import enums.RenderableName;
import keyboard.KeyboardAttributes;
import keyboard.KeyboardRenderable;

public class LeapTool extends KeyboardRenderable {
    private static final String RENDER_NAME = RenderableName.LEAP_TOOL.toString();
    private static final int NUM_VERTICIES = 16;
    private static final int NUM_STACKS = 4;
    private static final float DELTA_ANGLE = (float) (2.0f * Math.PI / NUM_VERTICIES);
    private static final float RADS_TO_DEGREES = (float) (180 / Math.PI);
    private static final float[] color = {0.8f, 0.67f, 0.49f, 1f};
    //private static final float mat_Ka2[] = { 0.2, 0.2, 0.2, 1f}; // default ambient material property
    //private static final float mat_Kd2[] = { 0.8, 0.8, 0.8, 1f}; // default diffuse material property
    //private static final float mat_Ks2[] = { 0.0, 0.0, 0.0, 1f}; // default specular material property
    //private static final float mat_Ksh2[] = {0f}; // default shinniness material property
    private InteractionBox iBox;
    private float length;
    private float radius;
    private float scaledLength;
    private float scaledRadius;
    private Vector tipDirection;
    private Vector tipPoint = new Vector(0,0,0);
    private boolean isValid = false;
    private float angleToDirection = 0;
    private Vector axisToDirection = tipPoint;
    private Vector cylinderDirection = new Vector(0,0,1);
    private GLUquadric cylinder;
    
    public LeapTool(KeyboardAttributes keyboardAttributes) {
        super(RENDER_NAME);
    }
    
    public void createCylinder() {
        GraphicsController.gl.getContext().makeCurrent();
        if(cylinder != null) {
            GraphicsController.glu.gluDeleteQuadric(cylinder);
        }
        cylinder = GraphicsController.glu.gluNewQuadric();
        //GraphicsController.glu.gluQuadricOrientation(cylinder, GLU.GLU_OUTSIDE);
        //GraphicsController.glu.gluQuadricDrawStyle(cylinder, GLU.GLU_FILL);
        GraphicsController.glu.gluQuadricNormals(cylinder, GL_TRUE);
    }
    
    public void setTool(Tool tool) {
        isValid = tool.isValid();
        if(isValid) {
            length = tool.length();
            radius = tool.width()/2;
            tipDirection = tool.direction();
        }
    }
    
    public void setPoint(Vector point) {
        tipPoint = point;
    }
    
    public void setInteractionBox(InteractionBox iBox) {
        this.iBox = iBox;
    }
    
    public boolean isValid() {
        return isValid;
    }
    
    private void normalizePoint() {
        tipPoint = iBox.normalizePoint(tipPoint);
    }
    
    public void scaleLengthAndWidth(float length, float radius) {
        this.length = length;
        this.radius = radius;
    }
    
    public void calculateOrientation() {
        angleToDirection = cylinderDirection.angleTo(tipDirection) * RADS_TO_DEGREES;
        axisToDirection = cylinderDirection.cross(tipDirection);
        axisToDirection = axisToDirection.divide(axisToDirection.magnitude());
    }
    
    public void applyPlaneRotationAndNormalizePoint(Vector axis, float angle) {
        tipPoint = MyUtilities.MATH_UTILITILES.rotateVector(tipPoint, axis, angle);
        normalizePoint();
    }
    
    public void scaleTo3DSpace(float planeWidth, float planeHeight) {
        //scaledLength = (length / iBox.height()) * planeHeight;
        //scaledRadius = (radius / iBox.width()) * planeWidth;
        scaledLength = length;
        scaledRadius = radius;
    }

    @Override
    public void render(GL2 gl) {
        if(isEnabled()) {
            gl.glPushMatrix();
            gl.glEnable(GL_LIGHTING);
            gl.glEnable(GL_CULL_FACE);
            gl.glMaterialfv(GL_FRONT, GL_DIFFUSE, color, 0);
            gl.glTranslatef(tipPoint.getX(), tipPoint.getY(), tipPoint.getZ());
            gl.glRotatef(angleToDirection, axisToDirection.getX(), axisToDirection.getY(), axisToDirection.getZ());
            gl.glTranslatef(0, 0, -scaledLength);
            drawTool(gl);
            gl.glDisable(GL_CULL_FACE);
            gl.glDisable(GL_LIGHTING);
            gl.glPopMatrix();
        }
    }
    
    private void drawTool(GL2 gl) {

        gl.glCullFace(GL_FRONT);
        drawCircle(gl);
        gl.glCullFace(GL_BACK);
        GraphicsController.glu.gluCylinder(cylinder, scaledRadius, scaledRadius, scaledLength, NUM_VERTICIES, NUM_STACKS);
        gl.glTranslatef(0, 0, scaledLength);
        drawCircle(gl);
        gl.glTranslatef(0, 0, -scaledLength);
    }
    
    private void drawCircle(GL2 gl) {
        gl.glBegin(GL_TRIANGLE_FAN);
        // Draw the vertex at the center of the circle
        gl.glVertex3f(0f, 0f, 0f);
        for(int i = 0; i < NUM_VERTICIES; i++)
        {
          gl.glVertex3d(Math.cos(DELTA_ANGLE * i) * scaledRadius, Math.sin(DELTA_ANGLE * i) * scaledRadius, 0.0);
        }
        gl.glVertex3f(1f * scaledRadius, 0f, 0f);
        gl.glEnd();
    }
}

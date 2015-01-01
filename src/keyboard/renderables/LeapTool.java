package keyboard.renderables;

import static javax.media.opengl.GL.GL_FRONT;
import static javax.media.opengl.GL2.*; // GL2 constants

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLUquadric;

import ui.GraphicsController;

import com.leapmotion.leap.Tool;
import com.leapmotion.leap.Vector;

import enums.Attribute;
import enums.Renderable;
import keyboard.KeyboardAttributes;
import keyboard.KeyboardRenderable;

public class LeapTool extends KeyboardRenderable {
    private static final String RENDER_NAME = Renderable.LEAP_TOOL.toString();
    private static final int NUM_VERTICIES = 32;
    private static final int NUM_STACKS = 32;
    private static final float DELTA_ANGLE = (float) (2.0f * Math.PI / NUM_VERTICIES);
    private static final float RADS_TO_DEGREES = (float) (180 / Math.PI);
    private static final float[] COLOR = {0.85f, 0.7f, 0.41f, 1f};
    private final int DIST_TO_CAMERA;
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
        DIST_TO_CAMERA = keyboardAttributes.getAttributeByName(Attribute.DIST_TO_CAMERA.toString()).getValueAsInteger();
    }
    
    public void createCylinder() {
        GraphicsController.gl.getContext().makeCurrent();
        if(cylinder != null) {
            GraphicsController.glu.gluDeleteQuadric(cylinder);
        }
        cylinder = GraphicsController.glu.gluNewQuadric();
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
    
    public boolean isValid() {
        return isValid;
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
    
    public void scaleTo3DSpace(/*float planeWidth, float planeHeight*/) {
        //scaledLength = (length / iBox.height()) * planeHeight;
        //scaledRadius = (radius / iBox.width()) * planeWidth;
        scaledLength = length;
        scaledRadius = radius;
    }

    @Override
    public void render(GL2 gl) {
        if(isEnabled()) {
            gl.glPushMatrix();
            gl.glEnable(GL_CULL_FACE);
            gl.glEnable(GL_LIGHTING);
            COLOR[3] = (DIST_TO_CAMERA-tipPoint.getZ())/DIST_TO_CAMERA;
            gl.glMaterialfv(GL_FRONT_AND_BACK, GL_AMBIENT_AND_DIFFUSE, COLOR, 0);
            gl.glTranslatef(tipPoint.getX(), tipPoint.getY(), tipPoint.getZ());
            gl.glRotatef(angleToDirection, axisToDirection.getX(), axisToDirection.getY(), axisToDirection.getZ());
            gl.glTranslatef(0, 0, -scaledLength);
            drawTool(gl);
            gl.glDisable(GL_LIGHTING);
            gl.glDisable(GL_CULL_FACE);
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

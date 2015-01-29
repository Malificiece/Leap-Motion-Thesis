package keyboard.renderables;

import static javax.media.opengl.GL.GL_FRONT;
import static javax.media.opengl.GL.GL_LINES;
import static javax.media.opengl.GL2.*; // GL2 constants

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLUquadric;

import ui.GraphicsController;
import utilities.GLColor;
import utilities.MyUtilities;

import com.leapmotion.leap.Tool;
import com.leapmotion.leap.Vector;

import enums.Attribute;
import enums.Color;
import enums.Renderable;
import keyboard.KeyboardAttributes;
import keyboard.KeyboardRenderable;

public class LeapTool extends KeyboardRenderable {
    private static final Renderable TYPE = Renderable.LEAP_TOOL;
    private static final int NUM_VERTICIES = 32;
    private static final int NUM_STACKS = 16;
    private static final float DELTA_ANGLE = (float) (2.0f * Math.PI / NUM_VERTICIES);
    private static final float RADS_TO_DEGREES = (float) (180 / Math.PI);
    private static final GLColor TOOL_COLOR = new GLColor(Color.WOOD);
    private static final GLColor LINE_COLOR = new GLColor(Color.YELLOW);
    private final int DEFAULT_LENGTH = 150;
    private final float DEFAULT_RADIUS = 3.15f;
    private final float CAMERA_DISTANCE;
    private float length;
    private float radius;
    private float scaledLength;
    private float scaledRadius;
    private Vector tipVelocity = Vector.zero();
    private Vector tipDirection = Vector.zero();
    private Vector tipPoint = Vector.zero();
    private boolean isValid = false;
    private float angleToDirection = 0;
    private Vector axisToDirection = tipPoint;
    private Vector upDirection = Vector.zAxis();
    private GLUquadric quadric;
    
    public LeapTool(KeyboardAttributes keyboardAttributes) {
        super(TYPE);
        CAMERA_DISTANCE = keyboardAttributes.getAttributeAsFloat(Attribute.CAMERA_DISTANCE);
    }
    
    private void createQuadric() {
        GraphicsController.gl.getContext().makeCurrent();
        if(quadric != null) {
            GraphicsController.glu.gluDeleteQuadric(quadric);
        }
        quadric = GraphicsController.glu.gluNewQuadric();
        GraphicsController.glu.gluQuadricNormals(quadric, GL_TRUE);
    }
    
    public void deleteQuadric() {
        if(quadric != null) {
            //GraphicsController.gl.getContext().makeCurrent();
            GraphicsController.glu.gluDeleteQuadric(quadric);
        }
    }
    
    public void setTool(Vector direction) {
        length = DEFAULT_LENGTH;
        radius = DEFAULT_RADIUS;
        tipDirection = direction;
    }
    
    public void setTool(Tool tool) {
        isValid = tool.isValid();
        if(isValid) {
            length = tool.length();
            radius = tool.width()/2;
            tipDirection = tool.direction();
        }
        tipVelocity = tool.tipVelocity();
    }
    
    public void update(Vector point) {
        tipPoint = point;
        calculateOrientation();
        scaleTo3DSpace();
    }
    
    public Vector getVelocity() {
        return tipVelocity;
    }
    
    public Vector getDirection() {
        return tipDirection;
    }
    
    public boolean isValid() {
        return isValid;
    }
    
    public void scaleLengthAndWidth(float length, float radius) {
        this.length = length;
        this.radius = radius;
    }
    
    private void calculateOrientation() {
        angleToDirection = upDirection.angleTo(tipDirection) * RADS_TO_DEGREES;
        axisToDirection = upDirection.cross(tipDirection);
        axisToDirection = axisToDirection.divide(axisToDirection.magnitude());
    }
    
    private void scaleTo3DSpace(/*float planeWidth, float planeHeight*/) {
        //scaledLength = (length / iBox.height()) * planeHeight;
        //scaledRadius = (radius / iBox.width()) * planeWidth;
        scaledLength = length;
        scaledRadius = radius;
    }

    @Override
    public void render(GL2 gl) {
        if(isEnabled()) {
            gl.glPushMatrix();
            gl.glTranslatef(tipPoint.getX(), tipPoint.getY(), tipPoint.getZ());
            drawDottedLine(gl);
            gl.glRotatef(angleToDirection, axisToDirection.getX(), axisToDirection.getY(), axisToDirection.getZ());
            gl.glTranslatef(0, 0, -scaledLength);
            gl.glEnable(GL_CULL_FACE);
            gl.glEnable(GL_LIGHTING);
            drawTool(gl);
            gl.glDisable(GL_LIGHTING);
            gl.glDisable(GL_CULL_FACE);
            gl.glPopMatrix();
        }
    }
    
    private void drawTool(GL2 gl) {
        TOOL_COLOR.setAlpha(LINE_COLOR.getAlpha());
        TOOL_COLOR.glColor(gl);
        gl.glCullFace(GL_FRONT);
        gl.glNormal3f(tipDirection.getX(), tipDirection.getY(), tipDirection.getZ());
        drawCircle(gl);
        gl.glCullFace(GL_BACK);
        if(quadric == null) {
            createQuadric();
        }
        GraphicsController.glu.gluCylinder(quadric, scaledRadius, scaledRadius, scaledLength, NUM_VERTICIES, NUM_STACKS);
        gl.glTranslatef(0, 0, scaledLength);
        gl.glNormal3f(-tipDirection.getX(), -tipDirection.getY(), -tipDirection.getZ());
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
    
    private void drawDottedLine(GL2 gl) {
        gl.glNormal3f(0, 0, 1);
        LINE_COLOR.setAlpha((CAMERA_DISTANCE-tipPoint.getZ())/CAMERA_DISTANCE);
        LINE_COLOR.glColor(gl);
        gl.glPushAttrib(GL_ENABLE_BIT);
        gl.glLineWidth(2);
        gl.glLineStipple(1, (short) 0xAAAA);
        gl.glEnable(GL_LINE_STIPPLE);
        gl.glBegin(GL_LINES);
        gl.glVertex3f(0f, 0f, 0f);
        if(tipDirection.isValid()) {
            float dist = MyUtilities.MATH_UTILITILES.findDistanceToPlane(tipPoint.plus(tipDirection), upDirection, 0f);
            Vector tmp = tipDirection.times(dist);
            gl.glVertex3f(tmp.getX(), tmp.getY(), tmp.getZ());
        }
        gl.glEnd();
        gl.glDisable(GL_LINE_STIPPLE);
        gl.glPopAttrib();
    }
}

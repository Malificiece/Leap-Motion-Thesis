package keyboard.renderables;

import java.util.ArrayList;
import java.util.Iterator;

import static javax.media.opengl.GL.*;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_LIGHTING;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLUquadric;

import ui.GraphicsController;
import utilities.GLColor;

import com.leapmotion.leap.Vector;

import enums.Color;
import enums.Gesture;
import enums.Renderable;
import keyboard.KeyboardAttributes;
import keyboard.KeyboardGesture;
import keyboard.KeyboardRenderable;

public class KeyboardGestures extends KeyboardRenderable {
    private static final Renderable TYPE = Renderable.KEYBOARD_GESTURES;
    private static final int NUM_VERTICIES = 32;
    private static final int NUM_STACKS = 32;
    private static final float DELTA_ANGLE = (float) (2.0f * Math.PI / NUM_VERTICIES);
    private static final float ARROW_LINE_THICKNESS = 2f;
    private static final float ARROW_HEAD_THICKNESS = 4f;
    private static final float ARROW_HEAD_LENGTH = 25f;
    private static final GLColor SWIPE_COLOR = new GLColor(Color.TEAL);
    private GLUquadric quadric;
    private ArrayList<KeyboardGesture> gestures = new ArrayList<KeyboardGesture>();

    public KeyboardGestures(KeyboardAttributes keyboardAttributes) {
        super(TYPE);
    }
    
    private void createQuadric() {
        if(quadric != null) {
            GraphicsController.GLU.gluDeleteQuadric(quadric);
        }
        quadric = GraphicsController.GLU.gluNewQuadric();
        GraphicsController.GLU.gluQuadricNormals(quadric, GL_TRUE);
    }
    
    public void deleteQuadric() {
        if(quadric != null) {
            GraphicsController.GLU.gluDeleteQuadric(quadric);
        }
    }
    
    public void addGesture(KeyboardGesture gesture) {
        gestures.add(gesture);
    }
    
    public ArrayList<KeyboardGesture> getGestures() {
        return gestures;
    }
    
    public boolean containsGesture(KeyboardGesture gesture) {
        return gestures.contains(gesture);
    }
    
    public void removeAndUpdateGestures() {
        Iterator<KeyboardGesture> iterator = gestures.iterator();
        while(iterator.hasNext()) {
            KeyboardGesture gesture = (KeyboardGesture) iterator.next();
            if(gesture.isDone()) {
                iterator.remove();
            } else {
                gesture.update();
            }
        }
    }
    
    public void removeFinishedGestures() {
        Iterator<KeyboardGesture> iterator = gestures.iterator();
        while(iterator.hasNext()) {
            KeyboardGesture gesture = (KeyboardGesture) iterator.next();
            if(gesture.isDone()) {
                iterator.remove();
            }
        }
    }
    
    public boolean isEmpty() {
        return gestures.isEmpty();
    }

    @Override
    public void render(GL2 gl) {
        if(isEnabled()) {
            for(KeyboardGesture gesture: gestures) {
                gl.glPushMatrix();
                if(gesture.getType() == Gesture.SWIPE) {
                    gl.glEnable(GL_LIGHTING);
                    gl.glEnable(GL_CULL_FACE);
                    SWIPE_COLOR.setAlpha(gesture.getOpacity());
                    SWIPE_COLOR.glColor(gl);
                    Vector source = gesture.getSource();
                    gl.glTranslatef(source.getX(), source.getY(), source.getZ());
                    Vector axis = gesture.getAxis();
                    gl.glRotatef(gesture.getAngle(), axis.getX(), axis.getY(), axis.getZ());
                    drawArrow(gl, gesture);
                    gl.glDisable(GL_CULL_FACE);
                    gl.glDisable(GL_LIGHTING);
                }
                gl.glPopMatrix();
            }
        }
    }
    
    private void drawArrow(GL2 gl, KeyboardGesture gesture) {
        Vector direction = gesture.getDirection();
        gl.glCullFace(GL_BACK);
        // draw arrow line
        if(quadric == null) {
            createQuadric();
        }
        GraphicsController.GLU.gluCylinder(quadric, ARROW_LINE_THICKNESS, ARROW_LINE_THICKNESS, gesture.getLength(), NUM_VERTICIES, NUM_STACKS);
        gl.glCullFace(GL_FRONT);
        gl.glNormal3f(direction.getX(), direction.getY(), direction.getZ());
        drawCircle(gl, ARROW_LINE_THICKNESS);
        // draw arrow head
        gl.glCullFace(GL_BACK);
        gl.glTranslatef(0, 0, gesture.getLength());
        GraphicsController.GLU.gluCylinder(quadric, ARROW_HEAD_THICKNESS, 0, ARROW_HEAD_LENGTH, NUM_VERTICIES, NUM_STACKS);
        gl.glCullFace(GL_FRONT);
        gl.glNormal3f(direction.getX(), direction.getY(), direction.getZ());
        drawCircle(gl, ARROW_HEAD_THICKNESS);
    }
    
    private void drawCircle(GL2 gl, float radius) {
        gl.glBegin(GL_TRIANGLE_FAN);
        // Draw the vertex at the center of the circle
        gl.glVertex3f(0f, 0f, 0f);
        for(int i = 0; i < NUM_VERTICIES; i++)
        {
          gl.glVertex3d(Math.cos(DELTA_ANGLE * i) * radius, Math.sin(DELTA_ANGLE * i) * radius, 0.0);
        }
        gl.glVertex3f(1f * radius, 0f, 0f);
        gl.glEnd();
    }
}

package keyboard.renderables;

import java.util.ArrayList;
import java.util.Iterator;

import static javax.media.opengl.GL.*;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_AMBIENT_AND_DIFFUSE;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_LIGHTING;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLUquadric;

import ui.GraphicsController;

import com.leapmotion.leap.Vector;

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
    private final float[] COLOR = {0f, 1f, 0.5f, 1f};
    private GLUquadric quadric;
    private ArrayList<KeyboardGesture> gestures = new ArrayList<KeyboardGesture>();

    public KeyboardGestures(KeyboardAttributes keyboardAttributes) {
        super(TYPE);
    }
    
    public void createQuadric() {
        GraphicsController.gl.getContext().makeCurrent();
        if(quadric != null) {
            GraphicsController.glu.gluDeleteQuadric(quadric);
        }
        quadric = GraphicsController.glu.gluNewQuadric();
        GraphicsController.glu.gluQuadricNormals(quadric, GL_TRUE);
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
                    COLOR[3] = gesture.getOpacity();
                    gl.glMaterialfv(GL_FRONT_AND_BACK, GL_AMBIENT_AND_DIFFUSE, COLOR, 0);
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
        gl.glCullFace(GL_BACK);
        // draw arrow line
        GraphicsController.glu.gluCylinder(quadric, ARROW_LINE_THICKNESS, ARROW_LINE_THICKNESS, gesture.getLength(), NUM_VERTICIES, NUM_STACKS);
        gl.glCullFace(GL_FRONT);
        drawCircle(gl, ARROW_LINE_THICKNESS);
        // draw arrow head
        gl.glCullFace(GL_BACK);
        gl.glTranslatef(0, 0, gesture.getLength());
        GraphicsController.glu.gluCylinder(quadric, ARROW_HEAD_THICKNESS, 0, ARROW_HEAD_LENGTH, NUM_VERTICIES, NUM_STACKS);
        gl.glCullFace(GL_FRONT);
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

package keyboard.renderables;

import static javax.media.opengl.GL.GL_LINES;

import java.util.ArrayList;

import javax.media.opengl.GL2;

import utilities.GLColor;
import utilities.MathUtilities;
import utilities.MyUtilities;
import keyboard.KeyboardAttributes;
import keyboard.KeyboardRenderable;

import com.leapmotion.leap.Vector;

import enums.Attribute;
import enums.Color;
import enums.Renderable;

public class SwipeTrail extends KeyboardRenderable {
    private static final Renderable TYPE = Renderable.SWIPE_TRAIL;
    private final GLColor TRAIL_COLOR = new GLColor(Color.YELLOW);
    //private final GLColor INTERPOLATED_COLOR = new GLColor(Color.RED);
    //private final GLColor PRESSED_COLOR = new GLColor(Color.CYAN);
    //private static final int NUM_VERTICIES = 32;
    //private static final float DELTA_ANGLE = (float) (2.0f * Math.PI / NUM_VERTICIES);
    //private static final float RADIUS = 5f;
    private final int SHRINK_DURATION = 250;
    private final float LINE_WIDTH = 7;
    private final int LINE_SIZE = 50;
    //private final int PRESSED_SIZE = 3;
    private Vector[] line;
    private int lineIndex = 0;
    private boolean isTouching = false;
    private boolean isShrinking = false;
    private boolean isCleared = false;
    private float lineWidth = LINE_WIDTH;
    private long previousTime;
    private long fadeTimeElapsed = 0;
    
    // Variables used in determining if our movements constitute a press event
    // TODO: Convert these to settings we can save/modify with the program.
    private final int MIN_INTERPOLATED_DISTANCE;
    private final int MIN_PRESSED_DISTANCE;
    private final float MIN_PRESSED_ANGLE_OFF_PATH = 165 * MathUtilities.DEGREES_TO_RADS;
    private final float MIN_PRESSED_ANGLE_ON_PATH = 90 * MathUtilities.DEGREES_TO_RADS; // 115 good, but still hit false positives, 90 almost gets rid of false positives
    private final int MIN_EXPECTED_PATH_DISTANCE;
    private Vector expectedPathSource = Vector.zero();
    private Vector expectedPathDestination = Vector.zero();
    private float pathDistance = 0;
    private boolean isPressed;
    private Vector lastPoint;
    private ArrayList<Vector> interpolatedPoints = new ArrayList<Vector>();
    private ArrayList<Vector> pressedPoints = new ArrayList<Vector>();
    

    public SwipeTrail(KeyboardAttributes keyboardAttributes) {
        super(TYPE);
        int keyWidth = keyboardAttributes.getAttributeAsPoint(Attribute.KEY_SIZE).x;
        MIN_INTERPOLATED_DISTANCE = (int) (keyWidth * 0.25f); // 16
        MIN_PRESSED_DISTANCE = (int) (keyWidth * 0.75f); // 48
        MIN_EXPECTED_PATH_DISTANCE = (int) (keyWidth * 0.8125f); // 52
        line = new Vector[LINE_SIZE];
    }
    
    public void update(Vector point) {
        if(!isTouching) {
            if(!isCleared) {
                clearTrail();
            }
            isTouching = true;
            isShrinking = false;
            isCleared = false;
            lineWidth = LINE_WIDTH;
        }
        point = new Vector(point.getX(), point.getY(), 0f);
        line[lineIndex = ++lineIndex % LINE_SIZE] = point;
        checkPressed(point);
    }

    public void update() {
        if(isTouching) {
            isTouching = false;
            isShrinking = true;
            checkPressed();
            previousTime = System.currentTimeMillis();
            fadeTimeElapsed = 0;
        } else if(isShrinking) {
            long now = System.currentTimeMillis();
            fadeTimeElapsed += now - previousTime;
            previousTime = now;
            
            if(fadeTimeElapsed <= SHRINK_DURATION) {
                lineWidth = (1f - fadeTimeElapsed/(float)SHRINK_DURATION)*LINE_WIDTH;
            } else {
                isShrinking = false;
            }
        } else if(!isCleared) {
            clearTrail();
        }
    }
    
    private void clearTrail() {
        for(int i = 0; i < LINE_SIZE; i++) {
            line[i] = null;
        }
        lineIndex = 0;
        isCleared = true;
        interpolatedPoints.clear();
        pressedPoints.clear();
        isPressed = false;
        lastPoint = null;
    }

    @Override
    public void render(GL2 gl) {
        if(isEnabled()) {
            gl.glPushMatrix();
            gl.glNormal3f(0, 0, 1);
            TRAIL_COLOR.glColor(gl);
            gl.glLineWidth(lineWidth);
            for(int i = 0; i < LINE_SIZE; i++) {
                if(i != lineIndex) {
                    int j = (i + 1) % LINE_SIZE;
                    if(line[i] != null && line[j] != null) {
                        drawLine(gl, line[i], line[j]);
                    }
                }
            }
            
            // Draw interpolated line.
            /*INTERPOLATED_COLOR.glColor(gl);
            gl.glLineWidth(1);
            for(int i = 0; i < interpolatedPoints.size() - 1; i++) {
                int j = i + 1;
                Vector v = interpolatedPoints.get(i);
                drawDottedLine(gl, v, interpolatedPoints.get(j));
                gl.glPushMatrix();
                gl.glTranslatef(v.getX(), v.getY(), v.getZ());
                drawPoint(gl);
                gl.glPopMatrix();
            }
            if(!interpolatedPoints.isEmpty()) {
                Vector v = getLast(interpolatedPoints);
                drawDottedLine(gl, v, line[lineIndex]);
                gl.glPushMatrix();
                gl.glTranslatef(v.getX(), v.getY(), v.getZ());
                drawPoint(gl);
                gl.glPopMatrix();
            }*/
            
            // Draw interpreted key presses.
            /*PRESSED_COLOR.glColor(gl);
            gl.glLineWidth(1);
            for(int i = (pressedPoints.size() >= PRESSED_SIZE ? pressedPoints.size() - PRESSED_SIZE : 0); i < pressedPoints.size() - 1; i++) {
                int j = i + 1;
                Vector v = pressedPoints.get(i);
                drawDottedLine(gl, v, pressedPoints.get(j));
                gl.glPushMatrix();
                gl.glTranslatef(v.getX(), v.getY(), v.getZ());
                drawPoint(gl);
                gl.glPopMatrix();
            }
            if(!pressedPoints.isEmpty()) {
                Vector v = getLast(pressedPoints);
                drawDottedLine(gl, v, line[lineIndex]);
                gl.glPushMatrix();
                gl.glTranslatef(v.getX(), v.getY(), v.getZ());
                drawPoint(gl);
                gl.glPopMatrix();
            }*/
            gl.glPopMatrix();
        }
    }
    
    private void drawLine(GL2 gl, Vector source, Vector dest) {
        gl.glBegin(GL_LINES);
        gl.glVertex3f(source.getX(), source.getY(), source.getZ());
        gl.glVertex3f(dest.getX(), dest.getY(), dest.getZ());
        gl.glEnd();
    }
    
    /*private void drawDottedLine(GL2 gl, Vector source, Vector dest) {
        gl.glPushAttrib(GL_ENABLE_BIT);
        gl.glLineWidth(2);
        gl.glLineStipple(1, (short) 0xAAAA);
        gl.glEnable(GL_LINE_STIPPLE);
        gl.glBegin(GL_LINES);
        gl.glVertex3f(source.getX(), source.getY(), source.getZ());
        gl.glVertex3f(dest.getX(), dest.getY(), dest.getZ());
        gl.glEnd();
        gl.glDisable(GL_LINE_STIPPLE);
        gl.glPopAttrib();
    }
    
    private void drawPoint(GL2 gl) {
        gl.glBegin(GL_TRIANGLE_FAN);
        // Draw the vertex at the center of the circle
        gl.glVertex3f(0f, 0f, 0f);
        for(int i = 0; i < NUM_VERTICIES; i++)
        {
          gl.glVertex3d(Math.cos(DELTA_ANGLE * i) * RADIUS, Math.sin(DELTA_ANGLE * i) * RADIUS, 0.0);
        }
        gl.glVertex3f(1f * RADIUS, 0f, 0f);
        gl.glEnd();
    }*/
    
    private void checkPressed() {
        if(lastPoint != null) {
            pressedPoints.add(lastPoint);
            interpolatedPoints.add(lastPoint);
            isPressed = false;
            //System.out.println("released");
        }
    }
    
    public void setExpectedPath(Vector source, Vector destination) {
        expectedPathSource = source;
        expectedPathDestination = destination;
        pathDistance = MyUtilities.MATH_UTILITILES.findDistanceToPoint(expectedPathSource, expectedPathDestination);
    }
    
    public float getPathDistance() {
        return pathDistance;
    }
    
    private void checkPressed(Vector point) {
        if(pressedPoints.isEmpty()) {
            pressedPoints.add(point);
            interpolatedPoints.add(point);
            //System.out.println("pressed");
        } else if(interpolatedPoints.size() < 2) {
            interpolatedPoints.add(point);
        } else if(MyUtilities.MATH_UTILITILES.findDistanceToPoint(getLast(interpolatedPoints), point) > MIN_INTERPOLATED_DISTANCE) {
            // find the thresholds needed to determine a press here!
            if(MyUtilities.MATH_UTILITILES.findDistanceToPoint(getLast(pressedPoints), getLast(interpolatedPoints)) > MIN_PRESSED_DISTANCE) {
                Vector AB = getLast(interpolatedPoints).minus(point); // B - A
                Vector CB = getLast(interpolatedPoints).minus(getSecondLast(interpolatedPoints)); // B - C
                float angle = AB.angleTo(CB);
                boolean onPath = MyUtilities.MATH_UTILITILES.findDistanceToLine(point, expectedPathSource, expectedPathDestination) <= MIN_EXPECTED_PATH_DISTANCE;
                if(angle < (onPath ? MIN_PRESSED_ANGLE_ON_PATH : MIN_PRESSED_ANGLE_OFF_PATH)) {
                    pressedPoints.add(getLast(interpolatedPoints));
                    isPressed = true;
                    //System.out.println("SWIPE_TRAIL: detected angle press"); // TODO: REMOVE ME
                }
            }
            interpolatedPoints.add(point);
        }
        lastPoint = point;
    }
    
    private Vector getLast(ArrayList<Vector> arrayList) {
        return arrayList.get(arrayList.size() - 1);
    }
    
    private Vector getSecondLast(ArrayList<Vector> arrayList) {
        return arrayList.get(arrayList.size() - 2);
    }

    public Vector isPressed() {
        try {
            if(isPressed && !pressedPoints.isEmpty()) {
                return getLast(pressedPoints);
            }
            return Vector.zero();
            
        } finally {
            // consumed pressed event
            isPressed = false;
        }
    }
    
    public void setPressedPoint(Vector point) {
        pressedPoints.add(point);
        isPressed = false;
    }
    
    public boolean detectPressed() {
        return isPressed;
    }
}

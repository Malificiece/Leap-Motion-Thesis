package keyboard.renderables;

import java.util.ArrayList;

import javax.media.opengl.GL2;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import static javax.media.opengl.GL.GL_TRIANGLE_FAN;
import static javax.media.opengl.GL2GL3.GL_QUADS;
import utilities.MyUtilities;

import com.leapmotion.leap.InteractionBox;
import com.leapmotion.leap.Vector;

import enums.Attribute;
import enums.Renderable;
import enums.Setting;
import keyboard.CalibrationObserver;
import keyboard.IKeyboard;
import keyboard.KeyboardAttribute;
import keyboard.KeyboardRenderable;
import keyboard.KeyboardSetting;

public class LeapPlane extends KeyboardRenderable {
    private static final String RENDER_NAME = Renderable.LEAP_PLANE.toString();
    private static final float[] COLOR = {0.4f, 0.7f, 1f, 1f};
    private static final int NUM_VERTICIES = 32;
    private static final float DELTA_ANGLE = (float) (2.0f * Math.PI / NUM_VERTICIES);
    private static final float RADIUS = 10f;
    private final KeyboardSetting TOUCH_THRESHOLD; // -0.10f; normalized // -10.0f; not normalized
    private final int KEYBOARD_WIDTH;
    private final int KEYBOARD_HEIGHT;
    private final int DIST_TO_CAMERA;
    private final KeyboardAttribute POINT_A_ATTRIBUTE;
    private final KeyboardAttribute POINT_B_ATTRIBUTE;
    private final KeyboardAttribute POINT_C_ATTRIBUTE;
    private ArrayList<CalibrationObserver> observers = new ArrayList<CalibrationObserver>();
    private InteractionBox iBox;
    private Vector pointA; // min
    private Vector pointB; // other
    private Vector pointC; // max
    private Vector pointD; // calculated
    private Vector scaledPointA;
    private Vector scaledPointB;
    private Vector scaledPointC;
    private Vector scaledPointD;
    private Vector planeNormal;
    private Vector planeCenter;
    private float distanceToCameraPlane;
    private float planeD;
    private float angleToCamera;
    private Vector axisToCamera;
    private boolean isCalibrated = false;
    private boolean isCalibrating = false;
    private float distanceToPlane;
    private float normalizedPlaneWidth;
    private float normalizedPlaneHeight;
    private float planeWidth;
    private float planeHeight;
    private Vector BA;
    private Vector DA;
    private Vector intersectionPoint = new Vector();
    private float x, y, z = -1;
    private JPanel textPanel;
    private JTextArea explinationArea;
    
    public LeapPlane(IKeyboard keyboard) {
        super(RENDER_NAME);
        KEYBOARD_WIDTH = keyboard.getAttributes().getAttributeByName(Attribute.KEYBOARD_WIDTH.toString()).getValueAsInteger();
        KEYBOARD_HEIGHT = keyboard.getAttributes().getAttributeByName(Attribute.KEYBOARD_HEIGHT.toString()).getValueAsInteger();
        DIST_TO_CAMERA = keyboard.getAttributes().getAttributeByName(Attribute.DIST_TO_CAMERA.toString()).getValueAsInteger();
        TOUCH_THRESHOLD = keyboard.getSettings().getSettingByName(Setting.TOUCH_THRESHOLD.toString());
        POINT_A_ATTRIBUTE = keyboard.getAttributes().getAttributeByName(Attribute.LEAP_PLANE_POINT_A.toString());
        POINT_B_ATTRIBUTE = keyboard.getAttributes().getAttributeByName(Attribute.LEAP_PLANE_POINT_B.toString());
        POINT_C_ATTRIBUTE = keyboard.getAttributes().getAttributeByName(Attribute.LEAP_PLANE_POINT_C.toString());
        getPlaneAttributes();
        if(pointA != null && pointB != null && pointC != null) {
            isCalibrated = true;
        } else {
            isCalibrated = false;
            pointA = new Vector();
            pointB = new Vector();
            pointC = new Vector();
            // Apply points to attributes.
            setPlaneAttributes();
        }
        calculatePlaneData();
        createExplinationArea();
    }
    
    private void createExplinationArea() {
        explinationArea = new JTextArea();
        explinationArea.setWrapStyleWord(true);
    }

    public void setInteractionBox(InteractionBox iBox) {
        this.iBox = iBox;
        calculatePlaneData();
    }
    
    private void getPlaneAttributes() {
        pointA = POINT_A_ATTRIBUTE.getValueAsVector();
        pointB = POINT_B_ATTRIBUTE.getValueAsVector();
        pointC = POINT_C_ATTRIBUTE.getValueAsVector();
    }
    
    private void setPlaneAttributes() {
        POINT_A_ATTRIBUTE.setVectorValue(pointA);
        POINT_B_ATTRIBUTE.setVectorValue(pointB);
        POINT_C_ATTRIBUTE.setVectorValue(pointC);
    }
    
    public void beginCalibration(JPanel textPanel) {
        // Calibrate the leap plane finding the min corner, third corner, and max corner.
        // MAKE SURE TO FORCE THE POINTS TO BE RIGHT ANGLE FOR X and Y. Will make things 10000x easier
        
        // populate frame with text field describing what we need to do
        // give instructions -- find average point of stick as it is moving in leap space
        // grab average point -- render points as they are grabbed
        this.textPanel = textPanel;
        pointA = new Vector();
        pointB = new Vector();
        pointC = new Vector();
        isCalibrating = true;
        isCalibrated = false;
    }
    
    public void finishCalibration() {
        
        // Remove everything we added to the textPanel.
        textPanel.removeAll();
        
        // Notify the calibration controller that we are done calibrating.
        isCalibrating = false;
        notifyListenersCalibrationFinished();
        
        // Write the newly calibrated coordinates to file.
        // add read from file for attributes on creation
        // add write to file here as well as in IKeyboard for attributes
    }
    
    public boolean isCalibrated() {
        return isCalibrated;
    }
    
    public float getDistance() {
        return distanceToPlane;
    }
    
    public boolean isTouching() {
        if(distanceToPlane > TOUCH_THRESHOLD.getValue()) {
            return true;
        }
        return false;
    }
    
    public boolean isValid() {
        if(x == -1 || y == -1 || !isCalibrated) {
            return false;
        }
        return true;
    }
    
    private void calculatePlaneData() {
        // Get the original attributes for recalculation.
        getPlaneAttributes();
        
        // Find the center of the plane
        planeCenter = MyUtilities.MATH_UTILITILES.midpoint(pointA, pointC);
        
        // Determine the vectors
        Vector AB = pointB.minus(pointA);
        Vector AC = pointC.minus(pointA);
        
        // Determine the normal of the plane by finding the cross product
        planeNormal = AB.cross(AC);
        
        // Find D, a simple variable that holds our normal time's a point on the plane
        planeD = MyUtilities.MATH_UTILITILES.calcPlaneD(planeNormal, planeCenter);
        
        // Compute the 4th point of the plane for drawing.
        pointD = pointA.minus(pointB).plus(pointC);
        
        // Calculate the axis and angle to the camera.
        Vector n = new Vector(0,0,-1);
        angleToCamera = planeNormal.angleTo(n);
        axisToCamera = planeNormal.cross(n).divide(planeNormal.cross(n).magnitude());
        
        // Calculate the width and height of the plane in real world space.
        //planeWidth = MyUtilities.MATH_UTILITILES.findDistanceToPoint(pointB, pointC);
        //planeHeight = MyUtilities.MATH_UTILITILES.findDistanceToPoint(pointB, pointA);
        
        // Rotate all points to face the camera.
        pointA = MyUtilities.MATH_UTILITILES.rotateVector(pointA, axisToCamera, angleToCamera);
        pointB = MyUtilities.MATH_UTILITILES.rotateVector(pointB, axisToCamera, angleToCamera);
        pointC = MyUtilities.MATH_UTILITILES.rotateVector(pointC, axisToCamera, angleToCamera);
        pointD = MyUtilities.MATH_UTILITILES.rotateVector(pointD, axisToCamera, angleToCamera);
        planeCenter = MyUtilities.MATH_UTILITILES.rotateVector(planeCenter, axisToCamera, angleToCamera);
        
        // Normalize points in the leap box.
        if(iBox != null) {
            pointA = iBox.normalizePoint(pointA);
            pointB = iBox.normalizePoint(pointB);
            pointC = iBox.normalizePoint(pointC);
            pointD = iBox.normalizePoint(pointD);
            planeCenter = iBox.normalizePoint(planeCenter);
        }
        
        // Recalculate normalized plane
        // Determine the vectors
        AB = pointB.minus(pointA);
        AC = pointC.minus(pointA);
        
        // Determine the normal of the plane by finding the cross product
        planeNormal = AB.cross(AC);
        
        // Find D, a simple variable that holds our normal time's a point on the plane
        planeD = MyUtilities.MATH_UTILITILES.calcPlaneD(planeNormal, planeCenter);
        
        // Precalculate side vectors needed for finding keyboard position.
        BA = pointA.minus(pointB);
        DA = pointA.minus(pointD);
        
        // Calculate the width and height of the normalized plane we created.
        normalizedPlaneWidth = MyUtilities.MATH_UTILITILES.findDistanceToPoint(pointB, pointC);
        normalizedPlaneHeight = MyUtilities.MATH_UTILITILES.findDistanceToPoint(pointB, pointA);
        //distanceToCameraPlane = Math.abs((iBox.normalizePoint(iBox.center()).getZ() - 0.5f) - planeCenter.getZ());
        distanceToCameraPlane = 1 - planeCenter.getZ();
        
        // Scale the plane to 3D space.
        scaledPointA = scaleTo3DSpace(pointA);
        scaledPointB = scaleTo3DSpace(pointB);
        scaledPointC = scaleTo3DSpace(pointC);
        scaledPointD = scaleTo3DSpace(pointD);
        
        // Move the normalized points to the origin.
        scaledPointB = scaledPointB.minus(scaledPointA);
        scaledPointC = scaledPointC.minus(scaledPointA);
        scaledPointD = scaledPointD.minus(scaledPointA);
        scaledPointA = scaledPointA.minus(scaledPointA);
        
        // Calculate the width and height of the scaled plane in 3D space.
        planeWidth = MyUtilities.MATH_UTILITILES.findDistanceToPoint(scaledPointB, scaledPointC);
        planeHeight = MyUtilities.MATH_UTILITILES.findDistanceToPoint(scaledPointB, scaledPointA);
    }
    
    private Vector scaleTo3DSpace(Vector point) {
        Vector vector = new Vector();
        vector.setX(point.getX() * KEYBOARD_WIDTH);
        vector.setY(point.getY() * KEYBOARD_HEIGHT);
        vector.setZ(point.getZ() * DIST_TO_CAMERA);
        return vector;
    }
    
    private void calcDistToPlane(Vector point) {
        distanceToPlane = MyUtilities.MATH_UTILITILES.findDistanceToPlane(point, planeNormal, planeD);
    }
    
    private void applyPlaneNormalization(Vector point) {
        // Since all of the Z's should be equal for the plane (this is post rotation). We can throw them out and
        // solve for LeMothe's 2D intersecting line equations instead. Makes things much easier.
        
        // Find horizontal position.
        // r1(t) = (point) + t(DA)
        // r2(s) = (B) + s(BA)
        // Find distance to intersection point and divide by planeWidth for %.
        if(MyUtilities.MATH_UTILITILES.findLineIntersection(intersectionPoint, point, DA, pointB, BA)) {
            x = MyUtilities.MATH_UTILITILES.findDistanceToPoint(point, intersectionPoint)/normalizedPlaneWidth;
            if(x > 1) {x = 1;} else if(x < 0) {x = 0;}
        } else {
            x = -1;
        }
        
        // Find vertical position.
        // r1(t) = (point) + t(BA)
        // r2(s) = (D) + s(DA)
        // Find distance to intersection point and divide by planeHeight for %.
        if(MyUtilities.MATH_UTILITILES.findLineIntersection(intersectionPoint, point, BA, pointD, DA)) {
            y = MyUtilities.MATH_UTILITILES.findDistanceToPoint(point, intersectionPoint)/normalizedPlaneHeight;
            if(y > 1) {y = 1;} else if(y < 0) {y = 0;}
        } else {
            y = -1;
        }
        
        // Use planeCenter and point's Z's to determine %.
        z = (point.getZ() - planeCenter.getZ()) / distanceToCameraPlane;
        if(z > 1) {z = 1;} else if(z < 0) {z = 0;}
        
        // Apply X, Y, and Z to point.
        point.setX(x);
        point.setY(y);
        point.setZ(z);
    }
    
    public void update(LeapPoint leapPoint, LeapTool leapTool, LeapGesture leapGesture) {
        if(isCalibrating && isCalibrated) { // means we just finished
            finishCalibration();
        }
        if(isCalibrated) {
            // Find point distance, normalize it, and position it.
            leapPoint.applyPlaneRotationAndNormalizePoint(axisToCamera, angleToCamera);
            calcDistToPlane(leapPoint.getNormalizedPoint());
            applyPlaneNormalization(leapPoint.getNormalizedPoint());
            leapPoint.scaleTo3DSpace();
            
            // Set tool point, scale it, rotate and position it.
            leapTool.setPoint(leapPoint.getNormalizedPoint());
            leapTool.calculateOrientation();
            leapTool.scaleTo3DSpace(/*planeWidth, planeHeight*/);
            
            // Set gesture location and scale it.
        } else if (isCalibrating) {
            // average current point with last point
            // detect stabilized point position average hasn't changed by EPSILON_THRESHOLD --- tiny number

            
            pointA = new Vector(-57.324f, 138.28f, -32.742f); // min
            pointB = new Vector(-60.117f, 196.815f, -28.5863f); // third
            pointC = new Vector(37.5257f, 196.318f, -26.0687f); // max
            
            // Apply points to attributes.
            setPlaneAttributes();
            calculatePlaneData();
            isCalibrated = true;
        }
    }

    @Override
    public void render(GL2 gl) {
        if(isEnabled()) {
            gl.glPushMatrix();
            gl.glColor4fv(COLOR, 0);
            gl.glTranslatef(KEYBOARD_WIDTH/2f-planeWidth/2f, KEYBOARD_HEIGHT/2f-planeHeight/2f, -10f);
            drawRectangle(gl);
            
            //drawPoint(gl);
            gl.glPopMatrix();
        }
    }
    
    private void drawPoint(GL2 gl) {
        gl.glColor4fv(COLOR, 0);
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
    
    private void drawRectangle(GL2 gl) {
        // Move points to origin.
        gl.glBegin(GL_QUADS);
        gl.glVertex3f(scaledPointA.getX(), scaledPointA.getY(), scaledPointA.getZ());
        gl.glVertex3f(scaledPointB.getX(), scaledPointB.getY(), scaledPointB.getZ());
        gl.glVertex3f(scaledPointC.getX(), scaledPointC.getY(), scaledPointC.getZ());
        gl.glVertex3f(scaledPointD.getX(), scaledPointD.getY(), scaledPointD.getZ());
        gl.glEnd();
    }
    
    public void registerObserver(CalibrationObserver observer) {
        if(observers.contains(observer)) {
            return;
        }
        observers.add(observer);
    }
    
    public void removeObserver(CalibrationObserver observer) {
        observers.remove(observer);
    }
    
    protected void notifyListenersCalibrationFinished() {
        for(CalibrationObserver observer : observers) {
            observer.keyboardCalibrationFinishedEventObserved();
        }
    }
}

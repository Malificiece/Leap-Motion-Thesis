package keyboard.renderables;

import javax.media.opengl.GL2;
import static javax.media.opengl.GL2GL3.GL_QUADS;
import static javax.media.opengl.GL2GL3.GL_LINES;

import utilities.MyUtilities;

import com.leapmotion.leap.InteractionBox;
import com.leapmotion.leap.Vector;

import enums.AttributeName;
import enums.RenderableName;
import keyboard.KeyboardAttributes;
import keyboard.KeyboardRenderable;

public class LeapPlane extends KeyboardRenderable {
    private static final String RENDER_NAME = RenderableName.LEAP_PLANE.toString();
    private static final float EPSILON = 0.01f;
    private static final float TOUCH_THRESHOLD =  -5.0f;
    private final int KEYBOARD_WIDTH;
    private final int KEYBOARD_HEIGHT;
    private final int DIST_TO_CAMERA;
    InteractionBox iBox;
    // TODO: Need to use calibration to detect these points and save these points to file once recorded so we only have to calibrate occasionally
    // other Leap Point: (-60.117, 196.815, -28.5863)   Normalized: (0.244452, 0.48646, 0.306524)
    // min Leap Point: (-57.324, 138.28, -32.742)   Normalized: (0.256324, 0.237636, 0.278398)
    // max Leap Point: (37.5257, 196.318, -26.0687)   Normalized: (0.659516, 0.48435, 0.323563)
    private Vector pointA; // min
    private Vector pointB; // other
    private Vector pointC; // max
    private Vector pointD; // calculated
    private Vector planeNormal;
    private Vector planeCenter;
    private float planeD;
    private float angleToCamera;
    private Vector axisToCamera;
    private boolean calibrated = false;
    private float distanceToPlane;
    private float planeWidth;
    private float planeHeight;
    
    public LeapPlane(KeyboardAttributes keyboardAttributes) {
        super(RENDER_NAME);
        KEYBOARD_WIDTH = keyboardAttributes.getAttributeByName(AttributeName.KEYBOARD_WIDTH.toString()).getValueAsInteger();
        KEYBOARD_HEIGHT = keyboardAttributes.getAttributeByName(AttributeName.KEYBOARD_HEIGHT.toString()).getValueAsInteger();
        DIST_TO_CAMERA = keyboardAttributes.getAttributeByName(AttributeName.DIST_TO_CAMERA.toString()).getValueAsInteger();
        // read points from file
    }
    
    public void setInteractionBox(InteractionBox iBox) {
        this.iBox = iBox;
    }
    
    public void calibrate() {
        // Calibrate the leap plane finding the min corner, third corner, and max corner.
        // We can then use these to set up the plane
        // the depth/thickness of the plane just depends on what we want it to be.
        // try to float just above the surface (5 mm maybe?) and then have it go deep into the binder
        
        // MAKE SURE TO FORCE THE POINTS TO BE RIGHT ANGLE FOR X and Y. Will make things 10000x easier
        
        // populate frame with canvas/text field
        // give instructions
        // grab average point -- render points as they are grabbed
        // save points to file
        pointA = new Vector(-57.324f, 138.28f, -32.742f); // min
        pointB =  new Vector(-60.117f, 196.815f, -28.5863f); // third
        pointC = new Vector(37.5257f, 196.318f, -26.0687f); // max
        calculatePlaneData();
        calibrated = true;
    }
    
    public boolean isCalibrated() {
        return calibrated;
    }
    
    public float getDistance() {
        return distanceToPlane;
    }
    
    public boolean isTouching(Vector point) {
        if(distanceToPlane > TOUCH_THRESHOLD) {
            return true;
        }
        return false;
    }
    
    private void calculatePlaneData() {
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
        
        // Calculate the width and height of the plane we created.
        planeWidth = MyUtilities.MATH_UTILITILES.findDistanceToPoint(pointB, pointC);
        planeHeight = MyUtilities.MATH_UTILITILES.findDistanceToPoint(pointB, pointA);
        
        // TEMP: force right angle
        
        /*pointA.setX(pointB.getX());
        pointA.setY(pointB.getY() - planeHeight);
        
        pointD.setX(pointC.getX());
        pointD.setY(pointC.getY() - planeHeight);
        System.out.println("A: " + pointA);
        System.out.println("B: " + pointB);
        System.out.println("C: " + pointC);
        System.out.println("D: " + pointD);*/
        
        // Calculate the axis and angle to the camera.
        Vector n = new Vector(0,0,-1);
        angleToCamera = planeNormal.angleTo(n);
        axisToCamera = planeNormal.cross(n).divide(planeNormal.cross(n).magnitude());
        
        System.out.println("perpendicular test: " + (EPSILON > planeNormal.dot(pointC.minus(pointA))));
        
        // Rotate all points to face the camera.
        pointA = MyUtilities.MATH_UTILITILES.rotateVector(pointA, axisToCamera, angleToCamera);
        pointB = MyUtilities.MATH_UTILITILES.rotateVector(pointB, axisToCamera, angleToCamera);
        pointC = MyUtilities.MATH_UTILITILES.rotateVector(pointC, axisToCamera, angleToCamera);
        pointD = MyUtilities.MATH_UTILITILES.rotateVector(pointD, axisToCamera, angleToCamera);
        planeCenter = MyUtilities.MATH_UTILITILES.rotateVector(planeCenter, axisToCamera, angleToCamera);
        
        // Normalize points in the leap box.
        //pointA = iBox.normalizePoint(pointA);
        //pointB = iBox.normalizePoint(pointB);
        //pointC = iBox.normalizePoint(pointC);
        //pointD = iBox.normalizePoint(pointD);
        
        
        // for our point, find a parallel vector to top or bot that runs through this point
        // the parallel vector of AB is just AB using whatever point we have, we'll find the intersection of the other vector BC etc
        System.out.println("A: " + pointA);
        System.out.println("B: " + pointB);
        System.out.println("Center: " + planeCenter);
        Vector BtoA = pointB.minus(pointA);
        System.out.println("BtoA: " + BtoA);
        Vector BtoC = pointB.minus(pointC);
        System.out.println(BtoA.angleTo(BtoC));
        
        //find distance to right side and divide by planeWidth for %
        
    }
    
    private void calcDistToPlane(Vector point) {
        distanceToPlane = MyUtilities.MATH_UTILITILES.findDistanceToPlane(point, planeNormal, planeD);
        System.out.println(distanceToPlane);
    }
    
    private void applyPlaneNormalization(Vector point) {
        //normalizedPointA
    }
    
    public void update(LeapPoint leapPoint) {
        calcDistToPlane(leapPoint.getPoint());
        leapPoint.applyPlaneRotationAndNormalizePoint(axisToCamera, angleToCamera);
        applyPlaneNormalization(leapPoint.getNormalizedPoint());
        leapPoint.applyPlaneNormalization();
        // rotate tool here
        // rotate gestures here
        //normalizedPoint.setX(normalizedPoint.getX() * KEYBOARD_WIDTH);
        //normalizedPoint.setY(normalizedPoint.getY() * KEYBOARD_HEIGHT);
        //normalizedPoint.setZ(normalizedPoint.getZ() * DIST_TO_CAMERA);
    }

    @Override
    public void render(GL2 gl) {
        if(isEnabled()) {
            gl.glPushMatrix();
            //gl.glRotatef(angle, x, y, z);
            gl.glColor3f(0.5f, 0.5f, 0.5f);
            gl.glTranslatef(KEYBOARD_WIDTH/2f-planeWidth/2f, KEYBOARD_HEIGHT/2f-planeHeight/2f, 0f);
            
            // Move points to origin.
            Vector tmpA = pointA.minus(pointA);
            Vector tmpB = pointB.minus(pointA);
            Vector tmpC = pointC.minus(pointA);
            Vector tmpD = pointD.minus(pointA);
            gl.glBegin(GL_QUADS);
            gl.glVertex3f(pointA.getX(), pointA.getY(), pointA.getZ());
            gl.glVertex3f(pointB.getX(), pointB.getY(), pointB.getZ());
            gl.glVertex3f(pointC.getX(), pointC.getY(), pointC.getZ());
            gl.glVertex3f(pointD.getX(), pointD.getY(), pointD.getZ());
            gl.glEnd();
            gl.glPopMatrix();
        }
    }
}

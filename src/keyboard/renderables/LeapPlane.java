package keyboard.renderables;

import javax.media.opengl.GL2;
import static javax.media.opengl.GL2GL3.GL_QUADS;
import utilities.MyUtilities;

import com.leapmotion.leap.InteractionBox;
import com.leapmotion.leap.Vector;

import enums.AttributeName;
import enums.RenderableName;
import keyboard.KeyboardAttributes;
import keyboard.KeyboardRenderable;

public class LeapPlane extends KeyboardRenderable {
    private static final String RENDER_NAME = RenderableName.LEAP_PLANE.toString();
    private static final float TOUCH_THRESHOLD = -0.10f;//-10.0f;
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
    private float distanceToCameraPlane;
    private float planeD;
    private float angleToCamera;
    private Vector axisToCamera;
    private boolean calibrated = false;
    private float distanceToPlane;
    private float normalizedPlaneWidth;
    private float normalizedPlaneHeight;
    private float planeWidth;
    private float planeHeight;
    private Vector BA;
    private Vector DA;
    private Vector intersectionPoint = new Vector();
    private float x, y, z = -1;
    
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
    
    public boolean isTouching() {
        if(distanceToPlane > TOUCH_THRESHOLD) {
            return true;
        }
        return false;
    }
    
    public boolean isValid() {
        if(x == -1 || y == -1) {
            return false;
        }
        return true;
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
        
        // Calculate the axis and angle to the camera.
        Vector n = new Vector(0,0,-1);
        angleToCamera = planeNormal.angleTo(n);
        axisToCamera = planeNormal.cross(n).divide(planeNormal.cross(n).magnitude());
        
        // Calculate the width and height of the plane in real world space.
        planeWidth = MyUtilities.MATH_UTILITILES.findDistanceToPoint(pointB, pointC);
        planeHeight = MyUtilities.MATH_UTILITILES.findDistanceToPoint(pointB, pointA);
        
        // Rotate all points to face the camera.
        pointA = MyUtilities.MATH_UTILITILES.rotateVector(pointA, axisToCamera, angleToCamera);
        pointB = MyUtilities.MATH_UTILITILES.rotateVector(pointB, axisToCamera, angleToCamera);
        pointC = MyUtilities.MATH_UTILITILES.rotateVector(pointC, axisToCamera, angleToCamera);
        pointD = MyUtilities.MATH_UTILITILES.rotateVector(pointD, axisToCamera, angleToCamera);
        planeCenter = MyUtilities.MATH_UTILITILES.rotateVector(planeCenter, axisToCamera, angleToCamera);
        
        // Normalize points in the leap box.
        pointA = iBox.normalizePoint(pointA);
        pointB = iBox.normalizePoint(pointB);
        pointC = iBox.normalizePoint(pointC);
        pointD = iBox.normalizePoint(pointD);
        planeCenter = iBox.normalizePoint(planeCenter);
        
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
    
    public void update(LeapPoint leapPoint, LeapTool leapTool) {
        // Find point distance, normalize it, and position it.
        leapPoint.applyPlaneRotationAndNormalizePoint(axisToCamera, angleToCamera);
        calcDistToPlane(leapPoint.getNormalizedPoint());
        applyPlaneNormalization(leapPoint.getNormalizedPoint());
        leapPoint.scaleTo3DSpace();
        
        // Set tool point, scale it, rotate and position it.
        leapTool.setPoint(leapPoint.getNormalizedPoint());
        leapTool.calculateOrientation();
        leapTool.scaleTo3DSpace(planeWidth, planeHeight);
        
        // Set gesture location and scale it.
    }

    @Override
    public void render(GL2 gl) {
        if(isEnabled()) {
            gl.glPushMatrix();
            gl.glColor3f(0.5f, 0.5f, 0.5f);
            gl.glTranslatef(KEYBOARD_WIDTH/2f-normalizedPlaneWidth/2f, KEYBOARD_HEIGHT/2f-normalizedPlaneHeight/2f, DIST_TO_CAMERA-1f);
            drawRectangle(gl);
            gl.glPopMatrix();
        }
    }
    
    private void drawRectangle(GL2 gl) {
        // Move points to origin.
        Vector tmpA = pointA.minus(pointA);
        Vector tmpB = pointB.minus(pointA);
        Vector tmpC = pointC.minus(pointA);
        Vector tmpD = pointD.minus(pointA);
        gl.glBegin(GL_QUADS);
        gl.glVertex3f(tmpA.getX(), tmpA.getY(), tmpA.getZ());
        gl.glVertex3f(tmpB.getX(), tmpB.getY(), tmpB.getZ());
        gl.glVertex3f(tmpC.getX(), tmpC.getY(), tmpC.getZ());
        gl.glVertex3f(tmpD.getX(), tmpD.getY(), tmpD.getZ());
        gl.glEnd();
    }
}

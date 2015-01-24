package keyboard.renderables;

import utilities.GLColor;
import utilities.Point;

import java.io.IOException;
import java.util.ArrayList;

import javax.media.opengl.GL2;
import javax.swing.JEditorPane;
import javax.swing.JPanel;

import static javax.media.opengl.GL.GL_TRIANGLE_FAN;
import static javax.media.opengl.GL2GL3.GL_TRIANGLES;
import ui.GraphicsController;
import utilities.MyUtilities;
import static com.jogamp.opengl.util.gl2.GLUT.*;

import com.leapmotion.leap.InteractionBox;
import com.leapmotion.leap.Vector;

import enums.Attribute;
import enums.Color;
import enums.FileExt;
import enums.FilePath;
import enums.Gesture;
import enums.Renderable;
import enums.Setting;
import keyboard.CalibrationObserver;
import keyboard.IKeyboard;
import keyboard.KeyboardAttribute;
import keyboard.KeyboardGesture;
import keyboard.KeyboardRenderable;
import keyboard.KeyboardSetting;
import leap.LeapPlaneCalibrator;

public class LeapPlane extends KeyboardRenderable {
    private static final Renderable TYPE = Renderable.LEAP_PLANE;
    private static final GLColor PLANE_COLOR = new GLColor(Color.CYAN);
    private static final GLColor CALIB_COLOR = new GLColor(Color.RED);
    private static final GLColor TEXT_COLOR = new GLColor(Color.BLACK);
    private static final int NUM_VERTICIES = 32;
    private static final float DELTA_ANGLE = (float) (2.0f * Math.PI / NUM_VERTICIES);
    private static final float RADIUS = 10f;
    private final KeyboardSetting TOUCH_THRESHOLD; // -0.10f; normalized // -10.0f; not normalized for defaults
    private final Point KEYBOARD_SIZE;
    private final int BORDER_SIZE;
    private final float CAMERA_DISTANCE;
    private final KeyboardAttribute POINT_A_ATTRIBUTE;
    private final KeyboardAttribute POINT_B_ATTRIBUTE;
    private final KeyboardAttribute POINT_C_ATTRIBUTE;
    private final String FILE_NAME;
    private final boolean AIR_KEYBOARD;
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
    private LeapPlaneCalibrator leapPlaneCalibrator;
    private float distanceToPlane;
    private float normalizedPlaneWidth;
    private float normalizedPlaneHeight;
    private float planeWidth;
    private float planeHeight;
    //private Vector BA;
    //private Vector DA;
    //private Vector intersectionPoint = Vector.zero();
    private float x, y, z = -1;
    private JPanel textPanel;
    private JEditorPane explinationPane; // TODO: Use JTextPane and character attributes instead of html
    private boolean removeTool = false;
    
    public LeapPlane(IKeyboard keyboard, boolean air) {
        super(TYPE);
        AIR_KEYBOARD = air;
        KEYBOARD_SIZE = keyboard.getAttributes().getAttributeAsPoint(Attribute.KEYBOARD_SIZE);
        BORDER_SIZE = keyboard.getAttributes().getAttributeAsInteger(Attribute.BORDER_SIZE);
        CAMERA_DISTANCE = keyboard.getAttributes().getAttributeAsFloat(Attribute.CAMERA_DISTANCE);
        TOUCH_THRESHOLD = keyboard.getSettings().getSetting(Setting.TOUCH_THRESHOLD);
        FILE_NAME = keyboard.getFileName();
        POINT_A_ATTRIBUTE = keyboard.getAttributes().getAttribute(Attribute.LEAP_PLANE_POINT_A);
        POINT_B_ATTRIBUTE = keyboard.getAttributes().getAttribute(Attribute.LEAP_PLANE_POINT_B);
        POINT_C_ATTRIBUTE = keyboard.getAttributes().getAttribute(Attribute.LEAP_PLANE_POINT_C);
        getPlaneAttributes();
        if(!pointA.equals(Vector.zero()) && !pointB.equals(Vector.zero()) && !pointC.equals(Vector.zero())) {
            isCalibrated = true;
        } else {
            isCalibrated = false;
        }
        calculatePlaneData();
        explinationPane = new JEditorPane("text/html", "");
        explinationPane.setEditable(false);
        explinationPane.setHighlighter(null);
        blockAccess(true);
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
    
    public void beginCalibration(JPanel textPanel) {      
        grantAccess(true);
        leapPlaneCalibrator = new LeapPlaneCalibrator(AIR_KEYBOARD);
        //explinationPane = new JEditorPane("text/html", "");
        //explinationPane.setEditable(false);
        this.textPanel = textPanel;
        textPanel.add(explinationPane);
        explinationPane.setVisible(true);
        pointA = Vector.zero();
        pointB = Vector.zero();
        pointC = Vector.zero();
        isCalibrating = true;
        isCalibrated = false;
        removeTool = true;
    }
    
    public void finishCalibration() {
        blockAccess(true);
        // Remove everything we added to the textPanel.
        explinationPane.setText("");
        explinationPane.setVisible(false);
        textPanel.remove(explinationPane);
        //explinationPane = null;
        //textPanel = null;
        
        // Notify the calibration controller that we are done calibrating.
        leapPlaneCalibrator = null;
        isCalibrating = false;
        notifyListenersCalibrationFinished();
        
        // Write the newly calibrated coordinates to file.
        try {
            System.out.println("Saving calibrated Leap Motion Interaction Plane points to " + FilePath.CONFIG.getPath() + FILE_NAME + FileExt.INI.getExt());
            MyUtilities.FILE_IO_UTILITIES.writeAttributeToFile(FilePath.CONFIG.getPath(), FILE_NAME + FileExt.INI.getExt(), POINT_A_ATTRIBUTE);
            MyUtilities.FILE_IO_UTILITIES.writeAttributeToFile(FilePath.CONFIG.getPath(), FILE_NAME + FileExt.INI.getExt(), POINT_B_ATTRIBUTE);
            MyUtilities.FILE_IO_UTILITIES.writeAttributeToFile(FilePath.CONFIG.getPath(), FILE_NAME + FileExt.INI.getExt(), POINT_C_ATTRIBUTE);
            System.out.println("Save Success");
        } catch (IOException e) {
            System.out.println("Save Failure. Try using the \"Save Settings\" button to save calibration to file.");
            e.printStackTrace();
        }
    }
    
    private void setCalibrationText(int point) {
        String text;
        switch(point) {
            case LeapPlaneCalibrator.POINT_A:
                text = "<div style=\"white-space: nowrap\"><font size=+1><b><font color=red>Place and hold</font></b> "
                        + "the tool over the <b><font color=red>bottom-left or \"A\" corner</font></b> of the keyboard.</font></div>";
                if(!MyUtilities.JAVA_SWING_UTILITIES.equalsIgnoreHTML(text, explinationPane.getText())) {
                    explinationPane.setText(text);
                }
                break;
            case LeapPlaneCalibrator.POINT_B:
                text = "<div style=\"white-space: nowrap\"><font size=+1><b><font color=red>Place and hold</font></b> "
                        + "the tool over the <b><font color=red>top-left or \"B\" corner</font></b> of the keyboard.</font></div>";
                if(!MyUtilities.JAVA_SWING_UTILITIES.equalsIgnoreHTML(text, explinationPane.getText())) {
                    explinationPane.setText(text);
                }
                break;
            case LeapPlaneCalibrator.POINT_C:
                text = "<div style=\"white-space: nowrap\"><font size=+1><b><font color=red>Place and hold</font></b> "
                        + "the tool over the <b><font color=red>top-right or \"C\" corner</font></b> of the keyboard.</font></div>";
                if(!MyUtilities.JAVA_SWING_UTILITIES.equalsIgnoreHTML(text, explinationPane.getText())) {
                    explinationPane.setText(text);
                }
                break;
            default:
                text = "<div style=\"white-space: nowrap\"><font size=+2><b><font color=red>Remove the tool</font></b> "
                        + "from the Leap Motinon Interaction Zone.</font></div>";
                if(!MyUtilities.JAVA_SWING_UTILITIES.equalsIgnoreHTML(text, explinationPane.getText())) {
                    explinationPane.setText(text);
                }
                break;
        }
    }
    
    public boolean isCalibrated() {
        return isCalibrated;
    }
    
    public boolean isCalibrating() {
        return isCalibrating;
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
        
        // Compute the 4th point of the plane for drawing.
        // TODO: Detect D as another point instead of auto calculating it.
        // This would allow us to take 4 points and use a transformation to create a more accurate plane
        // Take a look at alvin's work on this.
        pointD = pointA.minus(pointB).plus(pointC);
        
        // Determine the vectors
        Vector AB = pointB.minus(pointA);
        Vector AC = pointC.minus(pointA);
        
        // Find the center of the plane
        planeCenter = MyUtilities.MATH_UTILITILES.findMidpoint(pointA, pointC);
        
        // Determine the normal of the plane by finding the cross product
        planeNormal = AB.cross(AC);
        
        // Find D, a simple variable that holds our normal time's a point on the plane
        planeD = MyUtilities.MATH_UTILITILES.calcPlaneD(planeNormal, planeCenter);
        
        // Calculate the axis and angle to the camera.
        Vector n = Vector.zAxis().times(-1);
        angleToCamera = planeNormal.angleTo(n);
        axisToCamera = planeNormal.cross(n).divide(planeNormal.cross(n).magnitude());
        
        // Calculate the width and height of the plane in real world space.
        //planeWidth = MyUtilities.MATH_UTILITILES.findDistanceToPoint(pointB, pointC);
        //planeHeight = MyUtilities.MATH_UTILITILES.findDistanceToPoint(pointB, pointA);
        
        // Rotate all points to face the camera.
        if(axisToCamera.isValid()) {
            pointA = MyUtilities.MATH_UTILITILES.rotateVector(pointA, axisToCamera, angleToCamera);
            pointB = MyUtilities.MATH_UTILITILES.rotateVector(pointB, axisToCamera, angleToCamera);
            pointC = MyUtilities.MATH_UTILITILES.rotateVector(pointC, axisToCamera, angleToCamera);
            pointD = MyUtilities.MATH_UTILITILES.rotateVector(pointD, axisToCamera, angleToCamera);
            planeCenter = MyUtilities.MATH_UTILITILES.rotateVector(planeCenter, axisToCamera, angleToCamera);
        }
        
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
        //BA = pointA.minus(pointB);
        //DA = pointA.minus(pointD);
        
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
        vector.setX((point.getX() * KEYBOARD_SIZE.x) + BORDER_SIZE);
        vector.setY((point.getY() * KEYBOARD_SIZE.y) + BORDER_SIZE);
        vector.setZ(point.getZ() * CAMERA_DISTANCE);
        return vector;
    }
    
    private void calcDistToPlane(Vector point) {
        distanceToPlane = MyUtilities.MATH_UTILITILES.findDistanceToPlane(point, planeNormal, planeD);
        //System.out.println(distanceToPlane);
    }
    
    private void applyPlaneNormalization(Vector point) {
        /*/ Since all of the Z's should be equal for the plane (this is post rotation). We can throw them out and
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
        }*/
        
        // Alternative Method. The below method is as fast as the method above when the tool is invalid. However,
        // when the tool is valid and in view, the below method is faster than the above method by about 4-10 microseconds
        // per cycle. Since finding the distance to a line is always positive, we have to find the distance to both
        // the top and bottom lines and the left and right lines to determine our position in world space.
        // The above method and this method return results that are similar but not the same.
        
        // Find horizontal position.
        // Find distance to left and right sides. Use the right side to determine if we should negate the distance to the left side.
        float distAB = MyUtilities.MATH_UTILITILES.findDistanceToLine(point, pointA, pointB)/normalizedPlaneWidth;
        float distDC = MyUtilities.MATH_UTILITILES.findDistanceToLine(point, pointD, pointC);
        if (distDC > normalizedPlaneWidth) {
            x = -distAB;
        } else {
            x = distAB;
        }
        
        // Find vertical position.
        // Find distance to top and bottom sides. Use the top side to determine if we should negate the distance to the bottom side.
        float distAD = MyUtilities.MATH_UTILITILES.findDistanceToLine(point, pointA, pointD)/normalizedPlaneHeight;
        float distBC = MyUtilities.MATH_UTILITILES.findDistanceToLine(point, pointB, pointC);
        if (distBC > normalizedPlaneHeight) {
            y = -distAD;
        } else {
            y = distAD;
        }
        
        // Use planeCenter and point's Z's to determine %.
        z = (point.getZ() - planeCenter.getZ()) / distanceToCameraPlane;
        if(z > 1) {z = 1;} else if(z < 0) {z = 0;}
        
        // Apply X, Y, and Z to point.
        point.setX(x);
        point.setY(y);
        point.setZ(z);
    }
    
    public void update(LeapPoint leapPoint, LeapTool leapTool, KeyboardGestures keyboardGestures, LeapTrail leapTrail) {
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
            leapTool.update(leapPoint.getNormalizedPoint());
            
            if(Gesture.ENABLED) {
                // Set new gesture destination location.
                for(KeyboardGesture gesture: keyboardGestures.getGestures()) {
                    if(gesture.isValid()) {
                        gesture.update(leapPoint.getNormalizedPoint());
                    } else {
                        gesture.update();
                    }
                }
            }
            
            // Set add to trail and set location.
            if(isTouching() /*&& this.isValid()*/) {
                leapTrail.update(leapPoint.getNormalizedPoint());
            } else {
                leapTrail.update();
            }
            
        } else if (isCalibrating) {
            if(removeTool) {
                // If tool is removed from area.
                if(!leapTool.isValid()) {
                    setCalibrationText(leapPlaneCalibrator.calibratingPoint());
                    removeTool = false;
                } else {
                    // Populate text area with correct message.
                    setCalibrationText(LeapPlaneCalibrator.POINT_INVALID);
                }
            } else if(leapTool.isValid()) {
                // While calibrating, add each new point to the plane calibrator object.
                leapPlaneCalibrator.addPoint(leapPoint.getPoint());
                
                // Calibration order:
                // C - top right
                // A - bottom left
                // B - top left
                switch(leapPlaneCalibrator.calibratingPoint()) {
                    case LeapPlaneCalibrator.POINT_A:
                        // Get the midpoint and update it.
                        pointA = leapPlaneCalibrator.getMidPoint();
                        POINT_A_ATTRIBUTE.setVectorValue(pointA);
                        
                        // Remove the tool after finding a point.
                        if(leapPlaneCalibrator.doneWithCurrentPoint() && leapPlaneCalibrator.isValid()) {
                            removeTool = true;
                        }
                        break;
                    case LeapPlaneCalibrator.POINT_B:
                        // Get the midpoint and update it.
                        pointB = leapPlaneCalibrator.getMidPoint();
                        POINT_B_ATTRIBUTE.setVectorValue(pointB);
                        
                        // Remove the tool after finding a point.
                        if(leapPlaneCalibrator.doneWithCurrentPoint() && leapPlaneCalibrator.isValid()) {
                            removeTool = true;
                        }
                        break;
                    case LeapPlaneCalibrator.POINT_C:
                        // Get the midpoint and update it.
                        pointC = leapPlaneCalibrator.getMidPoint();
                        POINT_C_ATTRIBUTE.setVectorValue(pointC);

                        // Remove the tool after finding a point.
                        if(leapPlaneCalibrator.doneWithCurrentPoint() && leapPlaneCalibrator.isValid()) {
                            removeTool = true;
                        }
                        break;
                    default:
                        isCalibrated = true;
                        break;
                }
                calculatePlaneData();
            }
        }
    }

    @Override
    public void render(GL2 gl) {
        if(isEnabled()) {
            gl.glPushMatrix();
            gl.glNormal3f(0, 0, 1);
            PLANE_COLOR.glColor(gl);
            gl.glTranslatef((KEYBOARD_SIZE.x+BORDER_SIZE)/2f-planeWidth/2f, (KEYBOARD_SIZE.y+BORDER_SIZE)/2f-planeHeight/2f, -10f);
            drawRectangle(gl);
            drawCircleWithLetter(gl, scaledPointA, 'A', LeapPlaneCalibrator.POINT_A);
            drawCircleWithLetter(gl, scaledPointB, 'B', LeapPlaneCalibrator.POINT_B);
            drawCircleWithLetter(gl, scaledPointC, 'C', LeapPlaneCalibrator.POINT_C);
            drawCircleWithLetter(gl, scaledPointD, 'D', -1);
            gl.glPopMatrix();
        }
    }
    
    private void drawCircleWithLetter(GL2 gl, Vector vector, char pointLetter, int calibrationPoint) {
        if(leapPlaneCalibrator != null && calibrationPoint == leapPlaneCalibrator.calibratingPoint()) {
            CALIB_COLOR.glColor(gl);
        } else {
            PLANE_COLOR.glColor(gl);
        }
        gl.glTranslatef(vector.getX(), vector.getY(), vector.getZ());
        gl.glBegin(GL_TRIANGLE_FAN);
        // Draw the vertex at the center of the circle
        gl.glVertex3f(0f, 0f, 0f);
        for(int i = 0; i < NUM_VERTICIES; i++)
        {
          gl.glVertex3d(Math.cos(DELTA_ANGLE * i) * RADIUS, Math.sin(DELTA_ANGLE * i) * RADIUS, 0.0);
        }
        gl.glVertex3f(1f * RADIUS, 0f, 0f);
        gl.glEnd();
        drawLetter(gl, pointLetter);
        gl.glTranslatef(-vector.getX(), -vector.getY(), -vector.getZ());
    }
    
    private void drawLetter(GL2 gl, char letter) {
        gl.glPushMatrix();
        TEXT_COLOR.glColor(gl);
        gl.glTranslatef(-7f, -7f, 0f);
        gl.glScalef(0.15f, 0.15f, 0.15f);
        gl.glLineWidth(2);
        GraphicsController.glut.glutStrokeCharacter(STROKE_ROMAN, letter);
        gl.glPopMatrix();
    }
    
    private void drawRectangle(GL2 gl) {
        gl.glBegin(GL_TRIANGLES);
        gl.glVertex3f(scaledPointA.getX(), scaledPointA.getY(), scaledPointA.getZ());
        gl.glVertex3f(scaledPointB.getX(), scaledPointB.getY(), scaledPointB.getZ());
        gl.glVertex3f(scaledPointC.getX(), scaledPointC.getY(), scaledPointC.getZ());
        
        gl.glVertex3f(scaledPointC.getX(), scaledPointC.getY(), scaledPointC.getZ());
        gl.glVertex3f(scaledPointD.getX(), scaledPointD.getY(), scaledPointD.getZ());
        gl.glVertex3f(scaledPointA.getX(), scaledPointA.getY(), scaledPointA.getZ());
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

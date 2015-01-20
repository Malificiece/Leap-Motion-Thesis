package keyboard;

import utilities.MyUtilities;

import com.leapmotion.leap.Vector;

import enums.Gesture;

public class KeyboardGesture {
    private static final float RADS_TO_DEGREES = (float) (180 / Math.PI);
    private final Gesture gestureType;
    private final int FADE_DURATION = 400;
    private long previousFadeTime;
    private long fadeTimeElapsed = 0;
    private boolean isFading = false;
    private boolean isDone = false;
    private Vector sourcePoint = Vector.zero();
    private Vector destinationPoint = Vector.zero();
    private float length = 0;
    private float opacity = 1f;
    private float angleToDirection = 0;
    private Vector axisToDirection = Vector.zero();
    private Vector upDirection = Vector.zAxis();
    private Vector direction = Vector.zero();
    
    public KeyboardGesture(Vector sourcePoint, Gesture gestureType) {
        this.sourcePoint = sourcePoint;
        this.destinationPoint = sourcePoint;
        this.gestureType = gestureType;
    }
    
    public void update() {
        if(isFading && !isDone) {
            long now = System.currentTimeMillis();
            fadeTimeElapsed += now - previousFadeTime;
            previousFadeTime = now;

            if(fadeTimeElapsed <= FADE_DURATION) {
                opacity = 1f - fadeTimeElapsed/(float)FADE_DURATION;
            } else {
                isDone = true;
            }
        }
    }
    
    public void update(Vector point) {
        this.destinationPoint = point;
        calculateLength();
        calculateOrientation();
    }
    
    private void calculateLength() {
        length = MyUtilities.MATH_UTILITILES.findDistanceToPoint(sourcePoint, destinationPoint);
    }
    
    private void calculateOrientation() {
        direction = destinationPoint.minus(sourcePoint);
        angleToDirection = upDirection.angleTo(direction) * RADS_TO_DEGREES;
        axisToDirection = upDirection.cross(direction);
        axisToDirection = axisToDirection.divide(axisToDirection.magnitude());
    }
    
    public void gestureFinshed() {
        isFading = true;
        previousFadeTime = System.currentTimeMillis();
    }

    public Gesture getType() {
        return gestureType;
    }

    public float getOpacity() {
        return opacity;
    }

    public Vector getDestination() {
        return destinationPoint;
    }
    
    public Vector getSource() {
        return sourcePoint;
    }
    
    public Vector getAxis() {
        return axisToDirection;
    }
    
    public float getAngle() {
        return angleToDirection;
    }
    
    public Vector getDirection() {
        return direction;
    }

    public float getLength() {
        return length;
    }
    
    public boolean isDone() {
        return isDone;
    }

    public boolean isValid() {
        if(!isFading && !isDone) {
            return true;
        }
        return false;
    }
}

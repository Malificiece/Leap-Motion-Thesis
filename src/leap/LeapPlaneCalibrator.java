package leap;

import utilities.MyUtilities;

import com.leapmotion.leap.Vector;

public class LeapPlaneCalibrator {
    // Find order:
    // C - top right
    // A - bottom left
    // B - top left
    public static final int POINT_C = 0;
    public static final int POINT_A = 2;
    public static final int POINT_B = 1;
    public static final int POINT_D = 3;
    public static final int POINT_INVALID = 4;
    public static final int POINT_FIRST = POINT_C;
    private final int CALCULATE_MIDPOINT = 250;
    private final int CLUSTER_SIZE = 1000 + CALCULATE_MIDPOINT;
    private final int START_SEARCH = 500 + CALCULATE_MIDPOINT;
    private final float EPSILON = 0.00015f;
    private final float DELTA_EPSILON = 0.0001f;
    private float epsilon;
    private Vector[] cluster;
    private int point = 0;
    private int clusterIndex = 0;
    private int startSearch = 0;
    private Vector previousMidpoint;
    private Vector midpoint = new Vector();
    private boolean doneWithCurrentPoint = false;
    
    public LeapPlaneCalibrator() {
        epsilon = EPSILON;
        cluster = new Vector[CLUSTER_SIZE];
    }
    
    public void addPoint(Vector point) {
        if(!doneWithCurrentPoint) {
            startSearch = startSearch < CLUSTER_SIZE ? ++startSearch : startSearch;
            if(startSearch >= CALCULATE_MIDPOINT) {
                cluster[clusterIndex % CLUSTER_SIZE] = point;
                clusterIndex++;
                previousMidpoint = new Vector(midpoint);
                if(MyUtilities.MATH_UTILITILES.findMidpoint(midpoint, cluster)) {
                    if(startSearch >= START_SEARCH && MyUtilities.MATH_UTILITILES.findDistanceToPoint(previousMidpoint, midpoint) < epsilon) {
                        doneWithCurrentPoint = true;
                    } else if(startSearch == CLUSTER_SIZE) {
                        // reduce epsilon here
                        epsilon += DELTA_EPSILON;
                    }
                }
            } else {
                midpoint = point;
            }
        }
    }
    
    public boolean isValid() {
        return point < POINT_INVALID;
    }
    
    public boolean doneWithCurrentPoint() {
        if(doneWithCurrentPoint) {
            point++;
            if(point < POINT_INVALID) {
                reuseCluster();
                doneWithCurrentPoint = false;
            } else {
                clearCluster();
            }
            return true;
        }
        return false;
    }

    public int calibratingPoint() {
        return point;
    }

    public Vector getMidPoint() {
        if(doneWithCurrentPoint) {
            return new Vector(midpoint);
        }
        return midpoint;
    }
    
    private void reuseCluster() {
        for(int i = 0; i < CLUSTER_SIZE; i++) {
            cluster[i] = null;
        }
        clusterIndex = 0;
        startSearch = 0;
        epsilon = EPSILON;
    }
    
    private void clearCluster() {
        for(int i = 0; i < CLUSTER_SIZE; i++) {
            cluster[i] = null;
        }
        cluster = null;
        epsilon = EPSILON;
    }
}

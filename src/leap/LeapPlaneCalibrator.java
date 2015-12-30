/*
 * Copyright (c) 2015, Garrett Benoit. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */ 

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
    private final float EPSILON = 0.0002f;
    //private final float DELTA_EPSILON = 0.00001f; // Laptop
    private final float DELTA_EPSILON = 0.001f; // School Computer
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

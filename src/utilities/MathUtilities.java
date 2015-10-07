package utilities;

import com.leapmotion.leap.Vector;

public class MathUtilities {
    public static final float DEGREES_TO_RADS = (float) (Math.PI / 180f);
    
    public boolean findMidpoint(Vector midpoint, Vector[] vectors) {
        if(vectors.length > 0) {
            float x = 0, y = 0, z = 0;
            int numVectors = 0;
            for(Vector v: vectors) {
                if(v != null) {
                    x += v.getX();
                    y += v.getY();
                    z += v.getZ();
                    numVectors++;
                } else {
                    break;
                }
            }
            if(numVectors > 0) {
                midpoint.setX(x/numVectors);
                midpoint.setY(y/numVectors);
                midpoint.setZ(z/numVectors);
                return true;
            }
        }
        return false;
    }
    
    public Vector findMidpoint(Vector a, Vector b) {
        Vector midpoint = new Vector();
        midpoint.setX((a.getX() + b.getX())/2);
        midpoint.setY((a.getY() + b.getY())/2);
        midpoint.setZ((a.getZ() + b.getZ())/2);
        return midpoint;
    }
    
    public float findDistanceToPoint(Vector source, Vector dest) {
        return (float) Math.sqrt(Math.pow(dest.getX() - source.getX(), 2)
                + Math.pow(dest.getY() - source.getY(), 2)
                + Math.pow(dest.getZ() - source.getZ(), 2));
    }
    
    public float calcPlaneD(Vector planeNormal, Vector planePoint) {
        return (-(planeNormal.getX()*planePoint.getX())
                + (-(planeNormal.getY()*planePoint.getY())
                + (-(planeNormal.getZ()*planePoint.getZ()))));
    }
    
    public float findDistanceToPlane(Vector point, Vector planeNormal, float D) {
        return (float) (((planeNormal.getX()*point.getX())
                + (planeNormal.getY()*point.getY())
                + (planeNormal.getZ()*point.getZ()) + D)
                / planeNormal.magnitude());
    }
    
    public Vector rotateVector(Vector vector, Vector axis, float angle) {
        Vector rotatedVector;
        Vector part1 = vector.times((float) Math.cos(angle));
        Vector part2 = axis.cross(vector).times((float) Math.sin(angle));
        Vector part3 = axis.times(axis.dot(vector)).times((float) (1.0 - Math.cos(angle)));
        rotatedVector = part1.plus(part2).plus(part3);
        return rotatedVector;
    }
    
    public boolean findLineIntersection(Vector intersectionPoint, Vector leapPoint, Vector leapVector, Vector sidePoint, Vector sideVector) {
        float s, t;
        float bottom = (-sideVector.getX() * leapVector.getY() + leapVector.getX() * sideVector.getY());
        if(bottom != 0) {
            s = (-leapVector.getY() * (leapPoint.getX() - sidePoint.getX()) + leapVector.getX() * (leapPoint.getY() - sidePoint.getY())) / bottom;
            t = ( sideVector.getX() * (leapPoint.getY() - sidePoint.getY()) - sideVector.getY() * (leapPoint.getX() - sidePoint.getX())) / bottom;
        } else {
            return false;
        }
        
        if(0 <= s && s <= 1 && 0 <= t && t <= 1) {
            intersectionPoint.setX(leapPoint.getX() + (t * leapVector.getX()));
            intersectionPoint.setY(leapPoint.getY() + (t * leapVector.getY()));
            intersectionPoint.setZ(leapPoint.getZ());
            return true;
        }
        return false;
    }
    
    public float findDistanceToLine(Vector point, Vector p1, Vector p2) {
        return Math.abs(((p2.getY() - p1.getY()) * point.getX())
                - ((p2.getX() - p1.getX()) * point.getY())
                + (p2.getX() * p1.getY())
                - (p2.getY() * p1.getX()))
                / (float) Math.sqrt(Math.pow(p2.getY() - p1.getY(), 2) + Math.pow(p2.getX() - p1.getX(), 2));
    }
    
    public float findDimensionalAlignment(int height, int borderSize) {
        return (float) (((height + (borderSize * 2f)) / (Math.tan(DEGREES_TO_RADS*GraphicsUtilities.FOVY/2f))/2f)); // Purposefully leaving out negative
    }
    
    public float findDimensionalAlignment(float height, float borderSize) {
        return (float) (((height + (borderSize * 2f)) / (Math.tan(DEGREES_TO_RADS*GraphicsUtilities.FOVY/2f))/2f)); // Purposefully leaving out negative
    }
    
    public float calculateFrechetDistance(Vector[] P, Vector[] Q) {
        FrechetCalculator fc = new FrechetCalculator(P, Q);
        try {
            return fc.calculateFrechetDistanceRecursive(P.length - 1, Q.length - 1);
        } finally {
            fc.delete();
            fc = null;
        }
    }
    
    private class FrechetCalculator {
        Vector [] P;
        Vector [] Q;
        Float [][] CA;
        public FrechetCalculator(Vector[] P, Vector[] Q) {
            this.P = P;
            this.Q = Q;
            CA = new Float[P.length][Q.length];
            for(int i = 0; i < P.length; i++) {
                for(int j = 0; j < Q.length; j++) {
                    CA[i][j] = -1.0f;
                }
            }
        }
        
        public void delete() {
            for(int i = 0; i < P.length; i++) {
                for(int j = 0; j < Q.length; j++) {
                    CA[i][j] = null;
                }
                CA[i] = null;
            }
            P = null;
            Q = null;
            CA = null;
        }
        
        /**
         * This function recursively steps through the Frechet Distance
         * algorithm, reporting the minimum Frechet Distance calculated
         * for two paths.
         * @param P, Q the paths to measure
         * @param CA matrix holding calculated distances between P and Q
         * @param i, j the indicies compared on this recurion
         * @return calculated Frechet Distance for CA[i][j]
         */
        private float calculateFrechetDistanceRecursive(int i, int j) {
            float CAij = 0;
            // coupling search function
            if(CA[i][j] > -1) {
                // don't update CA in this case
                CAij = CA[i][j];
            } else if(i == 0 && j == 0) {
                // update the CA permanent
                CA[i][j] = findDistanceToPoint(P[0], Q[0]);
                // set the current relevant value
                CAij = CA[i][j];
            } else if(i > 0 && j == 0) {
                CA[i][j] = Math.max(calculateFrechetDistanceRecursive(i - 1, 0), findDistanceToPoint(P[i], Q[0]));
                CAij = CA[i][j];
            } else if(i == 0 && j > 0) {
                CA[i][j] = Math.max(calculateFrechetDistanceRecursive(0, j - 1), findDistanceToPoint(P[0], Q[j]));
                CAij = CA[i][j];
            } else if(i > 0 && j > 0) {
                float min = Math.min(calculateFrechetDistanceRecursive(i - 1, j), calculateFrechetDistanceRecursive(i - 1, j - 1));
                min = Math.min(min, calculateFrechetDistanceRecursive(i, j - 1));
                CA[i][j] = Math.max(min, findDistanceToPoint(P[i], Q[j]));
                CAij = CA[i][j];
            } else {
                CA[i][j] = Float.POSITIVE_INFINITY;
                System.out.println("This is where we should be assigning infinity"); // This might not be right
            }
            return CAij;
        }
    }
}

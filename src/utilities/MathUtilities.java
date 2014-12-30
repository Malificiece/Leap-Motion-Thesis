package utilities;

import com.leapmotion.leap.Vector;

public class MathUtilities {
    public Vector midpoint(Vector a, Vector b) {
        Vector v = new Vector();
        v.setX((a.getX() + b.getX())/2);
        v.setY((a.getY() + b.getY())/2);
        v.setZ((a.getZ() + b.getZ())/2);
        return v;
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
}

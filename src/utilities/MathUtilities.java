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
        return (float) Math.sqrt(Math.pow(dest.getX() - source.getX(), 2) + Math.pow(dest.getY() - source.getY(), 2) + Math.pow(dest.getZ() - source.getZ(), 2));
    }
    
    public float calcPlaneD(Vector planeNormal, Vector planePoint) {
        return (-(planeNormal.getX()*planePoint.getX()) + (-(planeNormal.getY()*planePoint.getY()) + (-(planeNormal.getZ()*planePoint.getZ()))));
    }
    
    public float findDistanceToPlane(Vector point, Vector planeNormal, float D) {
        return (float) (((planeNormal.getX()*point.getX()) + (planeNormal.getY()*point.getY()) + (planeNormal.getZ()*point.getZ()) + D)
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
}

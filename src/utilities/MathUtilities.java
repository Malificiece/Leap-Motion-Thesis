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
    
    public float calcPlaneD(Vector planeNormal, Vector planePoint) {
        return (-(planeNormal.getX()*planePoint.getX()) + (-(planeNormal.getY()*planePoint.getY()) + (-(planeNormal.getZ()*planePoint.getZ()))));
    }
    
    public float findDistanceToPlane(Vector point, Vector planeNormal, float D) {
        return (float) (((planeNormal.getX()*point.getX()) + (planeNormal.getY()*point.getY()) + (planeNormal.getZ()*point.getZ()) + D)
                / planeNormal.magnitude());
    }
}

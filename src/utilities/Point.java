package utilities;

@SuppressWarnings("serial")
public class Point extends java.awt.geom.Point2D.Float {
    public Point(float x, float y, float scale) {
        super(x * scale, y * scale);
    }
    
    public Point(float x, float y) {
        super(x, y);
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}

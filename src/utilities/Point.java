package utilities;

@SuppressWarnings("serial")
public class Point extends java.awt.Point {
    public Point(int x, int y) {
        super(x, y);
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}

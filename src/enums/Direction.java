package enums;

import com.leapmotion.leap.Vector;

public enum Direction {
    NONE(Vector.zero()),
    LEFT(Vector.left()),
    RIGHT(Vector.right()),
    UP(Vector.up()),
    DOWN(Vector.down());
    
    private Vector direction;
    
    private Direction(Vector direction) {
        this.direction = direction;
    }
    
    public Vector getDirection() {
        return direction;
    }
}

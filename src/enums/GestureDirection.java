package enums;

import com.leapmotion.leap.Vector;

public enum GestureDirection {
    LEFT(Vector.left()),
    RIGHT(Vector.right()),
    UP(Vector.up()),
    DOWN(Vector.down());
    
    private Vector direction;
    
    private GestureDirection(Vector direction) {
        this.direction = direction;
    }
    
    public Vector getDirection() {
        return direction;
    }
}

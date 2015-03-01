package enums;

import com.leapmotion.leap.Vector;

public enum Direction {
    NONE(Vector.zero()),
    LEFT(Vector.left()),
    RIGHT(Vector.right()),
    UP(Vector.up()),
    DOWN(Vector.down());
    
    private final Vector direction;
    
    private Direction(Vector direction) {
        this.direction = direction;
    }
    
    public Vector getDirection() {
        return direction;
    }
    
    public static Direction getByName(String directionName) {
        for(Direction direction: values()) {
            if(direction.name().equalsIgnoreCase(directionName)) return direction;
        }
        return null;
    }
}

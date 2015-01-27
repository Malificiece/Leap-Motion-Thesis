package enums;

import com.leapmotion.leap.Vector;

public enum Direction {
    NONE(Vector.zero()),
    LEFT(Vector.left()),
    RIGHT(Vector.right()),
    UP(Vector.up()),
    DOWN(Vector.down());
    
    private final Vector direction;
    
    private static final Direction[] VALUES = values();
    private static final int SIZE = VALUES.length;
    
    private Direction(Vector direction) {
        this.direction = direction;
    }
    
    public Vector getDirection() {
        return direction;
    }
    
    public static Direction getByName(String directionName) {
        for(int i = 0; i < SIZE; i++) {
            if(VALUES[i].name().equalsIgnoreCase(directionName)) return VALUES[i];
        }
        return null;
    }
}

package enums;

public enum Renderable {
    KEYBOARD_IMAGE("Keyboard Image"),
    VIRTUAL_KEYS("Virtual Keys"),
    LEAP_PLANE("Leap Motion Interaction Plane"),
    LEAP_TOOL("Leap Motion Tool"),
    LEAP_POINT("Leap Motion Point"),
    KEYBOARD_GESTURES("Keyboard Gestures"),
    LEAP_TRAIL("Leap Motion Interpolation Trail");

    private final String name;
    
    private Renderable(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
}

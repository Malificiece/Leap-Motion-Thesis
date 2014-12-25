package enums;

public enum RenderableName {
    KEYBOARD_IMAGE("Keyboard Image"),
    VIRTUAL_KEYS("Virtual Keys"),
    LEAP_PLANE("LeapMotion Interaction Plane"),
    LEAP_TOOL("LeapMotion Tool"),
    LEAP_POINT("LeapMotion Point"),
    LEAP_GESTURES("LeapMotion Gestures");

    private final String name;
    
    private RenderableName(String name) {
        this.name = name;
    }
    
    public String toString() {
        return name;
    }
}

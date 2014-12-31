package enums;

public enum Attribute {
    KEYBOARD_WIDTH("Keyboard Width"),
    KEYBOARD_HEIGHT("Keyboard Height"),
    GAP_SIZE("Gap Size"),
    KEY_WIDTH("Key Width"),
    KEY_HEIGHT("Key Height"),
    SPACE_KEY_WIDTH("Space Key Width"),
    BACK_SPACE_KEY_WIDTH("Backspace Key Width"),
    SHIFT_KEY_WIDTH("Shift Key Width"),
    ENTER_KEY_WIDTH("Enter Key Width"),
    ROW_OFFSETS("Row Offsets"),
    KEY_ROWS("Key Rows"),
    NUMBER_OF_KEYS("Number of Keys"),
    KEY_BINDINGS("Key Bindings"),
    DIST_TO_CAMERA("Distance to Camera");

    private final String name;
    
    private Attribute(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
}

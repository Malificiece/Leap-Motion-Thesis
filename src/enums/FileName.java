package enums;

public enum FileName {
    STANDARD_NAME("standard"),
    LEAP_NAME("leap"),
    CONTROLLER_NAME("controller"),
    TABLET_NAME("tablet"),
    KEYBOARD_DEFAULT_IMAGE_NAME("keyboard"),
    KEYBOARD_TRANS_IMAGE_NAME("keyboard_trans"),
    KEYBOARD_INVIS_IMAGE_NAME("keyboard_invis");

    private final String name;
    
    private FileName(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
}

package enums;

public enum FileName {
    STANDARD("standard"),
    LEAP("leap"),
    CONTROLLER("controller"),
    TABLET("tablet"),
    KEYBOARD_IMAGE("_keyboard"),
    KEYBOARD_IMAGE_UPPER("_keyboard_upper"),
    DICTIONARY("English-US"),
    SUBJECT_ID_LIST("subject_ID_list");

    private final String name;
    
    private FileName(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
}

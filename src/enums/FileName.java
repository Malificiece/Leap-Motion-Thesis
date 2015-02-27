package enums;

public enum FileName {
    STANDARD("standard"),
    LEAP_SURFACE("leapSurface"),
    LEAP_AIR("leapAir"),
    LEAP_PINCH("leapPinch"),
    CONTROLLER("controller"),
    TABLET("tablet"),
    SWIPE("swipe"),
    KEYBOARD_IMAGE("_keyboard"),
    KEYBOARD_IMAGE_UPPER("_keyboard_upper"),
    DICTIONARY("English-US"),
    DICTIONARY_FILTER("Terms-to-Block"),
    TEMPORARY("temporary_"),
    TUTORIAL("tutorial");

    private final String name;
    
    private FileName(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
}

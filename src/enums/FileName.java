package enums;

public enum FileName {
    STANDARD("standard"),
    LEAP_SURFACE("leapSurface"),
    LEAP_AIR_STATIC("leapAirStatic"),
    LEAP_AIR_PINCH("leapAirPinch"),
    LEAP_AIR_DYNAMIC("leapAirDynamic"),
    LEAP_AIR_AUGMENTED("leapAirAugmented"),
    LEAP_AIR_BIMODAL("leapAirBimodal"),
    CONTROLLER_CONSOLE("controllerConsole"),
    CONTROLLER_GESTURE("controllerGesture"),
    TABLET("tablet"),
    SWIPE("swipe"),
    EXIT_SURVEY("exitSurvey"),
    SUBJECT_MERGED_DATA("mergedData"),
    CONSOLIDATED_PILOT_DATA("consolidatedPilotData"),
    KEYBOARD_IMAGE("_keyboard"),
    KEYBOARD_IMAGE_UPPER("_keyboard_upper"),
    DICTIONARY("English-US"),
    DICTIONARY_FILTER("Terms-to-Block"),
    TEMPORARY("temporary_"),
    TUTORIAL("tutorial"),
    PLAY("play"),
    PAUSE("pause"),
    STOP("stop"),
    HOME("home"),
    UP_FOLDER("up_folder");

    private final String name;
    
    private FileName(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
}

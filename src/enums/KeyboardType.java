package enums;

public enum KeyboardType {
    DISABLED("DISABLED", "DISABLED"),
    STANDARD("Standard Keyboard", FileName.STANDARD.getName()),
    CONTROLLER_CONSOLE("Controller Console Keyboard", FileName.CONTROLLER_CONSOLE.getName()),
    CONTROLLER_GESTURE("Controller Word-Gesture Keyboard", FileName.CONTROLLER_GESTURE.getName()),
    TABLET("Tablet Keyboard", FileName.TABLET.getName()),
    LEAP_SURFACE("Leap Surface Keyboard", FileName.LEAP_SURFACE.getName()),
    LEAP_AIR_STATIC("Leap Air Static Keyboard", FileName.LEAP_AIR_STATIC.getName()),
    LEAP_AIR_PINCH("Leap Air Pinch Keyboard", FileName.LEAP_AIR_PINCH.getName()),
    LEAP_AIR_DYNAMIC("Leap Air Dynamic Keyboard", FileName.LEAP_AIR_DYNAMIC.getName()),
    LEAP_AIR_BIMODAL("Leap Air Bimodal Keyboard", FileName.LEAP_AIR_BIMODAL.getName()),
    LEAP_AIR_AUGMENTED("Leap Air Augmented Keyboard", FileName.LEAP_AIR_AUGMENTED.getName());
    
    private static final String LEAP = "LEAP";
    private final String keyboardName;
    private final String keyboardFileName;
    
    private KeyboardType(String keyboardName, String keyboardFileName) {
        this.keyboardName = keyboardName;
        this.keyboardFileName = keyboardFileName;
    }
    
    public String getName() {
        return keyboardName;
    }
    
    public String getFileName() {
        return keyboardFileName;
    }
    
    public boolean isLeap() {
        return(this.name().contains(LEAP));
    }
    
    public static KeyboardType getByName(String keyboardName) {
        for(KeyboardType keyboardType: values()) {
            if(keyboardType.getName().equalsIgnoreCase(keyboardName)) return keyboardType;
            if(keyboardType.getFileName().equalsIgnoreCase(keyboardName)) return keyboardType;
            if(keyboardName != null && keyboardName.contains(keyboardType.getName())) return keyboardType;
            if(keyboardName != null && keyboardName.contains(keyboardType.getFileName())) return keyboardType;
        }
        return null;
    }
}

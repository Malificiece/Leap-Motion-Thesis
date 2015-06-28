package enums;

public enum KeyboardType {
    STANDARD(0, "Standard Keyboard", FileName.STANDARD.getName()),
    CONTROLLER(1, "Controller Keyboard", FileName.CONTROLLER.getName()),
    TABLET(2, "Tablet Keyboard", FileName.TABLET.getName()),
    LEAP_SURFACE(3, "Leap Surface Keyboard", FileName.LEAP_SURFACE.getName()),
    LEAP_AIR_STATIC(4, "Leap Air Static Keyboard", FileName.LEAP_AIR_STATIC.getName()),
    LEAP_AIR_PINCH(5, "Leap Air Pinch Keyboard", FileName.LEAP_AIR_PINCH.getName()),
    LEAP_AIR_DYNAMIC(6, "Leap Air Dynamic Keyboard", FileName.LEAP_AIR_DYNAMIC.getName()),
    LEAP_AIR_BIMODAL(7, "Leap Air Bimodal Keyboard", FileName.LEAP_AIR_BIMODAL.getName()),
    LEAP_AIR_AUGMENTED(8, "Leap Air Augmented Keyboard", FileName.LEAP_AIR_AUGMENTED.getName());
    
    private static final String LEAP = "LEAP";
    private final int keyboardID;
    private final String keyboardName;
    private final String keyboardFileName;
    
    private KeyboardType(int keyboardID, String keyboardName, String keyboardFileName) {
        this.keyboardID = keyboardID;
        this.keyboardName = keyboardName;
        this.keyboardFileName = keyboardFileName;
    }
    
    public int getID() {
        return keyboardID;
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
    
    public static KeyboardType getByID(int keyboardID) {
        for(KeyboardType keyboard: values()) {
            if(keyboard.getID() == keyboardID) return keyboard;
        }
        return null;
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

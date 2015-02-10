package enums;

public enum KeyboardType {
    STANDARD(0, "Standard Keyboard", FileName.STANDARD.getName()),
    CONTROLLER(1, "Controller Keyboard", FileName.CONTROLLER.getName()),
    TABLET(2, "Tablet Keyboard", FileName.TABLET.getName()),
    LEAP_SURFACE(3, "Leap Surface Keyboard", FileName.LEAP_SURFACE.getName()),
    LEAP_AIR(4, "Leap Air Keyboard", FileName.LEAP_AIR.getName()),
    LEAP_PINCH(5, "Leap Pinch Keyboard", FileName.LEAP_PINCH.getName());
    
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
}

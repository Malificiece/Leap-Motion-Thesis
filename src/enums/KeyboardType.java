package enums;

public enum KeyboardType {
    STANDARD(0, "Standard Keyboard", FileName.STANDARD.getName()),
    LEAP_SURFACE(1, "Leap Surface Keyboard", FileName.LEAP_SURFACE.getName()),
    LEAP_AIR(2, "Leap Air Keyboard", FileName.LEAP_AIR.getName()),
    LEAP_PINCH(3, "Leap Pinch Keyboard", FileName.LEAP_PINCH.getName()),
    CONTROLLER(4, "Controller Keyboard", FileName.CONTROLLER.getName()),
    TABLET(5, "Tablet Keyboard", FileName.TABLET.getName());
    
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

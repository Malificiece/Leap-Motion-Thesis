package enums;

public enum KeyboardType {
    STANDARD(0, "Standard Keyboard"),
    LEAP(1, "Leap Keyboard"),
    TABLET(2, "Tablet Keyboard"),
    XBOX(3, "X-box Keyboard");

    private final int keyboardID;
    private final String keyboardName;
    
    private static final KeyboardType[] VALUES = values();
    private static final int SIZE = VALUES.length;
    
    private KeyboardType(int keyboardID, String keyboardName) {
        this.keyboardID = keyboardID;
        this.keyboardName = keyboardName;
    }
    
    public int getKeyboardID() {
        return keyboardID;
    }
    
    public String getKeyboardName() {
        return keyboardName;
    }
    
    public static int getSize() {
        return SIZE;
    }
    
    public static KeyboardType getByID(int keyboardID) {
        if(keyboardID >= 0 && keyboardID < SIZE) {
            return VALUES[keyboardID];
        }
        return null;
    }
    
    public static KeyboardType getByName(String keyboardName) {
        for(int i = 0; i < SIZE; i++) {
            if(VALUES[i].getKeyboardName().equalsIgnoreCase(keyboardName)) return VALUES[i];
        }
        return null;
    }
}

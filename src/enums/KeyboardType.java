package enums;

import keyboard.IKeyboard;
import keyboard.leap.LeapKeyboard;
import keyboard.standard.StandardKeyboard;

public enum KeyboardType {
    STANDARD(0, "Standard Keyboard", new StandardKeyboard()),
    LEAP(1, "Leap Keyboard", new LeapKeyboard()),
    TABLET(2, "Tablet Keyboard", new StandardKeyboard()),
    XBOX(3, "X-box Keyboard", new StandardKeyboard());

    private final int keyboardID;
    private final String keyboardName;
    private final IKeyboard keyboard;
    
    private static final KeyboardType[] VALUES = values();
    private static final int SIZE = VALUES.length;
    
    private KeyboardType(int keyboardID, String keyboardName, IKeyboard keyboard) {
        this.keyboardID = keyboardID;
        this.keyboardName = keyboardName;
        this.keyboard = keyboard;
    }
    
    public int getKeyboardID() {
        return keyboardID;
    }
    
    public String getKeyboardName() {
        return keyboardName;
    }
    
    public IKeyboard getKeyboard() {
        return keyboard;
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

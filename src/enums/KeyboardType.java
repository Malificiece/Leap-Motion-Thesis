package enums;

import keyboard.IKeyboard;
import keyboard.controller.ControllerKeyboard;
import keyboard.leap.LeapKeyboard;
import keyboard.standard.StandardKeyboard;
import keyboard.tablet.TabletKeyboard;

public enum KeyboardType {
    STANDARD(StandardKeyboard.KEYBOARD_ID, "Standard Keyboard", new StandardKeyboard()),
    LEAP(LeapKeyboard.KEYBOARD_ID, "Leap Keyboard", new LeapKeyboard()),
    TABLET(TabletKeyboard.KEYBOARD_ID, "Tablet Keyboard", new TabletKeyboard()),
    CONTROLLER(ControllerKeyboard.KEYBOARD_ID, "Controller Keyboard", new ControllerKeyboard());

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

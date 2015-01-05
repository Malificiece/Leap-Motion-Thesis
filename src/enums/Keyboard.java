package enums;

import keyboard.IKeyboard;
import keyboard.controller.ControllerKeyboard;
import keyboard.leap.LeapKeyboard;
import keyboard.standard.StandardKeyboard;
import keyboard.tablet.TabletKeyboard;

public enum Keyboard {
    STANDARD(new StandardKeyboard()),
    LEAP(new LeapKeyboard()),
    TABLET(new TabletKeyboard()),
    CONTROLLER(new ControllerKeyboard());

    private final int keyboardID;
    private final String keyboardName;
    private final IKeyboard keyboard;
    
    private static final Keyboard[] VALUES = values();
    private static final int SIZE = VALUES.length;
    
    private Keyboard(IKeyboard keyboard) {
        this.keyboardID = keyboard.getID();
        this.keyboardName = keyboard.getName();
        this.keyboard = keyboard;
    }
    
    public int getID() {
        return keyboardID;
    }
    
    public String getName() {
        return keyboardName;
    }
    
    public IKeyboard getKeyboard() {
        return keyboard;
    }
    
    public static int getSize() {
        return SIZE;
    }
    
    public static Keyboard getByID(int keyboardID) {
        if(keyboardID >= 0 && keyboardID < SIZE) {
            return VALUES[keyboardID];
        }
        return null;
    }
    
    public static Keyboard getByName(String keyboardName) {
        for(int i = 0; i < SIZE; i++) {
            if(VALUES[i].getName().equalsIgnoreCase(keyboardName)) return VALUES[i];
        }
        return null;
    }
}

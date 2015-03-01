package enums;

import keyboard.IKeyboard;
import keyboard.controller.ControllerKeyboard;
import keyboard.leap.LeapKeyboard;
import keyboard.standard.StandardKeyboard;
import keyboard.tablet.TabletKeyboard;

public enum Keyboard {
    STANDARD(new StandardKeyboard()),
    LEAP_SURFACE(new LeapKeyboard(KeyboardType.LEAP_SURFACE)),
    LEAP_AIR_STATIC(new LeapKeyboard(KeyboardType.LEAP_AIR_STATIC)),
    LEAP_AIR_PINCH(new LeapKeyboard(KeyboardType.LEAP_AIR_PINCH)),
    //LEAP_AIR_DYNAMIC(new LeapKeyboard(KeyboardType.LEAP_AIR_DYNAMIC)),
    //LEAP_AIR_BIMODAL(new LeapKeyboard(KeyboardType.LEAP_AIR_BIMODAL)),
    //LEAP_AIR_AUGMENTED(new LeapKeyboard(KeyboardType.LEAP_AIR_AUGMENTED)),
    TABLET(new TabletKeyboard()),
    CONTROLLER(new ControllerKeyboard());

    private final int keyboardID;
    private final String keyboardName;
    private final IKeyboard keyboard;
    
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
    
    public static Keyboard getByID(int keyboardID) {
        for(Keyboard keyboard: values()) {
            if(keyboard.getID() == keyboardID) return keyboard;
        }
        return null;
    }
    
    public static Keyboard getByName(String keyboardName) {
        for(Keyboard keyboard: values()) {
            if(keyboard.getName().equalsIgnoreCase(keyboardName)) return keyboard;
        }
        return null;
    }
}

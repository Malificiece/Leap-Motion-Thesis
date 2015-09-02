package enums;

import keyboard.IKeyboard;
import keyboard.controller.ControllerKeyboard;
import keyboard.leap.LeapKeyboard;
import keyboard.standard.StandardKeyboard;
import keyboard.tablet.TabletKeyboard;

public enum Keyboard {
    STANDARD(KeyboardType.DISABLED),
    CONTROLLER_CONSOLE(KeyboardType.DISABLED),
    CONTROLLER_GESTURE(KeyboardType.DISABLED),
    TABLET(KeyboardType.TABLET),
    LEAP_SURFACE(KeyboardType.LEAP_SURFACE),
    LEAP_AIR_STATIC(KeyboardType.LEAP_AIR_STATIC),
    LEAP_AIR_PINCH(KeyboardType.LEAP_AIR_PINCH),
    LEAP_AIR_DYNAMIC(KeyboardType.LEAP_AIR_DYNAMIC),
    LEAP_AIR_BIMODAL(KeyboardType.LEAP_AIR_BIMODAL),
    LEAP_AIR_AUGMENTED(KeyboardType.DISABLED);

    private final KeyboardType keyboardType;
    private final String keyboardName;
    private final IKeyboard keyboard;
    
    private Keyboard(KeyboardType keyboardType) {
        switch(keyboardType) {
            case CONTROLLER_CONSOLE:
                this.keyboard = new ControllerKeyboard(keyboardType);
                break;
            case CONTROLLER_GESTURE:
                this.keyboard = new ControllerKeyboard(keyboardType);
                break;
            case DISABLED:
                this.keyboard = null;
                break;
            case LEAP_AIR_AUGMENTED:
                this.keyboard = new LeapKeyboard(keyboardType);
                break;
            case LEAP_AIR_BIMODAL:
                this.keyboard = new LeapKeyboard(keyboardType);
                break;
            case LEAP_AIR_DYNAMIC:
                this.keyboard = new LeapKeyboard(keyboardType);
                break;
            case LEAP_AIR_PINCH:
                this.keyboard = new LeapKeyboard(keyboardType);
                break;
            case LEAP_AIR_STATIC:
                this.keyboard = new LeapKeyboard(keyboardType);
                break;
            case LEAP_SURFACE:
                this.keyboard = new LeapKeyboard(keyboardType);
                break;
            case STANDARD:
                this.keyboard = new StandardKeyboard(keyboardType);
                break;
            case TABLET:
                this.keyboard = new TabletKeyboard(keyboardType);
                break;
            default:
                this.keyboard = null;
                break;
        }
        if(this.keyboard != null) {
            this.keyboardType = keyboardType;
            this.keyboardName = keyboardType.getName();
        } else {
            this.keyboardType = KeyboardType.DISABLED;
            this.keyboardName = KeyboardType.DISABLED.getName();
        }
    }
    
    public KeyboardType getType() {
        return keyboardType;
    }
    
    public String getName() {
        return keyboardName;
    }
    
    public IKeyboard getKeyboard() {
        return keyboard;
    }
    
    public static Keyboard getByType(KeyboardType keyboardType) {
        for(Keyboard keyboard: values()) {
            if(keyboard.getType() == keyboardType) return keyboard;
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

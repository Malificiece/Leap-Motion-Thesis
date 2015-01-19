package enums;
public enum TestType {
    /*KEYBOARD_R(Keyboard.STANDARD.getID(), Keyboard.STANDARD.getName() + " (right-handed)", 'r'),
    KEYBOARD_L(Keyboard.STANDARD.getID(), Keyboard.STANDARD.getName() + " (left-handed)", 'l'),
    LEAP_S_R(Keyboard.LEAP.getID(), "Surface " + Keyboard.LEAP.getName() + " (right-handed)", 'r'),
    LEAP_S_L(Keyboard.LEAP.getID(), "Surface " + Keyboard.LEAP.getName() + " (left-handed)", 'l'),
    LEAP_F_R(Keyboard.LEAP.getID(), "Floating " + Keyboard.LEAP.getName() + " (right-handed)", 'r'),
    LEAP_F_L(Keyboard.LEAP.getID(), "Floating " + Keyboard.LEAP.getName() + " (left-handed)", 'l'),
    TABLET_R(Keyboard.TABLET.getID(), Keyboard.TABLET.getName() + " (right-handed)", 'r'),
    TABLET_L(Keyboard.TABLET.getID(), Keyboard.TABLET.getName() + " (left-handed)", 'l'),
    CONTROLLER(Keyboard.CONTROLLER.getID(), Keyboard.CONTROLLER.getName() + " (both)", 'n');*/
    KEYBOARD(Keyboard.STANDARD.getID(), Keyboard.STANDARD.getName()),
    LEAP_SURFACE(Keyboard.LEAP.getID(), "Surface " + Keyboard.LEAP.getName()),
    LEAP_FLOATING(Keyboard.LEAP.getID(), "Floating " + Keyboard.LEAP.getName()),
    TABLET(Keyboard.TABLET.getID(), Keyboard.TABLET.getName()),
    CONTROLLER(Keyboard.CONTROLLER.getID(), Keyboard.CONTROLLER.getName());

    private final int keyboardID;
    private final String testName;
    //private final char handUsed;
    
    private static final TestType[] VALUES = values();
    private static final int SIZE = VALUES.length;
    
    private TestType(int testID, String testName/*, char handUsed*/) {
        this.keyboardID = testID;
        this.testName = testName;
        //this.handUsed = handUsed;
    }
    
    public int getKeyboardID() {
        return keyboardID;
    }
    
    public String getName() {
        return testName;
    }
    
    /*public char getHandUsed() {
        return handUsed;
    }*/
    
    public static TestType getByName(String testName) {
        for(int i = 0; i < SIZE; i++) {
            if(VALUES[i].getName().equalsIgnoreCase(testName)) return VALUES[i];
        }
        return null;
    }
}
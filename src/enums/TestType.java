package enums;
public enum TestType {
    KEYBOARD(Keyboard.STANDARD.getID(), Keyboard.STANDARD.getName()),
    LEAP_SURFACE(Keyboard.LEAP_SURFACE.getID(), Keyboard.LEAP_SURFACE.getName()),
    LEAP_AIR(Keyboard.LEAP_AIR.getID(), Keyboard.LEAP_AIR.getName()),
    LEAP_PINCH(Keyboard.LEAP_PINCH.getID(), Keyboard.LEAP_PINCH.getName()),
    TABLET(Keyboard.TABLET.getID(), Keyboard.TABLET.getName()),
    CONTROLLER(Keyboard.CONTROLLER.getID(), Keyboard.CONTROLLER.getName());

    private final int keyboardID;
    private final String testName;
    
    private static final TestType[] VALUES = values();
    private static final int SIZE = VALUES.length;
    
    private TestType(int testID, String testName) {
        this.keyboardID = testID;
        this.testName = testName;
    }
    
    public int getKeyboardID() {
        return keyboardID;
    }
    
    public String getName() {
        return testName;
    }
    
    public static TestType getByName(String testName) {
        for(int i = 0; i < SIZE; i++) {
            if(VALUES[i].getName().equalsIgnoreCase(testName)) return VALUES[i];
        }
        return null;
    }
}
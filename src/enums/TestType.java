package enums;
public enum TestType {
    KEYBOARD_R(0, "Default keyboard - right-handed", 'r'),
    KEYBOARD_L(1, "Default keyboard - left-handed", 'l'),
    LEAP_S_R(2, "Small leap keyboard - right-handed", 'r'),
    LEAP_S_L(3, "Small leap keyboard - left-handed", 'l'),
    LEAP_L_R(4, "Large leap keyboard - right-handed", 'r'),
    LEAP_L_L(5, "Large leap keyboard - left-handed", 'l'),
    TABLET_R(6, "Tablet keyboard - right-handed", 'r'),
    TABLET_L(7, "Tablet keyboard - left-handed", 'l'),
    CONTROLLER(8, "Controller keyboard", 'n'),
    VOICE(10, "Voice input", 'n');

    private final int testID;
    private final String testName;
    private final char handUsed;
    
    private static final TestType[] VALUES = values();
    private static final int SIZE = VALUES.length;
    
    private TestType(int testID, String testName, char handUsed) {
        this.testID = testID;
        this.testName = testName;
        this.handUsed = handUsed;
    }
    
    public int getTestID() {
        return testID;
    }
    
    public String getTestName() {
        return testName;
    }
    
    public char getHandUsed() {
        return handUsed;
    }
    
    public static int getSize() {
        return SIZE;
    }
    
    public static TestType getByID(int testID) {
        if(testID >= 0 && testID < SIZE) {
            return VALUES[testID];
        }
        return null;
    }
    
    public static TestType getByName(String testName) {
        for(int i = 0; i < SIZE; i++) {
            if(VALUES[i].getTestName().equalsIgnoreCase(testName)) return VALUES[i];
        }
        return null;
    }
}
package enums;
public enum TestType {
    KEYBOARD_R(0, "Default keyboard - right-handed"),
    KEYBOARD_L(1, "Default keyboard - left-handed"),
    SWIPE_S_R(2, "Small swipe keyboard - right-handed"),
    SWIPE_S_L(3, "Small swipe keyboard - left-handed"),
    SWIPE_L_R(4, "Large swipe keyboard - right-handed"),
    SWIPE_L_L(5, "Large swipe keyboard - left-handed");

    private final int testID;
    private final String testName;
    
    private static final TestType[] VALUES = values();
    private static final int SIZE = VALUES.length;
    
    private TestType(int testID, String testName) {
        this.testID = testID;
        this.testName = testName;
    }
    
    public int getTestID() {
        return testID;
    }
    
    public String getTestName() {
        return testName;
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
}
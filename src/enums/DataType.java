package enums;

public enum DataType {
    TIME_EXPERIMENT_START,
    TIME_EXPERIMENT_END,
    TIME_WORD_START,
    TIME_WORD_END,
    TIME_PRESSED,
    WORD_VALUE,
    KEY_PRESSED,
    KEY_PRESSED_UPPER,
    KEY_EXPECTED,
    KEY_EXPECTED_UPPER,
    TIME_SPECIAL,
    POINT_POSITION,
    TOOL_DIRECTION,
    DIRECTION_PRESSED;
    
    private static final DataType[] VALUES = values();
    private static final int SIZE = VALUES.length;
    
    public static DataType getByName(String typeName) {
        for(int i = 0; i < SIZE; i++) {
            if(VALUES[i].name().equalsIgnoreCase(typeName)) return VALUES[i];
        }
        return null;
    }
}

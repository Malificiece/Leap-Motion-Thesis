package enums;

public enum DataType {
    TIME_EXPERIMENT_START,
    TIME_EXPERIMENT_END,
    TIME_WORD_START,
    TIME_WORD_END,
    TIME_PRESSED,
    WORD_VALUE,
    KEY_PRESSED,
    KEY_EXPECTED,
    TIME_SPECIAL,
    POINT_POSITION,
    TOOL_DIRECTION,
    DIRECTION_PRESSED,
    GESTURE_TYPE,
    GESTURE_STATE,
    PRACTICE_WORD_COUNT;
    
    public static DataType getByName(String typeName) {
        for(DataType dataType: values()) {
            if(dataType.name().equalsIgnoreCase(typeName)) return dataType;
        }
        return null;
    }
}

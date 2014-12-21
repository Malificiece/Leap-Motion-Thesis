package enums;

public enum SettingName {
    GESTURE_CIRCLE_MIN_RADIUS("Gesture.Circle.MinRadius"),
    GESTURE_CIRCLE_MIN_ARC("Gesture.Circle.MinArc"),
    GESTURE_SWIPE_MIN_LENGTH("Gesture.Swipe.MinLength"),
    GESTURE_SWIPE_MIN_VELOCITY("Gesture.Swipe.MinVelocity"),
    GESTURE_KEYTAP_MIN_DOWN_VELOCITY("Gesture.KeyTap.MinDownVelocity"),
    GESTURE_KEYTAP_MIN_HISTORY_SECONDS("Gesture.KeyTap.HistorySeconds"),
    GESTURE_KEYTAP_MIN_DISTANCE("Gesture.KeyTap.MinDistance"),
    GESTURE_SCREENTAP_MIN_FORWARD_VELOCITY("Gesture.ScreenTap.MinForwardVelocity"),
    GESTURE_SCREENTAP_MIN_HISTORY_SECONDS("Gesture.ScreenTap.HistorySeconds"),
    GESTURE_SCREENTAP_MIN_DISTANCE("Gesture.ScreenTap.MinDistance");

    private final String name;
    
    private SettingName(String name) {
        this.name = name;
    }
    
    public String toString() {
        return name;
    }
}

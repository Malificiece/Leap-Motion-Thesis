package enums;

public enum Setting {
    GESTURE_CIRCLE_MIN_RADIUS("Gesture.Circle.MinRadius", 0.1, 5.0, 25.0, DecimalPrecision.ONE),
    GESTURE_CIRCLE_MIN_ARC("Gesture.Circle.MinArc", 0.1, 1.5, 7.5, DecimalPrecision.ONE),
    GESTURE_SWIPE_MIN_LENGTH("Gesture.Swipe.MinLength", 15, 150, 750, DecimalPrecision.DEFAULT),
    GESTURE_SWIPE_MIN_VELOCITY("Gesture.Swipe.MinVelocity", 100, 1000, 5000, DecimalPrecision.DEFAULT),
    TOUCH_THRESHOLD("Touch.Threshold", -0.20, -0.10, 0, DecimalPrecision.TWO);

    private final String name;
    private final double minimumValue;
    private final double defaultValue;
    private final double maximumValue;
    private final DecimalPrecision decimalPrecision;
    
    private Setting(String name, double minimumValue, double defaultValue, double maximumValue, DecimalPrecision decimalPrecision) {
        this.name = name;
        this.minimumValue = minimumValue;
        this.defaultValue = defaultValue;
        this.maximumValue = maximumValue;
        this.decimalPrecision = decimalPrecision;
    }
    
    public String getName() {
        return name;
    }
    
    public double getMinVal() {
        return minimumValue;
    }
    
    public double getDefVal() {
        return defaultValue;
    }
    
    public double getMaxVal() {
        return maximumValue;
    }
    
    public DecimalPrecision getDecimalPrecision() {
        return decimalPrecision;
    }
}

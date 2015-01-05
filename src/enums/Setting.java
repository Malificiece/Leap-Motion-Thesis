package enums;

public enum Setting {
    GESTURE_CIRCLE_MIN_RADIUS(0.1, 5.0, 25.0, DecimalPrecision.ONE),
    GESTURE_CIRCLE_MIN_ARC(0.1, 1.5, 7.5, DecimalPrecision.ONE),
    GESTURE_SWIPE_MIN_LENGTH(15, 150, 750, DecimalPrecision.DEFAULT),
    GESTURE_SWIPE_MIN_VELOCITY(100, 1000, 5000, DecimalPrecision.DEFAULT),
    TOUCH_THRESHOLD(-0.20, -0.10, 0, DecimalPrecision.TWO);

    private final double minimumValue;
    private final double defaultValue;
    private final double maximumValue;
    private final DecimalPrecision decimalPrecision;
    
    private Setting(double minimumValue, double defaultValue, double maximumValue, DecimalPrecision decimalPrecision) {
        this.minimumValue = minimumValue;
        this.defaultValue = defaultValue;
        this.maximumValue = maximumValue;
        this.decimalPrecision = decimalPrecision;
    }
    
    public double getMin() {
        return minimumValue;
    }
    
    public double getDef() {
        return defaultValue;
    }
    
    public double getMax() {
        return maximumValue;
    }
    
    public DecimalPrecision getDecimalPrecision() {
        return decimalPrecision;
    }
}

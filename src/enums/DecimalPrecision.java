package enums;

public enum DecimalPrecision {
    DEFAULT(0, 1),
    ONE(1, 0.1),
    TWO(2, 0.01),
    THREE(3, 0.001),
    FOUR(4, 0.0001),
    FIVE(5, 0.00001);

    private final int places;
    private final double precision;
    
    private DecimalPrecision(int places, double precision) {
        this.places = places;
        this.precision = precision;
    }
    
    public int getPlaces() {
        return places;
    }
    
    public double getPrecision() {
        return precision;
    }
    
    public double round(double value) {
        return Math.round((double) value / precision) * precision;
    }
}

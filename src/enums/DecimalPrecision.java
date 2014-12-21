package enums;

public enum DecimalPrecision {
    DEFAULT(0, 1),
    ONE(1, 0.1),
    TWO(2, 0.01);

    private final int places;
    private final double precision;
    
    private static final DecimalPrecision[] VALUES = values();
    private static final int SIZE = VALUES.length;
    
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
    
    public static int getSize() {
        return SIZE;
    }
}

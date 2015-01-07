package enums;

public enum Color {
    RED(1f, 0f, 0f),
    GREEN(0f, 1f, 0f),
    BLUE(0f, 0f, 1f),
    YELLOW(1f, 1f, 0f),
    CYAN(0.4f, 0.7f, 1f),
    TEAL(0f, 1f, 0.5f),
    WHITE(1f, 1f, 1f),
    BLACK(0f, 0f, 0f),
    WOOD(0.85f, 0.7f, 0.41f);
    
    private final float[] color;
    
    private Color(float red, float green, float blue) {
        color = new float[] {red, green, blue, 1f};
    }
    
    public float[] getColor() {
        return color;
    }
}

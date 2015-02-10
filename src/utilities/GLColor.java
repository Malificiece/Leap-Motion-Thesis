package utilities;

import javax.media.opengl.GL2;

import enums.Color;

public class GLColor {
    private static final int SIZE = 4;
    private static final int ALPHA = 3;
    private final float[] COLOR = new float[SIZE];
    
    public GLColor(Color color) {
        float[] c = color.getColor();
        for(int i = 0; i < SIZE; i++) {
            COLOR[i] = c[i];
        }
    }
    
    public GLColor(Color color, float alpha) {
        float[] c = color.getColor();
        for(int i = 0; i < SIZE; i++) {
            COLOR[i] = c[i];
        }
        COLOR[ALPHA] = alpha;
    }
    
    public float getAlpha() {
        return COLOR[ALPHA];
    }
    
    public void setAlpha(float alpha) {
        COLOR[ALPHA] = alpha;
    }
    
    public void glColor(GL2 gl) {
        gl.glColor4fv(COLOR, 0);
    }
}

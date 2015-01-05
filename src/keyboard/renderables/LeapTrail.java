package keyboard.renderables;

import static javax.media.opengl.GL2.*;
import javax.media.opengl.GL2;

import keyboard.KeyboardAttributes;
import keyboard.KeyboardRenderable;

import com.leapmotion.leap.Vector;

import enums.Renderable;

public class LeapTrail extends KeyboardRenderable {
    private static final Renderable TYPE = Renderable.LEAP_TRAIL;
    private final float[] COLOR = {1f, 1f, 0f, 1f};
    private final int LINE_SIZE = 50;
    private Vector[] line;
    private int lineIndex = 0;

    public LeapTrail(KeyboardAttributes keyboardAttributes) {
        super(TYPE);
        line = new Vector[LINE_SIZE];
        for(int i = 0; i < LINE_SIZE; i++) {
            line[i] = Vector.zero();
        }
    }
    
    public void update(Vector point) {
        line[lineIndex = ++lineIndex % LINE_SIZE] = point;
    }

    @Override
    public void render(GL2 gl) {
        if(isEnabled()) {
            gl.glPushMatrix();
            gl.glColor4fv(COLOR, 0);
            gl.glLineWidth(10);
            for(int i = 0; i < LINE_SIZE; i++) {
                if(i != lineIndex) {
                    int j = (i + 1) % LINE_SIZE;
                    if(!line[i].equals(Vector.zero()) && !line[j].equals(Vector.zero())) {
                        drawLine(gl, line[i], line[j]);
                    }
                }
            }
            gl.glPopMatrix();
        }
    }
    
    private void drawLine(GL2 gl, Vector source, Vector dest) {
        gl.glBegin(GL_LINES);
        gl.glVertex3f(source.getX(), source.getY(), source.getZ());
        gl.glVertex3f(dest.getX(), dest.getY(), dest.getZ());
        gl.glEnd();
    }
    
    /*private void drawPoint(GL2 gl) {
        // only use if we want to draw a dot for each vertex
    }*/
}

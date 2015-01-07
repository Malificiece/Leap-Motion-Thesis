package keyboard.renderables;

import static javax.media.opengl.GL2.*;

import javax.media.opengl.GL2;

import utilities.GLColor;
import keyboard.KeyboardAttributes;
import keyboard.KeyboardRenderable;

import com.leapmotion.leap.Vector;

import enums.Color;
import enums.Renderable;

public class LeapTrail extends KeyboardRenderable {
    private static final Renderable TYPE = Renderable.LEAP_TRAIL;
    private final GLColor COLOR = new GLColor(Color.YELLOW);
    private final int SHRINK_DURATION = 250;
    private final float LINE_WIDTH = 7;
    private final int LINE_SIZE = 50;
    private Vector[] line;
    private int lineIndex = 0;
    private boolean isTouching = false;
    private boolean isShrinking = false;
    private boolean isCleared = false;
    private float lineWidth = LINE_WIDTH;
    private long previousTime;
    private long fadeTimeElapsed = 0;

    public LeapTrail(KeyboardAttributes keyboardAttributes) {
        super(TYPE);
        line = new Vector[LINE_SIZE];
    }
    
    public void update(Vector point) {
        if(!isTouching) {
            if(!isCleared) {
                clearTrail();
            }
            isTouching = true;
            isShrinking = false;
            isCleared = false;
            lineWidth = LINE_WIDTH;
        }
        line[lineIndex = ++lineIndex % LINE_SIZE] = point;
    }
    
    public void update() {
        if(isTouching) {
            isTouching = false;
            isShrinking = true;
            previousTime = System.currentTimeMillis();
            fadeTimeElapsed = 0;
        }else if(isShrinking) {
            long now = System.currentTimeMillis();
            fadeTimeElapsed += now - previousTime;
            previousTime = now;
            
            if(fadeTimeElapsed <= SHRINK_DURATION) {
                lineWidth = (1f - fadeTimeElapsed/(float)SHRINK_DURATION)*LINE_WIDTH;
            } else {
                isShrinking = false;
            }
        } else if(!isCleared) {
            clearTrail();
        }
    }
    
    private void clearTrail() {
        for(int i = 0; i < LINE_SIZE; i++) {
            line[i] = null;
        }
        lineIndex = 0;
        isCleared = true;
    }

    @Override
    public void render(GL2 gl) {
        if(isEnabled()) {
            gl.glPushMatrix();
            gl.glNormal3f(0, 0, 1);
            COLOR.glColor(gl);
            gl.glLineWidth(lineWidth);
            for(int i = 0; i < LINE_SIZE; i++) {
                if(i != lineIndex) {
                    int j = (i + 1) % LINE_SIZE;
                    if(line[i] != null && line[j] != null) {
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
}

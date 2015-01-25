package keyboard.renderables;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.leapmotion.leap.Vector;

import enums.Color;
import enums.Key;
import static javax.media.opengl.GL2.*;

import javax.media.opengl.GL2;
import javax.swing.Timer;

import utilities.GLColor;
import utilities.MyUtilities;

public class VirtualKey {
    private static enum KeyState {
        LOCKED(Color.BLUE),
        PRESSED(Color.GREEN),
        HOVER(Color.YELLOW),
        NONE(Color.RED);
        
        private final GLColor color;
        
        private KeyState(Color color) {
            this.color = new GLColor(color);
        }
        
        public void glColor(GL2 gl) {
            color.glColor(gl);
        }
        
        public void setAlpha(float alpha) {
            color.setAlpha(alpha);
        }
    };
    private Vector max;
    private Vector min; // also equivalent to location (not center)
    private Vector center;
    private int width;
    private int height;
    private Key key;
    private Timer lightUpKeyTimer;
    private KeyState keyState;
    
    public VirtualKey(float x, float y, float width, float height, Key key) {
        min = new Vector(x, y, 0);
        max = new Vector(x+width, y+height, 0);
        center = MyUtilities.MATH_UTILITILES.findMidpoint(min, max);
        this.width = (int)width;
        this.height = (int)height;
        this.key = key;
        keyState = KeyState.NONE;
        
        lightUpKeyTimer = new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                lightUpKeyTimer.stop();
                keyState = KeyState.NONE;
            }
        });
    }
    
    public Vector getCenter() {
        return center;
    }
    
    public void pressed() {
        keyState = KeyState.PRESSED;
        lightUpKeyTimer.restart();
    }
    
    public void locked() {
        keyState = KeyState.LOCKED;
    }
    
    public void selected() {
        keyState = KeyState.HOVER;
    }
    
    public void deselected() {
        keyState = KeyState.NONE;
    }
    
    public boolean isHovering(Vector point) {
        if(max.getX() < point.getX() || max.getY() < point.getY()) {
            keyState = KeyState.NONE;
            return false;
        }
        if(min.getX() > point.getX() || min.getY() > point.getY()) {
            keyState = KeyState.NONE;
            return false;
        }
        keyState = KeyState.HOVER;
        return true;
    }
    
    public Key getKey() {
        return key;
    }
    
    public void clear() {
        keyState = KeyState.NONE;
    }
    
    public void render(GL2 gl) {
        gl.glPushMatrix();
        gl.glNormal3f(0, 0, 1);
        gl.glTranslatef(min.getX(), min.getY(), min.getZ());
        keyState.setAlpha(0.3f);
        keyState.glColor(gl);
        drawRectangle(gl);
        gl.glPopMatrix();
    }
    
    private void drawRectangle(GL2 gl) {
        // Draw the key.
        gl.glBegin(GL_TRIANGLES);
        gl.glVertex3i(0, 0, 0);
        gl.glVertex3i(width, 0, 0);
        gl.glVertex3i(0, height, 0);
        
        gl.glVertex3i(width, height, 0);
        gl.glVertex3i(width, 0, 0);
        gl.glVertex3i(0, height, 0);
        gl.glEnd();
        
        if(keyState != KeyState.NONE) {
            drawHighlight(gl);
        }
    }
    
    private void drawHighlight(GL2 gl) {
        keyState.setAlpha(0.8f);
        keyState.glColor(gl);
        gl.glLineWidth(3);
        gl.glBegin(GL_LINE_LOOP);
        gl.glVertex3i(0, 0, 0);
        gl.glVertex3i(0, height, 0);
        gl.glVertex3i(width, height, 0);
        gl.glVertex3i(width, 0, 0);
        gl.glEnd();
    }
}

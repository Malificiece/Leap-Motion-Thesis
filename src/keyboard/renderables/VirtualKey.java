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
import utilities.Point;

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
    private static final int LIGHT_UP_TIME = 75;
    private Vector max;
    private Vector min; // also equivalent to location (not center)
    private Vector center;
    private float width;
    private float height;
    private Point paddingSize;
    private Key key;
    private Timer lightUpKeyTimer;
    private KeyState keyState;
    
    public VirtualKey(float x, float y, Point size, Point gapSize, Key key) {
        width = size.x;
        height = size.y;
        min = new Vector(x, y, 0);
        max = new Vector(x + width, y + height, 0);
        center = MyUtilities.MATH_UTILITILES.findMidpoint(min, max);
        paddingSize = new Point((int) (gapSize.getX()/2), (int) (gapSize.getY()/2));
        this.key = key;
        keyState = KeyState.NONE;
        
        lightUpKeyTimer = new Timer(LIGHT_UP_TIME, new ActionListener() {
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
        if(keyState != KeyState.PRESSED) {
            keyState = KeyState.HOVER;
        }
    }
    
    public void deselected() {
        if(keyState != KeyState.PRESSED) {
            keyState = KeyState.NONE;
        }
    }
    
    // Hovering is only called by the surface/leap interaction so we need to include the padding between keys.
    public boolean isHovering(Vector point) {
        if(max.getX() + paddingSize.x < point.getX() || max.getY() + paddingSize.y < point.getY()) {
            if(keyState != KeyState.PRESSED) {
                keyState = KeyState.NONE;
            }
            return false;
        }
        if(min.getX() - paddingSize.x > point.getX() || min.getY() - paddingSize.y > point.getY()) {
            if(keyState != KeyState.PRESSED) {
                keyState = KeyState.NONE;
            }
            return false;
        }
        if(keyState != KeyState.PRESSED) {
            keyState = KeyState.HOVER;
        }
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
        gl.glVertex3f(0, 0, 0);
        gl.glVertex3f(width, 0, 0);
        gl.glVertex3f(0, height, 0);
        
        gl.glVertex3f(width, height, 0);
        gl.glVertex3f(width, 0, 0);
        gl.glVertex3f(0, height, 0);
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
        gl.glVertex3f(0, 0, 0);
        gl.glVertex3f(0, height, 0);
        gl.glVertex3f(width, height, 0);
        gl.glVertex3f(width, 0, 0);
        gl.glEnd();
    }
}

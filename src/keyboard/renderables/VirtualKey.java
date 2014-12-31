package keyboard.renderables;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.leapmotion.leap.Vector;

import enums.Key;

import javax.media.opengl.GL2;
import javax.swing.Timer;

public class VirtualKey {
    private final static float[] ACTIVE_COLOR = {0f, 1f, 0f, 0.5f};
    private final static float[] HOVER_COLOR = {1f, 1f, 0f, 0.5f};
    private final static float[] NONE_COLOR = {1f, 0f, 0f, 0.5f};
    private Vector max;
    private Vector min; // also equivalent to location (not center)
    private int width;
    private int height;
    private Key key;
    private float[] color;
    private Timer lightUpKeyTimer;
    
    public VirtualKey(float x, float y, float width, float height, Key key) {
        min = new Vector(x, y, 0);
        max = new Vector(x+width, y+height, 0);
        this.width = (int)width;
        this.height = (int)height;
        this.key = key;
        color = NONE_COLOR;
        
        lightUpKeyTimer = new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                lightUpKeyTimer.stop();
                color = NONE_COLOR;
            }
        });
    }
    
    public void pressed() {
        color = ACTIVE_COLOR;
        lightUpKeyTimer.restart();
    }
    
    public void render(GL2 gl) {
        gl.glPushMatrix();
        gl.glColor4fv(color, 0);
        gl.glTranslatef(min.getX(), min.getY(), min.getZ());
        gl.glRecti(0, 0, width, height);
        gl.glPopMatrix();
    }
    
    public boolean isHovering(Vector point) {
        if(max.getX() < point.getX() || max.getY() < point.getY()) {
            color = NONE_COLOR;
            return false;
        }
        if(min.getX() > point.getX() || min.getY() > point.getY()) {
            color = NONE_COLOR;
            return false;
        }
        color = HOVER_COLOR;
        return true;
    }
    
    public Key getKey() {
        return key;
    }
    
    public void clear() {
        color = NONE_COLOR;
    }
}

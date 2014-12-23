package keyboard.renderables;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import com.leapmotion.leap.Vector;

import enums.Key;

import javax.media.opengl.GL2;
import javax.swing.Timer;

//import static javax.media.opengl.GL.*;  // GL constants
//import static javax.media.opengl.GL2.*; // GL2 constants

public class VirtualKey {
    private final static int THICKNESS = 10;
    private final static float[] ACTIVE = {0f, 1f, 0f, 0.5f};
    private final static float[] HOVER = {1f, 1f, 0f, 0.5f};
    private final static float[] NONE = {1f, 0f, 0f, 0.5f};
    private Vector max;
    private Vector min; // also equivalent to location (not center)
    private int width;
    private int height;
    private Key key;
    private FloatBuffer color;
    private Timer lightUpKeyTimer;
    
    public VirtualKey(float x, float y, float width, float height, Key key) {
        min = new Vector(x, y, 0);
        max = new Vector(x+width, y+height, THICKNESS);
        this.width = (int)width;
        this.height = (int)height;
        this.key = key;
        ByteBuffer vbb = ByteBuffer.allocateDirect(16); 
        vbb.order(ByteOrder.nativeOrder());    // use the device hardware's native byte order
        color = vbb.asFloatBuffer();  // create a floating point buffer from the ByteBuffer
        color.put(NONE);
        color.position(0);
        
        lightUpKeyTimer = new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                lightUpKeyTimer.stop();
                color.put(NONE);
                color.position(0);
            }
        });
    }
    
    public void pressed() {
        color.put(ACTIVE);
        color.position(0);
        lightUpKeyTimer.restart();
    }
    
    public void render(GL2 gl) {
        gl.glPushMatrix();
        gl.glTranslatef(min.getX(), min.getY(), min.getZ());
        // figure out how to make color change based off distance to key
        gl.glColor4fv(color);
        gl.glRecti(0, 0, width, height);
        gl.glPopMatrix();
    }
    
    public boolean isHovering(Vector point) {
        if(max.getX() < point.getX() || max.getY() < point.getY()) {
            color.put(NONE);
            color.position(0);
            return false;
        }
        if(min.getX() > point.getX() || min.getY() > point.getY()) {
            color.put(NONE);
            color.position(0);
            return false;
        }
        color.put(HOVER);
        color.position(0);
        return true;
    }
    
    public boolean isTouching(Vector point) {
        if(max.getX() < point.getX() || max.getY() < point.getY() || max.getZ() < point.getZ())
            return false;
        if(min.getX() > point.getX() || min.getY() > point.getY() || min.getZ() > point.getZ())
            return false;
        pressed();
        return true;
    }
    
    public Key getKey() {
        return key;
    }
}

package keyboard;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import com.leapmotion.leap.Vector;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import static javax.media.opengl.GL.*;  // GL constants
import static javax.media.opengl.GL2.*; // GL2 constants

public class VirtualKey {
    private final static int THICKNESS = 5;
    private final static float[] ACTIVE = {0f, 1f, 0f, 0.5f};
    private final static float[] HOVER = {1f, 1f, 0f, 0.5f};
    private final static float[] NONE = {1f, 0f, 0f, 0.5f};
    private Vector max;
    private Vector min; // also equivalent to location (not center)
    private int width;
    private int height;
    private String value;
    private FloatBuffer color;
    
    public VirtualKey(float x, float y, float width, float height, String value) {
        min = new Vector(x, y, 0);
        max = new Vector(x+width, y+height, THICKNESS);
        this.width = (int)width;
        this.height = (int)height;
        this.value = value;
        ByteBuffer vbb = ByteBuffer.allocateDirect(16); 
        vbb.order(ByteOrder.nativeOrder());    // use the device hardware's native byte order
        color = vbb.asFloatBuffer();  // create a floating point buffer from the ByteBuffer
        color.put(ACTIVE);
        color.position(0);
        color.put(NONE);
        color.position(0);
    }
    
    public void render(GL2 gl) {
        gl.glPushMatrix();
        gl.glTranslatef(min.getX(), min.getY(), min.getZ());
        // figure out how to make color change based off distance to key
        gl.glColor4fv(color);
        gl.glRecti(0, 0, width, height);
        gl.glPopMatrix();
    }
    
    private boolean containsPoint(Vector point) {
        if(max.getX() < point.getX() || max.getY() < point.getY() || max.getZ() < point.getZ())
            return false;
        if(min.getX() > point.getX() || min.getY() > point.getY() || min.getZ() > point.getZ())
            return false;
        //color = ACTIVE;
        return true;
    }
}

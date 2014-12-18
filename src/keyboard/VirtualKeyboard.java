package keyboard;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import com.leapmotion.leap.Vector;

import static javax.media.opengl.GL.*;  // GL constants
import static javax.media.opengl.GL2.*; // GL2 constants

public class VirtualKeyboard {
    public static final int HEIGHT = 385;
    public static final int WIDTH = 647;
    private static final int GAP_SIZE = 3;
    private static final int KEY_WIDTH = 62;
    private static final int KEY_HEIGHT = 94;
    private static final int NUM_KEYS = 29; // ignoring dark buttons for now
    private static final int SECOND_ROW_OFFSET = 33;
    private static final int THIRD_ROW_OFFSET = 85;
    private static final int FOURTH_ROW_OFFSET = 150;
    private static final int SPACE_KEY_WIDTH = 192;
    private static String [] firstRow = {"q", "w", "e", "r", "t", "y", "u", "i", "o", "p"};
    private static String [] secondRow = {"a", "s", "d", "f", "g", "h", "j", "k", "l"};
    private static String [] thirdRow = {"z", "x", "c", "v", "b", "n", "m"};
    private static String [] fourthRow = {",", " ", "."};
    private static GL2 gl;
    private static KeyboardImage  keyboardImage;
    private static VirtualKey [] keys;
    
    public static void init(GL2 gl) {
        keyboardImage = new KeyboardImage(null, null);
        keys = new VirtualKey[NUM_KEYS];
        
        int index = 0;
        // add first row keys
        float x = 0;
        float y = HEIGHT - KEY_HEIGHT;
        for(int rowIndex = 0; rowIndex < firstRow.length; rowIndex++) {
            keys[index++] = new VirtualKey(x, y, KEY_WIDTH, KEY_HEIGHT, firstRow[rowIndex]);
            x += KEY_WIDTH + GAP_SIZE;
        }
        
        // add second row keys
        x = SECOND_ROW_OFFSET;
        y -= (GAP_SIZE + KEY_HEIGHT);
        for(int rowIndex = 0; rowIndex < secondRow.length; rowIndex++) {
            keys[index++] = new VirtualKey(x, y, KEY_WIDTH, KEY_HEIGHT, secondRow[rowIndex]);
            x += KEY_WIDTH + GAP_SIZE;
        }
        
        // add third row keys
        x = THIRD_ROW_OFFSET;
        y -= (GAP_SIZE + KEY_HEIGHT);
        for(int rowIndex = 0; rowIndex < thirdRow.length; rowIndex++) {
            keys[index++] = new VirtualKey(x, y, KEY_WIDTH, KEY_HEIGHT, thirdRow[rowIndex]);
            x += KEY_WIDTH + GAP_SIZE;
        }
        
        // add fourth row keys
        x = FOURTH_ROW_OFFSET;
        y -= (GAP_SIZE + KEY_HEIGHT);
        for(int rowIndex = 0; rowIndex < fourthRow.length; rowIndex++) {
            if(fourthRow[rowIndex].equalsIgnoreCase(" ")) {
                keys[index++] = new VirtualKey(x, y, SPACE_KEY_WIDTH, KEY_HEIGHT, fourthRow[rowIndex]);
                x += SPACE_KEY_WIDTH + GAP_SIZE;
            } else {
                keys[index++] = new VirtualKey(x, y, KEY_WIDTH, KEY_HEIGHT, fourthRow[rowIndex]);
                x += KEY_WIDTH + GAP_SIZE;
            }
        }
    }
    
    public static void render(GL2 g, Vector leapPos) {
        gl = g;
        gl.glPushMatrix();
        gl.glTranslatef(0.0f, 0.0f, -100.0f);
        // TODO: Figure out what order is best for drawing. Image on top of colors or colors on top of image etc.
        //drawBackground(); // convert to drawing the leap plane in order to determine if leap plane is correct
        keyboardImage.render(gl);
        for(int keyIndex = 0; keyIndex < keys.length; keyIndex++) {
            keys[keyIndex].render(gl);
        }
        gl.glPopMatrix();
        
        //gl.glTranslatef(-323.5f, -192.5f, -1000.0f); // figure out what to do here in order to do perspective if we use texture
        //gl.GL_TEXTURE_RECTANGLE_ARB --- use this for exact texturing if imaging attempt fails.
    }
    
    private static void drawBackground() {
        gl.glColor4f(0.5f, 0.5f, 0.5f, 0.5f);
        gl.glRecti(0, 0, WIDTH, HEIGHT);
        
        
        /*// Use this for leap plane
        gl.glBegin(GL_QUADS);
        gl.glColor3f(1.0f, 0.0f, 0.0f); // red
        gl.glVertex3f(300, 300, 0);
        gl.glVertex3f(0, 300, -100);
        gl.glVertex3f(0, 0, -100);
        gl.glVertex3f(300, 0, 0);
        gl.glEnd();*/
    }
    

}

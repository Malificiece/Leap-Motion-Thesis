package keyboard.leap;

import javax.media.opengl.GL2;

import keyboard.KeyboardImage;
import keyboard.VirtualKey;
import keyboard.VirtualKeyboard;

import com.leapmotion.leap.Vector;

public class LeapKeyboard extends VirtualKeyboard {
    public static final int HEIGHT = 385;
    public static final int WIDTH = 647;
    private static final int GAP_SIZE = 3;
    private static final int KEY_WIDTH = 62;
    private static final int KEY_HEIGHT = 94;
    private static final int FIRST_ROW_OFFSET = 0;
    private static final int SECOND_ROW_OFFSET = 33;
    private static final int THIRD_ROW_OFFSET = 85;
    private static final int FOURTH_ROW_OFFSET = 150;
    private static final int SPACE_KEY_WIDTH = 192;
    private static final String [] FIRST_ROW = {"q", "w", "e", "r", "t", "y", "u", "i", "o", "p"};
    private static final String [] SECOND_ROW = {"a", "s", "d", "f", "g", "h", "j", "k", "l"};
    private static final String [] THIRD_ROW = {"z", "x", "c", "v", "b", "n", "m"};
    private static final String [] FOURTH_ROW = {",", " ", "."};
    private static KeyboardImage  keyboardImage;
    private static VirtualKey [] keys;
    
    public LeapKeyboard(GL2 gl, Vector leapPos) { // TODO: Why is leapPos here (was originally in render)..not used yet
        this.gl = gl;
        keyboardImage = new KeyboardImage("keyboard.png", "leap/");
        width = WIDTH;
        height = HEIGHT;
        gapSize = GAP_SIZE;
        keyWidth = KEY_WIDTH;
        keyHeight = KEY_HEIGHT;
        spaceKeyWidth = SPACE_KEY_WIDTH;
        numKeys = FIRST_ROW.length + SECOND_ROW.length + THIRD_ROW.length + FOURTH_ROW.length;
        rowOffsets = new int[] {FIRST_ROW_OFFSET,SECOND_ROW_OFFSET,THIRD_ROW_OFFSET,FOURTH_ROW_OFFSET};
        keyRows = new String[][] {FIRST_ROW, SECOND_ROW, THIRD_ROW, FOURTH_ROW};
        keys = createKeys();
    }
    
    @Override
    public void render() {
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
    
    private void drawBackground() {
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
    
    @Override
    public void update() {
        // TODO Auto-generated method stub
    }
    

}

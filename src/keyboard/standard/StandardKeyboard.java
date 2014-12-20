package keyboard.standard;

import javax.media.opengl.GL2;

import keyboard.VirtualKeyboard;
import keyboard.renderables.KeyboardImage;

public class StandardKeyboard extends VirtualKeyboard {
    protected static final int HEIGHT = 298;
    protected static final int WIDTH = 752;
    private static final int GAP_SIZE = 2;
    private static final int KEY_WIDTH = 50;
    private static final int KEY_HEIGHT = 58;
    private static final int FIRST_ROW_OFFSET = 0;
    private static final int SECOND_ROW_OFFSET = 78;
    private static final int THIRD_ROW_OFFSET = 90;
    private static final int FOURTH_ROW_OFFSET = 114;
    private static final int FIFTH_ROW_OFFSET = 204;
    private static final int SPACE_KEY_WIDTH = 344;
    private static final String [] FIRST_ROW = {};
    private static final String [] SECOND_ROW = {"q", "w", "e", "r", "t", "y", "u", "i", "o", "p"};
    private static final String [] THIRD_ROW = {"a", "s", "d", "f", "g", "h", "j", "k", "l"};
    private static final String [] FOURTH_ROW = {"z", "x", "c", "v", "b", "n", "m", ",", "."};
    private static final String [] FIFTH_ROW = {" "};
    //private static KeyboardImage  keyboardImage;
    //private static VirtualKey [] keys;
    
    public StandardKeyboard() { // TODO: Why is leapPos here (was originally in render)..not used yet
        keyboardImage = new KeyboardImage("keyboard.png", "standard/");
        width = WIDTH;
        height = HEIGHT;
        gapSize = GAP_SIZE;
        keyWidth = KEY_WIDTH;
        keyHeight = KEY_HEIGHT;
        spaceKeyWidth = SPACE_KEY_WIDTH;
        numKeys = FIRST_ROW.length + SECOND_ROW.length + THIRD_ROW.length + FOURTH_ROW.length + FIFTH_ROW.length;
        rowOffsets = new int[] {FIRST_ROW_OFFSET,SECOND_ROW_OFFSET,THIRD_ROW_OFFSET,FOURTH_ROW_OFFSET,FIFTH_ROW_OFFSET};
        keyRows = new String[][] {FIRST_ROW, SECOND_ROW, THIRD_ROW, FOURTH_ROW, FIFTH_ROW};
        keys = createKeys();
        
        keyboardRenderables.add(keyboardImage);
    }
    
    @Override
    public void render(GL2 gl) {
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
        key = 's';
        notifyListeners();
    }
    
    @Override
    public void update() {
        // TODO Auto-generated method stub
    }
    

}

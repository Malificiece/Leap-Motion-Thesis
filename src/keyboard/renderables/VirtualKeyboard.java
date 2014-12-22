package keyboard.renderables;

import java.util.TreeMap;

import javax.media.opengl.GL2;

import com.leapmotion.leap.Vector;

import enums.AttributeName;
import enums.RenderableName;
import keyboard.KeyboardAttributes;
import keyboard.KeyboardRenderable;

//import static javax.media.opengl.GL.*;  // GL constants
//import static javax.media.opengl.GL2.*; // GL2 constants

public class VirtualKeyboard extends KeyboardRenderable {
    private static final String RENDER_NAME = RenderableName.VIRTUAL_KEYS.toString();
    private TreeMap<Character, VirtualKey> keys = new TreeMap<Character, VirtualKey>();
    //private VirtualKey [] keys;
    
    public VirtualKeyboard(KeyboardAttributes keyboardAttributes) {
        super(RENDER_NAME);
        createKeys(keyboardAttributes);
    }
    
    private void createKeys(KeyboardAttributes keyboardAttributes) {
        int keyboardHeight = (int) keyboardAttributes.getValueByName(AttributeName.KEYBOARD_HEIGHT.toString());
        int gapSize = (int) keyboardAttributes.getValueByName(AttributeName.GAP_SIZE.toString());
        int keyWidth = (int) keyboardAttributes.getValueByName(AttributeName.KEY_WIDTH.toString());
        int keyHeight = (int) keyboardAttributes.getValueByName(AttributeName.KEY_HEIGHT.toString());
        int spaceKeyWidth = (int) keyboardAttributes.getValueByName(AttributeName.SPACE_KEY_WIDTH.toString());
        //int numKeys = (int) keyboardAttributes.getValueByName(AttributeName.NUMBER_OF_KEYS.toString());
        int[] rowOffsets = (int[]) keyboardAttributes.getValueByName(AttributeName.ROW_OFFSETS.toString());
        char[][] keyRows = (char[][]) keyboardAttributes.getValueByName(AttributeName.KEY_ROWS.toString());
        
        for(int rowIndex = 0, x = 0, y = keyboardHeight + gapSize; rowIndex < keyRows.length; rowIndex++) {
            x = rowOffsets[rowIndex];
            y -= (keyHeight + gapSize);
            for(int colIndex = 0; colIndex < keyRows[rowIndex].length; colIndex++) {
                char key = keyRows[rowIndex][colIndex];
                if(key == ' ') {
                    keys.put(key, new VirtualKey(x, y, spaceKeyWidth, keyHeight, key));
                    x += spaceKeyWidth + gapSize;
                } else {
                    keys.put(key, new VirtualKey(x, y, keyWidth, keyHeight, key));
                    x += keyWidth + gapSize;
                }
            }
        }
    }
    
    public void pressed(char key) { // possibly generate key pressed event here with time/key/etc saved. Return the key pressed object.
        VirtualKey vk = keys.get(key);
        vk.pressed();
    }
    
    public boolean isHovering(char key, Vector point) {
        VirtualKey vk = keys.get(key);
        return vk.isHovering(point);
    }
    
    public boolean isTouching(char key, Vector point) {
        VirtualKey vk = keys.get(key);
        return vk.isTouching(point);
    }

    @Override
    public void render(GL2 gl) {
        if(isEnabled()) {
            for(VirtualKey vk: keys.values()) {
                vk.render(gl);
            }
        }
    }
}

package keyboard.renderables;

import javax.media.opengl.GL2;

import enums.AttributeName;
import enums.RenderableName;
import keyboard.KeyboardAttributes;
import keyboard.KeyboardRenderable;

//import static javax.media.opengl.GL.*;  // GL constants
//import static javax.media.opengl.GL2.*; // GL2 constants

public class VirtualKeyboard extends KeyboardRenderable {
    private VirtualKey [] keys;
    
    public VirtualKeyboard(KeyboardAttributes keyboardAttributes) {
        super(RenderableName.VIRTUAL_KEYS.toString());
        createKeys(keyboardAttributes);
    }
    
    private void createKeys(KeyboardAttributes keyboardAttributes) {
        int keyboardHeight = (int) keyboardAttributes.getValueByName(AttributeName.KEYBOARD_HEIGHT.toString());
        int gapSize = (int) keyboardAttributes.getValueByName(AttributeName.GAP_SIZE.toString());
        int keyWidth = (int) keyboardAttributes.getValueByName(AttributeName.KEY_WIDTH.toString());
        int keyHeight = (int) keyboardAttributes.getValueByName(AttributeName.KEY_HEIGHT.toString());
        int spaceKeyWidth = (int) keyboardAttributes.getValueByName(AttributeName.SPACE_KEY_WIDTH.toString());
        int numKeys = (int) keyboardAttributes.getValueByName(AttributeName.NUMBER_OF_KEYS.toString());
        int[] rowOffsets = (int[]) keyboardAttributes.getValueByName(AttributeName.ROW_OFFSETS.toString());
        String[][] keyRows = (String[][]) keyboardAttributes.getValueByName(AttributeName.KEY_ROWS.toString());
        keys = new VirtualKey[numKeys];
        
        for(int keyIndex = 0, rowIndex = 0, x = 0, y = keyboardHeight + gapSize; rowIndex < keyRows.length; rowIndex++) {
            x = rowOffsets[rowIndex];
            y -= (keyHeight + gapSize);
            for(int colIndex = 0; colIndex < keyRows[rowIndex].length; colIndex++) {
                if(keyRows[rowIndex][colIndex].equalsIgnoreCase(" ")) {
                    keys[keyIndex++] = new VirtualKey(x, y, spaceKeyWidth, keyHeight, keyRows[rowIndex][colIndex]);
                    x += spaceKeyWidth + gapSize;
                } else {
                    keys[keyIndex++] = new VirtualKey(x, y, keyWidth, keyHeight, keyRows[rowIndex][colIndex]);
                    x += keyWidth + gapSize;
                }
            }
        }
    }

    @Override
    public void render(GL2 gl) {
        if(isEnabled()) {
            for(int keyIndex = 0; keyIndex < keys.length; keyIndex++) {
                keys[keyIndex].render(gl);
            }
        }
    }
}

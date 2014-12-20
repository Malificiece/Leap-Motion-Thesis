package keyboard;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import com.leapmotion.leap.Vector;

import static javax.media.opengl.GL.*;  // GL constants
import static javax.media.opengl.GL2.*; // GL2 constants

public abstract class VirtualKeyboard extends IKeyboard {
    protected int gapSize;
    protected int keyWidth;
    protected int keyHeight;
    protected int spaceKeyWidth;
    protected int numKeys;
    protected int [] rowOffsets;
    protected String [][] keyRows;
    protected KeyboardImage  keyboardImage;
    protected VirtualKey [] keys;
    
    protected VirtualKey[] createKeys() {
        VirtualKey [] keys = new VirtualKey[numKeys];
        for(int keyIndex = 0, rowIndex = 0, x = 0, y = height + gapSize; rowIndex < keyRows.length; rowIndex++) {
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
        return keys;
    }
}

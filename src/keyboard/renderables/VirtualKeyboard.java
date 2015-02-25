package keyboard.renderables;

import utilities.MyUtilities;
import utilities.Point;

import java.util.ArrayList;
import java.util.TreeMap;

import javax.media.opengl.GL2;

import com.leapmotion.leap.Vector;

import enums.Attribute;
import enums.Key;
import enums.Renderable;
import keyboard.KeyboardAttributes;
import keyboard.KeyboardRenderable;

//import static javax.media.opengl.GL.*;  // GL constants
//import static javax.media.opengl.GL2.*; // GL2 constants

public class VirtualKeyboard extends KeyboardRenderable {
    private static final Renderable TYPE = Renderable.VIRTUAL_KEYBOARD;
    private TreeMap<Key, VirtualKey> keys = new TreeMap<Key, VirtualKey>();
    
    public VirtualKeyboard(KeyboardAttributes keyboardAttributes) {
        super(TYPE);
        createKeys(keyboardAttributes);
    }
    
    private void createKeys(KeyboardAttributes keyboardAttributes) {
        Point keyboardSize = keyboardAttributes.getAttributeAsPoint(Attribute.KEYBOARD_SIZE);
        Integer borderSize = keyboardAttributes.getAttributeAsInteger(Attribute.BORDER_SIZE);
        Point gapSize = keyboardAttributes.getAttributeAsPoint(Attribute.GAP_SIZE);
        Point keySize = keyboardAttributes.getAttributeAsPoint(Attribute.KEY_SIZE);
        Point spaceKeySize = keyboardAttributes.getAttributeAsPoint(Attribute.SPACE_KEY_SIZE);
        Point backSpaceKeySize = keyboardAttributes.getAttributeAsPoint(Attribute.BACK_SPACE_KEY_SIZE);
        Point shiftKeySize = keyboardAttributes.getAttributeAsPoint(Attribute.SHIFT_KEY_SIZE);
        Point enterKeySize = keyboardAttributes.getAttributeAsPoint(Attribute.ENTER_KEY_SIZE);
        //int numKeys = (int) keyboardAttributes.getValueByName(AttributeName.NUMBER_OF_KEYS);
        Integer[] rowOffsets = (Integer[]) keyboardAttributes.getAttributeValue(Attribute.ROW_OFFSETS);
        Key[][] keyRows = (Key[][]) keyboardAttributes.getAttributeValue(Attribute.KEY_ROWS);
        
        for(int rowIndex = 0, x = 0, y = keyboardSize.y + gapSize.y + borderSize; rowIndex < keyRows.length; rowIndex++) {
            x = rowOffsets[rowIndex] + borderSize;
            y -= (keySize.y + gapSize.y);
            for(int colIndex = 0; colIndex < keyRows[rowIndex].length; colIndex++) {
                Key key = keyRows[rowIndex][colIndex];
                if(key == Key.VK_SPACE) {
                    keys.put(key, new VirtualKey(x, y, spaceKeySize, gapSize, key));
                    x += spaceKeySize.x + gapSize.x;
                } else if (key == Key.VK_BACK_SPACE) {
                    keys.put(key, new VirtualKey(x, y, backSpaceKeySize, gapSize, key));
                    x += backSpaceKeySize.x + gapSize.x;
                } else if (key == Key.VK_SHIFT_LEFT || key == Key.VK_SHIFT_RIGHT || key == Key.VK_SHIFT) {
                    keys.put(key, new VirtualKey(x, y, shiftKeySize, gapSize, key));
                    x += shiftKeySize.x + gapSize.x;
                } else if (key == Key.VK_ENTER) {
                    keys.put(key, new VirtualKey(x, y, enterKeySize, gapSize, key));
                    x += enterKeySize.x + gapSize.x;
                } else if (key == Key.VK_NULL) {
                    // Blank key, don't use it.
                    x += keySize.x + gapSize.x;
                } else {
                    keys.put(key, new VirtualKey(x, y, keySize, gapSize, key));
                    x += keySize.x + gapSize.x;
                }
            }
        }
        
    }
    
    public ArrayList<VirtualKey> getVirtualKeys() {
        ArrayList<VirtualKey> virtualKeys = new ArrayList<VirtualKey>();
        virtualKeys.addAll(keys.values());
        return virtualKeys;
    }
    
    public VirtualKey getVirtualKey(Key key) {
        return keys.get(key);
    }
    
    public void pressed(Key key) {
        VirtualKey virtualKey = keys.get(key);
        if(virtualKey != null) {
            virtualKey.pressed();
        }
    }
    
    public void locked(Key key) {
        VirtualKey virtualKey = keys.get(key);
        if(virtualKey != null) {
            virtualKey.locked();
        }
    }
    
    public void selected(Key key) {
        VirtualKey virtualKey = keys.get(key);
        if(virtualKey != null) {
            virtualKey.selected();
        }
    }
    
    public void deselected(Key key) {
        VirtualKey virtualKey = keys.get(key);
        if(virtualKey != null) {
            virtualKey.deselected();
        }
    }
    
    public VirtualKey isHoveringAny(Vector point) {
        VirtualKey vKey = null;
        for(VirtualKey virtualKey: keys.values()) {
            if(virtualKey.isHovering(point)) {
                vKey = virtualKey;
            }
        }
        return vKey;
    }
    
    public boolean isHovering(Key key, Vector point) {
        VirtualKey virtualKey = keys.get(key);
        if(virtualKey != null) {
            return virtualKey.isHovering(point);
        }
        return false;
    }
    
    public VirtualKey getNearestAlphaKey(Vector point, int maxDistance) {
        VirtualKey vKey = null;
        float minDistance = Float.MAX_VALUE;
        for(VirtualKey virtualKey: keys.values()) {
            if(virtualKey.getKey().isAlpha()) {
                float distance = MyUtilities.MATH_UTILITILES.findDistanceToPoint(point, virtualKey.getCenter());
                if(distance < minDistance && distance <= maxDistance) {
                    minDistance = distance;
                    vKey = virtualKey;
                }
            }
        }
        return vKey;
    }
    
    public VirtualKey getNearestAlphaKey(Vector point) {
        VirtualKey vKey = null;
        float minDistance = Float.MAX_VALUE;
        for(VirtualKey virtualKey: keys.values()) {
            if(virtualKey.getKey().isAlpha()) {
                float distance = MyUtilities.MATH_UTILITILES.findDistanceToPoint(point, virtualKey.getCenter());
                if(distance < minDistance) {
                    minDistance = distance;
                    vKey = virtualKey;
                }
            }
        }
        return vKey;
    }

    @Override
    public void render(GL2 gl) {
        if(isEnabled()) {
            for(VirtualKey virtualKey: keys.values()) {
                virtualKey.render(gl);
            }
        }
    }

    public void clearAll() {
        for(VirtualKey virtualKey: keys.values()) {
            virtualKey.clear();
        }
    }
}

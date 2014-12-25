package enums;

import java.awt.event.KeyEvent;

public enum Key {
    VK_NULL('\0', "VK_NULL", -1),
    VK_A('a', "VK_A", KeyEvent.VK_A),
    VK_B('b', "VK_B", KeyEvent.VK_B),
    VK_C('c', "VK_C", KeyEvent.VK_C),
    VK_D('d', "VK_D", KeyEvent.VK_D),
    VK_E('e', "VK_E", KeyEvent.VK_E),
    VK_F('f', "VK_F", KeyEvent.VK_F),
    VK_G('g', "VK_G", KeyEvent.VK_G),
    VK_H('h', "VK_H", KeyEvent.VK_H),
    VK_I('i', "VK_I", KeyEvent.VK_I),
    VK_J('j', "VK_J", KeyEvent.VK_J),
    VK_K('k', "VK_K", KeyEvent.VK_K),
    VK_L('l', "VK_L", KeyEvent.VK_L),
    VK_M('m', "VK_M", KeyEvent.VK_M),
    VK_N('n', "VK_N", KeyEvent.VK_N),
    VK_O('o', "VK_O", KeyEvent.VK_O),
    VK_P('p', "VK_P", KeyEvent.VK_P),
    VK_Q('q', "VK_Q", KeyEvent.VK_Q),
    VK_R('r', "VK_R", KeyEvent.VK_R),
    VK_S('s', "VK_S", KeyEvent.VK_S),
    VK_T('t', "VK_T", KeyEvent.VK_T),
    VK_U('u', "VK_U", KeyEvent.VK_U),
    VK_V('v', "VK_V", KeyEvent.VK_V),
    VK_W('w', "VK_W", KeyEvent.VK_W),
    VK_X('x', "VK_X", KeyEvent.VK_X),
    VK_Y('y', "VK_Y", KeyEvent.VK_Y),
    VK_Z('z', "VK_Z", KeyEvent.VK_Z),
    VK_SPACE(' ', "VK_SPACE", KeyEvent.VK_SPACE),
    VK_COMMA(',', "VK_COMMA", KeyEvent.VK_COMMA),
    VK_PERIOD('.', "VK_PERIOD", KeyEvent.VK_PERIOD),
    VK_SHIFT((char) KeyEvent.VK_SHIFT, "VK_SHIFT", KeyEvent.VK_SHIFT),
    VK_SHIFT_RELEASED((char) (KeyEvent.VK_SHIFT-1), "VK_SHIFT_RELEASED", KeyEvent.VK_SHIFT-1),
    VK_BACK_SPACE('\b', "VK_BACK_SPACE", KeyEvent.VK_BACK_SPACE),
    VK_ENTER('\n', "VK_ENTER", KeyEvent.VK_ENTER);

    private final char keyValue;
    private final String keyName;
    private final int keyCode;
    
    private static final Key[] VALUES = values();
    private static final int SIZE = VALUES.length;
    
    private Key(char keyValue, String keyName, int keyCode) {
        this.keyValue = keyValue;
        this.keyName = keyName;
        this.keyCode = keyCode;
    }
    
    public char getKeyValue() {
        return keyValue;
    }
    
    public String getKeyName() {
        return keyName;
    }
    
    public int getKeyCode() {
        return keyCode;
    }

    public static int getSize() {
        return SIZE;
    }
    
    public static Key getByValue(int keyValue) {
        for(int i = 0; i < SIZE; i++) {
            if(VALUES[i].getKeyValue() == keyValue) return VALUES[i];
        }
        return null;
    }
    
    public static Key getByName(String keyName) {
        for(int i = 0; i < SIZE; i++) {
            if(VALUES[i].getKeyName().equalsIgnoreCase(keyName)) return VALUES[i];
        }
        return null;
    }
    
    public static Key getByCode(int keyCode) {
        for(int i = 0; i < SIZE; i++) {
            if(VALUES[i].getKeyCode() == keyCode) return VALUES[i];
        }
        return null;
    }
    
    public static Key getByIndex(int index) {
        if(0 <= index && index < SIZE) {
            return VALUES[index];
        }
        return null;
    }
}

package enums;

import java.awt.event.KeyEvent;

public enum Key {
    VK_NULL('\0', '\0', "VK_NULL", -1),
    VK_A('a', 'A', "VK_A", KeyEvent.VK_A),
    VK_B('b', 'B', "VK_B", KeyEvent.VK_B),
    VK_C('c', 'C', "VK_C", KeyEvent.VK_C),
    VK_D('d', 'D', "VK_D", KeyEvent.VK_D),
    VK_E('e', 'E', "VK_E", KeyEvent.VK_E),
    VK_F('f', 'F', "VK_F", KeyEvent.VK_F),
    VK_G('g', 'G', "VK_G", KeyEvent.VK_G),
    VK_H('h', 'H', "VK_H", KeyEvent.VK_H),
    VK_I('i', 'I', "VK_I", KeyEvent.VK_I),
    VK_J('j', 'J', "VK_J", KeyEvent.VK_J),
    VK_K('k', 'K', "VK_K", KeyEvent.VK_K),
    VK_L('l', 'L', "VK_L", KeyEvent.VK_L),
    VK_M('m', 'M', "VK_M", KeyEvent.VK_M),
    VK_N('n', 'N', "VK_N", KeyEvent.VK_N),
    VK_O('o', 'O', "VK_O", KeyEvent.VK_O),
    VK_P('p', 'P', "VK_P", KeyEvent.VK_P),
    VK_Q('q', 'Q', "VK_Q", KeyEvent.VK_Q),
    VK_R('r', 'R', "VK_R", KeyEvent.VK_R),
    VK_S('s', 'S', "VK_S", KeyEvent.VK_S),
    VK_T('t', 'T', "VK_T", KeyEvent.VK_T),
    VK_U('u', 'U', "VK_U", KeyEvent.VK_U),
    VK_V('v', 'V', "VK_V", KeyEvent.VK_V),
    VK_W('w', 'W', "VK_W", KeyEvent.VK_W),
    VK_X('x', 'X', "VK_X", KeyEvent.VK_X),
    VK_Y('y', 'Y', "VK_Y", KeyEvent.VK_Y),
    VK_Z('z', 'Z', "VK_Z", KeyEvent.VK_Z),
    VK_SPACE(' ', ' ', "VK_SPACE", KeyEvent.VK_SPACE),
    VK_COMMA(',', ',', "VK_COMMA", KeyEvent.VK_COMMA),
    VK_PERIOD('.', '.',  "VK_PERIOD", KeyEvent.VK_PERIOD),
    VK_SHIFT((char) KeyEvent.VK_SHIFT, (char) KeyEvent.VK_SHIFT, "VK_SHIFT", KeyEvent.VK_SHIFT),
    VK_SHIFT_RELEASED((char) (KeyEvent.VK_SHIFT-1), (char) (KeyEvent.VK_SHIFT-1), "VK_SHIFT_RELEASED", KeyEvent.VK_SHIFT-1),
    VK_BACK_SPACE('\b', '\b', "VK_BACK_SPACE", KeyEvent.VK_BACK_SPACE),
    VK_ENTER('\n', '\n', "VK_ENTER", KeyEvent.VK_ENTER);

    private final char keyValue;
    private final char keyUpperCase;
    private final String keyName;
    private final int keyCode;
    
    private static final Key[] VALUES = values();
    private static final int SIZE = VALUES.length;
    
    private Key(char keyValue, char keyUpperCase, String keyName, int keyCode) {
        this.keyValue = keyValue;
        this.keyUpperCase = keyUpperCase;
        this.keyName = keyName;
        this.keyCode = keyCode;
    }
    
    public char getKeyValue() {
        return keyValue;
    }
    
    public char toUpper() {
        return keyUpperCase;
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
            if(VALUES[i].getKeyValue() == keyValue || VALUES[i].toUpper() == keyValue) return VALUES[i];
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

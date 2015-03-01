package enums;

import java.awt.event.KeyEvent;

public enum Key {
    VK_NULL('\0', '\0', "VK_NULL", -1, false),
    VK_BLANK_0('\0', '\0', "VK_BLANK", -1, false),
    VK_BLANK_1('\0', '\0', "VK_BLANK", -1, false),
    VK_BLANK_2('\0', '\0', "VK_BLANK", -1, false),
    VK_BLANK_3('\0', '\0', "VK_BLANK", -1, false),
    VK_A('a', 'A', "VK_A", KeyEvent.VK_A, true),
    VK_B('b', 'B', "VK_B", KeyEvent.VK_B, true),
    VK_C('c', 'C', "VK_C", KeyEvent.VK_C, true),
    VK_D('d', 'D', "VK_D", KeyEvent.VK_D, true),
    VK_E('e', 'E', "VK_E", KeyEvent.VK_E, true),
    VK_F('f', 'F', "VK_F", KeyEvent.VK_F, true),
    VK_G('g', 'G', "VK_G", KeyEvent.VK_G, true),
    VK_H('h', 'H', "VK_H", KeyEvent.VK_H, true),
    VK_I('i', 'I', "VK_I", KeyEvent.VK_I, true),
    VK_J('j', 'J', "VK_J", KeyEvent.VK_J, true),
    VK_K('k', 'K', "VK_K", KeyEvent.VK_K, true),
    VK_L('l', 'L', "VK_L", KeyEvent.VK_L, true),
    VK_M('m', 'M', "VK_M", KeyEvent.VK_M, true),
    VK_N('n', 'N', "VK_N", KeyEvent.VK_N, true),
    VK_O('o', 'O', "VK_O", KeyEvent.VK_O, true),
    VK_P('p', 'P', "VK_P", KeyEvent.VK_P, true),
    VK_Q('q', 'Q', "VK_Q", KeyEvent.VK_Q, true),
    VK_R('r', 'R', "VK_R", KeyEvent.VK_R, true),
    VK_S('s', 'S', "VK_S", KeyEvent.VK_S, true),
    VK_T('t', 'T', "VK_T", KeyEvent.VK_T, true),
    VK_U('u', 'U', "VK_U", KeyEvent.VK_U, true),
    VK_V('v', 'V', "VK_V", KeyEvent.VK_V, true),
    VK_W('w', 'W', "VK_W", KeyEvent.VK_W, true),
    VK_X('x', 'X', "VK_X", KeyEvent.VK_X, true),
    VK_Y('y', 'Y', "VK_Y", KeyEvent.VK_Y, true),
    VK_Z('z', 'Z', "VK_Z", KeyEvent.VK_Z, true),
    VK_SPACE(' ', ' ', "VK_SPACE", KeyEvent.VK_SPACE, true),
    VK_COMMA(',', ',', "VK_COMMA", KeyEvent.VK_COMMA, true),
    VK_PERIOD('.', '.',  "VK_PERIOD", KeyEvent.VK_PERIOD, true),
    VK_SHIFT((char) KeyEvent.VK_SHIFT, (char) KeyEvent.VK_SHIFT, "VK_SHIFT", KeyEvent.VK_SHIFT, false),
    VK_SHIFT_LEFT('\0', '\0', "VK_SHIFT_LEFT", -1, false),
    VK_SHIFT_RIGHT('\0', '\0', "VK_SHIFT_RIGHT", -1, false),
    VK_SHIFT_RELEASED((char) (KeyEvent.VK_SHIFT - 1), (char) (KeyEvent.VK_SHIFT - 1), "VK_SHIFT_RELEASED", - 1, false),
    VK_BACK_SPACE('\b', '\b', "VK_BACK_SPACE", KeyEvent.VK_BACK_SPACE, true),
    VK_ENTER('\n', '\n', "VK_ENTER", KeyEvent.VK_ENTER, true),
    VK_0('0', '0', "VK_0", KeyEvent.VK_0, true),
    VK_1('1', '1', "VK_1", KeyEvent.VK_1, true),
    VK_2('2', '2', "VK_2", KeyEvent.VK_2, true),
    VK_3('3', '3', "VK_3", KeyEvent.VK_3, true),
    VK_4('4', '4', "VK_4", KeyEvent.VK_4, true),
    VK_5('5', '5', "VK_5", KeyEvent.VK_5, true),
    VK_6('6', '6', "VK_6", KeyEvent.VK_6, true),
    VK_7('7', '7', "VK_7", KeyEvent.VK_7, true),
    VK_8('8', '8', "VK_8", KeyEvent.VK_8, true),
    VK_9('9', '9', "VK_9", KeyEvent.VK_9, true),
    VK_UP((char) KeyEvent.VK_UP, (char) KeyEvent.VK_UP, "VK_UP", KeyEvent.VK_UP, false),
    VK_DOWN((char) KeyEvent.VK_DOWN, (char) KeyEvent.VK_DOWN, "VK_DOWN", KeyEvent.VK_DOWN, false),
    VK_RIGHT((char) KeyEvent.VK_RIGHT, (char) KeyEvent.VK_RIGHT, "VK_RIGHT", KeyEvent.VK_RIGHT, false),
    VK_LEFT((char) KeyEvent.VK_LEFT, (char) KeyEvent.VK_LEFT, "VK_LEFT", KeyEvent.VK_LEFT, false);

    private final char keyValue;
    private final char keyUpperCase;
    private final String keyName;
    private final int keyCode;
    private final boolean isPrintable;
    
    private Key(char keyValue, char keyUpperCase, String keyName, int keyCode, boolean isPrintable) {
        this.keyValue = keyValue;
        this.keyUpperCase = keyUpperCase;
        this.keyName = keyName;
        this.keyCode = keyCode;
        this.isPrintable = isPrintable;
    }
    
    public char getValue() {
        return keyValue;
    }
    
    public char toUpper() {
        return keyUpperCase;
    }
    
    public String getName() {
        return keyName;
    }
    
    public int getCode() {
        return keyCode;
    }
    
    public boolean isUpper(char value) {
        if(isAlpha()) {
            return value == keyUpperCase;
        }
        return false;
    }
    
    public boolean isPrintable() {
        return isPrintable;
    }
    
    public boolean isBlank() {
        return keyName.equals(VK_BLANK_0.getName());
    }
    
    public boolean isArrow() {
        return 37 <= this.keyCode && this.keyCode <= 40;
    }
    
    public boolean isAlphaNumeric() {
        return this.isNumeric() || this.isAlpha();
    }
    
    public boolean isAlpha() {
        return 65 <= this.keyCode && this.keyCode <= 90;
    }
    
    public boolean isNumeric() {
        return 48 <= this.keyCode && this.keyCode <= 57;
    }
    
    public boolean isSpecial() {
        return this.equals(VK_ENTER) ||
                this.equals(VK_PERIOD) ||
                this.equals(VK_COMMA) ||
                this.equals(VK_SHIFT) ||
                this.equals(VK_BACK_SPACE) ||
                this.equals(VK_SPACE);
    }
    
    public static Key getByValue(int keyValue) {
        for(Key key: values()) {
            if(key.getValue() == keyValue || key.toUpper() == keyValue) return key;
        }
        return null;
    }
    
    public static Key getByName(String keyName) {
        for(Key key: values()) {
            if(key.getName().equalsIgnoreCase(keyName)) return key;
        }
        return null;
    }
    
    public static Key getByCode(int keyCode) {
        for(Key key: values()) {
            if(key.getCode() == keyCode) return key;
        }
        return null;
    }
    
    public static Key getByIndex(int index) {
        if(0 <= index && index < values().length) {
            return values()[index];
        }
        return null;
    }
}

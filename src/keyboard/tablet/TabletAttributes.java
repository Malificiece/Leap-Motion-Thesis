package keyboard.tablet;

import keyboard.KeyboardAttribute;
import keyboard.KeyboardAttributes;
import enums.Attribute;
import enums.Key;

public class TabletAttributes extends KeyboardAttributes {
    private static final int KEYBOARD_HEIGHT = 298;
    private static final int KEYBOARD_WIDTH = 752;
    private static final int GAP_SIZE = 2;
    private static final int KEY_WIDTH = 50;
    private static final int KEY_HEIGHT = 58;
    private static final int SPACE_KEY_WIDTH = 344;
    private static final int BACK_SPACE_KEY_WIDTH = 76;
    private static final int ENTER_KEY_WIDTH = 90;
    private static final int SHIFT_KEY_WIDTH = 112;
    private static final int FIRST_ROW_OFFSET = 676;
    private static final int SECOND_ROW_OFFSET = 78;
    private static final int THIRD_ROW_OFFSET = 90;
    private static final int FOURTH_ROW_OFFSET = 0;
    private static final int FIFTH_ROW_OFFSET = 204;
    private static final Key [] FIRST_ROW = {Key.VK_BACK_SPACE};
    private static final Key [] SECOND_ROW = {Key.VK_Q, Key.VK_W, Key.VK_E, Key.VK_R, Key.VK_T, Key.VK_Y, Key.VK_U, Key.VK_I, Key.VK_O, Key.VK_P};
    private static final Key [] THIRD_ROW = {Key.VK_A, Key.VK_S, Key.VK_D, Key.VK_F, Key.VK_G, Key.VK_H, Key.VK_J, Key.VK_K, Key.VK_L, Key.VK_NULL, Key.VK_NULL, Key.VK_ENTER};
    private static final Key [] FOURTH_ROW = {Key.VK_SHIFT, Key.VK_Z, Key.VK_X, Key.VK_C, Key.VK_V, Key.VK_B, Key.VK_N, Key.VK_M, Key.VK_COMMA, Key.VK_PERIOD};
    private static final Key [] FIFTH_ROW = {Key.VK_SPACE};
    
    TabletAttributes(TabletKeyboard keyboard) {
        this.addAttribute(new KeyboardAttribute(Attribute.KEYBOARD_HEIGHT.toString(), KEYBOARD_HEIGHT));
        this.addAttribute(new KeyboardAttribute(Attribute.KEYBOARD_WIDTH.toString(), KEYBOARD_WIDTH));
        this.addAttribute(new KeyboardAttribute(Attribute.GAP_SIZE.toString(), GAP_SIZE));
        this.addAttribute(new KeyboardAttribute(Attribute.KEY_WIDTH.toString(), KEY_WIDTH));
        this.addAttribute(new KeyboardAttribute(Attribute.KEY_HEIGHT.toString(), KEY_HEIGHT));
        this.addAttribute(new KeyboardAttribute(Attribute.SPACE_KEY_WIDTH.toString(), SPACE_KEY_WIDTH));
        this.addAttribute(new KeyboardAttribute(Attribute.BACK_SPACE_KEY_WIDTH.toString(), BACK_SPACE_KEY_WIDTH));
        this.addAttribute(new KeyboardAttribute(Attribute.ENTER_KEY_WIDTH.toString(), ENTER_KEY_WIDTH));
        this.addAttribute(new KeyboardAttribute(Attribute.SHIFT_KEY_WIDTH.toString(), SHIFT_KEY_WIDTH));
        this.addAttribute(new KeyboardAttribute(Attribute.NUMBER_OF_KEYS.toString(), FIRST_ROW.length + SECOND_ROW.length + THIRD_ROW.length + FOURTH_ROW.length + FIFTH_ROW.length));
        this.addAttribute(new KeyboardAttribute(Attribute.ROW_OFFSETS.toString(), (new int[] {FIRST_ROW_OFFSET,SECOND_ROW_OFFSET,THIRD_ROW_OFFSET,FOURTH_ROW_OFFSET,FIFTH_ROW_OFFSET})));
        this.addAttribute(new KeyboardAttribute(Attribute.KEY_ROWS.toString(), new Key[][] {FIRST_ROW, SECOND_ROW, THIRD_ROW, FOURTH_ROW, FIFTH_ROW}));
    }
}

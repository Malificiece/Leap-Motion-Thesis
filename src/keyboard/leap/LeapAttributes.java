package keyboard.leap;

import com.leapmotion.leap.Vector;

import keyboard.KeyboardAttribute;
import keyboard.KeyboardAttributes;
import enums.Attribute;
import enums.Key;

public class LeapAttributes extends KeyboardAttributes {
    public static final int KEYBOARD_HEIGHT = 385;
    public static final int KEYBOARD_WIDTH = 647;
    private static final int DIST_TO_CAMERA = 465;
    private static final int GAP_SIZE = 3;
    private static final int KEY_WIDTH = 62;
    private static final int KEY_HEIGHT = 94;
    private static final int SPACE_KEY_WIDTH = 192;
    private static final int BACK_SPACE_KEY_WIDTH = 107;
    private static final int ENTER_KEY_WIDTH = 172;
    private static final int SHIFT_KEY_WIDTH = 82;
    private static final int FIRST_ROW_OFFSET = 0;
    private static final int SECOND_ROW_OFFSET = 33;
    private static final int THIRD_ROW_OFFSET = 0;
    private static final int FOURTH_ROW_OFFSET = 150;
    private static final Key [] FIRST_ROW = {Key.VK_Q, Key.VK_W, Key.VK_E, Key.VK_R, Key.VK_T, Key.VK_Y, Key.VK_U, Key.VK_I, Key.VK_O, Key.VK_P};
    private static final Key [] SECOND_ROW = {Key.VK_A, Key.VK_S, Key.VK_D, Key.VK_F, Key.VK_G, Key.VK_H, Key.VK_J, Key.VK_K, Key.VK_L};
    private static final Key [] THIRD_ROW = {Key.VK_SHIFT, Key.VK_Z, Key.VK_X, Key.VK_C, Key.VK_V, Key.VK_B, Key.VK_N, Key.VK_M, Key.VK_BACK_SPACE};
    private static final Key [] FOURTH_ROW = {Key.VK_COMMA, Key.VK_SPACE, Key.VK_PERIOD, Key.VK_ENTER};
    
    LeapAttributes(LeapKeyboard keyboard) {
        this.addAttribute(new KeyboardAttribute(keyboard, Attribute.KEYBOARD_HEIGHT.toString(), KEYBOARD_HEIGHT));
        this.addAttribute(new KeyboardAttribute(keyboard, Attribute.KEYBOARD_WIDTH.toString(), KEYBOARD_WIDTH));
        this.addAttribute(new KeyboardAttribute(keyboard, Attribute.DIST_TO_CAMERA.toString(), DIST_TO_CAMERA));
        this.addAttribute(new KeyboardAttribute(keyboard, Attribute.GAP_SIZE.toString(), GAP_SIZE));
        this.addAttribute(new KeyboardAttribute(keyboard, Attribute.KEY_WIDTH.toString(), KEY_WIDTH));
        this.addAttribute(new KeyboardAttribute(keyboard, Attribute.KEY_HEIGHT.toString(), KEY_HEIGHT));
        this.addAttribute(new KeyboardAttribute(keyboard, Attribute.SPACE_KEY_WIDTH.toString(), SPACE_KEY_WIDTH));
        this.addAttribute(new KeyboardAttribute(keyboard, Attribute.BACK_SPACE_KEY_WIDTH.toString(), BACK_SPACE_KEY_WIDTH));
        this.addAttribute(new KeyboardAttribute(keyboard, Attribute.ENTER_KEY_WIDTH.toString(), ENTER_KEY_WIDTH));
        this.addAttribute(new KeyboardAttribute(keyboard, Attribute.SHIFT_KEY_WIDTH.toString(), SHIFT_KEY_WIDTH));
        this.addAttribute(new KeyboardAttribute(keyboard, Attribute.NUMBER_OF_KEYS.toString(), FIRST_ROW.length + SECOND_ROW.length + THIRD_ROW.length + FOURTH_ROW.length));
        this.addAttribute(new KeyboardAttribute(keyboard, Attribute.ROW_OFFSETS.toString(), (new int[] {FIRST_ROW_OFFSET,SECOND_ROW_OFFSET,THIRD_ROW_OFFSET,FOURTH_ROW_OFFSET})));
        this.addAttribute(new KeyboardAttribute(keyboard, Attribute.KEY_ROWS.toString(), new Key[][] {FIRST_ROW, SECOND_ROW, THIRD_ROW, FOURTH_ROW}));
        this.addAttribute(new KeyboardAttribute(keyboard, Attribute.LEAP_PLANE_POINT_A.toString(), Vector.zero()));
        this.addAttribute(new KeyboardAttribute(keyboard, Attribute.LEAP_PLANE_POINT_B.toString(), Vector.zero()));
        this.addAttribute(new KeyboardAttribute(keyboard, Attribute.LEAP_PLANE_POINT_C.toString(), Vector.zero()));
    }
}

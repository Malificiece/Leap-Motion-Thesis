package keyboard.controller;

import utilities.Point;
import utilities.MyUtilities;
import keyboard.KeyboardAttribute;
import keyboard.KeyboardAttributes;
import keyboard.renderables.KeyboardImage;
import enums.Attribute;
import enums.Key;

public class ControllerAttributes extends KeyboardAttributes {
    private static final Point KEYBOARD_SIZE = new Point(576, 271, KeyboardImage.SCALE); //new Point(808, 340, KeyboardImage.SCALE);
    private static final Float BORDER_SIZE = 1 * KeyboardImage.SCALE;
    private static final Float CAMERA_DISTANCE = MyUtilities.MATH_UTILITILES.findDimensionalAlignment(KEYBOARD_SIZE.y, BORDER_SIZE); // Align 2D and 3D
    private static final Point GAP_SIZE = new Point(4, 5, KeyboardImage.SCALE);
    private static final Point KEY_SIZE = new Point(54, 64, KeyboardImage.SCALE);
    //private static final Point SPACE_KEY_SIZE = new Point(286, 64, KeyboardImage.SCALE);
    private static final Point BACK_SPACE_KEY_SIZE = new Point(286 + 286 + 4, 64, KeyboardImage.SCALE); // REMOVE ME IF WE EVER CHANGE THINGS BACK
    private static final Point ENTER_KEY_SIZE = new Point(0, 0); //new Point(112, 133, KeyboardImage.SCALE);
    //private static final Point SHIFT_KEY_SIZE = new Point(112, 133, KeyboardImage.SCALE);
    private static final float FIRST_ROW_OFFSET = 0 * KeyboardImage.SCALE; //116 * KeyboardImage.SCALE;
    private static final float SECOND_ROW_OFFSET = FIRST_ROW_OFFSET;
    private static final float THIRD_ROW_OFFSET = FIRST_ROW_OFFSET;
    private static final float FOURTH_ROW_OFFSET = FIRST_ROW_OFFSET;
    private static final float FIFTH_ROW_OFFSET = FIRST_ROW_OFFSET; // 0;
    //private static final Key[] FIRST_ROW = {Key.VK_1, Key.VK_2, Key.VK_3, Key.VK_4, Key.VK_5, Key.VK_6, Key.VK_7, Key.VK_8, Key.VK_9, Key.VK_0};
    private static final Key[] SECOND_ROW = {Key.VK_Q, Key.VK_W, Key.VK_E, Key.VK_R, Key.VK_T, Key.VK_Y, Key.VK_U, Key.VK_I, Key.VK_O, Key.VK_P};
    private static final Key[] THIRD_ROW = {Key.VK_A, Key.VK_S, Key.VK_D, Key.VK_F, Key.VK_G, Key.VK_H, Key.VK_J, Key.VK_K, Key.VK_L, Key.VK_BLANK_0};
    private static final Key[] FOURTH_ROW = {Key.VK_Z, Key.VK_X, Key.VK_C, Key.VK_V, Key.VK_B, Key.VK_N, Key.VK_M,
        Key.VK_BLANK_2/*Key.VK_COMMA*/, Key.VK_BLANK_3/*Key.VK_PERIOD*/, Key.VK_BLANK_1};
    private static final Key[] FIFTH_ROW = {/*Key.VK_SHIFT,*/ Key.VK_BACK_SPACE, /*Key.VK_SPACE,*/ Key.VK_ENTER};
    
    ControllerAttributes(ControllerKeyboard keyboard) {
        this.addAttribute(new KeyboardAttribute(keyboard, Attribute.KEYBOARD_SIZE, KEYBOARD_SIZE));
        this.addAttribute(new KeyboardAttribute(keyboard, Attribute.BORDER_SIZE, BORDER_SIZE));
        this.addAttribute(new KeyboardAttribute(keyboard, Attribute.CAMERA_DISTANCE, CAMERA_DISTANCE));
        this.addAttribute(new KeyboardAttribute(keyboard, Attribute.GAP_SIZE, GAP_SIZE));
        this.addAttribute(new KeyboardAttribute(keyboard, Attribute.KEY_SIZE, KEY_SIZE));
        //this.addAttribute(new KeyboardAttribute(keyboard, Attribute.SPACE_KEY_SIZE, SPACE_KEY_SIZE));
        this.addAttribute(new KeyboardAttribute(keyboard, Attribute.BACK_SPACE_KEY_SIZE, BACK_SPACE_KEY_SIZE));
        this.addAttribute(new KeyboardAttribute(keyboard, Attribute.ENTER_KEY_SIZE, ENTER_KEY_SIZE));
        //this.addAttribute(new KeyboardAttribute(keyboard, Attribute.SHIFT_KEY_SIZE, SHIFT_KEY_SIZE));
        //this.addAttribute(new KeyboardAttribute(keyboard, Attribute.NUMBER_OF_KEYS, /*FIRST_ROW.length +*/ SECOND_ROW.length + THIRD_ROW.length + FOURTH_ROW.length + FIFTH_ROW.length));
        this.addAttribute(new KeyboardAttribute(keyboard, Attribute.ROW_OFFSETS, (new Float[] {/*FIRST_ROW_OFFSET,*/SECOND_ROW_OFFSET,THIRD_ROW_OFFSET,FOURTH_ROW_OFFSET,FIFTH_ROW_OFFSET})));
        this.addAttribute(new KeyboardAttribute(keyboard, Attribute.KEY_ROWS, new Key[][] {/*FIRST_ROW,*/ SECOND_ROW, THIRD_ROW, FOURTH_ROW, FIFTH_ROW}));
    }
}

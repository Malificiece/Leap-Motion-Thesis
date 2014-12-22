package keyboard.standard;

import enums.AttributeName;
import keyboard.KeyboardAttribute;
import keyboard.KeyboardAttributes;

public class StandardAttributes extends KeyboardAttributes {
    private static final int HEIGHT = 298;
    private static final int WIDTH = 752;
    private static final int GAP_SIZE = 2;
    private static final int KEY_WIDTH = 50;
    private static final int KEY_HEIGHT = 58;
    private static final int SPACE_KEY_WIDTH = 344;
    private static final int FIRST_ROW_OFFSET = 0;
    private static final int SECOND_ROW_OFFSET = 78;
    private static final int THIRD_ROW_OFFSET = 90;
    private static final int FOURTH_ROW_OFFSET = 114;
    private static final int FIFTH_ROW_OFFSET = 204;
    private static final char [] FIRST_ROW = {};
    private static final char [] SECOND_ROW = {'q', 'w', 'e', 'r', 't', 'y', 'u', 'i', 'o', 'p'};
    private static final char [] THIRD_ROW = {'a', 's', 'd', 'f', 'g', 'h', 'j', 'k', 'l'};
    private static final char [] FOURTH_ROW = {'z', 'x', 'c', 'v', 'b', 'n', 'm', ',', '.'};
    private static final char [] FIFTH_ROW = {' '};
    
    StandardAttributes(StandardKeyboard keyboard) {
        this.addAttribute(new KeyboardAttribute(AttributeName.KEYBOARD_HEIGHT.toString(), HEIGHT));
        this.addAttribute(new KeyboardAttribute(AttributeName.KEYBOARD_WIDTH.toString(), WIDTH));
        this.addAttribute(new KeyboardAttribute(AttributeName.GAP_SIZE.toString(), GAP_SIZE));
        this.addAttribute(new KeyboardAttribute(AttributeName.KEY_WIDTH.toString(), KEY_WIDTH));
        this.addAttribute(new KeyboardAttribute(AttributeName.KEY_HEIGHT.toString(), KEY_HEIGHT));
        this.addAttribute(new KeyboardAttribute(AttributeName.SPACE_KEY_WIDTH.toString(), SPACE_KEY_WIDTH));
        this.addAttribute(new KeyboardAttribute(AttributeName.NUMBER_OF_KEYS.toString(), FIRST_ROW.length + SECOND_ROW.length + THIRD_ROW.length + FOURTH_ROW.length + FIFTH_ROW.length));
        this.addAttribute(new KeyboardAttribute(AttributeName.ROW_OFFSETS.toString(), (new int[] {FIRST_ROW_OFFSET,SECOND_ROW_OFFSET,THIRD_ROW_OFFSET,FOURTH_ROW_OFFSET,FIFTH_ROW_OFFSET})));
        this.addAttribute(new KeyboardAttribute(AttributeName.KEY_ROWS.toString(), new char[][] {FIRST_ROW, SECOND_ROW, THIRD_ROW, FOURTH_ROW, FIFTH_ROW}));
    }
}

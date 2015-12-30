/*
 * Copyright (c) 2015, Garrett Benoit. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */ 

package keyboard.tablet;

import utilities.MyUtilities;
import utilities.Point;
import keyboard.KeyboardAttribute;
import keyboard.KeyboardAttributes;
import keyboard.renderables.KeyboardImage;
import enums.Attribute;
import enums.Key;

public class TabletAttributes extends KeyboardAttributes {
    private static final Point KEYBOARD_SIZE = new Point(952, 212, KeyboardImage.SCALE); // new Point(730, 360, KeyboardImage.SCALE);
    private static final Float BORDER_SIZE = 74 * KeyboardImage.SCALE;
    private static final Float CAMERA_DISTANCE = MyUtilities.MATH_UTILITILES.findDimensionalAlignment(KEYBOARD_SIZE.y, BORDER_SIZE); // Align 2D with 3D
    private static final Point GAP_SIZE = new Point(10, 10, KeyboardImage.SCALE);
    private static final Point KEY_SIZE = new Point(64, 64, KeyboardImage.SCALE);
    //private static final Point SPACE_KEY_SIZE = new Point(286, 64, KeyboardImage.SCALE);
    private static final Point BACK_SPACE_KEY_SIZE = new Point(101, 138 /*64*/, KeyboardImage.SCALE);
    private static final Point ENTER_KEY_SIZE = new Point(0, 0, KeyboardImage.SCALE); //new Point(143, 64, KeyboardImage.SCALE);
    //private static final Point SHIFT_KEY_SIZE = new Point(101, 64, KeyboardImage.SCALE);
    //private static final float FIRST_ROW_OFFSET = 0 * KeyboardImage.SCALE;
    private static final float SECOND_ROW_OFFSET = 0 * KeyboardImage.SCALE;
    private static final float THIRD_ROW_OFFSET = 37 * KeyboardImage.SCALE;
    private static final float FOURTH_ROW_OFFSET = (0f + 101 + 10) * KeyboardImage.SCALE; // REMOVE ME IF WE ADD BACK.
    //private static final float FIFTH_ROW_OFFSET = 10000; //143;
    //private static final Key[] FIRST_ROW = {/*Key.VK_1, Key.VK_2, Key.VK_3, Key.VK_4, Key.VK_5, Key.VK_6, Key.VK_7, Key.VK_8, Key.VK_9, Key.VK_0*/};
    private static final Key[] SECOND_ROW = {Key.VK_Q, Key.VK_W, Key.VK_E, Key.VK_R, Key.VK_T, Key.VK_Y, Key.VK_U, Key.VK_I, Key.VK_O, Key.VK_P};
    private static final Key[] THIRD_ROW = {Key.VK_A, Key.VK_S, Key.VK_D, Key.VK_F, Key.VK_G, Key.VK_H, Key.VK_J, Key.VK_K, Key.VK_L, Key.VK_NULL, Key.VK_NULL, Key.VK_BACK_SPACE};
    private static final Key[] FOURTH_ROW = {/*Key.VK_SHIFT,*/ Key.VK_Z, Key.VK_X, Key.VK_C, Key.VK_V, Key.VK_B, Key.VK_N, Key.VK_M, Key.VK_ENTER};
    //private static final Key[] FIFTH_ROW = {/*Key.VK_COMMA, Key.VK_SPACE, Key.VK_PERIOD, Key.VK_ENTER*/};
    
    TabletAttributes(TabletKeyboard keyboard) {
        this.addAttribute(new KeyboardAttribute(keyboard, Attribute.KEYBOARD_SIZE, KEYBOARD_SIZE));
        this.addAttribute(new KeyboardAttribute(keyboard, Attribute.BORDER_SIZE, BORDER_SIZE));
        this.addAttribute(new KeyboardAttribute(keyboard, Attribute.CAMERA_DISTANCE, CAMERA_DISTANCE));
        this.addAttribute(new KeyboardAttribute(keyboard, Attribute.GAP_SIZE, GAP_SIZE));
        this.addAttribute(new KeyboardAttribute(keyboard, Attribute.KEY_SIZE, KEY_SIZE));
        //this.addAttribute(new KeyboardAttribute(keyboard, Attribute.SPACE_KEY_SIZE, SPACE_KEY_SIZE));
        this.addAttribute(new KeyboardAttribute(keyboard, Attribute.BACK_SPACE_KEY_SIZE, BACK_SPACE_KEY_SIZE));
        this.addAttribute(new KeyboardAttribute(keyboard, Attribute.ENTER_KEY_SIZE, ENTER_KEY_SIZE));
        //this.addAttribute(new KeyboardAttribute(keyboard, Attribute.SHIFT_KEY_SIZE, SHIFT_KEY_SIZE));
        this.addAttribute(new KeyboardAttribute(keyboard, Attribute.NUMBER_OF_KEYS, /*FIRST_ROW.length +*/ SECOND_ROW.length + THIRD_ROW.length + FOURTH_ROW.length));
        this.addAttribute(new KeyboardAttribute(keyboard, Attribute.ROW_OFFSETS, (new Float[] {/*FIRST_ROW_OFFSET,*/SECOND_ROW_OFFSET,THIRD_ROW_OFFSET,FOURTH_ROW_OFFSET/*,FIFTH_ROW_OFFSET*/})));
        this.addAttribute(new KeyboardAttribute(keyboard, Attribute.KEY_ROWS, new Key[][] {/*FIRST_ROW,*/ SECOND_ROW, THIRD_ROW, FOURTH_ROW/*, FIFTH_ROW*/}));
    }
}

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

package enums;

import keyboard.IKeyboard;
import keyboard.controller.ControllerKeyboard;
import keyboard.leap.LeapKeyboard;
import keyboard.standard.StandardKeyboard;
import keyboard.tablet.TabletKeyboard;

public enum Keyboard {
    STANDARD(KeyboardType.DISABLED), // not needed for pilot or full study
    CONTROLLER_CONSOLE(KeyboardType.DISABLED/*KeyboardType.CONTROLLER_CONSOLE*/), // TODO: set to disabled for full study
    CONTROLLER_GESTURE(KeyboardType.DISABLED), // never implemented
    TABLET(KeyboardType.TABLET),
    LEAP_SURFACE(KeyboardType.LEAP_SURFACE),
    LEAP_AIR_STATIC(KeyboardType.LEAP_AIR_STATIC),
    LEAP_AIR_PINCH(KeyboardType.LEAP_AIR_PINCH),
    LEAP_AIR_DYNAMIC(KeyboardType.LEAP_AIR_DYNAMIC),
    LEAP_AIR_BIMODAL(KeyboardType.LEAP_AIR_BIMODAL),
    LEAP_AIR_AUGMENTED(KeyboardType.DISABLED); // never implemented

    private final KeyboardType keyboardType;
    private final String keyboardName;
    private final IKeyboard keyboard;
    
    private Keyboard(KeyboardType keyboardType) {
        switch(keyboardType) {
            case CONTROLLER_CONSOLE:
                this.keyboard = new ControllerKeyboard(keyboardType);
                break;
            case CONTROLLER_GESTURE:
                this.keyboard = new ControllerKeyboard(keyboardType);
                break;
            case DISABLED:
                this.keyboard = null;
                break;
            case LEAP_AIR_AUGMENTED:
                this.keyboard = new LeapKeyboard(keyboardType);
                break;
            case LEAP_AIR_BIMODAL:
                this.keyboard = new LeapKeyboard(keyboardType);
                break;
            case LEAP_AIR_DYNAMIC:
                this.keyboard = new LeapKeyboard(keyboardType);
                break;
            case LEAP_AIR_PINCH:
                this.keyboard = new LeapKeyboard(keyboardType);
                break;
            case LEAP_AIR_STATIC:
                this.keyboard = new LeapKeyboard(keyboardType);
                break;
            case LEAP_SURFACE:
                this.keyboard = new LeapKeyboard(keyboardType);
                break;
            case STANDARD:
                this.keyboard = new StandardKeyboard(keyboardType);
                break;
            case TABLET:
                this.keyboard = new TabletKeyboard(keyboardType);
                break;
            default:
                this.keyboard = null;
                break;
        }
        if(this.keyboard != null) {
            this.keyboardType = keyboardType;
            this.keyboardName = keyboardType.getName();
        } else {
            this.keyboardType = KeyboardType.DISABLED;
            this.keyboardName = KeyboardType.DISABLED.getName();
        }
    }
    
    public KeyboardType getType() {
        return keyboardType;
    }
    
    public String getName() {
        return keyboardName;
    }
    
    public IKeyboard getKeyboard() {
        return keyboard;
    }
    
    public static Keyboard getByType(KeyboardType keyboardType) {
        for(Keyboard keyboard: values()) {
            if(keyboard.getType() == keyboardType) return keyboard;
        }
        return null;
    }
    
    public static Keyboard getByName(String keyboardName) {
        for(Keyboard keyboard: values()) {
            if(keyboard.getName().equalsIgnoreCase(keyboardName)) return keyboard;
        }
        return null;
    }
}

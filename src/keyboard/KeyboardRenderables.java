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

package keyboard;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import javax.media.opengl.GL2;

import enums.Renderable;

public abstract class KeyboardRenderables {
    private LinkedHashMap<Renderable, KeyboardRenderable> keyboardRenderables = new LinkedHashMap<Renderable, KeyboardRenderable>();
    
    public void addRenderable(KeyboardRenderable renderable) {
        keyboardRenderables.put(renderable.getType(), renderable);
    }
    
    public KeyboardRenderable getRenderable(Renderable renderable) {
        return keyboardRenderables.get(renderable);
    }

    public ArrayList<KeyboardRenderable> getAllRenderables() {
        return new ArrayList<KeyboardRenderable>(keyboardRenderables.values());
    }
    
    public void swapToLowerCaseKeyboard() {
        KeyboardRenderable krUpper = keyboardRenderables.get(Renderable.KEYBOARD_IMAGE_UPPER);
        if(krUpper != null && krUpper.isEnabled()) {
            KeyboardRenderable krLower = keyboardRenderables.get(Renderable.KEYBOARD_IMAGE);
            if(krLower != null) {
                krUpper.blockAccess(true);
                krLower.grantAccess(true);
            }
        }
    }
    
    public void swapToUpperCaseKeyboard() {
        KeyboardRenderable krLower = keyboardRenderables.get(Renderable.KEYBOARD_IMAGE);
        if(krLower != null && krLower.isEnabled()) {
            KeyboardRenderable krUpper = keyboardRenderables.get(Renderable.KEYBOARD_IMAGE_UPPER);
            if(krUpper != null) {
                krLower.blockAccess(true);
                krUpper.grantAccess(true);
            }
        }
    }
    
    public abstract void render(GL2 gl);
}

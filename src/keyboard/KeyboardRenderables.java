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

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
    
    public abstract void render(GL2 gl);
}

package keyboard;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import javax.media.opengl.GL2;

public abstract class KeyboardRenderables {
    private LinkedHashMap<String, KeyboardRenderable> keyboardRenderables = new LinkedHashMap<String, KeyboardRenderable>();
    
    public void addRenderable(KeyboardRenderable renderable) {
        keyboardRenderables.put(renderable.getName(), renderable);
    }
    
    public KeyboardRenderable getRenderableByName(String name) {
        return keyboardRenderables.get(name);
    }

    public ArrayList<KeyboardRenderable> getAllRenderables() {
        return new ArrayList<KeyboardRenderable>(keyboardRenderables.values());
    }
    
    public abstract void render(GL2 gl);
}

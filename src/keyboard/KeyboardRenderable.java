package keyboard;

import javax.media.opengl.GL2;

public abstract class KeyboardRenderable {
    private String name;
    private Boolean enabled = true;
    
    public KeyboardRenderable(String name) {
        this.name = name;
    }
    
    public void enable() {
        enabled = true;
    }
    
    public void disable() {
        enabled = false;
    }
    
    public Boolean isEnabled() {
        return enabled;
    }
    
    public String getName() {
        return name;
    }
    
    public abstract void render(GL2 gl);
}

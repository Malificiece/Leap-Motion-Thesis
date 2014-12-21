package keyboard.leap;

import javax.media.opengl.GL2;

import keyboard.KeyboardRenderable;
import keyboard.KeyboardRenderables;
import keyboard.renderables.KeyboardImage;
import keyboard.renderables.VirtualKeyboard;

public class LeapRenderables extends KeyboardRenderables {
    //private KeyboardImage keyboardImage;
    //private VirtualKeyboard virtualKeyboard;
    //private LeapPlane leapPlane;
    //private LeapObject leapObject;
    //private LeapTool leapTool;

    LeapRenderables(LeapKeyboard keyboard) {
        // order here determines render order
        this.addRenderable(new KeyboardImage("keyboard.png", "leap/"));
        this.addRenderable(new VirtualKeyboard(keyboard.getAttributes()));
    }

    @Override
    public void render(GL2 gl) {
        for(KeyboardRenderable renderable: this.getAllRenderables()) {
            renderable.render(gl);
        }
    }
}

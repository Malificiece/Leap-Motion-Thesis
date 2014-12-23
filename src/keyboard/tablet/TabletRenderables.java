package keyboard.tablet;

import javax.media.opengl.GL2;

import keyboard.KeyboardRenderable;
import keyboard.KeyboardRenderables;
import keyboard.renderables.KeyboardImage;
import keyboard.renderables.VirtualKeyboard;

public class TabletRenderables extends KeyboardRenderables {
    //private KeyboardImage keyboardImage;
    //private VirtualKeyboard virtualKeyboard;

    TabletRenderables(TabletKeyboard keyboard) {
        // order here determines render order
        this.addRenderable(new KeyboardImage("keyboard.png", keyboard.getKeyboardFilePath()));
        this.addRenderable(new VirtualKeyboard(keyboard.getAttributes()));
    }

    @Override
    public void render(GL2 gl) {
        for(KeyboardRenderable renderable: this.getAllRenderables()) {
            renderable.render(gl);
        }
    }
}

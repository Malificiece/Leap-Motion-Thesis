package keyboard.standard;

import javax.media.opengl.GL2;

import enums.FileExt;
import enums.FileName;
import enums.Gesture;
import keyboard.KeyboardRenderable;
import keyboard.KeyboardRenderables;
import keyboard.renderables.KeyboardGestures;
import keyboard.renderables.KeyboardImage;
import keyboard.renderables.VirtualKeyboard;

public class StandardRenderables extends KeyboardRenderables {
    //private KeyboardImage keyboardImage;
    //private VirtualKeyboard virtualKeyboard;

    StandardRenderables(StandardKeyboard keyboard) {
        // order here determines render order
        this.addRenderable(new KeyboardImage(keyboard.getFileName() + FileName.KEYBOARD_IMAGE_UPPER.getName() + FileExt.PNG.getExt()));
        this.addRenderable(new VirtualKeyboard(keyboard.getAttributes()));
        if(Gesture.ENABLED) {
            this.addRenderable(new KeyboardGestures(keyboard.getAttributes()));
        }
    }

    @Override
    public void render(GL2 gl) {
        for(KeyboardRenderable renderable: this.getAllRenderables()) {
            renderable.render(gl);
        }
    }
}

package keyboard.controller;

import javax.media.opengl.GL2;

import enums.FileExt;
import enums.FileName;
import keyboard.KeyboardRenderable;
import keyboard.KeyboardRenderables;
import keyboard.renderables.KeyboardImage;
import keyboard.renderables.VirtualKeyboard;

public class ControllerRenderables extends KeyboardRenderables {
    //private KeyboardImage keyboardImage;
    //private VirtualKeyboard virtualKeyboard;

    ControllerRenderables(ControllerKeyboard keyboard) {
        // order here determines render order
        this.addRenderable(new KeyboardImage(keyboard.getFileName() + FileName.KEYBOARD_IMAGE.getName() + FileExt.PNG.getExt()));
        this.addRenderable(new KeyboardImage(keyboard.getFileName() + FileName.KEYBOARD_IMAGE_UPPER.getName() + FileExt.PNG.getExt()));
        this.addRenderable(new VirtualKeyboard(keyboard.getAttributes()));
    }

    @Override
    public void render(GL2 gl) {
        for(KeyboardRenderable renderable: this.getAllRenderables()) {
            renderable.render(gl);
        }
    }
}

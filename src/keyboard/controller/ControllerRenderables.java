package keyboard.controller;

import javax.media.opengl.GL2;

import enums.FileExtension;
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
        this.addRenderable(new KeyboardImage(FileName.KEYBOARD_DEFAULT_IMAGE_NAME.getName() + FileExtension.PNG.getExtension(), keyboard.getKeyboardFilePath()));
        this.addRenderable(new VirtualKeyboard(keyboard.getAttributes()));
    }

    @Override
    public void render(GL2 gl) {
        for(KeyboardRenderable renderable: this.getAllRenderables()) {
            renderable.render(gl);
        }
    }
}

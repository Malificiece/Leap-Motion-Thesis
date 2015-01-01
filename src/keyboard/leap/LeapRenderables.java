package keyboard.leap;

import javax.media.opengl.GL2;

import enums.FileExtension;
import enums.FileName;
import keyboard.KeyboardRenderable;
import keyboard.KeyboardRenderables;
import keyboard.renderables.KeyboardImage;
import keyboard.renderables.LeapGesture;
import keyboard.renderables.LeapPlane;
import keyboard.renderables.LeapPoint;
import keyboard.renderables.LeapTool;
import keyboard.renderables.VirtualKeyboard;

public class LeapRenderables extends KeyboardRenderables {
    //private KeyboardImage keyboardImage;
    //private VirtualKeyboard virtualKeyboard;
    //private LeapPlane leapPlane;
    //private LeapGestures leapGestures;
    //private LeapTool leapTool;
    //private LeapPoint leapPoint;

    LeapRenderables(LeapKeyboard keyboard) {
        // order here determines render order
        this.addRenderable(new LeapPlane(keyboard));
        this.addRenderable(new KeyboardImage(FileName.KEYBOARD_DEFAULT_IMAGE_NAME.getName() + FileExtension.PNG.getExtension(), keyboard.getKeyboardFilePath()));
        this.addRenderable(new VirtualKeyboard(keyboard.getAttributes()));
        this.addRenderable(new LeapGesture(keyboard.getAttributes()));
        this.addRenderable(new LeapPoint(keyboard.getAttributes()));
        this.addRenderable(new LeapTool(keyboard.getAttributes()));
    }

    @Override
    public void render(GL2 gl) {
        for(KeyboardRenderable renderable: this.getAllRenderables()) {
            renderable.render(gl);
        }
    }
}

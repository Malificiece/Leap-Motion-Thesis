package keyboard.leap;

import javax.media.opengl.GL2;

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
    //private LeapGesture leapGesture;
    //private LeapTool leapTool;
    //private LeapPoint leapPoint;

    LeapRenderables(LeapKeyboard keyboard) {
        // order here determines render order
        this.addRenderable(new LeapPlane());
        this.addRenderable(new KeyboardImage("keyboard.png", keyboard.getKeyboardFilePath()));
        this.addRenderable(new VirtualKeyboard(keyboard.getAttributes()));
        this.addRenderable(new LeapGesture());
        this.addRenderable(new LeapPoint());
        this.addRenderable(new LeapTool());
    }

    @Override
    public void render(GL2 gl) {
        for(KeyboardRenderable renderable: this.getAllRenderables()) {
            renderable.render(gl);
        }
    }
}

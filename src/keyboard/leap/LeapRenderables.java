package keyboard.leap;

import javax.media.opengl.GL2;

import enums.FileExt;
import enums.FileName;
import enums.Gesture;
import keyboard.KeyboardRenderable;
import keyboard.KeyboardRenderables;
import keyboard.renderables.KeyboardImage;
import keyboard.renderables.KeyboardGestures;
import keyboard.renderables.LeapPlane;
import keyboard.renderables.SwipePoint;
import keyboard.renderables.LeapTool;
import keyboard.renderables.SwipeTrail;
import keyboard.renderables.VirtualKeyboard;

public class LeapRenderables extends KeyboardRenderables {

    LeapRenderables(LeapKeyboard keyboard) {
        // order here determines render order
        this.addRenderable(new LeapPlane(keyboard));
        this.addRenderable(new KeyboardImage(FileName.SWIPE.getName() + FileName.KEYBOARD_IMAGE.getName() + FileExt.PNG.getExt()));
        this.addRenderable(new KeyboardImage(FileName.SWIPE.getName() + FileName.KEYBOARD_IMAGE_UPPER.getName() + FileExt.PNG.getExt()));
        this.swapToLowerCaseKeyboard();
        this.addRenderable(new VirtualKeyboard(keyboard.getAttributes()));
        if(Gesture.ENABLED) {
            this.addRenderable(new KeyboardGestures(keyboard.getAttributes()));
        }
        this.addRenderable(new SwipePoint(keyboard.getAttributes()));
        this.addRenderable(new LeapTool(keyboard.getAttributes()));
        this.addRenderable(new SwipeTrail(keyboard.getAttributes()));
    }

    @Override
    public void render(GL2 gl) {
        for(KeyboardRenderable renderable: this.getAllRenderables()) {
            renderable.render(gl);
        }
    }
}

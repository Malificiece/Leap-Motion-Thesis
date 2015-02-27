package keyboard.tablet;

import javax.media.opengl.GL2;

import enums.FileExt;
import enums.FileName;
import enums.Gesture;
import keyboard.KeyboardRenderable;
import keyboard.KeyboardRenderables;
import keyboard.renderables.KeyboardGestures;
import keyboard.renderables.KeyboardImage;
import keyboard.renderables.SwipePoint;
import keyboard.renderables.SwipeTrail;
import keyboard.renderables.VirtualKeyboard;

public class TabletRenderables extends KeyboardRenderables {
    //private KeyboardImage keyboardImage;
    //private VirtualKeyboard virtualKeyboard;

    TabletRenderables(TabletKeyboard keyboard) {
        // order here determines render order
        this.addRenderable(new KeyboardImage(FileName.SWIPE.getName() + FileName.KEYBOARD_IMAGE.getName() + FileExt.PNG.getExt()));
        this.addRenderable(new KeyboardImage(FileName.SWIPE.getName() + FileName.KEYBOARD_IMAGE_UPPER.getName() + FileExt.PNG.getExt()));
        this.swapToLowerCaseKeyboard();
        this.addRenderable(new VirtualKeyboard(keyboard.getAttributes()));
        if(Gesture.ENABLED) {
            this.addRenderable(new KeyboardGestures(keyboard.getAttributes()));
        }
        this.addRenderable(new SwipePoint(keyboard.getAttributes()));
        this.addRenderable(new SwipeTrail(keyboard.getAttributes()));
    }

    @Override
    public void render(GL2 gl) {
        for(KeyboardRenderable renderable: this.getAllRenderables()) {
            renderable.render(gl);
        }
    }
}

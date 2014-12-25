package keyboard.renderables;

import javax.media.opengl.GL2;

import com.leapmotion.leap.GestureList;
import com.leapmotion.leap.InteractionBox;

import enums.AttributeName;
import enums.RenderableName;
import keyboard.KeyboardAttributes;
import keyboard.KeyboardRenderable;

public class LeapGestures extends KeyboardRenderable {
    private static final String RENDER_NAME = RenderableName.LEAP_GESTURES.toString();
    private final int KEYBOARD_WIDTH;
    private final int KEYBOARD_HEIGHT;
    private GestureList gestures = new GestureList();
    private InteractionBox iBox;

    public LeapGestures(KeyboardAttributes keyboardAttributes) {
        super(RENDER_NAME);
        KEYBOARD_WIDTH = keyboardAttributes.getAttributeByName(AttributeName.KEYBOARD_WIDTH.toString()).getValueAsInteger();
        KEYBOARD_HEIGHT = keyboardAttributes.getAttributeByName(AttributeName.KEYBOARD_HEIGHT.toString()).getValueAsInteger();
    }
    
    public void setGestures(GestureList gestures) {
        this.gestures = gestures;
    }
    
    public GestureList getGestures() {
        return gestures;
    }
    
    public void setInteractionBox(InteractionBox iBox) {
        this.iBox = iBox;
    }
    
    public boolean isEmpty() {
        return gestures.isEmpty();
    }

    @Override
    public void render(GL2 gl) {
        if(isEnabled()) {
            // TODO Auto-generated method stub
        }
    }
}

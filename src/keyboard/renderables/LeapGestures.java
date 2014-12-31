package keyboard.renderables;

import javax.media.opengl.GL2;

import com.leapmotion.leap.InteractionBox;

import enums.Attribute;
import enums.Renderable;
import keyboard.KeyboardAttributes;
import keyboard.KeyboardRenderable;

public class LeapGestures extends KeyboardRenderable {
    private static final String RENDER_NAME = Renderable.LEAP_GESTURES.toString();
    private final int KEYBOARD_WIDTH;
    private final int KEYBOARD_HEIGHT;
    private InteractionBox iBox;
    //private ArrayList<Gesture> gestures;

    public LeapGestures(KeyboardAttributes keyboardAttributes) {
        super(RENDER_NAME);
        KEYBOARD_WIDTH = keyboardAttributes.getAttributeByName(Attribute.KEYBOARD_WIDTH.toString()).getValueAsInteger();
        KEYBOARD_HEIGHT = keyboardAttributes.getAttributeByName(Attribute.KEYBOARD_HEIGHT.toString()).getValueAsInteger();
    }
    
    public void setInteractionBox(InteractionBox iBox) {
        this.iBox = iBox;
    }
    
    /*public void addGesture(Gesture gesture) {
        gestures.add(gesture);
    }
    
    public ArrayList<Gesture> getGestures() {
        return gestures;
    }
    
    public void removeGesture(Gesture gesture) {
        gestures.remove(gesture);
    }
    
    public boolean isEmpty() {
        return gestures.isEmpty();
    }*/

    @Override
    public void render(GL2 gl) {
        if(isEnabled()) {
            
        }
    }
}

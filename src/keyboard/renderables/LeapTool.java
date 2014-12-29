package keyboard.renderables;

import javax.media.opengl.GL2;

import com.leapmotion.leap.InteractionBox;
import com.leapmotion.leap.Tool;

import enums.AttributeName;
import enums.RenderableName;
import keyboard.KeyboardAttributes;
import keyboard.KeyboardRenderable;

public class LeapTool extends KeyboardRenderable {
    private static final String RENDER_NAME = RenderableName.LEAP_TOOL.toString();
    private final int KEYBOARD_WIDTH;
    private final int KEYBOARD_HEIGHT;
    private Tool tool = new Tool();
    private InteractionBox iBox;
    
    public LeapTool(KeyboardAttributes keyboardAttributes) {
        super(RENDER_NAME);
        KEYBOARD_WIDTH = keyboardAttributes.getAttributeByName(AttributeName.KEYBOARD_WIDTH.toString()).getValueAsInteger();
        KEYBOARD_HEIGHT = keyboardAttributes.getAttributeByName(AttributeName.KEYBOARD_HEIGHT.toString()).getValueAsInteger();
    }
    
    public void setTool(Tool tool) {
        this.tool = tool;
    }
    
    public Tool getTool() {
        return tool;
    }
    
    public void setInteractionBox(InteractionBox iBox) {
        this.iBox = iBox;
    }
    
    public boolean isValid() {
        return tool.isValid();
    }
    
    public void normalize() {
        
    }

    @Override
    public void render(GL2 gl) {
        if(isEnabled()) {
            // TODO Auto-generated method stub
        }
    }
}
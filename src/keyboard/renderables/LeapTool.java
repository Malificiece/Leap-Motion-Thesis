package keyboard.renderables;

import javax.media.opengl.GL2;

import enums.RenderableName;
import keyboard.KeyboardRenderable;
import leap.LeapToolData;

public class LeapTool extends KeyboardRenderable {
    private static final String RENDER_NAME = RenderableName.LEAP_TOOL.toString();
    private LeapToolData leapToolData;
    
    public LeapTool() {
        super(RENDER_NAME);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void render(GL2 gl) {
        // TODO Auto-generated method stub
    }
}

package keyboard.renderables;

import javax.media.opengl.GL2;

import enums.RenderableName;
import keyboard.KeyboardRenderable;
import leap.LeapPointData;

public class LeapPoint extends KeyboardRenderable {
    private static final String RENDER_NAME = RenderableName.LEAP_POINT.toString();
    private LeapPointData leapPointData;
    
    public LeapPoint() {
        super(RENDER_NAME);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void render(GL2 gl) {
        // TODO Auto-generated method stub
    }
}

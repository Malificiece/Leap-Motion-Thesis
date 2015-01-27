package leap;

import keyboard.renderables.SwipePoint;
import keyboard.renderables.LeapTool;

import com.leapmotion.leap.Tool;


public class LeapData {
    private Tool toolData;
    
    public LeapData() {
        // On creation, be safe and create non-null invalid data
        toolData = new Tool();
    }

    public void setToolData(Tool toolData) {
        this.toolData = toolData;
    }
    
    public Tool getToolData() {
        return toolData;
    }
    
    public void populateData(SwipePoint leapPoint, LeapTool leapTool) {
        populateData(leapPoint);
        populateData(leapTool);
    }
    
    public void populateData(LeapTool leapTool) {
        leapTool.setTool(toolData);
    }
    
    public void populateData(SwipePoint leapPoint) {
        leapPoint.setPoint(toolData.stabilizedTipPosition());
    }
}

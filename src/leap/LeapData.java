package leap;

import keyboard.renderables.LeapPoint;
import keyboard.renderables.LeapTool;

import com.leapmotion.leap.Tool;


public class LeapData {
    private Long timeStamp;
    private Tool toolData;
    
    public LeapData() {
        // On creation, be safe and create non-null invalid data
        timeStamp = -1l;
        toolData = new Tool();
    }
    
    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setToolData(Tool toolData) {
        this.toolData = toolData;
    }
    
    public long getTimeStamp() {
        return timeStamp;
    }
    
    public Tool getToolData() {
        return toolData;
    }
    
    public void populateData(LeapPoint leapPoint, LeapTool leapTool) {
        populateData(leapPoint);
        populateData(leapTool);
    }
    
    public void populateData(LeapTool leapTool) {
        leapTool.setTool(toolData);
    }
    
    public void populateData(LeapPoint leapPoint) {
        leapPoint.setPoint(toolData.stabilizedTipPosition());
    }
}

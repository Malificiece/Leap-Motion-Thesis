package leap;

import keyboard.renderables.LeapGestures;
import keyboard.renderables.LeapPoint;
import keyboard.renderables.LeapTool;

import com.leapmotion.leap.GestureList;
import com.leapmotion.leap.Tool;
import com.leapmotion.leap.Vector;

public class LeapData {
    private Long timeStamp;
    private Vector pointData;
    private Tool toolData;
    private GestureList gesturesData;
    
    public LeapData() {
        // On creation, be safe and create non-null invalid data
        timeStamp = -1l;
        pointData = new Vector();
        toolData = new Tool();
        gesturesData = new GestureList();
    }
    
    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
    
    public void setPointData(Vector pointData) {
        this.pointData = pointData;
    }

    public void setToolData(Tool toolData) {
        this.toolData = toolData;
    }
    
    public void setGestureData(GestureList gesturesData) {
        this.gesturesData = gesturesData;
    }
    
    public long getTimeStamp() {
        return timeStamp;
    }
    
    public Vector getPointData() {
        return pointData;
    }
    
    public Tool getToolData() {
        return toolData;
    }
    
    public GestureList getGestureData() {
        return gesturesData;
    }
    
    public void populateData(LeapPoint leapPoint, LeapTool leapTool, LeapGestures leapGestures) {
        populateData(leapPoint);
        populateData(leapTool);
        populateData(leapGestures);
    }
    
    public void populateData(LeapTool leapTool) {
        leapTool.setTool(toolData);
    }
    
    public void populateData(LeapGestures leapGestures) {
        leapGestures.setGestures(gesturesData);
    }
    
    public void populateData(LeapPoint leapPoint) {
        leapPoint.setPoint(pointData);
    }
}

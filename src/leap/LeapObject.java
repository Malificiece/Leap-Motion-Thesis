package leap;

public class LeapObject {
    private LeapPointData leapPointData;
    private LeapPlaneData leapPlaneData;
    private LeapToolData leapToolData;
    private LeapGestureData leapGestureData;
    
    public LeapObject(LeapPointData leapPointData, LeapPlaneData leapPlaneData, LeapToolData leapToolData, LeapGestureData leapGestureData) {
        this.leapPointData = leapPointData;
        this.leapPlaneData = leapPlaneData;
        this.leapToolData = leapToolData;
        this.leapGestureData = leapGestureData;
    }
    
    public void setLeapPointData(LeapPointData leapPointData) {
        this.leapPointData = leapPointData;
    }
    
    public void setLeapPlaneData(LeapPlaneData leapPlaneData) {
        this.leapPlaneData = leapPlaneData;
    }
    
    public void setLeapToolData(LeapToolData leapToolData) {
        this.leapToolData = leapToolData;
    }
    
    public void setLeapGestureData(LeapGestureData leapGestureData) {
        this.leapGestureData = leapGestureData;
    }
    
    public LeapPointData getLeapPointData() {
        return leapPointData;
    }
    
    public LeapPlaneData getLeapPlaneData() {
        return leapPlaneData;
    }
    
    public LeapToolData getLeapToolData() {
        return leapToolData;
    }
    
    public LeapGestureData getLeapGestureData() {
        return leapGestureData;
    }
}

/*
 * Copyright (c) 2015, Garrett Benoit. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */ 

package leap;

import keyboard.renderables.SwipePoint;
import keyboard.renderables.LeapTool;

import com.leapmotion.leap.Hand;
import com.leapmotion.leap.Tool;
import com.leapmotion.leap.Vector;

public class LeapData {
    private Tool toolData;
    private Hand handData;
    public int frameCount = 0;
    
    public LeapData() {
        // On creation, be safe and create non-null invalid data
        toolData = new Tool();
    }

    public void setToolData(Tool toolData) {
        this.toolData = toolData;
    }
    
    public void setHandData(Hand handData) {
        this.handData = handData;
    }
    
    public Tool getToolData() {
        return toolData;
    }
    
    public Hand getHandData() {
        return handData;
    }
    
    public void populateToolData(SwipePoint leapPoint, LeapTool leapTool) {
        populatePointData(leapPoint);
        populateToolData(leapTool);
    }
    
    public void populateHandData(SwipePoint leapPoint) {
        if(leapPoint != null) {
            if(handData.isValid()) {
                leapPoint.setPoint(handData.stabilizedPalmPosition());
            } else {
                leapPoint.setPoint(Vector.zero());
            }
        }
    }
    
    private void populateToolData(LeapTool leapTool) {
        if(leapTool != null) {
            if(!toolData.isValid() && handData.isValid()) {
                leapTool.setTool(handData.fingers().get(1));
            } else {
                leapTool.setTool(toolData);
            }
        }
    }
    
    private void populatePointData(SwipePoint leapPoint) {
        if(leapPoint != null) {
            if(toolData.isValid()) {
                leapPoint.setPoint(toolData.stabilizedTipPosition());
                leapPoint.setTouchData(toolData);
            } else if(handData.isValid()) {
                // NOTE: Using the stabilizedTipPosition here causes some odd lag along the z-axis.
                leapPoint.setPoint(handData.fingers().get(1).tipPosition());
                leapPoint.setTouchData(handData.fingers().get(1));
            } else {
                leapPoint.setPoint(Vector.zero());
                leapPoint.setTouchData(null);
            }
        }
    }
}

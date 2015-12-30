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
/******************************************************************************\
* Copyright (C) 2012-2013 Leap Motion, Inc. All rights reserved.               *
* Leap Motion proprietary and confidential. Not for distribution.              *
* Use subject to the terms of the Leap Motion SDK Agreement available at       *
* https://developer.leapmotion.com/sdk_agreement, or another agreement         *
* between Leap Motion and you, your company or other organization.             *
\******************************************************************************/

import java.util.ArrayList;

import com.leapmotion.leap.*;

public class LeapListener extends Listener /*implements SaveSettingsObserver*/ {
    private static final Controller CONTROLLER = new Controller();
    private static LeapListener LISTENER;
    private static ArrayList<LeapObserver> OBSERVERS = new ArrayList<LeapObserver>();
    private LeapData leapData = new LeapData();
    
    public static void init() {
        LISTENER = new LeapListener();
    }
    
    public void onInit(Controller controller) {
        System.out.println("Leap - Initialized " + controller.frame().interactionBox());
        notifyListenersInteractionBoxUpdate(controller.frame().interactionBox());
    }

    public void onConnect(Controller controller) {
        System.out.println("Leap - Connected");
        // Gestures do not work with tools.
        // controller.enableGesture(Gesture.Type.TYPE_SWIPE);
        // controller.enableGesture(Gesture.Type.TYPE_CIRCLE);
    }

    public void onDisconnect(Controller controller) {
        //Note: not dispatched when running in a debugger.
        System.out.println("Leap - Disconnected");
    }

    public void onExit(Controller controller) {
        System.out.println("Leap - Exited");
    }

    public void onFrame(Controller controller) {
        // Get the most recent frame and report some basic information
        Frame frame = controller.frame();

        // Populate leapData with relevant hand data and gestures.
        leapData.setHandData(frame.hands().frontmost());
        
        // Override with tool if it's valid.
        // Populate leapData with relevant tool data and gestures.
        leapData.setToolData(frame.tools().frontmost());

        notifyListenersDataUpdate();
    }
    
    public static void stopListening() {
        CONTROLLER.removeListener(LISTENER);
    }
    
    public static void startListening() {
        CONTROLLER.addListener(LISTENER);
    }
    
    public static void registerObserver(LeapObserver observer) {
        if(OBSERVERS.contains(observer)) {
            return;
        }
        OBSERVERS.add(observer);
    }
    
    public static void removeObserver(LeapObserver observer) {
        OBSERVERS.remove(observer);
    }

    protected void notifyListenersInteractionBoxUpdate(InteractionBox iBox) {
        for(LeapObserver observer : OBSERVERS) {
            observer.leapInteractionBoxSet(iBox);
        }
    }
    
    protected void notifyListenersDataUpdate() {
        for(LeapObserver observer : OBSERVERS) {
            observer.leapEventObserved(leapData);
        }
    }

    // Leap does not register tool gestures so don't need to calibrate leap gesture settings.
    // Instead we have to implement our own.
    /*@Override
    public void saveSettingsEventObserved(IKeyboard keyboard) {
        if(keyboard == KeyboardType.LEAP.getKeyboard()) {
            Config config = controller.config();
            for(KeyboardSetting ks: keyboard.getSettings().getAllSettings()) {
                config.setFloat(ks.getName(), (float) ks.getValue());
            }
            System.out.println("Saving Settings to Leap Config: " + (config.save() ? "success" : "failed"));
        }
    }*/
}

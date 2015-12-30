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

package main;

import leap.LeapListener;

import ui.ControlCenter;
import ui.GraphicsController;

public class Main {
    public static void main(String[] args) {
        try {
            /* Initialization
             *      Choose a GLProfile and configuring GLCapabilities for a rendering context
             *      Create a window and GLContext through the GLAutoDrawable
             *      Make an animator thread
             *      Load resources needed by program
             */
            GraphicsController.init();
            
            // Create a sample listener and controller
            LeapListener.init();
            
            ControlCenter controlCenter = new ControlCenter();
    
            // Have the sample listener receive events from the controller
            //controller.addListener(listener);
            while(controlCenter.isRunning()) {
                /* Update (Simulate Leap)
                 *      Calculate geometry
                 *      Rearrange data
                 *      Perform computations
                 */
                controlCenter.update();
                
                /* Render
                 *      Draw scene geometry from a particular view
                 */
                controlCenter.render();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // We can't always close the current Java Swing thread,
            // therefore we must force a full system exit for the current JVM.
            System.exit(0);
        }
    }
}

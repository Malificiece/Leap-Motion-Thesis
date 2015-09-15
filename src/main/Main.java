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

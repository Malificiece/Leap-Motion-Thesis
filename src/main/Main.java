package main;

import ui.ControlCenter;
import ui.GraphicsController;
import leap.LeapListener;

import com.leapmotion.leap.Controller;


public class Main {
    public static ControlCenter controlCenter;
    public static boolean exit = false;
    
    public static void main(String[] args) {
        
        /* Initialization
         *      Choose a GLProfile and configuring GLCapabilities for a rendering context
         *      Create a window and GLContext through the GLAutoDrawable
         *      Make an animator thread
         *      Load resources needed by program
         */
        
        GraphicsController.init();
        
        // Create a sample listener and controller
        Controller controller = new Controller();
        LeapListener listener = new LeapListener(controller);
        
        controlCenter = new ControlCenter(listener);

        // Have the sample listener receive events from the controller
        //controller.addListener(listener);
        while(!exit) {
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
        
        //si.dispose();
        // Remove the sample listener when done
        controller.removeListener(listener);
    }
}

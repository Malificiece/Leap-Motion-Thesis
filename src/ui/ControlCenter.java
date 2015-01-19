/*
 * 
 */

package ui;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JTextField;

import utilities.MyUtilities;
import keyboard.leap.LeapKeyboard;
import enums.Keyboard;
import enums.TestType;
import leap.LeapListener;

public class ControlCenter {
    // Constants
    private final ExperimentController EXPERIMENT_CONTROLLER = new ExperimentController();
    private final CalibrationController CALIBRATION_CONTROLLER = new CalibrationController();
    //private final ExitSurveyController EXIT_SURVEY_CONTROLLER = new ExitSurveyController();
    
    // Not Constants
    private JFrame frame;
    private JTextField subjectField;
    private String subjectID;
    private JComboBox<String> testType;
    private JButton calibrate;
    private JButton experiment;
    private final  ReentrantLock expLock = new ReentrantLock();
    private final ReentrantLock calibLock = new ReentrantLock();
    
    
    public ControlCenter(LeapListener leapListener) {
        leapListener.registerObserver((LeapKeyboard) Keyboard.LEAP.getKeyboard());
        // CALIBRATION_CONTROLLER.registerObserver(leapListener); -- Leap can't see tool gestures.
        // Java Swing/AWT important fields and selections
        frame = new JFrame("Experiment Control Center");
        subjectID = MyUtilities.generateSubjectID();
        subjectField = new JTextField(subjectID);
        testType = new JComboBox<String>();
        calibrate = new JButton("Calibration");
        experiment = new JButton("Experiment");
        
        JButton buttons[] = {calibrate, experiment};
        
        // Window builder builds window using important fields here. It adds unimportant fields that we won't use for aesthetics only.
        WindowBuilder.buildControlWindow(frame, testType, subjectField, buttons);
        frame.setVisible(true);
        
        // by default, an AWT Frame doesn't do anything when you click
        // the close button; this bit of code will terminate the program when
        // the window is asked to close
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                // Change to close experiment window.
                frame.dispose();
                System.exit(0);
            }
        });
        
        // RUN EXPERIEMENT BUTTON CONTROL
        experiment.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                expLock.lock();
                try {
                    if(!isInProgress()) {
                        EXPERIMENT_CONTROLLER.enable(subjectID, TestType.getByName((String) testType.getSelectedItem()));
                        System.out.println("Starting Experiment");
                    }
                } finally {
                    expLock.unlock();
                }
            }
            
        });
        
        // RUN CALIBRATION BUTTON CONTROL
        calibrate.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                calibLock.lock();
                try {
                    if(!isInProgress()) {
                        CALIBRATION_CONTROLLER.enable();
                        System.out.println("Starting Calibration");
                    }
                } finally {
                    calibLock.unlock();
                }
            }
            
        });
    }
    
    public void update() {
        if(expInProgress()) {
            EXPERIMENT_CONTROLLER.update();
        }
        
        if(calibInProgress()) {
            CALIBRATION_CONTROLLER.update();
        }
    }
    
    public void render() {
        if(expInProgress()) {
            EXPERIMENT_CONTROLLER.display();
        }

        if(calibInProgress()) {
            CALIBRATION_CONTROLLER.display();
        }
    }
    
    public boolean isInProgress() {
        if(calibInProgress() || expInProgress()) {
            return true;
        }
        return false;
    }
    
    public boolean calibInProgress() {
        calibLock.lock();
        try {
            return CALIBRATION_CONTROLLER.isEnabled();
        } finally {
            calibLock.unlock();
        }
    }
    
    public boolean expInProgress() {
        expLock.lock();
        try {
            return EXPERIMENT_CONTROLLER.isEnabled();
        } finally {
            expLock.unlock();
        }
    }
}

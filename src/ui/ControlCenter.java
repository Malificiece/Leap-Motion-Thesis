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
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import utilities.MyUtilities;
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
    private JComboBox<String> testTypeComboBox;
    private JButton calibrateButton;
    private JButton experimentButton;
    private final  ReentrantLock expLock = new ReentrantLock();
    private final ReentrantLock calibLock = new ReentrantLock();
    
    
    public ControlCenter(LeapListener leapListener) {
        // Java Swing/AWT important fields and selections
        frame = new JFrame("Experiment Control Center");
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        subjectID = MyUtilities.generateSubjectID();
        subjectField = new JTextField(subjectID);
        testTypeComboBox = new JComboBox<String>();
        calibrateButton = new JButton("Calibration");
        experimentButton = new JButton("Experiment");
        
        JButton buttons[] = {calibrateButton, experimentButton};
        
        // Window builder builds window using important fields here. It adds unimportant fields that we won't use for aesthetics only.
        WindowBuilder.buildControlWindow(frame, testTypeComboBox, subjectField, buttons);
        frame.setVisible(true);
        
        // by default, an AWT Frame doesn't do anything when you click
        // the close button; this bit of code will terminate the program when
        // the window is asked to close
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                if(!expInProgress()) {
                    frame.dispose();
                    System.exit(0);
                } else {
                    Object[] options = {"Yes", "Cancel"};
                    int selection =
                            JOptionPane.showOptionDialog(frame,
                            "An experiment is currently running. If you close now, data will be lost.\nClose anyway?",
                            "Warning!",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE,
                            null,
                            options,
                            options[0]);
                    if(selection == JOptionPane.YES_OPTION) {
                        frame.dispose();
                        System.exit(0);
                    }
                }
            }
        });
        
        // RUN EXPERIEMENT BUTTON CONTROL
        experimentButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                expLock.lock();
                try {
                    if(!isInProgress()) {
                        EXPERIMENT_CONTROLLER.enable(subjectID, TestType.getByName((String) testTypeComboBox.getSelectedItem()));
                        System.out.println("Starting Experiment");
                        lockUI();
                    }
                } finally {
                    expLock.unlock();
                }
            }
            
        });
        
        // RUN CALIBRATION BUTTON CONTROL
        calibrateButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                calibLock.lock();
                try {
                    if(!isInProgress()) {
                        CALIBRATION_CONTROLLER.enable();
                        System.out.println("Starting Calibration");
                        lockUI();
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
        } else if(calibInProgress()) {
            CALIBRATION_CONTROLLER.update();
        } else {
            unlockUI();
        }
    }
    
    public void render() {
        if(expInProgress()) {
            EXPERIMENT_CONTROLLER.display();
        } else if(calibInProgress()) {
            CALIBRATION_CONTROLLER.display();
        } else {
            unlockUI();
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
    
    private void lockUI() {
        calibrateButton.setEnabled(false);
        experimentButton.setEnabled(false);
        testTypeComboBox.setEnabled(false);
    }
    
    private void unlockUI() {
        calibrateButton.setEnabled(true);
        experimentButton.setEnabled(true);
        testTypeComboBox.setEnabled(true);
    }
}

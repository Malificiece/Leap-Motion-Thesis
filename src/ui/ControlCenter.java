/*
 * 
 */

package ui;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JTextField;

import keyboard.leap.LeapKeyboard;
import enums.KeyboardType;
import leap.LeapListener;


public class ControlCenter {
    // Constants
    private final int HOURS_MIN = 0;
    private final int HOURS_MAX = 24;
    private final int HOURS_INIT = 12;
    private final CalibrationController CALIBRATION_CONTROLLER = new CalibrationController();
    private final ExperimentController EXPERIMENT_CONTROLLER = new ExperimentController();
    
    // Not Constants
    //LeapListener leapListener;
    private JFrame frame;
    private JTextField firstName;
    private JTextField lastName;
    private JTextField age;
    private JRadioButton male;
    private JRadioButton female;
    private ButtonGroup genderGroup;
    private JSlider hoursOnComputer; //per day
    private JComboBox<String> testType;
    private JRadioButton rightHanded;
    private JRadioButton leftHanded;
    private ButtonGroup handednessGroup;
    private JButton calibrate;
    private JButton experiment;
    private Boolean expInProgress = false;
    private Boolean calibInProgress = false;
    private final  ReentrantLock expLock = new ReentrantLock();
    private final ReentrantLock calibLock = new ReentrantLock();
    
    
    public ControlCenter(LeapListener leapListener) {
        leapListener.registerObserver((LeapKeyboard) KeyboardType.LEAP.getKeyboard());
       // CALIBRATION_CONTROLLER.registerObserver(leapListener); -- Leap can't see tool gestures.
        // Java Swing/AWT important fields and selections
        frame = new JFrame("Experiment Control Center v0.1");
        firstName = new JTextField(10);
        lastName = new JTextField(10);
        male = new JRadioButton("Male");
        female = new JRadioButton("Female");   
        age = new JTextField(10);
        hoursOnComputer = new JSlider(JSlider.HORIZONTAL, HOURS_MIN, HOURS_MAX, HOURS_INIT);
        testType = new JComboBox<String>();
        rightHanded = new JRadioButton("Right-handed");
        leftHanded = new JRadioButton("Left-handed");
        calibrate = new JButton("Calibration");
        experiment = new JButton("Experiment");
        
        JTextField textFields[] = {firstName, lastName, age};
        JRadioButton radioButtons[] = {male, female, rightHanded, leftHanded};
        JButton buttons[] = {calibrate, experiment};
        
        // Window builder builds window using important fields here. It adds unimportant fields that we won't use for aesthetics only.
        WindowBuilder.buildControlWindow(frame, textFields, testType, buttons, radioButtons, hoursOnComputer);
        
        handednessGroup = new ButtonGroup();
        handednessGroup.add(rightHanded);
        handednessGroup.add(leftHanded);
        rightHanded.setSelected(true);
        
        genderGroup = new ButtonGroup();
        genderGroup.add(male);
        genderGroup.add(female);
        male.setSelected(true);
        
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
                    EXPERIMENT_CONTROLLER.enable();
                    expInProgress = true;
                } finally {
                    expLock.unlock();
                }
                System.out.println("Starting Experiment");
            }
            
        });
        
        // RUN CALIBRATION BUTTON CONTROL
        calibrate.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                calibLock.lock();
                try {
                    //calibControl = null;
                    CALIBRATION_CONTROLLER.enable();
                    calibInProgress = true;
                } finally {
                    calibLock.unlock();
                }
                System.out.println("Starting Calibration");
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
    
    public Boolean calibInProgress() {
        calibLock.lock();
        try {
            return calibInProgress;
        } finally {
            calibLock.unlock();
        }
    }
    
    public Boolean expInProgress() {
        expLock.lock();
        try {
            return expInProgress;
        } finally {
            expLock.unlock();
        }
    }
}

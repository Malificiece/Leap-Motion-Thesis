package ui;

import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JRadioButton;

public class ExitSurveyController {
    //private final int HOURS_MIN = 0;
    //private final int HOURS_MAX = 24;
    //private final int HOURS_INIT = 12;
    
    private JFrame frame;
    //private JTextField firstName;
    //private JTextField lastName;
    //private JTextField age;
    private JRadioButton male;
    private JRadioButton female;
    private ButtonGroup genderGroup;
   // private JSlider hoursOnComputer; //per day
    private JRadioButton rightHanded;
    private JRadioButton leftHanded;
    private ButtonGroup handednessGroup;
    
    public ExitSurveyController() {
        frame = new JFrame("Exit Survey - Subject ID:");
        //firstName = new JTextField(10);
        //lastName = new JTextField(10);
        male = new JRadioButton("Male");
        female = new JRadioButton("Female");   
        //age = new JTextField(10);
        //hoursOnComputer = new JSlider(JSlider.HORIZONTAL, HOURS_MIN, HOURS_MAX, HOURS_INIT);
        rightHanded = new JRadioButton("Right-handed");
        leftHanded = new JRadioButton("Left-handed");
        
        //JTextField textFields[] = {firstName, lastName, age};
        //JRadioButton radioButtons[] = {male, female, rightHanded, leftHanded};
        
        // Window builder builds window using important fields here. It adds unimportant fields that we won't use for aesthetics only.
        //WindowBuilder.buildExitSurveyWindow(frame, textFields, testType, buttons, radioButtons, hoursOnComputer);
        
        handednessGroup = new ButtonGroup();
        handednessGroup.add(rightHanded);
        handednessGroup.add(leftHanded);
        rightHanded.setSelected(true);
        
        genderGroup = new ButtonGroup();
        genderGroup.add(male);
        genderGroup.add(female);
        male.setSelected(true);
        
        frame.setVisible(true);
    }
}

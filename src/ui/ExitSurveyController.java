package ui;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Enumeration;

import javax.media.opengl.GLAutoDrawable;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import enums.Keyboard;
import enums.TestType;

public class ExitSurveyController extends GraphicsController {
    private JFrame frame;
    private JTextField subjectIDTextField;
    private JTextField majorTextField;
    private JTextField ageTextField;
    private ButtonGroup genderButtonGroup;
    private ButtonGroup hasComputerButtonGroup;
    private ButtonGroup computerHoursPerWeekButtonGroup;
    private ButtonGroup physicalImpairmentButtonGroup;
    private JTextField physicalImpairmentTextField;
    private ButtonGroup gestureDeviceExperienceButtonGroup;
    private JTextField gestureDeviceExperienceTextField;
    private ButtonGroup touchDeviceExperienceButtonGroup;
    private JTextField touchDeviceExperienceTextField;
    private ButtonGroup swipeDeviceExperienceButtonGroup;
    private JTextField swipeDeviceExperienceTextField;
    private ButtonGroup handednessButtonGroup;
    private ButtonGroup preferedTouchHandButtonGroup;
    private ButtonGroup controllerDiscomfortButtonGroup;
    private ButtonGroup controllerFatigueButtonGroup;
    private ButtonGroup controllerDifficultyButtonGroup;
    private ButtonGroup tabletDiscomfortButtonGroup;
    private ButtonGroup tabletFatigueButtonGroup;
    private ButtonGroup tabletDifficultyButtonGroup;
    private ButtonGroup leapSurfaceDiscomfortButtonGroup;
    private ButtonGroup leapSurfaceFatigueButtonGroup;
    private ButtonGroup leapSurfaceDifficultyButtonGroup;
    private ButtonGroup leapAirDiscomfortButtonGroup;
    private ButtonGroup leapAirFatigueButtonGroup;
    private ButtonGroup leapAirDifficultyButtonGroup;
    private ButtonGroup leapPinchDiscomfortButtonGroup;
    private ButtonGroup leapPinchFatigueButtonGroup;
    private ButtonGroup leapPinchDifficultyButtonGroup;
    private JTextField controllerRatingTextField;
    private JTextField tabletRatingTextField;
    private JTextField leapSurfaceRatingTextField;
    private JTextField leapAirRatingTextField;
    private JTextField leapPinchRatingTextField;
    
    public ExitSurveyController() {
        frame = new JFrame("Exit Survey - Subject ID:");
        subjectIDTextField = new JTextField(10);
        majorTextField = new JTextField(10);
        ageTextField = new JTextField(10);
        genderButtonGroup = new ButtonGroup();
        hasComputerButtonGroup = new ButtonGroup();
        computerHoursPerWeekButtonGroup = new ButtonGroup();
        physicalImpairmentButtonGroup = new ButtonGroup();
        physicalImpairmentTextField = new JTextField(25);
        gestureDeviceExperienceButtonGroup = new ButtonGroup();
        gestureDeviceExperienceTextField = new JTextField(50);
        touchDeviceExperienceButtonGroup = new ButtonGroup();
        touchDeviceExperienceTextField = new JTextField(50);
        swipeDeviceExperienceButtonGroup = new ButtonGroup();
        swipeDeviceExperienceTextField = new JTextField(50);
        handednessButtonGroup = new ButtonGroup();
        preferedTouchHandButtonGroup = new ButtonGroup();
        controllerDiscomfortButtonGroup = new ButtonGroup();
        controllerFatigueButtonGroup = new ButtonGroup();
        controllerDifficultyButtonGroup = new ButtonGroup();
        tabletDiscomfortButtonGroup = new ButtonGroup();
        tabletFatigueButtonGroup = new ButtonGroup();
        tabletDifficultyButtonGroup = new ButtonGroup();
        leapSurfaceDiscomfortButtonGroup = new ButtonGroup();
        leapSurfaceFatigueButtonGroup = new ButtonGroup();
        leapSurfaceDifficultyButtonGroup = new ButtonGroup();
        leapAirDiscomfortButtonGroup = new ButtonGroup();
        leapAirFatigueButtonGroup = new ButtonGroup();
        leapAirDifficultyButtonGroup = new ButtonGroup();
        leapPinchDiscomfortButtonGroup = new ButtonGroup();
        leapPinchFatigueButtonGroup = new ButtonGroup();
        leapPinchDifficultyButtonGroup = new ButtonGroup();
        controllerRatingTextField = new JTextField(10);
        tabletRatingTextField = new JTextField(10);
        leapSurfaceRatingTextField = new JTextField(10);
        leapAirRatingTextField = new JTextField(10);
        leapPinchRatingTextField = new JTextField(10);
        
        JTextField subjectTextFields[] = {subjectIDTextField, ageTextField, majorTextField};
        JTextField historyTextFields[] = {physicalImpairmentTextField, gestureDeviceExperienceTextField, touchDeviceExperienceTextField, swipeDeviceExperienceTextField};
        JTextField ratingTextFields[] = {controllerRatingTextField, tabletRatingTextField, leapSurfaceRatingTextField, leapAirRatingTextField, leapPinchRatingTextField};
        ButtonGroup subjectButtonGroups[] = {genderButtonGroup, hasComputerButtonGroup, computerHoursPerWeekButtonGroup, handednessButtonGroup, preferedTouchHandButtonGroup};
        ButtonGroup historyButtonGroups[] = {physicalImpairmentButtonGroup, gestureDeviceExperienceButtonGroup, touchDeviceExperienceButtonGroup, swipeDeviceExperienceButtonGroup};
        ButtonGroup discomfortButtonGroups[] = {controllerDiscomfortButtonGroup, tabletDiscomfortButtonGroup, leapSurfaceDiscomfortButtonGroup, leapAirDiscomfortButtonGroup, leapPinchDiscomfortButtonGroup};
        ButtonGroup fatigueButtonGroups[] = {controllerFatigueButtonGroup, tabletFatigueButtonGroup, leapSurfaceFatigueButtonGroup, leapAirFatigueButtonGroup, leapPinchFatigueButtonGroup};
        ButtonGroup difficutlyButtonGroups[] = {controllerDifficultyButtonGroup, tabletDifficultyButtonGroup, leapSurfaceDifficultyButtonGroup, leapAirDifficultyButtonGroup, leapPinchDifficultyButtonGroup};
        
        // Window builder builds window using important fields here. It adds unimportant fields that we won't use for aesthetics only.
        WindowBuilder.buildExitSurveyWindow(frame,
                subjectTextFields, historyTextFields, ratingTextFields,
                subjectButtonGroups, historyButtonGroups, discomfortButtonGroups, fatigueButtonGroups, difficutlyButtonGroups);
        
        // by default, an AWT Frame doesn't do anything when you click
        // the close button; this bit of code will terminate the program when
        // the window is asked to close
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                if(true /* TODO: BOOLEAN FOR CHECK FOR EVERYTHING BEING PROPERLY FILLED OUT*/) {
                    disable();
                } // else pop up dialog warning we're not done
            }
        });
    }

    @Override
    public void disable() {
        frame.setVisible(false);
        enabled = false;
    }
    
    @Override
    public void enable() {
        frame.setVisible(true);
        frame.requestFocusInWindow();
        enabled = true;
    }
    
    public void enable(String subjectID) {
        if(frame != null) {
            frame.setTitle("Exit Survey - Subject ID: " + subjectID);
        }
        subjectIDTextField.setText(subjectID);
        subjectIDTextField.setEnabled(false);
        enable();
    }
    
    public String getSelectedButtonText(ButtonGroup buttonGroup) {
        for (Enumeration<AbstractButton> buttons = buttonGroup.getElements(); buttons.hasMoreElements();) {
            AbstractButton button = buttons.nextElement();
            if (button.isSelected()) {
                return button.getText();
            }
        }
        return null;
    }

    @Override
    public void update() {
        // Do nothing.
    }

    @Override
    public void render(GLAutoDrawable drawable) {
        // Do nothing.
    }
    
    @Override
    public void keyboardKeyEventObserved(char key) {
        // Do nothing.
    }

    @Override
    public void keyboardCalibrationFinishedEventObserved() {
        // Do nothing.
    }
}

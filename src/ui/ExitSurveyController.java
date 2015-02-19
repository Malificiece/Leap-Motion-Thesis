package ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.media.opengl.GLAutoDrawable;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import utilities.MyUtilities;
import enums.ExitSurveyDataType;
import enums.ExitSurveyOptions;
import enums.FileExt;
import enums.FilePath;

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
    private ButtonGroup preferedExperimentHandButtonGroup;
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
    private JTextField controllerRankingTextField;
    private JTextField tabletRankingTextField;
    private JTextField leapSurfaceRankingTextField;
    private JTextField leapAirRankingTextField;
    private JTextField leapPinchRankingTextField;
    private JButton saveButton;
    private boolean isSurveySaved = false;
    
    public ExitSurveyController() {
        frame = new JFrame("Exit Survey - Subject ID:");
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        subjectIDTextField = new JTextField(10);
        majorTextField = new JTextField(10);
        ageTextField = new JTextField(10);
        genderButtonGroup = new ButtonGroup();
        hasComputerButtonGroup = new ButtonGroup();
        computerHoursPerWeekButtonGroup = new ButtonGroup();
        physicalImpairmentButtonGroup = new ButtonGroup();
        physicalImpairmentTextField = new JTextField(37);
        gestureDeviceExperienceButtonGroup = new ButtonGroup();
        gestureDeviceExperienceTextField = new JTextField(39);
        touchDeviceExperienceButtonGroup = new ButtonGroup();
        touchDeviceExperienceTextField = new JTextField(39);
        swipeDeviceExperienceButtonGroup = new ButtonGroup();
        swipeDeviceExperienceTextField = new JTextField(39);
        handednessButtonGroup = new ButtonGroup();
        preferedExperimentHandButtonGroup = new ButtonGroup();
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
        controllerRankingTextField = new JTextField(3);
        tabletRankingTextField = new JTextField(3);
        leapSurfaceRankingTextField = new JTextField(3);
        leapAirRankingTextField = new JTextField(3);
        leapPinchRankingTextField = new JTextField(3);
        saveButton = new JButton("Save Survey");
        
        JTextField subjectTextFields[] = {subjectIDTextField, ageTextField, majorTextField};
        JTextField historyTextFields[] = {physicalImpairmentTextField, gestureDeviceExperienceTextField, touchDeviceExperienceTextField, swipeDeviceExperienceTextField};
        JTextField rankingTextFields[] = {controllerRankingTextField, tabletRankingTextField, leapSurfaceRankingTextField, leapAirRankingTextField, leapPinchRankingTextField};
        ButtonGroup subjectButtonGroups[] = {genderButtonGroup, hasComputerButtonGroup, computerHoursPerWeekButtonGroup, handednessButtonGroup, preferedExperimentHandButtonGroup};
        ButtonGroup historyButtonGroups[] = {physicalImpairmentButtonGroup, gestureDeviceExperienceButtonGroup, touchDeviceExperienceButtonGroup, swipeDeviceExperienceButtonGroup};
        ButtonGroup discomfortButtonGroups[] = {controllerDiscomfortButtonGroup, tabletDiscomfortButtonGroup, leapSurfaceDiscomfortButtonGroup, leapAirDiscomfortButtonGroup, leapPinchDiscomfortButtonGroup};
        ButtonGroup fatigueButtonGroups[] = {controllerFatigueButtonGroup, tabletFatigueButtonGroup, leapSurfaceFatigueButtonGroup, leapAirFatigueButtonGroup, leapPinchFatigueButtonGroup};
        ButtonGroup difficultyButtonGroups[] = {controllerDifficultyButtonGroup, tabletDifficultyButtonGroup, leapSurfaceDifficultyButtonGroup, leapAirDifficultyButtonGroup, leapPinchDifficultyButtonGroup};
        
        // Window builder builds window using important fields here. It adds unimportant fields that we won't use for aesthetics only.
        WindowBuilder.buildExitSurveyWindow(frame,
                subjectTextFields, historyTextFields, rankingTextFields,
                subjectButtonGroups, historyButtonGroups, discomfortButtonGroups, fatigueButtonGroups, difficultyButtonGroups,
                saveButton);
        
        JRadioButton physicalImpairmentRadioButton = getRadioButtonToListenTo(physicalImpairmentButtonGroup);
        if(physicalImpairmentRadioButton != null) {
        	physicalImpairmentRadioButton.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					if(physicalImpairmentRadioButton.isSelected()) {
						physicalImpairmentTextField.setEditable(true);
					} else {
						physicalImpairmentTextField.setEditable(false);
						physicalImpairmentTextField.setText("");
					}
				}
        	});
        }
        
        JRadioButton gestureDeviceExperienceRadioButton = getRadioButtonToListenTo(gestureDeviceExperienceButtonGroup);
        if(gestureDeviceExperienceRadioButton != null) {
        	gestureDeviceExperienceRadioButton.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					if(gestureDeviceExperienceRadioButton.isSelected()) {
						gestureDeviceExperienceTextField.setEditable(true);
					} else {
						gestureDeviceExperienceTextField.setEditable(false);
						gestureDeviceExperienceTextField.setText("");
					}
				}
        	});
        }
        
        JRadioButton touchDeviceExperienceRadioButton = getRadioButtonToListenTo(touchDeviceExperienceButtonGroup);
        if(touchDeviceExperienceRadioButton != null) {
        	touchDeviceExperienceRadioButton.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					if(touchDeviceExperienceRadioButton.isSelected()) {
						touchDeviceExperienceTextField.setEditable(true);
					} else {
						touchDeviceExperienceTextField.setEditable(false);
						touchDeviceExperienceTextField.setText("");
					}
				}
        	});
        }
        
        JRadioButton swipeDeviceExperienceRadioButton = getRadioButtonToListenTo(swipeDeviceExperienceButtonGroup);
        if(swipeDeviceExperienceRadioButton != null) {
        	swipeDeviceExperienceRadioButton.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					if(swipeDeviceExperienceRadioButton.isSelected()) {
						swipeDeviceExperienceTextField.setEditable(true);
					} else {
						swipeDeviceExperienceTextField.setEditable(false);
						swipeDeviceExperienceTextField.setText("");
					}
				}
        	});
        }
        
        saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
            	if(isFilledOut()) {
            		if(saveSurvey()) {
            			disable();
            		}
            	} else {
                    JOptionPane.showMessageDialog(frame,
	                        "You must fill out the entire form before saving.",
	                        "Warning!",
	                        JOptionPane.WARNING_MESSAGE,
	                        null);
            	}
			}
        });
        
        // by default, an AWT Frame doesn't do anything when you click
        // the close button; this bit of code will terminate the program when
        // the window is asked to close
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
            	if(isFilledOut() && isSurveySaved) {
            		disable();
            	} else {
                    Object[] options = {"Yes", "Cancel"};
                    int selection =
                            JOptionPane.showOptionDialog(frame,
                            "An exit survey is currently being filled out and has not been saved. If you close now, the survey will be lost.\nClose anyway?",
                            "Warning!",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE,
                            null,
                            options,
                            options[0]);
                    if(selection == JOptionPane.YES_OPTION) {
                        disable();
                    }
            	}
            }
        });
    }
    
    public boolean saveSurvey() {
    	ArrayList<String> exitSurveyData = new ArrayList<String>();
    	
    	// Populate exitSurveyData.
    	exitSurveyData.add(ExitSurveyDataType.SUBJECT_ID.name() + ": " + subjectIDTextField.getText());
    	exitSurveyData.add(ExitSurveyDataType.AGE.name() + ": " + ageTextField.getText());
    	exitSurveyData.add(ExitSurveyDataType.GENDER.name() + ": " + getSelectedButtonText(genderButtonGroup));
    	exitSurveyData.add(ExitSurveyDataType.MAJOR.name() + ": " + majorTextField.getText());
    	exitSurveyData.add(ExitSurveyDataType.HAS_PERSONAL_COMPUTER.name() + ": " + getSelectedButtonText(hasComputerButtonGroup));
    	exitSurveyData.add(ExitSurveyDataType.HOURS_PER_WEEK_ON_COMPUTER.name() + ": " + getSelectedButtonText(computerHoursPerWeekButtonGroup));
    	exitSurveyData.add(ExitSurveyDataType.HAS_PREVIOUS_GESTURE_DEVICE_EXPERIENCE.name() + ": " + getSelectedButtonText(gestureDeviceExperienceButtonGroup));
    	if(gestureDeviceExperienceTextField.isEditable() && gestureDeviceExperienceTextField.isEnabled()) {
    		exitSurveyData.add(ExitSurveyDataType.PREVIOUS_GESTURE_DEVICE_DESCRIPTION.name() + ": " + gestureDeviceExperienceTextField.getText());
    	}
    	exitSurveyData.add(ExitSurveyDataType.HAS_PREVIOUS_TOUCH_DEVICE_EXPERIENCE.name() + ": " + getSelectedButtonText(touchDeviceExperienceButtonGroup));
    	if(touchDeviceExperienceTextField.isEditable() && touchDeviceExperienceTextField.isEnabled()) {
    		exitSurveyData.add(ExitSurveyDataType.PREVIOUS_TOUCH_DEVICE_DESCRIPTION.name() + ": " + touchDeviceExperienceTextField.getText());
    	}
    	exitSurveyData.add(ExitSurveyDataType.HAS_PREVIOUS_SWIPE_DEVICE_EXPERIENCE.name() + ": " + getSelectedButtonText(swipeDeviceExperienceButtonGroup));
    	if(swipeDeviceExperienceTextField.isEditable() && swipeDeviceExperienceTextField.isEnabled()) {
    		exitSurveyData.add(ExitSurveyDataType.PREVIOUS_SWIPE_DEVICE_DESCRIPTION.name() + ": " + swipeDeviceExperienceTextField.getText());
    	}
    	exitSurveyData.add(ExitSurveyDataType.HAS_PHYSICAL_IMPAIRMENT.name() + ": " + getSelectedButtonText(physicalImpairmentButtonGroup));
    	if(physicalImpairmentTextField.isEditable() && physicalImpairmentTextField.isEnabled()) {
    		exitSurveyData.add(ExitSurveyDataType.PHYSICAL_INPAIRMENT_DESCRIPTION.name() + ": " + physicalImpairmentTextField.getText());
    	}
    	exitSurveyData.add(ExitSurveyDataType.HANDEDNESS.name() + ": " + getSelectedButtonText(handednessButtonGroup));
    	exitSurveyData.add(ExitSurveyDataType.PREFERED_HANDEDNESS_FOR_EXPERIMENT.name() + ": " + getSelectedButtonText(preferedExperimentHandButtonGroup));
    	
    	// Survey section
    	exitSurveyData.add(ExitSurveyDataType.CONTROLLER_KEYBOARD_DISCOMFORT_LEVEL.name() + ": " + getSelectedButtonText(controllerDiscomfortButtonGroup));
    	exitSurveyData.add(ExitSurveyDataType.CONTROLLER_KEYBOARD_FATIGUE_LEVEL.name() + ": " + getSelectedButtonText(controllerFatigueButtonGroup));
    	exitSurveyData.add(ExitSurveyDataType.CONTROLLER_KEYBOARD_DIFFICULTY_LEVEL.name() + ": " + getSelectedButtonText(controllerDifficultyButtonGroup));
    	exitSurveyData.add(ExitSurveyDataType.TABLET_KEYBOARD_DISCOMFORT_LEVEL.name() + ": " + getSelectedButtonText(tabletDiscomfortButtonGroup));
    	exitSurveyData.add(ExitSurveyDataType.TABLET_KEYBOARD_FATIGUE_LEVEL.name() + ": " + getSelectedButtonText(tabletFatigueButtonGroup));
    	exitSurveyData.add(ExitSurveyDataType.TABLET_KEYBOARD_DIFFICULTY_LEVEL.name() + ": " + getSelectedButtonText(tabletDifficultyButtonGroup));
    	exitSurveyData.add(ExitSurveyDataType.LEAP_SURFACE_KEYBOARD_DISCOMFORT_LEVEL.name() + ": " + getSelectedButtonText(leapSurfaceDiscomfortButtonGroup));
    	exitSurveyData.add(ExitSurveyDataType.LEAP_SURFACE_KEYBOARD_FATIGUE_LEVEL.name() + ": " + getSelectedButtonText(leapSurfaceFatigueButtonGroup));
    	exitSurveyData.add(ExitSurveyDataType.LEAP_SURFACE_KEYBOARD_DIFFICULTY_LEVEL.name() + ": " + getSelectedButtonText(leapSurfaceDifficultyButtonGroup));
    	exitSurveyData.add(ExitSurveyDataType.LEAP_AIR_KEYBOARD_DISCOMFORT_LEVEL.name() + ": " + getSelectedButtonText(leapAirDiscomfortButtonGroup));
    	exitSurveyData.add(ExitSurveyDataType.LEAP_AIR_KEYBOARD_FATIGUE_LEVEL.name() + ": " + getSelectedButtonText(leapAirFatigueButtonGroup));
    	exitSurveyData.add(ExitSurveyDataType.LEAP_AIR_KEYBOARD_DIFFICULTY_LEVEL.name() + ": " + getSelectedButtonText(leapAirDifficultyButtonGroup));
    	exitSurveyData.add(ExitSurveyDataType.LEAP_PINCH_KEYBOARD_DISCOMFORT_LEVEL.name() + ": " + getSelectedButtonText(leapPinchDiscomfortButtonGroup));
    	exitSurveyData.add(ExitSurveyDataType.LEAP_PINCH_KEYBOARD_FATIGUE_LEVEL.name() + ": " + getSelectedButtonText(leapPinchFatigueButtonGroup));
    	exitSurveyData.add(ExitSurveyDataType.LEAP_PINCH_KEYBOARD_DIFFICULTY_LEVEL.name() + ": " + getSelectedButtonText(leapPinchDifficultyButtonGroup));
    	
    	// Rank section
    	exitSurveyData.add(ExitSurveyDataType.CONTROLLER_KEYBOARD_PREFERENCE_RANKING.name() + ": " + controllerRankingTextField.getText());
    	exitSurveyData.add(ExitSurveyDataType.TABLET_KEYBOARD_PREFERENCE_RANKING.name() + ": " + tabletRankingTextField.getText());
    	exitSurveyData.add(ExitSurveyDataType.LEAP_SURFACE_KEYBOARD_PREFERENCE_RANKING.name() + ": " + leapSurfaceRankingTextField.getText());
    	exitSurveyData.add(ExitSurveyDataType.LEAP_AIR_KEYBOARD_PREFERENCE_RANKING.name() + ": " + leapAirRankingTextField.getText());
    	exitSurveyData.add(ExitSurveyDataType.LEAP_PINCH_KEYBOARD_PREFERENCE_RANKING.name() + ": " + leapPinchRankingTextField.getText());
    	
    	// Write to file.
        String time = "_";
        time += LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        time += "_" + LocalTime.now().format(DateTimeFormatter.ofPattern("kkmm"));
        String filePath = FilePath.DATA.getPath() + subjectIDTextField.getText() + "/";
        String fileName = subjectIDTextField.getText() + "_" + "exitSurvey" + time + FileExt.FILE.getExt();
    	try {
			MyUtilities.FILE_IO_UTILITIES.writeListToFile(exitSurveyData, filePath, fileName, false);
			isSurveySaved = true;
		} catch (IOException e) {
			System.out.println("An error occured while trying to save the survey.");
			e.printStackTrace();
		}
    	return isSurveySaved;
    }
    
    public boolean isFilledOut() {
        JTextField textFields[] = {subjectIDTextField, ageTextField, majorTextField,
        		physicalImpairmentTextField, gestureDeviceExperienceTextField, touchDeviceExperienceTextField, swipeDeviceExperienceTextField,
        		controllerRankingTextField, tabletRankingTextField, leapSurfaceRankingTextField, leapAirRankingTextField, leapPinchRankingTextField};
        ButtonGroup buttonGroups[] = {genderButtonGroup, hasComputerButtonGroup, computerHoursPerWeekButtonGroup, handednessButtonGroup, preferedExperimentHandButtonGroup,
        		physicalImpairmentButtonGroup, gestureDeviceExperienceButtonGroup, touchDeviceExperienceButtonGroup, swipeDeviceExperienceButtonGroup,
        		controllerDiscomfortButtonGroup, tabletDiscomfortButtonGroup, leapSurfaceDiscomfortButtonGroup, leapAirDiscomfortButtonGroup, leapPinchDiscomfortButtonGroup,
        		controllerFatigueButtonGroup, tabletFatigueButtonGroup, leapSurfaceFatigueButtonGroup, leapAirFatigueButtonGroup, leapPinchFatigueButtonGroup,
        		controllerDifficultyButtonGroup, tabletDifficultyButtonGroup, leapSurfaceDifficultyButtonGroup, leapAirDifficultyButtonGroup, leapPinchDifficultyButtonGroup};
        
        // Check text fields
        for(JTextField textField: textFields) {
        	if(textField.isEditable() && textField.isEnabled() && textField.getText().trim().equals("")) {
        		return false;
        	}
        }
        
        // Check button groups
        for(ButtonGroup buttonGroup: buttonGroups) {
        	if(getSelectedButtonText(buttonGroup) == null) {
        		return false;
        	}
        }
        
    	return true;
    }

    @Override
    public void disable() {
        frame.setVisible(false);
        isEnabled = false;
    }
    
    @Override
    public void enable() {
        frame.setVisible(true);
        frame.requestFocusInWindow();
        isEnabled = true;
    }
    
    public void enable(String subjectID) {
        if(frame != null) {
            frame.setTitle("Exit Survey - Subject ID: " + subjectID);
        }
        subjectIDTextField.setText(subjectID);
        subjectIDTextField.setEnabled(false);
        enable();
    }
    
    public JRadioButton getRadioButtonToListenTo(ButtonGroup group) {
        for (Enumeration<AbstractButton> e = group.getElements(); e.hasMoreElements();) {
        	JRadioButton radioButton = (JRadioButton) e.nextElement();
        	ExitSurveyOptions option = ExitSurveyOptions.getByDescription(radioButton.getText());
        	if (option == ExitSurveyOptions.YES) {
        		return radioButton;
        	}
        }
        return null;
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

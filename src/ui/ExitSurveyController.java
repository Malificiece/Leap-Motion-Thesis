package ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import javax.swing.text.AttributeSet;

import utilities.FileUtilities;
import utilities.MyUtilities;
import enums.ExitSurveyDataType;
import enums.ExitSurveyOptions;
import enums.FileExt;
import enums.FileName;
import enums.FilePath;
import enums.Key;
import enums.Keyboard;
import enums.KeyboardType;

public class ExitSurveyController extends WindowController {
    // Static text fields and button groups
    private final int MIN_RANKING = 1;
    private int maxRanking = 0;
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
    
    // Keyboard dependent button groups
    private ButtonGroup discomfortButtonGroup;
    private ButtonGroup fatigueButtonGroup;
    private ButtonGroup difficultyButtonGroup;
    
    // Dynamic final exit survey field for all keyboards
    private ArrayList<JTextField> rankingTextFields = new ArrayList<JTextField>();
    
    // Other
    private JTextArea rankingQuestion;
    private JButton saveButton;
    private boolean isSurveySaved = false;
    private KeyboardType surveyType = null;
    
    public ExitSurveyController(String subjectID, KeyboardType surveyType) {
        this.surveyType = surveyType;
        frame = new JFrame("Exit Survey - Subject ID:");
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        subjectIDTextField = new JTextField(10);
        subjectIDTextField.setText(subjectID);
        saveButton = new JButton("Save Survey");
        
        File surveyFile;
        try {
            String wildcardFileName = subjectID + "_" + FileName.EXIT_SURVEY.getName() + FileUtilities.WILDCARD + FileExt.FILE.getExt();
            surveyFile = MyUtilities.FILE_IO_UTILITIES.openWildcardFile(FilePath.RECORDED_DATA.getPath() + subjectIDTextField.getText() + "/", wildcardFileName);
        } catch (IOException e) {
            System.err.println("Error occured when trying to open exit survey for subject " + subjectIDTextField.getText() + ".");
            surveyFile = null;
        }
        
        ArrayList<String> fileContents;
        if(surveyFile != null) {
            try {
                fileContents = MyUtilities.FILE_IO_UTILITIES.readListFromFile(surveyFile);
            } catch (IOException e1) {
                e1.printStackTrace();
                System.err.println("An error occued when trying to read in the file contents for survey auto-fill.");
                fileContents = new ArrayList<String>();
            }
        } else {
            fileContents = new ArrayList<String>();
        }
        
        if(surveyType == null) {
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
            rankingQuestion = new JTextArea();
            
            JTextField[] subjectTextFields = {subjectIDTextField, ageTextField, majorTextField};
            JTextField[] historyTextFields = {physicalImpairmentTextField, gestureDeviceExperienceTextField, touchDeviceExperienceTextField, swipeDeviceExperienceTextField};
            ButtonGroup[] subjectButtonGroups = {genderButtonGroup, hasComputerButtonGroup, computerHoursPerWeekButtonGroup, handednessButtonGroup, preferedExperimentHandButtonGroup};
            ButtonGroup[] historyButtonGroups = {physicalImpairmentButtonGroup, gestureDeviceExperienceButtonGroup, touchDeviceExperienceButtonGroup, swipeDeviceExperienceButtonGroup};
            
            for(Keyboard keyboard: Keyboard.values()) {
                if(keyboard.getType() != KeyboardType.DISABLED) {
                    rankingTextFields.add(new JTextField(3));
                }
            }
            
            // Make the ranking system reflect the keyboards that are detected to be on record.
            ArrayList<KeyboardType> detectedKeyboardTypes = getSurveyKeyboardsDetected(fileContents);
            Iterator<JTextField> rankingTextFieldIterator = rankingTextFields.iterator();
            for(Keyboard keyboard: Keyboard.values()) {
                KeyboardType keyboardType = keyboard.getType();
                if(keyboardType != KeyboardType.DISABLED && rankingTextFieldIterator.hasNext()) {
                    JTextField rankingTextField = rankingTextFieldIterator.next();
                    rankingTextField.setEditable(false);
                    for(KeyboardType detectedType: detectedKeyboardTypes) {
                        if(keyboardType == detectedType) {
                            // If on the file record
                            rankingTextField.setEditable(true);
                            maxRanking++;   
                            break;
                        }
                    }
                }
            }
            
            // Window builder builds window using important fields here. It adds unimportant fields that we won't use for aesthetics only.
            WindowBuilder.buildExitSurveyWindow(frame,
                    subjectTextFields, historyTextFields, rankingTextFields,
                    subjectButtonGroups, historyButtonGroups,
                    maxRanking, rankingQuestion,
                    saveButton);
            
            ageTextField.setDocument(new NumericOnlyDocument());
            for(JTextField rankingTextField: rankingTextFields) {
                rankingTextField.setDocument(new UniqueRankingDocument(rankingTextField));
            }
            
            JRadioButton physicalImpairmentRadioButton = getYesRadioButton(physicalImpairmentButtonGroup);
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
            
            JRadioButton gestureDeviceExperienceRadioButton = getYesRadioButton(gestureDeviceExperienceButtonGroup);
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
            
            JRadioButton touchDeviceExperienceRadioButton = getYesRadioButton(touchDeviceExperienceButtonGroup);
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
            
            JRadioButton swipeDeviceExperienceRadioButton = getYesRadioButton(swipeDeviceExperienceButtonGroup);
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
            
            if(surveyFile != null) {
                fillOutExistingExitSurveyData(fileContents);
            }
        } else {
            discomfortButtonGroup = new ButtonGroup();
            fatigueButtonGroup = new ButtonGroup();
            difficultyButtonGroup = new ButtonGroup();
            
            ButtonGroup[] likertButtonGroups = new ButtonGroup[] {discomfortButtonGroup, fatigueButtonGroup, difficultyButtonGroup};
            
            // Window builder builds window using important fields here. It adds unimportant fields that we won't use for aesthetics only.
            WindowBuilder.buildExitSurveyWindow(frame, subjectIDTextField, surveyType, likertButtonGroups, saveButton);
            
            if(surveyFile != null) {
                fillOutExistingKeyboardSurveyData(fileContents);
            }
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
    
    private boolean saveSurvey() {
        String wildcardFileName = subjectIDTextField.getText() + "_" + FileName.EXIT_SURVEY.getName() +
                FileUtilities.WILDCARD + FileExt.FILE.getExt();
        File surveyFile;
        try {
            surveyFile = MyUtilities.FILE_IO_UTILITIES.openWildcardFile(FilePath.RECORDED_DATA.getPath() + subjectIDTextField.getText() + "/", wildcardFileName);
        } catch (IOException e) {
            System.err.println("Error occured when trying to open exit survey for subject " + subjectIDTextField.getText() + ". Creating new file.");
            surveyFile = null;
        }
        ArrayList<String> fileContents;
        if(surveyFile != null) {
            try {
                fileContents = MyUtilities.FILE_IO_UTILITIES.readListFromFile(surveyFile);
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Error occured when trying to read from file: " + surveyFile);
                fileContents = new ArrayList<String>();
            }
        } else {
            fileContents = new ArrayList<String>();
        }
        
        LinkedHashMap<String, String> surveyData = new LinkedHashMap<String, String>();
    	if(surveyType == null) {
    	    // Populate exitSurveyData.
    	    surveyData.put(ExitSurveyDataType.SUBJECT_ID.name(), subjectIDTextField.getText());
    	    surveyData.put(ExitSurveyDataType.AGE.name(), ageTextField.getText());
    	    surveyData.put(ExitSurveyDataType.GENDER.name(), getSelectedButtonText(genderButtonGroup));
    	    surveyData.put(ExitSurveyDataType.MAJOR.name(), majorTextField.getText());
    	    surveyData.put(ExitSurveyDataType.HAS_PERSONAL_COMPUTER.name(), getSelectedButtonText(hasComputerButtonGroup));
    	    surveyData.put(ExitSurveyDataType.HOURS_PER_WEEK_ON_COMPUTER.name(), getSelectedButtonText(computerHoursPerWeekButtonGroup));
    	    surveyData.put(ExitSurveyDataType.HAS_PREVIOUS_GESTURE_DEVICE_EXPERIENCE.name(), getSelectedButtonText(gestureDeviceExperienceButtonGroup));
            if(gestureDeviceExperienceTextField.isEditable() && gestureDeviceExperienceTextField.isEnabled()) {
                surveyData.put(ExitSurveyDataType.PREVIOUS_GESTURE_DEVICE_DESCRIPTION.name(), gestureDeviceExperienceTextField.getText());
            }
            surveyData.put(ExitSurveyDataType.HAS_PREVIOUS_TOUCH_DEVICE_EXPERIENCE.name(), getSelectedButtonText(touchDeviceExperienceButtonGroup));
            if(touchDeviceExperienceTextField.isEditable() && touchDeviceExperienceTextField.isEnabled()) {
                surveyData.put(ExitSurveyDataType.PREVIOUS_TOUCH_DEVICE_DESCRIPTION.name(), touchDeviceExperienceTextField.getText());
            }
            surveyData.put(ExitSurveyDataType.HAS_PREVIOUS_SWIPE_DEVICE_EXPERIENCE.name(), getSelectedButtonText(swipeDeviceExperienceButtonGroup));
            if(swipeDeviceExperienceTextField.isEditable() && swipeDeviceExperienceTextField.isEnabled()) {
                surveyData.put(ExitSurveyDataType.PREVIOUS_SWIPE_DEVICE_DESCRIPTION.name(), swipeDeviceExperienceTextField.getText());
            }
            surveyData.put(ExitSurveyDataType.HAS_PHYSICAL_IMPAIRMENT.name(), getSelectedButtonText(physicalImpairmentButtonGroup));
            if(physicalImpairmentTextField.isEditable() && physicalImpairmentTextField.isEnabled()) {
                surveyData.put(ExitSurveyDataType.PHYSICAL_INPAIRMENT_DESCRIPTION.name(), physicalImpairmentTextField.getText());
            }
            surveyData.put(ExitSurveyDataType.HANDEDNESS.name(), getSelectedButtonText(handednessButtonGroup));
            surveyData.put(ExitSurveyDataType.PREFERED_HANDEDNESS_FOR_EXPERIMENT.name(), getSelectedButtonText(preferedExperimentHandButtonGroup));
            
            // Survey section
            ArrayList<KeyboardType> detectedKeyboardTypes = getSurveyKeyboardsDetected(fileContents);
            for(Keyboard keyboard: Keyboard.values()) {
                KeyboardType keyboardType = keyboard.getType();
                if(keyboardType != KeyboardType.DISABLED) {
                    boolean detected = false;
                    for(KeyboardType detectedType: detectedKeyboardTypes) {
                        if(keyboardType == detectedType) {
                            detected = true;
                            break;
                        }
                    }
                    // If not detected, then fill out that they were not used in this experiment.
                    if(!detected) {
                        // discomfort
                        surveyData.put(keyboardType.getFileName() + "_" + ExitSurveyDataType.DISCOMFORT_LEVEL.name(),
                                ExitSurveyOptions.DID_NOT_USE.getDescription());
                        
                        // fatigue
                        surveyData.put(keyboardType.getFileName() + "_" + ExitSurveyDataType.FATIGUE_LEVEL.name(),
                                ExitSurveyOptions.DID_NOT_USE.getDescription());
                        
                        // difficulty
                        surveyData.put(keyboardType.getFileName() + "_" + ExitSurveyDataType.DIFFICULTY_LEVEL.name(),
                                ExitSurveyOptions.DID_NOT_USE.getDescription());
                    }
                }
            }
            
            // Rank section
            Iterator<JTextField> rankingTextFieldIterator = rankingTextFields.iterator();
            for(Keyboard keyboard: Keyboard.values()) {
                if(keyboard.getType() != KeyboardType.DISABLED && rankingTextFieldIterator.hasNext()) {
                    JTextField rankingTextField = rankingTextFieldIterator.next();
                    if(rankingTextField.isEnabled() && !rankingTextField.getText().trim().equals("")) {
                        // used ranking
                        surveyData.put(keyboard.getKeyboard().getFileName() + "_" + ExitSurveyDataType.PREFERENCE_RANKING.name(),
                                rankingTextField.getText());
                    } else {
                        // unused ranking
                        surveyData.put(keyboard.getKeyboard().getFileName() + "_" + ExitSurveyDataType.PREFERENCE_RANKING.name(),
                                ExitSurveyOptions.DID_NOT_USE.getDescription());
                    }
                }
            }
    	} else {
    	    // Populate exitSurveyData.
    	    surveyData.put(ExitSurveyDataType.SUBJECT_ID.name(), subjectIDTextField.getText());

            // discomfort
    	    surveyData.put(surveyType.getFileName() + "_" + ExitSurveyDataType.DISCOMFORT_LEVEL.name(),
                    getSelectedButtonText(discomfortButtonGroup));
            
            // fatigue
    	    surveyData.put(surveyType.getFileName() + "_" + ExitSurveyDataType.FATIGUE_LEVEL.name(),
                    getSelectedButtonText(fatigueButtonGroup));
            
            // difficulty
    	    surveyData.put(surveyType.getFileName() + "_" + ExitSurveyDataType.DIFFICULTY_LEVEL.name(),
                    getSelectedButtonText(difficultyButtonGroup));
    	}
    	
    	// Write to file.
        String time = "_";
        time += LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        time += "_" + LocalTime.now().format(DateTimeFormatter.ofPattern("kkmm"));
        String filePath = FilePath.RECORDED_DATA.getPath() + subjectIDTextField.getText() + "/";
        String fileName = subjectIDTextField.getText() + "_" + FileName.EXIT_SURVEY.getName() + time + FileExt.FILE.getExt();
    	try {
            ArrayList<String> formattedSurveyData = new ArrayList<String>();
    	    if(surveyFile != null) {
                LinkedHashMap<String, String> previousSurveyData = parseSurveyFileContents(fileContents);
                // Update previous survey data.
                for(Entry<String, String> entry: surveyData.entrySet()) {
                    previousSurveyData.put(entry.getKey(), entry.getValue());
                }
                // Format survey data.
                for(Entry<String, String> entry: previousSurveyData.entrySet()) {
                    formattedSurveyData.add(entry.getKey() + ": " + entry.getValue());
                }
                MyUtilities.FILE_IO_UTILITIES.writeListToFile(new ArrayList<String>(formattedSurveyData), surveyFile, false);
                surveyFile.renameTo(new File(filePath + fileName));
    	    } else {
    	        // Format new survey data.
                for(Entry<String, String> entry: surveyData.entrySet()) {
                    formattedSurveyData.add(entry.getKey() + ": " + entry.getValue());
                }
                MyUtilities.FILE_IO_UTILITIES.writeListToFile(new ArrayList<String>(formattedSurveyData), filePath, fileName, false);
    	    }
			isSurveySaved = true;
		} catch (IOException e) {
			System.out.println("An error occured while trying to save the survey.");
			e.printStackTrace();
		}
    	return isSurveySaved;
    }
    
    private boolean isFilledOut() {
        if(surveyType == null) {
            ArrayList<JTextField> textFields = new ArrayList<JTextField>();
            textFields.add(ageTextField);
            textFields.add(majorTextField);
            textFields.add(physicalImpairmentTextField);
            textFields.add(gestureDeviceExperienceTextField);
            textFields.add(touchDeviceExperienceTextField);
            textFields.add(swipeDeviceExperienceTextField);
            textFields.addAll(rankingTextFields);
            
            ButtonGroup[] buttonGroups = {genderButtonGroup, hasComputerButtonGroup, computerHoursPerWeekButtonGroup, handednessButtonGroup, preferedExperimentHandButtonGroup,
                    physicalImpairmentButtonGroup, gestureDeviceExperienceButtonGroup, touchDeviceExperienceButtonGroup, swipeDeviceExperienceButtonGroup};
            
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
        } else {
            ButtonGroup[] buttonGroups = {discomfortButtonGroup, fatigueButtonGroup, difficultyButtonGroup};
            
            // Check button groups
            for(ButtonGroup buttonGroup: buttonGroups) {
                if(getSelectedButtonText(buttonGroup) == null) {
                    return false;
                }
            }
        }
    	return true;
    }
    
    private void fillOutExistingExitSurveyData(ArrayList<String> fileContents) {
        LinkedHashMap<String, String> parsedSurveyFileContents = parseSurveyFileContents(fileContents);
        for(Entry<String, String> entry: parsedSurveyFileContents.entrySet()) {
            ExitSurveyDataType dataType = ExitSurveyDataType.getByName(entry.getKey());
            switch(dataType) {
                case AGE:
                    ageTextField.setText(entry.getValue());
                    break;
                case GENDER:
                    for (Enumeration<AbstractButton> buttons = genderButtonGroup.getElements(); buttons.hasMoreElements();) {
                        AbstractButton button = buttons.nextElement();
                        ExitSurveyOptions buttonOption = ExitSurveyOptions.getByDescriptionExact(button.getText());
                        ExitSurveyOptions dataOption = ExitSurveyOptions.getByDescriptionExact(entry.getValue());
                        if(dataOption == buttonOption) {
                            button.doClick();
                            break;
                        }
                    }
                    break;
                case MAJOR:
                    majorTextField.setText(entry.getValue());
                    break;
                case HAS_PERSONAL_COMPUTER:
                    for (Enumeration<AbstractButton> buttons = hasComputerButtonGroup.getElements(); buttons.hasMoreElements();) {
                        AbstractButton button = buttons.nextElement();
                        ExitSurveyOptions buttonOption = ExitSurveyOptions.getByDescriptionExact(button.getText());
                        ExitSurveyOptions dataOption = ExitSurveyOptions.getByDescriptionExact(entry.getValue());
                        if(dataOption == buttonOption) {
                            button.doClick();
                            break;
                        }
                    }
                    break;
                case HOURS_PER_WEEK_ON_COMPUTER:
                    for (Enumeration<AbstractButton> buttons = computerHoursPerWeekButtonGroup.getElements(); buttons.hasMoreElements();) {
                        AbstractButton button = buttons.nextElement();
                        ExitSurveyOptions buttonOption = ExitSurveyOptions.getByDescriptionPartial(button.getText());
                        ExitSurveyOptions dataOption = ExitSurveyOptions.getByDescriptionPartial(entry.getValue());
                        if(dataOption == buttonOption) {
                            button.doClick();
                            break;
                        }
                    }
                    break;
                case HAS_PREVIOUS_GESTURE_DEVICE_EXPERIENCE:
                    for (Enumeration<AbstractButton> buttons = gestureDeviceExperienceButtonGroup.getElements(); buttons.hasMoreElements();) {
                        AbstractButton button = buttons.nextElement();
                        ExitSurveyOptions buttonOption = ExitSurveyOptions.getByDescriptionExact(button.getText());
                        ExitSurveyOptions dataOption = ExitSurveyOptions.getByDescriptionExact(entry.getValue());
                        if(dataOption == buttonOption) {
                            button.doClick();
                            break;
                        }
                    }
                    break;
                case PREVIOUS_GESTURE_DEVICE_DESCRIPTION:
                    gestureDeviceExperienceTextField.setText(entry.getValue());
                    break;
                case HAS_PREVIOUS_TOUCH_DEVICE_EXPERIENCE:
                    for (Enumeration<AbstractButton> buttons = touchDeviceExperienceButtonGroup.getElements(); buttons.hasMoreElements();) {
                        AbstractButton button = buttons.nextElement();
                        ExitSurveyOptions buttonOption = ExitSurveyOptions.getByDescriptionExact(button.getText());
                        ExitSurveyOptions dataOption = ExitSurveyOptions.getByDescriptionExact(entry.getValue());
                        if(dataOption == buttonOption) {
                            button.doClick();
                            break;
                        }
                    }
                    break;
                case PREVIOUS_TOUCH_DEVICE_DESCRIPTION:
                    touchDeviceExperienceTextField.setText(entry.getValue());
                    break;
                case HAS_PREVIOUS_SWIPE_DEVICE_EXPERIENCE:
                    for (Enumeration<AbstractButton> buttons = swipeDeviceExperienceButtonGroup.getElements(); buttons.hasMoreElements();) {
                        AbstractButton button = buttons.nextElement();
                        ExitSurveyOptions buttonOption = ExitSurveyOptions.getByDescriptionExact(button.getText());
                        ExitSurveyOptions dataOption = ExitSurveyOptions.getByDescriptionExact(entry.getValue());
                        if(dataOption == buttonOption) {
                            button.doClick();
                            break;
                        }
                    }
                    break;
                case PREVIOUS_SWIPE_DEVICE_DESCRIPTION:
                    swipeDeviceExperienceTextField.setText(entry.getValue());
                    break;
                case HAS_PHYSICAL_IMPAIRMENT:
                    for (Enumeration<AbstractButton> buttons = physicalImpairmentButtonGroup.getElements(); buttons.hasMoreElements();) {
                        AbstractButton button = buttons.nextElement();
                        ExitSurveyOptions buttonOption = ExitSurveyOptions.getByDescriptionExact(button.getText());
                        ExitSurveyOptions dataOption = ExitSurveyOptions.getByDescriptionExact(entry.getValue());
                        if(dataOption == buttonOption) {
                            button.doClick();
                            break;
                        }
                    }
                    break;
                case PHYSICAL_INPAIRMENT_DESCRIPTION:
                    physicalImpairmentTextField.setText(entry.getValue());
                    break;
                case HANDEDNESS:
                    for (Enumeration<AbstractButton> buttons = handednessButtonGroup.getElements(); buttons.hasMoreElements();) {
                        AbstractButton button = buttons.nextElement();
                        ExitSurveyOptions buttonOption = ExitSurveyOptions.getByDescriptionPartial(button.getText());
                        ExitSurveyOptions dataOption = ExitSurveyOptions.getByDescriptionPartial(entry.getValue());
                        if(dataOption == buttonOption) {
                            button.doClick();
                            break;
                        }
                    }
                    break;
                case PREFERED_HANDEDNESS_FOR_EXPERIMENT:
                    for (Enumeration<AbstractButton> buttons = preferedExperimentHandButtonGroup.getElements(); buttons.hasMoreElements();) {
                        AbstractButton button = buttons.nextElement();
                        ExitSurveyOptions buttonOption = ExitSurveyOptions.getByDescriptionPartial(button.getText());
                        ExitSurveyOptions dataOption = ExitSurveyOptions.getByDescriptionPartial(entry.getValue());
                        if(dataOption == buttonOption) {
                            button.doClick();
                            break;
                        }
                    }
                    break;
                case PREFERENCE_RANKING:
                    if(ExitSurveyOptions.getByDescriptionExact(entry.getValue()) != ExitSurveyOptions.DID_NOT_USE) {
                        String[] details = entry.getKey().split("_", 2);
                        if(details.length > 1) {
                            int keyboardIndex = 0;
                            for(Keyboard keyboard: Keyboard.values()) {
                                KeyboardType keyboardType = keyboard.getType();
                                if(keyboardType != KeyboardType.DISABLED && keyboardType == KeyboardType.getByName(details[0])) {
                                    rankingTextFields.get(keyboardIndex).setText(entry.getValue());
                                    break;
                                }
                                if(keyboardType != KeyboardType.DISABLED) {
                                    keyboardIndex++;
                                }
                            }
                        }
                    }
                    break;
                default: break;
            }
        }
    }
    
    private void fillOutExistingKeyboardSurveyData(ArrayList<String> fileContents) {
        LinkedHashMap<String, String> parsedSurveyFileContents = parseSurveyFileContents(fileContents);
        for(Entry<String, String> entry: parsedSurveyFileContents.entrySet()) {
            ExitSurveyDataType dataType = ExitSurveyDataType.getByName(entry.getKey());
            switch(dataType) {
                case DISCOMFORT_LEVEL:
                    if(ExitSurveyOptions.getByDescriptionExact(entry.getValue()) != ExitSurveyOptions.DID_NOT_USE) {
                        String[] details = entry.getKey().split("_", 2);
                        if(details.length > 1 && surveyType == KeyboardType.getByName(details[0])) {
                            for (Enumeration<AbstractButton> buttons = discomfortButtonGroup.getElements(); buttons.hasMoreElements();) {
                                AbstractButton button = buttons.nextElement();
                                ExitSurveyOptions buttonOption = ExitSurveyOptions.getByDescriptionExact(button.getText());
                                ExitSurveyOptions dataOption = ExitSurveyOptions.getByDescriptionExact(entry.getValue());
                                if(dataOption == buttonOption) {
                                    button.doClick();
                                    break;
                                }
                            }
                        }
                    }
                    break;
                case FATIGUE_LEVEL:
                    if(ExitSurveyOptions.getByDescriptionExact(entry.getValue()) != ExitSurveyOptions.DID_NOT_USE) {
                        String[] details = entry.getKey().split("_", 2);
                        if(details.length > 1 && surveyType == KeyboardType.getByName(details[0])) {
                            for (Enumeration<AbstractButton> buttons = fatigueButtonGroup.getElements(); buttons.hasMoreElements();) {
                                AbstractButton button = buttons.nextElement();
                                ExitSurveyOptions buttonOption = ExitSurveyOptions.getByDescriptionExact(button.getText());
                                ExitSurveyOptions dataOption = ExitSurveyOptions.getByDescriptionExact(entry.getValue());
                                if(dataOption == buttonOption) {
                                    button.doClick();
                                    break;
                                }
                            }
                        }
                    }
                    break;
                case DIFFICULTY_LEVEL:
                    if(ExitSurveyOptions.getByDescriptionExact(entry.getValue()) != ExitSurveyOptions.DID_NOT_USE) {
                        String[] details = entry.getKey().split("_", 2);
                        if(details.length > 1 && surveyType == KeyboardType.getByName(details[0])) {
                            for (Enumeration<AbstractButton> buttons = difficultyButtonGroup.getElements(); buttons.hasMoreElements();) {
                                AbstractButton button = buttons.nextElement();
                                ExitSurveyOptions buttonOption = ExitSurveyOptions.getByDescriptionExact(button.getText());
                                ExitSurveyOptions dataOption = ExitSurveyOptions.getByDescriptionExact(entry.getValue());
                                if(dataOption == buttonOption) {
                                    button.doClick();
                                    break;
                                }
                            }
                        }
                    }
                    break;
                default: break;
            }
        }
    }
    
    private ArrayList<KeyboardType> getSurveyKeyboardsDetected(ArrayList<String> fileContents) {
        Set<KeyboardType> keyboardTypes = new HashSet<KeyboardType>();
        for(String line: fileContents) {
            String[] parts = line.split(": ", 2);
            if(ExitSurveyDataType.getByName(parts[0]) != ExitSurveyDataType.PREFERENCE_RANKING &&
                    ExitSurveyOptions.getByDescriptionExact(parts[1]) != ExitSurveyOptions.DID_NOT_USE) {
                String[] details = parts[0].split("_", 2);
                if(details.length > 1) {
                    for(Keyboard keyboard: Keyboard.values()) {
                        KeyboardType keyboardType = keyboard.getType();
                        if(keyboardType != KeyboardType.DISABLED && keyboardType == KeyboardType.getByName(details[0])) {
                            keyboardTypes.add(keyboardType);
                            break;
                        }
                    }
                }
            }
        }
        return new ArrayList<KeyboardType>(keyboardTypes);
    }
    
    private LinkedHashMap<String, String> parseSurveyFileContents(ArrayList<String> fileContents) {
        LinkedHashMap<String, String> parsedFileContents = new LinkedHashMap<String, String>();
        for(String line: fileContents) {
            String[] parts = line.split(": ", 2);
            parsedFileContents.put(parts[0], parts[1]);
        }
        return parsedFileContents;
    }

    @Override
    protected void disable() {
        frame.setVisible(false);
        isEnabled = false;
        frame.dispose();
    }
    
    @Override
    public void enable() {
        if(frame != null) {
            frame.setTitle("Exit Survey - Subject ID: " + subjectIDTextField.getText());
        }
        subjectIDTextField.setEnabled(false);
        frame.setVisible(true);
        frame.requestFocusInWindow();
        isEnabled = true;
    }
    
    private JRadioButton getYesRadioButton(ButtonGroup group) {
        for (Enumeration<AbstractButton> e = group.getElements(); e.hasMoreElements();) {
        	JRadioButton radioButton = (JRadioButton) e.nextElement();
        	ExitSurveyOptions option = ExitSurveyOptions.getByDescriptionPartial(radioButton.getText());
        	if (option == ExitSurveyOptions.YES) {
        		return radioButton;
        	}
        }
        return null;
    }
    
    private String getSelectedButtonText(ButtonGroup buttonGroup) {
        for (Enumeration<AbstractButton> buttons = buttonGroup.getElements(); buttons.hasMoreElements();) {
            AbstractButton button = buttons.nextElement();
            if (button.isSelected()) {
                return button.getText();
            }
        }
        return null;
    }
    
    @SuppressWarnings("serial")
    private class NumericOnlyDocument extends PlainDocument {
        @Override
        public void insertString(int offset, String str, AttributeSet a)
                throws BadLocationException {
            Key key = Key.getByValue(str.charAt(str.length() - 1));
            if(key != null && key.isNumeric()) {
                super.insertString(offset, str, a);
            }
        }
    }
    
    @SuppressWarnings("serial")
    private class UniqueRankingDocument extends PlainDocument {
        private final JTextField RANKING_FIELD;
        
        public UniqueRankingDocument(JTextField rankingField) {
            super();
            RANKING_FIELD = rankingField;
        }
        
        @Override
        public void insertString(int offset, String str, AttributeSet a)
                throws BadLocationException {
            Key key = Key.getByValue(str.charAt(str.length() - 1));
            if(key != null && key.isNumeric() &&
                    MIN_RANKING <= Character.getNumericValue(key.getValue()) && Character.getNumericValue(key.getValue()) <= maxRanking) {
                for(JTextField rankingTextField: rankingTextFields) {
                    if(!RANKING_FIELD.equals(rankingTextField) && rankingTextField.getText().length() > 0
                            && rankingTextField.getText().charAt(0) == key.getValue()) {
                        rankingTextField.setText("");
                        break;
                    }
                }
                if(RANKING_FIELD.getText().length() > 0) {
                    RANKING_FIELD.setText("");
                }
                super.insertString(0, str, a);
            }
        }
    }
}

package ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
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
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import javax.swing.text.AttributeSet;

import utilities.MyUtilities;
import enums.ExitSurveyDataType;
import enums.ExitSurveyOptions;
import enums.FileExt;
import enums.FilePath;
import enums.Key;
import enums.KeyboardType;

public class ExitSurveyController extends GraphicsController {
    // Static text fields and button groups
    private final int MIN_RANKING = 1;
    private int maxRanking = 0;
    private int questionIndex = 0;
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
    
    // Dynamic text fields and button groups
    private ArrayList<ButtonGroup> discomfortButtonGroups = new ArrayList<ButtonGroup>();
    private ArrayList<ButtonGroup> fatigueButtonGroups = new ArrayList<ButtonGroup>();
    private ArrayList<ButtonGroup> difficultyButtonGroups = new ArrayList<ButtonGroup>();
    private ArrayList<JTextField> rankingTextFields = new ArrayList<JTextField>();
    private ArrayList<ButtonGroup> usedButtonGroups = new ArrayList<ButtonGroup>();
    
    // Other
    JTextArea rankingQuestion;
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
        rankingQuestion = new JTextArea();
        saveButton = new JButton("Save Survey");
        
        JTextField[] subjectTextFields = {subjectIDTextField, ageTextField, majorTextField};
        JTextField[] historyTextFields = {physicalImpairmentTextField, gestureDeviceExperienceTextField, touchDeviceExperienceTextField, swipeDeviceExperienceTextField};
        ButtonGroup[] subjectButtonGroups = {genderButtonGroup, hasComputerButtonGroup, computerHoursPerWeekButtonGroup, handednessButtonGroup, preferedExperimentHandButtonGroup};
        ButtonGroup[] historyButtonGroups = {physicalImpairmentButtonGroup, gestureDeviceExperienceButtonGroup, touchDeviceExperienceButtonGroup, swipeDeviceExperienceButtonGroup};
        
        for(KeyboardType keyboardType: KeyboardType.values()) {
            if(keyboardType != KeyboardType.STANDARD) {
                usedButtonGroups.add(new ButtonGroup());
                rankingTextFields.add(new JTextField(3));
                discomfortButtonGroups.add(new ButtonGroup());
                fatigueButtonGroups.add(new ButtonGroup());
                difficultyButtonGroups.add(new ButtonGroup());
            }
        }
        
        // Window builder builds window using important fields here. It adds unimportant fields that we won't use for aesthetics only.
        questionIndex = WindowBuilder.buildExitSurveyWindow(frame,
                subjectTextFields, historyTextFields, rankingTextFields,
                subjectButtonGroups, historyButtonGroups, discomfortButtonGroups, fatigueButtonGroups, difficultyButtonGroups,
                usedButtonGroups, maxRanking, rankingQuestion,
                saveButton);
        
        ageTextField.setDocument(new NumericOnlyDocument());
        for(JTextField rankingTextField: rankingTextFields) {
            rankingTextField.setDocument(new UniqueRankingDocument(rankingTextField));
        }
        
        for(int i = 0; i < usedButtonGroups.size(); i++) {
            final int index = i;
            JRadioButton usedRadioButton = getYesRadioButton(usedButtonGroups.get(i));
            if(usedRadioButton != null) {
                usedRadioButton.addItemListener(new ItemListener() {
                    @Override
                    public void itemStateChanged(ItemEvent e) {
                        if(usedRadioButton.isSelected()) {
                            // enable everything
                            for (Enumeration<AbstractButton> buttons = discomfortButtonGroups.get(index).getElements(); buttons.hasMoreElements();) {
                                AbstractButton button = buttons.nextElement();
                                button.setEnabled(true);
                            }
                            for (Enumeration<AbstractButton> buttons = fatigueButtonGroups.get(index).getElements(); buttons.hasMoreElements();) {
                                AbstractButton button = buttons.nextElement();
                                button.setEnabled(true);
                            }
                            for (Enumeration<AbstractButton> buttons = difficultyButtonGroups.get(index).getElements(); buttons.hasMoreElements();) {
                                AbstractButton button = buttons.nextElement();
                                button.setEnabled(true);
                            }
                            rankingTextFields.get(index).setEditable(true);
                            maxRanking++;
                            rankingQuestion.setText(questionIndex + ". Please rank the keyboards from most preferred (1), to least preferred ("+ maxRanking + ").");
                        } else {
                            // disable everything
                            discomfortButtonGroups.get(index).clearSelection();
                            fatigueButtonGroups.get(index).clearSelection();
                            difficultyButtonGroups.get(index).clearSelection();
                            for (Enumeration<AbstractButton> buttons = discomfortButtonGroups.get(index).getElements(); buttons.hasMoreElements();) {
                                AbstractButton button = buttons.nextElement();
                                button.setEnabled(false);
                            }
                            for (Enumeration<AbstractButton> buttons = fatigueButtonGroups.get(index).getElements(); buttons.hasMoreElements();) {
                                AbstractButton button = buttons.nextElement();
                                button.setEnabled(false);
                            }
                            for (Enumeration<AbstractButton> buttons = difficultyButtonGroups.get(index).getElements(); buttons.hasMoreElements();) {
                                AbstractButton button = buttons.nextElement();
                                button.setEnabled(false);
                            }
                            if(!rankingTextFields.get(index).getText().isEmpty()) {
                                int rank = Integer.parseInt(rankingTextFields.get(index).getText());
                                rankingTextFields.get(index).setText("");
                                for(JTextField rankingTextField: rankingTextFields) {
                                    if(!rankingTextField.getText().isEmpty()) {
                                        int otherRank = Integer.parseInt(rankingTextField.getText());
                                        if(otherRank > rank) {
                                            rankingTextField.setText("");
                                        }
                                    }
                                }
                                
                            } else {
                                rankingTextFields.get(index).setText("");
                            }
                            rankingTextFields.get(index).setEditable(false);
                            maxRanking--;
                            rankingQuestion.setText(questionIndex + ". Please rank the keyboards from most preferred (1), to least preferred ("+ maxRanking + ").");
                        }
                    }
                });
            }
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
        for(KeyboardType keyboardType: KeyboardType.values()) {
            if(keyboardType != KeyboardType.STANDARD) {
                JRadioButton radioButton = getYesRadioButton(usedButtonGroups.get(keyboardType.getID() - 1));
                if(radioButton.isSelected()) {
                    // discomfort
                    exitSurveyData.add(keyboardType.getFileName() + ExitSurveyDataType._DISCOMFORT_LEVEL.name() + ": "
                            + getSelectedButtonText(discomfortButtonGroups.get(keyboardType.getID() - 1)));
                    
                    // fatigue
                    exitSurveyData.add(keyboardType.getFileName() + ExitSurveyDataType._FATIGUE_LEVEL.name() + ": "
                            + getSelectedButtonText(fatigueButtonGroups.get(keyboardType.getID() - 1)));
                    
                    // difficulty
                    exitSurveyData.add(keyboardType.getFileName() + ExitSurveyDataType._DIFFICULTY_LEVEL.name() + ": "
                            + getSelectedButtonText(difficultyButtonGroups.get(keyboardType.getID() - 1)));
                } else {
                    // discomfort
                    exitSurveyData.add(keyboardType.getFileName() + ExitSurveyDataType._DISCOMFORT_LEVEL.name() + ": "
                            + ExitSurveyOptions.DID_NOT_USE.getDescription());
                    
                    // fatigue
                    exitSurveyData.add(keyboardType.getFileName() + ExitSurveyDataType._FATIGUE_LEVEL.name() + ": "
                            + ExitSurveyOptions.DID_NOT_USE.getDescription());
                    
                    // difficulty
                    exitSurveyData.add(keyboardType.getFileName() + ExitSurveyDataType._DIFFICULTY_LEVEL.name() + ": "
                            + ExitSurveyOptions.DID_NOT_USE.getDescription());
                }
            }
        }
    	
    	// Rank section
        for(KeyboardType keyboardType: KeyboardType.values()) {
            if(keyboardType != KeyboardType.STANDARD) {
                JRadioButton radioButton = getYesRadioButton(usedButtonGroups.get(keyboardType.getID() - 1));
                if(radioButton.isSelected()) {
                    // ranking
                    exitSurveyData.add(keyboardType.getFileName() + ExitSurveyDataType._PREFERENCE_RANKING.name() + ": "
                            + rankingTextFields.get(keyboardType.getID() - 1).getText());
                } else {
                    // ranking
                    exitSurveyData.add(keyboardType.getFileName() + ExitSurveyDataType._PREFERENCE_RANKING.name() + ": "
                            + ExitSurveyOptions.DID_NOT_USE.getDescription());
                }
            }
        }
    	
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
        ArrayList<JTextField> textFields = new ArrayList<JTextField>();
        textFields.add(subjectIDTextField);
        textFields.add(subjectIDTextField);
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
        
        // Check special button groups
        for(int i = 0; i < usedButtonGroups.size(); i++) {
            if(getSelectedButtonText(usedButtonGroups.get(i)) == null) {
                return false;
            } else {
                JRadioButton radioButton = getYesRadioButton(usedButtonGroups.get(i));
                if(radioButton.isSelected()) {
                    if(getSelectedButtonText(discomfortButtonGroups.get(i)) == null ||
                            getSelectedButtonText(fatigueButtonGroups.get(i)) == null ||
                            getSelectedButtonText(difficultyButtonGroups.get(i)) == null) {
                        return false;
                    }
                }
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
    
    public JRadioButton getYesRadioButton(ButtonGroup group) {
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
                boolean unique = true;
                for(JTextField jtf: rankingTextFields) {
                    if(!RANKING_FIELD.equals(jtf) && jtf.getText().length() > 0 && jtf.getText().charAt(0) == key.getValue()) {
                        unique = false;
                    }
                }
                if(unique) {
                    if(RANKING_FIELD.getText().length() > 0) {
                        RANKING_FIELD.setText("");
                    }
                    super.insertString(0, str, a);
                }
            }
        }
    }
}

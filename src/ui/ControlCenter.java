/*
 * 
 */

package ui;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.PlainDocument;

import dictionary.DictionaryBuilder;
import utilities.FileUtilities;
import utilities.MyUtilities;
import enums.FileExt;
import enums.FilePath;
import enums.Key;
import enums.KeyboardType;

public class ControlCenter {
    // Constants
    private final int SUBJECT_ID_SIZE = 8;
    private final ReentrantLock CONTROL_LOCK = new ReentrantLock();
    private final ReentrantLock RUNNING_LOCK = new ReentrantLock();
    private final ExperimentController EXPERIMENT_CONTROLLER = new ExperimentController();
    private CalibrationController calibrationController;// = new CalibrationController();
    private ExitSurveyController exitSurveyController;
    private DictionaryBuilder dictionaryBuilder;
    private DataCenterController dataCenterController;
    
    // Not Constants
    private JFrame frame;
    private JTextField subjectField;
    private String subjectID;
    private JComboBox<String> testTypeComboBox;
    private JLabel testsFinishedLabel;
    private JButton editSubjectIDButton;
    private JButton randomizeSubjectIDButton;
    private JButton browseSubjectIDButton;
    private JButton calibrateButton;
    private JButton experimentButton;
    private JButton exitSurveyButton;
    private JButton likertSurveyButton;
    private JButton createDictionariesButton;
    private JButton dataCenterButton;
    private boolean isDisabled = false;
    private boolean isRunning = true;
    
    @SuppressWarnings("unchecked")
    public ControlCenter() {
        // Java Swing/AWT important fields and selections
        frame = new JFrame("Experiment Control Center");
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        subjectID = generateSubjectID();
        subjectField = new JTextField(subjectID, 10);
        testTypeComboBox = new JComboBox<String>();
        testsFinishedLabel = new JLabel();
        editSubjectIDButton = new JButton("Edit");
        randomizeSubjectIDButton = new JButton("Randomize");
        browseSubjectIDButton = new JButton("Browse...");
        calibrateButton = new JButton("Calibration Center");
        experimentButton = new JButton("Experiment");
        exitSurveyButton = new JButton("Exit Survey");
        likertSurveyButton = new JButton("Likert Survey");
        createDictionariesButton = new JButton("Create Dictionaries");
        dataCenterButton = new JButton("Data Center");
        
        JButton[] buttons = {calibrateButton, experimentButton, editSubjectIDButton,
        		exitSurveyButton, createDictionariesButton, dataCenterButton,
        		likertSurveyButton, randomizeSubjectIDButton, browseSubjectIDButton};
        
        // Window builder builds window using important fields here. It adds unimportant fields that we won't use for aesthetics only.
        WindowBuilder.buildControlWindow(frame, testTypeComboBox, subjectField, buttons, testsFinishedLabel);
        testTypeComboBox.setRenderer(new ComboBoxRenderer(testTypeComboBox));
        subjectField.setDocument(new AlphaNumericOnlyDocument());
        subjectField.setText(subjectID);
        frame.setVisible(true);
        
        // by default, an AWT Frame doesn't do anything when you click
        // the close button; this bit of code will terminate the program when
        // the window is asked to close
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                if(!experimentInProgress() && !exitSurveyInProgress() && !dictionaryBuildInProgress()) {
                    frame.dispose();
                    exit();
                } else if(experimentInProgress()){
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
                        exit();
                    }
                } else if(exitSurveyInProgress()) {
                    Object[] options = {"Yes", "Cancel"};
                    int selection =
                            JOptionPane.showOptionDialog(frame,
                            "An exit survey is currently being filled out. If you close now, data will be lost.\nClose anyway?",
                            "Warning!",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE,
                            null,
                            options,
                            options[0]);
                    if(selection == JOptionPane.YES_OPTION) {
                        frame.dispose();
                        exit();
                    }
                } else if(dictionaryBuildInProgress()) {
                    JOptionPane.showMessageDialog(frame,
                            "You must wait until the dictionary is built to exit.",
                            "Error!",
                            JOptionPane.ERROR_MESSAGE,
                            null);
                } else if(dataFormattingInProgress()) {
                    JOptionPane.showMessageDialog(frame,
                            "You must wait until the data is finished formatting to exit.",
                            "Error!",
                            JOptionPane.ERROR_MESSAGE,
                            null);
                }
            }
        });
        
        // This triggers the prompt for changing the subjectID
        subjectField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                editSubjectIDButton.doClick();
            }
        });
        
        // Edit the subject ID if we want to redo a test or add to an old test.
        editSubjectIDButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CONTROL_LOCK.lock();
                try {
                    if(!isInProgress()) {
                        if(editSubjectIDButton.getText().equals("Edit")) {
                            subjectField.setFocusable(true);
                            subjectField.setEditable(true);
                            subjectField.setHighlighter(new DefaultHighlighter());
                            subjectField.requestFocusInWindow();
                            subjectField.selectAll();
                            editSubjectIDButton.setText("Save");
                            disableUI(false);
                        } else if(subjectField.getText().length() != SUBJECT_ID_SIZE) {
                            JOptionPane.showMessageDialog(frame,
                                    "The subject ID must be 8 character long.",
                                    "Error!",
                                    JOptionPane.ERROR_MESSAGE,
                                    null);
                        } else if(!checkForUniqueSubjectID(subjectField.getText())) {
                            Object[] options = {"Save", "Randomize", "Cancel"};
                            int selection =
                                    JOptionPane.showOptionDialog(frame,
                                    "A duplicate subjectID was selected.\nContinue anyway?",
                                    "Warning!",
                                    JOptionPane.YES_NO_CANCEL_OPTION,
                                    JOptionPane.WARNING_MESSAGE,
                                    null,
                                    options,
                                    options[0]);
                            if(selection == JOptionPane.YES_OPTION) {
                                subjectField.setEditable(false);
                                subjectField.setHighlighter(null);
                                editSubjectIDButton.setText("Edit");
                                subjectID = subjectField.getText();
                                ((ComboBoxRenderer) testTypeComboBox.getRenderer()).updateSubjectID(testTypeComboBox, subjectID);
                            } else if(selection == JOptionPane.NO_OPTION) {
                                subjectField.setEditable(false);
                                subjectField.setHighlighter(null);
                                editSubjectIDButton.setText("Edit");
                                subjectID = generateSubjectID();
                                subjectField.setText(subjectID);
                                ((ComboBoxRenderer) testTypeComboBox.getRenderer()).updateSubjectID(testTypeComboBox, subjectID);
                            } else {
                                subjectField.setEditable(false);
                                subjectField.setHighlighter(null);
                                editSubjectIDButton.setText("Edit");
                                subjectField.setText(subjectID);
                            }
                            subjectField.setFocusable(false);
                            enableUI();
                        } else {
                            subjectField.setEditable(false);
                            subjectField.setHighlighter(null);
                            editSubjectIDButton.setText("Edit");
                            subjectID = subjectField.getText();
                            ((ComboBoxRenderer) testTypeComboBox.getRenderer()).updateSubjectID(testTypeComboBox, subjectID);
                            subjectField.setFocusable(false);
                            enableUI();
                        }
                    }
                } finally {
                    CONTROL_LOCK.unlock();
                }
            }
        });
        
        randomizeSubjectIDButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
                CONTROL_LOCK.lock();
                try {
                    subjectID = generateSubjectID();
                    subjectField.setText(subjectID);
                    ((ComboBoxRenderer) testTypeComboBox.getRenderer()).updateSubjectID(testTypeComboBox, subjectID);
                    if(((ComboBoxRenderer) testTypeComboBox.getRenderer()).dataExists(testTypeComboBox.getSelectedItem().toString())) {
                        likertSurveyButton.setEnabled(true);
                    } else {
                        likertSurveyButton.setEnabled(false);
                    }
	            } finally {
	                CONTROL_LOCK.unlock();
	            }
			}
        });
        
        browseSubjectIDButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
                CONTROL_LOCK.lock();
                try {
                    // needs to prompt the file chooser
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setCurrentDirectory(new File(FilePath.RECORDED_DATA.getPath()));
                    fileChooser.setDialogTitle("Choose Data Folder...");
                    fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    fileChooser.setAcceptAllFileFilterUsed(false);
                    if(fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                        File selectedFile = fileChooser.getSelectedFile();
                        if(selectedFile != null) {
                        	subjectID = selectedFile.getName();
                            subjectField.setText(subjectID);
                            ((ComboBoxRenderer) testTypeComboBox.getRenderer()).updateSubjectID(testTypeComboBox, subjectID);
                            if(((ComboBoxRenderer) testTypeComboBox.getRenderer()).dataExists(testTypeComboBox.getSelectedItem().toString())) {
                                likertSurveyButton.setEnabled(true);
                            } else {
                                likertSurveyButton.setEnabled(false);
                            }
                        }
                    } else {
                        // canceled, do nothing
                    }
                    fileChooser = null;
	            } finally {
	                CONTROL_LOCK.unlock();
	            }
			}
        });
        
        testTypeComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                if(((ComboBoxRenderer) testTypeComboBox.getRenderer()).dataExists(testTypeComboBox.getSelectedItem().toString())) {
                    likertSurveyButton.setEnabled(true);
                } else {
                    likertSurveyButton.setEnabled(false);
                }
            }
        });
        
        // RUN THE DICTIONARY RECOGNIZER / BUILDER
        createDictionariesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                CONTROL_LOCK.lock();
                try {
                    if(!isInProgress()) {
                        dictionaryBuilder = new DictionaryBuilder();
                        disableUI(true);
                    }
                } finally {
                    CONTROL_LOCK.unlock();
                }
            }
        });
        
        // RUN THE DATA FORMATTER
        dataCenterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                CONTROL_LOCK.lock();
                try {
                    if(!isInProgress()) {
                        dataCenterController = new DataCenterController();
                        dataCenterController.enable();
                        disableUI(true);
                    }
                } finally {
                    CONTROL_LOCK.unlock();
                }
            }
        });
        
        // RUN EXIT SURVEY CONTROLLER
        exitSurveyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CONTROL_LOCK.lock();
                try {
                    if(!isInProgress()) {
                    	exitSurveyController = new ExitSurveyController(subjectID, null);
                    	exitSurveyController.enable();
                    	disableUI(true);
                    }
                } finally {
                    CONTROL_LOCK.unlock();
                }
            }
        });
        
        // RUN LIKERT SURVEY CONTROLLER
        likertSurveyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CONTROL_LOCK.lock();
                try {
                    if(!isInProgress()) {
                        exitSurveyController = new ExitSurveyController(subjectID, KeyboardType.getByName((String) testTypeComboBox.getSelectedItem()));
                        exitSurveyController.enable();
                        disableUI(true);
                    }
                } finally {
                    CONTROL_LOCK.unlock();
                }
            }
        });
        
        // RUN EXPERIEMENT CONTORLLER
        experimentButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                CONTROL_LOCK.lock();
                try {
                    if(!isInProgress()) {
                        EXPERIMENT_CONTROLLER.enable(subjectID, KeyboardType.getByName((String) testTypeComboBox.getSelectedItem()));
                        disableUI(true);
                    }
                } finally {
                    CONTROL_LOCK.unlock();
                }
            }
            
        });
        
        // RUN CALIBRATION CONTROLLER
        calibrateButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                CONTROL_LOCK.lock();
                try {
                    if(!isInProgress()) {
                        calibrationController = new CalibrationController();
                        calibrationController.enable();
                        disableUI(true);
                    }
                } finally {
                    CONTROL_LOCK.unlock();
                }
            }
            
        });
    }
    
    public void update() {
        CONTROL_LOCK.lock();
        try {
            if(experimentInProgress()) {
                EXPERIMENT_CONTROLLER.update();
            } else if(calibrationInProgress()) {
                calibrationController.update();
            } else if(exitSurveyInProgress()) {
                // Do nothing
            } else if(dictionaryBuildInProgress()) {
                dictionaryBuilder.update();
            } else if(dataFormattingInProgress()) {
                dataCenterController.update();
            } else if(isDisabled) {
                if(EXPERIMENT_CONTROLLER.experimentCompletedSuccessfully()) {
                    exitSurveyController = new ExitSurveyController(subjectID, KeyboardType.getByName((String) testTypeComboBox.getSelectedItem()));
                    exitSurveyController.enable();
                } else {
                    enableUI();
                }
            }
        } finally {
            CONTROL_LOCK.unlock();
        }
    }
    
    public void render() {
        CONTROL_LOCK.lock();
        try {
            if(experimentInProgress()) {
                EXPERIMENT_CONTROLLER.render();
            } else if(calibrationInProgress()) {
                calibrationController.render();
            } else if(exitSurveyInProgress()) {
                // Do nothing
            } else if(dictionaryBuildInProgress()) {
                // Do nothing
            } else if(dataFormattingInProgress()) {
                dataCenterController.render();
            }
        } finally {
            CONTROL_LOCK.unlock();
        }
    }
    
    public boolean isRunning() {
        RUNNING_LOCK.lock();
        try {
            return isRunning;
        } finally {
            RUNNING_LOCK.unlock();
        }
    }
    
    private void exit() {
        RUNNING_LOCK.lock();
        try {
            isRunning = false;
        } finally {
            RUNNING_LOCK.unlock();
        }
    }
    
    private boolean isInProgress() {
        if(calibrationInProgress() || experimentInProgress() || exitSurveyInProgress() || dictionaryBuildInProgress() || dataFormattingInProgress()) {
            return true;
        }
        return false;
    }
    
    private boolean calibrationInProgress() {
        if(calibrationController != null) {
            if(calibrationController.isEnabled()) {
                return true;
            } else {
                calibrationController = null;
            }
        }
        return false;
    }
    
    private boolean experimentInProgress() {
        return EXPERIMENT_CONTROLLER.isEnabled();
    }
    
    private boolean exitSurveyInProgress() {
    	if(exitSurveyController != null) {
    		if(exitSurveyController.isEnabled()) {
    			return true;
    		} else {
    			exitSurveyController = null;
    		}
    	}
        return false;
    }
    
    private boolean dictionaryBuildInProgress() {
        if(dictionaryBuilder != null) {
            if(dictionaryBuilder.isEnabled()) {
                return true;
            } else {
                dictionaryBuilder = null;
            }
        }
        return false;
    }
    
    private boolean dataFormattingInProgress() {
        if(dataCenterController != null) {
            if(dataCenterController.isEnabled()) {
                return true;
            } else {
                dataCenterController = null;
            }
        }
        return false;
    }
    
    private void disableUI(boolean fullDisable) {
        isDisabled = fullDisable;
        calibrateButton.setEnabled(false);
        experimentButton.setEnabled(false);
        exitSurveyButton.setEnabled(false);
        likertSurveyButton.setEnabled(false);
        if(fullDisable) editSubjectIDButton.setEnabled(false);
        randomizeSubjectIDButton.setEnabled(false);
        browseSubjectIDButton.setEnabled(false);
        createDictionariesButton.setEnabled(false);
        dataCenterButton.setEnabled(false);
        testTypeComboBox.setEnabled(false);
    }
    
    private void enableUI() {
        ((ComboBoxRenderer) testTypeComboBox.getRenderer()).updateSubjectID(testTypeComboBox, subjectID);
        isDisabled = false;
        calibrateButton.setEnabled(true);
        experimentButton.setEnabled(true);
        exitSurveyButton.setEnabled(true);
        if(((ComboBoxRenderer) testTypeComboBox.getRenderer()).dataExists(testTypeComboBox.getSelectedItem().toString())) {
            likertSurveyButton.setEnabled(true);
        } else {
            likertSurveyButton.setEnabled(false);
        }
        editSubjectIDButton.setEnabled(true);
        randomizeSubjectIDButton.setEnabled(true);
        browseSubjectIDButton.setEnabled(true);
        createDictionariesButton.setEnabled(true);
        dataCenterButton.setEnabled(true);
        testTypeComboBox.setEnabled(true);
    }
    
    private String generateSubjectID() {
        ArrayList<String> subjectIDList = new ArrayList<String>();
        try {
            subjectIDList = MyUtilities.FILE_IO_UTILITIES.getListOfDirectories(FilePath.RECORDED_DATA.getPath());
        } catch (IOException e) {
            System.out.println("Unable to read existing ID's from file.");
            e.printStackTrace();
        }
        
        SecureRandom random = new SecureRandom();
        String subjectID = null;
        do {
            subjectID = new BigInteger(40, random).toString(32);
        } while(subjectIDList.contains(subjectID));
        
        return subjectID;
    }
    
    private boolean checkForUniqueSubjectID(String subjectID) {
        ArrayList<String> subjectIDList = new ArrayList<String>();
        try {
            subjectIDList = MyUtilities.FILE_IO_UTILITIES.getListOfDirectories(FilePath.RECORDED_DATA.getPath());
        } catch (IOException e) {
            System.out.println("Unable to read existing ID's from file.");
            e.printStackTrace();
        }
        
        if(subjectIDList.contains(subjectID)) {
            return false;
        } else {
            return true;
        }
    }
    
    @SuppressWarnings("serial")
    private class AlphaNumericOnlyDocument extends PlainDocument {
        @Override
        public void insertString(int offset, String str, AttributeSet a)
                throws BadLocationException {
            Key key = Key.getByValue(str.charAt(str.length() - 1));
            if(key != null && key.isAlphaNumeric() && getLength() < SUBJECT_ID_SIZE) {
                super.insertString(offset, str, a);
            }
        }
    }
    
    @SuppressWarnings({ "rawtypes", "serial" })
    private class ComboBoxRenderer extends JPanel implements ListCellRenderer {
        private final Color LIGHT_GREEN = new Color(204, 255, 204);
        private final Color HTML_TEXT_GREEN = new Color(0, 128, 0);
        private JPanel textPanel;
        private JLabel text;
        private HashMap<String, Boolean> dataExists = new HashMap<String, Boolean>();
        private ArrayList<String> fileNames = new ArrayList<String>();

        public ComboBoxRenderer(JComboBox<String> comboBox) {
            textPanel = new JPanel();
            textPanel.add(this);
            text = new JLabel();
            text.setOpaque(true);
            text.setFont(comboBox.getFont());
            textPanel.add(text);
            for(int i = 0; i < comboBox.getItemCount(); i++) {
                dataExists.put(comboBox.getItemAt(i).toString(), false); // By default we have a unique sujbect ID.
                fileNames.add(KeyboardType.getByName(comboBox.getItemAt(i).toString()).getFileName());
            }
        }
        
        public void updateSubjectID(JComboBox<String> comboBox, String subjectID) {
            String filePath = FilePath.RECORDED_DATA.getPath() + subjectID + "/";
            int numFinished = 0;
            for(int i = 0; i < comboBox.getItemCount(); i++) {
                String wildcardFileName = subjectID + "_" + fileNames.get(i) + FileUtilities.WILDCARD + FileExt.PLAYBACK.getExt();
                boolean fileExists;
                try {
                    fileExists = MyUtilities.FILE_IO_UTILITIES.checkWildcardFileExists(filePath, wildcardFileName);
                } catch (IOException e) {
                    fileExists = false;
                    System.out.println("Error occured while trying to check if wildcard file exists. File: " + filePath + wildcardFileName);
                    e.printStackTrace();
                }
                dataExists.put(comboBox.getItemAt(i).toString(), fileExists);
                if(fileExists) numFinished++;
            }
            testsFinishedLabel.setText(numFinished + "/" + comboBox.getItemCount());
        }
        
        public boolean dataExists(String value) {
            if(value != null) {
                return dataExists.get(value);
            }
            return false;
        }

        @Override
        public Component getListCellRendererComponent(JList list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {
            boolean dataExists = dataExists(value.toString());
            if (isSelected) {
                setBackground(list.getSelectionBackground());
            } else if(dataExists) {
                setBackground(LIGHT_GREEN);
            } else {
                setBackground(Color.WHITE);
            }
            text.setBackground(getBackground());

            text.setText(value.toString());
            if(dataExists) {
                text.setForeground(HTML_TEXT_GREEN);
            } else {
                text.setForeground(UIManager.getColor("Label.foreground"));
            }
            return text;
        }
    }
}

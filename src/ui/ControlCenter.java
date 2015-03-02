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
import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.JButton;
import javax.swing.JComboBox;
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
import leap.LeapListener;

public class ControlCenter {
    // Constants
    private final int SUBJECT_ID_SIZE = 8;
    private final ExperimentController EXPERIMENT_CONTROLLER = new ExperimentController();
    private final CalibrationController CALIBRATION_CONTROLLER = new CalibrationController();
    private final  ReentrantLock CONTROL_LOCK = new ReentrantLock();
    private ExitSurveyController exitSurveyController;
    private DictionaryBuilder dictionaryBuilder;
    
    // Not Constants
    private JFrame frame;
    private JTextField subjectField;
    private String subjectID;
    private JComboBox<String> testTypeComboBox;
    private JButton editSubjectIDButton;
    private JButton calibrateButton;
    private JButton experimentButton;
    private JButton exitSurveyButton;
    private JButton createDictionariesButton;
    private boolean isLocked = false;
    
    @SuppressWarnings("unchecked")
    public ControlCenter(LeapListener leapListener) {
        // Java Swing/AWT important fields and selections
        frame = new JFrame("Experiment Control Center");
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        subjectID = generateSubjectID();
        subjectField = new JTextField(subjectID);
        testTypeComboBox = new JComboBox<String>();
        editSubjectIDButton = new JButton("Edit");
        calibrateButton = new JButton("Calibration");
        experimentButton = new JButton("Experiment");
        exitSurveyButton = new JButton("Exit Survey");
        createDictionariesButton = new JButton("Create Dictionaries");
        
        JButton[] buttons = {calibrateButton, experimentButton, editSubjectIDButton, exitSurveyButton, createDictionariesButton};
        
        // Window builder builds window using important fields here. It adds unimportant fields that we won't use for aesthetics only.
        WindowBuilder.buildControlWindow(frame, testTypeComboBox, subjectField, buttons);
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
                    System.exit(0);
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
                        System.exit(0);
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
                        System.exit(0);
                    }
                } else if(dictionaryBuildInProgress()) {
                    JOptionPane.showMessageDialog(frame,
                            "You must wait until the dictionary is built to exit.",
                            "Error!",
                            JOptionPane.ERROR_MESSAGE,
                            null);
                }
            }
        });
        
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
                        } else {
                            subjectField.setEditable(false);
                            subjectField.setHighlighter(null);
                            editSubjectIDButton.setText("Edit");
                            subjectID = subjectField.getText();
                            ((ComboBoxRenderer) testTypeComboBox.getRenderer()).updateSubjectID(testTypeComboBox, subjectID);
                            subjectField.setFocusable(false);
                        }
                    }
                } finally {
                    CONTROL_LOCK.unlock();
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
                        System.out.println("Building dicitonaries...");
                        lockUI();
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
                    	exitSurveyController = new ExitSurveyController();
                    	exitSurveyController.enable(subjectID);
                        lockUI();
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
                        lockUI();
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
                        CALIBRATION_CONTROLLER.enable();
                        lockUI();
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
                CALIBRATION_CONTROLLER.update();
            } else if(exitSurveyInProgress()) {
                // Do nothing
            } else if(dictionaryBuildInProgress()) {
                dictionaryBuilder.update();
            } else if(isLocked) {
                unlockUI();
            }
        } finally {
            CONTROL_LOCK.unlock();
        }
    }
    
    public void render() {
        CONTROL_LOCK.lock();
        try {
            if(experimentInProgress()) {
                EXPERIMENT_CONTROLLER.display();
            } else if(calibrationInProgress()) {
                CALIBRATION_CONTROLLER.display();
            } else if(exitSurveyInProgress()) {
                // Do nothing
            } else if(dictionaryBuildInProgress()) {
                // Do nothing
            }
        } finally {
            CONTROL_LOCK.unlock();
        }
    }
    
    public boolean isInProgress() {
        if(calibrationInProgress() || experimentInProgress() || exitSurveyInProgress() || dictionaryBuildInProgress()) {
            return true;
        }
        return false;
    }
    
    public boolean calibrationInProgress() {
        return CALIBRATION_CONTROLLER.isEnabled();
    }
    
    public boolean experimentInProgress() {
        return EXPERIMENT_CONTROLLER.isEnabled();
    }
    
    public boolean exitSurveyInProgress() {
    	if(exitSurveyController != null) {
    		if(exitSurveyController.isEnabled()) {
    			return true;
    		} else {
    			exitSurveyController = null;
    		}
    	}
        return false;
    }
    
    public boolean dictionaryBuildInProgress() {
        if(dictionaryBuilder != null) {
            if(dictionaryBuilder.isEnabled()) {
                return true;
            } else {
                System.out.println("Dictionaries built.");
                dictionaryBuilder = null;
            }
        }
        return false;
    }
    
    private void lockUI() {
        isLocked = true;
        calibrateButton.setEnabled(false);
        experimentButton.setEnabled(false);
        exitSurveyButton.setEnabled(false);
        editSubjectIDButton.setEnabled(false);
        createDictionariesButton.setEnabled(false);
        testTypeComboBox.setEnabled(false);
        //frame.setVisible(false);
    }
    
    private void unlockUI() {
        ((ComboBoxRenderer) testTypeComboBox.getRenderer()).updateSubjectID(testTypeComboBox, subjectID);
        isLocked = false;
        calibrateButton.setEnabled(true);
        experimentButton.setEnabled(true);
        exitSurveyButton.setEnabled(true);
        editSubjectIDButton.setEnabled(true);
        createDictionariesButton.setEnabled(true);
        testTypeComboBox.setEnabled(true);
        //frame.setVisible(true);
    }
    
    private String generateSubjectID() {
        ArrayList<String> subjectIDList = new ArrayList<String>();
        try {
            subjectIDList = MyUtilities.FILE_IO_UTILITIES.getListOfDirectories(FilePath.DATA.getPath());
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
            subjectIDList = MyUtilities.FILE_IO_UTILITIES.getListOfDirectories(FilePath.DATA.getPath());
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
            String filePath = FilePath.DATA.getPath() + subjectID + "/";
            for(int i = 0; i < comboBox.getItemCount(); i++) {
                String wildcardFileName = subjectID + "_" + fileNames.get(i) + FileUtilities.WILDCARD + FileExt.DAT.getExt();
                boolean fileExists;
                try {
                    fileExists = MyUtilities.FILE_IO_UTILITIES.checkWildcardFileExists(filePath, wildcardFileName);
                } catch (IOException e) {
                    fileExists = false;
                    System.out.println("Error occured while trying to check if wildcard file exists. File: " + filePath + wildcardFileName);
                    e.printStackTrace();
                }
                dataExists.put(comboBox.getItemAt(i).toString(), fileExists);
            }
        }
        
        private boolean dataExists(String value) {
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

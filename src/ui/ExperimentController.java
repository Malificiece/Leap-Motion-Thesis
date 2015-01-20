package ui;
import static javax.media.opengl.GL.GL_COLOR_BUFFER_BIT;
import static javax.media.opengl.GL.GL_DEPTH_BUFFER_BIT;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

import utilities.MyUtilities;
import keyboard.KeyboardAttributes;
import keyboard.KeyboardSetting;
import enums.Attribute;
import enums.FilePath;
import enums.Keyboard;
import enums.TestType;
import experiment.WordManager;

public class ExperimentController extends GraphicsController {
    private static final String DEFAULT_INFO = "CALIBRATE:\nCalibrate the keyboard (if available).\n\n"
            + "TUTORIAL:\nA brief example to familiarize yourself with the keyboard.\n\n"
            + "PRACTICE:\nA small sample of what you should expect from the experiment.\n\n"
            + "EXPERIMENT:\nThe actual experiment with recorded data.";
    private JFrame frame;
    private JSplitPane splitPane;
    private JPanel canvasPanel;
    private JButton calibrateButton;
    private JButton tutorialButton;
    private JButton practiceButton;
    private JButton experimentButton;
    private JTextArea infoPane;
    private JPanel settingsPanel;
    private JPanel wordPanel;
    private JPanel answerPanel;
    private JLabel wordLabel;
    private JLabel answerLabel;
    private boolean runningCalibration = false;
    private boolean runningTutorial = false;
    private boolean runningPractice = false;
    private boolean runningExperiment = false;
    private boolean ranTutorial = false;
    private boolean ranPractice = false;
    private WordManager wordManager = new WordManager();
    
    public ExperimentController() {
        keyboard = Keyboard.STANDARD.getKeyboard();
        canvasPanel = new JPanel();
        canvas = new GLCanvas(capabilities);
        canvas.setPreferredSize(new Dimension(keyboard.getImageWidth(), keyboard.getImageHeight()));
        canvas.setSize(keyboard.getImageWidth(), keyboard.getImageHeight());
        canvasPanel.add(canvas);
        frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        infoPane = new JTextArea(DEFAULT_INFO);
        settingsPanel = new JPanel();
        wordPanel = new JPanel();
        answerPanel = new JPanel();
        wordLabel = new JLabel();
        answerLabel = new JLabel();
        calibrateButton = new JButton("Calibrate");
        tutorialButton = new JButton("Tutorial");
        practiceButton = new JButton("Practice");
        experimentButton = new JButton("Run Experiment");
        
        JButton buttons[] = {calibrateButton, tutorialButton, practiceButton, experimentButton};
        JLabel labels[] = {wordLabel, answerLabel};
        JPanel panels[] = {wordPanel, answerPanel, settingsPanel};

        // Window builder builds window using important fields here. It adds unimportant fields that we use for aesthetics only.
        WindowBuilder.buildExperimentWindow(frame, canvasPanel, infoPane, panels, labels, buttons, splitPane);
        infoPane.setFocusable(false);
        canvas.setFocusable(false);
        
        calibrateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(isLeapKeyboard()) {
                    if(keyboard.isCalibrated()) {
                        Object[] options = {"Recalibrate", "Cancel"};
                        int selection =
                                JOptionPane.showOptionDialog(frame,
                                "The Leap Motion Interaction Plane has already been calibrated.\nDo you want to recalibrate it?",
                                "Warning!",
                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.WARNING_MESSAGE,
                                null,
                                options,
                                options[0]);
                        if(selection == JOptionPane.YES_OPTION) {
                            beginCalibration();
                            keyboard.beginCalibration(answerPanel);
                        }
                    } else {
                        beginCalibration();
                        keyboard.beginCalibration(answerPanel);
                    }
                } else {
                    JOptionPane.showMessageDialog(frame,
                    "This keyboard cannot be calibrated.",
                    "Error!",
                    JOptionPane.ERROR_MESSAGE);
                }
                frame.requestFocusInWindow();
            }
        });
        
        tutorialButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                if(runningTutorial) {
                    finishTutorial();
                } else {
                    // Beging tutorial -- detect keyboard but don't record it's key event
                    beingTutorial();
                    //keyboard.streamDataFromFile(FilePath.TUTORIAL.getPath());
                    // change listener on keyboard to use DATA listener rather than it's classic listener
                    // turn off leap listener, ignore key bindings, don't update controller inputs
                    // turn on when done
                    
                    // show tutorial word, show keyboard response, DO NOT RECORD DATA
                }
                frame.requestFocusInWindow();
            }
        });
        
        practiceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                // Begin practice -- detect keyboard but don't record it's key events
                // show words, show keyboard, DO NOT RECORD DATA
                // Finish practice
                // once finished enable experiment
                frame.requestFocusInWindow();
            }
        });
        
        experimentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                // Begin experiment -- detect keyboard and record it's key events
                // show words, show keyboard, records data in memory
                // Finish experiment
                // once finished write data from memory to file
                frame.requestFocusInWindow();
            }
        });
        
        // by default, an AWT Frame doesn't do anything when you click
        // the close button; this bit of code will terminate the program when
        // the window is asked to close
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                if(!runningCalibration && !runningTutorial && !runningPractice && !runningExperiment) {
                    disable();
                }
            }
        });
        
        frame.pack();
    }
    
    @Override
    public void keyboardCalibrationFinishedEventObserved() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                finishCalibration();
            }
        });
    }
    
    private void beginCalibration() {
        runningCalibration = true;
        wordLabel.setText("");
        wordLabel.setVisible(false);
        answerLabel.setText("");
        answerLabel.setVisible(false);
        wordPanel.removeAll();
        calibrateButton.setEnabled(false);
        tutorialButton.setEnabled(false);
        practiceButton.setEnabled(false);
        experimentButton.setEnabled(false);
        infoPane.setText("Calibration in progress...");
        settingsPanel.removeAll();
        if(isLeapKeyboard()) {
            KeyboardAttributes ka = keyboard.getAttributes();
            settingsPanel.add(ka.getAttribute(Attribute.LEAP_PLANE_POINT_A).getAttributePanel());
            settingsPanel.add(ka.getAttribute(Attribute.LEAP_PLANE_POINT_B).getAttributePanel());
            settingsPanel.add(ka.getAttribute(Attribute.LEAP_PLANE_POINT_C).getAttributePanel());
        }
    }
    
    private void finishCalibration() {
        runningCalibration = false;
        wordPanel.add(wordLabel);
        wordLabel.setVisible(true);
        wordPanel.add(answerLabel);
        answerLabel.setVisible(true);
        calibrateButton.setEnabled(true);
        tutorialButton.setEnabled(true);
        if(ranTutorial) {
            practiceButton.setEnabled(true);
        }
        if(ranPractice) {
            experimentButton.setEnabled(true);
        }
        settingsPanel.removeAll();
        
        KeyboardAttributes ka = keyboard.getAttributes();
        settingsPanel.add(ka.getAttribute(Attribute.KEYBOARD_SIZE).getAttributePanel());
        if(isLeapKeyboard()) {
            settingsPanel.add(ka.getAttribute(Attribute.LEAP_PLANE_POINT_A).getAttributePanel());
            settingsPanel.add(ka.getAttribute(Attribute.LEAP_PLANE_POINT_B).getAttributePanel());
            settingsPanel.add(ka.getAttribute(Attribute.LEAP_PLANE_POINT_C).getAttributePanel());
        }
        
        for(KeyboardSetting ks: keyboard.getSettings().getAllSettings()) {
            settingsPanel.add(ks.getSettingsPanel());
        }
        frame.pack();
    }
    
    private void beingTutorial() {
        runningTutorial = true;
        wordLabel.setText("");
        answerLabel.setText("");
        calibrateButton.setEnabled(false);
        tutorialButton.setText("Done");
        practiceButton.setEnabled(false);
        experimentButton.setEnabled(false);
        infoPane.setText("Tutorial in progress...");
        // add instructions as we go through the tutorial
        // show two words at least in the tutorial
        settingsPanel.setEnabled(false);
    }
    
    private void finishTutorial() {
        runningTutorial = false;
        ranTutorial = true;
        calibrateButton.setEnabled(true);
        tutorialButton.setText("Tutorial");
        if(ranTutorial) {
            practiceButton.setEnabled(true);
        }
        if(ranPractice) {
            experimentButton.setEnabled(true);
        }
        infoPane.setText(DEFAULT_INFO);
        settingsPanel.setEnabled(true);
    }
    
    private void beingPractice() {
        runningPractice = true;
    }
    
    private void finishPractice() {
        runningPractice = false;
    }
    
    private void beginExperiment() {
        runningExperiment = true;
    }
    
    private void finishExeriment() {
        runningExperiment = false;
    }
    
    public void disable() {
        removeKeyboardFromUI();
        frame.setVisible(false);
        canvas.disposeGLEventListener(this, true);
        Keyboard.STANDARD.getKeyboard().removeObserver(this);
        Keyboard.LEAP_SURFACE.getKeyboard().removeObserver(this);
        Keyboard.LEAP_AIR.getKeyboard().removeObserver(this);
        Keyboard.TABLET.getKeyboard().removeObserver(this);
        Keyboard.CONTROLLER.getKeyboard().removeObserver(this);
        enabled = false;
    }
    
    public void enable() {
        addKeyboardToUI();
        frame.setVisible(true);
        frame.requestFocusInWindow();
        canvas.addGLEventListener(this);
        Keyboard.STANDARD.getKeyboard().registerObserver(this);
        Keyboard.LEAP_SURFACE.getKeyboard().registerObserver(this);
        Keyboard.LEAP_AIR.getKeyboard().registerObserver(this);
        Keyboard.TABLET.getKeyboard().registerObserver(this);
        Keyboard.CONTROLLER.getKeyboard().registerObserver(this);
        enabled = true;
    }
    
    public void enable(String subjectID, TestType testType) {
        if(frame != null) {
            frame.setTitle("Experiment - Subject ID: " + subjectID + " Test: " + testType.getName());
        }
        keyboard = Keyboard.getByID(testType.getKeyboardID()).getKeyboard();
        enable();
    }
    
    private void removeKeyboardFromUI() {
        wordLabel.setText("");
        answerLabel.setText("");
        settingsPanel.removeAll();
        keyboard.removeFromUI(canvasPanel, canvas);
    }
    
    private void addKeyboardToUI() {
        keyboard.addToUI(canvasPanel, canvas);
        
        KeyboardAttributes ka = keyboard.getAttributes();
        settingsPanel.add(ka.getAttribute(Attribute.KEYBOARD_SIZE).getAttributePanel());
        
        if(isLeapKeyboard()) {
            settingsPanel.add(ka.getAttribute(Attribute.LEAP_PLANE_POINT_A).getAttributePanel());
            settingsPanel.add(ka.getAttribute(Attribute.LEAP_PLANE_POINT_B).getAttributePanel());
            settingsPanel.add(ka.getAttribute(Attribute.LEAP_PLANE_POINT_C).getAttributePanel());
        }
        
        for(KeyboardSetting ks: keyboard.getSettings().getAllSettings()) {
            settingsPanel.add(ks.getSettingsPanel());
        }
        
        canvas.setPreferredSize(new Dimension(keyboard.getImageWidth(), keyboard.getImageHeight()));
        canvas.setSize(keyboard.getImageWidth(), keyboard.getImageHeight());
        frame.pack();
    }
    
    public void update() {        
        if(runningTutorial) {
            // if tutorial selected, read pre-recorded data and display the word/keyboard functioning
            // once tutorial is done, enable practice
        } else if(runningPractice) {
            // if practice selected, a practice experiment is given with a set of words, use default set for all keyboards. Don't record data
            // after practice is completed enable experiment
            // tutorial and practice are repeatable any number of times with no effect to experiment
        } else if(runningExperiment) {
            // if experiment selected, show keyboard, give set of real words, and record data for subject ID
            // possibly spawn separate thread for writing? If so, have to make sure that each thread that is
            // spawned works together to write to file. Possibly just have my datawriter on it's own thread
            // and send it events that make it write to file automatically
        }
        // if nothing selected, show keyboard but record nothing -- kind of like calibration
        
        // consider recording both pressed and released events so that we can just have our data reader
        // fully read the data without much help from the keyboard classes
        // otherwise perhaps we should implement these functionalities in the keyboard classes themselves via a listener
        // and the keyboard classes render the recorded data based on what they see. - might be best (still need released events)
        keyboard.update();
    }
    
    public void render(GLAutoDrawable drawable) {
        gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        keyboard.render(gl);
    }

    @Override
    public void keyboardKeyEventObserved(char key) {
        if(runningExperiment) {
            // record data here
        }
        if(key == '\b' && 0 < answerLabel.getText().length()) {
            answerLabel.setText(answerLabel.getText().substring(0, answerLabel.getText().length()-1));
        } else if(key == '\n') {
            // check if input is correct
            // flash red if no
            // flash green if yes and move to next word
        } else {
            answerLabel.setText(answerLabel.getText()+Character.toString(key));
            // check if input is correct
            // set background or text to light green if so
            // 
        }
        MyUtilities.JAVA_SWING_UTILITIES.calculateFontSize(wordLabel.getText(), answerLabel, answerPanel);
    }
    
    private boolean isLeapKeyboard() {
        return keyboard.getID() == Keyboard.LEAP_SURFACE.getID() || keyboard.getID() == Keyboard.LEAP_AIR.getID();
    }
}

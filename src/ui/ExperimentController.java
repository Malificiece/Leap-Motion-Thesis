package ui;
import static javax.media.opengl.GL.GL_COLOR_BUFFER_BIT;
import static javax.media.opengl.GL.GL_DEPTH_BUFFER_BIT;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

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
import enums.Keyboard;
import enums.TestType;
import experiment.TutorialManager;
import experiment.WordManager;

public class ExperimentController extends GraphicsController {
    private static final String DEFAULT_INFO = "CALIBRATE:\nCalibrate the keyboard (if available).\n\n"
            + "TUTORIAL:\nA brief example to familiarize yourself with the keyboard.\n\n"
            + "PRACTICE:\nA small sample of what you should expect from the experiment.\n\n"
            + "EXPERIMENT:\nThe actual experiment with recorded data.";
    private final int PRACTICE_SIZE = 10;
    private final int EXPERIMENT_SIZE = 30;
    private final Color LIGHT_GREEN = new Color(204, 255, 204);
    private final int FADE_DURATION = 500;
    private Color currentColor = Color.WHITE;
    private long previousFadeTime;
    private long fadeTimeElapsed = 0;
    private boolean isFading = false;
    private JFrame frame;
    private JSplitPane splitPane;
    private JPanel canvasPanel;
    private JButton calibrateButton;
    private JButton tutorialButton;
    private JButton practiceButton;
    private JButton experimentButton;
    private JTextArea infoPane;
    private JPanel infoPanel;
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
    private TutorialManager tutorialManager;
    
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
        infoPanel = new JPanel();
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
        JPanel panels[] = {wordPanel, answerPanel, settingsPanel, infoPanel};

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
                beginTutorial();
                // keyboard.streamDataFromFile(FilePath.TUTORIAL.getPath());
                // change listener on keyboard to use DATA listener rather than it's classic listener
                // turn off leap listener, ignore key bindings, don't update controller inputs
                // turn on when done
                frame.requestFocusInWindow();
            }
        });
        
        practiceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                beginPractice();
                frame.requestFocusInWindow();
            }
        });
        
        experimentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                beginExperiment();
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
        disableUI();
        wordLabel.setVisible(false);
        answerLabel.setVisible(false);
        wordPanel.removeAll();
        infoPane.setText("Calibration in progress...\n\n"
                + "Please follow the Calibration instructions.");
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
        enableUI();
        wordPanel.add(wordLabel);
        wordLabel.setVisible(true);
        wordPanel.add(answerLabel);
        answerLabel.setVisible(true);
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
    
    private void beginTutorial() {
        runningTutorial = true;
        tutorialManager = new TutorialManager();
        infoPanel.add(tutorialManager.getComponent());
        infoPane.setText(tutorialManager.getText());
        // set up word manager here
        disableUI();
    }
    
    private void finishTutorial() {
        runningTutorial = false;
        infoPanel.remove(tutorialManager.getComponent());
        tutorialManager = null;
        ranTutorial = true;
        enableUI();
    }
    
    private void beginPractice() {
        runningPractice = true;
        // TODO: Add a delay and a display to show when it's about to begin.
        // This will give us a head's up before things start
        wordManager.loadWords(PRACTICE_SIZE);
        disableUI();
        splitPane.getRightComponent().setVisible(false);
        splitPane.setSize(splitPane.getLeftComponent().getSize());
        splitPane.getLeftComponent().setMaximumSize(splitPane.getLeftComponent().getSize());
        splitPane.setDividerLocation(splitPane.getLeftComponent().getWidth());
        frame.setSize(splitPane.getLeftComponent().getWidth(), (int) frame.getSize().getHeight());
        System.out.println(splitPane.getLeftComponent().getSize());
        wordLabel.setText(wordManager.currentWord());
    }
    
    private void finishPractice() {
        runningPractice = false;
        System.out.println(splitPane.getLeftComponent().getSize());
        splitPane.getRightComponent().setVisible(true);
        splitPane.setSize(splitPane.getLeftComponent().getWidth() + splitPane.getRightComponent().getWidth(), splitPane.getHeight());
        splitPane.setDividerLocation(splitPane.getLeftComponent().getWidth());
        ranPractice = true;
        enableUI();
    }
    
    private void beginExperiment() {
        runningExperiment = true;
    }
    
    private void finishExperiment() {
        runningExperiment = false;
    }
    
    public void disableUI() {
        wordLabel.setText("");
        answerLabel.setText("");
        calibrateButton.setEnabled(false);
        tutorialButton.setEnabled(false);
        practiceButton.setEnabled(false);
        experimentButton.setEnabled(false);
        settingsPanel.setEnabled(false);
        frame.revalidate();
        frame.repaint();
        frame.pack();
    }
    
    public void enableUI() {
        wordManager.setDefault();
        wordLabel.setText(wordManager.currentWord());
        MyUtilities.JAVA_SWING_UTILITIES.calculateFontSize(wordLabel.getText(), wordLabel, wordPanel);
        calibrateButton.setEnabled(true);
        tutorialButton.setEnabled(true);
        if(ranTutorial) {
            practiceButton.setEnabled(true);
        }
        if(ranPractice) {
            experimentButton.setEnabled(true);
        }
        infoPane.setText(DEFAULT_INFO);
        settingsPanel.setEnabled(true);
        frame.revalidate();
        frame.repaint();
        frame.pack();
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
        wordLabel.setText(wordManager.currentWord());
        MyUtilities.JAVA_SWING_UTILITIES.calculateFontSize(wordLabel.getText(), wordLabel, wordPanel);
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
        keyboard = null;
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
        wordPanel.setSize(keyboard.getImageWidth(), wordPanel.getHeight());
        answerPanel.setSize(keyboard.getImageWidth(), answerPanel.getHeight());
        wordPanel.setPreferredSize(new Dimension(keyboard.getImageWidth(), wordPanel.getHeight()));
        answerPanel.setPreferredSize(new Dimension(keyboard.getImageWidth(), answerPanel.getHeight()));
        frame.revalidate();
        frame.repaint();
        frame.pack();
    }
    
    public void update() {        
        if(runningTutorial) {
            // if tutorial selected, read pre-recorded data and display the word/keyboard functioning
            // once tutorial is done, enable practice
            if(tutorialManager.hasNext() && tutorialManager.isValid()) {
                infoPane.setText(tutorialManager.getText());
            } else if(!tutorialManager.isValid()) {
                finishTutorial();
            }
        } else if(runningPractice && !wordManager.isValid()) {
            finishPractice();
        } else if(runningExperiment && !wordManager.isValid()) {
            // if experiment selected, show keyboard, give set of real words, and record data for subject ID
            // possibly spawn separate thread for writing? If so, have to make sure that each thread that is
            // spawned works together to write to file. Possibly just have my datawriter on it's own thread
            // and send it events that make it write to file automatically
            finishExperiment();
        }
        // if nothing selected, show keyboard but record nothing -- kind of like calibration
        
        if(isFading) {
            long now = System.currentTimeMillis();
            fadeTimeElapsed += now - previousFadeTime;
            previousFadeTime = now;

            if(fadeTimeElapsed <= FADE_DURATION) {
                float fraction = fadeTimeElapsed/(float)FADE_DURATION;
                Color color = wordPanel.getBackground();
                int red = (int)(fraction * currentColor.getRed() + 
                        (1 - fraction) * color.getRed());
                int green = (int)(fraction * currentColor.getGreen() + 
                        (1 - fraction) * color.getGreen());
                int blue = (int)(fraction * currentColor.getBlue() + 
                        (1 - fraction) * color.getBlue());
                color = new Color(red, green, blue);
                wordPanel.setBackground(color);
                answerPanel.setBackground(color);
            } else {
                isFading = false;
                fadeTimeElapsed = 0;
            }
        }
        
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
        if(key == '\b') {
            if(0 < answerLabel.getText().length()) {
                answerLabel.setText(answerLabel.getText().substring(0, answerLabel.getText().length()-1));
            }
            if(wordManager.isMatch(answerLabel.getText())) {
                currentColor = LIGHT_GREEN;
                wordPanel.setBackground(currentColor);
                answerPanel.setBackground(currentColor);
            } else {
                if(!wordPanel.getBackground().equals(Color.WHITE)) {
                    currentColor = Color.WHITE;
                    wordPanel.setBackground(currentColor);
                    answerPanel.setBackground(currentColor);
                }
            }
        } else if(key == '\n') {
            currentColor = Color.WHITE;
            previousFadeTime = System.currentTimeMillis();
            fadeTimeElapsed = 0;
            isFading = true;
            if(wordManager.isMatch(answerLabel.getText())) {
                wordPanel.setBackground(Color.GREEN);
                answerPanel.setBackground(Color.GREEN);
                wordManager.nextWord();
                answerLabel.setText("");
                wordLabel.setText(wordManager.currentWord());
            } else {
                wordPanel.setBackground(Color.RED);
                answerPanel.setBackground(Color.RED);
            }
        } else {
            answerLabel.setText(answerLabel.getText()+Character.toString(key));
            MyUtilities.JAVA_SWING_UTILITIES.calculateFontSize(wordLabel.getText(), answerLabel, answerPanel);
            if(wordManager.isMatch(answerLabel.getText())) {
                currentColor = LIGHT_GREEN;
                wordPanel.setBackground(currentColor);
                answerPanel.setBackground(currentColor);
            } else {
                if(!wordPanel.getBackground().equals(Color.WHITE)) {
                    currentColor = Color.WHITE;
                    wordPanel.setBackground(currentColor);
                    answerPanel.setBackground(currentColor);
                }
            }
        }
    }
    
    private boolean isLeapKeyboard() {
        return keyboard.getID() == Keyboard.LEAP_SURFACE.getID() || keyboard.getID() == Keyboard.LEAP_AIR.getID();
    }
}

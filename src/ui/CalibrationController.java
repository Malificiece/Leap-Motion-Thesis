package ui;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Timer;
import java.util.TimerTask;

import javax.media.opengl.GL2;
import javax.media.opengl.awt.GLCanvas;

import static javax.media.opengl.GL.*;  // GL constants

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import keyboard.KeyboardAttributes;
import keyboard.KeyboardRenderable;
import keyboard.KeyboardSetting;
import utilities.MyUtilities;
import enums.Attribute;
import enums.Keyboard;
import enums.KeyboardType;

public class CalibrationController extends GraphicsController {
    private JPanel canvasPanel;
    private JLabel wordLabel;
    private JPanel wordPanel;
    private JComboBox<String> keyboardTypeComboBox;
    private JButton calibrateButton;
    private JButton saveSettingsButton;
    private JPanel settingsPanel;
    private JPanel renderOptionsPanel;
    private Timer fpsTimer;
    private int frameCount = 0;
    private boolean runningCalibration = false;
    
    public CalibrationController() {
        keyboard = Keyboard.STANDARD.getKeyboard();
        canvasPanel = new JPanel();
        canvas = new GLCanvas(CAPABILITIES);
        canvas.setPreferredSize(new Dimension(keyboard.getImageWidth(), keyboard.getImageHeight()));
        canvas.setSize(keyboard.getImageWidth(), keyboard.getImageHeight());
        canvasPanel.add(canvas);
        frame = new JFrame("Calibration - FPS: 0");
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        wordLabel = new JLabel();
        wordPanel = new JPanel();
        keyboardTypeComboBox = new JComboBox<String>();
        calibrateButton = new JButton("Calibrate");
        saveSettingsButton = new JButton("Save Settings");
        settingsPanel = new JPanel();
        renderOptionsPanel = new JPanel();
        
        JPanel[] panels = {wordPanel, settingsPanel, renderOptionsPanel};
        JButton[] buttons = {calibrateButton, saveSettingsButton};      

        // Window builder builds window using important fields here. It adds unimportant fields that we use for aesthetics only.
        WindowBuilder.buildCalibrationWindow(frame, canvasPanel, wordLabel, keyboardTypeComboBox, buttons, panels);
        canvas.setFocusable(false);
        MyUtilities.SWING_UTILITIES.calculateFontSize(wordLabel.getText(), wordLabel, wordPanel);
        
        saveSettingsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                keyboard.saveSettings();
                frame.requestFocusInWindow();
            }
        });
        
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
                            keyboard.beginCalibration(wordPanel);
                        }
                    } else {
                        beginCalibration();
                        keyboard.beginCalibration(wordPanel);
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
        
        keyboardTypeComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                KeyboardType keyboardType = KeyboardType.getByName((String) keyboardTypeComboBox.getSelectedItem());
                if(keyboard.getID() != keyboardType.getID()) {
                    // Remove all settings, attributes, and renderables from the previous keyboard.
                    removeKeyboardFromUI();
                    
                    // Add all settings, attributes, and renderables from the new keyboard.
                    keyboard = Keyboard.getByID(keyboardType.getID()).getKeyboard();
                    addKeyboardToUI();
                }
                frame.requestFocusInWindow();
            }
        });
        
        // by default, an AWT Frame doesn't do anything when you click
        // the close button; this bit of code will terminate the program when
        // the window is asked to close
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                if(!runningCalibration) {
                    disable();
                }
            }
        });
        
        // Something causes the canvas to resize between the original frame.pack() and this one so we just pack again, no harm done.
        frame.pack();
    }
    
    @Override
    public void keyboardKeyEventObserved(char key) {
        if(key == '\b') {
            if(0 < wordLabel.getText().length()) {
                wordLabel.setText(wordLabel.getText().substring(0, wordLabel.getText().length()-1));
            }
        } else if(key == '\n') {
            wordLabel.setText("");
        } else {
            wordLabel.setText(wordLabel.getText()+Character.toString(key));
        }
        MyUtilities.SWING_UTILITIES.calculateFontSize(wordLabel.getText(), wordLabel, wordPanel);
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
        wordPanel.removeAll();
        calibrateButton.setEnabled(false);
        saveSettingsButton.setEnabled(false);
        keyboardTypeComboBox.setEnabled(false);
        settingsPanel.removeAll();
        if(isLeapKeyboard()) {
            KeyboardAttributes ka = keyboard.getAttributes();
            settingsPanel.add(ka.getAttribute(Attribute.LEAP_PLANE_POINT_A).getAttributePanel());
            settingsPanel.add(ka.getAttribute(Attribute.LEAP_PLANE_POINT_B).getAttributePanel());
            settingsPanel.add(ka.getAttribute(Attribute.LEAP_PLANE_POINT_C).getAttributePanel());
            settingsPanel.add(ka.getAttribute(Attribute.LEAP_PLANE_POINT_D).getAttributePanel());
        }
    }
    
    private void finishCalibration() {
        runningCalibration = false;
        wordPanel.add(wordLabel);
        wordLabel.setVisible(true);
        calibrateButton.setEnabled(true);
        saveSettingsButton.setEnabled(true);
        keyboardTypeComboBox.setEnabled(true);
        settingsPanel.removeAll();
        
        KeyboardAttributes ka = keyboard.getAttributes();
        settingsPanel.add(ka.getAttribute(Attribute.KEYBOARD_SIZE).getAttributePanel());
        if(isLeapKeyboard()) {
            settingsPanel.add(ka.getAttribute(Attribute.LEAP_PLANE_POINT_A).getAttributePanel());
            settingsPanel.add(ka.getAttribute(Attribute.LEAP_PLANE_POINT_B).getAttributePanel());
            settingsPanel.add(ka.getAttribute(Attribute.LEAP_PLANE_POINT_C).getAttributePanel());
            settingsPanel.add(ka.getAttribute(Attribute.LEAP_PLANE_POINT_D).getAttributePanel());
        }
        
        for(KeyboardSetting ks: keyboard.getSettings().getAllSettings()) {
            settingsPanel.add(ks.getSettingsPanel());
        }
        frame.pack();
    }
    
    @Override
    protected void disable() {
        removeKeyboardFromUI();
        frame.setVisible(false);
        canvas.disposeGLEventListener(this, true);
        for(Keyboard tmpKeyboard: Keyboard.values()) {
            tmpKeyboard.getKeyboard().removeObserver(this);
        }
        fpsTimer.cancel();
        isEnabled = false;
        frame.dispose();
    }
    
    @Override
    public void enable() {
        addKeyboardToUI();
        frame.setVisible(true);
        frame.requestFocusInWindow();
        canvas.addGLEventListener(this);
        for(Keyboard tmpKeyboard: Keyboard.values()) {
            tmpKeyboard.getKeyboard().registerObserver(this);
        }
        TimerTask updateFPS = new TimerTask() {
            @Override
            public void run() {
                if(frame != null) {
                    frame.setTitle("Calibration - FPS: " + frameCount);
                }
                frameCount = 0;
            }
        };
        fpsTimer = new Timer();
        fpsTimer.scheduleAtFixedRate(updateFPS, 1000, 1000);
        isEnabled = true;
    }
    
    @Override
    public void update() {
        keyboard.update();
    }
    
    @Override
    protected void render(GL2 gl) {
       gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
       keyboard.render(gl);
       frameCount++;
    }
    
    private void removeKeyboardFromUI() {
        wordLabel.setText("");
        settingsPanel.removeAll();
        renderOptionsPanel.removeAll();
        keyboard.removeFromUI(canvasPanel, canvas);
        // keyboard = null;
    }
    
    private void addKeyboardToUI() {
        keyboard.addToUI(canvasPanel, canvas);
        
        KeyboardAttributes ka = keyboard.getAttributes();
        settingsPanel.add(ka.getAttribute(Attribute.KEYBOARD_SIZE).getAttributePanel());
        
        if(isLeapKeyboard()) {
            settingsPanel.add(ka.getAttribute(Attribute.LEAP_PLANE_POINT_A).getAttributePanel());
            settingsPanel.add(ka.getAttribute(Attribute.LEAP_PLANE_POINT_B).getAttributePanel());
            settingsPanel.add(ka.getAttribute(Attribute.LEAP_PLANE_POINT_C).getAttributePanel());
            settingsPanel.add(ka.getAttribute(Attribute.LEAP_PLANE_POINT_D).getAttributePanel());
        }
        
        for(KeyboardSetting ks: keyboard.getSettings().getAllSettings()) {
            settingsPanel.add(ks.getSettingsPanel());
        }
        
        for(KeyboardRenderable kr: keyboard.getRenderables().getAllRenderables()) {
            renderOptionsPanel.add(kr.getRenderablePanel());
        }
        
        canvas.setPreferredSize(new Dimension(keyboard.getImageWidth(), keyboard.getImageHeight()));
        canvas.setSize(keyboard.getImageWidth(), keyboard.getImageHeight());
        frame.revalidate();
        frame.repaint();
        frame.pack();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension windowSize = frame.getSize();
        frame.setLocation((int)(screenSize.getWidth()/2 - windowSize.getWidth()/2),
                          (int)(screenSize.getHeight()/2 - windowSize.getHeight()/2));
    }
    
    private boolean isLeapKeyboard() {
        return KeyboardType.getByID(keyboard.getID()).isLeap();
    }
}

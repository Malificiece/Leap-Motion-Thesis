package ui;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.awt.GLCanvas;

import static javax.media.opengl.GL.*;  // GL constants

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

import keyboard.KeyboardAttributes;
import keyboard.KeyboardRenderable;
import keyboard.KeyboardSetting;
import leap.LeapListener;
import utilities.MyUtilities;

import com.leapmotion.leap.Vector;

import enums.Attribute;
import enums.KeyboardType;


public class CalibrationController extends GraphicsController {
    private ArrayList<SaveSettingsObserver> observers = new ArrayList<SaveSettingsObserver>();
    private JFrame frame;
    private JLabel typedLabel;
    private JPanel typedPanel;
    private JComboBox<String> keyboardTypeComboBox;
    private JButton calibrateButton;
    private JButton saveSettingsButton;
    private JPanel settingsPanel;
    private JPanel renderOptionsPanel;
    private Timer clearTextTimer;
    
    public CalibrationController() {
        keyboard = KeyboardType.STANDARD.getKeyboard();
        registerObserver(KeyboardType.STANDARD.getKeyboard());
        registerObserver(KeyboardType.LEAP.getKeyboard());
        registerObserver(KeyboardType.TABLET.getKeyboard());
        registerObserver(KeyboardType.CONTROLLER.getKeyboard());
        canvas = new GLCanvas(capabilities);
        canvas.setPreferredSize(new Dimension(keyboard.getWidth(), keyboard.getHeight()));
        canvas.setSize(keyboard.getWidth(), keyboard.getHeight());
        frame = new JFrame("Calibration");
        typedLabel = new JLabel();
        typedPanel = new JPanel();
        keyboardTypeComboBox = new JComboBox<String>();
        calibrateButton = new JButton("Calibrate");
        saveSettingsButton = new JButton("Save Settings");
        settingsPanel = new JPanel();
        renderOptionsPanel = new JPanel();
        
        JPanel panels[] = {typedPanel, settingsPanel, renderOptionsPanel};
        JButton buttons[] = {calibrateButton, saveSettingsButton};      

        // Window builder builds window using important fields here. It adds unimportant fields that we won't use for aesthetics only.
        WindowBuilder.buildCalibrationWindow(frame, canvas, typedLabel, keyboardTypeComboBox, buttons, panels);
        canvas.setFocusable(true);
        addKeyboardToUI();
        
        clearTextTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                typedLabel.setText("");
            }
        });
        clearTextTimer.start();
        
        saveSettingsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                notifyListeners();
                frame.requestFocusInWindow();
            }
        });
        
        calibrateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(keyboard == KeyboardType.LEAP.getKeyboard()) {
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
                            keyboard.beginCalibration(typedPanel);
                        }
                    } else {
                        beginCalibration();
                        keyboard.beginCalibration(typedPanel);
                    }
                }
                frame.requestFocusInWindow();
            }
        });
        
        keyboardTypeComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                @SuppressWarnings("unchecked")
                JComboBox<String> comboBox = (JComboBox<String>)e.getSource();
                int selectedIndex = comboBox.getSelectedIndex();
                if(keyboard != KeyboardType.getByID(selectedIndex).getKeyboard()) {
                    if(KeyboardType.LEAP == KeyboardType.getByID(selectedIndex)) {
                        LeapListener.startListening();
                        calibrateButton.setVisible(true);
                        calibrateButton.setEnabled(true);
                        saveSettingsButton.setEnabled(true);
                    } else {
                        LeapListener.stopListening();
                        calibrateButton.setVisible(false);
                        calibrateButton.setEnabled(false);
                        saveSettingsButton.setEnabled(false);
                    }
                    // Remove all settings, attributes, and renderables from the previous keyboard.
                    typedLabel.setText("");
                    settingsPanel.removeAll();
                    renderOptionsPanel.removeAll();
                    
                    // Add all settings, attributes, and renderables from the new keyboard.
                    keyboard = KeyboardType.getByID(selectedIndex).getKeyboard();
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
                // Change to close experiment window.
                //System.exit(0);
                frame.dispose();
            }
        });
        
        canvas.addGLEventListener(this);
        
        pos = new Vector();
        dir = new Vector();
        
        // Something causes the canvas to resize between the original frame.pack() and this one so we just pack again, no harm done.
        frame.pack();
    }
    
    @Override
    public void keyboardKeyEventObserved(char key) {
        clearTextTimer.restart();
        if(key == '\b' && 0 < typedLabel.getText().length()) {
            typedLabel.setText(typedLabel.getText().substring(0, typedLabel.getText().length()-1));
        } else if(key == '\n') {
            typedLabel.setText("");
        } else {
            typedLabel.setText(typedLabel.getText()+Character.toString(key));
        }
        MyUtilities.JAVA_SWING_UTILITIES.calculateFontSize(typedLabel.getText(),typedLabel, typedPanel);
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
        typedPanel.removeAll();
        calibrateButton.setEnabled(false);
        saveSettingsButton.setEnabled(false);
        keyboardTypeComboBox.setEnabled(false);
        clearTextTimer.stop();
        settingsPanel.removeAll();
        KeyboardAttributes ka = keyboard.getAttributes();
        settingsPanel.add(ka.getAttributeByName(Attribute.LEAP_PLANE_POINT_A.toString()).getAttributePanel());
        settingsPanel.add(ka.getAttributeByName(Attribute.LEAP_PLANE_POINT_B.toString()).getAttributePanel());
        settingsPanel.add(ka.getAttributeByName(Attribute.LEAP_PLANE_POINT_C.toString()).getAttributePanel());
    }
    
    private void finishCalibration() {
        typedPanel.add(typedLabel);
        calibrateButton.setEnabled(true);
        saveSettingsButton.setEnabled(true);
        keyboardTypeComboBox.setEnabled(true);
        clearTextTimer.start();
        settingsPanel.removeAll();
        KeyboardAttributes ka = keyboard.getAttributes();
        settingsPanel.add(ka.getAttributeByName(Attribute.KEYBOARD_WIDTH.toString()).getAttributePanel());
        settingsPanel.add(ka.getAttributeByName(Attribute.KEYBOARD_HEIGHT.toString()).getAttributePanel());
        if(keyboard == KeyboardType.STANDARD.getKeyboard()) {
            settingsPanel.add((JPanel) ka.getAttributeByName(Attribute.KEY_BINDINGS.toString()).getValue());
        } else if(keyboard == KeyboardType.LEAP.getKeyboard()) {
            settingsPanel.add(ka.getAttributeByName(Attribute.LEAP_PLANE_POINT_A.toString()).getAttributePanel());
            settingsPanel.add(ka.getAttributeByName(Attribute.LEAP_PLANE_POINT_B.toString()).getAttributePanel());
            settingsPanel.add(ka.getAttributeByName(Attribute.LEAP_PLANE_POINT_C.toString()).getAttributePanel());
        }
        
        for(KeyboardSetting ks: keyboard.getSettings().getAllSettings()) {
            settingsPanel.add(ks.getSettingsPanel());
        }
        frame.pack();
    }
    
    public void disable() {
        frame.setVisible(false);
    }
    
    public void enable() {
        frame.setVisible(true);
        frame.requestFocusInWindow();
    }
    
    public void update() {
        keyboard.update();
    }
    
    public void render(GLAutoDrawable drawable) {
       gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
       keyboard.render(gl);
    }
    
    private void addKeyboardToUI() {
        KeyboardAttributes ka = keyboard.getAttributes();
        settingsPanel.add(ka.getAttributeByName(Attribute.KEYBOARD_WIDTH.toString()).getAttributePanel());
        settingsPanel.add(ka.getAttributeByName(Attribute.KEYBOARD_HEIGHT.toString()).getAttributePanel());
        if(keyboard == KeyboardType.STANDARD.getKeyboard()) {
            settingsPanel.add((JPanel) ka.getAttributeByName(Attribute.KEY_BINDINGS.toString()).getValue());
        } else if(keyboard == KeyboardType.LEAP.getKeyboard()) {
            settingsPanel.add(ka.getAttributeByName(Attribute.LEAP_PLANE_POINT_A.toString()).getAttributePanel());
            settingsPanel.add(ka.getAttributeByName(Attribute.LEAP_PLANE_POINT_B.toString()).getAttributePanel());
            settingsPanel.add(ka.getAttributeByName(Attribute.LEAP_PLANE_POINT_C.toString()).getAttributePanel());
        }
        
        for(KeyboardSetting ks: keyboard.getSettings().getAllSettings()) {
            settingsPanel.add(ks.getSettingsPanel());
        }
        
        for(KeyboardRenderable kr: keyboard.getRenderables().getAllRenderables()) {
            renderOptionsPanel.add(kr.getRenderablePanel());
        }
        
        canvas.setPreferredSize(new Dimension(keyboard.getWidth(), keyboard.getHeight()));
        canvas.setSize(keyboard.getWidth(), keyboard.getHeight());
        frame.pack();
    }
    
    public void registerObserver(SaveSettingsObserver observer) {
        if(observers.contains(observer)) {
            return;
        }
        observers.add(observer);
    }
    
    public void removeObserver(SaveSettingsObserver observer) {
        observers.remove(observer);
    }

    private void notifyListeners() {
        for(SaveSettingsObserver observer : observers) {
            observer.saveSettingsEventObserved(keyboard);
        }
    }
}

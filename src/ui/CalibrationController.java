package ui;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.awt.GLCanvas;

import static javax.media.opengl.GL.*;  // GL constants
import static javax.media.opengl.GL2.*; // GL2 constants
import static javax.media.opengl.fixedfunc.GLMatrixFunc.GL_MODELVIEW;
import static javax.media.opengl.fixedfunc.GLMatrixFunc.GL_PROJECTION;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import keyboard.KeyboardAttributes;
import keyboard.KeyboardRenderable;
import keyboard.KeyboardSetting;
import keyboard.renderables.LeapPlane;
import keyboard.standard.StandardKeyboard;
import utilities.MyUtilities;

import com.leapmotion.leap.Vector;

import enums.AttributeName;
import enums.KeyboardType;
import enums.RenderableName;


public class CalibrationController extends GraphicsController {
    private JFrame frame;
    private JLabel typedLabel;
    private JPanel typedPanel;
    private JComboBox<String> keyboardTypeComboBox;
    private JButton calibrateButton;
    private JPanel settingsPanel;
    private JPanel renderOptionsPanel;
    private Timer clearTextTimer;
    //private Timer typedFocusTimer;
    
    public CalibrationController() {
        keyboard = KeyboardType.STANDARD.getKeyboard();
        canvas = new GLCanvas(capabilities);
        canvas.setPreferredSize(new Dimension(keyboard.getWidth(), keyboard.getHeight()));
        canvas.setSize(keyboard.getWidth(), keyboard.getHeight());
        frame = new JFrame("Calibration");
        typedLabel = new JLabel();
        typedPanel = new JPanel();
        keyboardTypeComboBox = new JComboBox<String>();
        calibrateButton = new JButton("Calibrate");
        settingsPanel = new JPanel();
        renderOptionsPanel = new JPanel();
        
        JPanel panels[] = {typedPanel, settingsPanel, renderOptionsPanel};

        // Window builder builds window using important fields here. It adds unimportant fields that we won't use for aesthetics only.
        WindowBuilder.buildCalibrationWindow(frame, canvas, typedLabel, keyboardTypeComboBox, calibrateButton, panels);
        canvas.setFocusable(true);
        addKeyboardToUI();
        //typedPanel.setFocusable(true);
        //typedPanel.requestFocusInWindow();
        //frame.setVisible(true);
        
        clearTextTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                typedLabel.setText("");
            }
        });
        clearTextTimer.start();
        
        calibrateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(keyboard == KeyboardType.LEAP.getKeyboard()) {
                    // START CALIBRATION EVENT
                    // PUT TEXT WHERE LABEL IS? I suppose.
                    typedPanel.removeAll();
                    clearTextTimer.stop();
                    LeapPlane leapPlane = (LeapPlane) keyboard.getRenderables().getRenderableByName(RenderableName.LEAP_PLANE.toString());
                    if(leapPlane != null) {
                        // leapPlane.calibrate(frame);
                    }
                    typedPanel.add(typedLabel);
                    clearTextTimer.start();
                }
                //typedPanel.requestFocusInWindow();
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
                        calibrateButton.setEnabled(true);
                    } else {
                        calibrateButton.setEnabled(false);
                    }
                    // REMOVE EVERYTHING FROM PEVIOUS KEYBOARD
                    settingsPanel.removeAll();
                    renderOptionsPanel.removeAll();
                    
                    // ADD EVERYTHING FROM NEW KEYBOARD
                    keyboard = KeyboardType.getByID(selectedIndex).getKeyboard();
                    addKeyboardToUI();
                }
                frame.requestFocusInWindow();
                //typedPanel.requestFocusInWindow();
            }
        });
        
        /*typedFocusTimer = new Timer(3000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                typedPanel.requestFocusInWindow();
            }
        });
        
        typedPanel.addFocusListener(new FocusListener() {

            @Override
            public void focusGained(FocusEvent e) {
                //typedFocusTimer.stop();
                clearTextTimer.start();
                typedLabel.setText(typedLabel.getText().replaceFirst("FOCUS LOST", ""));
                typedLabel.setForeground(Color.DARK_GRAY);
            }

            @Override
            public void focusLost(FocusEvent e) {
                //typedFocusTimer.start();
                clearTextTimer.stop();
                typedLabel.setText("FOCUS LOST");
                typedLabel.setForeground(Color.RED);
                MyUtilities.calculateFontSize(typedLabel.getText(), typedLabel, typedPanel);
            }
            
        });*/
        
        // TODO: Change to size of currently selected keyboard.
        
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
    public void keyboardEventObserved(char key) {
        // TODO: Add full implementation required for detailed analysis of key presses
        clearTextTimer.restart();
        typedLabel.setText(typedLabel.getText()+Character.toString(key));
        MyUtilities.calculateFontSize(typedLabel.getText(),typedLabel, typedPanel);
    }
    
    public void disable() {
        frame.setVisible(false);
    }
    
    public void enable() {
        frame.setVisible(true);
        //typedPanel.requestFocusInWindow();
    }
    
    public void update(Vector position, Vector direction) {
        pos = position;
        dir = direction;
        keyboard.update();
    }
    
    public void render(GLAutoDrawable drawable) {
        // TODO: Need to encapsulate all rendering within current selected keyboard. 
        // Only thing that might be appropriate to do here is set the perspectives
        // but even these can be pushed further down into the keyboard.
        
       //GL2 gl = drawable.getGL().getGL2();
       gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
       
       // Setup ortho projection, with aspect ratio matches viewport
       gl.glMatrixMode(GL_PROJECTION);
       gl.glLoadIdentity();
       gl.glOrtho(0, keyboard.getWidth(), 0, keyboard.getHeight(), 0.1, 1000);
  
       // Enable the model-view transform
       gl.glMatrixMode(GL_MODELVIEW);
       gl.glLoadIdentity();
       
       // keyboard.draw() // all data saved in Virtual Keyboard (have listeners there as well)
       // this consists of drawing a box for the whole keyboard to calibrated scale
       // cover this box in the texture
       // draw boxes over all keys to the calibrated scale to detect 3D key presses by leap pos
       keyboard.render(gl);
       
       // Setup ortho projection, with aspect ratio matches viewport
       gl.glMatrixMode(GL_PROJECTION);
       gl.glLoadIdentity();
       float aspect = (float) 800/800; // float aspect = (float) 647f/385f;
       //gl.glViewport((800/2 - 647/2), (800/2 - 385/2), 647, 385);
       glu.gluPerspective(45.0, aspect, 0.1, 1000.0);
  
       // Enable the model-view transform
       gl.glMatrixMode(GL_MODELVIEW);
       gl.glLoadIdentity();
       
       //gl.glLoadIdentity();
       gl.glPushMatrix();
       //gl.glTranslatef(pos.getX(), pos.getY(), pos.getZ());
       gl.glTranslatef(pos.getX()+50, pos.getY()-120.0f, pos.getZ());
       /// TEMP LEAP OFFSET STUFF: System.out.println("leapPos: " + pos + "\t\t glPos: " + "(" + (pos.getX()+50) + ", " + (pos.getY()-120) + ", " + pos.getZ() + ")");
       gl.glBegin(GL_QUADS); // of the color cube
       
       // Top-face
       gl.glColor3f(0.0f, 1.0f, 0.0f); // green
       gl.glVertex3f(3f, 3f, -3f);
       gl.glVertex3f(-3f, 3f, -3f);
       gl.glVertex3f(-3f, 3f, 3f);
       gl.glVertex3f(3f, 3f, 3f);
  
       // Bottom-face
       gl.glColor3f(1.0f, 0.5f, 0.0f); // orange
       gl.glVertex3f(3f, -3f, 3f);
       gl.glVertex3f(-3f, -3f, 3f);
       gl.glVertex3f(-3f, -3f, -3f);
       gl.glVertex3f(3f, -3f, -3f);
  
       // Front-face
       gl.glColor3f(1.0f, 0.0f, 0.0f); // red
       gl.glVertex3f(3f, 3f, 3f);
       gl.glVertex3f(-3f, 3f, 3f);
       gl.glVertex3f(-3f, -3f, 3f);
       gl.glVertex3f(3f, -3f, 3f);
  
       // Back-face
       gl.glColor3f(1.0f, 1.0f, 0.0f); // yellow
       gl.glVertex3f(3f, -3f, -3f);
       gl.glVertex3f(-3f, -3f, -3f);
       gl.glVertex3f(-3f, 3f, -3f);
       gl.glVertex3f(3f, 3f, -3f);
  
       // Left-face
       gl.glColor3f(0.0f, 0.0f, 1.0f); // blue
       gl.glVertex3f(-3f, 3f, 3f);
       gl.glVertex3f(-3f, 3f, -3f);
       gl.glVertex3f(-3f, -3f, -3f);
       gl.glVertex3f(-3f, -3f, 3f);
  
       // Right-face
       gl.glColor3f(1.0f, 0.0f, 1.0f); // magenta
       gl.glVertex3f(3f, 3f, -3f);
       gl.glVertex3f(3f, 3f, 3f);
       gl.glVertex3f(3f, -3f, 3f);
       gl.glVertex3f(3f, -3f, -3f);
  
       gl.glEnd(); // of the color cube*/
       gl.glPopMatrix();
    }
    
    private void addKeyboardToUI() {
        KeyboardAttributes ka = keyboard.getAttributes();
        settingsPanel.add(ka.getAttributeByName(AttributeName.KEYBOARD_WIDTH.toString()).getAttributePanel());
        settingsPanel.add(ka.getAttributeByName(AttributeName.KEYBOARD_HEIGHT.toString()).getAttributePanel());
        if(keyboard == KeyboardType.STANDARD.getKeyboard()) {
            settingsPanel.add((JPanel) ka.getAttributeByName(AttributeName.KEY_BINDINGS.toString()).getValue());
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
}

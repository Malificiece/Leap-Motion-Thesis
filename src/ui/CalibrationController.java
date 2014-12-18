package ui;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.awt.GLJPanel;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

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

import utilities.MyUtilities;
import keyboard.VirtualKeyboard;

import com.jogamp.opengl.util.FPSAnimator;
import com.leapmotion.leap.Vector;


public class CalibrationController extends GraphicsController {
    private JFrame frame;
    private JLabel typed;
    private JPanel typedPanel;
    private JComboBox<String> keyboardType;
    private JButton calibrate;
    private JPanel settings;
    private JPanel renderOptions;
    private Timer clearTextTimer;
    private Timer typedFocusTimer;
    
    public CalibrationController() {
        canvas = new GLCanvas(capabilities);
        frame = new JFrame("Calibration");
        typed = new JLabel();
        typedPanel = new JPanel();
        keyboardType = new JComboBox<String>();
        calibrate = new JButton("Calibrate");
        settings = new JPanel();
        renderOptions = new JPanel();
        
        JPanel panels[] = {settings, renderOptions, typedPanel};
        
        // Window builder builds window using important fields here. It adds unimportant fields that we won't use for aesthetics only.
        WindowBuilder.buildCalibrationWindow(frame, canvas, typed, keyboardType, calibrate, panels);
        canvas.setFocusable(false);
        typedPanel.setFocusable(true);
        typedPanel.requestFocusInWindow();
        //frame.setVisible(true);
        
        clearTextTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                typed.setText("");
            }
        });
        clearTextTimer.start();
        
        typedFocusTimer = new Timer(3000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                typedPanel.requestFocusInWindow();
            }
        });
        
        typedPanel.addFocusListener(new FocusListener() {

            @Override
            public void focusGained(FocusEvent arg0) {
                typedFocusTimer.stop();
                clearTextTimer.start();
                typed.setText(typed.getText().replaceFirst("FOCUS LOST", ""));
                typed.setForeground(Color.DARK_GRAY);
            }

            @Override
            public void focusLost(FocusEvent arg0) {
                typedFocusTimer.start();
                clearTextTimer.stop();
                typed.setText("FOCUS LOST");
                typed.setForeground(Color.RED);
                MyUtilities.calculateFontSize(typed.getText(), typed, typedPanel);
            }
            
        });
        
        /*
        typedPanel.addKeyListener(new KeyListener() {

            @Override
            public void keyPressed(KeyEvent e) {
                // TODO Auto-generated method stub
            }

            @Override
            public void keyReleased(KeyEvent e) {
                // TODO Auto-generated method stub 
            }

            @Override
            public void keyTyped(KeyEvent e) {
                clearTextTimer.restart();
                typed.setText(typed.getText()+Character.toString(e.getKeyChar()));
                MyUtilities.calculateFontSize(typed.getText(), typed, typedPanel);
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
                
            }
        });
        
        canvas.addGLEventListener(this);
        
        pos = new Vector();
        dir = new Vector();
    }
    
    @Override
    public void keyboardEventObserved(char key) {
        // TODO: Add full implementation required for detailed analysis of key presses
        clearTextTimer.restart();
        typed.setText(typed.getText()+Character.toString(key));
        MyUtilities.calculateFontSize(typed.getText(), typed, typedPanel);
    }
    
    public void disable() {
        frame.setVisible(false);
    }
    
    public void enable() {
        frame.setVisible(true);
        typedPanel.requestFocusInWindow();
    }
    
    public void update(Vector position, Vector direction) {
        pos = position;
        dir = direction;
    }
    
    public void render(GLAutoDrawable drawable) {
        // TODO: Need to encapsulate all rendering within current selected keyboard. 
        // Only thing that might be appropriate to do here is set the perspectives
        // but even these can be pushed further down into the keyboard.
        
       GL2 gl = drawable.getGL().getGL2();
       gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
       
       // Setup ortho projection, with aspect ratio matches viewport
       gl.glMatrixMode(GL_PROJECTION);
       gl.glLoadIdentity();
       gl.glOrtho(0, 647, 0, 385, 0.1, 1000);
  
       // Enable the model-view transform
       gl.glMatrixMode(GL_MODELVIEW);
       gl.glLoadIdentity();
       
       // keyboard.draw() // all data saved in Virtual Keyboard (have listeners there as well)
       // this consists of drawing a box for the whole keyboard to calibrated scale
       // cover this box in the texture
       // draw boxes over all keys to the calibrated scale to detect 3D key presses by leap pos
       VirtualKeyboard.render(gl, pos);
       
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
}

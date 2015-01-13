package ui;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import enums.Keyboard;

public class ExperimentController extends GraphicsController {
    private JFrame frame;
    private GLCanvas textCanvas;
    
    private double theta = 0;
    private double s = 0;
    private double c = 0;
    
    public ExperimentController() {
        keyboard = Keyboard.STANDARD.getKeyboard();
        canvas = new GLCanvas(capabilities);
        textCanvas = new GLCanvas(capabilities);
        frame = new JFrame("Experimental Run -- IMPORTANT DATA HERE");
        frame.setSize(600, 700);
        frame.setLocation(300,75);
        //frame.setLayout(new FlowLayout());
        
        JButton run = new JButton();
        run.setText("Run");
        run.setSize(100, 50);
        JButton calibrate = new JButton();
        calibrate.setText("Calibrate");
        calibrate.setSize(100, 50);
        JButton test = new JButton();
        test.setText("Test");
        test.setSize(100, 50);
        //frame.add(run);

        JPanel p = new JPanel();
        p.setBackground(new Color(155, 200, 100));
        p.add(run);
        frame.add(p, BorderLayout.NORTH);
        
        frame.add(test, BorderLayout.CENTER);
        frame.add(canvas, BorderLayout.CENTER);
        
        JPanel p2 = new JPanel();
        p2.setBackground(new Color(155, 100, 200));
        frame.getContentPane().add(p2, BorderLayout.SOUTH);
        p2.add(calibrate);

        
        // by default, an AWT Frame doesn't do anything when you click
        // the close button; this bit of code will terminate the program when
        // the window is asked to close
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                // Change to close experiment window.
                disable();
                frame.dispose();
                //System.exit(0);
            }
        });
    }
    
    @Override
    public void display() {
        super.display();
        if(textCanvas != null) {
            textCanvas.display();
        }
    }
    
    public void disable() {
        frame.setVisible(false);
        canvas.removeGLEventListener(this);
        textCanvas.removeGLEventListener(this);
        Keyboard.STANDARD.getKeyboard().removeObserver(this);
        Keyboard.LEAP.getKeyboard().removeObserver(this);
        Keyboard.TABLET.getKeyboard().removeObserver(this);
        Keyboard.CONTROLLER.getKeyboard().removeObserver(this);
        enabled = false;
    }
    
    public void enable() {
        frame.setVisible(true);
        frame.requestFocusInWindow();
        canvas.addGLEventListener(this);
        textCanvas.addGLEventListener(this);
        Keyboard.STANDARD.getKeyboard().registerObserver(this);
        Keyboard.LEAP.getKeyboard().registerObserver(this);
        Keyboard.TABLET.getKeyboard().registerObserver(this);
        Keyboard.CONTROLLER.getKeyboard().registerObserver(this);
        enabled = true;
    }
    
    public void update() {
        theta += 0.01;
        s = Math.sin(theta);
        c = Math.cos(theta);
    }
    
   public void render(GLAutoDrawable drawable) {

        gl.glClear(GL.GL_COLOR_BUFFER_BIT);

        // draw a triangle filling the window
        gl.glBegin(GL.GL_TRIANGLES);
        gl.glColor3f(1, 0, 0);
        gl.glVertex2d(-c, -c);
        gl.glColor3f(0, 1, 0);
        gl.glVertex2d(0, c);
        gl.glColor3f(0, 0, 1);
        gl.glVertex2d(s, -s);
        gl.glEnd();
    }

    @Override
    public void keyboardKeyEventObserved(char key) {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void keyboardCalibrationFinishedEventObserved() {
        // TODO Auto-generated method stub
        
    }
}

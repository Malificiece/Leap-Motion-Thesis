package ui;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.leapmotion.leap.Vector;


public class ExperimentController extends GraphicsController {
    private JFrame frame;
    
    private double theta = 0;
    private double s = 0;
    private double c = 0;
    
    public ExperimentController() {
        canvas = new GLCanvas(capabilities);
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
        


        // Add a layout manager so that the button is not placed on top of the label
        //frame.setLayout(new FlowLayout());
        // Add a label and a button
        //frame.add(new JLabel("Hello, world!"));
        //frame.add(new JButton("Press me!"));
        // Arrange the components inside the window
        //frame.pack();
        
        //frame.setVisible(true);
        
        // by default, an AWT Frame doesn't do anything when you click
        // the close button; this bit of code will terminate the program when
        // the window is asked to close
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                // Change to close experiment window.
                //frame.dispose();
                //System.exit(0);
            }
        });
        
        canvas.addGLEventListener(this);
    }
    
    @Override
    public void keyboardEventObserved(char key) {
        // TODO Auto-generated method stub
        
    }
    
    public void disable() {
        frame.setVisible(false);
    }
    
    public void enable() {
        frame.setVisible(true);
    }
    
    public void update(/*Vector position, Vector direction*/) {
        theta += 0.01;
        s = Math.sin(theta);
        c = Math.cos(theta);
    }
    
   public void render(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();

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
}

package ui;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.awt.GLJPanel;
import javax.media.opengl.glu.GLU;

import keyboard.VirtualKeyboard;
import static javax.media.opengl.GL.*;  // GL constants
import static javax.media.opengl.GL2.*; // GL2 constants

import com.leapmotion.leap.Vector;


public abstract class GraphicsController implements GLEventListener {
	private static GLProfile profile;
	protected static GLCapabilities capabilities;
	protected GLCanvas canvas;
	protected GLU glu;  // for the GL Utility
	protected Vector pos;
	protected Vector dir;
	
	public static void init() {
	    GLProfile.initSingleton();
		profile = GLProfile.getDefault();
		capabilities = new GLCapabilities(profile);
	}
	
	public abstract void update(Vector position, Vector direction);
	public abstract void render(GLAutoDrawable drawable);
	
	public void display() {
	    if(canvas != null) {
	        canvas.display();
	    }
	}

    @Override
    public void display(GLAutoDrawable drawable) {
        render(drawable);
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
        // TODO Auto-generated method stub
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();      // get the OpenGL graphics context
        glu = new GLU();                         // get GL Utilities
        // TODO: Remove this initialization. Change keyboards from singleton to instance. Move keyboard creation to somewhere that makes sense (keyboard selection in calib/run buttons).
        VirtualKeyboard.init(gl);
        gl.glClearColor(0.2f, 0.2f, 0.2f, 0.0f); // set background (clear) color
        gl.glClearDepth(1.0f);      // set clear depth value to farthest
        gl.glEnable(GL_DEPTH_TEST); // enables depth testing
        gl.glDepthFunc(GL_LEQUAL);  // the type of depth test to do
        gl.glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST); // best perspective correction
        //gl.glShadeModel(GL_SMOOTH); // blends colors nicely, and smoothes out lighting
        //gl.glEnable(GL_LIGHTING);
   
        // ----- Your OpenGL initialization code here -----
        
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL2 gl = drawable.getGL().getGL2();
        if (height == 0) height = 1;   // prevent divide by zero
   
        // Set the view port (display area) to cover the entire window
        gl.glViewport((width/2 - 647/2), (height/2 - 385/2), 647, 385);
   
        // Setup ortho projection, with aspect ratio matches viewport
        gl.glMatrixMode(GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrtho(0, 647, 0, 385, 0.1, 1000);
   
        // Enable the model-view transform
        gl.glMatrixMode(GL_MODELVIEW);
        gl.glLoadIdentity();
        
        // If we replace the raster img with a 3D texture
        //float aspect = (float) width/height; // float aspect = (float) 647f/385f;
        //gl.glViewport(0, 0, width, height);
        //glu.gluPerspective(45.0, aspect, 0.1, 1000.0);
    }
}

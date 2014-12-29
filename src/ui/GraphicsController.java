package ui;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;

import utilities.MyUtilities;
import keyboard.IKeyboard;
import keyboard.KeyboardObserver;
import static javax.media.opengl.GL.*;  // GL constants
import static javax.media.opengl.GL2.*; // GL2 constants

import com.leapmotion.leap.Vector;

import enums.KeyboardType;


public abstract class GraphicsController implements GLEventListener, KeyboardObserver {
	private static GLProfile profile;
	protected static GLCapabilities capabilities;
	protected GLCanvas canvas;
	protected GLU glu;  // for the GL Utility
	public static GL2 gl;
	protected Vector pos;
	protected Vector dir;
	protected IKeyboard keyboard;
	
	public static void init() {
	    GLProfile.initSingleton();
		profile = GLProfile.getDefault();
		capabilities = new GLCapabilities(profile);
	}
	
	public abstract void update();
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
        GraphicsController.gl = drawable.getGL().getGL2();      // get the OpenGL graphics context
        glu = new GLU();                         // get GL Utilities
        
        // TODO: Make sure this is where I want to register observers.
        KeyboardType.STANDARD.getKeyboard().registerObserver(this);
        KeyboardType.LEAP.getKeyboard().registerObserver(this);
        KeyboardType.TABLET.getKeyboard().registerObserver(this);
        KeyboardType.CONTROLLER.getKeyboard().registerObserver(this);

        gl.glClearColor(1f, 1f, 1f, 0.0f); // set background (clear) color
        gl.glClearDepth(1.0f);      // set clear depth value to farthest
        gl.glEnable(GL_DEPTH_TEST); // enables depth testing
        gl.glDepthFunc(GL_LEQUAL);  // the type of depth test to do
        gl.glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST); // best perspective correction
        //gl.glShadeModel(GL_SMOOTH); // blends colors nicely, and smoothes out lighting
        //gl.glEnable(GL_LIGHTING);
   
        // ----- Your OpenGL initialization code here -----
        /*float lightpos[] = {0f, 10f,0f, 1f};
        ByteBuffer vbb = ByteBuffer.allocateDirect(16); 
        vbb.order(ByteOrder.nativeOrder());    // use the device hardware's native byte order
        FloatBuffer light = vbb.asFloatBuffer();  // create a floating point buffer from the ByteBuffer
        light.put(lightpos);
        light.position(0);
        gl.glLightfv(GL_LIGHT0, GL_POSITION, light);
        gl.glEnable(GL_LIGHT0);*/
        
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) { 
        MyUtilities.OPEN_GL_UTILITIES.reshape(gl, glu, keyboard);
        
        // CHECK THIS OUT IF WE NEED IT --- OLD VIEWPORT CODE
        
        //if (height == 0) height = 1;   // prevent divide by zero
   
        // Set the view port (display area) to cover the entire window
        //gl.glViewport((width/2 - keyboard.getWidth()/2), (height/2 - keyboard.getHeight()/2), keyboard.getWidth(), keyboard.getHeight());
   
        // Setup ortho projection, with aspect ratio matches viewport
        /*gl.glMatrixMode(GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrtho(0, keyboard.getWidth(), 0, keyboard.getHeight(), 0.1, 1000);
   
        // Enable the model-view transform
        gl.glMatrixMode(GL_MODELVIEW);
        gl.glLoadIdentity();*/
        
        // If we replace the raster img with a 3D texture
        //float aspect = (float) width/height; // float aspect = (float) 647f/385f;
        //gl.glViewport(0, 0, width, height);
        //glu.gluPerspective(45.0, aspect, 0.1, 1000.0);
    }
}

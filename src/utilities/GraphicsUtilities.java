package utilities;

import static javax.media.opengl.GL2.*;

import javax.media.opengl.GL2;

import ui.GraphicsController;
import keyboard.IKeyboard;

public class GraphicsUtilities {
    public static final float FOVY = 45f;
    private boolean isOrtho = false;
    private IKeyboard keyboard;
    
    // Need to add in all the open GL stuff here to switch perspectives easily from 2D to 3D etc
    // Anything else we might need with open GL as well
    public void switchToPerspective(GL2 gl, IKeyboard keyboard, boolean loadIdentity) {
        this.keyboard = keyboard;
        if(isOrtho) {
            perspective(gl, keyboard);
            if(loadIdentity) gl.glLoadIdentity();
            isOrtho = false;
        } else {
            if(loadIdentity) gl.glLoadIdentity();
        }
    }
    
    public void switchToOrthogonal(GL2 gl, IKeyboard keyboard, boolean loadIdentity) {
        this.keyboard = keyboard;
        if(!isOrtho) {
            orthogonal(gl, keyboard);
            if(loadIdentity) gl.glLoadIdentity();
            isOrtho = true;
        } else {
            if(loadIdentity) gl.glLoadIdentity();
        }
    }
    
    public void switchToPerspective(GL2 gl, boolean loadIdentity) {
        if(isOrtho) {
            perspective(gl, keyboard);
            if(loadIdentity) gl.glLoadIdentity();
            isOrtho = false;
        } else {
            if(loadIdentity) gl.glLoadIdentity();
        }
    }
    
    public void switchToOrthogonal(GL2 gl, boolean loadIdentity) {
        if(!isOrtho) {
            orthogonal(gl, keyboard);
            if(loadIdentity) gl.glLoadIdentity();
            isOrtho = true;
        } else {
            if(loadIdentity) gl.glLoadIdentity();
        }
    }
    
    public void reshape(GL2 gl, IKeyboard keyboard) {
        this.keyboard = keyboard;
        // Set viewport
        gl.glViewport(0, 0, keyboard.getImageWidth(), keyboard.getImageHeight());
        
        // reuse current view
        if(isOrtho) {
            orthogonal(gl, keyboard);
        } else {
            perspective(gl, keyboard);
        }
        gl.glLoadIdentity();
    }
    
    private void perspective(GL2 gl, IKeyboard keyboard) {
        // Setup perspective projection, with aspect ratio matches viewport
        gl.glMatrixMode(GL_PROJECTION);
        gl.glLoadIdentity();
        float aspect = keyboard.getImageWidth()/ (float) keyboard.getImageHeight();
        GraphicsController.glu.gluPerspective(FOVY, aspect, 0.1, 1000.0);
   
        // Enable the model-view transform
        gl.glMatrixMode(GL_MODELVIEW);
    }
    
    private void orthogonal(GL2 gl, IKeyboard keyboard) {
        // Setup ortho projection, with aspect ratio matches viewport.
        gl.glMatrixMode(GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrtho(0, keyboard.getImageWidth(), 0, keyboard.getImageHeight(), 0.1, 1000);
   
        // Enable the model-view transform
        gl.glMatrixMode(GL_MODELVIEW);
    }
}

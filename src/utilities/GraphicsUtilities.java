package utilities;

import static javax.media.opengl.GL2.*;

import javax.media.opengl.GL2;

import ui.GraphicsController;
import keyboard.IKeyboard;

public class GraphicsUtilities {
    private boolean isOrtho = false;
    
    // Need to add in all the open GL stuff here to switch perspectives easily from 2D to 3D etc
    // Anything else we might need with open GL as well
    public void switchToPerspective(GL2 gl, IKeyboard keyboard, boolean loadIdentity) {
        if(isOrtho) {
            perspective(gl, keyboard);
            if(loadIdentity) gl.glLoadIdentity();
            isOrtho = false;
        } else {
            if(loadIdentity) gl.glLoadIdentity();
        }
    }
    
    public void switchToOrthogonal(GL2 gl, IKeyboard keyboard, boolean loadIdentity) {
        if(!isOrtho) {
            orthogonal(gl, keyboard);
            if(loadIdentity) gl.glLoadIdentity();
            isOrtho = true;
        } else {
            if(loadIdentity) gl.glLoadIdentity();
        }
    }
    
    public void reshape(GL2 gl, IKeyboard keyboard) {
        // Set viewport
        gl.glViewport(0, 0, keyboard.getWidth(), keyboard.getHeight());
        
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
        float aspect = keyboard.getWidth()/ (float) keyboard.getHeight();
        GraphicsController.glu.gluPerspective(45.0, aspect, 0.1, 1000.0);
   
        // Enable the model-view transform
        gl.glMatrixMode(GL_MODELVIEW);
    }
    
    private void orthogonal(GL2 gl, IKeyboard keyboard) {
        // Setup ortho projection, with aspect ratio matches viewport.
        gl.glMatrixMode(GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrtho(0, keyboard.getWidth(), 0, keyboard.getHeight(), 0.1, 1000);
   
        // Enable the model-view transform
        gl.glMatrixMode(GL_MODELVIEW);
    }
}

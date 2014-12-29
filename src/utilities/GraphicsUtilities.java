package utilities;

import static javax.media.opengl.fixedfunc.GLMatrixFunc.GL_MODELVIEW;
import static javax.media.opengl.fixedfunc.GLMatrixFunc.GL_PROJECTION;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import keyboard.IKeyboard;

public class GraphicsUtilities {
    private boolean isOrtho = false;
    
    // Need to add in all the open GL stuff here to switch perspectives easily from 2D to 3D etc
    // Anything else we might need with open GL as well
    public void switchToPerspective(GL2 gl, GLU glu, IKeyboard keyboard) {
        if(isOrtho) {
            perspective(gl, glu, keyboard);
            isOrtho = false;
        } else {
            gl.glLoadIdentity();
        }
    }
    
    public void switchToOrthogonal(GL2 gl, IKeyboard keyboard) {
        if(!isOrtho) {
            orthogonal(gl, keyboard);
            isOrtho = true;
        } else {
            gl.glLoadIdentity();
        }
    }
    
    public void reshape(GL2 gl, GLU glu, IKeyboard keyboard) {
        // Set viewport
        gl.glViewport(0, 0, keyboard.getWidth(), keyboard.getHeight());
        
        // reuse current view
        if(isOrtho) {
            orthogonal(gl, keyboard);
        } else {
            perspective(gl, glu, keyboard);
        }
    }
    
    private void perspective(GL2 gl, GLU glu, IKeyboard keyboard) {
        // Setup perspective projection, with aspect ratio matches viewport
        gl.glMatrixMode(GL_PROJECTION);
        gl.glLoadIdentity();
        float aspect = keyboard.getWidth()/ (float) keyboard.getHeight();
        glu.gluPerspective(45.0, aspect, 0.1, 1000.0);
   
        // Enable the model-view transform
        gl.glMatrixMode(GL_MODELVIEW);
        gl.glLoadIdentity();
    }
    
    private void orthogonal(GL2 gl, IKeyboard keyboard) {
        // Setup ortho projection, with aspect ratio matches viewport.
        gl.glMatrixMode(GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrtho(0, keyboard.getWidth(), 0, keyboard.getHeight(), 0.1, 1000);
   
        // Enable the model-view transform
        gl.glMatrixMode(GL_MODELVIEW);
        gl.glLoadIdentity();
    }
}

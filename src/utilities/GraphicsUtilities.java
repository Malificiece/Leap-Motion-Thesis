/*
 * Copyright (c) 2015, Garrett Benoit. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */ 

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
        GraphicsController.GLU.gluPerspective(FOVY, aspect, 0.1, 1000.0);
   
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

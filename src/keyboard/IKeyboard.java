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

package keyboard;

import utilities.Point;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.media.opengl.GL2;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.JPanel;

import enums.FileExt;
import enums.FilePath;
import enums.KeyboardType;
import experiment.data.DataManager;
import experiment.playback.PlaybackManager;
import utilities.MyUtilities;

public abstract class IKeyboard {
    protected final KeyboardType KEYBOARD_TYPE;
    protected final String KEYBOARD_NAME;
    protected final String KEYBOARD_FILE_NAME;
    protected final String KEYBOARD_FILE_PATH;
    private ArrayList<KeyboardObserver> observers = new ArrayList<KeyboardObserver>();
    protected Point keyboardSize;
    protected Point imageSize;
    protected KeyboardSettings keyboardSettings;
    protected KeyboardAttributes keyboardAttributes;
    protected KeyboardRenderables keyboardRenderables;
    protected char keyPressed;
    protected boolean isPlayback = false;
    protected PlaybackManager playbackManager;
    
    public IKeyboard(KeyboardType keyboardType) {
        KEYBOARD_TYPE = keyboardType;
        KEYBOARD_NAME = keyboardType.getName();
        KEYBOARD_FILE_NAME = keyboardType.getFileName();
        KEYBOARD_FILE_PATH = keyboardType.getFileName() + "/";
    }
    
    public abstract void render(GL2 gl);
    public abstract void update();
    
    protected abstract boolean isPlayingBack();
    public abstract void beginPlayback(PlaybackManager playbackManager);
    public abstract void finishPlayback(PlaybackManager playbackManager);
    
    public abstract void beginExperiment(DataManager dataManager);
    public abstract void finishExperiment(DataManager dataManager);
    
    public abstract void beginCalibration(JPanel textPanel);
    protected abstract void finishCalibration();
    public abstract boolean isCalibrated();
    
    public abstract void addToUI(JPanel panel, GLCanvas canvas);
    public abstract void removeFromUI(JPanel panel, GLCanvas canvas);
    
    public KeyboardType getType() {
        return KEYBOARD_TYPE;
    }
    
    public String getName() {
        return KEYBOARD_NAME;
    }
    
    public String getFilePath() {
        return KEYBOARD_FILE_PATH;
    }
    
    public String getFileName() {
        return KEYBOARD_FILE_NAME;
    }
    
    public float getHeight() {
        return keyboardSize.y;
    }
    public float getWidth() {
        return keyboardSize.x;
    }
    
    public int getImageHeight() {
        return (int) imageSize.y;
    }
    
    public int getImageWidth() {
        return (int) imageSize.x;
    }
    
    public KeyboardSettings getSettings() {
        return keyboardSettings;
    }
    public KeyboardAttributes getAttributes() {
        return keyboardAttributes;
    }
    public KeyboardRenderables getRenderables() {
        return keyboardRenderables;
    }
    
    public void registerObserver(KeyboardObserver observer) {
        if(observers.contains(observer)) {
            return;
        }
        observers.add(observer);
    }
    
    public void removeObserver(KeyboardObserver observer) {
        observers.remove(observer);
    }

    protected void notifyListenersKeyEvent() {
        for(KeyboardObserver observer : observers) {
            observer.keyboardKeyEventObserved(keyPressed);
        }
    }
    
    protected void notifyListenersCalibrationFinished() {
        for(KeyboardObserver observer : observers) {
            observer.keyboardCalibrationFinishedEventObserved();
        }
    }
    
    public void loadSettings(String filePath) {
        System.out.println(KEYBOARD_NAME + " - Loading playback settings from " + filePath + "\\" + KEYBOARD_FILE_NAME + FileExt.INI.getExt());
        try {
            MyUtilities.FILE_IO_UTILITIES.readSettingsAndAttributesFromFile(filePath, KEYBOARD_FILE_NAME + FileExt.INI.getExt(), this);
        } catch (IOException e) {
            System.out.println("Error occured while reading settings from file. Using default values on unreached settings.");
            e.printStackTrace();
        }
        System.out.println("-------------------------------------------------------");
    }
    
    public void loadSettings(File file) {
        System.out.println(KEYBOARD_NAME + " - Loading playback settings from " + file.getPath());
        try {
            MyUtilities.FILE_IO_UTILITIES.readSettingsAndAttributesFromFile(file, this);
        } catch (IOException e) {
            System.out.println("Error occured while reading settings from file. Using default values on unreached settings.");
            e.printStackTrace();
        }
        System.out.println("-------------------------------------------------------");
    }
    
    public void loadDefaultSettings() {
        System.out.println(KEYBOARD_NAME + " - Loading default settings from " + FilePath.CONFIG.getPath() + KEYBOARD_FILE_NAME + FileExt.INI.getExt());
        try {
            MyUtilities.FILE_IO_UTILITIES.readSettingsAndAttributesFromFile(FilePath.CONFIG.getPath(), KEYBOARD_FILE_NAME + FileExt.INI.getExt(), this);
        } catch (IOException e) {
            System.out.println("Error occured while reading settings from file. Using default values on unreached settings.");
            e.printStackTrace();
        }
        System.out.println("-------------------------------------------------------");
    }
    
    public void saveSettings() {
        // Save all settings and attributes to file (stored for next time program launched)
        System.out.println(KEYBOARD_NAME + " - Saving Settings to " + FilePath.CONFIG.getPath() + KEYBOARD_FILE_NAME + FileExt.INI.getExt());
        try {
            MyUtilities.FILE_IO_UTILITIES.writeSettingsAndAttributesToFile(FilePath.CONFIG.getPath(), KEYBOARD_FILE_NAME + FileExt.INI.getExt(), this);
            System.out.println("Save Success");
        } catch (IOException e) {
            System.out.println("Save Failed");
            e.printStackTrace();
        }
    }
    
    public void saveSettings(String filePath) {
        // Save all settings and attributes to file (stored for next time program launched)
        System.out.println(KEYBOARD_NAME + " - Saving Settings to " + filePath + KEYBOARD_FILE_NAME + FileExt.INI.getExt());
        try {
            MyUtilities.FILE_IO_UTILITIES.writeSettingsAndAttributesToFile(filePath, KEYBOARD_FILE_NAME + FileExt.INI.getExt(), this);
            System.out.println("Save Success");
        } catch (IOException e) {
            System.out.println("Save Failed");
            e.printStackTrace();
        }
    }
}

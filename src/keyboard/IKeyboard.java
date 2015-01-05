package keyboard;

import java.io.IOException;
import java.util.ArrayList;

import javax.media.opengl.GL2;
import javax.swing.JPanel;

import enums.FileExt;
import enums.FilePath;
import ui.SaveSettingsObserver;
import utilities.MyUtilities;

public abstract class IKeyboard implements SaveSettingsObserver {
    private int keyboardID;
    private String keyboardName;
    private String filePath;
    private String fileName;
    private ArrayList<KeyboardObserver> observers = new ArrayList<KeyboardObserver>();
    protected KeyboardSettings keyboardSettings;
    protected KeyboardAttributes keyboardAttributes;
    protected KeyboardRenderables keyboardRenderables;
    protected KeyboardAttribute keyboardWidth;
    protected KeyboardAttribute keyboardHeight;
    protected char keyPressed;
    
    public IKeyboard(int keyboardID, String keyboardName, String fileName) {
        this.keyboardID = keyboardID;
        this.keyboardName = keyboardName;
        this.fileName = fileName;
        this.filePath = fileName + "/";
    }
    
    public abstract void render(GL2 gl);
    public abstract void update();
    
    public abstract void beginCalibration(JPanel textPanel);
    protected abstract void finishCalibration();
    public abstract boolean isCalibrated();
    
    public int getID() {
        return keyboardID;
    }
    
    public String getName() {
        return keyboardName;
    }
    
    public String getFilePath() {
        return filePath;
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public int getHeight() {
        return keyboardHeight.getValueAsInteger();
    }
    public int getWidth() {
        return keyboardWidth.getValueAsInteger();
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
    
    public void saveSettingsEventObserved(IKeyboard keyboard) {
        // Save all settings and attributes to file (stored for next time program launched)
        if(keyboardID == keyboard.getID()) {
            System.out.println(keyboardName + " - Saving Settings to " + FilePath.CONFIG.getPath() + fileName + FileExt.INI.getExt());
            try {
                MyUtilities.FILE_IO_UTILITIES.writeSettingsAndAttributesToFile(FilePath.CONFIG.getPath(), fileName + FileExt.INI.getExt(), this);
                System.out.println("Save Success");
            } catch (IOException e) {
                System.out.println("Save Failed");
                e.printStackTrace();
            }
        }
    }
}

package keyboard;

import java.io.IOException;
import java.util.ArrayList;

import javax.media.opengl.GL2;
import javax.swing.JPanel;

import enums.FileExtension;
import enums.FilePath;
import enums.KeyboardType;
import ui.SaveSettingsObserver;
import utilities.MyUtilities;

public abstract class IKeyboard implements SaveSettingsObserver {
    private int keyboardID;
    private String filePath;
    private String fileName;
    private ArrayList<KeyboardObserver> observers = new ArrayList<KeyboardObserver>();
    protected KeyboardSettings keyboardSettings;
    protected KeyboardAttributes keyboardAttributes;
    protected KeyboardRenderables keyboardRenderables;
    protected KeyboardAttribute keyboardWidth;
    protected KeyboardAttribute keyboardHeight;
    protected char keyPressed;
    
    public IKeyboard(int keyboardID, String fileName) {
        this.keyboardID = keyboardID;
        this.fileName = fileName;
        this.filePath = fileName + "/";
    }
    
    public abstract void render(GL2 gl);
    public abstract void update();
    
    public abstract void beginCalibration(JPanel textPanel);
    protected abstract void finishCalibration();
    public abstract boolean isCalibrated();
    
    public int getKeyboardID() {
        return keyboardID;
    }
    
    public String getKeyboardFilePath() {
        return filePath;
    }
    
    public String getKeyboardFileName() {
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
        if(keyboardID == keyboard.getKeyboardID()) {
            System.out.print(KeyboardType.getByID(keyboardID).getKeyboardName() + ": Saving Settings to file - ");
            try {
                MyUtilities.FILE_IO_UTILITIES.writeSettingsToFile(FilePath.CONFIG_PATH.getPath(), fileName + FileExtension.INI.getExtension(), keyboardSettings);
                System.out.println("Success.");
            } catch (IOException e) {
                System.out.println("Failure.");
                e.printStackTrace();
            }
        }
    }
}

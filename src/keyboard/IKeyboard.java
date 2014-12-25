package keyboard;

import java.util.ArrayList;

import javax.media.opengl.GL2;

import enums.FilePath;
import ui.SaveSettingsObserver;
import utilities.MyUtilities;

public abstract class IKeyboard implements SaveSettingsObserver {
    // TODO: list of things we need from the keyboard as an outside party
    // 1 - render the keyboard
    // 2 - get the attributes of the keyboard (size, etc)
    // 3 - get/set the settings of the keyboard (settings I can change)
    // 4 - get/set the renderables of the keyboard (image, key colors, leap plane etc)
    // 5 - give it key pressed events (to render them properly) --- and record specialized data for the specific type of keyboard
    // 6 - must be capable of creating it's own keyboard events using the robot if not the default (This will require a self sustaining update function possibly)
    private int keyboardID;
    private String filePath;
    private ArrayList<KeyboardObserver> observers = new ArrayList<KeyboardObserver>();
    protected KeyboardSettings keyboardSettings;
    protected KeyboardAttributes keyboardAttributes;
    protected KeyboardRenderables keyboardRenderables;
    protected KeyboardAttribute keyboardWidth;
    protected KeyboardAttribute keyboardHeight;
    protected char keyPressed;
    
    public IKeyboard(int keyboardID, String filePath) {
        this.keyboardID = keyboardID;
        this.filePath = filePath;
    }
    
    public abstract void render(GL2 gl);
    public abstract void update();
    
    public int getKeyboardID() {
        return keyboardID;
    }
    
    public String getKeyboardFilePath() {
        return filePath;
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

    protected void notifyListeners() {
        for(KeyboardObserver observer : observers) {
            observer.keyboardEventObserved(keyPressed);
        }
    }
    
    public void saveSettingsEventObserved(IKeyboard keyboard) {
        // Save all settings and attributes to file (stored for next time program launched)
        if(keyboardID == keyboard.getKeyboardID()) {
            System.out.println(keyboard + ": Saving Settings and Attributes to file");
            MyUtilities.FILE_IO_UTILITIES.writeAttributeListToFile(FilePath.CONFIG_PATH.getPath() 
                    + (filePath.subSequence(0, filePath.length()-1)) + ".ini", keyboardAttributes.getAllAttributes());
            MyUtilities.FILE_IO_UTILITIES.writeSettingListToFile(FilePath.CONFIG_PATH.getPath() 
                    + (filePath.subSequence(0, filePath.length()-1)) + ".ini", keyboardSettings.getAllSettings());
        }
    }
}

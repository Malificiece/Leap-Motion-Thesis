package utilities;

import java.util.ArrayList;

import keyboard.KeyboardAttribute;
import keyboard.KeyboardSetting;

public class FileUtilities {
    // ALL OF THE BELOW must check to see if variable exists in file yet
    // if not, insert the variable in alphabetical order into the file and put the new value
    // if the variable already exists then we just update the value there
    public KeyboardAttribute readAttributeFromFile(String path, String name) {
        return null;
    }
    
    public void writeAttributeToFile(String path, KeyboardAttribute attribute) {
        
    }
    
    public void writeAttributeListToFile(String path, ArrayList<KeyboardAttribute> attributes) {
        openDirectory();
        openFile();
    }
    
    public KeyboardSetting readSettingFromFile(String path, String name) {
        return null;
    }
    
    public void writeSettingToFile(String path, KeyboardSetting setting) {
        
    }
    
    public void writeSettingListToFile(String path, ArrayList<KeyboardSetting> settings) {
        
    }
    
    // open's directory specified by path or creates the folder if it doesn't exist yet
    private void openDirectory() {
        
    }
    
    // open's file specified by path or creates a new file if it doesn't exist yet
    private void openFile() {
        
    }
    
    // Add data recording function or make a separate DataRecorder class to take care of it.
}

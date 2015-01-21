package experiment;

import enums.Key;
import keyboard.IKeyboard;

public class DataManager {
    private String word;
    
    public DataManager(IKeyboard keyboard) {
        // prepare a new data manager
        System.out.println("init for " + keyboard + " -- data manager");
    }
    
    public void save(String subjectID) { // get path and file name from keyboard
        // print to file
        // clear out current data manager
        System.out.println("print and clear " + subjectID + " -- data manager");
    }
    
    public void startRecording() {
        // time the experiment started
        System.out.println("start recording -- data manager");
    }
    
    public void stopRecording() {
        // time the experiment stopped
        System.out.println("stop recording -- data manager");
    }
    
    public void startWord(String currentWord) {
        // time the word started
        word = currentWord;
        System.out.println("start word " + word + " -- data manager");
    }
    
    public void stopWord() {
        // time the enter was pressed and word finished
        System.out.println("stop word " + word + " -- data manager");
    }
    
    public void keyPressedEvent(char pressedKey, char currentKey) {
        // key press detected
        Key pKey = Key.getByValue(pressedKey);
        Key cKey = Key.getByValue(currentKey);
        
        System.out.println("pressed key " + pKey + " -- data manager -- goal: " + cKey);
    }
    
    // TODO: controller keyboard needs to know what is selected ----- or maybe not, might be able to record just the joystick movements?
    // determining what is selected might be easiest
    
    // TODO: leap keyboard needs to know the position of the leap tip and direction --- we can fudge the length/radius of the tool.
    
    // TODO: tablet keyboard needs to know the position of the touch point as it moves around the surface
}

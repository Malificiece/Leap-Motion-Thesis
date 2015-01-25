package experiment;

import java.util.ArrayList;

import enums.DataType;
import enums.FileExt;
import enums.FilePath;
import enums.Key;
import keyboard.IKeyboard;

public class DataManager {
    private final String FILE_PATH;
    private final String FILE_NAME;
    private ArrayList<String> dataList;
    private String word;
    
    public DataManager(IKeyboard keyboard, String subjectID) {
        FILE_PATH = FilePath.DATA.getPath() + subjectID + "/";
        FILE_NAME = keyboard.getFileName() + FileExt.DAT.getExt();
        dataList = new ArrayList<String>();
        System.out.println("init for " + keyboard + " -- data manager");
    }
    
    public void save() {
        // TODO: Print to file, check to make sure it doesn't already exist
        // That or maybe we can append a (1), (2) etc to file to tell you
        // that they ran the test again.
        dataList.clear();
        dataList = null;
        System.out.println("saving to: " + FILE_PATH + FILE_NAME);
    }
    
    public void startRecording() {
        dataList.add(DataType.TIME_EXPERIMENT_START.name() + ": " + System.nanoTime());
        System.out.println(dataList.get(dataList.size()-1));
    }
    
    public void stopRecording() {
        dataList.add(DataType.TIME_EXPERIMENT_END.name() + ": " + System.nanoTime());
        System.out.println(dataList.get(dataList.size()-1));
    }
    
    public void startWord(String currentWord) {
        word = currentWord;
        dataList.add(DataType.TIME_WORD_START.name() + ": " + System.nanoTime()
                + " " + DataType.WORD_VALUE.name() + ": " + word);
        System.out.println(dataList.get(dataList.size()-1));
    }
    
    public void stopWord() {
        dataList.add(DataType.TIME_WORD_END.name() + ": " + System.nanoTime()
                + " " + DataType.WORD_VALUE.name() + ": " + word);
        word = null;
        System.out.println(dataList.get(dataList.size()-1));
    }
    
    public void keyPressedEvent(char pressedKey, char currentKey) {
        Key pKey = Key.getByValue(pressedKey);
        Key cKey = Key.getByValue(currentKey);
        dataList.add(DataType.TIME_PRESSED.name() + ": " + System.nanoTime()
                + " " + DataType.KEY_PRESSED.name() + ": " + pKey.getName()
                + " " + DataType.KEY_EXPECTED.name() + ": " + cKey.getName()
                + " " + DataType.KEY_PRESSED_UPPER.name() + ": " + pKey.isUpper(pressedKey)
                + " " + DataType.KEY_EXPECTED_UPPER.name() + ": " + cKey.isUpper(currentKey));
        System.out.println(dataList.get(dataList.size()-1));
    }
    
    // TODO: controller keyboard needs to know what is selected ----- or maybe not, might be able to record just the joystick movements?
    // determining what is selected might be easiest
    
    // TODO: leap keyboard needs to know the position of the leap tip and direction --- we can fudge the length/radius of the tool.
    
    // TODO: tablet keyboard needs to know the position of the touch point as it moves around the surface
}

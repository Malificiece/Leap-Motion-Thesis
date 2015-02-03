package experiment.data;

import java.io.IOException;
import java.util.ArrayList;

import utilities.MyUtilities;

import com.leapmotion.leap.Vector;

import enums.DataType;
import enums.Direction;
import enums.FileExt;
import enums.FilePath;
import enums.Key;
import keyboard.IKeyboard;

public class DataManager implements DataObserver {
    private final String FILE_PATH;
    private final String DATA_FILE_NAME;
    private ArrayList<String> dataList;
    private String word;
    
    public DataManager(IKeyboard keyboard, String subjectID, String experimentTime) {
        FILE_PATH = FilePath.DATA.getPath() + subjectID + "/";
        DATA_FILE_NAME = subjectID + "_" + keyboard.getFileName() + experimentTime + FileExt.DAT.getExt();
        dataList = new ArrayList<String>();
        System.out.println("Starting experiment for " + subjectID + " using " + keyboard.getName());
    }
    
    public void save(IKeyboard keyboard) {
        boolean saved = false;
        int saveAttempts = 5;
        do {
            try {
                MyUtilities.FILE_IO_UTILITIES.writeListToFile(dataList, FILE_PATH, DATA_FILE_NAME, true);
                saved = true;
            } catch (IOException e) {
                saveAttempts--;
                System.out.println("Critical Error encountered: Unable to save data to file.");
                e.printStackTrace();
            }
        } while(!saved && saveAttempts > 0);
        System.out.println("Saving Experiment to " + FILE_PATH + DATA_FILE_NAME);
        keyboard.saveSettings(FILE_PATH);
        dataList.clear();
        dataList = null;
    }
    
    public void startRecording() {
        dataList.add(DataType.TIME_EXPERIMENT_START.name() + ": " + System.nanoTime());
    }
    
    public void stopRecording() {
        dataList.add(DataType.TIME_EXPERIMENT_END.name() + ": " + System.nanoTime());
    }
    
    public void startWord(String currentWord) {
        word = currentWord;
        dataList.add(DataType.TIME_WORD_START.name() + ": " + System.nanoTime()
                + "; " + DataType.WORD_VALUE.name() + ": " + word);
    }
    
    public void stopWord() {
        dataList.add(DataType.TIME_WORD_END.name() + ": " + System.nanoTime()
                + "; " + DataType.WORD_VALUE.name() + ": " + word);
        word = null;
    }
    
    public void keyPressedEvent(char pressedKey, char currentKey) {
        Key pKey = Key.getByValue(pressedKey);
        Key cKey = Key.getByValue(currentKey);
        dataList.add(DataType.TIME_PRESSED.name() + ": " + System.nanoTime()
                + "; " + DataType.KEY_PRESSED.name() + ": " + pKey.getName()
                + "; " + DataType.KEY_EXPECTED.name() + ": " + cKey.getName()
                + "; " + DataType.KEY_PRESSED_UPPER.name() + ": " + pKey.isUpper(pressedKey)
                + "; " + DataType.KEY_EXPECTED_UPPER.name() + ": " + cKey.isUpper(currentKey));
    }

    @Override
    public void controllerDataEventObserved(Direction direction) {
        dataList.add(DataType.TIME_SPECIAL.name() + ": " + System.nanoTime()
                + "; " + DataType.DIRECTION_PRESSED.name() + ": " + direction.name());
    }

    @Override
    public void tabletDataEventObserved(Vector touchPoint) {
        dataList.add(DataType.TIME_SPECIAL.name() + ": " + System.nanoTime()
                + "; " + DataType.POINT_POSITION.name() + ": " + touchPoint);
    }

    @Override
    public void leapDataEventObserved(Vector leapPoint, Vector toolDirection) {
        dataList.add(DataType.TIME_SPECIAL.name() + ": " + System.nanoTime()
                + "; " + DataType.POINT_POSITION.name() + ": " + leapPoint
                + "; " + DataType.TOOL_DIRECTION.name() + ": " + toolDirection);
    }
}

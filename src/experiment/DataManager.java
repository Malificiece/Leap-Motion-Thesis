package experiment;

import java.util.ArrayList;

import com.leapmotion.leap.Vector;

import enums.DataType;
import enums.Direction;
import enums.FileExt;
import enums.FilePath;
import enums.Key;
import keyboard.IKeyboard;

public class DataManager implements DataObserver {
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

    @Override
    public void controllerDataEventObserved(Direction direction) {
        dataList.add(DataType.TIME_SPECIAL.name() + ": " + System.nanoTime()
                + " " + DataType.DIRECTION_PRESSED.name() + ": " + direction.name());
        System.out.println(dataList.get(dataList.size()-1));
    }

    @Override
    public void tabletDataEventObserved(Vector touchPoint) {
        dataList.add(DataType.TIME_SPECIAL.name() + ": " + System.nanoTime()
                + " " + DataType.POINT_POSITION.name() + ": " + touchPoint);
        System.out.println(dataList.get(dataList.size()-1));
    }

    @Override
    public void leapDataEventObserved(Vector leapPoint, Vector toolDirection) {
        dataList.add(DataType.TIME_SPECIAL.name() + ": " + System.nanoTime()
                + " " + DataType.POINT_POSITION.name() + ": " + leapPoint
                + " " + DataType.TOOL_DIRECTION.name() + ": " + toolDirection);
        System.out.println(dataList.get(dataList.size()-1));
    }
}

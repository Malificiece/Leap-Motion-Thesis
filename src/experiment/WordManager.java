package experiment;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

import enums.FileExt;
import enums.FileName;
import utilities.MyUtilities;

public class WordManager {
    private static final String DEFAULT_WORD = "test";
    private final int DICTIONARY_SIZE = 118619;
    private boolean isDefault = false;
    private Queue<String> wordList = new LinkedList<String>();
    
    public WordManager() {
        setDefault();
    }
    
    public void update() {
        // use this to paint the words the correct colors using timers
    }
    
    public void setDefault() {
        wordList.clear();
        wordList.add(DEFAULT_WORD);
        isDefault = true;
    }
    
    public String currentWord() {
        return wordList.peek();
    }
    
    public boolean isValid() {
        return !wordList.isEmpty();
    }
    
    public void nextWord() {
        if(!isDefault && !wordList.isEmpty()) {
            wordList.remove();
        }
    }
    
    public boolean isMatch(String answer) {
        if(!wordList.isEmpty() && wordList.peek().equals(answer)) {
            return true;
        }
        return false;
    }

    public void loadWords(int reservoirSize) {
        wordList.clear();
        try {
            isDefault = false;
            wordList.addAll(MyUtilities.FILE_IO_UTILITIES.reservoirSampling(reservoirSize, DICTIONARY_SIZE, FileName.DICTIONARY.getName() + FileExt.DICTIONARY.getExt()));
        } catch (IOException e) {
            setDefault();
            System.out.println("An error occured while trying to load the words.");
            e.printStackTrace();
        }
    }
}

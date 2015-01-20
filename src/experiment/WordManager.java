package experiment;

import java.util.LinkedList;
import java.util.Queue;

public class WordManager {
    private static final String TEST_WORD = "test";
    private Queue<String> wordList = new LinkedList<String>();
    
    public WordManager() {
        setDefault();
        System.out.println(wordList);
    }
    
    public void update() {
        // use this to paint the words the correct colors using timers
    }
    
    public void setDefault() {
        wordList.clear();
        wordList.add(TEST_WORD);
    }
    
    public String currentWord() {
        return wordList.peek();
    }
    
    public boolean isValid() {
        return !wordList.isEmpty();
    }
    
    public void nextWord() {
        if(!wordList.isEmpty()) {
            wordList.remove();
        }
    }
    
    public boolean isMatch(String answer) {
        if(!wordList.isEmpty() && wordList.peek().equals(answer)) {
            return true;
        }
        return false;
    }
}

package experiment;

import java.util.LinkedList;
import java.util.Queue;

public class WordManager {
    private static final String TEST_WORD = "test";
    private Queue<String> wordList = new LinkedList<String>();
    
    public WordManager() {
        setDefaultWord();
        System.out.println(wordList);
    }
    
    public void setDefaultWord() {
        wordList.clear();
        wordList.add(TEST_WORD);
    }
    
    public String getCurrentWord() {
        return wordList.peek();
    }
}

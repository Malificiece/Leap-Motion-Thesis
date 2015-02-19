package dictionary;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import enums.FileExt;
import enums.FileName;
import enums.FilePath;
import utilities.MyUtilities;

public class DictionaryBuilder {
	// TODO:
	// 3) Scan through the library and find like gestures
	//		- Look for distance between keys
	//		- Number of like keys / number of keys
	//		- Similar patterns in gestures
	// We can determine the similarity by measuring the total length of the path. Then, with the total length,
    // we can then trace along the two lines in equal intervals. By doing this, we can measure the distance
    // between the intervals no matter how many vertices each line has. The total distance between all intervals
    // is the measure of dissimilarity between the two lines. The more intervals we use the more accurate the
    // calculation will be but the longer it will take. A dissimilarity measure of 0 means two lines are identical.
    
    // edges = word length - 1
    // verticies = word length
    // let's only compare words of the same number of verticies?
    // this will more evenly distribute the size of the words.
    private final int NUMBER_OF_DICTIONARIES = 10;
    private final int MIN_LETTER_LENGTH = 3;
    private final int MAX_LETTER_LENGTH = 6;
    private final int NUMBER_OF_TOP_SIMILAR_WORDS = 50;
    private Queue<String> dictionary = new LinkedList<String>();
	private boolean isEnabled;

    public DictionaryBuilder() {
		isEnabled = true;
	}
    
    public void update() {
        try {
            dictionary.addAll(MyUtilities.FILE_IO_UTILITIES.readListFromFile(FilePath.DICTIONARY.getPath(), FileName.DICTIONARY.getName() + FileExt.DICTIONARY.getExt()));
            
            // Find the dissimilarity values for all words within the letter length range.
            for(int letterLength = MIN_LETTER_LENGTH; letterLength <= MAX_LETTER_LENGTH; letterLength++) {
                // Go through and and grab all the words of the current letter length.
                ArrayList<String> dictionaryPart = new ArrayList<String>();
                while(dictionary.peek().length() == letterLength && dictionary.size() > 0) {
                    dictionaryPart.add(dictionary.remove());
                }
                
                System.out.println(dictionary.size());
                System.out.println(dictionaryPart.size());
                
                // First we need to go through and find all the similarity values
                // remove each word as we go
                // store the top 50 words with the lowest dissimilarity scores
                /*for(int i = 0; i < 10; i++) {
                    for(int j = 0; j < 10; j++) {
                        
                    }
                }*/
            }
            
        } catch (IOException e) {
            System.out.println("There was an error in building the dictionaries.");
            e.printStackTrace();
        }
        isEnabled = false;
    }

    public boolean isEnabled() {
        return isEnabled ;
    }
    
    //private class WordAnalyzer
}

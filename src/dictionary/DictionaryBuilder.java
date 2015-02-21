package dictionary;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import com.leapmotion.leap.Vector;

import keyboard.renderables.VirtualKeyboard;
import enums.DecimalPrecision;
import enums.FileExt;
import enums.FileName;
import enums.FilePath;
import enums.Key;
import enums.Keyboard;
import enums.Renderable;
import ui.ExperimentController;
import utilities.MyUtilities;

public class DictionaryBuilder {
	// Scan through the library and find like gestures
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
    private final int NUMBER_OF_TOP_MATCHES = 50;
    private final int MIN_WORD_LENGTH = 2;
    private final int MAX_WORD_LENGTH = 4;
    private final float NUMBER_OF_SETS_TO_USE_IN_DICTIONARY = ((int) (ExperimentController.EXPERIMENT_SIZE / ((MAX_WORD_LENGTH - MIN_WORD_LENGTH) + 1))) == 0 ? 1 :
        ((int) (ExperimentController.EXPERIMENT_SIZE / ((MAX_WORD_LENGTH - MIN_WORD_LENGTH) + 1)));
    private final float NUMBER_OF_WORDS_TO_CONTAINER_RATIO = (float) ((ExperimentController.EXPERIMENT_SIZE / (double) ((MAX_WORD_LENGTH - MIN_WORD_LENGTH) + 1))
            - NUMBER_OF_SETS_TO_USE_IN_DICTIONARY) < 0 ? 0 : (float) ((ExperimentController.EXPERIMENT_SIZE / (double) ((MAX_WORD_LENGTH - MIN_WORD_LENGTH) + 1))
            - NUMBER_OF_SETS_TO_USE_IN_DICTIONARY);
    private final float MAX_DIFFERENCE_BETWEEN_LETTERS;
    private final float MIN_DISTANCE_BETWEEN_LETTERS;
    private Queue<String> dictionary = new LinkedList<String>();
	private boolean isEnabled = false;
	private VirtualKeyboard virtualKeyboard;

    public DictionaryBuilder() {
        try {
            dictionary.addAll(MyUtilities.FILE_IO_UTILITIES.readListFromFile(FilePath.DICTIONARY.getPath(), FileName.DICTIONARY.getName() + FileExt.DICTIONARY.getExt()));
            dictionary.removeAll(MyUtilities.FILE_IO_UTILITIES.readListFromFile(FilePath.DICTIONARY.getPath(), FileName.DICTIONARY_FILTER.getName() + FileExt.DICTIONARY.getExt()));
            System.out.println(NUMBER_OF_SETS_TO_USE_IN_DICTIONARY);
            System.out.println(NUMBER_OF_WORDS_TO_CONTAINER_RATIO);
            System.out.println(1/3f);
            System.out.println(2/3f);
            System.out.println((1f - 2f/3f));
            System.out.println((1/3f + 2f/3f));
            System.out.println((1 - 1/2f) == NUMBER_OF_WORDS_TO_CONTAINER_RATIO);
            System.out.println(1/3f);
            System.out.println(DecimalPrecision.FIVE.round(1/3f));
            System.out.println(1 - (2/3f));
            System.out.println(DecimalPrecision.FIVE.round(1 - (2/3f)));
            System.out.println((1/3f) == (1 - (2/3f)));
            System.out.println(DecimalPrecision.FIVE.round(1/3f) == DecimalPrecision.FIVE.round(1 - (2/3f)));
            
            int min = 0;
            for(int i = 0; i < 15; i++) {
                float NUMBER_OF_SETS_TO_USE_IN_DICTIONARY = ((int) (ExperimentController.EXPERIMENT_SIZE / ((i - min) + 1))) == 0 ? 1 :
                    ((int) (ExperimentController.EXPERIMENT_SIZE / ((i - min) + 1)));
                float NUMBER_OF_WORDS_TO_CONTAINER_RATIO = (float) ((float) ((ExperimentController.EXPERIMENT_SIZE / (double) ((i - min) + 1))
                        - NUMBER_OF_SETS_TO_USE_IN_DICTIONARY) < 0 ? 0 : DecimalPrecision.FIVE.round((float) ((ExperimentController.EXPERIMENT_SIZE / (double) ((i - min) + 1))
                        - NUMBER_OF_SETS_TO_USE_IN_DICTIONARY)));
                System.out.println(NUMBER_OF_SETS_TO_USE_IN_DICTIONARY + " " + NUMBER_OF_WORDS_TO_CONTAINER_RATIO);
            }
    		isEnabled = false;
        } catch (IOException e) {
            System.out.println("Unable to read the default dictionary.");
            e.printStackTrace();
        }
        virtualKeyboard = (VirtualKeyboard) Keyboard.TABLET.getKeyboard().getRenderables().getRenderable(Renderable.VIRTUAL_KEYBOARD);
        MIN_DISTANCE_BETWEEN_LETTERS = MyUtilities.MATH_UTILITILES.findDistanceToPoint(virtualKeyboard.getVirtualKey(Key.VK_Q).getCenter(),
                virtualKeyboard.getVirtualKey(Key.VK_W).getCenter());
        MAX_DIFFERENCE_BETWEEN_LETTERS = (MyUtilities.MATH_UTILITILES.findDistanceToPoint(virtualKeyboard.getVirtualKey(Key.VK_Q).getCenter(), 
                virtualKeyboard.getVirtualKey(Key.VK_P).getCenter()) - MIN_DISTANCE_BETWEEN_LETTERS) / MIN_DISTANCE_BETWEEN_LETTERS;
	}
    
    public boolean isEnabled() {
        return isEnabled ;
    }
    
    public void update() {
        if(isEnabled) {
            ArrayList<WordDissimilarityData> matchesToSave = new ArrayList<WordDissimilarityData>();
            // Find the dissimilarity values for all words within the letter length range.
            for(int wordLength = MIN_WORD_LENGTH; wordLength <= MAX_WORD_LENGTH && matchesToSave.size() < ExperimentController.EXPERIMENT_SIZE; wordLength++) {
                // Go through and and grab all the words of the current letter length.
                ArrayList<String> dictionaryPart = new ArrayList<String>();
                while(dictionary.size() > 0 && dictionary.peek().length() <= wordLength) {
                    if(dictionary.peek().length() == wordLength) {
                        dictionaryPart.add(dictionary.remove());
                    } else {
                        dictionary.remove();
                    }
                }
                
                // Now we must calculate all of the dissimilarity values. We can do this with n log(n) time and
                // never make any double comparisons. For n log(n) we need to keep a 2D matrix with the words of
                // the dictionary part lining each side.
                float [][] dissimilarityMatrix = new float[dictionaryPart.size()][dictionaryPart.size()];
                for(int wordIndex = 0; wordIndex < dictionaryPart.size(); wordIndex++) {
                    for(int compareIndex = wordIndex; compareIndex < dictionaryPart.size(); compareIndex++) {
                        if(wordIndex == compareIndex) {
                            dissimilarityMatrix[wordIndex][compareIndex] = -1f;
                        } else {
                            float dissimilarity = measureDissimilarity(dictionaryPart.get(wordIndex), dictionaryPart.get(compareIndex), wordLength);
                            // The space is already allocated so we might as well fill up the matrix on both sides of the diagonal
                            // even though the information is redundant. This makes for easier traversal later on.
                            dissimilarityMatrix[wordIndex][compareIndex] = dissimilarity;
                            dissimilarityMatrix[compareIndex][wordIndex] = dissimilarity;
                        }
                    }
                }
                
                // Here we go through the matrix and find the best matches for each word equal to the number of
                // dictionaries. We'll find the 50 best matches overall. To do this, we just take the lowest total
                // dissimilarity values for each word and it's best matches.
                ArrayList<WordDissimilarityData> topMatches = new ArrayList<WordDissimilarityData>();
                for(int wordIndex = 0; wordIndex < dictionaryPart.size(); wordIndex++) {
                    WordDissimilarityData dissimilarityData = new WordDissimilarityData(dictionaryPart.get(wordIndex));
                    for(int compareIndex = 0; compareIndex < dictionaryPart.size(); compareIndex++) {
                        //if(wordIndex != compareIndex) {
                            dissimilarityData.analyzeDissimilarity(dictionaryPart.get(compareIndex), dissimilarityMatrix[wordIndex][compareIndex]);
                        //}
                    }
                    if(topMatches.size() == 0) {
                        topMatches.add(dissimilarityData);
                    } else if(topMatches.size() < NUMBER_OF_TOP_MATCHES) {
                        for(int i = 0; i < topMatches.size(); i++) {
                            if(topMatches.get(i).getTotalDissimilarity() > dissimilarityData.getTotalDissimilarity()) {
                                topMatches.add(i, dissimilarityData);
                                break;
                            }
                            if(i == topMatches.size() - 1) {
                                topMatches.add(dissimilarityData);
                                break;
                            }
                        }
                    } else {
                        for(int i = 0; i < topMatches.size(); i++) {
                            if(topMatches.get(i).getTotalDissimilarity() > dissimilarityData.getTotalDissimilarity()) {
                                topMatches.add(i, dissimilarityData);
                                break;
                            }
                        }
                        if(topMatches.size() > NUMBER_OF_TOP_MATCHES) {
                            topMatches.remove(topMatches.size() - 1);
                        }
                    }
                }
                
                // Release matrix allocated space to garbage collector.
                dissimilarityMatrix = null;
                
                if(!topMatches.isEmpty()) {
                    int matchStartIndex = matchesToSave.size();
                    matchesToSave.add(topMatches.get(0));
                    
                    System.out.println("Top matches for size " + wordLength + ":");
                    for(WordDissimilarityData wdd: topMatches) {
                        System.out.print("word: " + wdd.getWord() + " total: " + wdd.getTotalDissimilarity() + " matches: ");
                        for(WordDissimilarityPair wdp: wdd.getTopMatches()) {
                            System.out.print("[" + wdp.getWord() + ", " + wdp.getDissimilarity() + "], ");
                        }
                        System.out.println();
                    }
                    System.out.println();
    
                    // Go through the top matches and find the specified number of sets of each word length.
                    while((matchesToSave.size() - matchStartIndex) < Math.round((wordLength / NUMBER_OF_SETS_TO_USE_IN_DICTIONARY))) {
                        for(WordDissimilarityData wdd: topMatches) {
                            boolean isUnique = true;
                            notUnique:
                            for(int i = matchStartIndex; i < matchesToSave.size(); i++) {
                                for(WordDissimilarityPair saveWDP: matchesToSave.get(i).getTopMatches()) {
                                    for(WordDissimilarityPair wdp: wdd.getTopMatches()) {
                                        if(saveWDP.getWord().equals(wdp.getWord())) {
                                            isUnique = false;
                                            break notUnique;
                                        }
                                    }
                                }
                            }
                            if(isUnique) {
                                matchesToSave.add(wdd);
                                break;
                            }
                        }
                    }
                }
                
                // Release match list allocated space to garbage collector.
                topMatches = null;
            }
            virtualKeyboard = null;
            
            System.out.println("Matches to save:");
            for(WordDissimilarityData wdd: matchesToSave) {
                System.out.print("word: " + wdd.getWord() + " total: " + wdd.getTotalDissimilarity() + " matches: ");
                for(WordDissimilarityPair wdp: wdd.getTopMatches()) {
                    System.out.print("[" + wdp.getWord() + ", " + wdp.getDissimilarity() + "], ");
                }
                System.out.println();
            }
            System.out.println();
            
            // Convert words to save into an easy to use format.
            System.out.println("Simplifying data:");
            ArrayList<ArrayList<String>> wordsToSave = new ArrayList<ArrayList<String>>();
            for(WordDissimilarityData wdd: matchesToSave) {
                wordsToSave.add(wdd.getTopMatchedWordsOnly());
                System.out.println(wordsToSave.get(wordsToSave.size()-1));
            }
            System.out.println();
            
            // Release wordsToSave allocated space to garbage collector.
            matchesToSave = null;
            
            // Save the words to the dictionaries.
            System.out.println("Saving dictionaries:");
            for(int dictionaryIndex = 0; dictionaryIndex < NUMBER_OF_DICTIONARIES; dictionaryIndex++) {
                // Randomly pick one word from each selection to insert into current dictionary.
                Random random = new Random();
                ArrayList<String> dictionaryWordSelection = new ArrayList<String>();
                for(ArrayList<String> wordsToChooseFrom: wordsToSave) {
                    int selectionIndex = random.nextInt(wordsToChooseFrom.size());
                    dictionaryWordSelection.add(wordsToChooseFrom.get(selectionIndex));
                    wordsToChooseFrom.remove(selectionIndex);
                }
                
                System.out.println(dictionaryWordSelection);
                
                /*if(dictionaryIndex < KeyboardType.values().length) {
                    try {
                        MyUtilities.FILE_IO_UTILITIES.writeListToFile(dictionaryWordSelection, FilePath.DICTIONARY.getPath(),
                                KeyboardType.values()[dictionaryIndex].getFileName() + FileExt.DICTIONARY.getExt(), false);
                    } catch (IOException e) {
                        System.out.println("One of the dictionaries encountered an error while saving. Please rebuild dictionaries.");
                        e.printStackTrace();
                    }
                } else {
                    String fileName = "temporary_" + (dictionaryIndex - KeyboardType.values().length);
                    try {
                        MyUtilities.FILE_IO_UTILITIES.writeListToFile(dictionaryWordSelection, FilePath.DICTIONARY.getPath(),
                                fileName + FileExt.DICTIONARY.getExt(), false);
                    } catch (IOException e) {
                        System.out.println("One of the dictionaries encountered an error while saving. Please rebuild dictionaries.");
                        e.printStackTrace();
                    }
                }*/
            }
            wordsToSave = null;
            dictionary = null;
            isEnabled = false;
        }
    }
    
    private float measureDissimilarity(String word, String compare, int wordLength) {
        // For now, words must be the same length.
        assert(word.length() == compare.length());
        
        // Dissimilarity is going to be two things:
        // 1) The difference in length between each pair of letters clamped to [0, 1]
        // 2) The angle difference between the direction vector for each pair of letters clamped to [0, 1]
        float dissimilarity = 0;
        Vector previousWordPoint = null;
        Vector previousComparePoint = null;
        for(int letterIndex = 0; letterIndex < word.length(); letterIndex++) {
            Vector currentWordPoint = virtualKeyboard.getVirtualKey(Key.getByValue(word.charAt(letterIndex))).getCenter();
            Vector currentComparePoint = virtualKeyboard.getVirtualKey(Key.getByValue(compare.charAt(letterIndex))).getCenter();

            if(previousWordPoint == null || previousComparePoint == null) {
                previousWordPoint = currentWordPoint;
                previousComparePoint = currentComparePoint;
            } else {
                float wordDistance = MyUtilities.MATH_UTILITILES.findDistanceToPoint(previousWordPoint, currentWordPoint);
                float compareDistance = MyUtilities.MATH_UTILITILES.findDistanceToPoint(previousComparePoint, currentComparePoint);
                float difference = (Math.abs(wordDistance - compareDistance) / MIN_DISTANCE_BETWEEN_LETTERS) / MAX_DIFFERENCE_BETWEEN_LETTERS;
                float angle = (float) (currentWordPoint.minus(previousWordPoint).angleTo(currentComparePoint.minus(previousComparePoint)) / Math.PI);
                
                // Take the average of the normalized difference and angle (which are clamped from [0, 1].
                dissimilarity += (difference + angle) / 2f;
                
                // Save the current points as the previous now that we're done.
                previousWordPoint = currentWordPoint;
                previousComparePoint = currentComparePoint;
            }
        }
        return dissimilarity / (wordLength - 1);
    }

    private class WordDissimilarityData {
        private final String BASE_WORD;
        private float totalDissimilarity = 1;
        private ArrayList<WordDissimilarityPair> topMatches = new ArrayList<WordDissimilarityPair>();
        
        public WordDissimilarityData(String word) {
            BASE_WORD = word;
        }
        
        public String getWord() {
            return BASE_WORD;
        }
        
        public float getTotalDissimilarity() {
            return totalDissimilarity;
        }
        
        public ArrayList<WordDissimilarityPair> getTopMatches() {
            return topMatches;
        }
        
        public ArrayList<String> getTopMatchedWordsOnly() {
            ArrayList<String> wordMatches = new ArrayList<String>();
            for(WordDissimilarityPair wdp: topMatches) {
                wordMatches.add(wdp.getWord());
            }
            return wordMatches;
        }
        
        public void analyzeDissimilarity(String word, float dissimilarity) {
            if(topMatches.size() == 0) {
                topMatches.add(new WordDissimilarityPair(word, dissimilarity));
                totalDissimilarity += dissimilarity;
            } else if(topMatches.size() < NUMBER_OF_DICTIONARIES /*- 1*/) {
                for(int i = 0; i < topMatches.size(); i++) {
                    if(topMatches.get(i).getDissimilarity() > dissimilarity) {
                        topMatches.add(i, new WordDissimilarityPair(word, dissimilarity));
                        totalDissimilarity += dissimilarity;
                        break;
                    }
                    if(i == topMatches.size() - 1) {
                        topMatches.add(new WordDissimilarityPair(word, dissimilarity));
                        totalDissimilarity += dissimilarity;
                        break;
                    }
                }
            } else {
                for(int i = 0; i < topMatches.size(); i++) {
                    if(topMatches.get(i).getDissimilarity() > dissimilarity) {
                        topMatches.add(i, new WordDissimilarityPair(word, dissimilarity));
                        totalDissimilarity += dissimilarity;
                        break;
                    }
                }
                if(topMatches.size() > NUMBER_OF_DICTIONARIES /*- 1*/) {
                    totalDissimilarity -= topMatches.get(topMatches.size() - 1).getDissimilarity();
                    topMatches.remove(topMatches.size() - 1);
                }
            }
        }
    }
    
    private class WordDissimilarityPair {
        private final String WORD;
        private final float DISSIMILARITY;
        
        public WordDissimilarityPair(String word, float dissimilarity) {
            WORD = word;
            DISSIMILARITY = dissimilarity;
        }
        
        public String getWord() {
            return WORD;
        }
        
        public float getDissimilarity() {
            return DISSIMILARITY;
        }
    }
}

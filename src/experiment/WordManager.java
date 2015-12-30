/*
 * Copyright (c) 2015, Garrett Benoit. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */ 

package experiment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;

import javax.swing.JLabel;

import enums.FileExt;
import enums.FileName;
import enums.FilePath;
import enums.Key;
import enums.KeyboardType;
import utilities.FileUtilities;
import utilities.MyUtilities;

public class WordManager {
    private static final String DEFAULT_WORD = "test"; // enunciating, ced, frazzled, test, calumnies, atherosclerosis --- check that A doesn't trigger when hit enter.....
    private static ArrayList<WordObserver> OBSERVERS = new ArrayList<WordObserver>();
    private boolean isDefault = false;
    private Queue<String> wordList = new LinkedList<String>();
    private int currentLetter = 0;
    private String answer = "";
    
    public WordManager() {
        setDefault();
    }
    
    public void setDefault() {
        wordList.clear();
        wordList.add(DEFAULT_WORD);
        currentLetter = 0;
        isDefault = true;
        notifyListenersWordSet();
    }
    
    public boolean isDefault() {
        return isDefault;
    }
    
    public void setAnswer(String answer) {
        this.answer = answer;
    }
    
    public String getAnswer() {
        return answer;
    }
    
    public int currentIndex() {
    	return currentLetter;
    }
    
    public char currentLetter() {
        if(currentLetter < answer.length()) {
            return Key.VK_BACK_SPACE.getValue();
        } else if(currentLetter == currentWord().length()) {
            return Key.VK_ENTER.getValue();
        } else {
            return currentWord().charAt(currentLetter);
        }
    }
    
    public Key currentLetterAsKey() {
        if(currentLetter < answer.length()) {
            return Key.VK_BACK_SPACE;
        } else if(currentLetter == currentWord().length()) {
            return Key.VK_ENTER;
        } else {
            return Key.getByValue(currentWord().charAt(currentLetter));
        }
    }
    
    public String currentWord() {
        if(isValid()) {
            return wordList.peek();
        } else {
            isDefault = true;
            return DEFAULT_WORD;
        }
    }
    
    public boolean isValid() {
        return !wordList.isEmpty();
    }
    
    public void nextWord() {
        if(!isDefault && isValid()) {
            wordList.remove();
            if(isValid()) {
                notifyListenersWordSet();
            }
        }
    }
    
    public boolean isMatch(boolean notifyObservers) {
        if(!wordList.isEmpty() && currentWord().equals(answer)) {
            if(notifyObservers) {
                notifyListenersMatchObserved();
            }
            return true;
        }
        return false;
    }
    
    public void loadTutorialWords() {
        wordList.clear();
        try {
            isDefault = false;
            wordList.addAll(MyUtilities.FILE_IO_UTILITIES.readListFromFile(FilePath.DICTIONARY.getPath(), FileName.TUTORIAL.getName() + FileExt.DICTIONARY.getExt()));
            currentLetter = 0;
            if(isValid()) {
                notifyListenersWordSet();
            }
        } catch (IOException e) {
            setDefault();
            System.out.println("An error occured while trying to load the words.");
            e.printStackTrace();
        }
    }
    
    public void loadPracticeWords(int reservoirSize) {
        wordList.clear();
        try {
            isDefault = false;
            wordList.addAll(MyUtilities.FILE_IO_UTILITIES.reservoirSampling(reservoirSize, FilePath.DICTIONARY.getPath(), FileName.DICTIONARY.getName() + FileExt.DICTIONARY.getExt()));
            
            // Filter swear words
            wordList.removeAll(MyUtilities.FILE_IO_UTILITIES.readListFromFile(FilePath.DICTIONARY.getPath(), FileName.DICTIONARY_FILTER.getName() + FileExt.DICTIONARY.getExt()));
            
            // Filter used words
            for(KeyboardType keyboardType: KeyboardType.values()) {
                wordList.removeAll(MyUtilities.FILE_IO_UTILITIES.readListFromFile(FilePath.DICTIONARY.getPath(), keyboardType.getFileName() + FileExt.DICTIONARY.getExt()));
            }
            
            // Filter standby words
            for(File file: MyUtilities.FILE_IO_UTILITIES.getListOfWildCardFileMatches(FilePath.DICTIONARY.getPath(),
                    FileName.TEMPORARY.getName() + FileUtilities.WILDCARD + FileExt.DICTIONARY.getExt())) {
                wordList.removeAll(MyUtilities.FILE_IO_UTILITIES.readListFromFile(file));
            }
            currentLetter = 0;
            if(isValid()) {
                notifyListenersWordSet();
            }
        } catch (IOException e) {
            setDefault();
            System.out.println("An error occured while trying to load the words.");
            e.printStackTrace();
        }
    }

    public void loadExperimentWords(String fileName) {
        wordList.clear();
        try {
            isDefault = false;
            ArrayList<String> randomizedWords = new ArrayList<String>();
            randomizedWords.addAll(MyUtilities.FILE_IO_UTILITIES.readListFromFile(FilePath.DICTIONARY.getPath(), fileName + FileExt.DICTIONARY.getExt()));
            // Considered the Fisher-Yates shuffle but it's not as good as this shuffle.
            Collections.shuffle(randomizedWords);
            wordList.addAll(randomizedWords);
            currentLetter = 0;
            if(isValid()) {
                notifyListenersWordSet();
            }
        } catch (IOException e) {
            setDefault();
            System.out.println("An error occured while trying to load the words.");
            e.printStackTrace();
        }
    }
    
    public void paintLetters(JLabel wordLabel, JLabel answerLabel) {
        String word = currentWord();
        int matchIndex = -1;
        for(int i = 0; i < word.length() && i < answer.length(); i++) {
            if(word.charAt(i) == answer.charAt(i)) {
                matchIndex = i;
            } else {
                break;
            }
        }
        if(matchIndex < word.length()) {
            currentLetter = matchIndex + 1;
            notifyListenersLetterIndexChanged();
        }
        if(matchIndex > -1) {
            wordLabel.setText("<html><nobr><font color=green>" + word.substring(0, matchIndex + 1) + "</font>" + word.substring(matchIndex + 1) + "</nobr></html>");
        } else {
            wordLabel.setText("<html><nobr>" + word + "</nobr></html>");
        }
        answerLabel.setText("<html><nobr><pre style=\"font-family:Dialog;\"><font color=green>" + answer.substring(0, matchIndex + 1) + "</font><font color=red>" + answer.substring(matchIndex + 1) + "</font></pre></nobr></html>");
    }
    
    public static void registerObserver(WordObserver observer) {
        if(OBSERVERS.contains(observer)) {
            return;
        }
        OBSERVERS.add(observer);
    }
    
    public static void removeObserver(WordObserver observer) {
        OBSERVERS.remove(observer);
    }

    protected void notifyListenersWordSet() {
        for(WordObserver observer : OBSERVERS) {
            observer.wordSetEventObserved(currentWord());
        }
    }
    
    protected void notifyListenersLetterIndexChanged() {
        for(WordObserver observer : OBSERVERS) {
            observer.currentLetterIndexChangedEventObservered(currentLetter, currentLetterAsKey());
        }
    }
    
    protected void notifyListenersMatchObserved() {
        for(WordObserver observer : OBSERVERS) {
            observer.matchEventObserved();
        }
    }
}

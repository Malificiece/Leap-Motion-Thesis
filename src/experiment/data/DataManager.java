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

package experiment.data;

import java.io.IOException;
import java.util.ArrayList;

import utilities.MyUtilities;

import com.leapmotion.leap.Vector;

import enums.RecordedDataType;
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
    
    public DataManager(IKeyboard keyboard, String subjectID, String experimentTime, int practiceWordCount) {
        FILE_PATH = FilePath.RECORDED_DATA.getPath() + subjectID + "/";
        DATA_FILE_NAME = subjectID + "_" + keyboard.getFileName() + experimentTime + FileExt.PLAYBACK.getExt(); // We record a playback file
        dataList = new ArrayList<String>();
        System.out.println("Starting experiment for " + subjectID + " using " + keyboard.getName());
        dataList.add(RecordedDataType.PRACTICE_WORD_COUNT.name() + ": " + practiceWordCount);
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
        dataList.add(RecordedDataType.TIME_EXPERIMENT_START.name() + ": " + System.nanoTime());
    }
    
    public void stopRecording() {
        dataList.add(RecordedDataType.TIME_EXPERIMENT_END.name() + ": " + System.nanoTime());
    }
    
    public void startWord(String currentWord) {
        word = currentWord;
        dataList.add(RecordedDataType.TIME_WORD_START.name() + ": " + System.nanoTime()
                + "; " + RecordedDataType.WORD_VALUE.name() + ": " + word);
    }
    
    public void stopWord() {
        dataList.add(RecordedDataType.TIME_WORD_END.name() + ": " + System.nanoTime()
                + "; " + RecordedDataType.WORD_VALUE.name() + ": " + word);
        word = null;
    }
    
    public void keyPressedEvent(char pressedKey, char currentKey) {
        Key pKey = Key.getByValue(pressedKey);
        Key cKey = Key.getByValue(currentKey);
        dataList.add(RecordedDataType.TIME_PRESSED.name() + ": " + System.nanoTime()
                + "; " + RecordedDataType.KEY_PRESSED.name() + ": " + pKey.getName()
                + "; " + RecordedDataType.KEY_EXPECTED.name() + ": " + cKey.getName());
                //+ "; " + DataType.KEY_PRESSED_UPPER.name() + ": " + pKey.isUpper(pressedKey)
                //+ "; " + DataType.KEY_EXPECTED_UPPER.name() + ": " + cKey.isUpper(currentKey));
    }

    @Override
    public void controllerDataEventObserved(Direction direction) {
        dataList.add(RecordedDataType.TIME_SPECIAL.name() + ": " + System.nanoTime()
                + "; " + RecordedDataType.DIRECTION_PRESSED.name() + ": " + direction.name());
    }

    @Override
    public void tabletDataEventObserved(Vector touchPoint) {
        dataList.add(RecordedDataType.TIME_SPECIAL.name() + ": " + System.nanoTime()
                + "; " + RecordedDataType.POINT_POSITION.name() + ": " + touchPoint);
    }

    @Override
    public void leapToolDataEventObserved(Vector leapPoint, Vector toolDirection) {
        dataList.add(RecordedDataType.TIME_SPECIAL.name() + ": " + System.nanoTime()
                + "; " + RecordedDataType.POINT_POSITION.name() + ": " + leapPoint
                + "; " + RecordedDataType.TOOL_DIRECTION.name() + ": " + toolDirection);
    }

    @Override
    public void leapHandDataEventObserved(Vector leapPoint) {
        dataList.add(RecordedDataType.TIME_SPECIAL.name() + ": " + System.nanoTime()
                + "; " + RecordedDataType.POINT_POSITION.name() + ": " + leapPoint);
    }
}

package experiment.data.formatter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.SwingUtilities;

import keyboard.IKeyboard;
import keyboard.renderables.VirtualKeyboard;

import com.leapmotion.leap.Vector;

import enums.Direction;
import enums.FileExt;
import enums.FileName;
import enums.Key;
import enums.Keyboard;
import enums.KeyboardType;
import enums.RecordedDataType;
import enums.Renderable;
import enums.StatisticDataType;
import ui.ExperimentController;
import utilities.FileUtilities;
import utilities.MyUtilities;

public class DataFormatter implements Runnable {
    public enum FormatProcessType {
        CONSOLIDATE,
        CALCULATE;
    }
    private final double SECOND_AS_NANO = 1000000000;
    private final float PIXEL_TO_METER = 1f;//0.000264583f;
    private final String TUTORIAL = FileName.TUTORIAL.getName();
    private FormatProcessType type;
    private File directory;
    private JButton [] buttons;
    private ArrayList<FileData> fileData = new ArrayList<FileData>();
    
    public DataFormatter(FormatProcessType type, File directory, JButton [] buttons) {
        this.type = type;
        this.directory = directory;
        this.buttons = buttons;
    }
    
    @Override
    public void run() {
        try {
            if(type.equals(FormatProcessType.CALCULATE)) {
                calculate();
            } else {
                consolidate();
            }
        } finally {
            enableUI();   
        }
    }
    
    // for now force run on anything in the recorded data folder
    // later change to choose what we want to run the formatter on
    
    // order for each one needs to be corrected to probably be in the same order so that we can compare same words too.
    // Need to be able to organize/calculate individual data and then also consolidate all into one area
    
    // give it the home directory for it to find all files and consolidate them
    private void consolidate() {
        // Go through each subject and then calculate their data and put it into one .dat file.
        for(File subjectDirectory: directory.listFiles()) {
            String subjectID = subjectDirectory.getName();
            if(!TUTORIAL.equals(subjectID)) {
                //ArrayList<String> subjectData = new ArrayList<String>();
                //MyUtilities.FILE_IO_UTILITIES.writeListToFile(subjectData, subjectDirectory.getPath(), fileName, true);
            }
        }
    }
    
    private void calculate() {
        // Go through each subject and then calculate their data and put it into one .dat file.
        for(File subjectDirectory: directory.listFiles()) {
            String subjectID = subjectDirectory.getName();
            if(!TUTORIAL.equals(subjectID)) {
                ArrayList<String> subjectData = new ArrayList<String>();
                for(Keyboard keyboardType: Keyboard.values()) {
                    if(KeyboardType.getByID(keyboardType.getID()) != KeyboardType.STANDARD) {
                        IKeyboard keyboard = keyboardType.getKeyboard();
                        String wildcardFileName = subjectID + "_" + keyboard.getFileName() + FileUtilities.WILDCARD + FileExt.PLAYBACK.getExt();
                        try {
                            ArrayList<String> fileContents = MyUtilities.FILE_IO_UTILITIES.readListFromWildcardFile(subjectDirectory.getPath() + "\\", wildcardFileName);
                            fileData = parseFileContents(fileContents);
                            switch(keyboardType) {
                                case CONTROLLER:
                                    subjectData.addAll(calculateSubjectDataController(fileContents, keyboard));
                                    break;
                                case TABLET:
                                    subjectData.addAll(calculateSubjectData(fileContents, keyboard));
                                    break;
                                case LEAP_SURFACE:
                                    subjectData.addAll(calculateSubjectData(fileContents, keyboard));
                                    break;
                                case LEAP_AIR_STATIC:
                                    subjectData.addAll(calculateSubjectData(fileContents, keyboard));
                                    break;
                                case LEAP_AIR_DYNAMIC:
                                    subjectData.addAll(calculateSubjectData(fileContents, keyboard));
                                    break;
                                case LEAP_AIR_PINCH:
                                    subjectData.addAll(calculateSubjectData(fileContents, keyboard));
                                    break;
                                case LEAP_AIR_BIMODAL:
                                    subjectData.addAll(calculateSubjectData(fileContents, keyboard));
                                    break;
                                default: break;
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            System.out.println("There was an error while trying to read from file for: " + subjectID);
                        }
                    }
                }
                try {
                    String fileName = subjectID + "_" + FileName.SUBJECT_MERGED_DATA.getName() + FileExt.DAT.getExt();
                    MyUtilities.FILE_IO_UTILITIES.writeListToFile(subjectData, subjectDirectory.getPath() + "\\", fileName, true);
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("There was an error while trying to write calculated data to file for: " + subjectID);
                }
            }
        }
    }
    
    private ArrayList<String> calculateSubjectDataController(ArrayList<String> fileContents, IKeyboard keyboard) {
        SimulateController simulateController = new SimulateController(keyboard);
        ArrayList<String> subjectData = new ArrayList<String>();
        subjectData.add(StatisticDataType.KEYBOARD_TYPE.name() + ": " + keyboard.getFileName());
        VirtualKeyboard virtualKeyboard = (VirtualKeyboard) keyboard.getRenderables().getRenderable(Renderable.VIRTUAL_KEYBOARD);
        // Determine key distances for controller.
        final float DISTANCE_RIGHT_LEFT;
        final float DISTANCE_UP_DOWN;
        {
            Vector Q = virtualKeyboard.getVirtualKey(Key.VK_Q).getCenter();
            Vector W = virtualKeyboard.getVirtualKey(Key.VK_W).getCenter();
            Vector A = virtualKeyboard.getVirtualKey(Key.VK_A).getCenter();
            DISTANCE_RIGHT_LEFT = MyUtilities.MATH_UTILITILES.findDistanceToPoint(Q, W);
            DISTANCE_UP_DOWN = MyUtilities.MATH_UTILITILES.findDistanceToPoint(Q, A);
        }
        // Arrays
        ArrayList<String> wordList = new ArrayList<String>();
        ArrayList<Integer> planeBreachedCountArray = new ArrayList<Integer>();
        ArrayList<Float> distanceTraveledArray = new ArrayList<Float>();
        ArrayList<Float> distanceTraveledShortArray = new ArrayList<Float>();
        ArrayList<Float> timeDurationArray = new ArrayList<Float>();
        ArrayList<Float> timeDurationShortArray = new ArrayList<Float>();
        ArrayList<Float> reactionTimeFirstPressedArray = new ArrayList<Float>();
        ArrayList<Float> reactionTimeFirstTouchArray = new ArrayList<Float>();
        ArrayList<Float> reactionTimeToErrorAvgArray = new ArrayList<Float>();
        ArrayList<Float> handVelocityAvgArray = new ArrayList<Float>();
        ArrayList<Float> WPM_Array = new ArrayList<Float>();
        ArrayList<Float> modShortWPM_Array = new ArrayList<Float>();
        ArrayList<Float> modVultureWPM_Array = new ArrayList<Float>();
        ArrayList<Float> modShortVultureWPM_Array = new ArrayList<Float>();
        ArrayList<Float> KSPC_Array = new ArrayList<Float>();
        ArrayList<Float> modShortMSD_Array = new ArrayList<Float>();
        ArrayList<Float> modBackspaceMSD_Array = new ArrayList<Float>();
        ArrayList<Float> frechetDistanceArray = new ArrayList<Float>();
        ArrayList<Float> modShortFrechetDistanceArray = new ArrayList<Float>();
        ArrayList<Float> modBackspaceFrechetDistanceArray = new ArrayList<Float>();
        
        // Tracked Variables
        boolean firstLetter = true;
        boolean firstTouch = false;
        String currentWord = null;
        long wordStartTime = 0l;
        long previousTime = 0l;
        Key previousKey = null;
        
        int planeBreachedCount = 0;
        
        float distanceTraveled = 0f;
        float distanceTraveledShort = 0f;
        
        float timeDuration = 0f; // convert to seconds
        float timeDurationShort = 0f;
        
        float reactionTimeFirstPressed = 0f; // convert to seconds
        float reactionTimeFirstTouch = 0;
        
        float reactionTimeToError = 0f; // convert to seconds
        long timeErrorOccured = 0l;
        int responseToErrorsCount = 0;
        
        float handVelocity = 0f; // convert to m/s
        int numberOfActionsCount = 0;
        
        ArrayList<Vector> expectedPath = new ArrayList<Vector>();
        ArrayList<Vector> takenPath = new ArrayList<Vector>();
        ArrayList<Vector> takenPathModifiedShortest = new ArrayList<Vector>();
        ArrayList<Vector> expectedPathModifiedBackspace = new ArrayList<Vector>();
        
        float KSPC_C = 0f;
        float KSPC_INF = 0f;
        float KSPC_IF = 0f;
        float KSPC_F = 0f;
        
        int modShortMSD_C = 0;
        float modShortMSD_INF = 0f;
        
        float modBackspaceMSD_C = 0f;
        float modBackspaceMSD_INF = 0f;
        
        boolean detectedShortestComplete = false;
        
        for(FileData currentLine: fileData) {
            Key pressedKey = null;
            while(currentLine.hasNext()) {
                Entry<RecordedDataType, Object> currentData = currentLine.next();
                // Total distance traveled per word. (Array)
                // Average hand velocity per word. (Array)
                // Time duration per word. (Array)
                // Reaction time per word. (Array)
                // Average reaction time to errors per word. (Array)
                // Calculate KSPC
                // Calculate modified-MSD
                // Calculate WPM
                // Calculate Frechet Distance - Use Djikstra's for shortest path for controller keyboard to find expected path.
                // Calculate Frechet Distance Modified - this will use same set as mod-MSD
                boolean reportData = false;
                switch(currentData.getKey()) {
                    case WORD_VALUE:
                        previousTime = currentLine.getTime();
                        wordStartTime = currentLine.getTime();
                        numberOfActionsCount = 0;
                        responseToErrorsCount = 0;
                        detectedShortestComplete = false;
                        
                        // This path should contain the last key used or where the selection started.
                        String newWord = (String) currentData.getValue();
                        if(currentWord == null) {
                            simulateController.selectKey(Key.VK_Q);
                            expectedPath.add(virtualKeyboard.getVirtualKey(Key.VK_Q).getCenter());
                            expectedPathModifiedBackspace.add(virtualKeyboard.getVirtualKey(Key.VK_Q).getCenter());
                            for(char c: newWord.toCharArray()) {
                                // This path should contain the last key used or where the selection started.
                                expectedPath.add(virtualKeyboard.getVirtualKey(Key.getByValue(c)).getCenter());
                            }
                            takenPath.add(virtualKeyboard.getVirtualKey(Key.VK_Q).getCenter());
                            takenPathModifiedShortest.add(virtualKeyboard.getVirtualKey(Key.VK_Q).getCenter());
                        } else if(!currentWord.equals(newWord)) {
                            expectedPath.add(virtualKeyboard.getVirtualKey(simulateController.getSelectedKey()).getCenter());
                            expectedPathModifiedBackspace.add(virtualKeyboard.getVirtualKey(simulateController.getSelectedKey()).getCenter());
                            for(char c: newWord.toCharArray()) {
                                // This path should contain the last key used or where the selection started.
                                expectedPath.add(virtualKeyboard.getVirtualKey(Key.getByValue(c)).getCenter());
                            }
                            takenPath.add(virtualKeyboard.getVirtualKey(simulateController.getSelectedKey()).getCenter());
                            takenPathModifiedShortest.add(virtualKeyboard.getVirtualKey(simulateController.getSelectedKey()).getCenter());
                            previousKey = null;
                        }
                        currentWord = newWord;
                        break;
                    case KEY_PRESSED:
                        // First letter of word
                        pressedKey = (Key) currentData.getValue();
                        if(!firstTouch) {
                            firstTouch = true;
                            reactionTimeFirstTouch = (float) ((currentLine.getTime() - wordStartTime) / SECOND_AS_NANO);
                            timeDuration = (float) (currentLine.getTime() / SECOND_AS_NANO);
                        }
                        if(!detectedShortestComplete && pressedKey == Key.getByValue(currentWord.charAt(modShortMSD_C))) {
                            modShortMSD_C++;
                            if(modShortMSD_C == currentWord.length()) {
                                detectedShortestComplete = true;
                                distanceTraveledShort = distanceTraveled;
                                timeDurationShort = (float) ((currentLine.getTime() / SECOND_AS_NANO) - timeDuration);
                            }
                        }
                        if(pressedKey == Key.VK_ENTER) {
                            planeBreachedCount++;
                        }
                        break;
                    case KEY_EXPECTED:
                        Key expectedKey = (Key) currentData.getValue();
                        if(pressedKey.equals(Key.VK_ENTER) && expectedKey.equals(Key.VK_ENTER)) {
                            reportData = true;
                            timeDuration = (float) ((previousTime / SECOND_AS_NANO) - timeDuration);
                        } else if(pressedKey.equals(Key.VK_BACK_SPACE) && expectedKey.equals(Key.VK_BACK_SPACE)) {
                            KSPC_F++;
                            KSPC_IF++;
                            modBackspaceMSD_C++;
                            expectedPathModifiedBackspace.add(virtualKeyboard.getVirtualKey(Key.VK_BACK_SPACE).getCenter());
                            if(!simulateController.getSelectedKey().equals(Key.VK_BACK_SPACE)) {
                                takenPath.add(virtualKeyboard.getVirtualKey(Key.VK_BACK_SPACE).getCenter());
                            }
                            if(timeErrorOccured > 0) {
                                reactionTimeToError += (currentLine.getTime() - timeErrorOccured) / SECOND_AS_NANO;
                                timeErrorOccured = 0;
                                responseToErrorsCount++;
                            }
                        } else if(pressedKey.equals(expectedKey)) {
                            KSPC_C++;
                            modBackspaceMSD_C++;
                            expectedPathModifiedBackspace.add(virtualKeyboard.getVirtualKey(pressedKey).getCenter());
                            if(pressedKey.equals(previousKey)) {
                                takenPath.add(virtualKeyboard.getVirtualKey(simulateController.getSelectedKey()).getCenter());
                            }
                            if(firstLetter) {
                                firstLetter = false;
                                reactionTimeFirstPressed = (float) ((currentLine.getTime() - wordStartTime) / SECOND_AS_NANO);
                            }
                        } else if(!pressedKey.equals(expectedKey) && pressedKey != Key.VK_ENTER) {
                            if(timeErrorOccured == 0 && expectedKey != Key.VK_BACK_SPACE && pressedKey != Key.VK_BACK_SPACE) {
                                timeErrorOccured = currentLine.getTime();
                                if(!detectedShortestComplete) modShortMSD_INF++;
                                modBackspaceMSD_INF++;
                            } else if(expectedKey != Key.VK_BACK_SPACE && pressedKey == Key.VK_BACK_SPACE) {
                                KSPC_F++;
                                //KSPC_INF++; - not quite right
                            } else if(expectedKey == Key.VK_BACK_SPACE && pressedKey != Key.VK_BACK_SPACE) {
                                if(!detectedShortestComplete) modShortMSD_INF++;
                                modBackspaceMSD_INF++;
                            }
                        }
                        previousTime = currentLine.getTime();
                        previousKey = pressedKey;
                        break;
                    case DIRECTION_PRESSED:
                        Direction direction = (Direction) currentData.getValue();
                        if(direction.equals(Direction.RIGHT) || direction.equals(Direction.LEFT)) {
                            distanceTraveled += DISTANCE_RIGHT_LEFT;
                            if(firstTouch) handVelocity += (DISTANCE_RIGHT_LEFT * PIXEL_TO_METER) / ((currentLine.getTime() - previousTime) / SECOND_AS_NANO);
                        } else if(direction.equals(Direction.UP) || direction.equals(Direction.DOWN)) {
                            distanceTraveled += DISTANCE_UP_DOWN;
                            if(firstTouch) handVelocity += (DISTANCE_UP_DOWN * PIXEL_TO_METER) / ((currentLine.getTime() - previousTime) / SECOND_AS_NANO);
                        }
                        if(firstTouch) numberOfActionsCount++;
                        simulateController.moveSelectedKey(direction);
                        takenPath.add(virtualKeyboard.getVirtualKey(simulateController.getSelectedKey()).getCenter());
                        if(!detectedShortestComplete) takenPathModifiedShortest.add(virtualKeyboard.getVirtualKey(simulateController.getSelectedKey()).getCenter());
                        if(!firstTouch) {
                            firstTouch = true;
                            reactionTimeFirstTouch = (float) ((currentLine.getTime() - wordStartTime) / SECOND_AS_NANO);
                            timeDuration = (float) (currentLine.getTime() / SECOND_AS_NANO);
                        }
                        previousTime = currentLine.getTime();
                        break;
                    case PRACTICE_WORD_COUNT:
                        subjectData.add(StatisticDataType.PRACTICE_WORDS_PER_INPUT.name() + ": " + currentData.getValue());
                        break;
                    default: break;
                }
                
                if(reportData) {
                    // WPM = (currentWord.length() / timeDuration) * 60 * (1/5);
                    // KSPC = (KSPC_C + KSPC_INF + KSPC_IF + KSPC_F) / (KSPC_C + KSPC_INF)
                    // modMSD = (modMSD_INF / (modMSD_C + modMSD_INF)) * 100;
                    wordList.add(currentWord);
                    planeBreachedCountArray.add(planeBreachedCount);
                    distanceTraveledArray.add(distanceTraveled *= PIXEL_TO_METER);
                    distanceTraveledShortArray.add(distanceTraveledShort *= PIXEL_TO_METER);
                    timeDurationArray.add(timeDuration);
                    timeDurationShortArray.add(timeDurationShort);
                    handVelocityAvgArray.add(handVelocity / numberOfActionsCount);
                    reactionTimeFirstPressedArray.add(reactionTimeFirstPressed);
                    reactionTimeFirstTouchArray.add(reactionTimeFirstTouch);
                    if(responseToErrorsCount == 0) {
                        reactionTimeToErrorAvgArray.add(-1f);
                    } else {
                        reactionTimeToErrorAvgArray.add(reactionTimeToError / responseToErrorsCount);   
                    }
                    WPM_Array.add(((currentWord.length() - 1) / ((timeDuration + reactionTimeFirstTouch) - reactionTimeFirstPressed)) * 60f * (1f / 5f));
                    modShortWPM_Array.add(((currentWord.length() - 1) / ((timeDurationShort + reactionTimeFirstTouch) - reactionTimeFirstPressed)) * 60f * (1f / 5f));
                    modVultureWPM_Array.add((currentWord.length() / (timeDuration + reactionTimeFirstTouch)) * 60f * (1f / 5f));
                    modShortVultureWPM_Array.add((currentWord.length() / (timeDurationShort + reactionTimeFirstTouch)) * 60f * (1f / 5f));
                    KSPC_Array.add((KSPC_C + KSPC_INF + KSPC_IF + KSPC_F) / (KSPC_C + KSPC_INF));
                    modShortMSD_Array.add((modShortMSD_INF / (modShortMSD_C + modShortMSD_INF)) * 100);
                    modBackspaceMSD_Array.add((modBackspaceMSD_INF / (modBackspaceMSD_C + modBackspaceMSD_INF)) * 100);
                    /*System.out.println(currentWord + " ----------------------------------------------");
                    System.out.print("TakenMod:  ");
                    for(Vector v: takenPathModifiedShortest) {
                        System.out.print(virtualKeyboard.getNearestKeyNoEnter(v, 9999).getKey() + "  ");
                    }
                    System.out.println("\n-------------------------------------------------------------");*/
                    frechetDistanceArray.add(MyUtilities.MATH_UTILITILES.calculateFrechetDistance(
                            takenPath.toArray(new Vector[takenPath.size()]),
                            expectedPath.toArray(new Vector[expectedPath.size()])));
                    modShortFrechetDistanceArray.add(MyUtilities.MATH_UTILITILES.calculateFrechetDistance(
                            takenPathModifiedShortest.toArray(new Vector[takenPathModifiedShortest.size()]),
                            expectedPath.toArray(new Vector[expectedPath.size()])));
                    modBackspaceFrechetDistanceArray.add(MyUtilities.MATH_UTILITILES.calculateFrechetDistance(
                            takenPath.toArray(new Vector[takenPath.size()]),
                            expectedPathModifiedBackspace.toArray(new Vector[expectedPathModifiedBackspace.size()])));
                    reportData = false;
                    firstLetter = true;
                    firstTouch = false;
                    planeBreachedCount = 0;
                    distanceTraveled = 0f;
                    distanceTraveledShort = 0f;
                    timeDuration = 0f; // convert to seconds
                    timeDurationShort = 0f;
                    reactionTimeFirstPressed = 0f; // convert to seconds
                    reactionTimeFirstTouch = 0f;
                    reactionTimeToError = 0f; // convert to seconds
                    responseToErrorsCount = 0;
                    handVelocity = 0f; // convert to m/s
                    numberOfActionsCount = 0;
                    expectedPath.clear();
                    takenPath.clear();
                    takenPathModifiedShortest.clear();
                    expectedPathModifiedBackspace.clear();
                    KSPC_C = 0;
                    KSPC_INF = 0;
                    KSPC_IF = 0;
                    KSPC_F = 0;
                    modShortMSD_C = 0;
                    modShortMSD_INF = 0;
                    modBackspaceMSD_C = 0;
                    modBackspaceMSD_INF = 0;
                }
            }
        }
        // stuff arrays into subject Data
        subjectData.add(StatisticDataType.WORD_ORDER.name() + ": " + wordList.toString());
        subjectData.add(StatisticDataType.NUMBER_OF_TIMES_PLANE_BREACHED.name() + ": " + planeBreachedCountArray.toString());
        subjectData.add(StatisticDataType.DISTANCE_TRAVELED_FIRST_TOUCH.name() + ": " + distanceTraveledArray.toString());
        subjectData.add(StatisticDataType.DISTANCE_TRAVELED_FIRST_TOUCH_SHORTEST.name() + ": " + distanceTraveledShortArray.toString());
        subjectData.add(StatisticDataType.TIME_DURATION_FIRST_TOUCH.name() + ": " + timeDurationArray.toString());
        subjectData.add(StatisticDataType.TIME_DURATION_FIRST_TOUCH_SHORTEST.name() + ": " + timeDurationShortArray.toString());
        subjectData.add(StatisticDataType.AVERAGE_TOUCH_VELOCITY.name() + ": " + handVelocityAvgArray.toString());
        subjectData.add(StatisticDataType.REACTION_TIME_FIRST_PRESSED.name() + ": " + reactionTimeFirstPressedArray.toString());
        subjectData.add(StatisticDataType.REACTION_TIME_FIRST_TOUCH.name() + ": " + reactionTimeFirstTouchArray.toString());
        subjectData.add(StatisticDataType.AVERAGE_REACTION_TIME_TO_ERRORS.name() + ": " + reactionTimeToErrorAvgArray.toString());
        subjectData.add(StatisticDataType.TEXT_ENTRY_RATE_WPM.name() + ": " + WPM_Array.toString());
        subjectData.add(StatisticDataType.TEXT_ENTRY_RATE_MODIFIED_WPM_SHORTEST.name() + ": " + modShortWPM_Array.toString());
        subjectData.add(StatisticDataType.TEXT_ENTRY_RATE_MODIFIED_WPM_VULTURE.name() + ": " + modVultureWPM_Array.toString());
        subjectData.add(StatisticDataType.TEXT_ENTRY_RATE_MODIFIED_WPM_SHORTEST_VULTURE.name() + ": " + modShortVultureWPM_Array.toString());
        subjectData.add(StatisticDataType.ERROR_RATE_KSPC.name() + ": " + KSPC_Array.toString());
        subjectData.add(StatisticDataType.ERROR_RATE_MODIFIED_MSD_SHORTEST.name() + ": " + modShortMSD_Array.toString());
        subjectData.add(StatisticDataType.ERROR_RATE_MODIFIED_MSD_WITH_BACKSPACE.name() + ": " + modBackspaceMSD_Array.toString());
        subjectData.add(StatisticDataType.FRECHET_DISTANCE_TOUCH_ONLY.name() + ": " + frechetDistanceArray.toString());
        subjectData.add(StatisticDataType.FRECHET_DISTANCE_TOUCH_ONLY_MODIFIED_SHORTEST.name() + ": " + modShortFrechetDistanceArray.toString());
        subjectData.add(StatisticDataType.FRECHET_DISTANCE_TOUCH_ONLY_MODIFIED_WITH_BACKSPACE.name() + ": " + modBackspaceFrechetDistanceArray.toString());
        return subjectData;
    }
    
    private ArrayList<String> calculateSubjectData(ArrayList<String> fileContents, IKeyboard keyboard) {
        ArrayList<String> subjectData = new ArrayList<String>();
        subjectData.add(StatisticDataType.KEYBOARD_TYPE.name() + ": " + keyboard.getFileName());
        VirtualKeyboard virtualKeyboard = (VirtualKeyboard) keyboard.getRenderables().getRenderable(Renderable.VIRTUAL_KEYBOARD);
        
        // Arrays
        ArrayList<String> wordList = new ArrayList<String>();
        ArrayList<Integer> planeBreachedCountArray = new ArrayList<Integer>();
        ArrayList<Float> distanceTraveledArray = new ArrayList<Float>();
        ArrayList<Float> distanceTraveledShortArray = new ArrayList<Float>();
        ArrayList<Float> timeDurationArray = new ArrayList<Float>();
        ArrayList<Float> timeDurationShortArray = new ArrayList<Float>();
        ArrayList<Float> reactionTimeFirstPressedArray = new ArrayList<Float>();
        ArrayList<Float> reactionTimeFirstTouchArray = new ArrayList<Float>();
        ArrayList<Float> reactionTimeToErrorAvgArray = new ArrayList<Float>();
        ArrayList<Float> handVelocityAvgArray = new ArrayList<Float>();
        ArrayList<Float> WPM_Array = new ArrayList<Float>();
        ArrayList<Float> modShortWPM_Array = new ArrayList<Float>();
        ArrayList<Float> modVultureWPM_Array = new ArrayList<Float>();
        ArrayList<Float> modShortVultureWPM_Array = new ArrayList<Float>();
        ArrayList<Float> KSPC_Array = new ArrayList<Float>();
        ArrayList<Float> modShortMSD_Array = new ArrayList<Float>();
        ArrayList<Float> modBackspaceMSD_Array = new ArrayList<Float>();
        ArrayList<Float> frechetDistanceArray = new ArrayList<Float>();
        ArrayList<Float> modShortFrechetDistanceArray = new ArrayList<Float>();
        ArrayList<Float> modBackspaceFrechetDistanceArray = new ArrayList<Float>();
        
        // Tracked Variables
        boolean firstLetter = true;
        String currentWord = null;
        long wordStartTime = 0l;
        long previousTime = 0l;
        
        boolean isTouching = false;
        boolean firstTouch = false;
        
        int planeBreachedCount = 0;
        
        float distanceTraveled = 0f;
        float distanceTraveledShort = 0f;
        Vector previousPosition = null;
        
        float timeDuration = 0f; // convert to seconds
        float timeDurationShort = 0f;
        
        float reactionTimeFirstPressed = 0f; // convert to seconds
        float reactionTimeFirstTouch = 0;
        
        float reactionTimeToError = 0f; // convert to seconds
        long timeErrorOccured = 0l;
        int responseToErrorsCount = 0;
        
        float handVelocity = 0f; // convert to m/s
        int numberOfActionsCount = 0;
        
        ArrayList<Vector> expectedPath = new ArrayList<Vector>();
        ArrayList<Vector> takenPath = new ArrayList<Vector>();
        ArrayList<Vector> takenPathModifiedShortest = new ArrayList<Vector>();
        ArrayList<Vector> expectedPathModifiedBackspace = new ArrayList<Vector>();
        
        float KSPC_C = 0f;
        float KSPC_INF = 0f;
        float KSPC_IF = 0f;
        float KSPC_F = 0f; // TODO: Figure out the problem here
        
        int modShortMSD_C = 0;
        float modShortMSD_INF = 0f;
        
        float modBackspaceMSD_C = 0f;
        float modBackspaceMSD_INF = 0f;
        
        boolean detectedShortestComplete = false;
        boolean lastEnterAfterShortestComplete = false;
        
        for(FileData currentLine: fileData) {
            Key pressedKey = null;
            while(currentLine.hasNext()) {
                Entry<RecordedDataType, Object> currentData = currentLine.next();
                // Total distance traveled per word. (Array)
                // Average hand velocity per word. (Array)
                // Time duration per word. (Array)
                // Reaction time per word. (Array)
                // Average reaction time to errors per word. (Array)
                // Calculate KSPC
                // Calculate modified-MSD
                // Calculate WPM
                // Calculate Frechet Distance - Use Djikstra's for shortest path for controller keyboard to find expected path.
                // Calculate Frechet Distance Modified - this will use same set as mod-MSD
                boolean reportData = false;
                switch(currentData.getKey()) {
                    case WORD_VALUE:
                        previousTime = currentLine.getTime();
                        wordStartTime = currentLine.getTime();
                        numberOfActionsCount = 0;
                        responseToErrorsCount = 0;
                        detectedShortestComplete = false;
                        lastEnterAfterShortestComplete = false;
                        previousPosition = null;
                        String newWord = (String) currentData.getValue();
                        for(char c: newWord.toCharArray()) {
                            expectedPath.add(virtualKeyboard.getVirtualKey(Key.getByValue(c)).getCenter());
                        }
                        currentWord = newWord;
                        break;
                    case KEY_PRESSED:
                        // First letter of word
                        pressedKey = (Key) currentData.getValue();
                        if(!detectedShortestComplete && pressedKey == Key.getByValue(currentWord.charAt(modShortMSD_C))) {
                            modShortMSD_C++;
                            if(modShortMSD_C == currentWord.length()) {
                                detectedShortestComplete = true;
                            }
                        }
                        if(!firstTouch) {
                            firstTouch = true;
                            reactionTimeFirstTouch = (float) ((previousTime - wordStartTime) / SECOND_AS_NANO);
                            timeDuration = (float) (previousTime / SECOND_AS_NANO);
                        }
                        if(!isTouching) {
                            isTouching = true;
                            takenPath.add(previousPosition);
                            if(!detectedShortestComplete) takenPathModifiedShortest.add(previousPosition);
                        }
                        if(pressedKey == Key.VK_ENTER) {
                            isTouching = false;
                            planeBreachedCount++;
                            if(detectedShortestComplete && !lastEnterAfterShortestComplete) {
                                lastEnterAfterShortestComplete = true;
                                distanceTraveledShort = distanceTraveled;
                                timeDurationShort = (float) ((previousTime / SECOND_AS_NANO) - timeDuration);
                            }
                        }
                        break;
                    case KEY_EXPECTED:
                        Key expectedKey = (Key) currentData.getValue();
                        if(pressedKey.equals(Key.VK_ENTER) && expectedKey.equals(Key.VK_ENTER)) {
                            reportData = true;
                            timeDuration = (float) ((previousTime / SECOND_AS_NANO) - timeDuration);
                        } else if(pressedKey.equals(Key.VK_BACK_SPACE) && expectedKey.equals(Key.VK_BACK_SPACE)) {
                            KSPC_F++;
                            KSPC_IF++;
                            modBackspaceMSD_C++;
                            expectedPathModifiedBackspace.add(virtualKeyboard.getVirtualKey(Key.VK_BACK_SPACE).getCenter());
                            if(timeErrorOccured > 0) {
                                reactionTimeToError += (previousTime - timeErrorOccured) / SECOND_AS_NANO;
                                timeErrorOccured = 0;
                                responseToErrorsCount++;
                            }
                        } else if(pressedKey.equals(expectedKey)) {
                            KSPC_C++;
                            modBackspaceMSD_C++;
                            expectedPathModifiedBackspace.add(virtualKeyboard.getVirtualKey(pressedKey).getCenter());
                            if(firstLetter) {
                                firstLetter = false;
                                reactionTimeFirstPressed = (float) ((previousTime - wordStartTime) / SECOND_AS_NANO);
                            }
                        } else if(!pressedKey.equals(expectedKey) && pressedKey != Key.VK_ENTER) {
                            if(timeErrorOccured == 0 && expectedKey != Key.VK_BACK_SPACE && pressedKey != Key.VK_BACK_SPACE) {
                                timeErrorOccured = currentLine.getTime();
                                if(!detectedShortestComplete) modShortMSD_INF++;
                                modBackspaceMSD_INF++;
                            } else if(expectedKey != Key.VK_BACK_SPACE && pressedKey == Key.VK_BACK_SPACE) {
                                KSPC_F++; // If you hit backspace more than you should...
                                //KSPC_INF++; - not quite right
                            } else if(expectedKey == Key.VK_BACK_SPACE && pressedKey != Key.VK_BACK_SPACE) {
                                if(!detectedShortestComplete) modShortMSD_INF++;
                                modBackspaceMSD_INF++;
                            }
                        }
                        previousTime = currentLine.getTime();
                        break;
                    case POINT_POSITION:
                        Vector newPosition = (Vector) currentData.getValue();
                        if(isTouching && previousPosition != null) {
                            takenPath.add(newPosition);
                            if(!detectedShortestComplete) takenPathModifiedShortest.add(newPosition);
                            if(isTouching) {
                                numberOfActionsCount++;
                                handVelocity += 
                                        (MyUtilities.MATH_UTILITILES.findDistanceToPoint(previousPosition, newPosition) * PIXEL_TO_METER)
                                        / ((currentLine.getTime() - previousTime) / SECOND_AS_NANO);
                            }
                        }
                        if(firstTouch && previousPosition != null) {
                            distanceTraveled += MyUtilities.MATH_UTILITILES.findDistanceToPoint(previousPosition, newPosition);
                        }
                        previousPosition = newPosition;
                        previousTime = currentLine.getTime();
                        break;
                    case PRACTICE_WORD_COUNT:
                        subjectData.add(StatisticDataType.PRACTICE_WORDS_PER_INPUT.name() + ": " + currentData.getValue());
                        break;
                    default: break;
                }
                
                if(reportData) {
                    // WPM = (currentWord.length() / timeDuration) * 60 * (1/5);
                    // KSPC = (KSPC_C + KSPC_INF + KSPC_IF + KSPC_F) / (KSPC_C + KSPC_INF)
                    // modMSD = (modMSD_INF / (modMSD_C + modMSD_INF)) * 100;
                    wordList.add(currentWord);
                    planeBreachedCountArray.add(planeBreachedCount);
                    distanceTraveledArray.add(distanceTraveled *= PIXEL_TO_METER);
                    distanceTraveledShortArray.add(distanceTraveledShort *= PIXEL_TO_METER);
                    timeDurationArray.add(timeDuration);
                    timeDurationShortArray.add(timeDurationShort);
                    handVelocityAvgArray.add(handVelocity / numberOfActionsCount);
                    reactionTimeFirstPressedArray.add(reactionTimeFirstPressed);
                    reactionTimeFirstTouchArray.add(reactionTimeFirstTouch);
                    if(responseToErrorsCount == 0) {
                        reactionTimeToErrorAvgArray.add(-1f);
                    } else {
                        reactionTimeToErrorAvgArray.add(reactionTimeToError / responseToErrorsCount);   
                    }
                    WPM_Array.add(((currentWord.length() - 1) / ((timeDuration + reactionTimeFirstTouch) - reactionTimeFirstPressed)) * 60f * (1f / 5f));
                    modShortWPM_Array.add(((currentWord.length() - 1) / ((timeDurationShort + reactionTimeFirstTouch) - reactionTimeFirstPressed)) * 60f * (1f / 5f));
                    modVultureWPM_Array.add((currentWord.length() / (timeDuration + reactionTimeFirstTouch)) * 60f * (1f / 5f));
                    modShortVultureWPM_Array.add((currentWord.length() / (timeDurationShort + reactionTimeFirstTouch)) * 60f * (1f / 5f));
                    KSPC_Array.add((KSPC_C + KSPC_INF + KSPC_IF + KSPC_F) / (KSPC_C + KSPC_INF));
                    modShortMSD_Array.add((modShortMSD_INF / (modShortMSD_C + modShortMSD_INF)) * 100);
                    modBackspaceMSD_Array.add((modBackspaceMSD_INF / (modBackspaceMSD_C + modBackspaceMSD_INF)) * 100);
                    frechetDistanceArray.add(MyUtilities.MATH_UTILITILES.calculateFrechetDistance(
                            takenPath.toArray(new Vector[takenPath.size()]),
                            expectedPath.toArray(new Vector[expectedPath.size()])));
                    modShortFrechetDistanceArray.add(MyUtilities.MATH_UTILITILES.calculateFrechetDistance(
                            takenPathModifiedShortest.toArray(new Vector[takenPathModifiedShortest.size()]),
                            expectedPath.toArray(new Vector[expectedPath.size()])));
                    modBackspaceFrechetDistanceArray.add(MyUtilities.MATH_UTILITILES.calculateFrechetDistance(
                            takenPath.toArray(new Vector[takenPath.size()]),
                            expectedPathModifiedBackspace.toArray(new Vector[expectedPathModifiedBackspace.size()])));
                    reportData = false;
                    firstLetter = true;
                    planeBreachedCount = 0;
                    isTouching = false;
                    firstTouch = false;
                    distanceTraveled = 0f;
                    distanceTraveledShort = 0f;
                    timeDuration = 0f; // convert to seconds
                    timeDurationShort = 0f;
                    previousPosition = null;
                    reactionTimeFirstPressed = 0f; // convert to seconds
                    reactionTimeFirstTouch = 0f;
                    reactionTimeToError = 0f; // convert to seconds
                    responseToErrorsCount = 0;
                    handVelocity = 0f; // convert to m/s
                    numberOfActionsCount = 0;
                    expectedPath.clear();
                    takenPath.clear();
                    takenPathModifiedShortest.clear();
                    expectedPathModifiedBackspace.clear();
                    KSPC_C = 0;
                    KSPC_INF = 0;
                    KSPC_IF = 0;
                    KSPC_F = 0;
                    modShortMSD_C = 0;
                    modShortMSD_INF = 0;
                    modBackspaceMSD_C = 0;
                    modBackspaceMSD_INF = 0;
                }
            }
        }
        // stuff arrays into subject Data
        subjectData.add(StatisticDataType.WORD_ORDER.name() + ": " + wordList.toString());
        subjectData.add(StatisticDataType.NUMBER_OF_TIMES_PLANE_BREACHED.name() + ": " + planeBreachedCountArray.toString());
        subjectData.add(StatisticDataType.DISTANCE_TRAVELED_FIRST_TOUCH.name() + ": " + distanceTraveledArray.toString());
        subjectData.add(StatisticDataType.DISTANCE_TRAVELED_FIRST_TOUCH_SHORTEST.name() + ": " + distanceTraveledShortArray.toString());
        subjectData.add(StatisticDataType.TIME_DURATION_FIRST_TOUCH.name() + ": " + timeDurationArray.toString());
        subjectData.add(StatisticDataType.TIME_DURATION_FIRST_TOUCH_SHORTEST.name() + ": " + timeDurationShortArray.toString());
        subjectData.add(StatisticDataType.AVERAGE_TOUCH_VELOCITY.name() + ": " + handVelocityAvgArray.toString());
        subjectData.add(StatisticDataType.REACTION_TIME_FIRST_PRESSED.name() + ": " + reactionTimeFirstPressedArray.toString());
        subjectData.add(StatisticDataType.REACTION_TIME_FIRST_TOUCH.name() + ": " + reactionTimeFirstTouchArray.toString());
        subjectData.add(StatisticDataType.AVERAGE_REACTION_TIME_TO_ERRORS.name() + ": " + reactionTimeToErrorAvgArray.toString());
        subjectData.add(StatisticDataType.TEXT_ENTRY_RATE_WPM.name() + ": " + WPM_Array.toString());
        subjectData.add(StatisticDataType.TEXT_ENTRY_RATE_MODIFIED_WPM_SHORTEST.name() + ": " + modShortWPM_Array.toString());
        subjectData.add(StatisticDataType.TEXT_ENTRY_RATE_MODIFIED_WPM_VULTURE.name() + ": " + modVultureWPM_Array.toString());
        subjectData.add(StatisticDataType.TEXT_ENTRY_RATE_MODIFIED_WPM_SHORTEST_VULTURE.name() + ": " + modShortVultureWPM_Array.toString());
        subjectData.add(StatisticDataType.ERROR_RATE_KSPC.name() + ": " + KSPC_Array.toString());
        subjectData.add(StatisticDataType.ERROR_RATE_MODIFIED_MSD_SHORTEST.name() + ": " + modShortMSD_Array.toString());
        subjectData.add(StatisticDataType.ERROR_RATE_MODIFIED_MSD_WITH_BACKSPACE.name() + ": " + modBackspaceMSD_Array.toString());
        subjectData.add(StatisticDataType.FRECHET_DISTANCE_TOUCH_ONLY.name() + ": " + frechetDistanceArray.toString());
        subjectData.add(StatisticDataType.FRECHET_DISTANCE_TOUCH_ONLY_MODIFIED_SHORTEST.name() + ": " + modShortFrechetDistanceArray.toString());
        subjectData.add(StatisticDataType.FRECHET_DISTANCE_TOUCH_ONLY_MODIFIED_WITH_BACKSPACE.name() + ": " + modBackspaceFrechetDistanceArray.toString());
        return subjectData;
    }
    
    private void enableUI() {
        // This is required to give pseudo-knowledge of when this
        // thread is done and re-enable the UI remotely.
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                for(JButton button: buttons) {
                    button.setEnabled(true);
                }
            }
        });
    }
    
    private ArrayList<FileData> parseFileContents(ArrayList<String> fileContents) {
        ArrayList<FileData> fileData = new ArrayList<FileData>();
        for(String line: fileContents) {
            // Remove whitespace from line and then delimit the string based on semicolon.
            line = line.replaceAll("\\s+|\\(|\\)", "");
            String[] events = line.split(";");
            
            // Want to get the event time and a list of data that was recorded at that time.
            long eventTime = 0;
            LinkedHashMap<RecordedDataType, Object> entries = new LinkedHashMap<RecordedDataType, Object>();
            
            for(int i = 0; i < events.length; i++) {
                // Delimit by colon to break into data type, value pair.
                String[] eventInfo = events[i].split(":");
                
                // Want to get the data type and data value unless it's a time value.
                Object dataValue = null;
                RecordedDataType dataType = RecordedDataType.getByName(eventInfo[0]);
                
                switch(dataType) {
                    case TIME_WORD_START:
                        eventTime = Long.valueOf(eventInfo[1]);
                        break;
                    case TIME_WORD_END:
                        eventTime = Long.valueOf(eventInfo[1]);
                        break;
                    case TIME_PRESSED:
                        eventTime = Long.valueOf(eventInfo[1]);
                        break;
                    case TIME_SPECIAL:
                        eventTime = Long.valueOf(eventInfo[1]);
                        break;
                    case WORD_VALUE:
                        dataValue = eventInfo[1];
                        break;
                    case KEY_PRESSED:
                        dataValue = Key.getByName(eventInfo[1]);
                        break;
                    case KEY_EXPECTED:
                        dataValue = Key.getByName(eventInfo[1]);
                        break;
                    case POINT_POSITION:
                        String[] vectorPos = eventInfo[1].split(",");
                        float xPos = Float.valueOf(vectorPos[0]);
                        float yPos = Float.valueOf(vectorPos[1]);
                        float zPos = Float.valueOf(vectorPos[2]);
                        dataValue = new Vector(xPos, yPos, zPos);
                        break;
                    case DIRECTION_PRESSED:
                        dataValue = Direction.getByName(eventInfo[1]);
                        break;
                    case PRACTICE_WORD_COUNT:
                        eventTime = -1;
                        dataValue = Integer.valueOf(eventInfo[1]);
                        break;
                    default: break;
                }
                if(dataValue != null) {
                    entries.put(dataType, dataValue);
                }
            }
            if(!entries.isEmpty() && eventTime != 0) {
                fileData.add(new FileData(eventTime, new ArrayList<Entry<RecordedDataType, Object>>(entries.entrySet())));
            }
        }
        return fileData;
    }
    
    private class FileData {
        private long eventTime;
        private ArrayList<Entry<RecordedDataType, Object>> eventData;
        private int eventIndex = 0;
        
        FileData(long eventTime, ArrayList<Entry<RecordedDataType, Object>> eventData) {
            this.eventTime = eventTime;
            this.eventData = eventData;
        }
        
        public long getTime() {
            return eventTime;
        }
        
        public boolean hasNext() {
            if(eventIndex < eventData.size()) {
                return true;
            }
            eventIndex = 0;
            return false;
        }
        
        public Entry<RecordedDataType, Object> next() {
            if(hasNext()) {
                return eventData.get(eventIndex++);
            }
            return null;
        }
    }
}

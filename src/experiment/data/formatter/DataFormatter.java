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
import utilities.FileUtilities;
import utilities.MyUtilities;

public class DataFormatter implements Runnable {
    public enum FormatProcessType {
        CONSOLIDATE,
        CALCULATE;
    }
    private static final long SECOND_AS_NANO = 1000000000;
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
        ArrayList<String> subjectData = new ArrayList<String>();
        subjectData.add(StatisticDataType.KEYBOARD_TYPE.name() + ": " + keyboard.getFileName());
        VirtualKeyboard virtualKeyboard = (VirtualKeyboard) keyboard.getRenderables().getRenderable(Renderable.VIRTUAL_KEYBOARD);
        // Tracked Variables
        String currentWord = "";
        long currentTime = 0l;
        Key currentKey = null;
        float distanceTraveled = 0f;
        float reactionTime = 0f; // convert to seconds
        float reactionTimeToErrorAvg = 0f; // convert to seconds
        float handVelocityAvg = 0f; // convert to m/s
        float timeDuration = 0f; // convert to seconds
        
        ArrayList<Vector> expectedPath;
        ArrayList<Vector> takenPath;
        ArrayList<Vector> expectedPathModified;
        ArrayList<Vector> takenPathModified;
        
        int KSPC_C = 0;
        int KSPC_INF = 0;
        int KSPC_IF = 0;
        int KSPC_F = 0;
        
        int modMSD_C = 0;
        int modMSD_INF = 0;
        
        // WPM = (currentWord.length / timeDuration) * 60 * (1/5);
        // KSPC = (KSPC_C + KSPC_INF + KSPC_IF + KSPC_F) / (KSPC_C + KSPC_INF)
        // modMSD = (modMSD_INF / (modMSD_C + modMSD_INF)) * 100;
        
        for(FileData currentLine: fileData) {
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
                switch(currentData.getKey()) {
                    case TIME_EXPERIMENT_START:
                        long start = currentLine.getTime();
                        break;
                    case TIME_EXPERIMENT_END:
                        long end = currentLine.getTime();
                        break;
                    case WORD_VALUE:
                        String w = (String) currentData.getValue();
                        break;
                    case KEY_PRESSED:
                        currentKey = (Key) currentData.getValue();
                        break;
                    case KEY_EXPECTED:
                        Key k = (Key) currentData.getValue();
                        if(currentKey == Key.VK_BACK_SPACE && k == Key.VK_BACK_SPACE) {
                            KSPC_F++;
                        }
                        break;
                    case DIRECTION_PRESSED:
                        Direction d = (Direction) currentData.getValue();
                        break;
                    case PRACTICE_WORD_COUNT:
                        subjectData.add(StatisticDataType.PRACTICE_WORDS_PER_INPUT.name() + ": " + currentData.getValue());
                        break;
                    default: break;
                }
            }
        }
        return subjectData;
    }
    
    private ArrayList<String> calculateSubjectData(ArrayList<String> fileContents, IKeyboard keyboard) {
        ArrayList<String> subjectData = new ArrayList<String>();
        VirtualKeyboard virtualKeyboard = (VirtualKeyboard) keyboard.getRenderables().getRenderable(Renderable.VIRTUAL_KEYBOARD);
        // First thing to write in the file is the file name of the keyboard:
        //KeyboardType.getByID(keyboardType.getID()).getFileName();
        for(FileData currentLine: fileData) {
            while(currentLine.hasNext()) {
                Entry<RecordedDataType, Object> currentData = currentLine.next();
                
                switch(currentData.getKey()) {
                    case TIME_EXPERIMENT_START:
                        long start = currentLine.getTime();
                        break;
                    case TIME_EXPERIMENT_END:
                        long end = currentLine.getTime();
                        break;
                    case WORD_VALUE:
                        String w = (String) currentData.getValue();
                        break;
                    case KEY_PRESSED:
                        Key kp = (Key) currentData.getValue();
                        break;
                    case KEY_EXPECTED:
                        Key ke = (Key) currentData.getValue();
                        break;
                    case POINT_POSITION:
                        Vector v = (Vector) currentData.getValue();
                        break;
                    case DIRECTION_PRESSED:
                        Direction d = (Direction) currentData.getValue();
                        break;
                    case PRACTICE_WORD_COUNT:
                        int pwc = (Integer) currentData.getValue();
                        break;
                    default: break;
                }
                System.out.println(currentData.getKey() + " " + currentData.getValue());
            }
        }
        // File Layout:
        // Keyboard Name: name
        // Practice words: #
        // Total distance traveled per word. (Array)
        // Reaction time per word. (Array)
        // Average reaction time to errors per word. (Array)
        // Time duration per word. (Array)
        
        
        // Calculate Frechet Distance - Use Djikstra's for shortest path for controller keyboard to find expected path.
        // Calculate Frechet Distance Modified - this will use same set as mod-MSD
        // Calculate KSPC
        // Calculate modified-MSD
        // Calculate WPM
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
                    case TIME_EXPERIMENT_START:
                        eventTime = Long.valueOf(eventInfo[1]);
                        dataValue = eventTime;
                        break;
                    case TIME_EXPERIMENT_END:
                        eventTime = Long.valueOf(eventInfo[1]);
                        dataValue = eventTime;
                        break;
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

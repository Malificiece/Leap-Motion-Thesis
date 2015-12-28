package experiment.data.formatter;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.swing.JButton;
import javax.swing.SwingUtilities;

import keyboard.IKeyboard;
import keyboard.leap.LeapKeyboard;
import keyboard.renderables.LeapPlane;
import keyboard.renderables.VirtualKeyboard;
import leap.LeapListener;

import com.leapmotion.leap.Vector;

import enums.Direction;
import enums.ExitSurveyDataType;
import enums.ExitSurveyOptions;
import enums.FileExt;
import enums.FileName;
import enums.FilePath;
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
        CALCULATE,
        SPECIAL;
    }
    private final double SECOND_AS_NANO = 1000000000;
    private final float PIXEL_TO_CENTIMETER_TOUCH = 0.053039f;
    private final float PIXEL_TO_CENTIMETER_OTHERS = 0.027673f;
    private final float MILLIMETER_TO_CENTIMETER = 0.1f;
    private final String TUTORIAL = FileName.TUTORIAL.getName();
    private FormatProcessType type;
    private File directory;
    private JButton [] buttons;
    
    public DataFormatter(FormatProcessType type, File directory, JButton [] buttons) {
        this.type = type;
        this.directory = directory;
        this.buttons = buttons;
    }
    
    @Override
    public void run() {
        try {
            if(type == FormatProcessType.CALCULATE) {
                calculate();
            } else if(type == FormatProcessType.CONSOLIDATE) {
                consolidate();
            } else {
                special();
            }
        } finally {
            enableUI();   
        }
    }
    
    private void special() {
        // retrieve dictionaries
        ArrayList<ArrayList<String>> dictionaries = new ArrayList<ArrayList<String>>();
        for(Keyboard keyboard: Keyboard.values()) {
            if(keyboard.getType() != KeyboardType.DISABLED) {
                try {
                    dictionaries.add(MyUtilities.FILE_IO_UTILITIES.readListFromFile(FilePath.DICTIONARY.getPath(), keyboard.getType().getFileName() + FileExt.DICTIONARY.getExt()));
                } catch (IOException e) {
                    System.out.println("Dictionary failed: " + FilePath.DICTIONARY.getPath() + "\\" + keyboard.getType().getFileName() + FileExt.DICTIONARY.getExt());
                    e.printStackTrace();
                }
            }
        }
        
        // reorder words
        ArrayList<ArrayList<String>> matlab = new ArrayList<ArrayList<String>>();
        for(int dictionaryIndex = 0; dictionaryIndex < dictionaries.get(0).size(); dictionaryIndex++) {
            ArrayList<String> words = new ArrayList<String>();
            for(int wordIndex = 0; wordIndex < dictionaries.size(); wordIndex++) {
                words.add("'" + dictionaries.get(wordIndex).get(dictionaryIndex) + "'");
            }
            matlab.add(words);
        }
        
        String output = "DICTIONARIES = {";
        output += matlab.get(0);
        for(int i = 1; i < matlab.size(); i++) {
            output += ", " + matlab.get(i);
        }
        output +=  "}";
        System.out.println(output.replaceAll("\\[", "\\{").replaceAll("\\]", "\\}"));
        
        String alphabet = "abcdefghijklmnopqrstuvwxyz";
        Keyboard keyboard = Keyboard.getByType(KeyboardType.TABLET);
        VirtualKeyboard virtualKeyboard = (VirtualKeyboard) keyboard.getKeyboard().getRenderables().getRenderable(Renderable.VIRTUAL_KEYBOARD);
        ArrayList<Vector> keyLocs = new ArrayList<Vector>();
        for(int i = 0; i < alphabet.length(); i++) {
            keyLocs.add(virtualKeyboard.getVirtualKey(Key.getByValue(alphabet.charAt(i))).getCenter());
        }
        String vectors = keyLocs.toString().replaceAll("\\[", "\\{").replaceAll("\\]", "\\}");
        vectors = vectors.replaceAll("\\(", "\\[").replaceAll("\\)", "\\]");
        System.out.println("KEYBOARD = " + vectors);
        
        // Stuff words into linked hashmap
        LinkedHashMap<String, ArrayList<String>> wordPaths = new LinkedHashMap<String, ArrayList<String>>();
        for(ArrayList<String> wordSet: matlab) {
            for(String w: wordSet) {
                wordPaths.put(w, new ArrayList<String>());
            }
        }
        
        // Go through each subject and then calculate their data and put it into one .dat file.
        for(File subjectDirectory: directory.listFiles()) {
            String subjectID = subjectDirectory.getName();
            if(subjectDirectory.isDirectory() && !TUTORIAL.equals(subjectID)
                    && !("t2qcj5nu".equals(subjectID) // Get rid of extras (reduce to 18)
                    || "n2hjcorb".equals(subjectID))) {
                for(File file: subjectDirectory.listFiles()) {
                    if(file.getName().contains(FileName.SUBJECT_MERGED_DATA.getName())) {
                        ArrayList<String> fileContents = null;
                        try {
                            fileContents = MyUtilities.FILE_IO_UTILITIES.readListFromFile(file);
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        //String keyboardName = null;
                        ArrayList<String> wordOrder = null;
                        int currentWord = -1;
                        for(String line: fileContents) {
                            // Remove whitespace from line and then delimit the string based on semicolon.
                            line = line.replaceAll("\\s+", "");
                            // Delimit by colon to break into data type, value pair.
                            String[] dataInfo = line.split(":");
                            
                            // Want to get the data type and data value unless it's a time value.
                            StatisticDataType dataType = StatisticDataType.getByName(dataInfo[0]);
                            if(dataType == StatisticDataType.KEYBOARD_TYPE) {
                                // assign current keyboard here
                                dataInfo[1] = dataInfo[1].replaceAll("\\[|\\]", "");
                                //keyboardName = dataInfo[1];
                            } else if(dataType == StatisticDataType.WORD_ORDER) {
                                dataInfo[1] = dataInfo[1].replaceAll("\\[|\\]", "");
                                wordOrder = new ArrayList<String>();
                                wordOrder.addAll(Arrays.asList(dataInfo[1].split(",")));
                                currentWord = 0;
                            } else if(dataType == StatisticDataType.TAKEN_PATH_TOUCH_ONLY) {
                                String [] parts = dataInfo[1].split("\\],\\[");
                                for(String p: parts) {
                                    p = p.replaceAll("\\[|\\]", "").replaceAll("\\)", "\\]").replaceAll("\\(", "\\[");
                                    ArrayList<String> value = wordPaths.get("'"+wordOrder.get(currentWord)+"'");
                                    value.add(p);
                                    currentWord++;
                                }
                            }
                        }
                    }
                }
            }
        }
        // Have to format the string for whatever word array we get
        // laid out as[word1, word2..., [x,y],[x,y],[x,y],...wordn]
        String order = "WORD_ORDER = {";
        String path = "WORD_PATHS = {";
        int first = 0;
        for(Entry<String, ArrayList<String>> entry: wordPaths.entrySet()) {
            ArrayList<String> paths = entry.getValue();
            if(first == 0) {
                order += entry.getKey();
                path += "{{" + paths.get(0);
                for(int i = 1; i < paths.size(); i++) {
                    path += "}, {" + paths.get(i);
                }
                path += "}}";
                first++;
            } else {
                order += ", "  +entry.getKey();
                path += ", {{" + paths.get(0);
                for(int i = 1; i < paths.size(); i++) {
                    path += "}, {" + paths.get(i);
                }
                path += "}}";
            }
        }
        order += "}";
        path += "}";
        
        System.out.println(order);
        System.out.println(path);
    }
    
    private void consolidate() {
        // Sort the order of the subjects by their dates first.
        ArrayList<File> subjectDirectories = new ArrayList<File>();
        {
            HashMap<File, Date> dateMap = new HashMap<File, Date>();
            DateComparator dateComparator = new DateComparator(dateMap);
            TreeMap<File, Date> sortedMap = new TreeMap<File, Date>(dateComparator);
            for(File subjectDirectory: directory.listFiles()) {
                String subjectID = subjectDirectory.getName();
                if(subjectDirectory.isDirectory() && !TUTORIAL.equals(subjectID)
                        && !("t2qcj5nu".equals(subjectID)
                                || "n2hjcorb".equals(subjectID))) {
                    for(File file: subjectDirectory.listFiles()) {
                        if(file.getName().contains(FileName.EXIT_SURVEY.getName())) {
                            // Remove extraneous text from filename.
                            String fileName = file.getName();
                            fileName = fileName.replace(subjectID + "_" + FileName.EXIT_SURVEY.getName() + "_", "");
                            fileName = fileName.replace(FileExt.FILE.getExt(), "");
                            
                            // Parse date with format "yyyyMMdd_kkmm"
                            DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_kkmm", Locale.ENGLISH);
                            Date date = null;
                            try {
                                date = dateFormat.parse(fileName);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }  
                            dateMap.put(subjectDirectory, date);
                        }
                    }
                }
            }
            sortedMap.putAll(dateMap);
            subjectDirectories.addAll(sortedMap.keySet());
        }

        // Go through each subject and then consolidate their data and put it into one .m file.
        LinkedHashMap<String, String> consolidatedData = new LinkedHashMap<String, String>();
        LinkedHashMap<String, String> exitSurveyData = new LinkedHashMap<String, String>();
        String subjectOrder = null;
        for(File subjectDirectory: subjectDirectories) {
            String subjectID = subjectDirectory.getName();
            String keyboardName = null;
            if(subjectOrder == null) {
                subjectOrder = "'" + subjectID + "'";
            } else {
                subjectOrder += ", '" + subjectID + "'";
            }
            // Read the merged data file
            try {
                String fileName = subjectID + "_" + FileName.SUBJECT_MERGED_DATA.getName() + FileExt.DAT.getExt();
                ArrayList<String> fileContents = MyUtilities.FILE_IO_UTILITIES.readListFromFile(subjectDirectory.getPath() + "\\", fileName);
                for(String line: fileContents) {
                    // Remove whitespace from line and then delimit the string based on semicolon.
                    line = line.replaceAll("\\s+|\\[|\\]", "");
                    // Delimit by colon to break into data type, value pair.
                    String[] dataInfo = line.split(":");
                    
                    // Want to get the data type and data value unless it's a time value.
                    StatisticDataType dataType = StatisticDataType.getByName(dataInfo[0]);
                    
                    if(dataType == StatisticDataType.KEYBOARD_TYPE) {
                        // assign current keyboard here
                        keyboardName = dataInfo[1];
                    } else if(dataType != StatisticDataType.WORD_ORDER && dataType != StatisticDataType.TAKEN_PATH_TOUCH_ONLY) {
                        String key = keyboardName + "_" + dataType.name();
                        String value = consolidatedData.get(key);
                        /*if(value != null) {
                            consolidatedData.put(key, value + "; " + dataInfo[1].replaceAll(",", "; "));
                        } else {
                            consolidatedData.put(key, dataInfo[1].replaceAll(",", "; "));
                        }*/
                        String [] values = dataInfo[1].split(",");
                        float average = 0;
                        for(String s: values) {
                            float v = Float.parseFloat(s);
                            average += v;
                        }
                        average /= values.length;
                        if(value != null) {
                            consolidatedData.put(key, value + "; " + average);
                        } else {
                            consolidatedData.put(key, "" + average);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("There was an error while trying to read from file for: " + subjectID);
            }
            // Read the exit survey file
            try {
                String fileName = subjectID + "_" + FileName.EXIT_SURVEY.getName() + FileUtilities.WILDCARD + FileExt.FILE.getExt();
                ArrayList<String> fileContents = MyUtilities.FILE_IO_UTILITIES.readListFromWildcardFile(subjectDirectory.getPath() + "\\", fileName);
                for(String line: fileContents) {
                    // Delimit by colon to break into data type, value pair.
                    String[] dataInfo = line.split(": ");
                    
                    // Want to get the data type and data value unless it's a time value.
                    ExitSurveyDataType dataType = ExitSurveyDataType.getByName(dataInfo[0]);
                    
                    if(ExitSurveyDataType.isLikert(dataType)) {
                        String key = dataInfo[0];
                        String value = consolidatedData.get(key);
                        int numeric = ExitSurveyOptions.getNumericValuebyDescription(dataInfo[1]);
                        if(value != null) {
                            consolidatedData.put(key, value + "; " + numeric);
                        } else {
                            consolidatedData.put(key, "" + numeric);
                        }
                    } else if(ExitSurveyDataType.isRanking(dataType)) {
                        String key = dataInfo[0];
                        String value = consolidatedData.get(key);
                        if(value != null) {
                            consolidatedData.put(key, value + "; " + dataInfo[1]);
                        } else {
                            consolidatedData.put(key, dataInfo[1]);
                        }
                    } else {
                        String key = dataInfo[0];
                        String value = exitSurveyData.get(key);
                        switch(dataType) {
                            case AGE:
                                if(value != null) {
                                    exitSurveyData.put(key, value + "; " + dataInfo[1]);
                                } else {
                                    exitSurveyData.put(key, dataInfo[1]);
                                }
                                break;
                            case GENDER:
                                if(value != null) {
                                    exitSurveyData.put(key, value + "; '" + dataInfo[1] + "'");
                                } else {
                                    exitSurveyData.put(key, "'" + dataInfo[1] + "'");
                                }
                                break;
                            case HANDEDNESS:
                                if(value != null) {
                                    exitSurveyData.put(key, value + "; '" + dataInfo[1] + "'");
                                } else {
                                    exitSurveyData.put(key, "'" + dataInfo[1] + "'");
                                }
                                break;
                            case HAS_PHYSICAL_IMPAIRMENT:
                            {
                                int numeric = ExitSurveyOptions.getNumericValuebyDescription(dataInfo[1]);
                                if(value != null) {
                                    exitSurveyData.put(key, value + "; " + numeric);
                                } else {
                                    exitSurveyData.put(key,  "" + numeric);
                                }
                            }
                                break;
                            case HAS_PREVIOUS_GESTURE_DEVICE_EXPERIENCE:
                            {
                                int numeric = ExitSurveyOptions.getNumericValuebyDescription(dataInfo[1]);
                                if(value != null) {
                                    exitSurveyData.put(key, value + "; " + numeric);
                                } else {
                                    exitSurveyData.put(key,  "" + numeric);
                                }
                            }
                                break;
                            case HAS_PREVIOUS_SWIPE_DEVICE_EXPERIENCE:
                            {
                                int numeric = ExitSurveyOptions.getNumericValuebyDescription(dataInfo[1]);
                                if(value != null) {
                                    exitSurveyData.put(key, value + "; " + numeric);
                                } else {
                                    exitSurveyData.put(key,  "" + numeric);
                                }
                            }
                                break;
                            case HAS_PREVIOUS_TOUCH_DEVICE_EXPERIENCE:
                            {
                                int numeric = ExitSurveyOptions.getNumericValuebyDescription(dataInfo[1]);
                                if(value != null) {
                                    exitSurveyData.put(key, value + "; " + numeric);
                                } else {
                                    exitSurveyData.put(key,  "" + numeric);
                                }
                            }
                                break;
                            case HOURS_PER_WEEK_ON_COMPUTER:
                                if(value != null) {
                                    exitSurveyData.put(key, value + "; '" + dataInfo[1] + "'");
                                } else {
                                    exitSurveyData.put(key, "'" + dataInfo[1] + "'");
                                }
                                break;
                            case PREFERED_HANDEDNESS_FOR_EXPERIMENT:
                                if(value != null) {
                                    exitSurveyData.put(key, value + "; '" + dataInfo[1] + "'");
                                } else {
                                    exitSurveyData.put(key, "'" + dataInfo[1] + "'");
                                }
                                break;
                            default: break;
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("There was an error while trying to read from file for: " + subjectID);
            }
        }
        LinkedHashMap<String, ArrayList<Float>> standardDeviations = new LinkedHashMap<String, ArrayList<Float>>();
        LinkedHashMap<String, ArrayList<Float>> means = new LinkedHashMap<String, ArrayList<Float>>();
        // Set up the matrices we'll perform ANOVAs on.
        LinkedHashMap<String, String> matrices = new LinkedHashMap<String, String>();
        for(Keyboard keyboard: Keyboard.values()) {
            KeyboardType keyboardType = keyboard.getType();
            if(keyboardType != KeyboardType.DISABLED) {
                for(Entry<String, String> entry: consolidatedData.entrySet()) {
                    if(KeyboardType.getByName(entry.getKey()) == keyboardType) {
                        
                        String key = entry.getKey().substring(entry.getKey().indexOf("_") + 1);
                        {
                            StatisticDataType sdt = StatisticDataType.getByName(key);
                            if(sdt != null) {
                                key = sdt.name();
                            } else {
                                key = ExitSurveyDataType.getByName(key).name();
                            }
                        }
                        
                        // Find the SD and Mean of the list
                        String [] values = entry.getValue().split("; ");
                        float avgM = 0;
                        for(String s: values) {
                            float v = Float.parseFloat(s);
                            avgM += v;
                        }
                        avgM /= values.length;
                        double avgSD = 0;
                        for(String s: values) {
                            float v = Float.parseFloat(s);
                            avgSD += Math.pow((v - avgM), 2);
                        }
                        avgSD /= values.length;
                        avgSD = Math.sqrt(avgSD);
    
                        String value = matrices.get(key);
                        if(value != null) {
                            matrices.put(key, value + ", " + entry.getKey());
                            standardDeviations.get(key).add((float) avgSD);
                            means.get(key).add(avgM);
                        } else {
                            matrices.put(key, entry.getKey());
                            ArrayList<Float> arraySD = new ArrayList<Float>();
                            ArrayList<Float> arrayM = new ArrayList<Float>();
                            arraySD.add((float) avgSD);
                            arrayM.add(avgM);
                            standardDeviations.put(key, arraySD);
                            means.put(key, arrayM);
                        }
                    }
                }
                String key = StatisticDataType.KEYBOARD_ORDER.name();
                String value = matrices.get(key);
                if(value != null) {
                    matrices.put(key, value + ", '" + keyboardType.getFileName() + "'");
                } else {
                    matrices.put(key, "'" + keyboardType.getFileName() + "'");
                }
            }
        }
        matrices.put(StatisticDataType.SUBJECT_ORDER.name(), subjectOrder);
        matrices.putAll(exitSurveyData);
        
        ArrayList<Entry<String, ArrayList<Float>>> meanEntries = new ArrayList<Entry<String, ArrayList<Float>>>(means.entrySet());
        ArrayList<Entry<String, ArrayList<Float>>> sdEntries = new ArrayList<Entry<String, ArrayList<Float>>>(standardDeviations.entrySet());
        
        float finalAverageSamplePooled = 0;
        float finalAverageSamplePop = 0;
        int finalCount = 0;
        for(int entryIndex = 0; entryIndex < meanEntries.size(); entryIndex++) {
            ArrayList<Float> arrayM = meanEntries.get(entryIndex).getValue();
            ArrayList<Float> arraySD = sdEntries.get(entryIndex).getValue();
            /*float minM = Float.POSITIVE_INFINITY;
            float maxM = -1;
            float avgM = 0;
            float minSD = Float.POSITIVE_INFINITY;
            float maxSD = -1;
            float avgSD = 0;
            for(int j = 0; j < arrayM.size(); j++) {
                float m = arrayM.get(j);
                float sd = arraySD.get(j);
                if(m < minM) minM = m;
                if(sd < minSD) minSD = sd;
                if(m > maxM) maxM = m;
                if(sd > maxSD) maxSD = sd;
                avgM += m;
                avgSD += sd;
            }
            avgM /= arrayM.size();
            avgSD /= arraySD.size();
            
            System.out.println(meanEntries.get(i).getKey() + " | minMean: " + minM + " avgMean: " + avgM + " maxMean: " + maxM
                    + " | minSD: " + minSD + " avgSD: " + avgSD + " maxSD: " + maxSD);*/
            //System.out.println(meanEntries.get(i).getKey() + "\t\t\t | mean = " + avgM + "\t\t | sd = " + avgSD);
            
            /*System.out.print("means: ");
            for(int j = 0; j < arrayM.size(); j++) {
                if(j == 0) {
                    System.out.print(arrayM.get(j));
                } else {
                    System.out.print(", " + arrayM.get(j));
                }
            }
            System.out.println();
            System.out.print("stds: ");
            for(int j = 0; j < arraySD.size(); j++) {
                if(j == 0) {
                    System.out.print(arraySD.get(j));
                } else {
                    System.out.print(", " + arraySD.get(j));
                }
            }
            System.out.println();*/
            
            StatisticDataType sdt = StatisticDataType.getByName(meanEntries.get(entryIndex).getKey());
            if(sdt != null
                    && sdt != StatisticDataType.PRACTICE_WORDS_PER_INPUT
                    && sdt != StatisticDataType.REACTION_TIME_FIRST_PRESSED
                    && sdt != StatisticDataType.REACTION_TIME_FIRST_TOUCH) {
                System.out.println("Variable: " + meanEntries.get(entryIndex).getKey());
                System.out.println("-------------------------------------------------------");
                // Use population standard deviation
                float averageSamplePooled = 0;
                float averageSamplePop = 0;
                int count = 0;
                
                // Tablet vs all Leap
                int tabletIndex = 0;
                for(Keyboard keyboard: Keyboard.values()) {
                    if(keyboard.getType() != KeyboardType.DISABLED) {
                        if(keyboard.getType() == KeyboardType.TABLET) {
                            break;
                        } else {
                            tabletIndex++;
                        }
                    }
                }
                //float maxSamplePooled = -1;
                //float maxSamplePop = -1;
                {
                    int keyboardIndex = 0;
                    for(Keyboard keyboard: Keyboard.values()) {
                        KeyboardType keyboardType = keyboard.getType();
                        if(keyboardType.isLeap()) {
                            float sdPop = arraySD.get(tabletIndex);
                            float sdPooled = (float) Math.pow(((6 * Math.pow(sdPop, 2)) + (6 * Math.pow(arraySD.get(keyboardIndex), 2))) / (12), 0.5);
                            float meanChange = arrayM.get(tabletIndex) - arrayM.get(keyboardIndex);
                            float zVal = (float) Math.pow(1.96f + 0.84f, 2);
                            float nPop = (float) (2 * zVal / Math.pow(meanChange / sdPop, 2));
                            float nPool = (float) (2 * zVal / Math.pow(meanChange / sdPooled, 2));
                            System.out.println("Population: " + KeyboardType.TABLET.getFileName() + " (mean=" + arrayM.get(tabletIndex) + ")"
                                    +  " | Sample: " + keyboardType.getFileName() + " (mean=" + arrayM.get(keyboardIndex) + ")");
                            System.out.println("sample size (population st. dev): " + nPop);
                            System.out.println("sample size     (pooled st. dev): " + nPool + "\n");
                            count++;
                            averageSamplePooled += nPool;
                            averageSamplePop += nPop;
                            //if(nPop > maxSamplePop) maxSamplePop = nPop;
                            //if(nPool > maxSamplePooled) maxSamplePooled = nPool;
                        }
                        if(keyboardType != KeyboardType.DISABLED) {
                            keyboardIndex++;
                        }
                    }
                }
                
                // AirPinch vs other AirLeap
                int airPinchIndex = 0;
                for(Keyboard keyboard: Keyboard.values()) {
                    if(keyboard.getType() != KeyboardType.DISABLED) {
                        if(keyboard.getType() == KeyboardType.LEAP_AIR_PINCH) {
                            break;
                        } else {
                            airPinchIndex++;
                        }
                    }
                }
                //float maxSamplePooled = -1;
                //float maxSamplePop = -1;
                {
                    int keyboardIndex = 0;
                    for(Keyboard keyboard: Keyboard.values()) {
                        KeyboardType keyboardType = keyboard.getType();
                        if(keyboardType != KeyboardType.LEAP_AIR_PINCH && keyboardType.isLeap()) {
                            float sdPop = arraySD.get(airPinchIndex);
                            float sdPooled = (float) Math.pow(((6 * Math.pow(sdPop, 2)) + (6 * Math.pow(arraySD.get(keyboardIndex), 2))) / (12), 0.5);
                            float meanChange = arrayM.get(airPinchIndex) - arrayM.get(keyboardIndex);
                            float zVal = (float) Math.pow(1.96f + 0.84f, 2);
                            float nPop = (float) (2 * zVal / Math.pow(meanChange / sdPop, 2));
                            float nPool = (float) (2 * zVal / Math.pow(meanChange / sdPooled, 2));
                            if(nPop <= 100 && nPool <= 100) {
                                System.out.println("Population: " + KeyboardType.LEAP_AIR_PINCH.getFileName() + " (mean=" + arrayM.get(airPinchIndex) + ")"
                                        +  " | Sample: " + keyboardType.getFileName() + " (mean=" + arrayM.get(keyboardIndex) + ")");
                                System.out.println("sample size (population st. dev): " + nPop);
                                System.out.println("sample size     (pooled st. dev): " + nPool + "\n");
                                count++;
                                averageSamplePooled += nPool;
                                averageSamplePop += nPop;
                                //if(nPop > maxSamplePop) maxSamplePop = nPop;
                                //if(nPool > maxSamplePooled) maxSamplePooled = nPool;
                            }
                        }
                        if(keyboardType != KeyboardType.DISABLED) {
                            keyboardIndex++;
                        }
                    }
                }
                finalAverageSamplePooled += averageSamplePooled;
                finalAverageSamplePop += averageSamplePop;
                finalCount += count;
                averageSamplePooled /= count;
                averageSamplePop /= count;
                System.out.println("Average sample size (population st. dev): " + averageSamplePop);
                System.out.println("Average sample size     (pooled st. dev): " + averageSamplePooled + "\n");
                System.out.println("-------------------------------------------------------");
            } else {
                //System.out.println(meanEntries.get(i).getKey() + " --- (not used to calculate sample size)");
                //System.out.println("-------------------------------------------------------");
            }
        }
        
        finalAverageSamplePooled /= finalCount;
        finalAverageSamplePop /= finalCount;
        
        System.out.println("Final average sample size (population st. dev): " + finalAverageSamplePop);
        System.out.println("Final average sample size     (pooled st. dev): " + finalAverageSamplePooled);
        
        // write to file
        try {
            ArrayList<String> data = new ArrayList<String>();
            data.add("% Input Variables");
            for(Keyboard keyboard: Keyboard.values()) {
                KeyboardType keyboardType = keyboard.getType();
                if(keyboardType != KeyboardType.DISABLED) {
                    for(Entry<String, String> entry: consolidatedData.entrySet()) {
                        if(KeyboardType.getByName(entry.getKey()) == keyboardType) {
                            data.add(entry.getKey() + " = [" + entry.getValue() + "];");
                        }
                    }
                }
            }
            data.add("\n% Merged Input Matrices");
            for(Entry<String, String> entry: matrices.entrySet()) {
                StatisticDataType dataType = StatisticDataType.getByName(entry.getKey());
                if(dataType == StatisticDataType.KEYBOARD_ORDER || dataType == StatisticDataType.SUBJECT_ORDER) {
                    data.add(entry.getKey() + " = {" + entry.getValue() + "};");
                } else {
                    data.add(entry.getKey() + " = [" + entry.getValue() + "];");
                }
            }
            data.add("\n% Anovas");
            
            String fileName = FileName.CONSOLIDATED_EXPERIMENT_DATA.getName() + FileExt.MATLAB.getExt();
            MyUtilities.FILE_IO_UTILITIES.writeListToFile(data, directory.getPath() + "\\", fileName, false);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("There was an error while trying to write the consolidated file.");
        }
    }
    
    private void calculate() {
        // Go through each subject and then calculate their data and put it into one .dat file.
        for(File subjectDirectory: directory.listFiles()) {
            String subjectID = subjectDirectory.getName();
            if(subjectDirectory.isDirectory() && !TUTORIAL.equals(subjectID)) {
                ArrayList<String> subjectData = new ArrayList<String>();
                for(Keyboard keyboard: Keyboard.values()) {
                    if(keyboard.getType() != KeyboardType.DISABLED) {
                        IKeyboard iKeyboard = keyboard.getKeyboard();
                        String wildcardFileName = subjectID + "_" + iKeyboard.getFileName() + FileUtilities.WILDCARD + FileExt.PLAYBACK.getExt();
                        try {
                            ArrayList<String> fileContents = MyUtilities.FILE_IO_UTILITIES.readListFromWildcardFile(subjectDirectory.getPath() + "\\", wildcardFileName);
                            ArrayList<PlaybackFileData> fileData = parsePlaybackFileContents(fileContents);
                            switch(keyboard) {
                                case CONTROLLER_CONSOLE:
                                    subjectData.addAll(calculateSubjectDataController(fileData, iKeyboard));
                                    break;
                                case TABLET:
                                    subjectData.addAll(calculateSubjectData(fileData, iKeyboard, subjectDirectory));
                                    break;
                                case LEAP_SURFACE:
                                    subjectData.addAll(calculateSubjectData(fileData, iKeyboard, subjectDirectory));
                                    break;
                                case LEAP_AIR_STATIC:
                                    subjectData.addAll(calculateSubjectData(fileData, iKeyboard, subjectDirectory));
                                    break;
                                case LEAP_AIR_DYNAMIC:
                                    subjectData.addAll(calculateSubjectData(fileData, iKeyboard, subjectDirectory));
                                    break;
                                case LEAP_AIR_PINCH:
                                    subjectData.addAll(calculateSubjectData(fileData, iKeyboard, subjectDirectory));
                                    break;
                                case LEAP_AIR_BIMODAL:
                                    subjectData.addAll(calculateSubjectData(fileData, iKeyboard, subjectDirectory));
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
                    MyUtilities.FILE_IO_UTILITIES.writeListToFile(subjectData, subjectDirectory.getPath() + "\\", fileName, false);
                } catch (IOException e) {
                    e.printStackTrace();
                    System.err.println("There was an error while trying to write calculated data to file for: " + subjectID);
                }
            }
        }
    }
    
    private ArrayList<String> calculateSubjectDataController(ArrayList<PlaybackFileData> fileData, IKeyboard keyboard) {
        SimulatedController simulatedController = new SimulatedController(keyboard);
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
        ArrayList<Float> pixelVelocityAvgArray = new ArrayList<Float>();
        ArrayList<Float> handVelocityAvgArray = new ArrayList<Float>();
        ArrayList<Float> WPM_Array = new ArrayList<Float>();
        ArrayList<Float> modShortWPM_Array = new ArrayList<Float>();
        ArrayList<Float> modVultureWPM_Array = new ArrayList<Float>();
        ArrayList<Float> modShortVultureWPM_Array = new ArrayList<Float>();
        ArrayList<Float> KSPC_Array = new ArrayList<Float>();
        ArrayList<Float> modShortKSPC_Array = new ArrayList<Float>();
        //ArrayList<Float> modBackspaceKSPC_Array = new ArrayList<Float>();
        ArrayList<Float> modShortMSD_Array = new ArrayList<Float>();
        ArrayList<Float> modBackspaceMSD_Array = new ArrayList<Float>();
        ArrayList<Float> MWD_Array = new ArrayList<Float>();
        ArrayList<Float> modShortMWD_Array = new ArrayList<Float>();
        ArrayList<Float> modBackspaceMWD_Array = new ArrayList<Float>();
        ArrayList<Float> totalErrorRateArray = new ArrayList<Float>();
        ArrayList<Float> totalErrorRateShortArray = new ArrayList<Float>();
        ArrayList<Float> totalErrorRateBackspaceArray = new ArrayList<Float>();
        ArrayList<Float> frechetDistanceArray = new ArrayList<Float>();
        ArrayList<Float> modShortFrechetDistanceArray = new ArrayList<Float>();
        ArrayList<Float> modBackspaceFrechetDistanceArray = new ArrayList<Float>();
        ArrayList<ArrayList<Vector>> takenPathArray = new ArrayList<ArrayList<Vector>>();
        
        // Tracked Variables
        boolean firstLetter = true;
        String currentWord = null;
        long wordStartTime = 0l;
        long previousTime = 0l;
        Key previousKey = null;
        
        boolean isTouching = false;
        boolean firstTouch = false;
        
        int planeBreachedCount = 0;
        
        float distanceTraveled = 0f;
        float distanceTraveledShort = 0f;
        
        float timeDuration = 0f; // convert to seconds
        float timeDurationShort = 0f;
        
        float touchDuration = 0f;
        
        float reactionTimeFirstPressed = 0f; // convert to seconds
        float reactionTimeFirstTouch = 0;
        
        float reactionTimeToError = 0f; // convert to seconds
        long timeErrorOccured = 0l;
        int responseToErrorsCount = 0;
        
        float handVelocity = 0f; // convert to m/s
        float pixelVelocity = 0f; // pixel/s
        int numberOfActionsCount = 0;
        
        ArrayList<Vector> expectedPath = new ArrayList<Vector>();
        ArrayList<Vector> takenPath = new ArrayList<Vector>();
        ArrayList<Vector> takenPathModifiedShortest = new ArrayList<Vector>();
        ArrayList<Vector> expectedPathModifiedBackspace = new ArrayList<Vector>();
        
        float C = 0f;
        float INF = 0f;
        float IF = 0f;
        float F = 0f;
        
        int shortC = 0;
        float shortINF = 0f;
        float shortIF = 0f;
        float shortF = 0f;
        
        float backspaceC = 0f;
        float backspaceINF = 0f;
        float backspaceIF = 0f;
        //float backspaceF = 0f;
        
        boolean detectedShortestComplete = false;
        
        int MWD = 0;
        boolean errorOccured = false;
        int shortMWD = 0;
        int backspaceMWD = 0;
        
        for(PlaybackFileData currentLine: fileData) {
            Key pressedKey = null;
            while(currentLine.hasNext()) {
                Entry<RecordedDataType, Object> currentData = currentLine.next();
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
                            simulatedController.selectKey(Key.VK_Q);
                            //expectedPath.add(virtualKeyboard.getVirtualKey(Key.VK_Q).getCenter());
                            //expectedPathModifiedBackspace.add(virtualKeyboard.getVirtualKey(Key.VK_Q).getCenter());
                            for(char c: newWord.toCharArray()) {
                                // This path should contain the last key used or where the selection started.
                                expectedPath.add(virtualKeyboard.getVirtualKey(Key.getByValue(c)).getCenter());
                            }
                            //takenPath.add(virtualKeyboard.getVirtualKey(Key.VK_Q).getCenter());
                            //takenPathModifiedShortest.add(virtualKeyboard.getVirtualKey(Key.VK_Q).getCenter());
                        } else if(!currentWord.equals(newWord)) {
                            //expectedPath.add(virtualKeyboard.getVirtualKey(simulatedController.getSelectedKey()).getCenter());
                            //expectedPathModifiedBackspace.add(virtualKeyboard.getVirtualKey(simulatedController.getSelectedKey()).getCenter());
                            for(char c: newWord.toCharArray()) {
                                // This path should contain the last key used or where the selection started.
                                expectedPath.add(virtualKeyboard.getVirtualKey(Key.getByValue(c)).getCenter());
                            }
                            //takenPath.add(virtualKeyboard.getVirtualKey(simulatedController.getSelectedKey()).getCenter());
                            //takenPathModifiedShortest.add(virtualKeyboard.getVirtualKey(simulatedController.getSelectedKey()).getCenter());
                            //previousKey = null;
                        }
                        currentWord = newWord;
                        previousKey = null;
                        break;
                    case KEY_PRESSED:
                        // First letter of word
                        pressedKey = (Key) currentData.getValue();
                        if(!detectedShortestComplete && pressedKey == Key.getByValue(currentWord.charAt(shortC))) {
                            shortC++;
                            if(shortC == currentWord.length()) {
                                detectedShortestComplete = true;
                                distanceTraveledShort = distanceTraveled;
                                timeDurationShort = (float) ((currentLine.getTime() / SECOND_AS_NANO) - touchDuration);
                            }
                        }
                        if(!firstTouch) {
                            firstTouch = true;
                            reactionTimeFirstTouch = (float) ((currentLine.getTime() - wordStartTime) / SECOND_AS_NANO);
                            //timeDuration = (float) (currentLine.getTime() / SECOND_AS_NANO);
                        }
                        if(!isTouching) {
                            isTouching = true;
                            takenPath.add(virtualKeyboard.getVirtualKey(simulatedController.getSelectedKey()).getCenter());
                            if(!detectedShortestComplete) takenPathModifiedShortest.add(virtualKeyboard.getVirtualKey(simulatedController.getSelectedKey()).getCenter());
                            touchDuration = (float) (currentLine.getTime() / SECOND_AS_NANO); 
                        }
                        if(pressedKey == Key.VK_ENTER) {
                            isTouching = false;
                            planeBreachedCount++;
                            // Update time duration here
                            if(!firstLetter) timeDuration += (float) ((previousTime / SECOND_AS_NANO) - touchDuration);
                        }
                        break;
                    case KEY_EXPECTED:
                        Key expectedKey = (Key) currentData.getValue();
                        if(pressedKey == Key.VK_ENTER && expectedKey == Key.VK_ENTER) {
                            reportData = true;
                            //timeDuration = (float) ((previousTime / SECOND_AS_NANO) - timeDuration);
                        } else if(pressedKey == Key.VK_BACK_SPACE && expectedKey == Key.VK_BACK_SPACE) {
                            INF--;
                            F++;
                            IF++;
                            if(!detectedShortestComplete) {
                                shortINF--;
                                shortIF++;
                                shortF++;
                            }
                            backspaceC++;
                            expectedPathModifiedBackspace.add(virtualKeyboard.getVirtualKey(Key.VK_BACK_SPACE).getCenter());
                            if(simulatedController.getSelectedKey() != Key.VK_BACK_SPACE) {
                                takenPath.add(virtualKeyboard.getVirtualKey(Key.VK_BACK_SPACE).getCenter());
                            }
                            if(timeErrorOccured > 0) {
                                reactionTimeToError += (currentLine.getTime() - timeErrorOccured) / SECOND_AS_NANO;
                                timeErrorOccured = 0;
                                responseToErrorsCount++;
                            }
                        } else if(pressedKey == expectedKey) {
                            C++;
                            backspaceC++;
                            expectedPathModifiedBackspace.add(virtualKeyboard.getVirtualKey(pressedKey).getCenter());
                            if(pressedKey == previousKey) {
                                takenPath.add(virtualKeyboard.getVirtualKey(simulatedController.getSelectedKey()).getCenter());
                            }
                            if(firstLetter) {
                                firstLetter = false;
                                reactionTimeFirstPressed = (float) ((currentLine.getTime() - wordStartTime) / SECOND_AS_NANO);
                            }
                        } else if(pressedKey != expectedKey && pressedKey != Key.VK_ENTER) {
                            if(timeErrorOccured == 0 && expectedKey != Key.VK_BACK_SPACE && pressedKey != Key.VK_BACK_SPACE) {
                                timeErrorOccured = currentLine.getTime();
                                if(!detectedShortestComplete) shortINF++;
                                backspaceINF++;
                                INF++;
                                if(expectedKey != Key.VK_ENTER) {
                                    expectedPathModifiedBackspace.add(virtualKeyboard.getVirtualKey(expectedKey).getCenter());
                                }
                                errorOccured = true;
                            } else if(expectedKey != Key.VK_BACK_SPACE && pressedKey == Key.VK_BACK_SPACE) {
                                // TODO: Backspace is hit when it shouldn't be ---
                                // however program prevents it from modifying the transcribed text
                                // Is this an error? Is this an attempted fix?
                                //F++;
                                //INF++;
                                //backspaceINF++;
                                if(previousKey != Key.VK_BACK_SPACE) {
                                    // This is equivalent to a mistake because they have no mistakes in the
                                    // transcribed text and they hit backspace, even if it doesn't affect
                                    // the transcribed text, this is still an error.
                                    // This is only done when the previousKey isn't a backspace because
                                    // The backspace key can be held down for multiple fires which often
                                    // happens with the more sensitive input methods.
                                    INF++;
                                    if(!detectedShortestComplete) shortINF++;
                                    backspaceINF++;
                                }
                            } else if(expectedKey == Key.VK_BACK_SPACE && pressedKey != Key.VK_BACK_SPACE) {
                                if(!detectedShortestComplete) shortINF++;
                                backspaceINF++;
                                INF++;
                            }
                        }
                        previousTime = currentLine.getTime();
                        previousKey = pressedKey;
                        break;
                    case DIRECTION_PRESSED:
                        Direction direction = (Direction) currentData.getValue();
                        Vector previousPosition = virtualKeyboard.getVirtualKey(simulatedController.getSelectedKey()).getCenter();
                        simulatedController.moveSelectedKey(direction);
                        Vector newPosition = virtualKeyboard.getVirtualKey(simulatedController.getSelectedKey()).getCenter();
                        if(isTouching) {
                            numberOfActionsCount++;
                            takenPath.add(newPosition);
                            if(!detectedShortestComplete) takenPathModifiedShortest.add(newPosition);
                            handVelocity += 
                                    (MyUtilities.MATH_UTILITILES.findDistanceToPoint(previousPosition, newPosition) * PIXEL_TO_CENTIMETER_OTHERS)
                                    / ((currentLine.getTime() - previousTime) / SECOND_AS_NANO);
                            pixelVelocity += 
                                    (MyUtilities.MATH_UTILITILES.findDistanceToPoint(previousPosition, newPosition))
                                    / ((currentLine.getTime() - previousTime) / SECOND_AS_NANO);
                            distanceTraveled += (MyUtilities.MATH_UTILITILES.findDistanceToPoint(previousPosition, newPosition));
                        }
                        previousTime = currentLine.getTime();
                        break;
                    case PRACTICE_WORD_COUNT:
                        subjectData.add(StatisticDataType.PRACTICE_WORDS_PER_INPUT.name() + ": " + currentData.getValue());
                        break;
                    default: break;
                }
                
                if(reportData) {
                    {
                        ArrayList<Vector> copy = new ArrayList<Vector>();
                        copy.addAll(takenPath);
                        takenPathArray.add(copy);
                    }
                    
                    // There is a weird situation that requires this to fire, but this is the correct fix.
                    if(timeDurationShort == 0) {
                        timeDurationShort = timeDuration;
                    }
                    
                    wordList.add(currentWord);
                    planeBreachedCountArray.add(planeBreachedCount);
                    distanceTraveledArray.add(distanceTraveled);
                    distanceTraveledShortArray.add(distanceTraveledShort);
                    timeDurationArray.add(timeDuration);
                    timeDurationShortArray.add(timeDurationShort);
                    handVelocityAvgArray.add(handVelocity / numberOfActionsCount);
                    pixelVelocityAvgArray.add(pixelVelocity / numberOfActionsCount);
                    reactionTimeFirstPressedArray.add(reactionTimeFirstPressed);
                    reactionTimeFirstTouchArray.add(reactionTimeFirstTouch);
                    if(responseToErrorsCount == 0) {
                        reactionTimeToErrorAvgArray.add(0f);
                    } else {
                        reactionTimeToErrorAvgArray.add(reactionTimeToError / responseToErrorsCount);   
                    }
                    WPM_Array.add(((currentWord.length() - 1) / timeDuration) * 60f * (1f / 5f));
                    modShortWPM_Array.add(((currentWord.length() - 1) / timeDurationShort) * 60f * (1f / 5f));
                    modVultureWPM_Array.add((currentWord.length() / (timeDuration + (reactionTimeFirstPressed))) * 60f * (1f / 5f));
                    modShortVultureWPM_Array.add((currentWord.length() / (timeDurationShort + (reactionTimeFirstPressed))) * 60f * (1f / 5f));
                    //System.out.println(reactionTimeFirstPressed + " - " + reactionTimeFirstTouch + " = " + (reactionTimeFirstPressed - reactionTimeFirstTouch));
                    //WPM_Array.add(((currentWord.length() - 1) / ((timeDuration + reactionTimeFirstTouch) - reactionTimeFirstPressed)) * 60f * (1f / 5f));
                    //modShortWPM_Array.add(((currentWord.length() - 1) / ((timeDurationShort + reactionTimeFirstTouch) - reactionTimeFirstPressed)) * 60f * (1f / 5f));
                    //modVultureWPM_Array.add((currentWord.length() / (timeDuration + reactionTimeFirstTouch)) * 60f * (1f / 5f));
                    //modShortVultureWPM_Array.add((currentWord.length() / (timeDurationShort + reactionTimeFirstTouch)) * 60f * (1f / 5f));
                    KSPC_Array.add((C + INF + IF + F) / (C + INF));
                    modShortKSPC_Array.add((shortC + shortINF + shortIF + shortF) / (shortC + shortINF));
                    //modBackspaceKSPC_Array.add((backspaceC + backspaceINF + backspaceIF + backspaceF) / (backspaceC + backspaceINF));
                    modShortMSD_Array.add((shortINF / (shortC + shortINF)) * 100);
                    modBackspaceMSD_Array.add((backspaceINF / (backspaceC + backspaceINF)) * 100);
                    if(errorOccured) {
                        MWD++;
                    }
                    if(shortINF > 0) {
                        shortMWD++;
                    }
                    if(backspaceINF > 0) {
                        backspaceMWD++;
                    }
                    totalErrorRateArray.add(((INF + IF) / (C + INF + IF)) * 100);
                    totalErrorRateShortArray.add(((shortINF + shortIF) / (shortC + shortINF + shortIF)) * 100);
                    totalErrorRateBackspaceArray.add(((backspaceINF + backspaceIF) / (backspaceC + backspaceINF + backspaceIF)) * 100);
                    /*System.out.println(currentWord + "----------------------------------------------------------------");
                    System.out.print("takenPath: ");
                    for(Vector v: takenPath) {
                        System.out.print(virtualKeyboard.getNearestKeyNoEnter(v, 9999).getKey().getValue() + " ");
                    }
                    System.out.println();
                    System.out.print("takeShort: ");
                    for(Vector v: takenPathModifiedShortest) {
                        System.out.print(virtualKeyboard.getNearestKeyNoEnter(v, 9999).getKey().getValue() + " ");
                    }
                    System.out.println();
                    System.out.print("expecPath: ");
                    for(Vector v: expectedPath) {
                        System.out.print(virtualKeyboard.getNearestKeyNoEnter(v, 9999).getKey().getValue() + " ");
                    }
                    System.out.println();
                    System.out.print("expBckspc: ");
                    for(Vector v: expectedPathModifiedBackspace) {
                        System.out.print(virtualKeyboard.getNearestKeyNoEnter(v, 9999).getKey().getValue() + " ");
                    }
                    System.out.println();
                    System.out.println("------------------------------------------------------------------------------");*/
                    // TODO: DECIDE IF THIS CONVERSION NEEDS TO BE DONE
                    frechetDistanceArray.add(MyUtilities.MATH_UTILITILES.calculateFrechetDistance(
                            takenPath.toArray(new Vector[takenPath.size()]),
                            expectedPath.toArray(new Vector[expectedPath.size()]))
                            /* * (keyboard.getType() == KeyboardType.TABLET ? PIXEL_TO_CENTIMETER_TOUCH : PIXEL_TO_CENTIMETER_OTHERS)*/);
                    modShortFrechetDistanceArray.add(MyUtilities.MATH_UTILITILES.calculateFrechetDistance(
                            takenPathModifiedShortest.toArray(new Vector[takenPathModifiedShortest.size()]),
                            expectedPath.toArray(new Vector[expectedPath.size()]))
                            /* * (keyboard.getType() == KeyboardType.TABLET ? PIXEL_TO_CENTIMETER_TOUCH : PIXEL_TO_CENTIMETER_OTHERS)*/);
                    modBackspaceFrechetDistanceArray.add(MyUtilities.MATH_UTILITILES.calculateFrechetDistance(
                            takenPath.toArray(new Vector[takenPath.size()]),
                            expectedPathModifiedBackspace.toArray(new Vector[expectedPathModifiedBackspace.size()]))
                            /* * (keyboard.getType() == KeyboardType.TABLET ? PIXEL_TO_CENTIMETER_TOUCH : PIXEL_TO_CENTIMETER_OTHERS)*/);
                    reportData = false;
                    firstLetter = true;
                    planeBreachedCount = 0;
                    isTouching = false;
                    firstTouch = false;
                    distanceTraveled = 0f;
                    distanceTraveledShort = 0f;
                    timeDuration = 0f; // convert to seconds
                    timeDurationShort = 0f;
                    previousKey = null;
                    reactionTimeFirstPressed = 0f; // convert to seconds
                    reactionTimeFirstTouch = 0f;
                    reactionTimeToError = 0f; // convert to seconds
                    responseToErrorsCount = 0;
                    handVelocity = 0f; // convert to m/s
                    pixelVelocity = 0f; // pixel/s
                    numberOfActionsCount = 0;
                    expectedPath.clear();
                    takenPath.clear();
                    takenPathModifiedShortest.clear();
                    expectedPathModifiedBackspace.clear();
                    C = 0f;
                    INF = 0f;
                    IF = 0f;
                    F = 0f;
                    shortC = 0;
                    shortINF = 0f;
                    shortIF = 0f;
                    shortF = 0f;
                    backspaceC = 0f;
                    backspaceINF = 0f;
                    backspaceIF = 0f;
                    //backspaceF = 0f;
                    detectedShortestComplete = false;
                    errorOccured = false;
                }
            }
        }
        
        MWD_Array.add((MWD / (float) wordList.size()) * 100);
        modShortMWD_Array.add((shortMWD / (float) wordList.size()) * 100);
        modBackspaceMWD_Array.add((backspaceMWD / (float) wordList.size()) * 100);
        
        // stuff arrays into subject Data
        subjectData.add(StatisticDataType.WORD_ORDER.name() + ": " + wordList.toString());
        subjectData.add(StatisticDataType.NUMBER_OF_TIMES_PLANE_BREACHED.name() + ": " + planeBreachedCountArray.toString());
        subjectData.add(StatisticDataType.DISTANCE_TRAVELED_TOUCH_ONLY.name() + ": " + distanceTraveledArray.toString());
        subjectData.add(StatisticDataType.DISTANCE_TRAVELED_TOUCH_ONLY_SHORTEST.name() + ": " + distanceTraveledShortArray.toString());
        subjectData.add(StatisticDataType.TIME_DURATION_TOUCH_ONLY.name() + ": " + timeDurationArray.toString());
        subjectData.add(StatisticDataType.TIME_DURATION_TOUCH_ONLY_SHORTEST.name() + ": " + timeDurationShortArray.toString());
        subjectData.add(StatisticDataType.AVERAGE_HAND_VELOCITY.name() + ": " + handVelocityAvgArray.toString());
        subjectData.add(StatisticDataType.AVERAGE_PIXEL_VELOCITY.name() + ": " + pixelVelocityAvgArray.toString());
        subjectData.add(StatisticDataType.REACTION_TIME_FIRST_PRESSED.name() + ": " + reactionTimeFirstPressedArray.toString());
        subjectData.add(StatisticDataType.REACTION_TIME_FIRST_TOUCH.name() + ": " + reactionTimeFirstTouchArray.toString());
        subjectData.add(StatisticDataType.AVERAGE_REACTION_TIME_TO_ERRORS.name() + ": " + reactionTimeToErrorAvgArray.toString());
        subjectData.add(StatisticDataType.TEXT_ENTRY_RATE_WPM.name() + ": " + WPM_Array.toString());
        subjectData.add(StatisticDataType.TEXT_ENTRY_RATE_MODIFIED_WPM_SHORTEST.name() + ": " + modShortWPM_Array.toString());
        subjectData.add(StatisticDataType.TEXT_ENTRY_RATE_MODIFIED_WPM_VULTURE.name() + ": " + WPM_Array.toString()); // TODO: since this is single input selection we use normal WPM
        subjectData.add(StatisticDataType.TEXT_ENTRY_RATE_MODIFIED_WPM_SHORTEST_VULTURE.name() + ": " + modShortWPM_Array.toString()); // TODO: since this is single input selection we use normal WPM
        subjectData.add(StatisticDataType.ERROR_RATE_KSPC.name() + ": " + KSPC_Array.toString());
        subjectData.add(StatisticDataType.ERROR_RATE_MODIFIED_KSPC_SHORTEST.name() + ": " + modShortKSPC_Array.toString());
        //subjectData.add(StatisticDataType.ERROR_RATE_MODIFIED_KSPC_BACKSPACE.name() + ": " + modBackspaceKSPC_Array.toString());
        subjectData.add(StatisticDataType.ERROR_RATE_MODIFIED_MSD_SHORTEST.name() + ": " + modShortMSD_Array.toString());
        subjectData.add(StatisticDataType.ERROR_RATE_MODIFIED_MSD_BACKSPACE.name() + ": " + modBackspaceMSD_Array.toString());
        subjectData.add(StatisticDataType.ERROR_RATE_MWD.name() + ": " + MWD_Array.toString());
        subjectData.add(StatisticDataType.ERROR_RATE_MODIFIED_MWD_SHORTEST.name() + ": " + modShortMWD_Array.toString());
        subjectData.add(StatisticDataType.ERROR_RATE_MODIFIED_MWD_BACKSPACE.name() + ": " + modBackspaceMWD_Array.toString());
        subjectData.add(StatisticDataType.TOTAL_ERROR_RATE.name() + ": " + totalErrorRateArray.toString());
        subjectData.add(StatisticDataType.TOTAL_ERROR_RATE_MODIFIED_SHORTEST.name() + ": " + totalErrorRateShortArray.toString());
        subjectData.add(StatisticDataType.TOTAL_ERROR_RATE_MODIFIED_BACKSPACE.name() + ": " + totalErrorRateBackspaceArray.toString());
        subjectData.add(StatisticDataType.FRECHET_DISTANCE_TOUCH_ONLY.name() + ": " + frechetDistanceArray.toString());
        subjectData.add(StatisticDataType.FRECHET_DISTANCE_TOUCH_ONLY_MODIFIED_SHORTEST.name() + ": " + modShortFrechetDistanceArray.toString());
        subjectData.add(StatisticDataType.FRECHET_DISTANCE_TOUCH_ONLY_MODIFIED_BACKSPACE.name() + ": " + modBackspaceFrechetDistanceArray.toString());
        subjectData.add(StatisticDataType.TAKEN_PATH_TOUCH_ONLY.name() + ": " + takenPathArray.toString());
        return subjectData;
    }
    
    private ArrayList<String> calculateSubjectData(ArrayList<PlaybackFileData> fileData, IKeyboard keyboard, File subjectDirectory) {
        ArrayList<String> subjectData = new ArrayList<String>();
        subjectData.add(StatisticDataType.KEYBOARD_TYPE.name() + ": " + keyboard.getFileName());
        VirtualKeyboard virtualKeyboard = (VirtualKeyboard) keyboard.getRenderables().getRenderable(Renderable.VIRTUAL_KEYBOARD);
        LeapPlane leapPlane = (LeapPlane) keyboard.getRenderables().getRenderable(Renderable.LEAP_PLANE);
        
        // We must load the settings that were used during the experiment for Leap Keyboards
        if(keyboard.getType().isLeap()) {
            File file = new File(subjectDirectory.getPath(), keyboard.getFileName() + FileExt.INI.getExt());
            keyboard.loadSettings(file);
            leapPlane.calculatePlaneData();
            LeapListener.registerObserver((LeapKeyboard) keyboard);
            LeapListener.startListening();
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
        ArrayList<Float> pixelVelocityAvgArray = new ArrayList<Float>();
        ArrayList<Float> handVelocityAvgArray = new ArrayList<Float>();
        ArrayList<Float> WPM_Array = new ArrayList<Float>();
        ArrayList<Float> modShortWPM_Array = new ArrayList<Float>();
        ArrayList<Float> modVultureWPM_Array = new ArrayList<Float>();
        ArrayList<Float> modShortVultureWPM_Array = new ArrayList<Float>();
        ArrayList<Float> KSPC_Array = new ArrayList<Float>();
        ArrayList<Float> modShortKSPC_Array = new ArrayList<Float>();
        //ArrayList<Float> modBackspaceKSPC_Array = new ArrayList<Float>();
        ArrayList<Float> modShortMSD_Array = new ArrayList<Float>();
        ArrayList<Float> modBackspaceMSD_Array = new ArrayList<Float>();
        ArrayList<Float> MWD_Array = new ArrayList<Float>();
        ArrayList<Float> modShortMWD_Array = new ArrayList<Float>();
        ArrayList<Float> modBackspaceMWD_Array = new ArrayList<Float>();
        ArrayList<Float> totalErrorRateArray = new ArrayList<Float>();
        ArrayList<Float> totalErrorRateShortArray = new ArrayList<Float>();
        ArrayList<Float> totalErrorRateBackspaceArray = new ArrayList<Float>();
        ArrayList<Float> frechetDistanceArray = new ArrayList<Float>();
        ArrayList<Float> modShortFrechetDistanceArray = new ArrayList<Float>();
        ArrayList<Float> modBackspaceFrechetDistanceArray = new ArrayList<Float>();
        ArrayList<ArrayList<Vector>> takenPathArray = new ArrayList<ArrayList<Vector>>();
        
        // Tracked Variables
        boolean firstLetter = true;
        String currentWord = null;
        long wordStartTime = 0l;
        long previousTime = 0l;
        Key previousKey = null;
        
        boolean isTouching = false;
        boolean firstTouch = false;
        
        int planeBreachedCount = 0;
        
        float distanceTraveled = 0f;
        float distanceTraveledShort = 0f;
        Vector previousPosition = null;
        
        float timeDuration = 0f; // convert to seconds
        float timeDurationShort = 0f;
        
        float touchDuration = 0f;
        
        float reactionTimeFirstPressed = 0f; // convert to seconds
        float reactionTimeFirstTouch = 0;
        
        float reactionTimeToError = 0f; // convert to seconds
        long timeErrorOccured = 0l;
        int responseToErrorsCount = 0;
        
        float handVelocity = 0f; // convert to m/s
        float pixelVelocity = 0f; // pixel/s
        int numberOfActionsCount = 0;
        
        ArrayList<Vector> expectedPath = new ArrayList<Vector>();
        ArrayList<Vector> takenPath = new ArrayList<Vector>();
        ArrayList<Vector> takenPathModifiedShortest = new ArrayList<Vector>();
        ArrayList<Vector> expectedPathModifiedBackspace = new ArrayList<Vector>();
        
        float C = 0f;
        float INF = 0f;
        float IF = 0f;
        float F = 0f;
        
        int shortC = 0;
        float shortINF = 0f;
        float shortIF = 0f;
        float shortF = 0f;
        
        float backspaceC = 0f;
        float backspaceINF = 0f;
        float backspaceIF = 0f;
        //float backspaceF = 0f;
        
        boolean detectedShortestComplete = false;
        boolean lastEnterAfterShortestComplete = false;
        
        int MWD = 0;
        boolean errorOccured = false;
        int shortMWD = 0;
        int backspaceMWD = 0;
        
        for(PlaybackFileData currentLine: fileData) {
            Key pressedKey = null;
            while(currentLine.hasNext()) {
                Entry<RecordedDataType, Object> currentData = currentLine.next();
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
                        if(!newWord.equals(currentWord)) {
                            for(char c: newWord.toCharArray()) {
                                expectedPath.add(virtualKeyboard.getVirtualKey(Key.getByValue(c)).getCenter());
                            }
                        }
                        currentWord = newWord;
                        previousKey = null;
                        break;
                    case KEY_PRESSED:
                        // First letter of word
                        pressedKey = (Key) currentData.getValue();
                        if(!detectedShortestComplete && pressedKey == Key.getByValue(currentWord.charAt(shortC))) {
                            shortC++;
                            if(shortC == currentWord.length()) {
                                detectedShortestComplete = true;
                            }
                        }
                        if(!firstTouch) {
                            firstTouch = true;
                            reactionTimeFirstTouch = (float) ((previousTime - wordStartTime) / SECOND_AS_NANO);
                            //timeDuration = (float) (previousTime / SECOND_AS_NANO);
                        }
                        if(!isTouching) {
                            isTouching = true;
                            takenPath.add(previousPosition);
                            if(!detectedShortestComplete) takenPathModifiedShortest.add(previousPosition);
                            touchDuration = (float) (previousTime / SECOND_AS_NANO); 
                        }
                        if(pressedKey == Key.VK_ENTER) {
                            isTouching = false;
                            planeBreachedCount++;
                            // Update time duration here
                            if(!firstLetter) timeDuration += (float) ((previousTime / SECOND_AS_NANO) - touchDuration);
                            if(detectedShortestComplete && !lastEnterAfterShortestComplete) {
                                lastEnterAfterShortestComplete = true;
                                distanceTraveledShort = distanceTraveled;
                                //timeDurationShort = (float) ((previousTime / SECOND_AS_NANO) - timeDuration);
                                timeDurationShort = timeDuration;
                            }
                        }
                        break;
                    case KEY_EXPECTED:
                        Key expectedKey = (Key) currentData.getValue();
                        if(pressedKey == Key.VK_ENTER && expectedKey == Key.VK_ENTER) {
                            reportData = true;
                            //timeDuration = (float) ((previousTime / SECOND_AS_NANO) - timeDuration);
                        } else if(pressedKey == Key.VK_BACK_SPACE && expectedKey == Key.VK_BACK_SPACE) {
                            INF--;
                            F++;
                            IF++;
                            if(!lastEnterAfterShortestComplete) {
                                shortINF--;
                                shortIF++;
                                shortF++;
                            }
                            backspaceC++;
                            expectedPathModifiedBackspace.add(virtualKeyboard.getVirtualKey(Key.VK_BACK_SPACE).getCenter());
                            if(timeErrorOccured > 0) {
                                reactionTimeToError += (previousTime - timeErrorOccured) / SECOND_AS_NANO;
                                timeErrorOccured = 0;
                                responseToErrorsCount++;
                            }
                        } else if(pressedKey == expectedKey) {
                            C++;
                            backspaceC++;
                            expectedPathModifiedBackspace.add(virtualKeyboard.getVirtualKey(pressedKey).getCenter());
                            if(firstLetter) {
                                firstLetter = false;
                                reactionTimeFirstPressed = (float) ((previousTime - wordStartTime) / SECOND_AS_NANO);
                            }
                        } else if(pressedKey != expectedKey && pressedKey != Key.VK_ENTER) {
                            if(timeErrorOccured == 0 && expectedKey != Key.VK_BACK_SPACE && pressedKey != Key.VK_BACK_SPACE) {
                                timeErrorOccured = currentLine.getTime();
                                if(!lastEnterAfterShortestComplete) shortINF++;
                                backspaceINF++;
                                INF++;
                                if(expectedKey != Key.VK_ENTER) {
                                    expectedPathModifiedBackspace.add(virtualKeyboard.getVirtualKey(expectedKey).getCenter());
                                }
                                errorOccured = true;
                            } else if(expectedKey != Key.VK_BACK_SPACE && pressedKey == Key.VK_BACK_SPACE) {
                                // TODO: Backspace is hit when it shouldn't be ---
                                // however program prevents it from modifying the transcribed text
                                // Is this an error? Is this an attempted fix?
                                //F++;
                                //INF++;
                                //backspaceINF++;
                                if(previousKey != Key.VK_BACK_SPACE) {
                                    // This is equivalent to a mistake because they have no mistakes in the
                                    // transcribed text and they hit backspace, even if it doesn't affect
                                    // the transcribed text, this is still an error.
                                    // This is only done when the previousKey isn't a backspace because
                                    // The backspace key can be held down for multiple fires which often
                                    // happens with the more sensitive input methods.
                                    INF++;
                                    if(!lastEnterAfterShortestComplete) shortINF++;
                                    backspaceINF++;
                                }
                            } else if(expectedKey == Key.VK_BACK_SPACE && pressedKey != Key.VK_BACK_SPACE) {
                                if(!lastEnterAfterShortestComplete) shortINF++;
                                backspaceINF++;
                                INF++;
                            }
                        }
                        previousKey = pressedKey;
                        previousTime = currentLine.getTime();
                        break;
                    case POINT_POSITION:
                        Vector newPosition = (Vector) currentData.getValue();
                        if(isTouching && previousPosition != null) {
                            takenPath.add(newPosition);
                            if(!lastEnterAfterShortestComplete) takenPathModifiedShortest.add(newPosition);
                            numberOfActionsCount++;
                            if(keyboard.getType().isLeap()) {
                                // We must convert the coordinates from the leap keyboards into the original device coordinate space
                                // so that we can get the actual hand movement speed and travel distance.
                                Vector previousDevicePosition = leapPlane.denormalizePoint(new Vector(previousPosition));
                                Vector newDevicePosition = leapPlane.denormalizePoint(new Vector(newPosition));
                                // Device coordinates are in millimeter so they need to be converted to centimeter.
                                handVelocity += 
                                        (MyUtilities.MATH_UTILITILES.findDistanceToPoint(previousDevicePosition, newDevicePosition) * MILLIMETER_TO_CENTIMETER)
                                        / ((currentLine.getTime() - previousTime) / SECOND_AS_NANO);
                                //distanceTraveled += (MyUtilities.MATH_UTILITILES.findDistanceToPoint(previousDevicePosition, newDevicePosition) * MILLIMETER_TO_CENTIMETER);
                            } else {
                                handVelocity += 
                                        (MyUtilities.MATH_UTILITILES.findDistanceToPoint(previousPosition, newPosition) * PIXEL_TO_CENTIMETER_TOUCH)
                                        / ((currentLine.getTime() - previousTime) / SECOND_AS_NANO);
                                //distanceTraveled += (MyUtilities.MATH_UTILITILES.findDistanceToPoint(previousPosition, newPosition) * PIXEL_TO_CENTIMETER_TOUCH);
                            }
                            distanceTraveled += (MyUtilities.MATH_UTILITILES.findDistanceToPoint(previousPosition, newPosition));
                            pixelVelocity += 
                                    (MyUtilities.MATH_UTILITILES.findDistanceToPoint(previousPosition, newPosition))
                                    / ((currentLine.getTime() - previousTime) / SECOND_AS_NANO);
                        }
                        if(firstTouch && previousPosition != null) {
                            // MOVED TO IS_TOUCHING SECTION
                            //distanceTraveled += MyUtilities.MATH_UTILITILES.findDistanceToPoint(previousPosition, newPosition);
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
                    {
                        ArrayList<Vector> copy = new ArrayList<Vector>();
                        copy.addAll(takenPath);
                        takenPathArray.add(copy);
                    }
                    
                    // There is a weird situation that requires this to fire, but this is the correct fix.
                    if(timeDurationShort == 0) {
                        timeDurationShort = timeDuration;
                    }
                    
                    wordList.add(currentWord);
                    planeBreachedCountArray.add(planeBreachedCount);
                    distanceTraveledArray.add(distanceTraveled);
                    distanceTraveledShortArray.add(distanceTraveledShort);
                    timeDurationArray.add(timeDuration);
                    timeDurationShortArray.add(timeDurationShort);
                    handVelocityAvgArray.add(handVelocity / numberOfActionsCount);
                    pixelVelocityAvgArray.add(pixelVelocity / numberOfActionsCount);
                    reactionTimeFirstPressedArray.add(reactionTimeFirstPressed);
                    reactionTimeFirstTouchArray.add(reactionTimeFirstTouch);
                    if(responseToErrorsCount == 0) {
                        reactionTimeToErrorAvgArray.add(0f);
                    } else {
                        reactionTimeToErrorAvgArray.add(reactionTimeToError / responseToErrorsCount);   
                    }
                    WPM_Array.add(((currentWord.length() - 1) / timeDuration) * 60f * (1f / 5f));
                    modShortWPM_Array.add(((currentWord.length() - 1) / timeDurationShort) * 60f * (1f / 5f));
                    modVultureWPM_Array.add((currentWord.length() / (timeDuration + (reactionTimeFirstPressed))) * 60f * (1f / 5f));
                    modShortVultureWPM_Array.add((currentWord.length() / (timeDurationShort + (reactionTimeFirstPressed))) * 60f * (1f / 5f));
                    //System.out.println(reactionTimeFirstPressed + " - " + reactionTimeFirstTouch + " = " + (reactionTimeFirstPressed - reactionTimeFirstTouch));
                    //WPM_Array.add(((currentWord.length() - 1) / ((timeDuration + reactionTimeFirstTouch) - reactionTimeFirstPressed)) * 60f * (1f / 5f));
                    //modShortWPM_Array.add(((currentWord.length() - 1) / ((timeDurationShort + reactionTimeFirstTouch) - reactionTimeFirstPressed)) * 60f * (1f / 5f));
                    //modVultureWPM_Array.add((currentWord.length() / (timeDuration + reactionTimeFirstTouch)) * 60f * (1f / 5f));
                    //modShortVultureWPM_Array.add((currentWord.length() / (timeDurationShort + reactionTimeFirstTouch)) * 60f * (1f / 5f));
                    KSPC_Array.add((C + INF + IF + F) / (C + INF));
                    modShortKSPC_Array.add((shortC + shortINF + shortIF + shortF) / (shortC + shortINF));
                    //modBackspaceKSPC_Array.add((backspaceC + backspaceINF + backspaceIF + backspaceF) / (backspaceC + backspaceINF));
                    modShortMSD_Array.add((shortINF / (shortC + shortINF)) * 100);
                    modBackspaceMSD_Array.add((backspaceINF / (backspaceC + backspaceINF)) * 100);
                    if(errorOccured) {
                        MWD++;
                    }
                    if(shortINF > 0) {
                        shortMWD++;
                    }
                    if(backspaceINF > 0) {
                        backspaceMWD++;
                    }
                    totalErrorRateArray.add(((INF + IF) / (C + INF + IF)) * 100);
                    totalErrorRateShortArray.add(((shortINF + shortIF) / (shortC + shortINF + shortIF)) * 100);
                    totalErrorRateBackspaceArray.add(((backspaceINF + backspaceIF) / (backspaceC + backspaceINF + backspaceIF)) * 100);
                    /*System.out.println(currentWord + "----------------------------------------------------------------");
                    System.out.print("takenPath: ");
                    for(Vector v: takenPath) {
                        System.out.print(virtualKeyboard.getNearestKeyNoEnter(v, 9999).getKey().getValue() + " ");
                    }
                    System.out.println();
                    System.out.print("takeShort: ");
                    for(Vector v: takenPathModifiedShortest) {
                        System.out.print(virtualKeyboard.getNearestKeyNoEnter(v, 9999).getKey().getValue() + " ");
                    }
                    System.out.println();
                    System.out.print("expecPath: ");
                    for(Vector v: expectedPath) {
                        System.out.print(virtualKeyboard.getNearestKeyNoEnter(v, 9999).getKey().getValue() + " ");
                    }
                    System.out.println();
                    System.out.print("expBckspc: ");
                    for(Vector v: expectedPathModifiedBackspace) {
                        System.out.print(virtualKeyboard.getNearestKeyNoEnter(v, 9999).getKey().getValue() + " ");
                    }
                    System.out.println();
                    System.out.println("------------------------------------------------------------------------------");*/
                    // TODO: DECIDE IF THIS CONVERSION NEEDS TO BE DONE
                    frechetDistanceArray.add(MyUtilities.MATH_UTILITILES.calculateFrechetDistance(
                            takenPath.toArray(new Vector[takenPath.size()]),
                            expectedPath.toArray(new Vector[expectedPath.size()]))
                            /* * (keyboard.getType() == KeyboardType.TABLET ? PIXEL_TO_CENTIMETER_TOUCH : PIXEL_TO_CENTIMETER_OTHERS)*/);
                    modShortFrechetDistanceArray.add(MyUtilities.MATH_UTILITILES.calculateFrechetDistance(
                            takenPathModifiedShortest.toArray(new Vector[takenPathModifiedShortest.size()]),
                            expectedPath.toArray(new Vector[expectedPath.size()]))
                            /* * (keyboard.getType() == KeyboardType.TABLET ? PIXEL_TO_CENTIMETER_TOUCH : PIXEL_TO_CENTIMETER_OTHERS)*/);
                    modBackspaceFrechetDistanceArray.add(MyUtilities.MATH_UTILITILES.calculateFrechetDistance(
                            takenPath.toArray(new Vector[takenPath.size()]),
                            expectedPathModifiedBackspace.toArray(new Vector[expectedPathModifiedBackspace.size()]))
                            /* * (keyboard.getType() == KeyboardType.TABLET ? PIXEL_TO_CENTIMETER_TOUCH : PIXEL_TO_CENTIMETER_OTHERS)*/);
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
                    previousKey = null;
                    reactionTimeFirstPressed = 0f; // convert to seconds
                    reactionTimeFirstTouch = 0f;
                    reactionTimeToError = 0f; // convert to seconds
                    responseToErrorsCount = 0;
                    handVelocity = 0f; // convert to m/s
                    pixelVelocity = 0f; // pixel/s
                    numberOfActionsCount = 0;
                    expectedPath.clear();
                    takenPath.clear();
                    takenPathModifiedShortest.clear();
                    expectedPathModifiedBackspace.clear();
                    C = 0f;
                    INF = 0f;
                    IF = 0f;
                    F = 0f;
                    shortC = 0;
                    shortINF = 0f;
                    shortIF = 0f;
                    shortF = 0f;
                    backspaceC = 0f;
                    backspaceINF = 0f;
                    backspaceIF = 0f;
                    //backspaceF = 0f;
                    detectedShortestComplete = false;
                    lastEnterAfterShortestComplete = false;
                    errorOccured = false;
                }
            }
        }
        
        MWD_Array.add((MWD / (float) wordList.size()) * 100);
        modShortMWD_Array.add((shortMWD / (float) wordList.size()) * 100);
        modBackspaceMWD_Array.add((backspaceMWD / (float) wordList.size()) * 100);
        
        // Reload the default settings for the Leap Keyboards
        if(keyboard.getType().isLeap()) {
            keyboard.loadDefaultSettings();
            LeapListener.removeObserver((LeapKeyboard) keyboard);
            LeapListener.stopListening();
        }
        
        // stuff arrays into subject Data
        subjectData.add(StatisticDataType.WORD_ORDER.name() + ": " + wordList.toString());
        subjectData.add(StatisticDataType.NUMBER_OF_TIMES_PLANE_BREACHED.name() + ": " + planeBreachedCountArray.toString());
        subjectData.add(StatisticDataType.DISTANCE_TRAVELED_TOUCH_ONLY.name() + ": " + distanceTraveledArray.toString());
        subjectData.add(StatisticDataType.DISTANCE_TRAVELED_TOUCH_ONLY_SHORTEST.name() + ": " + distanceTraveledShortArray.toString());
        subjectData.add(StatisticDataType.TIME_DURATION_TOUCH_ONLY.name() + ": " + timeDurationArray.toString());
        subjectData.add(StatisticDataType.TIME_DURATION_TOUCH_ONLY_SHORTEST.name() + ": " + timeDurationShortArray.toString());
        subjectData.add(StatisticDataType.AVERAGE_HAND_VELOCITY.name() + ": " + handVelocityAvgArray.toString());
        subjectData.add(StatisticDataType.AVERAGE_PIXEL_VELOCITY.name() + ": " + pixelVelocityAvgArray.toString());
        subjectData.add(StatisticDataType.REACTION_TIME_FIRST_PRESSED.name() + ": " + reactionTimeFirstPressedArray.toString());
        subjectData.add(StatisticDataType.REACTION_TIME_FIRST_TOUCH.name() + ": " + reactionTimeFirstTouchArray.toString());
        subjectData.add(StatisticDataType.AVERAGE_REACTION_TIME_TO_ERRORS.name() + ": " + reactionTimeToErrorAvgArray.toString());
        subjectData.add(StatisticDataType.TEXT_ENTRY_RATE_WPM.name() + ": " + WPM_Array.toString());
        subjectData.add(StatisticDataType.TEXT_ENTRY_RATE_MODIFIED_WPM_SHORTEST.name() + ": " + modShortWPM_Array.toString());
        subjectData.add(StatisticDataType.TEXT_ENTRY_RATE_MODIFIED_WPM_VULTURE.name() + ": " + modVultureWPM_Array.toString());
        subjectData.add(StatisticDataType.TEXT_ENTRY_RATE_MODIFIED_WPM_SHORTEST_VULTURE.name() + ": " + modShortVultureWPM_Array.toString());
        subjectData.add(StatisticDataType.ERROR_RATE_KSPC.name() + ": " + KSPC_Array.toString());
        subjectData.add(StatisticDataType.ERROR_RATE_MODIFIED_KSPC_SHORTEST.name() + ": " + modShortKSPC_Array.toString());
        //subjectData.add(StatisticDataType.ERROR_RATE_MODIFIED_KSPC_BACKSPACE.name() + ": " + modBackspaceKSPC_Array.toString());
        subjectData.add(StatisticDataType.ERROR_RATE_MODIFIED_MSD_SHORTEST.name() + ": " + modShortMSD_Array.toString());
        subjectData.add(StatisticDataType.ERROR_RATE_MODIFIED_MSD_BACKSPACE.name() + ": " + modBackspaceMSD_Array.toString());
        subjectData.add(StatisticDataType.ERROR_RATE_MWD.name() + ": " + MWD_Array.toString());
        subjectData.add(StatisticDataType.ERROR_RATE_MODIFIED_MWD_SHORTEST.name() + ": " + modShortMWD_Array.toString());
        subjectData.add(StatisticDataType.ERROR_RATE_MODIFIED_MWD_BACKSPACE.name() + ": " + modBackspaceMWD_Array.toString());
        subjectData.add(StatisticDataType.TOTAL_ERROR_RATE.name() + ": " + totalErrorRateArray.toString());
        subjectData.add(StatisticDataType.TOTAL_ERROR_RATE_MODIFIED_SHORTEST.name() + ": " + totalErrorRateShortArray.toString());
        subjectData.add(StatisticDataType.TOTAL_ERROR_RATE_MODIFIED_BACKSPACE.name() + ": " + totalErrorRateBackspaceArray.toString());
        subjectData.add(StatisticDataType.FRECHET_DISTANCE_TOUCH_ONLY.name() + ": " + frechetDistanceArray.toString());
        subjectData.add(StatisticDataType.FRECHET_DISTANCE_TOUCH_ONLY_MODIFIED_SHORTEST.name() + ": " + modShortFrechetDistanceArray.toString());
        subjectData.add(StatisticDataType.FRECHET_DISTANCE_TOUCH_ONLY_MODIFIED_BACKSPACE.name() + ": " + modBackspaceFrechetDistanceArray.toString());
        subjectData.add(StatisticDataType.TAKEN_PATH_TOUCH_ONLY.name() + ": " + takenPathArray.toString());
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
    
    private ArrayList<PlaybackFileData> parsePlaybackFileContents(ArrayList<String> fileContents) {
        ArrayList<PlaybackFileData> fileData = new ArrayList<PlaybackFileData>();
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
                fileData.add(new PlaybackFileData(eventTime, new ArrayList<Entry<RecordedDataType, Object>>(entries.entrySet())));
            }
        }
        return fileData;
    }
    
    private class PlaybackFileData {
        private long eventTime;
        private ArrayList<Entry<RecordedDataType, Object>> eventData;
        private int eventIndex = 0;
        
        PlaybackFileData(long eventTime, ArrayList<Entry<RecordedDataType, Object>> eventData) {
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
    
    private class DateComparator implements Comparator<File> {

        Map<File, Date> base;
        public DateComparator(Map<File, Date> base) {
            this.base = base;
        }
   
        // Note: this comparator imposes orderings that are inconsistent with equals.
        public int compare(File a, File b) {
            if (base.get(a).after(base.get(b))) {
                return 1;
            } else {
                return -1;
            } // returning 0 would merge keys
        }
    }
}

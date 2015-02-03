package utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.TreeMap;

import com.leapmotion.leap.Vector;

import enums.FileExt;
import enums.FileName;
import enums.FilePath;
import keyboard.IKeyboard;
import keyboard.KeyboardAttribute;
import keyboard.KeyboardAttributes;
import keyboard.KeyboardSetting;
import keyboard.KeyboardSettings;

public class FileUtilities {
    public static final String WILDCARD = "*";
    
    public FileUtilities() {
        File file;
        Path path;
        //Create data folder if it doesn't already exist.
        createDirectory(FilePath.DATA.getPath());
        
        //Create docs folder if it doesn't already exist.
        createDirectory(FilePath.DOCS.getPath());
        
        // Create config/settings path/files if they don't exist.
        // Create config folder.
        createDirectory(FilePath.CONFIG.getPath());

        // Create standard.ini
        path = FileSystems.getDefault().getPath(FilePath.CONFIG.getPath(), FileName.STANDARD.getName() + FileExt.INI.getExt());
        if(!Files.exists(path)) {
            file = path.toFile();
            try {
                file.createNewFile();
            } catch (IOException e) {e.printStackTrace();}
        }
        // Create leap_surface.ini
        path = FileSystems.getDefault().getPath(FilePath.CONFIG.getPath(), FileName.LEAP_SURFACE.getName() + FileExt.INI.getExt());
        if(!Files.exists(path)) {
            file = path.toFile();
            try {
                file.createNewFile();
            } catch (IOException e) {e.printStackTrace();}
        }
        // Create leap_air.ini
        path = FileSystems.getDefault().getPath(FilePath.CONFIG.getPath(), FileName.LEAP_AIR.getName() + FileExt.INI.getExt());
        if(!Files.exists(path)) {
            file = path.toFile();
            try {
                file.createNewFile();
            } catch (IOException e) {e.printStackTrace();}
        }
        // Create controller.ini
        path = FileSystems.getDefault().getPath(FilePath.CONFIG.getPath(), FileName.CONTROLLER.getName() + FileExt.INI.getExt());
        if(!Files.exists(path)) {
            file = path.toFile();
            try {
                file.createNewFile();
            } catch (IOException e) {e.printStackTrace();}
        }
        // Create tablet.ini
        path = FileSystems.getDefault().getPath(FilePath.CONFIG.getPath(), FileName.TABLET.getName() + FileExt.INI.getExt());
        if(!Files.exists(path)) {
            file = path.toFile();
            try {
                file.createNewFile();
            } catch (IOException e) {e.printStackTrace();}
        }
    }
    
    // Find and read the setting from file if it exists.
    private Object readValueFromFile(File file, String dataName, Object value) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        String line = null;
        while((line = bufferedReader.readLine()) != null) {
            String[] words = line.split(" ");
            String name = words[0].substring(0, words[0].length()-1); // remove colon
            if(dataName.equals(name)) {
                if(value instanceof Integer) {
                    value = Integer.valueOf(words[1]);
                } else if(value instanceof Double) {
                    value = Double.valueOf(words[1]);
                } else if(value instanceof Float) {
                    value = Float.valueOf(words[1]);
                } else if(value instanceof Vector) {
                    Vector tmpVector = new Vector();
                    tmpVector.setX(Float.valueOf(words[1].substring(1, words[1].length()-1)));
                    tmpVector.setY(Float.valueOf(words[2].substring(0, words[2].length()-1)));
                    tmpVector.setZ(Float.valueOf(words[3].substring(0, words[3].length()-1)));
                    value = tmpVector;
                } else if(value instanceof Point) {
                    Point tmpPoint = new Point(0, 0);
                    tmpPoint.setLocation(Integer.valueOf(words[1].substring(1, words[1].length()-1)), Integer.valueOf(words[2].substring(0, words[2].length()-1)));
                    value = tmpPoint;
                }
                System.out.println(dataName + ": " + value);
                break;
            }
        }
        // Close our reader.
        bufferedReader.close();
        return value;
    }
    
    // Read in all stored settings from file into an alphabetically sorted Map.
    private void parseAndStoreData(TreeMap<String, String> savedData, File file) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        String line = null;
        while((line = bufferedReader.readLine()) != null) {
            String[] words = line.split(" ");
            String name = words[0].substring(0, words[0].length()-1);
            String value = "";
            for(int i = 1; i < words.length; i++) {
                if(i < words.length-1) {
                    value += words[i] + " ";
                } else {
                    value += words[i];
                }
            }
            savedData.put(name, value);
        }
        bufferedReader.close();
    }
    
    // Read in contents directly into an array list.
    private void storeDataAsList(ArrayList<String> dataList, File file) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        String line = null;
        while((line = bufferedReader.readLine()) != null) {
            dataList.add(line);
        }
        bufferedReader.close();
    }
    
    // Write data to file.
    private void writeDataToFile(File file, TreeMap<String, String> savedData, boolean report) throws IOException {
        FileWriter fileWriter = new FileWriter(file);
        PrintWriter out = new PrintWriter(fileWriter);
        for(String name: savedData.keySet()) {
            String output = name + ": " + savedData.get(name);
            out.println(output);
            if(report) System.out.println(output);
        }
        out.flush();
        out.close();
        fileWriter.close();
    }
    
    // Write data to file.
    private void writeDataToFile(File file, ArrayList<String> savedData, boolean report) throws IOException {
        FileWriter fileWriter = new FileWriter(file);
        PrintWriter out = new PrintWriter(fileWriter);
        for(String data: savedData) {
            out.println(data);
            if(report) System.out.println(data);
        }
        out.flush();
        out.close();
        fileWriter.close();
    }
    
    // Attempt to open file, create it if it doesn't exist.
    private File createFile(String filePath, String fileName) throws IOException {
        File file = new File(filePath, fileName);
        if (!file.isFile() && !file.createNewFile()) {
            throw new IOException("Error creating new file: " + file.getAbsolutePath());
        }
        return file;
    }
    
    // Attempt to open wildcard file, do nothing if it doesn't exist
    private File openWildcardFile(String filePath, String fileName) throws IOException {
        // Attempt to open directory, create it if it doesn't exist
        File directory = createDirectory(filePath);
        for(File file: directory.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                String[] parts = fileName.split("\\" + WILDCARD);
                return name.contains(parts[0]) && name.endsWith(FileExt.DAT.getExt());
            }
        })) {
            // Currently we return the first match, but later I might need to get the most recent or specific selection.
            return file;
        }
        return null;
    }
    
    // Attempt to open directory, create it if it doesn't exist
    private File createDirectory(String filePath) {
        //Create data folder if it doesn't already exist.
        Path path = FileSystems.getDefault().getPath(filePath);
        if(!Files.exists(path)) {
            File file = path.toFile();
            file.mkdirs();
            return file;
        } else {
            return path.toFile();
        }
    }
    
    // Check if directory exists. Don't worry about creation.
    public boolean checkDirectoryExists(String filePath) {
        //Create data folder if it doesn't already exist.
        Path path = FileSystems.getDefault().getPath(filePath);
        return Files.exists(path);
    }
    
    // SETTINGS READ AND WRITE
    public double readSettingFromFile(String filePath, String fileName, String settingName, double defaultValue) throws IOException {
        // Store default value so we know what the original is.
        Double value = defaultValue;
        
        // Attempt to open file, create it if it doesn't exist.
        File file = createFile(filePath, fileName);
        
        // Find and read the setting from file if it exists.
        if((value = (Double) readValueFromFile(file, settingName, value)) != null) {
            return value;
        } else {
            return defaultValue;
        }
    }
    
    public void readSettingsFromFile(String filePath, String fileName, KeyboardSettings keyboardSettings) throws IOException {
        // Attempt to open file, create it if it doesn't exist.
        File file = createFile(filePath, fileName);
        
        // Go through each setting and attempt to read it from file.
        for(KeyboardSetting keyboardSetting: keyboardSettings.getAllSettings()) {
            // Store default value so we know what the original is.
            Double value = keyboardSetting.getValue();
            if((value = (Double) readValueFromFile(file, keyboardSetting.getType().name(), value)) != null) {
                keyboardSetting.setValue(value);
            }
        }
    }
    
    public void writeSettingToFile(String filePath, String fileName, KeyboardSetting keyboardSetting) throws IOException {
        // Attempt to open file, create it if it doesn't exist.
        File file = createFile(filePath, fileName);
        
        // Read in all stored settings from file into an alphabetically sorted Map.
        TreeMap<String, String> savedData = new TreeMap<String, String>();
        parseAndStoreData(savedData, file);
        
        // Change the setting specified.
        savedData.put(keyboardSetting.getType().name(), Double.toString(keyboardSetting.getValue()));
        
        // Rewrite settings to file.
        writeDataToFile(file, savedData, false);
        System.out.println(keyboardSetting.getType().name() + ": " +  Double.toString(keyboardSetting.getValue()));
    }
    
    public void writeSettingsToFile(String filePath, String fileName, KeyboardSettings keyboardSettings) throws IOException {
        // Attempt to open file, create it if it doesn't exist.
        File file = createFile(filePath, fileName);
        
        // Read in all stored settings from file into an alphabetically sorted Map.
        TreeMap<String, String> savedData = new TreeMap<String, String>();
        parseAndStoreData(savedData, file);
        
        // Change all of the setting's values.
        for(KeyboardSetting keyboardSetting: keyboardSettings.getAllSettings()) {
            savedData.put(keyboardSetting.getType().name(), Double.toString(keyboardSetting.getValue()));
        }
        
        // Rewrite settings to file.
        writeDataToFile(file, savedData, true);
    }
    
    // ATTRIBUTES READ AND WRITER
    public Object readAttributeFromFile(String filePath, String fileName, String attributeName, Object defaultValue) throws IOException {
        // Store default value so we know what class the original is.
        Object value = defaultValue;
        
        // Attempt to open file, create it if it doesn't exist.
        File file = createFile(filePath, fileName);
        
        // Find and read the setting from file if it exists.
        if((value = readValueFromFile(file, attributeName, value)) != null) {
            return value;
        } else {
            return defaultValue;
        }
    }
    
    public void readAttributesFromFile(String filePath, String fileName, KeyboardAttributes keyboardAttributes) throws IOException {
        // Attempt to open file, create it if it doesn't exist.
        File file = createFile(filePath, fileName);
        
        // Go through each attribute and attempt to read it from file.
        for(KeyboardAttribute keyboardAttribute: keyboardAttributes.getAllAttributes()) {
            // Store default value so we know what the original is.
            Object value = keyboardAttribute.getValue();
            if((value = readValueFromFile(file, keyboardAttribute.getType().name(), value)) != null) {
                keyboardAttribute.setValue(value);
            }
        }
    }
    
    public void writeAttributeToFile(String filePath, String fileName, KeyboardAttribute keyboardAttribute) throws IOException {
        if(keyboardAttribute.isWriteable()) {
            // Attempt to open file, create it if it doesn't exist.
            File file = createFile(filePath, fileName);
            
            // Read in all stored attributes from file into an alphabetically sorted Map.
            TreeMap<String, String> savedData = new TreeMap<String, String>();
            parseAndStoreData(savedData, file);
            
            // Change the attribute specified.
            savedData.put(keyboardAttribute.getType().name(), keyboardAttribute.getValue().toString());
            
            // Rewrite attributes to file.
            writeDataToFile(file, savedData, false);
            System.out.println(keyboardAttribute.getType().name() + ": " +  keyboardAttribute.getValue());
        }
    }
    
    public void writeAttributesToFile(String filePath, String fileName, KeyboardAttributes keyboardAttributes) throws IOException {
            // Attempt to open file, create it if it doesn't exist.
            File file = createFile(filePath, fileName);
            
            // Read in all stored attributes from file into an alphabetically sorted Map.
            TreeMap<String, String> savedData = new TreeMap<String, String>();
            parseAndStoreData(savedData, file);
            
            // Change all of the attributes values.
            for(KeyboardAttribute keyboardAttribute: keyboardAttributes.getAllAttributes()) {
                if(keyboardAttribute.isWriteable()) {
                    savedData.put(keyboardAttribute.getType().name(), keyboardAttribute.getValue().toString());
                }
            }
            
            // Rewrite attributes to file.
            writeDataToFile(file, savedData, true);
    }
    
    // BOTH ATTRIBUTES AND SETTINGS
    public void readSettingsAndAttributesFromFile(String filePath, String fileName, IKeyboard keyboard) throws IOException {
        // Attempt to open file, create it if it doesn't exist.
        File file = createFile(filePath, fileName);
        
        // Go through each attribute and attempt to read it from file.
        for(KeyboardAttribute keyboardAttribute: keyboard.getAttributes().getAllAttributes()) {
            // Store default value so we know what the original is.
            Object value = keyboardAttribute.getValue();
            if((value = readValueFromFile(file, keyboardAttribute.getType().name(), value)) != null) {
                keyboardAttribute.setValue(value);
            }
        }
        
        // Go through each setting and attempt to read it from file.
        for(KeyboardSetting keyboardSetting: keyboard.getSettings().getAllSettings()) {
            // Store default value so we know what the original is.
            Double value = keyboardSetting.getValue();
            if((value = (Double) readValueFromFile(file, keyboardSetting.getType().name(), value)) != null) {
                keyboardSetting.setValue(value);
            }
        }
    }
    
    public void writeSettingsAndAttributesToFile(String filePath, String fileName, IKeyboard keyboard) throws IOException {
        // Attempt to open file, create it if it doesn't exist.
        File file = createFile(filePath, fileName);
        
        // Read in all stored data from file into an alphabetically sorted Map.
        TreeMap<String, String> savedData = new TreeMap<String, String>();
        parseAndStoreData(savedData, file);
        
        // Change all of the data's values.
        for(KeyboardAttribute keyboardAttribute: keyboard.getAttributes().getAllAttributes()) {
            if(keyboardAttribute.isWriteable()) {
                savedData.put(keyboardAttribute.getType().name(), keyboardAttribute.getValue().toString());
            }
        }
        for(KeyboardSetting keyboardSetting: keyboard.getSettings().getAllSettings()) {
            savedData.put(keyboardSetting.getType().name(), Double.toString(keyboardSetting.getValue()));
        }
        
        // Rewrite data to file.
        writeDataToFile(file, savedData, true);
    }

    // Experiment file utilities
    // Read contents directly from file into array in memory.
    public ArrayList<String> readListFromFile(String filePath, String fileName) throws IOException {
        // Attempt to open file, create it if it doesn't exist.
        File file = createFile(filePath, fileName);
        
        // Read in all stored data from file into an array.
        ArrayList<String> dataList = new ArrayList<String>();
        storeDataAsList(dataList, file);
        
        return dataList;
    }
    
    public ArrayList<String> readListFromWildcardFile(String filePath, String wildcardFileName) throws IOException {
        // Attempt to open the wildcard file. Create directory if it doesn't exist
        File file = openWildcardFile(filePath, wildcardFileName);
        
        // Read in all stored data from file into an array.
        ArrayList<String> dataList = new ArrayList<String>();
        storeDataAsList(dataList, file);
        
        return dataList;
    }
    
    public boolean checkWildcardFileExists(String filePath, String wildcardFileName) throws IOException {
        // Check if base directory exists.
        if(checkDirectoryExists(filePath)) {
            // Attempt to open a file that matches.
            File file = openWildcardFile(filePath, wildcardFileName);
            if(file != null) {
                // We found a file that matches wildcard name.
                return true;
            }
        }
        return false;
    }
    
    public ArrayList<String> getListOfDirectories(String filePath) throws IOException {
        // Attempt to open directory, create it if it doesn't exist.
        File directory = createDirectory(filePath);
        
        // Filter directory to retrieve all sub-directories.
        String[] subDirectories = directory.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
              return new File(dir, name).isDirectory();
            }
          });
        
        // Return the list of directories as names.
        return new ArrayList<String>(Arrays.asList(subDirectories));
    }

    public ArrayList<String> reservoirSampling(int reservoirSize, int dictionarySize, String filePath, String fileName) throws IOException {
        // Attempt to open file, create it if it doesn't exist.
        File file = createFile(filePath, fileName);
        
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        ArrayList<String> reservoir = new ArrayList<String>();
        String line = null;
        // Read in the first 'size' elements.
        for(int i = 0; i < reservoirSize && (line = bufferedReader.readLine()) != null; i++) {
            reservoir.add(line);
        }
        Random random = new Random();
        // Replace elements with gradually decreasing probability.
        for(int i = reservoirSize; i < dictionarySize && (line = bufferedReader.readLine()) != null; i++) {
            int j = random.nextInt(i + 1);
            if(j < reservoirSize) {
                reservoir.set(j, line);
            }
        }
        bufferedReader.close();
        return reservoir;
    }

    public void writeListToFile(ArrayList<String> savedData, String filePath, String fileName, boolean report) throws IOException {
        // Attempt to open directory, create it if it doesn't exist.
        createDirectory(filePath);
        
        // Attempt to open file, create it if it doesn't exist.
        File file = createFile(filePath, fileName);
        
        // Write our data to file.
        writeDataToFile(file, savedData, report);
    }
}

package utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.TreeMap;

import enums.FileExtension;
import enums.FileName;
import enums.FilePath;
import keyboard.KeyboardSetting;
import keyboard.KeyboardSettings;

public class FileUtilities {
    
    public FileUtilities() {
        File file;
        Path path;
        //Create data folder if it doesn't already exist.
        path = FileSystems.getDefault().getPath(FilePath.DATA_PATH.getPath());
        if(!Files.exists(path)) {
            file = path.toFile();
            file.mkdirs();
        }
        
        // Create config/settings path/files if they don't exist.
        // Create config folder.
        path = FileSystems.getDefault().getPath(FilePath.CONFIG_PATH.getPath());
        if(!Files.exists(path)) {
            file = path.toFile();
            file.mkdirs();
        }
        // Create standard.ini
        path = FileSystems.getDefault().getPath(FilePath.CONFIG_PATH.getPath(), FileName.STANDARD_NAME.getName() + FileExtension.INI.getExtension());
        if(!Files.exists(path)) {
            file = path.toFile();
            try {
                file.createNewFile();
            } catch (IOException e) {e.printStackTrace();}
        }
        // Create leap.ini
        path = FileSystems.getDefault().getPath(FilePath.CONFIG_PATH.getPath(), FileName.LEAP_NAME.getName() + FileExtension.INI.getExtension());
        if(!Files.exists(path)) {
            file = path.toFile();
            try {
                file.createNewFile();
            } catch (IOException e) {e.printStackTrace();}
        }
        // Create controller.ini
        path = FileSystems.getDefault().getPath(FilePath.CONFIG_PATH.getPath(), FileName.CONTROLLER_NAME.getName() + FileExtension.INI.getExtension());
        if(!Files.exists(path)) {
            file = path.toFile();
            try {
                file.createNewFile();
            } catch (IOException e) {e.printStackTrace();}
        }
        // Create tablet.ini
        path = FileSystems.getDefault().getPath(FilePath.CONFIG_PATH.getPath(), FileName.TABLET_NAME.getName() + FileExtension.INI.getExtension());
        if(!Files.exists(path)) {
            file = path.toFile();
            try {
                file.createNewFile();
            } catch (IOException e) {e.printStackTrace();}
        }
    }
    
    public Number readAttributeOrSettingFromFile(String filePath, String fileName, String attributeOrSettingName, double defaultValue) throws IOException {
        // Store default value just in cause we find no other value.
        double value = defaultValue;
        
        // Attempt to open file, create it if it doesn't exist.
        File file = new File(filePath, fileName);
        if (!file.isFile() && !file.createNewFile())
        {
            throw new IOException("Error creating new file: " + file.getAbsolutePath());
        }
        
        // Find and read the setting from file if it exists.
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        String line = null;
        while((line = bufferedReader.readLine()) != null) {
            String[] words = line.split(" ");
            if(words.length == 2) {
                if(attributeOrSettingName.equals(words[0].substring(0, words[0].length()-1))) {
                    value = Double.parseDouble(words[1]);
                    break;
                }
            }
        }
        
        // Close our reader and return the value.
        bufferedReader.close();
        return value;
    }
    
    public void writeSettingToFile(String filePath, String fileName, KeyboardSetting keyboardSetting) throws IOException {
        // Attempt to open file, create it if it doesn't exist.
        File file = new File(filePath, fileName);
        if (!file.isFile() && !file.createNewFile())
        {
            throw new IOException("Error creating new file: " + file.getAbsolutePath());
        }
        
        // Read in all stored settings from file into an alphabetically sorted Map.
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        TreeMap<String, Double> savedSettings = new TreeMap<String, Double>();
        String line = null;
        while((line = bufferedReader.readLine()) != null) {
            String[] words = line.split(" ");
            if(words.length == 2) {
                savedSettings.put(words[0].substring(0, words[0].length()-1), Double.parseDouble(words[1]));
            }
        }
        bufferedReader.close();
        
        // Change the setting specified.
        savedSettings.put(keyboardSetting.getName(), keyboardSetting.getValue());
        
        // Rewrite settings to file.
        FileWriter fileWriter = new FileWriter(filePath+fileName);
        PrintWriter out = new PrintWriter(fileWriter);
        for(String settingName: savedSettings.keySet()) {
            out.println(settingName + ": " + savedSettings.get(settingName));
        }
        out.flush();
        out.close();
        fileWriter.close();
    }
    
    public void writeSettingsToFile(String filePath, String fileName, KeyboardSettings keyboardSettings) throws IOException {
        // Attempt to open file, create it if it doesn't exist.
        File file = new File(filePath, fileName);
        if (!file.isFile() && !file.createNewFile())
        {
            throw new IOException("Error creating new file: " + file.getAbsolutePath());
        }
        
        // Read in all stored settings from file into an alphabetically sorted Map.
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        TreeMap<String, Double> savedSettings = new TreeMap<String, Double>();
        String line = null;
        while((line = bufferedReader.readLine()) != null) {
            String[] words = line.split(" ");
            if(words.length == 2) {
                savedSettings.put(words[0].substring(0, words[0].length()-1), Double.parseDouble(words[1]));
            }
        }
        bufferedReader.close();
        
        // Change all of the setting's values.
        for(KeyboardSetting keyboardSetting: keyboardSettings.getAllSettings()) {
            savedSettings.put(keyboardSetting.getName(), keyboardSetting.getValue());
        }
        
        // Rewrite settings to file.
        FileWriter fileWriter = new FileWriter(filePath+fileName);
        PrintWriter out = new PrintWriter(fileWriter);
        for(String settingName: savedSettings.keySet()) {
            out.println(settingName + ": " + savedSettings.get(settingName));
        }
        out.flush();
        out.close();
        fileWriter.close();
    }
}

package utilities;

import java.awt.Font;
import java.util.ArrayList;

import javax.swing.JComponent;

import keyboard.KeyboardAttribute;
import keyboard.KeyboardSetting;

public class MyUtilities {
    public static void calculateFontSize (String labelText, JComponent component, JComponent container) {
        Font labelFont = component.getFont();

        int stringWidth = component.getFontMetrics(labelFont).stringWidth(labelText);
        int componentWidth = container.getWidth();

        // Find out how much the font can grow in width.
        double widthRatio = (double)componentWidth / (double)stringWidth;

        int newFontSize = (int)(labelFont.getSize() * widthRatio);
        int componentHeight = container.getHeight();

        // Pick a new font size so it will not be larger than the height of label.
        int fontSizeToUse = Math.min(newFontSize, componentHeight);

        // Set the label's font size to the newly determined size.
        component.setFont(new Font(labelFont.getName(), Font.PLAIN, fontSizeToUse));
    }
    
    
    // ALL OF THE BELOW must check to see if variable exists in file yet
    // if not, insert the variable in alphabetical order into the file and put the new value
    // if the variable already exists then we just update the value there
    public static KeyboardAttribute readAttributeFromFile(String path, String name) {
        return null;
    }
    
    public static void writeAttributeToFile(String path, KeyboardAttribute attribute) {
        
    }
    
    public static void writeAttributeListToFile(String path, ArrayList<KeyboardAttribute> attributes) {
        
    }
    
    public static KeyboardSetting readSettingFromFile(String path, String name) {
        return null;
    }
    
    public static void writeSettingToFile(String path, KeyboardSetting setting) {
        
    }
    
    public static void writeSettingListToFile(String path, ArrayList<KeyboardSetting> settings) {
        
    }
    
    // open's directory specified by path or creates the folder if it doesn't exist yet
    private static void openDirectory() {
        
    }
    
    // open's file specified by path or creates a new file if it doesn't exist yet
    private static void openFile() {
        
    }
    
    // Add data recording function or make a separate DataRecorder class to take care of it.
}

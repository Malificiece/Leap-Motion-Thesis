package utilities;

import java.awt.Container;
import java.awt.Font;

import javax.swing.JComponent;

public class SwingUtilities {
    private static final int OFFSET = 38;
    
    public void calculateFontSize(String text, JComponent component, Container container) {
        Font labelFont = component.getFont();
        
        // Find out how the sizes of the old font.
        int newStringWidth = component.getFontMetrics(labelFont).stringWidth(text);
        int componentWidth = container.getWidth() - OFFSET;
        
        // Find out how much the font can grow in width.
        double widthRatio = (double) componentWidth / (double) newStringWidth;

        // Different font sizes to consider
        int newFontSize = (int) ((labelFont.getSize() * widthRatio));
        int componentHeight = container.getHeight() - OFFSET;
        
        if(newFontSize == 0) {
            newFontSize = 1;
        }

        // Pick a new font size so it will not be larger than the height of label.
        int fontSizeToUse = Math.min(newFontSize, componentHeight);

        // Set the label's font size to the newly determined size.
        component.setFont(new Font(labelFont.getName(), Font.PLAIN, fontSizeToUse));
    }
    
    public void calculateFontSize(String oldText, String newText, JComponent component, Container container) {
        Font labelFont = component.getFont();
        
        // Find out how the sizes of the old font.
        int oldStringWidth = component.getFontMetrics(labelFont).stringWidth(oldText);
        int newStringWidth = component.getFontMetrics(labelFont).stringWidth(newText);
        int componentWidth = container.getWidth() - OFFSET;
        
        // Find out how much the font can grow in width.
        double newWidthRatio = (double) componentWidth / (double) newStringWidth;

        // Different font sizes to consider
        int oldFontSize = labelFont.getSize();
        int newFontSize = (int) (labelFont.getSize() * newWidthRatio);
        int componentHeight = container.getHeight() - OFFSET;
        
        if(newFontSize == 0) {
            newFontSize = 1;
        }

        // Pick a new font size so it will not be larger than the height of label.
        int chosenFontSize;
        if(oldStringWidth < newStringWidth) {
            chosenFontSize = Math.min(oldFontSize, newFontSize);
        } else {
            if(componentWidth > newStringWidth) {
                chosenFontSize = newFontSize;
            } else {
                chosenFontSize = oldFontSize;
            }
        }
        int fontSizeToUse = Math.min(chosenFontSize, componentHeight);

        // Set the label's font size to the newly determined size.
        component.setFont(new Font(labelFont.getName(), Font.PLAIN, fontSizeToUse));
    }
    
    public String parseHTML(String htmlString) {
        return htmlString.replaceAll("\\<.*?>|\\s+", "");
    }
    
    public boolean equalsIgnoreHTML(String htmlString0, String htmlString1) {
        return parseHTML(htmlString0).equals(parseHTML(htmlString1));
    }
}

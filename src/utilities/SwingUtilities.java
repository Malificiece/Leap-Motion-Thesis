package utilities;

import java.awt.Font;

import javax.swing.JComponent;

public class SwingUtilities {
    public void calculateFontSize (String labelText, JComponent component, JComponent container) {
        Font labelFont = component.getFont();

        int stringWidth = component.getFontMetrics(labelFont).stringWidth(labelText);
        int componentWidth = container.getWidth();

        // Find out how much the font can grow in width.
        double widthRatio = (double)componentWidth / (double)stringWidth;

        int newFontSize = (int) (labelFont.getSize() * widthRatio);
        int componentHeight = container.getHeight();
        
        if(newFontSize == 0) {
            newFontSize = 1;
        }

        // Pick a new font size so it will not be larger than the height of label.
        int fontSizeToUse = Math.min(newFontSize, componentHeight);

        // Set the label's font size to the newly determined size.
        component.setFont(new Font(labelFont.getName(), Font.PLAIN, fontSizeToUse));
    }
    
    private String parseHTML(String htmlString) {
        return htmlString.replaceAll("\\<.*?>|\\s+", "");
    }
    
    public boolean equalsIgnoreHTML(String htmlString0, String htmlString1) {
        return parseHTML(htmlString0).equals(parseHTML(htmlString1));
    }
}

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

package utilities;

import java.awt.Container;
import java.awt.Font;
import java.awt.Image;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class SwingUtilities {
    private final int OFFSET = 38;

    public void calculateFontSize(String text, JComponent component, Container container) {
        Font font = component.getFont();
        
        // Find out how the sizes of the old font.
        int stringWidth = component.getFontMetrics(font).stringWidth(text);
        int componentWidth = container.getWidth() - OFFSET;
        
        if(stringWidth > 0) {
            // Find out how much the font can grow in width.
            double widthRatio = (double) componentWidth / (double) stringWidth;
    
            // Different font sizes to consider
            int newFontSize = (int) ((font.getSize() * widthRatio));
            Font tmpFont = new Font(font.getName(), Font.PLAIN, newFontSize);
            stringWidth = component.getFontMetrics(tmpFont).stringWidth(text);
            if(stringWidth < componentWidth) {
                do {
                    tmpFont = new Font(font.getName(), Font.PLAIN, ++newFontSize);
                    stringWidth = component.getFontMetrics(tmpFont).stringWidth(text);
                } while(stringWidth < componentWidth);
                if(stringWidth > componentWidth) {
                    newFontSize--;
                }
            } else if(stringWidth > componentWidth) {
                do {
                    tmpFont = new Font(font.getName(), Font.PLAIN, --newFontSize);
                    stringWidth = component.getFontMetrics(tmpFont).stringWidth(text);
                } while(stringWidth > componentWidth);
            }
            
            int componentHeight = container.getHeight() - OFFSET;
            
            if(newFontSize == 0) {
                newFontSize = 1;
            }
    
            // Pick a new font size so it will not be larger than the height of label.
            int fontSizeToUse = Math.min(newFontSize, componentHeight);
    
            // Set the label's font size to the newly determined size.
            component.setFont(new Font(font.getName(), Font.PLAIN, fontSizeToUse));
        }
    }
    
    public String parseHTML(String htmlString) {
        return htmlString.replaceAll("\\<.*?>|\\s+", "");
    }
    
    public boolean equalsIgnoreHTML(String htmlString0, String htmlString1) {
        return parseHTML(htmlString0).equals(parseHTML(htmlString1));
    }
    
    public JPanel createPadding(int size, int direction) {
        assert(direction == SwingConstants.HORIZONTAL || direction == SwingConstants.VERTICAL);
        JPanel padding = new JPanel();
        if(direction == SwingConstants.HORIZONTAL) {
            padding.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, size));
        } else if(direction == SwingConstants.VERTICAL) {
            padding.setBorder(BorderFactory.createEmptyBorder(0, 0, size, 0));
        }
        padding.setOpaque(false);
        return padding;
    }
    
    public ImageIcon resizeImageIcon(ImageIcon icon, int width, int height) {
        Image resizedImage = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);  
        return new ImageIcon(resizedImage);
    }
}

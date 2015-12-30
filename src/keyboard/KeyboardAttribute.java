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

package keyboard;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import utilities.Point;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.leapmotion.leap.Vector;

import enums.Attribute;

public class KeyboardAttribute {
    private Attribute attribute;
    private Object value;
    private JPanel attributesPanel;
    private JLabel valueLabel;
    
    public KeyboardAttribute(IKeyboard keyboard, Attribute attribute, Object defaultValue) {
        this.attribute = attribute;
        /*try {
            this.value = MyUtilities.FILE_IO_UTILITIES.readAttributeFromFile(FilePath.CONFIG.getPath(),
                    keyboard.getFileName() + FileExt.INI.getExt(), attribute.name(), defaultValue);
        } catch(IOException e) {
            System.err.println("Error occured while trying to read "  + attribute + " from file. Using default value.");
            this.value = defaultValue;
        }*/
        value = defaultValue;
        createAttributePanel();
    }

    public Attribute getType() {
        return attribute;
    }
    
    
    public Object getValue() {
        return value;
    }
    
    public Integer getValueAsInteger() {
        if(value instanceof Integer) {
            return (Integer) value;
        }
        return null;
    }
    
    public Float getValueAsFloat() {
        if(value instanceof Float) {
            return (Float) value;
        }
        return null;
    }
    
    public Vector getValueAsVector() {
        if(value instanceof Vector) {
            return (Vector) value;
        }
        return null;
    }
    
    public boolean isWriteable() {
        return (value instanceof Integer ||
                value instanceof Float ||
                value instanceof Vector ||
                value instanceof Point);
    }
    
    public Point getValueAsPoint() {
        if(value instanceof Point) {
            return (Point) value;
        }
        return null;
    }
    
    public void setValue(Object value) {
        if(value != null) {
            this.value = value;
            valueLabel.setText(value.toString());
        }
    }
    
    public JPanel getAttributePanel() {
        return attributesPanel;
    }
    
    private void createAttributePanel() {
        attributesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel nameLabel = new JLabel(attribute.name() + ": ");
        if(value != null) {
            valueLabel = new JLabel(value.toString());
        } else {
            valueLabel = new JLabel("NaN");
        }
        attributesPanel.add(nameLabel);
        attributesPanel.add(valueLabel);
        attributesPanel.setMaximumSize(new Dimension(attributesPanel.getMaximumSize().width, attributesPanel.getMinimumSize().height));
        attributesPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
    }
}

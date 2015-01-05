package keyboard;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.IOException;

import javax.swing.JLabel;
import javax.swing.JPanel;

import utilities.MyUtilities;

import com.leapmotion.leap.Vector;

import enums.Attribute;
import enums.FileExt;
import enums.FilePath;

public class KeyboardAttribute {
    private Attribute attribute;
    private Object value;
    private JPanel attributesPanel;
    private JLabel valueLabel;
    
    public KeyboardAttribute(IKeyboard keyboard, Attribute attribute, Object defaultValue) {
        this.attribute = attribute;
        try {
            this.value = MyUtilities.FILE_IO_UTILITIES.readAttributeFromFile(FilePath.CONFIG.getPath(),
                    keyboard.getFileName() + FileExt.INI.getExt(), attribute.name(), defaultValue);
        } catch(IOException e) {
            System.err.println("Error occured while trying to read "  + attribute + " from file. Using default value.");
            this.value = defaultValue;
        }
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
    
    public Vector getValueAsVector() {
        if(value instanceof Vector) {
            return (Vector) value;
        }
        return null;
    }
    
    public void setVectorValue(Vector value) {
        if(value != null) {
            this.value = value;
            valueLabel.setText(this.value.toString());
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

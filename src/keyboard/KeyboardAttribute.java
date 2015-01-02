package keyboard;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.IOException;

import javax.swing.JLabel;
import javax.swing.JPanel;

import utilities.MyUtilities;

import com.leapmotion.leap.Vector;

import enums.FileExtension;
import enums.FilePath;

public class KeyboardAttribute {
    private String name;
    private Object value;
    private JPanel attributesPanel;
    private JLabel valueLabel;
    
    public KeyboardAttribute(IKeyboard keyboard, String attributeName, Object defaultValue) {
        this.name = attributeName;
        try {
            this.value = MyUtilities.FILE_IO_UTILITIES.readAttributeFromFile(FilePath.CONFIG_PATH.getPath(),
                    keyboard.getKeyboardFileName() + FileExtension.INI.getExtension(), attributeName, defaultValue);
        } catch(IOException e) {
            System.err.println("Error occured while trying to read "  + attributeName + " from file. Using default value.");
            this.value = defaultValue;
        }
        createAttributePanel();
    }

    public String getName() {
        return name;
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
        JLabel nameLabel = new JLabel(name+": ");
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

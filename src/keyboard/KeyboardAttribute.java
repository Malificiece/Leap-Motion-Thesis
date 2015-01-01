package keyboard;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.leapmotion.leap.Vector;

public class KeyboardAttribute {
    private String name;
    private Object value;
    private JPanel attributesPanel;
    private JLabel valueLabel;
    
    public KeyboardAttribute(String attributeName, Object defaultValue) {
        this.name = attributeName;
        this.value = defaultValue;
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

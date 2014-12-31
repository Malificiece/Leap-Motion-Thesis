package keyboard;

import java.awt.Component;
import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class KeyboardAttribute {
    private String name;
    private Object value;
    private JPanel attributesPanel;
    
    public KeyboardAttribute(String attributeName, Object defaultValue) {
        this.name = attributeName;
        this.value = defaultValue;
        createAttributePanel();
    }

    public String getName() {
        return name;
    }
    
    public int getValueAsInteger() {
        return (int) value;
    }
    
    public Object getValue() {
        return value;
    }
    
    public JPanel getAttributePanel() {
        return attributesPanel;
    }
    
    private void createAttributePanel() {
        attributesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel nameLabel = new JLabel(name+": ");
        JLabel valueLabel = new JLabel(value.toString());
        attributesPanel.add(nameLabel);
        attributesPanel.add(valueLabel);
        attributesPanel.setMaximumSize(attributesPanel.getPreferredSize());
        attributesPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
    }
}

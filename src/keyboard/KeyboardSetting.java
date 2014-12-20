package keyboard;

import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.media.opengl.GL2;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

public class KeyboardSetting {
    private String name;
    private Number value;
    private Number min;
    private Number max;
    private JPanel settingsPanel;
    
    public KeyboardSetting(String name, Number value, Number min, Number max) {
        this.name = name;
        createSliderPanel();
    }

    public String getName() {
        return name;
    }
    
    public JPanel getSettingsPanel() {
        return settingsPanel;
    }
    
    private void createSliderPanel() {
        settingsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JCheckBox checkBox = new JCheckBox(name);
        checkBox.setSelected(true);
        
        checkBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == ItemEvent.SELECTED) {

                } else {

                }
            }
        });
        
        settingsPanel.add(checkBox);
    }
}

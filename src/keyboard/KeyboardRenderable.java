package keyboard;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.media.opengl.GL2;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

public abstract class KeyboardRenderable {
    private String name;
    private Boolean enabled = true;
    private JPanel renderablePanel;
    
    public KeyboardRenderable(String name) {
        this.name = name;
        createCheckboxPanel();
    }
    
    public void enable() {
        enabled = true;
    }
    
    public void disable() {
        enabled = false;
    }
    
    public Boolean isEnabled() {
        return enabled;
    }
    
    public String getName() {
        return name;
    }
    
    public JPanel getRenderablePanel() {
        return renderablePanel;
    }
    
    public abstract void render(GL2 gl);
    
    private void createCheckboxPanel() {
        renderablePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JCheckBox renderableCheckBox = new JCheckBox(name);
        renderableCheckBox.setSelected(true);
        
        renderableCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == ItemEvent.SELECTED) {
                    enable();
                } else {
                    disable();
                }
                renderablePanel.getRootPane().requestFocusInWindow();
            }
        });
        
        renderablePanel.add(renderableCheckBox);
        renderablePanel.setMaximumSize(renderablePanel.getPreferredSize());
        renderablePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
    }
}

package keyboard;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.media.opengl.GL2;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JRootPane;

public abstract class KeyboardRenderable {
    private String name;
    private Boolean enabled = true;
    private JPanel renderablePanel;
    private JCheckBox renderableCheckBox;
    
    public KeyboardRenderable(String name) {
        this.name = name;
        createCheckboxPanel();
    }
    
    private void enable() {
        enabled = true;
    }
    
    private void disable() {
        enabled = false;
    }
    
    public void blockAccess(boolean disable) {
        if(disable) renderableCheckBox.setSelected(false);
        renderableCheckBox.setEnabled(false);
    }
    
    public void grantAccess(boolean enable) {
        renderableCheckBox.setEnabled(true);
        if(enable) renderableCheckBox.setSelected(true);
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
        renderableCheckBox = new JCheckBox(name);
        renderableCheckBox.setSelected(true);
        renderableCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == ItemEvent.SELECTED) {
                    enable();
                } else {
                    disable();
                }
                JRootPane rootPane = renderablePanel.getRootPane();
                if(rootPane != null) {
                    rootPane.requestFocusInWindow();
                }
            }
        });
        
        renderablePanel.add(renderableCheckBox);
        //renderablePanel.setMaximumSize(renderablePanel.getPreferredSize());
        renderablePanel.setMaximumSize(new Dimension(renderablePanel.getMaximumSize().width, renderablePanel.getMinimumSize().height));
        renderablePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
    }
}

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
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.media.opengl.GL2;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JRootPane;

import enums.Renderable;

public abstract class KeyboardRenderable {
    private Renderable renderable;
    private Boolean enabled = true;
    private JPanel renderablePanel;
    private JCheckBox renderableCheckBox;
    
    public KeyboardRenderable(Renderable renderable) {
        this.renderable = renderable;
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
    
    public Renderable getType() {
        return renderable;
    }
    
    public JPanel getRenderablePanel() {
        return renderablePanel;
    }
    
    public abstract void render(GL2 gl);
    
    private void createCheckboxPanel() {
        renderablePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        renderableCheckBox = new JCheckBox(renderable.name());
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

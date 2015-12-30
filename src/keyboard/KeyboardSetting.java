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
import java.text.DecimalFormat;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import enums.DecimalPrecision;
import enums.Setting;

public class KeyboardSetting {
    private final ReentrantLock VALUE_LOCK = new ReentrantLock();
    private final Setting SETTING;
    private double value;
    private JPanel settingsPanel;
    private DecimalPrecision decimalPrecision;
    private JSpinner settingsSpinner;
    private JSlider settingsSlider;
    //private boolean spinnerEvent = false; // Not a good programming practice. For some reason the lock I added didn't solve the problem
    //private boolean sliderEvent = false;  // but this pair of booleans did. Removing the redundant event would require additional classes.
    
    public KeyboardSetting(IKeyboard keyboard, Setting setting) {
        SETTING = setting;
        /*try {
            this.value = MyUtilities.FILE_IO_UTILITIES.readSettingFromFile(FilePath.CONFIG.getPath(),
                    keyboard.getFileName() + FileExt.INI.getExt(), setting.name(), setting.getDef());
        } catch(IOException e) {
            System.err.println("Error occured while trying to read "  + setting + " from file. Using default value.");
            this.value = setting.getDefault();
        }*/
        value = setting.getDefault();
        
        this.decimalPrecision = setting.getDecimalPrecision();
        createSettingsPanel();
    }

    public Setting getType() {
        return SETTING;
    }
    
    public int getValueAsInteger() {
        return (int) value;
    }
    
    public double getValue() {
        return value;
    }
    
    public void setValue(double value) {
        this.value = value;
        settingsSpinner.setValue(value);
        settingsSlider.setValue((int) (value / decimalPrecision.getPrecision()));
    }
    
    public JPanel getSettingsPanel() {
        return settingsPanel;
    }
    
    private void createSettingsPanel() {
        settingsPanel = new JPanel();
        settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.Y_AXIS));
        JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel modifiersPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel nameLabel = new JLabel(SETTING.name() + ":");
        settingsSlider = new JSlider(JSlider.HORIZONTAL,
                (int)(SETTING.getMin()/decimalPrecision.getPrecision()),
                (int)(SETTING.getMax()/decimalPrecision.getPrecision()),
                (int)(value/decimalPrecision.getPrecision()));
        SpinnerNumberModel settingsSpinnerNumberModel = new SpinnerNumberModel(value, SETTING.getMin(), SETTING.getMax(), decimalPrecision.getPrecision());
        settingsSpinner = new JSpinner(settingsSpinnerNumberModel);
        JSpinner.NumberEditor editor = (JSpinner.NumberEditor)settingsSpinner.getEditor();  
        DecimalFormat format = editor.getFormat();  
        format.setMinimumFractionDigits(decimalPrecision.getPlaces());  
        editor.getTextField().setHorizontalAlignment(SwingConstants.CENTER);  
        Dimension d = settingsSpinner.getPreferredSize();  
        d.width = 85;  
        settingsSpinner.setPreferredSize(d); 
        
        settingsSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if(!VALUE_LOCK.isLocked()) {
                    VALUE_LOCK.lock();
                    try {
                        value = settingsSlider.getValue() * decimalPrecision.getPrecision();
                        settingsSpinner.setValue(value);
                    } finally {
                        VALUE_LOCK.unlock();
                    }
                }
            }
        });
        
        settingsSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if(!VALUE_LOCK.isLocked()) {
                    VALUE_LOCK.lock();
                    try {
                        double precision = decimalPrecision.getPrecision();
                        value = Math.round((double) settingsSpinner.getValue() / precision) * precision;
                        settingsSlider.setValue((int) (value / precision));
                    } finally {
                        VALUE_LOCK.unlock();
                    }
                }
            }
        });
        
        namePanel.add(nameLabel);
        modifiersPanel.add(settingsSpinner);
        modifiersPanel.add(settingsSlider);
        settingsPanel.add(namePanel);
        settingsPanel.add(modifiersPanel);
        settingsPanel.setMaximumSize(settingsPanel.getPreferredSize());
        settingsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
    }
}

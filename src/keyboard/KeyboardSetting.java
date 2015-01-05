package keyboard;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.IOException;
import java.text.DecimalFormat;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import utilities.MyUtilities;
import enums.DecimalPrecision;
import enums.FileExt;
import enums.FilePath;
import enums.Setting;

public class KeyboardSetting {
    private Setting setting;
    private double value;
    private JPanel settingsPanel;
    private DecimalPrecision decimalPrecision;
    private boolean spinnerEvent = false; // Not a good programming practice. For some reason the lock I added didn't solve the problem
    private boolean sliderEvent = false;  // but this pair of booleans did. Removing the redundant event would require additional classes.
    
    public KeyboardSetting(IKeyboard keyboard, Setting setting) {
        this.setting = setting;
        try {
            this.value = MyUtilities.FILE_IO_UTILITIES.readSettingFromFile(FilePath.CONFIG.getPath(),
                    keyboard.getFileName() + FileExt.INI.getExt(), setting.name(), setting.getDef());
        } catch(IOException e) {
            System.err.println("Error occured while trying to read "  + setting + " from file. Using default value.");
            this.value = setting.getDef();
        }
        
        this.decimalPrecision = setting.getDecimalPrecision();
        createSettingsPanel();
    }

    public Setting getType() {
        return setting;
    }
    
    public int getValueAsInteger() {
        return (int) value;
    }
    
    public double getValue() {
        return value;
    }
    
    public JPanel getSettingsPanel() {
        return settingsPanel;
    }
    
    private void createSettingsPanel() {
        settingsPanel = new JPanel();
        settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.Y_AXIS));
        JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel modifiersPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel nameLabel = new JLabel(setting.name() + ":");
        JSlider settingsSlider = new JSlider(JSlider.HORIZONTAL,
                (int)(setting.getMin()/decimalPrecision.getPrecision()),
                (int)(setting.getMax()/decimalPrecision.getPrecision()),
                (int)(value/decimalPrecision.getPrecision()));
        SpinnerNumberModel settingsSpinnerNumberModel = new SpinnerNumberModel(value, setting.getMin(), setting.getMax(), decimalPrecision.getPrecision());
        JSpinner settingsSpinner = new JSpinner(settingsSpinnerNumberModel);
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
                sliderEvent = true;
                if(!spinnerEvent) {
                    JSlider slider = (JSlider) e.getSource();
                    value = slider.getValue()*decimalPrecision.getPrecision();
                    settingsSpinner.setValue(value);
                }
                sliderEvent = false;
            }
        });
        
        settingsSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                spinnerEvent = true;
                if(!sliderEvent) {
                    JSpinner spinner = (JSpinner) e.getSource();
                    value = (double) spinner.getValue();
                    settingsSlider.setValue((int)(value/decimalPrecision.getPrecision()));
                }
                spinnerEvent = false;
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

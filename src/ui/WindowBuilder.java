// THIS IS A TEST TO SEE IF EGIT WORKS
package ui;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import javax.media.opengl.awt.GLCanvas;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import keyboard.VirtualKeyboard;
import enums.*;

public class WindowBuilder {
    
    public static void buildControlWindow(JFrame frame,
            JTextField [] subjectTextFields,       // firstname, lastname, age
            JComboBox<String> testComboBox,
            JButton [] optionsButtons,             // calibrate, run
            JRadioButton [] subjectRadioButtons,   // male, female, righthanded, lefthanded
            JSlider hoursSlider) {
        
        JPanel background = new JPanel();
        background.setLayout(new BoxLayout(background, BoxLayout.Y_AXIS));
        background.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        frame.add(background);
        
        // Build layout for Subject information area
        JPanel subjectPanel = new JPanel();
        subjectPanel.setLayout(new BoxLayout(subjectPanel, BoxLayout.Y_AXIS));
        subjectPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Subject Information"), BorderFactory.createEmptyBorder(5, 10, 10, 10)));
        background.add(subjectPanel);
        
        // Details: firstname, lastname, age, gender 
        JPanel firstNamePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel lastNamePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel agePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel sexPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel handednessPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        subjectPanel.add(firstNamePanel);
        subjectPanel.add(lastNamePanel);
        subjectPanel.add(agePanel);
        subjectPanel.add(sexPanel);
        subjectPanel.add(handednessPanel);
        
        JLabel firstNameLabel = new JLabel("First Name:     ");
        JLabel lastNameLabel = new JLabel("Last Name:     ");
        JLabel ageLabel = new JLabel("Age:                  ");
        JLabel sexLabel = new JLabel("Sex:                 ");
        JLabel handednessLabel = new JLabel("THIS IS A TEST TO SEE IF EGIT WORK: ");
        
        firstNamePanel.add(firstNameLabel);
        lastNamePanel.add(lastNameLabel);
        agePanel.add(ageLabel);
        sexPanel.add(sexLabel);
        handednessPanel.add(handednessLabel);
        
        firstNamePanel.add(subjectTextFields[0]);
        lastNamePanel.add(subjectTextFields[1]);
        agePanel.add(subjectTextFields[2]);
        sexPanel.add(subjectRadioButtons[0]);
        sexPanel.add(subjectRadioButtons[1]);
        handednessPanel.add(subjectRadioButtons[2]);
        handednessPanel.add(subjectRadioButtons[3]);
        
        JPanel padding0 = new JPanel();
        padding0.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        subjectPanel.add(padding0);
        
        // Hours per day slider
        JPanel sliderBackground = new JPanel();
        sliderBackground.setLayout(new BoxLayout(sliderBackground, BoxLayout.Y_AXIS));
        sliderBackground.setBorder(BorderFactory.createEtchedBorder());
        subjectPanel.add(sliderBackground);
        
        JPanel hoursOnComputerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel hoursOnComputerLabel = new JLabel("How many hours per day do you typically use a computer?");
        hoursOnComputerPanel.add(hoursOnComputerLabel);
        sliderBackground.add(hoursOnComputerPanel);
        
        JPanel padding1 = new JPanel();
        padding1.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        sliderBackground.add(padding1);
        
        JPanel sliderPanel = new JPanel(new FlowLayout());
        sliderPanel.setSize(20, 40);
        
        //Turn on labels at major tick marks.
        hoursSlider.setMajorTickSpacing(6);
        hoursSlider.setMinorTickSpacing(1);
        hoursSlider.setPaintTicks(true);
        hoursSlider.setPaintLabels(true);
        sliderPanel.add(hoursSlider);
        
        sliderBackground.add(sliderPanel);
        
        // Build layout for Test Select area
        JPanel padding2 = new JPanel();
        padding2.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        background.add(padding2);
        
        JPanel testPanel = new JPanel();
        testPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Test Selection"), BorderFactory.createEmptyBorder(5, 10, 10, 10)));
        background.add(testPanel);
        
        // combo box
        for(int i = 0; i < TestType.getSize(); i++) {
            testComboBox.addItem(TestType.getByID(i).getTestName());
        }
        testComboBox.setBackground(Color.WHITE);
        testPanel.add(testComboBox);
        
        // Build layout for calibration and run buttons
        JPanel padding3 = new JPanel();
        padding3.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        background.add(padding3);
        
        JPanel optionsPanel = new JPanel();
        optionsPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Options"), BorderFactory.createEmptyBorder(5, 10, 10, 10)));
        background.add(optionsPanel);
        
        optionsPanel.add(optionsButtons[0]);
        JPanel padding4 = new JPanel();
        optionsPanel.add(padding4);
        optionsPanel.add(optionsButtons[1]);
        
        // Arrange the components inside the window
        frame.pack();
        frame.setResizable(false);
        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension windowSize = frame.getSize();
        frame.setLocation((int)(screenSize.getWidth()/2 - windowSize.getWidth()/2),
                          (int)(screenSize.getHeight()/2 - windowSize.getHeight()/2));
        
    }
    
    public static void buildExperimentWindow(JFrame frame,
            GLCanvas canvas,
            JTextField [] textFields,
            JComboBox<String> combo,
            JButton [] buttons) {
        
    }

    public static void buildCalibrationWindow(JFrame frame,
            GLCanvas canvas,
            JLabel typedLabel,
            JComboBox<String> keyboardTypeComboBox,
            JButton calibrateButton,
            JPanel[] panels) { // settings, render options
        
        JPanel background = new JPanel();
        background.setLayout(new BoxLayout(background, BoxLayout.X_AXIS));
        frame.add(background);
        
        // Left panel (typing preview and keyboard preview).
        JPanel leftPanelSetBackground = new JPanel();
        leftPanelSetBackground.setLayout(new BoxLayout(leftPanelSetBackground, BoxLayout.Y_AXIS));
        leftPanelSetBackground.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        background.add(leftPanelSetBackground);
        
        JPanel previewBackground = new JPanel();
        previewBackground.setLayout(new BoxLayout(previewBackground, BoxLayout.Y_AXIS));
        previewBackground.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Preview"), BorderFactory.createEmptyBorder(5, 10, 10, 10)));
        leftPanelSetBackground.add(previewBackground);
        
        // Build typing preview.
        JPanel typedPanel = new JPanel();
        typedPanel.setBackground(Color.WHITE);
        typedPanel.setLayout(new GridBagLayout());
        typedPanel.setBorder(BorderFactory.createEtchedBorder());
        typedPanel.setPreferredSize(new Dimension(VirtualKeyboard.WIDTH, 100));
        previewBackground.add(typedPanel);
        
        // Add our modded label.
        typedPanel.add(typedLabel);
        
        // Build canvas preview.
        canvas.setPreferredSize(new Dimension(VirtualKeyboard.WIDTH + 20, VirtualKeyboard.HEIGHT + 20));
        previewBackground.add(canvas);
        
        // Right panel (type selection, settings, and render options).
        JPanel rightPanelSet = new JPanel();
        rightPanelSet.setLayout(new BoxLayout(rightPanelSet, BoxLayout.Y_AXIS));
        rightPanelSet.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 10));
        background.add(rightPanelSet);
        
        // Add our three info panels (keyboard select, settings, render options) to the right side.
        // 1 - Keyboard Type
        JPanel keyboardTypePanel = new JPanel();
        keyboardTypePanel.setLayout(new BoxLayout(keyboardTypePanel, BoxLayout.Y_AXIS));
        keyboardTypePanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Keyboard Type"), BorderFactory.createEmptyBorder(5, 10, 10, 10)));
        //keyboardTypePanel.setPreferredSize(new Dimension(250, 0));
        rightPanelSet.add(keyboardTypePanel);
        
        // keyboard combo box
        for(int i = 0; i < KeyboardType.getSize(); i++) {
            keyboardTypeComboBox.addItem(KeyboardType.getByID(i).getKeyboardName());
        }
        keyboardTypeComboBox.setBackground(Color.WHITE);
        keyboardTypeComboBox.setMaximumSize(new Dimension(keyboardTypeComboBox.getMaximumSize().width, keyboardTypeComboBox.getMinimumSize().height));
        keyboardTypePanel.add(keyboardTypeComboBox);
        
        // calibration button (grey out for all but leap) -- calibrates the leap plane
        keyboardTypePanel.add(calibrateButton);
        
        // 2 - Render Options
        JPanel renderOptionsPanel = new JPanel();
        renderOptionsPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Render Options"), BorderFactory.createEmptyBorder(5, 10, 10, 10)));
        rightPanelSet.add(renderOptionsPanel);
        
        // 3 - Settings
        JPanel settingsPanelMain = new JPanel();
        settingsPanelMain.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Settings"), BorderFactory.createEmptyBorder(5, 10, 10, 10)));
        rightPanelSet.add(settingsPanelMain);
        
        settingsPanelMain.add(panels[0]);
        panels[0].setLayout(new BoxLayout(panels[0], BoxLayout.Y_AXIS));
        
        JPanel testPan = new JPanel(new FlowLayout(FlowLayout.LEFT));
        testPan.add(new JLabel("Test: "));
        testPan.add(new JTextField());
        
        panels[0].add(testPan);
        panels[0].add(new JLabel("TEST"));
        panels[0].add(new JLabel("TEST"));
        panels[0].add(new JLabel("TEST"));       
        panels[0].add(new JLabel("TEST lots of words to see what happens we need it to pack really"));
        panels[0].add(new JLabel("TEST"));
        panels[0].add(new JLabel("TEST"));
        panels[0].add(new JLabel("TEST"));
        panels[0].add(new JLabel("TEST"));
        panels[0].add(new JLabel("TEST"));
        panels[0].add(new JLabel("TEST"));
        panels[0].add(new JLabel("TEST"));
        panels[0].add(new JLabel("TEST"));
        panels[0].add(new JLabel("TEST"));
        panels[0].add(new JLabel("TEST"));
        panels[0].add(new JLabel("TEST"));
        panels[0].add(new JLabel("TEST"));
        panels[0].add(new JLabel("TEST"));
        panels[0].add(new JLabel("TEST"));
        panels[0].add(new JLabel("TEST"));
        panels[0].add(new JLabel("TEST"));
        panels[0].add(new JLabel("TEST"));
        panels[0].add(new JLabel("TEST"));
        panels[0].add(new JLabel("TEST"));
        panels[0].add(new JLabel("TEST"));
        panels[0].add(new JLabel("TEST"));
        panels[0].add(new JLabel("TEST"));
        panels[0].add(new JLabel("TEST"));
        panels[0].add(new JLabel("TEST"));
        panels[0].add(new JLabel("TEST"));
        panels[0].add(new JLabel("TEST"));
        panels[0].add(new JLabel("TEST"));
        panels[0].add(new JLabel("TEST"));
        panels[0].add(new JLabel("TEST"));
        panels[0].add(new JLabel("TEST"));
        panels[0].add(new JLabel("TEST"));
        panels[0].add(new JLabel("TEST"));
        panels[0].add(new JLabel("TEST"));
        panels[0].add(new JLabel("TEST"));
        panels[0].add(new JLabel("TEST"));
        panels[0].add(new JLabel("TEST"));
        panels[0].add(new JLabel("TEST"));
        panels[0].add(new JLabel("TEST"));
        panels[0].add(new JLabel("TEST"));
        panels[0].add(new JLabel("TEST"));
        panels[0].add(new JLabel("TEST"));
        panels[0].add(new JLabel("TEST"));
        panels[0].add(new JLabel("TEST"));
        panels[0].add(new JLabel("TEST"));
        panels[0].add(new JLabel("TEST"));
        panels[0].add(new JLabel("TEST"));
        panels[0].add(new JLabel("TEST"));
        panels[0].add(new JLabel("TEST"));
        panels[0].add(new JLabel("TEST"));
        
        
        JScrollPane scrollBar = new JScrollPane(panels[0], JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollBar.setPreferredSize(new Dimension(settingsPanelMain.getPreferredSize().width, 200));
        settingsPanelMain.add(scrollBar);
        
        // TODO: Change to size of currently selected keyboard.

        // This is just the code to change font size. Can put this in a location to set dynamically (need to worry about 'typed' parent panel to determine size).
        /*
        Font labelFont = typed.getFont();
        String labelText = typed.getText();

        int stringWidth = typed.getFontMetrics(labelFont).stringWidth(labelText);
        int componentWidth = frame.getWidth();

        // Find out how much the font can grow in width.
        double widthRatio = (double)componentWidth / (double)stringWidth;

        int newFontSize = (int)(labelFont.getSize() * widthRatio);
        int componentHeight = frame.getHeight();

        // Pick a new font size so it will not be larger than the height of label.
        int fontSizeToUse = Math.min(newFontSize, componentHeight);

        // Set the label's font size to the newly determined size.
        typed.setFont(new Font(labelFont.getName(), Font.PLAIN, fontSizeToUse));
        */
        
        // Arrange the components inside the window
        frame.pack();
        //frame.setResizable(false);
        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension windowSize = frame.getSize();
        frame.setLocation((int)(screenSize.getWidth()/2 - windowSize.getWidth()/2),
                          (int)(screenSize.getHeight()/2 - windowSize.getHeight()/2));
        
    }
}

package ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.UIManager;

import enums.*;

public class WindowBuilder {
    
    public static void buildControlWindow(JFrame frame,
            JComboBox<String> testComboBox,
            JTextField subjectField,
            JButton [] optionsButtons) { // calibrate, run, edit
        
        JPanel background = new JPanel();
        background.setLayout(new BoxLayout(background, BoxLayout.Y_AXIS));
        background.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        frame.add(background);
        
        // Build layout for the subject ID
        JPanel subjectPanel = new JPanel();
        subjectPanel.setLayout(new BoxLayout(subjectPanel, BoxLayout.X_AXIS));
        subjectPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Subject Information"), BorderFactory.createEmptyBorder(5, 10, 10, 10)));
        background.add(subjectPanel);
        
        JLabel subjectLabel = new JLabel("Subject ID:");
        subjectPanel.add(subjectLabel);
        JPanel padding0 = new JPanel();
        padding0.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
        subjectPanel.add(padding0);
        subjectPanel.add(subjectField);
        subjectField.setEditable(false);
        subjectField.setHighlighter(null);
        subjectField.setHorizontalAlignment(JTextField.CENTER);
        
        JPanel padding1 = new JPanel();
        padding1.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
        subjectPanel.add(padding1);
        subjectPanel.add(optionsButtons[2]);
        
        // Build layout for Test Select area
        JPanel padding2 = new JPanel();
        padding2.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        background.add(padding2);
        
        JPanel testPanel = new JPanel();
        testPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Test Selection"), BorderFactory.createEmptyBorder(5, 10, 10, 10)));
        background.add(testPanel);
        
        // combo box
        for(TestType testType: TestType.values()) {
            testComboBox.addItem(testType.getName());
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
    
    public static void buildExitSurveyWindow(JFrame frame) {
        /*// Build layout for Subject information area
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
        JLabel handednessLabel = new JLabel("Handedness: ");
        
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
        
        sliderBackground.add(sliderPanel);*/
    }
    
    public static void buildExperimentWindow(JFrame frame,
            JPanel canvasPanel,
            JEditorPane infoPane,
            JPanel[] panels, // word, answer, settingsPanel, infoPanel
            JLabel[] labels, // word, answer
            JButton [] buttons, // calibration, tutorial, practice, experiment
            JSplitPane splitPane) {
        
        JPanel background = new JPanel();
        background.setLayout(new BoxLayout(background, BoxLayout.X_AXIS));
        frame.add(background);
        
        // Add split pane.
        background.add(splitPane);
        
        // Left panel (word and keyboard preview)
        JPanel leftPanelSetBackground = new JPanel();
        leftPanelSetBackground.setLayout(new BoxLayout(leftPanelSetBackground, BoxLayout.Y_AXIS));
        leftPanelSetBackground.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        splitPane.setLeftComponent(leftPanelSetBackground);
        
        JPanel previewBackground = new JPanel();
        previewBackground.setLayout(new BoxLayout(previewBackground, BoxLayout.Y_AXIS));
        previewBackground.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Preview"), BorderFactory.createEmptyBorder(5, 10, 10, 10)));
        leftPanelSetBackground.add(previewBackground);
        
        // Build word panel.
        panels[0].setBackground(Color.WHITE);
        panels[0].setLayout(new GridBagLayout());
        panels[0].setBorder(BorderFactory.createMatteBorder(2, 2, 1, 2, Color.DARK_GRAY));
        Dimension d0 = panels[0].getPreferredSize();
        d0.height = 150;
        panels[0].setPreferredSize(d0);
        panels[0].setMinimumSize(d0);
        panels[0].setMaximumSize(new Dimension(1000, d0.height));
        previewBackground.add(panels[0]);
        
        panels[0].add(labels[0]);
        labels[0].setVerticalAlignment(JLabel.BOTTOM);
        
        // Add answer panel.
        panels[1].setBackground(Color.WHITE);
        panels[1].setLayout(new GridBagLayout());
        panels[1].setBorder(BorderFactory.createMatteBorder(1, 2, 2, 2, Color.DARK_GRAY));
        panels[1].setPreferredSize(d0);
        panels[1].setMinimumSize(d0);
        panels[1].setMaximumSize(new Dimension(1000, d0.height));
        previewBackground.add(panels[1]);
        
        panels[1].add(labels[1]);
        labels[1].setVerticalAlignment(JLabel.BOTTOM);
        
        // Add the keyboard canvas.
        previewBackground.add(canvasPanel);
        
        // Right panel (Description and experiment options).
        JPanel rightPanelSet = new JPanel();
        rightPanelSet.setLayout(new BoxLayout(rightPanelSet, BoxLayout.Y_AXIS));
        rightPanelSet.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        Dimension d1 = rightPanelSet.getPreferredSize();
        d1.width = 375;
        rightPanelSet.setPreferredSize(d1);
        rightPanelSet.setMinimumSize(rightPanelSet.getPreferredSize());
        splitPane.setRightComponent(rightPanelSet);
        
        // Add a info panel and add the text area.
        panels[3].setLayout(new BoxLayout(panels[3], BoxLayout.Y_AXIS));
        panels[3].setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Info"), BorderFactory.createEmptyBorder(5, 10, 10, 10)));
        rightPanelSet.add(panels[3]);
        
        infoPane.setEditable(false);
        infoPane.setHighlighter(null);
        infoPane.setBackground(UIManager.getColor("Panel.background"));
        panels[3].add(infoPane);
        
        // Add the settings panel
        JPanel settingsPanelMain = new JPanel(new GridLayout(0,1));
        settingsPanelMain.setPreferredSize(new Dimension(d1.width, 200));
        settingsPanelMain.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Settings"), BorderFactory.createEmptyBorder(5, 10, 10, 10)));
        rightPanelSet.add(settingsPanelMain);
        
        panels[2].setLayout(new BoxLayout(panels[2], BoxLayout.Y_AXIS));

        JScrollPane settingsScrollBar = new JScrollPane(panels[2], JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        settingsScrollBar.getVerticalScrollBar().setUnitIncrement(16);
        settingsPanelMain.add(settingsScrollBar);
        
        // Add the options section with all the buttons we desire.
        JPanel optionsPanel = new JPanel();
        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
        optionsPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Options"), BorderFactory.createEmptyBorder(5, 10, 10, 10)));
        rightPanelSet.add(optionsPanel);
        
        JPanel buttonsPanel0 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonsPanel0.add(buttons[0]);
        optionsPanel.add(buttonsPanel0);
        
        JPanel padding0 = new JPanel();
        optionsPanel.add(padding0);
        
        JPanel buttonsPanel1 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonsPanel1.add(buttons[1]);
        buttonsPanel1.add(buttons[2]);
        buttonsPanel1.add(buttons[3]);
        optionsPanel.add(buttonsPanel1);
        
        buttons[2].setEnabled(false);
        buttons[3].setEnabled(false);
        
        // Arrange the components inside the window
        frame.pack();
        frame.setResizable(false); 
        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension windowSize = frame.getSize();
        frame.setLocation((int)(screenSize.getWidth()/2 - windowSize.getWidth()/2),
                          (int)(screenSize.getHeight()/2 - windowSize.getHeight()/2));
    }

    public static void buildCalibrationWindow(JFrame frame,
            JPanel canvasPanel,
            JLabel wordLabel,
            JComboBox<String> keyboardTypeComboBox,
            JButton[] buttons, // calibration, settings
            JPanel[] panels) { // word, settings, render options
        
        JPanel background = new JPanel();
        background.setLayout(new BoxLayout(background, BoxLayout.X_AXIS));
        frame.add(background);
        
        // Left panel (word preview and keyboard preview).
        JPanel leftPanelSetBackground = new JPanel();
        leftPanelSetBackground.setLayout(new BoxLayout(leftPanelSetBackground, BoxLayout.Y_AXIS));
        leftPanelSetBackground.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        background.add(leftPanelSetBackground);
        
        JPanel previewBackground = new JPanel();
        previewBackground.setLayout(new BoxLayout(previewBackground, BoxLayout.Y_AXIS));
        previewBackground.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Preview"), BorderFactory.createEmptyBorder(5, 10, 10, 10)));
        leftPanelSetBackground.add(previewBackground);
        
        // Build word preview.
        panels[0].setBackground(Color.WHITE);
        panels[0].setLayout(new GridBagLayout());
        panels[0].setBorder(BorderFactory.createEtchedBorder());
        Dimension d0 = panels[0].getPreferredSize();
        d0.height = 150;
        panels[0].setPreferredSize(d0);
        panels[0].setMinimumSize(d0);
        panels[0].setMaximumSize(new Dimension(1000, d0.height));
        previewBackground.add(panels[0]);
        
        // Add our modded label.
        panels[0].add(wordLabel);
        wordLabel.setVerticalAlignment(JLabel.BOTTOM);
        
        // Build canvas preview.
        previewBackground.add(canvasPanel);
        
        // Right panel (type selection, settings, and render options).
        JPanel rightPanelSet = new JPanel();
        rightPanelSet.setLayout(new BoxLayout(rightPanelSet, BoxLayout.Y_AXIS));
        rightPanelSet.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 10));
        Dimension d1 = rightPanelSet.getPreferredSize();
        d1.width = 375;
        rightPanelSet.setPreferredSize(d1);
        rightPanelSet.setMinimumSize(rightPanelSet.getPreferredSize());
        background.add(rightPanelSet);
        
        // Add our three info panels (keyboard select, settings, render options) to the right side.
        // 1 - Keyboard Type
        JPanel keyboardTypeSelectionPanel = new JPanel();
        keyboardTypeSelectionPanel.setLayout(new BoxLayout(keyboardTypeSelectionPanel, BoxLayout.Y_AXIS));
        keyboardTypeSelectionPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Keyboard Type"), BorderFactory.createEmptyBorder(5, 10, 10, 10)));
        keyboardTypeSelectionPanel.setMaximumSize(new Dimension(rightPanelSet.getPreferredSize().width, 100));
        rightPanelSet.add(keyboardTypeSelectionPanel);
        
        // keyboard combo box
        for(Keyboard keyboard: Keyboard.values()) {
            keyboardTypeComboBox.addItem(keyboard.getName());
        }
        keyboardTypeComboBox.setBackground(Color.WHITE);
        keyboardTypeComboBox.setMaximumSize(new Dimension(keyboardTypeComboBox.getMaximumSize().width, keyboardTypeComboBox.getMinimumSize().height));
        keyboardTypeSelectionPanel.add(keyboardTypeComboBox);
        
        JPanel padding0 = new JPanel();
        keyboardTypeSelectionPanel.add(padding0);
        
        JPanel keyboardButtonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        keyboardTypeSelectionPanel.add(keyboardButtonsPanel);
        
        // Add the calibration button and save settings button.
        keyboardButtonsPanel.add(buttons[0]);
        keyboardButtonsPanel.add(buttons[1]);
        
        // 1.5 - Need a special panel to allow the scrollpanes to be the same size.
        JPanel settingsAndRenderPanel = new JPanel(new GridLayout(2,1));
        rightPanelSet.add(settingsAndRenderPanel);
        
        // 2 - Settings
        JPanel settingsPanelMain = new JPanel(new GridLayout(0,1));
        settingsPanelMain.setPreferredSize(new Dimension(d1.width, 200));
        settingsPanelMain.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Settings"), BorderFactory.createEmptyBorder(5, 10, 10, 10)));
        settingsAndRenderPanel.add(settingsPanelMain);
        
        panels[1].setLayout(new BoxLayout(panels[1], BoxLayout.Y_AXIS));

        JScrollPane settingsScrollBar = new JScrollPane(panels[1], JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        settingsScrollBar.getVerticalScrollBar().setUnitIncrement(16);
        settingsPanelMain.add(settingsScrollBar);
        
        // 3 - Render Options
        JPanel renderOptionsPanelMain = new JPanel(new GridLayout(0,1));
        renderOptionsPanelMain.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Render Options"), BorderFactory.createEmptyBorder(5, 10, 10, 10)));
        settingsAndRenderPanel.add(renderOptionsPanelMain);
        
        panels[2].setLayout(new BoxLayout(panels[2], BoxLayout.Y_AXIS));

        JScrollPane renderOptionsScrollBar = new JScrollPane(panels[2], JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        renderOptionsScrollBar.getVerticalScrollBar().setUnitIncrement(16);
        renderOptionsPanelMain.add(renderOptionsScrollBar);
        
        // Arrange the components inside the window
        frame.pack();
        frame.setResizable(false); 
        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension windowSize = frame.getSize();
        frame.setLocation((int)(screenSize.getWidth()/2 - windowSize.getWidth()/2),
                          (int)(screenSize.getHeight()/2 - windowSize.getHeight()/2));
    }
}

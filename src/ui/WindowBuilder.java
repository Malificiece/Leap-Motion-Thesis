package ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import utilities.MyUtilities;
import enums.*;

public class WindowBuilder {
    
    public static void buildControlWindow(JFrame frame,
            JComboBox<String> testComboBox,
            JTextField subjectField,
            JButton [] optionsButtons) { // calibrate, run, edit, exit
        
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
        subjectPanel.add(MyUtilities.SWING_UTILITIES.createPadding(10, SwingConstants.HORIZONTAL));
        subjectPanel.add(subjectField);
        subjectField.setEditable(false);
        subjectField.setHighlighter(null);
        subjectField.setHorizontalAlignment(JTextField.CENTER);
        
        subjectPanel.add(MyUtilities.SWING_UTILITIES.createPadding(10, SwingConstants.HORIZONTAL));
        subjectPanel.add(optionsButtons[2]);
        
        // Build layout for Test Select area
        background.add(MyUtilities.SWING_UTILITIES.createPadding(10, SwingConstants.VERTICAL));
        
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
        background.add(MyUtilities.SWING_UTILITIES.createPadding(10, SwingConstants.VERTICAL));
        
        JPanel optionsPanel = new JPanel();
        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
        optionsPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Options"), BorderFactory.createEmptyBorder(5, 10, 10, 10)));
        background.add(optionsPanel);
        
        JPanel buttonGroup1 = new JPanel();
        optionsPanel.add(buttonGroup1);
        buttonGroup1.add(optionsButtons[0]);
        JPanel padding = new JPanel();
        buttonGroup1.add(padding);
        buttonGroup1.add(optionsButtons[1]);
        
        JPanel buttonGroup2 = new JPanel();
        optionsPanel.add(buttonGroup2);
        buttonGroup2.add(optionsButtons[3]);
        
        // Arrange the components inside the window
        frame.pack();
        frame.setResizable(false);
        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension windowSize = frame.getSize();
        frame.setLocation((int)(screenSize.getWidth()/2 - windowSize.getWidth()/2),
                          (int)(screenSize.getHeight()/2 - windowSize.getHeight()/2));
    }
    
    public static void buildExitSurveyWindow(JFrame frame,
            JTextField[] subjectTextFields, // id, age, major
            JTextField[] historyTextFields, // physical, gesture, touch, swipe
            JTextField[] ratingTextFields, // controller, tablet, surface, air, pinch
            ButtonGroup[] subjectButtonGroups, // gender, own computer, hours, handedness, touch hand
            ButtonGroup[] historyButtonGroups, // physical, gesture, touch, swipe
            ButtonGroup[] discomfortButtonGroups, // controller, tablet, surface, air, pinch
            ButtonGroup[] fatigueButtonGroups, // controller, tablet, surface, air, pinch
            ButtonGroup[] difficutlyButtonGroups) { // controller, tablet, surface, air, pinch

        JPanel background = new JPanel(new GridLayout(0,1));
        background.setPreferredSize(new Dimension(800, 600));
        frame.add(background);
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        
        // Add subject information section.
        JPanel subjectInfoPanel = new JPanel(new GridLayout(1,3));
        subjectInfoPanel.setLayout(new BoxLayout(subjectInfoPanel, BoxLayout.X_AXIS));
        subjectInfoPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Subject Information"), BorderFactory.createEmptyBorder(5, 10, 10, 10)));
        contentPanel.add(subjectInfoPanel);
        
        // Subject info left side
        JPanel leftSubjectInfoPanel = new JPanel(new SpringLayout());
        subjectInfoPanel.add(leftSubjectInfoPanel);
        
        JLabel subjectIDLabel = new JLabel("Subject ID:", JLabel.LEADING);        
        leftSubjectInfoPanel.add(subjectIDLabel);
        subjectTextFields[0].setHorizontalAlignment(JTextField.CENTER);
        leftSubjectInfoPanel.add(subjectTextFields[0]);
        
        JLabel genderLabel = new JLabel("Gender:", JLabel.LEADING);
        JPanel genderGroupPanel = new JPanel();
        JRadioButton maleRadioButton = new JRadioButton(ExitSurveyOptions.MALE_GENDER.getDescription());
        JRadioButton femaleRadioButton = new JRadioButton(ExitSurveyOptions.FEMALE_GENDER.getDescription());
        JRadioButton otherRadioButton = new JRadioButton(ExitSurveyOptions.OTHER_GENDER.getDescription());
        genderGroupPanel.add(maleRadioButton);
        genderGroupPanel.add(femaleRadioButton);
        genderGroupPanel.add(otherRadioButton);
        leftSubjectInfoPanel.add(genderLabel);
        leftSubjectInfoPanel.add(genderGroupPanel);
        subjectButtonGroups[0].add(maleRadioButton);
        subjectButtonGroups[0].add(femaleRadioButton);
        subjectButtonGroups[0].add(otherRadioButton);
        
        MyUtilities.SPRING_UTILITIES.makeCompactGrid(leftSubjectInfoPanel,
                2, 2,        //rows, cols
                6, 6,        //initX, initY
                6, 6);       //xPad, yPad
        
        // Add some padding in between
        subjectInfoPanel.add(MyUtilities.SWING_UTILITIES.createPadding(225, SwingConstants.HORIZONTAL));
        
        // Subject info right side
        JPanel rightSubjectInfoPanel = new JPanel(new SpringLayout());
        subjectInfoPanel.add(rightSubjectInfoPanel);
        
        JLabel ageLabel = new JLabel("Age:", JLabel.TRAILING);        
        rightSubjectInfoPanel.add(ageLabel);
        subjectTextFields[1].setHorizontalAlignment(JTextField.CENTER);
        rightSubjectInfoPanel.add(subjectTextFields[1]);
        
        JLabel majorLabel = new JLabel("Major:", JLabel.TRAILING);        
        rightSubjectInfoPanel.add(majorLabel);
        subjectTextFields[2].setHorizontalAlignment(JTextField.CENTER);
        rightSubjectInfoPanel.add(subjectTextFields[2]);
        
        MyUtilities.SPRING_UTILITIES.makeCompactGrid(rightSubjectInfoPanel,
                2, 2,        //rows, cols
                6, 6,        //initX, initY
                6, 6);       //xPad, yPad
        
        // Question 1 - Do you own personal computer?
        contentPanel.add(MyUtilities.SWING_UTILITIES.createPadding(25, SwingConstants.VERTICAL));
        JTextArea ownComputerQuestion = new JTextArea("1. Do you own a personal computer (eg: Desktop, Lapton, Netbook, Tablet, etc)?");
        ownComputerQuestion.setEditable(false);
        ownComputerQuestion.setHighlighter(null);
        ownComputerQuestion.setBackground(UIManager.getColor("Panel.background"));
        ownComputerQuestion.setFont(UIManager.getFont("Label.font"));
        ownComputerQuestion.setLineWrap(true);
        ownComputerQuestion.setWrapStyleWord(true);
        contentPanel.add(ownComputerQuestion);
        
        JPanel hasComputerGroup = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JRadioButton yesComputerRadioButton = new JRadioButton(ExitSurveyOptions.YES.getDescription());
        JRadioButton noComputerRadioButton = new JRadioButton(ExitSurveyOptions.NO.getDescription());
        hasComputerGroup.add(yesComputerRadioButton);
        hasComputerGroup.add(noComputerRadioButton);
        contentPanel.add(hasComputerGroup);
        subjectButtonGroups[1].add(yesComputerRadioButton);
        subjectButtonGroups[1].add(noComputerRadioButton);
        
        // Question 2 - How many hours a week do you use a computer?
        contentPanel.add(MyUtilities.SWING_UTILITIES.createPadding(25, SwingConstants.VERTICAL));
        JTextArea hoursPerWeekQuestion = new JTextArea("2. How much time do you spend on a computer each week?");
        hoursPerWeekQuestion.setEditable(false);
        hoursPerWeekQuestion.setHighlighter(null);
        hoursPerWeekQuestion.setBackground(UIManager.getColor("Panel.background"));
        hoursPerWeekQuestion.setFont(UIManager.getFont("Label.font"));
        hoursPerWeekQuestion.setLineWrap(true);
        hoursPerWeekQuestion.setWrapStyleWord(true);
        contentPanel.add(hoursPerWeekQuestion);
        
        JPanel hoursGroup0 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel hoursGroup1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel hoursGroup2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel hoursGroup3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JRadioButton hoursRadioButton0 = new JRadioButton(ExitSurveyOptions.HOURS_0.getDescription() + " hours");
        JRadioButton hoursRadioButton1 = new JRadioButton(ExitSurveyOptions.HOURS_1.getDescription() + " hours");
        JRadioButton hoursRadioButton2 = new JRadioButton(ExitSurveyOptions.HOURS_2.getDescription() + " hours");
        JRadioButton hoursRadioButton3 = new JRadioButton(ExitSurveyOptions.HOURS_3.getDescription() + " hours");
        JRadioButton hoursRadioButton4 = new JRadioButton(ExitSurveyOptions.HOURS_4.getDescription() + " hours");
        JRadioButton hoursRadioButton5 = new JRadioButton(ExitSurveyOptions.HOURS_5.getDescription() + " hours");
        JRadioButton hoursRadioButton6 = new JRadioButton(ExitSurveyOptions.HOURS_6.getDescription() + " hours");
        JRadioButton hoursRadioButton7 = new JRadioButton(ExitSurveyOptions.HOURS_7.getDescription() + " hours");
        hoursGroup0.add(hoursRadioButton0);
        hoursGroup1.add(hoursRadioButton1);
        hoursGroup2.add(hoursRadioButton2);
        hoursGroup3.add(hoursRadioButton3);
        int hoursSize0 = hoursRadioButton0.getFontMetrics(hoursRadioButton0.getFont()).stringWidth(hoursRadioButton0.getText());
        int hoursSize1 = hoursRadioButton1.getFontMetrics(hoursRadioButton1.getFont()).stringWidth(hoursRadioButton1.getText());
        int hoursSize2 = hoursRadioButton2.getFontMetrics(hoursRadioButton2.getFont()).stringWidth(hoursRadioButton2.getText());
        int hoursSize3 = hoursRadioButton3.getFontMetrics(hoursRadioButton3.getFont()).stringWidth(hoursRadioButton3.getText());
        int gapDifference0 = hoursSize3 - hoursSize0;
        int gapDifference1 = hoursSize3 - hoursSize1;
        int gapDifference2 = hoursSize3 - hoursSize2;
        hoursGroup0.add(MyUtilities.SWING_UTILITIES.createPadding(75 + gapDifference0, SwingConstants.HORIZONTAL));
        hoursGroup1.add(MyUtilities.SWING_UTILITIES.createPadding(75 + gapDifference1, SwingConstants.HORIZONTAL));
        hoursGroup2.add(MyUtilities.SWING_UTILITIES.createPadding(75 + gapDifference2, SwingConstants.HORIZONTAL));
        hoursGroup3.add(MyUtilities.SWING_UTILITIES.createPadding(75, SwingConstants.HORIZONTAL));
        hoursGroup0.add(hoursRadioButton4);
        hoursGroup1.add(hoursRadioButton5);
        hoursGroup2.add(hoursRadioButton6);
        hoursGroup3.add(hoursRadioButton7);
        contentPanel.add(hoursGroup0);
        contentPanel.add(hoursGroup1);
        contentPanel.add(hoursGroup2);
        contentPanel.add(hoursGroup3);
        subjectButtonGroups[2].add(hoursRadioButton0);
        subjectButtonGroups[2].add(hoursRadioButton1);
        subjectButtonGroups[2].add(hoursRadioButton2);
        subjectButtonGroups[2].add(hoursRadioButton3);
        subjectButtonGroups[2].add(hoursRadioButton4);
        subjectButtonGroups[2].add(hoursRadioButton5);
        subjectButtonGroups[2].add(hoursRadioButton6);
        subjectButtonGroups[2].add(hoursRadioButton7);
        
        // Question 3 - Do you have gesture device experience?
        contentPanel.add(MyUtilities.SWING_UTILITIES.createPadding(25, SwingConstants.VERTICAL));
        JTextArea gestureExperienceQuestion = new JTextArea("3. Have you used gestural controllers before (eg: Xbox Kinect, Leap Motion, etc) or any other gesture devices?\n\n"
                + "If yes, please indicate the type of device.");
        gestureExperienceQuestion.setEditable(false);
        gestureExperienceQuestion.setHighlighter(null);
        gestureExperienceQuestion.setBackground(UIManager.getColor("Panel.background"));
        gestureExperienceQuestion.setFont(UIManager.getFont("Label.font"));
        gestureExperienceQuestion.setLineWrap(true);
        gestureExperienceQuestion.setWrapStyleWord(true);
        contentPanel.add(gestureExperienceQuestion);
        
        JPanel gestureExperienceYesGroup = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JRadioButton yesGestureExperienceRadioButton = new JRadioButton(ExitSurveyOptions.YES.getDescription());
        JLabel gestureExperienceYesLabel = new JLabel("(please list devices):");
        historyTextFields[1].setEditable(false);
        gestureExperienceYesGroup.add(yesGestureExperienceRadioButton);
        gestureExperienceYesGroup.add(gestureExperienceYesLabel);
        gestureExperienceYesGroup.add(historyTextFields[1]);
        JPanel gestureExperienceNoGroup = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JRadioButton noGestureExperienceRadioButton = new JRadioButton(ExitSurveyOptions.NO.getDescription());
        gestureExperienceNoGroup.add(noGestureExperienceRadioButton);
        contentPanel.add(gestureExperienceYesGroup);
        contentPanel.add(gestureExperienceNoGroup);
        historyButtonGroups[1].add(yesGestureExperienceRadioButton);
        historyButtonGroups[1].add(noGestureExperienceRadioButton);
        
        // Question 4 - Do you have touch screen device experience?
        contentPanel.add(MyUtilities.SWING_UTILITIES.createPadding(25, SwingConstants.VERTICAL));
        JTextArea touchExperienceQuestion = new JTextArea("4. Have you used touch devices before (eg: iPad, Surface, Smartphone, Laptop, etc)?\n\n"
                + "If yes, please indicate the type of device.");
        touchExperienceQuestion.setEditable(false);
        touchExperienceQuestion.setHighlighter(null);
        touchExperienceQuestion.setBackground(UIManager.getColor("Panel.background"));
        touchExperienceQuestion.setFont(UIManager.getFont("Label.font"));
        touchExperienceQuestion.setLineWrap(true);
        touchExperienceQuestion.setWrapStyleWord(true);
        contentPanel.add(touchExperienceQuestion);
        
        JPanel touchExperienceYesGroup = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JRadioButton yesTouchExperienceRadioButton = new JRadioButton(ExitSurveyOptions.YES.getDescription());
        JLabel touchExperienceYesLabel = new JLabel("(please list devices):");
        historyTextFields[2].setEditable(false);
        touchExperienceYesGroup.add(yesTouchExperienceRadioButton);
        touchExperienceYesGroup.add(touchExperienceYesLabel);
        touchExperienceYesGroup.add(historyTextFields[2]);
        JPanel touchExperienceNoGroup = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JRadioButton noTouchExperienceRadioButton = new JRadioButton(ExitSurveyOptions.NO.getDescription());
        touchExperienceNoGroup.add(noTouchExperienceRadioButton);
        contentPanel.add(touchExperienceYesGroup);
        contentPanel.add(touchExperienceNoGroup);
        historyButtonGroups[2].add(yesTouchExperienceRadioButton);
        historyButtonGroups[2].add(noTouchExperienceRadioButton);
        
        // Question 5 - Do you have swipe keyboard experience?
        contentPanel.add(MyUtilities.SWING_UTILITIES.createPadding(25, SwingConstants.VERTICAL));
        JTextArea swipeExperienceQuestion = new JTextArea("5. Have you used a swipe-based keyboard before on any device (eg: Android, Surface, etc)?\n\n"
                + "If yes, please indicate the type of device.");
        swipeExperienceQuestion.setEditable(false);
        swipeExperienceQuestion.setHighlighter(null);
        swipeExperienceQuestion.setBackground(UIManager.getColor("Panel.background"));
        swipeExperienceQuestion.setFont(UIManager.getFont("Label.font"));
        swipeExperienceQuestion.setLineWrap(true);
        swipeExperienceQuestion.setWrapStyleWord(true);
        contentPanel.add(swipeExperienceQuestion);
        
        JPanel swipeExperienceYesGroup = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JRadioButton yesSwipeExperienceRadioButton = new JRadioButton(ExitSurveyOptions.YES.getDescription());
        JLabel swipeExperienceYesLabel = new JLabel("(please list devices):");
        historyTextFields[3].setEditable(false);
        swipeExperienceYesGroup.add(yesSwipeExperienceRadioButton);
        swipeExperienceYesGroup.add(swipeExperienceYesLabel);
        swipeExperienceYesGroup.add(historyTextFields[3]);
        JPanel swipeExperienceNoGroup = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JRadioButton noSwipeExperienceRadioButton = new JRadioButton(ExitSurveyOptions.NO.getDescription());
        swipeExperienceNoGroup.add(noSwipeExperienceRadioButton);
        contentPanel.add(swipeExperienceYesGroup);
        contentPanel.add(swipeExperienceNoGroup);
        historyButtonGroups[3].add(yesSwipeExperienceRadioButton);
        historyButtonGroups[3].add(noSwipeExperienceRadioButton);
        
        // Question 6 - Do you have any physical impairments?
        contentPanel.add(MyUtilities.SWING_UTILITIES.createPadding(25, SwingConstants.VERTICAL));
        JTextArea impairExperienceQuestion = new JTextArea("5. Do you haev any physical impairment that makes it difficult to use a computer?\n\n"
                + "If yes, please indicate the impairment.");
        impairExperienceQuestion.setEditable(false);
        impairExperienceQuestion.setHighlighter(null);
        impairExperienceQuestion.setBackground(UIManager.getColor("Panel.background"));
        impairExperienceQuestion.setFont(UIManager.getFont("Label.font"));
        impairExperienceQuestion.setLineWrap(true);
        impairExperienceQuestion.setWrapStyleWord(true);
        contentPanel.add(impairExperienceQuestion);
        
        JPanel impairExperienceYesGroup = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JRadioButton yesImpairExperienceRadioButton = new JRadioButton(ExitSurveyOptions.YES.getDescription());
        JLabel impairExperienceYesLabel = new JLabel("(please list impairment):");
        historyTextFields[0].setEditable(false);
        impairExperienceYesGroup.add(yesImpairExperienceRadioButton);
        impairExperienceYesGroup.add(impairExperienceYesLabel);
        impairExperienceYesGroup.add(historyTextFields[0]);
        JPanel impairExperienceNoGroup = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JRadioButton noImpairExperienceRadioButton = new JRadioButton(ExitSurveyOptions.NO.getDescription());
        impairExperienceNoGroup.add(noImpairExperienceRadioButton);
        contentPanel.add(impairExperienceYesGroup);
        contentPanel.add(impairExperienceNoGroup);
        historyButtonGroups[0].add(yesImpairExperienceRadioButton);
        historyButtonGroups[0].add(noImpairExperienceRadioButton);
        
        // Add the entire content panel to a scroll pane.
        JScrollPane contentScrollBar = new JScrollPane(contentPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        contentScrollBar.getVerticalScrollBar().setUnitIncrement(16);
        background.add(contentScrollBar);
        
        // Arrange the components inside the window
        frame.pack();
        frame.setResizable(false);
        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension windowSize = frame.getSize();
        frame.setLocation((int)(screenSize.getWidth()/2 - windowSize.getWidth()/2),
                          (int)(screenSize.getHeight()/2 - windowSize.getHeight()/2));
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

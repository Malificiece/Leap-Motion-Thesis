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

package ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;

import utilities.MyUtilities;
import enums.*;

public class WindowBuilder {
    
    public static void buildControlWindow(JFrame frame,
            JComboBox<String> testComboBox,
            JTextField subjectField,
            JButton[] optionsButtons, // calibrate, run, edit, exit, dictionary, formatter, likert, randomize, browse
            JLabel testsFinishedLabel) {
        
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
        subjectField.setMaximumSize(new Dimension(subjectField.getPreferredSize().width, subjectField.getPreferredSize().height + 5));
        subjectField.setFocusable(false);
        subjectField.setEditable(false);
        subjectField.setHighlighter(null);
        subjectField.setHorizontalAlignment(JTextField.CENTER);
        
        subjectPanel.add(MyUtilities.SWING_UTILITIES.createPadding(10, SwingConstants.HORIZONTAL));
        
        JPanel subjectButtonsPanel = new JPanel();
        subjectButtonsPanel.setLayout(new BoxLayout(subjectButtonsPanel, BoxLayout.Y_AXIS));
        subjectPanel.add(subjectButtonsPanel);
        
        subjectButtonsPanel.add(optionsButtons[2]);
        subjectButtonsPanel.add(Box.createVerticalGlue());
        subjectButtonsPanel.add(Box.createVerticalGlue());
        subjectButtonsPanel.add(optionsButtons[8]);
        subjectButtonsPanel.add(Box.createVerticalGlue());
        subjectButtonsPanel.add(Box.createVerticalGlue());
        subjectButtonsPanel.add(optionsButtons[7]);
        
        // Build layout for Test Select area
        background.add(MyUtilities.SWING_UTILITIES.createPadding(10, SwingConstants.VERTICAL));
        
        JPanel testPanel = new JPanel();
        testPanel.setLayout(new BoxLayout(testPanel, BoxLayout.Y_AXIS));
        testPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Test Selection"), BorderFactory.createEmptyBorder(5, 10, 10, 10)));
        background.add(testPanel);

        // combo box
        for(Keyboard keyboard: Keyboard.values()) {
            if(keyboard.getType() != KeyboardType.DISABLED) {
                testComboBox.addItem(keyboard.getName());
            }
        }
        testComboBox.setBackground(Color.WHITE);
        testPanel.add(testComboBox);
        
        JPanel buttonGroup0 = new JPanel();
        testPanel.add(buttonGroup0);
        optionsButtons[6].setEnabled(false);
        buttonGroup0.add(MyUtilities.SWING_UTILITIES.createPadding(80, SwingConstants.HORIZONTAL));
        buttonGroup0.add(optionsButtons[6]);
        buttonGroup0.add(MyUtilities.SWING_UTILITIES.createPadding(55, SwingConstants.HORIZONTAL));
        testsFinishedLabel.setFont(new Font(testsFinishedLabel.getName(), Font.PLAIN, 10));
        testsFinishedLabel.setText(0 + "/" + testComboBox.getItemCount());
        buttonGroup0.add(testsFinishedLabel);
        
        // Build layout for calibration and run buttons
        background.add(MyUtilities.SWING_UTILITIES.createPadding(10, SwingConstants.VERTICAL));
        
        JPanel experimentOptionsPanel = new JPanel();
        experimentOptionsPanel.setLayout(new BoxLayout(experimentOptionsPanel, BoxLayout.Y_AXIS));
        experimentOptionsPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Experiment Options"), BorderFactory.createEmptyBorder(5, 10, 10, 10)));
        background.add(experimentOptionsPanel);
        
        JPanel buttonGroup1 = new JPanel();
        experimentOptionsPanel.add(buttonGroup1);
        buttonGroup1.add(optionsButtons[1]);
        buttonGroup1.add(MyUtilities.SWING_UTILITIES.createPadding(50, SwingConstants.HORIZONTAL));
        buttonGroup1.add(optionsButtons[3]);
        
        JPanel calibrationOptionsPanel = new JPanel();
        calibrationOptionsPanel.setLayout(new BoxLayout(calibrationOptionsPanel, BoxLayout.Y_AXIS));
        calibrationOptionsPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Calibration Options"), BorderFactory.createEmptyBorder(5, 10, 10, 10)));
        background.add(calibrationOptionsPanel);
        
        JPanel buttonGroup2 = new JPanel();
        calibrationOptionsPanel.add(buttonGroup2);
        buttonGroup2.add(optionsButtons[0]);
        buttonGroup2.add(MyUtilities.SWING_UTILITIES.createPadding(115, SwingConstants.HORIZONTAL));
        
        JPanel toolOptionsPanel = new JPanel();
        toolOptionsPanel.setLayout(new BoxLayout(toolOptionsPanel, BoxLayout.Y_AXIS));
        toolOptionsPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Tool Options"), BorderFactory.createEmptyBorder(5, 10, 10, 10)));
        background.add(toolOptionsPanel);
        
        JPanel buttonGroup3 = new JPanel();
        toolOptionsPanel.add(buttonGroup3);
        buttonGroup3.add(optionsButtons[4]);
        buttonGroup3.add(MyUtilities.SWING_UTILITIES.createPadding(0, SwingConstants.HORIZONTAL));
        buttonGroup3.add(optionsButtons[5]);
        
        // Arrange the components inside the window
        frame.pack();
        frame.setResizable(false);
        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension windowSize = frame.getSize();
        frame.setLocation((int)(screenSize.getWidth()/2 - windowSize.getWidth()/2),
                          (int)(screenSize.getHeight()/2 - windowSize.getHeight()/2));
    }
    
    public static void buildExitSurveyWindow(JFrame frame,
            JTextField subjectIDTextField,
            KeyboardType exitSurveyType,
            ButtonGroup[] likertButtonGroups, // discomfort, fatigue, difficulty
            JButton saveButton) {
        
        JPanel background = new JPanel();
        background.setLayout(new BoxLayout(background, BoxLayout.Y_AXIS));
        frame.add(background);
        
        // Add subject information section.
        JPanel subjectInfoPanel = new JPanel();
        subjectInfoPanel.setLayout(new BoxLayout(subjectInfoPanel, BoxLayout.X_AXIS));
        subjectInfoPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10), BorderFactory.createTitledBorder("Subject Information")));
        background.add(subjectInfoPanel);
        
        // Subject info left side
        JPanel leftSubjectInfoPanel = new JPanel(new SpringLayout());
        subjectInfoPanel.add(leftSubjectInfoPanel);
        
        JLabel subjectIDLabel = new JLabel("Subject ID:", JLabel.LEADING);        
        leftSubjectInfoPanel.add(subjectIDLabel);
        subjectIDTextField.setHorizontalAlignment(JTextField.CENTER);
        leftSubjectInfoPanel.add(subjectIDTextField);
        
        MyUtilities.SPRING_UTILITIES.makeCompactGrid(leftSubjectInfoPanel,
                1, 2,        //rows, cols
                6, 6,        //initX, initY
                6, 6);       //xPad, yPad
        
        // Add some padding in between
        subjectInfoPanel.add(MyUtilities.SWING_UTILITIES.createPadding(100, SwingConstants.HORIZONTAL));
        
        // Keyboard info right side
        JPanel rightSubjectInfoPanel = new JPanel(new SpringLayout());
        subjectInfoPanel.add(rightSubjectInfoPanel);
        
        JTextField keyboardTypeTextField = new JTextField(25);
        keyboardTypeTextField.setText(exitSurveyType.getName());
        keyboardTypeTextField.setEnabled(false);
        JLabel ageLabel = new JLabel("Keyboard:", JLabel.TRAILING);        
        rightSubjectInfoPanel.add(ageLabel);
        keyboardTypeTextField.setHorizontalAlignment(JTextField.CENTER);
        rightSubjectInfoPanel.add(keyboardTypeTextField);
        
        MyUtilities.SPRING_UTILITIES.makeCompactGrid(rightSubjectInfoPanel,
                1, 2,        //rows, cols
                6, 6,        //initX, initY
                6, 6);       //xPad, yPad
        
        // Create the content pane and scroll pane
        JPanel scrollPanel = new JPanel(new GridLayout(0,1));
        scrollPanel.setPreferredSize(new Dimension(675, 300));
        background.add(scrollPanel);
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        
        // Questions - Discomfort, Fatigue, Difficulty
        JTextArea deviceSurveySection = new JTextArea("Please indicate if you agree or disagree with the following statements.\n");
        deviceSurveySection.setFont(new Font(deviceSurveySection.getFont().getName(), Font.BOLD, deviceSurveySection.getFont().getSize()+2));
        deviceSurveySection.setEditable(false);
        deviceSurveySection.setHighlighter(null);
        deviceSurveySection.setBackground(UIManager.getColor("Panel.background"));
        deviceSurveySection.setFont(UIManager.getFont("Label.font"));
        deviceSurveySection.setLineWrap(true);
        deviceSurveySection.setWrapStyleWord(true);
        contentPanel.add(deviceSurveySection);

        // Discomfort Question
        JTextArea discomfortQuestion = new JTextArea("1. I experienced discomfort today while using the " + exitSurveyType.getName() + ". It was painful or awkward to use.");
        discomfortQuestion.setEditable(false);
        discomfortQuestion.setHighlighter(null);
        discomfortQuestion.setBackground(UIManager.getColor("Panel.background"));
        discomfortQuestion.setFont(UIManager.getFont("Label.font"));
        discomfortQuestion.setLineWrap(true);
        discomfortQuestion.setWrapStyleWord(true);
        contentPanel.add(discomfortQuestion);
        
        JPanel discomfortGroup = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JRadioButton discomfortRadioButton0 = new JRadioButton(ExitSurveyOptions.STRONGLY_AGREE.getDescription());
        JRadioButton discomfortRadioButton1 = new JRadioButton(ExitSurveyOptions.AGREE.getDescription());
        JRadioButton discomfortRadioButton2 = new JRadioButton(ExitSurveyOptions.NEUTRAL.getDescription());
        JRadioButton discomfortRadioButton3 = new JRadioButton(ExitSurveyOptions.DISAGREE.getDescription());
        JRadioButton discomfortRadioButton4 = new JRadioButton(ExitSurveyOptions.STRONGLY_DISAGREE.getDescription());
        discomfortGroup.add(discomfortRadioButton0);
        discomfortGroup.add(discomfortRadioButton1);
        discomfortGroup.add(discomfortRadioButton2);
        discomfortGroup.add(discomfortRadioButton3);
        discomfortGroup.add(discomfortRadioButton4);
        contentPanel.add(discomfortGroup);
        likertButtonGroups[0].add(discomfortRadioButton0);
        likertButtonGroups[0].add(discomfortRadioButton1);
        likertButtonGroups[0].add(discomfortRadioButton2);
        likertButtonGroups[0].add(discomfortRadioButton3);
        likertButtonGroups[0].add(discomfortRadioButton4);
        
        // Fatigue Question
        contentPanel.add(MyUtilities.SWING_UTILITIES.createPadding(25, SwingConstants.VERTICAL));
        JTextArea fatigueQuestion = new JTextArea("2. I experienced fatigue today while using the " + exitSurveyType.getName() + ". It made my arm/hand tired and sore.");
        fatigueQuestion.setEditable(false);
        fatigueQuestion.setHighlighter(null);
        fatigueQuestion.setBackground(UIManager.getColor("Panel.background"));
        fatigueQuestion.setFont(UIManager.getFont("Label.font"));
        fatigueQuestion.setLineWrap(true);
        fatigueQuestion.setWrapStyleWord(true);
        contentPanel.add(fatigueQuestion);
        
        JPanel fatigueGroup = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JRadioButton fatigueRadioButton0 = new JRadioButton(ExitSurveyOptions.STRONGLY_AGREE.getDescription());
        JRadioButton fatigueRadioButton1 = new JRadioButton(ExitSurveyOptions.AGREE.getDescription());
        JRadioButton fatigueRadioButton2 = new JRadioButton(ExitSurveyOptions.NEUTRAL.getDescription());
        JRadioButton fatigueRadioButton3 = new JRadioButton(ExitSurveyOptions.DISAGREE.getDescription());
        JRadioButton fatigueRadioButton4 = new JRadioButton(ExitSurveyOptions.STRONGLY_DISAGREE.getDescription());
        fatigueGroup.add(fatigueRadioButton0);
        fatigueGroup.add(fatigueRadioButton1);
        fatigueGroup.add(fatigueRadioButton2);
        fatigueGroup.add(fatigueRadioButton3);
        fatigueGroup.add(fatigueRadioButton4);
        contentPanel.add(fatigueGroup);
        likertButtonGroups[1].add(fatigueRadioButton0);
        likertButtonGroups[1].add(fatigueRadioButton1);
        likertButtonGroups[1].add(fatigueRadioButton2);
        likertButtonGroups[1].add(fatigueRadioButton3);
        likertButtonGroups[1].add(fatigueRadioButton4);
        
        // Difficulty Question
        contentPanel.add(MyUtilities.SWING_UTILITIES.createPadding(25, SwingConstants.VERTICAL));
        JTextArea difficultyQuestion = new JTextArea("3. I experienced difficulty today when using the " + exitSurveyType.getName() + ". This keyboard was confusing or hard to use.");
        difficultyQuestion.setEditable(false);
        difficultyQuestion.setHighlighter(null);
        difficultyQuestion.setBackground(UIManager.getColor("Panel.background"));
        difficultyQuestion.setFont(UIManager.getFont("Label.font"));
        difficultyQuestion.setLineWrap(true);
        difficultyQuestion.setWrapStyleWord(true);
        contentPanel.add(difficultyQuestion);
        
        JPanel difficultyGroup = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JRadioButton difficultyRadioButton0 = new JRadioButton(ExitSurveyOptions.STRONGLY_AGREE.getDescription());
        JRadioButton difficultyRadioButton1 = new JRadioButton(ExitSurveyOptions.AGREE.getDescription());
        JRadioButton difficultyRadioButton2 = new JRadioButton(ExitSurveyOptions.NEUTRAL.getDescription());
        JRadioButton difficultyRadioButton3 = new JRadioButton(ExitSurveyOptions.DISAGREE.getDescription());
        JRadioButton difficultyRadioButton4 = new JRadioButton(ExitSurveyOptions.STRONGLY_DISAGREE.getDescription());
        difficultyGroup.add(difficultyRadioButton0);
        difficultyGroup.add(difficultyRadioButton1);
        difficultyGroup.add(difficultyRadioButton2);
        difficultyGroup.add(difficultyRadioButton3);
        difficultyGroup.add(difficultyRadioButton4);
        contentPanel.add(difficultyGroup);
        likertButtonGroups[2].add(difficultyRadioButton0);
        likertButtonGroups[2].add(difficultyRadioButton1);
        likertButtonGroups[2].add(difficultyRadioButton2);
        likertButtonGroups[2].add(difficultyRadioButton3);
        likertButtonGroups[2].add(difficultyRadioButton4);

        // Add the entire content panel to a scroll pane.
        JScrollPane contentScrollBar = new JScrollPane(contentPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        contentScrollBar.getVerticalScrollBar().setUnitIncrement(16);
        scrollPanel.add(contentScrollBar);
        
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() { 
                contentScrollBar.getVerticalScrollBar().setValue(0);
            }
        });
        
        // Add save button
        saveButton.setHorizontalAlignment(JButton.RIGHT);
        background.add(MyUtilities.SWING_UTILITIES.createPadding(5, SwingConstants.VERTICAL));
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(saveButton);
        buttonPanel.add(MyUtilities.SWING_UTILITIES.createPadding(5, SwingConstants.HORIZONTAL));
        background.add(buttonPanel);
        background.add(MyUtilities.SWING_UTILITIES.createPadding(5, SwingConstants.VERTICAL));
        
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
            ArrayList<JTextField> rankingTextFields, // Follows Keyboard Type order
            ButtonGroup[] subjectButtonGroups, // gender, own computer, hours, handedness, touch hand
            ButtonGroup[] historyButtonGroups, // physical, gesture, touch, swipe
            int maxRanking,
            JTextArea rankingQuestion,
            JButton saveButton) {
    	
    	JPanel background = new JPanel();
    	background.setLayout(new BoxLayout(background, BoxLayout.Y_AXIS));
    	frame.add(background);
        
        // Add subject information section.
        JPanel subjectInfoPanel = new JPanel();
        subjectInfoPanel.setLayout(new BoxLayout(subjectInfoPanel, BoxLayout.X_AXIS));
        subjectInfoPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10), BorderFactory.createTitledBorder("Subject Information")));
        background.add(subjectInfoPanel);
        
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
        subjectInfoPanel.add(MyUtilities.SWING_UTILITIES.createPadding(150, SwingConstants.HORIZONTAL));
        
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
        
        // Create the content pane and scroll pane
        JPanel scrollPanel = new JPanel(new GridLayout(0,1));
        scrollPanel.setPreferredSize(new Dimension(675, 600));
        background.add(scrollPanel);
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        
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
        historyTextFields[1].setHorizontalAlignment(JTextField.CENTER);
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
        historyTextFields[2].setHorizontalAlignment(JTextField.CENTER);
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
        historyTextFields[3].setHorizontalAlignment(JTextField.CENTER);
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
        JTextArea impairExperienceQuestion = new JTextArea("6. Do you have any physical impairment that makes it difficult to use a computer?\n\n"
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
        historyTextFields[0].setHorizontalAlignment(JTextField.CENTER);
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
        
        // Question 7 - Which is your dominant hand?
        contentPanel.add(MyUtilities.SWING_UTILITIES.createPadding(25, SwingConstants.VERTICAL));
        JTextArea handednessQuestion = new JTextArea("7. Which is your dominant hand?");
        handednessQuestion.setEditable(false);
        handednessQuestion.setHighlighter(null);
        handednessQuestion.setBackground(UIManager.getColor("Panel.background"));
        handednessQuestion.setFont(UIManager.getFont("Label.font"));
        handednessQuestion.setLineWrap(true);
        handednessQuestion.setWrapStyleWord(true);
        contentPanel.add(handednessQuestion);
        
        JPanel handednessGroup = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JRadioButton rightHandRadioButton = new JRadioButton(ExitSurveyOptions.RIGHT_HAND.getDescription() + " hand");
        JRadioButton leftHandRadioButton = new JRadioButton(ExitSurveyOptions.LEFT_HAND.getDescription() + " hand");
        JRadioButton ambidextriousRadioButton = new JRadioButton(ExitSurveyOptions.AMBIDEXTROUS.getDescription());
        handednessGroup.add(rightHandRadioButton);
        handednessGroup.add(leftHandRadioButton);
        handednessGroup.add(ambidextriousRadioButton);
        contentPanel.add(handednessGroup);
        subjectButtonGroups[3].add(rightHandRadioButton);
        subjectButtonGroups[3].add(leftHandRadioButton);
        subjectButtonGroups[3].add(ambidextriousRadioButton);
        
        // Question 8 - Which hand did you use in today's experiments?
        contentPanel.add(MyUtilities.SWING_UTILITIES.createPadding(25, SwingConstants.VERTICAL));
        JTextArea experimentHandQuestion = new JTextArea("8. Which hand did you use in today's experiments?");
        experimentHandQuestion.setEditable(false);
        experimentHandQuestion.setHighlighter(null);
        experimentHandQuestion.setBackground(UIManager.getColor("Panel.background"));
        experimentHandQuestion.setFont(UIManager.getFont("Label.font"));
        experimentHandQuestion.setLineWrap(true);
        experimentHandQuestion.setWrapStyleWord(true);
        contentPanel.add(experimentHandQuestion);
        
        JPanel experimentHandGroup = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JRadioButton experimentRightHandRadioButton = new JRadioButton(ExitSurveyOptions.RIGHT_HAND.getDescription() + " hand");
        JRadioButton experimentLeftHandRadioButton = new JRadioButton(ExitSurveyOptions.LEFT_HAND.getDescription() + " hand");
        JRadioButton experimentBothRadioButton = new JRadioButton(ExitSurveyOptions.BOTH_HANDS.getDescription() + " hands");
        experimentHandGroup.add(experimentRightHandRadioButton);
        experimentHandGroup.add(experimentLeftHandRadioButton);
        experimentHandGroup.add(experimentBothRadioButton);
        contentPanel.add(experimentHandGroup);
        subjectButtonGroups[4].add(experimentRightHandRadioButton);
        subjectButtonGroups[4].add(experimentLeftHandRadioButton);
        subjectButtonGroups[4].add(experimentBothRadioButton); 
        
        // Question 9 - Rank the keyboards from best to worst
        contentPanel.add(MyUtilities.SWING_UTILITIES.createPadding(25, SwingConstants.VERTICAL));
        rankingQuestion.setText("9. Please rank the keyboards from most preferred (1), to least preferred ("+ maxRanking + ").");
        DefaultCaret caret = (DefaultCaret)rankingQuestion.getCaret();
        caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
        rankingQuestion.setEditable(false);
        rankingQuestion.setHighlighter(null);
        rankingQuestion.setBackground(UIManager.getColor("Panel.background"));
        rankingQuestion.setFont(UIManager.getFont("Label.font"));
        rankingQuestion.setLineWrap(true);
        rankingQuestion.setWrapStyleWord(true);
        contentPanel.add(rankingQuestion);
        
        JPanel rankingPanel = new JPanel(new SpringLayout());
        contentPanel.add(rankingPanel);
        
        Iterator<JTextField> rankingTextFieldIterator = rankingTextFields.iterator();
        for(Keyboard keyboard: Keyboard.values()) {
        	if(keyboard.getType() != KeyboardType.DISABLED && rankingTextFieldIterator.hasNext()) {
        		JLabel rankingLabel = new JLabel(keyboard.getName() + ":");
        		JTextField rankingTextField = rankingTextFieldIterator.next();
        		rankingTextField.setHorizontalAlignment(JTextField.CENTER);
        		rankingPanel.add(rankingLabel);
        		rankingPanel.add(rankingTextField);
        		rankingPanel.add(MyUtilities.SWING_UTILITIES.createPadding(500, SwingConstants.HORIZONTAL));
        	}
        }
        
        MyUtilities.SPRING_UTILITIES.makeCompactGrid(rankingPanel,
                rankingTextFields.size(), 3,        //rows, cols
                6, 6,        //initX, initY
                6, 6);       //xPad, yPad
        
        // Add the entire content panel to a scroll pane.
        JScrollPane contentScrollBar = new JScrollPane(contentPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        contentScrollBar.getVerticalScrollBar().setUnitIncrement(16);
        scrollPanel.add(contentScrollBar);
        
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() { 
                contentScrollBar.getVerticalScrollBar().setValue(0);
            }
        });
        
        // Add save button
        saveButton.setHorizontalAlignment(JButton.RIGHT);
        background.add(MyUtilities.SWING_UTILITIES.createPadding(5, SwingConstants.VERTICAL));
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(saveButton);
        buttonPanel.add(MyUtilities.SWING_UTILITIES.createPadding(5, SwingConstants.HORIZONTAL));
        background.add(buttonPanel);
        background.add(MyUtilities.SWING_UTILITIES.createPadding(5, SwingConstants.VERTICAL));
        
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
            JButton[] buttons, // calibration, tutorial, practice, experiment
            JSplitPane splitPane) {
        
        JPanel background = new JPanel();
        background.setLayout(new BoxLayout(background, BoxLayout.X_AXIS));
        frame.add(background);
        
        // Add split pane.
        background.add(splitPane);
        splitPane.setEnabled(false);
        
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
            if(keyboard.getType() != KeyboardType.DISABLED) {
                keyboardTypeComboBox.addItem(keyboard.getName());
            }
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

    public static void buildDictionaryWindow(JFrame frame,
            JProgressBar totalProgressBar,
            JLabel stepName,
            JProgressBar stepProgressBar) {
        
        JPanel background = new JPanel();
        background.setLayout(new BoxLayout(background, BoxLayout.Y_AXIS));
        background.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
        frame.add(background);
        
        // Add build progress section.
        JPanel buildProgressPanel = new JPanel();
        buildProgressPanel.setLayout(new BoxLayout(buildProgressPanel, BoxLayout.Y_AXIS));
        buildProgressPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Build Progress"), BorderFactory.createEmptyBorder(5, 10, 10, 10)));
        background.add(buildProgressPanel);
        
        // Add the total progress bar.
        buildProgressPanel.add(new JLabel("Building dictionaries..."));
        buildProgressPanel.add(totalProgressBar);
        totalProgressBar.setValue(0);
        totalProgressBar.setStringPainted(true);
        
        // Add padding between the two progress bars.
        buildProgressPanel.add(MyUtilities.SWING_UTILITIES.createPadding(5, SwingConstants.VERTICAL));
        
        // Add the step progress bar.
        buildProgressPanel.add(stepName);
        buildProgressPanel.add(stepProgressBar);
        stepProgressBar.setValue(0);
        stepProgressBar.setStringPainted(true);
        
        // Resize the Width of the window.
        Dimension d = buildProgressPanel.getPreferredSize();
        d.setSize(350, d.getHeight());
        buildProgressPanel.setPreferredSize(d);
        
        // Arrange the components inside the window
        frame.pack();
        frame.setResizable(false); 
        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension windowSize = frame.getSize();
        frame.setLocation((int)(screenSize.getWidth()/2 - windowSize.getWidth()/2),
                          (int)(screenSize.getHeight()/2 - windowSize.getHeight()/2));
    }
    
    public static void buildDataCenterWindow(JFrame frame,
            JPanel canvasPanel,
            JTextArea fileDetailsTextArea,
            JTextField[] textFields, // homeFolder, fileSelected
            JList<File>[] lists, // currentDirectory, fileViewer
            JButton[] buttons, // browse, consolidate, back, home, calculate, pause/play, stop, special
            JSplitPane splitPane,
            JLabel[] labels, // current, max
            JProgressBar playbackProgressBar) {

        JPanel background = new JPanel();
        background.setLayout(new BoxLayout(background, BoxLayout.X_AXIS));
        frame.add(background);
        
        // Add split pane.
        background.add(splitPane);
        splitPane.setEnabled(false);
        
        // Top panel (file selection, data manipulation buttons, file previewer)
        JPanel topPanelSetBackground = new JPanel();
        topPanelSetBackground.setLayout(new BoxLayout(topPanelSetBackground, BoxLayout.X_AXIS));
        topPanelSetBackground.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        splitPane.setTopComponent(topPanelSetBackground);
        
        // Left side, files selection and options
        JPanel fileOptionsBackground = new JPanel();
        fileOptionsBackground.setLayout(new BoxLayout(fileOptionsBackground, BoxLayout.Y_AXIS));
        fileOptionsBackground.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("File Options"), BorderFactory.createEmptyBorder(5, 10, 10, 10)));
        topPanelSetBackground.add(fileOptionsBackground);
        
        // Data folder browser
        JPanel dataFolderChooserPanel = new JPanel();
        dataFolderChooserPanel.setLayout(new BoxLayout(dataFolderChooserPanel, BoxLayout.X_AXIS));
        dataFolderChooserPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Home Folder"), BorderFactory.createEmptyBorder(5, 10, 10, 10)));
        fileOptionsBackground.add(dataFolderChooserPanel);
        
        JLabel selectDataFolderLabel = new JLabel("Home: ");
        dataFolderChooserPanel.add(selectDataFolderLabel);
        
        dataFolderChooserPanel.add(MyUtilities.SWING_UTILITIES.createPadding(0, SwingConstants.HORIZONTAL));
        
        textFields[0].setEditable(false);
        //textFields[0].setHighlighter(null);
        dataFolderChooserPanel.add(textFields[0]);
        
        dataFolderChooserPanel.add(MyUtilities.SWING_UTILITIES.createPadding(10, SwingConstants.HORIZONTAL));
        
        dataFolderChooserPanel.add(buttons[0]);
        
        // Formatter buttons
        JPanel dataFormatButtons = new JPanel();
        dataFormatButtons.setLayout(new BoxLayout(dataFormatButtons, BoxLayout.X_AXIS));
        fileOptionsBackground.add(dataFormatButtons);
        
        JPanel consolidateButtonPanel = new JPanel();
        consolidateButtonPanel.setLayout(new BoxLayout(consolidateButtonPanel, BoxLayout.Y_AXIS));
        consolidateButtonPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Consolidate Data"), BorderFactory.createEmptyBorder(5, 10, 10, 10)));
        dataFormatButtons.add(consolidateButtonPanel);
        
        JTextArea consolidateButtonInfo = new JTextArea("Consolidate the data from all of the subjects of the home folder.");
        consolidateButtonInfo.setEditable(false);
        consolidateButtonInfo.setHighlighter(null);
        consolidateButtonInfo.setBackground(UIManager.getColor("Panel.background"));
        consolidateButtonInfo.setWrapStyleWord(true);
        consolidateButtonInfo.setLineWrap(true);
        consolidateButtonPanel.add(consolidateButtonInfo);
        
        consolidateButtonPanel.add(MyUtilities.SWING_UTILITIES.createPadding(5, SwingConstants.VERTICAL));
        JPanel consolidateButtonCenter = new JPanel(new FlowLayout(FlowLayout.CENTER));
        consolidateButtonCenter.add(buttons[1]);
        consolidateButtonPanel.add(consolidateButtonCenter);
        
        consolidateButtonPanel.add(MyUtilities.SWING_UTILITIES.createPadding(0, SwingConstants.VERTICAL));
        
        Dimension d = consolidateButtonPanel.getPreferredSize();
        d.setSize(300, d.getHeight());
        consolidateButtonPanel.setPreferredSize(d);
        
        dataFormatButtons.add(MyUtilities.SWING_UTILITIES.createPadding(10, SwingConstants.HORIZONTAL));
        
        JPanel calculateButtonPanel = new JPanel();
        calculateButtonPanel.setLayout(new BoxLayout(calculateButtonPanel, BoxLayout.Y_AXIS));
        calculateButtonPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Calculate Data"), BorderFactory.createEmptyBorder(5, 10, 10, 10)));
        dataFormatButtons.add(calculateButtonPanel);
        
        JTextArea calculateButtonInfo = new JTextArea("Calculate all of the stastistical data for subjects in the home folder.");
        calculateButtonInfo.setEditable(false);
        calculateButtonInfo.setHighlighter(null);
        calculateButtonInfo.setBackground(UIManager.getColor("Panel.background"));
        calculateButtonInfo.setWrapStyleWord(true);
        calculateButtonInfo.setLineWrap(true);
        calculateButtonPanel.add(calculateButtonInfo);
        
        calculateButtonPanel.add(MyUtilities.SWING_UTILITIES.createPadding(5, SwingConstants.VERTICAL));
        JPanel calculateButtonCenter = new JPanel(new FlowLayout(FlowLayout.CENTER));
        calculateButtonCenter.add(buttons[4]);
        calculateButtonCenter.add(buttons[7]);
        calculateButtonPanel.add(calculateButtonCenter);
        
        calculateButtonPanel.add(MyUtilities.SWING_UTILITIES.createPadding(0, SwingConstants.VERTICAL));
        
        calculateButtonPanel.setPreferredSize(d);
        
        // File selector
        JPanel fileSelectorPanel = new JPanel();
        fileSelectorPanel.setLayout(new BoxLayout(fileSelectorPanel, BoxLayout.X_AXIS));
        fileSelectorPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("File Selector"), BorderFactory.createEmptyBorder(5, 10, 10, 10)));
        fileOptionsBackground.add(fileSelectorPanel);
        
        // File navigation tools
        JPanel navigationToolsPanel = new JPanel();
        navigationToolsPanel.setLayout(new BoxLayout(navigationToolsPanel, BoxLayout.Y_AXIS));
        fileSelectorPanel.add(navigationToolsPanel);
        
        navigationToolsPanel.add(buttons[2]);
        navigationToolsPanel.add(buttons[3]);
        
        // Add the current directory to a scroll pane.
        lists[0].setSelectionMode (ListSelectionModel.SINGLE_SELECTION);
        JScrollPane currentDirectoryScrollBar = new JScrollPane(lists[0], JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        currentDirectoryScrollBar.getVerticalScrollBar().setUnitIncrement(16);
        currentDirectoryScrollBar.setPreferredSize(new Dimension(115, 120));
        fileSelectorPanel.add(currentDirectoryScrollBar);
        
        // Add the directory contents to a scroll pane.
        lists[1].setSelectionMode (ListSelectionModel.SINGLE_SELECTION);
        //lists[1].setVisibleRowCount(5);
        JScrollPane directoryContentsScrollBar = new JScrollPane(lists[1], JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        directoryContentsScrollBar.getVerticalScrollBar().setUnitIncrement(16);
        directoryContentsScrollBar.setPreferredSize(new Dimension(330, 120));
        fileSelectorPanel.add(directoryContentsScrollBar);
        
        // File preview
        JPanel filePreviewerPanel = new JPanel();
        filePreviewerPanel.setLayout(new BoxLayout(filePreviewerPanel, BoxLayout.Y_AXIS));
        filePreviewerPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("File Preview"), BorderFactory.createEmptyBorder(5, 10, 10, 10)));
        topPanelSetBackground.add(filePreviewerPanel);
        
        //fileDetailsTextArea.setFont(new Font("TimesNewRoman", Font.BOLD, 12));
        fileDetailsTextArea.setEditable(false);
        fileDetailsTextArea.setBorder(new EmptyBorder(3,3,3,3));
        ((DefaultCaret) fileDetailsTextArea.getCaret()).setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
        //fileDetailsTextArea.setWrapStyleWord(true);
        //fileDetailsTextArea.setLineWrap(true);
        
        JScrollPane filePreviewScrollBar = new JScrollPane(fileDetailsTextArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        filePreviewScrollBar.getVerticalScrollBar().setUnitIncrement(16);
        filePreviewScrollBar.setPreferredSize(new Dimension(450, fileOptionsBackground.getPreferredSize().height - 75));
        filePreviewerPanel.add(filePreviewScrollBar);
        
        JPanel previewDetailsPanel = new JPanel();
        filePreviewerPanel.add(previewDetailsPanel);
        
        JLabel previewedFileLabel = new JLabel("File: ");
        previewDetailsPanel.add(previewedFileLabel);
        
        textFields[1].setEditable(false);
        textFields[1].setPreferredSize(new Dimension(425, textFields[1].getPreferredSize().height));
        //textFields[1].setHighlighter(null);
        previewDetailsPanel.add(textFields[1]);
        
        // Bottom panel (playback buttons and preview).
        JPanel bottomPanelSet = new JPanel();
        bottomPanelSet.setLayout(new BoxLayout(bottomPanelSet, BoxLayout.Y_AXIS));
        bottomPanelSet.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        splitPane.setBottomComponent(bottomPanelSet);
        
        JPanel playbackPanel = new JPanel();
        playbackPanel.setLayout(new BoxLayout(playbackPanel, BoxLayout.Y_AXIS));
        playbackPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Playback"), BorderFactory.createEmptyBorder(5, 10, 10, 10)));
        bottomPanelSet.add(playbackPanel);
        
        // Add the keyboard canvas.
        playbackPanel.add(canvasPanel);
        
        // Add the progress bar.
        playbackProgressBar.setValue(0);
        playbackProgressBar.setStringPainted(true);
        playbackPanel.add(playbackProgressBar);
        
        // Add the playback controls here.
        // play/pause, stop, progress bar, time
        JPanel playbackControlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        playbackControlPanel.add(buttons[5]);
        playbackControlPanel.add(buttons[6]);
        playbackPanel.add(playbackControlPanel);
        
        playbackControlPanel.add(MyUtilities.SWING_UTILITIES.createPadding(20, SwingConstants.HORIZONTAL));
        
        JPanel playbackTimePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        playbackTimePanel.add(labels[0]);
        playbackTimePanel.add(new JLabel("/"));
        playbackTimePanel.add(labels[1]);
        playbackControlPanel.add(playbackTimePanel);
        
        // Arrange the components inside the window
        frame.pack();
        frame.setResizable(false); 
        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension windowSize = frame.getSize();
        frame.setLocation((int)(screenSize.getWidth()/2 - windowSize.getWidth()/2),
                          (int)(screenSize.getHeight()/2 - windowSize.getHeight()/2));
    }
}

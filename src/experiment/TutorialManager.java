package experiment;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.JButton;
import javax.swing.JRootPane;

import enums.FileExt;
import enums.FileName;
import enums.FilePath;

public class TutorialManager {
    private final String TUTORIAL_WORD_PATH = FilePath.DOCS.getPath() + FileName.TUTORIAL.getName() + FileExt.DICTIONARY.getExt();
    private final String[] INSTRUCTIONS = {"This quick tutorial will go over what you need to know.\n\n"
            + "Press the \"Next\" button below to continue.",
            "Once the experiment begins, a random word will appear above the keyboard.\n\n"
            + "It is your job to type in the word you see at a rate comfortable to you.\n\n"
            + "Press the \"Next\" button below to continue.",
            "You will use the specified keyboard to type the seen word.\n\n"
            + "Any correctly typed letters will show in GREEN while errors will show in RED.\n\n"
            + "Press the \"Next\" button below to continue.",
            "Once a word has been completed, you will press the ENTER key to move to the next word.\n\n"
            + "The topmost box will flash GREEN if the word you entered was correct. It will flash RED if the word was incorrect.\n\n"
            + "Press the \"Next\" button below to continue.",
            "After you've gone through the randomly selected words, the experiment will be complete. The window will automatically closed.\n\n"
            + "Press the \"Next\" button below to continue.",
            "Please, take a moment to observe the example on the left and ask if you have any questions.\n\n"
            + "Press the \"Done\" button below to continue."};
    private final ReentrantLock tutorialLock = new ReentrantLock();
    private int step = 0;
    private boolean hasNext = false;
    private JButton stepButton;
    
    public TutorialManager() {
        stepButton = new JButton("Next");
        stepButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tutorialLock.lock();
                try {
                    step++;
                    hasNext = true;
                    if(step == INSTRUCTIONS.length - 1) {
                        stepButton.setText("Done");
                    }
                    JRootPane rootPane = stepButton.getRootPane();
                    if(rootPane != null) {
                        rootPane.requestFocusInWindow();
                    }
                } finally {
                    tutorialLock.unlock();
                }
            }
        });
    }
    
    public boolean hasNext() {
        tutorialLock.lock();
        try {
            return hasNext;
        } finally {
            // Consume next event
            hasNext = false;
            tutorialLock.unlock();
        }
    }
    
    public boolean isValid() {
        tutorialLock.lock();
        try {
            return step < INSTRUCTIONS.length;
        } finally {
            tutorialLock.unlock();
        }
    }
    
    public String getPath() {
        return TUTORIAL_WORD_PATH;
    }

    public String getText() {
        return INSTRUCTIONS[step];
    }

    public JButton getComponent() {
        return stepButton;
    }
}

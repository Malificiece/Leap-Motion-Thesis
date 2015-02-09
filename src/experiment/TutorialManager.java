package experiment;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.JButton;
import javax.swing.JRootPane;

public class TutorialManager {
    private final String[] INSTRUCTIONS = {"<font size=+1>This quick tutorial will go over what you need to know.<br><br></font>"
            + "<font size=+1>Press the <b>NEXT</b> button below to continue.</font>",
            "<font size=+1>Once the experiment begins, a random word will appear above the keyboard.<br><br></font>"
            + "<font size=+1>It is your job to type in the word you see at a rate comfortable to you.<br><br></font>"
            + "<font size=+1>Press the <b>NEXT</b> button below to continue.</font>",
            "<font size=+1>You will use the specified keyboard to type the seen word.<br><br></font>"
            + "<font size=+1>Any correctly typed letters will show in <font color=green><b>GREEN</b></font> while errors will show in <font color=red><b>RED</b></font>.<br><br></font>"
            + "<font size=+1>Press the <b>NEXT</b> button below to continue.</font>",
            "<font size=+1>Once a word has been completed, you will press the <b>ENTER</b> key to move to the next word.<br><br></font>"
            + "<font size=+1>The topmost box will flash <font color=green><b>GREEN</b></font> if the word you entered was correct. It will flash <font color=red><b>RED</b></font> if the word was incorrect.<br><br></font>"
            + "<font size=+1>Press the <b>NEXT</b> button below to continue.</font>",
            "<font size=+1>After you've gone through the randomly selected words, the experiment will be complete. The window will be automatically closed.<br><br></font>"
            + "<font size=+1>Press the <b>NEXT</b> button below to continue.</font>",
            "<font size=+1>Please, take a moment to observe the example on the left and feel free to ask questions if you have any.<br><br></font>"
            + "<font size=+1>Press the <b>DONE</b> button below to continue.</font>"};
    // TODO:
    // Implement different tutorial description for each keyboard
    private final ReentrantLock TUTORIAL_LOCK = new ReentrantLock();
    private int step = 0;
    private boolean hasNext = false;
    private JButton stepButton;
    
    public TutorialManager() {
        stepButton = new JButton("Next");
        stepButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TUTORIAL_LOCK.lock();
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
                    TUTORIAL_LOCK.unlock();
                }
            }
        });
    }
    
    public boolean hasNext() {
        TUTORIAL_LOCK.lock();
        try {
            return hasNext;
        } finally {
            // Consume next event
            hasNext = false;
            TUTORIAL_LOCK.unlock();
        }
    }
    
    public boolean isValid() {
        TUTORIAL_LOCK.lock();
        try {
            return step < INSTRUCTIONS.length;
        } finally {
            TUTORIAL_LOCK.unlock();
        }
    }

    public String getText() {
        TUTORIAL_LOCK.lock();
        try {
            return INSTRUCTIONS[step];
        } finally {
            TUTORIAL_LOCK.unlock();
        }
    }

    public JButton getComponent() {
        return stepButton;
    }
}

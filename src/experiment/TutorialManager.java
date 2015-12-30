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

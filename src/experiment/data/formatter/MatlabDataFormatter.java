package experiment.data.formatter;

import ui.WindowController;

public class MatlabDataFormatter extends WindowController {
    
    // for now force run on anything in the recorded data folder
    // later change to choose what we want to run the formatter on
    
    
    
    public void update() {
        // order for each one needs to be corrected to probably be in the same order so that we can compare same words too.
    }

    @Override
    public void enable() {
        frame.setVisible(true);
        frame.requestFocusInWindow();
        isEnabled = true;
    }

    @Override
    protected void disable() {
        frame.setVisible(false);
        isEnabled = false;
        frame.dispose();
    }
}

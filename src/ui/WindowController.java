package ui;

import javax.swing.JFrame;

public abstract class WindowController {
    protected JFrame frame;
    protected boolean isEnabled = false;
        
    public boolean isEnabled() {
        return isEnabled;
    }
    
    public abstract void enable();
    protected abstract void disable();
}

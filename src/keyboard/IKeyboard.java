package keyboard;

import java.util.ArrayList;
import java.util.Map;

public abstract class IKeyboard {
    // TODO: list of things we need from the keyboard as an outside party
    // 1 - render the keyboard
    // 2 - get the attributes of the keyboard (size, etc)
    // 3 - get/set the settings of the keyboard (settings I can change)
    // 4 - get/set the renderables of the keyboard (image, key colors, leap plane etc)
    // 5 - give it key pressed events (to render them properly) --- and record specialized data for the specific type of keyboard
    // 6 - must be capable of creating it's own keyboard events using the robot if not the default (This will require a self sustaining update function possibly)
    // 7 - 
    private ArrayList<KeyboardObserver> observers = new ArrayList<KeyboardObserver>();
    private ArrayList<KeyboardSetting> keyboardSettings = new ArrayList<KeyboardSetting>();
    private ArrayList<KeyboardAttribute> keyboardAttributes = new ArrayList<KeyboardAttribute>();
    private ArrayList<KeyboardRenderable> keyboardRenderables = new ArrayList<KeyboardRenderable>();
    
    public abstract void render();
    public abstract void update();
    
    public ArrayList<KeyboardSetting> getAllSettings() {
        return keyboardSettings;
    }
    public ArrayList<KeyboardAttribute> getAllAttributes() {
        return keyboardAttributes;
    }
    public  ArrayList<KeyboardRenderable> getAllRenderables() {
        return keyboardRenderables;
    }
    
    public void registerObserver(KeyboardObserver observer) {
        observers.add(observer);
    }

    protected void notifyListeners() {
        for(KeyboardObserver observer : observers) {
            observer.keyboardEventObserved('a');
        }
    }
}

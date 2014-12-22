package ui;

import keyboard.KeyboardSettings;

public interface SaveSettingsObserver {
    public void saveSettingsEventObserved(KeyboardSettings settings);
}

package keyboard;

import java.util.ArrayList;
import java.util.TreeMap;

public abstract class KeyboardSettings {
    private TreeMap<String, KeyboardSetting> keyboardSettings = new TreeMap<String, KeyboardSetting>();
    
    public void addSetting(KeyboardSetting setting) {
        keyboardSettings.put(setting.getName(), setting);
    }
    
    public KeyboardSetting getSettingByName(String name) {
        return keyboardSettings.get(name);
    }
    
    public Number getValueByName(String name) {
        return keyboardSettings.get(name).getValue();
    }
    
    public ArrayList<KeyboardSetting> getAllSettings() {
        return new ArrayList<KeyboardSetting>(keyboardSettings.values());
    }
}

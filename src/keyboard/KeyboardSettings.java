package keyboard;

import java.util.ArrayList;
import java.util.TreeMap;

import enums.Setting;

public abstract class KeyboardSettings {
    private TreeMap<Setting, KeyboardSetting> keyboardSettings = new TreeMap<Setting, KeyboardSetting>();
    
    public void addSetting(KeyboardSetting setting) {
        keyboardSettings.put(setting.getType(), setting);
    }
    
    public KeyboardSetting getSetting(Setting setting) {
        return keyboardSettings.get(setting);
    }
    
    public Number getSettingValue(Setting setting) {
        KeyboardSetting ks = keyboardSettings.get(setting);
        if(ks != null) {
            return ks.getValue();
        }
        return null;
    }
    
    public ArrayList<KeyboardSetting> getAllSettings() {
        return new ArrayList<KeyboardSetting>(keyboardSettings.values());
    }
}

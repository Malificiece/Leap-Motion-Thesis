package keyboard.leap;

import enums.Setting;
import keyboard.KeyboardSetting;
import keyboard.KeyboardSettings;

public class LeapSettings extends KeyboardSettings {
    LeapSettings(LeapKeyboard keyboard) {
        // For now this loop is okay. But this need to change if other keyboards require settings that can change.
        for(Setting setting: Setting.values()) {
            this.addSetting(new KeyboardSetting(keyboard, setting.toString(), setting.getMinVal(), setting.getDefVal(), setting.getMaxVal(), setting.getDecimalPrecision()));
        }
    }
}

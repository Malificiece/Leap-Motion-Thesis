package keyboard.leap;

import enums.DecimalPrecision;
import enums.SettingName;
import keyboard.KeyboardSetting;
import keyboard.KeyboardSettings;

public class LeapSettings extends KeyboardSettings {
    // possibly put leap defaults there
    // need to be able to read them from a file where I have fine tuned values saved
    // need to also be able to have a setting 'save' the current selection as the new default to a file
    
    LeapSettings(LeapKeyboard keyboard) {
        this.addSetting(new KeyboardSetting(SettingName.GESTURE_CIRCLE_MIN_RADIUS.toString(), 5.0, 0.1, 25.0, DecimalPrecision.ONE));
        this.addSetting(new KeyboardSetting(SettingName.GESTURE_CIRCLE_MIN_ARC.toString(), 1.5, 0.1, 7.5, DecimalPrecision.ONE));
        this.addSetting(new KeyboardSetting(SettingName.GESTURE_SWIPE_MIN_LENGTH.toString(), 150, 1, 750, DecimalPrecision.DEFAULT));
        this.addSetting(new KeyboardSetting(SettingName.GESTURE_SWIPE_MIN_VELOCITY.toString(), 1000, 1, 5000, DecimalPrecision.DEFAULT));
        this.addSetting(new KeyboardSetting(SettingName.GESTURE_KEYTAP_MIN_DOWN_VELOCITY.toString(), 50, 1, 250, DecimalPrecision.DEFAULT));
        this.addSetting(new KeyboardSetting(SettingName.GESTURE_KEYTAP_MIN_HISTORY_SECONDS.toString(), 0.1, 0.1, 1.0, DecimalPrecision.ONE));
        this.addSetting(new KeyboardSetting(SettingName.GESTURE_KEYTAP_MIN_DISTANCE.toString(), 3.0, 0.1, 15.0, DecimalPrecision.ONE));
        this.addSetting(new KeyboardSetting(SettingName.GESTURE_SCREENTAP_MIN_FORWARD_VELOCITY.toString(), 50, 1, 250, DecimalPrecision.DEFAULT));
        this.addSetting(new KeyboardSetting(SettingName.GESTURE_SCREENTAP_MIN_HISTORY_SECONDS.toString(), 0.1, 0.1, 1.0, DecimalPrecision.ONE));
        this.addSetting(new KeyboardSetting(SettingName.GESTURE_SCREENTAP_MIN_DISTANCE.toString(), 5.0, 0.1, 25.0, DecimalPrecision.ONE));
    }
}

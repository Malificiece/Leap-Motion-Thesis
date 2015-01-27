package keyboard.tablet;

import enums.Gesture;
import enums.Setting;
import keyboard.KeyboardSetting;
import keyboard.KeyboardSettings;

public class TabletSettings extends KeyboardSettings {

    TabletSettings(TabletKeyboard keyboard) {
        if(Gesture.ENABLED) {
            this.addSetting(new KeyboardSetting(keyboard, Setting.GESTURE_CIRCLE_MIN_ARC));
            this.addSetting(new KeyboardSetting(keyboard, Setting.GESTURE_CIRCLE_MIN_RADIUS));
            this.addSetting(new KeyboardSetting(keyboard, Setting.GESTURE_SWIPE_MIN_LENGTH));
            this.addSetting(new KeyboardSetting(keyboard, Setting.GESTURE_SWIPE_MIN_VELOCITY));
        }
    }
}

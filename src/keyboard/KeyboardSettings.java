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

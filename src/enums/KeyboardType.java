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

package enums;

public enum KeyboardType {
    DISABLED("DISABLED", "DISABLED"),
    STANDARD("Standard Keyboard", FileName.STANDARD.getName()),
    CONTROLLER_CONSOLE("Controller Console Keyboard", FileName.CONTROLLER_CONSOLE.getName()),
    CONTROLLER_GESTURE("Controller Word-Gesture Keyboard", FileName.CONTROLLER_GESTURE.getName()),
    TABLET("Tablet Keyboard", FileName.TABLET.getName()),
    LEAP_SURFACE("Leap Surface Keyboard", FileName.LEAP_SURFACE.getName()),
    LEAP_AIR_STATIC("Leap Air Static Keyboard", FileName.LEAP_AIR_STATIC.getName()),
    LEAP_AIR_PINCH("Leap Air Pinch Keyboard", FileName.LEAP_AIR_PINCH.getName()),
    LEAP_AIR_DYNAMIC("Leap Air Dynamic Keyboard", FileName.LEAP_AIR_DYNAMIC.getName()),
    LEAP_AIR_BIMODAL("Leap Air Bimodal Keyboard", FileName.LEAP_AIR_BIMODAL.getName()),
    LEAP_AIR_AUGMENTED("Leap Air Augmented Keyboard", FileName.LEAP_AIR_AUGMENTED.getName());
    
    private static final String LEAP = "LEAP";
    private final String keyboardName;
    private final String keyboardFileName;
    
    private KeyboardType(String keyboardName, String keyboardFileName) {
        this.keyboardName = keyboardName;
        this.keyboardFileName = keyboardFileName;
    }
    
    public String getName() {
        return keyboardName;
    }
    
    public String getFileName() {
        return keyboardFileName;
    }
    
    public boolean isLeap() {
        return(this.name().contains(LEAP));
    }
    
    public static KeyboardType getByName(String keyboardName) {
        for(KeyboardType keyboardType: values()) {
            if(keyboardType.getName().equalsIgnoreCase(keyboardName)) return keyboardType;
            if(keyboardType.getFileName().equalsIgnoreCase(keyboardName)) return keyboardType;
            if(keyboardName != null && keyboardName.contains(keyboardType.getName())) return keyboardType;
            if(keyboardName != null && keyboardName.contains(keyboardType.getFileName())) return keyboardType;
        }
        return null;
    }
}

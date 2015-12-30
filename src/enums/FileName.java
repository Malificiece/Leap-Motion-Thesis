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

public enum FileName {
    STANDARD("standard"),
    LEAP_SURFACE("leapSurface"),
    LEAP_AIR_STATIC("leapAirStatic"),
    LEAP_AIR_PINCH("leapAirPinch"),
    LEAP_AIR_DYNAMIC("leapAirDynamic"),
    LEAP_AIR_AUGMENTED("leapAirAugmented"),
    LEAP_AIR_BIMODAL("leapAirBimodal"),
    CONTROLLER_CONSOLE("controllerConsole"),
    CONTROLLER_GESTURE("controllerGesture"),
    TABLET("tablet"),
    SWIPE("swipe"),
    CONTROLLER("controller"),
    EXIT_SURVEY("exitSurvey"),
    SUBJECT_MERGED_DATA("mergedData"),
    CONSOLIDATED_EXPERIMENT_DATA("consolidatedExperimentData"),
    KEYBOARD_IMAGE("_keyboard"),
    KEYBOARD_IMAGE_UPPER("_keyboard_upper"),
    DICTIONARY("English-US"),
    DICTIONARY_FILTER("Terms-to-Block"),
    TEMPORARY("temporary_"),
    TUTORIAL("tutorial"),
    PLAY("play"),
    PAUSE("pause"),
    STOP("stop"),
    HOME("home"),
    UP_FOLDER("up_folder");

    private final String name;
    
    private FileName(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
}

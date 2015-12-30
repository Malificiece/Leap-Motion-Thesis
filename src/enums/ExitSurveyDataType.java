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

public enum ExitSurveyDataType {
    SUBJECT_ID,
    AGE,
    GENDER,
    MAJOR,
    HAS_PERSONAL_COMPUTER,
    HOURS_PER_WEEK_ON_COMPUTER,
    HAS_PREVIOUS_GESTURE_DEVICE_EXPERIENCE,
    PREVIOUS_GESTURE_DEVICE_DESCRIPTION,
    HAS_PREVIOUS_TOUCH_DEVICE_EXPERIENCE,
    PREVIOUS_TOUCH_DEVICE_DESCRIPTION,
    HAS_PREVIOUS_SWIPE_DEVICE_EXPERIENCE,
    PREVIOUS_SWIPE_DEVICE_DESCRIPTION,
    HAS_PHYSICAL_IMPAIRMENT,
    PHYSICAL_INPAIRMENT_DESCRIPTION,
    HANDEDNESS,
    PREFERED_HANDEDNESS_FOR_EXPERIMENT,
    DISCOMFORT_LEVEL,
    FATIGUE_LEVEL,
    DIFFICULTY_LEVEL,
    PREFERENCE_RANKING;
    
    public static ExitSurveyDataType getByName(String typeName) {
        for(ExitSurveyDataType esdt: values()) {
            if(esdt.name().equalsIgnoreCase(typeName)) return esdt;
            if(ExitSurveyDataType.isLikert(esdt) || ExitSurveyDataType.isRanking(esdt)) {
                if(typeName != null && typeName.contains(esdt.name())) return esdt;
            }
        }
        return null;
    }
    
    public static boolean isLikert(ExitSurveyDataType esdt) {
        if(esdt == DISCOMFORT_LEVEL ||
                esdt == FATIGUE_LEVEL ||
                esdt == DIFFICULTY_LEVEL) {
            return true;
        }
        return false;
    }
    
    public static boolean isRanking(ExitSurveyDataType esdt) {
        if(esdt == PREFERENCE_RANKING) {
            return true;
        }
        return false;
    }
}

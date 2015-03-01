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
    _DISCOMFORT_LEVEL,
    _FATIGUE_LEVEL,
    _DIFFICULTY_LEVEL,
    _PREFERENCE_RANKING;
    
    /*public static ExitSurveyDataType getByName(String typeName) {
        for(ExitSurveyDataType esdt: values()) {
            if(esdt.name().equalsIgnoreCase(typeName)) return esdt;
        }
        for(ExitSurveyDataType esdt: values()) {
            if(esdt.name().contains(typeName)) return esdt;
        }
        return null;
    }*/
}

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
            if(typeName != null && typeName.contains(esdt.name())) return esdt;
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

package enums;

public enum StatisticDataType {
    // TODO: Make a single MATLAB or R  script that takes all of these things and can
    // output all of the statistical analysis a that is needed and be saved. I just
    // want to be able to run this and then run the script directly in matlab with no problem at all.
    KEYBOARD_TYPE,
    KEYBOARD_ORDER,
    SUBJECT_ORDER,
    WORD_ORDER,
    // TODO: decide between FULL, FIRST_TOUCH, TOUCH_ONLY etc and consider shortest
    NUMBER_OF_TIMES_PLANE_BREACHED,
    DISTANCE_TRAVELED_TOUCH_ONLY,
    DISTANCE_TRAVELED_TOUCH_ONLY_SHORTEST,
    TIME_DURATION_TOUCH_ONLY,
    TIME_DURATION_TOUCH_ONLY_SHORTEST,
    REACTION_TIME_FIRST_PRESSED,
    REACTION_TIME_FIRST_TOUCH,
    AVERAGE_REACTION_TIME_TO_ERRORS, // This should be the average per word that way we have one reaction time per word rather than many.
    AVERAGE_PIXEL_VELOCITY,
    TEXT_ENTRY_RATE_WPM,
    TEXT_ENTRY_RATE_MODIFIED_WPM_SHORTEST,
    TEXT_ENTRY_RATE_MODIFIED_WPM_VULTURE,
    TEXT_ENTRY_RATE_MODIFIED_WPM_SHORTEST_VULTURE,
    ERROR_RATE_KSPC,
    ERROR_RATE_MODIFIED_KSPC_SHORTEST,
    //ERROR_RATE_MODIFIED_KSPC_BACKSPACE,
    ERROR_RATE_MODIFIED_MSD_SHORTEST,
    ERROR_RATE_MODIFIED_MSD_BACKSPACE,
    ERROR_RATE_MWD,
    ERROR_RATE_MODIFIED_MWD_SHORTEST,
    ERROR_RATE_MODIFIED_MWD_BACKSPACE,
    TOTAL_ERROR_RATE,
    TOTAL_ERROR_RATE_MODIFIED_SHORTEST,
    TOTAL_ERROR_RATE_MODIFIED_BACKSPACE,
    FRECHET_DISTANCE_TOUCH_ONLY,
    FRECHET_DISTANCE_TOUCH_ONLY_MODIFIED_SHORTEST, // same set as the modified MSD shortest
    FRECHET_DISTANCE_TOUCH_ONLY_MODIFIED_BACKSPACE, // same set as the modified MSD with backspace
    PRACTICE_WORDS_PER_INPUT,
    RANKINGS,
    LIKERT_DISCOMFORT,
    LIKERT_FATIGUE,
    LIKERT_DIFFICULTY;
    
    public static StatisticDataType getByName(String typeName) {
        for(StatisticDataType dataType: values()) {
            if(dataType.name().equalsIgnoreCase(typeName)) return dataType;
            //if(typeName != null && typeName.contains(dataType.name())) return dataType;
        }
        return null;
    }
}

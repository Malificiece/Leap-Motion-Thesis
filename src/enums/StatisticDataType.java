package enums;

public enum StatisticDataType {
    // TODO: Make a single MATLAB or R  script that takes all of these things and can
    // output all of the statistical analysis a that is needed and be saved. I just
    // want to be able to run this and then run the script directly in matlab with no problem at all.
    KEYBOARD_TYPE,
    WORD_ORDER,
    // TODO: decide between FULL, FIRST_TOUCH, TOUCH_ONLY etc and consider shortest
    NUMBER_OF_TIMES_PLANE_BREACHED,
    DISTANCE_TRAVELED_FIRST_TOUCH,
    DISTANCE_TRAVELED_FIRST_TOUCH_SHORTEST,
    TIME_DURATION_FIRST_TOUCH,
    TIME_DURATION_FIRST_TOUCH_SHORTEST,
    REACTION_TIME_FIRST_PRESSED,
    REACTION_TIME_FIRST_TOUCH,
    AVERAGE_REACTION_TIME_TO_ERRORS, // This should be the average per word that way we have one reaction time per word rather than many.
    AVERAGE_TOUCH_VELOCITY,
    TEXT_ENTRY_RATE_WPM,
    TEXT_ENTRY_RATE_MODIFIED_WPM_SHORTEST,
    TEXT_ENTRY_RATE_MODIFIED_WPM_VULTURE,
    TEXT_ENTRY_RATE_MODIFIED_WPM_SHORTEST_VULTURE,
    ERROR_RATE_KSPC,
    ERROR_RATE_MODIFIED_MSD_SHORTEST,
    ERROR_RATE_MODIFIED_MSD_WITH_BACKSPACE,
    FRECHET_DISTANCE_TOUCH_ONLY,
    FRECHET_DISTANCE_TOUCH_ONLY_MODIFIED_SHORTEST, // same set as the modified MSD shortest
    FRECHET_DISTANCE_TOUCH_ONLY_MODIFIED_WITH_BACKSPACE, // same set as the modified MSD with backspace
    PRACTICE_WORDS_PER_INPUT,
    RANKINGS,
    LIKERT_DISCOMFORT,
    LIKERT_FATIGUE,
    LIKERT_DIFFICULTY;
}

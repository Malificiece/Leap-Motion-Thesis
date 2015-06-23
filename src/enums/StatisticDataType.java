package enums;

public enum StatisticDataType {
    // TODO: Make a single MATLAB or R  script that takes all of these things and can
    // output all of the statistical analysis a that is needed and be saved. I just
    // want to be able to run this and then run the script directly in matlab with no problem at all.
    KEYBOARD_TYPE,
    TOTAL_DISTANCE_TRAVELED_PER_WORD,
    REACTION_TIME_PER_WORD,
    AVERAGE_REACTION_TIME_TO_ERRORS_PER_WORD, // This should be the average per word that way we have one reaction time per word rather than many.
    AVERAGE_VELOCITY_PER_WORD,
    TIME_DURATION_PER_WORD,
    TEXT_ENTRY_RATE_WPM,
    ERROR_RATE_KSPC,
    ERROR_RATE_MODIFIED_MSD,
    FRECHET_DISTANCE,
    FRECHET_DISTANCE_MODIFIED, // same set as the modified MSD
    PRACTICE_WORDS_PER_INPUT,
    RANKINGS,
    LIKERT_DISCOMFORT,
    LIKERT_FATIGUE,
    LIKERT_DIFFICULTY;
}

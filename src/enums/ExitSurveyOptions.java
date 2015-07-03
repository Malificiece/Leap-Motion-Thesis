package enums;

public enum ExitSurveyOptions {
    YES("Yes"),
    NO("No"),
    MALE_GENDER("Male"),
    FEMALE_GENDER("Female"),
    OTHER_GENDER("Other"),
    HOURS_0("0 to 1"),
    HOURS_1("1 to 5"),
    HOURS_2("6 to 10"),
    HOURS_3("11 to 20"),
    HOURS_4("21 to 30"),
    HOURS_5("31 to 40"),
    HOURS_6("41 to 50"),
    HOURS_7("More than 50"),
    RIGHT_HAND("Right"),
    LEFT_HAND("Left"),
    BOTH_HANDS("Both"),
    AMBIDEXTROUS("Ambidextrous"),
    STRONGLY_AGREE("strongly agree"),
    AGREE("agree"),
    NEUTRAL("neutral"),
    DISAGREE("disagree"),
    STRONGLY_DISAGREE("strongly disagree"),
    DID_NOT_USE("DID NOT USE");
    
    private final String description;
    
    private ExitSurveyOptions(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    public static ExitSurveyOptions getByDescriptionExact(String description) {
        for(ExitSurveyOptions eso: values()) {
            if(eso.getDescription().equalsIgnoreCase(description)) return eso;
        }
        return null;
    }
    
    public static ExitSurveyOptions getByDescriptionPartial(String description) {
        for(ExitSurveyOptions eso: values()) {
            if(description != null && description.contains(eso.getDescription())) return eso;
        }
        return null;
    }
    
    public static int getNumericValuebyDescription(String description) {
        ExitSurveyOptions surveyOption = getByDescriptionExact(description);
        if(surveyOption == null) return 0;
        switch(surveyOption) {
            case STRONGLY_AGREE:
                return 2;
            case AGREE:
                return 1;
            case NEUTRAL:
                return 0;
            case DISAGREE:
                return -1;
            case STRONGLY_DISAGREE:
                return -2;
            case YES:
                return 1;
            case NO:
                return 0;
            default:
                return 0;
        }
    }
}

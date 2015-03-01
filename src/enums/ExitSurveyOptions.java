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
    
    public static ExitSurveyOptions getByDescription(String description) {
        for(ExitSurveyOptions eso: values()) {
            if(eso.getDescription().contains(description)) return eso;
        }
        return null;
    }
}

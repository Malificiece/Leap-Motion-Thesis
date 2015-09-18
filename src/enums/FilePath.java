package enums;

public enum FilePath {
    ASSETS("./assets/"),
    CONFIG("./config/"),
    RECORDED_DATA("./recorded_data/"),
    STATISTIC_DATA("./statistic_data/"),
    DICTIONARY("./dictionary/");
    //STANDARD("standard/"),
    //LEAP("leap/"),
    //TABLET("tablet/"),
    //CONTROLLER("controller/");

    //private final static String WORKING_DIRECTORY = System.getProperty("user.dir");
    private final String path;
    
    private FilePath(String path) {
        this.path = path;
    }
    
    public String getPath() {
        //return WORKING_DIRECTORY + path;
    	return path;
    }
}

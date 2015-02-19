package enums;

public enum FilePath {
    ASSETS("./assets/"),
    CONFIG("./config/"),
    DATA("./data/"),
    DICTIONARY("./dictionary/");
    //STANDARD("standard/"),
    //LEAP("leap/"),
    //TABLET("tablet/"),
    //CONTROLLER("controller/");

    private final String path;
    
    private FilePath(String path) {
        this.path = path;
    }
    
    public String getPath() {
        return path;
    }
}

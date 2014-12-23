package enums;

public enum FilePath {
    ASSETS_PATH("./assets/"),
    CONFIG_PATH("./config/"),
    DATA_PATH("./data/"),
    STANDARD_PATH("standard/"),
    LEAP_PATH("leap/"),
    TABLET_PATH("tablet/"),
    CONTROLLER_PATH("controller/");

    private final String path;
    
    private FilePath(String path) {
        this.path = path;
    }
    
    public String getPath() {
        return path;
    }
}

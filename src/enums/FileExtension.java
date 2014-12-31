package enums;

public enum FileExtension {
    INI(".ini"),
    TXT(".txt"),
    PNG(".png"),
    FILE(".file"),
    DAT(".dat"),
    DB(".db");

    private final String extension;
    
    private FileExtension(String extension) {
        this.extension = extension;
    }
    
    public String getExtension() {
        return extension;
    }
}

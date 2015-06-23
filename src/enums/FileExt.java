package enums;

public enum FileExt {
    INI(".ini"),
    TXT(".txt"),
    PNG(".png"),
    FILE(".file"),
    DAT(".dat"),
    PLAYBACK(".playback"),
    DICTIONARY(".dictionary"),
    DATABASE(".db"),
    MATLAB(".m");

    private final String extension;
    
    private FileExt(String extension) {
        this.extension = extension;
    }
    
    public String getExt() {
        return extension;
    }
}

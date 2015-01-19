package utilities;

import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;

import enums.FileExt;
import enums.FileName;
import enums.FilePath;


public class MyUtilities {
    public static final SwingUtilities JAVA_SWING_UTILITIES = new SwingUtilities();
    public static final GraphicsUtilities OPEN_GL_UTILITIES = new GraphicsUtilities();
    public static final FileUtilities FILE_IO_UTILITIES = new FileUtilities();
    public static final MathUtilities MATH_UTILITILES = new MathUtilities();
    
    
    public static String generateSubjectID() {
        ArrayList<String> subjectIDList = new ArrayList<String>();
        try {
            subjectIDList = FILE_IO_UTILITIES.readListFromFile(FilePath.DATA.getPath(), FileName.SUBJECT_ID_LIST.getName() + FileExt.FILE.getExt());
        } catch (IOException e) {
            System.out.println("Unable to read existing ID's from file.");
            e.printStackTrace();
        }
        
        SecureRandom random = new SecureRandom();
        String subjectID = null;
        do {
            subjectID = new BigInteger(40, random).toString(32);
        } while(subjectIDList.contains(subjectID));
        
        return subjectID;
    }
}

package utilities;

import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.TreeSet;

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
    
    public static boolean checkForUniqueSubjectID(String subjectID) {
        ArrayList<String> subjectIDList = new ArrayList<String>();
        try {
            subjectIDList = FILE_IO_UTILITIES.readListFromFile(FilePath.DATA.getPath(), FileName.SUBJECT_ID_LIST.getName() + FileExt.FILE.getExt());
        } catch (IOException e) {
            System.out.println("Unable to read existing ID's from file.");
            e.printStackTrace();
        }
        
        if(subjectIDList.contains(subjectID)) {
            return false;
        } else {
            return true;
        }
    }
    
    public static void addSubjectIDToList(String subjectID) {
        TreeSet<String> subjectIDList = new TreeSet<String>();
        try {
            subjectIDList.addAll(FILE_IO_UTILITIES.readListFromFile(FilePath.DATA.getPath(), FileName.SUBJECT_ID_LIST.getName() + FileExt.FILE.getExt()));
        } catch (IOException e) {
            System.out.println("Unable to read existing ID's from file.");
            e.printStackTrace();
        }
        
        subjectIDList.add(subjectID);
        ArrayList<String> savedData = new ArrayList<String>();
        savedData.addAll(subjectIDList);
        try {
            FILE_IO_UTILITIES.writeListToFile(savedData, FilePath.DATA.getPath(),  FileName.SUBJECT_ID_LIST.getName() + FileExt.FILE.getExt(), false);
        } catch (IOException e) {
            System.out.println("Unable to write new ID to file.");
            e.printStackTrace();
        }
    }
}

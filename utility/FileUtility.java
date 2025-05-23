package utility;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

public class FileUtility {
  public static String currentDirectory() {
    String dir = System.getProperty("user.dir");
    if (dir == null)
        dir = new File(".").getAbsoluteFile().getParent();
    if (dir == null)
        dir = ".";
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(dir);
    if (dir.charAt(dir.length() - 1) != File.separatorChar)
        stringBuffer.append(File.separator);
    return stringBuffer.toString();
  }
  
  public static void open(File paramFile) {
    Desktop desktop = Desktop.getDesktop();
    try {
      desktop.open(paramFile);
    } catch (IOException iOException) {
      iOException.printStackTrace();
    } 
  }
  
  public static void open(String paramString) {
    File file = new File(paramString);
    open(file);
  }
}



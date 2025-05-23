package utility;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import javax.swing.JFrame;
import mvc.Controller;
import mvc.Model;
import mvc.View;

public class Example {
  private static int fileNo = 0;
  
  private static Point displayPoint = new Point(30, 50);
  
  private static Point offsetPoint = new Point(25, 25);
  
  public static void main(String[] paramArrayOfString) {
    String str = null;
    StringBuffer stringBuffer = null;
    str = "SampleTexts".concat(File.separator.concat("PrimeMinisters.csv"));
    stringBuffer = new StringBuffer();
    stringBuffer.append(FileUtility.currentDirectory());
    stringBuffer.append(str);
    str = stringBuffer.toString();
    List<List<String>> list = StringUtility.readRowsFromFile(str);
    for (List<String> list1 : list) {
      Integer integer = Integer.valueOf(0);
      while (integer.intValue() < list1.size()) {
        if (integer.intValue() > 0)
          System.out.print(","); 
        str = list1.get(integer.intValue());
        str = StringUtility.csvString(str);
        System.out.print(str);
        Integer integer1 = integer;
        Integer integer2 = integer = Integer.valueOf(integer.intValue() + 1);
      } 
      System.out.println();
    } 
    str = "SampleTexts".concat(File.separator.concat("PrimeMinisters2.csv"));
    stringBuffer = new StringBuffer();
    stringBuffer.append(FileUtility.currentDirectory());
    stringBuffer.append(str);
    str = stringBuffer.toString();
    StringUtility.writeRows(list, str);
    str = "SampleImages".concat(File.separator.concat("CROWN.jpg"));
    stringBuffer = new StringBuffer();
    stringBuffer.append(System.getProperty("user.dir"));
    stringBuffer.append(File.separator);
    stringBuffer.append(str);
    str = stringBuffer.toString();
    BufferedImage bufferedImage1 = ImageUtility.readImage(str);
    open(bufferedImage1, "CROWN (Color)");
    BufferedImage bufferedImage2 = ImageUtility.grayscaleImage(bufferedImage1);
    open(bufferedImage2, "CROWN (Gray Scale)");
    double d = 0.75D;
    int i = (int)(bufferedImage1.getWidth() * d);
    int j = (int)(bufferedImage1.getHeight() * d);
    bufferedImage2 = ImageUtility.adjustImage(bufferedImage1, i, j);
    open(bufferedImage2, "CROWN (Shrinked)");
    d = 1.25D;
    i = (int)(bufferedImage1.getWidth() * d);
    j = (int)(bufferedImage1.getHeight() * d);
    bufferedImage2 = ImageUtility.adjustImage(bufferedImage1, i, j);
    open(bufferedImage2, "CROWN (Magnified)");
    str = "http://aokilab.kyoto-su.ac.jp/documents/BlackBook/images/BlackBookFrontPage335x432.jpg";
    bufferedImage2 = ImageUtility.readImageFromURL(str);
    open(bufferedImage2, "Black Book");
    str = "SamplePDFs".concat(File.separator.concat("三つの世界.key.pdf"));
    stringBuffer = new StringBuffer();
    stringBuffer.append(FileUtility.currentDirectory());
    stringBuffer.append(str);
    str = stringBuffer.toString();
    FileUtility.open(str);
  }
  
  private static void open(BufferedImage paramBufferedImage, String paramString) {
    Model model = new Model();
    model.picture(paramBufferedImage);
    View view = new View(model, new Controller());
    JFrame jFrame = new JFrame(paramString);
    jFrame.getContentPane().add((Component)view);
    Dimension dimension = new Dimension(paramBufferedImage.getWidth(), paramBufferedImage.getHeight());
    jFrame.setMinimumSize(dimension);
    jFrame.setMaximumSize(dimension);
    jFrame.setResizable(false);
    jFrame.setDefaultCloseOperation(3);
    jFrame.addNotify();
    int i = (jFrame.getInsets()).top;
    jFrame.setSize(dimension.width, dimension.height + i);
    jFrame.setLocation(displayPoint.x, displayPoint.y);
    jFrame.setVisible(true);
    write(paramBufferedImage);
    displayPoint = new Point(displayPoint.x + offsetPoint.x, displayPoint.y + offsetPoint.y);
  }
  
  private static void write(BufferedImage paramBufferedImage) {
    File file = new File("ResultImages");
    if (!file.exists())
      try {
        file.mkdir();
      } catch (SecurityException securityException) {
        securityException.printStackTrace();
      }  
    String str;
    for (str = Integer.toString(fileNo++); str.length() < 2; str = "0".concat(str));
    ImageUtility.writeImage(paramBufferedImage, file.getName().concat(File.separator.concat("Utility".concat(str.concat(".jpg")))));
  }
}



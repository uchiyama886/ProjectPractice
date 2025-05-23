package utility;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import javax.imageio.ImageIO;

public class ImageUtility {
  public static BufferedImage adjustImage(BufferedImage paramBufferedImage, int paramInt1, int paramInt2) {
    BufferedImage bufferedImage = new BufferedImage(paramInt1, paramInt2, paramBufferedImage.getType());
    Graphics2D graphics2D = bufferedImage.createGraphics();
    graphics2D.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
    graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    graphics2D.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
    graphics2D.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
    graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    graphics2D.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
    graphics2D.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
    graphics2D.drawImage(paramBufferedImage, 0, 0, paramInt1, paramInt2, null);
    return bufferedImage;
  }
  
  public static BufferedImage grayscaleImage(BufferedImage paramBufferedImage) {
    int i = paramBufferedImage.getWidth();
    int j = paramBufferedImage.getHeight();
    BufferedImage bufferedImage = new BufferedImage(paramBufferedImage.getWidth(), paramBufferedImage.getHeight(), paramBufferedImage.getType());
    for (byte b = 0; b < j; b++) {
      for (byte b1 = 0; b1 < i; b1++) {
        int k = paramBufferedImage.getRGB(b1, b);
        double d = ColorUtility.luminanceFromRGB(k);
        k = ColorUtility.convertRGBtoINT(d, d, d);
        bufferedImage.setRGB(b1, b, k);
      } 
    } 
    return bufferedImage;
  }
  
  public static BufferedImage copyImage(BufferedImage paramBufferedImage) {
    BufferedImage bufferedImage = new BufferedImage(paramBufferedImage.getWidth(), paramBufferedImage.getHeight(), paramBufferedImage.getType());
    Graphics2D graphics2D = bufferedImage.createGraphics();
    graphics2D.setColor(Color.white);
    graphics2D.fillRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
    graphics2D.drawImage(paramBufferedImage, 0, 0, null);
    return bufferedImage;
  }
  
  public static double[][] convertImageToLuminanceMatrix(BufferedImage paramBufferedImage) {
    int i = paramBufferedImage.getWidth();
    int j = paramBufferedImage.getHeight();
    double[][] arrayOfDouble = new double[j][i];
    for (byte b = 0; b < j; b++) {
      for (byte b1 = 0; b1 < i; b1++) {
        int k = paramBufferedImage.getRGB(b1, b);
        arrayOfDouble[b][b1] = ColorUtility.luminanceFromRGB(k);
      } 
    } 
    return arrayOfDouble;
  }
  
  public static double[][][] convertImageToYUVMatrixes(BufferedImage paramBufferedImage) {
    int i = paramBufferedImage.getWidth();
    int j = paramBufferedImage.getHeight();
    double[][] arrayOfDouble1 = new double[j][i];
    double[][] arrayOfDouble2 = new double[j][i];
    double[][] arrayOfDouble3 = new double[j][i];
    for (byte b = 0; b < j; b++) {
      for (byte b1 = 0; b1 < i; b1++) {
        int k = paramBufferedImage.getRGB(b1, b);
        double[] arrayOfDouble = ColorUtility.convertRGBtoYUV(k);
        arrayOfDouble1[b][b1] = arrayOfDouble[0];
        arrayOfDouble2[b][b1] = arrayOfDouble[1];
        arrayOfDouble3[b][b1] = arrayOfDouble[2];
      } 
    } 
    return new double[][][] { arrayOfDouble1, arrayOfDouble2, arrayOfDouble3 };
  }
  
  public static BufferedImage convertLuminanceMatrixToImage(double[][] paramArrayOfdouble) {
    int i = (paramArrayOfdouble[0]).length;
    int j = paramArrayOfdouble.length;
    BufferedImage bufferedImage = new BufferedImage(i, j, 1);
    Graphics2D graphics2D = bufferedImage.createGraphics();
    graphics2D.setColor(Color.white);
    graphics2D.fillRect(0, 0, i, j);
    for (byte b = 0; b < j; b++) {
      for (byte b1 = 0; b1 < i; b1++) {
        double d = paramArrayOfdouble[b][b1];
        double[] arrayOfDouble = ColorUtility.convertYUVtoRGB(d, 0.0D, 0.0D);
        int k = ColorUtility.convertRGBtoINT(arrayOfDouble);
        bufferedImage.setRGB(b1, b, k);
      } 
    } 
    return bufferedImage;
  }
  
  public static BufferedImage convertYUVMatrixesToImage(double[][][] paramArrayOfdouble) {
    double[][] arrayOfDouble1 = paramArrayOfdouble[0];
    double[][] arrayOfDouble2 = paramArrayOfdouble[1];
    double[][] arrayOfDouble3 = paramArrayOfdouble[2];
    int i = (arrayOfDouble1[0]).length;
    int j = arrayOfDouble1.length;
    BufferedImage bufferedImage = new BufferedImage(i, j, 1);
    Graphics2D graphics2D = bufferedImage.createGraphics();
    graphics2D.setColor(Color.white);
    graphics2D.fillRect(0, 0, i, j);
    for (byte b = 0; b < j; b++) {
      for (byte b1 = 0; b1 < i; b1++) {
        double[] arrayOfDouble = ColorUtility.convertYUVtoRGB(arrayOfDouble1[b][b1], arrayOfDouble2[b][b1], arrayOfDouble3[b][b1]);
        int k = ColorUtility.convertRGBtoINT(arrayOfDouble);
        bufferedImage.setRGB(b1, b, k);
      } 
    } 
    return bufferedImage;
  }
  
  public static BufferedImage readImage(File paramFile) {
    return readImageFromFile(paramFile);
  }
  
  public static BufferedImage readImage(String paramString) {
    return readImageFromFile(paramString);
  }
  
  public static BufferedImage readImageFromFile(File paramFile) {
    BufferedImage bufferedImage = null;
    try {
      bufferedImage = ImageIO.read(paramFile);
    } catch (IOException iOException) {
      iOException.printStackTrace();
    } 
    return bufferedImage;
  }
  
  public static BufferedImage readImageFromFile(String paramString) {
    File file = new File(paramString);
    return readImageFromFile(file);
  }
  
  public static BufferedImage readImageFromURL(URL paramURL) {
    BufferedImage bufferedImage = null;
    try {
      bufferedImage = ImageIO.read(paramURL);
    } catch (IOException iOException) {
      iOException.printStackTrace();
    } 
    return bufferedImage;
  }
  
  public static BufferedImage readImageFromURL(String paramString) {
    URL uRL = null;
    try {
      uRL = new URL(paramString);
    } catch (MalformedURLException malformedURLException) {
      malformedURLException.printStackTrace();
    } 
    return readImageFromURL(uRL);
  }
  
  public static void writeImage(BufferedImage paramBufferedImage, File paramFile) {
    String str = paramFile.getName();
    str = str.substring(str.lastIndexOf(".") + 1);
    try {
      ImageIO.write(paramBufferedImage, str, paramFile);
    } catch (IOException iOException) {
      iOException.printStackTrace();
    } 
  }
  
  public static void writeImage(BufferedImage paramBufferedImage, String paramString) {
    File file = new File(paramString);
    writeImage(paramBufferedImage, file);
  }
}



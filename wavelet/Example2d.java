package wavelet;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.swing.JFrame;
import javax.swing.JPanel;
import utility.ColorUtility;
import utility.ImageUtility;

public class Example2d {
  private static int fileNo = 100;
  
  private static Point displayPoint = new Point(130, 50);
  
  private static Point offsetPoint = new Point(25, 25);
  
  public static void main(String[] paramArrayOfString) {
    example1();
    example2();
    example3();
  }
  
  protected static void example1() {
    fileNo = 100;
    double[][] arrayOfDouble = Wavelet2dModel.dataSampleCoefficients();
    perform(arrayOfDouble, new Point(4, 4), 0);
  }
  
  protected static void example2() {
    fileNo = 200;
    double[][][] arrayOfDouble = Wavelet2dModel.dataSmalltalkBalloon();
    perform(arrayOfDouble, "Smalltalk Balloon");
  }
  
  protected static void example3() {
    fileNo = 300;
    double[][][] arrayOfDouble = Wavelet2dModel.dataEarth();
    perform(arrayOfDouble, "Earth");
  }
  
  private static void open(JPanel paramJPanel) {
    open(paramJPanel, 512, 512);
  }
  
  protected static void open(JPanel paramJPanel, int paramInt1, int paramInt2) {
    JFrame jFrame = new JFrame("Wavelet Example (2D)");
    jFrame.getContentPane().add(paramJPanel);
    jFrame.setDefaultCloseOperation(2);
    jFrame.addNotify();
    int i = (jFrame.getInsets()).top;
    jFrame.setMinimumSize(new Dimension(paramInt1 / 2, paramInt2 / 2 + i));
    jFrame.setResizable(true);
    jFrame.setSize(paramInt1, paramInt2 + i);
    jFrame.setLocation(displayPoint.x, displayPoint.y);
    jFrame.setVisible(true);
    jFrame.toFront();
    displayPoint = new Point(displayPoint.x + offsetPoint.x, displayPoint.y + offsetPoint.y);
  }
  
  protected static double[][] perform(double[][] paramArrayOfdouble, Point paramPoint, int paramInt) {
    double[][] arrayOfDouble1 = paramArrayOfdouble;
    DiscreteWavelet2dTransformation discreteWavelet2dTransformation = new DiscreteWavelet2dTransformation(arrayOfDouble1);
    double[][] arrayOfDouble2 = discreteWavelet2dTransformation.scalingCoefficients();
    double[][] arrayOfDouble3 = discreteWavelet2dTransformation.horizontalWaveletCoefficients();
    double[][] arrayOfDouble4 = discreteWavelet2dTransformation.verticalWaveletCoefficients();
    double[][] arrayOfDouble5 = discreteWavelet2dTransformation.diagonalWaveletCoefficients();
    BufferedImage bufferedImage1 = Wavelet2dModel.generateImage(arrayOfDouble1, paramPoint, paramInt);
    BufferedImage bufferedImage2 = Wavelet2dModel.generateImage(arrayOfDouble2, paramPoint, paramInt);
    BufferedImage bufferedImage3 = Wavelet2dModel.generateImage(arrayOfDouble3, paramPoint, 0);
    BufferedImage bufferedImage4 = Wavelet2dModel.generateImage(arrayOfDouble4, paramPoint, 0);
    BufferedImage bufferedImage5 = Wavelet2dModel.generateImage(arrayOfDouble5, paramPoint, 0);
    write(bufferedImage1);
    write(bufferedImage2);
    write(bufferedImage3);
    write(bufferedImage4);
    write(bufferedImage5);
    BufferedImage bufferedImage6 = Wavelet2dModel.generateImage(bufferedImage2, bufferedImage3, bufferedImage4, bufferedImage5);
    write(bufferedImage6);
    discreteWavelet2dTransformation = new DiscreteWavelet2dTransformation(arrayOfDouble2);
    double[][] arrayOfDouble6 = discreteWavelet2dTransformation.scalingCoefficients();
    double[][] arrayOfDouble7 = discreteWavelet2dTransformation.horizontalWaveletCoefficients();
    double[][] arrayOfDouble8 = discreteWavelet2dTransformation.verticalWaveletCoefficients();
    double[][] arrayOfDouble9 = discreteWavelet2dTransformation.diagonalWaveletCoefficients();
    BufferedImage bufferedImage7 = Wavelet2dModel.generateImage(arrayOfDouble6, paramPoint, paramInt);
    BufferedImage bufferedImage8 = Wavelet2dModel.generateImage(arrayOfDouble7, paramPoint, 0);
    BufferedImage bufferedImage9 = Wavelet2dModel.generateImage(arrayOfDouble8, paramPoint, 0);
    BufferedImage bufferedImage10 = Wavelet2dModel.generateImage(arrayOfDouble9, paramPoint, 0);
    write(bufferedImage7);
    write(bufferedImage8);
    write(bufferedImage9);
    write(bufferedImage10);
    BufferedImage bufferedImage11 = Wavelet2dModel.generateImage(bufferedImage7, bufferedImage8, bufferedImage9, bufferedImage10);
    write(bufferedImage11);
    bufferedImage11 = Wavelet2dModel.generateImage(bufferedImage11, bufferedImage3, bufferedImage4, bufferedImage5);
    write(bufferedImage11);
    double[][][] arrayOfDouble10 = { arrayOfDouble7, arrayOfDouble8, arrayOfDouble9 };
    discreteWavelet2dTransformation = new DiscreteWavelet2dTransformation(arrayOfDouble6, arrayOfDouble10);
    double[][] arrayOfDouble11 = discreteWavelet2dTransformation.recomposedCoefficients();
    BufferedImage bufferedImage12 = Wavelet2dModel.generateImage(arrayOfDouble11, paramPoint, paramInt);
    write(bufferedImage12);
    double[][][] arrayOfDouble12 = { arrayOfDouble3, arrayOfDouble4, arrayOfDouble5 };
    discreteWavelet2dTransformation = new DiscreteWavelet2dTransformation(arrayOfDouble11, arrayOfDouble12);
    double[][] arrayOfDouble13 = discreteWavelet2dTransformation.recomposedCoefficients();
    BufferedImage bufferedImage13 = Wavelet2dModel.generateImage(arrayOfDouble13, paramPoint, paramInt);
    write(bufferedImage13);
    GridBagLayout gridBagLayout = new GridBagLayout();
    JPanel jPanel = new JPanel(gridBagLayout);
    GridBagConstraints gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.fill = 1;
    WaveletPaneModel waveletPaneModel = new WaveletPaneModel(bufferedImage1, "Source Coefficients");
    WaveletPaneView waveletPaneView = new WaveletPaneView(waveletPaneModel);
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.gridwidth = 1;
    gridBagConstraints.gridheight = 1;
    gridBagConstraints.weightx = 0.5D;
    gridBagConstraints.weighty = 0.5D;
    gridBagLayout.setConstraints((Component)waveletPaneView, gridBagConstraints);
    jPanel.add((Component)waveletPaneView);
    waveletPaneModel = new WaveletPaneModel(bufferedImage6, "Scaling & Wavelet Coefficients");
    waveletPaneView = new WaveletPaneView(waveletPaneModel);
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.gridwidth = 1;
    gridBagConstraints.gridheight = 1;
    gridBagConstraints.weightx = 0.5D;
    gridBagConstraints.weighty = 0.5D;
    gridBagLayout.setConstraints((Component)waveletPaneView, gridBagConstraints);
    jPanel.add((Component)waveletPaneView);
    waveletPaneModel = new WaveletPaneModel(bufferedImage13, "Recomposed Coefficients");
    waveletPaneView = new WaveletPaneView(waveletPaneModel);
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.gridwidth = 1;
    gridBagConstraints.gridheight = 1;
    gridBagConstraints.weightx = 0.5D;
    gridBagConstraints.weighty = 0.5D;
    gridBagLayout.setConstraints((Component)waveletPaneView, gridBagConstraints);
    jPanel.add((Component)waveletPaneView);
    waveletPaneModel = new WaveletPaneModel(bufferedImage11, "Scaling & Wavelet Coefficients");
    waveletPaneView = new WaveletPaneView(waveletPaneModel);
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.gridwidth = 1;
    gridBagConstraints.gridheight = 1;
    gridBagConstraints.weightx = 0.5D;
    gridBagConstraints.weighty = 0.5D;
    gridBagLayout.setConstraints((Component)waveletPaneView, gridBagConstraints);
    jPanel.add((Component)waveletPaneView);
    open(jPanel, paramArrayOfdouble.length * paramPoint.x, (paramArrayOfdouble[0]).length * paramPoint.y);
    return arrayOfDouble13;
  }
  
  protected static void perform(double[][][] paramArrayOfdouble, String paramString) {
    double[][] arrayOfDouble1 = paramArrayOfdouble[0];
    double[][] arrayOfDouble2 = paramArrayOfdouble[1];
    double[][] arrayOfDouble3 = paramArrayOfdouble[2];
    double[][] arrayOfDouble4 = paramArrayOfdouble[3];
    Point point = new Point(1, 1);
    double[][] arrayOfDouble5 = perform(arrayOfDouble1, point, 0);
    double[][] arrayOfDouble6 = perform(arrayOfDouble2, point, 1);
    double[][] arrayOfDouble7 = perform(arrayOfDouble3, point, 2);
    double[][] arrayOfDouble8 = perform(arrayOfDouble4, point, 3);
    int i = arrayOfDouble5.length;
    int j = (arrayOfDouble5[0]).length;
    BufferedImage bufferedImage = new BufferedImage(i, j, 1);
    for (byte b = 0; b < j; b++) {
      for (byte b1 = 0; b1 < i; b1++) {
        double d1 = arrayOfDouble6[b1][b];
        double d2 = arrayOfDouble7[b1][b];
        double d3 = arrayOfDouble8[b1][b];
        int k = ColorUtility.convertRGBtoINT(d1, d2, d3);
        bufferedImage.setRGB(b1, b, k);
      } 
    } 
    write(bufferedImage);
    WaveletPaneModel waveletPaneModel = new WaveletPaneModel(bufferedImage, paramString);
    WaveletPaneView waveletPaneView = new WaveletPaneView(waveletPaneModel);
    open((JPanel)waveletPaneView, arrayOfDouble1.length, (arrayOfDouble1[0]).length);
  }
  
  protected static void write(BufferedImage paramBufferedImage) {
    File file = new File("ResultImages");
    if (!file.exists())
      file.mkdir(); 
    String str;
    for (str = Integer.toString(fileNo++); str.length() < 3; str = "0" + str);
    ImageUtility.writeImage(paramBufferedImage, file.getName() + "/Wavelet" + file.getName() + ".jpg");
  }
}



package wavelet;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.Arrays;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import utility.ColorUtility;
import utility.ImageUtility;

public class Wavelet2dModel extends WaveletModel {
  protected double maximumAbsoluteSourceCoefficient = Double.MIN_VALUE;
  
  protected double maximumAbsoluteScalingCoefficient = Double.MIN_VALUE;
  
  protected double maximumAbsoluteWaveletCoefficient = Double.MIN_VALUE;
  
  protected double maximumAbsoluteRecomposedCoefficient = Double.MIN_VALUE;
  
  protected double[][][] sourceCoefficientsArray;
  
  protected double[][][] scalingCoefficientsArray;
  
  protected double[][][] horizontalWaveletCoefficientsArray;
  
  protected double[][][] verticalWaveletCoefficientsArray;
  
  protected double[][][] diagonalWaveletCoefficientsArray;
  
  protected double[][][] interactiveHorizontalWaveletCoefficientsArray;
  
  protected double[][][] interactiveVerticalWaveletCoefficientsArray;
  
  protected double[][][] interactiveDiagonalWaveletCoefficientsArray;
  
  protected double[][][] recomposedCoefficientsArray;
  
  protected WaveletPaneModel sourceCoefficientsPaneModel = null;
  
  protected WaveletPaneModel scalingAndWaveletCoefficientsPaneModel = null;
  
  protected WaveletPaneModel interactiveScalingAndWaveletCoefficientsPaneModel = null;
  
  protected WaveletPaneModel recomposedCoefficientsPaneModel = null;
  
  public Wavelet2dModel() {
    doSampleCoefficients();
  }
  
  public void actionPerformed(ActionEvent paramActionEvent) {
    String str = paramActionEvent.getActionCommand();
    if (str == "sample coefficients") {
      doSampleCoefficients();
      return;
    } 
    if (str == "smalltalk balloon") {
      doSmalltalkBalloon();
      return;
    } 
    if (str == "earth") {
      doEarth();
      return;
    } 
    if (str == "all coefficients") {
      doAllCoefficients();
      return;
    } 
    if (str == "clear coefficients") {
      doClearCoefficients();
      return;
    } 
  }
  
  public void computeFromPoint(Point paramPoint, boolean paramBoolean) {
    for (byte b = 0; b < this.sourceCoefficientsArray.length; b++) {
      double[][] arrayOfDouble = this.sourceCoefficientsArray[b];
      if (arrayOfDouble != null) {
        double[][] arrayOfDouble1 = this.scalingCoefficientsArray[b];
        int i = arrayOfDouble1.length;
        int j = (arrayOfDouble1[0]).length;
        int k = paramPoint.x % i;
        int m = paramPoint.y % j;
        double[][] arrayOfDouble2 = this.horizontalWaveletCoefficientsArray[b];
        double[][] arrayOfDouble3 = this.verticalWaveletCoefficientsArray[b];
        double[][] arrayOfDouble4 = this.diagonalWaveletCoefficientsArray[b];
        double[][] arrayOfDouble5 = this.interactiveHorizontalWaveletCoefficientsArray[b];
        double[][] arrayOfDouble6 = this.interactiveVerticalWaveletCoefficientsArray[b];
        double[][] arrayOfDouble7 = this.interactiveDiagonalWaveletCoefficientsArray[b];
        for (byte b1 = -2; b1 <= 2; b1++) {
          for (byte b2 = -2; b2 <= 2; b2++) {
            int n = k + b2;
            int i1 = m + b1;
            n = Math.min(Math.max(0, n), i - 1);
            i1 = Math.min(Math.max(0, i1), j - 1);
            if (paramBoolean) {
              arrayOfDouble5[n][i1] = 0.0D;
              arrayOfDouble6[n][i1] = 0.0D;
              arrayOfDouble7[n][i1] = 0.0D;
            } else {
              arrayOfDouble5[n][i1] = arrayOfDouble2[n][i1];
              arrayOfDouble6[n][i1] = arrayOfDouble3[n][i1];
              arrayOfDouble7[n][i1] = arrayOfDouble4[n][i1];
            } 
          } 
        } 
      } 
    } 
    computeRecomposedCoefficients();
  }
  
  public void computeRecomposedCoefficients() {
    this.maximumAbsoluteRecomposedCoefficient = Double.MIN_VALUE;
    for (byte b = 0; b < this.sourceCoefficientsArray.length; b++) {
      double[][] arrayOfDouble = this.sourceCoefficientsArray[b];
      if (arrayOfDouble != null) {
        double[][] arrayOfDouble1 = this.scalingCoefficientsArray[b];
        double[][][] arrayOfDouble2 = { this.interactiveHorizontalWaveletCoefficientsArray[b], this.interactiveVerticalWaveletCoefficientsArray[b], this.interactiveDiagonalWaveletCoefficientsArray[b] };
        DiscreteWavelet2dTransformation discreteWavelet2dTransformation = new DiscreteWavelet2dTransformation(arrayOfDouble1, arrayOfDouble2);
        double[][] arrayOfDouble3 = discreteWavelet2dTransformation.recomposedCoefficients();
        this.recomposedCoefficientsArray[b] = arrayOfDouble3;
      } 
    } 
    BufferedImage bufferedImage1 = generateImage(this.scalingCoefficientsArray, maximumAbsoluteScalingCoefficient());
    BufferedImage bufferedImage2 = generateImage(this.interactiveHorizontalWaveletCoefficientsArray, maximumAbsoluteWaveletCoefficient());
    BufferedImage bufferedImage3 = generateImage(this.interactiveVerticalWaveletCoefficientsArray, maximumAbsoluteWaveletCoefficient());
    BufferedImage bufferedImage4 = generateImage(this.interactiveDiagonalWaveletCoefficientsArray, maximumAbsoluteWaveletCoefficient());
    BufferedImage bufferedImage5 = generateImage(bufferedImage1, bufferedImage2, bufferedImage3, bufferedImage4);
    BufferedImage bufferedImage6 = generateImage(this.recomposedCoefficientsArray, maximumAbsoluteRecomposedCoefficient());
    this.interactiveScalingAndWaveletCoefficientsPaneModel.picture(bufferedImage5);
    this.recomposedCoefficientsPaneModel.picture(bufferedImage6);
    this.interactiveScalingAndWaveletCoefficientsPaneModel.changed();
    this.recomposedCoefficientsPaneModel.changed();
  }
  
  public static double[][] dataSampleCoefficients() {
    byte b1 = 64;
    double[][] arrayOfDouble = new double[b1][b1];
    byte b2;
    for (b2 = 0; b2 < arrayOfDouble.length; b2++)
      Arrays.fill(arrayOfDouble[b2], 0.2D); 
    for (b2 = 5; b2 < b1 - 5; b2++) {
      arrayOfDouble[5][b2] = 1.0D;
      arrayOfDouble[b1 - 6][b2] = 1.0D;
      arrayOfDouble[b2][5] = 1.0D;
      arrayOfDouble[b2][b1 - 6] = 1.0D;
      arrayOfDouble[b2][b2] = 1.0D;
      arrayOfDouble[b2][b1 - b2 - 1] = 1.0D;
    } 
    return arrayOfDouble;
  }
  
  public static double[][][] dataEarth() {
    BufferedImage bufferedImage = imageEarth();
    return lrgbMatrixes(bufferedImage);
  }
  
  public static double[][][] dataSmalltalkBalloon() {
    BufferedImage bufferedImage = imageSmalltalkBalloon();
    return lrgbMatrixes(bufferedImage);
  }
  
  public void doAllCoefficients() {
    for (byte b = 0; b < this.sourceCoefficientsArray.length; b++) {
      double[][] arrayOfDouble = this.sourceCoefficientsArray[b];
      if (arrayOfDouble != null) {
        double[][] arrayOfDouble1 = this.scalingCoefficientsArray[b];
        int i = arrayOfDouble1.length;
        int j = (arrayOfDouble1[0]).length;
        double[][] arrayOfDouble2 = this.horizontalWaveletCoefficientsArray[b];
        double[][] arrayOfDouble3 = this.verticalWaveletCoefficientsArray[b];
        double[][] arrayOfDouble4 = this.diagonalWaveletCoefficientsArray[b];
        double[][] arrayOfDouble5 = this.interactiveHorizontalWaveletCoefficientsArray[b];
        double[][] arrayOfDouble6 = this.interactiveVerticalWaveletCoefficientsArray[b];
        double[][] arrayOfDouble7 = this.interactiveDiagonalWaveletCoefficientsArray[b];
        for (byte b1 = 0; b1 < j; b1++) {
          for (byte b2 = 0; b2 < i; b2++) {
            arrayOfDouble5[b2][b1] = arrayOfDouble2[b2][b1];
            arrayOfDouble6[b2][b1] = arrayOfDouble3[b2][b1];
            arrayOfDouble7[b2][b1] = arrayOfDouble4[b2][b1];
          } 
        } 
      } 
    } 
    computeRecomposedCoefficients();
  }
  
  public void doClearCoefficients() {
    for (byte b = 0; b < this.sourceCoefficientsArray.length; b++) {
      double[][] arrayOfDouble = this.sourceCoefficientsArray[b];
      if (arrayOfDouble != null) {
        double[][] arrayOfDouble1 = this.scalingCoefficientsArray[b];
        int i = arrayOfDouble1.length;
        int j = (arrayOfDouble1[0]).length;
        double[][] arrayOfDouble2 = this.horizontalWaveletCoefficientsArray[b];
        double[][] arrayOfDouble3 = this.verticalWaveletCoefficientsArray[b];
        double[][] arrayOfDouble4 = this.diagonalWaveletCoefficientsArray[b];
        double[][] arrayOfDouble5 = this.interactiveHorizontalWaveletCoefficientsArray[b];
        double[][] arrayOfDouble6 = this.interactiveVerticalWaveletCoefficientsArray[b];
        double[][] arrayOfDouble7 = this.interactiveDiagonalWaveletCoefficientsArray[b];
        for (byte b1 = 0; b1 < j; b1++) {
          for (byte b2 = 0; b2 < i; b2++) {
            arrayOfDouble5[b2][b1] = 0.0D;
            arrayOfDouble6[b2][b1] = 0.0D;
            arrayOfDouble7[b2][b1] = 0.0D;
          } 
        } 
      } 
    } 
    computeRecomposedCoefficients();
  }
  
  public void doEarth() {
    setSourceData(dataEarth());
  }
  
  public void doSampleCoefficients() {
    setSourceData(dataSampleCoefficients());
  }
  
  public void doSmalltalkBalloon() {
    setSourceData(dataSmalltalkBalloon());
  }
  
  public static void fill(double[][] paramArrayOfdouble, double paramDouble) {
    for (byte b = 0; b < paramArrayOfdouble.length; b++) {
      double[] arrayOfDouble = paramArrayOfdouble[b];
      Arrays.fill(arrayOfDouble, paramDouble);
    } 
  }
  
  public static BufferedImage generateImage(double[][][] paramArrayOfdouble, double paramDouble) {
    double[][] arrayOfDouble = paramArrayOfdouble[0];
    int i = arrayOfDouble.length;
    int j = (arrayOfDouble[0]).length;
    BufferedImage bufferedImage = new BufferedImage(i, j, 1);
    Graphics2D graphics2D = bufferedImage.createGraphics();
    if (paramArrayOfdouble[1] == null || paramArrayOfdouble[2] == null || paramArrayOfdouble[3] == null) {
      for (byte b = 0; b < j; b++) {
        for (byte b1 = 0; b1 < i; b1++) {
          double d = Math.abs(arrayOfDouble[b1][b]);
          int k = (int)Math.round(d / paramDouble * 255.0D);
          Color color = new Color(k, k, k);
          graphics2D.setColor(color);
          graphics2D.fillRect(b1, b, 1, 1);
        } 
      } 
    } else {
      int[][] arrayOfInt1 = new int[i][j];
      int[][] arrayOfInt2 = new int[i][j];
      int[][] arrayOfInt3 = new int[i][j];
      for (byte b = 0; b < j; b++) {
        for (byte b1 = 0; b1 < i; b1++) {
          double d1 = Math.abs(paramArrayOfdouble[1][b1][b]);
          int k = (int)Math.round(d1 / paramDouble * 255.0D);
          double d2 = Math.abs(paramArrayOfdouble[2][b1][b]);
          int m = (int)Math.round(d2 / paramDouble * 255.0D);
          double d3 = Math.abs(paramArrayOfdouble[3][b1][b]);
          int n = (int)Math.round(d3 / paramDouble * 255.0D);
          Color color = new Color(k, m, n);
          graphics2D.setColor(color);
          graphics2D.fillRect(b1, b, 1, 1);
        } 
      } 
    } 
    return bufferedImage;
  }
  
  public static BufferedImage generateImage(double[][] paramArrayOfdouble, Point paramPoint, int paramInt) {
    int i = paramArrayOfdouble.length;
    int j = (paramArrayOfdouble[0]).length;
    int k = i * paramPoint.x;
    int m = j * paramPoint.y;
    BufferedImage bufferedImage = new BufferedImage(k, m, 1);
    Graphics2D graphics2D = bufferedImage.createGraphics();
    double d = Double.MIN_VALUE;
    byte b;
    for (b = 0; b < j; b++) {
      for (byte b1 = 0; b1 < i; b1++) {
        double d1 = Math.abs(paramArrayOfdouble[b1][b]);
        d = Math.max(d1, d);
      } 
    } 
    for (b = 0; b < j; b++) {
      for (byte b1 = 0; b1 < i; b1++) {
        double d1 = Math.abs(paramArrayOfdouble[b1][b]);
        int n = (int)Math.round(d1 / d * 255.0D);
        Color color = new Color(n, n, n);
        if (paramInt == 1)
          color = new Color(n, 0, 0); 
        if (paramInt == 2)
          color = new Color(0, n, 0); 
        if (paramInt == 3)
          color = new Color(0, 0, n); 
        graphics2D.setColor(color);
        graphics2D.fillRect(b1 * paramPoint.x, b * paramPoint.y, paramPoint.x, paramPoint.y);
      } 
    } 
    return bufferedImage;
  }
  
  public static BufferedImage generateImage(BufferedImage paramBufferedImage1, BufferedImage paramBufferedImage2, BufferedImage paramBufferedImage3, BufferedImage paramBufferedImage4) {
    int i = paramBufferedImage1.getWidth();
    int j = paramBufferedImage1.getHeight();
    int k = i + paramBufferedImage2.getWidth();
    int m = j + paramBufferedImage3.getHeight();
    BufferedImage bufferedImage = new BufferedImage(k, m, 1);
    Graphics2D graphics2D = bufferedImage.createGraphics();
    graphics2D.drawImage(paramBufferedImage1, 0, 0, (ImageObserver)null);
    graphics2D.drawImage(paramBufferedImage2, i, 0, (ImageObserver)null);
    graphics2D.drawImage(paramBufferedImage3, 0, j, (ImageObserver)null);
    graphics2D.drawImage(paramBufferedImage4, i, j, (ImageObserver)null);
    return bufferedImage;
  }
  
  public static BufferedImage imageEarth() {
    String str = "SampleImages/imageEarth512x256.jpg";
    return ImageUtility.readImage(str);
  }
  
  public static BufferedImage imageSmalltalkBalloon() {
    String str = "SampleImages/imageSmalltalkBalloon256x256.jpg";
    return ImageUtility.readImage(str);
  }
  
  public static double[][][] lrgbMatrixes(BufferedImage paramBufferedImage) {
    int i = paramBufferedImage.getWidth();
    int j = paramBufferedImage.getHeight();
    double[][] arrayOfDouble1 = new double[i][j];
    double[][] arrayOfDouble2 = new double[i][j];
    double[][] arrayOfDouble3 = new double[i][j];
    double[][] arrayOfDouble4 = new double[i][j];
    for (byte b = 0; b < j; b++) {
      for (byte b1 = 0; b1 < i; b1++) {
        int k = paramBufferedImage.getRGB(b1, b);
        arrayOfDouble1[b1][b] = ColorUtility.luminanceFromRGB(k);
        double[] arrayOfDouble = ColorUtility.convertINTtoRGB(k);
        arrayOfDouble2[b1][b] = arrayOfDouble[0];
        arrayOfDouble3[b1][b] = arrayOfDouble[1];
        arrayOfDouble4[b1][b] = arrayOfDouble[2];
      } 
    } 
    return new double[][][] { arrayOfDouble1, arrayOfDouble2, arrayOfDouble3, arrayOfDouble4 };
  }
  
  public double maximumAbsoluteScalingCoefficient() {
    if (this.maximumAbsoluteScalingCoefficient == Double.MIN_VALUE)
      for (byte b = 0; b < this.sourceCoefficientsArray.length; b++) {
        double[][] arrayOfDouble = this.sourceCoefficientsArray[b];
        if (arrayOfDouble != null) {
          double[][] arrayOfDouble1 = this.scalingCoefficientsArray[b];
          int i = arrayOfDouble1.length;
          int j = (arrayOfDouble1[0]).length;
          for (byte b1 = 0; b1 < j; b1++) {
            for (byte b2 = 0; b2 < i; b2++)
              this.maximumAbsoluteScalingCoefficient = Math.max(Math.abs(arrayOfDouble1[b2][b1]), this.maximumAbsoluteScalingCoefficient); 
          } 
        } 
      }  
    return this.maximumAbsoluteScalingCoefficient;
  }
  
  public double maximumAbsoluteSourceCoefficient() {
    if (this.maximumAbsoluteSourceCoefficient == Double.MIN_VALUE)
      for (byte b = 0; b < this.sourceCoefficientsArray.length; b++) {
        double[][] arrayOfDouble = this.sourceCoefficientsArray[b];
        if (arrayOfDouble != null) {
          int i = arrayOfDouble.length;
          int j = (arrayOfDouble[0]).length;
          for (byte b1 = 0; b1 < j; b1++) {
            for (byte b2 = 0; b2 < i; b2++)
              this.maximumAbsoluteSourceCoefficient = Math.max(Math.abs(arrayOfDouble[b2][b1]), this.maximumAbsoluteSourceCoefficient); 
          } 
        } 
      }  
    return this.maximumAbsoluteSourceCoefficient;
  }
  
  public double maximumAbsoluteRecomposedCoefficient() {
    if (this.maximumAbsoluteRecomposedCoefficient == Double.MIN_VALUE)
      for (byte b = 0; b < this.sourceCoefficientsArray.length; b++) {
        double[][] arrayOfDouble = this.sourceCoefficientsArray[b];
        if (arrayOfDouble != null) {
          double[][] arrayOfDouble1 = this.recomposedCoefficientsArray[b];
          int i = arrayOfDouble1.length;
          int j = (arrayOfDouble1[0]).length;
          for (byte b1 = 0; b1 < j; b1++) {
            for (byte b2 = 0; b2 < i; b2++)
              this.maximumAbsoluteRecomposedCoefficient = Math.max(Math.abs(arrayOfDouble1[b2][b1]), this.maximumAbsoluteRecomposedCoefficient); 
          } 
        } 
      }  
    return this.maximumAbsoluteRecomposedCoefficient;
  }
  
  public double maximumAbsoluteWaveletCoefficient() {
    if (this.maximumAbsoluteWaveletCoefficient == Double.MIN_VALUE)
      for (byte b = 0; b < this.sourceCoefficientsArray.length; b++) {
        double[][] arrayOfDouble = this.sourceCoefficientsArray[b];
        if (arrayOfDouble != null) {
          double[][] arrayOfDouble1 = this.scalingCoefficientsArray[b];
          int i = arrayOfDouble1.length;
          int j = (arrayOfDouble1[0]).length;
          double[][] arrayOfDouble2 = this.horizontalWaveletCoefficientsArray[b];
          double[][] arrayOfDouble3 = this.verticalWaveletCoefficientsArray[b];
          double[][] arrayOfDouble4 = this.diagonalWaveletCoefficientsArray[b];
          for (byte b1 = 0; b1 < j; b1++) {
            for (byte b2 = 0; b2 < i; b2++) {
              this.maximumAbsoluteWaveletCoefficient = Math.max(Math.abs(arrayOfDouble2[b2][b1]), this.maximumAbsoluteWaveletCoefficient);
              this.maximumAbsoluteWaveletCoefficient = Math.max(Math.abs(arrayOfDouble3[b2][b1]), this.maximumAbsoluteWaveletCoefficient);
              this.maximumAbsoluteWaveletCoefficient = Math.max(Math.abs(arrayOfDouble4[b2][b1]), this.maximumAbsoluteWaveletCoefficient);
            } 
          } 
        } 
      }  
    return this.maximumAbsoluteWaveletCoefficient;
  }
  
  public void mouseClicked(Point paramPoint, MouseEvent paramMouseEvent) {
    computeFromPoint(paramPoint, paramMouseEvent.isAltDown());
  }
  
  public void mouseDragged(Point paramPoint, MouseEvent paramMouseEvent) {
    computeFromPoint(paramPoint, paramMouseEvent.isAltDown());
  }
  
  public void open() {
    GridBagLayout gridBagLayout = new GridBagLayout();
    JPanel jPanel = new JPanel(gridBagLayout);
    GridBagConstraints gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.fill = 1;
    WaveletPaneView waveletPaneView = new WaveletPaneView(this.sourceCoefficientsPaneModel);
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.gridwidth = 1;
    gridBagConstraints.gridheight = 1;
    gridBagConstraints.weightx = 0.5D;
    gridBagConstraints.weighty = 0.5D;
    gridBagLayout.setConstraints((Component)waveletPaneView, gridBagConstraints);
    jPanel.add((Component)waveletPaneView);
    waveletPaneView = new WaveletPaneView(this.scalingAndWaveletCoefficientsPaneModel);
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.gridwidth = 1;
    gridBagConstraints.gridheight = 1;
    gridBagConstraints.weightx = 0.5D;
    gridBagConstraints.weighty = 0.5D;
    gridBagLayout.setConstraints((Component)waveletPaneView, gridBagConstraints);
    jPanel.add((Component)waveletPaneView);
    waveletPaneView = new WaveletPaneView(this.recomposedCoefficientsPaneModel);
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.gridwidth = 1;
    gridBagConstraints.gridheight = 1;
    gridBagConstraints.weightx = 0.5D;
    gridBagConstraints.weighty = 0.5D;
    gridBagLayout.setConstraints((Component)waveletPaneView, gridBagConstraints);
    jPanel.add((Component)waveletPaneView);
    waveletPaneView = new WaveletPaneView(this.interactiveScalingAndWaveletCoefficientsPaneModel);
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.gridwidth = 1;
    gridBagConstraints.gridheight = 1;
    gridBagConstraints.weightx = 0.5D;
    gridBagConstraints.weighty = 0.5D;
    gridBagLayout.setConstraints((Component)waveletPaneView, gridBagConstraints);
    jPanel.add((Component)waveletPaneView);
    JFrame jFrame = new JFrame("Wavelet Transform (2D)");
    jFrame.getContentPane().add(jPanel);
    jFrame.setDefaultCloseOperation(3);
    jFrame.addNotify();
    int i = (jFrame.getInsets()).top;
    jFrame.setMinimumSize(new Dimension(256, 256 + i));
    jFrame.setResizable(true);
    jFrame.setSize(512, 512 + i);
    jFrame.setLocationRelativeTo(null);
    jFrame.setVisible(true);
    jFrame.toFront();
  }
  
  public void setSourceData(double[][] paramArrayOfdouble) {
    double[][] arrayOfDouble1 = paramArrayOfdouble;
    double[][] arrayOfDouble2 = null;
    double[][] arrayOfDouble3 = null;
    double[][] arrayOfDouble4 = null;
    double[][][] arrayOfDouble = { arrayOfDouble1, arrayOfDouble2, arrayOfDouble3, arrayOfDouble4 };
    setSourceData(arrayOfDouble);
  }
  
  public void setSourceData(double[][][] paramArrayOfdouble) {
    this.sourceCoefficientsArray = paramArrayOfdouble;
    this.scalingCoefficientsArray = new double[this.sourceCoefficientsArray.length][][];
    this.horizontalWaveletCoefficientsArray = new double[this.sourceCoefficientsArray.length][][];
    this.verticalWaveletCoefficientsArray = new double[this.sourceCoefficientsArray.length][][];
    this.diagonalWaveletCoefficientsArray = new double[this.sourceCoefficientsArray.length][][];
    this.interactiveHorizontalWaveletCoefficientsArray = new double[this.sourceCoefficientsArray.length][][];
    this.interactiveVerticalWaveletCoefficientsArray = new double[this.sourceCoefficientsArray.length][][];
    this.interactiveDiagonalWaveletCoefficientsArray = new double[this.sourceCoefficientsArray.length][][];
    this.recomposedCoefficientsArray = new double[this.sourceCoefficientsArray.length][][];
    for (byte b = 0; b < this.sourceCoefficientsArray.length; b++) {
      double[][] arrayOfDouble = this.sourceCoefficientsArray[b];
      if (arrayOfDouble != null) {
        DiscreteWavelet2dTransformation discreteWavelet2dTransformation = new DiscreteWavelet2dTransformation(arrayOfDouble);
        double[][] arrayOfDouble1 = discreteWavelet2dTransformation.scalingCoefficients();
        double[][] arrayOfDouble2 = discreteWavelet2dTransformation.horizontalWaveletCoefficients();
        double[][] arrayOfDouble3 = discreteWavelet2dTransformation.verticalWaveletCoefficients();
        double[][] arrayOfDouble4 = discreteWavelet2dTransformation.diagonalWaveletCoefficients();
        double[][] arrayOfDouble5 = new double[arrayOfDouble2.length][(arrayOfDouble2[0]).length];
        double[][] arrayOfDouble6 = new double[arrayOfDouble3.length][(arrayOfDouble3[0]).length];
        double[][] arrayOfDouble7 = new double[arrayOfDouble4.length][(arrayOfDouble4[0]).length];
        fill(arrayOfDouble5, 0.0D);
        fill(arrayOfDouble6, 0.0D);
        fill(arrayOfDouble7, 0.0D);
        this.scalingCoefficientsArray[b] = arrayOfDouble1;
        this.horizontalWaveletCoefficientsArray[b] = arrayOfDouble2;
        this.verticalWaveletCoefficientsArray[b] = arrayOfDouble3;
        this.diagonalWaveletCoefficientsArray[b] = arrayOfDouble4;
        this.interactiveHorizontalWaveletCoefficientsArray[b] = arrayOfDouble5;
        this.interactiveVerticalWaveletCoefficientsArray[b] = arrayOfDouble6;
        this.interactiveDiagonalWaveletCoefficientsArray[b] = arrayOfDouble7;
        double[][][] arrayOfDouble8 = { arrayOfDouble5, arrayOfDouble6, arrayOfDouble7 };
        discreteWavelet2dTransformation = new DiscreteWavelet2dTransformation(arrayOfDouble1, arrayOfDouble8);
        double[][] arrayOfDouble9 = discreteWavelet2dTransformation.recomposedCoefficients();
        this.recomposedCoefficientsArray[b] = arrayOfDouble9;
      } 
    } 
    this.maximumAbsoluteSourceCoefficient = Double.MIN_VALUE;
    this.maximumAbsoluteScalingCoefficient = Double.MIN_VALUE;
    this.maximumAbsoluteWaveletCoefficient = Double.MIN_VALUE;
    this.maximumAbsoluteRecomposedCoefficient = Double.MIN_VALUE;
    BufferedImage bufferedImage1 = generateImage(this.sourceCoefficientsArray, maximumAbsoluteSourceCoefficient());
    BufferedImage bufferedImage2 = generateImage(this.scalingCoefficientsArray, maximumAbsoluteScalingCoefficient());
    BufferedImage bufferedImage3 = generateImage(this.horizontalWaveletCoefficientsArray, maximumAbsoluteWaveletCoefficient());
    BufferedImage bufferedImage4 = generateImage(this.verticalWaveletCoefficientsArray, maximumAbsoluteWaveletCoefficient());
    BufferedImage bufferedImage5 = generateImage(this.diagonalWaveletCoefficientsArray, maximumAbsoluteWaveletCoefficient());
    BufferedImage bufferedImage6 = generateImage(this.interactiveHorizontalWaveletCoefficientsArray, maximumAbsoluteWaveletCoefficient());
    BufferedImage bufferedImage7 = generateImage(this.interactiveVerticalWaveletCoefficientsArray, maximumAbsoluteWaveletCoefficient());
    BufferedImage bufferedImage8 = generateImage(this.interactiveDiagonalWaveletCoefficientsArray, maximumAbsoluteWaveletCoefficient());
    BufferedImage bufferedImage9 = generateImage(bufferedImage2, bufferedImage3, bufferedImage4, bufferedImage5);
    BufferedImage bufferedImage10 = generateImage(bufferedImage2, bufferedImage6, bufferedImage7, bufferedImage8);
    BufferedImage bufferedImage11 = generateImage(this.recomposedCoefficientsArray, maximumAbsoluteRecomposedCoefficient());
    if (this.sourceCoefficientsPaneModel == null)
      this.sourceCoefficientsPaneModel = new WaveletPaneModel(null, "Source Coefficients"); 
    this.sourceCoefficientsPaneModel.picture(bufferedImage1);
    if (this.scalingAndWaveletCoefficientsPaneModel == null)
      this.scalingAndWaveletCoefficientsPaneModel = new WaveletPaneModel(null, "Scaling & Wavelet Coefficients"); 
    this.scalingAndWaveletCoefficientsPaneModel.picture(bufferedImage9);
    if (this.interactiveScalingAndWaveletCoefficientsPaneModel == null)
      this.interactiveScalingAndWaveletCoefficientsPaneModel = new WaveletPaneModel(null, "Interactive Scaling & Wavelet Coefficients", this); 
    this.interactiveScalingAndWaveletCoefficientsPaneModel.picture(bufferedImage10);
    if (this.recomposedCoefficientsPaneModel == null)
      this.recomposedCoefficientsPaneModel = new WaveletPaneModel(null, "Recomposed Coefficients"); 
    this.recomposedCoefficientsPaneModel.picture(bufferedImage11);
    this.sourceCoefficientsPaneModel.changed();
    this.scalingAndWaveletCoefficientsPaneModel.changed();
    this.interactiveScalingAndWaveletCoefficientsPaneModel.changed();
    this.recomposedCoefficientsPaneModel.changed();
  }
  
  public void showPopupMenu(MouseEvent paramMouseEvent, WaveletPaneController paramWaveletPaneController) {
    int i = paramMouseEvent.getX();
    int j = paramMouseEvent.getY();
    Cursor cursor = Cursor.getDefaultCursor();
    Component component = paramMouseEvent.getComponent();
    component.setCursor(cursor);
    JPopupMenu jPopupMenu = new JPopupMenu();
    JMenuItem jMenuItem = new JMenuItem("sample coefficients");
    jMenuItem.addActionListener(paramWaveletPaneController);
    jPopupMenu.add(jMenuItem);
    jMenuItem = new JMenuItem("smalltalk balloon");
    jMenuItem.addActionListener(paramWaveletPaneController);
    jPopupMenu.add(jMenuItem);
    jMenuItem = new JMenuItem("earth");
    jMenuItem.addActionListener(paramWaveletPaneController);
    jPopupMenu.add(jMenuItem);
    jPopupMenu.addSeparator();
    jMenuItem = new JMenuItem("all coefficients");
    jMenuItem.addActionListener(paramWaveletPaneController);
    jPopupMenu.add(jMenuItem);
    jMenuItem = new JMenuItem("clear coefficients");
    jMenuItem.addActionListener(paramWaveletPaneController);
    jPopupMenu.add(jMenuItem);
    jPopupMenu.show(component, i, j);
  }
}



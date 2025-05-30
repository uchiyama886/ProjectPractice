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

  public static void main(String[] args) {
    example1();
    example2();
    example3();
  }

  protected static void example1() {
    fileNo = 100;
    double[][] sampleData = Wavelet2dModel.dataSampleCoefficients();
    perform(sampleData, new Point(4, 4), 0);
  }

  protected static void example2() {
    fileNo = 200;
    double[][][] sampleData = Wavelet2dModel.dataSmalltalkBalloon();
    perform(sampleData, "Smalltalk Balloon");
  }

  protected static void example3() {
    fileNo = 300;
    double[][][] sampleData = Wavelet2dModel.dataEarth();
    perform(sampleData, "Earth");
  }

  private static void open(JPanel panel) {
    open(panel, 512, 512);
  }

  protected static void open(JPanel panel, int width, int height) {
    JFrame frame = new JFrame("Wavelet Example (2D)");
    frame.getContentPane().add(panel);
    frame.setDefaultCloseOperation(2);
    frame.addNotify();
    int topInset = (frame.getInsets()).top;
    frame.setMinimumSize(new Dimension(width / 2, height / 2 + topInset));
    frame.setResizable(true);
    frame.setSize(width, height + topInset);
    frame.setLocation(displayPoint.x, displayPoint.y);
    frame.setVisible(true);
    frame.toFront();
    displayPoint = new Point(displayPoint.x + offsetPoint.x, displayPoint.y + offsetPoint.y);
  }

  protected static double[][] perform(double[][] sourceData, Point scaleFactor, int rgbFlag) {
    double[][] inputCoefficients = sourceData;
    DiscreteWavelet2dTransformation waveletTransform = new DiscreteWavelet2dTransformation(inputCoefficients);

    double[][] scalingCoefficients = waveletTransform.scalingCoefficients();
    double[][] horizontalCoefficients = waveletTransform.horizontalWaveletCoefficients();
    double[][] verticalCoefficients = waveletTransform.verticalWaveletCoefficients();
    double[][] diagonalCoefficients = waveletTransform.diagonalWaveletCoefficients();

    BufferedImage sourceImage = Wavelet2dModel.generateImage(inputCoefficients, scaleFactor, rgbFlag);
    BufferedImage scalingImage = Wavelet2dModel.generateImage(scalingCoefficients, scaleFactor, rgbFlag);
    BufferedImage horizontalImage = Wavelet2dModel.generateImage(horizontalCoefficients, scaleFactor, 0);
    BufferedImage verticalImage = Wavelet2dModel.generateImage(verticalCoefficients, scaleFactor, 0);
    BufferedImage diagonalImage = Wavelet2dModel.generateImage(diagonalCoefficients, scaleFactor, 0);

    write(sourceImage);
    write(scalingImage);
    write(horizontalImage);
    write(verticalImage);
    write(diagonalImage);

    BufferedImage combinedFirstLevel = Wavelet2dModel.generateImage(scalingImage, horizontalImage, verticalImage,
        diagonalImage);
    write(combinedFirstLevel);

    // Second level wavelet transform
    waveletTransform = new DiscreteWavelet2dTransformation(scalingCoefficients);
    double[][] level2ScalingCoeff = waveletTransform.scalingCoefficients();
    double[][] level2HorizontalCoeff = waveletTransform.horizontalWaveletCoefficients();
    double[][] level2VerticalCoeff = waveletTransform.verticalWaveletCoefficients();
    double[][] level2DiagonalCoeff = waveletTransform.diagonalWaveletCoefficients();

    BufferedImage level2ScalingImage = Wavelet2dModel.generateImage(level2ScalingCoeff, scaleFactor, rgbFlag);
    BufferedImage level2HorizontalImage = Wavelet2dModel.generateImage(level2HorizontalCoeff, scaleFactor, 0);
    BufferedImage level2VerticalImage = Wavelet2dModel.generateImage(level2VerticalCoeff, scaleFactor, 0);
    BufferedImage level2DiagonalImage = Wavelet2dModel.generateImage(level2DiagonalCoeff, scaleFactor, 0);

    write(level2ScalingImage);
    write(level2HorizontalImage);
    write(level2VerticalImage);
    write(level2DiagonalImage);

    BufferedImage combinedSecondLevel = Wavelet2dModel.generateImage(level2ScalingImage, level2HorizontalImage,
        level2VerticalImage, level2DiagonalImage);
    write(combinedSecondLevel);

    combinedSecondLevel = Wavelet2dModel.generateImage(combinedSecondLevel, horizontalImage, verticalImage,
        diagonalImage);
    write(combinedSecondLevel);

    // Reconstruction of second level
    double[][][] level2WaveletCoeffs = { level2HorizontalCoeff, level2VerticalCoeff, level2DiagonalCoeff };
    waveletTransform = new DiscreteWavelet2dTransformation(level2ScalingCoeff, level2WaveletCoeffs);
    double[][] level2Reconstructed = waveletTransform.recomposedCoefficients();
    BufferedImage level2ReconstructedImage = Wavelet2dModel.generateImage(level2Reconstructed, scaleFactor, rgbFlag);
    write(level2ReconstructedImage);

    // Final reconstruction
    double[][][] waveletCoeffs = { horizontalCoefficients, verticalCoefficients, diagonalCoefficients };
    waveletTransform = new DiscreteWavelet2dTransformation(level2Reconstructed, waveletCoeffs);
    double[][] finalReconstructed = waveletTransform.recomposedCoefficients();
    BufferedImage finalImage = Wavelet2dModel.generateImage(finalReconstructed, scaleFactor, rgbFlag);
    write(finalImage);

    // UI Setup
    GridBagLayout layout = new GridBagLayout();
    JPanel panel = new JPanel(layout);
    GridBagConstraints constraints = new GridBagConstraints();
    constraints.fill = 1;

    // Source coefficients panel
    WaveletPaneModel sourceModel = new WaveletPaneModel(sourceImage, "Source Coefficients");
    WaveletPaneView sourceView = new WaveletPaneView(sourceModel);
    constraints.gridx = 0;
    constraints.gridy = 0;
    constraints.gridwidth = 1;
    constraints.gridheight = 1;
    constraints.weightx = 0.5D;
    constraints.weighty = 0.5D;
    layout.setConstraints((Component) sourceView, constraints);
    panel.add((Component) sourceView);

    // First level coefficients panel
    WaveletPaneModel firstLevelModel = new WaveletPaneModel(combinedFirstLevel, "Scaling & Wavelet Coefficients");
    WaveletPaneView firstLevelView = new WaveletPaneView(firstLevelModel);
    constraints.gridx = 1;
    layout.setConstraints((Component) firstLevelView, constraints);
    panel.add((Component) firstLevelView);

    // Reconstructed coefficients panel
    WaveletPaneModel reconstructedModel = new WaveletPaneModel(finalImage, "Recomposed Coefficients");
    WaveletPaneView reconstructedView = new WaveletPaneView(reconstructedModel);
    constraints.gridx = 0;
    constraints.gridy = 1;
    layout.setConstraints((Component) reconstructedView, constraints);
    panel.add((Component) reconstructedView);

    // Second level coefficients panel
    WaveletPaneModel secondLevelModel = new WaveletPaneModel(combinedSecondLevel, "Scaling & Wavelet Coefficients");
    WaveletPaneView secondLevelView = new WaveletPaneView(secondLevelModel);
    constraints.gridx = 1;
    layout.setConstraints((Component) secondLevelView, constraints);
    panel.add((Component) secondLevelView);

    open(panel, sourceData.length * scaleFactor.x, sourceData[0].length * scaleFactor.y);
    return finalReconstructed;
  }

  protected static void perform(double[][][] colorChannels, String imageName) {
    double[][] luminanceChannel = colorChannels[0];
    double[][] redChannel = colorChannels[1];
    double[][] greenChannel = colorChannels[2];
    double[][] blueChannel = colorChannels[3];

    Point unitScale = new Point(1, 1);

    double[][] processedLuminance = perform(luminanceChannel, unitScale, 0);
    double[][] processedRed = perform(redChannel, unitScale, 1);
    double[][] processedGreen = perform(greenChannel, unitScale, 2);
    double[][] processedBlue = perform(blueChannel, unitScale, 3);

    int width = processedLuminance.length;
    int height = processedLuminance[0].length;
    BufferedImage resultImage = new BufferedImage(width, height, 1);

    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        double red = processedRed[x][y];
        double green = processedGreen[x][y];
        double blue = processedBlue[x][y];
        int rgb = ColorUtility.convertRGBtoINT(red, green, blue);
        resultImage.setRGB(x, y, rgb);
      }
    }

    write(resultImage);
    WaveletPaneModel paneModel = new WaveletPaneModel(resultImage, imageName);
    WaveletPaneView paneView = new WaveletPaneView(paneModel);
    open((JPanel) paneView, width, height);
  }

  protected static void write(BufferedImage image) {
    File outputDir = new File("ResultImages");
    if (!outputDir.exists()) {
      outputDir.mkdir();
    }
    String numberStr = String.format("%03d", fileNo++);
    ImageUtility.writeImage(image, outputDir.getName() + "/Wavelet" + numberStr + ".jpg");
  }
}

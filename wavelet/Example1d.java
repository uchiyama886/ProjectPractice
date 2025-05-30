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
import utility.ImageUtility;

public class Example1d {
  private static int fileNo = 0;

  private static Point displayPoint = new Point(30, 50);

  private static Point offsetPoint = new Point(25, 25);

  public static void main(String[] args) {
    example1();
  }

  protected static void example1() {
    double[] sampleData = Wavelet1dModel.dataSampleCoefficients();
    perform(sampleData);
  }

  protected static void perform(double[] sampleCoefficients) {
    double[] sourceCoefficients = sampleCoefficients;
    DiscreteWavelet1dTransformation waveletTransform = new DiscreteWavelet1dTransformation(sourceCoefficients);
    double[] scalingCoefficients = waveletTransform.scalingCoefficients();
    double[] waveletCoefficients = waveletTransform.waveletCoefficients();
    double[] recomposedCoefficients = waveletTransform.recomposedCoefficients();

    BufferedImage sourceImage = Wavelet1dModel.generateImage(sourceCoefficients);
    BufferedImage scalingImage = Wavelet1dModel.generateImage(scalingCoefficients);
    BufferedImage waveletImage = Wavelet1dModel.generateImage(waveletCoefficients);
    BufferedImage recomposedImage = Wavelet1dModel.generateImage(recomposedCoefficients);

    write(sourceImage);
    write(scalingImage);
    write(waveletImage);
    write(recomposedImage);

    GridBagLayout layout = new GridBagLayout();
    JPanel panel = new JPanel(layout);
    GridBagConstraints constraints = new GridBagConstraints();
    constraints.fill = 1;
    constraints.gridwidth = 1;
    constraints.gridheight = 1;

    // 元データのパネル
    WaveletPaneModel sourceModel = new WaveletPaneModel(sourceImage, "Source Coefficients");
    WaveletPaneView sourceView = new WaveletPaneView(sourceModel);
    constraints.gridx = 0;
    constraints.gridy = 0;
    constraints.weightx = 0.67D;
    constraints.weighty = 0.5D;
    layout.setConstraints((Component) sourceView, constraints);
    panel.add((Component) sourceView);

    // スケーリング係数のパネル
    WaveletPaneModel scalingModel = new WaveletPaneModel(scalingImage, "Scaling Coefficients");
    WaveletPaneView scalingView = new WaveletPaneView(scalingModel);
    constraints.gridx = 1;
    constraints.gridy = 0;
    constraints.weightx = 0.33D;
    constraints.weighty = 0.5D;
    layout.setConstraints((Component) scalingView, constraints);
    panel.add((Component) scalingView);

    // 再構成係数のパネル
    WaveletPaneModel recomposedModel = new WaveletPaneModel(recomposedImage, "Recomposed Coefficients");
    WaveletPaneView recomposedView = new WaveletPaneView(recomposedModel);
    constraints.gridx = 0;
    constraints.gridy = 1;
    constraints.weightx = 0.66D;
    constraints.weighty = 0.5D;
    layout.setConstraints((Component) recomposedView, constraints);
    panel.add((Component) recomposedView);

    // ウェーブレット係数のパネル
    WaveletPaneModel waveletModel = new WaveletPaneModel(waveletImage, "Wavelet Coefficients");
    WaveletPaneView waveletView = new WaveletPaneView(waveletModel);
    constraints.gridx = 1;
    constraints.gridy = 1;
    constraints.weightx = 0.33D;
    constraints.weighty = 0.5D;
    layout.setConstraints((Component) waveletView, constraints);
    panel.add((Component) waveletView);

    open(panel);
  }

  protected static void open(JPanel panel) {
    JFrame frame = new JFrame("Wavelet Example (1D)");
    frame.getContentPane().add(panel);
    frame.setDefaultCloseOperation(2);
    frame.addNotify();
    int topInset = (frame.getInsets()).top;
    frame.setMinimumSize(new Dimension(400, 200 + topInset));
    frame.setResizable(true);
    frame.setSize(800, 400 + topInset);
    frame.setLocation(displayPoint.x, displayPoint.y);
    frame.setVisible(true);
    frame.toFront();
    displayPoint = new Point(displayPoint.x + offsetPoint.x, displayPoint.y + offsetPoint.y);
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

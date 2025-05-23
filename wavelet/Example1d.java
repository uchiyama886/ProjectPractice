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
  
  public static void main(String[] paramArrayOfString) {
    example1();
  }
  
  protected static void example1() {
    double[] arrayOfDouble = Wavelet1dModel.dataSampleCoefficients();
    perform(arrayOfDouble);
  }
  
  protected static void perform(double[] paramArrayOfdouble) {
    double[] arrayOfDouble1 = paramArrayOfdouble;
    DiscreteWavelet1dTransformation discreteWavelet1dTransformation = new DiscreteWavelet1dTransformation(arrayOfDouble1);
    double[] arrayOfDouble2 = discreteWavelet1dTransformation.scalingCoefficients();
    double[] arrayOfDouble3 = discreteWavelet1dTransformation.waveletCoefficients();
    double[] arrayOfDouble4 = discreteWavelet1dTransformation.recomposedCoefficients();
    BufferedImage bufferedImage1 = Wavelet1dModel.generateImage(arrayOfDouble1);
    BufferedImage bufferedImage2 = Wavelet1dModel.generateImage(arrayOfDouble2);
    BufferedImage bufferedImage3 = Wavelet1dModel.generateImage(arrayOfDouble3);
    BufferedImage bufferedImage4 = Wavelet1dModel.generateImage(arrayOfDouble4);
    write(bufferedImage1);
    write(bufferedImage2);
    write(bufferedImage3);
    write(bufferedImage4);
    GridBagLayout gridBagLayout = new GridBagLayout();
    JPanel jPanel = new JPanel(gridBagLayout);
    GridBagConstraints gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.fill = 1;
    gridBagConstraints.gridwidth = 1;
    gridBagConstraints.gridheight = 1;
    WaveletPaneModel waveletPaneModel = new WaveletPaneModel(bufferedImage1, "Source Coefficients");
    WaveletPaneView waveletPaneView = new WaveletPaneView(waveletPaneModel);
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.weightx = 0.67D;
    gridBagConstraints.weighty = 0.5D;
    gridBagLayout.setConstraints((Component)waveletPaneView, gridBagConstraints);
    jPanel.add((Component)waveletPaneView);
    waveletPaneModel = new WaveletPaneModel(bufferedImage2, "Scaling Coefficients");
    waveletPaneView = new WaveletPaneView(waveletPaneModel);
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.weightx = 0.33D;
    gridBagConstraints.weighty = 0.5D;
    gridBagLayout.setConstraints((Component)waveletPaneView, gridBagConstraints);
    jPanel.add((Component)waveletPaneView);
    waveletPaneModel = new WaveletPaneModel(bufferedImage4, "Recomposed Coefficients");
    waveletPaneView = new WaveletPaneView(waveletPaneModel);
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.weightx = 0.66D;
    gridBagConstraints.weighty = 0.5D;
    gridBagLayout.setConstraints((Component)waveletPaneView, gridBagConstraints);
    jPanel.add((Component)waveletPaneView);
    waveletPaneModel = new WaveletPaneModel(bufferedImage3, "Wavelet Coefficients");
    waveletPaneView = new WaveletPaneView(waveletPaneModel);
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.weightx = 0.33D;
    gridBagConstraints.weighty = 0.5D;
    gridBagLayout.setConstraints((Component)waveletPaneView, gridBagConstraints);
    jPanel.add((Component)waveletPaneView);
    open(jPanel);
  }
  
  protected static void open(JPanel paramJPanel) {
    JFrame jFrame = new JFrame("Wavelet Example (1D)");
    jFrame.getContentPane().add(paramJPanel);
    jFrame.setDefaultCloseOperation(2);
    jFrame.addNotify();
    int i = (jFrame.getInsets()).top;
    jFrame.setMinimumSize(new Dimension(400, 200 + i));
    jFrame.setResizable(true);
    jFrame.setSize(800, 400 + i);
    jFrame.setLocation(displayPoint.x, displayPoint.y);
    jFrame.setVisible(true);
    jFrame.toFront();
    displayPoint = new Point(displayPoint.x + offsetPoint.x, displayPoint.y + offsetPoint.y);
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



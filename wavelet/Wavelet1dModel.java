package wavelet;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

public class Wavelet1dModel extends WaveletModel {
  protected double[] sourceCoefficients;

  protected double[] scalingCoefficients;

  protected double[] waveletCoefficients;

  protected double[] interactiveWaveletCoefficients;

  protected double[] recomposedCoefficients;

  protected WaveletPaneModel sourceCoefficientsPaneModel = null;

  protected WaveletPaneModel scalingCoefficientsPaneModel = null;

  protected WaveletPaneModel waveletCoefficientsPaneModel = null;

  protected WaveletPaneModel interactiveWaveletCoefficientsPaneModel = null;

  protected WaveletPaneModel recomposedCoefficientsPaneModel = null;

  private static Point scaleFactor = new Point(10, 100);

  private static double rangeValue = 2.8D;

  public Wavelet1dModel() {
    doSampleCoefficients();
  }

  public void actionPerformed(ActionEvent paramActionEvent) {
    String str = paramActionEvent.getActionCommand();
    if (str == "sample coefficients") {
      doSampleCoefficients();
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
    int i = this.interactiveWaveletCoefficients.length - 1;
    int j = Math.min(Math.max(paramPoint.x / scaleFactor.x, 0), i);
    if (paramBoolean) {
      this.interactiveWaveletCoefficients[j] = 0.0D;
    } else {
      this.interactiveWaveletCoefficients[j] = this.waveletCoefficients[j];
    }
    computeRecomposedCoefficients();
  }

  public void computeRecomposedCoefficients() {
    DiscreteWavelet1dTransformation discreteWavelet1dTransformation = new DiscreteWavelet1dTransformation(
        this.scalingCoefficients, this.interactiveWaveletCoefficients);
    this.recomposedCoefficients = discreteWavelet1dTransformation.recomposedCoefficients();
    BufferedImage bufferedImage1 = generateImage(this.interactiveWaveletCoefficients);
    this.interactiveWaveletCoefficientsPaneModel.picture(bufferedImage1);
    this.interactiveWaveletCoefficientsPaneModel.changed();
    BufferedImage bufferedImage2 = generateImage(this.recomposedCoefficients);
    this.recomposedCoefficientsPaneModel.picture(bufferedImage2);
    this.recomposedCoefficientsPaneModel.changed();
  }

  public static double[] dataSampleCoefficients() {
    double[] arrayOfDouble = new double[64];
    Arrays.fill(arrayOfDouble, 0.0D);
    int b;
    for (b = 0; b < 16; b++)
      arrayOfDouble[b] = Math.pow((b + 1), 2.0D) / 256.0D;
    for (b = 16; b < 32; b++)
      arrayOfDouble[b] = 0.2D;
    for (b = 32; b < 48; b++)
      arrayOfDouble[b] = Math.pow((48 - b + 1), 2.0D) / 256.0D - 0.5D;
    return arrayOfDouble;
  }

  public void doAllCoefficients() {
    int i = this.waveletCoefficients.length;
    for (int b = 0; b < i; b++)
      this.interactiveWaveletCoefficients[b] = this.waveletCoefficients[b];
    computeRecomposedCoefficients();
  }

  public void doClearCoefficients() {
    fill(this.interactiveWaveletCoefficients, 0.0D);
    computeRecomposedCoefficients();
  }

  public void doSampleCoefficients() {
    setSourceData(dataSampleCoefficients());
  }

  public static void fill(double[] paramArrayOfdouble, double paramDouble) {
    Arrays.fill(paramArrayOfdouble, paramDouble);
  }

  public static BufferedImage generateImage(double[] paramArrayOfdouble) {
    int i = paramArrayOfdouble.length;
    int j = (int) Math.round(i * scaleFactor.x);
    int k = (int) Math.round(rangeValue * scaleFactor.y);
    BufferedImage bufferedImage = new BufferedImage(j, k, 1);
    Graphics2D graphics2D = bufferedImage.createGraphics();
    graphics2D.setColor(Color.white);
    graphics2D.fillRect(0, 0, j, k);
    graphics2D.setColor(Color.gray);
    graphics2D.setStroke(new BasicStroke(1.0F));
    graphics2D.drawLine(0, k / 2, j, k / 2);
    for (int b = 0; b < i; b++) {
      double d = paramArrayOfdouble[b];
      int m = (int) Math.round(b * scaleFactor.x + scaleFactor.x / 2.0D);
      int n = (int) Math.round((0.0D - d) * scaleFactor.y + k / 2.0D);
      Rectangle rectangle = new Rectangle(m, n, 1, 1);
      rectangle.grow(2, 2);
      graphics2D.setColor(Color.black);
      graphics2D.fill(rectangle);
    }
    return bufferedImage;
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
    gridBagConstraints.gridwidth = 1;
    gridBagConstraints.gridheight = 1;
    WaveletPaneView waveletPaneView = new WaveletPaneView(this.sourceCoefficientsPaneModel);
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.weightx = 0.5D;
    gridBagConstraints.weighty = 0.5D;
    gridBagLayout.setConstraints((Component) waveletPaneView, gridBagConstraints);
    jPanel.add((Component) waveletPaneView);
    waveletPaneView = new WaveletPaneView(this.scalingCoefficientsPaneModel);
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.weightx = 0.25D;
    gridBagConstraints.weighty = 0.5D;
    gridBagLayout.setConstraints((Component) waveletPaneView, gridBagConstraints);
    jPanel.add((Component) waveletPaneView);
    waveletPaneView = new WaveletPaneView(this.waveletCoefficientsPaneModel);
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.weightx = 0.25D;
    gridBagConstraints.weighty = 0.5D;
    gridBagLayout.setConstraints((Component) waveletPaneView, gridBagConstraints);
    jPanel.add((Component) waveletPaneView);
    waveletPaneView = new WaveletPaneView(this.recomposedCoefficientsPaneModel);
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.weightx = 0.5D;
    gridBagConstraints.weighty = 0.5D;
    gridBagLayout.setConstraints((Component) waveletPaneView, gridBagConstraints);
    jPanel.add((Component) waveletPaneView);
    waveletPaneView = new WaveletPaneView(this.scalingCoefficientsPaneModel);
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.weightx = 0.25D;
    gridBagConstraints.weighty = 0.5D;
    gridBagLayout.setConstraints((Component) waveletPaneView, gridBagConstraints);
    jPanel.add((Component) waveletPaneView);
    waveletPaneView = new WaveletPaneView(this.interactiveWaveletCoefficientsPaneModel);
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.weightx = 0.25D;
    gridBagConstraints.weighty = 0.5D;
    gridBagLayout.setConstraints((Component) waveletPaneView, gridBagConstraints);
    jPanel.add((Component) waveletPaneView);
    JFrame jFrame = new JFrame("Wavelet Transform (1D)");
    jFrame.getContentPane().add(jPanel);
    jFrame.setDefaultCloseOperation(3);
    jFrame.addNotify();
    int i = (jFrame.getInsets()).top;
    jFrame.setMinimumSize(new Dimension(500, 200 + i));
    jFrame.setResizable(true);
    jFrame.setSize(1000, 400 + i);
    jFrame.setLocationRelativeTo(null);
    jFrame.setVisible(true);
    jFrame.toFront();
  }

  public void setSourceData(double[] paramArrayOfdouble) {
    this.sourceCoefficients = paramArrayOfdouble;
    DiscreteWavelet1dTransformation discreteWavelet1dTransformation1 = new DiscreteWavelet1dTransformation(
        this.sourceCoefficients);
    this.scalingCoefficients = discreteWavelet1dTransformation1.scalingCoefficients();
    this.waveletCoefficients = discreteWavelet1dTransformation1.waveletCoefficients();
    this.interactiveWaveletCoefficients = new double[this.waveletCoefficients.length];
    fill(this.interactiveWaveletCoefficients, 0.0D);
    DiscreteWavelet1dTransformation discreteWavelet1dTransformation2 = new DiscreteWavelet1dTransformation(
        this.scalingCoefficients, this.interactiveWaveletCoefficients);
    this.recomposedCoefficients = discreteWavelet1dTransformation2.recomposedCoefficients();
    BufferedImage bufferedImage1 = generateImage(this.sourceCoefficients);
    BufferedImage bufferedImage2 = generateImage(this.scalingCoefficients);
    BufferedImage bufferedImage3 = generateImage(this.waveletCoefficients);
    BufferedImage bufferedImage4 = generateImage(this.interactiveWaveletCoefficients);
    BufferedImage bufferedImage5 = generateImage(this.recomposedCoefficients);
    if (this.sourceCoefficientsPaneModel == null)
      this.sourceCoefficientsPaneModel = new WaveletPaneModel(null, "Source Coefficients");
    this.sourceCoefficientsPaneModel.picture(bufferedImage1);
    if (this.scalingCoefficientsPaneModel == null)
      this.scalingCoefficientsPaneModel = new WaveletPaneModel(null, "Scaling Coefficients");
    this.scalingCoefficientsPaneModel.picture(bufferedImage2);
    if (this.waveletCoefficientsPaneModel == null)
      this.waveletCoefficientsPaneModel = new WaveletPaneModel(null, "Wavelet Coefficients");
    this.waveletCoefficientsPaneModel.picture(bufferedImage3);
    if (this.interactiveWaveletCoefficientsPaneModel == null)
      this.interactiveWaveletCoefficientsPaneModel = new WaveletPaneModel(null, "Interactive Wavelet Coefficients",
          this);
    this.interactiveWaveletCoefficientsPaneModel.picture(bufferedImage4);
    if (this.recomposedCoefficientsPaneModel == null)
      this.recomposedCoefficientsPaneModel = new WaveletPaneModel(null, "Recomposed Coefficients");
    this.recomposedCoefficientsPaneModel.picture(bufferedImage5);
    this.sourceCoefficientsPaneModel.changed();
    this.scalingCoefficientsPaneModel.changed();
    this.waveletCoefficientsPaneModel.changed();
    this.interactiveWaveletCoefficientsPaneModel.changed();
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

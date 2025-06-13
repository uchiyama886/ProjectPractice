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

public class Example1d extends Object {
  // 保存する画像ファイルの連番カウンター
  private static int fileNo = 0;
  
  // ウィンドウの初期表示座標
  private static Point displayPoint = new Point(30, 50);
  
  // ウィンドウを開いていく際に少しずつずらしていくオフセット
  private static Point offsetPoint = new Point(25, 25);
  
  /**
   * example1を実行
   */
  public static void main(String[] argument) {
    example1();
  }
  
  /**
   * サンプル係数を取得し、変換処理を実行
   * @param arrayOfDouble：データのサンプル係数
   */
  protected static void example1() {
    // サンプル係数を取得
    double[] arrayOfDouble = Wavelet1dModel.dataSampleCoefficients();

    // 変換処理を実行
    perform(arrayOfDouble);
  }
  /**
   * 各種変数を所得し、画像の生成、表示、保存する
   * @param arrayOfDouble1 データのサンプル係数
   * @param arrayOfDouble2 離散ウェーブレットのスケーリング係数
   * @param arrayOfDouble3 離散ウェーブレットのウェーブレット係数
   * @param arrayOfDouble4 離散ウェーブレットの再構成した（元の信号に戻した）係数
   * @param bufferedImage1 サンプル係数を元にした画像
   * @param bufferedImage2 スケーリング係数を元にした画像
   * @param bufferedImage3 ウェーブレット係数を元にした画像
   * @param bufferedImage4 再構成した係数を元にした（元の信号に戻した）画像
   */
  protected static void perform(double[] sourceData) {
    // サンプル係数を束縛
    double[] arrayOfDouble1 = sourceData;

    // 離散ウェーブレット変換を実行
    DiscreteWavelet1dTransformation discreteWavelet1dTransformation = new DiscreteWavelet1dTransformation(arrayOfDouble1);
    
    // 各係数を束縛
    double[] arrayOfDouble2 = discreteWavelet1dTransformation.scalingCoefficients();
    double[] arrayOfDouble3 = discreteWavelet1dTransformation.waveletCoefficients();
    double[] arrayOfDouble4 = discreteWavelet1dTransformation.recomposedCoefficients();

    // 各係数をもとに画像を生成
    BufferedImage bufferedImage1 = Wavelet1dModel.generateImage(arrayOfDouble1);
    BufferedImage bufferedImage2 = Wavelet1dModel.generateImage(arrayOfDouble2);
    BufferedImage bufferedImage3 = Wavelet1dModel.generateImage(arrayOfDouble3);
    BufferedImage bufferedImage4 = Wavelet1dModel.generateImage(arrayOfDouble4);

    // 画像の書き出し
    write(bufferedImage1);
    write(bufferedImage2);
    write(bufferedImage3);
    write(bufferedImage4);

    // パネルのレイアウト設定(2行2列)
    GridBagLayout gridBagLayout = new GridBagLayout();
    JPanel jPanel = new JPanel(gridBagLayout);
    GridBagConstraints gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.fill = 1;
    gridBagConstraints.gridwidth = 1;
    gridBagConstraints.gridheight = 1;

    // 元画像のパネルのレイアウト構成
    WaveletPaneModel waveletPaneModel = new WaveletPaneModel(bufferedImage1, "Source Coefficients");
    WaveletPaneView waveletPaneView = new WaveletPaneView(waveletPaneModel);
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.weightx = 0.67D;
    gridBagConstraints.weighty = 0.5D;
    gridBagLayout.setConstraints((Component)waveletPaneView, gridBagConstraints);
    jPanel.add((Component)waveletPaneView);

    // スケーリング係数の画像のパネルのレイアウト構成
    waveletPaneModel = new WaveletPaneModel(bufferedImage2, "Scaling Coefficients");
    waveletPaneView = new WaveletPaneView(waveletPaneModel);
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.weightx = 0.33D;
    gridBagConstraints.weighty = 0.5D;
    gridBagLayout.setConstraints((Component)waveletPaneView, gridBagConstraints);
    jPanel.add((Component)waveletPaneView);

	// ウェーブレット係数のパネルのレイアウト構成
    waveletPaneModel = new WaveletPaneModel(bufferedImage3, "Wavelet Coefficients");
    waveletPaneView = new WaveletPaneView(waveletPaneModel);
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.weightx = 0.33D;
    gridBagConstraints.weighty = 0.5D;
    gridBagLayout.setConstraints((Component)waveletPaneView, gridBagConstraints);
    jPanel.add((Component)waveletPaneView);

    // 再構成した画像のパネルのレイアウト構成
    waveletPaneModel = new WaveletPaneModel(bufferedImage4, "Recomposed Coefficients");
    waveletPaneView = new WaveletPaneView(waveletPaneModel);
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.weightx = 0.66D;
    gridBagConstraints.weighty = 0.5D;
    gridBagLayout.setConstraints((Component)waveletPaneView, gridBagConstraints);
    jPanel.add((Component)waveletPaneView);

    // パネルを可視化
    open(jPanel);
  }
  
  /**
   * パネルを開いて表示
   */
  protected static void open(JPanel paramJPanel) {
    // タイトルをWavelet Example (1D）にしてフレームを用意
    JFrame jFrame = new JFrame("Wavelet Example (1D)");

    // paramJPanelをJFrameの表示エリアに追加する
    jFrame.getContentPane().add(paramJPanel);

    // ×ボタンを押すとそのウィンドウを閉じて破棄（他のウィンドウが開いていればそのウィンドウは継続）
    jFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    // jFrame.addNotify();

    // タイトルバーの余白の設定
    int i = (jFrame.getInsets()).top;

    // ウィンドウの最小サイズを設定
    jFrame.setMinimumSize(new Dimension(400, 200 + i));

    // ウィンドウのリサイズを可能にする
    jFrame.setResizable(true);

    // 初期サイズを設定
    jFrame.setSize(800, 400 + i);

    // 表示させる初期位置を設定
    jFrame.setLocation(displayPoint.x, displayPoint.y);

    // 可視化
    jFrame.setVisible(true);

    // 前に持ってくる
    jFrame.toFront();

    // 少しずつ配置する位置をずらす
    displayPoint = new Point(displayPoint.x + offsetPoint.x, displayPoint.y + offsetPoint.y);
  }
  
  /**
   * 画像ファイルを保存
   * @param fileNumber ファイル名の連番（001, 002, 003,,）
   * @param file 書き出す用のファイル
   */
  protected static void write(BufferedImage paramBufferedImage) {
    // 処理する画像を束縛
    File file = new File("ResultImages");


    // ファイルが存在するか
    if (!file.exists()) file.mkdir(); 
    // this.ifThenElse(file);

    // ファイル名の作成（連番）
    String fileNumber = String.format("%03d", fileNo++);

    // 画像ファイルを書き出す
    ImageUtility.writeImage(paramBufferedImage, file.getName() + "/Wavelet" + fileNumber + ".jpg");
  }

  /**
   * ファイルが存在するかの条件分岐
   * @param file 書き出す用のファイル
   */
  private void ifThenElse(File file)
  {
    // ファイルが存在しないならディレクトリを作成する
    if(!file.exists()) {file.mkdir();}

    return;
  }
}
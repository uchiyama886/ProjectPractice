package wavelet;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.image.BufferedImage;

import utility.Condition;
import utility.ImageUtility;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * ウィンドウ表示やユーザ操作の処理、画像変換への指示など、UIと処理をつなぐメイン機能全般(一次元)
 */
public class Example1d extends Object {
  /**
   * 保存する画像ファイルの連番カウンター
   */
  private static int fileNo = 0;
  
  /**
   * ウィンドウの初期表示座標
   */
  private static Point displayPoint = new Point(30, 50);
  
  /**
   * ウィンドウを開いていく際に少しずつずらしていくオフセット
   */
  private static Point offsetPoint = new Point(25, 25);
  
  /**
   * データのサンプル係数を取得する
   */
  public static void main(String[] argument) {
    example1();
  }
  
  /**
   * サンプル係数を取得し、変換処理を実行
   */
  protected static void example1() {
    // サンプル係数を取得
    double[] coefficientsOfSampledata = Wavelet1dModel.dataSampleCoefficients();

    // 変換処理を実行
    perform(coefficientsOfSampledata);
  }
  /**
   * 各種変数を所得し、画像の生成、表示、保存する
   * @param sourceData サンプル係数
   */
  protected static void perform(double[] sourceData) {
    // サンプル係数を束縛
    double[] coefficientsOfSampledata = sourceData;

    // 離散ウェーブレット変換を実行
    DiscreteWavelet1dTransformation discreteWavelet1dTransformation = new DiscreteWavelet1dTransformation(coefficientsOfSampledata);
    
    // 各係数を束縛
    double[] coefficientsOfScaling = discreteWavelet1dTransformation.scalingCoefficients();
    double[] coefficientsOfWavelet  = discreteWavelet1dTransformation.waveletCoefficients();
    double[] coefficientsOfComposites = discreteWavelet1dTransformation.recomposedCoefficients();

    // 各係数をもとに画像を生成
    BufferedImage imageOfSampledata = Wavelet1dModel.generateImage(coefficientsOfSampledata);
    BufferedImage imageOfScaling = Wavelet1dModel.generateImage(coefficientsOfScaling);
    BufferedImage imageOfWavelet = Wavelet1dModel.generateImage(coefficientsOfWavelet );
    BufferedImage compositesImage = Wavelet1dModel.generateImage(coefficientsOfComposites);

    // 画像の書き出し
    write(imageOfSampledata);
    write(imageOfScaling);
    write(imageOfWavelet);
    write(compositesImage);

    // パネルのレイアウト設定(2行2列)
    GridBagLayout gridBagLayout = new GridBagLayout();
    JPanel jPanel = new JPanel(gridBagLayout);
    GridBagConstraints gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.fill = 1;
    gridBagConstraints.gridwidth = 1;
    gridBagConstraints.gridheight = 1;

    // 元画像のパネルのレイアウト構成
    WaveletPaneModel waveletPaneModel = new WaveletPaneModel(imageOfSampledata, "Source Coefficients");
    WaveletPaneView waveletPaneView = new WaveletPaneView(waveletPaneModel);
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.weightx = 0.67D;
    gridBagConstraints.weighty = 0.5D;
    gridBagLayout.setConstraints((Component)waveletPaneView, gridBagConstraints);
    jPanel.add((Component)waveletPaneView);

    // スケーリング係数の画像のパネルのレイアウト構成
    waveletPaneModel = new WaveletPaneModel(imageOfScaling, "Scaling Coefficients");
    waveletPaneView = new WaveletPaneView(waveletPaneModel);
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.weightx = 0.33D;
    gridBagConstraints.weighty = 0.5D;
    gridBagLayout.setConstraints((Component)waveletPaneView, gridBagConstraints);
    jPanel.add((Component)waveletPaneView);

	// ウェーブレット係数のパネルのレイアウト構成
    waveletPaneModel = new WaveletPaneModel(imageOfWavelet, "Wavelet Coefficients");
    waveletPaneView = new WaveletPaneView(waveletPaneModel);
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.weightx = 0.33D;
    gridBagConstraints.weighty = 0.5D;
    gridBagLayout.setConstraints((Component)waveletPaneView, gridBagConstraints);
    jPanel.add((Component)waveletPaneView);

    // 再構成した画像のパネルのレイアウト構成
    waveletPaneModel = new WaveletPaneModel(compositesImage, "Recomposed Coefficients");
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
   * @param aJPanel 表示するパネル
   */
  protected static void open(JPanel aJPanel) {
    // タイトルをWavelet Example (1D）にしてフレームを用意
    JFrame jFrame = new JFrame("Wavelet Example (1D)");

    // aJPanelをJFrameの表示エリアに追加する
    jFrame.getContentPane().add(aJPanel);

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
   * @param anImage 書き出す画像
   */
  protected static void write(BufferedImage anImage) {
    // 処理する画像を束縛
    File file = new File("ResultImages");

    // ファイルが存在するか
    new Condition(() -> file == null).ifTrue(() -> file.mkdir());

    // ファイル名をパスオブジェクトに束縛
    Path path = Paths.get(file.getName());

    // ファイル名の作成（連番）
    String fileNumber = String.format("%03d", fileNo++);

    // ファイル名をパスで連結
    Path filePath = path.resolve(path).resolve("Wavelet" + fileNumber + ".jpg");

    // 画像ファイルを書き出す
    ImageUtility.writeImage(anImage, filePath.toString());
  }
}
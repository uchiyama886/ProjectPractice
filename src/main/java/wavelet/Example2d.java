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
  // 保存する画像ファイルの連番カウンター
  private static int fileNo = 100;
  
  // ウィンドウの初期表示座標
  private static Point displayPoint = new Point(130, 50);
  
  // ウィンドウを開いていく際に少しずつずらしていくオフセット
  private static Point offsetPoint = new Point(25, 25);
  
  /**
   * example1,2,3をそれぞれ実行
   */
  public static void main(String[] arguments) {
    // グレースケールデータの処理
    example1();

    // カラー画像（Smalltalk Balloon）の処理
    example2();

    // カラー画像（Earth）の処理
    example3();
  }
  
  /**
   * サンプル係数を取得し、変換処理を実行（グレースケール）
   * @param coefficientsOfSampledata：データのサンプル係数
   */
  protected static void example1() {
    // 連番
    fileNo = 100;

    // サンプル係数を取得
    double[][] coefficientsOfSampledata = Wavelet2dModel.dataSampleCoefficients();

    // 変換処理を実行（4×4ピクセル単位）
    perform(coefficientsOfSampledata, new Point(4, 4), 0);
  }
  
  /**
   * SmalltalkBalloonの画像に対する変換処理（カラー）
   * @param rgbData：SmalltalkBalloonのRGBデータ
   */
  protected static void example2() {
    // 連番
    fileNo = 200;

    // SmalltalkBalloonのRGBデータを取得
    double[][][] rgbData = Wavelet2dModel.dataSmalltalkBalloon();

    // カラー変換処理
    perform(rgbData, "Smalltalk Balloon");
  }
  
  /**
   * Earthの画像に対する変換処理（カラー）
   * @param rgbData：EarthのRGBデータ
   */
  protected static void example3() {
    // 連番
    fileNo = 300;

    // EarthのRGBデータを取得
    double[][][] rgbData = Wavelet2dModel.dataEarth();

    // カラー変換処理
    perform(rgbData, "Earth");
  }
  
  /**
   * デフォルトサイズでのパネルの表示
   */
  private static void open(JPanel aPanel) {
    open(aPanel, 512, 512);
  }
  
  /**
   * 指定サイズでのパネル表示
   * @param width         幅
   * @param height        高さ
   * @param spaceOfTitle  タイトルバーの余白
   */
  protected static void open(JPanel aPanel, int width, int height) {
    // タイトルをWavelet Example（2D）にしてフレームを用意
    JFrame jFrame = new JFrame("Wavelet Example (2D)");

    // paramJPanelをJFrameの表示エリアに追加する
    jFrame.getContentPane().add(aPanel);

    // ×ボタンを押すとそのウィンドウを閉じて破棄（他のウィンドウが開いていればそのウィンドウは継続）
    jFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    // jFrame.addNotify();

    // タイトルバーの上部オフセットを束縛
    int spaceOfTitle = (jFrame.getInsets()).top;

    // ウィンドウの最小サイズを設定
    jFrame.setMinimumSize(new Dimension(width / 2, height / 2 + spaceOfTitle));

    // ウィンドウのリサイズを可能にする
    jFrame.setResizable(true);

    // 初期サイズを設定
    jFrame.setSize(width, height + spaceOfTitle);

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
   * 各種変数を取得し、画像の生成、表示、保存する
   * @param coefficientsOfSampledata           データのサンプル係数
   * @param coefficientsOfScaling1             離散ウェーブレットのスケーリング係数
   * @param coefficientsOfHorizontalWavelet    水平方向の離散ウェーブレットのウェーブレット係数
   * @param coefficientsOfVerticalWavelet      垂直方向の離散ウェーブレットのウェーブレット係数
   * @param coefficientsOfDiagonalWavelet      対角方向の離散ウェーブレットのウェーブレット係数
   * @param waveletCoefficientsInAllDirections 水平・垂直・対角方向のウェーブレット係数をまとめたもの
   * @param coefficientsOfDisWavelet           逆ウェーブレット変換で得た係数
   * @param imageOfSampledata                  サンプル係数を元にした画像
   * @param imageOfScaling                     スケーリング係数を元にした画像
   * @param imageOfHorizontalWavelet           水平方向のウェーブレット係数を元にした画像
   * @param imageOfVerticalWavelet             垂直方向のウェーブレット係数を元にした画像
   * @param imageOfDiagonalWavelet             対角方向のウェーブレット係数を元にした画像
   * @param compositesImage                    bufferedImage2~6の4枚の画像を合成した画像
   * @param compositesDisImage1                一回目に復元した画像
   * @param compositesDisImage2                二回目に復元した画像
   */
  protected static double[][] perform(double[][] sourceDataMatrix, Point scaleFactor, int rgbFlag) {
    // 元画像の係数を束縛
    double[][] coefficientsOfSampledata = sourceDataMatrix;

    // 離散ウェーブレット変換を実行
    DiscreteWavelet2dTransformation discreteWavelet2dTransformation = new DiscreteWavelet2dTransformation(coefficientsOfSampledata);

    // 各種係数を束縛
    double[][] coefficientsOfScaling1 = discreteWavelet2dTransformation.scalingCoefficients();
    double[][] coefficientsOfHorizontalWavelet1 = discreteWavelet2dTransformation.horizontalWaveletCoefficients();
    double[][] coefficientsOfVerticalWavelet1 = discreteWavelet2dTransformation.verticalWaveletCoefficients();
    double[][] coefficientsOfDiagonalWavelet1 = discreteWavelet2dTransformation.diagonalWaveletCoefficients();

    // 各係数をもとに画像を生成
    BufferedImage imageOfSampledata1 = Wavelet2dModel.generateImage(coefficientsOfSampledata, scaleFactor, rgbFlag);
    BufferedImage imageOfScaling1 = Wavelet2dModel.generateImage(coefficientsOfScaling1, scaleFactor, rgbFlag);
    BufferedImage imageOfHorizontalWavelet1 = Wavelet2dModel.generateImage(coefficientsOfHorizontalWavelet1, scaleFactor, 0);
    BufferedImage imageOfVerticalWavelet1 = Wavelet2dModel.generateImage(coefficientsOfVerticalWavelet1, scaleFactor, 0);
    BufferedImage imageOfDiagonalWavelet1 = Wavelet2dModel.generateImage(coefficientsOfDiagonalWavelet1, scaleFactor, 0);

    // 画像の書き出し
    write(imageOfSampledata1);
    write(imageOfScaling1);
    write(imageOfHorizontalWavelet1);
    write(imageOfVerticalWavelet1);
    write(imageOfDiagonalWavelet1);

    // スケーリング係数、水平・垂直・対角方向のウェーブレット係数の画像をもとに画像を生成（元画像を生成）し、束縛
    BufferedImage compositesImage1 = Wavelet2dModel.generateImage(imageOfScaling1, imageOfHorizontalWavelet1, imageOfVerticalWavelet1, imageOfDiagonalWavelet1);

    // 元画像の書き出し
    write(compositesImage1);

    // もう一度スケーリング係数に対して離散ウェーブレット変換を実行
    discreteWavelet2dTransformation = new DiscreteWavelet2dTransformation(coefficientsOfScaling1);

    // もう一度スケーリング係数、水平・垂直・対角方向のウェーブレット係数を束縛
    double[][] coefficientsOfScaling2 = discreteWavelet2dTransformation.scalingCoefficients();
    double[][] coefficientsOfHorizontalWavelet2 = discreteWavelet2dTransformation.horizontalWaveletCoefficients();
    double[][] coefficientsOfVerticalWavelet2 = discreteWavelet2dTransformation.verticalWaveletCoefficients();
    double[][] coefficientsOfDiagonalWavelet2 = discreteWavelet2dTransformation.diagonalWaveletCoefficients();

    // 二回目の各係数をもとに画像を生成し、束縛
    BufferedImage imageOfScaling2 = Wavelet2dModel.generateImage(coefficientsOfScaling2, scaleFactor, rgbFlag);
    BufferedImage imageOfHorizontalWavelet2 = Wavelet2dModel.generateImage(coefficientsOfHorizontalWavelet2, scaleFactor, 0);
    BufferedImage imageOfVerticalWavelet2 = Wavelet2dModel.generateImage(coefficientsOfVerticalWavelet2, scaleFactor, 0);
    BufferedImage imageOfDiagonalWavelet2 = Wavelet2dModel.generateImage(coefficientsOfDiagonalWavelet2, scaleFactor, 0);

    // 二回目の画像の書き出し
    write(imageOfScaling2);
    write(imageOfHorizontalWavelet2);
    write(imageOfVerticalWavelet2);
    write(imageOfDiagonalWavelet2);

    // 二回目のスケーリング係数、水平・垂直・対角方向のウェーブレット係数の画像をもとに画像を生成（元画像を生成）し、束縛
    BufferedImage compositesImage2 = Wavelet2dModel.generateImage(imageOfScaling2, imageOfHorizontalWavelet2, imageOfVerticalWavelet2, imageOfDiagonalWavelet2);

    // 二回目の元画像を書き出し
    write(compositesImage2);

    // 二度目に生成した元画像と、一回目の水平・垂直・対角方向のウェーブレット係数の画像をもとに画像を生成し、束縛
    compositesImage2 = Wavelet2dModel.generateImage(compositesImage2, imageOfHorizontalWavelet1, imageOfVerticalWavelet1, imageOfDiagonalWavelet1);

    // 画像の書き出し
    write(compositesImage2);

    // 二回目の水平・垂直・対角のウェーブレット係数を束縛
    double[][][] waveletCoefficientsInAllDirections2 = { coefficientsOfHorizontalWavelet2, coefficientsOfVerticalWavelet2, coefficientsOfDiagonalWavelet2 };

    // 二回目のスケーリング係数、水平・垂直・対角方向のウェーブレット係数を用いて逆ウェーブレット変換を実行
    discreteWavelet2dTransformation = new DiscreteWavelet2dTransformation(coefficientsOfScaling2, waveletCoefficientsInAllDirections2);

    // 逆ウェーブレット変換を実行して得られた（元画像に相当する）係数を束縛
    double[][] coefficientsOfDisWavelet2 = discreteWavelet2dTransformation.recomposedCoefficients();

    // 元画像に相当する係数をもとに画像を生成し、束縛
    BufferedImage compositesDisImage1 = Wavelet2dModel.generateImage(coefficientsOfDisWavelet2, scaleFactor, rgbFlag);

    // 画像の書き出し
    write(compositesDisImage1);

    // 一回目の水平・垂直・対角方向のウェーブレット係数を束縛
    double[][][] waveletCoefficientsInAllDirections1 = { coefficientsOfHorizontalWavelet1, coefficientsOfVerticalWavelet1, coefficientsOfDiagonalWavelet1 };

    // 元画像に相当する係数と水平・垂直・対角方向のウェーブレット係数をもとに逆ウェーブレット変換を実行
    discreteWavelet2dTransformation = new DiscreteWavelet2dTransformation(coefficientsOfDisWavelet2, waveletCoefficientsInAllDirections1);

    // 逆ウェーブレット変換を実行して得られた（元画像に相当する）係数を束縛
    double[][] coefficientsOfDisWavelet1 = discreteWavelet2dTransformation.recomposedCoefficients();

    // 逆ウェーブレット変換を実行して得られた（元画像に相当する）係数をもとに画像を生成
    BufferedImage compositesDisImage2 = Wavelet2dModel.generateImage(coefficientsOfDisWavelet1, scaleFactor, rgbFlag);

    // 画像の書き出し
    write(compositesDisImage2);

    // GUIで4つの画像を表示するための設定
    GridBagLayout gridBagLayout = new GridBagLayout();
    JPanel jPanel = new JPanel(gridBagLayout);
    GridBagConstraints gridBagConstraints = new GridBagConstraints();

    // 空白は全て埋める
    gridBagConstraints.fill = 1;

    // 元画像のパネルのレイアウト構成
    WaveletPaneModel waveletPaneModel = new WaveletPaneModel(imageOfSampledata1, "Source Coefficients");
    WaveletPaneView waveletPaneView = new WaveletPaneView(waveletPaneModel);
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.gridwidth = 1;
    gridBagConstraints.gridheight = 1;
    gridBagConstraints.weightx = 0.5D;
    gridBagConstraints.weighty = 0.5D;
    gridBagLayout.setConstraints((Component)waveletPaneView, gridBagConstraints);
    jPanel.add((Component)waveletPaneView);

    // スケーリング係数と一回目のウェーブレット係数(水平・垂直・対角)の画像のパネルのレイアウト構成
    waveletPaneModel = new WaveletPaneModel(compositesImage1, "Scaling & Wavelet Coefficients");
    waveletPaneView = new WaveletPaneView(waveletPaneModel);
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.gridwidth = 1;
    gridBagConstraints.gridheight = 1;
    gridBagConstraints.weightx = 0.5D;
    gridBagConstraints.weighty = 0.5D;
    gridBagLayout.setConstraints((Component)waveletPaneView, gridBagConstraints);
    jPanel.add((Component)waveletPaneView);

    // 再構成した画像のパネルのレイアウト構成
    waveletPaneModel = new WaveletPaneModel(compositesDisImage2, "Recomposed Coefficients");
    waveletPaneView = new WaveletPaneView(waveletPaneModel);
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.gridwidth = 1;
    gridBagConstraints.gridheight = 1;
    gridBagConstraints.weightx = 0.5D;
    gridBagConstraints.weighty = 0.5D;
    gridBagLayout.setConstraints((Component)waveletPaneView, gridBagConstraints);
    jPanel.add((Component)waveletPaneView);

    // スケーリング係数と二回目のウェーブレット係数（水平・垂直・対角）の画像のパネルのレイアウト構成
    waveletPaneModel = new WaveletPaneModel(compositesImage2, "Scaling & Wavelet Coefficients");
    waveletPaneView = new WaveletPaneView(waveletPaneModel);
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.gridwidth = 1;
    gridBagConstraints.gridheight = 1;
    gridBagConstraints.weightx = 0.5D;
    gridBagConstraints.weighty = 0.5D;
    gridBagLayout.setConstraints((Component)waveletPaneView, gridBagConstraints);
    jPanel.add((Component)waveletPaneView);

    //  グレースケール画像の行列サイズとスケーリング係数から、パネルの幅と高さを計算して表示する
    open(jPanel, sourceDataMatrix.length * scaleFactor.x, (sourceDataMatrix[0]).length * scaleFactor.y);

    // 再構成した画像を返す
    return coefficientsOfDisWavelet1;
  }
  
  /**
   * 4つの2次元配列を入力として画像処理を行い、結果を表示・保存する
   * @param lrgbSourceCoefficients 入力データ
   * @param labelString            画像・ファイル名
   * @param originalDataMatrix     元画像のデータの行列
   * @param redDataMatrix          赤要素のデータの行列
   * @param greenDataMatrix        緑要素のデータの行列
   * @param blueDataMatrix         青要素のデータの行列
   * @param disOriginalDataMatrix  逆変換に用いる元画像のデータの行列
   * @param disRedDataMatrix       逆変換に用いる赤要素のデータの行列
   * @param disGreenDataMatrix     逆変換に用いる緑要素のデータの行列
   * @param disBlueDataMatrix      逆変換に用いる青要素のデータの行列
   */
  protected static void perform(double[][][] lrgbSourceCoefficients, String labelString) {
    // 元画像と赤と青と緑（上から順に）の要素を束縛
    double[][] originalDataMatrix = lrgbSourceCoefficients[0];
    double[][] redDataMatrix = lrgbSourceCoefficients[1];
    double[][] greenDataMatrix = lrgbSourceCoefficients[2];
    double[][] blueDataMatrix = lrgbSourceCoefficients[3];

    // スケーリング係数として（1, 1）を使用
    Point point = new Point(1, 1);

    // 各成分ごとの逆変換の準備処理
    double[][] disOriginalDataMatrix = perform(originalDataMatrix, point, 0);
    double[][] disRedDataMatrix = perform(redDataMatrix, point, 1);
    double[][] disGreenDataMatrix = perform(greenDataMatrix, point, 2);
    double[][] disBlueDataMatrix = perform(blueDataMatrix, point, 3);

    // 画像の出力サイズを取得
    int height = disOriginalDataMatrix.length;
    int width = (disOriginalDataMatrix[0]).length;

    // RGB画像を束縛するBufferedImageを生成
    BufferedImage colorImage = new BufferedImage(height, width, 1);

    // 各ピクセルにRGB値を設定
    for (byte b = 0; b < width; b++) {
      for (byte b1 = 0; b1 < height; b1++) {
        double red = disRedDataMatrix[b1][b];     // R成分
        double green = disGreenDataMatrix[b1][b]; // G成分
        double blue = disBlueDataMatrix[b1][b];   // B成分

        // RGBを整数値に変換
        int rgb = ColorUtility.convertRGBtoINT(red, green, blue);

        // 画像にビクセル値を設定
        colorImage.setRGB(b1, b, rgb);
      } 
    } 

    // 画像の書き出し
    write(colorImage);

    // モデルとビューを作成して画像を表示
    WaveletPaneModel waveletPaneModel = new WaveletPaneModel(colorImage, labelString);
    WaveletPaneView waveletPaneView = new WaveletPaneView(waveletPaneModel);

    // 画像サイズに基づいて表示パネルを開く
    open((JPanel)waveletPaneView, originalDataMatrix.length, (originalDataMatrix[0]).length);
  }
  
  /**
  * 画像ファイルを保存
  * @param fileNumber ファイル名の連番（001, 002, 003,,）
  * @param file 書き出す用のファイル
  */
  protected static void write(BufferedImage anImage) {
    File file = new File("ResultImages");


    // ファイルが存在するか
    this.ifThenElse(file);

    // ファイル名の作成（連番）
    String fileNumber = String.format("%03d", fileNo++);

    // 画像ファイルを書き出す
    ImageUtility.writeImage(anImage, file.getName() + "/Wavelet" + fileNumber + ".jpg");

    // if (!file.exists())
    //   file.mkdir(); 

    // String str;
    // for (str = Integer.toString(fileNo++); str.length() < 3; str = "0" + str);
    // ImageUtility.writeImage(paramBufferedImage, file.getName() + "/Wavelet" + file.getName() + ".jpg");
    
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

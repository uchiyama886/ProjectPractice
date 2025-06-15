package wavelet;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.util.Arrays;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import utility.ColorUtility;
import utility.Condition;
import utility.FileUtility;
import utility.ImageUtility;
import utility.Interval;

/**
 * 2次ウェーブレット変換のモデルクラス このクラスは、2次元ウェーブレット変換のデータと、その変換に関連する操作を管理する。
 * ウェーブレット変換の結果を保持し、再構成、係数の操作、および表示用の画像生成機能を提供する。
 */
public final class Wavelet2dModel extends WaveletModel {

    // 読み込み時の最大画像長辺ピクセル数 (2のべき乗にリサイズ後も大きくならない)
    private static final int MAX_IMAGE_DIMENSION = 1024;

    // 各種の係数配列における絶対値の最大値を保持するフィールド。
    // 画像表示時の正規化などに使用される。
    protected double maximumAbsoluteSourceCoefficient = Double.MIN_VALUE; // 元データの絶対値の最大値
    protected double maximumAbsoluteScalingCoefficient = Double.MIN_VALUE; // スケーリング係数の絶対値の最大値
    protected double maximumAbsoluteWaveletCoefficient = Double.MIN_VALUE; // ウェーブレット係数の絶対値の最大値
    protected double maximumAbsoluteRecomposedCoefficient = Double.MIN_VALUE; // 再構成された係数の絶対値の最大値

    // ウェーブレット変換に関連する各種係数を保持する3次元配列。
    // 各配列は、RGBチャネル（またはLuminanceとRGBチャネル）ごとに係数を格納する。
    protected double[][][] sourceCoefficientsArray; // 元の係数（画像データなど）
    protected double[][][] scalingCoefficientsArray; // スケーリング係数（近似情報）
    protected double[][][] horizontalWaveletCoefficientsArray; // 水平方向のウェーブレット係数（水平方向のエッジ情報）
    protected double[][][] verticalWaveletCoefficientsArray; // 垂直方向のウェーブレット係数（垂直方向のエッジ情報）
    protected double[][][] diagonalWaveletCoefficientsArray; // 対角方向のウェーブレット係数（対角方向のエッジ情報）
    protected double[][][] interactiveHorizontalWaveletCoefficientsArray; // 対話操作用の水平ウェーブレット係数（一部を操作して再構成する用）
    protected double[][][] interactiveVerticalWaveletCoefficientsArray; // 対話操作用の垂直ウェーブレット係数
    protected double[][][] interactiveDiagonalWaveletCoefficientsArray; // 対話操作用の対角ウェーブレット係数
    protected double[][][] recomposedCoefficientsArray; // 再構成された係数

    // 各種係数データを表示するためのモデルオブジェクト。
    // それぞれのパネルに表示される画像データを管理する。
    protected WaveletPaneModel sourceCoefficientsPaneModel = null; // 元の係数表示用モデル
    protected WaveletPaneModel scalingAndWaveletCoefficientsPaneModel = null; // スケーリング係数とウェーブレット係数表示用モデル
    protected WaveletPaneModel interactiveScalingAndWaveletCoefficientsPaneModel = null; // 対話操作用のスケーリング・ウェーブレット係数表示用モデル
    protected WaveletPaneModel recomposedCoefficientsPaneModel = null; // 再構成された係数表示用モデル

    /**
     * Wavelet2dModelのコンストラクタ。 インスタンス生成時にサンプル係数データをロードして初期化する。
     */
    public Wavelet2dModel() {
        doSampleCoefficients();
    }

    /**
     * アクションイベントを処理する。 メニュー項目からのコマンドに応じて、異なる画像データの設定や係数操作を実行する。
     *
     * @param anActionEvent 発生したアクションイベント
     */
    public void actionPerformed(ActionEvent anActionEvent) {
        String commandString = anActionEvent.getActionCommand();
        new Condition.Switch()
                .addCase(() -> "Change Image".equals(commandString), () -> { // "Change Image"の場合のケース
                    JFileChooser chooser = new JFileChooser();
                    int result = chooser.showOpenDialog(null);
                    Condition.ifTrue(() -> result == JFileChooser.APPROVE_OPTION, () -> { // resultがJFileChooser.APPROVE_OPTIONの場合のifTrue
                        File file = chooser.getSelectedFile();
                        BufferedImage img = ImageUtility.readImage(file.getAbsolutePath());
                    });
                    doInputImage(); // このアクションはif文の外側にあったため、ケースのラムダ式の最後に追加
                })
                .addCase(() -> "sample coefficients".equals(commandString), () -> { // "sample coefficients"の場合のケース
                    doSampleCoefficients();
                })
                .addCase(() -> "smalltalk balloon".equals(commandString), () -> { // "smalltalk balloon"の場合のケース
                    doSmalltalkBalloon();
                })
                .addCase(() -> "earth".equals(commandString), () -> { // "earth"の場合のケース
                    doEarth();
                })
                .addCase(() -> "all coefficients".equals(commandString), () -> { // "all coefficients"の場合のケース
                    doAllCoefficients();
                })
                .addCase(() -> "clear coefficients".equals(commandString), () -> { // "clear coefficients"の場合のケース
                    doClearCoefficients();
                })
                .evaluate(); // 作成したSwitch文を評価し、対応するアクションを実行する
    }

    /**
     * 指定された点に基づいてインタラクティブなウェーブレット係数を計算する。
     * このメソッドは、マウスイベントによって呼び出され、指定された点周辺の係数を操作する。
     *
     * @param aPoint マウスイベントが発生した座標
     * @param isAltDown Altキーが押されているかどうか（trueの場合、係数をクリア）
     */
    public void computeFromPoint(Point aPoint, boolean isAltDown) {
        new Interval<>(0, (channelIndex) -> channelIndex < this.sourceCoefficientsArray.length, (channelIndex) -> channelIndex + 1).forEach(channelIndex -> { //
            double[][] sourceData = this.sourceCoefficientsArray[channelIndex];
            Condition.ifTrue(() -> sourceData != null, () -> { // データが存在する場合のみ処理
                double[][] scalingCoefficients = this.scalingCoefficientsArray[channelIndex];
                Integer coefficientWidth = scalingCoefficients.length; // スケーリング係数行列の幅
                Integer coefficientHeight = (scalingCoefficients[0]).length; // スケーリング係数行列の高さ
                Integer mousePointX = aPoint.x % coefficientWidth; // マウスX座標を係数行列の幅で正規化
                Integer mousePointY = aPoint.y % coefficientHeight; // マウスY座標を係数行列の高さで正規化

                // 操作範囲の決定
                final int[] range = {2}; // デフォルトは -2 から +2 (5x5)

                // 画像サイズがリサイズされている（縦か横のピクセルが1023,1024の時）場合には、扱う係数の範囲を22に変更する
                Condition.ifThenElse(() -> coefficientWidth >= 1023 || coefficientHeight >= 1023, () -> {
                    range[0] = 10; // -10 から +10 (21x21) または -11から+10（22*22）
                },() -> {
                    // 何もしない（デフォルトのrange[0]が2のまま）
                });

                int startOffset = -range[0];
                int endOffset = range[0];


                // 各ウェーブレット係数配列を取得
                double[][] horizontalCoefficients = this.horizontalWaveletCoefficientsArray[channelIndex];
                double[][] verticalCoefficients = this.verticalWaveletCoefficientsArray[channelIndex];
                double[][] diagonalCoefficients = this.diagonalWaveletCoefficientsArray[channelIndex];
                double[][] interactiveHorizontalCoefficients = this.interactiveHorizontalWaveletCoefficientsArray[channelIndex];
                double[][] interactiveVerticalCoefficients = this.interactiveVerticalWaveletCoefficientsArray[channelIndex];
                double[][] interactiveDiagonalCoefficients = this.interactiveDiagonalWaveletCoefficientsArray[channelIndex];

                // 指定された点から決定された範囲の係数を操作
                new Interval<>(startOffset, (rowOffset) -> rowOffset <= endOffset, (rowOffset) -> rowOffset + 1).forEach(rowOffset -> {
                    new Interval<>(startOffset, (colOffset) -> colOffset <= endOffset, (colOffset) -> colOffset + 1).forEach(colOffset -> {
                        // 座標を配列の範囲内に制限
                        final Integer adjustedX = Math.min(Math.max(0, mousePointX + colOffset), coefficientWidth - 1);
                        final Integer adjustedY = Math.min(Math.max(0, mousePointY + rowOffset), coefficientHeight - 1);
                        Condition.ifThenElse(() -> isAltDown, () -> {
                            // Altキーが押されている場合、係数を0にクリア
                            interactiveHorizontalCoefficients[adjustedX][adjustedY] = 0.0D;
                            interactiveVerticalCoefficients[adjustedX][adjustedY] = 0.0D;
                            interactiveDiagonalCoefficients[adjustedX][adjustedY] = 0.0D;
                        },() -> {
                            // Altキーが押されていない場合、元の係数にコピー
                            interactiveHorizontalCoefficients[adjustedX][adjustedY] = horizontalCoefficients[adjustedX][adjustedY];
                            interactiveVerticalCoefficients[adjustedX][adjustedY] = verticalCoefficients[adjustedX][adjustedY];
                            interactiveDiagonalCoefficients[adjustedX][adjustedY] = diagonalCoefficients[adjustedX][adjustedY];
                        });
                    });
                });
            });
        });
        computeRecomposedCoefficients(); // 係数変更後、再構成画像を更新
    }

    /**
     * インタラクティブなウェーブレット係数を使用して画像を再構成する。 再構成された係数から画像を生成し、関連する表示モデルを更新する。
     */
    @Override
    public void computeRecomposedCoefficients() {
        this.maximumAbsoluteRecomposedCoefficient = Double.MIN_VALUE; // 再構成係数の最大絶対値をリセット

        // 各チャネルに対して再構成処理を繰り返す
        new Interval<>(0, (channelIndex) -> channelIndex < this.sourceCoefficientsArray.length, (channelIndex) -> channelIndex + 1).forEach(channelIndex -> {
            double[][] sourceData = this.sourceCoefficientsArray[channelIndex];
            Condition.ifTrue(() -> sourceData != null, () -> { // データが存在する場合のみ処理
                double[][] scalingCoefficients = this.scalingCoefficientsArray[channelIndex]; // スケーリング係数
                // インタラクティブなウェーブレット係数をまとめる
                double[][][] interactiveWaveletCoefficients = {this.interactiveHorizontalWaveletCoefficientsArray[channelIndex], this.interactiveVerticalWaveletCoefficientsArray[channelIndex], this.interactiveDiagonalWaveletCoefficientsArray[channelIndex]};
                // 離散ウェーブレット2D変換オブジェクトを生成し、逆変換（再構成）を実行
                DiscreteWavelet2dTransformation discreteWavelet2dTransformation = new DiscreteWavelet2dTransformation(scalingCoefficients, interactiveWaveletCoefficients);
                double[][] recomposedResult = discreteWavelet2dTransformation.recomposedCoefficients(); // 再構成された係数
                this.recomposedCoefficientsArray[channelIndex] = recomposedResult; // 結果を保存
            });
        });

        // 各係数配列から表示用の画像を生成
        BufferedImage scalingImage = generateImage(this.scalingCoefficientsArray, maximumAbsoluteScalingCoefficient()); // スケーリング係数画像
        BufferedImage interactiveHorizontalImage = generateImage(this.interactiveHorizontalWaveletCoefficientsArray, maximumAbsoluteWaveletCoefficient()); // 対話操作用水平ウェーブレット係数画像
        BufferedImage interactiveVerticalImage = generateImage(this.interactiveVerticalWaveletCoefficientsArray, maximumAbsoluteWaveletCoefficient()); // 対話操作用垂直ウェーブレット係数画像
        BufferedImage interactiveDiagonalImage = generateImage(this.interactiveDiagonalWaveletCoefficientsArray, maximumAbsoluteWaveletCoefficient()); // 対話操作用対角ウェーブレット係数画像
        BufferedImage combinedWaveletImage = generateImage(scalingImage, interactiveHorizontalImage, interactiveVerticalImage, interactiveDiagonalImage); // 結合されたウェーブレット係数画像
        BufferedImage recomposedImage = generateImage(this.recomposedCoefficientsArray, maximumAbsoluteRecomposedCoefficient()); // 再構成された係数画像

        // 対応する表示モデルの画像を更新
        this.interactiveScalingAndWaveletCoefficientsPaneModel.picture(combinedWaveletImage);
        this.recomposedCoefficientsPaneModel.picture(recomposedImage);

        // 表示モデルに変更を通知し、GUIを再描画
        this.interactiveScalingAndWaveletCoefficientsPaneModel.changed();
        this.recomposedCoefficientsPaneModel.changed();
    }

    /**
     * サンプルの2次元係数データ（四角い枠と対角線）を生成する。 このデータは初期表示やデモンストレーションに使用される。
     *
     * @return サンプルの2次元係数行列
     */
    public static double[][] dataSampleCoefficients() {
        Integer matrixSize = 64; // 行列のサイズ（64x64）
        double[][] coefficientMatrix = new double[matrixSize][matrixSize];
        Integer loopIndex;
        // 全ての要素を0.2で埋める
        for (loopIndex = 0; loopIndex < coefficientMatrix.length; loopIndex++) {
            Arrays.fill(coefficientMatrix[loopIndex], 0.2D);
        }
        // 四角い枠と対角線を描画する
        for (loopIndex = 5; loopIndex < matrixSize - 5; loopIndex++) {
            coefficientMatrix[5][loopIndex] = 1.0D; // 上の横線
            coefficientMatrix[matrixSize - 6][loopIndex] = 1.0D; // 下の横線
            coefficientMatrix[loopIndex][5] = 1.0D; // 左の縦線
            coefficientMatrix[loopIndex][matrixSize - 6] = 1.0D; // 右の縦線
            coefficientMatrix[loopIndex][loopIndex] = 1.0D; // 左上から右下への対角線
            coefficientMatrix[loopIndex][matrixSize - loopIndex - 1] = 1.0D; // 右上から左下への対角線
        }
        return coefficientMatrix;
    }

    /**
     * 地球の画像データを取得し、それを輝度およびRGB成分の係数行列に変換する。
     *
     * @return 地球画像の輝度およびRGB係数行列
     */
    public static double[][][] dataEarth() {
        BufferedImage earthImage = imageEarth(); // 地球画像をロード
        return lrgbMatrixes(earthImage); // RGBをLuminanceとRGB行列に変換
    }

    /**
     * Smalltalk Balloonの画像データを取得し、それを輝度およびRGB成分の係数行列に変換する。
     *
     * @return Smalltalk Balloon画像の輝度およびRGB係数行列
     */
    public static double[][][] dataSmalltalkBalloon() {
        BufferedImage balloonImage = imageSmalltalkBalloon(); // Smalltalk Balloon画像をロード
        return lrgbMatrixes(balloonImage); // RGBをLuminanceとRGB行列に変換
    }

    /**
     * 入力画像データを取得し、それを輝度およびRGB成分の係数行列に変換する。
     *
     * @return 入力画像の輝度およびRGB係数行列
     */
    public static double[][][] dataInput() {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showOpenDialog(null);

        BufferedImage inputImage = null; // 変数をメソッドスコープで宣言
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            inputImage = ImageUtility.readImage(file.getAbsolutePath());
        }
        // 画像が読み込まれたら、幅・高さを2のべき乗にリサイズ
        if (inputImage != null) {
            int w = inputImage.getWidth();
            int h = inputImage.getHeight();
            // 高解像度画像をヒープ節約のため、最大寸法にリサイズ
            if (w > MAX_IMAGE_DIMENSION || h > MAX_IMAGE_DIMENSION) {
                double scale = (double) MAX_IMAGE_DIMENSION / Math.max(w, h);
                int rw = (int) (w * scale);
                int rh = (int) (h * scale);
                BufferedImage tmp = new BufferedImage(rw, rh, inputImage.getType());
                Graphics2D gTmp = tmp.createGraphics();
                gTmp.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                gTmp.drawImage(inputImage, 0, 0, rw, rh, null);
                gTmp.dispose();
                inputImage = tmp; // 更新
                w = rw;
                h = rh;
            }
            // 縦横比を保ったまま、長辺を2のべき乗サイズに拡大/縮小
            int maxDim = Math.max(w, h);
            int p2 = nextPowerOfTwo(maxDim);
            int tw = (int) Math.round((double) w * p2 / maxDim);
            int th = (int) Math.round((double) h * p2 / maxDim);
            if (tw != w || th != h) {
                BufferedImage resized = new BufferedImage(tw, th, inputImage.getType());
                Graphics2D g2 = resized.createGraphics();
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g2.drawImage(inputImage, 0, 0, tw, th, null);
                g2.dispose();
                inputImage = resized;
            }
        }
        return lrgbMatrixes(inputImage); // RGBをLuminanceとRGB行列に変換
    }

    /**
     * 全てのウェーブレット係数をインタラクティブな係数配列にコピーする。
     * これにより、再構成時に全てのウェーブレット情報が使用され、元の画像が再構成される。
     */
    public void doAllCoefficients() {
        // 各チャネルに対して処理を繰り返す
        for (Integer channelIndex = 0; channelIndex < this.sourceCoefficientsArray.length; channelIndex++) {
            double[][] sourceData = this.sourceCoefficientsArray[channelIndex];
            if (sourceData != null) { // データが存在する場合のみ処理
                double[][] scalingCoefficients = this.scalingCoefficientsArray[channelIndex];
                Integer coefficientWidth = scalingCoefficients.length; // 幅
                Integer coefficientHeight = (scalingCoefficients[0]).length; // 高さ

                // 各ウェーブレット係数配列を取得
                double[][] horizontalCoefficients = this.horizontalWaveletCoefficientsArray[channelIndex];
                double[][] verticalCoefficients = this.verticalWaveletCoefficientsArray[channelIndex];
                double[][] diagonalCoefficients = this.diagonalWaveletCoefficientsArray[channelIndex];
                // インタラクティブなウェーブレット係数配列
                double[][] interactiveHorizontalCoefficients = this.interactiveHorizontalWaveletCoefficientsArray[channelIndex];
                double[][] interactiveVerticalCoefficients = this.interactiveVerticalWaveletCoefficientsArray[channelIndex];
                double[][] interactiveDiagonalCoefficients = this.interactiveDiagonalWaveletCoefficientsArray[channelIndex];

                // 全てのウェーブレット係数をインタラクティブな係数配列にコピー
                for (Integer rowIndex = 0; rowIndex < coefficientHeight; rowIndex++) {
                    for (Integer colIndex = 0; colIndex < coefficientWidth; colIndex++) {
                        interactiveHorizontalCoefficients[colIndex][rowIndex] = horizontalCoefficients[colIndex][rowIndex];
                        interactiveVerticalCoefficients[colIndex][rowIndex] = verticalCoefficients[colIndex][rowIndex];
                        interactiveDiagonalCoefficients[colIndex][rowIndex] = diagonalCoefficients[colIndex][rowIndex];
                    }
                }
            }
        }
        computeRecomposedCoefficients(); // 係数変更後、再構成画像を更新
    }

    /**
     * インタラクティブなウェーブレット係数を全て0にクリアする。
     * これにより、再構成時にはスケーリング係数のみが使用され、画像はぼやけた（低周波成分のみの）状態になる。
     */
    public void doClearCoefficients() {
        // 各チャネルに対して処理を繰り返す
        for (Integer channelIndex = 0; channelIndex < this.sourceCoefficientsArray.length; channelIndex++) {
            double[][] sourceData = this.sourceCoefficientsArray[channelIndex];
            if (sourceData != null) { // データが存在する場合のみ処理
                double[][] scalingCoefficients = this.scalingCoefficientsArray[channelIndex];
                Integer coefficientWidth = scalingCoefficients.length; // 幅
                Integer coefficientHeight = (scalingCoefficients[0]).length; // 高さ

                // インタラクティブなウェーブレット係数配列
                double[][] interactiveHorizontalCoefficients = this.interactiveHorizontalWaveletCoefficientsArray[channelIndex];
                double[][] interactiveVerticalCoefficients = this.interactiveVerticalWaveletCoefficientsArray[channelIndex];
                double[][] interactiveDiagonalCoefficients = this.interactiveDiagonalWaveletCoefficientsArray[channelIndex];

                // 全てのインタラクティブなウェーブレット係数を0にクリア
                for (Integer rowIndex = 0; rowIndex < coefficientHeight; rowIndex++) {
                    for (Integer colIndex = 0; colIndex < coefficientWidth; colIndex++) {
                        interactiveHorizontalCoefficients[colIndex][rowIndex] = 0.0D;
                        interactiveVerticalCoefficients[colIndex][rowIndex] = 0.0D;
                        interactiveDiagonalCoefficients[colIndex][rowIndex] = 0.0D;
                    }
                }
            }
        }
        computeRecomposedCoefficients(); // 係数変更後、再構成画像を更新
    }

    /**
     * 地球の画像データをソースデータとして設定する。
     */
    public void doEarth() {
        setSourceData(dataEarth());
    }

    /**
     * サンプルの係数データをソースデータとして設定する。
     */
    public void doSampleCoefficients() {
        setSourceData(dataSampleCoefficients());
    }

    /**
     * Smalltalk Balloonの画像データをソースデータとして設定する。
     */
    public void doSmalltalkBalloon() {
        setSourceData(dataSmalltalkBalloon());
    }

    public void doInputImage() {
        setSourceData(dataInput()); // メソッド名のスペル修正
    }

    /**
     * 指定された行列を特定の値で埋める。
     *
     * @param aMatrix 埋める対象の2次元行列
     * @param fillValue 埋める値
     */
    public static void fill(double[][] aMatrix, double fillValue) {
        for (Integer rowIndex = 0; rowIndex < aMatrix.length; rowIndex++) {
            double[] rowArray = aMatrix[rowIndex];
            Arrays.fill(rowArray, fillValue); // JavaのArrays.fillを使用して行を埋める
        }
    }

    /**
     * 3次元の係数行列（例：LuminanceまたはRGB各チャネル）からBufferedImageを生成する。 輝度画像またはRGB画像を生成する。
     *
     * @param valueMatrixArray 輝度またはRGBチャネルの係数行列配列
     * @param maxValue 係数の絶対値の最大値（正規化用）
     * @return 生成されたBufferedImage
     */
    public static BufferedImage generateImage(double[][][] valueMatrixArray, double maxValue) {
        double[][] firstChannelMatrix = valueMatrixArray[0]; // 最初のチャネル（通常は輝度またはR）
        Integer imageWidth = firstChannelMatrix.length; // 幅
        Integer imageHeight = (firstChannelMatrix[0]).length; // 高さ
        BufferedImage bufferedImage = new BufferedImage(imageWidth, imageHeight, 1); // TYPE_INT_RGB (1)
        Graphics2D graphics2D = bufferedImage.createGraphics();

        // 2番目以降のチャネルがnullの場合（輝度画像の場合など）、グレースケール画像を生成
        if (valueMatrixArray[1] == null || valueMatrixArray[2] == null || valueMatrixArray[3] == null) {
            for (Integer rowIndex = 0; rowIndex < imageHeight; rowIndex++) {
                for (Integer colIndex = 0; colIndex < imageWidth; colIndex++) {
                    double absoluteValue = Math.abs(firstChannelMatrix[colIndex][rowIndex]); // 係数の絶対値
                    Integer grayValue = (Integer) Math.round((float) (absoluteValue / maxValue * 255.0D)); // 0-255に正規化
                    Color pixelColor = new Color(grayValue, grayValue, grayValue); // グレースケールカラー
                    graphics2D.setColor(pixelColor);
                    graphics2D.fillRect(colIndex, rowIndex, 1, 1); // 1x1ピクセルを塗りつぶす
                }
            }
        } else { // RGBチャネルが存在する場合、カラー画像を生成
            // これらの配列は使用されていないが、元のコードに存在するため保持
            Integer[][] redValues = new Integer[imageWidth][imageHeight];
            Integer[][] greenValues = new Integer[imageWidth][imageHeight];
            Integer[][] blueValues = new Integer[imageWidth][imageHeight];

            for (Integer rowIndex = 0; rowIndex < imageHeight; rowIndex++) {
                for (Integer colIndex = 0; colIndex < imageWidth; colIndex++) {
                    double redCoefficient = Math.abs(valueMatrixArray[1][colIndex][rowIndex]); // Rチャネル
                    Integer redValue = (Integer) Math.round((float) (redCoefficient / maxValue * 255.0D));
                    double greenCoefficient = Math.abs(valueMatrixArray[2][colIndex][rowIndex]); // Gチャネル
                    Integer greenValue = (Integer) Math.round((float) (greenCoefficient / maxValue * 255.0D));
                    double blueCoefficient = Math.abs(valueMatrixArray[3][colIndex][rowIndex]); // Bチャネル
                    Integer blueValue = (Integer) Math.round((float) (blueCoefficient / maxValue * 255.0D));
                    Color pixelColor = new Color(redValue, greenValue, blueValue); // RGBカラー
                    graphics2D.setColor(pixelColor);
                    graphics2D.fillRect(colIndex, rowIndex, 1, 1);
                }
            }
        }
        return bufferedImage;
    }

    /**
     * 2次元の係数行列からBufferedImageを生成する。 主に個々のウェーブレット係数やスケーリング係数を視覚化するために使用される。
     *
     * @param valueMatrix 係数行列
     * @param scaleFactor スケールファクター（Point.x, Point.yでピクセルサイズを指定）
     * @param rgbFlag RGBチャネルを指定するフラグ (0: グレースケール, 1: 赤, 2: 緑, 3: 青)
     * @return 生成されたBufferedImage
     */
    public static BufferedImage generateImage(double[][] valueMatrix, Point scaleFactor, Integer rgbFlag) {
        Integer matrixWidth = valueMatrix.length; // 幅
        Integer matrixHeight = (valueMatrix[0]).length; // 高さ
        Integer imageOutputWidth = matrixWidth * scaleFactor.x; // 画像の幅（スケール適用後）
        Integer imageOutputHeight = matrixHeight * scaleFactor.y; // 画像の高さ（スケール適用後）
        BufferedImage bufferedImage = new BufferedImage(imageOutputWidth, imageOutputHeight, 1); // TYPE_INT_RGB (1)
        Graphics2D graphics2D = bufferedImage.createGraphics();

        // 係数行列の絶対値の最大値を計算（正規化用）
        double maxAbsoluteValue = Double.MIN_VALUE;
        Integer rowIndex;
        for (rowIndex = 0; rowIndex < matrixHeight; rowIndex++) {
            for (Integer colIndex = 0; colIndex < matrixWidth; colIndex++) {
                double currentValue = Math.abs(valueMatrix[colIndex][rowIndex]);
                maxAbsoluteValue = Math.max(currentValue, maxAbsoluteValue);
            }
        }

        // ピクセルごとに色を設定して画像を生成
        for (rowIndex = 0; rowIndex < matrixHeight; rowIndex++) {
            for (Integer colIndex = 0; colIndex < matrixWidth; colIndex++) {
                double currentValue = Math.abs(valueMatrix[colIndex][rowIndex]);
                Integer normalizedValue = (Integer) Math.round((float) (currentValue / maxAbsoluteValue * 255.0D)); // 0-255に正規化
                Color pixelColor = new Color(normalizedValue, normalizedValue, normalizedValue); // デフォルトはグレースケール
                if (rgbFlag == 1) // 赤チャネル
                {
                    pixelColor = new Color(normalizedValue, 0, 0);
                }
                if (rgbFlag == 2) // 緑チャネル
                {
                    pixelColor = new Color(0, normalizedValue, 0);
                }
                if (rgbFlag == 3) // 青チャネル
                {
                    pixelColor = new Color(0, 0, normalizedValue);
                }
                graphics2D.setColor(pixelColor);
                // スケールファクターを考慮してピクセルを塗りつぶす
                graphics2D.fillRect(colIndex * scaleFactor.x, rowIndex * scaleFactor.y, scaleFactor.x, scaleFactor.y);
            }
        }
        return bufferedImage;
    }

    /**
     * 4つのBufferedImage（スケーリング係数、水平、垂直、対角ウェーブレット係数）を結合して
     * 1つの大きなBufferedImageを生成する。 このメソッドは、ウェーブレット変換の各成分を視覚的に並べて表示するために使用される。
     *
     * 生成される画像レイアウトは以下の通り:
     * +--------------------------------+--------------------------------------+
     * | imageScalingCoefficients | imageHorizontalWaveletCoeffixcients |
     * +--------------------------------+--------------------------------------+
     * | imageVerticalWaveletCoefficients | imageDiagonalWaveletCoefficients |
     * +--------------------------------+--------------------------------------+
     *
     * @param imageScalingCoefficients スケーリング係数の画像
     * @param imageHorizontalWaveletCoeffixcients 水平ウェーブレット係数の画像
     * @param imageVerticalWaveletCoefficients 垂直ウェーブレット係数の画像
     * @param imageDiagonalWaveletCoefficients 対角ウェーブレット係数の画像
     * @return 結合されたBufferedImage
     */
    public static BufferedImage generateImage(BufferedImage imageScalingCoefficients,
            BufferedImage imageHorizontalWaveletCoeffixcients, BufferedImage imageVerticalWaveletCoefficients,
            BufferedImage imageDiagonalWaveletCoefficients) {
        Integer scalingImageWidth = imageScalingCoefficients.getWidth(); // スケーリング係数画像の幅
        Integer scalingImageHeight = imageScalingCoefficients.getHeight(); // スケーリング係数画像の高さ
        Integer combinedImageWidth = scalingImageWidth + imageHorizontalWaveletCoeffixcients.getWidth(); // 全体の幅 (スケーリング + 水平)
        Integer combinedImageHeight = scalingImageHeight + imageVerticalWaveletCoefficients.getHeight(); // 全体の高さ (スケーリング + 垂直)
        BufferedImage bufferedImage = new BufferedImage(combinedImageWidth, combinedImageHeight, 1); // TYPE_INT_RGB (1)
        Graphics2D graphics2D = bufferedImage.createGraphics();

        // 各画像を適切な位置に描画
        graphics2D.drawImage(imageScalingCoefficients, 0, 0, (ImageObserver) null); // 左上
        graphics2D.drawImage(imageHorizontalWaveletCoeffixcients, scalingImageWidth, 0, (ImageObserver) null); // 右上
        graphics2D.drawImage(imageVerticalWaveletCoefficients, 0, scalingImageHeight, (ImageObserver) null); // 左下
        graphics2D.drawImage(imageDiagonalWaveletCoefficients, scalingImageWidth, scalingImageHeight, (ImageObserver) null); // 右下
        return bufferedImage;
    }

    /**
     * 地球のサンプル画像をファイルから読み込む。
     *
     * @return 読み込まれたBufferedImage
     */
    public static BufferedImage imageEarth() {
        String imagePath = "SampleImages/imageEarth512x256.jpg"; // 画像ファイルのパス
        return FileUtility.readImageFromResource(imagePath); // FileUtilityを使用して画像を読み込む
    }

    /**
     * Smalltalk Balloonのサンプル画像をファイルから読み込む。
     *
     * @return 読み込まれたBufferedImage
     */
    public static BufferedImage imageSmalltalkBalloon() {
        String imagePath = "SampleImages/imageSmalltalkBalloon256x256.jpg"; // 画像ファイルのパス
        return FileUtility.readImageFromResource(imagePath); // FileUtilityを使用して画像を読み込む
    }

    /**
     * BufferedImageを輝度（Luminance）とRGB（赤、緑、青）の各成分を表す 2次元行列の配列に変換する。
     *
     * @param anImage 変換するBufferedImage
     * @return 輝度およびRGB成分のdouble型2次元行列の配列。 配列のインデックス0: 輝度, 1: R, 2: G, 3: B
     */
    public static double[][][] lrgbMatrixes(BufferedImage anImage) {
        Integer imageWidth = anImage.getWidth(); // 画像の幅
        Integer imageHeight = anImage.getHeight(); // 画像の高さ

        // 各成分の行列を初期化
        double[][] luminanceMatrix = new double[imageWidth][imageHeight]; // 輝度
        double[][] redMatrix = new double[imageWidth][imageHeight]; // 赤
        double[][] greenMatrix = new double[imageWidth][imageHeight]; // 緑
        double[][] blueMatrix = new double[imageWidth][imageHeight]; // 青

        // ピクセルごとに処理
        for (Integer rowIndex = 0; rowIndex < imageHeight; rowIndex++) {
            for (Integer colIndex = 0; colIndex < imageWidth; colIndex++) {
                Integer rgbValue = anImage.getRGB(colIndex, rowIndex); // ピクセルのRGB値を取得
                luminanceMatrix[colIndex][rowIndex] = ColorUtility.luminanceFromRGB(rgbValue); // 輝度を計算して格納
                double[] rgbComponents = ColorUtility.convertINTtoRGB(rgbValue); // RGB値をdouble配列に変換
                redMatrix[colIndex][rowIndex] = rgbComponents[0]; // 赤成分を格納
                greenMatrix[colIndex][rowIndex] = rgbComponents[1]; // 緑成分を格納
                blueMatrix[colIndex][rowIndex] = rgbComponents[2]; // 青成分を格納
            }
        }
        // 変換された全ての行列を3次元配列として返す
        return new double[][][]{luminanceMatrix, redMatrix, greenMatrix, blueMatrix};
    }

    /**
     * 全てのチャネルのスケーリング係数の絶対値の最大値を計算して返す。 計算済みの場合はキャッシュされた値を返す。
     *
     * @return スケーリング係数の絶対値の最大値
     */
    public double maximumAbsoluteScalingCoefficient() {
        // まだ計算されていない場合（初期値の場合）のみ計算を実行
        if (this.maximumAbsoluteScalingCoefficient == Double.MIN_VALUE) {
            for (Integer channelIndex = 0; channelIndex < this.sourceCoefficientsArray.length; channelIndex++) {
                double[][] sourceData = this.sourceCoefficientsArray[channelIndex];
                if (sourceData != null) {
                    double[][] scalingCoefficients = this.scalingCoefficientsArray[channelIndex];
                    Integer coefficientWidth = scalingCoefficients.length;
                    Integer coefficientHeight = (scalingCoefficients[0]).length;
                    for (Integer rowIndex = 0; rowIndex < coefficientHeight; rowIndex++) {
                        for (Integer colIndex = 0; colIndex < coefficientWidth; colIndex++) {
                            this.maximumAbsoluteScalingCoefficient = Math.max(Math.abs(scalingCoefficients[colIndex][rowIndex]), this.maximumAbsoluteScalingCoefficient);
                        }
                    }
                }
            }
        }
        return this.maximumAbsoluteScalingCoefficient;
    }

    /**
     * 全てのチャネルのソース係数（元の画像データ）の絶対値の最大値を計算して返す。 計算済みの場合はキャッシュされた値を返す。
     *
     * @return ソース係数の絶対値の最大値
     */
    public double maximumAbsoluteSourceCoefficient() {
        // まだ計算されていない場合（初期値の場合）のみ計算を実行
        if (this.maximumAbsoluteSourceCoefficient == Double.MIN_VALUE) {
            for (Integer channelIndex = 0; channelIndex < this.sourceCoefficientsArray.length; channelIndex++) {
                double[][] sourceData = this.sourceCoefficientsArray[channelIndex];
                if (sourceData != null) {
                    Integer coefficientWidth = sourceData.length;
                    Integer coefficientHeight = (sourceData[0]).length;
                    for (Integer rowIndex = 0; rowIndex < coefficientHeight; rowIndex++) {
                        for (Integer colIndex = 0; colIndex < coefficientWidth; colIndex++) {
                            this.maximumAbsoluteSourceCoefficient = Math.max(Math.abs(sourceData[colIndex][rowIndex]), this.maximumAbsoluteSourceCoefficient);
                        }
                    }
                }
            }
        }
        return this.maximumAbsoluteSourceCoefficient;
    }

    /**
     * 全てのチャネルの再構成された係数の絶対値の最大値を計算して返す。 計算済みの場合はキャッシュされた値を返す。
     *
     * @return 再構成された係数の絶対値の最大値
     */
    public double maximumAbsoluteRecomposedCoefficient() {
        // まだ計算されていない場合（初期値の場合）のみ計算を実行
        if (this.maximumAbsoluteRecomposedCoefficient == Double.MIN_VALUE) {
            for (Integer channelIndex = 0; channelIndex < this.sourceCoefficientsArray.length; channelIndex++) {
                double[][] sourceData = this.sourceCoefficientsArray[channelIndex];
                if (sourceData != null) {
                    double[][] recomposedCoefficients = this.recomposedCoefficientsArray[channelIndex];
                    Integer coefficientWidth = recomposedCoefficients.length;
                    Integer coefficientHeight = (recomposedCoefficients[0]).length;
                    for (Integer rowIndex = 0; rowIndex < coefficientHeight; rowIndex++) {
                        for (Integer colIndex = 0; colIndex < coefficientWidth; colIndex++) {
                            this.maximumAbsoluteRecomposedCoefficient = Math.max(Math.abs(recomposedCoefficients[colIndex][rowIndex]), this.maximumAbsoluteRecomposedCoefficient);
                        }
                    }
                }
            }
        }
        return this.maximumAbsoluteRecomposedCoefficient;
    }

    /**
     * 全てのチャネルのウェーブレット係数（水平、垂直、対角）の絶対値の最大値を計算して返す。 計算済みの場合はキャッシュされた値を返す。
     *
     * @return ウェーブレット係数の絶対値の最大値
     */
    public double maximumAbsoluteWaveletCoefficient() {
        // まだ計算されていない場合（初期値の場合）のみ計算を実行
        if (this.maximumAbsoluteWaveletCoefficient == Double.MIN_VALUE) {
            for (Integer channelIndex = 0; channelIndex < this.sourceCoefficientsArray.length; channelIndex++) {
                double[][] sourceData = this.sourceCoefficientsArray[channelIndex];
                if (sourceData != null) {
                    double[][] scalingCoefficients = this.scalingCoefficientsArray[channelIndex];
                    Integer coefficientWidth = scalingCoefficients.length;
                    Integer coefficientHeight = (scalingCoefficients[0]).length;
                    double[][] horizontalCoefficients = this.horizontalWaveletCoefficientsArray[channelIndex];
                    double[][] verticalCoefficients = this.verticalWaveletCoefficientsArray[channelIndex];
                    double[][] diagonalCoefficients = this.diagonalWaveletCoefficientsArray[channelIndex];
                    for (Integer rowIndex = 0; rowIndex < coefficientHeight; rowIndex++) {
                        for (Integer colIndex = 0; colIndex < coefficientWidth; colIndex++) {
                            this.maximumAbsoluteWaveletCoefficient = Math.max(Math.abs(horizontalCoefficients[colIndex][rowIndex]), this.maximumAbsoluteWaveletCoefficient);
                            this.maximumAbsoluteWaveletCoefficient = Math.max(Math.abs(verticalCoefficients[colIndex][rowIndex]), this.maximumAbsoluteWaveletCoefficient);
                            this.maximumAbsoluteWaveletCoefficient = Math.max(Math.abs(diagonalCoefficients[colIndex][rowIndex]), this.maximumAbsoluteWaveletCoefficient);
                        }
                    }
                }
            }
        }
        return this.maximumAbsoluteWaveletCoefficient;
    }

    /**
     * マウスクリックイベントを処理する。 クリックされた座標に基づいてウェーブレット係数を計算し、画像を更新する。
     *
     * @param aPoint マウスクリックの座標
     * @param aMouseEvent マウスイベントオブジェクト
     */
    public void mouseClicked(Point aPoint, MouseEvent aMouseEvent) {
        computeFromPoint(aPoint, aMouseEvent.isAltDown());
    }

    /**
     * マウスドラッグイベントを処理する。 ドラッグ中の座標に基づいてウェーブレット係数を計算し、画像を更新する。
     *
     * @param aPoint マウスドラッグの現在の座標
     * @param aMouseEvent マウスイベントオブジェクト
     */
    public void mouseDragged(Point aPoint, MouseEvent aMouseEvent) {
        computeFromPoint(aPoint, aMouseEvent.isAltDown());
    }

    /**
     * メインのGUIウィンドウを開き、ウェーブレット変換の結果を表示する。 GridBagLayoutを使用して4つの表示パネルを配置する。
     */
    public void open() {
        GridBagLayout gridBagLayout = new GridBagLayout();
        JPanel mainPanel = new JPanel(gridBagLayout); // レイアウトマネージャーを設定したパネル
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = 1; // Both (水平方向と垂直方向の両方にコンポーネントを拡張)

        // Source Coefficients (元データ) パネルの設定
        WaveletPaneView sourceView = new WaveletPaneView(this.sourceCoefficientsPaneModel);
        gridBagConstraints.gridx = 0; // グリッドのX座標
        gridBagConstraints.gridy = 0; // グリッドのY座標
        gridBagConstraints.gridwidth = 1; // グリッド幅
        gridBagConstraints.gridheight = 1; // グリッド高さ
        gridBagConstraints.weightx = 0.5D; // 水平方向の重み
        gridBagConstraints.weighty = 0.5D; // 垂直方向の重み
        gridBagLayout.setConstraints((Component) sourceView, gridBagConstraints);
        mainPanel.add((Component) sourceView);

        // Scaling & Wavelet Coefficients (変換結果) パネルの設定
        WaveletPaneView scalingWaveletView = new WaveletPaneView(this.scalingAndWaveletCoefficientsPaneModel);
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.gridheight = 1;
        gridBagConstraints.weightx = 0.5D;
        gridBagConstraints.weighty = 0.5D;
        gridBagLayout.setConstraints((Component) scalingWaveletView, gridBagConstraints);
        mainPanel.add((Component) scalingWaveletView);

        // Recomposed Coefficients (再構成結果) パネルの設定
        WaveletPaneView recomposedView = new WaveletPaneView(this.recomposedCoefficientsPaneModel);
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.gridheight = 1;
        gridBagConstraints.weightx = 0.5D;
        gridBagConstraints.weighty = 0.5D;
        gridBagLayout.setConstraints((Component) recomposedView, gridBagConstraints);
        mainPanel.add((Component) recomposedView);

        // Interactive Scaling & Wavelet Coefficients (対話操作用変換結果) パネルの設定
        WaveletPaneView interactiveView = new WaveletPaneView(this.interactiveScalingAndWaveletCoefficientsPaneModel);
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.gridheight = 1;
        gridBagConstraints.weightx = 0.5D;
        gridBagConstraints.weighty = 0.5D;
        gridBagLayout.setConstraints((Component) interactiveView, gridBagConstraints);
        mainPanel.add((Component) interactiveView);

        // JFrameのセットアップ
        JFrame mainFrame = new JFrame("Wavelet Transform (2D)");
        mainFrame.getContentPane().add(mainPanel); // パネルをフレームに追加
        mainFrame.setDefaultCloseOperation(3); // CLOSE_ON_EXIT (ウィンドウを閉じるとアプリケーションも終了)
        mainFrame.addNotify(); // コンポーネントをピアに接続 (UIの準備)
        Integer insetsTop = (mainFrame.getInsets()).top; // フレームのインセット（上部）を取得
        mainFrame.setMinimumSize(new Dimension(256, 256 + insetsTop)); // 最小サイズを設定
        mainFrame.setResizable(true); // リサイズ可能にする
        mainFrame.setSize(512, 512 + insetsTop); // 初期サイズを設定
        mainFrame.setLocationRelativeTo(null); // 画面中央に配置
        mainFrame.setVisible(true); // フレームを表示
        mainFrame.toFront(); // フレームを最前面に表示
    }

    /**
     * 単一の2次元係数行列をソースデータとして設定する。
     *
     * @param sourceDataMatrix ソースとなる2次元係数行列
     */
    public void setSourceData(double[][] sourceDataMatrix) {
        double[][] luminanceChannel = sourceDataMatrix;
        double[][] redChannel = null; // Rチャネル (未使用)
        double[][] greenChannel = null; // Gチャネル (未使用)
        double[][] blueChannel = null; // Bチャネル (未使用)
        double[][][] combinedDataArray = {luminanceChannel, redChannel, greenChannel, blueChannel}; // 3次元配列にまとめる
        setSourceData(combinedDataArray); // オーバーロードされたメソッドを呼び出す
    }

    /**
     * 3次元の係数行列配列（輝度およびRGB各チャネル）をソースデータとして設定し、 ウェーブレット変換を実行して、結果の係数と表示モデルを更新する。
     *
     * @param sourceDataMatrixArray ソースとなる3次元係数行列配列
     */
    public void setSourceData(double[][][] sourceDataMatrixArray) {
        this.sourceCoefficientsArray = sourceDataMatrixArray; // 元の係数を設定
        // 各種係数配列を初期化
        this.scalingCoefficientsArray = new double[this.sourceCoefficientsArray.length][][];
        this.horizontalWaveletCoefficientsArray = new double[this.sourceCoefficientsArray.length][][];
        this.verticalWaveletCoefficientsArray = new double[this.sourceCoefficientsArray.length][][];
        this.diagonalWaveletCoefficientsArray = new double[this.sourceCoefficientsArray.length][][];
        this.interactiveHorizontalWaveletCoefficientsArray = new double[this.sourceCoefficientsArray.length][][];
        this.interactiveVerticalWaveletCoefficientsArray = new double[this.sourceCoefficientsArray.length][][];
        this.interactiveDiagonalWaveletCoefficientsArray = new double[this.sourceCoefficientsArray.length][][];
        this.recomposedCoefficientsArray = new double[this.sourceCoefficientsArray.length][][];

        // 各チャネルに対してウェーブレット変換を実行
        for (Integer channelIndex = 0; channelIndex < this.sourceCoefficientsArray.length; channelIndex++) {
            double[][] currentSourceData = this.sourceCoefficientsArray[channelIndex];
            if (currentSourceData != null) { // データが存在する場合のみ処理
                // 離散ウェーブレット2D変換を実行
                DiscreteWavelet2dTransformation discreteWavelet2dTransformation = new DiscreteWavelet2dTransformation(currentSourceData);
                double[][] scalingResult = discreteWavelet2dTransformation.scalingCoefficients(); // スケーリング係数
                double[][] horizontalResult = discreteWavelet2dTransformation.horizontalWaveletCoefficients(); // 水平ウェーブレット係数
                double[][] verticalResult = discreteWavelet2dTransformation.verticalWaveletCoefficients(); // 垂直ウェーブレット係数
                double[][] diagonalResult = discreteWavelet2dTransformation.diagonalWaveletCoefficients(); // 対角ウェーブレット係数

                // インタラクティブ係数配列を初期化（最初は全て0でクリア）
                double[][] interactiveHorizontal = new double[horizontalResult.length][(horizontalResult[0]).length];
                double[][] interactiveVertical = new double[verticalResult.length][(verticalResult[0]).length];
                double[][] interactiveDiagonal = new double[diagonalResult.length][(diagonalResult[0]).length];
                fill(interactiveHorizontal, 0.0D);
                fill(interactiveVertical, 0.0D);
                fill(interactiveDiagonal, 0.0D);

                // 計算結果を各配列に格納
                this.scalingCoefficientsArray[channelIndex] = scalingResult;
                this.horizontalWaveletCoefficientsArray[channelIndex] = horizontalResult;
                this.verticalWaveletCoefficientsArray[channelIndex] = verticalResult;
                this.diagonalWaveletCoefficientsArray[channelIndex] = diagonalResult;
                this.interactiveHorizontalWaveletCoefficientsArray[channelIndex] = interactiveHorizontal;
                this.interactiveVerticalWaveletCoefficientsArray[channelIndex] = interactiveVertical;
                this.interactiveDiagonalWaveletCoefficientsArray[channelIndex] = interactiveDiagonal;

                // インタラクティブ係数とスケーリング係数から再構成を実行
                double[][][] interactiveCoefficientsCombined = {interactiveHorizontal, interactiveVertical, interactiveDiagonal};
                discreteWavelet2dTransformation = new DiscreteWavelet2dTransformation(scalingResult, interactiveCoefficientsCombined);
                double[][] recomposedFromInteractive = discreteWavelet2dTransformation.recomposedCoefficients();
                this.recomposedCoefficientsArray[channelIndex] = recomposedFromInteractive;
            }
        }

        // 各絶対値最大値をリセットし、再計算させる
        this.maximumAbsoluteSourceCoefficient = Double.MIN_VALUE;
        this.maximumAbsoluteScalingCoefficient = Double.MIN_VALUE;
        this.maximumAbsoluteWaveletCoefficient = Double.MIN_VALUE;
        this.maximumAbsoluteRecomposedCoefficient = Double.MIN_VALUE;

        // 各係数配列から表示用の画像を生成
        BufferedImage sourceImage = generateImage(this.sourceCoefficientsArray, maximumAbsoluteSourceCoefficient()); // 元データ画像
        BufferedImage scalingImage = generateImage(this.scalingCoefficientsArray, maximumAbsoluteScalingCoefficient()); // スケーリング係数画像
        BufferedImage horizontalImage = generateImage(this.horizontalWaveletCoefficientsArray, maximumAbsoluteWaveletCoefficient()); // 水平ウェーブレット係数画像
        BufferedImage verticalImage = generateImage(this.verticalWaveletCoefficientsArray, maximumAbsoluteWaveletCoefficient()); // 垂直ウェーブレット係数画像
        BufferedImage diagonalImage = generateImage(this.diagonalWaveletCoefficientsArray, maximumAbsoluteWaveletCoefficient()); // 対角ウェーブレット係数画像
        BufferedImage interactiveHorizontalImage = generateImage(this.interactiveHorizontalWaveletCoefficientsArray, maximumAbsoluteWaveletCoefficient()); // 対話操作用水平係数画像
        BufferedImage interactiveVerticalImage = generateImage(this.interactiveVerticalWaveletCoefficientsArray, maximumAbsoluteWaveletCoefficient()); // 対話操作用垂直係数画像
        BufferedImage interactiveDiagonalImage = generateImage(this.interactiveDiagonalWaveletCoefficientsArray, maximumAbsoluteWaveletCoefficient()); // 対話操作用対角係数画像
        BufferedImage combinedAllWaveletsImage = generateImage(scalingImage, horizontalImage, verticalImage, diagonalImage); // 全ウェーブレット係数を結合した画像
        BufferedImage combinedInteractiveWaveletsImage = generateImage(scalingImage, interactiveHorizontalImage, interactiveVerticalImage, interactiveDiagonalImage); // 対話操作用ウェーブレット係数を結合した画像
        BufferedImage recomposedImage = generateImage(this.recomposedCoefficientsArray, maximumAbsoluteRecomposedCoefficient()); // 再構成された係数画像

        // 各表示モデルの画像を更新 (モデルが未初期化の場合は初期化)
        if (this.sourceCoefficientsPaneModel == null) {
            this.sourceCoefficientsPaneModel = new WaveletPaneModel(null, "Source Coefficients");
        }
        this.sourceCoefficientsPaneModel.picture(sourceImage);

        if (this.scalingAndWaveletCoefficientsPaneModel == null) {
            this.scalingAndWaveletCoefficientsPaneModel = new WaveletPaneModel(null, "Scaling & Wavelet Coefficients");
        }
        this.scalingAndWaveletCoefficientsPaneModel.picture(combinedAllWaveletsImage);

        if (this.interactiveScalingAndWaveletCoefficientsPaneModel == null) {
            this.interactiveScalingAndWaveletCoefficientsPaneModel = new WaveletPaneModel(null, "Interactive Scaling & Wavelet Coefficients", this);
        }
        this.interactiveScalingAndWaveletCoefficientsPaneModel.picture(combinedInteractiveWaveletsImage);

        if (this.recomposedCoefficientsPaneModel == null) {
            this.recomposedCoefficientsPaneModel = new WaveletPaneModel(null, "Recomposed Coefficients");
        }
        this.recomposedCoefficientsPaneModel.picture(recomposedImage);

        // 各表示モデルに変更を通知し、GUIを再描画
        this.sourceCoefficientsPaneModel.changed();
        this.scalingAndWaveletCoefficientsPaneModel.changed();
        this.interactiveScalingAndWaveletCoefficientsPaneModel.changed();
        this.recomposedCoefficientsPaneModel.changed();
    }

    /**
     * 右クリックイベント時にポップアップメニューを表示する。 サンプルデータや係数の表示/クリアなどのオプションを提供する。
     *
     * @param aMouseEvent マウスイベントオブジェクト
     * @param aController ポップアップメニューのアクションを処理するコントローラ
     */
    public void showPopupMenu(MouseEvent aMouseEvent, WaveletPaneController aController) {
        Integer mouseX = aMouseEvent.getX(); // マウスX座標
        Integer mouseY = aMouseEvent.getY(); // マウスY座標
        Cursor defaultCursor = Cursor.getDefaultCursor(); // デフォルトカーソルを取得
        Component eventComponent = aMouseEvent.getComponent(); // イベントが発生したコンポーネント
        eventComponent.setCursor(defaultCursor); // カーソルをデフォルトに戻す

        JPopupMenu popupMenu = new JPopupMenu(); // ポップアップメニューを作成

        // メニュー項目を追加し、コントローラをアクションリスナーとして設定
        JMenuItem menuItem = new JMenuItem("sample coefficients");
        menuItem.addActionListener(aController);
        popupMenu.add(menuItem);

        menuItem = new JMenuItem("smalltalk balloon");
        menuItem.addActionListener(aController);
        popupMenu.add(menuItem);

        menuItem = new JMenuItem("earth");
        menuItem.addActionListener(aController);
        popupMenu.add(menuItem);

        popupMenu.addSeparator();
        JMenuItem changeItem = new JMenuItem("Change Image");
        changeItem.addActionListener(aController);
        popupMenu.add(changeItem);

        popupMenu.addSeparator(); // 区切り線を追加

        menuItem = new JMenuItem("all coefficients");
        menuItem.addActionListener(aController);
        popupMenu.add(menuItem);

        menuItem = new JMenuItem("clear coefficients");
        menuItem.addActionListener(aController);
        popupMenu.add(menuItem);

        popupMenu.show(eventComponent, mouseX, mouseY); // 指定された位置にポップアップメニューを表示
    }

    /**
     * 与えられた値以上の最小の2のべき乗を返す
     */
    private static int nextPowerOfTwo(int value) {
        int n = 1;
        while (n < value) {
            n <<= 1;
        }
        return n;
    }
}

package utility;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Point;

import wavelet.Wavelet2dModel;
import wavelet.Constants;

/**
 * ウェーブレット変換に関連するサンプルデータやユーティリティメソッドを提供するクラス。
 * 1次元および2次元の離散ウェーブレット変換のためのサンプル係数データ、
 * 特定の画像データの読み込み、および数値行列を画像に変換する機能などを提供する。
 *
 * <p>このクラスは、ウェーブレット変換のデモンストレーションやテストにおいて、
 * データソースとして利用されることを意図している。</p>
 */
public class WaveletData {
    /**
     * 離散ウェーブレット1次元変換のためのサンプル元データを生成し応答する。
     * 64個の要素を持つ {@code double} 型配列で、特定のパターン（2次曲線と定数）を持つ。
     *
     * @return 1次元離散ウェーブレット変換のためのサンプル係数配列
     */
    public static double[] Sample1dCoefficients()
    {
        double[] anArray = new double[64];
        Arrays.fill(anArray, 0.0d); // 全体を0で初期化
        // 最初の16要素: 2次曲線
        for (int i =  0; i < 16; i++) { anArray[i] = Math.pow((double)(i + 1), 2.0d) / 256.0d; }
        // 次の16要素: 定数
        for (int i = 16; i < 32; i++) { anArray[i] = 0.2d; }
        // その後の16要素: 2次曲線（反転）
        for (int i = 32; i < 48; i++) { anArray[i] = Math.pow((double)(48 - (i + 1)), 2.0d) / 256.0d - 0.5d; }
        return anArray;
    }

    /**
     * 離散ウェーブレット2次元変換のためのサンプル元データを生成し応答する。
     * 64x64の {@code double} 型2次元配列で、四角形と対角線が強調されたパターンを持つ。
     *
     * @return 2次元離散ウェーブレット変換のためのサンプル係数行列
     */
    public static double[][] Sample2dCoefficients()
    {
        int size = 64;
        double[][] aMatrix = new double[size][size];
        // 全体を0.2で初期化
        for (int index = 0; index < aMatrix.length; index++)
        {
            Arrays.fill(aMatrix[index], 0.2d);
        }
        // 四角形と対角線を描画
        for (int index = 5; index < size - 5; index++)
        {
            aMatrix[5][index] = 1.0d; // 上辺
            aMatrix[size - 6][index] = 1.0d; // 下辺
            aMatrix[index][5] = 1.0d; // 左辺
            aMatrix[index][size - 6] = 1.0d; // 右辺
            aMatrix[index][index] = 1.0d; // 主対角線
            aMatrix[index][size - index - 1] = 1.0d; // 副対角線
        }
        return aMatrix;
    }

    /**
     * 離散ウェーブレット2次元変換のためのサンプル元データ（Earth画像）を、
     * 輝度およびRGB各成分の行列として応答する。
     *
     * @return 輝度、赤、緑、青の各成分を表す {@code double[][][]} 配列
     */
    public static double[][][] dataEarth()
    {
        BufferedImage anImage = Wavelet2dModel.imageEarth(); // Earth画像を読み込む
        return Wavelet2dModel.lrgbMatrixes(anImage); // LRBG行列に変換して応答
    }

    /**
     * 離散ウェーブレット2次元変換のためのサンプル元データ（SmalltalkBalloon画像）を、
     * 輝度およびRGB各成分の行列として応答する。
     *
     * @return 輝度、赤、緑、青の各成分を表す {@code double[][][]} 配列
     */
    public static double[][][] dataSmalltalkBalloon()
    {
        BufferedImage anImage = Wavelet2dModel.imageSmalltalkBalloon(); // SmalltalkBalloon画像を読み込む
        return Wavelet2dModel.lrgbMatrixes(anImage); // LRBG行列に変換して応答
    }

    /**
     * 2次元配列の全ての要素を指定された値で初期化する。
     *
     * @param aMatrix 初期化対象の2次元配列
     * @param aValue 設定する値
     */
    public static void fill(double[][] aMatrix, double aValue)
    {
        for (double[] anArray : aMatrix) {
            Arrays.fill(anArray, aValue); // 各行を値で埋める
        }
        return;
    }

    /**
     * 離散ウェーブレット2次元変換のためのデータ値（輝度およびRGB各成分の行列）を画像に変換して応答する。
     *
     * @param valueMatrixArray 輝度、赤、緑、青の各成分を表す {@code double[][][]} 配列
     * 配列の2番目から4番目の要素（RGB成分）が {@code null} の場合、グレイスケール画像として生成される
     * @param maxValue 行列内の値の最大絶対値。色スケール計算に用いられる
     * @return 生成された {@link BufferedImage} オブジェクト
     */
    public static BufferedImage generateImage(double[][][] valueMatrixArray, double maxValue)
    {
        double[][] valueMatrix = valueMatrixArray[0]; // 輝度または主となる成分
        int width = valueMatrix.length;
        int height = valueMatrix[0].length;
        BufferedImage anImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D aGraphics = anImage.createGraphics();

        // RGB成分が存在しない場合（グレイスケール）
        if (valueMatrixArray[1] == null || valueMatrixArray[2] == null || valueMatrixArray[3] == null)
        {
            // グレイスケール画像として生成
            for (int y = 0; y < height; y++)
            {
                for (int x = 0; x < width; x++)
                {
                    double aValue = Math.abs(valueMatrix[x][y]); // 値の絶対値を使用
                    int brightness = (int)Math.round((aValue / maxValue) * 255.0d); // 輝度を0-255に正規化
                    Color aColor = new Color(brightness, brightness, brightness); // グレイスケールカラー
                    aGraphics.setColor(aColor);
                    aGraphics.fillRect(x, y, 1, 1); // 1ピクセル描画
                }
            }
        }
        // RGB成分が存在する場合（カラー）
        else
        {
            // カラー画像として生成
            // int[][] redMatrix = new int[width][height]; // 使用されていないためコメントアウトまたは削除を検討
            // int[][] greenMatrix = new int[width][height];
            // int[][] blueMatrix = new int[width][height];
            for (int y = 0; y < height; y++)
            {
                for (int x = 0; x < width; x++)
                {
                    double redValue = Math.abs(valueMatrixArray[1][x][y]); // 赤成分の絶対値
                    int red = (int)Math.round((redValue / maxValue) * 255.0d); // 0-255に正規化
                    double greenValue = Math.abs(valueMatrixArray[2][x][y]); // 緑成分の絶対値
                    int green = (int)Math.round((greenValue / maxValue) * 255.0d); // 0-255に正規化
                    double blueValue = Math.abs(valueMatrixArray[3][x][y]); // 青成分の絶対値
                    int blue = (int)Math.round((blueValue / maxValue) * 255.0d); // 0-255に正規化
                    Color aColor = new Color(red, green, blue); // RGBカラー
                    aGraphics.setColor(aColor);
                    aGraphics.fillRect(x, y, 1, 1); // 1ピクセル描画
                }
            }
        }

        return anImage;
    }

    /**
     * 離散ウェーブレット2次元変換のためのデータ値（単一の行列）を画像に変換して応答する。
     * このメソッドは、指定されたスケールファクターとRGBフラグに基づいて画像を生成する。
     *
     * @param valueMatrix 変換対象の2次元データ行列
     * @param scaleFactor 各ピクセルの拡大率を表す {@link Point} (x: 幅方向、y: 高さ方向)
     * @param rgbFlag 色成分（赤、緑、青、またはグレイスケール）を指定するフラグ
     * {@link Constants#Red}、{@link Constants#Green}、{@link Constants#Blue}、またはその他の値（グレイスケール）
     * @return 生成された {@link BufferedImage} オブジェクト
     */
    public static BufferedImage generateImage(double[][] valueMatrix, Point scaleFactor, int rgbFlag)
    {
        int width = valueMatrix.length;
        int height = valueMatrix[0].length;
        int w = width * scaleFactor.x; // スケール適用後の幅
        int h = height * scaleFactor.y; // スケール適用後の高さ
        BufferedImage anImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D aGraphics = anImage.createGraphics();

        // 最大絶対値を計算し、色の正規化に用いる
        double maxValue = Double.MIN_VALUE;
        for (int y = 0; y < height; y++)
        {
            for (int x = 0; x < width; x++)
            {
                double aValue = Math.abs(valueMatrix[x][y]);
                maxValue = Math.max(aValue, maxValue);
            }
        }

        // 各値をピクセルとして描画
        for (int y = 0; y < height; y++)
        {
            for (int x = 0; x < width; x++)
            {
                double aValue = Math.abs(valueMatrix[x][y]);
                int luminance = (int)Math.round((aValue / maxValue) * 255.0d); // 輝度を0-255に正規化
                
                Color aColor = new Color(luminance, luminance, luminance); // デフォルトはグレイスケール
                if (rgbFlag == Constants.Red  ) { aColor = new Color(luminance, 0, 0); } // 赤成分のみ
                if (rgbFlag == Constants.Green) { aColor = new Color(0, luminance, 0); } // 緑成分のみ
                if (rgbFlag == Constants.Blue ) { aColor = new Color(0, 0, luminance); } // 青成分のみ
                
                aGraphics.setColor(aColor);
                // スケールファクターを考慮して描画
                aGraphics.fillRect(x * scaleFactor.x, y * scaleFactor.y, scaleFactor.x, scaleFactor.y);
            }
        }

        return anImage;
    }

    /**
     * "SampleImages/imageEarth512x256.jpg" の画像ファイルを読み込み、
     * {@link BufferedImage} オブジェクトとして応答する。
     *
     * @return 読み込まれたEarth画像の {@link BufferedImage} オブジェクト
     */
    public static BufferedImage imageEarth()
    {
        String aString = "SampleImages/imageEarth512x256.jpg";
        BufferedImage anImage = ImageUtility.readImage(aString);
        return anImage;
    }

    /**
     * "SampleImages/imageSmalltalkBalloon256x256.jpg" の画像ファイルを読み込み、
     * {@link BufferedImage} オブジェクトとして応答する。
     *
     * @return 読み込まれたSmalltalkBalloon画像の {@link BufferedImage} オブジェクト
     */
    public static BufferedImage imageSmalltalkBalloon()
    {
        String aString = "SampleImages/imageSmalltalkBalloon256x256.jpg";
        BufferedImage anImage = ImageUtility.readImage(aString);
        return anImage;
    }

    /**
     * 指定された {@link BufferedImage} から、輝度（L）とRGB各成分（R, G, B）の
     * 2次元行列を抽出し、3次元配列として応答する。
     * 返される配列のインデックスは、0: 輝度、1: 赤、2: 緑、3: 青に対応する。
     *
     * @param anImage 処理対象の {@link BufferedImage} オブジェクト
     * @return 輝度、赤、緑、青の各成分を表す {@code double[][][]} 配列
     */
    public static double[][][] lrgbMatrixes(BufferedImage anImage)
    {
        int width = anImage.getWidth();
        int height = anImage.getHeight();
        double[][] luminanceMatrix = new double[width][height];
        double[][] redMatrix = new double[width][height];
        double[][] greenMatrix = new double[width][height];
        double[][] blueMatrix = new double[width][height];

        for (int y = 0; y < height; y++)
        {
            for (int x = 0; x < width; x++)
            {
                int aRGB = anImage.getRGB(x, y); // ピクセルのRGB値を整数として取得
                luminanceMatrix[x][y] = ColorUtility.luminanceFromRGB(aRGB); // 輝度を計算
                double[] rgb = ColorUtility.convertINTtoRGB(aRGB); // RGB値をdouble配列に変換
                redMatrix[x][y] = rgb[0];
                greenMatrix[x][y] = rgb[1];
                blueMatrix[x][y] = rgb[2];
            }
        }
        // 輝度、赤、緑、青の順に配列として応答
        return new double[][][] { luminanceMatrix, redMatrix, greenMatrix, blueMatrix };
    }
}
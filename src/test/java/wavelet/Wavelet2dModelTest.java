package wavelet;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import utility.ColorUtility;
import utility.Condition;
import utility.FileUtility;
import utility.ValueHolder;
import utility.WaveletData; // WaveletData クラスも使用されているのでインポート

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays; // assertArrayEquals などで使用するため

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Wavelet2dModel Class Unit Tests")
class Wavelet2dModelTest {

    // --- System.err のキャプチャのための設定 ---
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalErr = System.err;
    // --- System.out もキャプチャしたい場合は以下も追加
    // private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    // private final PrintStream originalOut = System.out;
    // ---

    // テスト全体で使用する Wavelet2dModel のインスタンスと DELTA
    private Wavelet2dModel model;
    private static final double DELTA = 1e-9;

    @BeforeEach
    void setUp() {
        // System.err のリダイレクト
        System.setErr(new PrintStream(errContent));
        // --- System.out もキャプチャする場合は以下も追加
        // System.setOut(new PrintStream(outContent));
        // ---

        // 各テストの前に新しいモデルインスタンスを作成
        model = new Wavelet2dModel();
    }

    @AfterEach
    void tearDown() {
        // System.err を元に戻す
        System.setErr(originalErr);
        errContent.reset(); // キャプチャ内容をクリア
        // --- System.out もキャプチャする場合は以下も追加
        // System.setOut(originalOut);
        // outContent.reset();
        // ---
    }

    @Test
    @DisplayName("Constructor and initial state are correctly initialized")
    void testConstructorAndInitialState() {
        // setUp() で model は既に初期化されている
        // コンストラクタで doSampleCoefficients() が呼ばれることを確認
        assertNotNull(model.sourceCoefficientsArray, "Source coefficients array should not be null after construction.");
        // setSourceData が常に4チャネル配列を生成するという前提で
        assertEquals(4, model.sourceCoefficientsArray.length, "Source coefficients array should have 4 channels after construction.");
        // sourceCoefficientsArray[0] (luminanceChannel) の幅と高さを検証
        assertTrue(model.sourceCoefficientsArray[0].length > 0, "Source coefficients array [0] should have a width greater than 0.");
        assertTrue(model.sourceCoefficientsArray[0][0].length > 0, "Source coefficients array [0][0] should have a height greater than 0.");

        assertNotNull(model.scalingCoefficientsArray, "Scaling coefficients array should not be null.");
        assertNotNull(model.horizontalWaveletCoefficientsArray, "Horizontal wavelet coefficients array should not be null.");
        assertNotNull(model.verticalWaveletCoefficientsArray, "Vertical wavelet coefficients array should not be null.");
        assertNotNull(model.diagonalWaveletCoefficientsArray, "Diagonal wavelet coefficients array should not be null.");
        assertNotNull(model.interactiveHorizontalWaveletCoefficientsArray, "Interactive horizontal wavelet coefficients array should not be null.");
        assertNotNull(model.interactiveVerticalWaveletCoefficientsArray, "Interactive vertical wavelet coefficients array should not be null.");
        assertNotNull(model.interactiveDiagonalWaveletCoefficientsArray, "Interactive diagonal wavelet coefficients array should not be null.");
        assertNotNull(model.recomposedCoefficientsArray, "Recomposed coefficients array should not be null.");

        assertNotNull(model.sourceCoefficientsPaneModel, "Source coefficients pane model should not be null.");
        assertNotNull(model.scalingAndWaveletCoefficientsPaneModel, "Scaling and wavelet coefficients pane model should not be null.");
        assertNotNull(model.interactiveScalingAndWaveletCoefficientsPaneModel, "Interactive scaling and wavelet coefficients pane model should not be null.");
        assertNotNull(model.recomposedCoefficientsPaneModel, "Recomposed coefficients pane model should not be null.");
    }


    @Test
    @DisplayName("setSourceData(double[][]) correctly initializes data with 4 channels")
    void testSetSourceDataWith2dArray() {
        double[][] sampleData = { // 2D配列のサンプルデータ (2x2の行列)
            {10.0, 20.0}, // row 0 (Y=0)
            {30.0, 40.0}  // row 1 (Y=1)
        };
        model.setSourceData(sampleData);

        assertNotNull(model.sourceCoefficientsArray, "Source coefficients array should not be null");
        // setSourceData(double[][]) は LRGB 4チャネルを作成する
        assertEquals(4, model.sourceCoefficientsArray.length, "Source coefficients array should have 4 channels (L, R, G, B)");
        
        // Luminance channel の内容が入力と一致することを確認 (Y,Xの順でアクセス)
        // model.sourceCoefficientsArray[0] は luminanceMatrix (double[][])
        assertEquals(sampleData.length, model.sourceCoefficientsArray[0].length, "Luminance matrix height should match sample data rows");
        assertEquals(sampleData[0].length, model.sourceCoefficientsArray[0][0].length, "Luminance matrix width should match sample data columns");

        assertEquals(sampleData[0][0], model.sourceCoefficientsArray[0][0][0], DELTA);
        assertEquals(sampleData[0][1], model.sourceCoefficientsArray[0][0][1], DELTA);
        assertEquals(sampleData[1][0], model.sourceCoefficientsArray[0][1][0], DELTA);
        assertEquals(sampleData[1][1], model.sourceCoefficientsArray[0][1][1], DELTA);

        // RGB channels は null であることを確認 (setSourceData の実装による)
        assertNull(model.sourceCoefficientsArray[1], "Red channel should be null");
        assertNull(model.sourceCoefficientsArray[2], "Green channel should be null");
        assertNull(model.sourceCoefficientsArray[3], "Blue channel should be null");

        // 他の係数配列も初期化されていることを確認
        assertNotNull(model.scalingCoefficientsArray, "Scaling coefficients array should be initialized.");
        assertNotNull(model.horizontalWaveletCoefficientsArray, "Horizontal wavelet coefficients array should be initialized.");
        assertNotNull(model.verticalWaveletCoefficientsArray, "Vertical wavelet coefficients array should be initialized.");
        assertNotNull(model.diagonalWaveletCoefficientsArray, "Diagonal wavelet coefficients array should be initialized.");
        assertNotNull(model.interactiveHorizontalWaveletCoefficientsArray, "Interactive horizontal wavelet coefficients array should be initialized.");
        assertNotNull(model.interactiveVerticalWaveletCoefficientsArray, "Interactive vertical wavelet coefficients array should be initialized.");
        assertNotNull(model.interactiveDiagonalWaveletCoefficientsArray, "Interactive diagonal wavelet coefficients array should be initialized.");
        assertNotNull(model.recomposedCoefficientsArray, "Recomposed coefficients array should be initialized.");
    }


    @Test
    @DisplayName("doSampleCoefficients() sets sample data and initializes models correctly")
    void testDoSampleCoefficients() {
        // setUp() で既にモデルは初期化されている (doSampleCoefficients が呼ばれる)
        // ここでは、doSampleCoefficients() の呼び出しが正しくモデルの状態を設定することを確認

        // doSampleCoefficients() はコンストラクタで呼ばれるが、明示的に呼び出してテスト
        model.doSampleCoefficients();

        assertNotNull(model.sourceCoefficientsArray, "Source coefficients array should not be null");
        assertEquals(4, model.sourceCoefficientsArray.length, "Source coefficients array should have 4 channels");
        // WaveletData.Sample2dCoefficients() は 64x64 の行列を返す
        // Wavelet2dModel.setSourceData(double[][]) はそれを 4チャネル配列にラップ
        // sourceCoefficientsArray[0] は luminanceMatrix = 64 rows, 64 cols
        assertEquals(64, model.sourceCoefficientsArray[0].length, "Source coefficients luminance matrix height should be 64"); // 行数 (Y)
        assertEquals(64, model.sourceCoefficientsArray[0][0].length, "Source coefficients luminance matrix width should be 64"); // 列数 (X)
        
        // 念のため、sample coefficients の内容の一部を検証
        assertEquals(0.2D, model.sourceCoefficientsArray[0][0][0], DELTA, "Top-left pixel should be 0.2"); // matrix[Y][X]
        assertEquals(1.0D, model.sourceCoefficientsArray[0][5][5], DELTA, "Border pixel at (5,5) should be 1.0"); // matrix[Y][X]
        
        // パネルモデルの画像が null でないことを確認
        assertNotNull(model.sourceCoefficientsPaneModel.picture(), "Source coefficients pane picture should not be null.");
        assertNotNull(model.scalingAndWaveletCoefficientsPaneModel.picture(), "Scaling and wavelet coefficients pane picture should not be null.");
        assertNotNull(model.interactiveScalingAndWaveletCoefficientsPaneModel.picture(), "Interactive scaling and wavelet coefficients pane picture should not be null.");
        assertNotNull(model.recomposedCoefficientsPaneModel.picture(), "Recomposed coefficients pane picture should not be null.");
    }

    @Test
    @DisplayName("doEarth() loads earth image data and initializes models correctly")
    void testDoEarth() {
        // Mocking static methods if needed, but for now assuming they work
        // Mocking is only needed if you want to test doEarth in isolation without real image loading
        // For actual behavior, just call it.
        model.doEarth();

        assertNotNull(model.sourceCoefficientsArray, "Source coefficients array should not be null");
        assertEquals(4, model.sourceCoefficientsArray.length, "Source coefficients array should have 4 channels for LRBG matrix");
        // imageEarth() returns 512x256 image, so sourceCoefficientsArray[0] should be 256 rows x 512 cols (after lrgbMatrixes)
        assertEquals(256, model.sourceCoefficientsArray[0].length, "Earth image matrix height should be 256"); // 行数 (Y)
        assertEquals(512, model.sourceCoefficientsArray[0][0].length, "Earth image matrix width should be 512"); // 列数 (X)

        // 他のプロパティも必要に応じてアサーション
        assertNotNull(model.scalingCoefficientsArray);
        assertNotNull(model.recomposedCoefficientsArray);
        // ... (他の係数配列も同様にチェック)
        assertNotNull(model.sourceCoefficientsPaneModel.picture());
        // ...
    }

    @Test
    @DisplayName("doSmalltalkBalloon() loads smalltalk balloon image data and initializes models correctly")
    void testDoSmalltalkBalloon() {
        model.doSmalltalkBalloon();

        assertNotNull(model.sourceCoefficientsArray, "Source coefficients array should not be null");
        assertEquals(4, model.sourceCoefficientsArray.length, "Source coefficients array should have 4 channels for LRBG matrix");
        // imageSmalltalkBalloon() returns 256x256 image, so sourceCoefficientsArray[0] should be 256 rows x 256 cols
        assertEquals(256, model.sourceCoefficientsArray[0].length, "Smalltalk balloon matrix height should be 256"); // 行数 (Y)
        assertEquals(256, model.sourceCoefficientsArray[0][0].length, "Smalltalk balloon matrix width should be 256"); // 列数 (X)

        assertNotNull(model.scalingCoefficientsArray);
        assertNotNull(model.recomposedCoefficientsArray);
        assertNotNull(model.sourceCoefficientsPaneModel.picture());
    }

    @Test
    @DisplayName("testGenerateImage3dArrayGrayscale()")
    void testGenerateImage3dArrayGrayscale() {
        // テストデータ: グレースケールを意図しているので、RGBチャネルは null に設定
        double[][] luminanceMatrix = {
            {0.1, 0.2}, // (row 0, col 0), (row 0, col 1)
            {0.3, 0.4}  // (row 1, col 0), (row 1, col 1)
        };
        // Wavelet2dModel.generateImage に渡す3次元配列。
        // グレースケールとして扱われるように、チャネル0のみデータを与え、他のチャネルはnullとする。
        // プロダクトコードの generateImage の if 条件 (valueMatrixArray[1] == null || ...) が正しく動作することを前提
        double[][][] grayScaleImageMatrix = {luminanceMatrix, null, null, null};

        BufferedImage generatedImage = Wavelet2dModel.generateImage(grayScaleImageMatrix, 1.0);

        assertNotNull(generatedImage, "生成された画像はnullであってはいけません");
        // Wavelet2dModel.generateImage の幅/高さの修正後（widthは列数、heightは行数）であれば、
        // 以下のアサーションが正しいはず
        assertEquals(luminanceMatrix[0].length, generatedImage.getWidth(), "生成画像の幅は行列の列数と一致すべき"); // 期待: 2 (cols)
        assertEquals(luminanceMatrix.length, generatedImage.getHeight(), "生成画像の高さは行列の行数と一致すべき"); // 期待: 2 (rows)

        // ピクセル値の検証 (image.getRGB(x, y) に対応する luminanceMatrix[y][x])
        // (0,0) -> luminanceMatrix[0][0]=0.1 -> round(0.1*255)=26 -> Color(26,26,26)
        assertEquals(new Color(26, 26, 26).getRGB() & 0xFFFFFF, generatedImage.getRGB(0, 0) & 0xFFFFFF);
        // (1,0) -> luminanceMatrix[0][1]=0.2 -> round(0.2*255)=51 -> Color(51,51,51)
        assertEquals(new Color(51, 51, 51).getRGB() & 0xFFFFFF, generatedImage.getRGB(1, 0) & 0xFFFFFF);
        // (0,1) -> luminanceMatrix[1][0]=0.3 -> round(0.3*255)=77 -> Color(77,77,77)
        assertEquals(new Color(77, 77, 77).getRGB() & 0xFFFFFF, generatedImage.getRGB(0, 1) & 0xFFFFFF);
        // (1,1) -> luminanceMatrix[1][1]=0.4 -> round(0.4*255)=102 -> Color(102,102,102)
        assertEquals(new Color(102, 102, 102).getRGB() & 0xFFFFFF, generatedImage.getRGB(1, 1) & 0xFFFFFF);
    }

    @Test
    @DisplayName("testGenerateImage2dArray()")
    void testGenerateImage2dArray() {
        // Wavelet2dModel.generateImage(double[][], Point, int) の内部ロジックに基づくテスト
        // テストデータ: 行列の最大値が 1.0 で、様々な値を検証できるように調整
        double[][] valueMatrix = {
            {1.0, 0.5}, // row 0 (Y=0)
            {0.2, 0.8}  // row 1 (Y=1)
        };
        // matrix[row][col]
        // image.getRGB(x, y)
        // x = col, y = row
        Point scaleFactor = new Point(1, 1);
        int rgbFlag = 0; // Grayscale

        BufferedImage image = Wavelet2dModel.generateImage(valueMatrix, scaleFactor, rgbFlag);

        assertNotNull(image);
        // width は valueMatrix[0].length * scaleFactor.x (列数)
        assertEquals(valueMatrix[0].length * scaleFactor.x, image.getWidth(), "Image width should match scaled matrix width"); // 期待: 2 * 1 = 2
        // height は valueMatrix.length * scaleFactor.y (行数)
        assertEquals(valueMatrix.length * scaleFactor.y, image.getHeight(), "Image height should match scaled matrix height"); // 期待: 2 * 1 = 2

        // 内部で計算される maxAbsoluteValue は 1.0 になるはず (valueMatrix から)

        // ピクセル検証 (getRGB(x, y) = matrix[y][x])
        // (0,0) -> valueMatrix[0][0] = 1.0 -> round(1.0/1.0*255) = 255
        assertEquals(new Color(255, 255, 255).getRGB() & 0xFFFFFF, image.getRGB(0, 0) & 0xFFFFFF, "Pixel (0,0) should be white");
        // (1,0) -> valueMatrix[0][1] = 0.5 -> round(0.5/1.0*255) = 128
        assertEquals(new Color(128, 128, 128).getRGB() & 0xFFFFFF, image.getRGB(1, 0) & 0xFFFFFF, "Pixel (1,0) should be gray");
        // (0,1) -> valueMatrix[1][0] = 0.2 -> round(0.2/1.0*255) = 51
        assertEquals(new Color(51, 51, 51).getRGB() & 0xFFFFFF, image.getRGB(0, 1) & 0xFFFFFF, "Pixel (0,1) should be dark gray");
        // (1,1) -> valueMatrix[1][1] = 0.8 -> round(0.8/1.0*255) = 204
        assertEquals(new Color(204, 204, 204).getRGB() & 0xFFFFFF, image.getRGB(1, 1) & 0xFFFFFF, "Pixel (1,1) should be light gray");
    }

    @Test
    @DisplayName("testMaximumAbsoluteCoefficients()")
    void testMaximumAbsoluteCoefficients() {
        // モデルはsetUpで初期化され、doSampleCoefficients()が呼ばれる
        // maxAbsolute...Coefficient() は初回呼び出し時に計算される
        // SampleCoefficients は 0.2 と 1.0 の値を持つ (最大絶対値は 1.0)
        // SetSourceData 時に初期化されるため、直接アクセス
        double[][][] testData = {
            {{-1.0, 0.5}, {0.2, -0.8}}, // Luminance
            {{0.0, 0.0}, {0.0, 0.0}},   // R (nullでなくてもよいが、このテストでは使わない)
            {{0.0, 0.0}, {0.0, 0.0}},   // G
            {{0.0, 0.0}, {0.0, 0.0}}    // B
        };
        // SetSourceData をテストするために model を再初期化
        model = new Wavelet2dModel();
        model.setSourceData(testData); // 計算をトリガー

        assertEquals(1.0, model.maximumAbsoluteSourceCoefficient(), DELTA, "Absolute source coefficient should be 1.0");
        // scaling/wavelet/recomposed coefficients の最大値は DWT の計算結果に依存
        // 正確な値をアサーションする代わりに、0より大きいことを確認する
        assertTrue(model.maximumAbsoluteScalingCoefficient() > 0, "Scaling coefficient should be positive");
        assertTrue(model.maximumAbsoluteWaveletCoefficient() > 0, "Wavelet coefficient should be positive");
        assertTrue(model.maximumAbsoluteRecomposedCoefficient() > 0, "Recomposed coefficient should be positive");

        // キャッシュのテスト
        double initialMax = model.maximumAbsoluteSourceCoefficient();
        model.sourceCoefficientsArray[0][0][0] = 100.0; // 値を変更
        assertEquals(initialMax, model.maximumAbsoluteSourceCoefficient(), DELTA, "Cached value should be returned.");
    }

    @Test
    @DisplayName("testComputeFromPointAndRecomposition()")
    void testComputeFromPointAndRecomposition() {
        // まず、Smalltalk Balloon 画像をロードして係数を初期化
        model.doSmalltalkBalloon(); // 画像ロードと係数初期化

        Point testPoint = new Point(10, 10); // テスト用のポイント

        // Altキーなしで呼び出し (係数がコピーされるべき)
        model.computeFromPoint(testPoint, false);

        // 係数がクリアされていないことを確認 (特定の場所の係数が 0 でない)
        assertTrue(model.interactiveHorizontalWaveletCoefficientsArray[0][10][10] != 0.0, "Coefficient at (10,10) should be copied.");
        assertEquals(model.horizontalWaveletCoefficientsArray[0][10][10], model.interactiveHorizontalWaveletCoefficientsArray[0][10][10], DELTA);

        // 再構成された係数配列が null でないことを確認
        assertNotNull(model.recomposedCoefficientsArray[0]);

        // Altキーありで呼び出し (係数がクリアされるべき)
        model.computeFromPoint(testPoint, true); // Call with Alt key

        // 係数がクリアされたことを確認 (特定の場所の係数が 0 になった)
        assertEquals(0.0, model.interactiveHorizontalWaveletCoefficientsArray[0][10][10], DELTA, "Coefficient at (10,10) should be cleared.");
        assertEquals(0.0, model.interactiveVerticalWaveletCoefficientsArray[0][20][20], DELTA);
        assertEquals(0.0, model.interactiveDiagonalWaveletCoefficientsArray[0][30][30], DELTA);

        // 再構成された係数配列が null でないことを確認
        assertNotNull(model.recomposedCoefficientsArray[0]);
    }

    @Test
    @DisplayName("testDoAllCoefficients() copies all coefficients correctly")
    void testDoAllCoefficients() {
        model.doSampleCoefficients(); // 係数を初期化

        // interactive coefficients が初期状態で0であることを確認（念のため）
        assertEquals(0.0, model.interactiveHorizontalWaveletCoefficientsArray[0][10][10], DELTA);

        model.doAllCoefficients(); // 全ての係数をコピー

        // 係数がコピーされたことを確認（元の係数と一致）
        assertEquals(model.horizontalWaveletCoefficientsArray[0][10][10], model.interactiveHorizontalWaveletCoefficientsArray[0][10][10], DELTA);
        assertEquals(model.verticalWaveletCoefficientsArray[0][20][20], model.interactiveVerticalWaveletCoefficientsArray[0][20][20], DELTA);
        assertEquals(model.diagonalWaveletCoefficientsArray[0][30][30], model.interactiveDiagonalWaveletCoefficientsArray[0][30][30], DELTA);

        assertNotNull(model.recomposedCoefficientsArray[0]); // 再構成結果がnullでないことを確認
    }

    @Test
    @DisplayName("testDoClearCoefficients() clears interactive coefficients to zero")
    void testDoClearCoefficients() {
        model.doSampleCoefficients(); // 係数を初期化
        model.doAllCoefficients(); // まず全てコピーして、クリア対象の状態にする

        // 全てコピーされたことを確認（念のため）
        assertTrue(model.interactiveHorizontalWaveletCoefficientsArray[0][10][10] != 0.0, "Coefficients should be copied before clearing.");

        model.doClearCoefficients(); // 係数をクリア

        // 係数が全て0になったことを確認
        assertEquals(0.0, model.interactiveHorizontalWaveletCoefficientsArray[0][10][10], DELTA, "Coefficient should be cleared to 0.0.");
        assertEquals(0.0, model.interactiveVerticalWaveletCoefficientsArray[0][20][20], DELTA);
        assertEquals(0.0, model.interactiveDiagonalWaveletCoefficientsArray[0][30][30], DELTA);

        assertNotNull(model.recomposedCoefficientsArray[0]); // 再構成結果がnullでないことを確認
    }

    @Test
    @DisplayName("testSetSourceDataWith3dArray() correctly initializes data and coefficients")
    void testSetSourceDataWith3dArray() {
        double[][][] testData = {
            {{1.0, 0.0}, {0.0, 1.0}}, // Luminance (Y)
            {{1.0, 0.0}, {0.0, 0.0}}, // Red (R)
            {{0.0, 1.0}, {0.0, 0.0}}, // Green (G)
            {{0.0, 0.0}, {1.0, 0.0}}  // Blue (B)
        };
        model.setSourceData(testData); // データと係数を設定

        assertNotNull(model.sourceCoefficientsArray);
        assertEquals(4, model.sourceCoefficientsArray.length); // 4 channels should be set
        // データがコピーされていることを検証
        assertArrayEquals(testData[1][0], model.sourceCoefficientsArray[1][0], DELTA); // Verify a part of the Red channel data

        // 他の係数配列も初期化されていることを確認
        assertNotNull(model.scalingCoefficientsArray[1]);
        assertNotNull(model.horizontalWaveletCoefficientsArray[1]);
        assertNotNull(model.recomposedCoefficientsArray[1]);
        // ...
    }

    @Test
    @DisplayName("testImageSmalltalkBalloon()")
    void testImageSmalltalkBalloon() {
        BufferedImage image = Wavelet2dModel.imageSmalltalkBalloon();
        assertNotNull(image, "Image should not be null");
        assertEquals(256, image.getWidth(), "Image width should be 256");
        assertEquals(256, image.getHeight(), "Image height should be 256");
    }

    @Test
    @DisplayName("testImageEarth()")
    void testImageEarth() {
        BufferedImage image = Wavelet2dModel.imageEarth();
        assertNotNull(image, "Image should not be null");
        assertEquals(512, image.getWidth(), "Image width should be 512");
        assertEquals(256, image.getHeight(), "Image height should be 256");
    }

    @Test
    @DisplayName("testLrgbMatrixes()")
    void testLrgbMatrixes() {
        BufferedImage image = new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB);
        image.setRGB(0, 0, new Color(255, 0, 0).getRGB()); // (0,0) Red
        image.setRGB(1, 0, new Color(0, 255, 0).getRGB()); // (1,0) Green
        image.setRGB(0, 1, new Color(0, 0, 255).getRGB()); // (0,1) Blue
        image.setRGB(1, 1, new Color(128, 128, 128).getRGB()); // (1,1) Gray

        try (MockedStatic<ColorUtility> mockedColorUtility = Mockito.mockStatic(ColorUtility.class)) {
            // Mocking luminanceFromRGB to return specific values
            mockedColorUtility.when(() -> ColorUtility.luminanceFromRGB(new Color(255, 0, 0).getRGB())).thenReturn(0.299);
            mockedColorUtility.when(() -> ColorUtility.luminanceFromRGB(new Color(0, 255, 0).getRGB())).thenReturn(0.587);
            mockedColorUtility.when(() -> ColorUtility.luminanceFromRGB(new Color(0, 0, 255).getRGB())).thenReturn(0.114);
            mockedColorUtility.when(() -> ColorUtility.luminanceFromRGB(new Color(128, 128, 128).getRGB())).thenReturn(0.5019607843137255);

            // Mocking convertINTtoRGB to return specific values
            mockedColorUtility.when(() -> ColorUtility.convertINTtoRGB(new Color(255, 0, 0).getRGB())).thenReturn(new double[]{1.0, 0.0, 0.0});
            mockedColorUtility.when(() -> ColorUtility.convertINTtoRGB(new Color(0, 255, 0).getRGB())).thenReturn(new double[]{0.0, 1.0, 0.0});
            mockedColorUtility.when(() -> ColorUtility.convertINTtoRGB(new Color(0, 0, 255).getRGB())).thenReturn(new double[]{0.0, 0.0, 1.0});
            mockedColorUtility.when(() -> ColorUtility.convertINTtoRGB(new Color(128, 128, 128).getRGB())).thenReturn(new double[]{0.5019607843137255, 0.5019607843137255, 0.5019607843137255});

            double[][][] lrgbMatrixes = Wavelet2dModel.lrgbMatrixes(image);

            assertNotNull(lrgbMatrixes, "LRGB matrixes should not be null");
            assertEquals(4, lrgbMatrixes.length, "Should contain luminance, R, G, B matrixes");

            // dimensions for [channel][height][width] or [channel][row][col]
            assertEquals(image.getHeight(), lrgbMatrixes[0].length, "Matrix height should match image height (rows)");
            assertEquals(image.getWidth(), lrgbMatrixes[0][0].length, "Matrix width should match image width (columns)");

            // Verify a few specific pixels for luminance and RGB
            // Pixel (0,0) is Red
            assertEquals(0.299, lrgbMatrixes[0][0][0], DELTA, "Luminance for (0,0)"); // Luminance
            assertEquals(1.0, lrgbMatrixes[1][0][0], DELTA, "Red for (0,0)");     // R
            assertEquals(0.0, lrgbMatrixes[2][0][0], DELTA, "Green for (0,0)");   // G
            assertEquals(0.0, lrgbMatrixes[3][0][0], DELTA, "Blue for (0,0)");    // B

            // Pixel (1,0) is Green
            assertEquals(0.587, lrgbMatrixes[0][0][1], DELTA, "Luminance for (1,0)");
            assertEquals(0.0, lrgbMatrixes[1][0][1], DELTA, "Red for (1,0)");
            assertEquals(1.0, lrgbMatrixes[2][0][1], DELTA, "Green for (1,0)");
            assertEquals(0.0, lrgbMatrixes[3][0][1], DELTA, "Blue for (1,0)");

            // Pixel (0,1) is Blue
            assertEquals(0.114, lrgbMatrixes[0][1][0], DELTA, "Luminance for (0,1)");
            assertEquals(0.0, lrgbMatrixes[1][1][0], DELTA, "Red for (0,1)");
            assertEquals(0.0, lrgbMatrixes[2][1][0], DELTA, "Green for (0,1)");
            assertEquals(1.0, lrgbMatrixes[3][1][0], DELTA, "Blue for (0,1)");

            // Pixel (1,1) is Gray
            assertEquals(0.5019607843137255, lrgbMatrixes[0][1][1], DELTA, "Luminance for (1,1)");
            assertEquals(0.5019607843137255, lrgbMatrixes[1][1][1], DELTA, "Red for (1,1)");
            assertEquals(0.5019607843137255, lrgbMatrixes[2][1][1], DELTA, "Green for (1,1)");
            assertEquals(0.5019607843137255, lrgbMatrixes[3][1][1], DELTA, "Blue for (1,1)");
        }
    }

    @Test
    @DisplayName("testOpen()")
    void testOpen() {
        // UIが表示されること自体をテストすることは難しい
        // しかし、関連するオブジェクトが初期化され、エラーが出ないことを確認する
        // model.open() は JFrame を作成し、表示する
        assertDoesNotThrow(() -> model.open());
        // その後、手動でウィンドウが閉じるのを待つ必要があるかもしれません。
        // 自動テストでは、通常 GUI の表示を直接テストすることは避けます。
        // ここでは、メソッドが例外を投げずに実行されることを確認するのみとします。
    }
}
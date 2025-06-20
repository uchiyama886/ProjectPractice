package utility;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import wavelet.Constants;
import wavelet.Wavelet2dModel;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("WaveletData Class Unit Tests")
class WaveletDataTest {

    private PrintStream originalOut;
    private ByteArrayOutputStream outContent;
    private PrintStream originalErr;
    private ByteArrayOutputStream errContent;

    @BeforeEach
    void setUp() {
        originalOut = System.out;
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        originalErr = System.err;
        errContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errContent));
    }

    @AfterEach
    void restoreStreams() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    private static final double DELTA = 1e-9;

    @Test
    @DisplayName("Sample1dCoefficients() generates correct 1D sample data")
    void testSample1dCoefficients() {
        double[] coefficients = WaveletData.Sample1dCoefficients();
        assertNotNull(coefficients, "Coefficients array should not be null");
        assertEquals(64, coefficients.length, "Array length should be 64");

        for (int i = 0; i < 16; i++) {
            assertEquals(Math.pow((double) (i + 1), 2.0d) / 256.0d, coefficients[i], DELTA, "Coefficients[i] should match quadratic formula for i < 16");
        }
        for (int i = 16; i < 32; i++) {
            assertEquals(0.2d, coefficients[i], DELTA, "Coefficients[i] should be 0.2 for 16 <= i < 32");
        }
        for (int i = 32; i < 48; i++) {
            assertEquals(Math.pow((double) (48 - (i + 1)), 2.0d) / 256.0d - 0.5d, coefficients[i], DELTA, "Coefficients[i] should match inverted quadratic for 32 <= i < 48");
        }
        for (int i = 48; i < 64; i++) {
            assertEquals(0.0d, coefficients[i], DELTA, "Coefficients[i] should be 0.0 for i >= 48");
        }
    }

    @Test
    @DisplayName("Sample2dCoefficients() generates correct 2D sample data")
    void testSample2dCoefficients() {
        double[][] matrix = WaveletData.Sample2dCoefficients();
        assertNotNull(matrix, "Matrix should not be null");
        assertEquals(64, matrix.length, "Matrix height should be 64");
        assertEquals(64, matrix[0].length, "Matrix width should be 64");

        for (int y = 0; y < 64; y++) {
            for (int x = 0; x < 64; x++) {
                boolean expectedToBeOne = false;
                boolean inRange = (x >= 5 && x < 64 - 5) && (y >= 5 && y < 64 - 5);

                if (inRange) {
                    if (y == 5 || y == 64 - 6 || x == 5 || x == 64 - 6) {
                        expectedToBeOne = true;
                    }
                    if (y == x || y == (64 - 1 - x)) {
                        expectedToBeOne = true;
                    }
                }

                if (expectedToBeOne) {
                    assertEquals(1.0d, matrix[y][x], DELTA, "Border or diagonal pixel should be 1.0 at (" + x + "," + y + ")");
                } else {
                    assertEquals(0.2d, matrix[y][x], DELTA, "Other pixel should be 0.2 at (" + x + "," + y + ")");
                }
            }
        }
    }

    @Test
    @DisplayName("dataEarth() reads Earth image and converts to LRBG matrixes")
    void testDataEarth() {
        BufferedImage mockImage = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        double[][][] expectedMatrixes = new double[4][10][10];

        try (MockedStatic<Wavelet2dModel> mockedWavelet2dModel = Mockito.mockStatic(Wavelet2dModel.class)) {
            mockedWavelet2dModel.when(Wavelet2dModel::imageEarth).thenReturn(mockImage);
            mockedWavelet2dModel.when(() -> Wavelet2dModel.lrgbMatrixes(mockImage)).thenReturn(expectedMatrixes);

            double[][][] result = WaveletData.dataEarth();
            assertNotNull(result, "Result should not be null");
            assertEquals(expectedMatrixes, result, "Returned matrixes should be from Wavelet2dModel");
            mockedWavelet2dModel.verify(Wavelet2dModel::imageEarth, times(1));
            mockedWavelet2dModel.verify(() -> Wavelet2dModel.lrgbMatrixes(mockImage), times(1));
        }
    }

    @Test
    @DisplayName("dataSmalltalkBalloon() reads SmalltalkBalloon image and converts to LRBG matrixes")
    void testDataSmalltalkBalloon() {
        BufferedImage mockImage = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        double[][][] expectedMatrixes = new double[4][10][10];

        try (MockedStatic<Wavelet2dModel> mockedWavelet2dModel = Mockito.mockStatic(Wavelet2dModel.class)) {
            mockedWavelet2dModel.when(Wavelet2dModel::imageSmalltalkBalloon).thenReturn(mockImage);
            mockedWavelet2dModel.when(() -> Wavelet2dModel.lrgbMatrixes(mockImage)).thenReturn(expectedMatrixes);

            double[][][] result = WaveletData.dataSmalltalkBalloon();
            assertNotNull(result, "Result should not be null");
            assertEquals(expectedMatrixes, result, "Returned matrixes should be from Wavelet2dModel");
            mockedWavelet2dModel.verify(Wavelet2dModel::imageSmalltalkBalloon, times(1));
            mockedWavelet2dModel.verify(() -> Wavelet2dModel.lrgbMatrixes(mockImage), times(1));
        }
    }

    @Test
    @DisplayName("fill() initializes all elements of a 2D array with a value")
    void testFill() {
        double[][] matrix = new double[2][3];
        WaveletData.fill(matrix, 0.5d);

        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                assertEquals(0.5d, matrix[i][j], DELTA, "All elements should be 0.5");
            }
        }
    }

    @Test
    @DisplayName("generateImage(double[][][], double) generates grayscale image when RGB components are null")
    void testGenerateImageGrayscale() {
        double[][] valueMatrix = {
            {0.0, 1.0}, // [0][0]=0.0, [0][1]=1.0
            {0.5, 0.25} // [1][0]=0.5, [1][1]=0.25
        };
        double maxValue = 1.0;
        double[][][] valueMatrixArray = {valueMatrix, null, null, null};

        BufferedImage image = WaveletData.generateImage(valueMatrixArray, maxValue);

        assertNotNull(image, "Generated image should not be null");
        assertEquals(valueMatrix.length, image.getWidth(), "Image width should match matrix width");
        assertEquals(valueMatrix[0].length, image.getHeight(), "Image height should match matrix height");

        // Pixels validation: getRGB(x, y) に対応する matrix[x][y]
        // (0,0) -> valueMatrix[0][0]=0.0 -> abs(0.0)/1.0 * 255 = 0 -> Color(0,0,0)
        assertEquals(new Color(0, 0, 0).getRGB(), image.getRGB(0, 0), "Pixel (0,0) should be black");
        // (1,0) -> valueMatrix[1][0]=0.5 -> abs(0.5)/1.0 * 255 = 128 -> Color(128,128,128)
        assertEquals(new Color(128, 128, 128).getRGB(), image.getRGB(1, 0), "Pixel (1,0) should be gray");
        // (0,1) -> valueMatrix[0][1]=1.0 -> abs(1.0)/1.0 * 255 = 255 -> Color(255,255,255)
        assertEquals(new Color(255, 255, 255).getRGB(), image.getRGB(0, 1), "Pixel (0,1) should be white");
        // (1,1) -> valueMatrix[1][1]=0.25 -> abs(0.25)/1.0 * 255 = 64 -> Color(64,64,64)
        assertEquals(new Color(64, 64, 64).getRGB(), image.getRGB(1, 1), "Pixel (1,1) should be dark gray");
    }

    @Test
    @DisplayName("generateImage(double[][][], double) generates color image when RGB components exist")
    void testGenerateImageColor() {
        // WaveletData.generateImage は width = valueMatrix.length, height = valueMatrix[0].length と解釈する
        // BufferedImage は width (列数), height (行数) の順でコンストラクタ引数を取るため、
        // テストの入力行列の定義を調整し、ArrayIndexOutOfBoundsExceptionを回避する
        // 2x1画像 (幅2, 高さ1) を生成するための行列
        double[][] yMatrixDummy = {{0.0}, {0.0}}; // ダミーなので内容は重要ではないが、次元を合わせる
        double[][] rMatrix = {{0.0}, {1.0}};     // R成分: valueMatrix[0][0]=0.0, valueMatrix[1][0]=1.0
        double[][] gMatrix = {{1.0}, {0.0}};     // G成分: valueMatrix[0][0]=1.0, valueMatrix[1][0]=0.0
        double[][] bMatrix = {{0.0}, {0.0}};     // B成分: valueMatrix[0][0]=0.0, valueMatrix[1][0]=0.0
        double maxValue = 1.0;

        double[][][] valueMatrixArray = {yMatrixDummy, rMatrix, gMatrix, bMatrix};

        BufferedImage image = WaveletData.generateImage(valueMatrixArray, maxValue);

        assertNotNull(image, "生成された画像はnullであってはいけません");
        // WaveletData.generateImage の width = valueMatrix.length (行列の行数)
        // WaveletData.generateImage の height = valueMatrix[0].length (行列の列数)
        // rMatrixの定義により、width=2, height=1 の画像が生成される
        assertEquals(rMatrix.length, image.getWidth(), "画像の幅は行列の行数と一致するはずです"); // rMatrix.length は 2
        assertEquals(rMatrix[0].length, image.getHeight(), "画像の高さは行列の列数と一致するはずです"); // rMatrix[0].length は 1

        // ピクセル (0,0) (x=0, y=0)
        // rMatrix[0][0]=0.0, gMatrix[0][0]=1.0, bMatrix[0][0]=0.0 -> Color(0,255,0) (緑)
        assertEquals(new Color(0, 255, 0).getRGB(), image.getRGB(0, 0), "ピクセル (0,0) は緑であるべきです");
        // ピクセル (1,0) (x=1, y=0)
        // rMatrix[1][0]=1.0, gMatrix[1][0]=0.0, bMatrix[1][0]=0.0 -> Color(255,0,0) (赤)
        assertEquals(new Color(255, 0, 0).getRGB(), image.getRGB(1, 0), "ピクセル (1,0) は赤であるべきです");
    }

    @Test
    @DisplayName("generateImage(double[][], Point, int) generates red component image")
    void testGenerateImageSingleMatrixRed() {
        double[][] valueMatrix = {{0.5}};
        Point scaleFactor = new Point(1, 1);
        int rgbFlag = Constants.Red;

        BufferedImage image = WaveletData.generateImage(valueMatrix, scaleFactor, rgbFlag);

        assertNotNull(image);
        assertEquals(new Color(255, 0, 0).getRGB(), image.getRGB(0, 0), "Pixel (0,0) should be red (scaled)");
    }

    @Test
    @DisplayName("lrgbMatrixes() extracts luminance and RGB matrixes from image")
    void testLrgbMatrixes() {
        BufferedImage image = new BufferedImage(2, 1, BufferedImage.TYPE_INT_RGB);
        image.setRGB(0, 0, new Color(255, 0, 0).getRGB());
        image.setRGB(1, 0, new Color(0, 255, 0).getRGB());

        try (MockedStatic<ColorUtility> mockedColorUtility = Mockito.mockStatic(ColorUtility.class)) {
            mockedColorUtility.when(() -> ColorUtility.luminanceFromRGB(new Color(255, 0, 0).getRGB())).thenReturn(0.299);
            mockedColorUtility.when(() -> ColorUtility.convertINTtoRGB(new Color(255, 0, 0).getRGB())).thenReturn(new double[]{1.0, 0.0, 0.0});
            mockedColorUtility.when(() -> ColorUtility.luminanceFromRGB(new Color(0, 255, 0).getRGB())).thenReturn(0.587);
            mockedColorUtility.when(() -> ColorUtility.convertINTtoRGB(new Color(0, 255, 0).getRGB())).thenReturn(new double[]{0.0, 1.0, 0.0});

            double[][][] lrgbMatrixes = WaveletData.lrgbMatrixes(image);

            assertNotNull(lrgbMatrixes, "LRGB matrixes should not be null");
            assertEquals(4, lrgbMatrixes.length, "Should contain luminance, R, G, B matrixes");

            assertEquals(image.getWidth(), lrgbMatrixes[0].length);
            assertEquals(image.getHeight(), lrgbMatrixes[0][0].length);

            assertEquals(0.299, lrgbMatrixes[0][0][0], DELTA, "Luminance for Red pixel at (0,0)");
            assertEquals(1.0, lrgbMatrixes[1][0][0], DELTA, "Red component for Red pixel at (0,0)");
            assertEquals(0.0, lrgbMatrixes[2][0][0], DELTA, "Green component for Red pixel at (0,0)");
            assertEquals(0.0, lrgbMatrixes[3][0][0], DELTA, "Blue component for Red pixel at (0,0)");

            assertEquals(0.587, lrgbMatrixes[0][1][0], DELTA, "Luminance for Green pixel at (1,0)");
            assertEquals(0.0, lrgbMatrixes[1][1][0], DELTA, "Red component for Green pixel at (1,0)");
            assertEquals(1.0, lrgbMatrixes[2][1][0], DELTA, "Green component for Green pixel at (1,0)");
            assertEquals(0.0, lrgbMatrixes[3][1][0], DELTA, "Blue component for Green pixel at (1,0)");
        }
    }
}
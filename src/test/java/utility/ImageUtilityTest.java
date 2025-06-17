package utility;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("ImageUtility Class Unit Tests")
class ImageUtilityTest {

    @TempDir
    Path tempDir;

    private BufferedImage testImage;
    private static final double DELTA = 1e-9;

    @BeforeEach
    void setUp() {
        testImage = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = testImage.createGraphics();
        g2d.setColor(Color.RED);
        g2d.fillRect(0, 0, 10, 10);
        g2d.dispose();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    @DisplayName("adjustImage() resizes image to specified dimensions")
    void testAdjustImage() {
        int newWidth = 20;
        int newHeight = 5;
        BufferedImage adjustedImage = ImageUtility.adjustImage(testImage, newWidth, newHeight);

        assertNotNull(adjustedImage, "Adjusted image should not be null");
        assertEquals(newWidth, adjustedImage.getWidth(), "Adjusted image width should match newWidth");
        assertEquals(newHeight, adjustedImage.getHeight(), "Adjusted image height should match newHeight");
    }

    @Test
    @DisplayName("grayscaleImage() converts image to grayscale")
    void testGrayscaleImage() {
        BufferedImage colorImage = new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB);
        colorImage.setRGB(0, 0, new Color(255, 0, 0).getRGB());
        colorImage.setRGB(1, 0, new Color(0, 255, 0).getRGB());
        colorImage.setRGB(0, 1, new Color(0, 0, 255).getRGB());
        colorImage.setRGB(1, 1, new Color(128, 128, 128).getRGB());

        try (MockedStatic<ColorUtility> mockedColorUtility = Mockito.mockStatic(ColorUtility.class)) {
            // ColorUtility.luminanceFromRGB は int を受け取る
            mockedColorUtility.when(() -> ColorUtility.luminanceFromRGB(new Color(255, 0, 0).getRGB())).thenReturn(0.299);
            mockedColorUtility.when(() -> ColorUtility.luminanceFromRGB(new Color(0, 255, 0).getRGB())).thenReturn(0.587);
            mockedColorUtility.when(() -> ColorUtility.luminanceFromRGB(new Color(0, 0, 255).getRGB())).thenReturn(0.114);
            mockedColorUtility.when(() -> ColorUtility.luminanceFromRGB(new Color(128, 128, 128).getRGB())).thenReturn(0.5019607843137255);

            // ColorUtility.convertRGBtoINT は double を受け取る
            mockedColorUtility.when(() -> ColorUtility.convertRGBtoINT(anyDouble(), anyDouble(), anyDouble())).thenAnswer(invocation -> {
                double r = invocation.getArgument(0);
                double g = invocation.getArgument(1);
                double b = invocation.getArgument(2);
                int red = (int) Math.round(r * 255.0d);
                int green = (int) Math.round(g * 255.0d);
                int blue = (int) Math.round(b * 255.0d);
                // 元のColorUtility.convertRGBtoINTの実装（アルファなし）をシミュレート
                return (red << 16) | (green << 8) | blue;
            });

            BufferedImage grayscaleImage = ImageUtility.grayscaleImage(colorImage);

            assertNotNull(grayscaleImage, "Grayscale image should not be null");
            assertEquals(colorImage.getWidth(), grayscaleImage.getWidth(), "Width should remain same");
            assertEquals(colorImage.getHeight(), grayscaleImage.getHeight(), "Height should remain same");

            // 期待されるグレースケール値 (アルファなし)
            int expectedRedGray = ((int) Math.round(0.299 * 255) << 16) | ((int) Math.round(0.299 * 255) << 8) | (int) Math.round(0.299 * 255);
            int expectedGreenGray = ((int) Math.round(0.587 * 255) << 16) | ((int) Math.round(0.587 * 255) << 8) | (int) Math.round(0.587 * 255);
            int expectedBlueGray = ((int) Math.round(0.114 * 255) << 16) | ((int) Math.round(0.114 * 255) << 8) | (int) Math.round(0.114 * 255);
            int expectedGrayGray = (128 << 16) | (128 << 8) | 128; // 元のカラーが灰色なのでそのまま

            assertEquals(expectedRedGray, grayscaleImage.getRGB(0, 0) & 0xFFFFFF, "赤ピクセルはグレースケールに変換されるべきです"); // & 0xFFFFFF を追加
            assertEquals(expectedGreenGray, grayscaleImage.getRGB(1, 0) & 0xFFFFFF, "緑ピクセルはグレースケールに変換されるべきです"); // & 0xFFFFFF を追加
            assertEquals(expectedBlueGray, grayscaleImage.getRGB(0, 1) & 0xFFFFFF, "青ピクセルはグレースケールに変換されるべきです"); // & 0xFFFFFF を追加
            assertEquals(expectedGrayGray, grayscaleImage.getRGB(1, 1) & 0xFFFFFF, "灰色のピクセルはグレースケールのままであるべきです"); // & 0xFFFFFF を追加
        }
    }

    @Test
    @DisplayName("copyImage() creates a deep copy of the image")
    void testCopyImage() {
        BufferedImage copiedImage = ImageUtility.copyImage(testImage);

        assertNotNull(copiedImage, "Copied image should not be null");
        assertNotSame(testImage, copiedImage, "Copied image should be a different instance");
        assertEquals(testImage.getWidth(), copiedImage.getWidth(), "Width should be same");
        assertEquals(testImage.getHeight(), copiedImage.getHeight(), "Height should be same");
        assertEquals(testImage.getRGB(0, 0), copiedImage.getRGB(0, 0), "Pixel data should be same");

        testImage.setRGB(0, 0, Color.BLUE.getRGB());
        assertNotEquals(testImage.getRGB(0, 0), copiedImage.getRGB(0, 0), "Changing original should not affect copy");
    }

    @Test
    @DisplayName("convertImageToLuminanceMatrix() converts image to luminance matrix")
    void testConvertImageToLuminanceMatrix() {
        BufferedImage image = new BufferedImage(2, 1, BufferedImage.TYPE_INT_RGB);
        image.setRGB(0, 0, new Color(255, 0, 0).getRGB());
        image.setRGB(1, 0, new Color(0, 255, 0).getRGB());

        try (MockedStatic<ColorUtility> mockedColorUtility = Mockito.mockStatic(ColorUtility.class)) {
            mockedColorUtility.when(() -> ColorUtility.luminanceFromRGB(new Color(255, 0, 0).getRGB())).thenReturn(0.299);
            mockedColorUtility.when(() -> ColorUtility.luminanceFromRGB(new Color(0, 255, 0).getRGB())).thenReturn(0.587);

            double[][] luminanceMatrix = ImageUtility.convertImageToLuminanceMatrix(image);

            assertNotNull(luminanceMatrix, "Luminance matrix should not be null");
            assertEquals(image.getHeight(), luminanceMatrix.length, "Matrix height should match image height");
            assertEquals(image.getWidth(), luminanceMatrix[0].length, "Matrix width should match image width");

            assertEquals(0.299, luminanceMatrix[0][0], DELTA, "Luminance for (0,0) should be 0.299");
            assertEquals(0.587, luminanceMatrix[0][1], DELTA, "Luminance for (0,1) should be 0.587");
        }
    }

    @Test
    @DisplayName("convertImageToYUVMatrixes() converts image to YUV matrixes")
    void testConvertImageToYUVMatrixes() {
        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        image.setRGB(0, 0, new Color(255, 0, 255).getRGB());

        try (MockedStatic<ColorUtility> mockedColorUtility = Mockito.mockStatic(ColorUtility.class)) {
            double[] expectedYUV = {0.413, 0.25, 0.25};
            mockedColorUtility.when(() -> ColorUtility.convertRGBtoYUV(image.getRGB(0, 0))).thenReturn(expectedYUV);

            double[][][] yuvMatrixes = ImageUtility.convertImageToYUVMatrixes(image);

            assertNotNull(yuvMatrixes, "YUV matrixes should not be null");
            assertEquals(3, yuvMatrixes.length, "Should contain Y, U, V matrixes");
            assertEquals(1, yuvMatrixes[0].length, "Y matrix width should match image width");
            assertEquals(1, yuvMatrixes[0][0].length, "Y matrix height should match image height");

            assertEquals(expectedYUV[0], yuvMatrixes[0][0][0], DELTA, "Y component should be correct");
            assertEquals(expectedYUV[1], yuvMatrixes[1][0][0], DELTA, "U component should be correct");
            assertEquals(expectedYUV[2], yuvMatrixes[2][0][0], DELTA, "V component should be correct");
        }
    }

    @Test
    @DisplayName("convertLuminanceMatrixToImage() converts luminance matrix to image")
    void testConvertLuminanceMatrixToImage() {
        double[][] luminanceMatrix = {
            {0.0, 0.5},
            {1.0, 0.2}
        };

        try (MockedStatic<ColorUtility> mockedColorUtility = Mockito.mockStatic(ColorUtility.class)) {
            // ColorUtility.convertYUVtoRGB は double を受け取る
            mockedColorUtility.when(() -> ColorUtility.convertYUVtoRGB(anyDouble(), anyDouble(), anyDouble())).thenAnswer(invocation -> {
                double y = invocation.getArgument(0);
                // 元の変換式をシミュレート
                return new double[]{y, y, y}; // グレースケールなのでR=G=B=Y
            });

            // ColorUtility.convertRGBtoINT は double[] を受け取る
            mockedColorUtility.when(() -> ColorUtility.convertRGBtoINT(any(double[].class))).thenAnswer(invocation -> {
                double[] rgb = invocation.getArgument(0);
                int r = (int) Math.round(rgb[0] * 255.0);
                int g = (int) Math.round(rgb[1] * 255.0);
                int b = (int) Math.round(rgb[2] * 255.0);
                return (r << 16) | (g << 8) | b; // 元のImageUtility.convertRGBtoINTの実装をシミュレート
            });

            BufferedImage image = ImageUtility.convertLuminanceMatrixToImage(luminanceMatrix);

            assertNotNull(image, "Image should not be null");
            assertEquals(luminanceMatrix[0].length, image.getWidth(), "Image width should match matrix width");
            assertEquals(luminanceMatrix.length, image.getHeight(), "Image height should match matrix height");

            assertEquals((0 << 16) | (0 << 8) | 0, image.getRGB(0, 0) & 0xFFFFFF, "Pixel (0,0) should be black");
            assertEquals((128 << 16) | (128 << 8) | 128, image.getRGB(1, 0) & 0xFFFFFF, "Pixel (1,0) should be gray");
            assertEquals((255 << 16) | (255 << 8) | 255, image.getRGB(0, 1) & 0xFFFFFF, "Pixel (0,1) should be white");
            assertEquals((51 << 16) | (51 << 8) | 51, image.getRGB(1, 1) & 0xFFFFFF, "Pixel (1,1) should be dark gray");
        }
    }

    @Test
    @DisplayName("convertYUVMatrixesToImage() converts YUV matrixes to image")
    void testConvertYUVMatrixesToImage() {
        double[][][] yuvMatrixes = {
            {{1.0, 0.0}}, // Y
            {{0.0, 0.0}}, // U
            {{0.0, 0.0}} // V
        };

        try (MockedStatic<ColorUtility> mockedColorUtility = Mockito.mockStatic(ColorUtility.class)) {
            mockedColorUtility.when(() -> ColorUtility.convertYUVtoRGB(anyDouble(), anyDouble(), anyDouble())).thenAnswer(invocation -> {
                double y = invocation.getArgument(0);
                double u = invocation.getArgument(1);
                double v = invocation.getArgument(2);
                // 元の変換式をシミュレート
                double r = (1.000d * y) + (1.402d * v);
                double g = (1.000d * y) + (-0.344d * u) + (-0.714d * v);
                double b = (1.000d * y) + (1.772d * u);
                return new double[]{r, g, b};
            });
            mockedColorUtility.when(() -> ColorUtility.convertRGBtoINT(any(double[].class))).thenAnswer(invocation -> {
                double[] rgb = invocation.getArgument(0);
                int r = (int) Math.round(rgb[0] * 255.0);
                int g = (int) Math.round(rgb[1] * 255.0);
                int b = (int) Math.round(rgb[2] * 255.0);
                return (r << 16) | (g << 8) | b;
            });

            BufferedImage image = ImageUtility.convertYUVMatrixesToImage(yuvMatrixes);

            assertNotNull(image, "Image should not be null");
            assertEquals(yuvMatrixes[0][0].length, image.getWidth(), "Image width should match matrix width");
            assertEquals(yuvMatrixes[0].length, image.getHeight(), "Image height should match matrix height");

            assertEquals((255 << 16) | (255 << 8) | 255, image.getRGB(0, 0) & 0xFFFFFF, "Pixel (0,0) should be white");
            assertEquals((0 << 16) | (0 << 8) | 0, image.getRGB(1, 0) & 0xFFFFFF, "Pixel (1,0) should be black");
        }
    }

    @Test
    @DisplayName("readImage(File) reads image from file")
    void testReadImageFile() throws IOException {
        Path filePath = tempDir.resolve("test_read.png");
        ImageIO.write(testImage, "png", filePath.toFile());

        BufferedImage readImage = ImageUtility.readImage(filePath.toFile());

        assertNotNull(readImage, "Read image should not be null");
        assertEquals(testImage.getWidth(), readImage.getWidth(), "Width should match");
        assertEquals(testImage.getHeight(), readImage.getHeight(), "Height should match");
        assertEquals(testImage.getRGB(0, 0), readImage.getRGB(0, 0), "Pixel should match");
    }

    @Test
    @DisplayName("readImage(String) reads image from file string")
    void testReadImageString() throws IOException {
        Path filePath = tempDir.resolve("test_read_string.png");
        ImageIO.write(testImage, "png", filePath.toFile());

        BufferedImage readImage = ImageUtility.readImage(filePath.toString());

        assertNotNull(readImage, "Read image should not be null");
        assertEquals(testImage.getWidth(), readImage.getWidth(), "Width should match");
        assertEquals(testImage.getHeight(), readImage.getHeight(), "Height should match");
        assertEquals(testImage.getRGB(0, 0), readImage.getRGB(0, 0), "Pixel should match");
    }

    @Test
    @DisplayName("readImageFromFile(File) reads image from file and handles IOException")
    void testReadImageFromFileIOException() {
        File mockFile = Mockito.mock(File.class);
        when(mockFile.exists()).thenReturn(true);
        when(mockFile.getAbsolutePath()).thenReturn("/dummy/path/nonexistent.png");

        try (MockedStatic<ImageIO> mockedImageIO = Mockito.mockStatic(ImageIO.class)) {
            mockedImageIO.when(() -> ImageIO.read(mockFile)).thenThrow(new IOException("Test Read Error"));

            BufferedImage result = ImageUtility.readImageFromFile(mockFile);
            assertNull(result, "Should return null on IOException");
            // printStackTrace() が System.err に出力されることを確認
            assertTrue(System.err.toString().contains("java.io.IOException: Test Read Error"), "Stack trace should be printed to stderr");
        }
    }

    @Test
    @DisplayName("readImageFromURL(URL) reads image from URL and handles IOException")
    void testReadImageFromURLIOException() throws MalformedURLException {
        URL mockURL = Mockito.mock(URL.class);
        try (MockedStatic<ImageIO> mockedImageIO = Mockito.mockStatic(ImageIO.class)) {
            mockedImageIO.when(() -> ImageIO.read(mockURL)).thenThrow(new IOException("URL Read Error"));

            BufferedImage result = ImageUtility.readImageFromURL(mockURL);
            assertNull(result, "Should return null on IOException");
            // printStackTrace() が System.err に出力されることを確認
            assertTrue(System.err.toString().contains("java.io.IOException: URL Read Error"), "Stack trace should be printed to stderr");
        }
    }

    @Test
    @DisplayName("readImageFromURL(String) converts string to URL and reads image")
    void testReadImageFromURLString() throws IOException, URISyntaxException {
        Path filePath = tempDir.resolve("test_read_url.png");
        ImageIO.write(testImage, "png", filePath.toFile());

        String urlString = filePath.toUri().toURL().toString();

        try (MockedStatic<ImageUtility> mockedImageUtility = Mockito.mockStatic(ImageUtility.class, CALLS_REAL_METHODS)) {
            mockedImageUtility.when(() -> ImageUtility.readImageFromURL(any(URL.class))).thenReturn(testImage);

            BufferedImage result = ImageUtility.readImageFromURL(urlString);

            assertNotNull(result, "Resulting image should not be null");
            assertEquals(testImage, result, "Returned image should be the expected test image");
            mockedImageUtility.verify(() -> ImageUtility.readImageFromURL(any(URL.class)), times(1));
        }
    }

    @Test
    @DisplayName("readImageFromURL(String) returns null on invalid URL string")
    void testReadImageFromURLStringInvalid() {
        String invalidUrlString = "this is not a url";
        BufferedImage result = ImageUtility.readImageFromURL(invalidUrlString);
        assertNull(result, "Should return null for invalid URL string");
        // StringUtility.readTextFromURL(String) の元の実装が URISyntaxException を捕捉し printStackTrace() を呼び出すため、
        // エラー出力が標準エラーに表示されることを確認する。
        assertTrue(System.err.toString().contains("java.net.URISyntaxException"), "URISyntaxException stack trace should be printed to stderr");
    }

    @Test
    @DisplayName("writeImage(BufferedImage, File) writes image to file")
    void testWriteImageFile() throws IOException {
        Path outputPath = tempDir.resolve("output.png");
        ImageUtility.writeImage(testImage, outputPath.toFile());

        assertTrue(Files.exists(outputPath), "Output file should exist");
        BufferedImage writtenImage = ImageIO.read(outputPath.toFile());
        assertNotNull(writtenImage, "Written image should be readable");
        assertEquals(testImage.getWidth(), writtenImage.getWidth());
        assertEquals(testImage.getHeight(), writtenImage.getHeight());
        assertEquals(testImage.getRGB(0, 0), writtenImage.getRGB(0, 0));
    }

    @Test
    @DisplayName("writeImage(BufferedImage, String) writes image to file string")
    void testWriteImageString() throws IOException {
        String outputPathString = tempDir.resolve("output_string.jpg").toString();
        ImageUtility.writeImage(testImage, outputPathString);

        File outputFile = new File(outputPathString);
        assertTrue(outputFile.exists(), "Output file should exist");
        BufferedImage writtenImage = ImageIO.read(outputFile);
        assertNotNull(writtenImage, "Written image should be readable");
    }

    @Test
    @DisplayName("writeImage() handles IOException during write")
    void testWriteImageIOException() throws IOException {
        File mockFile = Mockito.mock(File.class);
        BufferedImage dummyImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);

        // writeImage メソッドが getName() を呼び出すため、モックを設定
        when(mockFile.getName()).thenReturn("dummy.png");

        try (MockedStatic<ImageIO> mockedImageIO = Mockito.mockStatic(ImageIO.class)) {
            mockedImageIO.when(() -> ImageIO.write(any(BufferedImage.class), anyString(), any(File.class)))
                    .thenThrow(new IOException("Test Write Error"));

            ImageUtility.writeImage(dummyImage, mockFile);
            mockedImageIO.verify(() -> ImageIO.write(any(BufferedImage.class), anyString(), any(File.class)), times(1));
            // printStackTrace() が System.err に出力されることを確認
            assertTrue(System.err.toString().contains("java.io.IOException: Test Write Error"), "Stack trace should be printed to stderr");
        }
    }
}

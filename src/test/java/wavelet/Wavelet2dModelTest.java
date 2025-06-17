package wavelet;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.awt.image.BufferedImage;
import java.awt.Point;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Color; // Add Color import

import wavelet.Wavelet2dModel; // Test target class
import wavelet.Constants; // Constants class needed
import utility.ImageUtility; // ImageUtility needed

public class Wavelet2dModelTest {

    private Wavelet2dModel model;

    @BeforeEach
    void setUp() {
        // Create a new Wavelet2dModel instance before each test
        model = new Wavelet2dModel();
    }

    @Test
    void testConstructorAndInitialState() {
        // Verify that the constructor correctly initializes and sourceCoefficientsArray is not null
        assertNotNull(model.sourceCoefficientsArray, "Source coefficients array should not be null after construction.");
        // Verify that data is loaded because sample data is loaded
        assertTrue(model.sourceCoefficientsArray.length > 0, "Source coefficients array should contain data.");

        // Verify other coefficient arrays are initialized
        assertNotNull(model.scalingCoefficientsArray);
        assertNotNull(model.horizontalWaveletCoefficientsArray);
        assertNotNull(model.verticalWaveletCoefficientsArray);
        assertNotNull(model.diagonalWaveletCoefficientsArray);
        assertNotNull(model.interactiveHorizontalWaveletCoefficientsArray);
        assertNotNull(model.interactiveVerticalWaveletCoefficientsArray);
        assertNotNull(model.interactiveDiagonalWaveletCoefficientsArray);
        assertNotNull(model.recomposedCoefficientsArray);

        // PaneModel initialization could be indirectly verified through public methods that use them.
    }

    @Test
    void testSetSourceDataWith2dArray() {
        double[][] testData = {
                {0.1, 0.2, 0.3, 0.4},
                {0.5, 0.6, 0.7, 0.8},
                {0.9, 1.0, 1.1, 1.2},
                {1.3, 1.4, 1.5, 1.6}
        };
        model.setSourceData(testData);

        // Verify source data is correctly set
        assertNotNull(model.sourceCoefficientsArray);
        assertEquals(1, model.sourceCoefficientsArray.length); // When a 2D array is passed, it is treated as one channel
        assertArrayEquals(testData[0], model.sourceCoefficientsArray[0][0], 0.001);

        // Verify transformed coefficient arrays are also updated
        assertNotNull(model.scalingCoefficientsArray[0]);
        assertNotNull(model.horizontalWaveletCoefficientsArray[0]);
        assertNotNull(model.recomposedCoefficientsArray[0]);

        // Verify max absolute values are reset and recalculated (just check they are not null, as exact values depend on transformation logic)
        assertTrue(model.maximumAbsoluteSourceCoefficient() > 0);
        assertTrue(model.maximumAbsoluteScalingCoefficient() > 0);
        assertTrue(model.maximumAbsoluteWaveletCoefficient() > 0);
        assertTrue(model.maximumAbsoluteRecomposedCoefficient() > 0);
    }

    @Test
    void testSetSourceDataWith3dArray() {
        double[][][] testData = {
                {{0.1, 0.2}, {0.3, 0.4}}, // Channel 0 (e.g., Luminance)
                {{0.5, 0.6}, {0.7, 0.8}}, // Channel 1 (e.g., Red)
                {{0.9, 1.0}, {1.1, 1.2}}, // Channel 2 (e.g., Green)
                {{1.3, 1.4}, {1.5, 1.6}}  // Channel 3 (e.g., Blue)
        };
        model.setSourceData(testData);

        // Verify source data is correctly set
        assertNotNull(model.sourceCoefficientsArray);
        assertEquals(4, model.sourceCoefficientsArray.length); // 4 channels should be set
        assertArrayEquals(testData[1][0], model.sourceCoefficientsArray[1][0], 0.001); // Verify a part of the Red channel data

        // Verify transformed coefficient arrays are also updated
        assertNotNull(model.scalingCoefficientsArray[1]);
        assertNotNull(model.horizontalWaveletCoefficientsArray[1]);
        assertNotNull(model.recomposedCoefficientsArray[1]);
    }

    @Test
    void testDoSampleCoefficients() {
        model.doSampleCoefficients();
        // Verify sample coefficient data is loaded (specific values depend on WaveletData)
        assertNotNull(model.sourceCoefficientsArray);
        assertEquals(1, model.sourceCoefficientsArray.length); // Sample coefficients are loaded as grayscale, so one channel
        assertEquals(64, model.sourceCoefficientsArray[0].length); // Width is 64
        assertEquals(64, model.sourceCoefficientsArray[0][0].length); // Height is 64
    }

    @Test
    void testDoEarth() {
        model.doEarth();
        // Verify Earth image is loaded
        assertNotNull(model.sourceCoefficientsArray);
        assertEquals(4, model.sourceCoefficientsArray.length); // 4 channels for LRBG matrix
        // Verify image size (may be resized by ImageUtility.readImageFromResource or dataInput() logic)
        int width = model.sourceCoefficientsArray[0].length;
        int height = model.sourceCoefficientsArray[0][0].length;
        // Original Earth image size is 512x256. It might be adjusted to a power of two, e.g., 512x256 or larger.
        assertTrue(width >= 512 && width <= 1024);
        assertTrue(height >= 256 && height <= 1024);
    }

    @Test
    void testDoSmalltalkBalloon() {
        model.doSmalltalkBalloon();
        // Verify Smalltalk Balloon image is loaded
        assertNotNull(model.sourceCoefficientsArray);
        assertEquals(4, model.sourceCoefficientsArray.length); // 4 channels for LRBG matrix
        // Verify image size (originally 256x256, may be adjusted by nextPowerOfTwo)
        assertEquals(256, model.sourceCoefficientsArray[0].length);
        assertEquals(256, model.sourceCoefficientsArray[0][0].length);
    }

    @Test
    void testDoAllCoefficients() {
        // First, load sample data to ensure coefficients are set
        model.doSampleCoefficients();
        // Execute doAllCoefficients
        model.doAllCoefficients();

        // Verify interactive H/V/D are same as original H/V/D (check a few pixels)
        assertEquals(model.horizontalWaveletCoefficientsArray[0][10][10], model.interactiveHorizontalWaveletCoefficientsArray[0][10][10], 0.001);
        assertEquals(model.verticalWaveletCoefficientsArray[0][20][20], model.interactiveVerticalWaveletCoefficientsArray[0][20][20], 0.001);
        assertEquals(model.diagonalWaveletCoefficientsArray[0][30][30], model.interactiveDiagonalWaveletCoefficientsArray[0][30][30], 0.001);

        // Verify recomposed coefficients are updated
        assertNotNull(model.recomposedCoefficientsArray);
    }

    @Test
    void testDoClearCoefficients() {
        // First, load sample data to set coefficients
        model.doSampleCoefficients();
        // Execute doClearCoefficients
        model.doClearCoefficients();

        // Verify interactive H/V/D are all zero (check a few pixels)
        assertEquals(0.0, model.interactiveHorizontalWaveletCoefficientsArray[0][10][10], 0.001);
        assertEquals(0.0, model.interactiveVerticalWaveletCoefficientsArray[0][20][20], 0.001);
        assertEquals(0.0, model.interactiveDiagonalWaveletCoefficientsArray[0][30][30], 0.001);

        // Verify recomposed coefficients are updated
        assertNotNull(model.recomposedCoefficientsArray);
    }

    @Test
    void testFill() {
        double[][] matrix = new double[5][5];
        Wavelet2dModel.fill(matrix, 1.23);
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                assertEquals(1.23, matrix[i][j], 0.001, "Matrix element should be filled with 1.23");
            }
        }
    }

    @Test
    void testGenerateImage3dArrayGrayscale() {
        // Test data for grayscale image generation
        double[][][] valueMatrixArray = {{{0.5, 0.8}, {0.2, 0.1}}, {null}, {null}, {null}};
        double maxValue = 1.0; // Max value

        BufferedImage image = Wavelet2dModel.generateImage(valueMatrixArray, maxValue);
        assertNotNull(image);
        assertEquals(2, image.getWidth());
        assertEquals(2, image.getHeight());

        // Check pixel values (approximate)
        // (0,0) -> 0.5 * 255 = 127.5 -> 128 (rounded)
        assertEquals(128, new java.awt.Color(image.getRGB(0, 0)).getRed());
        // (1,0) -> 0.8 * 255 = 204
        assertEquals(204, new java.awt.Color(image.getRGB(1, 0)).getRed());
    }

    @Test
    void testGenerateImage3dArrayColor() {
        // Test data for color image generation
        double[][][] valueMatrixArray = {
                {{0.1, 0.2}, {0.3, 0.4}}, // Luminance (not used but formally required)
                {{0.5, 0.1}, {0.2, 0.3}}, // Red
                {{0.1, 0.5}, {0.3, 0.2}}, // Green
                {{0.3, 0.2}, {0.1, 0.5}}  // Blue
        };
        double maxValue = 1.0; // Max value

        BufferedImage image = Wavelet2dModel.generateImage(valueMatrixArray, maxValue);
        assertNotNull(image);
        assertEquals(2, image.getWidth());
        assertEquals(2, image.getHeight());

        // Check pixel values (approximate)
        // (0,0) R: 0.5*255=128, G: 0.1*255=26, B: 0.3*255=77
        Color pixel00 = new Color(image.getRGB(0, 0));
        assertEquals(128, pixel00.getRed());
        assertEquals(26, pixel00.getGreen());
        assertEquals(77, pixel00.getBlue());

        // (1,1) R: 0.3*255=77, G: 0.2*255=51, B: 0.5*255=128
        Color pixel11 = new Color(image.getRGB(1, 1));
        assertEquals(77, pixel11.getRed());
        assertEquals(51, pixel11.getGreen());
        assertEquals(128, pixel11.getBlue());
    }

    @Test
    void testGenerateImage2dArray() {
        double[][] valueMatrix = {{0.5, 0.8}, {0.2, 0.1}};
        Point scaleFactor = new Point(2, 2); // Scale by 2x2
        int rgbFlag = Constants.Red; // Red channel

        BufferedImage image = Wavelet2dModel.generateImage(valueMatrix, scaleFactor, rgbFlag);
        assertNotNull(image);
        assertEquals(4, image.getWidth()); // 2 * 2 = 4
        assertEquals(4, image.getHeight()); // 2 * 2 = 4

        // Check pixel values (only red component here)
        // (0,0) (after scaling) -> 0.5 * 255 = 128
        Color pixel00 = new Color(image.getRGB(0, 0));
        assertEquals(128, pixel00.getRed());
        assertEquals(0, pixel00.getGreen());
        assertEquals(0, pixel00.getBlue());

        // (2,0) (after scaling) -> data from (0,1) 0.8 * 255 = 204
        Color pixel20 = new Color(image.getRGB(2, 0));
        assertEquals(204, pixel20.getRed());
    }

    @Test
    void testGenerateImageCombined() {
        // Create dummy BufferedImages
        BufferedImage img1 = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        BufferedImage img2 = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        BufferedImage img3 = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        BufferedImage img4 = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);

        BufferedImage combinedImage = Wavelet2dModel.generateImage(img1, img2, img3, img4);
        assertNotNull(combinedImage);
        assertEquals(20, combinedImage.getWidth()); // 10 + 10
        assertEquals(20, combinedImage.getHeight()); // 10 + 10

        // Simple dimension check as content checking is complex here
    }

    @Test
    void testImageEarth() {
        BufferedImage earthImage = Wavelet2dModel.imageEarth();
        assertNotNull(earthImage);
        // Original Earth image size is 512x256.
        // However, Wavelet2dModel.imageEarth() calls FileUtility.readImageFromResource,
        // and dataInput() might apply resizing logic, making exact size checks difficult.
        // Here, we only verify successful loading and reasonable dimensions.
        assertTrue(earthImage.getWidth() > 0);
        assertTrue(earthImage.getHeight() > 0);
    }

    @Test
    void testImageSmalltalkBalloon() {
        BufferedImage balloonImage = Wavelet2dModel.imageSmalltalkBalloon();
        assertNotNull(balloonImage);
        // Original SmalltalkBalloon size is 256x256
        assertEquals(256, balloonImage.getWidth());
        assertEquals(256, balloonImage.getHeight());
    }

    @Test
    void testLrgbMatrixes() {
        // Create a dummy BufferedImage (2x2)
        BufferedImage testImage = new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB);
        // Set pixels: (0,0) Red, (1,0) Green, (0,1) Blue, (1,1) White
        testImage.setRGB(0, 0, new Color(255, 0, 0).getRGB());   // Red
        testImage.setRGB(1, 0, new Color(0, 255, 0).getRGB());   // Green
        testImage.setRGB(0, 1, new Color(0, 0, 255).getRGB());   // Blue
        testImage.setRGB(1, 1, new Color(255, 255, 255).getRGB()); // White

        double[][][] lrgb = Wavelet2dModel.lrgbMatrixes(testImage);

        assertNotNull(lrgb);
        assertEquals(4, lrgb.length); // 4 channels: Luminance, R, G, B
        assertEquals(2, lrgb[0].length); // Width
        assertEquals(2, lrgb[0][0].length); // Height

        // Check pixel values for each channel (depends on ColorUtility conversion logic)
        // (0,0) Red: R=1.0, G=0.0, B=0.0, Luminance = 0.299
        assertEquals(0.299, lrgb[0][0][0], 0.001); // Luminance
        assertEquals(1.0, lrgb[1][0][0], 0.001);   // Red
        assertEquals(0.0, lrgb[2][0][0], 0.001);   // Green
        assertEquals(0.0, lrgb[3][0][0], 0.001);   // Blue

        // (1,1) White: R=1.0, G=1.0, B=1.0, Luminance = 1.0
        assertEquals(1.0, lrgb[0][1][1], 0.001);
        assertEquals(1.0, lrgb[1][1][1], 0.001);
        assertEquals(1.0, lrgb[2][1][1], 0.001);
        assertEquals(1.0, lrgb[3][1][1], 0.001);
    }

    @Test
    void testMaximumAbsoluteCoefficients() {
        double[][][] testData = {
                {{0.1, -0.5}, {0.8, -0.2}},
                {{0.3, 0.9}, {-0.7, 0.4}},
                {{-1.0, 0.6}, {0.5, -0.1}},
                {{0.2, -0.3}, {0.9, 0.7}}
        };
        model.setSourceData(testData); // Set coefficients to trigger calculation

        // Verify each maximum absolute value is correctly calculated
        assertEquals(1.0, model.maximumAbsoluteSourceCoefficient(), 0.001); // Absolute value of -1.0
        // Max values for scaling and wavelet coefficients depend on DiscreteWavelet2dTransformation's logic
        // Just verify they are greater than zero.
        assertTrue(model.maximumAbsoluteScalingCoefficient() > 0);
        assertTrue(model.maximumAbsoluteWaveletCoefficient() > 0);
        assertTrue(model.maximumAbsoluteRecomposedCoefficient() > 0);

        // Verify caching works by calling again
        double initialMax = model.maximumAbsoluteSourceCoefficient();
        model.sourceCoefficientsArray[0][0][0] = 100.0; // Change value
        assertEquals(initialMax, model.maximumAbsoluteSourceCoefficient(), 0.001, "Cached value should be returned.");
    }

    // computeFromPoint and computeRecomposedCoefficients are closely related, so they are tested together.
    @Test
    void testComputeFromPointAndRecomposition() {
        model.doSmalltalkBalloon(); // Load image and initialize coefficients

        // Verify interactive coefficients are all cleared (initial state is cleared)
        assertEquals(0.0, model.interactiveHorizontalWaveletCoefficientsArray[0][0][0], 0.001);

        // Call computeFromPoint with a specific point (copy coefficients without Alt key)
        Point testPoint = new Point(10, 10);
        // Call with isAltDown=false
        model.computeFromPoint(testPoint, false);

        // Verify interactive coefficients around the specified point are copied from original coefficients
        // (Exact range depends on the code, but check the center point)
        assertTrue(model.interactiveHorizontalWaveletCoefficientsArray[0][10][10] != 0.0, "Coefficient at (10,10) should be copied.");
        assertEquals(model.horizontalWaveletCoefficientsArray[0][10][10], model.interactiveHorizontalWaveletCoefficientsArray[0][10][10], 0.001);

        // computeFromPoint calls computeRecomposedCoefficients(), so the recomposed image should be updated
        // Verify recomposed coefficient array is not null
        assertNotNull(model.recomposedCoefficientsArray[0]);

        // Verify it clears when called with Alt key pressed
        model.computeFromPoint(testPoint, true); // Call with Alt key

        // Verify interactive coefficients around the specified point are cleared
        assertEquals(0.0, model.interactiveHorizontalWaveletCoefficientsArray[0][10][10], 0.001, "Coefficient at (10,10) should be cleared.");
    }

    // GUI-related methods are complex to test.
    // For these, we just verify that the method executes without throwing exceptions or that it changes a specific internal state.
    @Test
    void testOpen() {
        // Opening GUI typically requires Swing's event dispatch thread in a testing environment.
        // Primarily, verify that the method completes without throwing exceptions.
        // Actual UI display and interaction are difficult to automate.
        assertDoesNotThrow(() -> model.open());
    }
}
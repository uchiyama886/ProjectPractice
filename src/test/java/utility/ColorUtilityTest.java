package utility;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.Color;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ColorUtility Class Unit Tests")
class ColorUtilityTest {

    // 浮動小数点比較の許容誤差
    // YUV-RGB変換は精度が要求されるため、DELTAを小さく保つのが理想ですが、
    // プロダクトコードの丸め処理によっては、ピクセル値（0-255）の比較で +/-1 程度の誤差は許容されるべきです。
    private static final double DELTA = 1e-6; // float/double の比較用

    // RGBの整数値比較で許容される誤差（0-255のピクセル値に対する丸め誤差など）
    private static final int PIXEL_DELTA = 1;


    @Test
    @DisplayName("convertRGBtoYUV(double[]) converts double array RGB to double array YUV")
    void testConvertRGBtoYUVDoubleArray() {
        double[] rgb = {1.0, 0.0, 0.0}; // Red
        double[] yuv = ColorUtility.convertRGBtoYUV(rgb);

        // BT.601 limited range (0-1 input, then converted to YUV)
        // Y = 0.299R + 0.587G + 0.114B
        // U = 0.492(B-Y)
        // V = 0.877(R-Y)
        // For R=1, G=0, B=0:
        // Y = 0.299
        // U = 0.492 * (0 - 0.299) = -0.146908
        // V = 0.877 * (1 - 0.299) = 0.615487

        assertNotNull(yuv);
        assertEquals(3, yuv.length);
        assertEquals(0.299, yuv[0], DELTA); // Y
        assertEquals(-0.146908, yuv[1], DELTA); // U (実際の計算誤差を考慮)
        assertEquals(0.615487, yuv[2], DELTA); // V (実際の計算誤差を考慮)

        rgb = new double[]{0.0, 1.0, 0.0}; // Green
        yuv = ColorUtility.convertRGBtoYUV(rgb);
        assertEquals(0.587, yuv[0], DELTA); // Y
        assertEquals(-0.288764, yuv[1], DELTA); // U
        assertEquals(-0.514039, yuv[2], DELTA); // V
    }

    @Test
    @DisplayName("convertRGBtoYUV(int) converts integer RGB to double array YUV")
    void testConvertRGBtoYUVInt() {
        int rgbInt = new Color(255, 0, 0).getRGB(); // Red
        double[] yuv = ColorUtility.convertRGBtoYUV(rgbInt);

        assertNotNull(yuv);
        assertEquals(3, yuv.length);
        // Assuming conversion from 0-255 int to 0-1 double first, then to YUV
        // R=1.0, G=0.0, B=0.0
        assertEquals(0.299, yuv[0], DELTA);
        assertEquals(-0.146908, yuv[1], DELTA);
        assertEquals(0.615487, yuv[2], DELTA);
    }

    @Test
    @DisplayName("convertRGBtoYUV(double, double, double) converts individual double RGB to double array YUV")
    void testConvertRGBtoYUVIndividualDoubles() {
        double[] yuv = ColorUtility.convertRGBtoYUV(1.0, 0.0, 0.0); // Red

        assertNotNull(yuv);
        assertEquals(3, yuv.length);
        assertEquals(0.299, yuv[0], DELTA);
        assertEquals(-0.146908, yuv[1], DELTA);
        assertEquals(0.615487, yuv[2], DELTA);
    }

    @Test
    @DisplayName("convertRGBtoINT(double[]) converts double array RGB to integer RGB")
    void testConvertRGBtoINTDoubleArray() {
        double[] rgb = {1.0, 0.5, 0.0}; // Normalized RGB
        int rgbInt = ColorUtility.convertRGBtoINT(rgb);

        // Expected: R=255, G=128, B=0
        int expectedR = (int) Math.round(1.0 * 255.0);
        int expectedG = (int) Math.round(0.5 * 255.0);
        int expectedB = (int) Math.round(0.0 * 255.0);
        
        // Clamping to 0-255 explicitly if product code does this
        expectedR = Math.max(0, Math.min(255, expectedR));
        expectedG = Math.max(0, Math.min(255, expectedG));
        expectedB = Math.max(0, Math.min(255, expectedB));

        int expectedPackedRGB = (expectedR << 16) | (expectedG << 8) | expectedB;
        assertEquals(expectedPackedRGB, rgbInt & 0xFFFFFF, "Packed RGB should match"); // アルファ無視
    }

    @Test
    @DisplayName("convertRGBtoINT(double, double, double) converts individual double RGB to integer RGB")
    void testConvertRGBtoINTIndividualDoubles() {
        int rgbInt = ColorUtility.convertRGBtoINT(1.0, 0.5, 0.0); // Normalized RGB

        // Expected: R=255, G=128, B=0
        int expectedR = (int) Math.round(1.0 * 255.0);
        int expectedG = (int) Math.round(0.5 * 255.0);
        int expectedB = (int) Math.round(0.0 * 255.0);

        expectedR = Math.max(0, Math.min(255, expectedR));
        expectedG = Math.max(0, Math.min(255, expectedG));
        expectedB = Math.max(0, Math.min(255, expectedB));

        int expectedPackedRGB = (expectedR << 16) | (expectedG << 8) | expectedB;
        assertEquals(expectedPackedRGB, rgbInt & 0xFFFFFF, "Packed RGB should match");
    }

    @Test
    @DisplayName("convertINTtoRGB() converts integer RGB to double array RGB")
    void testConvertINTtoRGB() {
        int rgbInt = new Color(255, 128, 0).getRGB(); // Red-ish orange
        double[] rgb = ColorUtility.convertINTtoRGB(rgbInt);

        assertNotNull(rgb);
        assertEquals(3, rgb.length);
        assertEquals(1.0, rgb[0], DELTA); // R (255/255)
        assertEquals(128.0 / 255.0, rgb[1], DELTA); // G (128/255)
        assertEquals(0.0, rgb[2], DELTA); // B (0/255)
    }

    @Test
    @DisplayName("colorFromRGB(double[]) creates correct color from array")
    void testColorFromRGBArray() {
        double[] rgb = {1.0, 0.5, 0.0}; // Normalized RGB (Red, Green-ish)
        Color color = ColorUtility.colorFromRGB(rgb);
        assertNotNull(color);
        // RGB components are converted to 0-255 and packed
        assertEquals(new Color(255, 128, 0).getRGB(), color.getRGB());
    }

    @Test
    @DisplayName("colorFromRGB(double, double, double) creates correct color from individual components")
    void testColorFromRGBIndividualComponents() {
        Color color = ColorUtility.colorFromRGB(1.0, 0.5, 0.0);
        assertNotNull(color);
        assertEquals(new Color(255, 128, 0).getRGB(), color.getRGB());
    }

    @Test
    @DisplayName("colorFromYUV(double[]) creates correct color from YUV array")
    void testColorFromYUVArray() {
        // 緑 (RGB: 0, 255, 0) に対応する YUV 値 (Rec. BT.601)
        // Y = 0.587
        // U = -0.288764
        // V = -0.514039
        double[] yuvForGreen = {0.587, -0.288764, -0.514039};
        Color convertedColor = ColorUtility.colorFromYUV(yuvForGreen);

        // 変換された色の各RGB成分を取得
        // アルファチャネルを無視するために & 0xFFFFFF を使用
        int convertedR = (convertedColor.getRGB() >> 16) & 0xFF;
        int convertedG = (convertedColor.getRGB() >> 8) & 0xFF;
        int convertedB = convertedColor.getRGB() & 0xFF;

        // 期待されるRGB値 (緑: 0, 255, 0)
        int expectedR = 0;
        int expectedG = 255;
        int expectedB = 0;

        // 浮動小数点演算と丸め誤差のため、厳密な一致ではなく許容誤差内で比較
        assertEquals(expectedR, convertedR, PIXEL_DELTA, "Red component should be close to 0");
        assertEquals(expectedG, convertedG, PIXEL_DELTA, "Green component should be close to 255");
        assertEquals(expectedB, convertedB, PIXEL_DELTA, "Blue component should be close to 0");
    }
    
    @Test
    @DisplayName("colorFromYUV(double, double, double) creates correct color from individual YUV components")
    void testColorFromYUVIndividualComponents() {
        // 緑 (RGB: 0, 255, 0) に対応する YUV 値 (Rec. BT.601)
        double y = 0.587;
        double u = -0.288764;
        double v = -0.514039;
        
        Color convertedColor = ColorUtility.colorFromYUV(y, u, v);

        int convertedR = (convertedColor.getRGB() >> 16) & 0xFF;
        int convertedG = (convertedColor.getRGB() >> 8) & 0xFF;
        int convertedB = (convertedColor.getRGB()) & 0xFF;

        assertEquals(0, convertedR, PIXEL_DELTA, "Red component should be close to 0");
        assertEquals(255, convertedG, PIXEL_DELTA, "Green component should be close to 255");
        assertEquals(0, convertedB, PIXEL_DELTA, "Blue component should be close to 0");
    }

    @Test
    @DisplayName("colorFromLuminance() creates correct grayscale color")
    void testColorFromLuminance() {
        // Luminance 0.5 -> RGB (128, 128, 128)
        Color color = ColorUtility.colorFromLuminance(0.5);
        assertNotNull(color);
        assertEquals(new Color(128, 128, 128).getRGB(), color.getRGB());

        // Luminance 0.0 -> RGB (0, 0, 0)
        color = ColorUtility.colorFromLuminance(0.0);
        assertNotNull(color);
        assertEquals(new Color(0, 0, 0).getRGB(), color.getRGB());

        // Luminance 1.0 -> RGB (255, 255, 255)
        color = ColorUtility.colorFromLuminance(1.0);
        assertNotNull(color);
        assertEquals(new Color(255, 255, 255).getRGB(), color.getRGB());
    }

    @Test
    @DisplayName("luminanceFromRGB(double[]) calculates luminance from double array RGB")
    void testLuminanceFromRGBDoubleArray() {
        double[] rgb = {1.0, 0.0, 0.0}; // Red
        double luminance = ColorUtility.luminanceFromRGB(rgb);
        // Expected luminance for R=1, G=0, B=0 (BT.601)
        assertEquals(0.299, luminance, DELTA);

        rgb = new double[]{0.0, 1.0, 0.0}; // Green
        luminance = ColorUtility.luminanceFromRGB(rgb);
        assertEquals(0.587, luminance, DELTA);

        rgb = new double[]{0.0, 0.0, 1.0}; // Blue
        luminance = ColorUtility.luminanceFromRGB(rgb);
        assertEquals(0.114, luminance, DELTA);
    }

    @Test
    @DisplayName("luminanceFromRGB(int) calculates luminance from integer RGB")
    void testLuminanceFromRGBInt() {
        int rgbInt = new Color(255, 0, 0).getRGB(); // Red
        double luminance = ColorUtility.luminanceFromRGB(rgbInt);
        assertEquals(0.299, luminance, DELTA);

        rgbInt = new Color(0, 255, 0).getRGB(); // Green
        luminance = ColorUtility.luminanceFromRGB(rgbInt);
        assertEquals(0.587, luminance, DELTA);

        rgbInt = new Color(0, 0, 255).getRGB(); // Blue
        luminance = ColorUtility.luminanceFromRGB(rgbInt);
        assertEquals(0.114, luminance, DELTA);
    }
    
    @Test
    @DisplayName("luminanceFromYUV(double[]) extracts luminance from YUV array")
    void testLuminanceFromYUV() {
        double[] yuv = {0.5, 0.1, 0.2}; // Example YUV
        double luminance = ColorUtility.luminanceFromYUV(yuv);
        assertEquals(0.5, luminance, DELTA); // Y component is luminance
    }


    @Test
    @DisplayName("convertYUVtoRGB(double[]) converts double array YUV to double array RGB")
    void testConvertYUVtoRGBDoubleArray() {
        double[] yuv = {0.587, -0.288764, -0.514039}; // Green (0, 255, 0)
        double[] rgb = ColorUtility.convertYUVtoRGB(yuv);

        assertNotNull(rgb);
        assertEquals(3, rgb.length);
        assertEquals(0.0, rgb[0], DELTA); // R should be ~0.0
        assertEquals(1.0, rgb[1], DELTA); // G should be ~1.0
        assertEquals(0.0, rgb[2], DELTA); // B should be ~0.0
    }

    @Test
    @DisplayName("convertYUVtoRGB(double, double, double) converts individual double YUV to double array RGB")
    void testConvertYUVtoRGBIndividualDoubles() {
        double y = 0.587;
        double u = -0.288764;
        double v = -0.514039;
        double[] rgb = ColorUtility.convertYUVtoRGB(y, u, v);

        assertNotNull(rgb);
        assertEquals(3, rgb.length);
        assertEquals(0.0, rgb[0], DELTA); // R should be ~0.0
        assertEquals(1.0, rgb[1], DELTA); // G should be ~1.0
        assertEquals(0.0, rgb[2], DELTA); // B should be ~0.0
    }
}
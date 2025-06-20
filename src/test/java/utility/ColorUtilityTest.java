package utility;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.Color;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ColorUtility Class Unit Tests")
class ColorUtilityTest {

    private static final double DELTA = 1e-3; // 浮動小数点比較のための許容誤差

    @Test
    @DisplayName("colorFromLuminance() creates correct grayscale color")
    void testColorFromLuminance() {
        Color color = ColorUtility.colorFromLuminance(0.5); // luminance = 0.5 (127.5 scaled to 255)
        // 0.5 * 255 = 127.5, 四捨五入で128
        // ColorUtility.colorFromLuminance は convertRGBtoINT を呼び出し、convertRGBtoINT はアルファを付けない
        // そのため、期待値も ColorUtility.convertRGBtoINT が返す形式に合わせる (0xFFFFFF をマスクしてアルファを無視)
        int expectedRGB = (128 << 16) | (128 << 8) | 128;
        assertEquals(expectedRGB, color.getRGB() & 0xFFFFFF, "Grayscale color should be correct (alpha ignored)");

        color = ColorUtility.colorFromLuminance(0.0);
        expectedRGB = (0 << 16) | (0 << 8) | 0;
        assertEquals(expectedRGB, color.getRGB() & 0xFFFFFF, "Black color should be correct for luminance 0.0 (alpha ignored)");

        color = ColorUtility.colorFromLuminance(1.0);
        expectedRGB = (255 << 16) | (255 << 8) | 255;
        assertEquals(expectedRGB, color.getRGB() & 0xFFFFFF, "White color should be correct for luminance 1.0 (alpha ignored)");
    }

    @Test
    @DisplayName("colorFromRGB(double[]) creates correct color from array")
    void testColorFromRGBArray() {
        double[] rgb = {1.0, 0.5, 0.0}; // R=255, G=127.5, B=0 -> R=255, G=128, B=0
        Color color = ColorUtility.colorFromRGB(rgb);
        int expectedR = 255;
        int expectedG = 128; // Math.round(0.5 * 255) = 128
        int expectedB = 0;
        int expectedRGB = (expectedR << 16) | (expectedG << 8) | expectedB;
        assertEquals(expectedRGB, color.getRGB() & 0xFFFFFF, "Color from RGB array should be correct (alpha ignored)");
    }

    @Test
    @DisplayName("colorFromRGB(double, double, double) creates correct color from individual components")
    void testColorFromRGBIndividual() {
        double r = 0.0, g = 1.0, b = 0.5; // R=0, G=255, B=127.5 -> R=0, G=255, B=128
        Color color = ColorUtility.colorFromRGB(r, g, b);
        int expectedR = 0;
        int expectedG = 255;
        int expectedB = 128; // Math.round(0.5 * 255) = 128
        int expectedRGB = (expectedR << 16) | (expectedG << 8) | expectedB;
        assertEquals(expectedRGB, color.getRGB() & 0xFFFFFF, "Color from individual RGB should be correct (alpha ignored)");
    }

    @Test
    @DisplayName("colorFromYUV(double[]) creates correct color from YUV array")
    void testColorFromYUVArray() {
        // 白のYUV (Y=1.0, U=0.0, V=0.0) -> RGB (1.0, 1.0, 1.0)
        double[] yuvWhite = {1.0, 0.0, 0.0};
        Color color = ColorUtility.colorFromYUV(yuvWhite);
        int expectedRGBWhite = (255 << 16) | (255 << 8) | 255;
        assertEquals(expectedRGBWhite, color.getRGB() & 0xFFFFFF, "Color from YUV array (White) should be correct (alpha ignored)");

        // 黒のYUV (Y=0.0, U=0.0, V=0.0) -> RGB (0.0, 0.0, 0.0)
        double[] yuvBlack = {0.0, 0.0, 0.0};
        color = ColorUtility.colorFromYUV(yuvBlack);
        int expectedRGBBlack = (0 << 16) | (0 << 8) | 0;
        assertEquals(expectedRGBBlack, color.getRGB() & 0xFFFFFF, "Color from YUV array (Black) should be correct (alpha ignored)");

        // 緑のYUV (Y=0.587, U=-0.331, V=-0.419) -> RGB (0.0, 1.0, 0.0) に近い
        // ColorUtility.convertYUVtoRGB(0.587, -0.331, -0.419) の結果が R:約0.000, G:約1.000, B:約0.000 になるため、それを丸める
        int expectedR = (int)Math.round(0.000 * 255.0); // ~0
        int expectedG = (int)Math.round(1.000 * 255.0); // ~255
        int expectedB = (int)Math.round(0.000 * 255.0); // ~0
        // 正しい期待値は 0x00FF00 (65280)
        int expectedRGBGreen = (expectedR << 16) | (expectedG << 8) | expectedB;
        color = ColorUtility.colorFromYUV(0.587, -0.331, -0.419);
        assertEquals(expectedRGBGreen, color.getRGB() & 0xFFFFFF, "Color from YUV array (Green) should be correct within tolerance (alpha ignored)"); // 修正: 期待値を 65280 に
    }

    @Test
    @DisplayName("colorFromYUV(double, double, double) creates correct color from individual YUV components")
    void testColorFromYUVIndividual() {
        // 赤のYUV (Y=0.299, U=-0.169, V=0.500) -> RGB (1.0, 0.0, 0.0) に近い
        // ColorUtility.convertYUVtoRGB(0.299, -0.169, 0.500) の結果が R:約0.999, G:約0.000, B:約0.001 になるため、それを丸める
        int expectedR = (int) Math.round(0.999 * 255.0); // ~255
        int expectedG = (int) Math.round(0.000 * 255.0); // ~0
        int expectedB = (int) Math.round(0.001 * 255.0); // ~0
        int expectedRGBRed = (expectedR << 16) | (expectedG << 8) | expectedB;
        Color color = ColorUtility.colorFromYUV(0.299, -0.169, 0.500);
        assertEquals(expectedRGBRed, color.getRGB() & 0xFFFFFF, "Color from individual YUV (Red) should be correct within tolerance (alpha ignored)");
    }

    @Test
    @DisplayName("convertINTtoRGB() converts integer RGB to double array RGB")
    void testConvertINTtoRGB() {
        int argb = 0xFF008040; // Alpha F, R 0, G 128, B 64
        double[] rgb = ColorUtility.convertINTtoRGB(argb);
        assertEquals(0.0, rgb[0], DELTA, "Red component should be 0.0");
        assertEquals(128.0 / 255.0, rgb[1], DELTA, "Green component should be 128/255");
        assertEquals(64.0 / 255.0, rgb[2], DELTA, "Blue component should be 64/255");

        argb = 0xFFFFFFFF; // White
        rgb = ColorUtility.convertINTtoRGB(argb);
        assertEquals(1.0, rgb[0], DELTA, "Red component should be 1.0 for white");
        assertEquals(1.0, rgb[1], DELTA, "Green component should be 1.0 for white");
        assertEquals(1.0, rgb[2], DELTA, "Blue component should be 1.0 for white");
    }

    @Test
    @DisplayName("convertRGBtoINT(double[]) converts double array RGB to integer RGB")
    void testConvertRGBtoINTArray() {
        // R=0.0, G=1.0, B=0.5 -> R=0, G=255, B=128
        double[] rgb = {0.0, 1.0, 0.5};
        int expectedR = (int) Math.round(rgb[0] * 255.0);
        int expectedG = (int) Math.round(rgb[1] * 255.0);
        int expectedB = (int) Math.round(rgb[2] * 255.0);
        // ColorUtility.convertRGBtoINT はアルファを含まないため、期待値もアルファなしの形式で生成
        int expectedRGB = (expectedR << 16) | (expectedG << 8) | expectedB;
        assertEquals(expectedRGB, ColorUtility.convertRGBtoINT(rgb), "Integer RGB from double array should be correct");
    }

    @Test
    @DisplayName("convertRGBtoINT(double, double, double) converts individual double RGB to integer RGB")
    void testConvertRGBtoINTIndividual() {
        // RGB(0.5, 0.0, 1.0) -> R=128, G=0, B=255
        double r = 0.5, g = 0.0, b = 1.0;
        int expectedR = (int) Math.round(r * 255.0); // 128
        int expectedG = (int) Math.round(g * 255.0); // 0
        int expectedB = (int) Math.round(b * 255.0); // 255
        // ColorUtility.convertRGBtoINT はアルファを含まないため、期待値もアルファなしの形式で生成
        int expectedRGB = (expectedR << 16) | (expectedG << 8) | expectedB;
        assertEquals(expectedRGB, ColorUtility.convertRGBtoINT(r, g, b), "Integer RGB from individual doubles should be correct");

        // 境界値テスト
        assertEquals(0x000000, ColorUtility.convertRGBtoINT(0.0, 0.0, 0.0), "Black");
        assertEquals(0xFFFFFF, ColorUtility.convertRGBtoINT(1.0, 1.0, 1.0), "White");
    }

    @Test
    @DisplayName("convertRGBtoYUV(double[]) converts double array RGB to double array YUV")
    void testConvertRGBtoYUVArray() {
        double[] rgb = {1.0, 0.0, 0.0}; // Pure Red
        double[] yuv = ColorUtility.convertRGBtoYUV(rgb);
        assertEquals(0.299, yuv[0], DELTA, "Y component for Red should be 0.299");
        assertEquals(-0.169, yuv[1], DELTA, "U component for Red should be -0.169");
        assertEquals(0.500, yuv[2], DELTA, "V component for Red should be 0.500");
    }

    @Test
    @DisplayName("convertRGBtoYUV(double, double, double) converts individual double RGB to double array YUV")
    void testConvertRGBtoYUVIndividual() {
        double[] yuv = ColorUtility.convertRGBtoYUV(0.0, 1.0, 0.0); // Pure Green
        assertEquals(0.587, yuv[0], DELTA, "Y component for Green should be 0.587");
        assertEquals(-0.331, yuv[1], DELTA, "U component for Green should be -0.331");
        assertEquals(-0.419, yuv[2], DELTA, "V component for Green should be -0.419");
    }

    @Test
    @DisplayName("convertRGBtoYUV(int) converts integer RGB to double array YUV")
    void testConvertRGBtoYUVInt() {
        int argb = new Color(0, 0, 255).getRGB(); // Pure Blue
        double[] yuv = ColorUtility.convertRGBtoYUV(argb);
        assertEquals(0.114, yuv[0], DELTA, "Y component for Blue should be 0.114");
        assertEquals(0.500, yuv[1], DELTA, "U component for Blue should be 0.500");
        assertEquals(-0.081, yuv[2], DELTA, "V component for Blue should be -0.081");
    }

    @Test
    @DisplayName("convertYUVtoRGB(double[]) converts double array YUV to double array RGB")
    void testConvertYUVtoRGBArray() {
        double[] yuv = {1.0, 0.0, 0.0}; // Y=1.0 (White)
        double[] rgb = ColorUtility.convertYUVtoRGB(yuv);
        assertEquals(1.0, rgb[0], DELTA, "Red component for White should be 1.0");
        assertEquals(1.0, rgb[1], DELTA, "Green component for White should be 1.0");
        assertEquals(1.0, rgb[2], DELTA, "Blue component for White should be 1.0");

        double[] yuvBlack = {0.0, 0.0, 0.0}; // Y=0.0 (Black)
        double[] rgbBlack = ColorUtility.convertYUVtoRGB(yuvBlack);
        assertEquals(0.0, rgbBlack[0], DELTA, "Red component for Black should be 0.0");
        assertEquals(0.0, rgbBlack[1], DELTA, "Green component for Black should be 0.0");
        assertEquals(0.0, rgbBlack[2], DELTA, "Blue component for Black should be 0.0");
    }

    @Test
    @DisplayName("convertYUVtoRGB(double, double, double) converts individual double YUV to double array RGB")
    void testConvertYUVtoRGBIndividual() {
        double[] yuv = {0.0, 0.0, 0.0}; // Y=0.0 (Black)
        double[] rgb = ColorUtility.convertYUVtoRGB(yuv[0], yuv[1], yuv[2]);
        assertEquals(0.0, rgb[0], DELTA, "Red component for Black should be 0.0");
        assertEquals(0.0, rgb[1], DELTA, "Green component for Black should be 0.0");
        assertEquals(0.0, rgb[2], DELTA, "Blue component for Black should be 0.0");
    }

    @Test
    @DisplayName("luminanceFromRGB(double[]) calculates luminance from double array RGB")
    void testLuminanceFromRGBArray() {
        double[] rgb = {1.0, 1.0, 1.0}; // White
        assertEquals(1.0, ColorUtility.luminanceFromRGB(rgb), DELTA, "Luminance for White should be 1.0");

        rgb = new double[]{0.0, 0.0, 0.0}; // Black
        assertEquals(0.0, ColorUtility.luminanceFromRGB(rgb), DELTA, "Luminance for Black should be 0.0");

        rgb = new double[]{0.5, 0.5, 0.5}; // Gray
        assertEquals(0.5, ColorUtility.luminanceFromRGB(rgb), DELTA, "Luminance for Gray should be 0.5");
    }

    @Test
    @DisplayName("luminanceFromRGB(int) calculates luminance from integer RGB")
    void testLuminanceFromRGBInt() {
        int argb = new Color(255, 255, 255).getRGB(); // White
        assertEquals(1.0, ColorUtility.luminanceFromRGB(argb), DELTA, "Luminance for White (int) should be 1.0");

        argb = new Color(0, 0, 0).getRGB(); // Black
        assertEquals(0.0, ColorUtility.luminanceFromRGB(argb), DELTA, "Luminance for Black (int) should be 0.0");

        argb = new Color(128, 128, 128).getRGB(); // Gray (approx 0.5 luminance)
        assertEquals(0.5019607843137255, ColorUtility.luminanceFromRGB(argb), DELTA, "Luminance for Gray (int) should be correct");
    }

    @Test
    @DisplayName("luminanceFromYUV(double[]) extracts luminance from YUV array")
    void testLuminanceFromYUVArray() {
        double[] yuv = {0.7, 0.1, -0.2};
        assertEquals(0.7, ColorUtility.luminanceFromYUV(yuv), DELTA, "Luminance should be the first component of YUV array");
    }
}
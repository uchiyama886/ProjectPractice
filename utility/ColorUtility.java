package utility;

import java.awt.Color;

public class ColorUtility {
  public static Color colorFromLuminance(double paramDouble) {
    int i = convertRGBtoINT(paramDouble, paramDouble, paramDouble);
    return new Color(i);
  }
  
  public static Color colorFromRGB(double[] paramArrayOfdouble) {
    double d1 = paramArrayOfdouble[0];
    double d2 = paramArrayOfdouble[1];
    double d3 = paramArrayOfdouble[2];
    return colorFromRGB(d1, d2, d3);
  }
  
  public static Color colorFromRGB(double paramDouble1, double paramDouble2, double paramDouble3) {
    int i = convertRGBtoINT(paramDouble1, paramDouble2, paramDouble3);
    return new Color(i);
  }
  
  public static Color colorFromYUV(double[] paramArrayOfdouble) {
    double[] arrayOfDouble = convertYUVtoRGB(paramArrayOfdouble);
    return colorFromRGB(arrayOfDouble);
  }
  
  public static Color colorFromYUV(double paramDouble1, double paramDouble2, double paramDouble3) {
    double[] arrayOfDouble = { paramDouble1, paramDouble2, paramDouble3 };
    return colorFromYUV(arrayOfDouble);
  }
  
  public static double[] convertINTtoRGB(int paramInt) {
    double d1 = (paramInt >> 16 & 0xFF) / 255.0D;
    double d2 = (paramInt >> 8 & 0xFF) / 255.0D;
    double d3 = (paramInt & 0xFF) / 255.0D;
    return new double[] { d1, d2, d3 };
  }
  
  public static int convertRGBtoINT(double[] paramArrayOfdouble) {
    double d1 = paramArrayOfdouble[0];
    double d2 = paramArrayOfdouble[1];
    double d3 = paramArrayOfdouble[2];
    return convertRGBtoINT(d1, d2, d3);
  }
  
  public static int convertRGBtoINT(double paramDouble1, double paramDouble2, double paramDouble3) {
    int i = (int)Math.round(paramDouble1 * 255.0D);
    int j = (int)Math.round(paramDouble2 * 255.0D);
    int k = (int)Math.round(paramDouble3 * 255.0D);
    i = i << 16 & 0xFF0000;
    j = j << 8 & 0xFF00;
    k &= 0xFF;
    return i + j + k;
  }
  
  public static double[] convertRGBtoYUV(double[] paramArrayOfdouble) {
    double d1 = paramArrayOfdouble[0];
    double d2 = paramArrayOfdouble[1];
    double d3 = paramArrayOfdouble[2];
    return convertRGBtoYUV(d1, d2, d3);
  }
  
  public static double[] convertRGBtoYUV(double paramDouble1, double paramDouble2, double paramDouble3) {
    double d1 = 0.299D * paramDouble1 + 0.587D * paramDouble2 + 0.114D * paramDouble3;
    double d2 = -0.169D * paramDouble1 + -0.331D * paramDouble2 + 0.5D * paramDouble3;
    double d3 = 0.5D * paramDouble1 + -0.419D * paramDouble2 + -0.081D * paramDouble3;
    return new double[] { d1, d2, d3 };
  }
  
  public static double[] convertRGBtoYUV(int paramInt) {
    double[] arrayOfDouble = convertINTtoRGB(paramInt);
    return convertRGBtoYUV(arrayOfDouble);
  }
  
  public static double[] convertYUVtoRGB(double[] paramArrayOfdouble) {
    double d1 = paramArrayOfdouble[0];
    double d2 = paramArrayOfdouble[1];
    double d3 = paramArrayOfdouble[2];
    return convertYUVtoRGB(d1, d2, d3);
  }
  
  public static double[] convertYUVtoRGB(double paramDouble1, double paramDouble2, double paramDouble3) {
    double d1 = 1.0D * paramDouble1 + 1.402D * paramDouble3;
    double d2 = 1.0D * paramDouble1 + -0.344D * paramDouble2 + -0.714D * paramDouble3;
    double d3 = 1.0D * paramDouble1 + 1.772D * paramDouble2;
    return new double[] { d1, d2, d3 };
  }
  
  public static double luminanceFromRGB(double[] paramArrayOfdouble) {
    double[] arrayOfDouble = convertRGBtoYUV(paramArrayOfdouble);
    return luminanceFromYUV(arrayOfDouble);
  }
  
  public static double luminanceFromRGB(int paramInt) {
    double[] arrayOfDouble = convertRGBtoYUV(paramInt);
    return luminanceFromYUV(arrayOfDouble);
  }
  
  public static double luminanceFromYUV(double[] paramArrayOfdouble) {
    return paramArrayOfdouble[0];
  }
}



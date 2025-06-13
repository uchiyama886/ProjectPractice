package utility;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Point;

import wavelet.Wavelet2dModel;
import wavelet.Constants;

public class WaveletData {
	/**
	 * 離散ウェーブレット1次元変換のための元データ。
	 */
	public static double[] Sample1dCoefficients()
	{
		double[] anArray = new double[64];
		Arrays.fill(anArray, 0.0d);
		for (int i =  0; i < 16; i++) { anArray[i] = Math.pow((double)(i + 1), 2.0d) / 256.0d; }
		for (int i = 16; i < 32; i++) { anArray[i] = 0.2d; }
		for (int i = 32; i < 48; i++) { anArray[i] = Math.pow((double)(48 - (i + 1)), 2.0d) / 256.0d - 0.5d; }
		return anArray;
	}

	/**
	 * 離散ウェーブレット2次元変換のための元データ。
	 */
	public static double[][] Sample2dCoefficients()
	{
		int size = 64;
		double[][] aMatrix = new double[size][size];
		for (int index = 0; index < aMatrix.length; index++)
		{
			Arrays.fill(aMatrix[index], 0.2d);
		}
		for (int index = 5; index < size - 5; index++)
		{
			aMatrix[5][index] = 1.0d;
			aMatrix[size - 6][index] = 1.0d;
			aMatrix[index][5] = 1.0d;
			aMatrix[index][size - 6] = 1.0d;
			aMatrix[index][index] = 1.0d;
			aMatrix[index][size - index - 1] = 1.0d;
		}
		return aMatrix;
	}

	/**
	 * 離散ウェーブレット2次元変換のための元データ(Earth)。
	 */
	public static double[][][] dataEarth()
	{
		BufferedImage anImage = Wavelet2dModel.imageEarth();
		return Wavelet2dModel.lrgbMatrixes(anImage);
	}

	/**
	 * 離散ウェーブレット2次元変換のための元データ(SmalltalkBalloon)。
	 */
	public static double[][][] dataSmalltalkBalloon()
	{
		BufferedImage anImage = Wavelet2dModel.imageSmalltalkBalloon();
		return Wavelet2dModel.lrgbMatrixes(anImage);
	}

	/**
	 * 2次元配列の中を指定された値で初期化する。
	 */
	public static void fill(double[][] aMatrix, double aValue)
	{
        for (double[] anArray : aMatrix) {
            Arrays.fill(anArray, aValue);
        }
		return;
	}

	/**
	 * 離散ウェーブレット2次元変換のためのデータ値(valueMatrix)を画像に変換して応答する。
	 */
	public static BufferedImage generateImage(double[][][] valueMatrixArray, double maxValue)
	{
		double[][] valueMatrix = valueMatrixArray[0];
		int width = valueMatrix.length;
		int height = valueMatrix[0].length;
		BufferedImage anImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D aGraphics = anImage.createGraphics();

		if (valueMatrixArray[1] == null || valueMatrixArray[2] == null || valueMatrixArray[3] == null)
		{
			// Grayscale
			for (int y = 0; y < height; y++)
			{
				for (int x = 0; x < width; x++)
				{
					double aValue = Math.abs(valueMatrix[x][y]);
					int brightness = (int)Math.round((aValue / maxValue) * 255.0d);
					Color aColor = new Color(brightness, brightness, brightness);
					aGraphics.setColor(aColor);
					aGraphics.fillRect(x, y, 1, 1);
				}
			}
		}
		else
		{
			// Color
			int[][] redMatrix = new int[width][height];
			int[][] greenMatrix = new int[width][height];
			int[][] blueMatrix = new int[width][height];
			for (int y = 0; y < height; y++)
			{
				for (int x = 0; x < width; x++)
				{
					double redValue = Math.abs(valueMatrixArray[1][x][y]);
					int red = (int)Math.round((redValue / maxValue) * 255.0d);
					double greenValue = Math.abs(valueMatrixArray[2][x][y]);
					int green = (int)Math.round((greenValue / maxValue) * 255.0d);
					double blueValue = Math.abs(valueMatrixArray[3][x][y]);
					int blue = (int)Math.round((blueValue / maxValue) * 255.0d);
					Color aColor = new Color(red, green, blue);
					aGraphics.setColor(aColor);
					aGraphics.fillRect(x, y, 1, 1);
				}
			}
		}

		return anImage;
	}

	/**
	 * 離散ウェーブレット2次元変換のためのデータ値(valueMatrix)を画像に変換して応答する。
	 */
	public static BufferedImage generateImage(double[][] valueMatrix, Point scaleFactor, int rgbFlag)
	{
		int width = valueMatrix.length;
		int height = valueMatrix[0].length;
		int w = width * scaleFactor.x;
		int h = height * scaleFactor.y;
		BufferedImage anImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		Graphics2D aGraphics = anImage.createGraphics();

		double maxValue = Double.MIN_VALUE;
		for (int y = 0; y < height; y++)
		{
			for (int x = 0; x < width; x++)
			{
				double aValue = Math.abs(valueMatrix[x][y]);
				maxValue = Math.max(aValue, maxValue);
			}
		}
		for (int y = 0; y < height; y++)
		{
			for (int x = 0; x < width; x++)
			{
				double aValue = Math.abs(valueMatrix[x][y]);
				int luminance = (int)Math.round((aValue / maxValue) * 255.0d);
				Color aColor = new Color(luminance, luminance, luminance);
				if (rgbFlag == Constants.Red  ) { aColor = new Color(luminance, 0, 0); }
				if (rgbFlag == Constants.Green) { aColor = new Color(0, luminance, 0); }
				if (rgbFlag == Constants.Blue ) { aColor = new Color(0, 0, luminance); }
				aGraphics.setColor(aColor);
				aGraphics.fillRect(x * scaleFactor.x, y * scaleFactor.y, scaleFactor.x, scaleFactor.y);
			}
		}

		return anImage;
	}

	/**
	 * 離散ウェーブレット2次元変換のための元データ(Earth)。
	 */
	public static BufferedImage imageEarth()
	{
		String aString = "SampleImages/imageEarth512x256.jpg";
		BufferedImage anImage = ImageUtility.readImage(aString);
		return anImage;
	}

	/**
	 * 離散ウェーブレット2次元変換のための元データ(SmalltalkBalloon)。
	 */
	public static BufferedImage imageSmalltalkBalloon()
	{
		String aString = "SampleImages/imageSmalltalkBalloon256x256.jpg";
		BufferedImage anImage = ImageUtility.readImage(aString);
		return anImage;
	}

	/**
	 * 離散ウェーブレット2次元変換のための元データ(SmalltalkBalloon)。
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
				int aRGB = anImage.getRGB(x, y);
				luminanceMatrix[x][y] = ColorUtility.luminanceFromRGB(aRGB);
				double[] rgb = ColorUtility.convertINTtoRGB(aRGB);
				redMatrix[x][y] = rgb[0];
				greenMatrix[x][y] = rgb[1];
				blueMatrix[x][y] = rgb[2];
			}
		}
		return new double[][][] { luminanceMatrix, redMatrix, greenMatrix, blueMatrix };
	}
}
package utility;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import javax.imageio.ImageIO;


/**
 * 画像のユーティリティのクラス。
 */
public class ImageUtility extends Object
{
	/**
	 * 画像(anImage)を指定された幅(width)と高さ(height)に変形した複製を応答する。
	 * @param anImage 画像
	 * @param width 幅(横)
	 * @param height 高さ(縦)
	 * @return 画像
	 */
	public static BufferedImage adjustImage(BufferedImage anImage, Integer width, Integer height)
	{
		BufferedImage adjustedImage = new BufferedImage(width, height, anImage.getType());
		Graphics2D aGraphics = adjustedImage.createGraphics();
		aGraphics.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		aGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		aGraphics.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		aGraphics.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
		aGraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		aGraphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		aGraphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		aGraphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		aGraphics.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
		aGraphics.drawImage(anImage, 0, 0, width, height, null);
		return adjustedImage;
	}

	/**
	 * 画像(anImage)をグレースケール画像に変換した複製を応答する。
	 * @param anImage 画像(カラー)
	 * @return 画像(グレースケール)
	 */
	public static BufferedImage grayscaleImage(BufferedImage anImage)
	{
		Integer width = anImage.getWidth();
		Integer height = anImage.getHeight();
		BufferedImage grayscaleImage = new BufferedImage(anImage.getWidth(), anImage.getHeight(), anImage.getType());
		ValueHolder<Integer> y = new ValueHolder<>(0);
		new Condition(() -> y.get() < height).whileTrue(() ->
		{
			ValueHolder<Integer> x = new ValueHolder<>(0);
			new Condition(() ->  x.get() < width).whileTrue(() ->
			{
				Integer aRGB = anImage.getRGB(x.get(), y.get());
				Double luminance = ColorUtility.luminanceFromRGB(aRGB);
				aRGB = ColorUtility.convertRGBtoINT(luminance, luminance, luminance);
				grayscaleImage.setRGB(x.get(), y.get(), aRGB);
				x.setDo((Integer it) -> it + 1);
			});
			y.setDo((Integer it) -> it + 1);
		});
		return grayscaleImage;
	}

	/**
	 * 画像(anImage)をコピーして応答する。
	 * @param anImage 画像
	 * @return コピー画像
	 */
	public static BufferedImage copyImage(BufferedImage anImage)
	{
		BufferedImage copiedImage = new BufferedImage(anImage.getWidth(), anImage.getHeight(), anImage.getType());
		Graphics aGraphics = copiedImage.createGraphics();
		aGraphics.setColor(Color.white);
		aGraphics.fillRect(0, 0, copiedImage.getWidth(), copiedImage.getHeight());
		aGraphics.drawImage((Image)anImage, 0, 0, null);
		return copiedImage;
	}

	/**
	 * 画像を輝度マトリックスへ変換して応答する。
	 * @param anImage 画像
	 * @return 輝度（ルミナンス）の二次元配列
	 */
	public static double[][] convertImageToLuminanceMatrix(BufferedImage anImage)
	{
		Integer width = anImage.getWidth();
		Integer height = anImage.getHeight();
		double[][] aMatrix = new double[height][width];
		ValueHolder<Integer> y = new ValueHolder<>(0);
		new Condition(() -> y.get() < height).whileTrue(() -> 
		{
			ValueHolder<Integer> x = new ValueHolder<>(0);
			new Condition(() -> x.get() < width).whileTrue(() ->
			{
				Integer xValue = x.get();
				Integer yValue = y.get();
				Integer aRGB = anImage.getRGB(xValue, yValue);
				aMatrix[yValue][xValue] = ColorUtility.luminanceFromRGB(aRGB);
				x.setDo((Integer it) -> it + 1);
			});
			y.setDo((Integer it) -> it + 1);
		});
		return aMatrix;
	}

	/**
	 * 画像をYUVマトリックスへ変換して応答する。
	 * @param anImage 画像
	 * @return yの二次元配列とuの二次元配列とvの二次元配列の配列
	 */
	public static double[][][] convertImageToYUVMatrixes(BufferedImage anImage)
	{
		Integer width = anImage.getWidth();
		Integer height = anImage.getHeight();
		double[][] yMatrix = new double[height][width];
		double[][] uMatrix = new double[height][width];
		double[][] vMatrix = new double[height][width];
		ValueHolder<Integer> y = new ValueHolder<>(0);
		new Condition(() -> y.get() < height).whileTrue(() ->
		{
			ValueHolder<Integer> x = new ValueHolder<>(0);
			new Condition(() -> x.get() < width).whileTrue(() ->
			{
				Integer xValue = x.get();
				Integer yValue = y.get();
				Integer aRGB = anImage.getRGB(xValue, yValue);
				double[] yuv = ColorUtility.convertRGBtoYUV(aRGB);
				yMatrix[yValue][xValue] = yuv[0];
				uMatrix[yValue][xValue] = yuv[1];
				vMatrix[yValue][xValue] = yuv[2];
				x.setDo((Integer it) -> it + 1);
			});
			y.setDo((Integer it) -> it + 1);
		});
		return new double[][][] { yMatrix, uMatrix, vMatrix };
	}

	/**
	 * 輝度マトリックスを画像へ変換して応答する。
	 * @param aMatrix 輝度（ルミナンス）の二次元配列
	 * @return 画像
	 */
	public static BufferedImage convertLuminanceMatrixToImage(double[][] aMatrix)
	{
		Integer width = aMatrix[0].length;
		Integer height = aMatrix.length;
		BufferedImage anImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D aGraphics = anImage.createGraphics();
		aGraphics.setColor(Color.white);
		aGraphics.fillRect(0, 0, width, height);
		ValueHolder<Integer> y = new ValueHolder<>(0);
		new Condition(() -> y.get() < height).whileTrue(() ->
		{
			ValueHolder<Integer> x = new ValueHolder<>(0);
			new Condition(() -> x.get() < width).whileTrue(() ->
			{
				double luminance = aMatrix[y.get()][x.get()];
				double[] rgb = ColorUtility.convertYUVtoRGB(luminance, 0.0d, 0.0d);
				Integer aRGB = ColorUtility.convertRGBtoINT(rgb);
				anImage.setRGB(x.get(), y.get(), aRGB);
				x.setDo((Integer it) -> it + 1);
			});
			y.setDo((Integer it) -> it + 1);
		});
		return anImage;
	}

	/**
	 * YUVマトリックスを画像へ変換して応答する。
	 * @param yuvMatrixes yの二次元配列とuの二次元配列とvの二次元配列の配列
	 * @return 画像
	 */
	public static BufferedImage convertYUVMatrixesToImage(double[][][] yuvMatrixes)
	{
		double[][] yMatrix = yuvMatrixes[0];
		double[][] uMatrix = yuvMatrixes[1];
		double[][] vMatrix = yuvMatrixes[2];
		Integer width = yMatrix[0].length;
		Integer height = yMatrix.length;
		BufferedImage anImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D aGraphics = anImage.createGraphics();
		aGraphics.setColor(Color.white);
		aGraphics.fillRect(0, 0, width, height);
		ValueHolder<Integer> y = new ValueHolder<>(0);
		new Condition(() -> y.get() < height).whileTrue(() ->
		{
			ValueHolder<Integer> x = new ValueHolder<>(0);
			new Condition(() -> x.get() < width).whileTrue(() ->
			{
				double[] rgb = ColorUtility.convertYUVtoRGB(yMatrix[y.get()][x.get()],
			                                                uMatrix[y.get()][x.get()],
				                                            vMatrix[y.get()][x.get()]);
				Integer aRGB = ColorUtility.convertRGBtoINT(rgb);
				anImage.setRGB(x.get(), y.get(), aRGB);
				x.setDo((Integer it) -> it + 1);
			});
			y.setDo((Integer it) -> it + 1);
		});
		return anImage;
	}

	/**
	 * ファイル(aFile)から画像を読み込んで応答する。
	 * @param aFile ファイル（ファイルの拡張子のフォーマットが重要）
	 * @return 画像
	 */
	public static BufferedImage readImage(File aFile)
	{
		return ImageUtility.readImageFromFile(aFile);
	}

	/**
	 * ファイル名(aString)で指定されるファイルから画像を読み込んで応答する。
	 * @param aString ファイル名（ファイル名の拡張子のフォーマットが重要）
	 * @return 画像
	 */
	public static BufferedImage readImage(String aString)
	{
		return ImageUtility.readImageFromFile(aString);
	}

	/**
	 * ファイル(aFile)から画像を読み込んで応答する。
	 * @param aFile ファイル（ファイルの拡張子のフォーマットが重要）
	 * @return 画像
	 */
	public static BufferedImage readImageFromFile(File aFile)
	{
		BufferedImage anImage = null;
		try { anImage = ImageIO.read(aFile); }
		catch (IOException anException) { anException.printStackTrace(); }
		return anImage;
	}

	/**
	 * ファイル名(aString)で指定されるファイルから画像を読み込んで応答する。
	 * @param aString ファイル名（ファイル名の拡張子のフォーマットが重要）
	 * @return 画像
	 */
	public static BufferedImage readImageFromFile(String aString)
	{
		File aFile = new File(aString);
		return ImageUtility.readImageFromFile(aFile);
	}

	/**
	 * URL(aURL)から画像を読み込んで応答する。
	 * @param aURL ユニフォームリソースロケータ（最後の拡張子のフォーマットが重要）
	 * @return 画像
	 */
	public static BufferedImage readImageFromURL(URL aURL)
	{
		BufferedImage anImage = null;
		try { anImage = ImageIO.read(aURL); }
		catch (IOException anException) { anException.printStackTrace(); }
		return anImage;
	}

	/**
	 * URL(aString)で指定されるファイルから画像を読み込んで応答する。
	 * @param aString ファイル名（ファイル名の拡張子のフォーマットが重要）
	 * @return 画像
	 */
	public static BufferedImage readImageFromURL(String aString)
	{
		URL aURL = null;
		try 
		{
			URI aURI = new URI(aString);
            aURL = aURI.toURL();
		} catch (URISyntaxException | MalformedURLException anException) 
		{
			anException.printStackTrace(); 
			return null;
		}
	
		return ImageUtility.readImageFromURL(aURL);
	}

	/**
	 * 画像をファイル(aFile)へ拡張子で指定されたフォーマットで書き込む。
	 * @param anImage 画像
	 * @param aFile ファイル（ファイルの拡張子のフォーマットが重要）
	 */
	public static void writeImage(BufferedImage anImage, File aFile)
	{
		String aString = aFile.getName();
		aString = aString.substring(aString.lastIndexOf(".") + 1);
		try { ImageIO.write(anImage, aString, aFile); }
		catch (IOException anException) { anException.printStackTrace(); }
		return;
	}

	/**
	 * 画像をファイル名(aString)で指定されるファイルへ拡張子で指定されたフォーマットで書き込む。
	 * @param anImage 画像
	 * @param aString ファイル名（ファイル名の拡張子のフォーマットが重要）
	 */
	public static void writeImage(BufferedImage anImage, String aString)
	{
		File aFile = new File(aString);
		ImageUtility.writeImage(anImage, aFile);
		return;
	}
}

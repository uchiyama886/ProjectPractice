package wavelet;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.Arrays;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import utility.ColorUtility;
import utility.ImageUtility;

/**
 * 2次ウェーブレット変換のモデルクラス
 * 
 */
public class Wavelet2dModel extends WaveletModel {

	protected double maximumAbsoluteSourceCoefficient = Double.MIN_VALUE;
	protected double maximumAbsoluteScalingCoefficient = Double.MIN_VALUE;
	protected double maximumAbsoluteWaveletCoefficient = Double.MIN_VALUE;
	protected double maximumAbsoluteRecomposedCoefficient = Double.MIN_VALUE;

	protected double[][][] sourceCoefficientsArray;
	protected double[][][] scalingCoefficientsArray;
	protected double[][][] horizontalWaveletCoefficientsArray;
	protected double[][][] verticalWaveletCoefficientsArray;
	protected double[][][] diagonalWaveletCoefficientsArray;
	protected double[][][] interactiveHorizontalWaveletCoefficientsArray;
	protected double[][][] interactiveVerticalWaveletCoefficientsArray;
	protected double[][][] interactiveDiagonalWaveletCoefficientsArray;
	protected double[][][] recomposedCoefficientsArray;

	protected WaveletPaneModel sourceCoefficientsPaneModel = null;
	protected WaveletPaneModel scalingAndWaveletCoefficientsPaneModel = null;
	protected WaveletPaneModel interactiveScalingAndWaveletCoefficientsPaneModel = null;
	protected WaveletPaneModel recomposedCoefficientsPaneModel = null;

	/**
	 * 
	 */
	public Wavelet2dModel() {
		doSampleCoefficients();
	}

	/**
	 * 
	 */
	public void actionPerformed(ActionEvent anActionEvent) {
		String str = anActionEvent.getActionCommand();
		if (str == "sample coefficients") {
			doSampleCoefficients();
			return;
		} 
		if (str == "smalltalk balloon") {
			doSmalltalkBalloon();
			return;
		} 
		if (str == "earth") {
			doEarth();
			return;
		} 
		if (str == "all coefficients") {
			doAllCoefficients();
			return;
		} 
		if (str == "clear coefficients") {
			doClearCoefficients();
			return;
		} 
	}

	/**
	 * 
	 */
	public void computeFromPoint(Point aPoint, boolean isAltDown) {
		for (byte b = 0; b < this.sourceCoefficientsArray.length; b++) {
      		double[][] arrayOfDouble = this.sourceCoefficientsArray[b];
      		if (arrayOfDouble != null) {
				double[][] arrayOfDouble1 = this.scalingCoefficientsArray[b];
				int i = arrayOfDouble1.length;
				int j = (arrayOfDouble1[0]).length;
				int k = paramPoint.x % i;
				int m = paramPoint.y % j;
				double[][] arrayOfDouble2 = this.horizontalWaveletCoefficientsArray[b];
				double[][] arrayOfDouble3 = this.verticalWaveletCoefficientsArray[b];
				double[][] arrayOfDouble4 = this.diagonalWaveletCoefficientsArray[b];
				double[][] arrayOfDouble5 = this.interactiveHorizontalWaveletCoefficientsArray[b];
				double[][] arrayOfDouble6 = this.interactiveVerticalWaveletCoefficientsArray[b];
				double[][] arrayOfDouble7 = this.interactiveDiagonalWaveletCoefficientsArray[b];
				for (byte b1 = -2; b1 <= 2; b1++) {
					for (byte b2 = -2; b2 <= 2; b2++) {
						int n = k + b2;
						int i1 = m + b1;
						n = Math.min(Math.max(0, n), i - 1);
						i1 = Math.min(Math.max(0, i1), j - 1);
						if (paramBoolean) {
							arrayOfDouble5[n][i1] = 0.0D;
							arrayOfDouble6[n][i1] = 0.0D;
							arrayOfDouble7[n][i1] = 0.0D;
						} else {
							arrayOfDouble5[n][i1] = arrayOfDouble2[n][i1];
							arrayOfDouble6[n][i1] = arrayOfDouble3[n][i1];
							arrayOfDouble7[n][i1] = arrayOfDouble4[n][i1];
						} 
					} 
				} 
      		}
    	}
    computeRecomposedCoefficients();
	}

	/**
	 * 
	 */
	public void computeRecomposedCoefficients() {

	}

	/**
	 * 
	 */
	public static double[][] dataSampleCoefficients() {
		return null;
	}

	/**
	 * 
	 */
	public static double[][][] dataEarth() {
		return null;
	}

	/**
	 * 
	 */
	public static double[][][] dataSmalltalkBalloon() {
		return null;
	}

	/**
	 * 
	 */
	public void doAllCoefficients() {

	}

	/**
	 * 
	 */
	public void doClearCoefficients() {

	}

	/**
	 * 
	 */
	public void doEarth() {

	}

	/**
	 * 
	 */
	public void doSampleCoefficients() {

	}

	/**
	 * 
	 */
	public void doSmalltalkBalloon() {

	}

	/**
	 * 
	 */
	public static void fill(double[][] aMatrix, double Value) {

	}

	/**
	 * 
	 */
	public static BufferedImage generateImage(double[][][] valueMatrixArray, double maxValue) {
		return null;
	}

	/**
	 * 
	 */
	public static BufferedImage generateImage(double[][] valueMatrix, Point scaleFactor, int rgbFlag) {
		return null;
	}

	/**
	 * 
	 */
	public static BufferedImage generateImage(BufferedImage imageScalingCoefficients,
			BufferedImage imageHorizontalWaveletCoeffixcients, BufferedImage imageVerticalWaveletCoefficients,
			BufferedImage imageDiagonalWaveletCoefficients) {
		return null;
	}

	/**
	 * 
	 */
	public static BufferedImage imageEarth() {
		return null;
	}

	/**
	 * 
	 */
	public static BufferedImage imageSmalltalkBalloon() {
		return null;
	}

	/**
	 * 
	 */
	public static double[][][] lrgbMatrixes(BufferedImage anImage) {
		return null;
	}

	/**
	 * 
	 */
	public double maximumAbsoluteScalingCoefficient() {
		return 0;
	}

	/**
	 * 
	 */
	public double maximumAbsoluteSourceCoefficient() {
		return 0;
	}

	/**
	 * 
	 */
	public double maximumAbsoluteRecomposedCoefficient() {
		return 0;
	}

	/**
	 * 
	 */
	public double maximumAbsoluteWaveletCoefficient() {
		return 0;
	}

	/**
	 * 
	 */
	public void mouseClicked(Point aPoint, MouseEvent aMouseEvent) {

	}

	/**
	 * 
	 */
	public void mouseDragged(Point aPoint, MouseEvent aMouseEvent) {

	}

	/**
	 * 
	 */
	public void open() {

	}

	/**
	 * 
	 */
	public void setSourceData(double[][] sourceDataMatrix) {

	}

	/**
	 * 
	 */
	public void setSourceData(double[][][] sourceDataMatrixArray) {

	}

	/**
	 * 
	 */
	public void showPopupMenu(MouseEvent aMouseEvent, WaveletPaneController aController) {

	}

}

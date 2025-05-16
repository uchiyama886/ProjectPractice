package wavelet;

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

	public Wavelet2dModel() {

	}

	public void actionPerformed(ActionEvent anActionEvent) {

	}

	public void computeFromPoint(Point aPoint, boolean isAltDown) {

	}

	public void computeRecomposedCoefficients() {

	}

	public static double[][] dataSampleCoefficients() {
		return null;
	}

	public static double[][][] dataEarth() {
		return null;
	}

	public static double[][][] dataSmalltalkBalloon() {
		return null;
	}

	public void doAllCoefficients() {

	}

	public void doClearCoefficients() {

	}

	public void doEarth() {

	}

	public void doSampleCoefficients() {

	}

	public void doSmalltalkBalloon() {

	}

	public static void fill(double[][] in aMatrix, double in Value) {

	}

	public static BufferedImage generateImage(double[][][] valueMatrixArray, double maxValue) {
		return null;
	}

	public static BufferedImage generateImage(double[][] valueMatrix, Point scaleFactor, int rgbFlag) {
		return null;
	}

	public static BufferedImage generateImage(BufferedImage imageScalingCoefficients, BufferedImage imageHorizontalWaveletCoeffixcients, BufferedImage imageVerticalWaveletCoefficients, BufferedImage imageDiagonalWaveletCoefficients) {
		return null;
	}

	public static BufferedImage imageEarth() {
		return null;
	}

	public static BufferedImage imageSmalltalkBalloon() {
		return null;
	}

	public static double[][][] lrgbMatrixes(BufferedImage in anImage) {
		return null;
	}

	public double maximumAbsoluteScalingCoefficient() {
		return 0;
	}

	public double maximumAbsoluteSourceCoefficient() {
		return 0;
	}

	public double maximumAbsoluteRecomposedCoefficient() {
		return 0;
	}

	public double maximumAbsoluteWaveletCoefficient() {
		return 0;
	}

	public void mouseClicked(Point aPoint, MouseEvent aMouseEvent) {

	}

	public void mouseDragged(Point aPoint, MouseEvent aMouseEvent) {

	}

	public void open() {

	}

	public void setSourceData(double[][] sourceDataMatrix) {

	}

	public void setSourceData(double[][][] sourceDataMatrixArray) {

	}

	public void showPopupMenu(MouseEvent aMouseEvent, WaveletPaneController in aController) {

	}

}

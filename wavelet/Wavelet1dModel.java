package wavelet;

public class Wavelet1dModel extends WaveletModel, Wavelet2dModel {

	protected double[] sourceCoefficients;

	protected double[] scalingCoefficients;

	protected double[] waveletCoefficients;

	protected double[] interactiveWaveletCoefficients;

	protected double[] recomposedCoefficients;

	protected WaveletPaneModel sourceCoefficientsPaneModel = null;

	protected WaveletPaneModel scalingCoefficientsPaneModel = null;

	protected WaveletPaneModel waveletCoefficientsPaneModel = null;

	protected WaveletPaneModel interactiveWaveletCoefficientsPaneModel = null;

	protected WaveletPaneModel recomposedCoefficientsPaneModel = null;

	private static Point scaleFactor = new Point(10, 100);

	private static double rangeValue = 2.8d;

	public Wavelet1dModel() {

	}

	public void actionPerformed(ActionEvent anActionEvent) {

	}

	public void computeFromPoint(Point aPoint, boolean isAltDown) {

	}

	public void computeRecomposedCoefficients() {

	}

	public static double[] dataSampleCoefficients() {
		return null;
	}

	public void doAllCoefficients() {

	}

	public void doClearCoefficients() {

	}

	public void doSampleCoefficients() {

	}

	public static void fill(double[] anArray, double aValue) {

	}

	public static BufferedImage generateImage(double[] valueCollection) {
		return null;
	}

	public void mouseClicked(Point aPoint, MouseEvent aMouseEvent) {

	}

	public void mouseDragged(Point aPoint, MouseEvent aMouseEvent) {

	}

	public void open() {

	}

	public void setSourceData(double[] sourceDataArray) {

	}

	public void showPopupMenu(MouseEvent aMouseEvent, WaveletPaneController aController) {

	}

}

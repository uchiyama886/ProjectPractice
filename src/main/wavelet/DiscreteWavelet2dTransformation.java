package wavelet;

public class DiscreteWavelet2dTransformation extends DiscreteWaveletTransformation {

	protected double[][] sourceCoefficients;

	protected double[][] scalingCoefficients;

	protected double[][][] waveletCoefficients;

	protected double[][] recomposedCoefficients;

	public DiscreteWavelet2dTransformation(double[][] sourceCollection) {

	}

	public DiscreteWavelet2dTransformation(double[][] scalingCollection, double[][][] waveletCollection) {

	}

	protected void initialize() {

	}

	public double[][] recomposedCoefficients() {
		return null;
	}

	public double[][] scalingCoefficients() {
		return null;
	}

	public void scalingCoefficients(double[][] scalingCollection) {

	}

	public double[][] sourceCoefficients() {
		return null;
	}

	public void sourceCoefficients(double[][] valueCollection) {

	}

	public double[][] waveletCoefficients() {
		return null;
	}

	public void waveletCoefficients(double[][][] waveletCollection) {

	}

	public double[][] diagonalWaveletCoefficients() {
		return null;
	}

	public double[][] horizotalWaveletCoefficients() {
		return null;
	}

	public WaveletTransformation applyTo(Object anObject) {
		return null;
	}

	public WaveletTransformation transform(WaveletTransformation waveletTransformation) {
		return null;
	}

	protected void computeRecomposedCoefficients() {

	}

	protected void computeScalingAndWaveletCoeffcients() {

	}

	private double[] atRow(double[][] coefficients, int index) {
		return null;
	}

	private void atRowPut(double[][] coefficients, int index, double[] values) {

	}

	private int columnSize(double[][] coefficients) {
		return 0;
	}

	private int rowSize(double[][] coefficients) {
		return 0;
	}

	private double[][] transpose(double[][] coefficients) {
		return null;
	}

}

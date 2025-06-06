package wavelet;

public class DiscreteWavelet1dTransformation extends DiscreteWaveletTransformation {

	protected double[] sourceCoefficients;

	protected double[] scalingCoefficients;

	protected double[] waveletCoefficients;

	protected double[] recomposedCoefficients;

	public DiscreteWavelet1dTransformation(double[] sourceCollection) {

	}

	public DiscreteWavelet1dTransformation(double[] scalingCollection, double[] waveletCollection) {

	}

	protected void initialize() {

	}

	public double[] recomposedCoefficients() {
		return null;
	}

	public double[] scalingCoefficients() {
		return null;
	}

	public void scalingCoefficients(double[] scalingCollection) {

	}

	public double[] sourceCoefficients() {
		return null;
	}

	public void sourceCoefficients(double[] valueCollection) {

	}

	public double[] waveletCoefficients() {
		return null;
	}

	public void waveletCoefficients(double[] waveletCollection) {

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

}

package wavelet;

public class DiscreteWavelet1dTransformation extends DiscreteWaveletTransformation {
  protected double[] sourceCoefficients;

  protected double[] scalingCoefficients;

  protected double[] waveletCoefficients;

  protected double[] recomposedCoefficients;

  public DiscreteWavelet1dTransformation(double[] paramArrayOfdouble) {
    sourceCoefficients(paramArrayOfdouble);
  }

  public DiscreteWavelet1dTransformation(double[] paramArrayOfdouble1, double[] paramArrayOfdouble2) {
    scalingCoefficients(paramArrayOfdouble1);
    waveletCoefficients(paramArrayOfdouble2);
  }

  protected void initialize() {
    super.initialize();
    this.sourceCoefficients = null;
    this.scalingCoefficients = null;
    this.waveletCoefficients = null;
    this.recomposedCoefficients = null;
  }

  public double[] recomposedCoefficients() {
    if (this.recomposedCoefficients == null)
      computeRecomposedCoefficients();
    return this.recomposedCoefficients;
  }

  public double[] scalingCoefficients() {
    if (this.scalingCoefficients == null)
      computeScalingAndWaveletCoefficients();
    return this.scalingCoefficients;
  }

  public void scalingCoefficients(double[] paramArrayOfdouble) {
    this.scalingCoefficients = paramArrayOfdouble;
    this.recomposedCoefficients = null;
  }

  public double[] sourceCoefficients() {
    return this.sourceCoefficients;
  }

  public void sourceCoefficients(double[] paramArrayOfdouble) {
    this.sourceCoefficients = paramArrayOfdouble;
    this.scalingCoefficients = null;
    this.recomposedCoefficients = null;
  }

  public double[] waveletCoefficients() {
    if (this.waveletCoefficients == null)
      computeScalingAndWaveletCoefficients();
    return this.waveletCoefficients;
  }

  public void waveletCoefficients(double[] paramArrayOfdouble) {
    this.waveletCoefficients = paramArrayOfdouble;
    this.recomposedCoefficients = null;
  }

  public WaveletTransformation applyTo(Object paramObject) {
    if (!(paramObject instanceof double[]))
      throw new IllegalArgumentException("anObject must be a double[].");
    sourceCoefficients((double[]) paramObject);
    scalingCoefficients();
    waveletCoefficients();
    return this;
  }

  public WaveletTransformation transform(WaveletTransformation paramWaveletTransformation) {
    if (!(paramWaveletTransformation instanceof DiscreteWavelet1dTransformation))
      throw new IllegalArgumentException("waveletTransformation must be a DiscreteWavelet1dTransformation.");
    DiscreteWavelet1dTransformation discreteWavelet1dTransformation = (DiscreteWavelet1dTransformation) paramWaveletTransformation;
    double[] arrayOfDouble = discreteWavelet1dTransformation.sourceCoefficients();
    if (arrayOfDouble == null)
      arrayOfDouble = discreteWavelet1dTransformation.recomposedCoefficients();
    return new DiscreteWavelet1dTransformation(arrayOfDouble);
  }

  protected void computeRecomposedCoefficients() {
    if (this.scalingCoefficients == null)
      return;
    if (this.waveletCoefficients == null)
      return;
    int i = this.scalingCoefficients.length;
    this.recomposedCoefficients = new double[i * 2];
    int j = Math.max(1024, i);
    for (int b = 0; b < i; b++) {
      int b1 = b;
      int k = b1 * 2;
      this.recomposedCoefficients[k] = 0.0D;
      this.recomposedCoefficients[k + 1] = 0.0D;
      for (int b2 = 0; b2 < this.daubechiesScalingSequence.length / 2; b2++) {
        int b3 = b2;
        int m = b3 * 2;
        int n = (b1 - b3 + j) % i;
        double d1 = this.scalingCoefficients[n];
        double d2 = this.waveletCoefficients[n];
        this.recomposedCoefficients[k] = this.recomposedCoefficients[k] + this.daubechiesScalingSequence[m] * d1
            + this.daubechiesWaveletSequence[m] * d2;
        this.recomposedCoefficients[k + 1] = this.recomposedCoefficients[k + 1]
            + this.daubechiesScalingSequence[m + 1] * d1 + this.daubechiesWaveletSequence[m + 1] * d2;
      }
    }
  }

  protected void computeScalingAndWaveletCoefficients() {
    if (this.sourceCoefficients == null)
      return;
    int i = this.sourceCoefficients.length;
    int j = i / 2;
    this.scalingCoefficients = new double[j];
    this.waveletCoefficients = new double[j];
    for (int b = 0; b < j; b++) {
      this.scalingCoefficients[b] = 0.0D;
      this.waveletCoefficients[b] = 0.0D;
      for (int b1 = 0; b1 < this.daubechiesScalingSequence.length; b1++) {
        int k = (b1 + 2 * b) % i;
        double d = this.sourceCoefficients[k];
        this.scalingCoefficients[b] = this.scalingCoefficients[b] + this.daubechiesScalingSequence[b1] * d;
        this.waveletCoefficients[b] = this.waveletCoefficients[b] + this.daubechiesWaveletSequence[b1] * d;
      }
    }
  }
}

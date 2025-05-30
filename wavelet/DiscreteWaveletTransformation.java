package wavelet;

public abstract class DiscreteWaveletTransformation extends WaveletTransformation {
  protected double[] daubechiesScalingSequence;

  protected double[] daubechiesWaveletSequence;

  protected void initialize() {
    initialize(2);
  }

  protected void initialize(int paramInt) {
    super.initialize();
    this.daubechiesScalingSequence = new double[] { 0.4829629131445341D, 0.8365163037378077D, 0.2241438680420134D,
        -0.1294095225512603D };
    if (paramInt == 3)
      this.daubechiesScalingSequence = new double[] { 0.3326705529500825D, 0.8068915093110924D, 0.4598775021184914D,
          -0.1350110200102546D, -0.0854412738820267D, 0.0352262918857095D };
    if (paramInt == 4)
      this.daubechiesScalingSequence = new double[] { 0.2303778133088964D, 0.7148465705529155D, 0.6308807679298599D,
          -0.0279837694168599D, -0.1870348117190931D, 0.0308413818355607D, 0.0328830116668852D, -0.010597401785069D };
    int i = this.daubechiesScalingSequence.length;
    this.daubechiesWaveletSequence = new double[i];
    for (int b = 0; b < i; b++)
      this.daubechiesWaveletSequence[b] = Math.pow(-1.0D, b) * this.daubechiesScalingSequence[i - 1 - b];
  }
}

package wavelet;

public class DiscreteWavelet2dTransformation extends DiscreteWaveletTransformation {
  protected double[][] sourceCoefficients;
  
  protected double[][] scalingCoefficients;
  
  protected double[][][] waveletCoefficients;
  
  protected double[][] recomposedCoefficients;
  
  public DiscreteWavelet2dTransformation(double[][] paramArrayOfdouble) {
    sourceCoefficients(paramArrayOfdouble);
  }
  
  public DiscreteWavelet2dTransformation(double[][] paramArrayOfdouble, double[][][] paramArrayOfdouble1) {
    scalingCoefficients(paramArrayOfdouble);
    waveletCoefficients(paramArrayOfdouble1);
  }
  
  protected void initialize() {
    super.initialize();
    this.sourceCoefficients = null;
    this.scalingCoefficients = null;
    this.waveletCoefficients = null;
    this.recomposedCoefficients = null;
  }
  
  public double[][] recomposedCoefficients() {
    if (this.recomposedCoefficients == null)
      computeRecomposedCoefficients(); 
    return this.recomposedCoefficients;
  }
  
  public double[][] scalingCoefficients() {
    if (this.scalingCoefficients == null)
      computeScalingAndWaveletCoefficients(); 
    return this.scalingCoefficients;
  }
  
  public void scalingCoefficients(double[][] paramArrayOfdouble) {
    this.scalingCoefficients = paramArrayOfdouble;
    this.recomposedCoefficients = null;
  }
  
  public double[][] sourceCoefficients() {
    return this.sourceCoefficients;
  }
  
  public void sourceCoefficients(double[][] paramArrayOfdouble) {
    this.sourceCoefficients = paramArrayOfdouble;
    this.scalingCoefficients = null;
    this.recomposedCoefficients = null;
  }
  
  public double[][][] waveletCoefficients() {
    if (this.waveletCoefficients == null)
      computeScalingAndWaveletCoefficients(); 
    return this.waveletCoefficients;
  }
  
  public void waveletCoefficients(double[][][] paramArrayOfdouble) {
    this.waveletCoefficients = paramArrayOfdouble;
    this.recomposedCoefficients = null;
  }
  
  public double[][] diagonalWaveletCoefficients() {
    return waveletCoefficients()[2];
  }
  
  public double[][] horizontalWaveletCoefficients() {
    return waveletCoefficients()[0];
  }
  
  public double[][] verticalWaveletCoefficients() {
    return waveletCoefficients()[1];
  }
  
  public WaveletTransformation applyTo(Object paramObject) {
    if (!(paramObject instanceof double[][]))
      throw new IllegalArgumentException("anObject must be a double[][]."); 
    sourceCoefficients((double[][])paramObject);
    scalingCoefficients();
    waveletCoefficients();
    return this;
  }
  
  public WaveletTransformation transform(WaveletTransformation paramWaveletTransformation) {
    if (!(paramWaveletTransformation instanceof DiscreteWavelet2dTransformation))
      throw new IllegalArgumentException("waveletTransformation must be a DiscreteWavelet2dTransformation."); 
    DiscreteWavelet2dTransformation discreteWavelet2dTransformation = (DiscreteWavelet2dTransformation)paramWaveletTransformation;
    double[][] arrayOfDouble = discreteWavelet2dTransformation.sourceCoefficients();
    if (arrayOfDouble == null)
      arrayOfDouble = discreteWavelet2dTransformation.recomposedCoefficients(); 
    return new DiscreteWavelet2dTransformation(arrayOfDouble);
  }
  
  protected void computeRecomposedCoefficients() {
    if (this.scalingCoefficients == null)
      return; 
    if (this.waveletCoefficients == null)
      return; 
    int i = rowSize(this.scalingCoefficients);
    int j = i * 2;
    int k = columnSize(this.scalingCoefficients);
    int m = k * 2;
    double[][] arrayOfDouble1 = transpose(this.scalingCoefficients);
    double[][] arrayOfDouble2 = transpose(horizontalWaveletCoefficients());
    double[][] arrayOfDouble3 = transpose(verticalWaveletCoefficients());
    double[][] arrayOfDouble4 = transpose(diagonalWaveletCoefficients());
    double[][] arrayOfDouble5 = new double[k][j];
    double[][] arrayOfDouble6 = new double[k][j];
    for (byte b1 = 0; b1 < k; b1++) {
      DiscreteWavelet1dTransformation discreteWavelet1dTransformation = new DiscreteWavelet1dTransformation(atRow(arrayOfDouble1, b1), atRow(arrayOfDouble2, b1));
      atRowPut(arrayOfDouble5, b1, discreteWavelet1dTransformation.recomposedCoefficients());
      discreteWavelet1dTransformation = new DiscreteWavelet1dTransformation(atRow(arrayOfDouble3, b1), atRow(arrayOfDouble4, b1));
      atRowPut(arrayOfDouble6, b1, discreteWavelet1dTransformation.recomposedCoefficients());
    } 
    arrayOfDouble5 = transpose(arrayOfDouble5);
    arrayOfDouble6 = transpose(arrayOfDouble6);
    double[][] arrayOfDouble7 = new double[j][m];
    for (byte b2 = 0; b2 < j; b2++) {
      DiscreteWavelet1dTransformation discreteWavelet1dTransformation = new DiscreteWavelet1dTransformation(atRow(arrayOfDouble5, b2), atRow(arrayOfDouble6, b2));
      atRowPut(arrayOfDouble7, b2, discreteWavelet1dTransformation.recomposedCoefficients());
    } 
    this.recomposedCoefficients = arrayOfDouble7;
  }
  
  protected void computeScalingAndWaveletCoefficients() {
    if (this.sourceCoefficients == null)
      return; 
    int i = rowSize(this.sourceCoefficients);
    int j = i / 2;
    int k = columnSize(this.sourceCoefficients);
    int m = k / 2;
    double[][] arrayOfDouble1 = new double[i][m];
    double[][] arrayOfDouble2 = new double[i][m];
    for (byte b1 = 0; b1 < i; b1++) {
      DiscreteWavelet1dTransformation discreteWavelet1dTransformation = new DiscreteWavelet1dTransformation(atRow(this.sourceCoefficients, b1));
      atRowPut(arrayOfDouble1, b1, discreteWavelet1dTransformation.scalingCoefficients());
      atRowPut(arrayOfDouble2, b1, discreteWavelet1dTransformation.waveletCoefficients());
    } 
    arrayOfDouble1 = transpose(arrayOfDouble1);
    arrayOfDouble2 = transpose(arrayOfDouble2);
    double[][] arrayOfDouble3 = new double[m][j];
    double[][] arrayOfDouble4 = new double[m][j];
    double[][] arrayOfDouble5 = new double[m][j];
    double[][] arrayOfDouble6 = new double[m][j];
    for (byte b2 = 0; b2 < m; b2++) {
      DiscreteWavelet1dTransformation discreteWavelet1dTransformation = new DiscreteWavelet1dTransformation(atRow(arrayOfDouble1, b2));
      atRowPut(arrayOfDouble3, b2, discreteWavelet1dTransformation.scalingCoefficients());
      atRowPut(arrayOfDouble4, b2, discreteWavelet1dTransformation.waveletCoefficients());
      discreteWavelet1dTransformation = new DiscreteWavelet1dTransformation(atRow(arrayOfDouble2, b2));
      atRowPut(arrayOfDouble5, b2, discreteWavelet1dTransformation.scalingCoefficients());
      atRowPut(arrayOfDouble6, b2, discreteWavelet1dTransformation.waveletCoefficients());
    } 
    arrayOfDouble3 = transpose(arrayOfDouble3);
    arrayOfDouble4 = transpose(arrayOfDouble4);
    arrayOfDouble5 = transpose(arrayOfDouble5);
    arrayOfDouble6 = transpose(arrayOfDouble6);
    this.scalingCoefficients = arrayOfDouble3;
    this.waveletCoefficients = new double[][][] { arrayOfDouble4, arrayOfDouble5, arrayOfDouble6 };
  }
  
  private double[] atRow(double[][] paramArrayOfdouble, int paramInt) {
    return paramArrayOfdouble[paramInt];
  }
  
  private void atRowPut(double[][] paramArrayOfdouble, int paramInt, double[] paramArrayOfdouble1) {
    for (byte b = 0; b < (paramArrayOfdouble[paramInt]).length; b++)
      paramArrayOfdouble[paramInt][b] = paramArrayOfdouble1[b]; 
  }
  
  private int columnSize(double[][] paramArrayOfdouble) {
    return (paramArrayOfdouble[0]).length;
  }
  
  private int rowSize(double[][] paramArrayOfdouble) {
    return paramArrayOfdouble.length;
  }
  
  private double[][] transpose(double[][] paramArrayOfdouble) {
    int i = rowSize(paramArrayOfdouble);
    int j = columnSize(paramArrayOfdouble);
    double[][] arrayOfDouble = new double[j][i];
    for (byte b = 0; b < paramArrayOfdouble.length; b++) {
      for (byte b1 = 0; b1 < (paramArrayOfdouble[b]).length; b1++)
        arrayOfDouble[b1][b] = paramArrayOfdouble[b][b1]; 
    } 
    return arrayOfDouble;
  }
}



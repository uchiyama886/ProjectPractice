package wavelet;

/**
 * 2次元離散ウェーブレット変換（DWT）を実装するクラス。
 * <p>
 * このクラスは画像などの2次元データに対してスケーリング係数と
 * ウェーブレット係数（水平・垂直・斜め）への分解および逆変換を行う。
 * 内部的には1次元DWTを行と列の両方向に適用して処理を行う。
 * </p>
 *
 * @see DiscreteWavelet1dTransformation
 * @see DiscreteWaveletTransformation
 */
public class DiscreteWavelet2dTransformation extends DiscreteWaveletTransformation {
  /**
   * 元の二次元信号(行列)。
   */
  protected double[][] sourceCoefficients;
  
  /**
   * 低周波成分(近似画像)。
   */
  protected double[][] scalingCoefficients;
  
  /**
   * 高周波成分(水平・垂直・斜めの詳細成分)。
   */
  protected double[][][] waveletCoefficients;
  
  /**
   * 再構成された二次元信号。
   */
  protected double[][] recomposedCoefficients;
  
  /**
   * 元画像(二次元配列)からDWTを行う。
   * @param sourceCollection 入力信号
   */
  public DiscreteWavelet2dTransformation(double[][] sourceCollection) 
  {
    sourceCoefficients(sourceCollection);
  }
  
  /**
   * スケーリング係数とウェーブレット係数から再構成用インスタンスを作成。
   * @param scalingCollection スケーリング係数
   * @param waveletCollection ウェーブレット係数(水平・垂直・斜め)
   */
  public DiscreteWavelet2dTransformation(double[][] scalingCollection, double[][][] waveletCollection) 
  {
    scalingCoefficients(scalingCollection);
    waveletCoefficients(waveletCollection);
  }
  
  /**
   * フィールドを初期状態に初期化する。
   */
  protected void initialize() 
  {
    super.initialize();
    this.sourceCoefficients = null;
    this.scalingCoefficients = null;
    this.waveletCoefficients = null;
    this.recomposedCoefficients = null;
  }
  
  /**
   * 再構成された二次元配列を返す。
   * 未計算の場合は自動計算される。
   * @return 再構成画像
   */
  public double[][] recomposedCoefficients() 
  {
    if (this.recomposedCoefficients == null)
      computeRecomposedCoefficients(); 
    return this.recomposedCoefficients;
  }
  
  /**
   * スケーリング係数を返す。
   * 未計算なら自動的に計算される。
   * @return スケーリング係数
   */
  public double[][] scalingCoefficients() 
  {
    if (this.scalingCoefficients == null)
      computeScalingAndWaveletCoefficients(); 
    return this.scalingCoefficients;
  }
  
  /**
   * スケーリング係数を設定する。
   * @param scalingCollection スケーリング係数
   */
  public void scalingCoefficients(double[][] scalingCollection) 
  {
    this.scalingCoefficients = scalingCollection;
    this.recomposedCoefficients = null;
  }
  
  /**
   * 元の二次元信号を返す。
   * @return 元信号
   */
  public double[][] sourceCoefficients() 
  {
    return this.sourceCoefficients;
  }
  
  /**
   * 元の信号を設定し、変換結果をリセットする。
   * @param valueCollction 元信号(二次元配列)
   */
  public void sourceCoefficients(double[][] valueCollction) 
  {
    this.sourceCoefficients = valueCollction;
    this.scalingCoefficients = null;
    this.recomposedCoefficients = null;
  }
  
  /**
   * ウェーブレット係数(水平・垂直・斜め)を返す。
   * 未計算の場合は計算される。
   * @return
   */
  public double[][][] waveletCoefficients() 
  {
    if (this.waveletCoefficients == null)
      computeScalingAndWaveletCoefficients(); 
    return this.waveletCoefficients;
  }
  
  /**
   * ウェーブレット係数を設定する。
   * @param waveletCollection ウェーブレット係数
   */
  public void waveletCoefficients(double[][][] waveletCollection) 
  {
    this.waveletCoefficients = waveletCollection;
    this.recomposedCoefficients = null;
  }
  
  /**
   * ウェーブレット係数を設定する。
   * @return 斜め成分のウェーブレット係数
   */
  public double[][] diagonalWaveletCoefficients() 
  {
    return waveletCoefficients()[2];
  }
  
  /**
   * ウェーブレット係数を設定する。
   * @return 水平成分のウェーブレット係数
   */
  public double[][] horizontalWaveletCoefficients() 
  {
    return waveletCoefficients()[0];
  }
  
  /**
   * ウェーブレット係数を設定する。
   * @return 垂直方向のウェーブレット係数
   */
  public double[][] verticalWaveletCoefficients() 
  {
    return waveletCoefficients()[1];
  }
  
  /**
   * 任意のオブジェクトに対してDWTを適用する。
   * @param anObject オブジェクト
   * @return このインスタンス
   */
  public WaveletTransformation applyTo(Object anObject) 
  {
    if (!(anObject instanceof double[][]))
      throw new IllegalArgumentException("anObject must be a double[][]."); 
    sourceCoefficients((double[][])anObject);
    scalingCoefficients();
    waveletCoefficients();
    return this;
  }
  
  /**
   * 他の変換インスタンスから再変換する。
   * @param waveletTransformation 入力変換インスタンス
   * @return 新しい変換済みインスタンス
   */
  public WaveletTransformation transform(WaveletTransformation waveletTransformation) 
  {
    if (!(waveletTransformation instanceof DiscreteWavelet2dTransformation))
      throw new IllegalArgumentException("waveletTransformation must be a DiscreteWavelet2dTransformation."); 
    DiscreteWavelet2dTransformation discreteWavelet2dTransformation = (DiscreteWavelet2dTransformation)waveletTransformation;
    double[][] arrayOfDouble = discreteWavelet2dTransformation.sourceCoefficients();
    if (arrayOfDouble == null)
      arrayOfDouble = discreteWavelet2dTransformation.recomposedCoefficients(); 
    return new DiscreteWavelet2dTransformation(arrayOfDouble);
  }
  
  /**
   * 再構成処理(逆変換)
   */
  protected void computeRecomposedCoefficients() 
  {
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
    for (Integer b1 = 0; b1 < k; b1++) 
    {
      DiscreteWavelet1dTransformation discreteWavelet1dTransformation = new DiscreteWavelet1dTransformation(atRow(arrayOfDouble1, b1), atRow(arrayOfDouble2, b1));
      atRowPut(arrayOfDouble5, b1, discreteWavelet1dTransformation.recomposedCoefficients());
      discreteWavelet1dTransformation = new DiscreteWavelet1dTransformation(atRow(arrayOfDouble3, b1), atRow(arrayOfDouble4, b1));
      atRowPut(arrayOfDouble6, b1, discreteWavelet1dTransformation.recomposedCoefficients());
    } 
    arrayOfDouble5 = transpose(arrayOfDouble5);
    arrayOfDouble6 = transpose(arrayOfDouble6);
    double[][] arrayOfDouble7 = new double[j][m];
    for (Integer b2 = 0; b2 < j; b2++) 
    {
      DiscreteWavelet1dTransformation discreteWavelet1dTransformation = new DiscreteWavelet1dTransformation(atRow(arrayOfDouble5, b2), atRow(arrayOfDouble6, b2));
      atRowPut(arrayOfDouble7, b2, discreteWavelet1dTransformation.recomposedCoefficients());
    } 
    this.recomposedCoefficients = arrayOfDouble7;
  }
  
  /**
   * 順変換処理(DWTの本体)
   */
  protected void computeScalingAndWaveletCoefficients() 
  {
    if (this.sourceCoefficients == null)
      return; 
    int i = rowSize(this.sourceCoefficients);
    int j = i / 2;
    int k = columnSize(this.sourceCoefficients);
    int m = k / 2;
    double[][] arrayOfDouble1 = new double[i][m];
    double[][] arrayOfDouble2 = new double[i][m];
    for (Integer b1 = 0; b1 < i; b1++) 
    {
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
    for (Integer b2 = 0; b2 < m; b2++) 
    {
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
  
  /**
   * 指定した二次元配列の特定行を取得する。
   * @param coefficients 二次元配列
   * @param index 取得したい行のインデックス
   * @return 指定行の一次元配列
   */
  private double[] atRow(double[][] coefficients, int index) 
  {
    return coefficients[index];
  }
  
  // private void atRowPut(double[][] paramArrayOfdouble, int paramInt, double[] paramArrayOfdouble1) {
  //   for (Integer b = 0; b < (paramArrayOfdouble[paramInt]).length; b++)
  //     paramArrayOfdouble[paramInt][b] = paramArrayOfdouble1[b]; 
  // }

  /** 
   * 指定した二次元配列の特定行に、一次元配列の値を上書きする。
   * 行長と values の長さが異なる場合、短い方の長さまでコピーされる。
   * @param coefficients 二次元配列
   * @param index 書き込み先の行インデックス
   * @param values 上書きする一次元配列の値
   */
  private void atRowPut(double[][] coefficients, int index, double[] values) 
  {
    // paramArrayOfdouble[paramInt] の実際の長さと、paramArrayOfdouble1 の実際の長さの、短い方をループの上限にする
    int copyLength = Math.min((coefficients[index]).length, values.length);
    for (Integer b = 0; b < copyLength; b++)
      coefficients[index][b] = values[b]; 
  }
  
  /**
   * 二次元配列の列数を取得する。
   * @param coefficients 二次元配列
   * @return 列数
   */
  private int columnSize(double[][] coefficients) 
  {
    return (coefficients[0]).length;
  }
  
  /**
   * 二次元配列の行数を取得する。
   * @param coefficients 二次元配列
   * @return 行数
   */
  private int rowSize(double[][] coefficients) 
  {
    return coefficients.length;
  }
  
  /**
   * 指定した二次元配列を転置する。
   * 行と列を入れ替えた新しい配列を返す。
   * @param coefficients 転置対象の二次元配列
   * @return 転置された二次元配列
   */
  private double[][] transpose(double[][] coefficients) 
  {
    int i = rowSize(coefficients);
    int j = columnSize(coefficients);
    double[][] arrayOfDouble = new double[j][i];
    for (Integer b = 0; b < coefficients.length; b++) {
      for (Integer b1 = 0; b1 < (coefficients[b]).length; b1++)
        arrayOfDouble[b1][b] = coefficients[b][b1]; 
    } 
    return arrayOfDouble;
  }
}
package wavelet;

/**
 * 1次元離散ウェーブレット変換（DWT）を実装するクラス。
 * このクラスは、与えられた1次元のデータ（配列）に対して、
 * スケーリング係数とウェーブレット係数への分解、およびそれらの係数からの再構成を扱う。
 *
 * <p>離散ウェーブレット変換は、信号を異なる周波数帯域に分解することで、
 * 信号の様々なスケールにおける特徴を抽出するために用いられる。
 * この実装は、特にDaubechiesウェーブレットに基づく変換をサポートしている。</p>
 *
 * @see DiscreteWaveletTransformation
 * @see ContinuosWaveletTransformation
 */
public class DiscreteWavelet1dTransformation extends DiscreteWaveletTransformation {
  /**
   * 変換元の係数を保持するフィールド。
   * 通常、時間領域の信号データがここに格納される。
   */
  protected double[] sourceCoefficients;
  
  /**
   * スケーリング係数（近似係数）を保持するフィールド。
   * 信号の低周波成分を表す。
   */
  protected double[] scalingCoefficients;
  
  /**
   * ウェーブレット係数（詳細係数）を保持するフィールド。
   * 信号の高周波成分を表す。
   */
  protected double[] waveletCoefficients;
  
  /**
   * 再構成された係数を保持するフィールド。
   * スケーリング係数とウェーブレット係数から再構築された信号が格納される。
   */
  protected double[] recomposedCoefficients;
  
  /**
   * 変換元の係数を指定して新しい {@code DiscreteWavelet1dTransformation} インスタンスを構築する。
   * このコンストラクタは、与えられたデータからスケーリング係数とウェーブレット係数を計算する準備を行う。
   * @param paramArrayOfdouble 変換元の信号データ（係数）
   */
  public DiscreteWavelet1dTransformation(double[] paramArrayOfdouble) {
    sourceCoefficients(paramArrayOfdouble);
  }
  
  /**
   * スケーリング係数とウェーブレット係数を指定して新しい {@code DiscreteWavelet1dTransformation} インスタンスを構築する。
   * このコンストラクタは、与えられた係数から元の信号を再構成する準備を行う。
   * @param paramArrayOfdouble1 スケーリング係数
   * @param paramArrayOfdouble2 ウェーブレット係数
   */
  public DiscreteWavelet1dTransformation(double[] paramArrayOfdouble1, double[] paramArrayOfdouble2) {
    scalingCoefficients(paramArrayOfdouble1);
    waveletCoefficients(paramArrayOfdouble2);
  }
  
  /**
   * この変換オブジェクトの内部状態を初期化する。
   * 全ての係数配列を {@code null} に設定し、親クラスの初期化メソッドを呼び出す。
   */
  protected void initialize() {
    super.initialize();
    this.sourceCoefficients = null;
    this.scalingCoefficients = null;
    this.waveletCoefficients = null;
    this.recomposedCoefficients = null;
  }
  
  /**
   * 再構成された係数を応答する。
   * もし再構成係数がまだ計算されていない場合、このメソッドは自動的に計算を実行する。
   * @return 再構成された信号データ（係数）の配列
   */
  public double[] recomposedCoefficients() {
    if (this.recomposedCoefficients == null)
      computeRecomposedCoefficients(); 
    return this.recomposedCoefficients;
  }
  
  /**
   * スケーリング係数（近似係数）を応答する。
   * もしスケーリング係数がまだ計算されていない場合、このメソッドは自動的にスケーリング係数とウェーブレット係数の両方を計算する。
   * @return スケーリング係数の配列
   */
  public double[] scalingCoefficients() {
    if (this.scalingCoefficients == null)
      computeScalingAndWaveletCoefficients(); 
    return this.scalingCoefficients;
  }
  
  /**
   * スケーリング係数を設定する。
   * これにより、以前に計算された再構成係数は無効になる。
   * @param paramArrayOfdouble 新しいスケーリング係数の配列
   */
  public void scalingCoefficients(double[] paramArrayOfdouble) {
    this.scalingCoefficients = paramArrayOfdouble;
    this.recomposedCoefficients = null;
  }
  
  /**
   * 変換元の係数を応答する。
   * @return 変換元の信号データ（係数）の配列
   */
  public double[] sourceCoefficients() {
    return this.sourceCoefficients;
  }
  
  /**
   * 変換元の係数を設定する。
   * これにより、以前に計算されたスケーリング係数、ウェーブレット係数、および再構成係数は無効になる。
   * @param paramArrayOfdouble 新しい変換元の信号データ（係数）の配列
   */
  public void sourceCoefficients(double[] paramArrayOfdouble) {
    this.sourceCoefficients = paramArrayOfdouble;
    this.scalingCoefficients = null;
    this.recomposedCoefficients = null;
  }
  
  /**
   * ウェーブレット係数（詳細係数）を応答する。
   * もしウェーブレット係数がまだ計算されていない場合、このメソッドは自動的にスケーリング係数とウェーブレット係数の両方を計算する。
   * @return ウェーブレット係数の配列
   */
  public double[] waveletCoefficients() {
    if (this.waveletCoefficients == null)
      computeScalingAndWaveletCoefficients(); 
    return this.waveletCoefficients;
  }
  
  /**
   * ウェーブレット係数を設定する。
   * これにより、以前に計算された再構成係数は無効になる。
   * @param paramArrayOfdouble 新しいウェーブレット係数の配列
   */
  public void waveletCoefficients(double[] paramArrayOfdouble) {
    this.waveletCoefficients = paramArrayOfdouble;
    this.recomposedCoefficients = null;
  }
  
  /**
   * 指定されたオブジェクトに対して1次元離散ウェーブレット変換を適用する。
   * 変換元の係数を設定し、スケーリング係数とウェーブレット係数を計算する。
   * @param paramObject 変換を適用する対象のオブジェクト。{@code double[]} 型である必要がある。
   * @return この変換オブジェクト自身
   * @throws IllegalArgumentException {@code paramObject} が {@code double[]} 型でない場合
   */
  public WaveletTransformation applyTo(Object paramObject) {
    if (!(paramObject instanceof double[]))
      throw new IllegalArgumentException("anObject must be a double[]."); 
    sourceCoefficients((double[])paramObject);
    scalingCoefficients();
    waveletCoefficients();
    return this;
  }
  
  /**
   * 別のウェーブレット変換オブジェクトに対してこの変換を適用する。
   * このメソッドは、指定された変換オブジェクトのソース係数、または再構成係数を使用して、
   * 新しい {@code DiscreteWavelet1dTransformation} インスタンスを生成する。
   * @param paramWaveletTransformation 変換を適用する対象のウェーブレット変換。
   * {@code DiscreteWavelet1dTransformation} 型である必要がある。
   * @return 新しい {@code DiscreteWavelet1dTransformation} インスタンス
   * @throws IllegalArgumentException {@code paramWaveletTransformation} が
   * {@code DiscreteWavelet1dTransformation} 型でない場合
   */
  public WaveletTransformation transform(WaveletTransformation paramWaveletTransformation) {
    if (!(paramWaveletTransformation instanceof DiscreteWavelet1dTransformation))
      throw new IllegalArgumentException("waveletTransformation must be a DiscreteWavelet1dTransformation."); 
    DiscreteWavelet1dTransformation discreteWavelet1dTransformation = (DiscreteWavelet1dTransformation)paramWaveletTransformation;
    double[] arrayOfDouble = discreteWavelet1dTransformation.sourceCoefficients();
    if (arrayOfDouble == null)
      arrayOfDouble = discreteWavelet1dTransformation.recomposedCoefficients(); 
    return new DiscreteWavelet1dTransformation(arrayOfDouble);
  }
  
  /**
   * スケーリング係数とウェーブレット係数から元の信号を再構成する。
   * このメソッドは、{@link #scalingCoefficients} と {@link #waveletCoefficients} が設定されている場合にのみ機能する。
   * 結果は {@link #recomposedCoefficients} に格納される。
   * <p>再構成のアルゴリズムは、Daubechiesウェーブレットの再構成フィルターに基づいている。</p>
   */
  protected void computeRecomposedCoefficients() {
    if (this.scalingCoefficients == null)
      return; 
    if (this.waveletCoefficients == null)
      return; 
    int i = this.scalingCoefficients.length;
    this.recomposedCoefficients = new double[i * 2];
    int j = Math.max(1024, i); // 周期的畳み込みのためのオフセット計算に使用される可能性のある値
    for (byte b = 0; b < i; b++) {
      byte b1 = b;
      int k = b1 * 2;
      this.recomposedCoefficients[k] = 0.0D;
      this.recomposedCoefficients[k + 1] = 0.0D;
      for (byte b2 = 0; b2 < this.daubechiesScalingSequence.length / 2; b2++) {
        byte b3 = b2;
        int m = b3 * 2;
        int n = (b1 - b3 + j) % i; // 周期的境界条件を考慮した係数のインデックス
        double d1 = this.scalingCoefficients[n];
        double d2 = this.waveletCoefficients[n];
        this.recomposedCoefficients[k] = this.recomposedCoefficients[k] + this.daubechiesScalingSequence[m] * d1 + this.daubechiesWaveletSequence[m] * d2;
        this.recomposedCoefficients[k + 1] = this.recomposedCoefficients[k + 1] + this.daubechiesScalingSequence[m + 1] * d1 + this.daubechiesWaveletSequence[m + 1] * d2;
      } 
    } 
  }
  
  /**
   * 変換元の信号データからスケーリング係数とウェーブレット係数を計算する。
   * このメソッドは、{@link #sourceCoefficients} が設定されている場合にのみ機能する。
   * 結果は {@link #scalingCoefficients} と {@link #waveletCoefficients} に格納される。
   * <p>分解のアルゴリズムは、Daubechiesウェーブレットの分解フィルターに基づいている。</p>
   */
  protected void computeScalingAndWaveletCoefficients() {
    if (this.sourceCoefficients == null)
      return; 
    int i = this.sourceCoefficients.length;
    int j = i / 2;
    this.scalingCoefficients = new double[j];
    this.waveletCoefficients = new double[j];
    for (byte b = 0; b < j; b++) {
      this.scalingCoefficients[b] = 0.0D;
      this.waveletCoefficients[b] = 0.0D;
      for (byte b1 = 0; b1 < this.daubechiesScalingSequence.length; b1++) {
        int k = (b1 + 2 * b) % i; // 周期的境界条件を考慮した変換元の係数のインデックス
        double d = this.sourceCoefficients[k];
        this.scalingCoefficients[b] = this.scalingCoefficients[b] + this.daubechiesScalingSequence[b1] * d;
        this.waveletCoefficients[b] = this.waveletCoefficients[b] + this.daubechiesWaveletSequence[b1] * d;
      } 
    } 
  }
}
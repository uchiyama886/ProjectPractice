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
   * 変換元の1次元データ系列（係数）を保持する。
   * 分解（transform）の入力、または再構成（recompose）の出力として使用される。
   */
  protected double[] sourceCoefficients;
  
  /**
   * スケーリング係数（近似係数）を保持する。
   * これは、信号の低周波成分や滑らかな部分を表す。
   * 分解の出力、または再構成の入力として使用される。
   */
  protected double[] scalingCoefficients;
  
  /**
   * ウェーブレット係数（詳細係数）を保持する。
   * これは、信号の高周波成分やエッジなどの詳細部分を表す。
   * 分解の出力、または再構成の入力として使用される。
   */
  protected double[] waveletCoefficients;
  
  /**
   * ウェーブレット係数から再構成された1次元データ系列（係数）を保持する。
   * 再構成（recompose）の出力として使用される。
   */
  protected double[] recomposedCoefficients;
  
  /**
   * 新しいDiscreteWavelet1dTransformationのインスタンスを初期化する。
   * このコンストラクタは、変換元のデータ系列を指定してオブジェクトを構築する。
   *
   * @param paramArrayOfdouble 変換元の1次元データ系列（ソース係数）。
   */
  public DiscreteWavelet1dTransformation(double[] paramArrayOfdouble) {
    initialize();
    sourceCoefficients(paramArrayOfdouble);
  }
  
  /**
   * 新しいDiscreteWavelet1dTransformationのインスタンスを初期化する。
   * このコンストラクタは、スケーリング係数とウェーブレット係数を指定してオブジェクトを構築する。
   * これらは、後で元のデータ系列を再構成するために使用できる。
   *
   * @param paramArrayOfdouble1 スケーリング係数（近似係数）。
   * @param paramArrayOfdouble2 ウェーブレット係数（詳細係数）。
   */
  public DiscreteWavelet1dTransformation(double[] paramArrayOfdouble1, double[] paramArrayOfdouble2) {
    initialize();
    scalingCoefficients(paramArrayOfdouble1);
    waveletCoefficients(paramArrayOfdouble2);
  }
  
  /**
   * オブジェクトの内部状態を初期化する。
   * 親クラスの初期化メソッドを呼び出し、自身の係数配列をnullに設定する。
   */
  @Override
  protected void initialize() {
    super.initialize();
    this.sourceCoefficients = null;
    this.scalingCoefficients = null;
    this.waveletCoefficients = null;
    this.recomposedCoefficients = null;
  }
  
  /**
   * 再構成された係数（元のデータ系列）を返す。
   * もし係数がまだ計算されていない場合、内部的に計算処理を行う。
   *
   * @return 再構成されたdouble型配列。
   */
  public double[] recomposedCoefficients() {
    if (this.recomposedCoefficients == null)
      computeRecomposedCoefficients(); 
    return this.recomposedCoefficients;
  }
  
  /**
   * スケーリング係数（近似係数）を返す。
   * もし係数がまだ計算されていない場合、内部的にスケーリング係数とウェーブレット係数の
   * 両方を計算する処理を行う。
   *
   * @return スケーリング係数のdouble型配列。
   */
  public double[] scalingCoefficients() {
    if (this.scalingCoefficients == null)
      computeScalingAndWaveletCoefficients(); 
    return this.scalingCoefficients;
  }
  
  /**
   * スケーリング係数を設定する。
   * スケーリング係数が変更されると、再構成された係数も無効になるため、nullに設定される。
   *
   * @param paramArrayOfdouble 設定するスケーリング係数のdouble型配列。
   */
  public void scalingCoefficients(double[] paramArrayOfdouble) {
    this.scalingCoefficients = paramArrayOfdouble;
    this.recomposedCoefficients = null;
  }
  
  /**
   * 変換元のデータ系列（ソース係数）を返す。
   *
   * @return 変換元のデータ系列のdouble型配列。
   */
  public double[] sourceCoefficients() {
    return this.sourceCoefficients;
  }
  
  /**
   * 変換元のデータ系列（ソース係数）を設定する。
   * ソース係数が変更されると、スケーリング係数、ウェーブレット係数、
   * および再構成された係数が全て無効になるため、nullに設定される。
   *
   * @param paramArrayOfdouble 設定する変換元のデータ系列のdouble型配列。
   */
  public void sourceCoefficients(double[] paramArrayOfdouble) {
    this.sourceCoefficients = paramArrayOfdouble;
    this.scalingCoefficients = null;
    this.recomposedCoefficients = null;
  }
  
  /**
   * ウェーブレット係数（詳細係数）を返す。
   * もし係数がまだ計算されていない場合、内部的にスケーリング係数とウェーブレット係数の
   * 両方を計算する処理を行う。
   *
   * @return ウェーブレット係数のdouble型配列。
   */
  public double[] waveletCoefficients() {
    if (this.waveletCoefficients == null)
      computeScalingAndWaveletCoefficients(); 
    return this.waveletCoefficients;
  }
  
  /**
   * ウェーブレット係数を設定する。
   * ウェーブレット係数が変更されると、再構成された係数も無効になるため、nullに設定される。
   *
   * @param paramArrayOfdouble 設定するウェーブレット係数のdouble型配列。
   */
  public void waveletCoefficients(double[] paramArrayOfdouble) {
    this.waveletCoefficients = paramArrayOfdouble;
    this.recomposedCoefficients = null;
  }
  
  /**
   * 指定されたオブジェクトに1次元離散ウェーブレット変換を適用する。
   * このメソッドは、入力としてdouble配列を受け取り、それをソース係数として設定し、
   * スケーリング係数とウェーブレット係数を計算する。
   *
   * @param paramObject 変換を適用する対象のオブジェクト。double型配列である必要がある。
   * @return 変換を適用した後のこのインスタンス自身。
   * @throws IllegalArgumentException paramObjectがdouble[]型でない場合。
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
   * 別のWaveletTransformationオブジェクトからこのインスタンスへの変換を行う。
   * 具体的には、指定されたDiscreteWavelet1dTransformationのソース係数、
   * または再構成された係数を基に、新しいDiscreteWavelet1dTransformationインスタンスを生成する。
   *
   * @param paramWaveletTransformation 変換元となるWaveletTransformationオブジェクト。
   * DiscreteWavelet1dTransformationのインスタンスである必要がある。
   * @return 新しいDiscreteWavelet1dTransformationインスタンス。
   * @throws IllegalArgumentException paramWaveletTransformationがDiscreteWavelet1dTransformationの
   * インスタンスでない場合。
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
   * スケーリング係数とウェーブレット係数から元のデータ系列（再構成された係数）を計算する。
   * このメソッドは、主に逆ウェーブレット変換（IWT）を実行する。
   * 計算にはDaubechiesウェーブレットの逆フィルタリング（合成）シーケンスを使用する。
   */
  protected void computeRecomposedCoefficients() {
    if (this.scalingCoefficients == null)
      return; 
    if (this.waveletCoefficients == null)
      return; 
    int i = this.scalingCoefficients.length;
    this.recomposedCoefficients = new double[i * 2];
    int j = Math.max(1024, i);
    for (Integer b = 0; b < i; b++) {
      Integer b1 = b;
      int k = b1 * 2;
      this.recomposedCoefficients[k] = 0.0D;
      this.recomposedCoefficients[k + 1] = 0.0D;
      for (Integer b2 = 0; b2 < this.daubechiesScalingSequence.length / 2; b2++) {
        Integer b3 = b2;
        int m = b3 * 2;
        int n = (b1 - b3 + j) % i;
        double d1 = this.scalingCoefficients[n];
        double d2 = this.waveletCoefficients[n];
        this.recomposedCoefficients[k] = this.recomposedCoefficients[k] + this.daubechiesScalingSequence[m] * d1 + this.daubechiesWaveletSequence[m] * d2;
        this.recomposedCoefficients[k + 1] = this.recomposedCoefficients[k + 1] + this.daubechiesScalingSequence[m + 1] * d1 + this.daubechiesWaveletSequence[m + 1] * d2;
      } 
    } 
  }
  
  /**
   * 変換元のデータ系列からスケーリング係数（近似係数）とウェーブレット係数（詳細係数）を計算する。
   * このメソッドは、主に順ウェーブレット変換（DWT）を実行する。
   * 計算にはDaubechiesウェーブレットフィルタリングシーケンスを使用する。
   */
  protected void computeScalingAndWaveletCoefficients() {
    if (this.sourceCoefficients == null)
      return; 
    int i = this.sourceCoefficients.length;
    int j = i / 2;
    this.scalingCoefficients = new double[j];
    this.waveletCoefficients = new double[j];
    for (Integer b = 0; b < j; b++) {
      this.scalingCoefficients[b] = 0.0D;
      this.waveletCoefficients[b] = 0.0D;
      for (Integer b1 = 0; b1 < this.daubechiesScalingSequence.length; b1++) {
        int k = (b1 + 2 * b) % i;
        double d = this.sourceCoefficients[k];
        this.scalingCoefficients[b] = this.scalingCoefficients[b] + this.daubechiesScalingSequence[b1] * d;
        this.waveletCoefficients[b] = this.waveletCoefficients[b] + this.daubechiesWaveletSequence[b1] * d;
      } 
    } 
  }
}
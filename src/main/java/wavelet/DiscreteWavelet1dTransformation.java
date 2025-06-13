package wavelet;

/**
 * 1次元離散ウェーブレット変換（DWT）を実装するクラスである。
 * このクラスは、与えられた1次元データ（配列）に対し、
 * スケーリング係数およびウェーブレット係数への分解、ならびにそれら係数からの再構成を扱う。
 *
 * <p>離散ウェーブレット変換は、信号を異なる周波数帯域に分解することで、
 * 信号の様々なスケールにおける特徴を抽出するために用いられる。
 * 本実装は、特にDaubechiesウェーブレットに基づく変換をサポートする。</p>
 *
 * @see DiscreteWaveletTransformation
 * @see ContinuosWaveletTransformation
 */
public class DiscreteWavelet1dTransformation extends DiscreteWaveletTransformation {

  /**
   * 変換元の係数を保持するフィールドである。
   * 通常、時間領域の信号データがここに格納される。
   */
  protected double[] sourceCoefficients;

  /**
   * スケーリング係数（近似係数）を保持するフィールドである。
   * 信号の低周波成分を表す。
   */
  protected double[] scalingCoefficients;

  /**
   * ウェーブレット係数（詳細係数）を保持するフィールドである。
   * 信号の高周波成分を表す。
   */
  protected double[] waveletCoefficients;

  /**
   * 再構成された係数を保持するフィールドである。
   * スケーリング係数とウェーブレット係数から再構築された信号が格納される。
   */
  protected double[] recomposedCoefficients;

  /**
   * 変換元の係数を指定して新しい {@code DiscreteWavelet1dTransformation} インスタンスを構築する。
   * 本コンストラクタは、与えられたデータからスケーリング係数およびウェーブレット係数を計算する準備を行う。
   *
   * @param paramArrayOfdouble 変換元の信号データ（係数）
   */
  public DiscreteWavelet1dTransformation(double[] paramArrayOfdouble) {
    sourceCoefficients(paramArrayOfdouble);
  }

  /**
   * スケーリング係数およびウェーブレット係数を指定して新しい {@code DiscreteWavelet1dTransformation} インスタンスを構築する。
   * 本コンストラクタは、与えられた係数から元の信号を再構成する準備を行う。
   *
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
   * もし再構成係数がまだ計算されていない場合、本メソッドは自動的に計算を実行する。
   *
   * @return 再構成された信号データ（係数）の配列
   */
  public double[] recomposedCoefficients() {
    if (this.recomposedCoefficients == null) {
      computeRecomposedCoefficients();
    }
    return this.recomposedCoefficients;
  }

  /**
   * スケーリング係数（近似係数）を応答する。
   * もしスケーリング係数がまだ計算されていない場合、本メソッドは自動的にスケーリング係数とウェーブレット係数の両方を計算する。
   *
   * @return スケーリング係数の配列
   */
  public double[] scalingCoefficients() {
    if (this.scalingCoefficients == null) {
      computeScalingAndWaveletCoefficients();
    }
    return this.scalingCoefficients;
  }

  /**
   * スケーリング係数を設定する。
   * これにより、以前に計算された再構成係数は無効になる。
   *
   * @param paramArrayOfdouble 新しいスケーリング係数の配列
   */
  public void scalingCoefficients(double[] paramArrayOfdouble) {
    this.scalingCoefficients = paramArrayOfdouble;
    this.recomposedCoefficients = null; // 再構成係数をリセット
  }

  /**
   * 変換元の係数を応答する。
   *
   * @return 変換元の信号データ（係数）の配列
   */
  public double[] sourceCoefficients() {
    return this.sourceCoefficients;
  }

  /**
   * 変換元の係数を設定する。
   * これにより、以前に計算されたスケーリング係数、ウェーブレット係数、および再構成係数は無効になる。
   *
   * @param paramArrayOfdouble 新しい変換元の信号データ（係数）の配列
   */
  public void sourceCoefficients(double[] paramArrayOfdouble) {
    this.sourceCoefficients = paramArrayOfdouble;
    this.scalingCoefficients = null; // スケーリング係数をリセット
    this.recomposedCoefficients = null; // 再構成係数をリセット
  }

  /**
   * ウェーブレット係数（詳細係数）を応答する。
   * もしウェーブレット係数がまだ計算されていない場合、本メソッドは自動的にスケーリング係数とウェーブレット係数の両方を計算する。
   *
   * @return ウェーブレット係数の配列
   */
  public double[] waveletCoefficients() {
    if (this.waveletCoefficients == null) {
      computeScalingAndWaveletCoefficients();
    }
    return this.waveletCoefficients;
  }

  /**
   * ウェーブレット係数を設定する。
   * これにより、以前に計算された再構成係数は無効になる。
   *
   * @param paramArrayOfdouble 新しいウェーブレット係数の配列
   */
  public void waveletCoefficients(double[] paramArrayOfdouble) {
    this.waveletCoefficients = paramArrayOfdouble;
    this.recomposedCoefficients = null; // 再構成係数をリセット
  }

  /**
   * 指定されたオブジェクトに対し1次元離散ウェーブレット変換を適用する。
   * 変換元の係数を設定し、スケーリング係数とウェーブレット係数を計算する。
   *
   * @param paramObject 変換を適用する対象のオブジェクト。{@code double[]} 型である必要がある。
   * @return 本変換オブジェクト自身
   * @throws IllegalArgumentException {@code paramObject} が {@code double[]} 型でない場合
   */
  @Override
  public WaveletTransformation applyTo(Object paramObject) {
    if (!(paramObject instanceof double[])) {
      throw new IllegalArgumentException("anObject must be a double[].");
    }
    sourceCoefficients((double[])paramObject);
    // スケーリング係数を計算（必要であれば）
    scalingCoefficients();
    // ウェーブレット係数を計算（必要であれば）
    waveletCoefficients();
    return this;
  }

  /**
   * 別のウェーブレット変換オブジェクトに対し本変換を適用する。
   * 本メソッドは、指定された変換オブジェクトのソース係数、あるいは再構成係数を用いて、
   * 新しい {@code DiscreteWavelet1dTransformation} インスタンスを生成する。
   *
   * @param paramWaveletTransformation 変換を適用する対象のウェーブレット変換。
   * {@code DiscreteWavelet1dTransformation} 型である必要がある。
   * @return 新しい {@code DiscreteWavelet1dTransformation} インスタンス
   * @throws IllegalArgumentException {@code paramWaveletTransformation} が
   * {@code DiscreteWavelet1dTransformation} 型でない場合
   */
  @Override
  public WaveletTransformation transform(WaveletTransformation paramWaveletTransformation) {
    if (!(paramWaveletTransformation instanceof DiscreteWavelet1dTransformation)) {
      throw new IllegalArgumentException("waveletTransformation must be a DiscreteWavelet1dTransformation.");
    }
    DiscreteWavelet1dTransformation discreteWavelet1dTransformation = (DiscreteWavelet1dTransformation)paramWaveletTransformation;
    double[] arrayOfDouble = discreteWavelet1dTransformation.sourceCoefficients();
    if (arrayOfDouble == null) {
      arrayOfDouble = discreteWavelet1dTransformation.recomposedCoefficients();
    }
    return new DiscreteWavelet1dTransformation(arrayOfDouble);
  }

  /**
   * スケーリング係数とウェーブレット係数から元の信号を再構成する。
   * 本メソッドは、{@link #scalingCoefficients} および {@link #waveletCoefficients} が設定されている場合にのみ機能する。
   * 結果は {@link #recomposedCoefficients} に格納される。
   *
   * <p>再構成のアルゴリズムは、Daubechiesウェーブレットの再構成フィルターに基づく。</p>
   */
  protected void computeRecomposedCoefficients() {
    if (this.scalingCoefficients == null || this.waveletCoefficients == null) {
      // 必要な係数が存在しない場合は処理を中断
      return;
    }

    int i = this.scalingCoefficients.length;
    this.recomposedCoefficients = new double[i * 2]; // 再構成後のデータサイズは元の2倍
    int j = Math.max(1024, i); // 周期的畳み込みのためのオフセット計算に使用される可能性のある値

    // 各スケーリング係数とウェーブレット係数のペアから2つの元の係数を再構成
    for (byte b = 0; b < i; b++) {
      int k = b * 2; // 再構成された係数配列での現在の書き込み位置
      this.recomposedCoefficients[k] = 0.0D;
      this.recomposedCoefficients[k + 1] = 0.0D;

      // Daubechies再構成フィルターを用いて畳み込みを計算
      for (byte b2 = 0; b2 < this.daubechiesScalingSequence.length / 2; b2++) {
        // フィルター係数のインデックス
        int m = b2 * 2;
        // 周期的境界条件を考慮した係数のインデックス
        int n = (b - b2 + j) % i;
        
        // 現在のスケーリング係数
        double d1 = this.scalingCoefficients[n];
        // 現在のウェーブレット係数
        double d2 = this.waveletCoefficients[n];

        // 再構成された係数の計算
        this.recomposedCoefficients[k] = this.recomposedCoefficients[k] + this.daubechiesScalingSequence[m] * d1 + this.daubechiesWaveletSequence[m] * d2;
        this.recomposedCoefficients[k + 1] = this.recomposedCoefficients[k + 1] + this.daubechiesScalingSequence[m + 1] * d1 + this.daubechiesWaveletSequence[m + 1] * d2;
      }
    }
  }

  /**
   * 変換元の信号データからスケーリング係数とウェーブレット係数を計算する。
   * 本メソッドは、{@link #sourceCoefficients} が設定されている場合にのみ機能する。
   * 結果は {@link #scalingCoefficients} および {@link #waveletCoefficients} に格納される。
   *
   * <p>分解のアルゴリズムは、Daubechiesウェーブレットの分解フィルターに基づく。</p>
   */
  protected void computeScalingAndWaveletCoefficients() {
    if (this.sourceCoefficients == null) {
      // 変換元の係数が存在しない場合は処理を中断
      return;
    }

    // 変換元のデータ長
    int i = this.sourceCoefficients.length;
    // 分解後の係数配列の長さ（元の半分）
    int j = i / 2;

    // スケーリング係数配列を初期化
    this.scalingCoefficients = new double[j];
    // ウェーブレット係数配列を初期化
    this.waveletCoefficients = new double[j];

    // 各スケーリング係数とウェーブレット係数を計算
    for (byte b = 0; b < j; b++) {
      this.scalingCoefficients[b] = 0.0D;
      this.waveletCoefficients[b] = 0.0D;

      // Daubechies分解フィルターを用いて畳み込みを計算
      for (byte b1 = 0; b1 < this.daubechiesScalingSequence.length; b1++) {
        // 周期的境界条件を考慮した変換元の係数のインデックス
        int k = (b1 + 2 * b) % i;
        // 現在の変換元の係数
        double d = this.sourceCoefficients[k];

        // スケーリング係数とウェーブレット係数の計算
        this.scalingCoefficients[b] = this.scalingCoefficients[b] + this.daubechiesScalingSequence[b1] * d;
        this.waveletCoefficients[b] = this.waveletCoefficients[b] + this.daubechiesWaveletSequence[b1] * d;
      }
    }
  }
}
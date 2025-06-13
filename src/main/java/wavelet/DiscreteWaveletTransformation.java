package wavelet;

/**
 * 離散ウェーブレット変換（DWT）の抽象基底クラスである。
 * このクラスは、1次元または2次元の離散ウェーブレット変換を行うための共通の構造と、
 * Daubechiesウェーブレットの基底フィルター係数を提供する。
 *
 * <p>具体的な変換（例えば1次元または2次元）は、この抽象クラスを継承するサブクラスで実装される。</p>
 *
 * @see DiscreteWavelet1dTransformation
 * @see DiscreteWavelet2dTransformation
 */
public abstract class DiscreteWaveletTransformation extends WaveletTransformation {
  /**
   * Daubechiesスケーリング（近似）フィルターの係数シーケンスを保持する。
   * これらの係数は、信号の低周波成分を抽出するために使用される。
   */
  protected double[] daubechiesScalingSequence;

  /**
   * Daubechiesウェーブレット（詳細）フィルターの係数シーケンスを保持する。
   * これらの係数は、信号の高周波成分を抽出するために使用される。
   * スケーリング係数から導出される。
   */
  protected double[] daubechiesWaveletSequence;

  /**
   * この変換オブジェクトの内部状態を初期化する。
   * デフォルトのDaubechiesウェーブレットの次数（N=2、つまり4タップフィルター）を用いてフィルター係数を設定する。
   */
  protected void initialize() {
    initialize(2); // デフォルトで次数N=2のDaubechiesウェーブレットを初期化する
  }

  /**
   * 指定された次数に基づいて、Daubechiesウェーブレットのフィルター係数を初期化する。
   *
   * <p>サポートされる次数は以下の通りである。
   * <ul>
   * <li>{@code paramInt == 2}: 4タップフィルター係数（デフォルト）</li>
   * <li>{@code paramInt == 3}: 6タップフィルター係数</li>
   * <li>{@code paramInt == 4}: 8タップフィルター係数</li>
   * </ul>
   * 指定された次数に応じて、{@link #daubechiesScalingSequence} と {@link #daubechiesWaveletSequence} を設定する。
   * ウェーブレット係数はスケーリング係数から導出される。</p>
   *
   * @param paramInt 初期化するDaubechiesウェーブレットの次数（通常、Nである）
   */
  protected void initialize(int paramInt) {
    super.initialize(); // 親クラスの初期化メソッドを呼び出す

    // 指定された次数に基づいてDaubechiesスケーリングシーケンスを設定する
    this.daubechiesScalingSequence = new double[] { 0.4829629131445341D, 0.8365163037378077D, 0.2241438680420134D, -0.1294095225512603D }; // N=2 (4タップ)
    if (paramInt == 3) {
      this.daubechiesScalingSequence = new double[] { 0.3326705529500825D, 0.8068915093110924D, 0.4598775021184914D, -0.1350110200102546D, -0.0854412738820267D, 0.0352262918857095D }; // N=3 (6タップ)
    }
    if (paramInt == 4) {
      this.daubechiesScalingSequence = new double[] { 0.2303778133088964D, 0.7148465705529155D, 0.6308807679298599D, -0.0279837694168599D, -0.1870348117190931D, 0.0308413818355607D, 0.0328830116668852D, -0.010597401785069D }; // N=4 (8タップ)
    }

    // スケーリングシーケンスの長さに基づいてウェーブレットシーケンスを生成する
    int i = this.daubechiesScalingSequence.length;
    this.daubechiesWaveletSequence = new double[i];
    for (byte b = 0; b < i; b++) {
      // ウェーブレット係数は、スケーリング係数を反転させ、交互に符号を反転させることで導出される
      this.daubechiesWaveletSequence[b] = Math.pow(-1.0D, b) * this.daubechiesScalingSequence[i - 1 - b];
    }
  }
}
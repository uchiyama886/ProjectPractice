package wavelet;

/**
 * 2次元離散ウェーブレット変換（2D DWT）を実装するクラスである。
 * このクラスは、画像データなどの2次元データに対して、
 * スケーリング係数および3方向（水平、垂直、対角）のウェーブレット係数への分解、
 * ならびにそれら係数からの再構成を扱う。
 *
 * <p>2次元離散ウェーブレット変換は、画像圧縮、ノイズ除去、特徴抽出など、
 * 多くの画像処理アプリケーションで利用される強力なツールである。
 * 信号を異なる周波数帯域に分解し、画像のマルチスケールな特徴を捉えることが可能である。
 * 本実装は、Daubechiesウェーブレットに基づく変換をサポートする。</p>
 *
 * @see DiscreteWaveletTransformation
 * @see DiscreteWavelet1dTransformation
 */
public class DiscreteWavelet2dTransformation extends DiscreteWaveletTransformation {

  /**
   * 変換元の2次元係数を保持するフィールドである。
   * 通常、入力画像データがここに格納される。
   */
  protected double[][] sourceCoefficients;

  /**
   * スケーリング係数（近似係数）を保持するフィールドである。
   * 信号の低周波成分、すなわち画像の平滑な部分を表す。
   */
  protected double[][] scalingCoefficients;

  /**
   * ウェーブレット係数（詳細係数）を保持するフィールドである。
   * 0: 水平方向、1: 垂直方向、2: 対角方向の3つの詳細成分を格納する。
   * 画像の高周波成分、すなわちエッジやテクスチャの情報を表す。
   */
  protected double[][][] waveletCoefficients;

  /**
   * 再構成された2次元係数を保持するフィールドである。
   * スケーリング係数とウェーブレット係数から再構築された画像データが格納される。
   */
  protected double[][] recomposedCoefficients;

  /**
   * 変換元の2次元係数を指定して新しい {@code DiscreteWavelet2dTransformation} インスタンスを構築する。
   * 本コンストラクタは、与えられたデータからスケーリング係数およびウェーブレット係数を計算する準備を行う。
   *
   * @param paramArrayOfdouble 変換元の2次元信号データ（係数）
   */
  public DiscreteWavelet2dTransformation(double[][] paramArrayOfdouble) {
    sourceCoefficients(paramArrayOfdouble);
  }

  /**
   * スケーリング係数およびウェーブレット係数を指定して新しい {@code DiscreteWavelet2dTransformation} インスタンスを構築する。
   * 本コンストラクタは、与えられた係数から元の信号を再構成する準備を行う。
   *
   * @param paramArrayOfdouble スケーリング係数（近似成分）の2次元配列
   * @param paramArrayOfdouble1 ウェーブレット係数（詳細成分）の3次元配列（水平、垂直、対角の順）
   */
  public DiscreteWavelet2dTransformation(double[][] paramArrayOfdouble, double[][][] paramArrayOfdouble1) {
    scalingCoefficients(paramArrayOfdouble);
    waveletCoefficients(paramArrayOfdouble1);
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
   * 再構成された2次元係数を応答する。
   * もし再構成係数がまだ計算されていない場合、本メソッドは自動的に計算を実行する。
   *
   * @return 再構成された信号データ（係数）の2次元配列
   */
  public double[][] recomposedCoefficients() {
    if (this.recomposedCoefficients == null) {
      computeRecomposedCoefficients();
    }
    return this.recomposedCoefficients;
  }

  /**
   * スケーリング係数（近似係数）を応答する。
   * もしスケーリング係数がまだ計算されていない場合、本メソッドは自動的にスケーリング係数とウェーブレット係数の両方を計算する。
   *
   * @return スケーリング係数の2次元配列
   */
  public double[][] scalingCoefficients() {
    if (this.scalingCoefficients == null) {
      computeScalingAndWaveletCoefficients();
    }
    return this.scalingCoefficients;
  }

  /**
   * スケーリング係数を設定する。
   * これにより、以前に計算された再構成係数は無効になる。
   *
   * @param paramArrayOfdouble 新しいスケーリング係数の2次元配列
   */
  public void scalingCoefficients(double[][] paramArrayOfdouble) {
    this.scalingCoefficients = paramArrayOfdouble;
    this.recomposedCoefficients = null; // 再構成係数をリセット
  }

  /**
   * 変換元の2次元係数を応答する。
   *
   * @return 変換元の信号データ（係数）の2次元配列
   */
  public double[][] sourceCoefficients() {
    return this.sourceCoefficients;
  }

  /**
   * 変換元の2次元係数を設定する。
   * これにより、以前に計算されたスケーリング係数、ウェーブレット係数、および再構成係数は無効になる。
   *
   * @param paramArrayOfdouble 新しい変換元の信号データ（係数）の2次元配列
   */
  public void sourceCoefficients(double[][] paramArrayOfdouble) {
    this.sourceCoefficients = paramArrayOfdouble;
    this.scalingCoefficients = null; // スケーリング係数をリセット
    this.recomposedCoefficients = null; // 再構成係数をリセット
  }

  /**
   * ウェーブレット係数（詳細係数）を応答する。
   * もしウェーブレット係数がまだ計算されていない場合、本メソッドは自動的にスケーリング係数とウェーブレット係数の両方を計算する。
   *
   * @return ウェーブレット係数の3次元配列（水平、垂直、対角の順）
   */
  public double[][][] waveletCoefficients() {
    if (this.waveletCoefficients == null) {
      computeScalingAndWaveletCoefficients();
    }
    return this.waveletCoefficients;
  }

  /**
   * ウェーブレット係数を設定する。
   * これにより、以前に計算された再構成係数は無効になる。
   *
   * @param paramArrayOfdouble 新しいウェーブレット係数の3次元配列
   */
  public void waveletCoefficients(double[][][] paramArrayOfdouble) {
    this.waveletCoefficients = paramArrayOfdouble;
    this.recomposedCoefficients = null; // 再構成係数をリセット
  }

  /**
   * 対角方向のウェーブレット係数を応答する。
   * これは {@link #waveletCoefficients()} の3番目の要素（インデックス2）である。
   *
   * @return 対角方向のウェーブレット係数の2次元配列
   */
  public double[][] diagonalWaveletCoefficients() {
    return waveletCoefficients()[2];
  }

  /**
   * 水平方向のウェーブレット係数を応答する。
   * これは {@link #waveletCoefficients()} の1番目の要素（インデックス0）である。
   *
   * @return 水平方向のウェーブレット係数の2次元配列
   */
  public double[][] horizontalWaveletCoefficients() {
    return waveletCoefficients()[0];
  }

  /**
   * 垂直方向のウェーブレット係数を応答する。
   * これは {@link #waveletCoefficients()} の2番目の要素（インデックス1）である。
   *
   * @return 垂直方向のウェーブレット係数の2次元配列
   */
  public double[][] verticalWaveletCoefficients() {
    return waveletCoefficients()[1];
  }

  /**
   * 指定されたオブジェクトに対し2次元離散ウェーブレット変換を適用する。
   * 変換元の係数を設定し、スケーリング係数およびウェーブレット係数を計算する。
   *
   * @param paramObject 変換を適用する対象のオブジェクト。{@code double[][]} 型である必要がある。
   * @return 本変換オブジェクト自身
   * @throws IllegalArgumentException {@code paramObject} が {@code double[][]} 型でない場合
   */
  @Override
  public WaveletTransformation applyTo(Object paramObject) {
    if (!(paramObject instanceof double[][])) {
      throw new IllegalArgumentException("anObject must be a double[][].");
    }
    sourceCoefficients((double[][])paramObject);
    scalingCoefficients(); // スケーリング係数を計算（必要であれば）
    waveletCoefficients(); // ウェーブレット係数を計算（必要であれば）
    return this;
  }

  /**
   * 別のウェーブレット変換オブジェクトに対し本変換を適用する。
   * 本メソッドは、指定された変換オブジェクトのソース係数、あるいは再構成係数を用いて、
   * 新しい {@code DiscreteWavelet2dTransformation} インスタンスを生成する。
   *
   * @param paramWaveletTransformation 変換を適用する対象のウェーブレット変換。
   * {@code DiscreteWavelet2dTransformation} 型である必要がある。
   * @return 新しい {@code DiscreteWavelet2dTransformation} インスタンス
   * @throws IllegalArgumentException {@code paramWaveletTransformation} が
   * {@code DiscreteWavelet2dTransformation} 型でない場合
   */
  @Override
  public WaveletTransformation transform(WaveletTransformation paramWaveletTransformation) {
    if (!(paramWaveletTransformation instanceof DiscreteWavelet2dTransformation)) {
      throw new IllegalArgumentException("waveletTransformation must be a DiscreteWavelet2dTransformation.");
    }
    DiscreteWavelet2dTransformation discreteWavelet2dTransformation = (DiscreteWavelet2dTransformation)paramWaveletTransformation;
    double[][] arrayOfDouble = discreteWavelet2dTransformation.sourceCoefficients();
    if (arrayOfDouble == null) {
      arrayOfDouble = discreteWavelet2dTransformation.recomposedCoefficients();
    }
    return new DiscreteWavelet2dTransformation(arrayOfDouble);
  }

  /**
   * スケーリング係数とウェーブレット係数から元の2次元信号を再構成する。
   * 本メソッドは、{@link #scalingCoefficients} および {@link #waveletCoefficients} が設定されている場合にのみ機能する。
   * 結果は {@link #recomposedCoefficients} に格納される。
   *
   * <p>再構成は、行方向と列方向の1次元ウェーブレット変換の再構成処理を組み合わせることで行われる。</p>
   */
  protected void computeRecomposedCoefficients() {
    if (this.scalingCoefficients == null || this.waveletCoefficients == null) {
      // 必要な係数が存在しない場合は処理を中断
      return;
    }

    int i = rowSize(this.scalingCoefficients); // スケーリング係数の行数
    int j = i * 2; // 再構成後の行数 (元の2倍)
    int k = columnSize(this.scalingCoefficients); // スケーリング係数の列数
    int m = k * 2; // 再構成後の列数 (元の2倍)

    // 各係数行列を転置し、行ごとに1次元再構成を行う準備
    double[][] arrayOfDouble1 = transpose(this.scalingCoefficients);
    double[][] arrayOfDouble2 = transpose(horizontalWaveletCoefficients());
    double[][] arrayOfDouble3 = transpose(verticalWaveletCoefficients());
    double[][] arrayOfDouble4 = transpose(diagonalWaveletCoefficients());

    double[][] arrayOfDouble5 = new double[k][j]; // 転置後のスケーリング係数と水平ウェーブレット係数からの再構成結果
    double[][] arrayOfDouble6 = new double[k][j]; // 転置後の垂直ウェーブレット係数と対角ウェーブレット係数からの再構成結果

    // 行ごとに1次元ウェーブレット再構成を適用 (最初のステージ: 列方向の再構成に相当)
    for (byte b1 = 0; b1 < k; b1++) {
      // スケーリング係数と水平ウェーブレット係数から再構成
      DiscreteWavelet1dTransformation discreteWavelet1dTransformation = new DiscreteWavelet1dTransformation(atRow(arrayOfDouble1, b1), atRow(arrayOfDouble2, b1));
      atRowPut(arrayOfDouble5, b1, discreteWavelet1dTransformation.recomposedCoefficients());

      // 垂直ウェーブレット係数と対角ウェーブレット係数から再構成
      discreteWavelet1dTransformation = new DiscreteWavelet1dTransformation(atRow(arrayOfDouble3, b1), atRow(arrayOfDouble4, b1));
      atRowPut(arrayOfDouble6, b1, discreteWavelet1dTransformation.recomposedCoefficients());
    }

    // 再構成結果を再度転置し、次のステージ（行方向の再構成）の準備
    arrayOfDouble5 = transpose(arrayOfDouble5);
    arrayOfDouble6 = transpose(arrayOfDouble6);

    double[][] arrayOfDouble7 = new double[j][m]; // 最終的な再構成結果

    // 行ごとに1次元ウェーブレット再構成を適用 (第二のステージ: 行方向の再構成に相当)
    for (byte b2 = 0; b2 < j; b2++) {
      DiscreteWavelet1dTransformation discreteWavelet1dTransformation = new DiscreteWavelet1dTransformation(atRow(arrayOfDouble5, b2), atRow(arrayOfDouble6, b2));
      atRowPut(arrayOfDouble7, b2, discreteWavelet1dTransformation.recomposedCoefficients());
    }
    this.recomposedCoefficients = arrayOfDouble7;
  }

  /**
   * 変換元の2次元信号データからスケーリング係数とウェーブレット係数を計算する。
   * 本メソッドは、{@link #sourceCoefficients} が設定されている場合にのみ機能する。
   * 結果は {@link #scalingCoefficients} および {@link #waveletCoefficients} に格納される。
   *
   * <p>分解は、行方向と列方向の1次元ウェーブレット変換の分解処理を組み合わせることで行われる。</p>
   */
  protected void computeScalingAndWaveletCoefficients() {
    if (this.sourceCoefficients == null) {
      // 変換元の係数が存在しない場合は処理を中断
      return;
    }

    int i = rowSize(this.sourceCoefficients); // 変換元の行数
    int j = i / 2; // 分解後の行数 (元の半分)
    int k = columnSize(this.sourceCoefficients); // 変換元の列数
    int m = k / 2; // 分解後の列数 (元の半分)

    // 行ごとに1次元ウェーブレット分解を適用 (最初のステージ: 行方向の分解)
    double[][] arrayOfDouble1 = new double[i][m]; // 各行のスケーリング係数を格納
    double[][] arrayOfDouble2 = new double[i][m]; // 各行のウェーブレット係数を格納
    for (byte b1 = 0; b1 < i; b1++) {
      DiscreteWavelet1dTransformation discreteWavelet1dTransformation = new DiscreteWavelet1dTransformation(atRow(this.sourceCoefficients, b1));
      atRowPut(arrayOfDouble1, b1, discreteWavelet1dTransformation.scalingCoefficients()); // スケーリング成分
      atRowPut(arrayOfDouble2, b1, discreteWavelet1dTransformation.waveletCoefficients()); // ウェーブレット成分
    }

    // 分解結果を転置し、次のステージ（列方向の分解）の準備
    arrayOfDouble1 = transpose(arrayOfDouble1);
    arrayOfDouble2 = transpose(arrayOfDouble2);

    double[][] arrayOfDouble3 = new double[m][j]; // HH (Scaling-Scaling)成分
    double[][] arrayOfDouble4 = new double[m][j]; // HL (Scaling-Wavelet)成分
    double[][] arrayOfDouble5 = new double[m][j]; // LH (Wavelet-Scaling)成分
    double[][] arrayOfDouble6 = new double[m][j]; // LL (Wavelet-Wavelet)成分

    // 行ごとに1次元ウェーブレット分解を適用 (第二のステージ: 列方向の分解)
    for (byte b2 = 0; b2 < m; b2++) {
      // HH成分とHL成分の計算
      DiscreteWavelet1dTransformation discreteWavelet1dTransformation = new DiscreteWavelet1dTransformation(atRow(arrayOfDouble1, b2));
      atRowPut(arrayOfDouble3, b2, discreteWavelet1dTransformation.scalingCoefficients()); // HH成分
      atRowPut(arrayOfDouble4, b2, discreteWavelet1dTransformation.waveletCoefficients()); // HL成分

      // LH成分とLL成分の計算
      discreteWavelet1dTransformation = new DiscreteWavelet1dTransformation(atRow(arrayOfDouble2, b2));
      atRowPut(arrayOfDouble5, b2, discreteWavelet1dTransformation.scalingCoefficients()); // LH成分
      atRowPut(arrayOfDouble6, b2, discreteWavelet1dTransformation.waveletCoefficients()); // LL成分
    }

    // 最終的な係数行列を元の向きに転置
    arrayOfDouble3 = transpose(arrayOfDouble3); // スケーリング係数
    arrayOfDouble4 = transpose(arrayOfDouble4); // 水平ウェーブレット係数
    arrayOfDouble5 = transpose(arrayOfDouble5); // 垂直ウェーブレット係数
    arrayOfDouble6 = transpose(arrayOfDouble6); // 対角ウェーブレット係数

    this.scalingCoefficients = arrayOfDouble3;
    // waveletCoefficients[0]: 水平 (HL), [1]: 垂直 (LH), [2]: 対角 (LL)
    this.waveletCoefficients = new double[][][] { arrayOfDouble4, arrayOfDouble5, arrayOfDouble6 };
  }

  /**
   * 指定された2次元配列の特定の行を応答する。
   *
   * @param paramArrayOfdouble 2次元配列
   * @param paramInt 取得する行のインデックス
   * @return 指定された行の1次元配列
   */
  private double[] atRow(double[][] paramArrayOfdouble, int paramInt) {
    return paramArrayOfdouble[paramInt];
  }

  /**
   * 指定された2次元配列の特定の行に1次元配列の値を設定する。
   *
   * @param paramArrayOfdouble 設定先の2次元配列
   * @param paramInt 設定する行のインデックス
   * @param paramArrayOfdouble1 設定する値の1次元配列
   */
  private void atRowPut(double[][] paramArrayOfdouble, int paramInt, double[] paramArrayOfdouble1) {
    for (byte b = 0; b < (paramArrayOfdouble[paramInt]).length; b++) {
      paramArrayOfdouble[paramInt][b] = paramArrayOfdouble1[b];
    }
  }

  /**
   * 指定された2次元配列の列サイズ（列数）を応答する。
   * 配列が空でないことを前提とする。
   *
   * @param paramArrayOfdouble サイズを取得する2次元配列
   * @return 2次元配列の列数
   */
  private int columnSize(double[][] paramArrayOfdouble) {
    return (paramArrayOfdouble[0]).length;
  }

  /**
   * 指定された2次元配列の行サイズ（行数）を応答する。
   *
   * @param paramArrayOfdouble サイズを取得する2次元配列
   * @return 2次元配列の行数
   */
  private int rowSize(double[][] paramArrayOfdouble) {
    return paramArrayOfdouble.length;
  }

  /**
   * 指定された2次元配列を転置した新しい配列を応答する。
   * 元の配列の行と列が入れ替わる。
   *
   * @param paramArrayOfdouble 転置する2次元配列
   * @return 転置された新しい2次元配列
   */
  private double[][] transpose(double[][] paramArrayOfdouble) {
    int i = rowSize(paramArrayOfdouble);
    int j = columnSize(paramArrayOfdouble);
    double[][] arrayOfDouble = new double[j][i];
    for (byte b = 0; b < paramArrayOfdouble.length; b++) {
      for (byte b1 = 0; b1 < (paramArrayOfdouble[b]).length; b1++) {
        arrayOfDouble[b1][b] = paramArrayOfdouble[b][b1];
      }
    }
    return arrayOfDouble;
  }
}
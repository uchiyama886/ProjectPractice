package wavelet;

import java.util.Arrays;

/**
 * 1次元離散ウェーブレット変換（DWT）を実装するクラス。 このクラスは、与えられた1次元のデータ（配列）に対して、
 * スケーリング係数とウェーブレット係数への分解、およびそれらの係数からの再構成を扱う。
 *
 * <p>
 * 離散ウェーブレット変換は、信号を異なる周波数帯域に分解することで、 信号の様々なスケールにおける特徴を抽出するために用いられる。
 * この実装は、特にDaubechiesウェーブレットに基づく変換をサポートしている。</p>
 *
 * @see DiscreteWaveletTransformation
 * @see ContinuosWaveletTransformation
 */
public final class DiscreteWavelet1dTransformation extends DiscreteWaveletTransformation 
{
    /**
     * 元の入力データ。
     */
    protected double[] sourceCoefficients;

    /**
     * DWTにより得られたスケーリング係数。
     */
    protected double[] scalingCoefficients;

    /**
     * DWTにより得られたウェーブレット係数。
     */
    protected double[] waveletCoefficients;

    /**
     * スケーリング・ウェーブレット係数から再構成された信号。
     */
    protected double[] recomposedCoefficients;

    /**
     * 入力の元の長さ。
     */
    private int originalLength;

    /**
     * 入力がゼロパディングされたかどうかのフラグ。
     */
    private boolean isPadded;

    /**
     * 元信号から離散ウェーブレット変換を行うコンストラクタ。
     * @param sourceCollection 入力信号の配列
     */
    public DiscreteWavelet1dTransformation(double[] sourceCollection) 
    {
        this.initialize();
        this.originalLength = sourceCollection.length;
        int nextPowerOfTwo = nextPowerOfTwo(this.originalLength);

        // 入力された画像の幅・高さが2の冪乗ならそのまま使う
        if (this.originalLength != nextPowerOfTwo) 
        {
            this.sourceCoefficients = Arrays.copyOf(sourceCollection, nextPowerOfTwo);
            this.isPadded = true;
        } else { // 冪乗でないならゼロパディングを行ってリサイズする
            this.sourceCoefficients = sourceCollection;
            this.isPadded = false;
        }
    }

    /**
     * スケーリング係数とウェーブレット係数を指定して復元用のインスタンスを作成するコンストラクタ。
     * @param scalingCollection スケーリング係数
     * @param waveletCollection ウェーブレット係数
     */
    public DiscreteWavelet1dTransformation(double[] scalingCollection, double[] waveletCollection) 
    {
        // このコンストラクタもパディングロジックを考慮する必要があるかもしれません
        // ここでは簡略化のため、scalingCollection の長さを基準にしますが、
        // 実際には両方の配列の長さを確認し、揃える必要があります。
        // 簡単な例として、scalingCoefficients の長さに合わせて処理します。
        this.initialize();
        this.originalLength = scalingCollection.length * 2; // 再構成後の長さを仮定
        int nextPowerOfTwo = nextPowerOfTwo(this.originalLength);

        if (this.originalLength != nextPowerOfTwo) 
        {
            // scalingCoefficients と waveletCoefficients の両方をパディングして扱う必要がある
            // これは少し複雑になるため、ここでは scalingCoefficients の長さを基準に簡略化
            this.scalingCoefficients = Arrays.copyOf(scalingCollection, nextPowerOfTwo / 2);
            this.waveletCoefficients = Arrays.copyOf(waveletCollection, nextPowerOfTwo / 2);
            this.isPadded = true;
        } else 
        {
            this.scalingCoefficients = scalingCollection;
            this.waveletCoefficients = waveletCollection;
            this.isPadded = false;
        }
    }

    /**
     * フィールドを初期状態にリセットする初期化メソッド。
     */
    @Override
    protected void initialize() {
        super.initialize();
        this.sourceCoefficients = null;
        this.scalingCoefficients = null;
        this.waveletCoefficients = null;
        this.recomposedCoefficients = null;
        this.originalLength = 0;
        this.isPadded = false;
    }

    /**
     * 再構成された係数(元信号)を返す。
     * 未計算の場合は計算する。
     * @return 再構成された信号配列
     */
    public double[] recomposedCoefficients() {
        if (this.recomposedCoefficients == null) {
            computeRecomposedCoefficients();
        }
        if (this.isPadded) { // 逆変換後に切り詰める
          return Arrays.copyOf(this.recomposedCoefficients, this.originalLength);
        }        
        return this.recomposedCoefficients;
    }

    public double[] scalingCoefficients() {
        if (this.scalingCoefficients == null) {
            computeScalingAndWaveletCoefficients();
        }
        return this.scalingCoefficients;
    }

    public void scalingCoefficients(double[] scalingCollection) {
        this.scalingCoefficients = scalingCollection;
        this.recomposedCoefficients = null;
    }

    public double[] sourceCoefficients() {
        return this.sourceCoefficients;
    }

    public void sourceCoefficients(double[] valueCollction) {
        this.originalLength = valueCollction.length;
        int nextPowerOfTwo = nextPowerOfTwo(this.originalLength);

        if (this.originalLength != nextPowerOfTwo) {
            this.sourceCoefficients = Arrays.copyOf(valueCollction, nextPowerOfTwo);
            this.isPadded = true;
        } else {
            this.sourceCoefficients = valueCollction;
            this.isPadded = false;
        }
        this.scalingCoefficients = null;
        this.recomposedCoefficients = null;
    }


    public double[] waveletCoefficients() {
        if (this.waveletCoefficients == null) {
            computeScalingAndWaveletCoefficients();
        }
        return this.waveletCoefficients;
    }

    public void waveletCoefficients(double[] waveletCollection) {
        this.waveletCoefficients = waveletCollection;
        this.recomposedCoefficients = null;
    }

    public WaveletTransformation applyTo(Object anObject) {
        if (!(anObject instanceof double[])) {
            throw new IllegalArgumentException("anObject must be a double[].");
        }
        sourceCoefficients((double[]) anObject);
        scalingCoefficients();
        waveletCoefficients();
        return this;
    }

    public WaveletTransformation transform(WaveletTransformation waveletTransformation) {
        if (!(waveletTransformation instanceof DiscreteWavelet1dTransformation)) {
            throw new IllegalArgumentException("waveletTransformation must be a DiscreteWavelet1dTransformation.");
        }
        DiscreteWavelet1dTransformation discreteWavelet1dTransformation = (DiscreteWavelet1dTransformation) waveletTransformation;
        double[] arrayOfDouble = discreteWavelet1dTransformation.sourceCoefficients();
        if (arrayOfDouble == null) {
            arrayOfDouble = discreteWavelet1dTransformation.recomposedCoefficients();
        }
        return new DiscreteWavelet1dTransformation(arrayOfDouble);
    }

    protected void computeRecomposedCoefficients() {
        if (this.scalingCoefficients == null) {
            return;
        }
        if (this.waveletCoefficients == null) {
            return;
        }
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

    protected void computeScalingAndWaveletCoefficients() {
        if (this.sourceCoefficients == null) {
            return;
        }
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

    private static int nextPowerOfTwo(int value) {
      Integer n = 1;
      while (n < value) {
        n <<= 1;
      }
      return n;
    }
}

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
public final class DiscreteWavelet1dTransformation extends DiscreteWaveletTransformation {

    protected double[] sourceCoefficients;

    protected double[] scalingCoefficients;

    protected double[] waveletCoefficients;

    protected double[] recomposedCoefficients;

    private int originalLength; // 元のデータ長を保持する

    private boolean isPadded; //パディングされたかどうかを示すフラグ

    public DiscreteWavelet1dTransformation(double[] paramArrayOfdouble) {
        this.initialize();
        this.originalLength = paramArrayOfdouble.length;
        int nextPowerOfTwo = nextPowerOfTwo(this.originalLength);

        // 入力された画像の幅・高さが2の冪乗ならそのまま使う
        if (this.originalLength != nextPowerOfTwo) {
            this.sourceCoefficients = Arrays.copyOf(paramArrayOfdouble, nextPowerOfTwo);
            this.isPadded = true;
        } else { // 冪乗でないならゼロパディングを行ってリサイズする
            this.sourceCoefficients = paramArrayOfdouble;
            this.isPadded = false;
        }
    }

    public DiscreteWavelet1dTransformation(double[] paramArrayOfdouble, double[] paramArrayOfdouble1) {
        // このコンストラクタもパディングロジックを考慮する必要があるかもしれません
        // ここでは簡略化のため、paramArrayOfdouble の長さを基準にしますが、
        // 実際には両方の配列の長さを確認し、揃える必要があります。
        // 簡単な例として、scalingCoefficients の長さに合わせて処理します。
        this.initialize();
        this.originalLength = paramArrayOfdouble.length * 2; // 再構成後の長さを仮定
        int nextPowerOfTwo = nextPowerOfTwo(this.originalLength);

        if (this.originalLength != nextPowerOfTwo) {
            // scalingCoefficients と waveletCoefficients の両方をパディングして扱う必要がある
            // これは少し複雑になるため、ここでは scalingCoefficients の長さを基準に簡略化
            this.scalingCoefficients = Arrays.copyOf(paramArrayOfdouble, nextPowerOfTwo / 2);
            this.waveletCoefficients = Arrays.copyOf(paramArrayOfdouble1, nextPowerOfTwo / 2);
            this.isPadded = true;
        } else {
            this.scalingCoefficients = paramArrayOfdouble;
            this.waveletCoefficients = paramArrayOfdouble1;
            this.isPadded = false;
        }
    }

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

    public void scalingCoefficients(double[] paramArrayOfdouble) {
        this.scalingCoefficients = paramArrayOfdouble;
        this.recomposedCoefficients = null;
    }

    public double[] sourceCoefficients() {
        return this.sourceCoefficients;
    }

    public void sourceCoefficients(double[] paramArrayOfdouble) {
        this.originalLength = paramArrayOfdouble.length;
        int nextPowerOfTwo = nextPowerOfTwo(this.originalLength);

        if (this.originalLength != nextPowerOfTwo) {
            this.sourceCoefficients = Arrays.copyOf(paramArrayOfdouble, nextPowerOfTwo);
            this.isPadded = true;
        } else {
            this.sourceCoefficients = paramArrayOfdouble;
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

    public void waveletCoefficients(double[] paramArrayOfdouble) {
        this.waveletCoefficients = paramArrayOfdouble;
        this.recomposedCoefficients = null;
    }

    public WaveletTransformation applyTo(Object paramObject) {
        if (!(paramObject instanceof double[])) {
            throw new IllegalArgumentException("anObject must be a double[].");
        }
        sourceCoefficients((double[]) paramObject);
        scalingCoefficients();
        waveletCoefficients();
        return this;
    }

    public WaveletTransformation transform(WaveletTransformation paramWaveletTransformation) {
        if (!(paramWaveletTransformation instanceof DiscreteWavelet1dTransformation)) {
            throw new IllegalArgumentException("waveletTransformation must be a DiscreteWavelet1dTransformation.");
        }
        DiscreteWavelet1dTransformation discreteWavelet1dTransformation = (DiscreteWavelet1dTransformation) paramWaveletTransformation;
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

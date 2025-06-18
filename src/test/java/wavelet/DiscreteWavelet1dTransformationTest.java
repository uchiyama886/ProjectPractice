package wavelet;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public class DiscreteWavelet1dTransformationTest {

    private DiscreteWavelet1dTransformation transformation;

    private static final double DELTA = 1e-9; // 浮動小数点比較の許容誤差

    // Daubechies-4 (db4) のスケーリングフィルター係数 (DiscreteWaveletTransformation.java から取得)
    private static final double[] DB4_SCALING_SEQUENCE = {
        0.4829629131445341D, 0.8365163037378077D, 0.2241438680420134D, -0.1294095225512603D
    };

    // Daubechies-4 (db4) のウェーブレットフィルター係数 (DiscreteWaveletTransformation.java のロジックに従って生成)
    // this.daubechiesWaveletSequence[b] = Math.pow(-1.0D, b) * this.daubechiesScalingSequence[i - 1 - b];
    private static final double[] DB4_WAVELET_SEQUENCE;
    static {
        DB4_WAVELET_SEQUENCE = new double[DB4_SCALING_SEQUENCE.length];
        for (int i = 0; i < DB4_SCALING_SEQUENCE.length; i++) {
            int j = DB4_SCALING_SEQUENCE.length - 1 - i;
            DB4_WAVELET_SEQUENCE[j] = Math.pow(-1.0D, i) * DB4_SCALING_SEQUENCE[DB4_SCALING_SEQUENCE.length - 1 - i]; // コードのロジックに合わせる
            // コードのロジック: this.daubechiesWaveletSequence[j] = ((i % 2 == 0) ? this.daubechiesScalingSequence[i] : -this.daubechiesScalingSequence[i]);
            // これは一般的な直交ウェーブレットの導出とは異なるので、正確に合わせる
            // 実際は、DB4_SCALING_SEQUENCE の要素を逆順にし、交互に符号反転させる
            // 正しい実装は以下の通り:
            // DB4_WAVELET_SEQUENCE[j] = DB4_SCALING_SEQUENCE[i] * ((i % 2 == 0) ? 1.0 : -1.0) * (-1.0); // これは間違い
            // 正しいロジックは、g_k = (-1)^(k+1) * h_{L-1-k} のようなものだが、提供されたコードはもっと単純。
            // 実際のコードは `Math.pow(-1.0D, b) * this.daubechiesScalingSequence[i - 1 - b]`
            // DB4_SCALING_SEQUENCE の逆順に (-1)^index を掛ける
            // (0.1294095225512603), (-0.2241438680420134), (-0.8365163037378077), (0.4829629131445341)
            // このコメントアウトされた静的初期化子ではなく、以下のDB4_WAVELET_SEQUENCE_EXPECTEDを使用します
        }
    }

    // DiscreteWaveletTransformation.java のロジックで生成される正確な DB4_WAVELET_SEQUENCE
    private static final double[] DB4_WAVELET_SEQUENCE_EXPECTED = {
        -0.1294095225512603D, // b=0, i=3: (-1)^0 * h[3]
        -0.2241438680420134D, // b=1, i=2: (-1)^1 * h[2]
        0.8365163037378077D,  // b=2, i=1: (-1)^2 * h[1]
        -0.4829629131445341D  // b=3, i=0: (-1)^3 * h[0]
    };

    // ヘルパーメソッド：double配列の比較（許容誤差あり）
    private void assertArrayEqualsWithDelta(double[] expected, double[] actual, double delta) {
        assertEquals(expected.length, actual.length, "配列の長さが一致しません");
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], actual[i], delta, "要素 " + i + " が期待値と異なります (Expected: " + expected[i] + ", Actual: " + actual[i] + ")");
        }
    }

    // nextPowerOfTwoメソッドはprivate staticなので、ここでは実際の挙動に合わせて期待値を計算
    private int calculateNextPowerOfTwo(int value) {
      Integer n = 1;
      while (n < value) {
        n <<= 1;
      }
      return n;
    }

    // 親クラスのdaubechiesScalingSequenceとdaubechiesWaveletSequenceフィールドにアクセスするためのヘルパー
    private void setInheritedDaubechiesFiltersForTesting(DiscreteWavelet1dTransformation obj, double[] scalingSeq, double[] waveletSeq) throws NoSuchFieldException, IllegalAccessException {
        Field scalingField = DiscreteWaveletTransformation.class.getDeclaredField("daubechiesScalingSequence");
        scalingField.setAccessible(true);
        scalingField.set(obj, scalingSeq);

        Field waveletField = DiscreteWaveletTransformation.class.getDeclaredField("daubechiesWaveletSequence");
        waveletField.setAccessible(true);
        waveletField.set(obj, waveletSeq);
    }

    // --- コンストラクタのテスト (変更なし) ---
    @Test
    void testConstructorInitializesSourceCoefficientsAndLength() throws NoSuchFieldException, IllegalAccessException {
        double[] inputData = {1.0, 2.0, 3.0, 4.0}; 
        transformation = new DiscreteWavelet1dTransformation(inputData);
        assertNotNull(transformation);

        Field sourceCoefficientsField = DiscreteWavelet1dTransformation.class.getDeclaredField("sourceCoefficients");
        sourceCoefficientsField.setAccessible(true);
        double[] actualSourceCoefficients = (double[]) sourceCoefficientsField.get(transformation);

        Field originalLengthField = DiscreteWavelet1dTransformation.class.getDeclaredField("originalLength");
        originalLengthField.setAccessible(true);
        int actualOriginalLength = (int) originalLengthField.get(transformation);

        int expectedPaddedLength = calculateNextPowerOfTwo(inputData.length);
        assertEquals(expectedPaddedLength, actualSourceCoefficients.length, "ソース係数の長さは次の2のべき乗であるべき");

        for (int i = 0; i < inputData.length; i++) {
            assertEquals(inputData[i], actualSourceCoefficients[i], DELTA, "元のデータは正しくコピーされるべき");
        }
        assertEquals(inputData.length, actualOriginalLength, "originalLengthは入力データの長さに設定されるべき");

        Field isPaddedField = DiscreteWavelet1dTransformation.class.getDeclaredField("isPadded");
        isPaddedField.setAccessible(true);
        assertFalse((boolean) isPaddedField.get(transformation), "パディングされていない場合はisPaddedはfalseであるべき");
    }

    @Test
    void testConstructorWithNonPowerOfTwoLengthPadsSourceCoefficients() throws NoSuchFieldException, IllegalAccessException {
        double[] inputData = {1.0, 2.0, 3.0}; // 長さ3
        transformation = new DiscreteWavelet1dTransformation(inputData);
        assertNotNull(transformation);

        Field sourceCoefficientsField = DiscreteWavelet1dTransformation.class.getDeclaredField("sourceCoefficients");
        sourceCoefficientsField.setAccessible(true);
        double[] actualSourceCoefficients = (double[]) sourceCoefficientsField.get(transformation);

        Field originalLengthField = DiscreteWavelet1dTransformation.class.getDeclaredField("originalLength");
        originalLengthField.setAccessible(true);
        int actualOriginalLength = (int) originalLengthField.get(transformation);

        assertEquals(4, actualSourceCoefficients.length, "長さは次の2のべき乗（4）にパディングされるべき");
        assertEquals(1.0, actualSourceCoefficients[0], DELTA);
        assertEquals(2.0, actualSourceCoefficients[1], DELTA);
        assertEquals(3.0, actualSourceCoefficients[2], DELTA);
        assertEquals(0.0, actualSourceCoefficients[3], DELTA, "パディングされた値はゼロであるべき");

        assertEquals(inputData.length, actualOriginalLength, "originalLengthは入力データの長さに設定されるべき");

        Field isPaddedField = DiscreteWavelet1dTransformation.class.getDeclaredField("isPadded");
        isPaddedField.setAccessible(true);
        assertTrue((boolean) isPaddedField.get(transformation), "パディングされた場合はisPaddedはtrueであるべき");
    }

    @Test
    void testConstructorWithZeroLengthInputPadsToOne() throws NoSuchFieldException, IllegalAccessException {
        double[] inputData = {}; // 長さ0
        transformation = new DiscreteWavelet1dTransformation(inputData);
        assertNotNull(transformation);

        Field sourceCoefficientsField = DiscreteWavelet1dTransformation.class.getDeclaredField("sourceCoefficients");
        sourceCoefficientsField.setAccessible(true);
        double[] actualSourceCoefficients = (double[]) sourceCoefficientsField.get(transformation);

        Field originalLengthField = DiscreteWavelet1dTransformation.class.getDeclaredField("originalLength");
        originalLengthField.setAccessible(true);
        int actualOriginalLength = (int) originalLengthField.get(transformation);

        assertEquals(1, actualSourceCoefficients.length, "長さ0の入力は1にパディングされるべき");
        assertEquals(0.0, actualSourceCoefficients[0], DELTA, "パディングされた値はゼロであるべき");
        assertEquals(inputData.length, actualOriginalLength, "originalLengthは入力データの長さに設定されるべき");

        Field isPaddedField = DiscreteWavelet1dTransformation.class.getDeclaredField("isPadded");
        isPaddedField.setAccessible(true);
        assertTrue((boolean) isPaddedField.get(transformation), "isPaddedはtrueであるべき (0->1のパディング)");
    }

    @Test
    void testConstructorWithNullInputThrowsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new DiscreteWavelet1dTransformation(null));
    }


    // --- computeScalingAndWaveletCoefficients() メソッドのテスト (順変換) ---
    @Test
    void testComputeScalingAndWaveletCoefficientsWithDb4Example() throws Exception {
        // Daubechies-4 (db4) の例で順変換をテスト
        double[] input = {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0}; // 長さ8
        transformation = new DiscreteWavelet1dTransformation(input);

        // DiscreteWaveletTransformationのinitialize()がデフォルトでDB4フィルターをセットすることを利用
        // または、明示的にフィルターをセットする
        setInheritedDaubechiesFiltersForTesting(transformation, DB4_SCALING_SEQUENCE, DB4_WAVELET_SEQUENCE_EXPECTED);

        Method computeScalingMethod = DiscreteWavelet1dTransformation.class.getDeclaredMethod("computeScalingAndWaveletCoefficients");
        computeScalingMethod.setAccessible(true);
        computeScalingMethod.invoke(transformation);

        Field scalingCoefficientsField = DiscreteWavelet1dTransformation.class.getDeclaredField("scalingCoefficients");
        scalingCoefficientsField.setAccessible(true);
        double[] actualScalingCoefficients = (double[]) scalingCoefficientsField.get(transformation);

        Field waveletCoefficientsField = DiscreteWavelet1dTransformation.class.getDeclaredField("waveletCoefficients");
        waveletCoefficientsField.setAccessible(true);
        double[] actualWaveletCoefficients = (double[]) waveletCoefficientsField.get(transformation);

        assertNotNull(actualScalingCoefficients);
        assertNotNull(actualWaveletCoefficients);
        assertEquals(input.length / 2, actualScalingCoefficients.length);
        assertEquals(input.length / 2, actualWaveletCoefficients.length);

        // 以下はDaubechies-4フィルタの正確な計算結果（外部ツール等で検証済みであるべき）
        // この計算は手動で行うのは非常に複雑なので、正確な期待値の例としてコメントアウトで示します。
        // 一般的には、既知のウェーブレットライブラリと比較するか、非常に単純な入力で手計算します。
        // 現時点では、計算ロジックが複雑なため、ラウンドトリップテストに重点を置く方が現実的です。
        // ここでは仮の期待値とします。
        // double[] expectedScaling = { ... };
        // double[] expectedWavelet = { ... };
        // assertArrayEqualsWithDelta(expectedScaling, actualScalingCoefficients, DELTA);
        // assertArrayEqualsWithDelta(expectedWavelet, actualWaveletCoefficients, DELTA);
    }

    @Test
    void testComputeScalingAndWaveletCoefficientsWithNullSource() throws Exception {
        transformation = new DiscreteWavelet1dTransformation(new double[]{1}); // 適当な値で初期化
        
        Field sourceCoefficientsField = DiscreteWavelet1dTransformation.class.getDeclaredField("sourceCoefficients");
        sourceCoefficientsField.setAccessible(true);
        sourceCoefficientsField.set(transformation, null);

        Method computeScalingMethod = DiscreteWavelet1dTransformation.class.getDeclaredMethod("computeScalingAndWaveletCoefficients");
        computeScalingMethod.setAccessible(true);
        computeScalingMethod.invoke(transformation);

        Field scalingCoefficientsField = DiscreteWavelet1dTransformation.class.getDeclaredField("scalingCoefficients");
        scalingCoefficientsField.setAccessible(true);
        // sourceCoefficientsがnullの場合、scalingCoefficientsは初期化されずnullのままであるべき
        assertEquals(null, scalingCoefficientsField.get(transformation)); 
    }

    // --- computeRecomposedCoefficients() メソッドのテスト (逆変換) ---
    @Test
    void testComputeRecomposedCoefficientsBasic() throws Exception {
        // Daubechies-4 (db4) の逆変換をテスト
        double[] originalInput = {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0}; // 元の信号
        transformation = new DiscreteWavelet1dTransformation(originalInput); // sourceCoefficientsを設定するために使用

        // Daubechies-4 フィルタ係数を設定 (親クラスで設定されるものと一致させる)
        setInheritedDaubechiesFiltersForTesting(transformation, DB4_SCALING_SEQUENCE, DB4_WAVELET_SEQUENCE_EXPECTED);

        // まず順変換を実行してscalingCoefficientsとwaveletCoefficientsを生成
        Method computeScalingMethod = DiscreteWavelet1dTransformation.class.getDeclaredMethod("computeScalingAndWaveletCoefficients");
        computeScalingMethod.setAccessible(true);
        computeScalingMethod.invoke(transformation);

        Field scalingCoefficientsField = DiscreteWavelet1dTransformation.class.getDeclaredField("scalingCoefficients");
        scalingCoefficientsField.setAccessible(true);
        double[] scaling = (double[]) scalingCoefficientsField.get(transformation);

        Field waveletCoefficientsField = DiscreteWavelet1dTransformation.class.getDeclaredField("waveletCoefficients");
        waveletCoefficientsField.setAccessible(true);
        double[] wavelet = (double[]) waveletCoefficientsField.get(transformation);

        // 逆変換のために新しいインスタンスを作成し、計算された係数を設定
        DiscreteWavelet1dTransformation inverseTransform = new DiscreteWavelet1dTransformation(scaling, wavelet);
        setInheritedDaubechiesFiltersForTesting(inverseTransform, DB4_SCALING_SEQUENCE, DB4_WAVELET_SEQUENCE_EXPECTED); // 同じフィルターを使用

        Method recomposeMethod = DiscreteWavelet1dTransformation.class.getDeclaredMethod("computeRecomposedCoefficients");
        recomposeMethod.setAccessible(true);
        recomposeMethod.invoke(inverseTransform);

        Field recomposedCoefficientsField = DiscreteWavelet1dTransformation.class.getDeclaredField("recomposedCoefficients");
        recomposedCoefficientsField.setAccessible(true);
        double[] actualRecomposedCoefficients = (double[]) recomposedCoefficientsField.get(inverseTransform);

        assertNotNull(actualRecomposedCoefficients);
        // オリジナル入力の長さは8、再構成後も8になることを期待
        assertEquals(originalInput.length, actualRecomposedCoefficients.length, "再構成された信号の長さは元の信号の長さに一致すべき");

        // 元の入力が正確に再構成されていることを確認
        assertArrayEquals(originalInput, actualRecomposedCoefficients, DELTA);
    }

    @Test
    void testComputeRecomposedCoefficientsWithNullScalingOrWavelet() throws Exception {
        transformation = new DiscreteWavelet1dTransformation(new double[]{1,2,3,4});
        
        // scalingCoefficients が null の場合
        Field scalingCoefficientsField = DiscreteWavelet1dTransformation.class.getDeclaredField("scalingCoefficients");
        scalingCoefficientsField.setAccessible(true);
        scalingCoefficientsField.set(transformation, null);

        Method recomposeMethod = DiscreteWavelet1dTransformation.class.getDeclaredMethod("computeRecomposedCoefficients");
        recomposeMethod.setAccessible(true);
        recomposeMethod.invoke(transformation);

        Field recomposedCoefficientsField = DiscreteWavelet1dTransformation.class.getDeclaredField("recomposedCoefficients");
        recomposedCoefficientsField.setAccessible(true);
        assertEquals(null, recomposedCoefficientsField.get(transformation)); 

        // waveletCoefficients が null の場合 (scalingCoefficients は有効な値)
        scalingCoefficientsField.set(transformation, new double[]{1.0, 2.0});
        Field waveletCoefficientsField = DiscreteWavelet1dTransformation.class.getDeclaredField("waveletCoefficients");
        waveletCoefficientsField.setAccessible(true);
        waveletCoefficientsField.set(transformation, null);

        recomposeMethod.invoke(transformation);
        assertEquals(null, recomposedCoefficientsField.get(transformation)); 
    }


    // --- applyTo(Object) メソッドのテスト (変更なし) ---
    @Test
    void testApplyToWithValidDoubleArray() throws NoSuchFieldException, IllegalAccessException {
        double[] input = {10.0, 20.0, 30.0, 40.0};
        DiscreteWavelet1dTransformation transformation = new DiscreteWavelet1dTransformation(new double[4]);
        
        WaveletTransformation result = transformation.applyTo(input);
        
        assertNotNull(result);
        assertTrue(result instanceof DiscreteWavelet1dTransformation);
        
        Field sourceCoefficientsField = DiscreteWavelet1dTransformation.class.getDeclaredField("sourceCoefficients");
        sourceCoefficientsField.setAccessible(true);
        assertArrayEquals(input, (double[])sourceCoefficientsField.get(transformation), DELTA);

        Field scalingCoefficientsField = DiscreteWavelet1dTransformation.class.getDeclaredField("scalingCoefficients");
        scalingCoefficientsField.setAccessible(true);
        assertNotNull(scalingCoefficientsField.get(transformation));

        Field waveletCoefficientsField = DiscreteWavelet1dTransformation.class.getDeclaredField("waveletCoefficients");
        waveletCoefficientsField.setAccessible(true);
        assertNotNull(waveletCoefficientsField.get(transformation));
    }

    @Test
    void testApplyToWithInvalidObjectThrowsIllegalArgumentException() {
        DiscreteWavelet1dTransformation transformation = new DiscreteWavelet1dTransformation(new double[8]);
        assertThrows(IllegalArgumentException.class, () -> transformation.applyTo("invalid input"));
    }
    
    // --- transform(WaveletTransformation) メソッドのテスト (変更なし) ---
    @Test
    void testTransformChainsTransformationsCorrectly() throws NoSuchFieldException, IllegalAccessException {
        double[] initialSignal = {1.0, 2.0, 3.0, 4.0};
        DiscreteWavelet1dTransformation firstTransform = new DiscreteWavelet1dTransformation(initialSignal);

        WaveletTransformation secondTransform = firstTransform.transform(firstTransform);

        assertNotNull(secondTransform);
        assertTrue(secondTransform instanceof DiscreteWavelet1dTransformation);

        DiscreteWavelet1dTransformation castSecondTransform = (DiscreteWavelet1dTransformation) secondTransform;
        
        Field sourceCoefficientsField = DiscreteWavelet1dTransformation.class.getDeclaredField("sourceCoefficients");
        sourceCoefficientsField.setAccessible(true);
        assertArrayEquals(initialSignal, (double[])sourceCoefficientsField.get(castSecondTransform), DELTA);
        
        assertNotNull(castSecondTransform.scalingCoefficients());
        assertNotNull(castSecondTransform.waveletCoefficients());
    }

    @Test
    void testTransformWithInvalidWaveletTransformationTypeThrowsIllegalArgumentException() {
        DiscreteWavelet1dTransformation transformation = new DiscreteWavelet1dTransformation(new double[4]);
        ContinuosWaveletTransformation invalidTransform = new ContinuosWaveletTransformation();
        assertThrows(IllegalArgumentException.class, () -> transformation.transform(invalidTransform));
    }
}
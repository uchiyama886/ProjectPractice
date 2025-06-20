package wavelet;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ContinuosWaveletTransformation Class Unit Tests")
class ContinuosWaveletTransformationTest {

    @Test
    @DisplayName("Constructor creates an instance")
    void testConstructor() {
        ContinuosWaveletTransformation cwt = new ContinuosWaveletTransformation();
        assertNotNull(cwt, "ContinuosWaveletTransformation instance should not be null.");
    }

    @Test
    @DisplayName("applyTo() returns itself regardless of the argument")
    void testApplyTo() {
        ContinuosWaveletTransformation cwt = new ContinuosWaveletTransformation();
        Object dummyObject = new Object(); // ダミーのオブジェクト
        WaveletTransformation result = cwt.applyTo(dummyObject);
        assertSame(cwt, result, "applyTo() should return the instance itself.");

        // nullを渡しても自身を返すことを確認
        result = cwt.applyTo(null);
        assertSame(cwt, result, "applyTo() should return the instance itself even with null argument.");
    }

    @Test
    @DisplayName("transform() returns itself regardless of the argument")
    void testTransform() {
        ContinuosWaveletTransformation cwt = new ContinuosWaveletTransformation();
        WaveletTransformation dummyTransformation = new DiscreteWavelet1dTransformation(new double[]{1.0}); // ダミーのWaveletTransformation
        WaveletTransformation result = cwt.transform(dummyTransformation);
        assertSame(cwt, result, "transform() should return the instance itself.");

        // nullを渡しても自身を返すことを確認
        result = cwt.transform(null);
        assertSame(cwt, result, "transform() should return the instance itself even with null argument.");
    }
}
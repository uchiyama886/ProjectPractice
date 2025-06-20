package wavelet;

/**
 * 連続ウェーブレット変換（CWT）を実装するクラス。
 * このクラスは、{@link WaveletTransformation} の具象サブクラスであり、
 * 信号の連続ウェーブレット変換を表現する。
 *
 * @see WaveletTransformation
 */
public class ContinuosWaveletTransformation extends WaveletTransformation {
  public WaveletTransformation applyTo(Object paramObject) {
    return this;
  }
  
  public WaveletTransformation transform(WaveletTransformation paramWaveletTransformation) {
    return this;
  }
}
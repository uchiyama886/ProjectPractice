package wavelet;

/**
 * 連続ウェーブレット変換（CWT）を実装するクラス。
 * このクラスは、{@link WaveletTransformation} の具象サブクラスであり、
 * 信号の連続ウェーブレット変換を表現する。
 *
 * @see WaveletTransformation
 */
public class ContinuosWaveletTransformation extends WaveletTransformation 
{
  /**
   * 指定されたオブジェクトに連続ウェーブレット変換を適用する。
   * @param anObject 対象となる入力オブジェクト
   * @return このインスタンス
   */
  @Override
  public WaveletTransformation applyTo(Object anObject) 
  {
    return this;
  }
  
  /**
   * 指定されたウェーブレット変換に対して、連続ウェーブレット変換を適用する。
   * @param waveletTransformation 対象となるウェーブレット変換
   * @return このインスタンス
   */
  public WaveletTransformation transform(WaveletTransformation waveletTransformation) 
  {
    return this;
  }
}
package wavelet;

public abstract class WaveletTransformation {
  public WaveletTransformation() {
    initialize();
  }
  
  protected void initialize() {}
  
  public abstract WaveletTransformation applyTo(Object paramObject);
  
  public abstract WaveletTransformation transform(WaveletTransformation paramWaveletTransformation);
}



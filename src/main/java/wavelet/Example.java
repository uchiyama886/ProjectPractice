package wavelet;

public class Example {
  public static void main(String[] paramArrayOfString) {
    Example1d.main(paramArrayOfString);
    Example2d.main(paramArrayOfString);
    (new Wavelet1dModel()).open();
    (new Wavelet2dModel()).open();
  }
}

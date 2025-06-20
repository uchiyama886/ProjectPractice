package wavelet;

/**
 * 1次元および2次元のウェーブレット変換のデモンストレーションを実行するエントリポイントクラス。
 */
public class Example 
{
  /**
   * アプリケーションのエントリポイント。
   * 各種1次元および2次元ウェーブレット変換のデモやモデルを順に起動する。
   * @param arguments コマンドライン引数
   */
  public static void main(String[] arguments) {
    Example1d.main(arguments);
    Example2d.main(arguments);
    (new Wavelet1dModel()).open();
    (new Wavelet2dModel()).open();
  }
}

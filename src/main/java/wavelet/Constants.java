package wavelet;

/**
* 定数をフィールド変数にまとめたクラス。定数の定義を行う。
*/
public class Constants extends Object {
    /**
     * 'Luminance'という名前の公開静的整数型変数を宣言している。
     * 静的であるため、このクラスのインスタンスを作成せずに直接'Constans.Luminance'としてアクセスできる。
     */
    public static Integer Luminance; 

    /**
     * 'Red'という名前の公開静的整数型変数を宣言し、1で初期化する。
     */
    public static Integer Red = 1; 

    /**
     * Greenチャネルに対応する定数。通常は2に設定される。
     */
    public static Integer Green = 2;
    /**
     * Blueチャネルに対応する定数。通常は3に設定される。
     */
    public static Integer Blue = 3;

}
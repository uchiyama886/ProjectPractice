package wavelet;

/**
* 定数をフィールド変数にまとめたクラス。定数の定義を行う。
*/
public class Constants extends Object {
	/**
     * 輝度（Luminance）に対応する定数である。
     * 通常、画像処理などにおいて、明るさ成分を示すために使用される。
     * このフィールドは初期化されていないため、使用前に値を設定する必要がある。
     */
    public static Integer Luminance; 

	/**
     * 赤色チャネル（Red）に対応する定数である。
     * 一般的に、RGB形式の画像データにおける赤色成分を示すために使用され、値は1に設定される。
     */
    public static Integer Red = 1; 

    /**
     * 緑色チャネル（Green）に対応する定数である。
     * 一般的に、RGB形式の画像データにおける緑色成分を示すために使用され、値は2に設定される。
     */
    public static Integer Green = 2;

    /**
     * 青色チャネル（Blue）に対応する定数である。
     * 一般的に、RGB形式の画像データにおける青色成分を示すために使用され、値は3に設定される。
     */
    public static Integer Blue = 3;

}
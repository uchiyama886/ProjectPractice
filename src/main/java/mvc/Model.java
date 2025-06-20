package mvc;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * MVCデザインパターンにおける、アプリケーションのビジネスロジックとデータを管理するモデルクラス。
 */
public class Model 
{

  /**
   * このモデルに依存しているビュー(View)の一覧を保持するリスト。
   */
  protected ArrayList<View> dependents;

  /**
   * モデルが保持している画像データ。
   */
  private BufferedImage picture;

  /**
   * 初期化メソッドを呼び出して dependents と picture を初期化。
   */
  public Model() 
  {
    super();
    this.initialize();
    return;
  }

  /**
   * View を dependents に追加する。
   *  @param aView dependentsに追加する用
   */
  public void addDependent(View aView) 
  {
    this.dependents.add(aView);
    return;
  }

  /**
   * モデルが変更されたとき、登録されている全ての View に更新を通知する。
   */
  public void changed() 
  {
    this.dependents.forEach((View aView) -> { aView.update(); });
    return;
  }

  /**
   * モデルの内部状態（依存ビューリストと画像）を初期化。
   */
  private void initialize() {
    // 新しい空のリストを作成
    this.dependents = new ArrayList<>(); 

    // 画像はまだ読み込まれていない状態
    this.picture = null; 

    return;
  }

  /**
   * モデルに対して操作を行うメソッド（空実装）。
   */ 
  public void perform() {return;}

  /**
   * 現在保持している画像（picture）を返すメソッド。
   */
  public BufferedImage picture() {
    return this.picture;
  }

  /**
   * 新しい画像を設定するメソッド。
   * @param anImage 新しい画像
   */
  public void picture(BufferedImage anImage) {
    this.picture = anImage;
    return;
  }

  /**
   * モデルの文字列表現を返す。
   */
  @Override
  public String toString() {
    // 文字列を効率よく連結するためのバッファ
    StringBuffer stringBuffer = new StringBuffer();  

    // このクラスの実行時クラスを取得
    Class<?> clazz = getClass();                     

    // クラス名を追加
    stringBuffer.append(clazz.getName());            

    // ラベル文字列
    stringBuffer.append("[picture=");            
    
    // picture の中身を追加（null か画像情報）
    stringBuffer.append(this.picture);               

    // 閉じる
    stringBuffer.append("]");                    
    
    // 完成した文字列を返す
    return stringBuffer.toString();                  
  }
}

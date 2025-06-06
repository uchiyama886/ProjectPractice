package mvc;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * ビューの依存リストと画像データ（picture）を管理し、変更通知も行う。
 */
public class Model {

  // このモデルに依存しているビュー(View)の一覧を保持するリスト
  protected ArrayList<View> dependents;

  // モデルが保持している画像データ
  private BufferedImage picture;

  /**
   * 初期化メソッドを呼び出して dependents と picture を初期化
   */
  public Model() {
    initialize();
  }

  /**
   * View を dependents に追加する
   */
  public void addDependent(View paramView) {
    this.dependents.add(paramView);
  }

  /**
   * モデルが変更されたとき、登録されている全ての View に更新を通知する
   */
  public void changed() {
    Consumer<Object> aConsumer=(view -> view.update());
    this.dependents.forEach(aConsumer);
  }

  /**
   * モデルの内部状態（依存ビューリストと画像）を初期化
   */
  private void initialize() {
    this.dependents = new ArrayList<>(); // 新しい空のリストを作成
    this.picture = null; // 画像はまだ読み込まれていない状態
  }

  /**
   * モデルに対して操作を行うメソッド（空実装）
   */
  public void perform() {}

  /**
   * 現在保持している画像（picture）を返すメソッド
   */
  public BufferedImage picture() {
    return this.picture;
  }

  /**
   * 新しい画像を設定するメソッド
   */
  public void picture(BufferedImage paramBufferedImage) {
    this.picture = paramBufferedImage;
  }

  /**
   * モデルの文字列表現を返す
   */
  @Override
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();  // 文字列を効率よく連結するためのバッファ
    Class<?> clazz = getClass();                     // このクラスの実行時クラスを取得
    stringBuffer.append(clazz.getName());            // クラス名を追加
    stringBuffer.append("[picture=");                // ラベル文字列
    stringBuffer.append(this.picture);               // picture の中身を追加（null か画像情報）
    stringBuffer.append("]");                        // 閉じる
    return stringBuffer.toString();                  // 完成した文字列を返す
  }
}

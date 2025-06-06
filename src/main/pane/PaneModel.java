package pane;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import mvc.Model;
import utility.ImageUtility;

/**
 * 画像を扱う汎用的なパネルモデルを表すクラス。
 * 画像の読み込みや、マウス操作に対する反応（クリックやドラッグ）を提供する。
 */
public class PaneModel extends Model {

  /**
   * デフォルトコンストラクタ。
   */
  public PaneModel() {
  }

  /**
   * ファイル名を指定して画像を読み込み、モデルにセットするコンストラクタ。
   * @param paramString 画像ファイルのパス（文字列）
   */
  public PaneModel(String paramString) {
    // ユーティリティクラスを使って画像を読み込む
    BufferedImage bufferedImage = ImageUtility.readImage(paramString);
    // 読み込んだ画像をモデルにセット
    picture(bufferedImage);
  }

  /**
   * 画像を直接指定してモデルにセットするコンストラクタ。
   * @param paramBufferedImage セットする画像
   */
  public PaneModel(BufferedImage paramBufferedImage) {
    picture(paramBufferedImage);
  }

  /**
   * マウスがクリックされたときに呼び出されるメソッド。
   * 現在はクリック位置を標準出力に表示するだけのデバッグ用実装。
   * @param paramPoint      クリックされた座標
   * @param paramMouseEvent マウスイベントオブジェクト
   */
  public void mouseClicked(Point paramPoint, MouseEvent paramMouseEvent) {
    System.out.println(paramPoint);
  }

  /**
   * マウスがドラッグされたときに呼び出されるメソッド。
   * 現在はドラッグ位置を標準出力に表示するだけのデバッグ用実装。
   * @param paramPoint      ドラッグされた座標
   * @param paramMouseEvent マウスイベントオブジェクト
   */
  public void mouseDragged(Point paramPoint, MouseEvent paramMouseEvent) {
    System.out.println(paramPoint);
  }
}

package pane;

import java.awt.Point;
import java.awt.event.MouseEvent;
import mvc.Controller;
import utility.Condition;
import utility.ValueHolder;

/**
 * マウスイベントを処理し、モデルとビューを仲介するコントローラクラス。
 * View（画面）で発生したマウスクリックやドラッグのイベントを受け取り、
 * 画面上の座標を画像上の座標に変換した後、Modelに処理を委譲する。
 */
public class PaneController extends Controller {

  /**
   * 関連付けられたビューをPaneViewとして取得する。
   * 親クラスの汎用的なviewプロパティを、具体的なPaneView型に
   * キャストして返す。これにより、PaneView固有のメソッドに
   * アクセスできるようになる。
   *
   * @return このコントローラに関連付けられたPaneViewオブジェクト
   */
  public PaneView getView() {
    // viewをPaneViewにキャストして返す
    return (PaneView)this.view;
  }
  
  /**
   * マウスクリックイベントを処理する。
   * マウスイベントから画面上の座標を取得し、それを画像上の座標に変換。
   * 変換後の座標が有効な場合、その座標と元のマウスイベントをModelに渡し、
   * クリック時のロジック実行を依頼する。
   *
   * @param aMouseEvent AWTから通知されるマウスイベントオブジェクト
   */
  @Override
  public void mouseClicked(MouseEvent aMouseEvent) {
    // 画面上のマウスクリック位置の座標を取得
    ValueHolder<Point> point = new ValueHolder<>(aMouseEvent.getPoint());
    PaneView paneView = getView();
    // 画面上の座標を画像上の座標に変換
    point.set(paneView.convertViewPointToPicturePoint(point.get()));
    new Condition(() -> point.get() == null).ifTrue(() -> {});
    // PaneViewからPaneModelを取得し、画像座標とマウスイベントを渡してクリック処理を委譲
    paneView.getModel().mouseClicked(point.get(), aMouseEvent);
  }
  
  /**
   * マウスドラッグイベントを処理する。
   * マウスがドラッグされている間の座標を画面座標から画像座標へ変換し、
   * Modelに処理を委譲する。これにより、連続的な描画やオブジェクトの
   * 移動などが可能となる。
   *
   * @param aMouseEvent AWTから通知されるマウスイベントオブジェクト
   */
  @Override
  public void mouseDragged(MouseEvent aMouseEvent) {
    // 画面上のマウスクリック位置の座標を取得
    ValueHolder<Point> point = new ValueHolder<>(aMouseEvent.getPoint());
    PaneView paneView = getView();
    // 画面上の座標を画像上の座標に変換
    point.set(paneView.convertViewPointToPicturePoint(point.get()));
    new Condition(() -> point.get() == null).ifTrue(() -> {});
    paneView.getModel().mouseDragged(point.get(), aMouseEvent);
  }
}
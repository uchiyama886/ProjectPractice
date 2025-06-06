package pane;

import java.awt.Point;
import java.awt.event.MouseEvent;
import mvc.Controller;

public class PaneController extends Controller {
  public PaneView getView() {
    // viewをPaneViewにキャストして返す
    return (PaneView)this.view;
  }
  
  // MouseListener インターフェースの mouseClicked メソッドをオーバーライド
  @Override
  public void mouseClicked(MouseEvent aMouseEvent) {
    // 画面上のマウスクリック位置の座標を取得
    Point point = aMouseEvent.getPoint();
    PaneView paneView = getView();
    // 画面上の座標を画像上の座標に変換
    point = paneView.convertViewPointToPicturePoint(point);
    if (point == null)
      return;
    // PaneViewからPaneModelを取得し、画像座標とマウスイベントを渡してクリック処理を委譲
    paneView.getModel().mouseClicked(point, aMouseEvent);
  }
  
  public void mouseDragged(MouseEvent paramMouseEvent) {
    Point point = paramMouseEvent.getPoint();
    PaneView paneView = getView();
    point = paneView.convertViewPointToPicturePoint(point);
    if (point == null)
      return; 
    paneView.getModel().mouseDragged(point, paramMouseEvent);
  }
}
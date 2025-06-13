package pane;

import java.awt.Point;
import java.awt.event.MouseEvent;
import mvc.Controller;

public class PaneController extends Controller {
  public PaneView getView() {
    // viewをPaneViewにキャストして返す
    return (PaneView)this.view;
  }
  
  public void mouseClicked(MouseEvent paramMouseEvent) {
    Point point = paramMouseEvent.getPoint();
    PaneView paneView = getView();
    point = paneView.convertViewPointToPicturePoint(point);
    if (point == null)
      return; 
    paneView.getModel().mouseClicked(point, paramMouseEvent);
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
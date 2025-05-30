package pane;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import mvc.View;
import utility.ImageUtility;

public class PaneView extends View {
  private Point2D.Double originPoint;

  private Point2D.Double scaleFactor;

  public PaneView(PaneModel paramPaneModel, PaneController paramPaneController) {
    super(paramPaneModel, paramPaneController);
    intialize();
  }

  public void intialize() {
    this.originPoint = new Point2D.Double(0.0D, 0.0D);
    this.scaleFactor = new Point2D.Double(1.0D, 1.0D);
  }

  public PaneModel getModel() {
    return (PaneModel) this.model;
  }

  public Point convertViewPointToModelPoint(Point paramPoint) {
    Point point = scrollAmount();
    return new Point(paramPoint.x + point.x, paramPoint.y + point.y);
  }

  public Point convertViewPointToPicturePoint(Point paramPoint) {
    Point point = convertViewPointToModelPoint(paramPoint);
    return convertModelPointToPicturePoint(point);
  }

  public Point convertModelPointToPicturePoint(Point paramPoint) {
    PaneModel paneModel = getModel();
    BufferedImage bufferedImage = paneModel.picture();
    if (bufferedImage == null)
      return null;
    double d1 = (paramPoint.x - this.originPoint.x) / this.scaleFactor.x;
    double d2 = (paramPoint.y - this.originPoint.y) / this.scaleFactor.y;
    int i = (int) d1;
    int j = (int) d2;
    if (i < 0)
      return null;
    if (j < 0)
      return null;
    int k = bufferedImage.getWidth();
    int m = bufferedImage.getHeight();
    return (i > k) ? null : ((j > m) ? null : new Point(i, j));
  }

  public void paintComponent(Graphics paramGraphics) {
    super.paintComponent(paramGraphics);
    int i = getWidth();
    int j = getHeight();
    if (i <= 0 || j <= 0)
      return; // 描画領域がない場合は処理しない
    paramGraphics.setColor(Color.white);
    paramGraphics.fillRect(0, 0, i, j);
    if (this.model == null)
      return;
    BufferedImage bufferedImage = this.model.picture();
    if (bufferedImage == null)
      return;
    int k = bufferedImage.getWidth();
    int m = bufferedImage.getHeight();
    // avoid integer division, use double ratio
    double d1 = (double) i / k;
    double d2 = (double) j / m;
    if (d1 > d2) {
      d1 = d2;
    } else {
      d2 = d1;
    }
    this.scaleFactor = new Point2D.Double(d1, d2);
    k = (int) (k * d1);
    m = (int) (m * d2);
    bufferedImage = ImageUtility.adjustImage(bufferedImage, k, m);
    d1 = (i - k) / 2.0D;
    d2 = (j - m) / 2.0D;
    this.originPoint = new Point2D.Double(d1, d2);
    paramGraphics.drawImage(bufferedImage, (int) d1, (int) d2, null);
  }

  public Point scrollAmount() {
    return new Point(0, 0);
  }

  public void scrollBy(Point paramPoint) {
  }

  public void scrollTo(Point paramPoint) {
  }
}

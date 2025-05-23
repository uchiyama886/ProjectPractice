package mvc;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

public class View extends JPanel {
  protected Model model;
  
  protected Controller controller;
  
  private Point offset;
  
  public View(Model paramModel) {
    this.model = paramModel;
    this.model.addDependent(this);
    this.controller = new Controller();
    this.controller.setModel(this.model);
    this.controller.setView(this);
    this.offset = new Point(0, 0);
  }
  
  public View(Model paramModel, Controller paramController) {
    this.model = paramModel;
    this.model.addDependent(this);
    this.controller = paramController;
    this.controller.setModel(this.model);
    this.controller.setView(this);
    this.offset = new Point(0, 0);
  }
  
  public void paintComponent(Graphics paramGraphics) {
    int i = getWidth();
    int j = getHeight();
    paramGraphics.setColor(Color.lightGray);
    paramGraphics.fillRect(0, 0, i, j);
    if (this.model == null)
      return; 
    BufferedImage bufferedImage = this.model.picture();
    if (bufferedImage == null)
      return; 
    paramGraphics.drawImage(bufferedImage, this.offset.x, this.offset.y, null);
  }
  
  public Point scrollAmount() {
    int i = 0 - this.offset.x;
    int j = 0 - this.offset.y;
    return new Point(i, j);
  }
  
  public void scrollBy(Point paramPoint) {
    int i = this.offset.x + paramPoint.x;
    int j = this.offset.y + paramPoint.y;
    scrollTo(new Point(i, j));
  }
  
  public void scrollTo(Point paramPoint) {
    this.offset = paramPoint;
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    Class<?> clazz = getClass();
    stringBuffer.append(clazz.getName());
    stringBuffer.append("[model=");
    stringBuffer.append(this.model);
    stringBuffer.append(",offset=");
    stringBuffer.append(this.offset);
    stringBuffer.append("]");
    return stringBuffer.toString();
  }
  
  public void update() {
    repaint(0, 0, getWidth(), getHeight());
  }
}



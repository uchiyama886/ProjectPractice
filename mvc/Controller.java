package mvc;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import javax.swing.event.MouseInputAdapter;

public class Controller extends MouseInputAdapter implements MouseWheelListener {
  protected Model model = null;
  
  protected View view = null;
  
  private Point previous = null;
  
  private Point current = null;
  
  public void mouseClicked(MouseEvent paramMouseEvent) {
    Point point = paramMouseEvent.getPoint();
    point.translate((this.view.scrollAmount()).x, (this.view.scrollAmount()).y);
    System.out.println(point);
  }
  
  public void mouseDragged(MouseEvent paramMouseEvent) {
    Cursor cursor = Cursor.getPredefinedCursor(13);
    Component component = (Component)paramMouseEvent.getSource();
    component.setCursor(cursor);
    this.current = paramMouseEvent.getPoint();
    Integer integer1 = Integer.valueOf(this.current.x - this.previous.x);
    Integer integer2 = Integer.valueOf(this.current.y - this.previous.y);
    Point point = new Point(integer1.intValue(), integer2.intValue());
    scrollBy(point, paramMouseEvent);
    this.previous = this.current;
  }
  
  public void mouseEntered(MouseEvent paramMouseEvent) {}
  
  public void mouseExited(MouseEvent paramMouseEvent) {}
  
  public void mouseMoved(MouseEvent paramMouseEvent) {}
  
  public void mousePressed(MouseEvent paramMouseEvent) {
    Cursor cursor = Cursor.getPredefinedCursor(1);
    Component component = (Component)paramMouseEvent.getSource();
    component.setCursor(cursor);
    this.current = paramMouseEvent.getPoint();
    this.previous = this.current;
  }
  
  public void mouseReleased(MouseEvent paramMouseEvent) {
    Cursor cursor = Cursor.getDefaultCursor();
    Component component = (Component)paramMouseEvent.getSource();
    component.setCursor(cursor);
    this.current = paramMouseEvent.getPoint();
    this.previous = this.current;
  }
  
  public void mouseWheelMoved(MouseWheelEvent paramMouseWheelEvent) {
    Integer integer1 = Integer.valueOf(-paramMouseWheelEvent.getWheelRotation());
    if (integer1.intValue() == 0)
      return; 
    Point point = new Point(0, integer1.intValue());
    Integer integer2 = Integer.valueOf(paramMouseWheelEvent.getModifiersEx());
    Boolean bool = Boolean.valueOf(((integer2.intValue() & 0x200) != 0));
    if (bool.booleanValue())
      point = new Point(integer1.intValue(), 0); 
    scrollBy(point, paramMouseWheelEvent);
  }
  
  public void scrollBy(Point paramPoint, MouseEvent paramMouseEvent) {
    this.view.scrollBy(paramPoint);
    this.view.repaint();
    Integer integer = Integer.valueOf(paramMouseEvent.getModifiersEx());
    Boolean bool1 = Boolean.valueOf(((integer.intValue() & 0x40) != 0));
    if (!bool1.booleanValue())
      return; 
    Point point1 = this.view.scrollAmount();
    Point point2 = new Point(0 - point1.x, 0 - point1.y);
    Boolean bool2 = Boolean.valueOf(((integer.intValue() & 0x100) != 0));
    for (View view : this.view.model.dependents) {
      if (view != this.view) {
        if (!bool2.booleanValue()) {
          view.scrollBy(paramPoint);
        } else {
          view.scrollTo(point2);
        } 
        view.repaint();
      } 
    } 
  }
  
  public void setModel(Model paramModel) {
    this.model = paramModel;
  }
  
  public void setView(View paramView) {
    this.view = paramView;
    this.view.addMouseListener(this);
    this.view.addMouseMotionListener(this);
    this.view.addMouseWheelListener(this);
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    Class<?> clazz = getClass();
    stringBuffer.append(clazz.getName());
    stringBuffer.append("[model=");
    stringBuffer.append(this.model);
    stringBuffer.append(",view=");
    stringBuffer.append(this.view);
    stringBuffer.append("]");
    return stringBuffer.toString();
  }
}



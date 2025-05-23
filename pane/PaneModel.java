package pane;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import mvc.Model;
import utility.ImageUtility;

public class PaneModel extends Model {
  public PaneModel() {}
  
  public PaneModel(String paramString) {
    BufferedImage bufferedImage = ImageUtility.readImage(paramString);
    picture(bufferedImage);
  }
  
  public PaneModel(BufferedImage paramBufferedImage) {
    picture(paramBufferedImage);
  }
  
  public void mouseClicked(Point paramPoint, MouseEvent paramMouseEvent) {
    System.out.println(paramPoint);
  }
  
  public void mouseDragged(Point paramPoint, MouseEvent paramMouseEvent) {
    System.out.println(paramPoint);
  }
}



package wavelet;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import mvc.Model;

public abstract class WaveletModel extends Model {
  public static final double accuracy = 1.0E-5D;
  
  public abstract void actionPerformed(ActionEvent paramActionEvent);
  
  public abstract void computeFromPoint(Point paramPoint, boolean paramBoolean);
  
  public abstract void computeRecomposedCoefficients();
  
  public abstract void mouseClicked(Point paramPoint, MouseEvent paramMouseEvent);
  
  public abstract void mouseDragged(Point paramPoint, MouseEvent paramMouseEvent);
  
  public abstract void open();
  
  public abstract void showPopupMenu(MouseEvent paramMouseEvent, WaveletPaneController paramWaveletPaneController);
}



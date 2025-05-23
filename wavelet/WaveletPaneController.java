package wavelet;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import pane.PaneController;

public class WaveletPaneController extends PaneController implements ActionListener {
  private boolean isMenuPopuping = false;
  
  public void actionPerformed(ActionEvent paramActionEvent) {
    String str = paramActionEvent.getActionCommand();
    WaveletPaneModel waveletPaneModel = (WaveletPaneModel)this.model;
    waveletPaneModel.actionPerformed(paramActionEvent);
  }
  
  public void mouseClicked(MouseEvent paramMouseEvent) {
    if (this.isMenuPopuping) {
      this.isMenuPopuping = false;
      return;
    } 
    super.mouseClicked(paramMouseEvent);
  }
  
  public void mouseDragged(MouseEvent paramMouseEvent) {
    if (this.isMenuPopuping)
      return; 
    super.mouseDragged(paramMouseEvent);
  }
  
  public void mouseMoved(MouseEvent paramMouseEvent) {}
  
  public void mousePressed(MouseEvent paramMouseEvent) {
    super.mousePressed(paramMouseEvent);
    if (paramMouseEvent.isPopupTrigger()) {
      showPopupMenu(paramMouseEvent);
    } else {
      this.isMenuPopuping = false;
    } 
  }
  
  public void mouseReleased(MouseEvent paramMouseEvent) {
    super.mouseReleased(paramMouseEvent);
  }
  
  public void showPopupMenu(MouseEvent paramMouseEvent) {
    WaveletPaneModel waveletPaneModel = (WaveletPaneModel)this.model;
    waveletPaneModel.showPopupMenu(paramMouseEvent, this);
    this.isMenuPopuping = true;
  }
}



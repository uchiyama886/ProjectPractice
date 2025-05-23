package wavelet;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import pane.PaneModel;

public class WaveletPaneModel extends PaneModel {
  private String label = "";
  
  private WaveletModel listener = null;
  
  public WaveletPaneModel() {}
  
  public WaveletPaneModel(String paramString) {
    this.label = paramString;
    this.listener = null;
  }
  
  public WaveletPaneModel(BufferedImage paramBufferedImage, String paramString) {
    super(paramBufferedImage);
    this.label = paramString;
    this.listener = null;
  }
  
  public WaveletPaneModel(BufferedImage paramBufferedImage, String paramString, WaveletModel paramWaveletModel) {
    super(paramBufferedImage);
    this.label = paramString;
    this.listener = paramWaveletModel;
  }
  
  public void actionPerformed(ActionEvent paramActionEvent) {
    if (isNotInteractive())
      return; 
    this.listener.actionPerformed(paramActionEvent);
  }
  
  public boolean isInteractive() {
    return (this.listener != null);
  }
  
  public boolean isNotInteractive() {
    return (this.listener == null);
  }
  
  public String label() {
    return this.label;
  }
  
  public void mouseClicked(Point paramPoint, MouseEvent paramMouseEvent) {
    if (isNotInteractive())
      return; 
    this.listener.mouseClicked(paramPoint, paramMouseEvent);
  }
  
  public void mouseDragged(Point paramPoint, MouseEvent paramMouseEvent) {
    if (isNotInteractive())
      return; 
    this.listener.mouseDragged(paramPoint, paramMouseEvent);
  }
  
  public void showPopupMenu(MouseEvent paramMouseEvent, WaveletPaneController paramWaveletPaneController) {
    if (isNotInteractive())
      return; 
    this.listener.showPopupMenu(paramMouseEvent, paramWaveletPaneController);
  }
}



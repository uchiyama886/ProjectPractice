package wavelet;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import pane.PaneView;

public class WaveletPaneView extends PaneView {
  public WaveletPaneView(WaveletPaneModel paramWaveletPaneModel) {
    super(paramWaveletPaneModel, new WaveletPaneController());
  }
  
  public WaveletPaneView(WaveletPaneModel paramWaveletPaneModel, WaveletPaneController paramWaveletPaneController) {
    super(paramWaveletPaneModel, paramWaveletPaneController);
  }
  
  public void paintComponent(Graphics paramGraphics) {
    super.paintComponent(paramGraphics);
    Font font = new Font("MonoSpaced", 0, 12);
    paramGraphics.setFont(font);
    WaveletPaneModel waveletPaneModel = (WaveletPaneModel)getModel();
    int i = font.getSize();
    String str = waveletPaneModel.label();
    paramGraphics.setColor(Color.white);
    paramGraphics.drawString(str, 1, i + 1);
    paramGraphics.drawString(str, 2, i + 1);
    paramGraphics.drawString(str, 3, i + 1);
    paramGraphics.drawString(str, 1, i + 2);
    paramGraphics.drawString(str, 3, i + 2);
    paramGraphics.drawString(str, 1, i + 3);
    paramGraphics.drawString(str, 2, i + 3);
    paramGraphics.drawString(str, 3, i + 3);
    paramGraphics.setColor(Color.black);
    paramGraphics.drawString(str, 2, i + 2);
  }
}

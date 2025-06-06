package wavelet;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import wavelet.PaneView;

public class WaveletPaneView extends PaneView {

	public WaveletPaneView(WaveletPaneModel aModel) {
		super(aModel);
  }

	public WaveletPaneView(WaveletPaneModel aModel, WaveletPaneController aController) {
		super(aModel, aController);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (model.picture() != null) {
			g.drawImage(model.picture(), 0, 0, getWidth(), getHeight(), null);
		}
	}
}

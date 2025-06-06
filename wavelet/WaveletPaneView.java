package wavelet;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import wavelet.PaneView;

public class WaveletPaneView extends PaneView {

	public WaveletPaneView(WaveletPaneModel aModel) {
		super(aModel, new WaveletPaneController());

	}

	public WaveletPaneView(WaveletPaneModel aModel, WaveletPaneController aController) {
		super(aModel, aController);
	}

	protected void paintComponent(Graphics aGraphics) {

		super.paintComponent(aGraphics);
		Font font = new Font("MonoSpaced", 0, 12);
		aGraphics.setFont(font);
		WaveletPaneModel waveletPaneModel = (WaveletPaneModel) getModel();
		int i = font.getSize();
		String str = waveletPaneModel.label();

		aGraphics.setColor(Color.white);
		aGraphics.drawString(str, 1, i + 1);
		aGraphics.drawString(str, 2, i + 1);
		aGraphics.drawString(str, 3, i + 1);
		aGraphics.drawString(str, 1, i + 2);
		aGraphics.drawString(str, 3, i + 2);
		aGraphics.drawString(str, 1, i + 3);
		aGraphics.drawString(str, 2, i + 3);
		aGraphics.drawString(str, 3, i + 3);
		aGraphics.setColor(Color.black);
		aGraphics.drawString(str, 2, i + 2);
	}
}

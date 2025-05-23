package wavelet;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class WaveletPaneModel extends PaneModel {

	private String label;

	private WaveletModel listener;

	/**
	 * ペインモデルを作るコンストラクタ。
	 */
	public WaveletPaneModel() {
		super();
	}

	public WaveletPaneModel(String aString) {

	}

	public WaveletPaneModel(BufferedImage anlmage, String aString) {

	}

	public WaveletPaneModel(BufferedImage anlmage, String aString, WaveletModel aModel) {

	}

	public void actionPerformed(ActionEvent anActionEvent) {

	}

	public boolean isInteractive() {
		return false;
	}

	public boolean isNotlnteractive() {
		return false;
	}

	public String label() {
		return null;
	}

	public void mouseClicked(Point aPoint, MouseEvent aMouseEvent) {

	}

	public void mouseDragged(Point aPoint, MouseEvent aMouseEvent) {

	}

	public void showPopupMenu(MouseEvent aMouseEvent, WaveletPaneController aController) {

	}

}

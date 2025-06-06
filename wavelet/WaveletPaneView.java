package wavelet;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import wavelet.PaneView;

public class WaveletPaneView extends PaneView {
	// モデルのみを受け取り、コントローラを新規作成
	public WaveletPaneView(WaveletPaneModel aWaveletPaneModel) {
		super(aWaveletPaneModel, new WaveletPaneController());
	}

	// モデルとコンロトーラを外部から受け取る
	public WaveletPaneView(WaveletPaneModel aModel, WaveletPaneController aController) {
		super(aModel, aController);
	}

	// 描画処理
	public void paintComponent(Graphics aGraphics) {
		// 基本描画（画像表示、拡大縮小、中央配置）
		super.paintComponent(aGraphics);
		// フォント指定
		Font font = new Font("MonoSpaced", 0, 12);
		aGraphics.setFont(font);
		// モデルキャスト
		WaveletPaneModel waveletPaneModel = (WaveletPaneModel) getModel();
		int i = font.getSize();
		// テキストを所得
		String str = waveletPaneModel.label();
		// ドロップシャドウ風に描画
		// 白色で8回描画
		aGraphics.setColor(Color.white);
		aGraphics.drawString(str, 1, i + 1);
		aGraphics.drawString(str, 2, i + 1);
		aGraphics.drawString(str, 3, i + 1);
		aGraphics.drawString(str, 1, i + 2);
		aGraphics.drawString(str, 3, i + 2);
		aGraphics.drawString(str, 1, i + 3);
		aGraphics.drawString(str, 2, i + 3);
		aGraphics.drawString(str, 3, i + 3);
		//黒で本来の文字を中央に描く
		aGraphics.setColor(Color.black);
		aGraphics.drawString(str, 2, i + 2);
	}
}

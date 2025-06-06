package wavelet;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

public class WaveletPaneController extends PaneController implements ActionListener {

	// ポップアップメニュー(右クリックメニュー)が表示中かどうかを表すフラグ
	private boolean isMenuPopuping = false;

	/**
	 * 上位コンストラクタを継承するただけのコンストラクタ。
	 */
	public WaveletPaneController() {

	}

	/**
	 * アクションイベントが発生したときに呼び出されます。
	 * モデルにアクションイベントを転送します。
	 * 
	 * @param anActionEvent アクションイベント
	 */
	// ActionListenerをimportしているのでオーバーライドできる
	@Override
	public void actionPerformed(ActionEvent anActionEvent) {

		// modelをWaveletPaneModel にキャスト
		WaveletPaneModel aWaveletPaneModel = (WaveletPaneModel) this.model;

		// aWaveletPaneModelのactionPerformedメソッドを呼び出し、イベント処理を委譲
		aWaveletPaneModel.actionPerformed(anActionEvent);
	}

	// マウスのボタンがクリックされた時に呼ばれる
	@Override
	public void mouseClicked(MouseEvent aMouseEvent) {
		// メニューをポップアップ中のときのとき、親クラスを呼ばない
		if (this.isMenuPopuping) {
			this.isMenuPopuping = false;
			return;
		}
		super.mouseClicked(aMouseEvent);
	}

	// マウスをドラッグした時に呼ばれる
	@Override
	public void mouseDragged(MouseEvent aMouseEvent) {
		// メニューをポップアップ中のときのとき、親クラスを呼ばない
		if (this.isMenuPopuping)
			return;
		super.mouseDragged(aMouseEvent);
	}

	@Override
	public void mouseMoved(MouseEvent aMouseEvent) {

	}

	// Windows：mouseReleased で isPopupTrigger() が true
	// Mac：mousePressed で isPopupTrigger() が true
	// マウスが押された瞬間呼ばれる
	@Override
	public void mousePressed(MouseEvent aMouseEvent) {
		// 親クラスのmousePressedを呼ぶ
		super.mousePressed(aMouseEvent);
		// ポップアップメニュー(右クリックメニュー)を表示するべきイベントかどうかを判断
		if (aMouseEvent.isPopupTrigger()) {
			// ポップアップメニューを表示
			showPopupMenu(aMouseEvent);
		} else {
			this.isMenuPopuping = false;
		}
	}

	@Override
	public void mouseReleased(MouseEvent aMouseEvent) {
		super.mouseReleased(aMouseEvent);
		if (aMouseEvent.isPopupTrigger()) {
			showPopupMenu(aMouseEvent);
		}
	}

	@Override
	public void showPopupMenu(MouseEvent aMouseEvent) {
		WaveletPaneModel waveletPaneModel = (WaveletPaneModel) this.model;
		waveletPaneModel.showPopupMenu(aMouseEvent, this);
		this.isMenuPopuping = true;
	}
}

package wavelet;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.event.ActionEvent;
import condition.Condition;

/**
 * Wavelet処理に関する画像ペインのモデルを定義するクラス。
 * ユーザーのアクションやマウス操作に応じて、WaveletModelにイベントを伝搬させる。
 */
public class WaveletPaneModel extends PaneModel {

	// このペインのラベル（画像の名前など）
	private String label;

	// WaveletModel リスナー（イベント処理を委譲する相手）
	private WaveletModel listener = null;

	/**
	 * デフォルトのコンストラクタ。
	 */
	public WaveletPaneModel() {

	}

	/**
	 * ファイル名(aString)からペインモデルを作るコンストラクタ。
	 * 
	 * @param aString 画像のファイル名
	 */
	public WaveletPaneModel(String aString) {
		this.label = aString;
		this.listener = null;
	}

	/**
	 * 画像(anImage)とファイル名からペインモデルを作るコンストラクタ。
	 * 
	 * @param anImage 画像
	 * @param aString ファイル名
	 */
	public WaveletPaneModel(BufferedImage anImage, String aString) {
		super(anImage);
		this.label = aString;
		this.listener = null;
	}

	/**
	 * 画像、ファイル名、モデルを指定してペインモデルを作成するコンストラクタ。
	 * 
	 * @param anImage 画像
	 * @param aString ファイル名
	 * @param aModel  関連付ける WaveletModel リスナー
	 */
	public WaveletPaneModel(BufferedImage anImage, String aString, WaveletModel aModel) {
		super(anImage);
		this.label = aString;
		this.listener = aModel;
	}

	/**
	 * アクションイベントを処理する。
	 * インタラクティブでない場合は何もしない。
	 * 
	 * @param anActionEvent 実行されたアクションイベント
	 */
	public void actionPerformed(ActionEvent anActionEvent) {
		try {
			new Condition(() -> isNotInteractive()).ifTrue(() -> {
				throw new RuntimeException();
			});
		} catch (RuntimeException anException) {
			return;
		}
		this.listener.actionPerformed(anActionEvent);
	}

	/**
	 * インタラクティブであるかどうかを判定する。
	 * リスナーが設定されていればインタラクティブ。
	 * 
	 * @return true: インタラクティブ / false: 非インタラクティブ
	 */
	public boolean isInteractive() {
		return (this.listener != null);
	}

	/**
	 * インタラクティブでないかどうかを判定する。
	 * 
	 * @return true: 非インタラクティブ / false: インタラクティブ
	 */
	public boolean isNotInteractive() {
		return (this.listener == null);
	}

	/**
	 * ペインのラベル（画像の名前など）を取得する。
	 * 
	 * @return ラベル文字列
	 */
	public String label() {
		return this.label;
	}

	/**
	 * マウスクリックイベントを処理する。
	 * インタラクティブでない場合は何もしない。
	 * 
	 * @param aPoint      クリックされた座標
	 * @param aMouseEvent クリックイベント
	 */
	public void mouseClicked(Point aPoint, MouseEvent aMouseEvent) {
		try {
			new Condition(() -> isNotInteractive()).ifTrue(() -> {
				throw new RuntimeException();
			});
		} catch (RuntimeException anException) {
			return;
		}
		this.listener.mouseClicked(aPoint, aMouseEvent);
	}

	/**
	 * マウスドラッグイベントを処理する。
	 * インタラクティブでない場合は何もしない。
	 * 
	 * @param aPoint      ドラッグ中の座標
	 * @param aMouseEvent ドラッグイベント
	 */
	public void mouseDragged(Point aPoint, MouseEvent aMouseEvent) {
		try {
			new Condition(() -> isNotInteractive()).ifTrue(() -> {
				throw new RuntimeException();
			});
		} catch (RuntimeException anException) {
			return;
		}
		this.listener.mouseDragged(aPoint, aMouseEvent);
	}

	/**
	 * ポップアップメニューを表示する。
	 * インタラクティブでない場合は何もしない。
	 * 
	 * @param aMouseEvent トリガーとなったマウスイベント
	 * @param aController ポップアップメニュー制御用コントローラ
	 */
	public void showPopupMenu(MouseEvent aMouseEvent, WaveletPaneController aController) {
		try {
			new Condition(() -> isNotInteractive()).ifTrue(() -> {
				throw new RuntimeException();
			});
		} catch (RuntimeException anException) {
			return;
		}
		this.listener.showPopupMenu(aMouseEvent, aController);
	}
}

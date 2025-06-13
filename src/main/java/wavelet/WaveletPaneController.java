package wavelet;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import pane.PaneController;

/**
 * ウェーブレット変換結果を表示するペインにおける**コントローラ**クラスである。
 * ユーザーからのマウスイベントやアクションイベントを処理し、
 * それに応じて関連する {@link WaveletPaneModel} の状態を更新したり、
 * {@link WaveletPaneView} の表示を操作したりする責任を持つ。
 *
 * <p>特に、マウスイベントによるポップアップメニュー（右クリックメニュー）の表示制御、
 * およびアクションイベントをモデルに転送する機能を提供する。</p>
 *
 * @see WaveletPaneModel
 * @see WaveletPaneView
 * @see pane.PaneController
 */
public class WaveletPaneController extends PaneController implements ActionListener {

    /**
     * ポップアップメニュー（右クリックメニュー）が表示中であるかを示すフラグである。
     * これが {@code true} の場合、マウスイベント処理は親クラスに委譲されず、
     * メニュー表示が優先される。
     */
    private boolean isMenuPopuping = false;
    
    /**
     * 上位クラスのコンストラクタを継承するだけのコンストラクタである。
     * 特段の初期化処理は行わない。
     */
    public WaveletPaneController() {
        // 親クラスのコンストラクタが自動的に呼び出される
    }

    /**
     * アクションイベントが発生した際に呼び出される。
     * このメソッドは、発生したアクションイベントを関連付けられた
     * {@link WaveletPaneModel} の {@code actionPerformed} メソッドに転送し、
     * イベント処理をモデルに委譲する。
     *
     * @param anActionEvent 発生したアクションイベント
     */
    @Override
    public void actionPerformed(ActionEvent anActionEvent) {
        // アクションコマンド文字列を取得（デバッグ用途）
        // String string = anActionEvent.getActionCommand();

        // modelをWaveletPaneModel にキャスト
        WaveletPaneModel aWaveletPaneModel = (WaveletPaneModel)this.model;

        // aWaveletPaneModelのactionPerformedメソッドを呼び出し、イベント処理を委譲する
        aWaveletPaneModel.actionPerformed(anActionEvent);
    }

    /**
     * マウスのボタンがクリックされた際に呼び出される。
     * ポップアップメニューが表示中の場合、クリックイベントは無視され、
     * 親クラスの {@code mouseClicked} メソッドは呼び出されない。
     *
     * @param aMouseEvent マウスイベント
     */
    @Override
    public void mouseClicked(MouseEvent aMouseEvent) {
        // メニューをポップアップ中の場合、親クラスを呼び出さずに処理を終了する
        if (this.isMenuPopuping) {
            this.isMenuPopuping = false; // メニューが表示された後にクリックされたため、フラグをリセット
            return;
        } 
        super.mouseClicked(aMouseEvent); // 親クラスのクリック処理を呼び出す
    }

    /**
     * マウスがドラッグされた際に呼び出される。
     * ポップアップメニューが表示中の場合、ドラッグイベントは無視され、
     * 親クラスの {@code mouseDragged} メソッドは呼び出されない。
     *
     * @param aMouseEvent マウスイベント
     */
    @Override
    public void mouseDragged(MouseEvent aMouseEvent) {
        // メニューをポップアップ中の場合、親クラスを呼び出さずに処理を終了する
        if (this.isMenuPopuping) {
            return; 
        }
        super.mouseDragged(aMouseEvent); // 親クラスのドラッグ処理を呼び出す
    }

    /**
     * マウスが移動したが、ボタンが押されていない場合に呼び出される。
     * 現在の実装では特に処理を行わない。
     * @param aMouseEvent マウスイベント
     */
    @Override
    public void mouseMoved(MouseEvent aMouseEvent) {
        // 現在は何も行わない
    }

    /**
     * マウスのボタンが押された瞬間に呼び出される。
     * 親クラスの処理を呼び出した後、イベントがポップアップメニュー表示のトリガーであるか判断する。
     * ポップアップトリガーの場合、{@link #showPopupMenu(MouseEvent)} を呼び出してメニューを表示し、
     * {@link #isMenuPopuping} フラグを {@code true} に設定する。
     * それ以外の場合、フラグは {@code false} に設定される。
     *
     * @param aMouseEvent マウスイベント
     */
    @Override
    public void mousePressed(MouseEvent aMouseEvent) {
        // 親クラスのmousePressedを呼び出す
        super.mousePressed(aMouseEvent);
        // ポップアップメニュー(右クリックメニュー)を表示するべきイベントかどうかを判断する
        if (aMouseEvent.isPopupTrigger()) {
            // ポップアップメニューを表示する
            showPopupMenu(aMouseEvent);
        } else {
            this.isMenuPopuping = false; // ポップアップトリガーでない場合はフラグをリセット
        } 
    }

    /**
     * マウスのボタンが離された際に呼び出される。
     * 親クラスの処理を呼び出した後、イベントがポップアップメニュー表示のトリガーであるか判断する。
     * ポップアップトリガーの場合、{@link #showPopupMenu(MouseEvent)} を呼び出してメニューを表示する。
     * @param aMouseEvent マウスイベント
     */
    @Override
    public void mouseReleased(MouseEvent aMouseEvent) {
        super.mouseReleased(aMouseEvent);
        if (aMouseEvent.isPopupTrigger()) {
            showPopupMenu(aMouseEvent);
        }
    }

    /**
     * ポップアップメニューを表示する。
     * 関連付けられた {@link WaveletPaneModel} の {@code showPopupMenu} メソッドを呼び出し、
     * ポップアップメニューの表示をモデルに委譲する。
     * このメソッドが呼び出された後、{@link #isMenuPopuping} フラグは {@code true} に設定される。
     *
     * @param aMouseEvent ポップアップメニュー表示のトリガーとなったマウスイベント
     */
    public void showPopupMenu(MouseEvent aMouseEvent) {
        WaveletPaneModel waveletPaneModel = (WaveletPaneModel)this.model;
        waveletPaneModel.showPopupMenu(aMouseEvent, this);
        this.isMenuPopuping = true;
    }
}
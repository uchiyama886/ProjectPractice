package wavelet;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import pane.PaneView;

/**
 * ウェーブレット変換結果を表示するためのビュークラスである。
 * {@link pane.PaneView} を拡張し、画像データに加えて、
 * 現在のビューが表すウェーブレット成分を示すテキストラベルを描画する機能を提供する。
 *
 * <p>本ビューは、関連付けられた {@link WaveletPaneModel} から画像データと表示ラベルを取得し、
 * それらを GUI 上にレンダリングする責任を持つ。
 * ユーザーからの入力イベント処理は、関連付けられた {@link WaveletPaneController} に委譲される。</p>
 *
 * @see WaveletPaneModel
 * @see WaveletPaneController
 * @see pane.PaneView
 */
public class WaveletPaneView extends PaneView {

  /**
   * 指定されたモデルを用いて {@code WaveletPaneView} の新しいインスタンスを構築する。
   * コントローラは自動的に新しい {@link WaveletPaneController} が割り当てられる。
   *
   * @param paramWaveletPaneModel このビューに関連付けられる {@link WaveletPaneModel} インスタンス
   */
  public WaveletPaneView(WaveletPaneModel paramWaveletPaneModel) {
    super(paramWaveletPaneModel, new WaveletPaneController());
  }

  /**
   * 指定されたモデルとコントローラを用いて {@code WaveletPaneView} の新しいインスタンスを構築する。
   *
   * @param paramWaveletPaneModel このビューに関連付けられる {@link WaveletPaneModel} インスタンス
   * @param paramWaveletPaneController このビューに関連付けられる {@link WaveletPaneController} インスタンス
   */
  public WaveletPaneView(WaveletPaneModel paramWaveletPaneModel, WaveletPaneController paramWaveletPaneController) {
    super(paramWaveletPaneModel, paramWaveletPaneController);
  }

  /**
   * このコンポーネントを描画する。
   * 親クラスの画像描画処理に加え、{@link WaveletPaneModel} から取得したテキストラベルを
   * ビューの左上隅に描画する。ラベルは縁取り効果を持つように描画される。
   *
   * @param paramGraphics 描画に使用する {@link Graphics} オブジェクト
   */
  @Override
  public void paintComponent(Graphics paramGraphics) {
    super.paintComponent(paramGraphics); // 親クラス (PaneView) の描画処理を呼び出す

    Font font = new Font("MonoSpaced", Font.PLAIN, 12); // フォントを設定
    paramGraphics.setFont(font);

    WaveletPaneModel waveletPaneModel = (WaveletPaneModel)getModel(); // モデルを取得
    int i = font.getSize(); // フォントサイズを取得

    String str = waveletPaneModel.label(); // モデルから表示ラベルを取得

    // ラベルに縁取り効果を付けるために、白色で複数回オフセットして描画する
    paramGraphics.setColor(Color.white);
    paramGraphics.drawString(str, 1, i + 1);
    paramGraphics.drawString(str, 2, i + 1);
    paramGraphics.drawString(str, 3, i + 1);
    paramGraphics.drawString(str, 1, i + 2);
    paramGraphics.drawString(str, 3, i + 2);
    paramGraphics.drawString(str, 1, i + 3);
    paramGraphics.drawString(str, 2, i + 3);
    paramGraphics.drawString(str, 3, i + 3);

    // 最後に黒色で中心のテキストを描画する
    paramGraphics.setColor(Color.black);
    paramGraphics.drawString(str, 2, i + 2);
  }
}
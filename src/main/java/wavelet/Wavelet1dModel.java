package wavelet;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import static java.lang.Math.round;
import java.util.Arrays;
import java.util.stream.IntStream;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

/**
 * 1次ウェーブレット変換のモデルクラス。
 * データの変換、表示、およびユーザーインタラクションによる操作を管理する
*/
public class Wavelet1dModel extends WaveletModel {

        // 各種係数を保持する配列
        /**
        * 元の信号または入力データ系列の係数を保持する。
        * このデータに対してウェーブレット変換が適用される。
        */
	protected double[] sourceCoefficients;	// 元の信号の係数
        /**
        * ウェーブレット分解によって得られるスケーリング係数（近似係数）を保持する。
        * これは信号の低周波成分や滑らかな部分を表す。
        */
	protected double[] scalingCoefficients;	// スケーリング係数（低周波成分）
        /**
        * ウェーブレット分解によって得られるウェーブレット係数（詳細係数）を保持する。
        * これは信号の高周波成分やエッジなどの詳細部分を表す。
        */
	protected double[] waveletCoefficients;	// ウェーブレット係数（高周波成分）
        /**
        * ユーザーが操作可能なウェーブレット係数を保持する。
        * インタラクティブな操作（例: 特定の係数をゼロにする）による信号の変化をシミュレートするために使用される。
        */
	protected double[] interactiveWaveletCoefficients; // ユーザーが操作可能なウェーブレット係数
        /**
        * スケーリング係数と（操作後の）ウェーブレット係数から再構成された信号の係数を保持する
        */
	protected double[] recomposedCoefficients; // 再構成された信号の係数

	// 各係数データを表示するためのペインモデル
        /**
        * 元の係数データを表示するためのインスタンス。
        * このモデルを通じて、元の信号がUIに視覚化される。
        */
	protected WaveletPaneModel sourceCoefficientsPaneModel = null;	 // 元の係数表示用のペインモデル
        /**
        * スケーリング係数データを表示するためのWaveletPaneModelインスタンス。
        * このモデルを通じて、分解された信号の低周波成分がUIに視覚化される。
        */
	protected WaveletPaneModel scalingCoefficientsPaneModel = null;	// スケーリング係数表示用のペインモデル
        /**
        * ウェーブレット係数データを表示するための{@link WaveletPaneModel}インスタンス。
        * このモデルを通じて、分解された信号の高周波成分がUIに視覚化される。
        */
	protected WaveletPaneModel waveletCoefficientsPaneModel = null;	// ウェーブレット係数表示用のペインモデル
        /**
        * ユーザーが操作可能なウェーブレット係数データを表示するための{@link WaveletPaneModel}インスタンス。
        * このモデルは、ユーザーのインタラクションを処理し、再構成された信号にその影響を反映させる。
        */
	protected WaveletPaneModel interactiveWaveletCoefficientsPaneModel = null; // ユーザー操作ウェーブレット係数表示用のペインモデル
        /**
        * 再構成された係数データを表示するための{@link WaveletPaneModel}インスタンス。
        * このモデルを通じて、ウェーブレット係数から再構築された信号がUIに視覚化される。
        */
	protected WaveletPaneModel recomposedCoefficientsPaneModel = null;	// 再構成された係数表示用のペインモデル

	// 画像描画に関する静的設定値
        /**
        * 描画時のスケールファクターを定義する静的{@link Point}オブジェクト。
        * x成分はX軸方向の、y成分はY軸方向の拡大率（ピクセル/単位）を示す。
        */
	private static Point scaleFactor = new Point(10, 100);	// 描画時のスケールファクター
        /**
        * 描画範囲の最大絶対値を定義する静的な値。
        * グラフのY軸表示範囲を決定するために使用される。
        */
	private static double rangeValue = 2.8d;	// 描画範囲の値

    /**
     * 初期状態でサンプル係数を生成し、設定する。
     */
	public Wavelet1dModel() {
		doSampleCoefficients();	// サンプル係数を初期化
	}

    /**
     * アクションイベントを処理する。
     * イベントコマンドに基づいて異なる係数操作を実行する。
     */
	public void actionPerformed(ActionEvent anActionEvent) {
		String string = anActionEvent.getActionCommand();	 // アクションコマンドを取得
		// アクションコマンドに応じて適切なメソッドを呼び出す
		if (string == "sample coefficients") {
		doSampleCoefficients();
		return;
		} 
		if (string == "all coefficients") {
		doAllCoefficients();
		return;
		} 
		if (string == "clear coefficients") {
		doClearCoefficients();
		return;
		}
	}

    /**
     * 指定されたポイントに基づいて、インタラクティブなウェーブレット係数を計算する。
     * Altキーが押されているかどうかに応じて、係数を0にするか元の値に戻すかを決定する。
     */
	public void computeFromPoint(Point aPoint, boolean isAltDown) {
		Integer maxIndex = this.interactiveWaveletCoefficients.length - 1;	// 係数配列の最大インデックス
		Integer coefficientIndex = Math.min(Math.max(aPoint.x / scaleFactor.x, 0), maxIndex);	// クリック位置から係数インデックスを計算
		// Altキーの状態に応じて係数を設定
		if (isAltDown) {
		this.interactiveWaveletCoefficients[coefficientIndex] = 0.0D;	// Altキーが押されていれば0に設定
		} else {
		this.interactiveWaveletCoefficients[coefficientIndex] = this.waveletCoefficients[coefficientIndex];	// 押されていなければ元の値に戻す
		} 
		computeRecomposedCoefficients();	// 再構成された係数を計算し、更新
	}

    /**
     * インタラクティブなウェーブレット係数から信号を再構成し、
     * 関連するペインモデルを更新する。
     */
	public void computeRecomposedCoefficients() {
        // インタラクティブな係数から1次元ウェーブレット変換を再構成
		DiscreteWavelet1dTransformation discreteWavelet1dTransformation = new DiscreteWavelet1dTransformation(this.scalingCoefficients, this.interactiveWaveletCoefficients);
		this.recomposedCoefficients = discreteWavelet1dTransformation.recomposedCoefficients();

        // インタラクティブウェーブレット係数の画像を生成し、ペインモデルを更新
		BufferedImage bufferedImage1 = generateImage(this.interactiveWaveletCoefficients);
		this.interactiveWaveletCoefficientsPaneModel.picture(bufferedImage1);
		this.interactiveWaveletCoefficientsPaneModel.changed();

        // 再構成された係数の画像を生成し、ペインモデルを更新
		BufferedImage bufferedImage2 = generateImage(this.recomposedCoefficients);
		this.recomposedCoefficientsPaneModel.picture(bufferedImage2);
		this.recomposedCoefficientsPaneModel.changed();
	}

        /**
        * サンプルの係数データを生成して返す。
        * 特定のパターンを持つダブル配列を生成する。
        @return 特定のパターンで初期化された64要素の配列
        */
	public static double[] dataSampleCoefficients() {
        double[] arrayOfDouble = new double[64]; // 64要素のダブル配列を初期化
        Arrays.fill(arrayOfDouble, 0.0D); // 全ての要素を0.0で埋める

        // 各セクションの値を設定
        IntStream.range(0, 16)
                .forEach(index -> arrayOfDouble[index] = Math.pow((index + 1), 2.0D) / 256.0D); // 最初の16要素を設定
        IntStream.range(16, 32)
                .forEach(index -> arrayOfDouble[index] = 0.2D); // 次の16要素を設定
        IntStream.range(32, 48)
                .forEach(index -> arrayOfDouble[index] = Math.pow((48 - index + 1), 2.0D) / 256.0D - 0.5D); // 次の16要素を設定

        return arrayOfDouble; // 生成されたサンプル係数配列を返す
	}

    /**
     * 全てのウェーブレット係数をインタラクティブな係数にコピーする。
     */
	public void doAllCoefficients() {
        // ウェーブレット係数をインタラクティブな係数にコピー
        IntStream.range(0, this.waveletCoefficients.length)
                .forEach(index -> this.interactiveWaveletCoefficients[index] = this.waveletCoefficients[index]);
        computeRecomposedCoefficients(); // 再構成された係数を計算し、更新
	}

    /**
     * インタラクティブなウェーブレット係数を全てクリア（0に設定）する。
     */
	public void doClearCoefficients() {
        fill(this.interactiveWaveletCoefficients, 0.0D); // インタラクティブな係数を0で埋める
        computeRecomposedCoefficients(); // 再構成された係数を計算し、更新
	}

    /**
     * サンプル係数を生成し、現在のソースデータとして設定する。
     */
	public void doSampleCoefficients() {
        setSourceData(dataSampleCoefficients()); // サンプル係数を生成し、ソースデータとして設定
	}

        /**
        * 指定された配列を特定の値で埋める。
        *
        * @param anArray 要素を埋める対象の型配列
        * @param aValue 配列の全要素に設定する値
        */
	public static void fill(double[] anArray, double aValue) {
        Arrays.fill(anArray, aValue); // Arrays.fillメソッドを使用して配列を埋める
	}

        /**
        * double配列のデータから画像を生成する。
        * データは折れ線グラフとして描画される。
        * @param valueCollection 画像生成に使用する型のデータ配列
        * @return 生成されたオブジェクト
        */
	public static BufferedImage generateImage(double[] valueCollection) {
        Integer dataLength = valueCollection.length; // データコレクションの要素数
        Integer imageWidth = (Integer) Math.round(dataLength * scaleFactor.x); // 画像の幅を計算
        Integer imageHeight = (Integer) Math.round((float) (rangeValue * scaleFactor.y)); // 画像の高さを計算
        BufferedImage bufferedImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB); // RGBタイプの画像を生成
        Graphics2D graphics2D = bufferedImage.createGraphics(); // Graphics2Dオブジェクトを取得

        graphics2D.setColor(Color.white); // 白で背景を塗りつぶす
        graphics2D.fillRect(0, 0, imageWidth, imageHeight);
        graphics2D.setColor(Color.gray); // 灰色で描画色を設定
        graphics2D.setStroke(new BasicStroke(1.0F)); // ストロークの太さを設定
        graphics2D.drawLine(0, imageHeight / 2, imageWidth, imageHeight / 2); // 中央に基準線を描画

        // 各データポイントを画像に描画（forループをIntStreamで書き換え）
        IntStream.range(0, dataLength)
                .forEach(dataIndex -> {
                    double dataValue = valueCollection[dataIndex]; // 現在のデータ値
                    Integer plotX = (Integer) Math.round((float) (dataIndex * scaleFactor.x + scaleFactor.x / 2.0D)); // X座標を計算
                    Integer plotY = (Integer) round((float) ((0.0D - dataValue) * scaleFactor.y + imageHeight / 2.0D)); // Y座標を計算 (Y軸反転)
                    Rectangle rectangle = new Rectangle(plotX, plotY, 1, 1); // 1x1の四角形を作成
                    rectangle.grow(2, 2); // 四角形を拡大
                    graphics2D.setColor(Color.black); // 黒で描画色を設定
                    graphics2D.fill(rectangle); // 四角形を塗りつぶす
                });

        return bufferedImage; // 生成された画像を返す
	}

    /**
     * マウスクリックイベントを処理する。
     * クリックされた点に基づいてインタラクティブな係数を更新する。
     */
	public void mouseClicked(Point aPoint, MouseEvent aMouseEvent) {
        computeFromPoint(aPoint, aMouseEvent.isAltDown()); // ポイントとAltキーの状態に基づいて係数を計算
	}

    /**
     * マウスドラッグイベントを処理する。
     * ドラッグされた点に基づいてインタラクティブな係数を継続的に更新する。
     */
	public void mouseDragged(Point aPoint, MouseEvent aMouseEvent) {
        computeFromPoint(aPoint, aMouseEvent.isAltDown()); // ポイントとAltキーの状態に基づいて係数を計算
	}

    /**
     * アプリケーションのメインウィンドウを開き、UIコンポーネントを配置する。
     */
	public void open() {
        GridBagLayout gridBagLayout = new GridBagLayout(); // GridBagLayoutを初期化
        JPanel jPanel = new JPanel(gridBagLayout); // GridBagLayoutを持つJPanelを作成
        GridBagConstraints gridBagConstraints = new GridBagConstraints(); // GridBagConstraintsを初期化
        gridBagConstraints.fill = GridBagConstraints.BOTH; // コンポーネントがセル内で両方向に拡大するように設定
        gridBagConstraints.gridwidth = 1; // グリッド幅を1に設定
        gridBagConstraints.gridheight = 1; // グリッド高さを1に設定

        // 各WaveletPaneViewの配置と追加（コードの重複を避けるため、ここでは簡略化。実際には共通メソッド化を検討）

        // ソース係数Pane
        WaveletPaneView waveletPaneViewSource = new WaveletPaneView(this.sourceCoefficientsPaneModel);
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 0.5D;
        gridBagConstraints.weighty = 0.5D;
        gridBagLayout.setConstraints(waveletPaneViewSource, gridBagConstraints);
        jPanel.add(waveletPaneViewSource);

        // スケーリング係数Pane　（右上）
        WaveletPaneView waveletPaneViewScalingTop = new WaveletPaneView(this.scalingCoefficientsPaneModel);
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 0.25D;
        gridBagConstraints.weighty = 0.5D;
        gridBagLayout.setConstraints(waveletPaneViewScalingTop, gridBagConstraints);
        jPanel.add(waveletPaneViewScalingTop);

        // Wavelet係数Pane
        WaveletPaneView waveletPaneViewWavelet = new WaveletPaneView(this.waveletCoefficientsPaneModel);
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 0.25D;
        gridBagConstraints.weighty = 0.5D;
        gridBagLayout.setConstraints(waveletPaneViewWavelet, gridBagConstraints);
        jPanel.add(waveletPaneViewWavelet);

        // 再構成係数Pane
        WaveletPaneView waveletPaneViewRecomposed = new WaveletPaneView(this.recomposedCoefficientsPaneModel);
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.weightx = 0.5D;
        gridBagConstraints.weighty = 0.5D;
        gridBagLayout.setConstraints(waveletPaneViewRecomposed, gridBagConstraints);
        jPanel.add(waveletPaneViewRecomposed);

        // スケーリング係数Pane
        WaveletPaneView waveletPaneViewScalingBottom = new WaveletPaneView(this.scalingCoefficientsPaneModel);
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.weightx = 0.25D;
        gridBagConstraints.weighty = 0.5D;
        gridBagLayout.setConstraints(waveletPaneViewScalingBottom, gridBagConstraints);
        jPanel.add(waveletPaneViewScalingBottom);

        // インタラクティブウェーブレット係数Pane
        WaveletPaneView waveletPaneViewInteractive = new WaveletPaneView(this.interactiveWaveletCoefficientsPaneModel);
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.weightx = 0.25D;
        gridBagConstraints.weighty = 0.5D;
        gridBagLayout.setConstraints(waveletPaneViewInteractive, gridBagConstraints);
        jPanel.add(waveletPaneViewInteractive);

        JFrame jFrame = new JFrame("Wavelet Transform (1D)"); // 新しいJFrameを作成
        jFrame.getContentPane().add(jPanel); // JFrameのコンテンツペインにJPanelを追加
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // クローズボタンでアプリケーションを終了
        jFrame.pack(); // コンポーネントの推奨サイズに合わせてフレームのサイズを調整 (addNotify()の代わりにpack()を推奨)
        // jFrame.addNotify(); // レガシーな呼び出し、pack()で通常不要
        Integer i = jFrame.getInsets().top; // フレームの上部インセットを取得
        jFrame.setMinimumSize(new Dimension(500, 200 + i)); // 最小サイズを設定
        jFrame.setResizable(true); // リサイズ可能に設定
        jFrame.setSize(1000, 400 + i); // サイズを設定
        jFrame.setLocationRelativeTo(null); // 画面中央に配置
        jFrame.setVisible(true); // フレームを表示
        jFrame.toFront(); // フレームを最前面に表示
	}

        /**
        * ソースデータを設定し、ウェーブレット変換を実行して、関連するペインモデルを更新する。
        * @param sourceDataArray 変換の元となる{@code double}型のデータ配列
        */
	public void setSourceData(double[] sourceDataArray) {
	        this.sourceCoefficients = sourceDataArray; // ソースデータを設定

        // 1次元ウェーブレット変換を実行
        DiscreteWavelet1dTransformation discreteWavelet1dTransformation1 = new DiscreteWavelet1dTransformation(this.sourceCoefficients);
        this.scalingCoefficients = discreteWavelet1dTransformation1.scalingCoefficients(); // スケーリング係数を取得
        this.waveletCoefficients = discreteWavelet1dTransformation1.waveletCoefficients(); // ウェーブレット係数を取得

        // インタラクティブウェーブレット係数配列を初期化し、0で埋める
        this.interactiveWaveletCoefficients = new double[this.waveletCoefficients.length];
        fill(this.interactiveWaveletCoefficients, 0.0D);

        // 再構成された係数を計算
        DiscreteWavelet1dTransformation discreteWavelet1dTransformation2 = new DiscreteWavelet1dTransformation(this.scalingCoefficients, this.interactiveWaveletCoefficients);
        this.recomposedCoefficients = discreteWavelet1dTransformation2.recomposedCoefficients();

        // 各係数から画像を生成
        BufferedImage bufferedImage1 = generateImage(this.sourceCoefficients);
        BufferedImage bufferedImage2 = generateImage(this.scalingCoefficients);
        BufferedImage bufferedImage3 = generateImage(this.waveletCoefficients);
        BufferedImage bufferedImage4 = generateImage(this.interactiveWaveletCoefficients);
        BufferedImage bufferedImage5 = generateImage(this.recomposedCoefficients);

		// 各ペインモデルを初期化し、画像を更新
		if (this.sourceCoefficientsPaneModel == null)
		this.sourceCoefficientsPaneModel = new WaveletPaneModel(null, "Source Coefficients"); 
		this.sourceCoefficientsPaneModel.picture(bufferedImage1);
		if (this.scalingCoefficientsPaneModel == null)
		this.scalingCoefficientsPaneModel = new WaveletPaneModel(null, "Scaling Coefficients"); 
		this.scalingCoefficientsPaneModel.picture(bufferedImage2);
		if (this.waveletCoefficientsPaneModel == null)
		this.waveletCoefficientsPaneModel = new WaveletPaneModel(null, "Wavelet Coefficients"); 
		this.waveletCoefficientsPaneModel.picture(bufferedImage3);
		if (this.interactiveWaveletCoefficientsPaneModel == null)
		this.interactiveWaveletCoefficientsPaneModel = new WaveletPaneModel(null, "Interactive Wavelet Coefficients", this); 
		this.interactiveWaveletCoefficientsPaneModel.picture(bufferedImage4);
		if (this.recomposedCoefficientsPaneModel == null)
		this.recomposedCoefficientsPaneModel = new WaveletPaneModel(null, "Recomposed Coefficients"); 
		this.recomposedCoefficientsPaneModel.picture(bufferedImage5);

        // 各ペインモデルに変更を通知
		this.sourceCoefficientsPaneModel.changed();
		this.scalingCoefficientsPaneModel.changed();
		this.waveletCoefficientsPaneModel.changed();
		this.interactiveWaveletCoefficientsPaneModel.changed();
		this.recomposedCoefficientsPaneModel.changed();
	}

    /**
     * ポップアップメニューを表示する。
     * メニューアイテムにアクションリスナーを設定する。
     */
	public void showPopupMenu(MouseEvent aMouseEvent, WaveletPaneController aController) {
        Integer mouseX = aMouseEvent.getX(); // マウスイベントのX座標
        Integer mouseY = aMouseEvent.getY(); // マウスイベントのY座標
        Cursor defaultCursor = Cursor.getDefaultCursor(); // デフォルトのカーソルを取得
        Component eventComponent = aMouseEvent.getComponent(); // イベント発生元のコンポーネントを取得
        eventComponent.setCursor(defaultCursor); // コンポーネントのカーソルをデフォルトに戻す

        JPopupMenu popupMenu = new JPopupMenu(); // 新しいJPopupMenuを作成

        // メニューアイテムの追加とアクションリスナーの設定
        JMenuItem sampleMenuItem = new JMenuItem("sample coefficients");
        sampleMenuItem.addActionListener(aController);
        popupMenu.add(sampleMenuItem);

        popupMenu.addSeparator(); // セパレータを追加

        JMenuItem allCoefficientsMenuItem = new JMenuItem("all coefficients");
        allCoefficientsMenuItem.addActionListener(aController);
        popupMenu.add(allCoefficientsMenuItem);

        JMenuItem clearCoefficientsMenuItem = new JMenuItem("clear coefficients");
        clearCoefficientsMenuItem.addActionListener(aController);
        popupMenu.add(clearCoefficientsMenuItem);

        popupMenu.show(eventComponent, mouseX, mouseY); // 指定された位置にポップアップメニューを表示
	}
}

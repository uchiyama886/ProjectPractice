package wavelet;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.lang.ModuleLayer.Controller;

import javax.swing.JPanel;

public class PaneView extends JPanel{

    // ビューに対応するデータ
    protected Model model;
    // イベント処理をするコントローラ
    protected Controller controller;
    // 画像を描画する場所を決める
    private Point offset;

    /* PaneView */
    // 画像の表示開始位置
    private Point2D.Double originPoint;
    // 拡大縮小比率
    private Point2D.Double scaleFactor;

    public PaneView(PaneModel aPaneModel, PaneController aPaneController) {
        // モデルを受け取る
        this.model = aPaneModel;
        // モデルにこのビューを依存先として登録
        this.model.addDependent(this);
        // 外部から渡されたコントローラを用いる
        this.controller = aPaneController;
        // コントローラにモデルを設定
        this.controller.setModel(this.model);
        // コントローラにビューを渡す
        this.controller.setView(this);
        // 初期のスクロール位置を設定
        this.offset = new Point(0, 0);

        intialize();// 初期化
    }

    // 初期化
    public void intialize() {
        this.originPoint = new Point2D.Double(0.0D, 0.0D);
        this.scaleFactor = new Point2D.Double(1.0D, 1.0D);
    }

    // モデルを所得
    public PaneModel getModel() {
        return (PaneModel) this.model;
    }

    // ビュー状のスクロール量を加算し座標に変換
    public Point convertViewPointToModelPoint(Point paramPoint) {
        Point point = scrollAmount();
        return new Point(paramPoint.x + point.x, paramPoint.y + point.y);
    }

    // ビュー座標 → モデル座標 → 画像座標に変換
    public Point convertViewPointToPicturePoint(Point paramPoint) {
        Point point = convertViewPointToModelPoint(paramPoint);
        return convertModelPointToPicturePoint(point);
    }

    // 描画メソッド
    public Point convertModelPointToPicturePoint(Point paramPoint) {
        // モデルを呼び出し、画像を所得
        PaneModel paneModel = getModel();
        BufferedImage bufferedImage = paneModel.picture();// モデル座標を画像座標に変換
        if (bufferedImage == null)
            return null;
        // ウィンドウサイズに合わせて、画像を拡大縮小比率を決定
        double d1 = (paramPoint.x - this.originPoint.x) / this.scaleFactor.x;
        double d2 = (paramPoint.y - this.originPoint.y) / this.scaleFactor.y;
        int i = (int) d1;
        int j = (int) d2;
        if (i < 0)
            return null;
        if (j < 0)
            return null;
        // 画像をリサイズするユーティリティクラスを使って画像を変形
        int k = bufferedImage.getWidth();
        int m = bufferedImage.getHeight();
        return (i > k) ? null : ((j > m) ? null : new Point(i, j));
    }

    public void paintComponent(Graphics paramGraphics) {
        int i = getWidth();
        int j = getHeight();
        paramGraphics.setColor(Color.white);// 背景を白に
        paramGraphics.fillRect(0, 0, i, j);
        if (this.model == null)
            return;
        BufferedImage bufferedImage = this.model.picture();// モデルから画像所得
        if (bufferedImage == null)
            return;
        int k = bufferedImage.getWidth();
        int m = bufferedImage.getHeight();
        // アスペクト比を維持して画像サイズを決定
        double d1 = i / k;
        double d2 = j / m;
        if (d1 > d2) {
            d1 = d2;
        } else {
            d2 = d1;
        }
        this.scaleFactor = new Point2D.Double(d1, d2);
        k = (int) (k * d1);
        m = (int) (m * d2);
        // 画像をリサイズ
        bufferedImage = ImageUtility.adjustImage(bufferedImage, k, m);
        d1 = (i - k) / 2.0D;
        d2 = (j - m) / 2.0D;
        // 画像をパネル中央に配置
        this.originPoint = new Point2D.Double(d1, d2);
        paramGraphics.drawImage(bufferedImage, (int) d1, (int) d2, null);// 描画
    }

    // スクロール関連
    // 現在のスクロール量
    public Point scrollAmount() {
        return new Point(0, 0);
    }

    /*
     * 
     * // スクロール機能のための空メソッド
     * public void scrollBy(Point aPoint) {
     * }
     * 
     * public void scrollTo(Point aPoint) {
     * }
     * 
     */
}

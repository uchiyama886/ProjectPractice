package wavelet;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.lang.ModuleLayer.Controller;

import javax.swing.JPanel;

public class PaneView extends JPanel {

    /* PaneView */
    // 画像の表示開始位置
    private Point2D.Double originPoint;
    // 拡大縮小比率
    private Point2D.Double scaleFactor;

    public PaneView(PaneModel aPaneModel, PaneController aPaneController) {
        super(aPaneModel, aPaneController);
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

        // 画像をリサイズするユーティリティクラスを使って画像を変形
        int k = bufferedImage.getWidth();
        int m = bufferedImage.getHeight();
        return (i < 0 || j < 0 || i > k || j > m) ? null : new Point(i, j);
    }

    public void paintComponent(Graphics aGraphics) {
        int i = getWidth();
        int j = getHeight();
        // 背景を白に
        aGraphics.setColor(Color.white);
        aGraphics.fillRect(0, 0, i, j);
        BufferedImage bufferedImage = (model == null) ? null : model.picture();

        // false なら return
        while (bufferedImage != null) {
            int k = bufferedImage.getWidth();
            int m = bufferedImage.getHeight();
            // アスペクト比を維持して画像サイズを決定
            double d1 = (double) i / k;
            double d2 = (double) j / m;
            double scale = (d1 > d2) ? d2 : d1;

            this.scaleFactor = new Point2D.Double(scale, scale);
            k = (int) (k * scale);
            m = (int) (m * scale);
            // 画像をリサイズ
            bufferedImage = ImageUtility.adjustImage(bufferedImage, k, m);
            double x = (i - k) / 2.0;
            double y = (j - m) / 2.0;
            // 画像をパネル中央に配置
            this.originPoint = new Point2D.Double(x, y);
            aGraphics.drawImage(bufferedImage, (int) x, (int) y, null);
            break;// whileは1回のみ
        }
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

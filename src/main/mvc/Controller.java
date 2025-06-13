package mvc;

// すべてのGUI部品の大元となる抽象クラス
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import javax.swing.event.MouseInputAdapter;

/**
 * MVCデザインパターンにおける、汎用的なコントローラクラス。
 * ユーザーからのマウス入力（クリック、ドラッグ、ホイール回転）を検知し、
 * それに応じてビュー（View）の状態を操作（主にスクロール）する役割を担う。
 * ドラッグによるパンニングや、修飾キーによる複数ビューの同期スクロールといった機能を提供。
 */
public class Controller extends MouseInputAdapter implements MouseWheelListener {
  /**
   * このコントローラが操作するモデル。
   */
  protected Model model = null;
  
  /**
   * このコントローラが主に関連付けられているビュー。
   */
  protected View view = null;
  
  /**
   * ドラッグ操作時の、前回のマウス座標を保持します。
   */
  private Point previous = null;
  
  /**
   * ドラッグ操作時の、現在のマウス座標を保持します。
   */
  private Point current = null;
  
  /**
   * マウスクリックイベントを処理する。
   * クリックされた画面上の座標を、ビューのスクロール量を考慮したモデル上の座標に変換し、
   * その座標を標準出力に出力する。
   * @param aMouseEvent AWTから通知されるマウスイベントオブジェクト
   */
  @Override
  public void mouseClicked(MouseEvent aMouseEvent) {
    Point point = aMouseEvent.getPoint();
    point.translate((this.view.scrollAmount()).x, (this.view.scrollAmount()).y);
    System.out.println(point);
  }

  /**
   * マウスドラッグイベントを処理する。
   * カーソルを移動中の形状に変更し、前回の座標と現在の座標の差分から移動量を計算します。
   * 計算された移動量をもとにscrollBy(Point, MouseEvent)メソッドを呼び出し、ビューをスクロールさせる。
   * @param aMouseEvent AWTから通知されるマウスイベントオブジェクト
   */
  @Override
  public void mouseDragged(MouseEvent aMouseEvent) {
    Cursor cursor = Cursor.getPredefinedCursor(13);
    Component component = (Component)aMouseEvent.getSource();
    component.setCursor(cursor);
    this.current = aMouseEvent.getPoint();
    Integer integer1 = Integer.valueOf(this.current.x - this.previous.x);
    Integer integer2 = Integer.valueOf(this.current.y - this.previous.y);
    Point point = new Point(integer1.intValue(), integer2.intValue());
    scrollBy(point, aMouseEvent);
    this.previous = this.current;
  }
  
  /**
   * マウスがコンポーネントの領域内に入った際のイベントを処理する。
   * @param aMouseEvent AWTから通知されるマウスイベントオブジェクト
   */
  @Override
  public void mouseEntered(MouseEvent aMouseEvent) {}
  
    /**
   * マウスがコンポーネントの領域外に出た際のイベントを処理する。
   * @param aMouseEvent AWTから通知されるマウスイベントオブジェクト
   */
  @Override
  public void mouseExited(MouseEvent aMouseEvent) {}
  
  /**
   * マウスがボタンを押さずに移動した際のイベントを処理する。
   * @param aMouseEvent AWTから通知されるマウスイベントオブジェクト
   */
  @Override
  public void mouseMoved(MouseEvent aMouseEvent) {}
  
  /**
   * マウスのボタンが押された瞬間のイベントを処理する。
   * カーソルの形状を変更し、ドラッグ操作の開始点となる座標を記録する。
   * @param aMouseEvent AWTから通知されるマウスイベントオブジェクト
   */
  @Override
  public void mousePressed(MouseEvent aMouseEvent) {
    Cursor cursor = Cursor.getPredefinedCursor(1);
    Component component = (Component)aMouseEvent.getSource();
    component.setCursor(cursor);
    this.current = aMouseEvent.getPoint();
    this.previous = this.current;
  }
  
  /**
   * マウスのボタンが離された瞬間のイベントを処理する。
   * カーソルの形状をデフォルトに戻す。
   * @param aMouseEvent AWTから通知されるマウスイベントオブジェクト
   */
  @Override
  public void mouseReleased(MouseEvent aMouseEvent) {
    Cursor cursor = Cursor.getDefaultCursor();
    Component component = (Component)aMouseEvent.getSource();
    component.setCursor(cursor);
    this.current = aMouseEvent.getPoint();
    this.previous = this.current;
  }
  
  /**
   * マウスホイールが回転した際のイベントを処理する。
   * ホイールの回転量を取得し、スクロール量を計算する。
   * Shiftキーが押されている場合は、縦スクロールを横スクロールに切り替える。
   * @param aMouseWheelEvent AWTから通知されるマウスホイールイベントオブジェクト
   */
  @Override
  public void mouseWheelMoved(MouseWheelEvent aMouseWheelEvent) {
    Integer integer1 = Integer.valueOf(-aMouseWheelEvent.getWheelRotation());
    if (integer1.intValue() == 0)
      return; 
    Point point = new Point(0, integer1.intValue());
    Integer integer2 = Integer.valueOf(aMouseWheelEvent.getModifiersEx());
    Boolean bool = Boolean.valueOf(((integer2.intValue() & 0x200) != 0));
    if (bool.booleanValue())
      point = new Point(integer1.intValue(), 0); 
    scrollBy(point, aMouseWheelEvent);
  }
  
  /**
   * 指定された量だけビューをスクロールさせる。
   * このコントローラが関連付けられたビューをスクロールさせ、再描画する。
   * @param aPoint スクロールさせるx方向およびy方向の移動量
   * @param aMouseEvent 修飾キーの状態を確認するための元のマウスイベント
   */
  public void scrollBy(Point aPoint, MouseEvent aMouseEvent) {
    this.view.scrollBy(aPoint);
    this.view.repaint();
    Integer integer = Integer.valueOf(aMouseEvent.getModifiersEx());
    Boolean bool1 = Boolean.valueOf(((integer.intValue() & 0x40) != 0));
    if (!bool1.booleanValue())
      return; 
    Point point1 = this.view.scrollAmount();
    Point point2 = new Point(0 - point1.x, 0 - point1.y);
    Boolean bool2 = Boolean.valueOf(((integer.intValue() & 0x100) != 0));
    for (View view : this.view.model.dependents) {
      if (view != this.view) {
        if (!bool2.booleanValue()) {
          view.scrollBy(aPoint);
        } else {
          view.scrollTo(point2);
        } 
        view.repaint();
      } 
    } 
  }
  
  /**
   * このコントローラにモデルを設定する。
   * @param aModel 設定するモデルオブジェクト
   */
  public void setModel(Model aModel) {
    this.model = aModel;
  }
  
  /**
   * このコントローラにビューを設定し、リスナーとして自身を登録する。
   * このメソッドは、コントローラとビューを接続し、マウスイベントの監視を開始するためのステップ。
   * @param aView 設定するビューオブジェクト
   */
  public void setView(View aView) {
    this.view = aView;
    this.view.addMouseListener(this);
    this.view.addMouseMotionListener(this);
    this.view.addMouseWheelListener(this);
  }
  
  /**
   * このコントローラの状態を表す文字列を返す。
   * @return クラス名、モデル、ビューの情報を含む文字列
   */
  @Override
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    Class<?> clazz = getClass();
    stringBuffer.append(clazz.getName());
    stringBuffer.append("[model=");
    stringBuffer.append(this.model);
    stringBuffer.append(",view=");
    stringBuffer.append(this.view);
    stringBuffer.append("]");
    return stringBuffer.toString();
  }
}

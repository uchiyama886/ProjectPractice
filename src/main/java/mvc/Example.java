package mvc;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

import utility.Condition;
import utility.ValueHolder;

/**
 * 例題プログラム。
 */
public class Example extends Object
{
	/**
	 * 画面をキャプチャして画像化し、ビューとコントローラの3つのペア
	 *（MVC-1, MVC-2, MVC-3のウィンドウたち）から1つのモデルを観測している状態を作り出す。
	 * その後、モデルの内容物を先ほどキャプチャした画像にして、
	 * 自分が変化したと騒いだ瞬間、MVC-1, MVC-2, MVC-3のすべてのウィンドウが更新される。
	 * そして、モデルの内容物をnull化して、自分が変化したと騒ぎ、すべてのウィンドウが空に更新される。
	 * この過程を何回か繰り返すことで、MVC: Model-View-Controller（Observerデザインパターン）が
	 * きちんと動いているかを確かめる例題プログラムである。
	 * @param arguments 引数の文字列の配列
	 */
	public static void main(String[] arguments)
	{
		// スクリーンのサイズを求め、スクリーン全体をキャプチャ（画像に）する。
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Robot aRobot = null;
		try { aRobot = new Robot(); }
		catch (Exception anException)
		{
			System.err.println(anException);
			throw new RuntimeException(anException);
		}
		BufferedImage anImage = aRobot.createScreenCapture(new Rectangle(screenSize));

		// ウィンドウのサイズを決め、モデルを作る。
		Dimension aDimension = new Dimension(800, 600);
		Model aModel = new Model();

		// MVCの出現数から、最初のウィンドウの出現位置を計算する。
		Integer howMany = 3; // MVCの出現回数
		Point offsetPoint = new Point(80, 60); // ウィンドウを出現させる時のオフセット(ズレ：ずらし)
		ValueHolder<Integer> width = new ValueHolder<Integer>(aDimension.width + (offsetPoint.x * (howMany - 1)));
		ValueHolder<Integer> height = new ValueHolder<Integer>(aDimension.height + (offsetPoint.y * (howMany - 1)));
		ValueHolder<Integer> x = new ValueHolder<Integer>((screenSize.width / 2) - (width.get() / 2));
		ValueHolder<Integer> y = new ValueHolder<Integer>((screenSize.height / 2) - (height.get() / 2));
		Point displayPoint = new Point(x.get(), y.get());

		// MVCを出現回数分だけ出現させる。
		ValueHolder<Integer> index = new ValueHolder<Integer>(0);
		new Condition(() -> index.get() < howMany).whileTrue(() ->
		{
			// 上記のモデルのビューとコンピュローラのペアを作り、ウィンドウに乗せる。
			View aView = new View(aModel);
			JFrame aWindow = new JFrame("MVC-" + Integer.toString(index.get() + 1));
			aWindow.getContentPane().add(aView);

			// 高さはタイトルバーの高さを考慮してウィンドウの大きさを決定する。
			aWindow.addNotify();
			Integer titleBarHeight = aWindow.getInsets().top;
			width.set(aDimension.width);
			height.set(aDimension.height + titleBarHeight);
			Dimension windowSize = new Dimension(width.get(), height.get());
			aWindow.setSize(windowSize.width, windowSize.height);

			// ウィンドウに各種の設定を行って出現させる。
			aWindow.setMinimumSize(new Dimension(400, 300 + titleBarHeight));
			aWindow.setResizable(true);
			aWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			x.set(displayPoint.x + (index.get() * offsetPoint.x));
			y.set(displayPoint.y + (index.get() * offsetPoint.y));
			aWindow.setLocation(x.get(), y.get());
			aWindow.setVisible(true);
			aWindow.toFront();
			index.setDo((Integer it) -> it + 1);
		});

		// モデルのピクチャを、奇数の時はnullに、偶数の時はスクリーン全体のキャプチャ画像にする。
		ValueHolder<Integer> count = new ValueHolder<Integer>(0);
		new Condition(() -> count.get() < (howMany * 4 - 1)).whileTrue(() ->
		{
			try { Thread.sleep(250); }
			catch (InterruptedException anException)
			{
				System.err.println(anException);
				throw new RuntimeException(anException);
			}
			new Condition(() -> count.get() % 2 == 0).ifThenElse(() ->
			{ aModel.picture(anImage); }, () ->
			{ aModel.picture(null); });
			aModel.changed();
			count.setDo((Integer it) -> it + 1);
		});

		return;
	}
}

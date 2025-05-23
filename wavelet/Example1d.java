package wavelet;

import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class Example1d extends Object {

	private static int fileNo = 0;

	private static Point displayPoint = new Point(30, 50);

	private static Point offsetPoint = new Point(25, 25);

	public static void main(String[] argument) 
	{
		JFrame frame = new JFrame("Window");
		frame.setSize(800, 600);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel panel = new JPanel();

		panel.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				System.out.println(String.format("x: %d, y: %d%n", e.getX(), e.getY()));
			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
			}
			
		});

		frame.getContentPane().add(panel);

		frame.setVisible(true);

	}

	protected static void example1() 
	{

	}

	protected static void perform(double[] sourceData) 
	{

	}

	// protected static void open(IPanel aPanel) 
	// {

	// }

	// protected static void write(BufferdImage anImage) 
	// {

	// }

}

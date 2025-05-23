package mvc;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;

public class Example {
  public static void main(String[] paramArrayOfString) {
    Dimension dimension1 = Toolkit.getDefaultToolkit().getScreenSize();
    Robot robot = null;
    try {
      robot = new Robot();
    } catch (Exception exception) {
      System.err.println(exception);
      throw new RuntimeException(exception.toString());
    } 
    BufferedImage bufferedImage = robot.createScreenCapture(new Rectangle(dimension1));
    Dimension dimension2 = new Dimension(800, 600);
    Model model = new Model();
    Integer integer1 = Integer.valueOf(3);
    Point point1 = new Point(80, 60);
    Integer integer2 = Integer.valueOf(dimension2.width + point1.x * (integer1.intValue() - 1));
    Integer integer3 = Integer.valueOf(dimension2.height + point1.y * (integer1.intValue() - 1));
    Integer integer4 = Integer.valueOf(dimension1.width / 2 - integer2.intValue() / 2);
    Integer integer5 = Integer.valueOf(dimension1.height / 2 - integer3.intValue() / 2);
    Point point2 = new Point(integer4.intValue(), integer5.intValue());
    Integer integer6 = Integer.valueOf(0);
    while (integer6.intValue() < integer1.intValue()) {
      View view = new View(model);
      JFrame jFrame = new JFrame("MVC-" + Integer.toString(integer6.intValue() + 1));
      jFrame.getContentPane().add(view);
      jFrame.addNotify();
      Integer integer9 = Integer.valueOf((jFrame.getInsets()).top);
      integer2 = Integer.valueOf(dimension2.width);
      integer3 = Integer.valueOf(dimension2.height + integer9.intValue());
      Dimension dimension = new Dimension(integer2.intValue(), integer3.intValue());
      jFrame.setSize(dimension.width, dimension.height);
      jFrame.setMinimumSize(new Dimension(400, 300 + integer9.intValue()));
      jFrame.setResizable(true);
      jFrame.setDefaultCloseOperation(3);
      integer4 = Integer.valueOf(point2.x + integer6.intValue() * point1.x);
      integer5 = Integer.valueOf(point2.y + integer6.intValue() * point1.y);
      jFrame.setLocation(integer4.intValue(), integer5.intValue());
      jFrame.setVisible(true);
      jFrame.toFront();
      Integer integer7 = integer6;
      Integer integer8 = integer6 = Integer.valueOf(integer6.intValue() + 1);
    } 
    integer6 = Integer.valueOf(0);
    while (integer6.intValue() < integer1.intValue() * 4 - 1) {
      try {
        Thread.sleep(1000L);
      } catch (InterruptedException interruptedException) {
        System.err.println(interruptedException);
        throw new RuntimeException(interruptedException.toString());
      } 
      if (integer6.intValue() % 2 == 0) {
        model.picture(bufferedImage);
      } else {
        model.picture(null);
      } 
      model.changed();
      Integer integer7 = integer6;
      Integer integer8 = integer6 = Integer.valueOf(integer6.intValue() + 1);
    } 
  }
}



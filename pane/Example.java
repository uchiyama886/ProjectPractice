package pane;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Example {
  public static void main(String[] paramArrayOfString) {
    GridLayout gridLayout = new GridLayout(2, 3);
    JPanel jPanel = new JPanel(gridLayout);
    PaneModel paneModel = new PaneModel("SampleImages/BernhardRiemann.jpg");
    PaneView paneView = new PaneView(paneModel, new PaneController());
    jPanel.add((Component)paneView);
    paneModel = new PaneModel("SampleImages/GeorgeBoole.jpg");
    paneView = new PaneView(paneModel, new PaneController());
    jPanel.add((Component)paneView);
    paneModel = new PaneModel("SampleImages/JosephFourier.jpg");
    paneView = new PaneView(paneModel, new PaneController());
    jPanel.add((Component)paneView);
    paneModel = new PaneModel("SampleImages/LeonardoFibonacci.jpg");
    paneView = new PaneView(paneModel, new PaneController());
    jPanel.add((Component)paneView);
    paneModel = new PaneModel("SampleImages/LeonhardEuler.jpg");
    paneView = new PaneView(paneModel, new PaneController());
    jPanel.add((Component)paneView);
    paneModel = new PaneModel("SampleImages/RichardDedekind.jpg");
    paneView = new PaneView(paneModel, new PaneController());
    jPanel.add((Component)paneView);
    JFrame jFrame = new JFrame("Pane");
    jFrame.getContentPane().add(jPanel);
    jFrame.setMinimumSize(new Dimension(100, 100));
    jFrame.setResizable(true);
    jFrame.setDefaultCloseOperation(3);
    jFrame.addNotify();
    int i = (jFrame.getInsets()).top;
    jFrame.setSize(400, 300 + i);
    jFrame.setLocation(230, 250);
    jFrame.setVisible(true);
    jFrame.toFront();
  }
}



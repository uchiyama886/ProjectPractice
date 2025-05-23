package mvc;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Model {
  protected ArrayList<View> dependents;
  
  private BufferedImage picture;
  
  public Model() {
    initialize();
  }
  
  public void addDependent(View paramView) {
    this.dependents.add(paramView);
  }
  
  public void changed() {
    for (View view : this.dependents)
      view.update(); 
  }
  
  private void initialize() {
    this.dependents = new ArrayList<>();
    this.picture = null;
  }
  
  public void perform() {}
  
  public BufferedImage picture() {
    return this.picture;
  }
  
  public void picture(BufferedImage paramBufferedImage) {
    this.picture = paramBufferedImage;
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    Class<?> clazz = getClass();
    stringBuffer.append(clazz.getName());
    stringBuffer.append("[picture=");
    stringBuffer.append(this.picture);
    stringBuffer.append("]");
    return stringBuffer.toString();
  }
}



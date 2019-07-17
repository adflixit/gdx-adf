package adflixit.gdx;

import java.util.ArrayList;
import java.util.List;

public class Updater {
  private List<Updatable> list = new ArrayList<>();

  public void add(Updatable a) {
    list.add(a);
  }

  public void remove(Updatable a) {
    list.remove(a);
  }

  public void update() {
    for (Updatable i : list) {
      i.update();
    }
  }
}

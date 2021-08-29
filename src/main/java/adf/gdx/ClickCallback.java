package adf.gdx;

import com.badlogic.gdx.scenes.scene2d.InputEvent;

@FunctionalInterface public interface ClickCallback {
  public void clicked(InputEvent event, float x, float y);
}

package adf.gdx;

import static adf.gdx.Logger.*;

import adf.gdx.app.TestApp;
import adf.gdx.app.TestAppContext;

import com.badlogic.gdx.Input.Keys;

public class AppWidgetTest extends TestApp {
  private class Screen extends TestAppContext {
    public Screen(TestApp al) {
      super(al);
    }

    @Override public boolean keyDown(int keycode) {
      switch (keycode) {
      case Keys.R:
        break;
      }
      return true;
    }

    @Override public boolean touchDown(int screenX, int screenY, int pointer, int button) {
      super.touchDown(screenX, screenY, pointer, button);
      log(String.format("%f %f", touch.x, touch.y));
      return true;
    }
  }

  public AppWidgetTest() {
    super();
  }

  @Override public void create() {
    super.create();
    reset();
  }

  public void reset() {
    setContext(new Screen(this));
  }

  public static void main(String[] argv) {
  launch(new AppWidgetTest());
  }
}

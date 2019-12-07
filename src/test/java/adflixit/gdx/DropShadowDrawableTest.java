package adflixit.gdx;

import static adflixit.gdx.Logger.*;

import adflixit.gdx.app.TestApp;
import adflixit.gdx.app.TestAppContext;
import com.badlogic.gdx.Input.Keys;

public class DropShadowDrawableTest extends TestApp {
  private class Screen extends TestAppContext {
    public Screen(TestApp game) {
      super(game);
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
      log(touch.x+" "+touch.y);
      return true;
    }
  }

  public DropShadowDrawableTest() {
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
    launch(new DropShadowDrawableTest());
  }
}

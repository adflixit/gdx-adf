package adf.gdx.app;

import static adf.gdx.Logger.*;

import adf.gdx.BaseContext;

public class TestAppContext extends BaseContext<TestApp> {
  private class Context extends TestAppContext {
    public Context(TestApp al) {
      super(al);
    }

    @Override public boolean touchDown(int screenX, int screenY, int pointer, int button) {
      super.touchDown(screenX, screenY, pointer, button);
      log(String.format("%f %f", touch.x, touch.y));
      return true;
    }
  }

  public TestAppContext(TestApp al) {
    super(al);
    blur.loadDefault();
  }
}

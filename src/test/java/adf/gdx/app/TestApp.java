package adf.gdx.app;

import adf.gdx.BaseAppListener;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;

public class TestApp extends BaseAppListener {
  public TestApp() {
    super();
  }

  @Override public void create() {
    super.create();
    loadSkin(internalFile("tests/data/uiskin.json"), internalFile("tests/data/textures.atlas"));
    loadProps("tests/data/cfg.properties");
    loadPrefs("test");
    Gdx.input.setCatchKey(Input.Keys.BACK, true);
  }

  public static void launch(TestApp app) {
    new LwjglApplication(app, "Test app", 360, 640);
  }
}

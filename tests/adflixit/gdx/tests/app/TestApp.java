package adflixit.gdx.tests.app;

import adflixit.gdx.BaseGame;
import adflixit.gdx.XApi;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class TestApp extends BaseGame {
  public TestApp() {
    super();
  }

  public TestApp(XApi xApi) {
    super(xApi);
  }

  @Override public void create() {
    super.create();
    loadSkin(internalFile("tests/data/uiskin.json"), internalFile("tests/data/textures.atlas"));
    loadProps("tests/data/cfg.properties");
    loadPrefs("test");
    //Gdx.input.setCatchMenuKey(true);
    Gdx.input.setCatchBackKey(true);
  }

  public static void launch(TestApp app) {
    new LwjglApplication(app, "Test App", 360, 640);
  }
}

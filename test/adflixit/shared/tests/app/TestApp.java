package adflixit.shared.tests.app;

import adflixit.shared.BaseGame;
import adflixit.shared.BaseScreen;
import adflixit.shared.XApi;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class TestApp extends BaseGame {
	public TestApp(BaseScreen<?> screen) {
		super();
		setScreen(screen);
	}

	public TestApp(XApi xApi) {
		super(xApi);
		setScreen(screen);
	}

	@Override public void create() {
		super.create();
		initSkin(internalFile("test/uiskin.json"), new TextureAtlas("test/textures.atlas"));
		loadProps(localFile("test/cfg.properties"));
		loadPrefs("test");
		//Gdx.input.setCatchMenuKey(true);
		Gdx.input.setCatchBackKey(true);
	}
}

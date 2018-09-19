package adflixit.shared.tests.app;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;

import adflixit.shared.BaseScreen;

public class TestAppScreen extends BaseScreen<TestApp> {

	public TestAppScreen(TestApp game) {
		super(game);
	}

	@Override public void goBack() {
	}

	public static void launch(BaseScreen<?> screen) {
		new LwjglApplication(new TestApp(screen), "Test App", 360, 640);
	}
}

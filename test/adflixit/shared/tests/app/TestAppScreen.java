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
		int h = 680;
		new LwjglApplication(new TestApp(screen), "Test App", (int)(h*(9/16f)), h);
	}
}

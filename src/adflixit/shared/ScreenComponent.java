package adflixit.shared;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;

public abstract class ScreenComponent<S extends BaseScreen<?>> {
	protected final S				scr;	// screen
	protected final SpriteBatch		bat;	// screen batch
	protected final Camera			cam;
	protected final Stage			ui;
	protected final Vector2			tmpv2			= new Vector2();
	protected final Vector3			tmpv3			= new Vector3();
	protected final Color			tmpclr			= new Color();
	protected boolean				firstResize		= true;

	public ScreenComponent(S screen) {
		scr	= screen;
		bat	= screen.batch;
		cam	= screen.camera;
		ui	= screen.ui;
	}
}

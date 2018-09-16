package adflixit.shared.misc;

import static adflixit.shared.BaseGame.*;
import static adflixit.shared.BaseScreen.*;
import static adflixit.shared.Defs.*;
import static adflixit.shared.TweenUtils.*;
import static com.badlogic.gdx.graphics.Color.*;

import aurelienribon.tweenengine.Timeline;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * {@link ImageButton} {@link ClickListener}. Tweens the ImageButton image color and the background color.
 */
public class IbClickListener extends ClickListener {
	private ImageButton		btn;
	private Image			image;
	private Color			initBgClr;		// initial background color
	private Color			initImageClr;	// initial image color
	private Color			bgClr;
	private Color			imageClr;

	public IbClickListener(ImageButton button, Color initBgClr, Color initImageClr, Color bgClr, Color imageClr) {
		super();
		btn = button;
		image = button.getImage();
		this.initBgClr = initBgClr != null ? initBgClr : WHITE;
		if (initBgClr != null) {
			button.setColor(initBgClr);
		}
		this.initImageClr = initImageClr != null ? initImageClr : WHITE;
		if (initImageClr != null) {
			image.setColor(initImageClr);
		}
		this.bgClr = bgClr;
		this.imageClr = imageClr;
	}

	public IbClickListener(ImageButton button, Color bgClr, Color imageClr) {
		this(button, null, null, bgClr, imageClr);
	}

	public IbClickListener(ImageButton button, Color clr) {
		this(button, clr, clr);
	}

	public IbClickListener(ImageButton button, String initBgClr, String initImageClr, String bgClr, String imageClr) {
		this(button, color(bgClr), color(initBgClr), color(imageClr), color(initImageClr));
	}

	public IbClickListener(ImageButton button, String bgClr, String imageClr) {
		this(button, color(bgClr), color(imageClr));
	}

	public IbClickListener(ImageButton button, String clr) {
		this(button, color(clr));
	}

	public IbClickListener(ImageButton button) {
		this(button, "blue");
	}

	@Override public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
		super.touchDown(event, x, y, pointer, button);
		Timeline.createParallel()
		.push($tweenActorColor(btn, bgClr, C_HD))
		.push($tweenActorColor(image, imageClr, C_HD))
		.start(tweenMgr);
		return true;
	}

	@Override public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
		super.touchUp(event, x, y, pointer, button);
		Timeline.createParallel()
		.push($tweenActorColor(btn, initBgClr, C_HD))
		.push($tweenActorColor(image, initImageClr, C_HD))
		.start(tweenMgr);
	}
}

package adflixit.shared.misc;

import static adflixit.shared.BaseGame.*;
import static adflixit.shared.Defs.*;
import static adflixit.shared.TweenUtils.*;
import static com.badlogic.gdx.graphics.Color.*;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class TintClickListener extends ClickListener {
	private Actor		actor;
	private Color		initClr;
	private Color		clr;

	public TintClickListener(Actor actor, Color initClr, Color clr) {
		super();
		this.actor = actor;
		this.initClr = initClr != null ? initClr : WHITE;
		if (initClr != null) {
			actor.setColor(initClr);
		}
		this.clr = clr;
	}

	public TintClickListener(Actor actor, Color clr) {
		this(actor, null, clr);
	}

	public TintClickListener(Actor actor, String initClr, String clr) {
		this(actor, color(initClr), color(clr));
	}

	public TintClickListener(Actor actor, String clr) {
		this(actor, color(clr));
	}

	public TintClickListener(Actor actor) {
		this(actor, "blue");
	}

	@Override public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
		super.touchDown(event, x, y, pointer, button);
		tweenActorColor(actor, clr, C_HD);
		return true;
	}

	@Override public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
		super.touchUp(event, x, y, pointer, button);
		tweenActorColor(actor, initClr, C_HD);
	}
}

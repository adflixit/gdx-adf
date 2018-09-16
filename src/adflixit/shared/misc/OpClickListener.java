package adflixit.shared.misc;

import static adflixit.shared.BaseGame.*;
import static adflixit.shared.Defs.*;
import static adflixit.shared.TweenUtils.*;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * {@link ClickListener} that operates the target actor's opacity.
 */
public class OpClickListener extends ClickListener {
	private Actor		actor;
	private float		initOp, fadeOp;

	public OpClickListener(Actor actor, float iop, float fop) {
		super();
		this.actor = actor;
		this.initOp = iop;
		this.fadeOp = fop;
	}

	@Override public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
		super.touchDown(event, x, y, pointer, button);
		fadeActor(actor, fadeOp, C_HD);
		return true;
	}

	@Override public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
		super.touchUp(event, x, y, pointer, button);
		fadeActor(actor, initOp, C_HD);
	}
}

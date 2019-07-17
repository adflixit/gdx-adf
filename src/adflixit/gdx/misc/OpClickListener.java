package adflixit.gdx.misc;

import static adflixit.gdx.TweenUtils.*;
import static adflixit.gdx.Util.*;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import adflixit.gdx.ClickCallback;

/**
 * {@link ClickListener} that operates the target actor's opacity.
 */
public class OpClickListener extends ClickListener {
  private Actor         actor;
  private float         initOp, fadeOp;
  private ClickCallback callback;

  public OpClickListener(Actor actor, float iop, float fop, ClickCallback cb) {
    super();
    this.actor = actor;
    this.initOp = iop;
    this.fadeOp = fop;
    setCallback(cb);
  }

  public void setCallback(ClickCallback cb) {
    callback = cb;
  }

  public void fadeIn() {
    fadeActor(actor, fadeOp, C_HD);
  }

  public void fadeOut() {
    fadeActor(actor, initOp, C_HD);
  }

  @Override public void clicked(InputEvent event, float x, float y) {
    callback.clicked(event, x, y);
  }

  @Override public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
    super.enter(event, x, y, pointer, fromActor);
    fadeIn();
  }

  @Override public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
    super.exit(event, x, y, pointer, toActor);
    fadeOut();
  }
}

package adflixit.gdx.misc;

import static adflixit.gdx.BaseGame.*;
import static adflixit.gdx.TweenUtils.*;
import static adflixit.gdx.Util.*;
import static com.badlogic.gdx.graphics.Color.*;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import adflixit.gdx.ClickCallback;

public class TintClickListener extends ClickListener {
  private Actor         actor;
  private Color         initClr;
  private Color         clr;
  private ClickCallback callback;

  public TintClickListener(Actor actor, Color initClr, Color clr, ClickCallback cb) {
    super();
    this.actor = actor;
    this.initClr = initClr != null ? initClr : WHITE;
    if (initClr != null) {
      actor.setColor(initClr);
    }
    this.clr = clr;
    setCallback(cb);
  }

  public TintClickListener(Actor actor, Color clr, ClickCallback cb) {
    this(actor, null, clr, cb);
  }

  public TintClickListener(Actor actor, String initClr, String clr, ClickCallback cb) {
    this(actor, color(initClr), color(clr), cb);
  }

  public TintClickListener(Actor actor, String clr, ClickCallback cb) {
    this(actor, color(clr), cb);
  }

  public TintClickListener(Actor actor, ClickCallback cb) {
    this(actor, "blue", cb);
  }

  public void setCallback(ClickCallback cb) {
    callback = cb;
  }

  protected void fadeIn() {
    tweenActorColor(actor, clr, C_HD);
  }

  protected void fadeOut() {
    tweenActorColor(actor, initClr, C_HD);
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

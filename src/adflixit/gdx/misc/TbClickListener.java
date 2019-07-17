package adflixit.gdx.misc;

import static adflixit.gdx.BaseGame.*;
import static adflixit.gdx.BaseContext.*;
import static adflixit.gdx.TweenUtils.*;
import static adflixit.gdx.Util.*;
import static com.badlogic.gdx.graphics.Color.*;

import aurelienribon.tweenengine.Timeline;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import adflixit.gdx.ClickCallback;

/**
 * {@link TextButton} {@link ClickListener}. Tweens the TextButton font color and the background color.
 */
public class TbClickListener extends ClickListener {
  private TextButton    btn;
  private Label         label;
  private Color         initBgClr;    // initial background color
  private Color         initFontClr;  // initial font color
  private Color         bgClr;
  private Color         fontClr;
  private ClickCallback callback;

  public TbClickListener(TextButton button, Color initBgClr, Color initFontClr,
      Color bgClr, Color fontClr, ClickCallback cb) {
    super();
    btn = button;
    label = button.getLabel();

    this.initBgClr = initBgClr != null ? initBgClr : WHITE;
    if (initBgClr != null) {
      button.setColor(initBgClr);
    }

    this.initFontClr = initFontClr != null ? initFontClr : WHITE;
    if (initFontClr != null) {
      label.setColor(initFontClr);
    }

    this.bgClr = bgClr;
    this.fontClr = fontClr;
    setCallback(cb);
  }

  public TbClickListener(TextButton button, Color bgClr, Color fontClr, ClickCallback cb) {
    this(button, null, null, bgClr, fontClr, cb);
  }

  public TbClickListener(TextButton button, Color clr, ClickCallback cb) {
    this(button, clr, clr, cb);
  }

  public TbClickListener(TextButton button, String initBgClr, String initFontClr,
      String bgClr, String fontClr, ClickCallback cb) {
    this(button, color(bgClr), color(initBgClr), color(fontClr), color(initFontClr), cb);
  }

  public TbClickListener(TextButton button, String bgClr, String fontClr, ClickCallback cb) {
    this(button, color(bgClr), color(fontClr), cb);
  }

  public TbClickListener(TextButton button, String clr, ClickCallback cb) {
    this(button, color(clr), cb);
  }

  public TbClickListener(TextButton button, ClickCallback cb) {
    this(button, "blue", cb);
  }

  public void setCallback(ClickCallback cb) {
    callback = cb;
  }

  protected void fadeIn() {
    Timeline.createParallel()
    .push($tweenActorColor(btn, bgClr, C_HD))
    .push($tweenLabelColor(label, fontClr, C_HD))
    .start(tweenMgr);
  }

  protected void fadeOut() {
    Timeline.createParallel()
    .push($tweenActorColor(btn, initBgClr, C_HD))
    .push($tweenLabelColor(label, initFontClr, C_HD))
    .start(tweenMgr);
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

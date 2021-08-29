package adf.gdx.utils;

import static adf.gdx.BaseAppListener.*;
import static adf.gdx.BaseContext.*;
import static adf.gdx.TweenUtil.$fadeActorColor;
import static adf.gdx.Util.C_HD;
import static com.badlogic.gdx.graphics.Color.*;

import adf.gdx.ClickCallback;

import aurelienribon.tweenengine.Timeline;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * {@link ImageButton} {@link ClickListener}. Fades the ImageButton image color and the background color.
 */
public class IbClickListener extends ClickListener {
  private ImageButton   btn;
  private Image         image;
  private Color         initBgClr;    // initial background color
  private Color         initImageClr; // initial image color
  private Color         bgClr;
  private Color         imageClr;
  private ClickCallback callback;

  public IbClickListener(ImageButton button, Color initBgClr, Color initImageClr,
      Color bgClr, Color imageClr, ClickCallback cb) {
    super();
    btn = button;
    image = button.getImage();

    this.initBgClr = (initBgClr != null) ? initBgClr : WHITE;
    if (initBgClr != null) {
      button.setColor(initBgClr);
    }

    this.initImageClr = (initImageClr != null) ? initImageClr : WHITE;
    if (initImageClr != null) {
      image.setColor(initImageClr);
    }

    this.bgClr = bgClr;
    this.imageClr = imageClr;
    setCallback(cb);
  }

  public IbClickListener(ImageButton button, Color bgClr, Color imageClr, ClickCallback cb) {
    this(button, null, null, bgClr, imageClr, cb);
  }

  public IbClickListener(ImageButton button, Color clr, ClickCallback cb) {
    this(button, clr, clr, cb);
  }

  public IbClickListener(ImageButton button, String initBgClr, String initImageClr,
      String bgClr, String imageClr, ClickCallback cb) {
    this(button, color(bgClr), color(initBgClr), color(imageClr), color(initImageClr), cb);
  }

  public IbClickListener(ImageButton button, String bgClr, String imageClr, ClickCallback cb) {
    this(button, color(bgClr), color(imageClr), cb);
  }

  public IbClickListener(ImageButton button, String clr, ClickCallback cb) {
    this(button, color(clr), cb);
  }

  public IbClickListener(ImageButton button, ClickCallback cb) {
    this(button, "blue", cb);
  }

  public void setCallback(ClickCallback cb) {
    callback = cb;
  }

  public void fadeIn() {
    Timeline.createParallel()
      .push($fadeActorColor(btn, bgClr, C_HD))
      .push($fadeActorColor(image, imageClr, C_HD))
      .start(tweenMgr);
  }

  public void fadeOut() {
    Timeline.createParallel()
      .push($fadeActorColor(btn, initBgClr, C_HD))
      .push($fadeActorColor(image, initImageClr, C_HD))
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

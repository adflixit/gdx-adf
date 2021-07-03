package adf.gdx.utils;

import static adf.gdx.BaseAppListener.*;
import static adf.gdx.BaseContext.*;
import static adf.gdx.TweenUtil.*;
import static adf.gdx.Util.*;
import static com.badlogic.gdx.utils.Align.*;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenEquation;
import aurelienribon.tweenengine.primitives.MutableFloat;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

/**
 * Draws an arc aka radial progress bar.
 */
public class Arc extends Widget {
  private Drawable            drawable;
  private ShapeRenderer       rend;
  private final MutableFloat  progress  = new MutableFloat(0);
  private float               radius;
  private boolean             clockwise = true;

  public Arc(Drawable drawable, float radius, ShapeRenderer rnd) {
    setDrawable(drawable);
    setRadius(radius);
    setSize(getPrefWidth(), getPrefHeight());
    setOrigin(center);
    rend = rnd;
  }

  public Arc(String drawable, float radius, ShapeRenderer rnd) {
    this(drawable(drawable), radius, rnd);
  }

  public Arc(float radius, ShapeRenderer rnd) {
    this("arc", radius, rnd);
  }

  public Arc(ShapeRenderer rnd) {
    this(200, rnd);
  }

  @Override public void draw(Batch batch, float parentAlpha) {
    validate();
    Color color = getColor();
    batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
    float x = getX();
    float y = getY();
    float width = getWidth();
    float height = getHeight();
    float rotation = getRotation();

    // nested position
    float nx = x;
    float ny = y;
    Group parent = getParent();
    while (parent != getStage().getRoot()) {
      nx += parent.getX();
      ny += parent.getY();
      parent = parent.getParent();
    }

    batch.end();
    Gdx.gl.glClearDepthf(1.0f);
    Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT);
    Gdx.gl.glColorMask(false, false, false, false);
    Gdx.gl.glDepthFunc(GL20.GL_LESS);
    Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
    Gdx.gl.glDepthMask(true);

    rend.begin(ShapeType.Filled);
      rend.setColor(1, 1, 1, .5f);
      if (clockwise) {
        rend.arc(nx + radius, ny + radius, radius + 10, rotation - degrees() + 90, degrees());
      } else {
        rend.arc(nx + radius, ny + radius, radius + 10, rotation + 90, degrees());
      }
    rend.end();

    Gdx.gl.glColorMask(true, true, true, true);
    Gdx.gl.glDepthMask(true);
    Gdx.gl.glDepthFunc(GL20.GL_EQUAL);

    batch.begin();
      drawable.draw(batch, x, y, width, height);
    batch.end();

    Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
    batch.begin();
  }

  public Arc setDrawable(Drawable drawable) {
    this.drawable = drawable;
    return this;
  }

  public Arc setRadius(float radius) {
    this.radius = radius;
    return this;
  }

  public Arc setClockwise(boolean value) {
    clockwise = value;
    return this;
  }

  public float progress() {
    return progress.floatValue();
  }

  public float degrees() {
    return progress() * 360;
  }

  public void set(float value) {
    uiTweenMgr.killTarget(progress);
    progress.setValue(value);
  }

  public void reset() {
    set(0);
  }

  /**
   * @param v value
   * @param d duration
   * @param eq equation
   */
  public void tween(float v, float d, TweenEquation eq) {
    $tween(v, d, eq).start(uiTweenMgr);
  }

  /**
   * @param v value
   * @param d duration
   */
  public void tween(float v, float d) {
    $tween(v, d).start(uiTweenMgr);
  }

  /**
   * @param v value
   */
  public void tween(float v) {
    $tween(v).start(uiTweenMgr);
  }

  public void tween() {
    $tween().start(uiTweenMgr);
  }

  /**
   * @param v value
   * @param d duration
   * @param eq equation
   */
  public Tween $tween(float v, float d, TweenEquation eq) {
    uiTweenMgr.killTarget(progress);
    return Tween.to(progress, 0, d).target(v).ease(eq);
  }

  /**
   * @param v value
   * @param d duration
   */
  public Tween $tween(float v, float d) {
    return $tween(v, d, Soft.INOUT);
  }

  /**
   * @param v value
   */
  public Tween $tween(float v) {
    return $tween(v, C_ID);
  }

  public Tween $tween() {
    return $tween(1);
  }

  /**
   * @param v value
   */
  public Tween $set(float v) {
    return $tween(v, 0);
  }

  public Tween $reset() {
    return $set(0);
  }

  @Override public float getPrefWidth() {
    return radius * 2;
  }

  @Override public float getPrefHeight() {
    return radius * 2;
  }
}

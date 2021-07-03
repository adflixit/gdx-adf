package adf.gdx;

import static adf.gdx.BaseAppListener.*;
import static adf.gdx.BaseContext.*;
import static adf.gdx.TweenUtil.*;
import static adf.gdx.SceneUtil.*;
import static adf.gdx.Util.*;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

/**
 * Set of full-screen overlay layers that create effects such as screen fading, tinting, flashing, etc.
 */
public class Overlay extends ContextComponent<BaseContext<?>> {
  private final Group root            = new Group();
  private final Image sheers          = new Image(drawable("white"));   // used for light color effects
  private final Image blackouts       = new Image(drawable("white"));   // used for fading the screen in and out
  private final Image tint            = new Image(drawable("white"));   // used for long-term screen tinting
  private final Image dim             = new Image(drawable("white"));   // used to add contrast to focus out the menus
  private final Image vignette        = new Image(drawable("vignette"));

  public final Color blackoutsColor   = new Color(0x000000ff);          // base color used by default
  public final Color dimColor         = new Color(0x000000ff);

  public Overlay(BaseContext<?> context) {
    super(context);
    ctx.addToUiLayer(UI_OVERLAY, root);
    addActors(root, sheers, blackouts, tint, dim, vignette);
    init();
  }

  public void init() {
    setAlpha(0, sheers, blackouts, tint, dim, vignette);
    setRgb(blackouts, blackoutsColor);
    setRgb(dim, dimColor);
  }

  /**
   * @return is {@link #sheers} visible.
   */
  public boolean checkSheers() {
    return getAlpha(sheers) > 0;
  }

  /**
   * @return is {@link #blackouts} visible.
   */
  public boolean checkBlackouts() {
    return getAlpha(blackouts) > 0;
  }

  /**
   * @return is {@link #dim} visible.
   */
  public boolean checkDim() {
    return getAlpha(dim) > 0;
  }

  /**
   * @return is {@link #vignette} visible.
   */
  public boolean checkVignette() {
    return getAlpha(vignette) > 0;
  }

  public void killBlackouts() {
    tweenMgr.killTarget(blackouts);
  }

  /**
   * Fades the {@link Overlay} sheers layer color.
   * @param clr color
   * @param d duration
   */
  public void fadeSheersColor(Color clr, float d) {
    fadeActorColor(sheers, clr, d);
  }

  /**
   * Fades the {@link Overlay} sheers layer color.
   * @param clr color
   */
  public void fadeSheersColor(Color clr) {
    fadeActorColor(sheers, clr, C_D);
  }

  /**
   * Sets the {@link Overlay} sheers layer.
   * @param clr color
   */
  public void setSheersColor(Color clr) {
    tweenMgr.killTarget(sheers, ActorAccessor.RGB);
    setRgb(sheers, clr);
  }

  /**
   * Fades the {@link Overlay} sheers layer.
   * @param a alpha
   * @param d duration
   */
  public void fadeSheers(float a, float d) {
    fadeActor(sheers, a, d);
  }

  /**
   * Fades the {@link Overlay} sheers layer.
   * @param a alpha
   */
  public void fadeSheers(float a) {
    fadeActor(sheers, a, C_D);
  }

  /**
   * Sets the color and fades the {@link Overlay} sheers layer to {@code a} and to 0 successively.
   * @param a alpha
   * @param id in duration
   * @param od out duration
   */
  public void fadeSheersInOut(float a, float id, float od) {
    $fadeSheersInOut(a, id, od).start(tweenMgr);
  }

  /**
   * Fades the {@link Overlay} sheers layer to {@code a} and to 0 successively.
   * @param a alpha
   */
  public void fadeSheersInOut(float a) {
    $fadeSheersInOut(a).start(tweenMgr);
  }

  /**
   * Sets the {@link Overlay} sheers layer opacity.
   * @param a alpha
   */
  public void setSheers(float a) {
    tweenMgr.killTarget(sheers, ActorAccessor.A);
    setAlpha(sheers, a);
  }

  /**
   * Flashes the screen.
   */
  public void flashFx() {
    $flashFx().start(tweenMgr);
  }

  /**
   * Fades the {@link Overlay} tint layer color.
   * @param clr color
   * @param d duration
   */
  public void fadeTintColor(Color clr, float d) {
    fadeActorColor(tint, clr, d);
  }

  /**
   * Fades the {@link Overlay} tint layer color.
   * @param clr color
   */
  public void fadeTintColor(Color clr) {
    fadeActorColor(tint, clr, C_D);
  }

  /**
   * Fades the {@link Overlay} tint layer.
   * @param clr color
   */
  public void setTintColor(Color clr) {
    tweenMgr.killTarget(tint, ActorAccessor.RGB);
    setRgb(tint, clr);
  }

  /**
   * Fades the {@link Overlay} tint layer.
   * @param a alpha
   * @param d duration
   */
  public void fadeTint(float a, float d) {
    fadeActor(tint, a, d);
  }

  /**
   * Fades the {@link Overlay} tint layer.
   * @param a alpha
   */
  public void fadeTint(float a) {
    fadeActor(tint, a, C_D);
  }

  /**
   * Sets the {@link Overlay} tint layer opacity.
   * @param a alpha
   */
  public void setTint(float a) {
    tweenMgr.killTarget(vignette, ActorAccessor.A);
    setAlpha(vignette, a);
  }

  /**
   * Smoothly dims the screen.
   * Used to create a dark contrasting background for the UI.
   * @param d duration
   */
  public void dimIn(float a, float d) {
    fadeActor(dim, a, d);
  }

  /**
   * Smoothly dims the screen.
   * Used to create a dark contrasting background for the UI.
   */
  public void dimIn(float d) {
    fadeActor(dim, C_OP_D, d);
  }

  /**
   * Smoothly fades out the screen dim.
   * @param d duration
   */
  public void dimOut(float d) {
    fadeActor(dim, 0, d);
  }

  /**
   * Smoothly fades out the screen dim.
   */
  public void dimOut() {
    fadeActor(dim, 0, C_D);
  }

  /**
   * Instantly applies the screen dim.
   * Used to create a dark contrasting background for the UI.
   * @param a alpha
   */
  public void setDim(float a) {
    tweenMgr.killTarget(dim, ActorAccessor.A);
    setAlpha(dim, a);
  }

  /**
   * Fades the {@link Overlay} vignette layer color.
   * @param clr color
   * @param d duration
   */
  public void fadeVignetteColor(Color clr, float d) {
    $fadeVignetteColor(clr, d).start(tweenMgr);
  }

  /**
   * Fades the {@link Overlay} vignette layer color.
   * @param clr color
   */
  public void fadeVignetteColor(Color clr) {
    $fadeVignetteColor(clr).start(tweenMgr);
  }

  /**
   * Sets the {@link #vignette} color.
   * @param clr color
   */
  public void setVignetteColor(Color clr) {
    tweenMgr.killTarget(sheers, ActorAccessor.RGB);
    setRgb(sheers, clr);
  }

  /**
   * Fades the {@link Overlay} vignette layer.
   * @param a alpha
   * @param d duration
   */
  public void fadeVignette(float a, float d) {
    $fadeVignette(a, d).start(tweenMgr);
  }

  /**
   * Fades the {@link Overlay} vignette layer.
   * @param a alpha
   */
  public void fadeVignette(float a) {
    $fadeVignette(a, C_D).start(tweenMgr);
  }

  /**
   * Fades the {@link Overlay} vignette layer to {@code a} and to 0 successively.
   * @param a alpha
   * @param id in delay
   * @param od out delay
   */
  public void fadeVignetteInOut(float a, float id, float od) {
    $fadeVignetteInOut(a, id, od).start(tweenMgr);
  }

  /**
   * Fades the {@link Overlay} vignette layer to {@code a} and to 0 successively.
   * @param a alpha
   */
  public void fadeVignetteInOut(Color clr, float a) {
    $fadeVignetteInOut(a).start(tweenMgr);
  }

  /**
   * Sets the {@link #vignette} opacity.
   * @param a alpha
   */
  public void setVignette(float a) {
    tweenMgr.killTarget(sheers, ActorAccessor.A);
    setAlpha(vignette, a);
  }

  /**
   * Flashes the vignette.
   */
  public void flashFxVig() {
    $flashFxVig().start(tweenMgr);
  }

  /**
   * Creates a handle to fade the {@link #sheers} color.
   * @param clr color
   * @param d duration
   * @return tween handle
   */
  public Tween $fadeSheersColor(Color clr, float d) {
    return $fadeActorColor(sheers, clr, d);
  }

  /**
   * Creates a fade to fade the {@link Overlay} sheers layer color.
   * @param clr color
   * @return tween handle
   */
  public Tween $fadeSheersColor(Color clr) {
    return $fadeActorColor(sheers, clr, C_D);
  }

  /**
   * Creates a handle to set the {@link #sheers} color.
   * @param clr color
   * @return tween handle
   */
  public Tween $setSheersColor(Color clr) {
    return $fadeActorColor(sheers, clr, 0);
  }

  /**
   * Creates a handle to fade the {@link #sheers} opacity.
   * @param a alpha
   * @param d duration
   * @return tween handle
   */
  public Tween $fadeSheers(float a, float d) {
    return $fadeActor(sheers, a, d);
  }

  /**
   * Creates a handle to fade the {@link Overlay} sheers layer.
   * @param a alpha
   * @return tween handle
   */
  public Tween $fadeSheers(float a) {
    return $fadeActor(sheers, a, C_D);
  }

  /**
   * Creates a handle to set the color and fade the {@link Overlay} sheers layer to {@code a} and to 0 successively.
   * @param a alpha
   * @param id in delay
   * @param od out delay
   * @return tween handle
   */
  public Timeline $fadeSheersInOut(float a, float id, float od) {
    return Timeline.createSequence()
            .push($fadeSheers(a, id))
            .push($fadeSheers(0, od));
  }

  /**
   * Creates a handle to set the color and fade the {@link Overlay} sheers layer to 1 and to 0 successively.
   * @param a alpha
   * @return tween handle
   */
  public Timeline $fadeSheersInOut(float a) {
    return $fadeSheersInOut(a, C_TD, C_D);
  }

  /**
   * Creates a handle to set the {@link #sheers} opacity.
   * @param a alpha
   * @return tween handle
   */
  public Tween $setSheers(float a) {
    return $fadeActor(sheers, a, 0);
  }

  /**
   * Creates a tween that flashes the {@link Overlay} sheers layer.
   * @return tween handle
   */
  public Timeline $flashFx() {
    return $fadeSheersInOut(OL_FLASH_OP);
  }

  /**
   * Creates a handle to tween the {@link #blackouts} color.
   * @param clr color
   * @param d duration
   * @return tween handle
   */
  public Tween $tweenBlackoutsColor(Color clr, float d) {
    return $fadeActorColor(blackouts, clr, d);
  }

  /**
   * Creates a handle to set the {@link #blackouts} color.
   * @param clr color
   * @return tween handle
   */
  public Tween $setBlackoutsColor(Color clr) {
    return $fadeActorColor(blackouts, clr, 0);
  }

  /**
   * Creates a handle to tween the {@link #blackouts} opacity.
   * @param a alpha
   * @param d duration
   * @return tween handle
   */
  public Tween $fadeBlackouts(float a, float d) {
    return $fadeActor(blackouts, a, d);
  }

  /**
   * Creates a handle to set the {@link #blackouts} opacity.
   * @param a alpha
   * @return tween handle
   */
  public Tween $setBlackouts(float a) {
    return $fadeActor(blackouts, a, 0);
  }

  /**
   * Creates a handle to tween the {@link #tint} color.
   * @param clr color
   * @param d duration
   * @return tween handle
   */
  public Tween $fadeTintColor(Color clr, float d) {
    return $fadeActorColor(tint, clr, d);
  }

  /**
   * Creates a tween to fade the {@link Overlay} tint layer color.
   * @param clr color
   * @return tween handle
   */
  public Tween $fadeTintColor(Color clr) {
    return $fadeActorColor(tint, clr, C_D);
  }

  /**
   * Creates a handle to set the {@link #tint} color.
   * @param clr color
   * @return tween handle
   */
  public Tween $setTintColor(Color clr) {
    return $fadeActorColor(tint, clr, 0);
  }

  /**
   * Creates a handle to tween the {@link #tint} opacity.
   * @param a alpha
   * @param d duration
   * @return tween handle
   */
  public Tween $fadeTint(float a, float d) {
    return $fadeActor(tint, a, d);
  }

  /**
   * Creates a handle to fade the {@link Overlay} tint layer.
   * @param a alpha
   * @return tween handle
   */
  public Tween $fadeTint(float a) {
    return $fadeActor(tint, a, C_D);
  }

  /**
   * Creates a handle to set the {@link #tint} opacity.
   * @param a alpha
   * @return tween handle
   */
  public Tween $setTint(float a) {
    return $fadeActor(tint, a, 0);
  }

  /**
   * Creates a handle to tween the {@link #dim} opacity.
   * @param a alpha
   * @param d duration
   * @return tween handle
   */
  public Tween $dimIn(float a, float d) {
    return $fadeActor(dim, a, d);
  }

  /**
   * Creates a handle to darken the menu background.
   * @param d duration
   * @return tween handle
   */
  public Tween $dimIn(float d) {
    return $fadeActor(dim, C_OP_S, d);
  }

  /**
   * Creates a handle to darken the menu background.
   * @return tween handle
   */
  public Tween $dimIn() {
    return $fadeActor(dim, C_OP_S, C_D);
  }

  /**
   * Creates a handle to tween the {@link #dim} opacity to 0.
   * @param d duration
   * @return tween handle
   */
  public Tween $dimOut(float d) {
    return $fadeActor(dim, 0, d);
  }

  /**
   * Creates a handle to release the background darkening.
   * @return tween handle
   */
  public Tween $dimOut() {
    return $fadeActor(dim, 0, C_D);
  }

  /**
   * Creates a handle to set the {@link #dim} opacity.
   * @param a alpha
   * @return tween handle
   */
  public Tween $setDim(float a) {
    return $fadeActor(dim, a, 0);
  }

  /**
   * Creates a handle to tween the {@link #vignette} color.
   * @param clr color
   * @param d duration
   * @return tween handle
   */
  public Tween $fadeVignetteColor(Color clr, float d) {
    return $fadeActorColor(vignette, clr, d);
  }

  /**
   * Creates a tween to fade the {@link Overlay} vignette layer color.
   * @param clr color
   * @return tween handle
   */
  public Tween $fadeVignetteColor(Color clr) {
    return $fadeActorColor(vignette, clr, C_D);
  }

  /**
   * Creates a handle to set the {@link #vignette} color.
   * @param clr color
   * @return tween handle
   */
  public Tween $setVignetteColor(Color clr) {
    return $fadeActorColor(vignette, clr, 0);
  }

  /**
   * Creates a handle to tween the {@link #vignette} opacity.
   * @param a alpha
   * @param d duration
   *
   */
  public Tween $fadeVignette(float a, float d) {
    return $fadeActor(vignette, a, d);
  }

  /**
   * Creates a handle to set the color and fade the {@link Overlay} vignette layer.
   * @param a alpha
   * @return tween handle
   */
  public Tween $fadeVignette(float a) {
    return $fadeActor(vignette, a, C_D);
  }

  /**
   * Creates a handle to fade the {@link Overlay} vignette layer to {@code a} and to 0 successively.
   * @param a alpha
   * @param id in duration
   * @param od out duration
   * @return tween handle
   */
  public Timeline $fadeVignetteInOut(float a, float id, float od) {
    return Timeline.createSequence()
             .push($fadeVignette(a, id))
             .push($fadeVignette(0, od));
  }

  /**
   * Creates a handle to fade the {@link Overlay} vignette layer to {@code a} and to 0 successively.
   * @param a alpha
   * @return tween handle
   */
  public Timeline $fadeVignetteInOut(float a) {
    return $fadeVignetteInOut(a, C_TD, C_BD);
  }

  /**
   * Creates a handle to set the {@link #vignette} opacity.
   * @param a alpha
   * @return tween handle
   */
  public Tween $setVignette(float a) {
    return $fadeActor(vignette, a, 0);
  }

  /**
   * Creates a tween that flashes the {@link Overlay} vignette layer.
   * @return tween handle
   */
  public Timeline $flashFxVig() {
    return $fadeVignetteInOut(OL_FLASH_OP);
  }

  public void resize() {
    sheers.setSize(ctx.screenWidth(), ctx.screenHeight());
    blackouts.setSize(ctx.screenWidth(), ctx.screenHeight());
    tint.setSize(ctx.screenWidth(), ctx.screenHeight());
    dim.setSize(ctx.screenWidth(), ctx.screenHeight());
    vignette.setSize(ctx.screenWidth(), ctx.screenHeight());
  }
}

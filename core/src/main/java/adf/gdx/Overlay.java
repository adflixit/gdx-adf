package adf.gdx;

import static adf.gdx.BaseGame.*;
import static adf.gdx.BaseContext.*;
import static adf.gdx.TweenUtils.*;
import static adf.gdx.Util.*;

import aurelienribon.tweenengine.Tween;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

/**
 * Set of full-screen overlay rectangles which create effects such as screen fading, tinting, flashing, etc.
 */
public class Overlay extends ScreenComponent<BaseContext<?>> {
  private final Group root            = new Group();
  private final Image sheers          = new Image(drawable("white"));   // used for light color effects
  private final Image blackouts       = new Image(drawable("white"));   // used for fading the screen in and out
  private final Image tint            = new Image(drawable("white"));   // used for long-term screen tinting
  private final Image dim             = new Image(drawable("white"));   // used to add contrast to focus out the menus
  private final Image vignette        = new Image(drawable("vignette"));

  private final Color blackoutsColor  = new Color(0x000000);            // base color used by default

  public Overlay(BaseContext<?> context) {
    super(context);
    ctx.addToUiLayer(UI_OVERLAY, root);
    addActors(root, sheers, blackouts, tint, dim, vignette);
    init();
  }

  public void init() {
    resetAlpha(sheers, blackouts, tint, dim, vignette);
    setRgb(dim, color("onyx"));
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

  /**
   * Sets the {@link #sheers} color.
   * @param clr color
   */
  public void setSheersColor(Color clr) {
    killTweenTarget(sheers, ActorAccessor.RGB);
    setRgb(sheers, clr);
  }

  /**
   * Sets the {@link #sheers} color and opacity.
   * @param clr color
   * @param v value
   */
  public void setupSheers(Color clr, float v) {
    setSheersColor(clr);
    setAlpha(sheers, v);
  }

  /**
   * Sets the {@link #sheers} opacity.
   * @param v value
   */
  public void setupSheers(float v) {
    killTweenTarget(sheers, ActorAccessor.A);
    setAlpha(sheers, v);
  }

  /**
   * Sets the {@link #sheers} opacity to 1.
   */
  public void resetSheers() {
    killTweenTarget(sheers, ActorAccessor.A);
    setAlpha(sheers, 0);
  }

  /**
   * Sets the {@link #blackouts} color.
   * @param clr color
   */
  public void setBlackoutsColor(Color clr) {
    killTweenTarget(blackouts, ActorAccessor.RGB);
    setRgb(blackouts, clr);
  }

  /**
   * Sets the {@link #blackouts} base color used by default.
   * @param clr color
   */
  public void setBlackoutsBaseColor(Color clr) {
    killTweenTarget(blackouts, ActorAccessor.RGB);
    setRgb(blackouts, clr);
    blackoutsColor.set(clr);
  }

  /**
   * Sets the {@link #blackouts} base color used by default.
   * @param clr rgba8888 int color
   */
  public void setBlackoutsBaseColor(int clr) {
    killTweenTarget(blackouts, ActorAccessor.RGB);
    blackoutsColor.set(clr);
    setRgb(blackouts, blackoutsColor);
  }

  /**
   * Sets the {@link #blackouts} color and sets the opacity to 1.
   * @param clr color
   */
  public void setupBlackouts(Color clr) {
    setBlackoutsColor(clr);
    setupAlpha(blackouts);
  }

  /**
   * Sets the {@link #blackouts} opacity to 1.
   */
  public void setupBlackouts() {
    killTweenTarget(blackouts, ActorAccessor.A);
    setupAlpha(blackouts);
  }

  /**
   * Sets the {@link #blackouts} opacity to 0.
   */
  public void resetBlackouts() {
    killTweenTarget(blackouts, ActorAccessor.A);
    resetAlpha(blackouts);
  }

  /**
   * Sets the {@link #tint} color.
   * @param clr color
   */
  public void setTintColor(Color clr) {
    killTweenTarget(tint, ActorAccessor.RGB);
    setRgb(tint, clr);
  }

  /**
   * Sets the {@link #tint} color and opacity.
   * @param clr color
   * @param v value
   */
  public void setupTint(Color clr, float v) {
    setTintColor(clr);
    setAlpha(tint, v);
  }

  /**
   * Sets the {@link #tint} opacity.
   * @param v value
   */
  public void setupTint(float v) {
    killTweenTarget(tint, ActorAccessor.A);
    setAlpha(tint, v);
  }

  /**
   * Sets the {@link #tint} opacity to 1.
   */
  public void setupTint() {
    killTweenTarget(tint, ActorAccessor.A);
    setupAlpha(tint);
  }

  /**
   * Sets the {@link #tint} opacity to 0.
   */
  public void resetTint() {
    killTweenTarget(tint, ActorAccessor.A);
    resetAlpha(tint);
  }

  /**
   * Sets the {@link #dim} opacity.
   * @param v value
   */
  public void setupDim(float v) {
    killTweenTarget(sheers, ActorAccessor.A);
    setAlpha(dim, v);
  }

  /**
   * Sets the {@link #dim} opacity to 0.
   */
  public void resetDim() {
    killTweenTarget(sheers, ActorAccessor.A);
    resetAlpha(dim);
  }

  /**
   * Sets the {@link #vignette} color.
   * @param clr color
   */
  public void setVignetteColor(Color clr) {
    killTweenTarget(sheers, ActorAccessor.RGB);
    setRgb(sheers, clr);
  }

  /**
   * Sets the {@link #vignette} color and opacity.
   * @param clr color
   * @param v value
   */
  public void setupVignette(Color clr, float v) {
    setBlackoutsColor(clr);
    setAlpha(vignette, v);
  }

  /**
   * Sets the {@link #vignette} opacity.
   * @param v value
   */
  public void setupVignette(float v) {
    killTweenTarget(vignette, ActorAccessor.A);
    setAlpha(vignette, v);
  }

  /**
   * Sets the {@link #vignette} opacity to 0.
   */
  public void resetVignette() {
    killTweenTarget(vignette, ActorAccessor.A);
    resetAlpha(vignette);
  }

  /**
   * Creates a handle to tween the {@link #sheers} color.
   * @param clr color
   * @param d duration
   */
  public Tween $tweenSheersColor(Color clr, float d) {
    return $tweenActorColor(sheers, clr, d);
  }

  /**
   * Creates a handle to tween the {@link #sheers} color and opacity.
   * @param clr color
   * @param v value
   * @param d duration
   */
  public Tween $fadeSheers(Color clr, float v, float d) {
    setRgb(sheers, clr);
    return $fadeActor(sheers, v, d);
  }

  /**
   * Creates a handle to tween the {@link #sheers} opacity to 0.
   * @param d duration
   */
  public Tween $fadeSheersOut(float d) {
    return $fadeActor(sheers, 0, d);
  }

  /**
   * Creates a handle to set the {@link #sheers} color.
   * @param clr color
   */
  public Tween $setSheersColor(Color clr) {
    return $tweenSheersColor(clr, 0);
  }

  /**
   * Creates a handle to set the {@link #sheers} color and opacity.
   * @param clr color
   * @param v value
   */
  public Tween $setupSheers(Color clr, float v) {
    return $fadeSheers(clr, v, 0);
  }

  /**
   * Creates a handle to set the {@link #sheers} opacity to 0.
   */
  public Tween $resetSheers() {
    return $fadeSheersOut(0);
  }

  /**
   * Creates a handle to tween the {@link #blackouts} color.
   * @param clr color
   * @param d duration
   */
  public Tween $tweenBlackoutsColor(Color clr, float d) {
    return $tweenActorColor(blackouts, clr, d);
  }

  /**
   * Creates a handle to tween the {@link #blackouts} color and opacity.
   * @param clr color
   * @param v value
   * @param d duration
   */
  public Tween $fadeBlackouts(Color clr, float v, float d) {
    setRgb(blackouts, clr);
    return $fadeActor(blackouts, v, d);
  }

  /**
   * Creates a handle to tween the {@link #blackouts} opacity to 0.
   * @param d duration
   */
  public Tween $fadeBlackoutsOut(float d) {
    return $fadeActor(blackouts, 0, d);
  }

  /**
   * Creates a handle to set the {@link #blackouts} color and opacity.
   * @param clr color
   * @param v value
   */
  public Tween $setupBlackouts(Color clr, float v) {
    return $fadeBlackouts(clr, v, 0);
  }

  /**
   * Creates a handle to set the {@link #blackouts} opacity to 0.
   */
  public Tween $resetBlackouts() {
    return $fadeBlackoutsOut(0);
  }

  /**
   * Creates a handle to tween the {@link #tint} color.
   * @param clr color
   * @param d duration
   */
  public Tween $tweenTintColor(Color clr, float d) {
    return $tweenActorColor(tint, clr, d);
  }

  /**
   * Creates a handle to tween the {@link #tint} color and opacity.
   * @param clr color
   * @param v value
   * @param d duration
   */
  public Tween $fadeTint(Color clr, float v, float d) {
    setRgb(tint, clr);
    return $fadeActor(tint, v, d);
  }

  /**
   * Creates a handle to tween the {@link #tint} opacity to 0.
   * @param d duration
   */
  public Tween $fadeTintOut(float d) {
    return $fadeActor(tint, 0, d);
  }

  /**
   * Creates a handle to set the {@link #tint} color and opacity.
   * @param clr color
   * @param v value
   */
  public Tween $setupTint(Color clr, float v) {
    return $fadeTint(clr, v, 0);
  }

  /**
   * Creates a handle to set the {@link #tint} opacity to 0.
   */
  public Tween $resetTint() {
    return $fadeTintOut(0);
  }

  /**
   * Creates a handle to tween the {@link #dim} opacity.
   * @param v value
   * @param d duration
   */
  public Tween $dimIn(float v, float d) {
    return $fadeActor(dim, v, d);
  }

  /**
   * Creates a handle to tween the {@link #dim} opacity to 0.
   * @param d duration
   */
  public Tween $dimOut(float d) {
    return $fadeActor(dim, 0, d);
  }

  /**
   * Creates a handle to set the {@link #dim} opacity.
   * @param v value
   */
  public Tween $setupDim(float v) {
    return $dimIn(v, 0);
  }

  /**
   * Creates a handle to set the {@link #dim} opacity to 0.
   */
  public Tween $resetDim() {
    return $dimOut(0);
  }

  /**
   * Creates a handle to tween the {@link #vignette} color.
   * @param clr color
   * @param d duration
   */
  public Tween $tweenVignetteColor(Color clr, float d) {
    return $tweenActorColor(vignette, clr, d);
  }
  
  /**
   * Creates a handle to tween the {@link #vignette} color and opacity.
   * @param clr color
   * @param v value
   * @param d duration
   */
  public Tween $fadeVignette(Color clr, float v, float d) {
    setRgb(vignette, clr);
    return $fadeActor(vignette, v, d);
  }

  /**
   * Creates a handle to tween the {@link #vignette} opacity.
   * @param v value
   * @param d duration
   */
  public Tween $fadeVignette(float v, float d) {
    return $fadeActor(vignette, v, d);
  }

  /**
   * Creates a handle to tween the {@link #vignette} opacity to 0.
   * @param d duration
   */
  public Tween $fadeVignetteOut(float d) {
    return $fadeActor(vignette, 0, d);
  }
  
  /**
   * Creates a handle to set the {@link #vignette} color and opacity.
   * @param clr color
   * @param v value
   */
  public Tween $setupVignette(Color clr, float v) {
    return $fadeVignette(clr, v, 0);
  }

  /**
   * Creates a handle to set the {@link #vignette} opacity.
   * @param v value
   */
  public Tween $setupVignette(float v) {
    return $fadeVignette(v, 0);
  }

  /**
   * Creates a handle to set the {@link #vignette} opacity to 0.
   */
  public Tween $resetVignette() {
    return $fadeVignetteOut(0);
  }

  public void resize() {
    sheers.setSize(ctx.screenWidth(), ctx.screenHeight());
    blackouts.setSize(ctx.screenWidth(), ctx.screenHeight());
    tint.setSize(ctx.screenWidth(), ctx.screenHeight());
    dim.setSize(ctx.screenWidth(), ctx.screenHeight());
    vignette.setSize(ctx.screenWidth(), ctx.screenHeight());
  }
}

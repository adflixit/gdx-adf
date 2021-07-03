package adf.gdx;

import static adf.gdx.BaseContext.*;
import static adf.gdx.Util.*;
import static adf.gdx.TweenUtil.*;

import adf.gdx.utils.Soft;

import aurelienribon.tweenengine.Tween;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class ParallaxBg extends ContextComponent<BaseContext<?>> {
  public float            factor  = PLXBG_FC;
  private TextureRegion[] layers;
  private int             length;
  private final Vector2   pos     = new Vector2();
  public final Vector2    shift   = new Vector2();
  private boolean         isEnabled;

  public ParallaxBg(BaseContext context) {
    super(context);
  }

  public void init(TextureRegion... regions) {
    layers = regions;
    length = regions.length;
    enable();
  }

  public void enable() {
    isEnabled = true;
  }

  public void disable() {
    isEnabled = false;
  }

  public void draw() {
    if (isEnabled) {
      pos.set(ctx.cameraPos0());
      for (int i=0; i < length; i++) {
        drawTiledRect(bat, layers[i], pos.x, pos.y,
            shift.x * factor * (i+1), shift.y * factor * (i+1),
            ctx.screenWidthI(), ctx.screenHeightI());
      }
    }
  }

  public Tween moveTo(float x, float y, float d) {
    tscTweenMgr.killTarget(shift);
    return Tween.to(shift, Vector2Accessor.XY, d).target(x, y).ease(Soft.INOUT);
  }

  public Tween setShift(float x, float y) {
    return moveTo(x, y, 0);
  }
}

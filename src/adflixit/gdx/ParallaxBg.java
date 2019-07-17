package adflixit.gdx;

import static adflixit.gdx.TweenUtils.*;
import static adflixit.gdx.Util.*;

import adflixit.gdx.misc.Soft;
import aurelienribon.tweenengine.Tween;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class ParallaxBg extends ScreenComponent<BaseContext<?>> {
  public float            factor  = PLXBG_FC;
  private TextureRegion[] layers;
  private int             length;
  private final Vector2   pos     = new Vector2();
  public final Vector2    shift   = new Vector2();
  private boolean         enabled;

  public ParallaxBg(BaseContext context) {
    super(context);
  }

  public void init(TextureRegion... regions) {
    layers = regions;
    length = regions.length;
    enable();
  }

  public void enable() {
    enabled = true;
  }

  public void disable() {
    enabled = false;
  }

  public void draw() {
    if (enabled) {
      pos.set(ctx.cameraPos0());
      for (int i=0; i < length; i++) {
        drawTiledRect(bat, layers[i], pos.x, pos.y,
            shift.x * factor * (i+1), shift.y * factor * (i+1),
            ctx.screenWidthI(), ctx.screenHeightI());
      }
    }
  }

  public Tween moveTo(float x, float y, float d) {
    killTweenTarget(shift);
    return Tween.to(shift, TweenUtils.Vector2Accessor.XY, d).target(x, y).ease(Soft.INOUT);
  }

  public Tween setShift(float x, float y) {
    return moveTo(x, y, 0);
  }
}

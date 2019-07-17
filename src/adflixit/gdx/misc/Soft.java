package adflixit.gdx.misc;

import adflixit.gdx.thirdparty.BezierEasing;
import aurelienribon.tweenengine.TweenEquation;

/**
 * Bezier-based soft tween equation.
 */
public abstract class Soft extends TweenEquation {
  // All setups go here.
  public static BezierEasing inout  = new BezierEasing(.25f, .1f, .25f, 1);
  public static BezierEasing in     = new BezierEasing(.75f, 0, .9f, .75f);
  public static BezierEasing out    = new BezierEasing(.1f, .25f, .25f, 1);

  public static final Soft INOUT = new Soft() {
    @Override
    public final float compute(float t) {
      return inout.ease(t);
    }

    @Override
    public String toString() {
      return "Soft.INOUT";
    }
  };

  public static final Soft IN = new Soft() {
    @Override
    public final float compute(float t) {
      return in.ease(t);
    }

    @Override
    public String toString() {
      return "Soft.IN";
    }
  };

  public static final Soft OUT = new Soft() {
    @Override
    public final float compute(float t) {
      return out.ease(t);
    }

    @Override
    public String toString() {
      return "Soft.OUT";
    }
  };
}

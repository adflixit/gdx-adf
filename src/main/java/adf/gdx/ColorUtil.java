package adf.gdx;

import static adf.gdx.MathUtil.*;
import static adf.gdx.Util.throwIllegalArgumentExceptionf;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;

public final class ColorUtil {
  private ColorUtil() {}

  public static final Vector3 tmpv3 = new Vector3();

  /**
   * @return HSL hue of a color.
   */
  public static float getHue(Color clr) {
    float hue = atan2f(SQRT3 * (clr.g - clr.b), 2 * clr.r - clr.g - clr.b) / PI2;
    return hue < 0 ? 1 + hue : hue;
  }

  /**
   * @return HSL saturation of a color.
   */
  public static float getSat(Color clr) {
    float min = min(clr.r, clr.g, clr.b), max = max(clr.r, clr.g, clr.b);
    return (max - min) / max;
  }

  /**
   * @return HSL lightness of a color.
   */
  public static float getLgt(Color clr) {
    return max(clr.r, clr.g, clr.b);
  }

  public static Vector3 getHsl(Color clr) {
    return tmpv3.set(getHue(clr), getSat(clr), getLgt(clr));
  }

  /**
   * Sets HSL hue.
   */
  public static Color setHue(Color clr, float h) {
    return setHsl(clr, h, getSat(clr), getLgt(clr));
  }

  /**
   * Sets HSL saturation.
   */
  public static Color setSat(Color clr, float s) {
    return setHsl(clr, getHue(clr), s, getLgt(clr));
  }

  /**
   * Sets HSL lightness.
   */
  public static Color setLgt(Color clr, float l) {
    float m = l / getLgt(clr);
    clr.r *= m;
    clr.g *= m;
    clr.b *= m;
    return clr;
  }

  public static Vector3 hslToRgb(float h, float s, float l) {
    float hs = fract(h)*6, f = fract(hs), p = l*(1-s), q = l*(1-f*s), t = l*(1-(1-f)*s);
    switch ((int)hs) {
      case 0: return tmpv3.set(l,t,p);
      case 1: return tmpv3.set(q,l,p);
      case 2: return tmpv3.set(p,l,t);
      case 3: return tmpv3.set(p,q,l);
      case 4: return tmpv3.set(t,p,l);
      case 5: return tmpv3.set(l,p,q);
      default: throwIllegalArgumentExceptionf("hs = " + hs); return null;
    }
  }

  public static Vector3 hslToRgb(Vector3 hsl) {
    return hslToRgb(hsl.x, hsl.y, hsl.z);
  }

  public static Color setHsl(Color clr, float h, float s, float l) {
    hslToRgb(h,s,l);
    return clr.set(tmpv3.x, tmpv3.y, tmpv3.z, clr.a);
  }
}

package adf.gdx;

import static adf.gdx.MathUtil.hasFlag;
import static com.badlogic.gdx.utils.Align.*;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public final class Util {
  private Util() {}

  public static final float     C_D               = .4f,    // duration
                                C_TD              = .025f,  // tiny duration
                                C_SD              = .1f,    // small duration
                                C_HD              = .2f,    // half duration
                                C_OD              = .6f,    // one and a half duration
                                C_DD              = .8f,    // double duration
                                C_MD              = 1,      // moderate duration
                                C_ID              = 1.5f,   // intermediate duration
                                C_FD              = 2,      // fair duration
                                C_BD              = 2.5f,   // big duration
                                C_GD              = 4,      // giant duration
                                C_DT              = 100,    // distance
                                C_SDT             = 20,     // small distance
                                C_MDT             = 150,    // moderate distance
                                C_BDT             = 250,    // big distance
                                C_GDT             = 500,    // giant distance
                                C_WDT             = 720,    // width
                                C_HGT             = 444,    // height
                                C_MRG             = 18,     // margin
                                C_PAD             = 20,     // padding
                                C_PAD_S           = 10,     // small padding
                                C_INV             = 10,     // interval
                                C_SHD_RAD         = 8,      // shadow radius
                                C_SHD_OFS         = 4,      // shadow offset
                                C_SHD_OP          = .2f,    // shadow opacity
                                C_OP_S            = .8f,    // solid opacity
                                C_OP_D            = .6f,    // dimmed opacity
                                C_OP_T            = .3f,    // transparent opacity
                                C_OP_G            = .15f,   // ghost opacity
                                C_IDLEACTION_D    = 30,     // idle action delay
                                C_REPACTION_D     = 10,     // repetitive action delay
                                OL_FLASH_OP       = .4f,    // overlay flash opacity
                                PLXBG_FC          = .8f,    // parallax background scrolling factor
                                BTN_BG            = 150,    // big button
                                BTN_MD            = 100,    // medium button
                                BTN_SM            = 80;     // small button

  public static final Vector2 tmpv2 = new Vector2();

  /**
   * @return current time in milliseconds.
   */
  public static long currentTime() {
    return System.currentTimeMillis();
  }

  /**
   * @return current time in seconds.
   */
  public static long currentTimeS() {
    return currentTime() / 1000;
  }

  /**
   * @return current time in minutes.
   */
  public static long currentTimeM() {
    return currentTimeS() / 60;
  }

  /**
   * @return current time in hours.
   */
  public static long currentTimeH() {
    return currentTimeM() / 60;
  }

  /**
   * @return current time in days.
   */
  public static long currentTimeD() {
    return currentTimeH() / 24;
  }

  public static void throwIllegalArgumentExceptionf(String info) {
    throw new IllegalArgumentException(String.format("Illegal argument(s): %s.", info));
  }

  /**
   * A template for calculating the position of one point relative to another based on edge points in one dimension.
   * @param pos position
   * @param len length
   * @param aln alignment flags
   * @param piv initial pivot
   * @param piv0 lowest pivot
   * @param piv1 highest pivot
   */
  private static float alignTemplate(float pos, float len, int aln, int piv, int piv0, int piv1) {
    if (hasFlag(piv, piv0)) {
      if (hasFlag(aln, piv1)) {
        pos -= len;
      } else if (!hasFlag(aln, piv0) && !hasFlag(piv, piv1)) {
        pos -= len/2;
      }
    } else if (hasFlag(piv, piv1)) {
      if (hasFlag(aln, piv0)) {
        pos += len;
      } else if (!hasFlag(aln, piv0) && !hasFlag(piv, piv1)) {
        pos += len/2;
      }
    } else if (!hasFlag(piv, piv0) && !hasFlag(piv, piv1)) {
      if (hasFlag(aln, piv0)) {
        pos += len/2;
      } else if (hasFlag(aln, piv1)) {
        pos -= len/2;
      }
    }
    return pos;
  }

  /**
   * A template for undoing the alignment of one point relative to another based on edge points in one dimension.
   * @param pos position
   * @param len length
   * @param aln alignment flags
   * @param piv initial pivot
   * @param piv0 lowest pivot
   * @param piv1 highest pivot
   */
  private static float disalignTemplate(float pos, float len, int aln, int piv, int piv0, int piv1) {
    if (hasFlag(piv, piv0)) {
      if (hasFlag(aln, piv1)) {
        pos += len;
      } else if (!hasFlag(aln, piv0) && !hasFlag(piv, piv1)) {
        pos += len/2;
      }
    } else if (hasFlag(piv, piv1)) {
      if (hasFlag(aln, piv0)) {
        pos -= len;
      } else if (!hasFlag(aln, piv0) && !hasFlag(piv, piv1)) {
        pos -= len/2;
      }
    } else if (!hasFlag(piv, piv0) && !hasFlag(piv, piv1)) {
      if (hasFlag(aln, piv0)) {
        pos -= len/2;
      } else if (hasFlag(aln, piv1)) {
        pos += len/2;
      }
    }
    return pos;
  }

  /**
   * Aligns {@code x} in {@code width} relative to the pivot.
   * @param aln alignment flags
   * @param piv initial pivot
   */
  public static float alignX(float x, float width, int aln, int piv) {
    return alignTemplate(x, width, aln, piv, left, right);
  }

  /**
   * Aligns {@code x} in {@code width} relative to the bottom left point.
   * @param aln alignment flags
   */
  public static float alignX(float x, float width, int aln) {
    return alignX(x, width, aln, bottomLeft);
  }

  /**
   * Aligns {@code y} in {@code height} relative to the pivot.
   * @param aln alignment flags
   * @param piv initial pivot
   */
  public static float alignY(float y, float height, int aln, int piv) {
    return alignTemplate(y, height, aln, piv, bottom, top);
  }

  /**
   * Aligns {@code y} in {@code height} relative to the bottom left point.
   * @param aln alignment flags
   */
  public static float alignY(float y, float height, int aln) {
    return alignY(y, height, aln, bottomLeft);
  }

  /**
   * Aligns {@code x} and {@code y} both in {@code width} and {@code height} respectively relative to the pivot.
   * @param aln alignment flags
   * @param piv initial pivot
   */
  public static Vector2 align(float x, float y, float width, float height, int aln, int piv) {
    return tmpv2.set(alignX(x, width, aln, piv), alignY(y, height, aln, piv));
  }

  /**
   * Aligns {@code x} and {@code y} both in {@code width} and {@code height} respectively relative to the bottom left point.
   * @param aln alignment flags
   */
  public static Vector2 align(float x, float y, float width, float height, int aln) {
    return align(x, y, width, height, aln, bottomLeft);
  }

  /**
   * Aligns {@code x} and {@code y} both in {@code width} and {@code height} respectively relative to the pivot.
   * @param v vector
   * @param aln alignment flags
   * @param piv initial pivot
   */
  public static Vector2 align(Vector2 v, float width, float height, int aln, int piv) {
    return align(v.x, v.y, width, height, aln, piv);
  }

  /**
   * Aligns {@code x} and {@code y} both in {@code width} and {@code height} respectively relative to the bottom left point.
   * @param v vector
   * @param aln alignment flags
   */
  public static Vector2 align(Vector2 v, float width, float height, int aln) {
    return align(v, width, height, aln, bottomLeft);
  }

  /**
   * Reverses the alignment of {@code x} in {@code width} relative to the pivot.
   * @param aln alignment flags
   * @param piv initial pivot
   */
  public static float disalignX(float x, float width, int aln, int piv) {
    return disalignTemplate(x, width, aln, piv, left, right);
  }

  /**
   * Reverses the alignment of {@code x} in {@code width} relative to the bottom left point.
   * @param aln alignment flags
   */
  public static float disalignX(float x, float width, int aln) {
    return disalignX(x, width, aln, bottomLeft);
  }

  /**
   * Reverses the alignment of {@code y} in {@code height} relative to the pivot.
   * @param aln alignment flags
   * @param piv initial pivot
   */
  public static float disalignY(float y, float height, int aln, int piv) {
    return disalignTemplate(y, height, aln, piv, bottom, top);
  }

  /**
   * Reverses the alignment of {@code y} in {@code height} relative to the bottom left point.
   * @param aln alignment flags
   */
  public static float disalignY(float y, float height, int aln) {
    return disalignY(y, height, aln, bottomLeft);
  }

  /**
   * Reverses the alignment of {@code x} and {@code y} both in {@code width} and {@code height} respectively 
   * relative to the pivot.
   * @param aln alignment flags
   * @param piv initial pivot
   */
  public static Vector2 disalign(float x, float y, float width, float height, int aln, int piv) {
    return tmpv2.set(disalignX(x, width, aln, piv), disalignY(y, height, aln, piv));
  }

  /**
   * Reverses the alignment of {@code x} and {@code y} both in {@code width} and {@code height} respectively 
   * relative to the bottom left point.
   * @param aln alignment flags
   */
  public static Vector2 disalign(float x, float y, float width, float height, int aln) {
    return disalign(x, y, width, height, aln, bottomLeft);
  }

  /**
   * Reverses the alignment of {@code x} and {@code y} both in {@code width} and {@code height} respectively 
   * relative to the pivot.
   * @param v vector
   * @param aln alignment flags
   * @param piv initial pivot
   */
  public static Vector2 disalign(Vector2 v, float width, float height, int aln, int piv) {
    return disalign(v.x, v.y, width, height, aln, piv);
  }

  /**
   * Reverses the alignment of {@code x} and {@code y} both in {@code width} and {@code height} respectively 
   * relative to the bottom left point.
   * @param v vector
   * @param aln alignment flags
   */
  public static Vector2 disalign(Vector2 v, float width, float height, int aln) {
    return disalign(v, width, height, aln, bottomLeft);
  }

  /**
   * @return {@link com.badlogic.gdx.utils.Align} translated to string.
   */
  public static String alnToStr(int aln) {
    String s = "";
    boolean v = hasFlag(aln,top) || hasFlag(aln,bottom), h = hasFlag(aln,left) || hasFlag(aln,right);
    if (hasFlag(aln, center)) {
      s += "center" + (v||h ? "-" : "");
    }
    if (hasFlag(aln, top)) {
      s += "top" + (h ? "-" : "");
    }
    if (hasFlag(aln, bottom)) {
      s += "bottom" + (h ? "-" : "");
    }
    if (hasFlag(aln, left)) {
      s += "left";
    }
    if (hasFlag(aln, right)) {
      s += "right";
    }
    return aln != 0 ? s : "n/a";
  }

  /**
   * @return wrapped primitive converted to text.
   */
  public static String wrapToStr(Object obj) {
    if (obj instanceof Boolean) {
      return String.valueOf(((Boolean)obj).booleanValue());
    } else if (obj instanceof Character) {
      return String.valueOf(((Character)obj).charValue());
    } else if (obj instanceof Byte) {
      return String.valueOf(((Byte)obj).byteValue());
    } else if (obj instanceof Short) {
      return String.valueOf(((Short)obj).shortValue());
    } else if (obj instanceof Integer) {
      return String.valueOf(((Integer)obj).intValue());
    } else if (obj instanceof Long) {
      return String.valueOf(((Long)obj).longValue());
    } else if (obj instanceof Float) {
      return String.valueOf(((Float)obj).floatValue());
    } else if (obj instanceof Double) {
      return String.valueOf(((Double)obj).doubleValue());
    } else {
      return obj.toString();
    }
  }

  /**
   * @return array items represented as text.
   */
  public static <T> String arrToStr(T... array) {
    String s = "";
    for (T i : array) {
      s += wrapToStr(i) + ", ";
    }
    return s.substring(0, s.length() - 2);
  }

  /**
   * Uses the standard string formatting pattern, where "%s" marks the placeholder,
   * e.g. pattern "%s, " will format array {'a', 'b', 'c'} as "a, b, c".
   * "$|" is the optional terminator that marks trimming at the last iteration.
   * @return array items ordered by the pattern, represented as text.
   */
  public static <T> String arrToStrf(String pattern, T... array) {
    String s = "";
    // merge all except the last one
    for (int i=0; i < array.length-1; i++) {
      s += String.format(pattern, wrapToStr(array[i]));
    }
    // clear the tokens
    s = s.replaceAll("\\$\\|", "");
    // add last item with token
    s += String.format(pattern, wrapToStr(array[array.length-1]));
    // trim
    return s.substring(0, s.length() - (pattern.length() -
        (pattern.contains("$|") ? pattern.indexOf("$|") : pattern.indexOf("%s") + 2)));
  }

  /**
   * This code was partially taken from Google Guava.
   * @param s text to be repeated.
   * @param r repetition times.
   * @return {@code s} repeated {@code r} times.
   */
  public static String repeat(String s, int r) {
    if (r < 0) {
      throw new IllegalArgumentException(String.format("A number of repetitions can't be negative: %d.", r));
    }

    final int len = s.length();
    final int size = len * r;
    final char[] array = new char[size];

    s.getChars(0, len, array, 0);
    int n;
    for (n = len; n < size - n; n <<= 1) {
      System.arraycopy(array, 0, array, n, n);
    }
    System.arraycopy(array, 0, array, n, size - n);
    return new String(array);
  }

  public static String getStackTrace() {
    return arrToStrf("%s$|\n", Thread.currentThread().getStackTrace());
  }

  public static void printStackTrace() {
    System.out.print("\u001B[31m");
    int c = 0;
    for (StackTraceElement i : Thread.currentThread().getStackTrace()) {
      if (c != 1) {
        System.out.println(((c > 0) ? "    " : "") + i);
      }
      c++;
    }
    System.out.print("\u001B[0m");
  }

  /**
   * Draws a rectangle tiled with {@link TextureRegion} with an offset.
   * TODO: unfinished.
   */
  public static void drawTiledRect(Batch batch, TextureRegion region, float x, float y,
      float ofsX, float ofsY, float width, float height) {
    float regionWidth = region.getRegionWidth(), regionHeight = region.getRegionHeight();
    if (ofsX > 0) {
      ofsX %= regionWidth;
    }
    if (ofsY > 0) {
      ofsY %= regionHeight;
    }
    if (ofsX < 0) {
      ofsX += regionWidth;
    }
    if (ofsY < 0) {
      ofsY += regionHeight;
    }

    int fullX = (int)(width/regionWidth), fullY = (int)(height/regionHeight);
    float fullWidth = regionWidth*fullX, fullHeight = regionHeight*fullY;
    float remainingX = width - fullWidth - ofsX, remainingY = height - fullHeight - ofsY;
    float startX = x, startY = y;
    float endX = x + width - remainingX, endY = y + height - remainingY;

    for (int i=0; i < fullX; i++) {
      y = startY + ofsY;
      for (int j=0; j < fullY; j++) {
        batch.draw(region, x, y, regionWidth, regionHeight);
        y += regionHeight;
      }
      x += regionWidth;
    }

    Texture texture = region.getTexture();
    float u = region.getU();
    float v2 = region.getV2();

    if (ofsX > 0) {
      float u2 = u + ofsX / texture.getWidth();
      float v = region.getV();
      y = startY;
      for (int i=0; i < fullY; i++) {
        batch.draw(texture, x, y, ofsX, regionHeight, u, v2, u2, v);
        y += regionHeight;
      }
      if (remainingY > 0) {
        v = v2 - remainingY / texture.getHeight();
        batch.draw(texture, x, y, remainingX, remainingY, u, v2, u2, v);
      }
    }

    if (ofsY > 0) {
      float u2 = region.getU2();
      float v = v2 - ofsY / texture.getHeight();
      x = startX;
      for (int i=0; i < fullX; i++) {
        batch.draw(texture, x, y, regionWidth, ofsY, u, v2, u2, v);
        x += regionWidth;
      }
    }

    if (remainingX > 0) {
      float u2 = u + remainingX / texture.getWidth();
      float v = region.getV();
      y = fullHeight;
      for (int i=0; i < fullY; i++) {
        batch.draw(texture, x, y, remainingX, regionHeight, u, v2, u2, v);
        y += regionHeight;
      }
      if (remainingY > 0) {
        v = v2 - remainingY / texture.getHeight();
        batch.draw(texture, x, y, remainingX, remainingY, u, v2, u2, v);
      }
    }

    if (remainingY > 0) {
      float u2 = region.getU2();
      float v = v2 - remainingY / texture.getHeight();
      x = fullWidth;
      for (int i=0; i < fullX; i++) {
        batch.draw(texture, x, y, regionWidth, remainingY, u, v2, u2, v);
        x += regionWidth;
      }
    }
  }
}

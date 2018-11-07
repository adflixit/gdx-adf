/*
 * Copyright 2018 Adflixit
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package adflixit.shared;

import static adflixit.shared.BaseGame.*;
import static com.badlogic.gdx.utils.Align.*;
import static java.lang.Math.*;

import adflixit.shared.misc.RoundedDrawable;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Json;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public final class Util {
  private Util() {}

  public static final double		SQRT2			= sqrt(2),
  					SQRT3			= sqrt(3),
  					LOG2			= log(2),
  					CIRCLE			= PI*2,
  					SEMICIRCLE		= PI,
  					QTRCIRCLE		= PI/2,
  					DEGREE			= PI/180,
  					GOLDENRATIO		= (1+sqrt(5))/2,
  					INVGOLDENRATIO		= 1/GOLDENRATIO;

  public static final float		C_D			= .4f,		// duration
  					C_TD			= .025f,	// tiny duration
  					C_SD			= .1f,		// small duration
  					C_HD			= .2f,		// half duration
  					C_OD			= .6f,		// one and a half duration
  					C_DD			= .8f,		// double duration
  					C_MD			= 1f,		// moderate duration
  					C_ID			= 1.5f,		// intermediate duration
  					C_FD			= 2f,		// fair duration
  					C_BD			= 2.5f,		// big duration
  					C_GD			= 4,		// giant duration
  					C_DT			= 100,		// distance
  					C_SDT			= 20,		// small distance
  					C_MDT			= 150,		// moderate distance
  					C_BDT			= 250,		// big distance
  					C_GDT			= 500,		// giant distance
  					C_WDT			= 720,		// width
  					C_HGT			= 445,		// height
  					C_MRG			= 18,		// margin
  					C_PAD			= 20,		// padding
  					C_PAD_S			= 10,		// small padding
  					C_INV			= 10,		// interval
  					C_SHD_RAD		= 8,		// shadow radius
  					C_SHD_OFS		= 4,		// shadow offset
  					C_SHD_OP		= .2f,		// shadow opacity
  					C_POPUP_IN_SC		= 1.2f,		// popup intro scale
  					C_POPUP_OUT_SC		= .8f,		// popup outro scale
  					C_OP			= .3f,		// opacity
  					C_OP_DIM		= .8f,		// dim opacity
  					C_OP_DIM_L		= .6f,		// dim light opacity
  					C_IDLEACTION_D		= 30,		// idle action delay
  					C_REPACTION_D		= 10,		// repetitive action delay

  					OL_VIG_OP		= .2f,		// overlay vignette opacity
  					OL_FLASH_OP		= .4f,		// overlay flash opacity

  					PLXBG_FC		= .8f,		// parallax background scrolling factor

  					// Button sizes
  					BTN_BG			= 150,
  					BTN_MD			= 100,
  					BTN_SM			= 80,

  					SPARKS_FPS		= 60,		// sparks frame rate
  					CIRCOUT_FPS		= 60;		// circout animation framerate

  // Common use temporary variables
  public static final Vector2		tmpv2			= new Vector2();
  public static final Vector3		tmpv3			= new Vector3();
  public static final Color		tmpclr			= new Color();

  /** @return current time in milliseconds. */
  public static long currentTime() {
    return System.currentTimeMillis();
  }

  /** @return current time in seconds. */
  public static long currentTimeS() {
    return currentTime()/1000;
  }

  /** @return current time in minutes. */
  public static long currentTimeM() {
    return currentTimeS()/60;
  }

  /** @return current time in hours. */
  public static long currentTimeH() {
    return currentTimeM()/60;
  }

  /** @return current time in days. */
  public static long currentTimeD() {
    return currentTimeH()/24;
  }

  public static void illegalArgument(String info) {
    throw new IllegalArgumentException("Illegal argument(s): "+info+".");
  }

  /** A template for calculating the position of one point relative to another in a single dimension.
   * @param p position
   * @param l length
   * @param aln alignment flags
   * @param piv initial pivot
   * @param piv0 lowest pivot
   * @param piv1 highest pivot */
  private static float alignT(float p, float l, int aln, int piv, int piv0, int piv1) {
    if (hasFlag(piv, piv0)) {
      if (hasFlag(aln, piv1)) {
        p -= l;
      } else if (!hasFlag(aln, piv0) && !hasFlag(piv, piv1)) {
        p -= l/2;
      }
    } else if (hasFlag(piv, piv1)) {
      if (hasFlag(aln, piv0)) {
        p += l;
      } else if (!hasFlag(aln, piv0) && !hasFlag(piv, piv1)) {
        p += l/2;
      }
    } else if (!hasFlag(piv, piv0) && !hasFlag(piv, piv1)) {
      if (hasFlag(aln, piv0)) {
        p += l/2;
      } else if (hasFlag(aln, piv1)) {
        p -= l/2;
      }
    }
    return p;
  }

  /** A template for undoing the alignment of one point relative to another in a single dimension.
   * @param p position
   * @param l length
   * @param aln alignment flags
   * @param piv initial pivot
   * @param piv0 lowest pivot
   * @param piv1 highest pivot */
  private static float disalignT(float p, float d, int aln, int piv, int piv0, int piv1) {
    if (hasFlag(piv, piv0)) {
      if (hasFlag(aln, piv1)) {
        p += d;
      } else if (!hasFlag(aln, piv0) && !hasFlag(piv, piv1)) {
        p += d/2;
      }
    } else if (hasFlag(piv, piv1)) {
      if (hasFlag(aln, piv0)) {
        p -= d;
      } else if (!hasFlag(aln, piv0) && !hasFlag(piv, piv1)) {
        p -= d/2;
      }
    } else if (!hasFlag(piv, piv0) && !hasFlag(piv, piv1)) {
      if (hasFlag(aln, piv0)) {
        p -= d/2;
      } else if (hasFlag(aln, piv1)) {
        p += d/2;
      }
    }
    return p;
  }

  /** Aligns {@code x} in {@code width} relative to the pivot.
   * @param aln alignment flags
   * @param piv initial pivot */
  public static float alignX(float x, float width, int aln, int piv) {
    return alignT(x, width, aln, piv, left, right);
  }

  /** Aligns {@code x} in {@code width} relative to the bottom left point.
   * @param aln alignment flags */
  public static float alignX(float x, float width, int aln) {
    return alignX(x, width, aln, bottomLeft);
  }

  /** Aligns {@code y} in {@code height} relative to the pivot.
   * @param aln alignment flags
   * @param piv initial pivot */
  public static float alignY(float y, float height, int aln, int piv) {
    return alignT(y, height, aln, piv, bottom, top);
  }

  /** Aligns {@code y} in {@code height} relative to the bottom left point.
   * @param aln alignment flags */
  public static float alignY(float y, float height, int aln) {
    return alignY(y, height, aln, bottomLeft);
  }

  /** Aligns {@code x} and {@code y} both in {@code width} and {@code height} respectively relative to the pivot.
   * @param aln alignment flags
   * @param piv initial pivot */
  public static Vector2 align(float x, float y, float width, float height, int aln, int piv) {
    /*if ((aln & right) != 0) x -= width;
    else if ((aln & left)==0) x -= width / 2;
    if ((aln & top) != 0) y -= height;
    else if ((aln & bottom)==0) y -= height / 2;*/
    return tmpv2.set(alignX(x, width, aln, piv), alignY(y, height, aln, piv));
  }

  /** Aligns {@code x} and {@code y} both in {@code width} and {@code height} respectively relative to the bottom left point.
   * @param aln alignment flags */
  public static Vector2 align(float x, float y, float width, float height, int aln) {
    return align(x, y, width, height, aln, bottomLeft);
  }

  /** Aligns {@code x} and {@code y} both in {@code width} and {@code height} respectively relative to the pivot.
   * @param v vector
   * @param aln alignment flags
   * @param piv initial pivot */
  public static Vector2 align(Vector2 v, float width, float height, int aln, int piv) {
    return align(v.x, v.y, width, height, aln, piv);
  }

  /** Aligns {@code x} and {@code y} both in {@code width} and {@code height} respectively relative to the bottom left point.
   * @param v vector
   * @param aln alignment flags */
  public static Vector2 align(Vector2 v, float width, float height, int aln) {
    return align(v, width, height, aln, bottomLeft);
  }

  /** Reverses the alignment of {@code x} in {@code width} relative to the pivot.
   * @param aln alignment flags
   * @param piv initial pivot */
  public static float disalignX(float x, float width, int aln, int piv) {
    return disalignT(x, width, aln, piv, left, right);
  }

  /** Reverses the alignment of {@code x} in {@code width} relative to the bottom left point.
   * @param aln alignment flags */
  public static float disalignX(float x, float width, int aln) {
    return disalignX(x, width, aln, bottomLeft);
  }

  /** Reverses the alignment of {@code y} in {@code height} relative to the pivot.
   * @param aln alignment flags
   * @param piv initial pivot */
  public static float disalignY(float y, float height, int aln, int piv) {
    return disalignT(y, height, aln, piv, bottom, top);
  }

  /** Reverses the alignment of {@code y} in {@code height} relative to the bottom left point.
   * @param aln alignment flags */
  public static float disalignY(float y, float height, int aln) {
    return disalignY(y, height, aln, bottomLeft);
  }

  /** Reverses the alignment of {@code x} and {@code y} both in {@code width} and {@code height} respectively 
   * relative to the pivot.
   * @param aln alignment flags
   * @param piv initial pivot */
  public static Vector2 disalign(float x, float y, float width, float height, int aln, int piv) {
    return tmpv2.set(disalignX(x, width, aln, piv), disalignY(y, height, aln, piv));
  }

  /** Reverses the alignment of {@code x} and {@code y} both in {@code width} and {@code height} respectively 
   * relative to the bottom left point.
   * @param aln alignment flags */
  public static Vector2 disalign(float x, float y, float width, float height, int aln) {
    return disalign(x, y, width, height, aln, bottomLeft);
  }

  /** Reverses the alignment of {@code x} and {@code y} both in {@code width} and {@code height} respectively 
   * relative to the pivot.
   * @param v vector
   * @param aln alignment flags
   * @param piv initial pivot */
  public static Vector2 disalign(Vector2 v, float width, float height, int aln, int piv) {
    return disalign(v.x, v.y, width, height, aln, piv);
  }

  /** Reverses the alignment of {@code x} and {@code y} both in {@code width} and {@code height} respectively 
   * relative to the bottom left point.
   * @param v vector
   * @param aln alignment flags */
  public static Vector2 disalign(Vector2 v, float width, float height, int aln) {
    return disalign(v, width, height, aln, bottomLeft);
  }

  /** @return {@link Align} translated to string. */
  public static String alnToStr(int aln) {
    String s = "";
    boolean v = hasFlag(aln,top)||hasFlag(aln,bottom), h = hasFlag(aln,left)||hasFlag(aln,right);
    if (hasFlag(aln, center)) {
      s+="center"+(v||h?" ":"");
    }
    if (hasFlag(aln, top)) {
      s+="top"+(h?" ":"");
    }
    if (hasFlag(aln, bottom)) {
      s+="bottom"+(h?" ":"");
    }
    if (hasFlag(aln, left)) {
      s+="left";
    }
    if (hasFlag(aln, right)) {
      s+="right";
    }
    return aln != 0 ? s : "n/a";
  }

  /** @return array items represented as text. */
  public static <T> String arrayToString(T... array) {
    String s = "";
    for (T i : array) {
      s += i+", ";
    }
    return s.substring(0, s.length()-2);
  }

  /** @return array items represented as text. */
  public static String arrayToString(int... array) {
    String s = "";
    for (Object i : array) {
      s += i+", ";
    }
    return s.substring(0, s.length()-2);
  }

  /** @return array items represented as text. */
  public static String arrayToString(long... array) {
    String s = "";
    for (Object i : array) {
      s += i+", ";
    }
    return s.substring(0, s.length()-2);
  }

  /** @return array items represented as text. */
  public static String arrayToString(float... array) {
    String s = "";
    for (Object i : array) {
      s += i+", ";
    }
    return s.substring(0, s.length()-2);
  }

  /** @return array items represented as text. */
  public static String arrayToString(double... array) {
    String s = "";
    for (Object i : array) {
      s += i+", ";
    }
    return s.substring(0, s.length()-2);
  }

  /** Uses the standard string formatting pattern, where "%s" marks the placeholder, 
   * e.g. the pattern "%s, " will format the array {'a', 'b', 'c'} as "a, b, c".
   * @return array items ordered by the pattern, represented as text. */
  public static <T> String arrayToStringF(String pattern, T... array) {
    String s = "";
    for (T i : array) {
      s += String.format(pattern, i);
    }
    // trimming
    return s.substring(0, s.length()-(pattern.length()-(pattern.indexOf("%s")+2)));
  }

  /** @return array items ordered by the pattern, represented as text.
   * @see #arrayToStringF(String, Object...) */
  public static String arrayToStringF(String pattern, int... array) {
    String s = "";
    for (Object i : array) {
      s += String.format(pattern, i);
    }
    // trimming
    return s.substring(0, s.length()-(pattern.length()-(pattern.indexOf("%s")+2)));
  }

  /** @return array items ordered by the pattern, represented as text.
   * @see #arrayToStringF(String, Object...) */
  public static String arrayToStringF(String pattern, long... array) {
    String s = "";
    for (Object i : array) {
      s += String.format(pattern, i);
    }
    // trimming
    return s.substring(0, s.length()-(pattern.length()-(pattern.indexOf("%s")+2)));
  }

  /** @return array items ordered by the pattern, represented as text.
   * @see #arrayToStringF(String, Object...) */
  public static String arrayToStringF(String pattern, float... array) {
    String s = "";
    for (Object i : array) {
      s += String.format(pattern, i);
    }
    // trimming
    return s.substring(0, s.length()-(pattern.length()-(pattern.indexOf("%s")+2)));
  }

  /** @return array items ordered by the pattern, represented as text.
   * @see #arrayToStringF(String, Object...) */
  public static String arrayToStringF(String pattern, double... array) {
    String s = "";
    for (Object i : array) {
      s += String.format(pattern, i);
    }
    // trimming
    return s.substring(0, s.length()-(pattern.length()-(pattern.indexOf("%s")+2)));
  }

  public static String getStackTrace() {
    return arrayToStringF("%s\n", Thread.currentThread().getStackTrace());
  }

  public static void printStackTrace() {
    for (StackTraceElement i : Thread.currentThread().getStackTrace()) {
      System.out.println(i);
    }
  }

  /*public static BitmapFont createFont(FileHandle file, int size) {
    FreeTypeFontGenerator gen = new FreeTypeFontGenerator(file);
    FreeTypeFontParameter par = new FreeTypeFontParameter();
    par.size = size;
    BitmapFont font = gen.generateFont(par);
    gen.dispose();
    return font;
  }*/

  /** Draws a rectangle tiled with {@link TextureRegion} with an offset.
   * FIXME: unfinished. */
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
        y+=regionHeight;
      }
      x+=regionWidth;
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
        y+=regionHeight;
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
        x+=regionWidth;
      }
    }
    if (remainingX > 0) {
      float u2 = u + remainingX / texture.getWidth();
      float v = region.getV();
      y = fullHeight;
      for (int i=0; i < fullY; i++) {
        batch.draw(texture, x, y, remainingX, regionHeight, u, v2, u2, v);
        y+=regionHeight;
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
        x+=regionWidth;
      }
    }
  }

  /************* Scene utilities ************/

  public static void addActors(Group g, Actor... a) {
    for (Actor actor : a) {
      g.addActor(actor);
    }
  }

  public static boolean hasChild(Group g, Actor a) {
    return g.getChildren().contains(a, true);
  }

  /** Turns actor visible. */
  public static void showActor(Actor actor) {
    actor.setVisible(true);
  }

  /** Turns actors visible. */
  public static void showActors(Actor... actors) {
    for (Actor actor : actors) {
      showActor(actor);
    }
  }

  /** Turns actor invisible. */
  public static void hideActor(Actor actor) {
    actor.setVisible(false);
  }

  /** Turns actors invisible. */
  public static void hideActors(Actor... actors) {
    for (Actor actor : actors) {
      hideActor(actor);
    }
  }

  public static float getAlpha(Actor actor) {
    return actor.getColor().a;
  }

  public static float getAlpha(Sprite sprite) {
    return sprite.getColor().a;
  }

  public static void setAlpha(Actor actor, float a) {
    actor.getColor().a = a;
  }

  /** Sets alpha to 0. */
  public static void resetAlpha(Actor actor) {
    actor.getColor().a = 0;
  }

  /** Sets alpha to 0. */
  public static void resetAlpha(Sprite sprite) {
    sprite.setAlpha(0);
  }

  /** Sets alpha to 0. */
  public static void resetAlpha(Actor... actors) {
    for (Actor actor : actors) {
      resetAlpha(actor);
    }
  }

  /** Sets alpha to 0. */
  public static void resetAlpha(Sprite... sprites) {
    for (Sprite sprite : sprites) {
      resetAlpha(sprite);
    }
  }

  public static void setRgb(Actor actor, float r, float g, float b) {
    actor.setColor(r, g, b, getAlpha(actor));
  }

  public static void setRgb(Actor actor, Color clr) {
    setRgb(actor, clr.r, clr.g, clr.b);
  }

  public static void setRgb(Sprite spr, float r, float g, float b) {
    spr.setColor(r, g, b, getAlpha(spr));
  }

  public static void setRgb(Sprite spr, Color clr) {
    setRgb(spr, clr.r, clr.g, clr.b);
  }

  /** @return a {@link ImageButton} with the specified image and background. */
  public static ImageButton simpleImageButton(Drawable image, Drawable bg) {
    return new ImageButton(new ImageButtonStyle(bg, null, null, image, null, null));
  }

  /** @return a {@link ImageButton} with the specified image without background. */
  public static ImageButton simpleImageButton(Drawable image) {
    return simpleImageButton(image, drawable("btn_bg"));
  }

  /** @return a {@link ImageButton} with the specified image and background skin drawable names. */
  public static ImageButton simpleImageButton(String image, String bg) {
    return simpleImageButton(drawable(image), drawable(bg));
  }

  /** @return a {@link ImageButton} with the specified image skin drawable name without background. */
  public static ImageButton simpleImageButton(String image) {
    return simpleImageButton(drawable(image));
  }

  /** Sets the {@link ImageButton} {@link ImageButtonStyle#imageUp}. */
  public static void setImageButtonImage(ImageButton btn, Drawable image) {
    btn.getStyle().imageUp = image;
  }

  /** Converts an existing {@link ButtonStyle} to a rounded one 
   * by turning its drawables into {@link RoundedDrawable}.<br><br>
   * Warning: this function permanently mutates the specified {@link ButtonStyle}.
   * @return converted {@link ButtonStyle}. */
  public static ButtonStyle convertToRounded(ButtonStyle style) {
    Drawable up = style.up, down = style.down, over = style.over, checked = style.checked,
        checkedOver = style.checkedOver, disabled = style.disabled;
    // turning each drawable rounded
    if (up != null && up instanceof NinePatchDrawable) {
      style.up = new RoundedDrawable((NinePatchDrawable)up);
    } else if (up != null && up instanceof RoundedDrawable) {
      style.up = (RoundedDrawable)up;
    }
    if (down != null && down instanceof NinePatchDrawable) {
      style.up = new RoundedDrawable((NinePatchDrawable)down);
    } else if (down != null && down instanceof RoundedDrawable) {
      style.up = (RoundedDrawable)down;
    }
    if (over != null && over instanceof NinePatchDrawable) {
      style.up = new RoundedDrawable((NinePatchDrawable)over);
    } else if (over != null && over instanceof RoundedDrawable) {
      style.up = (RoundedDrawable)over;
    }
    if (checked != null && checked instanceof NinePatchDrawable) {
      style.up = new RoundedDrawable((NinePatchDrawable)checked);
    } else if (checked != null && checked instanceof RoundedDrawable) {
      style.up = (RoundedDrawable)checked;
    }
    if (checkedOver != null && checkedOver instanceof NinePatchDrawable) {
      style.up = new RoundedDrawable((NinePatchDrawable)checkedOver);
    } else if (checkedOver != null && checkedOver instanceof RoundedDrawable) {
      style.up = (RoundedDrawable)checkedOver;
    }
    if (disabled != null && disabled instanceof NinePatchDrawable) {
      style.up = new RoundedDrawable((NinePatchDrawable)disabled);
    } else if (disabled != null && disabled instanceof RoundedDrawable) {
      style.up = (RoundedDrawable)disabled;
    }
    return style;
  }

  public static TextButton roundedTextButton(String text, Skin skin, String styleName) {
    TextButton btn = new TextButton(text, skin, styleName);
    convertToRounded(btn.getStyle());
    // adding 2 to bypass the linear filter clipping
    btn.setHeight(btn.getStyle().up.getMinHeight() + 2);
    btn.pad(0);
    return btn;
  }

  public static ImageButton roundedImageButton(ImageButton btn) {
    convertToRounded(btn.getStyle());
    // adding 2 to bypass the linear filter clipping
    btn.setHeight(btn.getStyle().up.getMinHeight() + 2);
    btn.pad(0);
    return btn;
  }

  public static ImageButton roundedImageButton(String text, Skin skin, String styleName) {
    return roundedImageButton(new ImageButton(skin, styleName));
  }

  /** @return {@code null} if nothing found. */
  public static ParticleEmitter getPfxEmitterByName(ParticleEffect pfx, String name) {
    for (ParticleEmitter emitter : pfx.getEmitters()) {
      if (emitter.getName().equals(name)) {
        return emitter;
      }
    }
    return null;
  }

  public static ClickListener simpleClickListener(ClickCallback cb) {
    return new ClickListener() {
      @Override public void clicked(InputEvent event, float x, float y) {
        cb.clicked(event, x, y);
      }
    };
  }

  /************* Box2D utilities ************/

  /** @return {@link Filter#categoryBits} of a Box2D {@link Fixture}. */
  public static short categoryBits(Fixture fix) {
    return fix.getFilterData().categoryBits;
  }

  /** @return {@link maskBits} of a Box2D {@link Fixture}. */
  public static short maskBits(Fixture fix) {
    return fix.getFilterData().maskBits;
  }

  /** @return {@link groupIndex} of a Box2D {@link Fixture}. */
  public static short groupIndex(Fixture fix) {
    return fix.getFilterData().groupIndex;
  }

  public static boolean fixtureIsInContact(Contact contact, Fixture fix) {
    return contact.getFixtureA()==fix || contact.getFixtureB()==fix;
  }

  public static boolean checkContactByCategory(Fixture fixa, Fixture fixb, short cba, short cbb) {
    return categoryBits(fixa)==cba && categoryBits(fixb)==cbb;
  }

  public static boolean checkContactByMask(Fixture fixa, Fixture fixb, short mba, short mbb) {
    return maskBits(fixa)==mba && maskBits(fixb)==mbb;
  }

  public static boolean checkContactByGroup(Fixture fixa, Fixture fixb, short gia, short gib) {
    return groupIndex(fixa)==gia && groupIndex(fixb)==gib;
  }

  public static boolean checkContactByCategory(Contact contact, short cba, short cbb) {
    return checkContactByCategory(contact.getFixtureA(), contact.getFixtureB(), cba, cbb);
  }

  public static boolean checkContactByMask(Contact contact, short mba, short mbb) {
    return checkContactByMask(contact.getFixtureA(), contact.getFixtureB(), mba, mbb);
  }

  public static boolean checkContactByGroup(Contact contact, short gia, short gib) {
    return checkContactByGroup(contact.getFixtureA(), contact.getFixtureB(), gia, gib);
  }

  public static boolean checkUnorderedContactByCategory(Fixture fixa, Fixture fixb, short cba, short cbb) {
    return (categoryBits(fixa)==cba && categoryBits(fixb)==cbb) || (categoryBits(fixa)==cbb && categoryBits(fixb)==cba);
  }

  public static boolean checkUnorderedContactByMask(Fixture fixa, Fixture fixb, short mba, short mbb) {
    return (maskBits(fixa)==mba && maskBits(fixb)==mbb) || (maskBits(fixa)==mbb && maskBits(fixb)==mba);
  }

  public static boolean checkUnorderedContactByGroup(Fixture fixa, Fixture fixb, short gia, short gib) {
    return (groupIndex(fixa)==gia && groupIndex(fixb)==gib) || (maskBits(fixa)==gib && maskBits(fixb)==gia);
  }

  public static boolean checkUnorderedContactByCategory(Contact contact, short cba, short cbb) {
    return checkUnorderedContactByGroup(contact.getFixtureA(), contact.getFixtureB(), cba, cbb);
  }

  public static boolean checkUnorderedContactByMask(Contact contact, short mba, short mbb) {
    return checkUnorderedContactByMask(contact.getFixtureA(), contact.getFixtureB(), mba, mbb);
  }

  public static boolean checkUnorderedContactByGroup(Contact contact, short gia, short gib) {
    return checkUnorderedContactByGroup(contact.getFixtureA(), contact.getFixtureB(), gia, gib);
  }

  /************* Color utilities ************/

  public static void setAlpha(Color clr, float a) {
    clr.a = a;
  }

  /** @return HSL hue of a color. */
  public static float getHue(Color clr) {
    return fatan2((float)(SQRT3 * (clr.g-clr.b)), 2*clr.r - clr.g - clr.b);
  }

  /** @return HSL hue of a color. */
  public static float getSat(Color clr) {
    float min = tmin(clr.r, clr.g, clr.b), max = tmax(clr.r, clr.g, clr.b);
    return (max - min) / max;
  }

  /** @return HSL hue of a color. */
  public static float getLgt(Color clr) {
    return tmax(clr.r, clr.g, clr.b);
  }

  public static Vector3 getHsl(Color clr) {
    return tmpv3.set(getHue(clr), getSat(clr), getLgt(clr));
  }

  /** Sets HSL hue. */
  public static Color setHue(Color clr, float h) {
    float r = clr.r, g = clr.g, b = clr.b, u = (float)cos(h * DEGREE), w = (float)sin(h * DEGREE);
    return clr.set((.299f + .701f * u+.168f*w)*r + (.587f-.587f*u+.330f*w)*g + (.114f-.114f*u-.497f*w)*b,
        (.299f-.299f*u-.328f*w)*r + (.587f+.413f*u+.035f*w)*g + (.114f-.114f*u+.292f*w)*b,
        (.299f-.3f*u+1.25f*w)*r + (.587f-.588f*u-1.05f*w)*g + (.114f+.886f*u-.203f*w)*b,
        clr.a);
  }

  /** Sets HSL saturation. */
  public static Color setSat(Color clr, float s) {
    float r = clr.r, g = clr.g, b = clr.b,
        min = tmin(clr.r, clr.g, clr.b), max = tmax(clr.r, clr.g, clr.b), amin = (1-s)*max;
    return clr.set(r==max ? r : r==min ? amin : amin + (max-amin) * ((r-min)/(max-min)),
        g==max ? g : g==min ? amin : amin + (max-amin) * ((g-min)/(max-min)),
        b==max ? b : b==min ? amin : amin + (max-amin) * ((b-min)/(max-min)),
        clr.a);
  }

  /** Sets HSL lightness. */
  public static Color setLgt(Color clr, float l) {
    return clr.mul(l / getLgt(clr));
  }

  public static Vector3 HslToRgb(float h, float s, float l) {
    float hs = fract(h)*6, f = fract(hs), p = l*(1-s), q = l*(1-f*s), t = l*(1-(1-f)*s);
    switch ((int)hs) {
      case 0: return tmpv3.set(l,t,p);
      case 1: return tmpv3.set(q,l,p);
      case 2: return tmpv3.set(p,l,t);
      case 3: return tmpv3.set(p,q,l);
      case 4: return tmpv3.set(t,p,l);
      case 5: return tmpv3.set(l,p,q);
      default: illegalArgument("hs = "+hs); return null;
    }
  }

  public static Vector3 HslToRgb(Vector3 hsl) {
    return HslToRgb(hsl.x, hsl.y, hsl.z);
  }

  public static Color setHsl(Color clr, float h, float s, float l) {
    tmpv3.set(HslToRgb(h,s,l));
    return clr.set(tmpv3.x, tmpv3.y, tmpv3.z, clr.a);
  }

  /************** Math utilities ************/

  public static float fsin(float a)			{return (float)sin(a);}
  public static float fsin(double a)			{return (float)sin(a);}
  public static float fcos(float a)			{return (float)cos(a);}
  public static float fcos(double a)			{return (float)cos(a);}
  public static float ftan(float a)			{return (float)tan(a);}
  public static float ftan(double a)			{return (float)tan(a);}
  public static float fasin(float a)			{return (float)asin(a);}
  public static float fasin(double a)			{return (float)asin(a);}
  public static float facos(float a)			{return (float)acos(a);}
  public static float facos(double a)			{return (float)acos(a);}
  public static float fatan(float a)			{return (float)atan(a);}
  public static float fatan(double a)			{return (float)atan(a);}
  public static float fexp(float a)			{return (float)exp(a);}
  public static float fexp(double a)			{return (float)exp(a);}
  public static float flog(float a)			{return (float)log(a);}
  public static float flog(double a)			{return (float)log(a);}
  public static float flog10(float a)			{return (float)log10(a);}
  public static float flog10(double a)			{return (float)log10(a);}
  public static float fsqrt(float a)			{return (float)sqrt(a);}
  public static float fsqrt(double a)			{return (float)sqrt(a);}
  public static float fcbrt(float a)			{return (float)cbrt(a);}
  public static float fcbrt(double a)			{return (float)cbrt(a);}
  public static float fceil(float a)			{return (float)ceil(a);}
  public static float fceil(double a)			{return (float)ceil(a);}
  public static float ffloor(float a)			{return (float)floor(a);}
  public static float ffloor(double a)			{return (float)floor(a);}
  public static float frint(float a)			{return (float)rint(a);}
  public static float frint(double a)			{return (float)rint(a);}
  public static float fatan2(float y, float x)		{return (float)atan2(y,x);}
  public static float fatan2(double y, double x)	{return (float)atan2(y,x);}
  public static float fpow(float a, float b)		{return (float)pow(a,b);}
  public static float fpow(double a, double b)		{return (float)pow(a,b);}
  public static float fsinh(float x)			{return (float)sinh(x);}
  public static float fsinh(double x)			{return (float)sinh(x);}
  public static float fcosh(float x)			{return (float)cosh(x);}
  public static float fcosh(double x)			{return (float)cosh(x);}
  public static float ftanh(float x)			{return (float)tanh(x);}
  public static float ftanh(double x)			{return (float)tanh(x);}
  public static float fhypot(float x, float y)		{return (float)hypot(x,y);}
  public static float fhypot(double x, double y)	{return (float)hypot(x,y);}

  /** @return if {@code flag} intersects {@code flags}. {@code flag} has to be one single bit. */
  public static boolean hasFlag(int flags, int flag) {
    return (flags&flag)!=0;
  }

  /** @return if {@code flag} intersects {@code flags}. {@code flag} has to be one single bit. */
  public static boolean hasFlag(long flags, long flag) {
    return (flags&flag)!=0;
  }

  /** @return a union of all flags preceding {@code flag}.
   * @author Nayuki */
  public static int sumFlags(int flag) {
    return flag*2-1;
  }

  /** @return a union of all flags preceding {@code flag}.
   * @author Nayuki */
  public static long sumFlags(long flag) {
    return flag*2-1;
  }

  /** @return binary shift of {@code a}. */
  public static int getShift(int a) {
    return (int)(log(a)/LOG2);
  }

  /** @return binary shift of {@code a}. */
  public static long getShift(long a) {
    return (long)(log(a)/LOG2);
  }

  /** @return random double in a range from 0 to 1. */
  public static double rand() {
    return random();
  }

  /** @return random double in a range from 0 to {@code a}. */
  public static double rand(double a) {
    return a*rand();
  }

  /** @return random double in a range from {@code a} to {@code b}. */
  public static double rand(double a, double b) {
    return a+(b-a)*rand();
  }

  /** @return random float in a range from 0 to 1. */
  public static float randFloat() {
    return (float)random();
  }

  /** @return random float in a range from 0 to {@code a}. */
  public static float randFloat(float a) {
    return a*randFloat();
  }

  /** @return random float in a range from {@code a} to {@code b}. */
  public static float randFloat(float a, float b) {
    return a+(b-a)*randFloat();
  }

  /** @return random int in a range from 0 to {@code a}. */
  public static int randInt(int a) {
    return round(randFloat(a));
  }

  /** @return random int in a range from {@code a} to {@code b}. */
  public static int randInt(int a, int b) {
    return round(randFloat(a,b));
  }

  /** @return random bool, either true or false. */
  public static boolean randBool() {
    return randInt(1) > 0;
  }

  /** @return is {@code a} even.
   * @author Nayuki */
  public static boolean isEven(int a) {
    return (a&1)==0;
  }

  /** @return is {@code a} even.
   * @author Nayuki */
  public static boolean isEven(long a) {
    return (a&1)==0;
  }

  /** @return is {@code a} even. */
  public static boolean isEven(float a) {
    return (dec(a)%2)==0;
  }

  /** @return is {@code a} even. */
  public static boolean isEven(double a) {
    return (dec(a)%2)==0;
  }

  /** @return is {@code a} decimal. */
  public static boolean isDec(float a) {
    return fract(a)==0;
  }

  /** @return is {@code a} decimal. */
  public static boolean isDec(double a) {
    return fract(a)==0;
  }

  /** @return the fraction of {@code a}. */
  public static float fract(float a) {
    return a%1;
  }

  /** @return the fraction of {@code a}. */
  public static double fract(double a) {
    return a%1;
  }

  /** @return the fraction of a vector. */
  public static Vector2 fract(Vector2 v2) {
    return tmpv2.set(fract(v2.x), fract(v2.y));
  }

  /** @return the fraction of a vector. */
  public static Vector3 fract(Vector3 v3) {
    return tmpv3.set(fract(v3.x), fract(v3.y), fract(v3.z));
  }

  /** @return the decimal part of {@code a}. */
  public static float dec(float a) {
    return a-fract(a);
  }

  /** @return the decimal part of {@code a}. */
  public static double dec(double a) {
    return a-fract(a);
  }

  /** @return float floor of a vector. */
  public static Vector2 ffloor(Vector2 v2) {
    return tmpv2.set(ffloor(v2.x), ffloor(v2.y));
  }

  /** @return float floor of a vector. */
  public static Vector3 ffloor(Vector3 v3) {
    return tmpv3.set(ffloor(v3.x), ffloor(v3.y), ffloor(v3.z));
  }

  /** @return {@code a} bounded between {@code min} and {@code max}. */
  public static int clamp(int a, int min, int max) {
    if (min>max) {
      int t = min;
      min = max;
      max = t;
    }
    return a<min ? min : a>max?max:a;
  }

  /** @return {@code a} bounded between {@code min} and {@code max}. */
  public static long clamp(long a, long min, long max) {
    if (min>max) {
      long t = min;
      min = max;
      max = t;
    }
    return a<min ? min : a>max?max:a;
  }

  /** @return {@code a} bounded between {@code min} and {@code max}. */
  public static float clamp(float a, float min, float max) {
    if (min>max) {
      float t = min;
      min = max;
      max = t;
    }
    return a<min ? min : a>max?max:a;
  }

  /** @return {@code a} bounded between {@code min} and {@code max}. */
  public static double clamp(double a, double min, double max) {
    if (min>max) {
      double t = min;
      min = max;
      max = t;
    }
    return a<min ? min : a>max?max:a;
  }

  /** @return the greatest of the three arguments. */
  public static int tmax(int a, int b, int c) {
    return max(a, max(b, c));
  }

  /** @return the greatest of the three arguments. */
  public static long tmax(long a, long b, long c) {
    return max(a, max(b, c));
  }

  /** @return the greatest of the three arguments. */
  public static float tmax(float a, float b, float c) {
    return max(a, max(b, c));
  }

  /** @return the greatest of the three arguments. */
  public static double tmax(double a, double b, double c) {
    return max(a, max(b, c));
  }

  /** @return the smallest of the three arguments. */
  public static int tmin(int a, int b, int c) {
    return min(a, min(b, c));
  }

  /** @return the smallest of the three arguments. */
  public static long tmin(long a, long b, long c) {
    return min(a, min(b, c));
  }

  /** @return the smallest of the three arguments. */
  public static float tmin(float a, float b, float c) {
    return min(a, min(b, c));
  }

  /** @return the smallest of the three arguments. */
  public static double tmin(double a, double b, double c) {
    return min(a, min(b, c));
  }

  /** @return the absolute values. */
  public static Vector2 abs(Vector2 v2) {
    return tmpv2.set(Math.abs(v2.x), Math.abs(v2.y));
  }

  /** @return the absolute values. */
  public static Vector3 abs(Vector3 v3) {
    return v3.set(Math.abs(v3.x), Math.abs(v3.y), Math.abs(v3.z));
  }

  /** @return sine of {@code a} multiplied by {@code m}. */
  public static double msin(double a, double m) {
    return sin(a)*m;
  }

  /** @return cosine of {@code a} multiplied by {@code m}. */
  public static double mcos(double a, double m) {
    return cos(a)*m;
  }

  /** @return sine of {@code a} multiplied by {@code m}. */
  public static float msin(double a, float m) {
    return (float)sin(a)*m;
  }

  /** @return cosine of {@code a} multiplied by {@code m}. */
  public static float mcos(double a, float m) {
    return (float)cos(a)*m;
  }

  /** @return multiplication of {@code a} by {@code n} divided by {@code d}. */
  public static float div(float a, float n, float d) {
    return a*(n/d);
  }

  /** @return multiplication of {@code a} by {@code n} divided by {@code d}. */
  public static double div(double a, double n, double d) {
    return a*(n/d);
  }

  /** @return is {@code a} divisible by {@code b}. */
  public static boolean isDivBy(int a, int b) {
    return a%b==0;
  }

  /** @return is {@code a} divisible by {@code b}. */
  public static boolean isDivBy(float a, float b) {
    return a%b==0;
  }

  /** @return is {@code a} divisible by {@code b}. */
  public static boolean isDivBy(double a, double b) {
    return a%b==0;
  }

  /** @return distance between two given points. */
  public static float dist(float x1, float y1, float x2, float y2) {
    return fhypot(x2-x1, y2-y1);
  }

  /** @return distance between two given points. */
  public static float dist(Vector2 p1, Vector2 p2) {
    return dist(p1.x, p1.y, p2.x, p2.y);
  }

  /** @param r radius
   * @return side of a circumscribed square. */
  public static float circSqrSide(float r) {
    return (float)(r*SQRT2)/2;
  }

  /** @param p power
   * @param a 0 to 1 value
   * @return exponential interpolation of {@code a} based on {@code p}. */
  public static float easeIn(float p, float a) {
    return fpow(a,p);
  }

  /** @param a 0 to 1 value
   * @return quartic exponential interpolation of {@code a}. */
  public static float easeIn(float a) {
    return easeIn(4, a);
  }

  /** @param p power
   * @param a 0 to 1 value
   * @return exponential interpolation of {@code a} based on {@code p}. */
  public static float easeOut(float p, float a) {
    float m = fpow(-1,ffloor(p)-1);
    boolean c = p > 2;
    return m * ((a-=(c?1:0)) * (c?fpow(a,p-1):(a-2)) + (c?m:0));
  }

  /** @param a 0 to 1 value
   * @return quartic exponential interpolation of {@code a}. */
  public static float easeOut(float a) {
    return easeOut(4, a);
  }

  /** @param p power
   * @param a 0 to 1 value
   * @return exponential interpolation of {@code a} based on {@code p}. */
  public static float easeInOut(float p, float a) {
    float m = fpow(-1,ffloor(p)-1), b = p>2?2:1;
    if ((a*=2) < 1) {
      return .5f*fpow(a,p);
    }
    return m*.5f * ((a-=b) * (p>2?fpow(a,p-1):a-2) + m*b);
  }

  /** @param a 0 to 1 value
   * @return quartic exponential interpolation of {@code a}. */
  public static float easeInOut(float a) {
    return easeInOut(4, a);
  }

  /** @param p power
   * @param a 0 to 1 value
   * @return exponential interpolation of {@code a} based on {@code p}. */
  public static double easeIn(double p, double a) {
    return pow(a,p);
  }

  /** @param a 0 to 1 value
   * @return quartic exponential interpolation of {@code a}. */
  public static double easeIn(double a) {
    return easeIn(4, a);
  }

  /** @param p power
   * @param a 0 to 1 value
   * @return exponential interpolation of {@code a} based on {@code p}. */
  public static double easeOut(double p, double a) {
    double m = pow(-1,floor(p)-1);
    boolean c = p > 2;
    return m * ((a-=(c?1:0)) * (c?pow(a,p-1):(a-2)) + (c?m:0));
  }

  /** @param a 0 to 1 value
   * @return quartic exponential interpolation of {@code a}. */
  public static double easeOut(double a) {
    return easeOut(4, a);
  }

  /** @param p power
   * @param a 0 to 1 value
   * @return exponential interpolation of {@code a} based on {@code p}. */
  public static double easeInOut(double p, double a) {
    double m = pow(-1,floor(p)-1), b = p>2?2:1;
    if ((a*=2) < 1) {
      return .5*pow(a,p);
    }
    return m*.5 * ((a-=b) * (p>2?pow(a,p-1):a-2) + m*b);
  }

  /** @param a 0 to 1 value
   * @return quartic exponential interpolation of {@code a}. */
  public static double easeInOut(double a) {
    return easeInOut(4, a);
  }

  /************** Type utilities ************/

  public static Integer	wrap(int a)	{return new Integer(a);}
  public static Long	wrap(long a)	{return new Long(a);}
  public static Float	wrap(float a)	{return new Float(a);}
  public static Double	wrap(double a)	{return new Double(a);}
  public static Boolean	wrap(boolean a)	{return new Boolean(a);}

  /************** JSON utilities ************/

  public static String			toJson(Object obj)			{return (new Json()).toJson(obj);}
  public static <T extends Object> T	readJson(Class<T> type, String data)	{return (new Json()).fromJson(type, data);}
  public static int			JsonInt(JSONObject obj, String key)	{return ((Long)obj.get(key)).intValue();}
  public static short			JsonShort(JSONObject obj, String key)	{return ((Long)obj.get(key)).shortValue();}
  public static long			JsonLong(JSONObject obj, String key)	{return ((Long)obj.get(key)).longValue();}
  public static float			JsonFloat(JSONObject obj, String key)	{return ((Double)obj.get(key)).floatValue();}
  public static double			JsonDouble(JSONObject obj, String key)	{return ((Double)obj.get(key)).doubleValue();}
  public static boolean			JsonBool(JSONObject obj, String key)	{return ((Boolean)obj.get(key)).booleanValue();}
  public static String			JsonString(JSONObject obj, String key)	{return (String)obj.get(key);}
  public static JSONObject		JsonObject(JSONObject obj, String key)	{return (JSONObject)obj.get(key);}
  public static JSONArray		JsonArray(JSONObject obj, String key)	{return (JSONArray)obj.get(key);}
}

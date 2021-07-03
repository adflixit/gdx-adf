package adf.gdx;

import static adf.gdx.BaseContext.*;
import static adf.gdx.MathUtil.hasFlag;
import static aurelienribon.tweenengine.TweenCallback.*;

import adf.gdx.utils.Soft;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.primitives.MutableFloat;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

import java.util.ArrayList;
import java.util.List;

/**
 * Draws the specified texture (usually the frame buffer output) with a shader filter.
 * TODO: tilt shift shader doesn't work correctly.
 */
public class Postprocessor extends ContextComponent<BaseContext<?>> {
  public static final int           RES_DENOM   = 4;
  
  public static final int           BLUR        = 0,
                                    TSY         = 1,
                                    TSX         = 2,
                                    TSYP        = 3;

  private static final String[]     uniNames    = {
    "u_blur",
    "u_tiltshiftY",
    "u_tiltshiftX",
    "u_tiltshiftYPos"
  };
  private static final int          uniLength   = uniNames.length;

  private ShaderProgram             firstPass;
  private ShaderProgram             lastPass;
  private FrameBuffer               fb;
  private FrameBuffer               firstFb;
  private FrameBuffer               lastFb;
  private final List<MutableFloat>  values      = new ArrayList<>();
  private int                       pass;       // update route permits
  private int                       schedule;   // one-time update route permits

  public Postprocessor(BaseContext<?> context) {
    super(context);
    for (int i=0; i < uniLength; i++) {
      values.add(new MutableFloat(0));
    }
  }

  public Postprocessor(BaseContext<?> context, FileHandle hvert, FileHandle hfrag, FileHandle vvert, FileHandle vfrag) {
    this(context);
    load(hvert, hfrag, vvert, vfrag);
  }

  public Postprocessor(BaseContext<?> context, String hvert, String hfrag, String vvert, String vfrag) {
    this(context);
    load(hvert, hfrag, vvert, vfrag);
  }

  public void load(FileHandle hvert, FileHandle hfrag, FileHandle vvert, FileHandle vfrag) {
    firstPass = new ShaderProgram(hvert, hfrag);
    lastPass = new ShaderProgram(vvert, vfrag);
  }

  public void load(String hvert, String hfrag, String vvert, String vfrag) {
    firstPass = new ShaderProgram(hvert, hfrag);
    lastPass = new ShaderProgram(vvert, vfrag);
  }

  public void reset() {
    for (int i=0; i < uniLength; i++) {
      set(i, 0);
    }
    pass = 0;
    schedule = 0;
  }

  public void begin() {
    fb.begin();
  }

  public void end() {
    fb.end();
  }

  public Texture inputTex() {
    return fb.getColorBufferTexture();
  }

  public void draw() {
    Texture tex = inputTex();
    float x = ctx.cameraX0(), y = ctx.cameraY0();

    // if the processed area is not full, the original texture has to be drawn first
    if (!isFull()) {
      bat.begin();
        bat.draw(tex, x, y, ctx.screenWidth(), ctx.screenHeight(), 0,0,1,1);
      bat.end();
    }

    // horizontal pass
    bat.setShader(firstPass);
    firstFb.begin();
    bat.begin();
      for (int i=0; i < uniLength; i++) {
        int m = 1<<i;
        if (hasFlag(pass, m) || hasFlag(schedule, m)) {
          firstPass.setUniformf(uniNames[i], value(i));
        }
      }
      bat.draw(tex, x, y, ctx.screenWidth(), ctx.screenHeight(), 0,0,1,1);
    bat.end();
    firstFb.end();

    // vertical pass
    bat.setShader(lastPass);
    lastFb.begin();
    bat.begin();
      for (int i=0; i < uniLength; i++) {
        int m = 1<<i;
        if (hasFlag(pass, m) || hasFlag(schedule, m)) {
          lastPass.setUniformf(uniNames[i], value(i));
          schedule ^= 1<<i;
        }
      }
      tex = firstFb.getColorBufferTexture();
      bat.draw(tex, x, y, ctx.screenWidth(), ctx.screenHeight(), 0,0,1,1);
    bat.end();
    lastFb.end();

    bat.setShader(null);
    bat.begin();
      tex = lastFb.getColorBufferTexture();
      bat.draw(tex, x, y, ctx.screenWidth(), ctx.screenHeight(), 0,0,1,1);
    bat.end();
  }

  public boolean isActive() {
    return value(BLUR) + value(TSY) + value(TSX) > 0;
  }

  /**
   * @return does the filter occupy the whole screen. 
   */
  public boolean isFull() {
    return value(BLUR) > 0;
  }

  public float value(int i) {
    return values.get(i).floatValue();
  }

  public void set(int i, float v) {
    tweenMgr.killTarget(values.get(i));
    values.get(i).setValue(v);
    schedule |= 1>>i;
  }

  /**
   * @param i index
   * @param v value
   * @param d duration
   */
  public Tween $fade(int i, float v, float d) {
    tweenMgr.killTarget(values.get(i));
    return Tween.to(values.get(i), 0, d).target(v).ease(Soft.INOUT)
             .setCallback((type, source) -> {
               if (type == BEGIN) {
                 pass |= 1<<i;
               } else {
                 pass ^= 1<<i;
               }
             })
             .setCallbackTriggers(BEGIN|COMPLETE);
  }

  /**
   * @param i index
   * @param v value
   */
  public Tween $set(int i, float v) {
    tweenMgr.killTarget(values.get(i));
    return Tween.set(values.get(i), 0).target(v).setCallback((type, source) -> schedule |= 1<<i);
  }

  public void dispose() {
    firstPass.dispose();
    lastPass.dispose();
    firstFb.dispose();
    lastFb.dispose();
  }

  public void resize() {
    if (firstResize) {
      firstResize = false;
    } else {
      firstFb.dispose();
      lastFb.dispose();
    }
    fb = new FrameBuffer(Format.RGBA8888, ctx.fbWidth() / RES_DENOM, ctx.fbHeight() / RES_DENOM, false);
    firstFb = new FrameBuffer(Format.RGBA8888, ctx.fbWidth(), ctx.fbHeight(), false);
    lastFb = new FrameBuffer(Format.RGBA8888, ctx.fbWidth(), ctx.fbHeight(), false);
  }
}

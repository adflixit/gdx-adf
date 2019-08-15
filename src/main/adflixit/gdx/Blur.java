package adflixit.gdx;

import static adflixit.gdx.DefaultBlur.*;
import static adflixit.gdx.TweenUtils.*;
import static aurelienribon.tweenengine.TweenCallback.*;

import adflixit.gdx.misc.Soft;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.primitives.MutableFloat;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

/**
 * Performs two-pass Gaussian blur.
 */
public class Blur extends ScreenComponent<BaseContext<?>> {
  private static final int    RES_DENOM = 4;
  private static final String UNI_NAME  = "u_blur";

  private boolean             isFileBased;  // is shader code file-based
  private final FileHandle[]  files     = new FileHandle[4];

  private ShaderProgram       hpass;
  private ShaderProgram       vpass;
  private FrameBuffer         fb;
  private FrameBuffer         hfb;
  private FrameBuffer         vfb;

  private int                 iters     = 1;  // number of blur cycles
  private final MutableFloat  amount    = new MutableFloat(0);
  private boolean             pass;       // update route permit
  private boolean             scheduled;  // one-time update route permit

  public boolean              skipH;      // skip horizontal pass
  public boolean              skipV;      // skip vertical pass

  public Blur(BaseContext<?> context) {
    super(context);
  }

  public Blur(BaseContext<?> context, int iters) {
    super(context);
    setIters(iters);
  }

  public Blur(BaseContext<?> context, FileHandle hvert, FileHandle hfrag, FileHandle vvert, FileHandle vfrag) {
    super(context);
    load(hvert, hfrag, vvert, vfrag);
  }

  public Blur(BaseContext<?> context, String hvert, String hfrag, String vvert, String vfrag) {
    super(context);
    load(hvert, hfrag, vvert, vfrag);
  }

  public void load(FileHandle hvert, FileHandle hfrag, FileHandle vvert, FileHandle vfrag) {
    hpass = new ShaderProgram(hvert, hfrag);
    vpass = new ShaderProgram(vvert, vfrag);
    if (!isFileBased) {
      isFileBased = true;
      files[0] = hvert;
      files[1] = hfrag;
      files[2] = vvert;
      files[3] = vfrag;
    }
  }

  public void load(String hvert, String hfrag, String vvert, String vfrag) {
    hpass = new ShaderProgram(hvert, hfrag);
    vpass = new ShaderProgram(vvert, vfrag);
  }

  public Blur loadDefault() {
    load(BLURH_VERT, BLUR_FRAG, BLURV_VERT, BLUR_FRAG);
    return this;
  }

  public void reload() {
    if (isFileBased) {
      load(files[0], files[1], files[2], files[3]);
    }
  }

  public Blur setIters(int i) {
    iters = i;
    return this;
  }

  public void reset() {
    resetAmount();
    lock();
    unschedule();
  }

  public void draw() {
    if (skipH && skipV) {
      return;
    }

    float x = ctx.cameraX0(), y = ctx.cameraY0();
    for (int i=0; i < iters; i++) {
      pass(i, x, y);
    }

    bat.setShader(null);
    bat.begin();
      bat.draw(vfb.getColorBufferTexture(), x, y, ctx.screenWidth(), ctx.screenHeight(), 0,0,1,1);
    bat.end();

    if (scheduled) {
      unschedule();
    }
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

  /**
   * Performs blurring routine.
   * @param i iterations
   * @param x result drawing x
   * @param y result drawing y
   */
  private void pass(int i, float x, float y) {
    Texture tex;

    // horizontal pass
    if (!skipH) {
      bat.setShader(hpass);
      hfb.begin();
      bat.begin();
        if (pass || scheduled) {
          hpass.setUniformf(UNI_NAME, amount());
        }
        tex = i > 0 ? vfb.getColorBufferTexture() : inputTex();
        bat.draw(tex, x, y, ctx.screenWidth(), ctx.screenHeight());
      bat.end();
      hfb.end();
    }

    // vertical pass
    if (!skipV) {
      bat.setShader(vpass);
      vfb.begin();
      bat.begin();
        if (pass || scheduled) {
          vpass.setUniformf(UNI_NAME, amount());
        }
        tex = skipH ? inputTex() : hfb.getColorBufferTexture();
        bat.draw(tex, x, y, ctx.screenWidth(), ctx.screenHeight());
      bat.end();
      vfb.end();
    }
  }

  /**
   * Locks shader update route.
   */
  private void lock() {
    pass = false;
  }

  /**
   * Unlocks shader update route.
   */
  private void unlock() {
    pass = true;
  }

  /**
   * Schedules a one-time access to update route.
   */
  private void schedule() {
    scheduled = true;
  }

  /**
   * Resets the one-time access to update route.
   */
  private void unschedule() {
    scheduled = false;
  }

  public boolean isActive() {
    return amount() > 0;  
  }

  public float amount() {
    return amount.floatValue();
  }

  public void setAmount(float v) {
    killTweenTarget(amount);
    amount.setValue(v);
    schedule();
  }

  public void resetAmount() {
    setAmount(0);
  }

  /**
   * @param v value
   * @param d duration
   */
  public Tween $tween(float v, float d) {
    killTweenTarget(amount);
    return Tween.to(amount, 0, d).target(v).ease(Soft.INOUT)
           .setCallback((type, source) -> {
             if (type == BEGIN) {
               unlock();
             } else {
               lock();
             }
           })
           .setCallbackTriggers(BEGIN|COMPLETE);
  }

  /**
   * @param d duration
   */
  public Tween $tweenOut(float d) {
    return $tween(0, d);
  }

  /**
   * @param v value
   */
  public Tween $setAmount(float v) {
    killTweenTarget(amount);
    return Tween.set(amount, 0).target(v).setCallback((type, source) -> schedule());
  }

  public Tween $resetAmount() {
    return $setAmount(0);
  }

  public void dispose() {
    hpass.dispose();
    vpass.dispose();
    hfb.dispose();
    vfb.dispose();
  }

  public void resize() {
    if (firstResize) {
      firstResize = false;
    } else {
      fb.dispose();
      hfb.dispose();
      vfb.dispose();
    }
    fb = new FrameBuffer(Format.RGBA8888, ctx.fbWidth() / RES_DENOM, ctx.fbHeight() / RES_DENOM, false);
    hfb = new FrameBuffer(Format.RGBA8888, ctx.fbWidth() / RES_DENOM, ctx.fbHeight() / RES_DENOM, false);
    vfb = new FrameBuffer(Format.RGBA8888, ctx.fbWidth() / RES_DENOM, ctx.fbHeight() / RES_DENOM, false);
  }
}

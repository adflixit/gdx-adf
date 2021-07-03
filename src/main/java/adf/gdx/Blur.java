package adf.gdx;

import static adf.gdx.BaseContext.tweenMgr;
import static adf.gdx.DefaultBlurShader.*;
import static adf.gdx.Util.C_D;
import static aurelienribon.tweenengine.TweenCallback.*;

import adf.gdx.utils.Soft;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.primitives.MutableFloat;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

/**
 * Performs two-pass Gaussian blur.
 */
public class Blur {
  protected static final String UNI_NAME    = "u_blur";

  protected boolean             isFileBased;  // is shader code file-based
  protected final FileHandle[]  files       = new FileHandle[4];

  protected ShaderProgram       hpass;
  protected ShaderProgram       vpass;
  protected FrameBuffer         fb;
  protected FrameBuffer         hfb;
  protected FrameBuffer         vfb;

  protected Format              format      = Format.RGB888;
  protected int                 resDiv      = 4;  // resolution divider
  protected int                 iters       = 1;  // number of blur cycles
  protected Texture             inputTex;
  protected final MutableFloat  amount      = new MutableFloat(0);
  protected final MutableFloat  disp        = new MutableFloat(0);  // blur disposition, positive adds to h-pass and vice versa
  protected boolean             pass;       // update route permit
  protected boolean             schedule;   // one-time update route permit

  protected boolean             skipH;      // skip horizontal pass
  protected boolean             skipV;      // skip vertical pass

  protected boolean             firstResize = true;

  public Blur() {}

  public Blur(FileHandle hvert, FileHandle hfrag, FileHandle vvert, FileHandle vfrag) {
    load(hvert, hfrag, vvert, vfrag);
  }

  public Blur(String hvert, String hfrag, String vvert, String vfrag) {
    load(hvert, hfrag, vvert, vfrag);
  }

  public void load(FileHandle hvert, FileHandle hfrag, FileHandle vvert, FileHandle vfrag) {
    isFileBased = true;

    if (isInit()) {
      hpass.dispose();
      vpass.dispose();
    }

    hpass = new ShaderProgram(hvert, hfrag);
    vpass = new ShaderProgram(vvert, vfrag);

    files[0] = hvert;
    files[1] = hfrag;
    files[2] = vvert;
    files[3] = vfrag;
  }

  public void load(String hvert, String hfrag, String vvert, String vfrag) {
    isFileBased = false;

    if (isInit()) {
      hpass.dispose();
      vpass.dispose();
    }

    hpass = new ShaderProgram(hvert, hfrag);
    vpass = new ShaderProgram(vvert, vfrag);
  }

  public void generateShader(int size, float sigma) {
    String[] src = BlurShaderConstructor.generate(size, sigma);

    if (hpass != null) {
      hpass.dispose();
      vpass.dispose();
    }

    hpass = new ShaderProgram(src[0], src[2]);
    vpass = new ShaderProgram(src[1], src[2]);
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

  public boolean isInit() {
    return (hpass != null) && (vpass != null);
  }

  public Blur setFormat(Format fmt) {
    format = fmt;
    return this;
  }

  public Blur setResDiv(int v) {
    resDiv = v;
    return this;
  }

  public Blur setIters(int i) {
    iters = i;
    return this;
  }

  public void reset() {
    setAmount(0);
    pass = false;
    schedule = false;
  }

  /**
   * Blurs the captured texture.
   */
  public void process(Batch batch, float x, float y, float width, float height) {
    if (skipH && skipV) {
      return;
    }

    if (iters == 1) {
      pass(batch, 1, x, y, width, height);
    } else {
      for (int i=0; i < iters; i++) {
        pass(batch, i, x, y, width, height);
      }
    }
    batch.setShader(null);

    schedule = false;
  }

  /**
   * Initiates buffer capture.
   */
  public void begin() {
    fb.begin();
  }

  /**
   * Finishes buffer capture.
   */
  public void end() {
    fb.end();
  }

  public void setInputTex(Texture tex) {
    inputTex = tex;
  }

  public Texture inputTex() {
    return (inputTex == null) ? fb.getColorBufferTexture() : inputTex;
  }

  public Texture outputTex() {
    return vfb.getColorBufferTexture();
  }

  protected void setupHPassUniforms() {
    hpass.setUniformf(UNI_NAME, amount() + disp());
  }

  protected void setupVPassUniforms() {
    vpass.setUniformf(UNI_NAME, amount() - disp());
  }

  /**
   * Performs blurring routine.
   * @param i iterations
   * @param x result drawing x
   * @param y result drawing y
   */
  protected void pass(Batch batch, int i, float x, float y, float width, float height) {
    Texture tex;

    // horizontal pass
    if (!skipH) {
      batch.setShader(hpass);
      hfb.begin();
      batch.begin();
        if (pass || schedule) {
          setupHPassUniforms();
        }
        tex = (i > 0) ? vfb.getColorBufferTexture() : inputTex();
        batch.draw(tex, x, y, width, height);
      batch.end();
      hfb.end();
    }

    // vertical pass
    if (!skipV) {
      batch.setShader(vpass);
      vfb.begin();
      batch.begin();
        if (pass || schedule) {
          setupVPassUniforms();
        }
        tex = skipH ? inputTex() : hfb.getColorBufferTexture();
        batch.draw(tex, x, y, width, height);
      batch.end();
      vfb.end();
    }
  }

  public boolean isActive() {
    return amount() > 0;  
  }

  public float amount() {
    return amount.floatValue();
  }

  public void setAmount(float v) {
    tweenMgr.killTarget(amount);
    amount.setValue(v);
    schedule = true;
  }

  public void fade(float v, float d) {
    tweenMgr.killTarget(amount);
    $fade(v, d).start(tweenMgr);
  }

  public void fade(float v) {
    fade(v, C_D);
  }

  public float disp() {
    return disp.floatValue();
  }

  public void setDisp(float v) {
    tweenMgr.killTarget(disp);
    disp.setValue(v);
    schedule = true;
  }

  public void fadeDisp(float v, float d) {
    tweenMgr.killTarget(disp);
    $fadeDisp(v, d).start(tweenMgr);
  }

  public void fadeDisp(float v) {
    fadeDisp(v, C_D);
  }

  /**
   * @param v value
   * @param d duration
   */
  public Tween $fade(float v, float d) {
    return Tween.to(amount, 0, d).target(v).ease(Soft.INOUT)
             .setCallback((type, source) -> {
               if (type == BEGIN) {
                 pass = true;
               } else {
                 pass = false;
               }
             })
             .setCallbackTriggers(BEGIN|COMPLETE);
  }

  public Tween $fade(float v) {
    return $fade(v, C_D);
  }
  /**
   * @param v value
   */
  public Tween $setAmount(float v) {
    return Tween.set(amount, 0).target(v).setCallback((type, source) -> schedule = true);
  }

  /**
   * @param v value
   * @param d duration
   */
  public Tween $fadeDisp(float v, float d) {
    return Tween.to(disp, 0, d).target(v).ease(Soft.INOUT)
             .setCallback((type, source) -> {
               if (type == BEGIN) {
                 pass = true;
               } else {
                 pass = false;
               }
             })
             .setCallbackTriggers(BEGIN|COMPLETE);
  }

  /**
   * @param v value
   */
  public Tween $setDisp(float v) {
    return Tween.set(disp, 0).target(v).setCallback((type, source) -> schedule = true);
  }

  public void dispose() {
    hpass.dispose();
    vpass.dispose();
    fb.dispose();
    hfb.dispose();
    vfb.dispose();
  }

  public void resize(int width, int height) {
    if (firstResize) {
      firstResize = false;
    } else {
      fb.dispose();
      hfb.dispose();
      vfb.dispose();
    }
    fb = new FrameBuffer(format, width / resDiv, height / resDiv, false);
    hfb = new FrameBuffer(format, width / resDiv, height / resDiv, false);
    vfb = new FrameBuffer(format, width / resDiv, height / resDiv, false);
  }
}

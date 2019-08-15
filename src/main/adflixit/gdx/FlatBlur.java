package adflixit.gdx;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

/**
 * Xissburg's blur optimized for solid alpha blur.
 * TODO: not tested.
 */
public class FlatBlur {
  private static final int    RES_DENOM = 4;
  private static final int    DEF_RAD   = 7;  // default radius
  private static final String UNI_NAME  = "u_blur";
  private static final String UNI_COLOR = "u_color";

  private final ShaderProgram hpass     = new ShaderProgram(DefaultBlur.BLURH_VERT, FlatBlurShader.BLUR_FRAG);
  private final ShaderProgram vpass     = new ShaderProgram(DefaultBlur.BLURV_VERT, FlatBlurShader.BLUR_FRAG);
  private FrameBuffer         fb;
  private FrameBuffer         hfb;
  private FrameBuffer         vfb;

  private int                 width, height;
  private int                 radius    = DEF_RAD;  // number of blur cycles
  private final Color         color     = new Color(0x000000ff);

  public FlatBlur() {}

  public FlatBlur(int rad, Color clr) {
    setRadius(rad);
    setColor(clr);
  }

  public FlatBlur init(int rad, Color clr) {
    radius = rad;
    color.set(clr);
    return this;
  }

  public FlatBlur setRadius(int rad) {
    radius = rad;
    return this;
  }

  public FlatBlur setColor(Color clr) {
    color.set(clr);
    return this;
  }

  public void setSize(int width, int height) {
    if (this.width != width && this.height != height) {
      this.width = width;
      this.height = height;
      if (fb != null) {
        fb.dispose();
        hfb.dispose();
        vfb.dispose();
      }
      fb = new FrameBuffer(Format.RGBA8888, width / RES_DENOM, height / RES_DENOM, false);
      hfb = new FrameBuffer(Format.RGBA8888, width / RES_DENOM, height / RES_DENOM, false);
      vfb = new FrameBuffer(Format.RGBA8888, width / RES_DENOM, height / RES_DENOM, false);
    }
  }

  public void draw(Batch batch, float x, float y) {
    batch.begin();
      batch.draw(vfb.getColorBufferTexture(), x, y, width, height, 0,0,1,1);
    batch.end();
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

  public Texture outputTex() {
    return vfb.getColorBufferTexture();
  }

  public void make(Batch batch) {
    int iters = radius / DEF_RAD;
    float residue = radius % DEF_RAD;

    for (int i=0; i < iters; i++) {
      pass(batch, i, i < iters-1 ? 1 : residue);
    }

    batch.setShader(null);
  }

  /**
   * Performs a blurring routine.
   * @param i iterations
   */
  private void pass(Batch batch, int i, float amount) {
    Texture tex;
    float width = inputTex().getWidth();
    float height = inputTex().getHeight();

    // horizontal pass
    batch.setShader(hpass);
    hfb.begin();
    batch.begin();
      hpass.setUniformf(UNI_NAME, amount);
      hpass.setUniformf(UNI_COLOR, color);
      tex = i > 0 ? vfb.getColorBufferTexture() : inputTex();
      batch.draw(tex, radius, radius, width, height);
    batch.end();
    hfb.end();

    // vertical pass
    batch.setShader(vpass);
    vfb.begin();
    batch.begin();
      vpass.setUniformf(UNI_NAME, amount);
      vpass.setUniformf(UNI_COLOR, color);
      tex = hfb.getColorBufferTexture();
      batch.draw(tex, radius, radius, width, height);
    batch.end();
    vfb.end();
  }

  public void dispose() {
    hpass.dispose();
    vpass.dispose();
    fb.dispose();
    hfb.dispose();
    vfb.dispose();
  }
}

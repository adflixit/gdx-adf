/*
 * Copyright 2019 Adflixit
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
public class FillBlur {
  private static final int    RES_DENOM = 4;
  private static final int    DEF_RAD   = 7;  // default radius
  private static final String UNI_NAME  = "u_blur";
  private static final String UNI_COLOR = "u_color";

  private final ShaderProgram hpass   = new ShaderProgram(DefaultBlur.BLURH_VERT, FillBlurShader.BLUR_FRAG);
  private final ShaderProgram vpass   = new ShaderProgram(DefaultBlur.BLURV_VERT, FillBlurShader.BLUR_FRAG);
  private FrameBuffer         fb;
  private FrameBuffer         hfb;
  private FrameBuffer         vfb;
  private Texture             inputTex;

  private int                 width, height;
  private int                 radius  = DEF_RAD;  // number of blur cycles
  private final Color         color   = new Color(0x000000ff);

  public FillBlur() {}

  public FillBlur(int rad, Color clr) {
    setRadius(rad);
    setColor(clr);
  }

  public FillBlur init(int rad, Color clr) {
    radius = rad;
    color.set(clr);
    return this;
  }

  public FillBlur setRadius(int rad) {
    radius = rad;
    return this;
  }

  public FillBlur setColor(Color clr) {
    color.set(clr);
    return this;
  }

  private void setSize(int width, int height) {
    this.width = width;
    this.height = height;
    if (fb != null) {
      fb.dispose();
      hfb.dispose();
      vfb.dispose();
    }
    fb = new FrameBuffer(Format.RGBA8888, width + radius*2, height + radius*2, false);
    hfb = new FrameBuffer(Format.RGBA8888, (width + radius*2) / RES_DENOM, (height + radius*2) / RES_DENOM, false);
    vfb = new FrameBuffer(Format.RGBA8888, (width + radius*2) / RES_DENOM, (height + radius*2) / RES_DENOM, false);
  }

  /**
   * @return resulting image.
   */
  public Texture outputTex() {
    return fb.getColorBufferTexture();
  }

  /**
   * Does the blur by the settings.
   */
  public Texture make(Batch batch, Texture tex) {
    inputTex = tex;
    setSize(tex.getWidth(), tex.getHeight());
    int iters = radius / DEF_RAD;
    float residue = radius % DEF_RAD;

    for (int i=0; i < iters; i++) {
      pass(batch, i, i < iters-1 ? 1 : residue);
    }

    batch.setShader(null);
    fb.begin();
    batch.begin();
      batch.draw(vfb.getColorBufferTexture(), radius, radius, width, height, 0,0,1,1);
    batch.end();
    fb.end();

    return outputTex();
  }

  /**
   * Performs a blurring routine.
   * @param i iterations
   */
  private void pass(Batch batch, int i, float amount) {
    Texture tex;
    float width = inputTex.getWidth();
    float height = inputTex.getHeight();

    // horizontal pass
    batch.setShader(hpass);
    hfb.begin();
    batch.begin();
      hpass.setUniformf(UNI_NAME, amount);
      tex = i > 0 ? vfb.getColorBufferTexture() : inputTex;
      batch.draw(tex, radius, radius, width, height);
    batch.end();
    hfb.end();

    // vertical pass
    batch.setShader(vpass);
    vfb.begin();
    batch.begin();
      vpass.setUniformf(UNI_NAME, amount);
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

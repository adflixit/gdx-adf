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

package adflixit.shared.misc;

import static adflixit.shared.Util.*;
import static com.badlogic.gdx.utils.Align.*;

import adflixit.shared.thirdparty.BlurUtils;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class DropShadowLabel extends Label {
  private static final Color        tempColor = new Color();
  private static final FrameBuffer  fb        = new FrameBuffer(Format.RGBA8888, 1024, 1024, false);
  private static final SpriteBatch  batch     = new SpriteBatch();
  private Color   color   = new Color(0x000000ff);
  private float   offsetX;
  private float   offsetY = C_SHD_OFS;
  private float   opacity = C_SHD_RAD;
  private int     radius  = (int)C_SHD_OP;
  private Texture shadow;
  public boolean  dependOnParentAlpha;  // if true, shadow alpha will depend on parent alpha if it's greater than 0

  public DropShadowLabel(CharSequence text, Skin skin) {
    super(text, skin);
  }

  public DropShadowLabel(CharSequence text, Skin skin, String styleName) {
    super(text, skin, styleName);
  }

  public DropShadowLabel(CharSequence text, Skin skin, String fontName, Color color) {
    super(text, skin, fontName, color);
  }

  public DropShadowLabel(CharSequence text, Skin skin, String fontName, String colorName) {
    super(text, skin, fontName, colorName);
  }

  public DropShadowLabel(CharSequence text, LabelStyle style) {
    super(text, style);
  }

  @Override public void setText(CharSequence newText) {
    super.setText(newText);
    compile();
  }

  public DropShadowLabel init(float x, float y, float op, int rad) {
    offsetX = x;
    offsetY = y;
    opacity = op;
    radius = rad;
    return this;
  }

  public DropShadowLabel init(Color clr, float x, float y, float op, int rad) {
    color.set(clr);
    init(x, y, op, rad);
    return this;
  }

  public DropShadowLabel setShadowColor(Color clr) {
    color.set(clr);
    return this;
  }

  public DropShadowLabel setShadowOffsetX(float x) {
    offsetX = x;
    return this;
  }

  public DropShadowLabel setShadowOffsetY(float y) {
    offsetY = y;
    return this;
  }

  public DropShadowLabel setShadowOpacity(float op) {
    opacity = op;
    return this;
  }

  public DropShadowLabel setShadowRadius(int rad) {
    radius = rad;
    return this;
  }

  @Override public void draw(Batch batch, float parentAlpha) {
    validate();
    Color color = tempColor.set(getColor());
    if (getStyle().background != null) {
      batch.setColor(color.r, color.g, color.b, color.a * opacity * (dependOnParentAlpha ? parentAlpha : 1));
      batch.draw(shadow, getX() + offsetX, getY() + offsetY,
          shadow.getWidth()*getFontScaleX(), shadow.getHeight()*getFontScaleY());
      color.a *= parentAlpha;
      batch.setColor(color.r, color.g, color.b, color.a);
      getStyle().background.draw(batch, getX(), getY(), getWidth(), getHeight());
    }
    if (getStyle().fontColor != null) color.mul(getStyle().fontColor);
    getBitmapFontCache().tint(color);
    getBitmapFontCache().setPosition(getX(), getY());
    getBitmapFontCache().draw(batch);
  }

  private void compile() {
    // imprinting the text
    float x = getX(), y = getY();
    setPosition(0, 1024, top);
    fb.begin();
    batch.begin();
    draw(batch, 1);
    batch.end();
    fb.end();
    setPosition(x, y);
    // trimming
    Pixmap pixmap = fb.getColorBufferTexture().getTextureData().consumePixmap();
    // finding the boundaries
    int i, j, w=0, h=0;
    int pixel=0;
    for (i=0; i < pixmap.getHeight(); i++) {
      for (j=0; j < pixmap.getWidth(); j++) {
        pixel = pixmap.getPixel(j,i);
        if (pixel!=0) {
          // the right boundary
          if (j > w) {
            w = j;
          }
          // the lower boundary
          if (i > h) {
            h = i;
          }
          // coloring
          pixmap.setColor(color.r, color.g, color.b, ((pixel & 0x000000ff)) / 255f);
        }
      }
    }
    if (shadow != null) {
      shadow.dispose();
    }
    if (radius > 0) {
      Pixmap trimmed = new Pixmap(w + radius*2, h + radius*2, Format.RGBA8888);
      trimmed.drawPixmap(pixmap, radius, radius, 0, 0, w, h);
      trimmed = BlurUtils.blur(trimmed, radius, 2, false);
      pixmap.dispose();
      pixmap = trimmed;
    }
    shadow = new Texture(pixmap);
  }
}

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

import adflixit.shared.FillBlur;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 * {@link Label} with a shadow underneath. In fact, it's a glow and it can be tinted.
 * TODO: under construction.
 */
public class DropShadowLabel extends Label {
  private static final Color tempColor = new Color();

  private final FillBlur  blur    = new FillBlur();
  private FrameBuffer     fb;

  public final Color      color   = new Color(0x000000ff);
  public float            offsetX;
  public float            offsetY = C_SHD_OFS;
  public float            opacity = C_SHD_RAD;
  public int              radius  = (int)C_SHD_OP;

  private Texture         shadow;
  public boolean          dependOnParentAlpha;  // if true, shadow alpha will depend on parent alpha if it's greater than 0

  public DropShadowLabel(CharSequence text, Skin skin) {
    super(text, skin);
    compile();
  }

  public DropShadowLabel(CharSequence text, Skin skin, String styleName) {
    super(text, skin, styleName);
    compile();
  }

  public DropShadowLabel(CharSequence text, Skin skin, String fontName, Color color) {
    super(text, skin, fontName, color);
    compile();
  }

  public DropShadowLabel(CharSequence text, Skin skin, String fontName, String colorName) {
    super(text, skin, fontName, colorName);
    compile();
  }

  public DropShadowLabel(CharSequence text, LabelStyle style) {
    super(text, style);
    compile();
  }

  @Override public void setText(CharSequence newText) {
    super.setText(newText);
    compile();
  }

  public DropShadowLabel init(Color clr, float x, float y, float op, int rad) {
    color.set(clr);
    offsetX = x;
    offsetY = y;
    opacity = op;
    radius = rad;
    blur.init(rad, clr);
    return this;
  }

  @Override public void draw(Batch batch, float parentAlpha) {
    validate();
    Color color = tempColor.set(getColor());
    batch.setColor(color.r, color.g, color.b, color.a * opacity * (dependOnParentAlpha ? parentAlpha : 1));
    batch.draw(shadow, getX() + offsetX, getY() + offsetY,
        shadow.getWidth() * getFontScaleX(), shadow.getHeight() * getFontScaleY());
    color.a *= parentAlpha;
    batch.setColor(color.r, color.g, color.b, color.a);
    getStyle().background.draw(batch, getX(), getY(), getWidth(), getHeight());
    if (getStyle().fontColor != null) color.mul(getStyle().fontColor);
    getBitmapFontCache().tint(color);
    getBitmapFontCache().setPosition(getX(), getY());
    getBitmapFontCache().draw(batch);
  }

  private void compile() {
    blur.init(radius, color);

    float width = getGlyphLayout().width;
    float height = getGlyphLayout().height;

    fb = new FrameBuffer(Format.RGBA8888, (int)width, (int)height, false);

    float x = getX(), y = getY();
    setPosition(0, height, top);

    Batch batch = getStage().getBatch();

    // imprinting the text
    fb.begin();
    batch.begin();
      super.draw(batch, 1);
    batch.end();
    fb.end();

    setPosition(x, y);

    shadow = blur.make(batch, fb.getColorBufferTexture());
  }
}

package adf.gdx.utils;

import static adf.gdx.Util.*;

import adf.gdx.FlatBlur;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * {@link Label} with a shadow underneath. In fact, it's a glow and it can be tinted.
 * TODO: under construction.
 */
public class DropShadowLabel extends Label {
  private static final Color tempColor = new Color();

  private final FlatBlur  blur    = new FlatBlur();
  public final Color      color   = new Color(0x000000ff);
  public float            offsetX;
  public float            offsetY = C_SHD_OFS;
  public float            opacity = C_SHD_RAD;
  public int              radius  = (int)C_SHD_OP;

  private Texture         shadow;
  public boolean          dependsOnParentAlpha;  // if true, shadow alpha will depend on parent alpha if it's > 0

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
    return this;
  }

  @Override public void draw(Batch batch, float parentAlpha) {
    validate();
    Color color = tempColor.set(getColor());
    batch.setColor(color.r, color.g, color.b, color.a * opacity * (dependsOnParentAlpha ? parentAlpha : 1));
    batch.draw(shadow, getX() + offsetX, getY() + offsetY,
        shadow.getWidth() * getFontScaleX(), shadow.getHeight() * getFontScaleY());
    color.a *= parentAlpha;
    batch.setColor(color.r, color.g, color.b, color.a);
    getStyle().background.draw(batch, getX(), getY(), getWidth(), getHeight());

    Viewport vp = getStage().getViewport();
    blur.draw(batch, vp.getScreenX(), vp.getScreenY());

    if (getStyle().fontColor != null) color.mul(getStyle().fontColor);
    getBitmapFontCache().tint(color);
    getBitmapFontCache().setPosition(getX(), getY());
    getBitmapFontCache().draw(batch);
  }

  private void compile() {
    Batch batch = getStage().getBatch();
    Viewport vp = getStage().getViewport();

    blur.init(radius, color);
    blur.setSize((int)vp.getWorldWidth(), (int)vp.getWorldHeight());

    // imprint the text
    blur.begin();
    batch.begin();
      super.draw(batch, 1);
    batch.end();
    blur.end();
  }
}

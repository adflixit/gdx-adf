package adflixit.gdx.misc;

import static adflixit.gdx.BaseGame.*;
import static adflixit.gdx.Util.*;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TransformDrawable;

/**
 * Draws any drawable with a shadow underneath it.
 * The shadow has to be a pre-made {@link Drawable}.
 */
public class DropShadowDrawable extends BaseDrawable implements TransformDrawable {
  private Drawable          drawable;
  private Drawable          shadow;
  private TransformDrawable transDrawable;  // in case it is a TransformDrawable
  private TransformDrawable transShadow;    // ditto
  private float             radius;         // shadow radius, has to be specified manually
  public float              opacity;        // shadow opacity
  public float              offsetX;        // shadow x offset
  public float              offsetY;        // shadow y offset

  public DropShadowDrawable(Drawable drawable, Drawable shadow, float radius, float opacity, float offsetX, float offsetY) {
    this.drawable = drawable;
    if (drawable instanceof TransformDrawable) {
      transDrawable = (TransformDrawable)drawable;
    }
    setShadow(shadow, radius, opacity, offsetX, offsetY);
  }

  public DropShadowDrawable(Drawable drawable, Drawable shadow) {
    this(drawable, shadow, C_SHD_RAD, C_SHD_OP, 0, C_SHD_OFS);
  }

  public DropShadowDrawable(Skin skin, String drawable, String shadow, float radius, float opacity, float offsetX, float offsetY) {
    this(skin.getDrawable(drawable), skin.getDrawable(shadow), radius, opacity, offsetX, offsetY);
  }

  public DropShadowDrawable(Skin skin, String drawable, String shadow) {
    this(skin, drawable, shadow, C_SHD_RAD, C_SHD_OP, 0, C_SHD_OFS);
  }

  public DropShadowDrawable(Skin skin, String drawable, float radius, float opacity, float offsetX, float offsetY) {
    this(skin, drawable, drawable+"_shd", radius, opacity, offsetX, offsetY);
  }

  public DropShadowDrawable(Skin skin, String drawable) {
    this(skin, drawable, drawable+"_shd", C_SHD_RAD, C_SHD_OP, 0, C_SHD_OFS);
  }

  public DropShadowDrawable(String drawable, String shadow, float radius, float opacity, float offsetX, float offsetY) {
    this(skin(), drawable, shadow, radius, opacity, offsetX, offsetY);
  }

  public DropShadowDrawable(String drawable, String shadow) {
    this(skin(), drawable, shadow);
  }

  public DropShadowDrawable(String drawable, float radius, float opacity, float offsetX, float offsetY) {
    this(skin(), drawable, radius, opacity, offsetX, offsetY);
  }

  public DropShadowDrawable(String drawable) {
    this(skin(), drawable);
  }

  public DropShadowDrawable() {
    this((Drawable)null, (Drawable)null);
  }

  @Override public void draw(Batch batch, float x, float y, float originX, float originY,
      float width, float height, float scaleX, float scaleY, float rotation) {
    Color clr = batch.getColor();
    float r = clr.r, g = clr.g, b = clr.b, a = clr.a;

    if (isTransformable()) {
      batch.setColor(r, g, b, opacity*a);
      transShadow.draw(batch, x - radius - offsetX, y - radius - offsetY, originX + radius, originY + radius,
          width + radius*2, height + radius*2, scaleX, scaleY, rotation);
      batch.setColor(r, g, b, a);
      transDrawable.draw(batch, x, y, originX, originY, width, height, scaleX, scaleY, rotation);
    } else {
      if (shadow != null) {
        batch.setColor(r, g, b, opacity*a);
        shadow.draw(batch, x - radius - offsetX, y - radius - offsetY, width + radius*2, height + radius*2);
        batch.setColor(r, g, b, a);
      }
      if (drawable != null) {
        drawable.draw(batch, x, y, width, height);
      }
    }
  }

  @Override public void draw(Batch batch, float x, float y, float width, float height) {
    draw(batch, x, y, 0, 0, width, height, 1, 1, 0);
  }

  private boolean isTransformable() {
    return transDrawable != null && transShadow != null;
  }

  public Drawable getShadow() {
    return shadow;
  }

  public void setShadow(Drawable shadow, float radius, float opacity, float offsetX, float offsetY) {
    if (this.shadow != shadow) {
      this.shadow = shadow;
      if (shadow instanceof TransformDrawable) {
        transShadow = (TransformDrawable)shadow;
      }
    }
    this.radius = radius;
    this.opacity = opacity;
    this.offsetX = offsetX;
    this.offsetY = offsetY;
  }

  public void setShadow(Drawable shadow) {
    setShadow(shadow, radius, opacity, offsetX, offsetY);
  }

  public DropShadowDrawable setRadius(float radius) {
    this.radius = radius;
    return this;
  }

  public DropShadowDrawable setOpacity(float opacity) {
    this.opacity = opacity;
    return this;
  }

  public DropShadowDrawable setOffsetX(float offsetX) {
    this.offsetX = offsetX;
    return this;
  }

  public DropShadowDrawable setOffsetY(float offsetY) {
    this.offsetY = offsetY;
    return this;
  }

  @Override public float getLeftWidth() {
    return drawable.getLeftWidth();
  }

  @Override public void setLeftWidth(float leftWidth) {
    drawable.setLeftWidth(leftWidth);
    shadow.setLeftWidth(leftWidth + radius/2);
  }

  @Override public float getRightWidth() {
    return drawable.getRightWidth();
  }

  @Override public void setRightWidth(float rightWidth) {
    drawable.setRightWidth(rightWidth);
    shadow.setRightWidth(rightWidth + radius/2);
  }

  @Override public float getTopHeight() {
    return drawable.getTopHeight();
  }

  @Override public void setTopHeight(float topHeight) {
    drawable.setTopHeight(topHeight);
    shadow.setTopHeight(topHeight + radius/2);
  }

  @Override public float getBottomHeight() {
    return drawable.getBottomHeight();
  }

  @Override public void setBottomHeight(float bottomHeight) {
    drawable.setBottomHeight(bottomHeight);
    shadow.setBottomHeight(bottomHeight + radius/2);
  }

  @Override public float getMinWidth() {
    return drawable.getMinWidth();
  }

  @Override public void setMinWidth(float minWidth) {
    drawable.setMinWidth(minWidth);
    shadow.setMinWidth(minWidth + radius);
  }

  @Override public float getMinHeight() {
    return drawable.getMinHeight();
  }

  @Override public void setMinHeight(float minHeight) {
    drawable.setMinHeight(minHeight);
    shadow.setMinHeight(minHeight + radius);
  }
}

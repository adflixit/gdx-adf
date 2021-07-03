package adf.gdx;

import static adf.gdx.BaseAppListener.*;

import adf.gdx.utils.DropShadowDrawable;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider.SliderStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public final class SceneUtil {
  private SceneUtil() {}

  public static Group addActors(Group group, Actor... actors) {
    for (Actor actor : actors) {
      group.addActor(actor);
    }
    return group;
  }

  public static boolean hasChild(Group group, Actor actor) {
    return group.getChildren().contains(actor, true);
  }

  /**
   * Turns actor visible.
   */
  public static Actor showActor(Actor actor) {
    actor.setVisible(true);
    return actor;
  }

  /**
   * Turns actors visible.
   */
  public static void showActors(Actor... actors) {
    for (Actor actor : actors) {
      showActor(actor);
    }
  }

  /**
   * Turns actor invisible.
   */
  public static Actor hideActor(Actor actor) {
    actor.setVisible(false);
    return actor;
  }

  /**
   * Turns actors invisible.
   */
  public static void hideActors(Actor... actors) {
    for (Actor actor : actors) {
      hideActor(actor);
    }
  }

  public static float getAlpha(Actor actor) {
    return actor.getColor().a;
  }

  public static Actor setAlpha(Actor actor, float a) {
    actor.getColor().a = a;
    return actor;
  }

  public static void setAlpha(float a, Actor... actors) {
    for (Actor actor : actors) {
      setAlpha(actor, a);
    }
  }

  public static Actor setRgb(Actor actor, float r, float g, float b) {
    actor.setColor(r, g, b, getAlpha(actor));
    return actor;
  }

  public static Actor setRgb(Actor actor, Color clr) {
    setRgb(actor, clr.r, clr.g, clr.b);
    return actor;
  }

  public static float getAlpha(Sprite sprite) {
    return sprite.getColor().a;
  }

  public static void setAlpha(float a, Sprite... sprites) {
    for (Sprite sprite : sprites) {
      sprite.setAlpha(a);
    }
  }

  public static Sprite setRgb(Sprite sprite, float r, float g, float b) {
    sprite.setColor(r, g, b, getAlpha(sprite));
    return sprite;
  }

  public static Sprite setRgb(Sprite sprite, Color clr) {
    setRgb(sprite, clr.r, clr.g, clr.b);
    return sprite;
  }

  /**
   * @return a {@link ImageButton} with the specified image and background.
   */
  public static ImageButton createImageButton(Drawable image, Drawable bg) {
    return new ImageButton(new ImageButtonStyle(bg, null, null, image, null, null));
  }

  /**
   * @return a {@link ImageButton} with the specified image without background.
   */
  public static ImageButton createImageButton(Drawable image) {
    return createImageButton(image, drawable("btn_bg"));
  }

  /**
   * @return a {@link ImageButton} with the specified image and background skin drawable names.
   */
  public static ImageButton createImageButton(String image, String bg) {
    return createImageButton(drawable(image), drawable(bg));
  }

  /**
   * @return a {@link ImageButton} with the specified image skin drawable name without background.
   */
  public static ImageButton createImageButton(String image) {
    return createImageButton(drawable(image));
  }

  /**
   * Sets the {@link ImageButton} {@link ImageButtonStyle#imageUp}.
   */
  public static void setImageButtonImage(ImageButton btn, Drawable image) {
    btn.getStyle().imageUp = image;
  }

  /**
   * Converts {@code style}'s {@link SliderStyle#knob} into {@link DropShadowDrawable}.
   * Should be called once, preferably right after skin creation.
   */
  public static void convertToDropShadow(SliderStyle style, Drawable shadow) {
    if (!(style.knob instanceof DropShadowDrawable)) {
      style.knob = new DropShadowDrawable(style.knob, shadow);
    }
  }

  /**
   * @see #convertToDropShadow(SliderStyle, Drawable)
   */
  public static void convertToDropShadow(SliderStyle style, String shadow) {
    convertToDropShadow(style, drawable(shadow));
  }

  /**
   * @see #convertToDropShadow(SliderStyle, Drawable)
   */
  public static void convertToDropShadow(Skin skin, String styleName, Drawable shadow) {
    convertToDropShadow(skin.get(styleName, SliderStyle.class), shadow);
  }

  /**
   * @see #convertToDropShadow(SliderStyle, Drawable)
   */
  public static void convertToDropShadow(Skin skin, String styleName, String shadow) {
    convertToDropShadow(skin.get(styleName, SliderStyle.class), drawable(shadow));
  }

  /**
   * @return {@code null} if nothing found.
   */
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
}

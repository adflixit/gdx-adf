/*
 * Copyright 2018 Adflixit
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

import static adflixit.shared.BaseGame.*;
import static adflixit.shared.BaseScreen.*;
import static adflixit.shared.Util.*;
import static aurelienribon.tweenengine.TweenCallback.*;
import static com.badlogic.gdx.utils.Align.*;

import android.view.View;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenAccessor;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Quart;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

/**
 * {@link Tween} {@link TweenAccessor accessors} and utilities.
 */
public final class TweenUtils {
  private TweenUtils() {}

  public static Tween		emptyTween()		{return Tween.mark();}
  public static Tween		delayTween(float d)	{return Tween.to(null, -1, d);}
  public static Timeline	emptyTimeline()		{return Timeline.createSequence();}

  /** Removes {@code target} from all trackers. */
  public static void killTweenTarget(Object target) {
    tweenMgr.killTarget(target);
    uiTweenMgr.killTarget(target);
    tscTweenMgr.killTarget(target);
  }

  /** Removes {@code targets} from all trackers. */
  public static void killTweenTargets(Object... targets) {
    for (Object target : targets) {
      killTweenTarget(target);
    }
  }

  /** Removes the specified type of tween of {@code target} from all trackers. */
  public static void killTweenTarget(Object target, int tweenType) {
    tweenMgr.killTarget(target, tweenType);
    uiTweenMgr.killTarget(target, tweenType);
    tscTweenMgr.killTarget(target, tweenType);
  }

  /** Tweens the actor's opacity.
   * @param a alpha
   * @param d duration */
  public static void fadeActor(Actor target, float a, float d) {
    $fadeActor(target, a, d).start(tweenMgr);
  }

  /** Tweens the actor's opacity.
   * @param a alpha */
  public static void fadeActor(Actor target, float a) {
    $fadeActor(target, a).start(tweenMgr);
  }

  /** Tweens the actor's opacity to 1.
   * @param d duration */
  public static void fadeActorIn(Actor target, float d) {
    $fadeActorIn(target, d).start(tweenMgr);
  }

  /** Tweens the actor's opacity to 1. */
  public static void fadeActorIn(Actor target) {
    $fadeActorIn(target).start(tweenMgr);
  }

  /** Tweens the actor's opacity to 0.
   * @param d duration */
  public static void fadeActorOut(Actor target, float d) {
    $fadeActorIn(target, d).start(tweenMgr);
  }

  /** Tweens the actor's opacity to 0. */
  public static void fadeActorOut(Actor target) {
    $fadeActorOut(target).start(tweenMgr);
  }

  /** Tweens the label's opacity.
   * @param a alpha
   * @param d duration */
  public static void fadeLabel(Label target, float a, float d) {
    $fadeLabel(target, a, d).start(tweenMgr);
  }

  /** Tweens the label's opacity.
   * @param a alpha */
  public static void fadeLabel(Label target, float a) {
    $fadeLabel(target, a).start(tweenMgr);
  }

  /** Tweens the label's opacity to 1.
   * @param d duration */
  public static void fadeLabelIn(Label target, float d) {
    $fadeLabelIn(target, d).start(tweenMgr);
  }

  /** Tweens the label's opacity to 1. */
  public static void fadeLabelIn(Label target) {
    $fadeLabelIn(target).start(tweenMgr);
  }

  /** Tweens the label's opacity to 0.
   * @param d duration */
  public static void fadeLabelOut(Label target, float d) {
    $fadeLabelOut(target, d).start(tweenMgr);
  }

  /** Tweens the label's opacity to 0. */
  public static void fadeLabelOut(Label target) {
    $fadeLabelOut(target).start(tweenMgr);
  }

  /** Tweens the actor's color.
   * @param clr color
   * @param d duration */
  public static void tweenActorColor(Actor target, Color clr, float d) {
    $tweenActorColor(target, clr, d).start(tweenMgr);
  }

  /** Tweens the actor's color.
   * @param clr color */
  public static void tweenActorColor(Actor target, Color clr) {
    $tweenActorColor(target, clr).start(tweenMgr);
  }

  /** Tweens the label's color.
   * @param clr color
   * @param d duration */
  public static void tweenLabelColor(Label target, Color clr, float d) {
    $tweenLabelColor(target, clr, d).start(tweenMgr);
  }

  /** Tweens the label's color.
   * @param clr color
   * @param d duration */
  public static void tweenLabelColor(Label target, Color clr) {
    $tweenLabelColor(target, clr).start(tweenMgr);
  }

  /** Tweens the actor's color.
   * @param clr color name
   * @param d duration */
  public static void tweenActorColor(Actor target, String clr, float d) {
    $tweenActorColor(target, clr, d).start(tweenMgr);
  }

  /** Tweens the label's color.
   * @param clr color name
   * @param d duration */
  public static void tweenLabelColor(Label target, String clr, float d) {
    $tweenLabelColor(target, clr, d).start(tweenMgr);
  }

  /** @param a alpha
   * @param d duration
   * @return handle to tween the actor's opacity. */
  public static Tween $fadeActor(Actor target, float a, float d) {
    killTweenTarget(target, ActorAccessor.A);
    return Tween.to(target, ActorAccessor.A, d).target(a).ease(Quart.OUT);
  }

  /** @param a alpha
   * @return handle to tween the actor's opacity. */
  public static Tween $fadeActor(Actor target, float a) {
    return $fadeActor(target, a, C_D);
  }

  /** @param d duration
   * @return handle to tween the actor's opacity to 1. */
  public static Tween $fadeActorIn(Actor target, float d) {
    return $fadeActor(target, 1, d);
  }

  /** @return handle to tween the actor's opacity to 1. */
  public static Tween $fadeActorIn(Actor target) {
    return $fadeActorIn(target, C_D);
  }

  /** @return handle to tween the actor's opacity to 0.
   * @param d duration */
  public static Tween $fadeActorOut(Actor target, float d) {
    return $fadeActor(target, 0, d);
  }

  /** @return handle to tween the actor's opacity to 1. */
  public static Tween $fadeActorOut(Actor target) {
    return $fadeActorOut(target, C_D);
  }

  /** @param a alpha
   * @param d duration
   * @return handle to tween the label's opacity. */
  public static Tween $fadeLabel(Label target, float a, float d) {
    killTweenTarget(target, LabelAccessor.A);
    return Tween.to(target, LabelAccessor.A, d).target(a).ease(Quart.OUT);
  }

  /** @return handle to tween the label's opacity.
   * @param a alpha */
  public static Tween $fadeLabel(Label target, float a) {
    return $fadeLabel(target, a, C_D);
  }

  /** @return handle to tween the label's opacity to 1.
   * @param d duration */
  public static Tween $fadeLabelIn(Label target, float d) {
    return $fadeLabel(target, 1, d);
  }

  /** @return handle to tween the label's opacity to 1. */
  public static Tween $fadeLabelIn(Label target) {
    return $fadeLabelIn(target, C_D);
  }

  /** @param d duration
   * @return handle to tween the label's opacity to 0. */
  public static Tween $fadeLabelOut(Label target, float d) {
    return $fadeLabel(target, 0, d);
  }

  /** @return handle to tween the label's opacity to 0. */
  public static Tween $fadeLabelOut(Label target) {
    return $fadeLabelOut(target, C_D);
  }

  /** @param clr color
   * @param d duration
   * @return handle to tween the actor's color. */
  public static Tween $tweenActorColor(Actor target, Color clr, float d) {
    killTweenTarget(target, ActorAccessor.RGB);
    return Tween.to(target, ActorAccessor.RGB, d).target(clr.r, clr.g, clr.b).ease(Quart.OUT);
  }

  /** @param clr color
   * @return handle to tween the actor's color. */
  public static Tween $tweenActorColor(Actor target, Color clr) {
    return $tweenActorColor(target, clr, C_D);
  }

  /** @param clr color
   * @param d duration
   * @return handle to tween the label's color. */
  public static Tween $tweenLabelColor(Label target, Color clr, float d) {
    killTweenTarget(target, LabelAccessor.RGB);
    return Tween.to(target, LabelAccessor.RGB, d).target(clr.r, clr.g, clr.b).ease(Quart.OUT);
  }

  /** @param clr color
   * @return handle to tween the label's color. */
  public static Tween $tweenLabelColor(Label target, Color clr) {
    return $tweenLabelColor(target, clr, C_D);
  }

  /** @param clr color name
   * @param d duration
   * @return handle to tween the actor's color. */
  public static Tween $tweenActorColor(Actor target, String clr, float d) {
    return $tweenActorColor(target, color(clr), d);
  }

  /** @param clr color name
   * @return handle to tween the actor's color. */
  public static Tween $tweenActorColor(Actor target, String clr) {
    return $tweenActorColor(target, clr, C_D);
  }

  /** @param clr color name
   * @param d duration
   * @return handle to tween the label's color. */
  public static Tween $tweenLabelColor(Label target, String clr, float d) {
    return $tweenLabelColor(target, color(clr), d);
  }

  /** @param clr color name
   * @return handle to tween the label's color. */
  public static Tween $tweenLabelColor(Label target, String clr) {
    return $tweenLabelColor(target, clr, C_D);
  }

  /** @return multiple {@link TweenCallback} stacked into one. */
  public static TweenCallback multiplexCallbacks(final TweenCallback... callbacks) {
    return (type, source) -> {
      if (type==BEGIN) {
        for (TweenCallback callback : callbacks) {
          callback.onEvent(type, source);
        }
      } else if (type==BEGIN) {
        for (TweenCallback callback : callbacks) {
          callback.onEvent(type, source);
        }
      }
    };
  }

  /** @return {@link TweenCallback} that shows actor(s) in the beginning of an animation and hides it in the end. */
  public static TweenCallback visibilityCallback(final Actor... targets) {
    return (type, source) -> {
      if (type==BEGIN) {
        showActors(targets);
      } else if (type==COMPLETE) {
        hideActors(targets);
      }
    };
  }

  /** @return {@link TweenCallback} that removes the target from all trackers. */
  public static TweenCallback killTargetCallback(final Object target) {
    return (type, source) -> {
      if (type==BEGIN || type==COMPLETE) {
        killTweenTarget(target);
      }
    };
  }

  public static TweenCallback killTargetsCallback(final Object... targets) {
    return (type, source) -> {
      if (type==BEGIN || type==COMPLETE) {
        for (Object target : targets) {
          killTweenTarget(target);
        }
      }
    };
  }

  /**
   * {@link #POS_C}: centered position, 
   * {@link #X_C}: centered x, 
   * {@link #X_Y}: centered y, 
   * {@link #ORG_X}: origin x, 
   * {@link #ORG_Y}: origin y, 
   * {@link #SCL_X}: scale x, 
   * {@link #SCL_Y}: scale y, 
   * {@link #ROT}: rotation, 
   * {@link #R}: red, 
   * {@link #G}: green, 
   * {@link #B}: blue, 
   * {@link #A}: alpha, 
   * {@link #H}: hue, 
   * {@link #S}: saturation, 
   * {@link #L}: lightness.
   */
  public static class ActorAccessor implements TweenAccessor<Actor> {
    public static final int	POS = 0, X = 1, Y = 2, POS_C = 3, X_C = 4, Y_C = 5,
    				ORIGIN = 6, ORG_X = 7, ORG_Y = 8,
    				SCALE = 9, SCL_X = 10, SCL_Y = 11, ROT = 12,
    				RGB = 13, R = 14, G = 15, B = 16, A = 17, H = 18, S = 19, L = 20;

    @Override public int getValues(Actor target, int tweenType, float[] returnValues) {
      switch (tweenType) {
      case POS:
        returnValues[0] = target.getX();
        returnValues[1] = target.getY();
        return 2;
      case X:
        returnValues[0] = target.getX();
        return 1;
      case Y:
        returnValues[0] = target.getY();
        return 1;
      case POS_C:
        returnValues[0] = target.getX(center);
        returnValues[1] = target.getY(center);
        return 2;
      case X_C:
        returnValues[0] = target.getX(center);
        return 1;
      case Y_C:
        returnValues[0] = target.getY(center);
        return 1;
      case ORIGIN:
        returnValues[0] = target.getOriginX();
        returnValues[1] = target.getOriginY();
        return 2;
      case ORG_X:
        returnValues[0] = target.getOriginX();
        return 1;
      case ORG_Y:
        returnValues[0] = target.getOriginY();
        return 1;
      case SCALE:
        returnValues[0] = target.getScaleX();
        returnValues[1] = target.getScaleY();
        return 2;
      case SCL_X:
        returnValues[0] = target.getScaleX();
        return 1;
      case SCL_Y:
        returnValues[0] = target.getScaleY();
        return 1;
      case ROT:
        returnValues[0] = target.getRotation();
        return 1;
      case RGB:
        returnValues[0] = target.getColor().r;
        returnValues[1] = target.getColor().g;
        returnValues[2] = target.getColor().b;
        return 3;
      case R:
        returnValues[0] = target.getColor().r;
        return 1;
      case G:
        returnValues[0] = target.getColor().g;
        return 1;
      case B:
        returnValues[0] = target.getColor().b;
        return 1;
      case A:
        returnValues[0] = target.getColor().a;
        return 1;
      case H:
        returnValues[0] = getHue(target.getColor());
        return 1;
      case S:
        returnValues[0] = getSat(target.getColor());
        return 1;
      case L:
        returnValues[0] = getLgt(target.getColor());
        return 1;
      default: illegalArgument("tweenType = "+tweenType); return -1;
      }
    }

    @Override public void setValues(Actor target, int tweenType, float[] newValues) {
      switch (tweenType) {
      case POS:
        target.setPosition(newValues[0], newValues[1]);
        break;
      case X:
        target.setX(newValues[0]);
        break;
      case Y:
        target.setY(newValues[0]);
        break;
      case POS_C:
        target.setPosition(newValues[0] - target.getWidth()/2, newValues[1] - target.getHeight()/2);
        break;
      case X_C:
        target.setX(newValues[0] - target.getWidth()/2);
        break;
      case Y_C:
        target.setY(newValues[0] - target.getHeight()/2);
        break;
      case ORIGIN:
        target.setOrigin(newValues[0], newValues[1]);
        break;
      case ORG_X:
        target.setOriginX(newValues[0]);
        break;
      case ORG_Y:
        target.setOriginY(newValues[0]);
        break;
      case SCALE:
        target.setScale(newValues[0], newValues[1]);
        break;
      case SCL_X:
        target.setScale(newValues[0], target.getScaleY());
        break;
      case SCL_Y:
        target.setScale(target.getScaleX(), newValues[0]);
        break;
      case ROT:
        target.setRotation(newValues[0]);
        break;
      case RGB:
        target.setColor(newValues[0], newValues[1], newValues[2], target.getColor().a);
        break;
      case R:
        target.getColor().r = newValues[0];
        break;
      case G:
        target.getColor().g = newValues[0];
        break;
      case B:
        target.getColor().b = newValues[0];
        break;
      case A:
        target.getColor().a = newValues[0];
        break;
      case H:
        target.setColor(setHue(target.getColor(), newValues[0]));
        break;
      case S:
        target.setColor(setSat(target.getColor(), newValues[0]));
        break;
      case L:
        target.setColor(setLgt(target.getColor(), newValues[0]));
        break;
      default: illegalArgument("tweenType = "+tweenType);
      }
    }
  }

  /**
   * {@link #POS_C}: centered position, 
   * {@link #X_C}: centered x, 
   * {@link #X_Y}: centered y, 
   * {@link #ORG_X}: origin x, 
   * {@link #ORG_Y}: origin y, 
   * {@link #SCL_X}: scale x, 
   * {@link #SCL_Y}: scale y, 
   * {@link #ROT}: rotation, 
   * {@link #R}: red, 
   * {@link #G}: green, 
   * {@link #B}: blue, 
   * {@link #A}: alpha, 
   * {@link #H}: hue, 
   * {@link #S}: saturation, 
   * {@link #L}: lightness.
   */
  public static class LabelAccessor implements TweenAccessor<Label> {
    public static final int	POS = 0, X = 1, Y = 2, POS_C = 3, X_C = 4, Y_C = 5,
    				ORIGIN = 6, ORG_X = 7, ORG_Y = 8,
    				SCALE = 9, SCL_X = 10, SCL_Y = 11, ROT = 12,
    				RGB = 13, R = 14, G = 15, B = 16, A = 17, H = 18, S = 19, L = 20;

    @Override public int getValues(Label target, int tweenType, float[] returnValues) {
      switch (tweenType) {
      case POS:
        returnValues[0] = target.getX();
        returnValues[1] = target.getY();
        return 2;
      case X:
        returnValues[0] = target.getX();
        return 1;
      case Y:
        returnValues[0] = target.getY();
        return 1;
      case POS_C:
        returnValues[0] = target.getX(center);
        returnValues[1] = target.getY(center);
        return 2;
      case X_C:
        returnValues[0] = target.getX(center);
        return 1;
      case Y_C:
        returnValues[0] = target.getY(center);
        return 1;
      case ORIGIN:
        returnValues[0] = target.getOriginX();
        returnValues[1] = target.getOriginY();
        return 2;
      case ORG_X:
        returnValues[0] = target.getOriginX();
        return 1;
      case ORG_Y:
        returnValues[0] = target.getOriginY();
        return 1;
      case SCALE:
        returnValues[0] = target.getFontScaleX();
        returnValues[1] = target.getFontScaleY();
        return 2;
      case SCL_X:
        returnValues[0] = target.getFontScaleX();
        return 1;
      case SCL_Y:
        returnValues[0] = target.getFontScaleY();
        return 1;
      case ROT:
        returnValues[0] = target.getRotation();
        return 1;
      case RGB:
        returnValues[0] = target.getColor().r;
        returnValues[1] = target.getColor().g;
        returnValues[2] = target.getColor().b;
        return 3;
      case R:
        returnValues[0] = target.getColor().r;
        return 1;
      case G:
        returnValues[0] = target.getColor().g;
        return 1;
      case B:
        returnValues[0] = target.getColor().b;
        return 1;
      case A:
        returnValues[0] = target.getColor().a;
        return 1;
      case H:
        returnValues[0] = getHue(target.getColor());
        return 1;
      case S:
        returnValues[0] = getSat(target.getColor());
        return 1;
      case L:
        returnValues[0] = getLgt(target.getColor());
        return 1;
      default: illegalArgument("tweenType = "+tweenType); return -1;
      }
    }

    @Override public void setValues(Label target, int tweenType, float[] newValues) {
      switch (tweenType) {
      case POS:
        target.setPosition(newValues[0], newValues[1]);
        break;
      case X:
        target.setX(newValues[0]);
        break;
      case Y:
        target.setY(newValues[0]);
        break;
      case POS_C:
        target.setPosition(newValues[0] - target.getWidth()/2, newValues[1] - target.getHeight()/2);
        break;
      case X_C:
        target.setX(newValues[0] - target.getWidth()/2);
        break;
      case Y_C:
        target.setY(newValues[0] - target.getHeight()/2);
        break;
      case ORIGIN:
        target.setOrigin(newValues[0], newValues[1]);
        break;
      case ORG_X:
        target.setOriginX(newValues[0]);
        break;
      case ORG_Y:
        target.setOriginY(newValues[0]);
        break;
      case SCALE:
        target.setFontScale(newValues[0], newValues[1]);
        break;
      case SCL_X:
        target.setFontScale(newValues[0], target.getFontScaleY());
        break;
      case SCL_Y:
        target.setFontScale(target.getFontScaleX(), newValues[0]);
        break;
      case ROT:
        target.setRotation(newValues[0]);
        break;
      case RGB:
        target.setColor(newValues[0], newValues[1], newValues[2], target.getColor().a);
        break;
      case R:
        target.getColor().r = newValues[0];
        break;
      case G:
        target.getColor().g = newValues[0];
        break;
      case B:
        target.getColor().b = newValues[0];
        break;
      case A:
        target.getColor().a = newValues[0];
        break;
      case H:
        target.setColor(setHue(target.getColor(), newValues[0]));
        break;
      case S:
        target.setColor(setSat(target.getColor(), newValues[0]));
        break;
      case L:
        target.setColor(setLgt(target.getColor(), newValues[0]));
        break;
      default: illegalArgument("tweenType = "+tweenType);
      }
    }
  }

  /**
   * {@link #POS_C}: centered position, 
   * {@link #X_C}: centered x, 
   * {@link #X_Y}: centered y, 
   * {@link #SCL_X}: scale x, 
   * {@link #SCL_Y}: scale y, 
   * {@link #ROT}: rotation, 
   * {@link #R}: red, 
   * {@link #G}: green, 
   * {@link #B}: blue, 
   * {@link #A}: alpha, 
   * {@link #H}: hue, 
   * {@link #S}: saturation, 
   * {@link #L}: lightness.
   */
  public static class SpriteAccessor implements TweenAccessor<Sprite> {
    public static final int	POS = 0, X = 1, Y = 2, POS_C = 3, X_C = 4, Y_C = 5,
    				SCALE = 6, SCL_X = 7, SCL_Y = 8, ROT = 9,
    				RGB = 10, R = 11, G = 12, B = 13, A = 14, H = 15, S = 16, L = 17;

    @Override public int getValues(Sprite target, int tweenType, float[] returnValues) {
      switch (tweenType) {
      case POS:
        returnValues[0] = target.getX();
        returnValues[1] = target.getY();
        return 2;
      case X:
        returnValues[0] = target.getX(); return 1;
      case Y:
        returnValues[0] = target.getY(); return 1;
      case POS_C:
        returnValues[0] = target.getX() + target.getWidth()/2;
        returnValues[1] = target.getY() + target.getHeight()/2;
        return 2;
      case X_C:
        returnValues[0] = target.getX() + target.getWidth()/2;
        return 1;
      case Y_C:
        returnValues[0] = target.getY() + target.getHeight()/2;
        return 1;
      case SCALE:
        returnValues[0] = target.getScaleX();
        returnValues[1] = target.getScaleY();
        return 2;
      case SCL_X:
        returnValues[0] = target.getScaleX();
        return 1;
      case SCL_Y:
        returnValues[0] = target.getScaleY();
        return 1;
      case ROT:
        returnValues[0] = target.getRotation();
        return 1;
      case RGB:
        returnValues[0] = target.getColor().r;
        returnValues[1] = target.getColor().g;
        returnValues[2] = target.getColor().b;
        return 3;
      case R:
        returnValues[0] = target.getColor().r;
        return 1;
      case G:
        returnValues[0] = target.getColor().g;
        return 1;
      case B:
        returnValues[0] = target.getColor().b;
        return 1;
      case A:
        returnValues[0] = target.getColor().a;
        return 1;
      case H:
        returnValues[0] = getHue(target.getColor());
        return 1;
      case S:
        returnValues[0] = getSat(target.getColor());
        return 1;
      case L:
        returnValues[0] = getLgt(target.getColor());
        return 1;
      default: illegalArgument("tweenType = "+tweenType); return -1;
      }
    }

    @Override public void setValues(Sprite target, int tweenType, float[] newValues) {
      switch (tweenType) {
      case POS:
        target.setPosition(newValues[0], newValues[1]);
        break;
      case X:
        target.setX(newValues[0]);
        break;
      case Y:
        target.setY(newValues[0]);
        break;
      case POS_C:
        target.setPosition(newValues[0] - target.getWidth()/2, newValues[1] - target.getHeight()/2);
        break;
      case X_C:
        target.setX(newValues[0] - target.getWidth()/2);
        break;
      case Y_C:
        target.setY(newValues[0] - target.getHeight()/2);
        break;
      case SCALE:
        target.setScale(newValues[0], newValues[1]);
        break;
      case SCL_X:
        target.setScale(newValues[0], target.getScaleY());
        break;
      case SCL_Y:
        target.setScale(target.getScaleX(), newValues[0]);
        break;
      case ROT:
        target.setRotation(newValues[0]);
        break;
      case RGB:
        target.setColor(newValues[0], newValues[1], newValues[2], target.getColor().a);
        break;
      case R:
        target.setColor(newValues[0], target.getColor().g, target.getColor().b, target.getColor().a);
        break;
      case G:
        target.setColor(target.getColor().r, newValues[0], target.getColor().b, target.getColor().a);
        break;
      case B:
        target.setColor(target.getColor().r, target.getColor().g, newValues[0], target.getColor().a);
        break;
      case A:
        target.setAlpha(newValues[0]);
        break;
      case H:
        target.setColor(setHue(target.getColor(), newValues[0]));
        break;
      case S:
        target.setColor(setSat(target.getColor(), newValues[0]));
        break;
      case L:
        target.setColor(setLgt(target.getColor(), newValues[0]));
        break;
      default: illegalArgument("tweenType = "+tweenType);
      }
    }
  }

  /**
   * {@link #VOL}: volume.
   */
  public static class MusicAccessor implements TweenAccessor<Music> {
    public static final int	VOL = 0;

    @Override public int getValues(Music target, int tweenType, float[] returnValues) {
      switch (tweenType) {
      case VOL:
        returnValues[0] = target.getVolume();
        return 1;
      default: illegalArgument("tweenType = "+tweenType); return -1;
      }
    }

    @Override public void setValues(Music target, int tweenType, float[] newValues) {
      switch (tweenType) {
      case VOL:
        target.setVolume(newValues[0]);
        break;
      default: illegalArgument("tweenType = "+tweenType);
      }
    }
  }

  public static class OrthoCameraAccessor implements TweenAccessor<OrthographicCamera> {
    public static final int	POS = 0, X = 1, Y = 2, ZOOM = 3;

    @Override public int getValues(OrthographicCamera target, int tweenType, float[] returnValues) {
      switch (tweenType) {
      case POS:
        returnValues[0] = target.position.x;
        returnValues[1] = target.position.y;
        return 2;
      case X:
        returnValues[0] = target.position.x;
        return 1;
      case Y:
        returnValues[0] = target.position.y;
        return 1;
      case ZOOM:
        returnValues[0] = target.zoom;
        return 1;
      default: illegalArgument("tweenType = "+tweenType); return -1;
      }
    }

    @Override public void setValues(OrthographicCamera target, int tweenType, float[] newValues) {
      switch (tweenType) {
      case POS:
        target.position.set(newValues[0], newValues[1], 0);
        break;
      case X:
        target.position.x = newValues[0];
        break;
      case Y:
        target.position.y = newValues[0];
        break;
      case ZOOM:
        target.zoom = newValues[0];
        break;
      default: illegalArgument("tweenType = "+tweenType);
      }
    }
  }

  /** {@link #VEL}: velocity, 
   * {@link #VX}: velocity x, 
   * {@link #VY}: velocity y, 
   * {@link #ANG}: angle. */
  public static class Box2DBodyAccessor implements TweenAccessor<Body> {
    public static final int	VEL = 0, VX = 1, VY = 2, ANG = 3;

    @Override public int getValues(Body target, int tweenType, float[] returnValues) {
      switch (tweenType) {
      case VEL:
        returnValues[0] = target.getLinearVelocity().x;
        returnValues[1] = target.getLinearVelocity().y;
        return 2;
      case VX:
        returnValues[0] = target.getLinearVelocity().x;
        return 1;
      case VY:
        returnValues[0] = target.getLinearVelocity().y;
        return 1;
      case ANG:
        returnValues[0] = target.getAngularVelocity();
        return 1;
      default: illegalArgument("tweenType = "+tweenType); return -1;
      }
    }

    @Override public void setValues(Body target, int tweenType, float[] newValues) {
      switch (tweenType) {
      case VEL:
        target.setLinearVelocity(newValues[0], newValues[1]);
        break;
      case VX:
        target.setLinearVelocity(newValues[0], target.getLinearVelocity().y);
        break;
      case VY:
        target.setLinearVelocity(target.getLinearVelocity().x, newValues[0]);
        break;
      case ANG:
        target.setAngularVelocity(newValues[0]);
        break;
      default: illegalArgument("tweenType = "+tweenType);
      }
    }
  }

  /** {@link #POS_C}: centered position, 
   * {@link #X_C}: centered x, 
   * {@link #X_Y}: centered y, 
   * {@link #PVT_X}: pivot x, 
   * {@link #PVT_Y}: pivot y, 
   * {@link #SCL_X}: scale x, 
   * {@link #SCL_Y}: scale y, 
   * {@link #ROT}: rotation. */
  public static class AndViewAccessor implements TweenAccessor<View> {
    public static final int	POS = 0, X = 1, Y = 2, POS_C = 3, X_C = 4, Y_C = 5,
    				PIVOT = 6, PVT_X = 7, PVT_Y = 8,
    				SCALE = 9, SCL_X = 10, SCL_Y = 11, ROT = 12, ALPHA = 13;

    @Override public int getValues(View target, int tweenType, float[] returnValues) {
      switch (tweenType) {
      case POS:
        returnValues[0] = target.getX();
        returnValues[1] = target.getY();
        return 2;
      case X:
        returnValues[0] = target.getX();
        return 1;
      case Y:
        returnValues[0] = target.getY();
        return 1;
      case POS_C:
        returnValues[0] = target.getX() + target.getWidth()/2;
        returnValues[1] = target.getY() + target.getHeight()/2;
        return 2;
      case X_C:
        returnValues[0] = target.getX() + target.getWidth()/2;
        return 1;
      case Y_C:
        returnValues[0] = target.getY() + target.getHeight()/2;
        return 1;
      case PIVOT:
        returnValues[0] = target.getPivotX();
        returnValues[1] = target.getPivotY();
        return 2;
      case PVT_X:
        returnValues[0] = target.getPivotX();
        return 1;
      case PVT_Y:
        returnValues[0] = target.getPivotY();
        return 1;
      case SCALE:
        returnValues[0] = target.getScaleX();
        returnValues[1] = target.getScaleY();
        return 2;
      case SCL_X:
        returnValues[0] = target.getScaleX();
        return 1;
      case SCL_Y:
        returnValues[0] = target.getScaleY();
        return 1;
      case ROT:
        returnValues[0] = target.getRotation();
        return 1;
      case ALPHA:
        returnValues[0] = target.getAlpha();
        return 1;
      default: illegalArgument("tweenType = "+tweenType); return -1;
      }
    }

    @Override public void setValues(View target, int tweenType, float[] newValues) {
      switch (tweenType) {
      case POS:
        target.setX(newValues[0]);
        target.setY(newValues[1]);
        break;
      case X:
        target.setX(newValues[0]);
        break;
      case Y:
        target.setY(newValues[0]);
        break;
      case POS_C:
        target.setX(newValues[0] - target.getWidth()/2);
        target.setY(newValues[1] - target.getHeight()/2);
        break;
      case X_C:
        target.setX(newValues[0] - target.getWidth()/2);
        break;
      case Y_C:
        target.setY(newValues[0] - target.getHeight()/2);
        break;
      case PIVOT:
        target.setPivotX(newValues[0]);
        target.setPivotY(newValues[1]);
        break;
      case PVT_X:
        target.setPivotX(newValues[0]);
        break;
      case PVT_Y:
        target.setPivotY(newValues[0]);
        break;
      case SCALE:
        target.setScaleX(newValues[0]);
        target.setScaleY(newValues[1]);
        break;
      case SCL_X:
        target.setScaleX(newValues[0]);
        break;
      case SCL_Y:
        target.setScaleY(newValues[0]);
        break;
      case ROT:
        target.setRotation(newValues[0]);
        break;
      case ALPHA:
        target.setAlpha(newValues[0]);
        break;
      default: illegalArgument("tweenType = "+tweenType);
      }
    }
  }

  public static class Vector2Accessor implements TweenAccessor<Vector2> {
    public static final int	XY = 0, X = 1, Y = 2;

    @Override public int getValues(Vector2 target, int tweenType, float[] returnValues) {
      switch (tweenType) {
      case XY:
        returnValues[0] = target.x;
        returnValues[1] = target.y;
        return 2;
      case X:
        returnValues[0] = target.x;
        return 1;
      case Y:
        returnValues[0] = target.y;
        return 1;
      default: illegalArgument("tweenType = "+tweenType); return -1;
      }
    }

    @Override public void setValues(Vector2 target, int tweenType, float[] newValues) {
      switch (tweenType) {
      case XY:
        target.x = newValues[0];
        target.y = newValues[1];
        break;
      case X:
        target.x = newValues[0];
        break;
      case Y:
        target.y = newValues[0];
        break;
      default: illegalArgument("tweenType = "+tweenType);
      }
    }
  }

  public static class Vector3Accessor implements TweenAccessor<Vector3> {
    public static final int	XYZ = 0, XY = 1, X = 2, Y = 3, Z = 4;

    @Override public int getValues(Vector3 target, int tweenType, float[] returnValues) {
      switch (tweenType) {
      case XYZ:
        returnValues[0] = target.x;
        returnValues[1] = target.y;
        returnValues[2] = target.z;
        return 3;
      case XY:
        returnValues[0] = target.x;
        returnValues[1] = target.y;
        return 2;
      case X:
        returnValues[0] = target.x;
        return 1;
      case Y:
        returnValues[0] = target.y;
        return 1;
      case Z:
        returnValues[0] = target.z;
        return 1;
      default: illegalArgument("tweenType = "+tweenType); return -1;
      }
    }

    @Override public void setValues(Vector3 target, int tweenType, float[] newValues) {
      switch (tweenType) {
      case XYZ:
        target.x = newValues[0];
        target.y = newValues[1];
        target.z = newValues[2];
        break;
      case XY:
        target.x = newValues[0];
        target.y = newValues[1];
        break;
      case X:
        target.x = newValues[0];
        break;
      case Y:
        target.y = newValues[0];
        break;
      case Z:
        target.z = newValues[0];
        break;
      default: illegalArgument("tweenType = "+tweenType);
      }
    }
  }

  /** {@link #POS}: position, 
   * {@link #POS_C}: centered position, 
   * {@link #X_C}: centered x, 
   * {@link #X_Y}: centered y, 
   * {@link #ORG_X}: origin x, 
   * {@link #ORG_Y}: origin y, 
   * {@link #SCL_X}: scale x, 
   * {@link #SCL_Y}: scale y, 
   * {@link #ROT}: rotation, 
   * {@link #R}: red, 
   * {@link #G}: green, 
   * {@link #B}: blue, 
   * {@link #A}: alpha, 
   * {@link #H}: hue, 
   * {@link #S}: saturation, 
   * {@link #L}: lightness. */
  public static class ColorAccessor implements TweenAccessor<Color> {
    public static final int	RGB = 0, R = 1, G = 2, B = 3, A = 4, H = 5, S = 6, L = 7;

    @Override public int getValues(Color target, int tweenType, float[] returnValues) {
      switch (tweenType) {
      case RGB:
        returnValues[0] = target.r;
        returnValues[1] = target.g;
        returnValues[2] = target.b;
        return 3;
      case R:
        returnValues[0] = target.r;
        return 1;
      case G:
        returnValues[0] = target.g;
        return 1;
      case B:
        returnValues[0] = target.b;
        return 1;
      case A:
        returnValues[0] = target.a;
        return 1;
      case H:
        returnValues[0] = getHue(target);
        return 1;
      case S:
        returnValues[0] = getSat(target);
        return 1;
      case L:
        returnValues[0] = getLgt(target);
        return 1;
      default: illegalArgument("tweenType = "+tweenType); return -1;
      }
    }

    @Override public void setValues(Color target, int tweenType, float[] newValues) {
      switch (tweenType) {
      case RGB:
        target.r = newValues[0];
        target.g = newValues[1];
        target.b = newValues[2];
        break;
      case R:
        target.r = newValues[0];
        break;
      case G:
        target.g = newValues[0];
        break;
      case B:
        target.b = newValues[0];
        break;
      case A:
        target.a = newValues[0];
        break;
      case H:
        setHue(target, newValues[0]);
        break;
      case S:
        setSat(target, newValues[0]);
        break;
      case L:
        setLgt(target, newValues[0]);
        break;
      default: illegalArgument("tweenType = "+tweenType);
      }
    }
  }
}

package adf.gdx;

import static adf.gdx.BaseContext.*;
import static adf.gdx.TweenUtil.Box2DBodyAccessor;
import static adf.gdx.MathUtil.*;
import static adf.gdx.Util.*;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public final class Box2DController {
  private Box2DController() {}

  public static void moveBody(Body body, float x, float y, float d) {
    $moveBody(body, x, y, d).start(tscTweenMgr);
  }

  public static void moveBody(Body body, float x, float y) {
    $moveBody(body, x, y).start(tscTweenMgr);
  }

  public static void moveBody(Body body, Vector2 pos, float d) {
    $moveBody(body, pos, d).start(tscTweenMgr);
  }

  public static void moveBody(Body body, Vector2 pos) {
    $moveBody(body, pos).start(tscTweenMgr);
  }

  public static void rotateBody(Body body, float a, float d) {
  $rotateBody(body, a, d).start(tscTweenMgr);
  }

  public static void rotateBody(Body body, float a) {
    $rotateBody(body, a).start(tscTweenMgr);
  }

  public static Timeline $moveBody(Body body, float x, float y, float d) {
    float bx = body.getPosition().x, by = body.getPosition().y, dx = x - bx, dy = y - by;
    float ang = atan2f(dy, dx);
    float dt = hypotf(dx, dy), vx = cosf(ang) * dt, vy = sinf(ang) * dt;
    return Timeline.createSequence()
             .push(Tween.set(body, Box2DBodyAccessor.VEL).target(vx, vy))
             .pushPause(d)
             .push(Tween.set(body, Box2DBodyAccessor.VEL).target(0, 0));
  }

  public static Timeline $moveBody(Body body, float x, float y) {
    return $moveBody(body, x, y, C_MD);
  }

  public static Timeline $moveBody(Body body, Vector2 pos, float d) {
    return $moveBody(body, pos.x, pos.y, d);
  }

  public static Timeline $moveBody(Body body, Vector2 pos) {
    return $moveBody(body, pos.x, pos.y, C_MD);
  }

  public static Timeline $rotateBody(Body body, float a, float d) {
    return Timeline.createSequence()
             .push(Tween.set(body, Box2DBodyAccessor.ANG).target((body.getAngle() - a) / d))
             .pushPause(d)
             .push(Tween.set(body, Box2DBodyAccessor.VEL).target(0));
  }

  public static Timeline $rotateBody(Body body, float a) {
    return $rotateBody(body, a, C_D);
  }
}

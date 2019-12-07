package adflixit.gdx;

import static adflixit.gdx.BaseContext.*;
import static adflixit.gdx.TweenUtils.*;
import static adflixit.gdx.Util.*;
import static java.lang.Math.*;

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
    double ang = atan2(dy, dx);
    float dt = hypotf(dx, dy), vx = mcos(ang, dt), vy = msin(ang, dt);
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

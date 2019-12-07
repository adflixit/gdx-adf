package adflixit.gdx.collision;

import com.badlogic.gdx.math.Vector2;

public interface RayCastCallback {
  /**
   * When ray casting is performed, a callback is being called every time the ray hits a {@link Collideable} body.
   * @param body {@link Collideable} that is being hit
   * @param point intersection point
   * @param normal intersection normal
   * @param fraction ray fraction
   * @return whether the ray has to proceed
   */
  public boolean call(Collideable body, Vector2 point, Vector2 normal, float fraction);
}

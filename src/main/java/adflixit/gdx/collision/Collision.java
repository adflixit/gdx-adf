package adflixit.gdx.collision;

import com.badlogic.gdx.math.Vector2;

/**
 * Collision info which references the participants, the point and the normal of a collision.
 */
public class Collision {
  public Collideable a, b;
  public final Vector2 point = new Vector2();
  public final Vector2 normal = new Vector2();

  public Vector2 point()  {return point;}
  public Vector2 normal() {return normal;}
}

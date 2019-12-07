package adflixit.gdx.collision;

import com.badlogic.gdx.math.Vector2;

public class Circle extends Collideable {
  public final Vector2  pos   = new Vector2();
  public final Vector2  ppos  = new Vector2();  // position in the previous frame
  public final Vector2  vel   = new Vector2();  // velocity
  private float         rad;      // radius
  private float         mass;     // 0 if not movable
  public boolean        isSensor; // collision does not affect the position

  public Circle() {
  }

  public Circle setup(float r, int mask, int filter) {
    setRad(r);
    super.setup(mask, filter);
    return this;
  }

  @Override public Circle setup(int mask, int filter) {
    super.setup(mask, filter);
    return this;
  }

  public Circle setPos(float x, float y) {
    pos.set(x, y);
    return this;
  }

  public Circle setPos(Vector2 p) {
    pos.set(p);
    return this;
  }

  public Circle setVel(float x, float y) {
    vel.set(x, y);
    return this;
  }

  public Circle setVel(Vector2 p) {
    vel.set(p);
    return this;
  }

  public Circle setRad(float r) {
    rad = r;
    return this;
  }

  public Circle mulRad(float a) {
    rad *= a;
    return this;
  }

  public float x()     {return pos.x;}
  public float y()     {return pos.y;}
  public float px()    {return ppos.x;}
  public float py()    {return ppos.y;}
  public float vx()    {return vel.x;}
  public float vy()    {return vel.y;}
  public float rad()   {return rad;}
  public float mass()  {return mass;}
}

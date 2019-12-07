package adflixit.gdx.collision;

import com.badlogic.gdx.math.Vector2;

public class Box extends Collideable {
  public final Vector2  pos   = new Vector2();
  public final Vector2  ppos  = new Vector2();  // position in the previous frame
  public final Vector2  vel   = new Vector2();  // velocity
  private float         width, height;
  private float         mass;     // 0 if not movable
  public boolean        isSensor; // collision does not affect the position

  public Box() {
  }

  public Box setup(float width, float height, int mask, int filter) {
    setSize(width, height);
    super.setup(mask, filter);
    return this;
  }

  @Override public Box setup(int mask, int filter) {
    super.setup(mask, filter);
    return this;
  }

  public Box setPos(float x, float y) {
    pos.set(x, y);
    return this;
  }

  public Box setPos(Vector2 p) {
    pos.set(p);
    return this;
  }

  public Box setVel(float x, float y) {
    vel.set(x, y);
    return this;
  }

  public Box setVel(Vector2 p) {
    vel.set(p);
    return this;
  }

  public Box setSize(float w, float h) {
    width = w;
    height = h;
    return this;
  }

  public Box setSize(float size) {
    setSize(size, size);
    return this;
  }

  public Box setWidth(float a) {
    width = a;
    return this;
  }

  public Box setHeight(float a) {
    height = a;
    return this;
  }

  public Box mulSize(float a) {
    width *= a;
    height *= a;
    return this;
  }

  public Box mulWidth(float a) {
    width *= a;
    return this;
  }

  public Box mulHeight(float a) {
    height *= a;
    return this;
  }

  public float x()       {return pos.x + width/2;}
  public float y()       {return pos.y + height/2;}
  public float px()      {return ppos.x + width/2;}
  public float py()      {return ppos.y + height/2;}
  public float vx()      {return vel.x;}
  public float vy()      {return vel.y;}
  public float width()   {return width;}
  public float height()  {return height;}
  public float mass()    {return mass;}
  public float x1()      {return pos.x;}
  public float y1()      {return pos.y;}
  public float x2()      {return pos.x + width;}
  public float y2()      {return pos.y + height;}
}

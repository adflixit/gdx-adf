package adflixit.gdx.collision;

import static adflixit.gdx.Util.*;
import static java.lang.Math.*;

import com.badlogic.gdx.math.Vector2;

public class Line extends Collideable {
  public final Vector2 p1 = new Vector2(), p2 = new Vector2();

  public Line() {}

  public Line set(float x1, float y1, float x2, float y2) {
    p1.set(x1, y1);
    p2.set(x2, y2);
    return this;
  }

  public Line set(Vector2 p1, Vector2 p2) {
    return set(p1.x, p1.y, p2.x, p2.y);
  }

  public Line setP1(float x, float y) {
    p1.set(x, y);
    return this;
  }

  public Line setP1(Vector2 p) {
    return setP1(p.x, p.y);
  }

  public Line setP2(float x, float y) {
    p2.set(x, y);
    return this;
  }

  public Line setP2(Vector2 p) {
    return setP2(p.x, p.y);
  }

  public float x1()     {return min(p1.x, p2.x);}
  public float y1()     {return min(p1.y, p2.y);}
  public float x2()     {return max(p1.x, p2.x);}
  public float y2()     {return max(p1.y, p2.y);}
  public float length() {return dist(p1, p2);}
  public double angle() {return abs(atan2(p1.y-p2.y, p1.x-p2.x));}
}

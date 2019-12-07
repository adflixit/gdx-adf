package adflixit.gdx.collision;

import static adflixit.gdx.Util.*;
import static java.lang.Math.*;

import com.badlogic.gdx.math.Vector2;

public class CollisionUtils {
  private CollisionUtils() {}

  /**
   * @return whether point {@code p} belongs to box {@code b}.
   */
  public static boolean isPointInBox(Vector2 p, Box b) {
    return p.x>=b.x1() && p.x<=b.x2() && p.y>=b.y1() && p.y<=b.y2();
  }

  /**
   * @return whether point {@code p} belongs to line {@code l}.
   */
  public static boolean isPointOnLine(Vector2 p, Line l) {
    return dist(p.x,p.y, l.x1(),l.y1()) + dist(p.x,p.y, l.x2(),l.y2()) == dist(l.x1(),l.y1(), l.x2(),l.y2());
  }

  /**
   * @return whether point {@code p} belongs to circle {@code c}.
   */
  public static boolean isPointInCircle(Vector2 p, Circle c) {
    return hypotf(p.x-c.x(), p.y-c.y()) <= c.rad();
  }

  /**
   * @return whether box {@code a} intersects box {@code b}.
   */
  public static boolean testBoxIntersection(Box a, Box b) {
    return a.x1()<b.x2() && a.x2()>b.x1() && a.y1()<b.y2() && a.y2()>b.y1();
  }

  /**
   * @return whether box {@code b} intersects line {@code l}.
   */
  public static boolean testBoxLineIntersection(Box b, Line l) {
    if ((l.x1()<=b.x1() && l.x2()<=b.x1()) ||
        (l.y1()<=b.y1() && l.y2()<=b.y1()) ||
        (l.x1()>=b.x2() && l.x2()>=b.x2()) ||
        (l.y1()>=b.y2() && l.y2()>=b.y2())) {
      return false;
    }
    if ((l.x1()>b.x1() && l.x1()<b.x2() && l.y1()>b.y1() && l.y1()<b.y2()) ||
        (l.x2()>b.x1() && l.x2()<b.x2() && l.y2()>b.y1() && l.y2()<b.y2())) {
      return true;
    }
    return false;
  }

  /**
   * @return whether box {@code b} intersects circle {@code c}.
   */
  public static boolean testBoxCircleIntersection(Box b, Circle c) {
    float width = b.x2()-b.x1(), height = b.y2()-b.y1(), x = b.x1() + width/2, y = b.y1() + height/2,
        cdx = Math.abs(c.x()-x), cdy = Math.abs(c.y()-y);
    if (cdx > width/2 + c.rad() || cdy > height/2 + c.rad()) {
      return false;
    }
    if (cdx <= width/2 || cdy <= height/2) {
      return true;
    }
    return powf(cdx-width/2,2) + powf(cdy-height/2,2) <= (powf(c.rad(),2));
  }

  /**
   * @return whether line {@code a} intersects line {@code b}.
   */
  public static boolean testLineIntersection(Line a, Line b) {
    float bx = a.x2()-a.x1();
    float by = a.y2()-a.y1();
    float dx = b.x2()-b.x1();
    float dy = b.y2()-b.y1();
    float perp = bx*dy - by*dx;

    if (perp == 0) {
      return false;
    }

    float cx = b.x1()-a.x1();
    float cy = b.y1()-a.y1();
    float t = (cx*dy - cy*dx) / perp;

    if (t<0 || t>1) {
      return false;
    }

    float u = (cx*by - cy*bx) / perp;
    if (u<0 || u>1) {
      return false;
    }
    return true;
  }

  /**
   * @return whether line {@code l} intersects circle {@code c}.
   */
  public static boolean testLineCircleIntersection(Line l, Circle c) {
    float a = tmpv2.set(l.x2()-l.x1(), l.y2()-l.y1()).dot(tmpv2);
    float b = 2*tmpv2.dot(l.x1()-c.x(), l.y1()-c.y());
    float d = tmpv2.set(l.x1()-c.x(), l.y1()-c.y()).dot(tmpv2) - c.rad()*c.rad();
    float dsc = b*b - 4*a*d;

    if (dsc > 0) {
      dsc = sqrtf(dsc);
      float t1 = (-b - dsc) / (2*a);
      float t2 = (-b + dsc) / (2*a);
      return t1>=0 && t1<=1 || t2>=0 && t2<=t1;
    }
    return false;
  }

  /**
   * Sets {@code p} to the intersection point between line {@code a} and line {@code b}.
   * @return whether line {@code a} intersects line {@code b}.
   */
  public static boolean lineIntersectionPoint(Line a, Line b, Vector2 p) {
    float d = (a.x1()-a.x2()) * (b.y1()-b.y2()) - (a.y1()-a.y2()) * (b.x1()-b.x2());
    if (d == 0) {
      return false;
    }
    p.set(((b.x1()-b.x2()) * (a.x1()*a.y2() - a.y1()*a.x2()) - (a.x1()-a.x2()) * (b.x1()*b.y2() - b.y1()*b.x2())) / d,
        ((b.y1()-b.y2()) * (a.x1()*a.y2() - a.y1()*a.x2()) - (a.y1()-a.y2()) * (b.x1()*b.y2() - b.y1()*b.x2())) / d);
    return true;
  }

  /**
   * @return angle between line {@code a} line {@code b} in radians.
   */
  public static double angleBetweenLines(Line a, Line b) {
    return atan2(a.y1()-a.y2(), a.x1()-a.x2()) - atan2(b.y1()-b.y2(), b.x1()-b.x2());
  }

  /**
   * @return x on line {@code l} at the specified {@code y}.
   */
  public static float lineXatY(Line l, float y) {
    return l.x1() - ((l.x1()-l.x2()) * y);
  }

  /**
   * @return y on line {@code l} at the specified {@code x}.
   */
  public static float lineYatX(Line l, float x) {
    return l.y1() - ((l.y1()-l.y2()) * x);
  }

  /**
   * @return whether circle {@code a} intersects circle {@code b}.
   */
  public static boolean testCircleIntersection(Circle a, Circle b) {
    return hypotf(a.x()-b.x(), a.y()-b.y()) <= (a.rad()+b.rad());
  }

  /**
   * Sets {@code p} to the intersection point between box {@code a} and box {@code b}.
   * @return whether box {@code a} intersects box {@code b}.
   */
  public static boolean boxIntersectionNormal(Box a, Box b, Vector2 p) {
    if (testBoxIntersection(a, b)) {
      float x = 0, y = 0;
      // up
      if (a.y1() < b.y2() && a.y2() > b.y2()) {
        y = b.y2() - a.y1();
      }
      // right
      if (a.x1() < b.x2() && a.x2() > b.x2()) {
        x = b.x2() - a.x1();
      }
      // bottom
      if (a.y2() > b.y1() && a.y1() < b.y1()) {
        y = b.y1() - a.y2();
      }
      // left
      if (a.x2() > b.x1() && a.x1() < b.x1()) {
        x = b.x1() - a.x2();
      }
      p.set(x, y);
      return true;
    }
    return false;
  }

  /**
   * Sets {@code p} to the intersection point between box {@code b} and line {@code l}.
   * @return whether box {@code b} intersects line {@code l}.
   */
  public static boolean boxLineIntersectionNormal(Box b, Line l, Vector2 v2) {
    if (testBoxLineIntersection(b, l)) {
      float x = 0, y = 0;
      // is the line axis-aligned
      boolean px = false, py = false;
      if (py = l.y1() == l.y2() && b.y2() > l.y1() && b.y1() < l.y1()) {
        y = l.y1() > b.y() ? b.y2() - l.y1() : l.y1() - b.y2();
      } else if (px = l.x1() == l.x2() && b.x2() > l.x1() && b.x1() < l.x1()) {
        x = l.x1() > b.x() ? b.x2() - l.x1() : l.x1() - b.x2();
      }

      // if it's not
      if (!(py||px)) {
        float xp = (l.x2()-l.x1()) * (l.y2()-b.y()) - (l.y2()-l.y1()) * (l.x2()-b.x());
        // if the line angled against b corner of the box
        x = xp < 0 ? lineXatY(l, l.angle() < CIRC_4 ? b.y2() : b.y1())-b.x1() :
            b.x2() - lineXatY(l, l.angle() > CIRC_4 ? b.y2() : b.y1());
        xp = (l.y2()-l.y1()) * (l.x2()-b.x()) - (l.x2()-l.x1()) * (l.y2()-b.y());
        y = xp < 0 ? lineYatX(l, l.angle() < CIRC_4 ? b.x2() : b.x1())-b.y1() :
            b.y2() - lineYatX(l, l.angle() > CIRC_4 ? b.x2() : b.x1());
      }
      v2.set(x/2, y/2);
      return true;
    }
    return false;
  }
}

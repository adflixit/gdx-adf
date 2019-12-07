package adflixit.gdx;

import static adflixit.gdx.Util.*;

import adflixit.gdx.thirdparty.FixedList;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * Based on https://github.com/mattdesl/gdx-swiper.
 */
public class Trail {
  private final FixedList<Vector2>  points;
  private final Array<Vector2>      simplified;
  private final Vector2             lastPoint           = new Vector2();
  private final Vector2             tmpv2               = new Vector2();
  private final Array<Vector2>      tmpv2a              = new Array<>(Vector2.class);
  private TrailMesh                 mesh;
  private Texture                   tex;

  public int                        initialDistance     = 10;
  public int                        minDistance         = 20;
  public int                        simplifyIterations  = 2;
  public float                      simplifyTolerance   = 35;

  public Trail(int maxInputPoints, TrailMesh mesh) {
    points = new FixedList<>(maxInputPoints, Vector2.class);
    simplified = new Array<>(true, maxInputPoints, Vector2.class);
    this.mesh = mesh;
    resolve();
  }

  public Trail(int maxInputPoints) {
    this(maxInputPoints, new TrailMesh());
  }

  public Trail() {
    this(10);
  }

  public Trail setMesh(TrailMesh mesh) {
    this.mesh = mesh;
    return this;
  }

  public Trail setMesh(Texture tex) {
    this.tex = tex;
    return this;
  }

  public void resolve(Array<Vector2> input, Array<Vector2> output) {
    output.clear();
    if (input.size <= 2) {
      output.addAll(input);
      return;
    }

    if (simplifyTolerance > 0 && input.size > 3) {
      simplify(input, simplifyTolerance*simplifyTolerance, tmpv2a);
      input = tmpv2a;
    }

    if (simplifyIterations <= 0) {
      output.addAll(input);
    } else if (simplifyIterations == 1) {
      smooth(input, output);
    } else {
      int i = simplifyIterations;
      do {
        smooth(input, output);
        tmpv2a.clear();
        tmpv2a.addAll(output);
        Array<Vector2> old = output;
        input = tmpv2a;
        output = old;
      } while (--i > 0);
    }
  }

  public void resolve() {
    resolve(points, simplified);
  }

  public static void smooth(Array<Vector2> input, Array<Vector2> output) {
    output.clear();
    output.ensureCapacity(input.size*2);
    output.add(input.get(0));
    for (int i=0; i < input.size - 1; i++) {
      Vector2 p0 = input.get(i);
      Vector2 p1 = input.get(i+1);
      Vector2 Q = new Vector2(p0.x*.75f + p1.x*.25f, p0.y*.75f + p1.y*.25f);
      Vector2 R = new Vector2(p0.x*.25f + p1.x*.75f, p0.y*.25f + p1.y*.75f);
      output.add(Q);
      output.add(R);
    }
    output.add(input.get(input.size - 1));
  }

  public static void simplify(Array<Vector2> points, float sqTolerance, Array<Vector2> out) {
    int len = points.size;
    Vector2 point = new Vector2();
    Vector2 prevPoint = points.get(0);
    out.clear();
    out.add(prevPoint);

    for (int i = 1; i < len; i++) {
      point = points.get(i);
      if (dist(point, prevPoint) > sqTolerance) {
        out.add(point);
        prevPoint = point;
      }
    }

    if (!prevPoint.equals(point)) {
      out.add(point);
    }
  }

  public void start(float x, float y, float scrH) {
    points.clear();
    lastPoint.set(x, scrH - y);
    points.insert(lastPoint);
    resolve();
  }

  public void stop() {
    resolve();
  }

  public void update(float x, float y, float scrH) {
    tmpv2.set(x, scrH - y);
    float dx = tmpv2.x - lastPoint.x;
    float dy = tmpv2.y - lastPoint.y;
    float len = hypotf(dx, dy);
    if (len < minDistance && (points.size > 1 || len < initialDistance)) {
      return;
    }
    points.insert(tmpv2);
    lastPoint.set(tmpv2);
    resolve();
  }

  public void draw(Matrix4 combined) {
    Gdx.gl.glEnable(GL20.GL_BLEND);
    Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
    tex.bind();
    mesh.update(simplified);
    mesh.draw(combined);
  }
}

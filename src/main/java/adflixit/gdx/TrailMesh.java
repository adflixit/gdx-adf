package adflixit.gdx;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer20;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * Based on https://github.com/mattdesl/gdx-swiper.
 */
public class TrailMesh {
  public final ImmediateModeRenderer20  gl20      = new ImmediateModeRenderer20(false, true, 1);
  public final Array<Vector2>           texcoord  = new Array<>();
  public final Array<Vector2>           tristrip  = new Array<>();
  public final Vector2                  perp      = new Vector2();
  public Color                          color     = Color.CLEAR;
  public int                            batchSize;

  public TrailMesh() {

  }

  protected int generate(Array<Vector2> input, int mult) {
    int c = tristrip.size;
    float thickness = 30, endcap = 8.5f;

    if (endcap <= 0) {
      tristrip.add(input.get(0));
    } else {
      Vector2 p = input.get(0);
      Vector2 p2 = input.get(1);
      perp.set(p).sub(p2).scl(endcap);
      tristrip.add(new Vector2(p.x + perp.x, p.y + perp.y));
    }

    texcoord.add(new Vector2(0,0));
    for (int i=1; i < input.size-1; i++) {
      Vector2 p = input.get(i);
      Vector2 p2 = input.get(i + 1);
      perp.set(p).sub(p2).nor();
      perp.set(-perp.y, perp.x);
      float thick = thickness * (1-(i/(float)input.size));
      perp.scl(thick/2);
      perp.scl(mult);
      tristrip.add(new Vector2(p.x + perp.x, p.y + perp.y));
      texcoord.add(new Vector2(0, 0));
      tristrip.add(new Vector2(p.x, p.y));
      texcoord.add(new Vector2(1, 0));
    }

    if (endcap <= 0) {
      tristrip.add(input.get(input.size-1));
    } else {
      Vector2 p = input.get(input.size-2);
      Vector2 p2 = input.get(input.size-1);
      perp.set(p2).sub(p).scl(endcap);
      tristrip.add(new Vector2(p2.x + perp.x, p2.y + perp.y));
    }
    texcoord.add(new Vector2(0,0));
    return tristrip.size - c;
  }

  public void update(Array<Vector2> input) {
    tristrip.clear();
    texcoord.clear();
    if (input.size < 2) {
      return;
    }
    batchSize = generate(input, 1);
    int b = generate(input, -1);
  }

  public void draw(Matrix4 combined) {
    if (tristrip.size <= 0) {
      return;
    }

    gl20.begin(combined, GL20.GL_TRIANGLE_STRIP);
    for (int i=0; i < tristrip.size; i++) {
      if (i == batchSize) {
        gl20.end();
        gl20.begin(combined, GL20.GL_TRIANGLE_STRIP);
      }
      Vector2 point = tristrip.get(i);
      Vector2 tc = texcoord.get(i);
      gl20.color(color.r, color.g, color.b, color.a);
      gl20.texCoord(tc.x, 0);
      gl20.vertex(point.x, point.y, 0);
    }
    gl20.end();
  }
}

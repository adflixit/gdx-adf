package adf.gdx;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer20;
import com.badlogic.gdx.math.Vector2;

public class DistortionMesh {
  public static final int           VX          = 0,
                                    VY          = 1,
                                    VTX         = 2,
                                    VTY         = 3,
                                    VR          = 4,
                                    VG          = 5,
                                    VB          = 6,
                                    VA          = 7,
                                    VATRS       = 8;

  public enum DispRef {
    Node, Center, TopLeft
  }

  private int                       cols, rows;
  private float                     cellw, cellh;
  private float                     width, height;
  private float[][]                 vert;
  private int                       blendSrc;
  private int                       blendDst;
  private boolean                   usesReg;    // uses texture region instead of a whole texture
  private Texture                   tex;
  private ImmediateModeRenderer20   gl20        = new ImmediateModeRenderer20(false, true, 1);
  private Color                     retColor    = new Color(0xffffffff);  // return value
  private Vector2                   retDisp     = new Vector2();          // return value

  public DistortionMesh(int cols, int rows) {
    this.cols = cols;
    this.rows = rows;
    vert = new float[cols*rows][VATRS];
    for (int i=0; i < cols*rows; i++) {
      vert[i][VR] = vert[i][VG] = vert[i][VB] = 1;
    }
    blendSrc = GL20.GL_ONE;
    blendDst = GL20.GL_ONE_MINUS_SRC_ALPHA;
  }

  public DistortionMesh(DistortionMesh dm) {
    cols = dm.cols;
    rows = dm.rows;
    cellw = dm.cellw;
    cellh = dm.cellh;
    width = dm.width;
    height = dm.height;
    vert = new float[cols*rows][VATRS];
    System.arraycopy(dm.vert, 0, vert, 0, dm.vert.length);
    blendSrc = dm.blendSrc;
    blendDst = dm.blendDst;
    usesReg = dm.usesReg;
    tex = dm.tex;
  }

  public void draw(Batch batch) {
    //gl20.begin(batch.combined, GL20.GL_TRIANGLE_STRIP);
    for (int j=0; j < rows - 1; j++) {
      for (int i=0; i < cols - 1; i++) {
        int idx = j * cols + i;

        gl20.texCoord(vert[idx][VTX], vert[idx][VTY]);
        gl20.vertex(vert[idx][VX], vert[idx][VY], 0);
        gl20.color(vert[idx][VR], vert[idx][VG], vert[idx][VB], vert[idx][VA]);

        idx++;

        gl20.texCoord(vert[idx][VTX], vert[idx][VTY]);
        gl20.vertex(vert[idx][VX], vert[idx][VY], 0);
        gl20.color(vert[idx][VR], vert[idx][VG], vert[idx][VB], vert[idx][VA]);

        idx += cols;

        gl20.texCoord(vert[idx][VTX], vert[idx][VTY]);
        gl20.vertex(vert[idx][VX], vert[idx][VY], 0);
        gl20.color(vert[idx][VR], vert[idx][VG], vert[idx][VB], vert[idx][VA]);

        idx--;

        gl20.texCoord(vert[idx][VTX], vert[idx][VTY]);
        gl20.vertex(vert[idx][VX], vert[idx][VY], 0);
        gl20.color(vert[idx][VR], vert[idx][VG], vert[idx][VB], vert[idx][VA]);
      }
    }
    gl20.end();
  }

  public void clear(Color clr) {
    for (int j=0; j < rows; j++) {
      for (int i=0; i < cols; i++) {
        vert[j * cols + i][VX] = i * cellw;
        vert[j * cols + i][VY] = j * cellh;
        vert[j * cols + i][VR] = clr.r;
        vert[j * cols + i][VG] = clr.g;
        vert[j * cols + i][VB] = clr.b;
        vert[j * cols + i][VA] = clr.a;
      }
    }
  }

  public void setBlendFunc(int src, int dst) {
    blendSrc = src;
    blendDst = dst;
  }

  public void setTexture(Texture tex) {
    usesReg = false;
    this.tex = tex;
    width = tex.getWidth();
    height = tex.getHeight();
    initGrid(0, 0);
  }

  public void setTextureRegion(TextureRegion reg) {
    usesReg = true;
    tex = reg.getTexture();
    width = reg.getRegionWidth();
    height = reg.getRegionHeight();
    initGrid(reg.getRegionX() / width, reg.getRegionY() / height);
  }

  private void initGrid(float tx, float ty) {
    cellw = width / (cols - 1);
    cellh = height / (rows - 1);

    for (int j=0; j < rows; j++) {
      for (int i=0; i < cols; i++) {
        //vert[j * cols + i][VTX] = tx + (x + i * cellw) / tw;
        //vert[j * cols + i][VTY] = ty + (y + j * cellh) / th;
        vert[j * cols + i][VX] = i * cellw;
        vert[j * cols + i][VY] = j * cellh;
      }
    }
  }

  public void setColor(int col, int row, float r, float g, float b, float a) {
    if (row < rows && col < cols) {
      int idx = row * cols + col;
      vert[idx][VR] = r;
      vert[idx][VG] = g;
      vert[idx][VB] = b;
      vert[idx][VA] = a;
    }  
  }

  public void setDisplacement(int col, int row, float dx, float dy, DispRef ref) {
    if (row < rows && col < cols) {
      switch (ref) {
      case Node:
        dx += col * cellw;
        dy += row * cellh;
        break;
      case Center:
        dx += cellw * (cols - 1) / 2;
        dy += cellh * (rows - 1) / 2;
        break;
      case TopLeft:
        break;
      }
      vert[row * cols + col][VX] = dx;
      vert[row * cols + col][VY] = dy;
    }
  }

  public Color getColor(int col, int row) {
    if (row < rows && col < cols) {
      int index = row * cols + col;
      retColor.set(vert[index][VR], vert[index][VG], vert[index][VB], vert[index][VA]);
      return retColor;
    }
    return Color.WHITE;
  }

  public Vector2 getDisplacement(int col, int row, DispRef ref) {
    if (row < rows && col < cols) {
      int index = row * cols + col;
      switch (ref) {
      case Node:
        retDisp.set(vert[index][VX] - col * cellw, vert[index][VY] - row * cellh);
        break;
      case Center:
        retDisp.set(vert[index][VX] - cellw * (cols - 1) / 2, vert[index][VY] - cellh * (rows - 1) / 2);
        break;
      case TopLeft:
        retDisp.set(vert[index][VX], vert[index][VY]);
        break;
      }
    }
    return Vector2.Zero;
  }

  public int cols() {
    return cols;
  }

  public int rows() {
    return rows;
  }
}

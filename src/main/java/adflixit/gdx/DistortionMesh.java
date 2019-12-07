package adflixit.gdx;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class DistortionMesh {
  public static final int V_X     = 0,
                          V_Y     = 1,
                          V_TX    = 2,
                          V_TY    = 3,
                          V_R     = 4,
                          V_G     = 5,
                          V_B     = 6,
                          V_ATRS  = 7;

  public enum DispRef {
    Node, Center
  }

  private int             cols, rows;
  private float           cellw, cellh;
  private float           tx, ty;
  private float           width, height;
  private float[][]       disp;
  private Texture         tex;

  public DistortionMesh(int cols, int rows) {
    this.cols = cols;
    this.rows = rows;
    disp = new float[cols*rows][V_ATRS];
    for (int i=0; i < cols*rows; i++) {
      disp[i][V_R] = disp[i][V_G] = disp[i][V_B] = 1;
    }
  }

  public DistortionMesh(DistortionMesh dm) {
    cols = dm.cols;
    rows = dm.rows;
    cellw = dm.cellw;
    cellh = dm.cellh;
    tx = dm.tx;
    ty = dm.ty;
    width = dm.width;
    height = dm.height;
    disp = new float[cols*rows][V_ATRS];
    System.arraycopy(dm.disp, 0, disp, 0, dm.disp.length);
  }

  public void update() {

  }

  public void draw(Batch batch) {

  }

  public void setTexture(Texture tex) {
    this.tex = tex;

  }

  /*public void setTextureRegion(TextureRegion reg) {
    tx = reg.getRegionX();
    ty = reg.getRegionY();
    width = reg.getRegionWidth();
    height = re

    tx_ = x;
    ty_ = y;
    width_ = w;
    height_ = h;

    if (quad_.tex) {
      tw = static_cast<float>(hge_->Texture_GetWidth(quad_.tex));
      th = static_cast<float>(hge_->Texture_GetHeight(quad_.tex));
    }
    else {
      tw = w;
      th = h;
    }

    cellw_ = w / (cols_ - 1);
    cellh_ = h / (rows_ - 1);

    for (int j = 0; j < rows; j++)
      for (int i = 0; i < cols; i++) {
        disp[j * cols + i].tx = (x + i * cellw) / tw;
        disp[j * cols + i].ty = (y + j * cellh) / th;

        disp[j * cols + i].x = i * cellw;
        disp[j * cols + i].y = j * cellh;
      }
  }

  public void setColor(int col, int row, float r, float g, float b, float a) {

  }

  public void setDisplacement(int col, int row, float dx, float dy, int ref) {
    if (row < rows && col < cols) {
      switch (ref) {
        case HGEDISP_NODE:
          dx += col * cellw_;
          dy += row * cellh_;
          break;
        case HGEDISP_CENTER:
          dx += cellw_ * (cols_ - 1) / 2;
          dy += cellh_ * (rows_ - 1) / 2;
          break;
        case HGEDISP_TOPLEFT:
          break;
      }

      disp[row * cols + col].x = dx;
      disp[row * cols + col].y = dy;
    }
  }*/

  public int cols() {
    return cols;
  }

  public int rows() {
    return rows;
  }
}

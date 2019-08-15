package adflixit.gdx;

import com.badlogic.gdx.graphics.g2d.Batch;

public interface Box2DUserData {
  public void render(Batch batch, float x, float y, float angle);
  public boolean isInForeground();
  public boolean isInBackground();
}

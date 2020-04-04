package adf.gdx;

import static java.lang.Math.round;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * {@link Viewport} that scales the larger dimension while preserving the aspect ratio.
 * 
 * TODO: apparently doesn't scale correctly in landscape mode.
 */
public class AdaptiveViewport extends Viewport {
  public AdaptiveViewport(Camera camera) {
    setCamera(camera);
  }

  public AdaptiveViewport() {
    this(new OrthographicCamera());
  }

  public void update(float worldWidth, float worldHeight, int screenWidth, int screenHeight, boolean centerCamera) {
    Vector2 scaled = Scaling.fit.apply(worldWidth, worldHeight, screenWidth, screenHeight);
    int viewportWidth = round(scaled.x), viewportHeight = round(scaled.y);
    if (viewportWidth < screenWidth) {
      float toViewportSpace = viewportHeight / worldHeight;
      float toWorldSpace = worldHeight / viewportHeight;
      float lengthen = (screenWidth - viewportWidth) * toWorldSpace;

      worldWidth += lengthen;
      viewportWidth += round(lengthen * toViewportSpace);
    } else if (viewportHeight < screenHeight) {
      float toViewportSpace = viewportWidth / worldWidth;
      float toWorldSpace = worldWidth / viewportWidth;
      float lengthen = (screenHeight - viewportHeight) * toWorldSpace;

      worldHeight += lengthen;
      viewportHeight += round(lengthen * toViewportSpace);
    }
    setWorldSize(worldWidth, worldHeight);
    setScreenBounds((screenWidth - viewportWidth) / 2, (screenHeight - viewportHeight) / 2, viewportWidth, viewportHeight);
    apply(centerCamera);
  }

  public void update(float worldWidth, float worldHeight, int screenWidth, int screenHeight) {
    update(worldWidth, worldHeight, screenWidth, screenHeight, false);
  }
}

package adf.gdx;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

/**
 * A simple widget that utilizes UI input event handling, while preserving the valid UI stage hierarchy.
 * Creates a full-screen layer which receives the input.
 */
public class Tapper extends ScreenComponent<BaseContext<?>> {
  private final Actor   area  = new Actor();
  private InputListener listener;

  public Tapper(BaseContext<?> context) {
    super(context);
  }

  public void reset() {
    area.remove();
    area.removeListener(listener);
  }

  public void set(Group parent, int z, final Callback cb, boolean down) {
    parent.addActor(area);
    // set position to the absolute zero
    updatePos();
    area.setZIndex(z);

    if (down) {
      area.addListener(listener = new InputListener() {
        @Override public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
          cb.call();
          return true;
        }
      });
    } else {
      area.addListener(listener = new InputListener() {
        @Override public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
          return true;
        }

        @Override public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
          super.touchDown(event, x, y, pointer, button);
          cb.call();
        }
      });
    }
  }

  public void setOnce(Group parent, int z, final Callback cb, boolean down) {
    parent.addActor(area);
    // set position to the absolute zero
    updatePos();
    area.setZIndex(z);

    if (down) {
      area.addListener(listener = new InputListener() {
        @Override public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
          cb.call();
          reset();
          return true;
        }
      });
    } else {
      area.addListener(listener = new InputListener() {
        @Override public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
          return true;
        }

        @Override public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
          cb.call();
          reset();
        }
      });
    }
  }

  /**
   * Aligns position to the very root of UI scene.
   */
  public void updatePos() {
    Group parent = area.getParent();
    area.setPosition(0, 0);
    while (parent != ui.getRoot()) {
      area.setPosition(area.getX() - parent.getX(), area.getY() - parent.getY());
      parent = parent.getParent();
    }
  }

  public void resize() {
    area.setSize(ui.getWidth(), ui.getHeight());
  }
}

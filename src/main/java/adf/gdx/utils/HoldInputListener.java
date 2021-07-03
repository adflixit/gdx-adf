package adf.gdx.utils;

import static adf.gdx.BaseContext.*;
import static adf.gdx.Util.currentTime;

import adf.gdx.Callback;
import adf.gdx.Updatable;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

/**
 * {@link InputListener} that fires after it has been held for a certain amount of time.
 */
public class HoldInputListener extends InputListener implements Updatable {
  private boolean   pressed, locked;
  private long      startTime, threshold;
  private Callback  callback;

  /**
   * @param threshold time in milliseconds
   */
  public HoldInputListener(long threshold, Callback cb) {
    this.threshold = threshold;
    setCallback(cb);
    addUpdatable(this);
  }

  public HoldInputListener(Callback cb) {
    this(2000, cb);
  }

  public void setCallback(Callback cb) {
    callback = cb;
  }

  @Override public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
    pressed = true;
    startTime = currentTime();
    return true;
  }

  @Override public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
    pressed = false;
    locked = false;
  }

  @Override public void update() {
    if (pressed && currentTime() - startTime >= threshold && !locked) {
      locked = true;
      callback.call();
    }
  }
}

/*
 * Copyright 2019 Adflixit
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package adflixit.shared.misc;

import static adflixit.shared.BaseContext.*;
import static adflixit.shared.Util.*;

import adflixit.shared.Callback;
import adflixit.shared.Updatable;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

/**
 * {@link InputListener} that fires after it has been held for a certain amount of time.
 */
public class HoldInputListener extends InputListener implements Updatable {
  private boolean   pressed, locked;
  private long      startTime, treshold;
  private Callback  callback;

  /**
   * @param treshold time in milliseconds
   */
  public HoldInputListener(long treshold, Callback cb) {
    this.treshold = treshold;
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
    if (pressed && currentTime() - startTime >= treshold && !locked) {
      locked = true;
      callback.call();
    }
  }
}

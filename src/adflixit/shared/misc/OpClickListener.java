/*
 * Copyright 2018 Adflixit
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

import static adflixit.shared.TweenUtils.*;
import static adflixit.shared.Util.*;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import adflixit.shared.ClickCallback;

/**
 * {@link ClickListener} that operates the target actor's opacity.
 */
public class OpClickListener extends ClickListener {
  private Actor         actor;
  private float         initOp, fadeOp;
  private ClickCallback callback;

  public OpClickListener(Actor actor, float iop, float fop, ClickCallback cb) {
    super();
    this.actor = actor;
    this.initOp = iop;
    this.fadeOp = fop;
    setCallback(cb);
  }

  public void setCallback(ClickCallback cb) {
    callback = cb;
  }

  public void fadeIn() {
    fadeActor(actor, fadeOp, C_HD);
  }

  public void fadeOut() {
    fadeActor(actor, initOp, C_HD);
  }

  @Override public void clicked(InputEvent event, float x, float y) {
    callback.clicked(event, x, y);
  }

  @Override public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
    super.enter(event, x, y, pointer, fromActor);
    fadeIn();
  }

  @Override public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
    super.exit(event, x, y, pointer, toActor);
    fadeOut();
  }
}

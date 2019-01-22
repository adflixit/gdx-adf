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

package adflixit.shared;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * Array of reusable {@link SplashAnim} items.
 */
public class SplashPool extends Pool<SplashAnim> {
  private float                           frameDuration;
  private Array<? extends TextureRegion>  keyFrames;
  private float                           scale;

  public SplashPool(int cap, float frameDuration, Array<? extends TextureRegion> keyFrames, float scale) {
    super(cap);
    init(cap, frameDuration, keyFrames, scale);
  }

  public SplashPool(int cap, float frameDuration, Array<? extends TextureRegion> keyFrames) {
    this(cap, frameDuration, keyFrames, 1);
  }

  public SplashPool(float frameDuration, Array<? extends TextureRegion> keyFrames, float scale) {
    this(10, frameDuration, keyFrames, scale);
  }

  public SplashPool(float frameDuration, Array<? extends TextureRegion> keyFrames) {
    this(10, frameDuration, keyFrames);
  }

  public void init(int cap, float frameDuration, Array<? extends TextureRegion> keyFrames, float scale) {
    this.frameDuration = frameDuration;
    this.keyFrames = keyFrames;
    setScale(scale);
    resize(cap);
  }

  public SplashPool setScale(float scale) {
    this.scale = scale;
    for (SplashAnim anim : this) {
      anim.setBaseScale(scale);
    }
    return this;
  }

  public void draw(Batch batch) {
    for (SplashAnim anim : this) {
      anim.draw(batch);
    }
  }

  /** Plays animation once at this point. */
  public void fire(float x, float y) {
    SplashAnim anim = nextFree();
    if (anim != null) {
      anim.fire(x, y);
    }
  }

  /** Plays animation once at this point. */
  public void fire(Vector2 pos) {
    fire(pos.x, pos.y);
  }

  /** Plays animation once at this point. */
  public void fire(float x, float y, float scale) {
    SplashAnim anim = nextFree();
    if (anim != null) {
      anim.fire(x, y, scale);
    }
  }

  /** Plays animation once at this point. */
  public void fire(Vector2 pos, float scale) {
    fire(pos.x, pos.y, scale);
  }

  @Override public SplashAnim newObj() {
    return new SplashAnim(frameDuration, keyFrames, scale);
  }

  @Override public boolean isFree(SplashAnim obj) {
    return obj.isFree();
  }
}

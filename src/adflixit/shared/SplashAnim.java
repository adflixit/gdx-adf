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

package adflixit.shared;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

/**
 * {@link Animation} that is being played fully once in a certain point when called.
 */
public class SplashAnim extends Animation<TextureRegion> {
  private final Timestamp  time  = new Timestamp();
  private float            x, y;
  private float            baseScale, scale;

  public SplashAnim(float frameDuration, Array<? extends TextureRegion> keyFrames, float baseScale) {
    super(frameDuration, keyFrames);
    this.baseScale = baseScale;
  }

  public void setBaseScale(float scale) {
    baseScale = scale;
  }

  /** Plays animation once at this point. */
  public void fire(float x, float y, float scale) {
    time.set();
    this.x = x;
    this.y = y;
    this.scale = scale;
  }

  /** Plays animation once at this point. */
  public void fire(float x, float y) {
    fire(x, y, 1);
  }

  public void draw(Batch batch) {
    if (!isFree()) {
      TextureRegion keyFrame = getKeyFrame(time.elapsedSecs(), false);
      float scl = baseScale*scale,
      width = keyFrame.getRegionWidth() * scl, height = keyFrame.getRegionHeight() * scl;
      batch.draw(keyFrame, x - width/2, y - height/2, width, height);
    }
  }

  public boolean isFree() {
    return time.elapsedSecs() > getAnimationDuration();
  }
}

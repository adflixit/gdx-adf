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

import static adflixit.shared.TweenUtils.*;
import static adflixit.shared.Util.*;

import adflixit.shared.misc.Soft;
import aurelienribon.tweenengine.Tween;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class ParallaxBg extends ScreenComponent<BaseContext<?>> {
  public float            factor  = PLXBG_FC;
  private TextureRegion[] layers;
  private int             length;
  private final Vector2   pos     = new Vector2();
  public final Vector2    shift   = new Vector2();
  private boolean         enabled;

  public ParallaxBg(BaseContext context) {
    super(context);
  }

  public void init(TextureRegion... regions) {
    layers = regions;
    length = regions.length;
    enable();
  }

  public void enable() {
    enabled = true;
  }

  public void disable() {
    enabled = false;
  }

  public void draw() {
    if (enabled) {
      pos.set(ctx.cameraPos0());
      for (int i=0; i < length; i++) {
        drawTiledRect(bat, layers[i], pos.x, pos.y,
            shift.x * factor * (i+1), shift.y * factor * (i+1),
            ctx.screenWidthI(), ctx.screenHeightI());
      }
    }
  }

  public Tween moveTo(float x, float y, float d) {
    killTweenTarget(shift);
    return Tween.to(shift, TweenUtils.Vector2Accessor.XY, d).target(x, y).ease(Soft.INOUT);
  }

  public Tween setShift(float x, float y) {
    return moveTo(x, y, 0);
  }
}

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

import adflixit.shared.thirdparty.BezierEasing;
import aurelienribon.tweenengine.TweenEquation;

/**
 * Bezier-based soft tween equation.
 */
public abstract class Soft extends TweenEquation {
  // All setups go here.
  public static BezierEasing inout = new BezierEasing(.25f, .1f, .25f, 1);

  public static final Soft INOUT = new Soft() {
    @Override
    public final float compute(float t) {
      return inout.ease(t);
    }

    @Override
    public String toString() {
      return "Soft.INOUT";
    }
  };
}

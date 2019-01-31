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
import static aurelienribon.tweenengine.TweenCallback.*;

import adflixit.shared.misc.Soft;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Quart;
import aurelienribon.tweenengine.primitives.MutableFloat;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import java.util.ArrayList;
import java.util.List;

/**
 * Draws the specified texture (usually the frame buffer output) with a shader filter.
 * TODO: tilt shift shader doesn't work correctly.
 */
public class Postprocessor extends ScreenComponent<BaseScreen<?>> {
  public static final int           RES_DENOM = 4;
  
  public static final int           BLUR      = 0,
                                    TSY       = 1,
                                    TSX       = 2,
                                    TSYP      = 3;

  private static final String[]     uniNames  = {
    "u_blur",
    "u_tiltshiftY",
    "u_tiltshiftX",
    "u_tiltshiftYPos"
  };
  private static final int          uniLength = uniNames.length;

  private ShaderProgram             firstPass;
  private ShaderProgram             lastPass;
  private FrameBuffer               fb;
  private FrameBuffer               firstFb;
  private FrameBuffer               lastFb;
  private final List<MutableFloat>  values    = new ArrayList<>();
  private int                       locks;      // update route permits
  private int                       schedules;  // one-time update route permits

  public Postprocessor(BaseScreen<?> screen) {
    super(screen);
    for (int i=0; i < uniLength; i++) {
      values.add(new MutableFloat(0));
    }
  }

  public Postprocessor(BaseScreen<?> screen, FileHandle hvert, FileHandle hfrag, FileHandle vvert, FileHandle vfrag) {
    this(screen);
    load(hvert, hfrag, vvert, vfrag);
  }

  public Postprocessor(BaseScreen<?> screen, String hvert, String hfrag, String vvert, String vfrag) {
    this(screen);
    load(hvert, hfrag, vvert, vfrag);
  }

  public void load(FileHandle hvert, FileHandle hfrag, FileHandle vvert, FileHandle vfrag) {
    firstPass = new ShaderProgram(hvert, hfrag);
    lastPass = new ShaderProgram(vvert, vfrag);
  }

  public void load(String hvert, String hfrag, String vvert, String vfrag) {
    firstPass = new ShaderProgram(hvert, hfrag);
    lastPass = new ShaderProgram(vvert, vfrag);
  }

  public void reset() {
    for (int i=0; i < uniLength; i++) {
      reset(i);
    }
    locks = 0;
    schedules = 0;
  }

  public void begin() {
    fb.begin();
  }

  public void end() {
    fb.end();
  }

  public Texture inputTex() {
    return fb.getColorBufferTexture();
  }

  public void draw() {
    Texture tex = inputTex();
    float x = scr.cameraX0(), y = scr.cameraY0();

    // if the processed area is not full, the original texture has to be drawn first
    if (!isFull()) {
      bat.begin();
        bat.draw(tex, x, y, scr.screenWidth(), scr.screenHeight(), 0,0,1,1);
      bat.end();
    }

    // horizontal pass
    bat.setShader(firstPass);
    firstFb.begin();
    bat.begin();
      for (int i=0; i < uniLength; i++) {
        int m = 1<<i;
        if (hasFlag(locks, m) || hasFlag(schedules, m)) {
          firstPass.setUniformf(uniNames[i], value(i));
        }
      }
      bat.draw(tex, x, y, scr.screenWidth(), scr.screenHeight(), 0,0,1,1);
    bat.end();
    firstFb.end();

    // vertical pass
    bat.setShader(lastPass);
    lastFb.begin();
    bat.begin();
      for (int i=0; i < uniLength; i++) {
        int m = 1<<i;
        boolean scheduled = hasFlag(schedules, m);
        if (hasFlag(locks, m) || scheduled) {
          lastPass.setUniformf(uniNames[i], value(i));
          if (scheduled) {
            unschedule(i);
          }
        }
      }
      tex = firstFb.getColorBufferTexture();
      bat.draw(tex, x, y, scr.screenWidth(), scr.screenHeight(), 0,0,1,1);
    bat.end();
    lastFb.end();

    bat.setShader(null);
    bat.begin();
      tex = lastFb.getColorBufferTexture();
      bat.draw(tex, x, y, scr.screenWidth(), scr.screenHeight(), 0,0,1,1);
    bat.end();
  }

  public boolean isActive() {
    return value(BLUR) + value(TSY) + value(TSX) > 0;
  }

  /**
   * @return does the filter occupy the whole screen. 
   */
  public boolean isFull() {
    return value(BLUR) > 0;
  }

  /**
   * Grants access to the update route for a filter specified by the index.
   */
  private void unlock(int i) {
    int m = 1<<i;
    if (!hasFlag(locks, m)) {
      locks |= m;
    }
  }

  /**
   * Revokes access to the update route for a filter specified by the index.
   */
  private void lock(int i) {
    int m = 1<<i;
    if (hasFlag(locks, m)) {
      locks ^= m;
    }
  }

  /**
   * Requests one-time access to the update route.
   */
  private void schedule(int i) {
    int m = 1<<i;
    if (!hasFlag(schedules, m)) {
      schedules |= m;
    }
  }

  /**
   * Resets one-time access to the update route.
   */
  private void unschedule(int i) {
    int m = 1<<i;
    if (hasFlag(schedules, m)) {
      schedules ^= m;
    }
  }
  
  private MutableFloat field(int i) {
    return values.get(i);
  }

  public float value(int i) {
    return field(i).floatValue();
  }

  public void set(int i, float v) {
    killTweenTarget(field(i));
    field(i).setValue(v);
    schedule(i);
  }

  public void reset(int i) {
    set(i, 0);
  }

  /**
   * @param i index
   * @param v value
   * @param d duration
   */
  public Tween $tween(int i, float v, float d) {
    killTweenTarget(field(i));
    return Tween.to(field(i), 0, d).target(v).ease(Soft.INOUT)
           .setCallback((type, source) -> {
             if (type == BEGIN) {
               unlock(i);
             } else {
               lock(i);
             }
           })
           .setCallbackTriggers(BEGIN|COMPLETE);
  }

  /**
   * @param i index
   * @param d duration
   */
  public Tween $tweenOut(int i, float d) {
    return $tween(i, 0, d);
  }

  /**
   * @param i index
   * @param v value
   */
  public Tween $set(int i, float v) {
    killTweenTarget(field(i));
    return Tween.set(field(i), 0).target(v).setCallback((type, source) -> schedule(i));
  }

  /**
   * @param i index
   */
  public Tween $reset(int i) {
    return $set(i, 0);
  }

  public void dispose() {
    firstPass.dispose();
    lastPass.dispose();
    firstFb.dispose();
    lastFb.dispose();
  }

  public void resize() {
    if (firstResize) {
      firstResize = false;
    } else {
      firstFb.dispose();
      lastFb.dispose();
    }
    fb = new FrameBuffer(Format.RGBA8888, scr.fbWidth() / RES_DENOM, scr.fbHeight() / RES_DENOM, false);
    firstFb = new FrameBuffer(Format.RGBA8888, scr.fbWidth(), scr.fbHeight(), false);
    lastFb = new FrameBuffer(Format.RGBA8888, scr.fbWidth(), scr.fbHeight(), false);
  }
}

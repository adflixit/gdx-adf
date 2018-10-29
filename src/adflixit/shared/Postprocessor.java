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

import static adflixit.shared.TweenUtils.*;
import static adflixit.shared.Util.*;
import static aurelienribon.tweenengine.TweenCallback.*;

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
 */
public class Postprocessor extends ScreenComponent<BaseScreen<?>> {
  private TweenCallback createLockCallback(final int i) {
    return (type, source) -> {
      if (type==BEGIN) {
        unlock(i);
      } else {
        lock(i);
      }
    };
  }

  private TweenCallback createScheduleCallback(final int i) {
    return (type, source) -> schedule(i);
  }

  public static final int		uniBlur			= 0,
  					uniTiltshiftY		= 1,
  					uniTiltshiftX		= 2;
  private static final String[]		uniNames		= {
    "u_blur",
    "u_tiltshiftY",
    "u_tiltshiftX"
  };
  private static final int		uniLength		= uniNames.length;
  private static final String		uniResName		= "u_res";

  private ShaderProgram			shader;
  private FrameBuffer			frameBuffer;
  private final List<MutableFloat>	values			= new ArrayList<>();
  private int				locks;			// update route permits
  private int				schedules;		// one-time access tickets
  private final TweenCallback[]		locksCallbacks		= new TweenCallback[uniLength];
  private final TweenCallback[]		schedulesCallbacks	= new TweenCallback[uniLength];

  public Postprocessor(BaseScreen<?> screen) {
    super(screen);
    for (int i=0; i < uniLength; i++) {
      values.add(new MutableFloat(0));
      locksCallbacks[i] = createLockCallback(i);
      schedulesCallbacks[i] = createScheduleCallback(i);
    }
  }

  public Postprocessor(BaseScreen<?> screen, FileHandle vert, FileHandle frag) {
    this(screen);
    load(vert, frag);
  }

  public Postprocessor(BaseScreen<?> screen, String vert, String frag) {
    this(screen);
    load(vert, frag);
  }

  public void load(FileHandle vert, FileHandle frag) {
    shader = new ShaderProgram(vert, frag);
  }

  public void load(String vert, String frag) {
    shader = new ShaderProgram(vert, frag);
  }

  public void reset() {
    for (int i=0; i < uniLength; i++) {
      resetValue(i);
    }
    locks = 0;
    schedules = 0;
  }

  public Texture inputTex() {
    return scr.screenTex();
  }

  public void draw() {
    Texture tex = inputTex();
    // first drawn with mipmap filter, next with linear
    TextureFilter ff = TextureFilter.MipMap, lf = TextureFilter.Linear;
    float x = scr.cameraXAtZero(), y = scr.cameraYAtZero();
    // if the filtering area is not full, the original texture has to be drawn first
    if (!isFull()) {
      bat.begin();
        bat.draw(tex, x, y, tex.getWidth(), tex.getHeight(), 0,0,1,1);
      bat.end();
    }
    bat.setShader(shader);
    frameBuffer.begin();
    bat.begin();
      shader.setUniformf(uniResName, scr.frameBufferWidth(), scr.frameBufferHeight());
      for (int i=0; i < uniLength; i++) {
        int m = 1<<i;
        boolean scheduled = hasFlag(schedules, m);
        if (hasFlag(locks, m) || scheduled) {
          shader.setUniformf(uniNames[i], value(i).floatValue());
          if (scheduled) {
          unschedule(i);
          }
        }
      }
      TextureFilter mgf = tex.getMagFilter(), mnf = tex.getMinFilter();
      // switching to mipmap
      tex.setFilter(ff, ff);
      bat.draw(tex, x, y, tex.getWidth(), tex.getHeight(), 0,0,1,1);
      // switching back
      tex.setFilter(mgf, mnf);
    bat.end();
    frameBuffer.end();
    bat.begin();
      tex = frameBuffer.getColorBufferTexture();
      // setting to linear
      tex.setFilter(lf, lf);
      bat.draw(frameBuffer.getColorBufferTexture(), x, y, scr.screenWidth(), scr.screenHeight(), 0,0,1,1);
    bat.end();
    bat.setShader(null);
  }

  public boolean isActive() {
    return (locks|schedules) != 0;
  }

  /** @return does the filter occupy the whole screen.  */
  public boolean isFull() {
    return values.get(uniBlur).floatValue() > 0;
  }

  /** Grants access to the update route for a filter specified by the index. */
  private void unlock(int i) {
    int m = 1<<i;
    if (!hasFlag(locks, m)) {
      locks |= m;
    }
  }

  /** Revokes access to the update route for a filter specified by the index. */
  private void lock(int i) {
    int m = 1<<i;
    if (hasFlag(locks, m)) {
      locks ^= m;
    }
  }

  /** Requests a one-time access to the update route. */
  private void schedule(int i) {
    int m = 1<<i;
    if (!hasFlag(schedules, m)) {
      schedules |= m;
    }
  }

  /** Resets the one-time access to the update route. */
  private void unschedule(int i) {
    int m = 1<<i;
    if (hasFlag(schedules, m)) {
      schedules ^= m;
    }
  }

  private MutableFloat value(int i) {
    return values.get(i);
  }

  public void setValue(int i, float v) {
    killTweenTarget(value(i));
    value(i).setValue(v);
    schedule(i);
  }

  public void resetValue(int i) {
    setValue(i, 0);
  }

  /** @param i index
   * @param v value
   * @param d duration */
  public Tween $tween(int i, float v, float d) {
    killTweenTarget(value(i));
    return Tween.to(value(i), 0, d).target(v).ease(Quart.OUT)
           .setCallback(locksCallbacks[i])
           .setCallbackTriggers(BEGIN|COMPLETE);
  }

  /** @param i index
   * @param d duration */
  public Tween $tweenOut(int i, float d) {
    return $tween(i, 0, d);
  }

  /** @param i index
   * @param v value */
  public Tween $setValue(int i, float v) {
    killTweenTarget(value(i));
    return Tween.set(value(i), 0).target(v).setCallback(schedulesCallbacks[i]);
  }

  /** @param i index */
  public Tween $resetValue(int i) {
    return $setValue(i, 0);
  }

  public void dispose() {
    shader.dispose();
    frameBuffer.dispose();
  }

  public void resize() {
    if (firstResize) {
      firstResize = false;
    } else {
      frameBuffer.dispose();
    }
    frameBuffer = new FrameBuffer(Format.RGBA8888, scr.frameBufferWidth(), scr.frameBufferHeight(), false);
  }
}

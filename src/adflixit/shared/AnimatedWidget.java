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

import static adflixit.shared.BaseScreen.*;
import static adflixit.shared.Defs.*;
import static adflixit.shared.TweenUtils.*;
import static java.lang.Math.*;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenEquation;
import aurelienribon.tweenengine.equations.Linear;
import aurelienribon.tweenengine.equations.Quart;
import aurelienribon.tweenengine.primitives.MutableFloat;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.utils.Array;

/**
 * {@link Widget} used to display an {@link Animation}.
 */
public class AnimatedWidget extends Widget {
	private Animation<TextureRegion>	anim;
	private float						duration;
	private Tween						tween;		// used to manipulate the playtime mark
	private final MutableFloat			time		= new MutableFloat(0);	// current playtime mark

	public AnimatedWidget(float frameDuration, Array<? extends TextureRegion> keyFrames, PlayMode playMode) {
		setAnimation(new Animation<TextureRegion>(frameDuration, keyFrames, playMode));
	}

	public AnimatedWidget(float frameDuration, Array<? extends TextureRegion> keyFrames) {
		this(frameDuration, keyFrames, PlayMode.NORMAL);
	}

	public void setAnimation(Animation<TextureRegion> anim) {
		if (anim==null) {
			throw new IllegalArgumentException("Animation can't be null.");
		}
		this.anim = anim;
		duration = anim.getAnimationDuration();
		float width = 0, height = 0;
		for (TextureRegion region : anim.getKeyFrames()) {
			width = max(width, region.getRegionWidth());
		}
		for (TextureRegion region : anim.getKeyFrames()) {
			height = max(height, region.getRegionHeight());
		}
		setSize(width, height);
	}

	/** @return current playtime mark. */
	public float time() {
		return time.floatValue();
	}

	public void setTime(float value) {
		time.setValue(value);
	}

	/** Sets current playtime mark to zero. */
	public void resetTime() {
		setTime(0);
	}

	/** Tweens the playtime mark.
	 * @param v value
	 * @param d duration
	 * @param eq tween equation */
	public void tween(float v, float d, TweenEquation eq) {
		$tween(v, d, eq).start(uiTweenMgr);
	}

	/** Tweens the playtime mark with predetermined tween equation.
	 * @param v value
	 * @param d duration */
	public void tween(float v, float d) {
		$tween(v, d).start(uiTweenMgr);
	}

	/** Tweens the playtime mark with predetermined duration and tween equation.
	 * @param v value */
	public void tween(float v) {
		tween(v, C_D);
	}

	/** Creates a handle to tween the playtime mark.
	 * @param v value
	 * @param d duration 
	 * @param eq tween equation
	 * @return tween handle */
	public Tween $tween(float v, float d, TweenEquation eq) {
		killTweenTarget(time);
		return Tween.to(time, 0, d).target(v).ease(eq);
	}

	/** Creates a handle to tween the playtime mark.
	 * @param v value
	 * @param d duration
	 * @return tween handle */
	public Tween $tween(float v, float d) {
		return $tween(v, d, Quart.OUT);
	}

	/** Creates a tween handle to tween the playtime mark.
	 * @param v value
	 * @return tween handle */
	public Tween $tween(float v) {
		return $tween(v, C_D);
	}

	/** Creates the playtime tween handle. */
	private void initTween() {
		if (tween==null) {
			tween = Tween.to(time, 0, duration).target(duration).ease(Linear.INOUT);
		}
	}

	public AnimatedWidget play(float delay, boolean looped) {
		initTween();
		if (looped) {
			tween.repeat(-1, delay);
		}
		tween.start(uiTweenMgr);
		return this;
	}

	public AnimatedWidget play() {
		return play(0, false);
	}

	public AnimatedWidget playLooped(float delay) {
		return play(delay, true);
	}

	public AnimatedWidget playLooped() {
		return playLooped(0);
	}

	public void pause() {
		tween.pause();
	}

	public void resume() {
		tween.resume();
	}

	public void stop() {
		tween.kill();
	}

	@Override public void draw(Batch batch, float parentAlpha) {
		validate();
		Color color = getColor();
		batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
		float x = getX();
		float y = getY();
		float originX = getOriginX();
		float originY = getOriginY();
		float width = getWidth();
		float height = getHeight();
		float scaleX = getScaleX();
		float scaleY = getScaleY();
		float rotation = getRotation();
		batch.draw(anim.getKeyFrame(time(), false), x, y, originX, originY, width, height, scaleX, scaleY, rotation);
	}
}

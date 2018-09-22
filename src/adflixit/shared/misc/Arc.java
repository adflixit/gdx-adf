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

import static com.badlogic.gdx.utils.Align.*;
import static adflixit.shared.BaseGame.*;
import static adflixit.shared.BaseScreen.*;
import static adflixit.shared.Defs.*;
import static adflixit.shared.TweenUtils.*;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenEquation;
import aurelienribon.tweenengine.equations.Quart;
import aurelienribon.tweenengine.primitives.MutableFloat;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TransformDrawable;

/**
 * Widget to draw an arc. Draws it by drawing multiple one-degree fragments of a circle.
 * This can also be called radial progress bar, which it is.
 * It draws clockwise.
 */
public class Arc extends Widget {
	private TransformDrawable		drawable;		// drawable of a single degree
	private final MutableFloat		progress		= new MutableFloat(0);
	private float					radius;

	public Arc(Drawable drawable, float radius) {
		setDrawable(drawable);
		setRadius(radius);
		setSize(getPrefWidth(), getPrefHeight());
		setOrigin(center);
	}

	public Arc(String drawable, float radius) {
		this(drawable(drawable), radius);
	}

	public Arc(float radius) {
		this("arc_deg", radius);
	}

	public Arc() {
		this(200);
	}

	public void setDrawable(Drawable drawable) {
		if (!(drawable instanceof TransformDrawable)) {
			throw new IllegalArgumentException("The drawable should be an instance of TransformDrawable.");
		}
		this.drawable = (TransformDrawable)drawable;
	}

	public void setRadius(float radius) {
		this.radius = radius;
	}

	public float progress() {
		return progress.floatValue();
	}

	public int degrees() {
		return progress() >= 1 ? 360 : (int)((progress()*360)%360);
	}

	public void set(float value) {
		killTweenTarget(progress);
		progress.setValue(value);
	}

	public void reset() {
		set(0);
	}

	/** @param v value
	 * @param d duration
	 * @param eq equation */
	public void tween(float v, float d, TweenEquation eq) {
		$tween(v, d, eq).start(uiTweenMgr);
	}

	/** @param v value
	 * @param d duration */
	public void tween(float v, float d) {
		$tween(v, d).start(uiTweenMgr);
	}

	/** @param v value */
	public void tween(float v) {
		$tween(v).start(uiTweenMgr);
	}

	public void tween() {
		$tween().start(uiTweenMgr);
	}

	/** @param v value
	 * @param d duration
	 * @param eq equation */
	public Tween $tween(float v, float d, TweenEquation eq) {
		killTweenTarget(progress);
		return Tween.to(progress, 0, d).target(v).ease(eq);
	}

	/** @param v value
	 * @param d duration */
	public Tween $tween(float v, float d) {
		return $tween(v, d, Quart.OUT);
	}

	/** @param v value */
	public Tween $tween(float v) {
		return $tween(v, C_ID);
	}

	public Tween $tween() {
		return $tween(1);
	}

	/** @param v value */
	public Tween $set(float v) {
		return $tween(v, 0);
	}

	public Tween $reset() {
		return $set(0);
	}

	@Override public void draw(Batch batch, float parentAlpha) {
		if (progress() <= 0) {
			return;
		}
		validate();
		Color color = getColor();
		batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
		float x = getX() + radius;
		float y = getY() + radius;
		float width = drawable.getMinWidth();
		float height = drawable.getMinHeight();
		float scaleX = getScaleX() * (radius/height);
		float scaleY = getScaleY() * (radius/height);
		float rotation = getRotation();
		for (int i=0; i < degrees()/2; i++) {
			drawable.draw(batch, x, y, 0, 0, width, height, scaleX, scaleY, rotation - i*2);
		}
	}

	@Override public float getPrefWidth() {
		return radius * 2;
	}

	@Override public float getPrefHeight() {
		return radius * 2;
	}
}

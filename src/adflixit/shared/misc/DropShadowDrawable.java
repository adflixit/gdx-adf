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

import static adflixit.shared.BaseGame.*;
import static adflixit.shared.Util.*;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TransformDrawable;

/**
 * Draws any drawable with a shadow underneath it.
 * The shadow has to be a pre-made {@link TextureRegion}.
 */
public class DropShadowDrawable extends BaseDrawable implements TransformDrawable {
	private Drawable		drawable;
	private Drawable		shadow;
	private TransformDrawable	transDrawable;	// in case it is a TransformDrawable
	private TransformDrawable	transShadow;	// ditto
	private float			radius;		// shadow radius, should be specified manually
	private float			offset;		// shadow vertical offset

	public DropShadowDrawable(Drawable drawable, Drawable shadow, float radius, float offset) {
		this.drawable = drawable;
		if (drawable instanceof TransformDrawable) {
			transDrawable	= (TransformDrawable)drawable;
		}
		setShadow(shadow, radius, offset);
	}

	public DropShadowDrawable(Drawable drawable, Drawable shadow) {
		this(drawable, shadow, C_SHD_RAD, C_SHD_OFS);
	}

	public DropShadowDrawable(Skin skin, String drawable, String shadow, float radius, float offset) {
		this(skin.getDrawable(drawable), skin.getDrawable(shadow), radius, offset);
	}

	public DropShadowDrawable(Skin skin, String drawable, String shadow) {
		this(skin, drawable, shadow, C_SHD_RAD, C_SHD_OFS);
	}

	public DropShadowDrawable(Skin skin, String drawable, float radius, float offset) {
		this(skin, drawable, drawable+"_shd", radius, offset);
	}

	public DropShadowDrawable(Skin skin, String drawable) {
		this(skin, drawable, drawable+"_shd", C_SHD_RAD, C_SHD_OFS);
	}

	public DropShadowDrawable(String drawable, String shadow, float radius, float offset) {
		this(skin(), drawable, shadow, radius, offset);
	}

	public DropShadowDrawable(String drawable, String shadow) {
		this(skin(), drawable, shadow);
	}

	public DropShadowDrawable(String drawable, float radius, float offset) {
		this(skin(), drawable, radius, offset);
	}

	public DropShadowDrawable(String drawable) {
		this(skin(), drawable);
	}

	public DropShadowDrawable() {
		this((Drawable)null, (Drawable)null);
	}

	@Override public void draw(Batch batch, float x, float y, float originX, float originY, float width, float height, float scaleX, float scaleY, float rotation) {
		if (isTransformable()) {
			transShadow.draw(batch, x - radius/2, y - radius/2 - offset, originX + radius/2, originY + radius/2,
			width + radius, height + radius, scaleX, scaleY, rotation);
			transDrawable.draw(batch, x, y, originX, originY, width, height, scaleX, scaleY, rotation);
		} else {
			if (shadow != null) {
				shadow.draw(batch, x - radius/2, y - radius/2 - offset, width + radius, height + radius);
			}
			if (drawable != null) {
				drawable.draw(batch, x, y, width, height);
			}
		}
	}

	@Override public void draw(Batch batch, float x, float y, float width, float height) {
		draw(batch, x, y, 0, 0, width, height, 1, 1, 0);
	}

	private boolean isTransformable() {
		return transDrawable != null && transShadow != null;
	}

	public Drawable getShadow() {
		return shadow;
	}

	public void setShadow(Drawable shadow, float radius, float offset) {
		if (this.shadow != shadow) {
			this.shadow = shadow;
			if (shadow instanceof TransformDrawable) {
				transShadow = (TransformDrawable)shadow;
			}
		}
		this.radius = radius;
		this.offset = offset;
	}

	public void setShadow(Drawable shadow) {
		setShadow(shadow, radius, offset);
	}

	@Override public float getLeftWidth() {
		return drawable.getLeftWidth();
	}

	@Override public void setLeftWidth(float leftWidth) {
		drawable.setLeftWidth(leftWidth);
		shadow.setLeftWidth(leftWidth + radius/2);
	}

	@Override public float getRightWidth() {
		return drawable.getRightWidth();
	}

	@Override public void setRightWidth(float rightWidth) {
		drawable.setRightWidth(rightWidth);
		shadow.setRightWidth(rightWidth + radius/2);
	}

	@Override public float getTopHeight() {
		return drawable.getTopHeight();
	}

	@Override public void setTopHeight(float topHeight) {
		drawable.setTopHeight(topHeight);
		shadow.setTopHeight(topHeight + radius/2);
	}

	@Override public float getBottomHeight() {
		return drawable.getBottomHeight();
	}

	@Override public void setBottomHeight(float bottomHeight) {
		drawable.setBottomHeight(bottomHeight);
		shadow.setBottomHeight(bottomHeight + radius/2);
	}

	@Override public float getMinWidth() {
		return drawable.getMinWidth();
	}

	@Override public void setMinWidth(float minWidth) {
		drawable.setMinWidth(minWidth);
		shadow.setMinWidth(minWidth + radius);
	}

	@Override public float getMinHeight() {
		return drawable.getMinHeight();
	}

	@Override public void setMinHeight(float minHeight) {
		drawable.setMinHeight(minHeight);
		shadow.setMinHeight(minHeight + radius);
	}
}

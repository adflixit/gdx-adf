package adflixit.shared.misc;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TransformDrawable;

/**
 * Wrapping for an exiting {@link NinePatch} that can only be scaled horizontally to preserve the roundness.
 */
public class RoundedDrawable extends BaseDrawable implements TransformDrawable {
	private NinePatch patch;

	public RoundedDrawable() {}

	public RoundedDrawable(NinePatch patch) {
		setPatch(patch);
	}
	
	public RoundedDrawable(NinePatchDrawable drawable) {
		super(drawable);
		setPatch(drawable.getPatch());
	}

	public RoundedDrawable(RoundedDrawable drawable) {
		super(drawable);
		setPatch(drawable.patch);
	}

	@Override
	public void draw(Batch batch, float x, float y, float width, float height) {
		float h = getMinHeight();
		patch.draw(batch, x, y + (height-h)/2, width, h);
	}

	@Override
	public void draw(Batch batch, float x, float y, float originX, float originY, float width, float height, float scaleX, float scaleY, float rotation) {
		float h = getMinHeight();
		patch.draw(batch, x, y + (height-h)/2, originX, originY + (height-h)/2, width, h, scaleX, scaleY, rotation);
	}

	public void setPatch(NinePatch patch) {
		this.patch = patch;
		setMinWidth(patch.getTotalWidth());
		setMinHeight(patch.getTotalHeight());
		setTopHeight(patch.getPadTop());
		setRightWidth(patch.getPadRight());
		setBottomHeight(patch.getPadBottom());
		setLeftWidth(patch.getPadLeft());
	}

	public NinePatch getPatch() {
		return patch;
	}

	public RoundedDrawable tint(Color tint) {
		RoundedDrawable drawable = new RoundedDrawable(this);
		drawable.setPatch(new NinePatch(drawable.getPatch(), tint));
		return drawable;
	}
}

package adflixit.shared;

import static adflixit.shared.BaseGame.*;
import static adflixit.shared.TweenUtils.*;
import static aurelienribon.tweenengine.TweenCallback.*;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Quart;
import aurelienribon.tweenengine.primitives.MutableFloat;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

/**
 * Performs a two-pass Gaussian blur.
 */
public class Blur extends ScreenComponent<BaseScreen<?>> {
	private final String			uniName				= "u_blur";
	private ShaderProgram			firstPass;
	private ShaderProgram			lastPass;
	private FrameBuffer				firstFrameBuffer;
	private FrameBuffer				lastFrameBuffer;
	private int						passes;				// number of the blurring cycles
	private final MutableFloat		amount				= new MutableFloat(0);
	/** Pass is used to access the route to update the shader info.
	 * Schedule is used to update the info one time after which it's being reset. */
	private boolean					pass, scheduled;
	private final TweenCallback		passCallback		= (type, source) -> {
		if (type==BEGIN) {
			unlock();
		} else {
			lock();
		}
	};
	private final TweenCallback		scheduleCallback	= (type, source) -> schedule();
	
	public Blur(BaseScreen<?> screen, int passes, FileHandle hvert, FileHandle hfrag, FileHandle vvert, FileHandle vfrag) {
		super(screen);
		setPasses(passes);
		load(hvert, hfrag, vvert, vfrag);
	}

	public Blur(BaseScreen<?> screen, int passes, String hvert, String hfrag, String vvert, String vfrag) {
		this(screen, passes, internalFile(hvert), internalFile(hfrag), internalFile(vvert), internalFile(vfrag));
	}

	public Blur(BaseScreen<?> screen, int passes) {
		this(screen, passes, "shared/data/hblur.vert", "shared/data/blur.frag", "shared/data/vblur.vert", "shared/data/blur.frag");
	}

	public Blur(BaseScreen<?> screen) {
		this(screen, 1);
	}
	
	public void load(FileHandle hvert, FileHandle hfrag, FileHandle vvert, FileHandle vfrag) {
		firstPass = new ShaderProgram(hvert, hfrag);
		lastPass = new ShaderProgram(vvert, vfrag);
	}

	public void load(String hvert, String hfrag, String vvert, String vfrag) {
		firstPass = new ShaderProgram(internalFile(hvert), internalFile(hfrag));
		lastPass = new ShaderProgram(internalFile(vvert), internalFile(vfrag));
	}

	public void setPasses(int i) {
		passes = i;
	}

	public void reset() {
		resetAmount();
		lock();
		unschedule();
	}

	public void draw() {
		Texture tex;
		float x = scr.cameraXAtZero(), y = scr.cameraYAtZero();
		for (int i=0; i < passes; i++) {
			pass(i, x, y);
		}
		bat.setShader(null);
		bat.begin();
			tex = lastFrameBuffer.getColorBufferTexture();
			bat.draw(tex, x, y, scr.screenWidth(), scr.screenHeight(), 0,0,1,1);
		bat.end();
		if (scheduled) {
			unschedule();
		}
	}

	public Texture inputTex() {
		return scr.screenTex();
	}

	/** Performs the blurring routine.
	 * @param i iterations
	 * @param x result drawing x
	 * @param y result drawing y */
	private void pass(int i, float x, float y) {
		Texture tex;
		// horizontal pass
		bat.setShader(firstPass);
		firstFrameBuffer.begin();
		bat.begin();
			if (pass || scheduled) {
				firstPass.setUniformf(uniName, amount());
			}
			tex = i > 0 ? lastFrameBuffer.getColorBufferTexture() : inputTex();
			bat.draw(tex, x, y, scr.screenWidth(), scr.screenHeight());
		bat.end();
		firstFrameBuffer.end();
		// vertical pass
		bat.setShader(lastPass);
		lastFrameBuffer.begin();
		bat.begin();
			if (pass || scheduled) {
				lastPass.setUniformf(uniName, amount());
			}
			tex = firstFrameBuffer.getColorBufferTexture();
			bat.draw(tex, x, y, scr.screenWidth(), scr.screenHeight());
		bat.end();
		lastFrameBuffer.end();
	}

	/** Locks the shader update route. */
	private void lock() {
		pass = false;
	}

	/** Unlocks the shader update route. */
	private void unlock() {
		pass = true;
	}

	/** Schedules a one-time access to the update route. */
	private void schedule() {
		scheduled = true;
	}

	/** Resets the one-time access to the update route. */
	private void unschedule() {
		scheduled = false;
	}

	public boolean isActive() {
		return amount() > 0;	
	}

	public float amount() {
		return amount.floatValue();
	}

	public void setAmount(float v) {
		schedule();
		killTweenTarget(amount);
		amount.setValue(v);
	}

	public void resetAmount() {
		setAmount(0);
	}

	/** @param v value
	 * @param d duration */
	public Tween $tween(float v, float d) {
		killTweenTarget(amount);
		return Tween.to(amount, 0, d).target(v).ease(Quart.OUT)
				.setCallback(passCallback)
				.setCallbackTriggers(BEGIN|COMPLETE);
	}

	/** @param v value
	 * @param d duration */
	public Tween $tweenOut(float d) {
		return $tween(0, d);
	}

	/** @param v value */
	public Tween $setAmount(float v) {
		killTweenTarget(amount);
		return Tween.set(amount, 0).target(v).setCallback(scheduleCallback);
	}

	public Tween $resetAmount() {
		return $setAmount(0);
	}

	public void dispose() {
		firstPass.dispose();
		lastPass.dispose();
		firstFrameBuffer.dispose();
		lastFrameBuffer.dispose();
	}

	public void resize() {
		if (firstResize) {
			firstResize = false;
		} else {
			firstFrameBuffer.dispose();
			lastFrameBuffer.dispose();
		}
		firstFrameBuffer = new FrameBuffer(Format.RGB888, scr.frameBufferWidth(), scr.frameBufferHeight(), false);
		lastFrameBuffer = new FrameBuffer(Format.RGB888, scr.frameBufferWidth(), scr.frameBufferHeight(), false);
	}
}

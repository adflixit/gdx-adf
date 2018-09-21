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

import static adflixit.shared.BaseGame.*;
import static adflixit.shared.Defs.*;
import static adflixit.shared.TweenUtils.*;
import static adflixit.shared.Util.*;
import static aurelienribon.tweenengine.TweenCallback.*;
import static com.badlogic.gdx.graphics.Color.WHITE;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Quart;
import aurelienribon.tweenengine.primitives.MutableFloat;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import java.util.ArrayList;
import java.util.List;

/**
 * Includes:
 * <ul>
 * <li>Three levels of {@link TweenManager}:<ul><li>General</li><li>Timescaled</li><li>UI</li></ul></li>
 * <li>Adaptive viewport adjustments.</li>
 * <li>UI layers:<ul><li>Overlay</li><li>Game</li><li>Menus</li></ul></li>
 * <li>Benchmark.</li>
 * <li>Graphics and postprocessing adjustments.</li>
 * <li>Blur.</li>
 * <li>Tweening handlers:<ul><li>Timescale</li><li>Sound volume</li><li>Camera</li><li>UI layers</li><li>Overlay</li><li>Blur</li></ul></li>
 * </ul>
 * @param <G> a {@link BaseGame} instance.
 */
public abstract class BaseScreen<G extends BaseGame> extends Logger implements InputProcessor, GestureListener {
	public static final TweenManager		tweenMgr			= new TweenManager();	// general tween manager
	public static final TweenManager		tscTweenMgr			= new TweenManager();	// timescaled tween manager
	public static final TweenManager		uiTweenMgr			= new TweenManager();	// UI tween manager

	public static final Updater				updater				= new Updater();

	public static void addUpdatable(Updatable a) {
		updater.add(a);
	}

	public static void removeUpdatable(Updatable a) {
		updater.remove(a);
	}

	// The screen lesser dimension which determines the larger dimension by scaling it by the aspect ratio.
	// It can only be changed once during lifetime of the app, preferably at the very start.
	private static float					ldm					= 1080;
	private static boolean					ldmDeadlock;

	/** Changes the screen lesser dimension once during the runtime. */
	public static void setLdm(float v) {
		if (!ldmDeadlock) {
			glog("Setting the screen lesser dimension to "+v);
			ldmDeadlock = true;		
			ldm = v;
		}
	}

	/** @return lesser screen dimension. */
	public static float ldm() {
		return ldm;
	}

	// UI layers
	public static final int					// indices
											UIL_OVERLAY			= 0,
											UIL_GAME			= 1,
											UIL_MENUS			= 2,
											UIL_LENGTH			= 3,
											// flags
											UIL_OVERLAY_F		= 1<<0,
											UIL_GAME_F			= 1<<1,
											UIL_MENUS_F			= 1<<2,
											UIL_ALL				= sumFlags(UIL_MENUS_F),
											UIL_GENERAL			= UIL_GAME_F | UIL_MENUS_F;

	/** Benchmarking events. */
	public static interface BenchmarkCallback {
		public void start();
		public void finish();
	}

	// Benchmark is used to determine whether the device is capable of using the advanced performance features such as postprocessing.
	public static final String				benchmarkKey		= "benchmark";
	public static final float				benchmarkDuration	= 3;	// duration of benchmarking in seconds
	private static boolean					benchmarked;		// assigned whether when the benchmark info is loaded or when benchmarking is done
	private static BenchmarkCallback		benchmarkCallback;	// called whether benchmarking started of finished
	private static boolean					benchmarkTesting;	// whether it is running or not
	private static float					benchmarkTime;		// relative time spent on benchmarking
	private static int						benchmarkFrames;	// number of frames rendered during benchmarking
	private static float					benchmarkFps;		// FPS during the benchmarking

	public static void loadBenchmarkInfo() {
		if (prefsContain(benchmarkKey)) {
			benchmarked = true;
			setAdvancedPerformance(prefB(benchmarkKey));
		}
	}

	// Indicates whether the app has to run without any advanced graphical features, such as both postprocessing and texture filtering other than nearest.
	private static boolean					simpleGraphics;
	private static boolean					advancedPerformance;		// indicates whether the app may use advanced performance features, such as a GPU
	public static final int					resDenom			= 4;	// resolution denominator used by the postprocessing resource economy
	public static final TextureFilter		textureFilterHq		= TextureFilter.Linear,
											textureFilterLq		= TextureFilter.Nearest;

	private static void setAdvancedPerformance(boolean v) {
		glog("Advanced performance "+(v?"enabled":"disabled"));
		advancedPerformance = v;
	}

	protected G								game;
	protected final SpriteBatch				batch				= new SpriteBatch();
	protected final OrthographicCamera		camera				= new OrthographicCamera();
	protected final Vector3					camBasePos			= new Vector3();
	protected final AdaptiveViewport		viewport			= new AdaptiveViewport(camera);
	protected final ShapeRenderer			shapeRenderer		= new ShapeRenderer();
	protected final AdaptiveViewport		uiViewport			= new AdaptiveViewport();
	protected final Stage					ui					= new Stage(uiViewport);
	protected final Group[]					uiLayers			= {new Group(), new Group(), new Group()};
	protected final Overlay					overlay				= new Overlay(this);
	//protected final Postprocessor			postprocessor		= new Postprocessor(this);
	protected final Blur					blur				= new Blur(this);
	protected final Tapper					tapper				= new Tapper(this);
	protected final Timestamp				timestamp			= new Timestamp();
	protected final InputMultiplexer		inputMultiplexer	= new InputMultiplexer();
	protected final Vector2					touchPoint			= new Vector2();
	protected FrameBuffer					frameBuffer;

	private final MutableFloat				camShake			= new MutableFloat(0);	// current camera shake amplitude
	private final MutableFloat				timescale			= new MutableFloat(1);	// time multiplier
	private final MutableFloat				uiTimescale			= new MutableFloat(1);
	private final MutableFloat				masterVolume		= new MutableFloat(1);	// sound volume
	private final MutableFloat				sfxVolume			= new MutableFloat(1);	// sound effects volume
	private final MutableFloat				musicVolume			= new MutableFloat(1);
	private final List<Music>				musicList			= new ArrayList<>();	// music volume has to be manually update on a transition
	private boolean							updatingVolume;
	private final TweenCallback				masterVolumeCallback = (type, source) -> updatingVolume = type==BEGIN;

	// Temporal values
	protected final Vector2					tmpv2				= new Vector2();
	protected final Vector3					tmpv3				= new Vector3();
	protected final Color					tmpclr				= new Color();

	// Last screen size info is stored in two different fields switching after every other resize
	private boolean							lastScreenSizeJunc;
	private final Vector2					evenLastScreenSize	= new Vector2();
	private final Vector2					oddLastScreenSize	= new Vector2();
	private boolean							firstResize			= true;

	private final Color						black				= new Color(0x0f1114ff);

	public BaseScreen(G game) {
		this();
		this.game = game;
	}
	
	protected BaseScreen() {
		ui.setViewport(uiViewport);
		// add all UI layers to the stage
		for (Group layer : uiLayers) {
			ui.addActor(layer);
		}
		// overlay shouldn't receive input events
		uiLayers[UIL_OVERLAY].setTouchable(Touchable.disabled);
		timestamp.set();
		// stack all input listeners
		inputMultiplexer.addProcessor(ui);
		inputMultiplexer.addProcessor(this);
		inputMultiplexer.addProcessor(new GestureDetector(this));
		evenLastScreenSize.set(screenWidth(), screenHeight());
		oddLastScreenSize.set(screenWidth(), screenHeight());
	}

	protected boolean isBenchmarked() {
		return benchmarked;
	}

	/** Prepares and starts a benchmark. */
	protected void initBenchmark(BenchmarkCallback callback) {
		benchmarkCallback = callback;
		startBenchmark();
	}

	private void startBenchmark() {
		log("Benchmarking");
		benchmarkTesting = true;
		benchmarkTime = 0;
		benchmarkFrames = 0;
		benchmarkFps = 0;
		benchmarkCallback.start();
	}

	private void finishBenchmark() {
		log("Benchmark finished, fps = "+benchmarkFps);
		benchmarkTesting = false;
		boolean result = benchmarkFps/benchmarkFrames > 50;
		setAdvancedPerformance(result);
		// saving info
		putPrefB(benchmarkKey, result);
		flushPrefs();
		benchmarkCallback.finish();
	}

	private void updateBenchmark() {
		if (benchmarkTesting) {
			benchmarkFrames++;
			benchmarkFps += getFps();
			benchmarkTime += dt();
			if (benchmarkTime >= benchmarkDuration) {
				finishBenchmark();
			}
		}
	}

	/** Disables advanced features and changes the texture atlases' filter to nearest. */
	protected void enableSimpleGraphics() {
		logSetup("Enabling simple graphics");
		simpleGraphics = true;
		for (Texture tex : skin().getAtlas().getTextures()) {
			tex.setFilter(textureFilterLq, textureFilterLq);
		}
		logDone();
	}

	/** Disables the advanced feature limitation and changes the texture atlases' filter to linear. */
	protected void disableSimpleGraphics() {
		logSetup("Disabling simple graphics");
		simpleGraphics = false;
		for (Texture tex : skin().getAtlas().getTextures()) {
			tex.setFilter(textureFilterHq, textureFilterHq);
		}
		logDone();
	}

	/** @return the frame buffer texture. */
	public Texture fbTex() {
		return frameBuffer.getColorBufferTexture();
	}

	/** @return the whole screen capture texture. */
	public Texture screenTex() {
		return fbTex();
	}
	
	/** Adds actor to the UI layer specified by index. */
	public void addToUiLayer(int i, Actor a) {
		uiLayers[i].addActor(a);
	}

	/** @return the last screen size, i.e. screen size before the last resize. */
	public Vector2 lastScreenSize() {
		return lastScreenSizeJunc ? evenLastScreenSize : oddLastScreenSize;
	}

	/** Switches debug mode. */
	public void toggleDebug() {
		log("Debug mode enabled = "+(debug?"enabled":"disabled"));
		debug = !debug;	
	}

	/** @return x and y of the screen center. */
	public Vector2 screenCenter() {
		return tmpv2.set(screenWidth()/2, screenHeight()/2);
	}

	public float screenCenterX() {
		return screenWidth()/2;
	}

	public float screenCenterY() {
		return screenHeight()/2;
	}

	/** @return arguments converted from physical screen units into virtual screen units. */
	public Vector2 unproject(float x, float y) {
		return viewport.unproject(tmpv2.set(x, y));
	}

	/** Changes this screen to another.
	 * @param dispose should this screen be disposed. */
	public void gotoScreen(BaseScreen<?> screen, boolean dispose) {
		game.setScreen(screen, dispose);
	}

	public void gotoScreen(BaseScreen<?> screen) {
		game.setScreen(screen, true);
	}

	public float screenWidth() {
		return landscapeOrientation() ? ldm * screenRatio() : ldm;
	}

	public float screenHeight() {
		return !landscapeOrientation() ? ldm * inverseScreenRatio() : ldm;
	}

	public int screenWidthI() {
		return (int)screenWidth();
	}

	public int screenHeightI() {
		return (int)screenHeight();
	}

	public int frameBufferWidth() {
		return screenWidthI() / resDenom;
	}

	public int frameBufferHeight() {
		return screenHeightI() / resDenom;
	}

	/** @return physical screen width. */
	public int outputWidth() {
		return Gdx.graphics.getWidth();
	}

	/** @return physical screen height. */
	public int outputHeight() {
		return Gdx.graphics.getHeight();
	}

	/** @return relation of screen width to screen height. */
	public float screenRatio() {
		return (float)outputWidth() / outputHeight();
	}

	/** @return relation of screen height to screen width. */
	public float inverseScreenRatio() {
		return (float)outputHeight() / outputWidth();
	}

	/** @return is screen width greater than screen height. */
	public boolean landscapeOrientation() {
		return screenRatio() > 1;
	}

	/** @return screen width multiplied by {@code a}. */
	public float screenXm(float a) {
		return screenWidth() * a;
	}

	/** @return screen height multiplied by {@code a}. */
	public float screenYm(float a) {
		return screenHeight() * a;
	}

	/** @return {@code a} divided by screen width. */
	public float screenXr(float a) {
		return a / screenWidth();
	}

	/** @return {@code a} divided by screen height. */
	public float screenYr(float a) {
		return a / screenHeight();
	}

	/** @return output units converted to screen units. */
	public float toScreenUnits(float a) {
		return a * (screenWidth() / outputWidth());
	}

	/** @return screen units converted to output units. */
	public float toOutputUnits(float a) {
		return a * (outputWidth() / screenWidth());
	}

	/** Shows the ad banner with the specified animation duration. */
	protected void showAd(float d) {
		/*if (Gdx.app.getType()==ApplicationType.Android) {
			AdView av = getAdView();
			killTarget(av);
			Tween.to(av, AndViewAccessor.Y, d)
			.targetRelative(av.getTop() > av.getBottom() ? -av.getHeight() : av.getHeight()).ease(Quart.OUT)
			.start(tweenMgr);
		}*/
	}

	/** Shows the ad banner with fixed animation duration. */
	protected void showAd() {
		showAd(C_D);
	}

	/** Hides the ad banner with the specified animation duration. */
	protected void hideAd(float d) {
		/*if (Gdx.app.getType()==ApplicationType.Android) {
			AdView av = getAdView();
			killTarget(av);
			Tween.to(av, AndViewAccessor.Y, d)
			.targetRelative(av.getTop() > av.getBottom() ? av.getHeight() : -av.getHeight()).ease(Quart.OUT)
			.start(tweenMgr);
		}*/
	}

	/** Hides the ad banner with fixed animation duration. */
	protected void hideAd() {
		hideAd(C_D);
	}

	/** A contextual action, such as getting back to the main menu, opening the in-game menu, returning to the parent section, etc. */
	public abstract void goBack();

	/** Prints the debug info. */
	public void printDebug() {
		log("screen size = "+screenWidth()+" "+screenHeight()+
		", fps = "+getFps()+
		", cam = "+cameraPos().x+" "+cameraPos().y+
		", cam at zero = "+cameraXAtZero()+" "+cameraYAtZero()+
		", blur = "+blur.amount()+
		", time = "+timestamp.elapsed()+
		", cam shake = "+camShake()+
		", timescale = "+timescale()+
		", ui timescale = "+uiTimescale()+
		", master volume = "+masterVolume()+
		", sfx volume = "+sfxVolume()+
		", music volume = "+musicVolume());
	}

	/** @return camera shake amplitude. */
	public float camShake() {
		return camShake.floatValue();
	}

	/** Tweens the camera shake amplitude.
	 * @param v value
	 * @param d duration */
	public void tweenCamShake(float v, float d) {
		$tweenCamShake(v, d).start(tweenMgr);
	}

	/** Tweens the camera shake amplitude.
	 * @param v value */
	public void tweenCamShake(float v) {
		$tweenCamShake(v).start(tweenMgr);
	}

	/** Tweens the camera shake amplitude to 1. */
	public void tweenCamShakeIn() {
		$tweenCamShakeIn().start(tweenMgr);
	}

	/** Tweens the camera shake amplitude to 0.
	 * @param d duration */
	public void tweenCamShakeOut(float d) {
		$tweenCamShakeOut(d).start(tweenMgr);
	}

	/** Tweens the camera shake amplitude to 0. */
	public void tweenCamShakeOut() {
		$tweenCamShakeOut().start(tweenMgr);
	}

	/** Sets the camera shake amplitude.
	 * @param v value */
	public void setCamShake(float v) {
		killTweenTarget(camShake);
		camShake.setValue(v);
	}

	/** Sets the camera shake amplitude to 1. */
	public void setCamShake() {
		setCamShake(1);
	}

	/** Sets the camera shake amplitude to 0. */
	public void resetCamShake() {
		setCamShake(0);
	}

	/** @return timescale multiplier. */
	public float timescale() {
		return timescale.floatValue();
	}

	/** @return time delta multiplied by the timescale. */
	public float dtm() {
		return dt() * timescale();
	}

	/** Tweens the timescale multiplier.
	 * @param v value
	 * @param d duration */
	public void tweenTimescale(float v, float d) {
		$tweenTimescale(v, d).start(tweenMgr);
	}

	/** Tweens the timescale multiplier.
	 * @param v value */
	public void tweenTimescale(float v) {
		$tweenTimescale(v).start(tweenMgr);
	}

	/** Tweens the timescale multiplier to 1.
	 * @param d duration */
	public void tweenTimescaleIn(float d) {
		$tweenTimescaleIn(d).start(tweenMgr);
	}

	/** Tweens the timescale multiplier to 1. */
	public void tweenTimescaleIn() {
		$tweenTimescaleIn().start(tweenMgr);
	}

	/** Tweens the timescale multiplier to 0.
	 * @param d duration */
	public void tweenTimescaleOut(float d) {
		$tweenTimescaleOut(d).start(tweenMgr);
	}

	/** Tweens the timescale multiplier to 0. */
	public void tweenTimescaleOut() {
		$tweenTimescaleOut().start(tweenMgr);
	}

	/** Sets the timescale multiplier.
	 * @param v value */
	public void setTimescale(float v) {
		killTweenTarget(timescale);
		timescale.setValue(v);
	}

	/** Sets the timescale multiplier to 1. */
	public void setTimescale() {
		setTimescale(1);
	}

	/** Sets the timescale multiplier to 0. */
	public void resetTimescale() {
		setTimescale(0);
	}

	/** @return UI timescale multiplier. */
	public float uiTimescale() {
		return uiTimescale.floatValue();
	}

	/** @return time delta multiplied by the UI timescale. */
	public float uiMdt() {
		return dt() * uiTimescale();
	}

	/** Tweens the UI timescale multiplier.
	 * @param v value
	 * @param d duration */
	public void tweenUiTimescale(float v, float d) {
		$tweenUiTimescale(v, d).start(tweenMgr);
	}

	/** Tweens the UI timescale multiplier.
	 * @param v value */
	public void tweenUiTimescale(float v) {
		$tweenUiTimescale(v).start(tweenMgr);
	}

	/** Tweens the UI timescale multiplier to 1. */
	public void tweenUiTimescaleIn() {
		$tweenUiTimescaleIn().start(tweenMgr);
	}

	/** Tweens the UI timescale multiplier to 0.
	 * @param d duration */
	public void tweenUiTimescaleOut(float d) {
		$tweenUiTimescaleOut(d).start(tweenMgr);
	}

	/** Tweens the UI timescale multiplier to 0. */
	public void tweenUiTimescaleOut() {
		$tweenUiTimescaleOut().start(tweenMgr);
	}

	/** Sets the UI timescale multiplier.
	 * @param v value */
	public void setUiTimescale(float v) {
		killTweenTarget(uiTimescale);
		uiTimescale.setValue(v);
	}

	/** Sets the UI timescale multiplier to 1. */
	public void setUiTimescale() {
		setUiTimescale(1);
	}

	/** Sets the UI timescale multiplier to 0. */
	public void resetUITimescale() {
		setUiTimescale(0);
	}

	/** Adds {@link Music} to a list for the further management.
	 * All music has to be registered. */
	public void registerMusic(Music music) {
		music.setVolume(musicVolume());
		musicList.add(music);
	}

	/** Adds multiple {@link Music} to a list for the further management.
	 * All music has to be registered. */
	public void registerMusic(Music... music) {
		for (Music m : music) {
			registerMusic(m);
		};
	}

	public float masterVolume() {
		return masterVolume.floatValue();
	}

	/** Tweens the master volume.
	 * @param v value
	 * @param d duration */
	public void tweenMasterVolume(float v, float d) {
		$tweenMasterVolume(v, d).start(tweenMgr);
	}

	/** Tweens the master volume.
	 * @param v value */
	public void tweenMasterVolume(float v) {
		$tweenMasterVolume(v).start(tweenMgr);
	}

	/** Tweens the master volume to 1. */
	public void tweenMasterVolumeIn() {
		$tweenMasterVolumeIn().start(tweenMgr);
	}

	/** Tweens the master volume to 0.
	 * @param d duration */
	public void tweenMasterVolumeOut(float d) {
		$tweenMasterVolumeOut(d).start(tweenMgr);
	}

	/** Tweens the master volume to 0. */
	public void tweenMasterVolumeOut() {
		$tweenMasterVolumeOut().start(tweenMgr);
	}

	/** Sets the master volume.
	 * @param v value */
	public void setMasterVolume(float v) {
		killTweenTarget(masterVolume);
		masterVolume.setValue(v);
	}

	/** Sets the master volume to 1. */
	public void setMasterVolume() {
		setMasterVolume(1);
	}

	/** Sets the master volume to 0. */
	public void resetMasterVolume() {
		setMasterVolume(0);
	}

	/** @return sound effects volume. */
	public float sfxVolume() {
		return sfxVolume.floatValue() * masterVolume();
	}

	/** Tweens the sound effects volume.
	 * @param v value
	 * @param d duration */
	public void tweenSfxVolume(float v, float d) {
		$tweenSfxVolume(v, d).start(tweenMgr);
	}

	/** Tweens the sound effects volume.
	 * @param v value */
	public void tweenSfxVolume(float v) {
		$tweenSfxVolume(v).start(tweenMgr);
	}

	/** Tweens the sound effects volume to 1. */
	public void tweenSfxVolumeIn() {
		$tweenSfxVolumeIn().start(tweenMgr);
	}

	/** Tweens the sound effects volume to 0.
	 * @param d duration */
	public void tweenSfxVolumeOut(float d) {
		$tweenSfxVolumeOut(d).start(tweenMgr);
	}

	/** Tweens the sound effects volume to 0. */
	public void tweenSfxVolumeOut() {
		$tweenSfxVolumeOut().start(tweenMgr);
	}

	/** Sets the sound effects volume.
	 * @param v value */
	public void setSfxVolume(float v) {
		killTweenTarget(sfxVolume);
		sfxVolume.setValue(v);
	}

	/** Sets the sound effects volume to 1. */
	public void setSfxVolume() {
		setSfxVolume(1);
	}

	/** Sets the sound effects volume to 0. */
	public void resetSfxVolume() {
		setSfxVolume(0);
	}

	public float musicVolume() {
		return musicVolume.floatValue() * masterVolume();
	}

	/** Fades the music volume.
	 * @param v value
	 * @param d duration */
	public void tweenMusicVolume(float v, float d) {
		$tweenMusicVolume(v, d).start(tweenMgr);
	}

	/** Fades the music volume.
	 * @param v value */
	public void tweenMusicVolume(float v) {
		$tweenMusicVolume(v).start(tweenMgr);
	}

	/** Fades the music volume to 1. */
	public void tweenMusicVolumeIn() {
		$tweenMusicVolumeIn().start(tweenMgr);
	}

	/** Fades the music volume to 0.
	 * @param d duration */
	public void tweenMusicVolumeOut(float d) {
		$tweenMusicVolumeOut(d).start(tweenMgr);
	}

	/** Fades the music volume to 0. */
	public void tweenMusicVolumeOut() {
		$tweenMusicVolumeOut().start(tweenMgr);
	}

	/** Sets the music volume.
	 * @param v value */
	public void setMusicVolume(float v) {
		killTweenTarget(musicVolume);
		musicVolume.setValue(v);
	}

	/** Sets the music volume to 1. */
	public void setMusicVolume() {
		setMusicVolume(1);
	}

	/** Sets the music volume to 0. */
	public void resetMusicVolume() {
		setMusicVolume(0);
	}

	/** @return camera x, y, z at the center. */
	public Vector3 cameraPos() {
		return camBasePos;
	}

	/** @return camera x at the center. */
	public float cameraX() {
		return cameraPos().x;
	}

	/** @return camera y at the center. */
	public float cameraY() {
		return cameraPos().y;
	}

	/** @return x of the bottom left corner of the camera frustum. */
	public float cameraXAtZero() {
		return cameraPos().x - screenCenterX();
	}

	/** @return y of the bottom left corner of the camera frustum. */
	public float cameraYAtZero() {
		return cameraPos().y - screenCenterY();
	}

	/** @return x, y of the bottom left corner of the camera frustum. */
	public Vector2 cameraAtZero() {
		return tmpv2.set(cameraXAtZero(), cameraYAtZero());
	}

	/** Sets camera x at the center. */
	public void setCameraX(float x) {
		killTweenTarget(camera, OrthoCameraAccessor.X);
		cameraPos().x = x;
	}

	/** Sets camera x at the center. */
	public void setCameraY(float y) {
		// stop all animations related to this
		killTweenTarget(camera, OrthoCameraAccessor.Y);
		cameraPos().y = y;
	}

	/** Sets camera x, y at the center. */
	public void setCameraPos(float x, float y) {
		killTweenTarget(camera, OrthoCameraAccessor.POS);
		setCameraX(x);
		setCameraY(y);
	}

	/** Sets camera x, y at the center. */
	public void setCameraPos(Vector2 pos) {
		setCameraPos(pos.x, pos.y);
	}

	/** Sets camera camera x at the bottom left corner of the frustum. */
	public void setCameraXAtZero(float x) {
		setCameraX(x - screenCenterX());
	}

	/** Sets camera camera y at the bottom left corner of the frustum. */
	public void setCameraYAtZero(float y) {
		setCameraY(y - screenCenterY());
	}

	/** Sets camera camera x, y at the bottom left corner of the frustum. */
	public void setCameraAtZero(float x, float y) {
		setCameraPos(x - screenCenterX(), y - screenCenterY());
	}

	/** Sets camera camera x, y at the bottom left corner of the frustum. */
	public void setCameraAtZero(Vector2 pos) {
		setCameraAtZero(pos.x, pos.y);
	}

	/** Slides camera in the position.
	 * @param d duration */
	public void moveCamera(float x, float y, float d) {
		$moveCamera(x, y, d).start(tweenMgr);
	}

	/** Slides camera in the position. */
	public void moveCamera(float x, float y) {
		$moveCamera(x, y).start(tweenMgr);
	}

	/** Tweens the camera zoom.
	 * @param z zoom
	 * @param d duration */
	public void zoomCamera(float z, float d) {
		$zoomCamera(z, d).start(tweenMgr);
	}

	/** Tweens the camera zoom.
	 * @param z zoom */
	public void zoomCamera(float z) {
		$zoomCamera(z).start(tweenMgr);
	}

	/** Tweens the camera zoom out.
	 * @param d duration */
	public void zoomCameraOut(float d) {
		$zoomCameraOut(d).start(tweenMgr);
	}

	/** Tweens the camera zoom out. */
	public void zoomCameraOut() {
		$zoomCameraOut().start(tweenMgr);
	}

	/** Tweens the UI opacity.
	 * @param v value
	 * @param d duration */
	public void fadeUi(float v, float d) {
		$fadeUi(v, d).start(tweenMgr);
	}

	/** Tweens the UI opacity to 1.
	 * @param d duration */
	public void fadeUiIn(float d) {
		$fadeUiIn(d).start(tweenMgr);
	}

	/** Tweens the UI opacity to 1. */
	public void fadeUiIn() {
		$fadeUiIn(C_D).start(tweenMgr);
	}

	/** Tweens the UI opacity to 0.
	 * @param d duration */
	public void fadeUiOut(float d) {
		$fadeUiOut(d).start(tweenMgr);
	}

	/** Tweens the UI opacity to 0. */
	public void fadeUiOut() {
		$fadeUiOut(C_D).start(tweenMgr);
	}

	/** Tweens the specified UI layers' opacity.
	 * @param fl flags
	 * @param v value
	 * @param d duration */
	public void fadeUiLayers(int fl, float v, float d) {
		$fadeUiLayers(fl, v, d).start(tweenMgr);
	}

	/** Tweens the specified UI layers' opacity.
	 * @param fl flags
	 * @param v value */
	public void fadeUiLayers(int fl, float v) {
		$fadeUiLayers(fl, v, C_D).start(tweenMgr);
	}

	/** Tweens the specified UI layers' opacity to 1.
	 * @param fl flags */
	public void fadeUiLayersIn(int fl) {
		$fadeUiLayersIn(fl, 1).start(tweenMgr);
	}

	/** Tweens the specified UI layers' opacity to 0.
	 * @param fl flags
	 * @param d duration */
	public void fadeUiLayersOut(int fl, float d) {
		$fadeUiLayers(fl, 0, d).start(tweenMgr);
	}

	/** Tweens the specified UI layers' opacity to 0.
	 * @param fl flags */
	public void fadeUiLayersOut(int fl) {
		$fadeUiLayersOut(fl, C_D).start(tweenMgr);
	}

	/** Sets the specified UI layers' opacity.
	 * @param fl flags
	 * @param v value */
	public void setUiLayersAlpha(int fl, float v) {
		for (int i=0; i < UIL_LENGTH; i++) {
			if (hasFlag(fl, 1<<i)) {
				killTweenTarget(uiLayers[i], ActorAccessor.A);
				setAlpha(uiLayers[i], v);
			}
		}
	}

	/** Sets the specified UI layer's opacity to 1.
	 * @param fl flags */
	public void setUiLayersAlpha(int fl) {
		setUiLayersAlpha(fl, 1);
	}

	/** Sets the specified UI layer's opacity to 0.
	 * @param fl flags */
	public void resetUITiersAlpha(int fl) {
		setUiLayersAlpha(fl, 0);
	}

	/** Tweens the specified UI layer's opacity.
	 * @param i index
	 * @param v value
	 * @param d duration */
	public void fadeUiLayer(int i, float v, float d) {
		$fadeUiLayer(i, v, d).start(tweenMgr);
	}

	/** Tweens the specified UI layer's opacity to 1.
	 * @param i index
	 * @param v value */
	public void fadeUiLayerIn(int i, float v) {
		$fadeUiLayer(i, v, C_D).start(tweenMgr);
	}

	/** Tweens the specified UI layer's opacity to 1.
	 * @param i index */
	public void fadeUiLayerIn(int i) {
		$fadeUiLayerIn(i, 1).start(tweenMgr);
	}

	/** Tweens the specified UI layer's opacity to 0.
	 * @param i index
	 * @param d duration */
	public void fadeUiLayerOut(int i, float d) {
		$fadeUiLayerOut(i, d).start(tweenMgr);
	}

	/** Tweens the specified UI layer's opacity to 0.
	 * @param i index */
	public void fadeUiLayerOut(int i) {
		$fadeUiLayerOut(i, C_D).start(tweenMgr);
	}

	/** Sets the specified UI layer's opacity to 1.
	 * @param i index */
	public void setUiLayerAlpha(int i) {
		$setUiLayerAlpha(i).start(tweenMgr);
	}

	/** Sets the specified UI layer's opacity to 0.
	 * @param i index */
	public void resetUiLayerAlpha(int i) {
		$resetUiLayerAlpha(i).start(tweenMgr);
	}

	/** Creates a handle to tween the camera shake amplitude.
	 * @param v value
	 * @param d duration
	 * @return tween handle */
	public Tween $tweenCamShake(float v, float d) {
		killTweenTarget(camShake);
		return Tween.to(camShake, 0, d).target(v).ease(Quart.OUT);
	}

	/** Creates a handle to tween the camera shake amplitude.
	 * @param v value
	 * @return tween handle */
	public Tween $tweenCamShake(float v) {
		return $tweenCamShake(v, C_D);
	}

	/** Creates a handle to tween the camera shake amplitude to 1.
	 * @param d duration
	 * @return tween handle */
	public Tween $tweenCamShakeIn(float d) {
		return $tweenCamShake(1, d);
	}

	/** Creates a handle to tween the camera shake amplitude to 1.
	 * @return tween handle */
	public Tween $tweenCamShakeIn() {
		return $tweenCamShakeIn(C_D);
	}

	/** Creates a handle to tween the camera shake amplitude to 0.
	 * @param d duration
	 * @return tween handle */
	public Tween $tweenCamShakeOut(float d) {
		return $tweenCamShake(0, d);
	}

	/** Creates a handle to tween the camera shake amplitude to 0.
	 * @return tween handle */
	public Tween $tweenCamShakeOut() {
		return $tweenCamShakeOut(C_D);
	}

	/** Creates a handle to set the camera shake amplitude.
	 * @param v value
	 * @return tween handle */
	public Tween $setCamShake(float v) {
		return $tweenCamShake(v, 0);
	}

	/** Creates a handle to set the camera shake amplitude to 1.
	 * @return tween handle */
	public Tween $setCamShake() {
		return $setCamShake(1);
	}

	/** Creates a handle to set the camera shake amplitude to 0.
	 * @return tween handle */
	public Tween $resetCamShake() {
		return $tweenCamShakeOut(0);
	}

	/** Creates a handle to tween the timescale multiplier.
	 * @param v value
	 * @param d duration
	 * @return tween handle */
	public Tween $tweenTimescale(float v, float d) {
		killTweenTarget(timescale);
		return Tween.to(timescale, 0, d).target(v).ease(Quart.OUT);
	}

	/** Creates a handle to tween the timescale multiplier.
	 * @param v value
	 * @return tween handle */
	public Tween $tweenTimescale(float v) {
		return $tweenTimescale(v, C_D);
	}

	/** Creates a handle to tween the timescale multiplier to 1.
	 * @param d duration
	 * @return tween handle */
	public Tween $tweenTimescaleIn(float d) {
		return $tweenTimescale(1, d);
	}

	/** Creates a handle to tween the timescale multiplier to 1.
	 * @return tween handle */
	public Tween $tweenTimescaleIn() {
		return $tweenTimescaleIn(C_D);
	}

	/** Creates a handle to tween the timescale multiplier to 0.
	 * @param d duration
	 * @return tween handle */
	public Tween $tweenTimescaleOut(float d) {
		return $tweenTimescale(0, d);
	}

	/** Creates a handle to tween the timescale multiplier to 0.
	 * @return tween handle */
	public Tween $tweenTimescaleOut() {
		return $tweenTimescaleOut(C_D);
	}

	/** Creates a handle to set the timescale multiplier.
	 * @param v value
	 * @return tween handle */
	public Tween $setTimescale(float v) {
		return $tweenTimescale(v, 0);
	}

	/** Creates a handle to set the timescale multiplier to 1.
	 * @return tween handle */
	public Tween $setTimescale() {
		return $setTimescale(1);
	}

	/** Creates a handle to set the timescale multiplier to 0.
	 * @return tween handle */
	public Tween $resetTimescale() {
		return $tweenTimescaleOut(0);
	}

	/** Creates a handle to tween the UI timescale multiplier.
	 * @param v value
	 * @param d duration
	 * @return tween handle */
	public Tween $tweenUiTimescale(float v, float d) {
		killTweenTarget(uiTimescale);
		return Tween.to(uiTimescale, 0, d).target(v).ease(Quart.OUT);
	}

	/** Creates a handle to tween the UI timescale multiplier.
	 * @param v value
	 * @return tween handle */
	public Tween $tweenUiTimescale(float v) {
		return $tweenUiTimescale(v, C_D);
	}

	/** Creates a handle to tween the UI timescale multiplier to 1.
	 * @param d duration
	 * @return tween handle */
	public Tween $tweenUiTimescaleIn(float d) {
		return $tweenUiTimescale(1, d);
	}

	/** Creates a handle to tween the UI timescale multiplier to 1.
	 * @return tween handle */
	public Tween $tweenUiTimescaleIn() {
		return $tweenUiTimescaleIn(C_D);
	}

	/** Creates a handle to tween the UI timescale multiplier to 0.
	 * @param d duration
	 * @return tween handle */
	public Tween $tweenUiTimescaleOut(float d) {
		return $tweenUiTimescale(0, d);
	}

	/** Creates a handle to tween the UI timescale multiplier to 0.
	 * @return tween handle */
	public Tween $tweenUiTimescaleOut() {
		return $tweenUiTimescaleOut(C_D);
	}

	/** Creates a handle to set the UI timescale multiplier.
	 * @param v value
	 * @return tween handle */
	public Tween $setUiTimescale(float v) {
		return $tweenUiTimescale(v, 0);
	}

	/** Creates a handle to set the UI timescale multiplier to 1.
	 * @return tween handle */
	public Tween $setUiTimescale() {
		return $setUiTimescale(1);
	}

	/** Creates a handle to set the UI timescale multiplier to 0.
	 * @return tween handle */
	public Tween $resetUiTimescale() {
		return $tweenUiTimescaleOut(0);
	}

	/** Creates a handle to tween the master volume.
	 * @param v value
	 * @param d duration
	 * @return tween handle */
	public Tween $tweenMasterVolume(float v, float d) {
		killTweenTarget(masterVolume);
		return Tween.to(masterVolume, 0, d).target(v).ease(Quart.OUT)
				.setCallbackTriggers(BEGIN|COMPLETE).setCallback(masterVolumeCallback);
	}

	/** Creates a handle to tween the master volume.
	 * @param v value
	 * @return tween handle */
	public Tween $tweenMasterVolume(float v) {
		return $tweenMasterVolume(v, C_MD);
	}

	/** Creates a handle to tween the master volume to 1.
	 * @param d duration
	 * @return tween handle */
	public Tween $tweenMasterVolumeIn(float d) {
		return $tweenMasterVolume(1, d);
	}

	/** Creates a handle to tween the master volume to 1.
	 * @return tween handle */
	public Tween $tweenMasterVolumeIn() {
		return $tweenMasterVolumeIn(C_MD);
	}

	/** Creates a handle to tween the master volume to 0.
	 * @param d duration
	 * @return tween handle */
	public Tween $tweenMasterVolumeOut(float d) {
		return $tweenMasterVolume(0, d);
	}

	/** Creates a handle to tween the master volume to 0.
	 * @return tween handle */
	public Tween $tweenMasterVolumeOut() {
		return $tweenMasterVolumeOut(C_MD);
	}

	/** Creates a handle to set the master volume.
	 * @param v value
	 * @return tween handle */
	public Tween $setMasterVolume(float v) {
		return $tweenMasterVolume(v, 0);
	}

	/** Creates a handle to set the master volume to 1.
	 * @return tween handle */
	public Tween $setMasterVolume() {
		return $setMasterVolume(1);
	}

	/** Creates a handle to set the master volume to 0.
	 * @return tween handle */
	public Tween $resetMasterVolume() {
		return $tweenMasterVolumeOut(0);
	}

	/** Creates a handle to tween the sound effects volume.
	 * @param v value
	 * @param d duration
	 * @return tween handle */
	public Tween $tweenSfxVolume(float v, float d) {
		killTweenTarget(sfxVolume);
		return Tween.to(sfxVolume, 0, d).target(v).ease(Quart.OUT);
	}

	/** Creates a handle to tween the sound effects volume.
	 * @param v value
	 * @return tween handle */
	public Tween $tweenSfxVolume(float v) {
		return $tweenSfxVolume(v, C_MD);
	}

	/** Creates a handle to tween the sound effects volume to 1.
	 * @param d duration
	 * @return tween handle */
	public Tween $tweenSfxVolumeIn(float d) {
		return $tweenSfxVolume(1, d);
	}

	/** Creates a handle to tween the sound effects volume to 1.
	 * @return tween handle */
	public Tween $tweenSfxVolumeIn() {
		return $tweenSfxVolumeIn(C_MD);
	}

	/** Creates a handle to tween the sound effects volume to 0.
	 * @param d duration
	 * @return tween handle */
	public Tween $tweenSfxVolumeOut(float d) {
		return $tweenSfxVolume(0, d);
	}

	/** Creates a handle to tween the sound effects volume to 0.
	 * @return tween handle */
	public Tween $tweenSfxVolumeOut() {
		return $tweenSfxVolumeOut(C_MD);
	}

	/** Creates a handle to set the sound effects volume.
	 * @param v value
	 * @return tween handle */
	public Tween $setSfxVolume(float v) {
		return $tweenSfxVolume(v, 0);
	}

	/** Creates a handle to set the sound effects volume to 1.
	 * @return tween handle */
	public Tween $setSfxVolume() {
		return $setSfxVolume(1);
	}

	/** Creates a handle to set the sound effects volume to 0.
	 * @return tween handle */
	public Tween $resetSfxVolume() {
		return $tweenSfxVolumeOut(0);
	}

	/** Creates a handle to tween the music volume.
	 * @param v value
	 * @param d duration
	 * @return tween handle */
	public Tween $tweenMusicVolume(float v, float d) {
		killTweenTarget(musicVolume);
		return Tween.to(musicVolume, 0, d).target(v).ease(Quart.OUT)
				.setCallbackTriggers(BEGIN|COMPLETE).setCallback(masterVolumeCallback);
	}

	/** Creates a handle to tween the music volume.
	 * @param v value
	 * @return tween handle */
	public Tween $tweenMusicVolume(float v) {
		return $tweenMusicVolume(v, C_ID);
	}

	/** Creates a handle to tween the music volume to 1.
	 * @param d duration
	 * @return tween handle */
	public Tween $tweenMusicVolumeIn(float d) {
		return $tweenMusicVolume(1, d);
	}

	/** Creates a handle to tween the music volume to 1.
	 * @return tween handle */
	public Tween $tweenMusicVolumeIn() {
		return $tweenMusicVolumeIn(C_ID);
	}

	/** Creates a handle to tween the music volume to 0.
	 * @param d duration
	 * @return tween handle */
	public Tween $tweenMusicVolumeOut(float d) {
		return $tweenMusicVolume(0, d);
	}

	/** Creates a handle to tween the music volume to 0.
	 * @return tween handle */
	public Tween $tweenMusicVolumeOut() {
		return $tweenMusicVolumeOut(C_ID);
	}

	/** Creates a handle to set the music volume.
	 * @param v value
	 * @return tween handle */
	public Tween $setMusicVolume(float v) {
		return $tweenMusicVolume(v, 0);
	}

	/** Creates a handle to set the music volume to 1.
	 * @return tween handle */
	public Tween $setMusicVolume() {
		return $setMusicVolume(1);
	}

	/** Creates a handle to set the music volume to 0.
	 * @return tween handle */
	public Tween $resetMusicVolume() {
		return $tweenMusicVolumeOut(0);
	}

	/** Creates a handle to slide the camera x.
	 * @param d duration
	 * @return tween handle */
	public Tween $moveCameraX(float x, float d) {
		killTweenTarget(camera, OrthoCameraAccessor.X);
		return Tween.to(camera, OrthoCameraAccessor.X, d).target(x).ease(Quart.INOUT);
	}

	/** Creates a handle to slide the camera x.
	 * @return tween handle */
	public Tween $moveCameraX(float x) {
		return $moveCameraX(x, C_D);
	}

	/** Creates a handle to tween to slide the camera y.
	 * @param d duration
	 * @return tween handle */
	public Tween $moveCameraY(float y, float d) {
		killTweenTarget(camera, OrthoCameraAccessor.Y);
		return Tween.to(camera, OrthoCameraAccessor.Y, d).target(y).ease(Quart.INOUT);
	}

	/** Creates a handle to tween to slide the camera y.
	 * @return tween handle */
	public Tween $moveCameraY(float y) {
		return $moveCameraX(y, C_D);
	}

	/** Creates a handle to tween to slide the camera position.
	 * @param v value
	 * @param d duration
	 * @return tween handle */
	public Tween $moveCamera(float x, float y, float d) {
		killTweenTarget(camera, OrthoCameraAccessor.POS);
		return Tween.to(camera, OrthoCameraAccessor.POS, d).target(x, y).ease(Quart.INOUT);
	}

	/** Creates a handle to tween to slide the camera position.
	 * @return tween handle */
	public Tween $moveCamera(float x, float y) {
		return $moveCamera(x, y, C_D);
	}

	/** Creates a handle to tween the camera zoom.
	 * @param z zoom
	 * @param d duration
	 * @return tween handle */
	public Tween $zoomCamera(float z, float d) {
		killTweenTarget(camera, OrthoCameraAccessor.ZOOM);
		return Tween.to(camera, OrthoCameraAccessor.ZOOM, d).target(z).ease(Quart.OUT);
	}
	
	/** Creates a handle to tween the camera zoom.
	 * @param z zoom
	 * @param d duration
	 * @return tween handle */
	public Tween $zoomCamera(float z) {
		return $zoomCamera(z, C_D);
	}
	
	/** Creates a handle to tween the camera zoom out.
	 * @param d duration
	 * @return tween handle */
	public Tween $zoomCameraOut(float d) {
		return $zoomCamera(1, d);
	}

	/** Creates a handle to tween the camera zoom out.
	 * @return tween handle */
	public Tween $zoomCameraOut() {
		return $zoomCamera(1, C_D);
	}

	/** Creates a handle to tween the UI opacity.
	 * @param v value
	 * @param d duration
	 * @return tween handle */
	public Tween $fadeUi(float v, float d) {
		killTweenTarget(ui.getRoot(), ActorAccessor.A);
		return Tween.to(ui.getRoot(), ActorAccessor.A, d).target(v).ease(Quart.OUT);
	}

	/** Creates a handle to tween  to 1.
	 * @param d duration
	 * @return tween handle */
	public Tween $fadeUiIn(float d) {
		return $fadeUi(1, d);
	}

	/** Creates a handle to tween  to 1.
	 * @return tween handle */
	public Tween $fadeUiIn() {
		return $fadeUiIn(C_D);
	}

	/** Creates a handle to tween  to 0.
	 * @param d duration
	 * @return tween handle */
	public Tween $fadeUiOut(float d) {
		return $fadeUi(0, d);
	}

	/** Creates a handle to tween  to 0.
	 * @return tween handle */
	public Tween $fadeUiOut() {
		return $fadeUiOut(C_D);
	}

	/** Creates a handle to set  to 1.
	 * @param d duration
	 * @return tween handle */
	public Tween $setUiAlpha() {
		return $fadeUi(1, 0);
	}

	/** Creates a handle to set  to 0.
	 * @param d duration
	 * @return tween handle */
	public Tween $resetUiAlpha() {
		return $fadeUiOut(0);
	}

	/** Creates a handle to tween the specified UI layers' opacity.
	 * @param fl flags
	 * @param v value
	 * @param d duration
	 * @return tween handle */
	public Timeline $fadeUiLayers(int fl, float v, float d) {
		Timeline tl = Timeline.createParallel();
		for (int i=0; i < UIL_LENGTH; i++) {
			if (hasFlag(fl, 1<<i)) {
				killTweenTarget(uiLayers[i], ActorAccessor.A);
				tl.push(Tween.to(uiLayers[i], ActorAccessor.A, d).target(v).ease(Quart.OUT));
			}
		}
		return tl;
	}

	/** Creates a handle to tween the specified UI layers' opacity to 1.
	 * @param fl flags
	 * @param d duration
	 * @return tween handle */
	public Timeline $fadeUiLayersIn(int fl, float d) {
		return $fadeUiLayers(fl, 1, d);
	}

	/** Creates a handle to tween the specified UI layers' opacity to 1.
	 * @param fl flags
	 * @return tween handle */
	public Timeline $fadeUiLayersIn(int fl) {
		return $fadeUiLayersIn(fl, 1);
	}

	/** Creates a handle to tween the specified UI layers' opacity to 0.
	 * @param fl flags
	 * @param d duration
	 * @return tween handle */
	public Timeline $fadeUiLayersOut(int fl, float d) {
		return $fadeUiLayers(fl, 0, d);
	}

	/** Creates a handle to tween the specified UI layers' opacity to 0.
	 * @param fl flags
	 * @return tween handle */
	public Timeline $fadeUiLayersOut(int fl) {
		return $fadeUiLayersOut(fl, C_D);
	}

	/** Creates a handle to set the specified UI layers' opacity.
	 * @param fl flags
	 * @return tween handle */
	public Timeline $setUiLayersAlpha(int fl, float v) {
		return $fadeUiLayers(fl, v, 0);
	}

	/** Creates a handle to set the specified UI layers' opacity to 1.
	 * @param fl flags
	 * @return tween handle */
	public Timeline $setUiLayersAlpha(int fl) {
		return $setUiLayersAlpha(fl, 1);
	}

	/** Creates a handle to set the specified UI layers' opacity to 0.
	 * @param fl flags
	 * @return tween handle */
	public Timeline $resetUiLayersAlpha(int fl) {
		return $setUiLayersAlpha(fl, 0);
	}

	/** Creates a handle to tween the specified UI layer's opacity.
	 * @param i index
	 * @param v value
	 * @param d duration
	 * @return tween handle */
	public Timeline $fadeUiLayer(int i, float v, float d) {
		return $fadeUiLayers(1<<i, v, d);
	}

	/** Creates a handle to tween the specified UI layer's opacity to 1.
	 * @param i index
	 * @param d duration
	 * @return tween handle */
	public Timeline $fadeUiLayerIn(int i, float d) {
		return $fadeUiLayer(i, 1, d);
	}

	/** Creates a handle to tween the specified UI layer's opacity to 1.
	 * @param i index
	 * @return tween handle */
	public Timeline $fadeUiLayerIn(int i) {
		return $fadeUiLayerIn(i, C_D);
	}

	/** Creates a handle to tween the specified UI layer's opacity to 0.
	 * @param i index
	 * @param d duration
	 * @return tween handle */
	public Timeline $fadeUiLayerOut(int i, float d) {
		return $fadeUiLayer(i, 0, d);
	}

	/** Creates a handle to tween the specified UI layer's opacity to 0.
	 * @param i index
	 * @return tween handle */
	public Timeline $fadeUiLayerOut(int i) {
		return $fadeUiLayerOut(i, C_D);
	}

	/** Creates a handle to set the specified UI layer's opacity.
	 * @param i index
	 * @return tween handle */
	public Timeline $setUiLayerAlpha(int i, float v) {
		return $fadeUiLayer(i, v, 0);
	}

	/** Creates a handle to set the specified UI layer's opacity to 1.
	 * @param i index
	 * @return tween handle */
	public Timeline $setUiLayerAlpha(int i) {
		return $setUiLayerAlpha(i, 1);
	}

	/** Creates a handle to set the specified UI layer's opacity to 0.
	 * @param i index
	 * @return tween handle */
	public Timeline $resetUiLayerAlpha(int i) {
		return $setUiLayerAlpha(i, 0);
	}

	/** Tweens the blur.
	 * @param v value
	 * @param d duration */
	public void tweenBlur(float v, float d) {
		$tweenBlur(v, d).start(tweenMgr);
	}

	/** Tweens the blur to 1.
	 * @param d duration */
	public void blurIn(float d) {
		tweenBlur(1, d);
	}

	/** Tweens the blur to 1. */
	public void blurIn() {
		blurIn(C_D);
	}

	/** Tweens the blur to 0.
	 * @param d duration */
	public void blurOut(float d) {
		$blurOut(d).start(tweenMgr);
	}

	/** Tweens the blur to 0. */
	public void blurOut() {
		blurOut(C_D);
	}

	/** Sets the blur if advanced performance is on.
	 * @param v value */
	public void setBlur(float v) {
		if (advancedPerformance) {
			blur.setAmount(v);
		}
	}

	/** Sets the blur to 1 if advanced performance is on. */
	public void setBlur() {
		setBlur(1);
	}

	/** Sets the blur to 0 if advanced performance is on. */
	public void resetBlur() {
		if (advancedPerformance) {
			blur.resetAmount();
		}
	}

	/*public void blurIn(float v, float d) {
		$blur(v, d).start(tweenMgr);
	}

	public void blurIn(float v) {
		blurIn(v, D);
	}

	public void blurIn() {
		blurIn(D);
	}

	public void blurOut(float d) {
		$blurOut(d).start(tweenMgr);
	}

	public void blurOut() {
		blurOut(D);
	}

	public void setBlur(float v) {
		if (advancedPerformance) {
			postprocessor.setValue(uniBlur, v);
		}
	}

	public void setBlur() {
		setBlur(1);
	}

	public void resetBlur() {
		if (advancedPerformance) {
			postprocessor.resetValue(uniBlur);
		}
	}

	public void tweenTiltshiftY(float v, float d) {
		$tweenTiltshiftY(v, d).start(tweenMgr);
	}

	public void tweenTiltshiftY(float v) {
		tweenTiltshiftY(v, D);
	}

	public void tweenTiltshiftYIn() {
		tweenTiltshiftY(D);
	}

	public void tweenTiltshiftYOut(float d) {
		$tweenTiltshiftYOut(d).start(tweenMgr);
	}

	public void tweenTiltshiftYOut() {
		tweenTiltshiftYOut(D);
	}

	public void setTiltshiftY(float v) {
		if (advancedPerformance) {
			postprocessor.setValue(uniTiltshiftY, v);
		}
	}

	public void setTiltshiftY() {
		setTiltshiftY(1);
	}

	public void resetTiltshiftY() {
		if (advancedPerformance) {
			postprocessor.resetValue(uniTiltshiftY);
		}
	}

	public void tweenTiltshiftX(float v, float d) {
		$tweenTiltshiftX(v, d).start(tweenMgr);
	}

	public void tweenTiltshiftX(float v) {
		tweenTiltshiftX(v, D);
	}

	public void tweenTiltshiftXIn() {
		tweenTiltshiftX(D);
	}

	public void tweenTiltshiftXOut(float d) {
		$tweenTiltshiftXOut(d).start(tweenMgr);
	}

	public void tweenTiltshiftXOut() {
		tweenTiltshiftXOut(D);
	}

	public void setTiltshiftX(float v) {
		if (advancedPerformance) {
			postprocessor.setValue(uniTiltshiftX, v);
		}
	}

	public void setTiltshiftX() {
		setTiltshiftX(1);
	}

	public void resetTiltshiftX() {
		if (advancedPerformance) {
			postprocessor.resetValue(uniTiltshiftX);
		}
	}*/

	/** Tweens the {@link Overlay} sheers layer color.
	 * @param clr color
	 * @param d duration */
	public void fadeSheersColor(Color clr, float d) {
		$fadeSheersColor(clr, d).start(tweenMgr);
	}

	/** Tweens the {@link Overlay} sheers layer color.
	 * @param clr color */
	public void fadeSheersColor(Color clr) {
		$fadeSheersColor(clr).start(tweenMgr);
	}

	/** Tweens the {@link Overlay} sheers layer.
	 * @param clr color
	 * @param d duration */
	public void setSheersColor(Color clr) {
		overlay.setSheersColor(clr);
	}

	/** Sets the color and fades the {@link Overlay} sheers layer.
	 * @param clr color
	 * @param v value
	 * @param d duration */
	public void fadeSheers(Color clr, float v, float d) {
		$fadeSheers(clr, v, d).start(tweenMgr);
	}

	/** Sets the color and fades the {@link Overlay} sheers layer.
	 * @param clr color
	 * @param v value */
	public void fadeSheers(Color clr, float v) {
		$fadeSheers(clr, v).start(tweenMgr);
	}

	/** Fades the {@link Overlay} sheers layer.
	 * @param v value
	 * @param d duration */
	public void fadeSheers(float v, float d) {
		$fadeSheers(v, d).start(tweenMgr);
	}

	/** Fades the {@link Overlay} sheers layer.
	 * @param v value */
	public void fadeSheers(float v) {
		$fadeSheers(v).start(tweenMgr);
	}

	/** Fades the {@link Overlay} sheers layer to 1. */
	public void fadeSheersIn() {
		$fadeSheersIn().start(tweenMgr);
	}

	/** Fades the {@link Overlay} sheers layer to 0.
	 * @param d duration */
	public void fadeSheersOut(float d) {
		$fadeSheersOut(d).start(tweenMgr);
	}

	/** Fades to fade the {@link Overlay} sheers layer to 0. */
	public void fadeSheersOut() {
		$fadeSheersOut().start(tweenMgr);
	}

	/** Sets the color and fades the {@link Overlay} sheers layer to 1 and to 0 successively.
	 * @param clr color
	 * @param v value
	 * @param id in delay
	 * @param od out delay */
	public void fadeSheersInOut(Color clr, float v, float id, float od) {
		$fadeSheersInOut(clr, v, id, od).start(tweenMgr);
	}

	/** Sets the color and fades the {@link Overlay} sheers layer to 1 and to 0 successively.
	 * @param clr color
	 * @param v value */
	public void fadeSheersInOut(Color clr, float v) {
		$fadeSheersInOut(clr, v).start(tweenMgr);
	}

	/** Sets the color and fades the {@link Overlay} sheers layer to 1 and to 0 successively.
	 * @param clr color */
	public void fadeSheersInOut(Color clr) {
		$fadeSheersInOut(clr).start(tweenMgr);
	}

	/** Sets the color and fades the {@link Overlay} sheers layer in and to the specified opacity successively.
	 * @param clr color
	 * @param iv in value
	 * @param ov out value
	 * @param id in delay
	 * @param od out delay */
	public void fadeSheersInTo(Color clr, float iv, float ov, float id, float od) {
		$fadeSheersInTo(clr, iv, ov, id, od).start(tweenMgr);
	}

	/** Sets the color and fades the {@link Overlay} sheers layer in and to the specified opacity successively.
	 * @param clr color
	 * @param iv in value
	 * @param ov out value */
	public void fadeSheersInTo(Color clr, float iv, float ov) {
		$fadeSheersInTo(clr, iv, ov).start(tweenMgr);
	}

	/** Sets the color and fades the {@link Overlay} sheers layer in and to the specified opacity successively.
	 * @param clr color
	 * @param ov out value */
	public void fadeSheersInTo(Color clr, float ov) {
		$fadeSheersInTo(clr, ov).start(tweenMgr);
	}

	/** Sets the color and the {@link Overlay} sheers layer opacity.
	 * @param clr color
	 * @param v value */
	public void setSheers(Color clr, float v) {
		overlay.setSheers(clr, v);
	}

	/** Sets the {@link Overlay} sheers layer opacity.
	 * @param v value */
	public void setSheers(float v) {
		overlay.setSheers(v);
	}

	/** Sets the {@link Overlay} sheers layer opacity to 1. */
	public void setSheers() {
		setSheers(1);
	}

	/** Sets the {@link Overlay} sheers layer opacity to 0. */
	public void resetSheers() {
		overlay.resetSheers();
	}

	/** Tweens the {@link Overlay} tint layer color.
	 * @param clr color
	 * @param d duration */
	public void tweenTintColor(Color clr, float d) {
		$tweenTintColor(clr, d).start(tweenMgr);
	}

	/** Tweens the {@link Overlay} tint layer color.
	 * @param clr color */
	public void tweenTintColor(Color clr) {
		$tweenTintColor(clr).start(tweenMgr);
	}

	/** Tweens the {@link Overlay} tint layer.
	 * @param clr color
	 * @param d duration */
	public void setTintColor(Color clr) {
		overlay.setTintColor(clr);
	}

	/** Sets the color and fades the {@link Overlay} tint layer.
	 * @param clr color
	 * @param v value
	 * @param d duration */
	public void fadeTint(Color clr, float v, float d) {
		$fadeTint(clr, v, d).start(tweenMgr);
	}

	/** Sets the color and fades the {@link Overlay} tint layer.
	 * @param clr color
	 * @param v value */
	public void fadeTint(Color clr, float v) {
		$fadeTint(clr, v).start(tweenMgr);
	}

	/** Fades the {@link Overlay} tint layer.
	 * @param v value
	 * @param d duration */
	public void fadeTint(float v, float d) {
		$fadeTint(v, d).start(tweenMgr);
	}

	/** Fades the {@link Overlay} tint layer.
	 * @param v value */
	public void fadeTint(float v) {
		$fadeTint(v).start(tweenMgr);
	}

	/** Fades the {@link Overlay} tint layer to 1. */
	public void fadeTintIn() {
		$fadeTintIn().start(tweenMgr);
	}

	/** Fades the {@link Overlay} tint layer to 0.
	 * @param d duration */
	public void fadeTintOut(float d) {
		$fadeTintOut(d).start(tweenMgr);
	}

	/** Fades to fade the {@link Overlay} tint layer to 0. */
	public void fadeTintOut() {
		$fadeTintOut().start(tweenMgr);
	}

	/** Sets the color and fades the {@link Overlay} tint layer to 1 and to 0 successively.
	 * @param clr color
	 * @param v value
	 * @param id in delay
	 * @param od out delay */
	public void fadeTintInOut(Color clr, float v, float id, float od) {
		$fadeTintInOut(clr, v, id, od).start(tweenMgr);
	}

	/** Sets the color and fades the {@link Overlay} tint layer to 1 and to 0 successively.
	 * @param clr color
	 * @param v value */
	public void fadeTintInOut(Color clr, float v) {
		$fadeTintInOut(clr, v).start(tweenMgr);
	}

	/** Sets the color and the {@link Overlay} tint layer opacity.
	 * @param clr color
	 * @param v value */
	public void setTint(Color clr, float v) {
		overlay.setTint(clr, v);
	}

	/** Sets the {@link Overlay} tint layer opacity.
	 * @param v value */
	public void setTint(float v) {
		overlay.setTint(v);
	}

	/** Sets the {@link Overlay} tint layer opacity to 1. */
	public void setTint() {
		setTint(1);
	}

	/** Sets the {@link Overlay} tint layer opacity to 0. */
	public void resetTint() {
		overlay.resetTint();
	}

	/** Smoothly dims the screen.
	 * Used to create a dark contrasting background for the UI.
	 * @param d duration */
	public void dimIn(float d) {
		$dimIn(d).start(tweenMgr);
	}

	/** Smoothly dims the screen.
	 * Used to create a dark contrasting background for the UI. */
	public void dimIn() {
		$dimIn().start(tweenMgr);
	}

	/** Smoothly fades out the screen dim.
	 * @param d duration */
	public void dimOut(float d) {
		$dimOut(d).start(tweenMgr);
	}

	/** Smoothly fades out the screen dim. */
	public void dimOut() {
		$dimOut().start(tweenMgr);
	}

	/** Instantly applies the screen dim.
	 * Used to create a dark contrasting background for the UI. */
	public void setDim() {
		$setDim().start(tweenMgr);
	}

	/** Instantly removes the screen dim. */
	public void resetDim() {
		$resetDim().start(tweenMgr);
	}

	/** Smoothly lightly dims the screen.
	 * Used to create a slightly dark contrasting background for the UI.
	 * @param d duration */
	public void dimInL(float d) {
		$dimInL(d).start(tweenMgr);
	}

	/** Smoothly lightly dims the screen.
	 * Used to create a slightly dark contrasting background for the UI. */
	public void dimInL() {
		$dimInL().start(tweenMgr);
	}

	/** Instantly applies the light screen dim.
	 * Used to create a slightly dark contrasting background for the UI. */
	public void setDimL() {
		$setDimL().start(tweenMgr);
	}

	/** Tweens the {@link Overlay} vignette layer color.
	 * @param clr color
	 * @param d duration */
	public void fadeVignetteColor(Color clr, float d) {
		$tweenVignetteColor(clr, d).start(tweenMgr);
	}

	/** Tweens the {@link Overlay} vignette layer color.
	 * @param clr color */
	public void fadeVignetteColor(Color clr) {
		$tweenVignetteColor(clr).start(tweenMgr);
	}

	/** Sets the color and fades the {@link Overlay} vignette layer.
	 * @param clr color
	 * @param v value
	 * @param d duration */
	public void fadeVignette(Color clr, float v, float d) {
		$fadeVignette(clr, v, d).start(tweenMgr);
	}

	/** Sets the color and fades the {@link Overlay} vignette layer.
	 * @param clr color
	 * @param v value */
	public void fadeVignette(Color clr, float v) {
		$fadeVignette(clr, v).start(tweenMgr);
	}

	/** Sets the color and fades the {@link Overlay} vignette layer to 1 and to 0 successively.
	 * @param clr color */
	public void fadeVignette(Color clr) {
		$fadeVignette(clr).start(tweenMgr);
	}

	/** Fades the {@link Overlay} vignette layer.
	 * @param v value
	 * @param d duration */
	public void fadeVignette(float v, float d) {
		$fadeVignette(v, d).start(tweenMgr);
	}

	/** Fades the {@link Overlay} vignette layer.
	 * @param v value */
	public void fadeVignette(float v) {
		$fadeVignette(v, C_D).start(tweenMgr);
	}

	/** Fades the {@link Overlay} vignette layer to 1. */
	public void fadeVignetteIn() {
		$fadeVignetteIn().start(tweenMgr);
	}

	/** Fades the {@link Overlay} vignette layer to 0.
	 * @param d duration */
	public void fadeVignetteOut(float d) {
		$fadeVignetteOut(d).start(tweenMgr);
	}

	/** Fades to fade the {@link Overlay} vignette layer to 0. */
	public void fadeVignetteOut() {
		$fadeVignetteOut().start(tweenMgr);
	}

	/** Sets the color and fades the {@link Overlay} vignette layer to 1 and to 0 successively.
	 * @param clr color
	 * @param v value
	 * @param id in delay
	 * @param od out delay */
	public void fadeVignetteInOut(Color clr, float v, float id, float od) {
		$fadeVignetteInOut(clr, v, id, od).start(tweenMgr);
	}

	/** Sets the color and fades the {@link Overlay} vignette layer to 1 and to 0 successively.
	 * @param clr color
	 * @param v value */
	public void fadeVignetteInOut(Color clr, float v) {
		$fadeVignetteInOut(clr, v).start(tweenMgr);
	}

	/** Sets the color and fades the {@link Overlay} vignette layer in and to the specified opacity successively.
	 * @param clr color
	 * @param iv in value
	 * @param ov out value
	 * @param id in delay
	 * @param od out delay */
	public void fadeVignetteInTo(Color clr, float iv, float ov, float id, float od) {
		$fadeVignetteInTo(clr, iv, ov, id, od).start(tweenMgr);
	}

	/** Sets the color and fades the {@link Overlay} vignette layer in and to the specified opacity successively.
	 * @param clr color
	 * @param iv in value
	 * @param ov out value */
	public void fadeVignetteInTo(Color clr, float iv, float ov) {
		$fadeVignetteInTo(clr, iv, ov).start(tweenMgr);
	}

	/** Sets the color and fades the {@link Overlay} vignette layer in and to the specified opacity successively.
	 * @param clr color
	 * @param ov out value */
	public void fadeVignetteInTo(Color clr, float ov) {
		$fadeVignetteInTo(clr, ov).start(tweenMgr);
	}

	/** Sets the color and the {@link Overlay} vignette layer opacity.
	 * @param clr color
	 * @param v value */
	public void setVignette(Color clr, float v) {
		overlay.setVignette(clr, v);
	}

	/** Sets the {@link Overlay} vignette layer opacity.
	 * @param v value */
	public void setVignette(float v) {
		overlay.setVignette(v);
	}

	/** Sets the {@link Overlay} vignette layer opacity to 1. */
	public void setVignette() {
		$setTint(OL_VIG_OP).start(tweenMgr);
	}

	/** Sets the {@link Overlay} vignette layer opacity to 0. */
	public void resetVignette() {
		$resetTint().start(tweenMgr);
	}

	/** Fades the screen and UI layers in after it first sets it black and invisible respectively.
	 * @param fl flags
	 * @param duration */
	public void fadeIn(int fl, float d) {
		$fadeIn(fl, d).start(tweenMgr);
	}

	/** Fades the screen and the general UI layers (game and menus) in after it first sets it black and invisible respectively.
	 * @param duration */
	public void fadeIn(float d) {
		$fadeIn(d).start(tweenMgr);
	}

	/** Fades the screen and UI layers in after it first sets it black and invisible respectively.
	 * @param fl flags */
	public void fadeIn(int fl) {
		$fadeIn(fl).start(tweenMgr);
	}

	/** Fades the screen and the general UI layers (game and menus) in after it first sets it black and invisible respectively. */
	public void fadeIn() {
		$fadeIn().start(tweenMgr);
	}

	/** Fades the screen to black and fade the specified UI layers out.
	 * @param fl flags
	 * @param d duration */
	public void fadeOut(int fl, float d) {
		$fadeOut(fl, d).start(tweenMgr);
	}

	/** Fades the screen to black and fade the general UI layers (game and menus) out.
	 * @param d duration */
	public void fadeOut(float d) {
		$fadeOut(d).start(tweenMgr);
	}

	/** Fades the screen to black and fade the specified UI layers out.
	 * @param fl flags */
	public void fadeOut(int fl) {
		$fadeOut(fl).start(tweenMgr);
	}

	/** Fades the screen to black and fade the general UI (game and menus) layers out. */
	public void fadeOut() {
		$fadeOut().start(tweenMgr);
	}

	/** Fades the screen and the specified UI layers out, call the callback and fade it all back in.
	 * @param fli in flags
	 * @param flo out flags
	 * @param id in duration
	 * @param od out duration
	 * @param cb callback */
	public void fadeOutIn(int fli, int flo, float id, float od, TweenCallback cb) {
		$fadeOutIn(fli, flo, id, od, cb).start(tweenMgr);
	}

	/** Fades the screen and the general UI layers (game and menus) out, call the callback and fade it all back in.
	 * @param id in duration
	 * @param od out duration
	 * @param cb callback */
	public void fadeOutIn(float id, float od, TweenCallback cb) {
		$fadeOutIn(id, od, cb).start(tweenMgr);
	}

	/** Fades the screen and the specified UI layers out, call the callback and fade it all back in.
	 * @param fli in flags
	 * @param flo out flags
	 * @param cb callback */
	public void fadeOutIn(int fli, int flo, TweenCallback cb) {
		$fadeOutIn(fli, flo, cb).start(tweenMgr);
	}

	/** Fades the screen and the general UI layers (game and menus) out, call the callback and fade it all back in.
	 * @param cb callback */
	public void fadeOutIn(TweenCallback cb) {
		$fadeOutIn(cb).start(tweenMgr);
	}

	/** Fades the screen and the specified UI layers out, call the first part of the callback, 
	 * fade it all back in and call the second part of the callback.
	 * @param fli in flags
	 * @param flo out flags
	 * @param id in duration
	 * @param od out duration
	 * @param cb callback */
	public void fadeOutInTo(int fli, int flo, float id, float od, TweenCallback cb) {
		$fadeOutInTo(fli, flo, id, od, cb).start(tweenMgr);
	}

	/** Fades the screen and the general UI layers (game and menus) out, call the first part of the callback, 
	 * fade it all back in and call the second part of the callback.
	 * @param id in duration
	 * @param od out duration
	 * @param cb callback */
	public void fadeOutInTo(float id, float od, TweenCallback cb) {
		$fadeOutInTo(id, od, cb).start(tweenMgr);
	}

	/** Fades the screen and the specified UI layers out, call the first part of the callback, 
	 * fade it all back in and call the second part of the callback.
	 * @param fli in flags
	 * @param flo out flags
	 * @param cb callback */
	public void fadeOutInTo(int fli, int flo, TweenCallback cb) {
		$fadeOutInTo(fli, flo, cb).start(tweenMgr);
	}

	/** Fades the screen and the general UI layers (game and menus) out, call the first part of the callback, 
	 * fade it all back in and call the second part of the callback.
	 * @param cb callback */
	public void fadeOutInTo(TweenCallback cb) {
		$fadeOutInTo(cb).start(tweenMgr);
	}

	/** Fades the screen and the specified UI layers out and call the the callback.
	 * @param fl flags
	 * @param d duration
	 * @param cb callback */
	public void fadeTo(int fl, float d, TweenCallback cb) {
		$fadeTo(fl, d, cb).start(tweenMgr);
	}

	/** Fades the screen and the general UI layers (game and menus) out and call the the callback.
	 * @param d duration
	 * @param cb callback */
	public void fadeTo(float d, TweenCallback cb) {
		$fadeTo(d, cb).start(tweenMgr);
	}

	/** Fades the screen and the general UI layers (game and menus) out and call the the callback.
	 * @param cb callback */
	public void fadeTo(TweenCallback cb) {
		$fadeTo(cb).start(tweenMgr);
	}

	/** Flashes the screen with a specified color.
	 * @param clr color */
	public void flashFx(Color clr) {
		$flashFx(clr).start(tweenMgr);
	}

	/** Flashes the screen with white. */
	public void flashFx() {
		$flashFx().start(tweenMgr);
	}

	/** Flashes the vignette with a specified color.
	 * @param clr color */
	public void flashFxVig(Color clr) {
		$flashFxVig(clr).start(tweenMgr);
	}

	/******************************************/

	/** Creates a handle to tween the blur.
	 * @param v value
	 * @param d duration
	 * @return tween if advanced features are available, otherwise returns blank. */
	public Tween $tweenBlur(float v, float d) {
		return advancedPerformance ? blur.$tween(v, d) : emptyTween();
	}

	/** Creates a handle to tween the blur to 1.
	 * @param d duration
	 * @return tween if advanced features are available, otherwise returns blank. */
	public Tween $blurIn(float d) {
		return $tweenBlur(1, d);
	}

	/** Creates a handle to tween the blur to 1.
	 * @return tween if advanced features are available, otherwise returns blank. */
	public Tween $blurIn() {
		return $blurIn(C_D);
	}

	/** Creates a handle to tween the blur to 0.
	 * @param d duration
	 * @return tween if advanced features are available, otherwise returns blank. */
	public Tween $blurOut(float d) {
		return advancedPerformance ? blur.$tweenOut(d) : emptyTween();
	}

	/** Creates a handle to tween the blur to 0.
	 * @return tween if advanced features are available, otherwise returns blank. */
	public Tween $blurOut() {
		return $blurIn(C_D);
	}

	/** Creates a handle to set the blur.
	 * @param v value
	 * @return tween if advanced features are available, otherwise returns blank. */
	public Tween $setBlur(float v) {
		return advancedPerformance ? blur.$setAmount(v) : emptyTween();
	}

	/** Creates a handle to set the blur to 1.
	 * @return tween if advanced features are available, otherwise returns blank. */
	public Tween $setBlur() {
		return $setBlur(1);
	}

	/** Creates a handle to set the blur to 0.
	 * @return tween if advanced features are available, otherwise returns blank. */
	public Tween $resetBlur() {
		return advancedPerformance ? blur.$resetAmount() : emptyTween();
	}

	/*public Tween $tweenBlur(float v, float d) {
		return advancedPerformance ? postprocessor.$tween(uniBlur, v, d) : emptyTween();
	}

	public Tween $blurIn(float d) {
		return $tweenBlur(1, d);
	}

	public Tween $blurIn() {
		return $blurIn(D);
	}

	public Tween $blurOut(float d) {
		return advancedPerformance ? postprocessor.$tweenOut(uniBlur, d) : emptyTween();
	}

	public Tween $blurOut() {
		return $blurIn(D);
	}

	public Tween $setBlur(float v) {
		return advancedPerformance ? postprocessor.$setValue(uniBlur, v) : emptyTween();
	}

	public Tween $setBlur() {
		return $setBlur(1);
	}

	public Tween $resetBlur() {
		return advancedPerformance ? postprocessor.$resetValue(uniBlur) : emptyTween();
	}

	public Tween $tweenTiltshiftY(float v, float d) {
		return advancedPerformance ? postprocessor.$tween(uniTiltshiftY, v, d) : emptyTween();
	}

	public Tween $tweenTiltshiftYIn(float d) {
		return $tweenTiltshiftY(1, d);
	}

	public Tween $tweenTiltshiftYIn() {
		return $tweenTiltshiftYIn(D);
	}

	public Tween $tweenTiltshiftYOut(float d) {
		return advancedPerformance ? postprocessor.$tweenOut(uniTiltshiftY, d) : emptyTween();
	}

	public Tween $tweenTiltshiftYOut() {
		return $tweenTiltshiftYIn(D);
	}

	public Tween $setTiltshiftY(float v) {
		return advancedPerformance ? postprocessor.$setValue(uniTiltshiftY, v) : emptyTween();
	}

	public Tween $setTiltshiftY() {
		return $setTiltshiftY(1);
	}

	public Tween $resetTiltshiftY() {
		return advancedPerformance ? postprocessor.$resetValue(uniTiltshiftY) : emptyTween();
	}

	public Tween $tweenTiltshiftX(float v, float d) {
		return advancedPerformance ? postprocessor.$tween(uniTiltshiftX, v, d) : emptyTween();
	}

	public Tween $tweenTiltshiftXIn(float d) {
		return $tweenTiltshiftX(1, d);
	}

	public Tween $tweenTiltshiftXIn() {
		return $tweenTiltshiftXIn(D);
	}

	public Tween $tweenTiltshiftXOut(float d) {
		return advancedPerformance ? postprocessor.$tweenOut(uniTiltshiftX, d) : emptyTween();
	}

	public Tween $tweenTiltshiftXOut() {
		return $tweenTiltshiftXIn(D);
	}

	public Tween $setTiltshiftX(float v) {
		return advancedPerformance ? postprocessor.$setValue(uniTiltshiftX, v) : emptyTween();
	}

	public Tween $setTiltshiftX() {
		return $setTiltshiftX(1);
	}

	public Tween $resetTiltshiftX() {
		return advancedPerformance ? postprocessor.$resetValue(uniTiltshiftX) : emptyTween();
	}*/

	/** Creates a tween to fade the {@link Overlay} sheers layer color.
	 * @param clr color
	 * @param d duration
	 * @return tween handle */
	public Tween $fadeSheersColor(Color clr, float d) {
		return overlay.$tweenSheersColor(clr, d);
	}

	/** Creates a tween to fade the {@link Overlay} sheers layer color.
	 * @param clr color
	 * @return tween handle */
	public Tween $fadeSheersColor(Color clr) {
		return $fadeSheersColor(clr, C_D);
	}

	/** Creates a handle to set the color and fade the {@link Overlay} sheers layer.
	 * @param clr color
	 * @param v value
	 * @param d duration
	 * @return tween handle */
	public Tween $fadeSheers(Color clr, float v, float d) {
		return overlay.$fadeSheers(clr, v, d);
	}

	/** Creates a handle to set the color and fade the {@link Overlay} sheers layer.
	 * @param clr color
	 * @param v value
	 * @return tween handle */
	public Tween $fadeSheers(Color clr, float v) {
		return $fadeSheers(clr, v, C_D);
	}

	/** Creates a handle to fade the {@link Overlay} sheers layer.
	 * @param v value
	 * @param d duration
	 * @return tween handle */
	public Tween $fadeSheers(float v, float d) {
		return $fadeSheers(black, v, d);
	}

	/** Creates a tween to fade the {@link Overlay} sheers layer.
	 * @param v value
	 * @return tween handle */
	public Tween $fadeSheers(float v) {
		return $fadeSheers(black, v, C_D);
	}

	/** Creates a tween to fade the {@link Overlay} sheers layer to 1.
	 * @return tween handle */
	public Tween $fadeSheersIn() {
		return $fadeSheers(1);
	}

	/** Creates a tween to fade the {@link Overlay} sheers layer to 0.
	 * @param d duration
	 * @return tween handle */
	public Tween $fadeSheersOut(float d) {
		return overlay.$fadeSheersOut(d);
	}

	/** Creates a tween to fade the {@link Overlay} sheers layer to 0.
	 * @return tween handle */
	public Tween $fadeSheersOut() {
		return $fadeSheersOut(C_D);
	}

	/** Creates a handle to set the color and fade the {@link Overlay} sheers layer to 1 and to 0 successively.
	 * @param clr color
	 * @param v value
	 * @param id in delay
	 * @param od out delay
	 * @return tween handle */
	public Timeline $fadeSheersInOut(Color clr, float v, float id, float od) {
		return Timeline.createSequence()
				.push($fadeSheers(clr, v, id))
				.push($fadeSheersOut(od));
	}

	/** Creates a handle to set the color and fade the {@link Overlay} sheers layer to 1 and to 0 successively.
	 * @param clr color
	 * @param v value
	 * @return tween handle */
	public Timeline $fadeSheersInOut(Color clr, float v) {
		return $fadeSheersInOut(clr, v, C_TD, C_D);
	}

	/** Creates a tween that flashes the {@link Overlay} sheers layer to 1 and to 0 successively.
	 * @param clr color
	 * @return tween handle */
	public Timeline $fadeSheersInOut(Color clr) {
		return $fadeSheersInOut(clr, OL_FLASH_OP, C_TD, C_D);
	}

	/** Creates a handle to set the color and fade the {@link Overlay} sheers layer in and to the specified opacity successively.
	 * @param clr color
	 * @param iv in value
	 * @param ov out value
	 * @param id in delay
	 * @param od out delay
	 * @return tween handle */
	public Timeline $fadeSheersInTo(Color clr, float iv, float ov, float id, float od) {
		return Timeline.createSequence()
				.push($fadeSheers(clr, iv, id))
				.push($fadeSheers(clr, ov, od));
	}

	/** Creates a handle to set the color and fade the {@link Overlay} sheers layer in and to the specified opacity successively.
	 * @param clr color
	 * @param iv in value
	 * @param ov out value
	 * @return tween handle */
	public Timeline $fadeSheersInTo(Color clr, float iv, float ov) {
		return $fadeSheersInTo(clr, iv, ov, C_TD, C_D);
	}

	/** Creates a handle to set the color and fade the {@link Overlay} sheers layer in and to the specified opacity successively.
	 * @param clr color
	 * @param ov out value
	 * @return tween handle */
	public Timeline $fadeSheersInTo(Color clr, float ov) {
		return $fadeSheersInTo(clr, OL_FLASH_OP, ov, C_TD, C_D);
	}

	/** Creates a handle to set the color and the {@link Overlay} sheers layer opacity.
	 * @param clr color
	 * @param v value
	 * @return tween handle */
	public Tween $setSheers(Color clr, float v) {
		return overlay.$setSheers(clr, v);
	}

	/** Creates a handle to set the {@link Overlay} sheers layer opacity.
	 * @param v value
	 * @return tween handle */
	public Tween $setSheers(float v) {
		return $setSheers(black, v);
	}

	/** Creates a handle to set the {@link Overlay} sheers layer opacity to 1.
	 * @return tween handle */
	public Tween $setSheers() {
		return $setSheers(black, 1);
	}

	/** Creates a handle to set the {@link Overlay} sheers layer opacity to 0.
	 * @return tween handle */
	public Tween $resetSheers() {
		return overlay.$resetSheers();
	}

	/** Creates a tween to fade the {@link Overlay} blackouts layer color.
	 * @param clr color
	 * @param d duration
	 * @return tween handle */
	public Tween $tweenBoColor(Color clr, float d) {
		return overlay.$tweenBoColor(clr, d);
	}

	/** Creates a tween to fade the {@link Overlay} blackouts layer color.
	 * @param clr color
	 * @return tween handle */
	public Tween $tweenBoColor(Color clr) {
		return $tweenBoColor(clr, C_D);
	}

	/** Creates a handle to set the color and fade the {@link Overlay} blackouts layer.
	 * @param clr color
	 * @param v value
	 * @param d duration
	 * @return tween handle */
	public Tween $fadeBo(Color clr, float v, float d) {
		return overlay.$fadeBo(clr, v, d);
	}

	/** Creates a handle to set the color and fade the {@link Overlay} blackouts layer.
	 * @param clr color
	 * @param v value
	 * @return tween handle */
	public Tween $fadeBo(Color clr, float v) {
		return $fadeBo(clr, v, C_D);
	}

	/** Creates a handle to fade the {@link Overlay} blackouts layer.
	 * @param v value
	 * @param d duration
	 * @return tween handle */
	public Tween $fadeBo(float v, float d) {
		return $fadeBo(black, v, d);
	}

	/** Creates a tween to fade the {@link Overlay} blackouts layer.
	 * @param v value
	 * @return tween handle */
	public Tween $fadeBo(float v) {
		return $fadeBo(black, v, C_D);
	}

	/** Creates a tween to fade the {@link Overlay} blackouts layer to 1.
	 * @return tween handle */
	public Tween $fadeBoIn() {
		return $fadeBo(1);
	}

	/** Creates a tween to fade the {@link Overlay} blackouts layer to 0.
	 * @param d duration
	 * @return tween handle */
	public Tween $fadeBoOut(float d) {
		return overlay.$fadeBoOut(d);
	}

	/** Creates a tween to fade the {@link Overlay} blackouts layer to 0.
	 * @return tween handle */
	public Tween $fadeBoOut() {
		return $fadeBoOut(C_D);
	}

	/** Creates a handle to set the color and fade the {@link Overlay} blackouts layer to 1 and to 0 successively.
	 * @param clr color
	 * @param v value
	 * @param id in delay
	 * @param od out delay
	 * @return tween handle */
	public Timeline $fadeBoInOut(Color clr, float v, float id, float od) {
		return Timeline.createSequence()
				.push($fadeBo(clr, v, id))
				.push($fadeBoOut(od));
	}

	/** Creates a handle to set the color and fade the {@link Overlay} blackouts layer to 1 and to 0 successively.
	 * @param clr color
	 * @param v value
	 * @return tween handle */
	public Timeline $fadeBoInOut(Color clr, float v) {
		return $fadeBoInOut(clr, v, C_TD, C_BD);
	}

	/** Creates a handle to set the color and the {@link Overlay} blackouts layer opacity.
	 * @param clr color
	 * @param v value
	 * @return tween handle */
	public Tween $setBo(Color clr, float v) {
		return overlay.$setBlackouts(clr, v);
	}

	/** Creates a handle to set the {@link Overlay} blackouts layer opacity.
	 * @param v value
	 * @return tween handle */
	public Tween $setBlackouts(float v) {
		return $setBo(black, v);
	}

	/** Creates a handle to set the {@link Overlay} blackouts layer opacity to 1.
	 * @return tween handle */
	public Tween $setBlackouts() {
		return $setBo(black, 1);
	}

	/** Creates a handle to set the {@link Overlay} blackouts layer opacity to 0.
	 * @return tween handle */
	public Tween $resetBo() {
		return overlay.$resetBlackouts();
	}

	/** Creates a tween to fade the {@link Overlay} tint layer color.
	 * @param clr color
	 * @param d duration
	 * @return tween handle */
	public Tween $tweenTintColor(Color clr, float d) {
		return overlay.$tweenTintColor(clr, d);
	}

	/** Creates a tween to fade the {@link Overlay} tint layer color.
	 * @param clr color
	 * @return tween handle */
	public Tween $tweenTintColor(Color clr) {
		return $tweenTintColor(clr, C_D);
	}

	/** Creates a handle to set the color and fade the {@link Overlay} tint layer.
	 * @param clr color
	 * @param v value
	 * @param d duration
	 * @return tween handle */
	public Tween $fadeTint(Color clr, float v, float d) {
		return overlay.$fadeTint(clr, v, d);
	}

	/** Creates a handle to set the color and fade the {@link Overlay} tint layer.
	 * @param clr color
	 * @param v value
	 * @return tween handle */
	public Tween $fadeTint(Color clr, float v) {
		return $fadeTint(clr, v, C_D);
	}

	/** Creates a handle to fade the {@link Overlay} tint layer.
	 * @param v value
	 * @param d duration
	 * @return tween handle */
	public Tween $fadeTint(float v, float d) {
		return $fadeTint(black, v, d);
	}

	/** Creates a tween to fade the {@link Overlay} tint layer.
	 * @param v value
	 * @return tween handle */
	public Tween $fadeTint(float v) {
		return $fadeTint(black, v, C_D);
	}

	/** Creates a tween to fade the {@link Overlay} tint layer to 1.
	 * @return tween handle */
	public Tween $fadeTintIn() {
		return $fadeTint(1);
	}

	/** Creates a tween to fade the {@link Overlay} tint layer to 0.
	 * @param d duration
	 * @return tween handle */
	public Tween $fadeTintOut(float d) {
		return overlay.$fadeTintOut(d);
	}

	/** Creates a tween to fade the {@link Overlay} tint layer to 0.
	 * @return tween handle */
	public Tween $fadeTintOut() {
		return $fadeTintOut(C_D);
	}

	/** Creates a handle to set the color and fade the {@link Overlay} tint layer to 1 and to 0 successively.
	 * @param clr color
	 * @param v value
	 * @param id in delay
	 * @param od out delay
	 * @return tween handle */
	public Timeline $fadeTintInOut(Color clr, float v, float id, float od) {
		return Timeline.createSequence()
				.push($fadeTint(clr, v, id))
				.push($fadeTintOut(od));
	}

	/** Creates a handle to set the color and fade the {@link Overlay} tint layer to 1 and to 0 successively.
	 * @param clr color
	 * @param v value
	 * @return tween handle */
	public Timeline $fadeTintInOut(Color clr, float v) {
		return $fadeTintInOut(clr, v, C_TD, C_D);
	}

	/** Creates a handle to set the color and the {@link Overlay} tint layer opacity.
	 * @param clr color
	 * @param v value
	 * @return tween handle */
	public Tween $setTint(Color clr, float v) {
		return overlay.$setTint(clr, v);
	}

	/** Creates a handle to set the {@link Overlay} tint layer opacity.
	 * @param v value
	 * @return tween handle */
	public Tween $setTint(float v) {
		return $setTint(black, v);
	}

	/** Creates a handle to set the {@link Overlay} tint layer opacity to 1.
	 * @return tween handle */
	public Tween $setTint() {
		return $setTint(black, 1);
	}

	/** Creates a handle to set the {@link Overlay} tint layer opacity to 0.
	 * @return tween handle */
	public Tween $resetTint() {
		return overlay.$resetTint();
	}

	/** Creates a tween to fade the {@link Overlay} vignette layer color.
	 * @param clr color
	 * @param d duration
	 * @return tween handle */
	public Tween $tweenVignetteColor(Color clr, float d) {
		return overlay.$tweenVignetteColor(clr, d);
	}

	/** Creates a tween to fade the {@link Overlay} vignette layer color.
	 * @param clr color
	 * @return tween handle */
	public Tween $tweenVignetteColor(Color clr) {
		return overlay.$tweenVignetteColor(clr, C_D);
	}

	/** Creates a handle to set the color and fade the {@link Overlay} vignette layer.
	 * @param clr color
	 * @param v value
	 * @param d duration
	 * @return tween handle */
	public Tween $fadeVignette(Color clr, float v, float d) {
		return overlay.$fadeVignette(clr, v, d);
	}

	/** Creates a handle to set the color and fade the {@link Overlay} vignette layer.
	 * @param clr color
	 * @param v value
	 * @return tween handle */
	public Tween $fadeVignette(Color clr, float v) {
		return $fadeVignette(clr, v, C_D);
	}

	/** Creates a handle to set the color and fade the {@link Overlay} vignette layer.
	 * @param clr color
	 * @return tween handle */
	public Tween $fadeVignette(Color clr) {
		return $fadeVignette(clr, OL_VIG_OP, C_D);
	}

	/** Creates a handle to fade the {@link Overlay} vignette layer.
	 * @param v value
	 * @param d duration
	 * @return tween handle */
	public Tween $fadeVignette(float v, float d) {
		return $fadeVignette(black, v, d);
	}

	/** Creates a tween to fade the {@link Overlay} vignette layer.
	 * @param d duration
	 * @return tween handle */
	public Tween $fadeVignetteIn(float d) {
		return $fadeVignette(black, OL_VIG_OP, d);
	}

	/** Creates a tween to fade the {@link Overlay} vignette layer to 1.
	 * @return tween handle */
	public Tween $fadeVignetteIn() {
		return $fadeVignetteIn(C_D);
	}

	/** Creates a tween to fade the {@link Overlay} vignette layer to 0.
	 * @param d duration
	 * @return tween handle */
	public Tween $fadeVignetteOut(float d) {
		return overlay.$fadeVignetteOut(d);
	}

	/** Creates a tween to fade the {@link Overlay} vignette layer to 0.
	 * @return tween handle */
	public Tween $fadeVignetteOut() {
		return $fadeVignetteOut(C_D);
	}

	/** Creates a handle to set the color and fade the {@link Overlay} vignette layer to 1 and to 0 successively.
	 * @param clr color
	 * @param v value
	 * @param id in delay
	 * @param od out delay
	 * @return tween handle */
	public Timeline $fadeVignetteInOut(Color clr, float v, float id, float od) {
		return Timeline.createSequence()
				.push($fadeVignette(clr, v, id))
				.push($fadeVignetteOut(od));
	}

	/** Creates a handle to set the color and fade the {@link Overlay} vignette layer to 1 and to 0 successively.
	 * @param clr color
	 * @param v value
	 * @return tween handle */
	public Timeline $fadeVignetteInOut(Color clr, float v) {
		return $fadeVignetteInOut(clr, v, C_TD, C_BD);
	}

	/** Creates a handle to set the color and fade the {@link Overlay} vignette layer in and to the specified opacity successively.
	 * @param clr color
	 * @param iv in value
	 * @param ov out value
	 * @param id in delay
	 * @param od out delay
	 * @return tween handle */
	public Timeline $fadeVignetteInTo(Color clr, float iv, float ov, float id, float od) {
		return Timeline.createSequence()
				.push($fadeVignette(clr, iv, id))
				.push($fadeVignette(clr, ov, od));
	}

	/** Creates a handle to set the color and fade the {@link Overlay} vignette layer in and to the specified opacity successively.
	 * @param clr color
	 * @param iv in value
	 * @param ov out value
	 * @return tween handle */
	public Timeline $fadeVignetteInTo(Color clr, float iv, float ov) {
		return $fadeVignetteInTo(clr, iv, ov, C_TD, C_D);
	}

	/** Creates a handle to set the color and fade the {@link Overlay} vignette layer in and to the specified opacity successively.
	 * @param clr color
	 * @param ov out value
	 * @return tween handle */
	public Timeline $fadeVignetteInTo(Color clr, float ov) {
		return $fadeVignetteInTo(clr, OL_VIG_OP, ov, C_TD, C_D);
	}

	/** Creates a handle to set the color and the {@link Overlay} vignette layer opacity.
	 * @param clr color
	 * @param v value
	 * @return tween handle */
	public Tween $setVignette(Color clr, float v) {
		return overlay.$setVignette(clr, v);
	}

	/** Creates a handle to set the {@link Overlay} vignette layer opacity.
	 * @param v value
	 * @return tween handle */
	public Tween $setVignette(float v) {
		return $setVignette(black, v);
	}

	/** Creates a handle to set the {@link Overlay} vignette layer opacity to 1.
	 * @return tween handle */
	public Tween $setVignette() {
		return $setVignette(black, OL_VIG_OP);
	}

	/** Creates a handle to set the {@link Overlay} vignette layer opacity to 0.
	 * @return tween handle */
	public Tween $resetVignette() {
		return overlay.$resetVignette();
	}

	/** Creates a handle to fade the screen and UI layers in after it first sets it black and invisible respectively.
	 * @param fl flags
	 * @param duration
	 * @return tween handle */
	public Timeline $fadeIn(int fl, float d) {
		return Timeline.createSequence()
				.push($setBlackouts())
				.push($resetUiLayersAlpha(fl))
				.beginParallel()
					.push($fadeBoOut(d))
					.push($fadeUiLayersIn(fl, d))
				.end();
	}

	/** Creates a handle to fade the screen and the general UI layers (game and menus) in after it first sets it black and invisible respectively.
	 * @param duration
	 * @return tween handle */
	public Timeline $fadeIn(float d) {
		return $fadeIn(UIL_GENERAL, d);
	}

	/** Creates a handle to fade the screen and UI layers in after it first sets it black and invisible respectively.
	 * @param fl flags
	 * @return tween handle */
	public Timeline $fadeIn(int fl) {
		return $fadeIn(fl, C_OD);
	}

	/** Creates a handle to fade the screen and the general UI layers (game and menus) in after it first sets it black and invisible respectively.
	 * @return tween handle */
	public Timeline $fadeIn() {
		return $fadeIn(C_OD);
	}

	/** Creates a handle to fade the screen to black and fade the specified UI layers out.
	 * @param fl flags
	 * @param d duration
	 * @return tween handle */
	public Timeline $fadeOut(int fl, float d) {
		return Timeline.createParallel()
				.push($fadeBo(1, d))
				.push($fadeUiLayersOut(fl, d));
	}

	/** Creates a handle to fade the screen to black and fade the general UI layers (game and menus) out.
	 * @param d duration
	 * @return tween handle */
	public Timeline $fadeOut(float d) {
		return $fadeOut(UIL_GENERAL, d);
	}

	/** Creates a handle to fade the screen to black and fade the specified UI layers out.
	 * @param fl flags
	 * @return tween handle */
	public Timeline $fadeOut(int fl) {
		return $fadeOut(fl, C_D);
	}

	/** Creates a handle to fade the screen to black and fade the general UI (game and menus) layers out.
	 * @return tween handle */
	public Timeline $fadeOut() {
		return $fadeOut(C_D);
	}

	/** Creates a handle to fade the screen and the specified UI layers out, call the callback and fade it all back in.
	 * @param fli in flags
	 * @param flo out flags
	 * @param id in duration
	 * @param od out duration
	 * @param cb callback
	 * @return tween handle */
	public Timeline $fadeOutIn(int fli, int flo, float id, float od, TweenCallback cb) {
		return Timeline.createSequence()
				.push($fadeOut(fli, id))
				.push(Tween.call(cb))
				.push($fadeIn(flo, od));
	}

	/** Creates a handle to fade the screen and the general UI layers (game and menus) out, call the callback and fade it all back in.
	 * @param id in duration
	 * @param od out duration
	 * @param cb callback
	 * @return tween handle */
	public Timeline $fadeOutIn(float id, float od, TweenCallback cb) {
		return $fadeOutIn(UIL_GENERAL, UIL_GENERAL, id, od, cb);
	}

	/** Creates a handle to fade the screen and the specified UI layers out, call the callback and fade it all back in.
	 * @param fli in flags
	 * @param flo out flags
	 * @param cb callback
	 * @return tween handle */
	public Timeline $fadeOutIn(int fli, int flo, TweenCallback cb) {
		return $fadeOutIn(fli, flo, C_D, C_OD, cb);
	}

	/** Creates a handle to fade the screen and the general UI layers (game and menus) out, call the callback and fade it all back in.
	 * @param cb callback
	 * @return tween handle */
	public Timeline $fadeOutIn(TweenCallback cb) {
		return $fadeOutIn(C_D, C_OD, cb);
	}

	/** Creates a handle to fade the screen and the specified UI layers out, call the first part of the callback, 
	 * fade it all back in and call the second part of the callback.
	 * @param fli in flags
	 * @param flo out flags
	 * @param id in duration
	 * @param od out duration
	 * @param cb callback
	 * @return tween handle */
	public Timeline $fadeOutInTo(int fli, int flo, float id, float od, TweenCallback cb) {
		return Timeline.createSequence()
				.push($fadeOut(fli, id))
				.push($fadeIn(flo, od).setCallback(cb).setCallbackTriggers(BEGIN|COMPLETE));
	}

	/** Creates a handle to fade the screen and the general UI layers (game and menus) out, call the first part of the callback, 
	 * fade it all back in and call the second part of the callback.
	 * @param id in duration
	 * @param od out duration
	 * @param cb callback
	 * @return tween handle */
	public Timeline $fadeOutInTo(float id, float od, TweenCallback cb) {
		return $fadeOutInTo(UIL_GENERAL, UIL_GENERAL, id, od, cb);
	}

	/** Creates a handle to fade the screen and the specified UI layers out, call the first part of the callback, 
	 * fade it all back in and call the second part of the callback.
	 * @param fli in flags
	 * @param flo out flags
	 * @param cb callback
	 * @return tween handle */
	public Timeline $fadeOutInTo(int fli, int flo, TweenCallback cb) {
		return $fadeOutInTo(fli, flo, C_D, C_OD, cb);
	}

	/** Creates a handle to fade the screen and the general UI layers (game and menus) out, call the first part of the callback, 
	 * fade it all back in and call the second part of the callback.
	 * @param cb callback
	 * @return tween handle */
	public Timeline $fadeOutInTo(TweenCallback cb) {
		return $fadeOutInTo(C_D, C_OD, cb);
	}

	/** Creates a handle to fade the screen and the specified UI layers out and call the the callback.
	 * @param fl flags
	 * @param d duration
	 * @param cb callback
	 * @return tween handle */
	public Timeline $fadeTo(int fl, float d, TweenCallback cb) {
		return Timeline.createSequence()
				.push($fadeOut(d))
				.push(Tween.call(cb));
	}

	/** Creates a handle to fade the screen and the specified UI layers out and call the the callback.
	 * @param fl flags
	 * @param cb callback
	 * @return tween handle */
	public Timeline $fadeTo(int fl, TweenCallback cb) {
		return $fadeTo(fl, C_D, cb);
	}

	/** Creates a handle to fade the screen and the general UI layers (game and menus) out and call the the callback.
	 * @param d duration
	 * @param cb callback
	 * @return tween handle */
	public Timeline $fadeTo(float d, TweenCallback cb) {
		return $fadeTo(UIL_GENERAL, C_D, cb);
	}

	/** Creates a handle to fade the screen and the general UI layers (game and menus) out and call the the callback.
	 * @param cb callback
	 * @return tween handle */
	public Timeline $fadeTo(TweenCallback cb) {
		return $fadeTo(C_D, cb);
	}

	/** Creates a tween that flashes the {@link Overlay} sheers layer with specified color.
	 * @param clr color
	 * @return tween handle */
	public Timeline $flashFx(Color clr) {
		return $fadeSheersInOut(clr, OL_FLASH_OP);
	}

	/** Creates a tween that flashes the {@link Overlay} sheers layer with white.
	 * @param clr color
	 * @return tween handle */
	public Timeline $flashFx() {
		return $flashFx(WHITE);
	}

	/** Creates a tween that flashes the {@link Overlay} vignette layer with specified color.
	 * @param clr color
	 * @return tween handle */
	public Timeline $flashFxVig(Color clr) {
		return $fadeVignetteInOut(clr, OL_FLASH_OP);
	}

	/** Creates a tween that flashes the {@link Overlay} vignette layer.
	 * @return tween handle */
	public Timeline $flashFxVig() {
		return $flashFxVig(WHITE);
	}

	/** Creates a handle to darken the menu background.
	 * @param d duration
	 * @return tween handle */
	public Tween $dimIn(float d) {
		return overlay.$dimIn(C_OP_DIM, d);
	}

	/** Creates a handle to darken the menu background.
	 * @return tween handle */
	public Tween $dimIn() {
		return $dimIn(C_D);
	}

	/** Creates a handle to release the background darkening.
	 * @param d duration
	 * @return tween handle */
	public Tween $dimOut(float d) {
		return overlay.$dimOut(d);
	}

	/** Creates a handle to release the background darkening.
	 * @return tween handle */
	public Tween $dimOut() {
		return $dimOut(C_D);
	}

	/** Creates a handle to apply the background darkening.
	 * @return tween handle */
	public Tween $setDim() {
		return $dimIn(0);
	}

	/** Creates a handle to dismiss the background darkening.
	 * @return tween handle */
	public Tween $resetDim() {
		return $dimOut(0);
	}

	/** Creates a handle to lightly darken the menu background.
	 * @param d duration
	 * @return tween handle */
	public Tween $dimInL(float d) {
		return overlay.$dimIn(C_OP_DIM_L, d);
	}

	/** Creates a handle to lightly darken the menu background.
	 * @param d duration
	 * @return tween handle */
	public Tween $dimInL() {
		return $dimInL(C_D);
	}

	/** Creates a handle to apply the light background darkening.
	 * @return tween handle */
	public Tween $setDimL() {
		return $dimInL(0);
	}

	/** @param parent container
	 * @param z z-index
	 * @param cb callback
	 * @param down should it fire on touch down
	 * @see {@link Tapper#set(Group, int, Callback, boolean)} */
	public void setTapper(Group parent, int z, Callback cb, boolean down) {
		tapper.set(parent, z, cb, down);
	}

	/** Sets tapper to fire on touch down.
	 * @param parent container
	 * @param z z-index
	 * @param cb callback
	 * @param down should it fire on touch down
	 * @see {@link Tapper#set(Group, int, Callback, boolean)} */
	public void setTapper(Group parent, int z, Callback cb) {
		tapper.set(parent, z, cb, true);
	}

	/** @param parent container
	 * @param z z-index
	 * @param cb callback
	 * @param down should it fire on touch down
	 * @see {@link Tapper#setOnce(Group, int, Callback, boolean)} */
	public void setTapperOnce(Group parent, int z, Callback cb, boolean down) {
		tapper.setOnce(parent, z, cb, down);
	}

	/** Sets tapper to fire on touch down.
	 * @param parent container
	 * @param z z-index
	 * @param cb callback
	 * @param down should it fire on touch down
	 * @see {@link Tapper#setOnce(Group, int, Callback, boolean)} */
	public void setTapperOnce(Group parent, int z, Callback cb) {
		tapper.setOnce(parent, z, cb, true);
	}

	/** Starts a timer that calls the callback at the start and at the end.
	 * @param d delay
	 * @param cb callback
	 * @return tween handle */
	public Tween setTimer(float d, TweenCallback cb) {
		return $setTimer(d, cb).start(tweenMgr);
	}

	/** Starts a timer that calls the callback at the start and at the end and repeats specified number of times.
	 * @param d delay
	 * @param r repetition
	 * @param cb callback
	 * @return tween handle */
	public Tween setTimer(float d, int r, TweenCallback cb) {
		return $setTimer(d, r, cb).start(tweenMgr);
	}

	/** Starts a timer counted by the timescale that calls the callback at the start and at the end.
	 * @param d delay
	 * @param cb callback
	 * @return tween handle */
	public Tween setTimescaledTimer(float d, TweenCallback cb) {
		return $setTimer(d, cb).start(tscTweenMgr);
	}

	/** Starts a timer counted by the timescale that calls the callback at the start and at the end and repeats specified number of times.
	 * @param d delay
	 * @param r repetition
	 * @param cb callback
	 * @return tween handle */
	public Tween setTimescaledTimer(float d, int r, TweenCallback cb) {
		return $setTimer(d, r, cb).start(tscTweenMgr);
	}

	/** Creates a timer handle that calls the callback at the start and at the end.
	 * @param d duration
	 * @param cb callback
	 * @return tween handle */
	public Tween $setTimer(float d, TweenCallback cb) {
		return delayTween(d)
				.setCallback(cb)
				.setCallbackTriggers(BEGIN|COMPLETE);
	}

	/** Creates a timer handle that calls the callback at the start and at the end and repeats specified number of times.
	 * @param d duration
	 * @param r repetition
	 * @param cb callback
	 * @return tween handle */
	public Tween $setTimer(float d, int r, TweenCallback cb) {
		return $setTimer(d, cb).repeat(r, 0);
	}

	/** Performs the camera shake procedure. */
	private void shakeCamera() {
		// move both camera and UI off their pivots
		if (camShake() > 0) {
			double camAngle = rand(CIRCLE), uiAngle = rand(CIRCLE);
			float camRange = camShake(), uiRange = camRange * .5f;
			Vector3 pos = cameraPos();
			camera.position.set(pos.x + mcos(camAngle, camRange), pos.y + msin(camAngle, camRange), 0);
			uiLayers[UIL_GAME].setPosition(mcos(uiAngle, uiRange), msin(uiAngle, uiRange));
		}
	}

	private void resetCameraPos() {
		cameraPos().set(camBasePos);
		uiLayers[UIL_GAME].setPosition(0, 0);
	}

	protected void update() {
		tweenMgr.update(dt());
		tscTweenMgr.update(dtm());
		uiTweenMgr.update(uiMdt());
		updater.update();
		overlay.update();
		ui.act();
		updateInput();
		// music has to be manually updated when changed through transitions
		if (updatingVolume) {
			for (Music music : musicList) {
				music.setVolume(musicVolume());
			}
		}
	}

	/** Called before {@link #draw()}. */
	protected void drawPre() {
	}

	protected void draw() {
	}

	/** Called after {@link #draw()}. */
	protected void drawPost() {
	}

	protected void drawDebug() {
	}

	protected void drawUi() {
		ui.draw();
	}

	private void clearScreen() {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
	}

	private void setCameraToOrtho(boolean yDown) {
		if (yDown) {
			camera.up.set(0,-1,0);
			camera.direction.set(0,0,1);
		} else {
			camera.up.set(0,1,0);
			camera.direction.set(0,0,-1);
		}
		camera.position.set(camera.zoom*cameraX(), camera.zoom*cameraY(), 0);
	}

	public void render() {
		update();
		setCameraToOrtho(false);
		viewport.apply();
		shakeCamera();
		batch.setProjectionMatrix(camera.combined);
		// when postprocessed, the whole scene is drawn to the buffer and is processed later
		boolean doPostprocess = benchmarkTesting || (blur.isActive() && advancedPerformance && !simpleGraphics);
		if (doPostprocess) {
			frameBuffer.begin();
		}
		clearScreen();
		batch.begin();
			drawPre();
			draw();
			drawPost();
			if (debug) {
				drawDebug();
			}
		batch.end();
		if (doPostprocess) {
			frameBuffer.end();
			blur.draw();
		}
		drawUi();
		resetCameraPos();
		updateBenchmark();
	}

	public void dispose() {
		batch.dispose();
		shapeRenderer.dispose();
		ui.dispose();
		overlay.dispose();
		blur.dispose();
		frameBuffer.dispose();
	}

	public void resize() {
		logSetup("resizing = "+screenWidth()+" "+screenHeight());
		// record the previous screen size
		if (lastScreenSizeJunc) {
			evenLastScreenSize.set(screenWidth(), screenHeight());
		} else {
			oddLastScreenSize.set(screenWidth(), screenHeight());
		}
		lastScreenSizeJunc = !lastScreenSizeJunc;
		viewport.update(screenWidth(), screenHeight(), outputWidth(), outputHeight());
		uiViewport.update(screenWidth(), screenHeight(), outputWidth(), outputHeight(), true);
		overlay.resize();
		tapper.resize();
		blur.resize();
		// frame buffer has to be created here to avoid being created twice every time 
		if (firstResize) {
			firstResize = false;
		} else {
			frameBuffer.dispose();
		}
		frameBuffer = new FrameBuffer(Format.RGB888, frameBufferWidth(), frameBufferHeight(), false);
		logDone();
	}

	/** Called when the app first starts fully working, i.e. after the screen turns on. */
	public void startup() {
	}

	public void show() {
		Gdx.input.setInputProcessor(inputMultiplexer);
	}

	public void hide() {
	}

	public void pause() {
	}

	public void resume() {
	}

	protected void updateInput() {
	}

	@Override public boolean keyDown(int keycode) {
		return false;
	}

	@Override public boolean keyUp(int keycode) {
		return false;
	}

	@Override public boolean keyTyped(char character) {
		return false;
	}

	@Override public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		touchPoint.set(unproject(screenX, screenY));
		return false;
	}

	@Override public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override public boolean scrolled(int amount) {
		return false;
	}

	@Override public boolean touchDown(float x, float y, int pointer, int button) {
		return false;
	}

	@Override public boolean tap(float x, float y, int count, int button) {
		return false;
	}

	@Override public boolean longPress(float x, float y) {
		return false;
	}

	@Override public boolean fling(float velocityX, float velocityY, int button) {
		return false;
	}

	@Override public boolean pan(float x, float y, float deltaX, float deltaY) {
		return false;
	}

	@Override public boolean panStop(float x, float y, int pointer, int button) {
		return false;
	}

	@Override public boolean zoom(float initialDistance, float distance) {
		return false;
	}

	@Override public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
		return false;
	}

	@Override public void pinchStop() {
	}
}

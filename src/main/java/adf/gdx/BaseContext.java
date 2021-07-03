package adf.gdx;

import static adf.gdx.BaseAppListener.*;
import static adf.gdx.Logger.*;
import static adf.gdx.TweenUtil.*;
import static adf.gdx.MathUtil.*;
import static adf.gdx.SceneUtil.*;
import static adf.gdx.Util.*;
import static aurelienribon.tweenengine.TweenCallback.*;

import adf.gdx.utils.Soft;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.primitives.MutableFloat;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
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

import java.util.HashMap;
import java.util.Map;

/**
 * Includes:
 * <ul>
 * <li>Three instances of {@link TweenManager}:<ul><li>General purpose</li>
 * <li>Timescaled</li><li>UI</li></ul></li>
 * <li>Adaptive viewport adjustments.</li>
 * <li>UI layers:<ul><li>Overlay</li><li>Game</li><li>Menus</li></ul></li>
 * <li>Benchmark.</li>
 * <li>Graphics and postprocessing adjustments.</li>
 * <li>Blur.</li>
 * <li>Tween handlers:<ul><li>Timescale</li><li>Sound volume</li><li>Camera</li>
 * <li>UI layers</li><li>Overlay</li><li>Blur</li></ul></li>
 * </ul>
 * @param <AL> a {@link BaseAppListener} instance.
 */
public abstract class BaseContext<AL extends BaseAppListener> implements InputProcessor, GestureListener {
  public static final TweenManager    tweenMgr            = new TweenManager();  // general tween manager
  public static final TweenManager    tscTweenMgr         = new TweenManager();  // timescaled tween manager
  public static final TweenManager    uiTweenMgr          = new TweenManager();  // UI tween manager

  public static final Updater         updater             = new Updater();

  public static void addUpdatable(Updatable a) {
    updater.add(a);
  }

  public static void removeUpdatable(Updatable a) {
    updater.remove(a);
  }

  // The lesser screen dimension which determines the larger dimension by scaling it after the aspect ratio.
  // It can only be changed once during lifetime of the app, preferably at the very start.
  private static float                ldm                 = 720;
  private static boolean              ldmDeadlock;

  /**
   * Changes the lesser screen dimension once during runtime.
   */
  public static void setLdm(float v) {
    if (!ldmDeadlock) {
      log("Set lesser screen dimension to " + v);
      ldmDeadlock = true;    
      ldm = v;
    }
  }

  /**
   * @return lesser screen dimension.
   */
  public static float ldm() {
    return ldm;
  }

  // UI layers
  public static final int             // indices
                                      UI_OVERLAY          = 0,
                                      UI_GAME             = 1,
                                      UI_MENUS            = 2,
                                      UI_LENGTH           = 3,
                                      // flags
                                      UI_OVERLAY_FL       = 1<<0,
                                      UI_GAME_FL          = 1<<1,
                                      UI_MENUS_FL         = 1<<2,
                                      UI_ALL              = sumFlags(UI_MENUS_FL),
                                      UI_GENERAL          = UI_GAME_FL | UI_MENUS_FL;

  // Benchmark is used to determine whether the device is capable of using advanced performance features such as postprocessing.
  public static final String          benchmarkKey        = "is_benchmarked";
  public static float                 benchmarkDuration   = 3;  // duration of benchmark in seconds
  private static boolean              isBenchmarked;      // assigned whether when the benchmark info is loaded or when benchmark is done
  private static boolean              benchmarkTesting;   // whether it is running or not
  private static float                benchmarkTime;      // time spent on benchmark
  private static int                  benchmarkFrames;    // number of frames rendered during benchmark

  public static void loadBenchmarkInfo() {
    if (prefsContain(benchmarkKey)) {
      isBenchmarked = true;
      setAdvancedPerformance(prefb(benchmarkKey));
    }
  }

  protected static boolean isBenchmarked() {
    return isBenchmarked;
  }

  protected static boolean isBenchmarkDone() {
    return !benchmarkTesting;
  }

  public static float benchmarkTime() {
    return benchmarkTime;
  }

  // Indicates whether the app has to run without any advanced graphical features, such as both postprocessing and texture filtering other than nearest.
  private static boolean              simpleGraphics;
  private static boolean              advancedPerformance;  // indicates whether the app may use advanced performance features, such as GPU

  public boolean simpleGraphics() {
    return simpleGraphics;
  }

  public boolean advancedPerformance() {
    return advancedPerformance;
  }

  private static void setAdvancedPerformance(boolean v) {
    log("Advanced performance " + (v ? "enabled" : "disabled"));
    advancedPerformance = v;
  }

  protected final AL                  al;                   // app listener
  protected final SpriteBatch         batch                 = new SpriteBatch();
  protected final OrthographicCamera  camera                = new OrthographicCamera();
  protected final Vector3             camBasePos            = new Vector3();
  protected final AdaptiveViewport    viewport              = new AdaptiveViewport(camera);
  protected final ShapeRenderer       shapeRenderer         = new ShapeRenderer();
  protected final AdaptiveViewport    uiViewport            = new AdaptiveViewport();
  protected final Stage               ui                    = new Stage(uiViewport);
  protected final ShapeRenderer       uiShapeRenderer       = new ShapeRenderer();
  protected final Group[]             uiLayers              = {new Group(), new Group(), new Group()};
  protected final Overlay             ol                    = new Overlay(this);
  protected final Blur                blur                  = new Blur();
  protected final Tapper              tapper                = new Tapper(this);
  protected final Timestamp           timestamp             = new Timestamp();
  protected final InputMultiplexer    inputMultiplexer      = new InputMultiplexer();
  protected final Vector2             touch                 = new Vector2();
  protected FrameBuffer               frameBuffer;
  protected boolean                   drawToFrameBuffer;

  private final MutableFloat          camShake              = new MutableFloat(0);  // camera shake amplitude
  private final MutableFloat          timescale             = new MutableFloat(1);  // time multiplier
  private final MutableFloat          uiTimescale           = new MutableFloat(1);
  private final MutableFloat          soundVolume           = new MutableFloat(1);  // master volume
  private final MutableFloat          sfxVolume             = new MutableFloat(1);  // sound effects volume
  private final Map<String, Sound>    sfxList               = new HashMap<>();
  private final MutableFloat          musicVolume           = new MutableFloat(1);
  private final Map<String, Music>    musicList             = new HashMap<>();
  private boolean                     updatingVolume;
  private final TweenCallback         masterVolumeCallback  = (type, source) -> updatingVolume = type == BEGIN;

  // Temporal values
  protected final Vector2             tmpv2                 = new Vector2();
  protected final Vector3             tmpv3                 = new Vector3();
  protected final Color               tmpclr                = new Color();

  // Last screen size info is stored in two different fields switching after each resize
  private boolean                     lastScreenSizeJunc;
  private final Vector2               evenLastScreenSize    = new Vector2();
  private final Vector2               oddLastScreenSize     = new Vector2();
  private boolean                     firstResize           = true;
  private boolean                     drawDebug;
  protected boolean                   doPostprocess;

  public BaseContext(AL al) {
    this.al = al;
    ui.setViewport(uiViewport);
    // add all UI layers to the stage
    for (Group layer : uiLayers) {
      ui.addActor(layer);
    }
    // ol shouldn't receive input
    uiLayers[UI_OVERLAY].setTouchable(Touchable.disabled);
    timestamp.set();
    // stack all input listeners
    inputMultiplexer.addProcessor(ui);
    inputMultiplexer.addProcessor(this);
    inputMultiplexer.addProcessor(new GestureDetector(this));

    evenLastScreenSize.set(screenWidth(), screenHeight());
    oddLastScreenSize.set(screenWidth(), screenHeight());
  }

  protected void startBenchmark() {
    log("Benchmark started...");
    benchmarkTesting = true;
    benchmarkTime = 0;
    benchmarkFrames = 0;
  }

  protected void finishBenchmark() {
    float fps = benchmarkFrames / (benchmarkTime - 1);
    log("Benchmark finished, fps = " + fps);
    benchmarkTesting = false;
    boolean result = fps > 50;
    setAdvancedPerformance(result);
    putPrefBool(benchmarkKey, result);
    flushPrefs();
  }

  protected void updateBenchmark() {
    if (benchmarkTesting) {
      benchmarkTime += dt();
      if (benchmarkTime >= 1) {
        benchmarkFrames++;
      }
      if (benchmarkTime >= benchmarkDuration + 1) {
        finishBenchmark();
      }
    }
  }

  /**
   * Disables advanced features and changes the texture atlases' filter to nearest.
   */
  protected void enableSimpleGraphics() {
    logSetup("Enable simple graphics");
    simpleGraphics = true;
    for (Texture tex : skin().getAtlas().getTextures()) {
      tex.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
    }
    logDone();
  }

  /**
   * Disables the advanced feature limitation and changes the texture atlases' filter to linear.
   */
  protected void disableSimpleGraphics() {
    logSetup("Disable simple graphics");
    simpleGraphics = false;
    for (Texture tex : skin().getAtlas().getTextures()) {
      tex.setFilter(TextureFilter.Linear, TextureFilter.Linear);
    }
    logDone();
  }

  /**
   * @return the frame buffer texture.
   */
  public Texture fbTex() {
    return frameBuffer.getColorBufferTexture();
  }

  /**
   * Adds actor to the UI layer specified by the index.
   */
  public void addToUiLayer(int i, Actor a) {
    uiLayers[i].addActor(a);
  }

  /**
   * @return the last screen size, i.e. screen size before the last resize.
   */
  public Vector2 lastScreenSize() {
    return lastScreenSizeJunc ? evenLastScreenSize : oddLastScreenSize;
  }

  /**
   * @return x and y of the screen center.
   */
  public Vector2 screenCenter() {
    return tmpv2.set(screenWidth()/2, screenHeight()/2);
  }

  public float screenCenterX() {
    return screenWidth() / 2;
  }

  public float screenCenterY() {
    return screenHeight() / 2;
  }

  /**
   * @return arguments converted from physical screen units into virtual screen units.
   */
  public Vector2 unproject(float x, float y) {
    return viewport.unproject(tmpv2.set(x, y));
  }

  /**
   * Changes this context to another.
   * @param dispose should this context be disposed.
   */
  public void switchToContext(BaseContext<?> context, boolean dispose) {
    al.setContext(context, dispose);
  }

  public void switchToContext(BaseContext<?> context) {
    al.setContext(context, true);
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

  public int fbWidth() {
    return screenWidthI();
  }

  public int fbHeight() {
    return screenHeightI();
  }

  /**
   * @return physical screen width.
   */
  public int outputWidth() {
    return Gdx.graphics.getWidth();
  }

  /**
   * @return physical screen height.
   */
  public int outputHeight() {
    return Gdx.graphics.getHeight();
  }

  /**
   * @return relation of screen width to screen height.
   */
  public float screenRatio() {
    return (float)outputWidth() / outputHeight();
  }

  /**
   * @return relation of screen height to screen width.
   */
  public float inverseScreenRatio() {
    return (float)outputHeight() / outputWidth();
  }

  /**
   * @return is screen width greater than screen height.
   */
  public boolean landscapeOrientation() {
    return screenRatio() > 1;
  }

  /**
   * @return screen width multiplied by {@code a}.
   */
  public float screenXm(float a) {
    return screenWidth() * a;
  }

  /**
   * @return screen height multiplied by {@code a}.
   */
  public float screenYm(float a) {
    return screenHeight() * a;
  }

  /**
   * @return {@code a} divided by screen width.
   */
  public float screenXr(float a) {
    return a / screenWidth();
  }

  /**
   * @return {@code a} divided by screen height.
   */
  public float screenYr(float a) {
    return a / screenHeight();
  }

  /**
   * @return output units converted to screen units.
   */
  public float toScreenUnits(float a) {
    return a * (screenWidth() / outputWidth());
  }

  /**
   * @return screen units converted to output units.
   */
  public float toOutputUnits(float a) {
    return a * (outputWidth() / screenWidth());
  }

  /**
   * Performs the camera shake procedure.
   */
  private void shakeCamera() {
    // move both camera and UI off their pivots
    if (camShake() > 0) {
      float camAngle = randf(PI2), uiAngle = randf(PI2);
      float camRange = camShake(), uiRange = camRange * .5f;
      Vector3 pos = cameraPos();
      camera.position.set(pos.x + cosf(camAngle) * camRange, pos.y + sinf(camAngle) * camRange, 0);
      uiLayers[UI_GAME].setPosition(cosf(uiAngle) * uiRange, sinf(uiAngle) * uiRange);
    }
  }

  private void resetCameraPos() {
    cameraPos().set(camBasePos);
    uiLayers[UI_GAME].setPosition(0, 0);
  }

  protected void update() {
    tweenMgr.update(dt());
    tscTweenMgr.update(dtm());
    uiTweenMgr.update(uiDtm());
    updater.update();

    shapeRenderer.setProjectionMatrix(camera.combined);
    ui.act();
    uiShapeRenderer.setProjectionMatrix(ui.getCamera().combined);
    updateInput();

    // music has to be manually updated when is changed through transitions
    if (updatingVolume) {
      for (Map.Entry<String, Music> e : musicList.entrySet()) {
        e.getValue().setVolume(musicVolume());
      }
    }
  }

  public void toggleDrawDebug() {
    drawDebug = !drawDebug;
  }

  public void toggleDrawUiDebug() {
    ui.setDebugAll(!ui.isDebugAll());
  }

  protected boolean doPostprocess() {
    return benchmarkTesting || (blur.isActive() && advancedPerformance && !simpleGraphics);
  }

  protected void prepareDraw() {
    if (doPostprocess()) {
      blur.begin();
    }
  }

  protected void finalizeDraw() {
    if (doPostprocess()) {
      blur.end();
      blur.process(batch, cameraX0(), cameraY0(), screenWidth(), screenHeight());
      batch.begin();
        batch.draw(blur.outputTex(), cameraX0(), cameraY0(), screenWidth(), screenHeight(), 0,0,1,1);
      batch.end();
    }
  }

  /**
   * Called before {@link #draw()}.
   */
  protected void drawPre() {}

  protected void draw() {}

  /**
   * Called after {@link #draw()}.
   */
  protected void drawPost() {}

  protected void drawDebug() {}

  protected void drawUi() {
    ui.draw();
  }

  protected void clearScreen() {
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
    camera.position.set(camera.zoom * cameraX(), camera.zoom * cameraY(), 0);
  }

  public void render() {
    update();
    setCameraToOrtho(false);
    viewport.apply();
    shakeCamera();
    batch.setProjectionMatrix(camera.combined);
    shapeRenderer.setProjectionMatrix(camera.combined);
    uiShapeRenderer.setProjectionMatrix(ui.getCamera().combined);

    if (drawToFrameBuffer) {
      frameBuffer.begin();
    }
    prepareDraw();
    clearScreen();
    batch.begin();
    drawPre();
    draw();
    drawPost();
    if (drawDebug) {
      drawDebug();
    }
    batch.end();
    finalizeDraw();
    if (drawToFrameBuffer) {
      frameBuffer.end();
    }

    drawUi();
    resetCameraPos();
    updateBenchmark();
  }

  public void dispose() {
    batch.dispose();
    shapeRenderer.dispose();
    ui.dispose();
    uiShapeRenderer.dispose();
    frameBuffer.dispose();
    blur.dispose();
    for (Map.Entry<String, Sound> e : sfxList.entrySet()) {
      e.getValue().dispose();
    }
    for (Map.Entry<String, Music> e : musicList.entrySet()) {
      e.getValue().dispose();
    }
  }

  public void resize() {
    logSetup(String.format("Resize to %.1f %.1f", screenWidth(), screenHeight()));
    // record previous screen size
    if (lastScreenSizeJunc) {
      evenLastScreenSize.set(screenWidth(), screenHeight());
    } else {
      oddLastScreenSize.set(screenWidth(), screenHeight());
    }
    lastScreenSizeJunc = !lastScreenSizeJunc;

    viewport.update(screenWidth(), screenHeight(), outputWidth(), outputHeight());
    uiViewport.update(screenWidth(), screenHeight(), outputWidth(), outputHeight(), true);
    ol.resize();
    tapper.resize();
    blur.resize(fbWidth(), fbHeight());

    // frame buffer has to be created here to avoid creating it twice every time
    if (firstResize) {
      firstResize = false;
    } else {
      frameBuffer.dispose();
    }
    frameBuffer = new FrameBuffer(Format.RGB888, fbWidth(), fbHeight(), false);
    logDone();
  }

  /**
   * Used by {@link BaseAppListener#setContext(BaseContext, boolean)}.
   */
  public void contextChangeSuccess() {
    logDone();
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

  /**
   * A contextual action, such as getting back to the main menu, opening the in-game menu, returning to the parent section, etc.
   */
  public void goBackAction() {}

  public void goBack() {
    Gdx.app.postRunnable(() -> goBackAction());
  }

  /**
   * Prints debug info.
   */
  public void printDebug() {
    log(String.format("screen size = %.1f %.1f, fps = %.1f, cam = %.1f %.1f, cam at zero = %.1f %.1f, time = %d, " +
        "cam shake = %.1f, timescale = %.1f, UI timescale = %.1f, sound vol = %.1f, sfx vol = %.1f, music vol = %.1f",
        screenWidth(), screenHeight(), fps(), cameraPos().x, cameraPos().y, cameraX0(), cameraY0(), timestamp.elapsed(),
        camShake(), timescale(), uiTimescale(), soundVolume(), sfxVolume(), musicVolume()));
  }

  /**
   * Starts a timer that calls the callback at the start and at the end.
   * @param d delay
   * @param cb callback
   * @return tween handle
   */
  public Tween setTimer(float d, TweenCallback cb) {
    return $setTimer(d, cb).start(tweenMgr);
  }

  /**
   * Starts a timer that calls the callback at start and end and repeats the specified number of times.
   * @param d delay
   * @param r repetition
   * @param cb callback
   * @return tween handle
   */
  public Tween setTimer(float d, int r, TweenCallback cb) {
    return $setTimer(d, r, cb).start(tweenMgr);
  }

  /**
   * Starts a timer counted by the timescale that calls the callback at start and end.
   * @param d delay
   * @param cb callback
   * @return tween handle
   */
  public Tween setTimescaledTimer(float d, TweenCallback cb) {
    return $setTimer(d, cb).start(tscTweenMgr);
  }

  /**
   * Starts a timer counted by the timescale that calls the callback at start and end and
   * repeats the specified number of times.
   * @param d delay
   * @param r repetition
   * @param cb callback
   * @return tween handle
   */
  public Tween setTimescaledTimer(float d, int r, TweenCallback cb) {
    return $setTimer(d, r, cb).start(tscTweenMgr);
  }

  /**
   * Creates a timer handle that calls the callback at start and end.
   * @param d duration
   * @param cb callback
   * @return tween handle
   */
  public Tween $setTimer(float d, TweenCallback cb) {
    return delayTween(d)
             .setCallback(cb)
             .setCallbackTriggers(BEGIN|COMPLETE);
  }

  /**
   * Creates a timer handle that calls the callback at the start and at the end and repeats the specified number of times.
   * @param d duration
   * @param r repetition
   * @param cb callback
   * @return tween handle
   */
  public Tween $setTimer(float d, int r, TweenCallback cb) {
    return $setTimer(d, cb).repeat(r, 0);
  }

  /**
   * @return camera shake amplitude.
   */
  public float camShake() {
    return camShake.floatValue();
  }

  /**
   * Fades camera shake amplitude.
   * @param v value
   * @param d duration
   */
  public void fadeCamShake(float v, float d) {
    $fadeCamShake(v, d).start(tweenMgr);
  }

  /**
   * Fades camera shake amplitude.
   * @param v value
   */
  public void fadeCamShake(float v) {
    $fadeCamShake(v).start(tweenMgr);
  }
  
  /**
   * Sets camera shake amplitude.
   * @param v value
   */
  public void setCamShake(float v) {
    tweenMgr.killTarget(camShake);
    camShake.setValue(v);
  }

  /**
   * @return timescale multiplier.
   */
  public float timescale() {
    return timescale.floatValue();
  }

  /**
   * @return time delta multiplied by the timescale.
   */
  public float dtm() {
    return dt() * timescale();
  }

  /**
   * Fades the timescale multiplier.
   * @param v value
   * @param d duration
   */
  public void fadeTimescale(float v, float d) {
    $fadeTimescale(v, d).start(tweenMgr);
  }

  /**
   * Fades the timescale multiplier.
   * @param v value
   */
  public void fadeTimescale(float v) {
    $fadeTimescale(v).start(tweenMgr);
  }

  /**
   * Sets the timescale multiplier.
   * @param v value
   */
  public void setTimescale(float v) {
    tweenMgr.killTarget(timescale);
    timescale.setValue(v);
  }

  /**
   * @return UI timescale multiplier.
   */
  public float uiTimescale() {
    return uiTimescale.floatValue();
  }

  /**
   * @return time delta multiplied by the UI timescale.
   */
  public float uiDtm() {
    return dt() * uiTimescale();
  }

  /**
   * Fades the UI timescale multiplier.
   * @param v value
   * @param d duration
   */
  public void fadeUiTimescale(float v, float d) {
    $fadeUiTimescale(v, d).start(tweenMgr);
  }

  /**
   * Fades the UI timescale multiplier.
   * @param v value
   */
  public void fadeUiTimescale(float v) {
    $fadeUiTimescale(v).start(tweenMgr);
  }

  /**
   * Sets the UI timescale multiplier.
   * @param v value
   */
  public void setUiTimescale(float v) {
    tweenMgr.killTarget(uiTimescale);
    uiTimescale.setValue(v);
  }

  /**
   * Loads {@link Sound} instances from a file and adds them to the list.
   */
  public void loadSfx(FileHandle... files) {
    logSetup("Load sounds " + arrToStrf("'%s'$|, ", files));
    for (FileHandle file : files) {
      sfxList.put(file.nameWithoutExtension(), Gdx.audio.newSound(file));
    }
    logDone();
  }

  /**
   * Loads {@link Sound} instances into the list from an internal file path.
   */
  public void loadSfx(String... paths) {
    FileHandle[] files = new FileHandle[paths.length];
    for (int i=0; i < paths.length; i++) {
      files[i] = internalFile(paths[i]);
    }
    loadSfx(files);
  }

  /**
   * Loads {@link Sound} instances into the list from a list of file paths.
   */
  public void loadSfx(String list) {
    loadSfx(list.split("\\s*\\r?\\n\\s*"));
  }

  /**
   * Loads {@link Sound} instances into the list from a list from an internal text file.
   */
  public void loadSfx(FileHandle file) {
    loadSfx(file.readString());
  }

  /**
   * Clears the sfx list.
   */
  public void clearSfxList() {
    logSetup("Clear sfx list");
    for (Map.Entry<String, Sound> e : sfxList.entrySet()) {
      e.getValue().dispose();
    }
    sfxList.clear();
    logDone();
  }

  /**
   * Loads {@link Music} instances from a file and adds them to the list.
   * All music has to be registered.
   */
  public void loadMusic(FileHandle... files) {
    logSetup("Load music " + arrToStrf("'%s'$|, ", files));
    for (FileHandle file : files) {
      Music music = Gdx.audio.newMusic(file);
      music.setVolume(musicVolume());
      musicList.put(file.nameWithoutExtension(), music);
    }
    logDone();
  }

  /**
   * Loads {@link Music} instances into the list from an internal file path.
   * All music has to be registered.
   */
  public void loadMusic(String... paths) {
    FileHandle[] files = new FileHandle[paths.length];
    for (int i=0; i < paths.length; i++) {
      files[i] = internalFile(paths[i]);
    }
    loadMusic(files);
  }

  /**
   * Loads {@link Music} instances into the list from a list of file paths.
   * All music has to be registered.
   */
  public void loadMusic(String list) {
    loadMusic(list.split("\\s*\\r?\\n\\s*"));
  }

  /**
   * Loads {@link Music} instances into the list from a list from an internal text file.
   * All music has to be registered.
   */
  public void loadMusic(FileHandle file) {
    loadMusic(file.readString());
  }

  /**
   * Clears the music list.
   */
  public void clearMusicList() {
    logSetup("Clear music list");
    for (Map.Entry<String, Music> e : musicList.entrySet()) {
      e.getValue().dispose();
    }
    musicList.clear();
    logDone();
  }

  public Sound sfx(String name) {
    return sfxList.get(name);
  }

  public Music music(String name) {
    return musicList.get(name);
  }

  public float soundVolume() {
    return soundVolume.floatValue() * soundVolume.floatValue();
  }

  /**
   * Fades the sound volume.
   * @param v value
   * @param d duration
   */
  public void fadeSoundVolume(float v, float d) {
    $tweenMasterVolume(v, d).start(tweenMgr);
  }

  /**
   * Fades the sound volume.
   * @param v value
   */
  public void fadeSoundVolume(float v) {
    $tweenMasterVolume(v).start(tweenMgr);
  }

  /**
   * Sets the sound volume.
   * @param v value
   */
  public void setSoundVolume(float v) {
    tweenMgr.killTarget(soundVolume);
    soundVolume.setValue(v);
  }

  /**
   * @return sound effects volume.
   */
  public float sfxVolume() {
    return sfxVolume.floatValue() * sfxVolume.floatValue() * soundVolume();
  }

  /**
   * Fades the sound effects volume.
   * @param v value
   * @param d duration
   */
  public void fadeSfxVolume(float v, float d) {
    $fadeSfxVolume(v, d).start(tweenMgr);
  }

  /**
   * Fades the sound effects volume.
   * @param v value
   */
  public void fadeSfxVolume(float v) {
    $fadeSfxVolume(v).start(tweenMgr);
  }

  /**
   * Sets the sound effects volume.
   * @param v value
   */
  public void setSfxVolume(float v) {
    tweenMgr.killTarget(sfxVolume);
    sfxVolume.setValue(v);
  }

  public float musicVolume() {
    return musicVolume.floatValue() * musicVolume.floatValue() * soundVolume();
  }

  /**
   * Fades the music volume.
   * @param v value
   * @param d duration
   */
  public void tweenMusicVolume(float v, float d) {
    $tweenMusicVolume(v, d).start(tweenMgr);
  }

  /**
   * Fades the music volume.
   * @param v value
   */
  public void tweenMusicVolume(float v) {
    $tweenMusicVolume(v).start(tweenMgr);
  }

  /**
   * Sets the music volume.
   * @param v value
   */
  public void setMusicVolume(float v) {
    tweenMgr.killTarget(musicVolume);
    musicVolume.setValue(clamp(v,0,1));
  }

  /**
   * Sets the music volume to 1.
   */
  public void setupMusicVolume() {
    setMusicVolume(1);
  }

  /**
   * Sets the music volume to 0.
   */
  public void resetMusicVolume() {
    setMusicVolume(0);
  }

  /**
   * @return camera x, y, z at the center.
   */
  public Vector3 cameraPos() {
    return camBasePos;
  }

  /**
   * @return camera x at the center.
   */
  public float cameraX() {
    return cameraPos().x;
  }

  /**
   * @return camera y at the center.
   */
  public float cameraY() {
    return cameraPos().y;
  }

  /**
   * @return x of the bottom left corner of the camera frustum.
   */
  public float cameraX0() {
    return cameraPos().x - screenCenterX();
  }

  /**
   * @return y of the bottom left corner of the camera frustum.
   */
  public float cameraY0() {
    return cameraPos().y - screenCenterY();
  }

  /**
   * @return x, y of the bottom left corner of the camera frustum.
   */
  public Vector2 cameraPos0() {
    return tmpv2.set(cameraX0(), cameraY0());
  }

  /**
   * Sets camera x at the center.
   */
  public void setCameraX(float x) {
    tweenMgr.killTarget(camera, OrthoCameraAccessor.X);
    cameraPos().x = x;
  }

  /**
   * Sets camera x at the center.
   */
  public void setCameraY(float y) {
    // stop all animations related to this
    tweenMgr.killTarget(camera, OrthoCameraAccessor.Y);
    cameraPos().y = y;
  }

  /**
   * Sets camera x, y at the center.
   */
  public void setCameraPos(float x, float y) {
    tweenMgr.killTarget(camera, OrthoCameraAccessor.POS);
    setCameraX(x);
    setCameraY(y);
  }

  /**
   * Sets camera x, y at the center.
   */
  public void setCameraPos(Vector2 pos) {
    setCameraPos(pos.x, pos.y);
  }

  /**
   * Sets camera camera x at the bottom left corner of the frustum.
   */
  public void setCameraX0(float x) {
    setCameraX(x - screenCenterX());
  }

  /**
   * Sets camera camera y at the bottom left corner of the frustum.
   */
  public void setCameraY0(float y) {
    setCameraY(y - screenCenterY());
  }

  /**
   * Sets camera camera x, y at the bottom left corner of the frustum.
   */
  public void setCameraPos0(float x, float y) {
    setCameraPos(x - screenCenterX(), y - screenCenterY());
  }

  /**
   * Sets camera camera x, y at the bottom left corner of the frustum.
   */
  public void setCameraPos0(Vector2 pos) {
    setCameraPos0(pos.x, pos.y);
  }

  /**
   * Slides camera in the position.
   * @param d duration
   */
  public void moveCamera(float x, float y, float d) {
    $moveCamera(x, y, d).start(tweenMgr);
  }

  /**
   * Slides camera in the position.
   */
  public void moveCamera(float x, float y) {
    $moveCamera(x, y).start(tweenMgr);
  }

  /**
   * Fades the camera zoom.
   * @param z zoom
   * @param d duration
   */
  public void zoomCamera(float z, float d) {
    $zoomCamera(z, d).start(tweenMgr);
  }

  /**
   * Fades the camera zoom.
   * @param z zoom
   */
  public void zoomCamera(float z) {
    $zoomCamera(z).start(tweenMgr);
  }

  /**
   * Fades the UI opacity.
   * @param a alpha
   * @param d duration
   */
  public void fadeUi(float a, float d) {
    $fadeUi(a, d).start(tweenMgr);
  }

  /**
   * Fades the specified UI layers' opacity.
   * @param fl flags
   * @param a alpha
   * @param d duration
   */
  public void fadeUiLayers(int fl, float a, float d) {
    $fadeUiLayers(fl, a, d).start(tweenMgr);
  }

  /**
   * Fades the specified UI layers' opacity.
   * @param fl flags
   * @param a alpha
   */
  public void fadeUiLayers(int fl, float a) {
    $fadeUiLayers(fl, a, C_D).start(tweenMgr);
  }

  /**
   * Sets the specified UI layers' opacity.
   * @param fl flags
   * @param a alpha
   */
  public void setUiLayersAlpha(int fl, float a) {
  for (int i = 0; i < UI_LENGTH; i++) {
    if (hasFlag(fl, 1<<i)) {
        tweenMgr.killTarget(uiLayers[i], ActorAccessor.A);
        setAlpha(uiLayers[i], a);
      }
    }
  }

  /**
   * Fades the specified UI layer's opacity.
   * @param i index
   * @param a alpha
   * @param d duration
   */
  public void fadeUiLayer(int i, float a, float d) {
    $fadeUiLayer(i, a, d).start(tweenMgr);
  }

  /**
   * Sets the specified UI layer's opacity to 1.
   * @param i index
   * @param a alpha
   */
  public void setUiLayerAlpha(int i, float a) {
    $setUiLayerAlpha(i, a).start(tweenMgr);
  }

  /**
   * Creates a handle to fade the camera shake amplitude.
   * @param v value
   * @param d duration
   * @return tween handle
   */
  public Tween $fadeCamShake(float v, float d) {
    tweenMgr.killTarget(camShake);
    return Tween.to(camShake, 0, d).target(v).ease(Soft.INOUT);
  }

  /**
   * Creates a handle to fade the camera shake amplitude.
   * @param v value
   * @return tween handle
   */
  public Tween $fadeCamShake(float v) {
    return $fadeCamShake(v, C_D);
  }

  /**
   * Creates a handle to set the camera shake amplitude.
   * @param v value
   * @return tween handle
   */
  public Tween $setCamShake(float v) {
    return $fadeCamShake(v, 0);
  }

  /**
   * Creates a handle to fade the timescale multiplier.
   * @param v value
   * @param d duration
   * @return tween handle
   */
  public Tween $fadeTimescale(float v, float d) {
    tweenMgr.killTarget(timescale);
    return Tween.to(timescale, 0, d).target(v).ease(Soft.INOUT);
  }

  /**
   * Creates a handle to fade the timescale multiplier.
   * @param v value
   * @return tween handle
   */
  public Tween $fadeTimescale(float v) {
    return $fadeTimescale(v, C_D);
  }

  /**
   * Creates a handle to set the timescale multiplier.
   * @param v value
   * @return tween handle
   */
  public Tween $setTimescale(float v) {
    return $fadeTimescale(v, 0);
  }

  /**
   * Creates a handle to fade the UI timescale multiplier.
   * @param v value
   * @param d duration
   * @return tween handle
   */
  public Tween $fadeUiTimescale(float v, float d) {
    tweenMgr.killTarget(uiTimescale);
    return Tween.to(uiTimescale, 0, d).target(v).ease(Soft.INOUT);
  }

  /**
   * Creates a handle to fade the UI timescale multiplier.
   * @param v value
   * @return tween handle
   */
  public Tween $fadeUiTimescale(float v) {
    return $fadeUiTimescale(v, C_D);
  }

  /**
   * Creates a handle to set the UI timescale multiplier.
   * @param v value
   * @return tween handle
   */
  public Tween $setUiTimescale(float v) {
    return $fadeUiTimescale(v, 0);
  }

  /**
   * Creates a handle to fade the sound volume.
   * @param v value
   * @param d duration
   * @return tween handle
   */
  public Tween $tweenMasterVolume(float v, float d) {
    tweenMgr.killTarget(soundVolume);
    return Tween.to(soundVolume, 0, d).target(v).ease(Soft.INOUT)
             .setCallbackTriggers(BEGIN|COMPLETE).setCallback(masterVolumeCallback);
  }

  /**
   * Creates a handle to fade the sound volume.
   * @param v value
   * @return tween handle
   */
  public Tween $tweenMasterVolume(float v) {
    return $tweenMasterVolume(v, C_MD);
  }

  /**
   * Creates a handle to set the sound volume.
   * @param v value
   * @return tween handle
   */
  public Tween $setMasterVolume(float v) {
    return $tweenMasterVolume(v, 0);
  }

  /**
   * Creates a handle to fade the sound effects volume.
   * @param v value
   * @param d duration
   * @return tween handle
   */
  public Tween $fadeSfxVolume(float v, float d) {
    tweenMgr.killTarget(sfxVolume);
    return Tween.to(sfxVolume, 0, d).target(v).ease(Soft.INOUT);
  }

  /**
   * Creates a handle to fade the sound effects volume.
   * @param v value
   * @return tween handle
   */
  public Tween $fadeSfxVolume(float v) {
    return $fadeSfxVolume(v, C_MD);
  }

  /**
   * Creates a handle to set the sound effects volume.
   * @param v value
   * @return tween handle
   */
  public Tween $setSfxVolume(float v) {
    return $fadeSfxVolume(v, 0);
  }

  /**
   * Creates a handle to fade the music volume.
   * @param v value
   * @param d duration
   * @return tween handle
   */
  public Tween $tweenMusicVolume(float v, float d) {
    tweenMgr.killTarget(musicVolume);
    return Tween.to(musicVolume, 0, d).target(v).ease(Soft.INOUT)
             .setCallbackTriggers(BEGIN|COMPLETE).setCallback(masterVolumeCallback);
  }

  /**
   * Creates a handle to fade the music volume.
   * @param v value
   * @return tween handle
   */
  public Tween $tweenMusicVolume(float v) {
    return $tweenMusicVolume(v, C_MD);
  }

  /**
   * Creates a handle to set the music volume.
   * @param v value
   * @return tween handle
   */
  public Tween $setMusicVolume(float v) {
    return $tweenMusicVolume(v, 0);
  }

  /**
   * Creates a handle to slide the camera x.
   * @param d duration
   * @return tween handle
   */
  public Tween $moveCameraX(float x, float d) {
    tweenMgr.killTarget(camera, OrthoCameraAccessor.X);
    return Tween.to(camera, OrthoCameraAccessor.X, d).target(x).ease(Soft.INOUT);
  }

  /**
   * Creates a handle to slide the camera x.
   * @return tween handle
   */
  public Tween $moveCameraX(float x) {
    return $moveCameraX(x, C_D);
  }

  /**
   * Creates a handle to fade to slide the camera y.
   * @param d duration
   * @return tween handle
   */
  public Tween $moveCameraY(float y, float d) {
    tweenMgr.killTarget(camera, OrthoCameraAccessor.Y);
    return Tween.to(camera, OrthoCameraAccessor.Y, d).target(y).ease(Soft.INOUT);
  }

  /**
   * Creates a handle to fade to slide the camera y.
   * @return tween handle
   */
  public Tween $moveCameraY(float y) {
    return $moveCameraX(y, C_D);
  }

  /**
   * Creates a handle to fade to slide the camera position.
   * @param d duration
   * @return tween handle
   */
  public Tween $moveCamera(float x, float y, float d) {
    tweenMgr.killTarget(camera, OrthoCameraAccessor.POS);
    return Tween.to(camera, OrthoCameraAccessor.POS, d).target(x, y).ease(Soft.INOUT);
  }

  /**
   * Creates a handle to fade to slide the camera position.
   * @return tween handle
   */
  public Tween $moveCamera(float x, float y) {
    return $moveCamera(x, y, C_D);
  }

  /**
   * Creates a handle to fade the camera zoom.
   * @param z zoom
   * @param d duration
   * @return tween handle
   */
  public Tween $zoomCamera(float z, float d) {
    tweenMgr.killTarget(camera, OrthoCameraAccessor.ZOOM);
    return Tween.to(camera, OrthoCameraAccessor.ZOOM, d).target(z).ease(Soft.INOUT);
  }

  /**
   * Creates a handle to fade the camera zoom.
   * @param z zoom
   * @return tween handle
   */
  public Tween $zoomCamera(float z) {
    return $zoomCamera(z, C_D);
  }

  /**
   * Creates a handle to fade the UI opacity.
   * @param a alpha
   * @param d duration
   * @return tween handle
   */
  public Tween $fadeUi(float a, float d) {
    tweenMgr.killTarget(ui.getRoot(), ActorAccessor.A);
    return Tween.to(ui.getRoot(), ActorAccessor.A, d).target(a).ease(Soft.INOUT);
  }

  /**
   * Creates a handle to fade the specified UI layers' opacity.
   * @param fl flags
   * @param a alpha
   * @param d duration
   * @return tween handle
   */
  public Timeline $fadeUiLayers(int fl, float a, float d) {
    Timeline tl = Timeline.createParallel();
    for (int i = 0; i < UI_LENGTH; i++) {
      if (hasFlag(fl, 1<<i)) {
        tweenMgr.killTarget(uiLayers[i], ActorAccessor.A);
        tl.push(Tween.to(uiLayers[i], ActorAccessor.A, d).target(a).ease(Soft.INOUT));
      }
    }
    return tl;
  }

  /**
   * Creates a handle to set the specified UI layers' opacity.
   * @param fl flags
   * @return tween handle
   */
  public Timeline $setUiLayersAlpha(int fl, float v) {
    return $fadeUiLayers(fl, v, 0);
  }

  /**
   * Creates a handle to fade the specified UI layer's opacity.
   * @param i index
   * @param a alpha
   * @param d duration
   * @return tween handle
   */
  public Timeline $fadeUiLayer(int i, float a, float d) {
    return $fadeUiLayers(1<<i, a, d);
  }

  /**
   * Creates a handle to set the specified UI layer's opacity.
   * @param i index
   * @return tween handle
   */
  public Timeline $setUiLayerAlpha(int i, float v) {
    return $fadeUiLayer(i, v, 0);
  }

  /**
   * Fades the screen and UI layers in after it first turns it black and invisible respectively.
   * @param fl flags
   * @param d duration
   */
  public void fadeIn(int fl, float d) {
    ol.killBlackouts();
    $fadeIn(fl, d).start(tweenMgr);
  }

  /**
   * Fades the screen and the general UI layers (game and menus) in after it first turns it black and invisible respectively.
   * @param d duration
   */
  public void fadeIn(float d) {
    ol.killBlackouts();
    $fadeIn(d).start(tweenMgr);
  }

  /**
   * Fades the screen and UI layers in after it first turns it black and invisible respectively.
   * @param fl flags
   */
  public void fadeIn(int fl) {
    ol.killBlackouts();
    $fadeIn(fl).start(tweenMgr);
  }

  /**
   * Fades the screen and the general UI layers (game and menus) in after it first turns it black and invisible respectively.
   */
  public void fadeIn() {
    ol.killBlackouts();
    $fadeIn().start(tweenMgr);
  }

  /**
   * Fades the screen to black and fade the specified UI layers out.
   * @param fl flags
   * @param d duration
   */
  public void fadeOut(int fl, float d) {
    ol.killBlackouts();
    $fadeOut(fl, d).start(tweenMgr);
  }

  /**
   * Fades the screen to black and fade the general UI layers (game and menus) out.
   * @param d duration
   */
  public void fadeOut(float d) {
    ol.killBlackouts();
    $fadeOut(d).start(tweenMgr);
  }

  /**
   * Fades the screen to black and fade the specified UI layers out.
   * @param fl flags
   */
  public void fadeOut(int fl) {
    ol.killBlackouts();
    $fadeOut(fl).start(tweenMgr);
  }

  /**
   * Fades the screen to black and fade the general UI (game and menus) layers out.
   */
  public void fadeOut() {
    ol.killBlackouts();
    $fadeOut().start(tweenMgr);
  }

  /**
   * Fades the screen and the specified UI layers out, call the callback and fade it all back in.
   * @param fli in flags
   * @param flo out flags
   * @param id in duration
   * @param od out duration
   * @param cb callback
   */
  public void fadeOutIn(int fli, int flo, float id, float od, TweenCallback cb) {
    ol.killBlackouts();
    $fadeOutIn(fli, flo, id, od, cb).start(tweenMgr);
  }

  /**
   * Fades the screen and the general UI layers (game and menus) out, call the callback and fade it all back in.
   * @param id in duration
   * @param od out duration
   * @param cb callback
   */
  public void fadeOutIn(float id, float od, TweenCallback cb) {
    ol.killBlackouts();
    $fadeOutIn(id, od, cb).start(tweenMgr);
  }

  /**
   * Fades the screen and the specified UI layers out, call the callback and fade it all back in.
   * @param fli in flags
   * @param flo out flags
   * @param cb callback
   */
  public void fadeOutIn(int fli, int flo, TweenCallback cb) {
    ol.killBlackouts();
    $fadeOutIn(fli, flo, cb).start(tweenMgr);
  }

  /**
   * Fades the screen and the general UI layers (game and menus) out, call the callback and fade it all back in.
   * @param cb callback
   */
  public void fadeOutIn(TweenCallback cb) {
    ol.killBlackouts();
    $fadeOutIn(cb).start(tweenMgr);
  }

  /**
   * Fades the screen and the specified UI layers out and call the the callback.
   * @param fl flags
   * @param d duration
   * @param cb callback
   */
  public void fadeTo(int fl, float d, TweenCallback cb) {
    ol.killBlackouts();
    $fadeTo(fl, d, cb).start(tweenMgr);
  }

  /**
   * Fades the screen and the general UI layers (game and menus) out and call the the callback.
   * @param d duration
   * @param cb callback
   */
  public void fadeTo(float d, TweenCallback cb) {
    ol.killBlackouts();
    $fadeTo(d, cb).start(tweenMgr);
  }

  /**
   * Fades the screen and the general UI layers (game and menus) out and call the the callback.
   * @param cb callback
   */
  public void fadeTo(TweenCallback cb) {
    ol.killBlackouts();
    $fadeTo(cb).start(tweenMgr);
  }

  /**
   * Creates a handle to fade the screen and UI layers in after it first turns it black and invisible respectively.
   * @param fl flags
   * @param d duration
   * @return tween handle
   */
  public Timeline $fadeIn(int fl, float d) {
    return Timeline.createSequence()
             .push(ol.$setBlackouts(1))
             .push($setUiLayersAlpha(fl, 0))
             .beginParallel()
               .push(ol.$fadeBlackouts(0, d))
               .push($fadeUiLayers(fl, 1, d))
             .end();
  }

  /**
   * Creates a handle to fade the screen and the general UI layers (game and menus) in after it first turns it black and invisible respectively.
   * @param d duration
   * @return tween handle
   */
  public Timeline $fadeIn(float d) {
    return $fadeIn(UI_GENERAL, d);
  }

  /**
   * Creates a handle to fade the screen and UI layers in after it first turns it black and invisible respectively.
   * @param fl flags
   * @return tween handle
   */
  public Timeline $fadeIn(int fl) {
    return $fadeIn(fl, C_OD);
  }

  /**
   * Creates a handle to fade the screen and the general UI layers (game and menus) in after it first turns them
   * black and invisible respectively.
   * @return tween handle
   */
  public Timeline $fadeIn() {
    return $fadeIn(C_OD);
  }

  /**
   * Creates a handle to fade the screen to black and fade the specified UI layers out.
   * @param fl flags
   * @param d duration
   * @return tween handle
   */
  public Timeline $fadeOut(int fl, float d) {
    return Timeline.createParallel()
             .push(ol.$fadeBlackouts(1, d))
             .push($fadeUiLayers(fl, 0, d));
  }

  /**
   * Creates a handle to fade the screen to black and fade the general UI layers (game and menus) out.
   * @param d duration
   * @return tween handle
   */
  public Timeline $fadeOut(float d) {
    return $fadeOut(UI_GENERAL, d);
  }

  /**
   * Creates a handle to fade the screen to black and fade the specified UI layers out.
   * @param fl flags
   * @return tween handle
   */
  public Timeline $fadeOut(int fl) {
    return $fadeOut(fl, C_D);
  }

  /**
   * Creates a handle to fade the screen to black and fade the general UI (game and menus) layers out.
   * @return tween handle
   */
  public Timeline $fadeOut() {
    return $fadeOut(C_D);
  }

  /**
   * Creates a handle to fade the screen and the specified UI layers out, call the callback and fade it all back in.
   * @param fli in flags
   * @param flo out flags
   * @param id in duration
   * @param od out duration
   * @param cb callback
   * @return tween handle
   */
  public Timeline $fadeOutIn(int fli, int flo, float id, float od, TweenCallback cb) {
    return Timeline.createSequence()
             .push($fadeOut(fli, id))
             .push(Tween.call(cb))
             .push($fadeIn(flo, od));
  }

  /**
   * Creates a handle to fade the screen and the general UI layers (game and menus) out,
   * call the callback and fade it all back in.
   * @param id in duration
   * @param od out duration
   * @param cb callback
   * @return tween handle
   */
  public Timeline $fadeOutIn(float id, float od, TweenCallback cb) {
    return $fadeOutIn(UI_GENERAL, UI_GENERAL, id, od, cb);
  }

  /**
   * Creates a handle to fade the screen and the specified UI layers out, call the callback and fade it all back in.
   * @param fli in flags
   * @param flo out flags
   * @param cb callback
   * @return tween handle
   */
  public Timeline $fadeOutIn(int fli, int flo, TweenCallback cb) {
    return $fadeOutIn(fli, flo, C_D, C_OD, cb);
  }

  /**
   * Creates a handle to fade the screen and the general UI layers (game and menus) out,
   * call the callback and fade it all back in.
   * @param cb callback
   * @return tween handle
   */
  public Timeline $fadeOutIn(TweenCallback cb) {
    return $fadeOutIn(C_D, C_OD, cb);
  }

  /**
   * Creates a handle to fade the screen and the specified UI layers out and call the the callback.
   * @param fl flags
   * @param d duration
   * @param cb callback
   * @return tween handle
   */
  public Timeline $fadeTo(int fl, float d, TweenCallback cb) {
    return Timeline.createSequence()
             .push($fadeOut(d))
             .push(Tween.call(cb));
  }

  /**
   * Creates a handle to fade the screen and the specified UI layers out and call the the callback.
   * @param fl flags
   * @param cb callback
   * @return tween handle
   */
  public Timeline $fadeTo(int fl, TweenCallback cb) {
    return $fadeTo(fl, C_D, cb);
  }

  /**
   * Creates a handle to fade the screen and the general UI layers (game and menus) out and call the the callback.
   * @param d duration
   * @param cb callback
   * @return tween handle
   */
  public Timeline $fadeTo(float d, TweenCallback cb) {
    return $fadeTo(UI_GENERAL, C_D, cb);
  }

  /**
   * Creates a handle to fade the screen and the general UI layers (game and menus) out and call the the callback.
   * @param cb callback
   * @return tween handle
   */
  public Timeline $fadeTo(TweenCallback cb) {
    return $fadeTo(C_D, cb);
  }

  protected void updateInput() {}

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
    touch.set(unproject(screenX, screenY));
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

  @Override public boolean scrolled(float amountX, float amountY) {
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

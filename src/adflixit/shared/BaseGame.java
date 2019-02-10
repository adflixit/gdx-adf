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

import static adflixit.shared.Logger.*;
import static adflixit.shared.TweenUtils.*;
import static adflixit.shared.Util.*;

import adflixit.shared.TweenUtils.SpriteAccessor;
import adflixit.shared.console.ConCmd;
import adflixit.shared.console.ConVar;
import adflixit.shared.console.Console;
import adflixit.shared.misc.DropShadowDrawable;
import adflixit.shared.misc.DropShadowLabel;
import android.view.View;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.primitives.MutableFloat;
import aurelienribon.tweenengine.primitives.MutableInteger;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import com.google.android.gms.ads.AdView;
import java.util.Arrays;

/**
 * Includes:
 * <ul>
 * <li>Cross-platform API handling.</li>
 * <li>A {@link Console}.</li>
 * <li>{@link Skin} handles.</li>
 * <li>{@link MutableProperties}.</li>
 * <li>Preferences handles.</li>
 * </ul>
 */
public abstract class BaseGame implements ApplicationListener {
  private final XApi noXApi = new XApi() {
    @Override public AdView getAdView() {return null;}
    @Override public void showAd() {}
    @Override public void hideAd() {}
    @Override public void refreshAd() {}
    @Override public void showLeaderboard(String id) {}
    @Override public void showAchievements() {}
    @Override public void shareText(String txt) {}
    @Override public boolean isSignedIn() {return false;}
    @Override public void signIn() {}
    @Override public String getPlayerId() {return "";}
    @Override public long getPersonalBest(String id) {return 0;}
    @Override public long getAllTimeRecord(String id) {return 0;}
    @Override public long getWeeklyRecord(String id) {return 0;}
    @Override public long getDailyRecord(String id) {return 0;}
    @Override public void submitScore(String id, long score) {}
    @Override public void unlockAchievement(String id) {}
    @Override public void quit() {Gdx.app.exit();}
  };

  private static BaseGame               currentInstance;

  public static Class<? extends Actor>  actorClassDescendants; // used as the Actor class signature for the tween engine
  public static boolean                 debug;
  private static XApi                   xApi;
  public static final Console           console = new Console();
  private static Skin                   skin;
  private static MutableProperties      props   = new MutableProperties();
  private static Preferences            prefs;
  protected BaseScreen<?>               screen; // current screen

  public BaseGame() {
    setXApi(noXApi);
  }

  public BaseGame(XApi xApi) {
    setXApi(xApi);
  }

  @Override public void create() {
    currentInstance = this;
    ShaderProgram.pedantic = false;

    Tween.setWaypointsLimit(10);
    Tween.registerAccessor(Actor.class,               new ActorAccessor());
    Tween.registerAccessor(actorClassDescendants,     new ActorAccessor());
    Tween.registerAccessor(Label.class,               new LabelAccessor());
    Tween.registerAccessor(DropShadowLabel.class,     new DropShadowLabelAccessor());
    Tween.registerAccessor(Sprite.class,              new SpriteAccessor());
    Tween.registerAccessor(DropShadowDrawable.class,  new DropShadowDrawableAccessor());
    Tween.registerAccessor(Music.class,               new MusicAccessor());
    Tween.registerAccessor(OrthographicCamera.class,  new OrthoCameraAccessor());
    Tween.registerAccessor(Body.class,                new Box2DBodyAccessor());
    Tween.registerAccessor(View.class,                new AndViewAccessor());
    Tween.registerAccessor(Vector2.class,             new Vector2Accessor());
    Tween.registerAccessor(Vector3.class,             new Vector3Accessor());
    Tween.registerAccessor(Color.class,               new ColorAccessor());
    Tween.registerAccessor(MutableInteger.class,      new MutableInteger(0));
    Tween.registerAccessor(MutableFloat.class,        new MutableFloat(0));

    // default console commands
    ConCmd("prop", args -> {
      try {
        log(prop(args[0]));
      } catch (Exception e) {
        log(e.getLocalizedMessage());
      }
    });
    ConCmd("setprop", args -> {
      try {
        setProp(args[0], arrayToStringf("%s ", Arrays.copyOfRange(args, 1, args.length)));
      } catch (Exception e) {
        log(e.getLocalizedMessage());
      }
    });
    ConCmd("resetprop", args -> {
      try {
        resetProp(args[0]);
      } catch (Exception e) {
        log(e.getLocalizedMessage());
      }
    });
    ConCmd("resetprops", args -> resetProps());
    ConCmd("flushprop", args -> {
      try {
        flushProp(args[0]);
      } catch (Exception e) {
        log(e.getLocalizedMessage());
      }
    });
    ConCmd("flushprops", args -> flushProps());
    ConCmd("proplist", args -> log(propList()));
    ConCmd("pref", args -> log(pref(args[0])));
    ConCmd("setpref", args -> setProp(args[0], arrayToStringf("%s ", Arrays.copyOfRange(args, 1, args.length))));
    ConCmd("flushprefs", args -> flushPrefs());
    ConCmd("quit", args -> quit());
  }

  public static BaseGame getInstance() {
    return currentInstance;
  }

  private static void setXApi(XApi api) {
    xApi = api;
  }

  /**
   * @see XApi#getAdView()
   */
  public static AdView getAdView() {
    return xApi.getAdView();
  }

  /**
   * @see XApi#showAd()
   */
  public static void showAd() {
    logSetup("Showing ad");
    xApi.showAd();
    logDone();
  }

  /**
   * @see XApi#hideAd()
   */
  public static void hideAd() {
    logSetup("Hiding ad");
    xApi.hideAd();
    logDone();
  }

  /**
   * @see XApi#refreshAd()
   */
  public static void refreshAd() {
    logSetup("Refreshing ad");
    xApi.refreshAd();
    logDone();
  }

  /**
   * @see XApi#showLeaderboard(String)
   */
  public static void showLeaderboard(String id) {
    logSetup("Showing leaderboard");
    xApi.showLeaderboard(id);
    logDone();
  }

  /**
   * @see XApi#showAchievements()
   */
  public static void showAchievements() {
    logSetup("Showing achievements");
    xApi.showAchievements();
    logDone();
  }

  /**
   * @see XApi#shareText(String)
   */
  public static void shareText(String txt) {
    logSetup("Sharing text '"+txt+"'");
    xApi.shareText(txt);
    logDone();
  }

  /**
   * @see XApi#isSignedIn()
   */
  public static boolean isSignedIn() {
    return xApi.isSignedIn();
  }

  /**
   * @see XApi#signIn()
   */
  public static void signIn() {
    logSetup("Signing in");
    xApi.signIn();
    logDone();
  }

  /**
   * @see XApi#getPlayerId()
   */
  public static String getPlayerId() {
    return xApi.getPlayerId();
  }

  /**
   * @see XApi#getPersonalBest(String)
   */
  public static long getPersonalBest(String id) {
    log("Retrieving personal best from id "+id);
    return xApi.getPersonalBest(id);
  }

  /**
   * @see XApi#getAllTimeRecord(String)
   */
  public static long getAllTimeRecord(String id) {
    log("Retrieving all time record from id "+id);
    return xApi.getAllTimeRecord(id);
  }

  /**
   * @see XApi#getWeeklyRecord(String)
   */
  public static long getWeeklyRecord(String id) {
    log("Retrieving weekly record from id "+id);
    return xApi.getWeeklyRecord(id);
  }

  /**
   * @see XApi#getDailyRecord(String)
   */
  public static long getDailyRecord(String id) {
    log("Retrieving daily record from id "+id);
    return xApi.getDailyRecord(id);
  }

  /**
   * @see XApi#submitScore(String, long)
   */
  public static void submitScore(String id, long score) {
    logSetup("Submitting score "+score);
    xApi.submitScore(id, score);
    logDone();
  }

  /**
   * @see XApi#unlockAchievement(String)
   */
  public static void unlockAchievement(String id) {
    logSetup("Unlocking achievement");
    xApi.unlockAchievement(id);
    logDone();
  }

  public static void loadSkin(FileHandle skinFile, TextureAtlas atlas) {
    logSetup("Loading skin '"+skinFile+"'");
    skin = new Skin(skinFile, atlas);
    logDone();
  }

  public static void ConCmd(String name, ConCmd cmd) {
    try {
      console.registerCommand(name, cmd);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void ConVar(String name, ConVar var) {
    try {
      console.registerVariable(name, var);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static Skin skin() {
    return skin;
  }

  /**
   * @return console variable.
   */
  public static ConVar conVar(String name) {
    return console.var(name);
  }

  /**
   * @return console variable {@code String} value.
   */
  public static String cvS(String name) {
    return conVar(name).raw();
  }

  /**
   * @return console variable {@code int} value.
   */
  public static int cvI(String name) {
    return conVar(name).intValue();
  }

  /**
   * @return console variable {@code long} value.
   */
  public static long cvL(String name) {
    return conVar(name).longValue();
  }

  /**
   * @return console variable {@code float} value.
   */
  public static float cvF(String name) {
    return conVar(name).floatValue();
  }

  /**
   * @return console variable {@code double} value.
   */
  public static double cvD(String name) {
    return conVar(name).doubleValue();
  }

  /**
   * @return console variable {@code boolean} value.
   */
  public static boolean cvB(String name) {
    return conVar(name).boolValue();
  }

  /**
   * Loads the specified properties.
   * @deprecated Not used anymore due to the introduced ability to change properties during runtime.
   */
  @Deprecated
  public static void loadProps(FileHandle... files) {
    logSetup("Loading properties "+arrayToString("'%s'", files));
    props.loadAll(files);
    logDone();
  }

  /**
   * Loads the specified properties.
   */
  public static void loadProps(String path) {
    logSetup("Loading properties '"+path+"'");
    props.load(path);
    logDone();
  }

  /**
   * @return property.
   */
  public static String prop(String key) {
    return props.get(key);
  }

  /**
   * @return property interpreted as {@code int}.
   */
  public static int propi(String key) {
    return props.getInt(key);
  }

  /**
   * @return property interpreted as {@code long}.
   */
  public static long propl(String key) {
    return props.getLong(key);
  }

  /**
   * @return property interpreted as {@code float}.
   * @throws IllegalArgumentException if no entry is found.
   */
  public static float propf(String key) {
    return props.getFloat(key);
  }

  /**
   * @return property interpreted as {@code double}.
   */
  public static double propd(String key) {
    return props.getDouble(key);
  }

  /**
   * @return property interpreted as {@code boolean}.
   */
  public static boolean propb(String key) {
    return props.getBool(key);
  }

  /**
   * Changes the existing property.
   * @return the previous value of the specified key.
   */
  public static Object setProp(String key, String value) {
    log("Changing property '"+key+"' from '"+prop(key)+"' to '"+value+"'");
    return props.set(key, value);
  }

  /**
   * Changes the existing property to an {@code int} value.
   */
  public static void setPropInt(String key, int value) {
    log("Changing property '"+key+"' from '"+prop(key)+"' to '"+value+"'");
    props.setInt(key, value);
    logDone();
  }

  /**
   * Changes the existing property to an {@code long} value.
   */
  public static void setPropLong(String key, long value) {
    log("Changing property '"+key+"' from '"+prop(key)+"' to '"+value+"'");
    props.setLong(key, value);
    logDone();
  }

  /**
   * Changes the existing property to an {@code float} value.
   */
  public static void setPropFloat(String key, float value) {
    log("Changing property '"+key+"' from '"+prop(key)+"' to '"+value+"'");
    props.setFloat(key, value);
    logDone();
  }

  /**
   * Changes the existing property to an {@code double} value.
   */
  public static void setPropDouble(String key, double value) {
    log("Changing property '"+key+"' from '"+prop(key)+"' to '"+value+"'");
    props.setDouble(key, value);
    logDone();
  }

  /**
   * Changes the existing property to an {@code boolean} value.
   */
  public static void setPropBool(String key, boolean value) {
    log("Changing property '"+key+"' from '"+prop(key)+"' to '"+value+"'");
    props.setBool(key, value);
    logDone();
  }

  /**
   * Resets the property to the default value.
   */
  public static void resetProp(String key) {
    logSetup("Resetting property '"+key+"'");
    props.reset(key);
    logDone();
  }

  /**
   * Resets all property to the default values.
   */
  public static void resetProps() {
    logSetup("Resetting properties");
    props.resetAll();
    logDone();
  }

  /**
   * Saves the specified property to the file.
   */
  public static void flushProp(String key) {
    logSetup("Flushing property '"+key+"'");
    props.flush(key);
    logDone();
  }

  /**
   * Saves properties to the file.
   */
  public static void flushProps() {
    logSetup("Flushing properties");
    props.flushAll();
    logDone();
  }

  public static String propList() {
    return props.toString();
  }

  public static void loadPrefs(String name) {
    logSetup("Loading preferences '"+name+"'");
    prefs = Gdx.app.getPreferences(name);
    logDone();
  }

  /**
   * @return do preferences contain the key entry.
   */
  public static boolean prefsContain(String key) {
    return prefs.contains(key);
  }

  /**
   * @return preferences entry interpreted as string.
   */
  public static String pref(String key) {
    return prefs.getString(key);
  }

  /**
   * @return preferences entry interpreted as int.
   */
  public static int prefi(String key) {
    return prefs.getInteger(key);
  }

  /**
   * @return preferences entry interpreted as long.
   */
  public static long prefl(String key) {
    return prefs.getLong(key);
  }

  /**
   * @return preferences entry interpreted as float.
   */
  public static float preff(String key) {
    return prefs.getFloat(key);
  }

  /**
   * @return preferences entry interpreted as boolean.
   */
  public static boolean prefb(String key) {
    return prefs.getBoolean(key);
  }

  /**
   * Adds a string entry to the preferences.
   */
  public static void putPref(String key, String value) {
    logSetup("Setting preference "+key+" to "+value);
    prefs.putString(key, value);
    logDone();
  }

  /**
   * Adds an int entry to the preferences.
   */
  public static void putPrefInt(String key, int value) {
    logSetup("Setting preference "+key+" to "+value);
    prefs.putInteger(key, value);
    logDone();
  }

  /**
   * Adds a long entry to the preferences.
   */
  public static void putPrefLong(String key, long value) {
    logSetup("Setting preference "+key+" to "+value);
    prefs.putLong(key, value);
    logDone();
  }

  /**
   * Adds a float entry to the preferences.
   */
  public static void putPrefFloat(String key, float value) {
    logSetup("Setting preference "+key+" to "+value);
    prefs.putFloat(key, value);
    logDone();
  }

  /**
   * Adds a boolean entry to the preferences.
   */
  public static void putPrefBool(String key, boolean value) {
    logSetup("Setting preference "+key+" to "+value);
    prefs.putBoolean(key, value);
    logDone();
  }

  /**
   * Has to be called after every set of preferences is changed.
   */
  public static void flushPrefs() {
    logSetup("Flushing preferences");
    prefs.flush();
    logDone();
  }

  public static int fps() {
    return Gdx.graphics.getFramesPerSecond();
  }

  /**
   * @return rendering time delta.
   */
  public static float dt() {
    return Gdx.graphics.getDeltaTime();
  }

  /**
   * @return {@code a} multiplied by rendering time delta.
   */
  public static float dt(float a) {
    return a * dt();
  }

  public static FileHandle internalFile(String path) {
    return Gdx.files.internal(path);
  }

  public static FileHandle externalFile(String path) {
    return Gdx.files.external(path);
  }

  public static FileHandle absoluteFile(String path) {
    return Gdx.files.absolute(path);
  }

  public static FileHandle localFile(String path) {
    return Gdx.files.local(path);
  }

  /**
   * @return color from the skin.
   */
  public static Color color(String name) {
    return skin.getColor(name);
  }

  /**
   * @return drawable from the skin.
   */
  public static Drawable drawable(String name) {
    return skin.getDrawable(name);
  }

  /**
   * @return texture region from the skin.
   */
  public static TextureRegion texRegion(String name) {
    return skin.getRegion(name);
  }

  /**
   * @return texture regions from the skin.
   */
  public static Array<TextureRegion> texRegions(String name) {
    return skin.getRegions(name);
  }

  /**
   * @return the texture atlas.
   */
  public static TextureAtlas atlas() {
    return skin.getAtlas();
  }

  public static void quit() {
    log("Closing app");
    xApi.quit();
  }

  public void startup() {
    logSetup("Starting up");
    screen.startup();
    logDone();
  }

  @Override public void dispose() {
    logSetup("Disposing of skin");
    skin.dispose();
    logDone();
    if (screen != null) {
      logSetup("Disposing of screen");
      screen.dispose();
      logDone();
    }
    logSetup("Disposing of console");
    console.dispose();
    logDone();
  }

  @Override public void pause() {
    if (screen != null) {
      screen.pause();
    }
  }

  @Override public void resume() {
    if (screen != null) {
      screen.resume();
    }
  }

  @Override public void render() {
    if (screen != null) {
      screen.render();
    }
  }

  @Override public void resize(int width, int height) {
    if (screen != null) {
      screen.resize();
    }
  }

  public void setScreen(BaseScreen<?> newScreen, boolean dispose) {
    if (screen != null) {
      if (dispose) {
        logSetup("Disposing of screen");
        screen.dispose();
        logDone();
      } else {
        logSetup("Hiding screen");
        screen.hide();
        logDone();
      }
    }

    screen = newScreen;

    if (screen != null) {
      log("Setting screen "+newScreen.getClass().getSimpleName());
      screen.show();
      screen.resize();
    }
  }

  public void setScreen(BaseScreen<?> screen) {
    setScreen(screen, true);
  }

  public BaseScreen<?> getScreen() {
    return screen;
  }
}

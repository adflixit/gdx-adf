package adf.gdx;

import static adf.gdx.Logger.*;
import static adf.gdx.TweenUtils.*;
import static adf.gdx.Util.*;

import adf.gdx.TweenUtils.SpriteAccessor;
import adf.gdx.console.ConCmd;
import adf.gdx.console.ConVar;
import adf.gdx.console.Console;
import adf.gdx.utils.DropShadowDrawable;
import adf.gdx.utils.DropShadowLabel;
import android.view.View;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.primitives.MutableFloat;
import aurelienribon.tweenengine.primitives.MutableInteger;
import com.badlogic.gdx.Application.ApplicationType;
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
  private static BaseGame               instance;

  public static Class<? extends Actor>  actorClassDescendants; // used as the Actor class signature for tween engine
  private static AndroidApi             aApi;
  public static final Console           console = new Console();
  private static Skin                   skin;
  private static MutableProperties      props   = new MutableProperties();
  private static Preferences            prefs;
  protected BaseContext<?>              context; // current context

  public BaseGame() {}

  public BaseGame(AndroidApi api) {
    aApi = api;
  }

  @Override public void create() {
    instance = this;
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
    registerCommand("reloadprops", args -> reloadProps());
    registerCommand("prop", args -> {
      try {
        log(prop(args[0]));
      } catch (Exception e) {
        log(e.getLocalizedMessage());
      }
    });
    registerCommand("setprop", args -> {
      try {
        setProp(args[0], arrToStrf("%s ", Arrays.copyOfRange(args, 1, args.length)));
      } catch (Exception e) {
        log(e.getLocalizedMessage());
      }
    });
    registerCommand("resetprop", args -> {
      try {
        resetProp(args[0]);
      } catch (Exception e) {
        log(e.getLocalizedMessage());
      }
    });
    registerCommand("resetprops", args -> resetProps());
    registerCommand("flushprop", args -> {
      try {
        flushProp(args[0]);
      } catch (Exception e) {
        log(e.getLocalizedMessage());
      }
    });
    registerCommand("flushprops", args -> flushProps());
    registerCommand("proplist", args -> log(propList()));
    registerCommand("pref", args -> log(pref(args[0])));
    registerCommand("setpref", args -> setProp(args[0], arrToStrf("%s ", Arrays.copyOfRange(args, 1, args.length))));
    registerCommand("flushprefs", args -> flushPrefs());
    registerCommand("quit", args -> Gdx.app.exit());
  }

  public static BaseGame getInstance() {
    return instance;
  }

  public static AndroidApi aApi() {
    return aApi;
  }

  public static void loadSkin(FileHandle skinFile, FileHandle atlas) {
    logSetup(String.format("Loading skin '%s'", skinFile);
    skin = new Skin(skinFile, new TextureAtlas(atlas));
    logDone();
  }

  public static void registerCommand(String name, ConCmd cmd) {
    try {
      console.registerCommand(name, cmd);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void registerVariable(String name, ConVar var) {
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
  public static String cvs(String name) {
    return conVar(name).rawValue();
  }

  /**
   * @return console variable {@code int} value.
   */
  public static int cvi(String name) {
    return conVar(name).intValue();
  }

  /**
   * @return console variable {@code long} value.
   */
  public static long cvl(String name) {
    return conVar(name).longValue();
  }

  /**
   * @return console variable {@code float} value.
   */
  public static float cvf(String name) {
    return conVar(name).floatValue();
  }

  /**
   * @return console variable {@code double} value.
   */
  public static double cvd(String name) {
    return conVar(name).doubleValue();
  }

  /**
   * @return console variable {@code boolean} value.
   */
  public static boolean cvb(String name) {
    return conVar(name).boolValue();
  }

  /**
   * Loads the specified properties.
   */
  public static void loadProps(String path) {
    logSetup(String.format("Load properties '%s'", path);
    props.load(path);
    logDone();
  }

  public static void reloadProps() {
    logSetup("Reload properties");
    props.reload();
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
    log(String.format("Change property '%s' from '%s' to '%s'", key, prop(key), value));
    return props.set(key, value);
  }

  /**
   * Changes the existing property to an {@code int} value.
   */
  public static void setPropInt(String key, int value) {
    log(String.format("Change property '%s' from '%s' to '%d'", key, prop(key), value));
    props.setInt(key, value);
    logDone();
  }

  /**
   * Changes the existing property to an {@code long} value.
   */
  public static void setPropLong(String key, long value) {
    log(String.format("Change property '%s' from '%s' to '%d'", key, prop(key), value));
    props.setLong(key, value);
    logDone();
  }

  /**
   * Changes the existing property to an {@code float} value.
   */
  public static void setPropFloat(String key, float value) {
    log(String.format("Change property '%s' from '%s' to '%f'", key, prop(key), value));
    props.setFloat(key, value);
    logDone();
  }

  /**
   * Changes the existing property to an {@code double} value.
   */
  public static void setPropDouble(String key, double value) {
    log(String.format("Change property '%s' from '%s' to '%f'", key, prop(key), value));
    props.setDouble(key, value);
    logDone();
  }

  /**
   * Changes the existing property to an {@code boolean} value.
   */
  public static void setPropBool(String key, boolean value) {
    log(String.format("Change property '%s' from '%s' to '%b'", key, prop(key), value));
    props.setBool(key, value);
    logDone();
  }

  /**
   * Resets the property to the default value.
   */
  public static void resetProp(String key) {
    logSetup(String.format("Reset property '%s'", key));
    props.reset(key);
    logDone();
  }

  /**
   * Resets all property to the default values.
   */
  public static void resetProps() {
    logSetup("Reset properties");
    props.resetAll();
    logDone();
  }

  /**
   * Saves the specified property to the file.
   */
  public static void flushProp(String key) {
    logSetup(String.format("Flush property '%s'", key));
    props.flush(key);
    logDone();
  }

  /**
   * Saves properties to the file.
   */
  public static void flushProps() {
    logSetup("Flush properties");
    props.flushAll();
    logDone();
  }

  public static String propList() {
    return props.toString();
  }

  public static void loadPrefs(String name) {
    logSetup(String.format("Load preferences '%s'", name));
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
    logSetup(String.format("Set preference '%s' to '%s'", key, value));
    prefs.putString(key, value);
    logDone();
  }

  /**
   * Adds an int entry to the preferences.
   */
  public static void putPrefInt(String key, int value) {
    logSetup(String.format("Set preference '%s' to '%d'", key, value));
    prefs.putInteger(key, value);
    logDone();
  }

  /**
   * Adds a long entry to the preferences.
   */
  public static void putPrefLong(String key, long value) {
    logSetup(String.format("Set preference '%s' to '%d'", key, value));
    prefs.putLong(key, value);
    logDone();
  }

  /**
   * Adds a float entry to the preferences.
   */
  public static void putPrefFloat(String key, float value) {
    logSetup(String.format("Set preference '%s' to '%f'", key, value));
    prefs.putFloat(key, value);
    logDone();
  }

  /**
   * Adds a boolean entry to the preferences.
   */
  public static void putPrefBool(String key, boolean value) {
    logSetup(String.format("Set preference '%s' to '%b'", key, value));
    prefs.putBoolean(key, value);
    logDone();
  }

  /**
   * Has to be called after every set of preferences is changed.
   */
  public static void flushPrefs() {
    logSetup("Flush preferences");
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

  public static boolean isAndroidApp() {
    return Gdx.app.getType() == ApplicationType.Android;
  }

  public static boolean isIOSApp() {
    return Gdx.app.getType() == ApplicationType.iOS;
  }

  public static boolean isDesktopApp() {
    return Gdx.app.getType() == ApplicationType.Desktop;
  }

  public static boolean isWebGLApp() {
    return Gdx.app.getType() == ApplicationType.WebGL;
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

  @Override public void dispose() {
    logSetup("Dispose of skin");
    skin.dispose();
    logDone();
    if (context != null) {
      logSetup("Dispose of context");
      context.dispose();
      logDone();
    }
    logSetup("Dispose of console");
    console.dispose();
    logDone();
  }

  @Override public void pause() {
    if (context != null) {
      context.pause();
    }
  }

  @Override public void resume() {
    if (context != null) {
      context.resume();
    }
  }

  @Override public void render() {
    console.update();
    if (context != null) {
      context.render();
    }
  }

  @Override public void resize(int width, int height) {
    if (context != null) {
      context.resize();
    }
  }

  public void setContext(BaseContext<?> newContext, boolean dispose) {
    if (context != null) {
      if (dispose) {
        logSetup("Dispose of context " + newContext.getClass().getSimpleName());
        context.dispose();
        logDone();
      } else {
        logSetup("Hide context " + newContext.getClass().getSimpleName());
        context.hide();
        logDone();
      }
    }

    context = newContext;

    if (context != null) {
      logSetup("Set context " + newContext.getClass().getSimpleName());
      context.contextChangeSuccess();
      context.show();
      context.resize();
    }
  }

  public void setContext(BaseContext<?> context) {
    setContext(context, true);
  }

  public BaseContext<?> getContext() {
    return context;
  }
}

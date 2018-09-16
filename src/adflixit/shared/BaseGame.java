package adflixit.shared;

import static adflixit.shared.TweenUtils.*;
import static adflixit.shared.Util.*;

import adflixit.shared.TweenUtils.SpriteAccessor;
import adflixit.shared.console.ConCmd;
import adflixit.shared.console.ConVar;
import adflixit.shared.console.Console;
import android.view.View;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
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
 * <li>Mutable configurations {@link MutableProperties}.</li>
 * <li>Preferences handles.</li>
 * </ul>
 */
public abstract class BaseGame extends Logger implements ApplicationListener {
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

	public static Class<? extends Actor>	actorClassDescendants;	// used as the Actor class signature for the tween engine
	public static boolean					debug;
	private static XApi						xApi;
	public static final Console				console					= new Console();
	private static Skin						skin;
	private static MutableProperties		props					= new MutableProperties();
	private static Preferences				prefs;

	protected BaseScreen<?>					screen;					// current screen

	public BaseGame() {
		setXApi(noXApi);
	}

	public BaseGame(XApi xApi) {
		setXApi(xApi);
	}

	@Override public void create() {
		ShaderProgram.pedantic = false;
		Tween.registerAccessor(Actor.class,					new ActorAccessor());
		Tween.registerAccessor(actorClassDescendants,		new ActorAccessor());
		Tween.registerAccessor(Label.class,					new LabelAccessor());
		Tween.registerAccessor(Sprite.class,				new SpriteAccessor());
		Tween.registerAccessor(Music.class,					new MusicAccessor());
		Tween.registerAccessor(OrthographicCamera.class,	new OrthoCameraAccessor());
		Tween.registerAccessor(Body.class,					new Box2DBodyAccessor());
		Tween.registerAccessor(View.class,					new AndViewAccessor());
		Tween.registerAccessor(Vector2.class,				new Vector2Accessor());
		Tween.registerAccessor(Vector3.class,				new Vector3Accessor());
		Tween.registerAccessor(Color.class,					new ColorAccessor());
		Tween.registerAccessor(MutableInteger.class,		new MutableInteger(0));
		Tween.registerAccessor(MutableFloat.class,			new MutableFloat(0));
		// default console commands
		registerConsoleCommand("prop", (args) -> {
			try {
				glog(prop(args[0]));
			} catch (Exception e) {
				glog(e.getLocalizedMessage());
			}
		});
		registerConsoleCommand("setprop", (args) -> {
			try {
				setProp(args[0], arrayToStringF("%s ", Arrays.copyOfRange(args, 1, args.length)));
			} catch (Exception e) {
				glog(e.getLocalizedMessage());
			}
		});
		registerConsoleCommand("resetprop", (args) -> {
			try {
				resetProp(args[0]);
			} catch (Exception e) {
				glog(e.getLocalizedMessage());
			}
		});
		registerConsoleCommand("resetprops", (args) -> resetProps());
		registerConsoleCommand("flushprop", (args) -> {
			try {
				flushProp(args[0]);
			} catch (Exception e) {
				glog(e.getLocalizedMessage());
			}
		});
		registerConsoleCommand("flushprops", (args) -> flushProps());
		registerConsoleCommand("proplist", (args) -> glog(propList()));
		registerConsoleCommand("quit", (args) -> quit());
	}

	private static void setXApi(XApi api) {
		xApi = api;
	}

	/** @see XApi#getAdView() */
	public static AdView getAdView() {
		return xApi.getAdView();
	}

	/** @see XApi#showAd() */
	public static void showAd() {
		glogSetup("Showing ad");
		xApi.showAd();
		glogDone("Ad shown");
	}

	/** @see XApi#hideAd() */
	public static void hideAd() {
		glogSetup("Hiding ad");
		xApi.hideAd();
		glogDone("Ad hidden");
	}

	/** @see XApi#refreshAd() */
	public static void refreshAd() {
		glogSetup("Refreshing ad");
		xApi.refreshAd();
		glogDone("Ad refreshed");
	}

	/** @see XApi#showLeaderboard(String) */
	public static void showLeaderboard(String id) {
		glogSetup("Showing leaderboard");
		xApi.showLeaderboard(id);
		glogDone("Leaderboard shown");
	}

	/** @see XApi#showAchievements() */
	public static void showAchievements() {
		glogSetup("Showing achievements");
		xApi.showAchievements();
		glogDone("Achievements shown");
	}

	/** @see XApi#shareText(String) */
	public static void shareText(String txt) {
		glogSetup("Sharing text \""+txt+"\"");
		xApi.shareText(txt);
		glogDone("Text shared");
	}

	/** @see XApi#isSignedIn() */
	public static boolean isSignedIn() {
		return xApi.isSignedIn();
	}

	/** @see XApi#signIn() */
	public static void signIn() {
		glogSetup("Signing in");
		xApi.signIn();
		glogDone("Signed in");
	}

	/** @see XApi#getPlayerId() */
	public static String getPlayerId() {
		return xApi.getPlayerId();
	}

	/** @see XApi#getPersonalBest(String) */
	public static long getPersonalBest(String id) {
		glog("Retrieving personal best from id "+id);
		return xApi.getPersonalBest(id);
	}

	/** @see XApi#getAllTimeRecord(String) */
	public static long getAllTimeRecord(String id) {
		glog("Retrieving all time record from id "+id);
		return xApi.getAllTimeRecord(id);
	}

	/** @see XApi#getWeeklyRecord(String) */
	public static long getWeeklyRecord(String id) {
		glog("Retrieving weekly record from id "+id);
		return xApi.getWeeklyRecord(id);
	}

	/** @see XApi#getDailyRecord(String) */
	public static long getDailyRecord(String id) {
		glog("Retrieving daily record from id "+id);
		return xApi.getDailyRecord(id);
	}

	/** @see XApi#submitScore(String, long) */
	public static void submitScore(String id, long score) {
		glogSetup("Submitting score "+score);
		xApi.submitScore(id, score);
		glogDone("Score submitted");
	}

	/** @see XApi#unlockAchievement(String) */
	public static void unlockAchievement(String id) {
		glogSetup("Unlocking achievement");
		xApi.unlockAchievement(id);
		glogDone("Achievement unlocked");
	}

	public static void initSkin(FileHandle skinFile, TextureAtlas atlas) {
		glogSetup("Loading skin \""+skinFile+"\"");
		skin = new Skin(skinFile, atlas);
		glogDone("Skin loaded");
	}

	public static void registerConsoleCommand(String name, ConCmd cmd) {
		glogSetup("Registering console command "+name);
		try {
			console.registerCommand(name, cmd);
		} catch (Exception e) {
			e.printStackTrace();
		}
		glogDone(name+" console command registered");
	}

	public static void registerConsoleVariable(String name, ConVar var) {
		glogSetup("Registering console variable "+name);
		try {
			console.registerVariable(name, var);
		} catch (Exception e) {
			e.printStackTrace();
		}
		glogDone(name+" console variable registered");
	}

	public static Skin skin() {
		return skin;
	}
	
	/** Loads the specified properties.
	 * @deprecated Not used anymore due to the introduced ability to change properties during runtime. */
	public static void loadProps(FileHandle... files) {
		glogSetup("Loading properties "+arrayToString("\"%s\"", files));
		props.loadProps(files);
		glogDone("Properties loaded");
	}

	/** Loads the specified properties. */
	public static void loadProps(FileHandle file) {
		glogSetup("Loading properties \""+file+"\"");
		props.load(file);
		glogDone("Properties loaded");
	}

	/** Interprets all properties entries as {@code float} and stores the digest to the quick access array. */
	public static void initPropsF() {
		props.initF();
	}

	/** @return property. */
	public static String prop(String key) {
		return props.prop(key);
	}

	/** @return property interpreted as {@code int}. */
	public static int propI(String key) {
		return props.propI(key);
	}

	/** @return property interpreted as {@code long}. */
	public static long propL(String key) {
		return props.propL(key);
	}

	/** @return property interpreted as {@code float}.
	 * @throws IllegalArgumentException if no entry is found. */
	public static float propF(String key) {
		return props.propF(key);
	}

	/** @return property interpreted as {@code double}. */
	public static double propD(String key) {
		return props.propD(key);
	}

	/** @return property interpreted as {@code boolean}. */
	public static boolean propB(String key) {
		return props.propB(key);
	}

	/** Changes the existing property.
	 * @return the previous value of the specified key. */
	public static Object setProp(String key, String value) {
		glog("Changing property \""+key+"\" from \""+prop(key)+"\" to \""+value+"\"");
		return props.setProp(key, value);
	}

	/** Changes the existing property to an {@code int} value. */
	public static void setPropI(String key, int value) {
		glog("Changing property \""+key+"\" from \""+prop(key)+"\" to \""+value+"\"");
		props.setPropI(key, value);
		glogDone("Property \""+key+"\" is changed");
	}

	/** Changes the existing property to an {@code long} value. */
	public static void setPropL(String key, long value) {
		glog("Changing property \""+key+"\" from \""+prop(key)+"\" to \""+value+"\"");
		props.setPropL(key, value);
		glogDone("Property \""+key+"\" is changed");
	}

	/** Changes the existing property to an {@code float} value. */
	public static void setPropF(String key, float value) {
		glog("Changing property \""+key+"\" from \""+prop(key)+"\" to \""+value+"\"");
		props.setPropF(key, value);
		glogDone("Property \""+key+"\" is changed");
	}

	/** Changes the existing property to an {@code double} value. */
	public static void setPropD(String key, double value) {
		glog("Changing property \""+key+"\" from \""+prop(key)+"\" to \""+value+"\"");
		props.setPropD(key, value);
		glogDone("Property \""+key+"\" is changed");
	}

	/** Changes the existing property to an {@code boolean} value. */
	public static void setPropB(String key, boolean value) {
		glog("Changing property \""+key+"\" from \""+prop(key)+"\" to \""+value+"\"");
		props.setPropB(key, value);
		glogDone("Property \""+key+"\" is changed");
	}

	/** Resets the property to the default value. */
	public static void resetProp(String key) {
		glogSetup("Resetting property \""+key+"\"");
		props.resetProp(key);
		glogDone("Property \""+key+"\" reset");
	}

	/** Resets all property to the default values. */
	public static void resetProps() {
		glogSetup("Resetting properties");
		props.resetProps();
		glogDone("Properties reset");
	}

	/** Saves the specified property to the file. */
	public static void flushProp(String key) {
		glogSetup("Flushing property \""+key+"\"");
		props.flushProp(key);
		glogDone("Property \""+key+"\" flushed");
	}

	/** Saves properties to the file. */
	public static void flushProps() {
		glogSetup("Flushing properties");
		props.flushProps();
		glogDone("Properties flushed");
	}

	public static String propList() {
		return props.toString();
	}

	public static void loadPrefs(String name) {
		glogSetup("Loading preferences \""+name+"\"");
		prefs = Gdx.app.getPreferences(name);
		glogDone("Preferences loaded");
	}

	/** @return do preferences contain the key entry. */
	public static boolean prefsContain(String key) {
		return prefs.contains(key);
	}

	/** @return preferences entry interpreted as string. */
	public static String prefS(String key) {
		return prefs.getString(key);
	}

	/** @return preferences entry interpreted as int. */
	public static int prefI(String key) {
		return prefs.getInteger(key);
	}

	/** @return preferences entry interpreted as long. */
	public static long prefL(String key) {
		return prefs.getLong(key);
	}

	/** @return preferences entry interpreted as float. */
	public static float prefF(String key) {
		return prefs.getFloat(key);
	}

	/** @return preferences entry interpreted as boolean. */
	public static boolean prefB(String key) {
		return prefs.getBoolean(key);
	}

	/** Adds a string entry to the preferences. */
	public static void putPrefS(String key, String value) {
		glogSetup("Setting preference "+key+" to "+value);
		prefs.putString(key, value);
		glogDone("Preference "+key+" set to "+value);
	}

	/** Adds an int entry to the preferences. */
	public static void putPrefI(String key, int value) {
		glogSetup("Setting preference "+key+" to "+value);
		prefs.putInteger(key, value);
		glogDone("Preference "+key+" set to "+value);
	}

	/** Adds a long entry to the preferences. */
	public static void putPrefL(String key, long value) {
		glogSetup("Setting preference "+key+" to "+value);
		prefs.putLong(key, value);
		glogDone("Preference "+key+" set to "+value);
	}

	/** Adds a float entry to the preferences. */
	public static void putPrefF(String key, float value) {
		glogSetup("Setting preference "+key+" to "+value);
		prefs.putFloat(key, value);
		glogDone("Preference "+key+" set to "+value);
	}

	/** Adds a boolean entry to the preferences. */
	public static void putPrefB(String key, boolean value) {
		glogSetup("Setting preference "+key+" to "+value);
		prefs.putBoolean(key, value);
		glogDone("Preference "+key+" set to "+value);
	}

	/** Has to be called after every set of preference changes to save them. */
	public static void flushPrefs() {
		glogSetup("Flushing preferences");
		prefs.flush();
		glogDone("Preferences flushed");
	}

	public static int getFps() {
		return Gdx.graphics.getFramesPerSecond();
	}

	/** @return rendering time delta. */
	public static float dt() {
		return Gdx.graphics.getDeltaTime();
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

	/** @return color from the skin. */
	public static Color color(String name) {
		return skin.getColor(name);
	}

	/** @return drawable from the skin. */
	public static Drawable drawable(String name) {
		return skin.getDrawable(name);
	}

	/** @return texture region from the skin. */
	public static TextureRegion texRegion(String name) {
		return skin.getRegion(name);
	}

	/** @return texture regions from the skin. */
	public static Array<TextureRegion> texRegions(String name) {
		return skin.getRegions(name);
	}

	/** @return the texture atlas. */
	public static TextureAtlas atlas() {
		return skin.getAtlas();
	}

	public static void quit() {
		glog("Quitting app");
		xApi.quit();
	}

	public void startup() {
		glogSetup("Starting up");
		screen.startup();
		glogDone("Started up");
	}

	@Override public void dispose() {
		glogSetup("Disposing of skin");
		skin.dispose();
		glogDone("Skin disposed");
		if (screen != null) {
			glogSetup("Disposing of screen");
			screen.dispose();
			glogDone("Screen disposed");
		}
		glogSetup("Disposing of console");
		console.dispose();
		glogDone("Console disposed");
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
				glogSetup("Disposing of screen");
				screen.dispose();
				glogDone("Screen disposed");
			} else {
				glogSetup("Hiding screen");
				screen.hide();
				glogDone("Screen hidden");
			}
		}
		screen = newScreen;
		if (screen != null) {
			glog("Setting screen "+newScreen.getClass().getSimpleName());
			screen.show();
			screen.resize();
		}
	}

	public void setScreen(BaseScreen<?> screen) {
		setScreen(screen, true);
	}
}

package adflixit.gdx;

/*import static com.google.android.gms.games.leaderboard.LeaderboardVariant.*;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.GameHelper;
import com.google.example.games.basegameutils.GameHelper.GameHelperListener;*/

public abstract class BaseAndroidLauncher/* extends AndroidApplication implements XApi, GameHelperListener*/ {
  /*protected static long       tmpScore; // used as a proxy for the data retrieving methods
  protected BaseGame          game;
  protected GameHelper        gameHelper;
  protected AdView            adView;
  protected AdRequest.Builder adRequestBuilder;

  protected abstract String getAdUnitId();
  protected abstract ApplicationListener getInstance();  

  public void initializeEx(BaseGame game) {
    this.game = game;
    initialize(game);
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (gameHelper == null) {
      gameHelper = new GameHelper(this, GameHelper.CLIENT_GAMES);
      gameHelper.enableDebugLog(true);
    }

    gameHelper.setup(this);
    adRequestBuilder = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR);

    // creating the ad layout
    RelativeLayout layout = new RelativeLayout(this);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
    adView = new AdView(this);

    // only using a banner, no pop-ups
    adView.setAdSize(AdSize.BANNER);
    adView.setAdUnitId(getAdUnitId());

    // the banner stays on the top
    RelativeLayout.LayoutParams adParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
        LayoutParams.WRAP_CONTENT);
    adParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
    //adParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
    layout.addView(initializeForView(getInstance()));
    layout.addView(adView, adParams);
    setContentView(layout);
  }

  @Override public void onStart() {
  super.onStart();
    gameHelper.onStart(this);
  }

  @Override public void onStop() {
    super.onStop();
    gameHelper.onStop();
  }

  @Override public void onResume() {
    super.onResume();
    adView.resume();
  }

  @Override public void onPause() {
    adView.pause();
    super.onPause();
  }

  @Override public void onDestroy() {
    adView.destroy();
    super.onDestroy();
  }

  @Override public void onActivityResult(int request, int response, Intent data) {
    super.onActivityResult(request, response, data);
    gameHelper.onActivityResult(request, response, data);
  }

  @Override public void onSignInFailed() {
  }

  @Override public void onSignInSucceeded() {
  }

  protected GoogleApiClient getApiClient() {
    return gameHelper.getApiClient();
  }

  private static void setTmpScore(long s) {
    tmpScore = s;
  }

  private void getTopScore(String id, int span, int collection) {
    Games.Leaderboards.loadTopScores(getApiClient(), id, span, collection, 1)
    .setResultCallback(scoreResult -> setTmpScore(scoreResult.getScores().get(0).getRawScore()));
  }

  @Override public void showAd() {
    adView.setVisibility(View.VISIBLE);
  }

  @Override public void hideAd() {
    adView.setVisibility(View.INVISIBLE);
  }

  @Override public void refreshAd() {
    adView.loadAd(adRequestBuilder.build());
  }

  @Override public AdView getAdView() {
    return adView;
  }

  @Override public void showLeaderboard(String id) {
    if (gameHelper.isSignedIn()) {
      startActivityForResult(Games.Leaderboards.getLeaderboardIntent(getApiClient(), id), 100);
    } else if (!gameHelper.isConnecting()) {
      signIn();
    }
  }

  @Override public void showAchievements() {
    if (gameHelper.isSignedIn()) {
      startActivityForResult(Games.Achievements.getAchievementsIntent(getApiClient()), 101);
    } else if (!gameHelper.isConnecting()) {
      signIn();
    }
  }

  @Override public void shareText(String txt) {
    Intent intent = new Intent();
    intent.setAction(Intent.ACTION_SEND);
    intent.putExtra(Intent.EXTRA_TEXT, txt);
    intent.setType("text/plain");
    startActivity(intent);
  }

  @Override public boolean isSignedIn() {
    return gameHelper.isSignedIn();
  }

  @Override public void signIn() {
    try {
      runOnUiThread(() -> gameHelper.beginUserInitiatedSignIn());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override public String getPlayerId() {
    return Games.Players.getCurrentPlayerId(getApiClient());
  }

  @Override public long getPersonalBest(String id) {
    Games.Leaderboards.loadCurrentPlayerLeaderboardScore
        (getApiClient(), id, TIME_SPAN_ALL_TIME, COLLECTION_PUBLIC)
        .setResultCallback(scoreResult -> setTmpScore(scoreResult.getScore().getRawScore()));
    return tmpScore;
  }

  @Override public long getAllTimeRecord(String id) {
    getTopScore(id, TIME_SPAN_ALL_TIME, COLLECTION_PUBLIC);
    return tmpScore;
  }

  @Override public long getWeeklyRecord(String id) {
    getTopScore(id, TIME_SPAN_WEEKLY, COLLECTION_PUBLIC);
    return tmpScore;
  }

  @Override public long getDailyRecord(String id) {
    getTopScore(id, TIME_SPAN_DAILY, COLLECTION_PUBLIC);
    return tmpScore;
  }

  @Override public void submitScore(String id, long score) {
    Games.Leaderboards.submitScore(getApiClient(), id, score);
  }

  @Override public void unlockAchievement(String id) {
    Games.Achievements.unlock(getApiClient(), id);
  }

  @Override public void quit() {
    // minimizing the app, not actually closing it
    handler.post(() -> {
      Intent intent = new Intent(Intent.ACTION_MAIN);
      intent.addCategory(Intent.CATEGORY_HOME);
      intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      startActivity(intent);
    });
  }*/
}

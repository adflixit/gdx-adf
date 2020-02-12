package adflixit.gdx;

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

public class AndroidClient {
  protected static long       tmpScore; // used as proxy to retrieve data
  protected AdView            adView;
  protected AdRequest.Builder adRequestBuilder;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (gameHelper == null) {
      gameHelper = new GameHelper(this, GameHelper.CLIENT_GAMES);
      gameHelper.enableDebugLog(true);
    }

    gameHelper.setup(this);
    adRequestBuilder = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR);

    // create the ad layout
    RelativeLayout layout = new RelativeLayout(this);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
    adView = new AdView(this);

    // only using a banner, no pop-ups
    adView.setAdSize(AdSize.BANNER);
    adView.setAdUnitId(getAdUnitId());

    // the banner stays on the top
    RelativeLayout.LayoutParams adParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
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
    logSetup("Showing ad");
    adView.setVisibility(View.VISIBLE);
    logDone();
  }

  @Override public void hideAd() {
    logSetup("Hiding ad");
    adView.setVisibility(View.INVISIBLE);
    logDone();
  }

  @Override public void loadAd() {
    logSetup("Loading ad");
    adView.loadAd(adRequestBuilder.build());
    logDone();
  }

  @Override public AdView getAdView() {
    return adView;
  }

  @Override public void openLeaderboard(String id) {
    logSetup("Opening leaderboard");
    if (gameHelper.isSignedIn()) {
      startActivityForResult(Games.Leaderboards.getLeaderboardIntent(getApiClient(), id), 100);
    } else if (!gameHelper.isConnecting()) {
      signIn();
    }
    logDone();
  }

  @Override public void openAchievements() {
    logSetup("Opening achievements");
    if (gameHelper.isSignedIn()) {
      startActivityForResult(Games.Achievements.getAchievementsIntent(getApiClient()), 101);
    } else if (!gameHelper.isConnecting()) {
      signIn();
    }
    logDone();
  }

  @Override public void shareText(String txt) {
    logSetup("Sharing text '"+txt+"'");
    Intent intent = new Intent();
    intent.setAction(Intent.ACTION_SEND);
    intent.putExtra(Intent.EXTRA_TEXT, txt);
    intent.setType("text/plain");
    startActivity(intent);
    logDone();
  }

  @Override public boolean isSignedIn() {
    return gameHelper.isSignedIn();
  }

  @Override public void signIn() {
    logSetup("Signing in");
    try {
      runOnUiThread(() -> gameHelper.beginUserInitiatedSignIn());
    } catch (Exception e) {
      e.printStackTrace();
    }
    logDone();
  }

  @Override public String getPlayerId() {
    return Games.Players.getCurrentPlayerId(getApiClient());
  }

  @Override public long getPersonalBest(String id) {
    log("Retrieving personal best from id "+id);
    Games.Leaderboards.loadCurrentPlayerLeaderboardScore(getApiClient(), id, LeaderboardVariant.TIME_SPAN_ALL_TIME, LeaderboardVariant.COLLECTION_PUBLIC)
            .setResultCallback(scoreResult -> setTmpScore(scoreResult.getScore().getRawScore()));
    return tmpScore;
  }

  @Override public long getAllTimeRecord(String id) {
    log("Retrieving all time record from id "+id);
    getTopScore(id, LeaderboardVariant.TIME_SPAN_ALL_TIME, LeaderboardVariant.COLLECTION_PUBLIC);
    return tmpScore;
  }

  @Override public long getWeeklyRecord(String id) {
    log("Retrieving weekly record from id "+id);
    getTopScore(id, LeaderboardVariant.TIME_SPAN_WEEKLY, LeaderboardVariant.COLLECTION_PUBLIC);
    return tmpScore;
  }

  @Override public long getDailyRecord(String id) {
    log("Retrieving daily record from id "+id);
    getTopScore(id, LeaderboardVariant.TIME_SPAN_DAILY, LeaderboardVariant.COLLECTION_PUBLIC);
    return tmpScore;
  }

  @Override public void submitScore(String id, long score) {
    logSetup("Submitting score "+score);
    Games.Leaderboards.submitScore(getApiClient(), id, score);
    logDone();
  }

  @Override public void unlockAchievement(String id) {
    logSetup("Unlocking achievement "+id);
    Games.Achievements.unlock(getApiClient(), id);
    logDone();
  }

  @Override public void quit() {
    log("Closing app");
    // minimize the app, not actually close it
    handler.post(() -> {
      Intent intent = new Intent(Intent.ACTION_MAIN);
      intent.addCategory(Intent.CATEGORY_HOME);
      intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      startActivity(intent);
    });
  }
}

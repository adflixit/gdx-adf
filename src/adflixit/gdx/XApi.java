package adflixit.gdx;

import com.google.android.gms.ads.AdView;

/**
 * A simple cross-platform API.
 */
public interface XApi {
  /**
   * Loads ad contents.
   */
  public void refreshAd();

  public void showAd();

  public void hideAd();

  /**
   * @return AdMob {@link AdView}.
   */
  public AdView getAdView();

  /**
   * Opens an external leaderboard window.
   * @param id leaderboard ID.
   */
  public void showLeaderboard(String id);

  public void showAchievements();

  public void shareText(String txt);

  /**
   * @return is player signed into the scoring system.
   */
  public boolean isSignedIn();

  public void signIn();

  /**
   * @return player ID in the scoring system.
   */
  public String getPlayerId();

  /**
   * @param id leaderboard ID.
   * @return player's highest score on the leaderboard.
   */
  public long getPersonalBest(String id);

  /**
   * @param id leaderboard ID.
   * @return all-time top score.
   */
  public long getAllTimeRecord(String id);

  /**
   * @param id leaderboard ID.
   * @return weekly top score.
   */
  public long getWeeklyRecord(String id);

  /**
   * @param id leaderboard ID.
   * @return daily top score.
   */
  public long getDailyRecord(String id);

  /**
   * Submits current player's score to the leaderboard.
   * @param id leaderboard ID.
   */
  public void submitScore(String id, long score);

  /**
   * Unlocks the specified achievement for current player.
   * @param id achievement ID.
   */
  public void unlockAchievement(String id);

  /**
   * Quits app, either fully closes or minimizes it.
   */
  public void quit();
}

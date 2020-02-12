package adflixit.gdx;

public interface AndroidApi {
  public void loadAd();
  public void showAd();
  public void hideAd();

  /**
   * @param id leaderboard ID.
   */
  public void openLeaderboard(String id);
  public void openAchievements();
  public void shareText(String txt);
  public boolean checkConnection();
  public boolean isSignedIn();
  public void signIn();
  public String getPlayerId();

  /**
   * @param id leaderboard ID.
   */
  public long getPersonalBest(String id);

  /**
   * @param id leaderboard ID.
   */
  public long getAllTimeRecord(String id);

  /**
   * @param id leaderboard ID.
   */
  public long getWeeklyRecord(String id);

  /**
   * @param id leaderboard ID.
   */
  public long getDailyRecord(String id);

  /**
   * Submits player's score.
   * @param id leaderboard ID.
   */
  public void submitScore(String id, long score);

  /**
   * @param id achievement ID.
   */
  public void unlockAchievement(String id);

  /**
   * Minimizes app.
   */
  public void quit();
}

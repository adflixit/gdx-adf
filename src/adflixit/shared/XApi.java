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

import com.google.android.gms.ads.AdView;

/**
 * A simple cross-platform API.
 */
public interface XApi {
    /** Loads the ad contents. */
    public void refreshAd();

    public void showAd();

    public void hideAd();

    /** @return AdMod {@link AdView}. */
    public AdView getAdView();

    /** Calls an external leaderboard window.
     * @param id leaderboard ID. */
    public void showLeaderboard(String id);

    public void showAchievements();

    public void shareText(String txt);

    /** @return is player signed into the scoring system. */
    public boolean isSignedIn();

    /** Sign into the scoring system account. */
    public void signIn();

    /** @return player id in the scoring system. */
    public String getPlayerId();

    /** @param id leaderboard ID.
     * @return player's highest score on the leaderboard. */
    public long getPersonalBest(String id);

    /** @param id leaderboard ID.
     * @return top score. */
    public long getAllTimeRecord(String id);

    /** @param id leaderboard ID.
     * @return week's highest score. */
    public long getWeeklyRecord(String id);

    /** @param id leaderboard ID.
     * @return day's highest score. */
    public long getDailyRecord(String id);

    /** Submits the current player's score to the leaderboard.
     * @param id leaderboard ID. */
    public void submitScore(String id, long score);

    /** Unlocks the specified achievement for this player.
     * @param id achievement ID. */
    public void unlockAchievement(String id);

    /** Quits the app, either fully closes or minimizes. */
    public void quit();
}

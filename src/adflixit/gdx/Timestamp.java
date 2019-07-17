package adflixit.gdx;

import static adflixit.gdx.Util.*;

public class Timestamp {
  private long startTime;
  private long stopTime;

  public void set() {
    startTime = currentTime();
  }

  public long startTime() {
    return startTime;
  }

  public long elapsed() {
    return currentTime() - startTime;
  }

  public void stop() {
    stopTime = elapsed();
  }

  public long recorded() {
    return stopTime;
  }

  public float elapsedSecs() {
    return elapsed() / 1000f;
  }
}

package adflixit.shared;

import static adflixit.shared.Util.*;

public class Timestamp {
	private long startTime;

	public void set() {
		startTime = currentTime();
	}

	public long startTime() {
		return startTime;
	}

	public long elapsed() {
		return currentTime() - startTime;
	}

	public float elapsedSecs() {
		return elapsed() / 1000f;
	}
}

package adflixit.gdx;

import static adflixit.gdx.Util.*;

/**
 * A sequence of random unique indices.
 * Has the current index in focus that has to manually navigated by moving to the next index.
 * When the rotation meets the end the first half of the existing sequence gets scrambled and appended.
 * 
 * FIXME: unfinished.
 */
public class RandSeq {
  private int start, end, length, lengthPos, current;
  private int[] indices;

  public RandSeq(int start, int end) {
    init(start, end);
    build();
  }

  public RandSeq(int length) {
    this(0, length-1);
  }

  public RandSeq() {
    this(2);
  }

  public void init(int start, int end) {
    // in case if the range is misplaced
    if (start > end) {
      int t = end;
      end = start;
      start = t;
    }

    if (start-end == 0) {
      throw new IllegalArgumentException("A range can't be shorter than two: ["+start+", "+end+"].");
    }

    this.start  = start;
    this.end  = end;
    length    = end - start + 1;
    lengthPos  = length;
    indices    = new int[length];
  }

  public void init(int length) {
    init(0, length);
  }

  public void reset() {
    init(start, end);
  }

  /**
   * @return does index belong to the section of a sequence within the specified range.
   */
  private boolean belongs(int index, int[] indices, int start, int end) {
    for (int i=start; i<=end; i++) {
      if (indices[i] == index) {
        return true;
      }
    }
    return false;
  }

  /**
   * @return does index belong to a sequence.
   */
  private boolean belongs(int index, int[] indices) {
    return belongs(index, indices, 0, indices.length-1);
  }

  /**
   * @return does index belong to {@link #indices}.
   */
  private boolean belongs(int index) {
    return belongs(index, indices);
  }

  public void build() {
    int rand;
    for (int i=0; i<length; i++) {
      do {
        rand = randi(start, end);
      } while (belongs(rand));
      indices[i] = rand;
    }
  }

  /**
   * Builds the sequence after it's already on the run.
   */
  private void buildShifted() {
    int hlength = length/2;
    int[] cluster = new int[length+hlength];

    System.arraycopy(indices, hlength, cluster, 0, hlength);

    int rand;
    for (int i = hlength; i<length; i++) {
      do {
        rand = randi(start, end);
      } while (belongs(rand, cluster, 0, hlength));
      cluster[i] = rand;
    }

    for (int i=length; i<length+hlength; i++) {
      do {
        rand = randi(start, end);
      } while (belongs(rand, cluster));
      cluster[i] = rand;
    }

    System.arraycopy(cluster, hlength, indices, 0, lengthPos);
  }

  /**
   * @return {@link #indices} snapshot.
   */
  public int[] indices() {
    int[] snapshot = new int[length];
    System.arraycopy(indices, 0, snapshot, 0, length);
    return snapshot;
  }

  /**
   * @return next index.
   */
  public int next() {
    if (++current == length) {
      buildShifted();
      current = 0;
    }
    return indices[current];
  }

  /**
   * @return current index.
   */
  public int get() {
    return indices[current];
  }

  @Override public String toString() {
    return arrToStr(indices);
  }
}

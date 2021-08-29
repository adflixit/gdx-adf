package adf.gdx;

import java.util.Arrays;

import static adf.gdx.MathUtil.randi;
import static adf.gdx.Util.*;

/**
 * A sequence of random unique indices.
 * Has the current index in focus that has to manually navigated by moving to the next index.
 * When the rotation meets the end the first half of the existing sequence gets scrambled and appended.
 */
public class RandSeq {
  private int start, end, length, current;
  private int[] indices;

  public RandSeq(int start, int end) {
    init(start, end);
    build();
  }

  public RandSeq(int length) {
    this(0, length-1);
  }

  public void init(int start, int end) {
    if (end-start <= 0) {
      throw new IllegalArgumentException(String.format("A range can't be shorter than two: [%d, %d].", start, end));
    }

    this.start  = start;
    this.end    = end;
    length      = end - start + 1;
    indices     = new int[length];
  }

  public void init(int length) {
    init(0, length-1);
  }

  public void reset() {
    init(start, end);
  }

  /**
   * @return does index belong to the section of a sequence within the specified range.
   */
  private boolean belongs(int index, int[] indices, int start, int end) {
    for (int i = start; i <= end; i++) {
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

  public void build() {
    int rand;
    Arrays.fill(indices, -1);

    for (int i=0; i<length; i++) {
      indices[i] = -1;
      do {
        rand = randi(start, end);
      } while (belongs(rand, indices));
      indices[i] = rand;
    }
  }

  /**
   * Builds the sequence after it's already on the run.
   */
  private void buildShifted() {
    // no repetition in 2-item range
    int rand, i0, i1;
    do {
      i0 = randi(start, end);
    } while (i0 == indices[length-2] || i0 == indices[length-1]);

    do {
      i1 = randi(start, end);
    } while (i1 == indices[length-1] || i1 == i0);

    Arrays.fill(indices, -1);
    indices[0] = i0;
    indices[1] = i1;

    for (int i=2; i < length; i++) {
      do {
        rand = randi(start, end);
      } while (belongs(rand, indices));
      indices[i] = rand;
    }
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

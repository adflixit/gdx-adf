package adflixit.gdx;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link ArrayList} of reusable items.
 * All items are obligated to have an explicit indicator of their availability to be recycled.
 * On an allocation request, the first free item is returned.
 */
public abstract class Pool<E> extends ArrayList<E> implements List<E> {
  private int initialSize;

  public Pool(int cap) {
    super(cap);
    initialSize = cap;
  }

  public Pool() {
    this(10);
  }

  public Pool<E> init(int cap) {
    resize(cap);
    return this;
  }

  public Pool<E> init() {
    return init(10);
  }

  public void resize(int cap) {
    if (cap != size()) {
      if (cap > size()) {
        for (int i=0; i < cap-size(); i++) {
          add(newObj());
          setup(get(i));
        }
      } else if (cap < size()) {
        for (int i=cap; i < size(); i++) {
          reset(get(i));
          remove(i);
        }
      }
    }
  }

  /**
   * Resizes the list to the initial size.
   */
  public void resetSize() {
    resize(initialSize);
  }

  public void grow(int amount) {
    resize(size() + amount);
  }

  public void contract(int amount) {
    resize(size() - amount);
  }

  /**
   * Clears the list and renews it.
   */
  public void refresh(int cap) {
    clear();
    resize(cap);
  }

  public E first() {
    return get(0);
  }

  public E last() {
    return get(size() - 1);
  }

  /**
   * @return the first found free item. If there are none, returns null.
   */
  public E nextFree() {
    for (E e : this) {
      if (isFree(e)) {
        return e;
      }
    }
    return null;
  }

  /**
   * @return the first found free item. If there are none, the pool is being expanded.
   */
  public E nextFreeExtra() {
    if (nextFree() == null) {
      grow(5);
    }
    return nextFree();
  }

  /**
   * @return the item constructor.
   */
  public abstract E newObj();

  /**
   * @return is item available to be recycled, i.e. not used.
   */
  public abstract boolean isFree(E obj);

  /**
   * Called when item is being added to the list.
   */
  public void setup(E obj) {}

  /**
   * Called when item is being removed from the list.
   */
  public void reset(E obj) {}
}

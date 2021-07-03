package adf.gdx;

public final class Poolable<T> {
  private T obj;
  public boolean isFree = true;

  public Poolable(T obj) {
    this.obj = obj;
  }

  public T obj() {
    return obj;
  }
}

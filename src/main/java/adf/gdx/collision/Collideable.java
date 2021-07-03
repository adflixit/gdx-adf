package adf.gdx.collision;

public abstract class Collideable {
  private int     id;
  // Mask is one bit that determines the collision layer of the object.
  public int      mask;
  // Filter is a bit field that determines the set of collision layers this object is able to collide with.
  public int      filter;
  private boolean free = true;  // ready to be recycled

  private String  name = "";
  public Object   userData = null;

  public Collideable() {}

  public void init(int id) {
    free = false;
    this.id = id;
  }

  public Collideable setup(int mask, int filter) {
    this.mask = mask;
    this.filter = filter;
    return this;
  }

  public void release() {
    free = true;
    userData = null;
  }

  public int id() {
    return id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String name() {
    return name;
  }

  public boolean isFree() {
    return free;
  }

  @Override public boolean equals(Object obj) {
    if (obj instanceof Collideable) {
      Collideable Collideable = (Collideable)obj;
      if (Collideable.id() == id()) {
        return true;
      }
    }
    return super.equals(obj);
  }

  @Override public String toString() {
    return name.isEmpty() ? "" + id() : name;
  }
}

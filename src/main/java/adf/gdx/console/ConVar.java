package adf.gdx.console;

public class ConVar {
  public static Console console;

  private final String  defValue;
  private String        rawValue;
  private double        value;

  public ConVar(String name, String def) {
    defValue = def;
    set(def);

    if (console == null) {
      throw new RuntimeException("Console has to be initialized first.");
    }
    console.registerVariable(name, this);
  }

  public ConVar set(String val) {
    rawValue = val;
    try {
      value = Double.parseDouble(rawValue);
    } catch (Exception e) {
      value = 0;
    }
    return this;
  }

  public ConVar set(int val) {return set(String.valueOf(val));}
  public ConVar set(long val) {return set(String.valueOf(val));}
  public ConVar set(float val) {return set(String.valueOf(val));}
  public ConVar set(double val) {return set(String.valueOf(val));}
  public ConVar set(boolean val) {return set(String.valueOf(val));}

  public ConVar reset() {
    return set(defValue);
  }

  public String defValue()    {return defValue;}
  public String rawValue()    {return rawValue;}
  public int intValue()       {return (int)value;}
  public long longValue()     {return (long)value;}
  public float floatValue()   {return (float)value;}
  public double doubleValue() {return value;}
  public boolean boolValue()  {return value > 0;}

  @Override public String toString() {
    return rawValue;
  }
}

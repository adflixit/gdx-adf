package adflixit.gdx.console;

public class ConVar {
  private final String  defValue;
  private String        rawValue;
  private double        value;  // numerical data is stored as double
  private boolean       bool;

  public ConVar(String def) {
    defValue = def;
  }

  public ConVar set(String val) {
    rawValue = val;
    try {
      value = Double.parseDouble(rawValue);
    } catch (Exception e) {
      value = 0;
    }
    try {
      bool = Boolean.parseBoolean(rawValue);
    } catch (Exception e) {
      bool = false;
    }
    return this;
  }

  public ConVar reset() {
    return set(defValue);
  }

  public String defValue()    {return defValue;}
  public String rawValue()    {return rawValue;}
  public int intValue()       {return (int)value;}
  public long longValue()     {return (long)value;}
  public float floatValue()   {return (float)value;}
  public double doubleValue() {return value;}
  public boolean boolValue()  {return bool;}

  @Override public String toString() {
    return rawValue;
  }
}

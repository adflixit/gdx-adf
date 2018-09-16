package adflixit.shared.console;

public class ConVar {
	private final String		defValue;
	private String				rawValue;
	private int					intValue;
	private long				longValue;
	private double				doubleValue;
	private float				floatValue;
	private boolean				boolValue;

	public ConVar(String def) {
		defValue = def;
	}

	public ConVar set(String value) {
		rawValue = value;
		try {
			intValue = Integer.parseInt(rawValue);
			longValue = Long.parseLong(rawValue);
			floatValue = Float.parseFloat(rawValue);
			doubleValue = Double.parseDouble(rawValue);
			boolValue = Boolean.parseBoolean(rawValue);
		} catch (Exception e) {}
		return this;
	}

	public String def()			{return defValue;}
	public String raw()			{return rawValue;}
	public int intValue()		{return intValue;}
	public long longValue()		{return longValue;}
	public float floatValue()	{return floatValue;}
	public double doubleValue()	{return doubleValue;}
	public boolean boolValue()	{return boolValue;}
}

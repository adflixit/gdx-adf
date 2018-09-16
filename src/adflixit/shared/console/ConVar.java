/*
 * Copyright 2018 Adflixit
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

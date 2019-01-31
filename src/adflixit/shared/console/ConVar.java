/*
 * Copyright 2019 Adflixit
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package adflixit.shared.console;

public class ConVar {
  private final String  defValue;
  private String        rawValue;
  private double        value;  // numerical data is stored as double

  public ConVar(String def) {
    defValue = def;
  }

  public ConVar set(String val) {
    rawValue = val;
    try {
      value = Double.parseDouble(rawValue);
    } catch (Exception e) {}
    return this;
  }

  public ConVar reset() {
    return set(defValue);
  }

  public String def()         {return defValue;}
  public String raw()         {return rawValue;}
  public int intValue()       {return (int)value;}
  public long longValue()     {return (long)value;}
  public float floatValue()   {return (float)value;}
  public double doubleValue() {return value;}
  public boolean boolValue()  {return (int)value != 0;}

  @Override public String toString() {
    return rawValue;
  }
}

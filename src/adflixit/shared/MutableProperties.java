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

package adflixit.shared;

import static adflixit.shared.BaseGame.*;

import com.badlogic.gdx.files.FileHandle;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class MutableProperties extends Properties {
  private final Properties          original  = new Properties();   // the original properties unchanged during runtime
  private final List<String>        flushed   = new ArrayList<>();  // a list of the flushed properties
  private final List<String>        raw       = new ArrayList<>();  // raw properties file text
  private final Map<String, Float>  floats    = new HashMap<>();    // the entries pre-interpreted as float
  private FileHandle                file;

  /**
   * Loads the specified properties. The file type depends on platform.
   */
  public void load(String path) {
    file = isDesktopApp() ? localFile(path) : internalFile(path);
    try {
      load(file.read());
    } catch (IOException e) {
      e.printStackTrace();
    }
    raw.addAll(Arrays.asList(file.readString().split("\\r?\\n")));
    original.putAll(this);
    initFloats();
  }

  public void reload() {
    if (file != null) {
      load(file.path());
    }
  }

  /**
   * Interprets properties as {@code float} and stores them in {@link #floats}.
   */
  private void initFloats() {
    String key;
    for (Map.Entry<Object, Object> entry : entrySet()) {
      try {
        key = (String)entry.getKey();
        floats.put(key, Float.parseFloat(get(key)));
      } catch (NumberFormatException e) {}
    }
  }

  /**
   * @return properties entry.
   * @throws IllegalArgumentException if no entry is found.
   */
  public String get(String key) throws IllegalArgumentException {
  String value = getProperty(key);
    if (value == null) {
      throw new IllegalArgumentException("No '"+key+"' property found.");
    }
    return value;
  }

  /**
   * @return entry interpreted as {@code int}.
   */
  public int getInt(String key) {
    return Integer.parseInt(get(key));
  }

  /**
   * @return entry interpreted as {@code long}.
   */
  public long getLong(String key) {
    return Long.parseLong(get(key));
  }

  /**
   * @return entry interpreted as {@code float}.
   * @throws IllegalArgumentException if no entry is found.
   */
  public float getFloat(String key) throws IllegalArgumentException {
    Float value = floats.get(key);
    if (value == null) {
      throw new IllegalArgumentException("No '"+key+"' property found.");
    }
    return value.floatValue();
  }

  /**
   * @return entry interpreted as {@code double}.
   */
  public double getDouble(String key) {
    return Double.parseDouble(get(key));
  }

  /**
   * @return entry interpreted as {@code boolean}.
   */
  public boolean getBool(String key) {
    return Boolean.parseBoolean(get(key));
  }

  /**
   * Changes an existing property.
   * @return the previous value of the specified key.
   */
  public Object set(String key, String value) throws NullPointerException, IllegalArgumentException {
    if (key == null) {
      throw new NullPointerException("Key can't be null.");
    }
    if (value == null || value.equals("")) {
      throw new NullPointerException("Value can't be null or empty.");
    }
    if (!containsKey(key)) {
      throw new IllegalArgumentException("No '"+key+"' property found.");
    }
    flushed.remove(key);
    return setProperty(key, value);
  }

  /**
   * Changes an existing property to an {@code int} value.
   */
  public void setInt(String key, int value) {
    set(key, Integer.toString(value));
  }

  /**
   * Changes an existing property to a {@code long} value.
   */
  public void setLong(String key, long value) {
    set(key, Long.toString(value));
  }

  /**
   * Changes an existing property to a {@code float} value.
   */
  public void setFloat(String key, float value) {
    if (set(key, Float.toString(value)) != null) {
      floats.put(key, value);
    }
  }

  /**
   * Changes an existing property to a {@code double} value.
   */
  public void setDouble(String key, double value) {
    set(key, Double.toString(value));
  }

  /**
   * Changes an existing property to a {@code boolean} value.
   */
  public void setBool(String key, boolean value) {
    set(key, Boolean.toString(value));
  }

  /**
   * Resets a property to the default value.
   */
  public void reset(String key) {
    set(key, original.getProperty(key));
  }

  /**
   * Resets all property to the default values.
   */
  public void resetAll() {
    clear();
    putAll(original);
    flushed.clear();
  }

  /**
   * Saves the specified property to the file.
   */
  public void flush(String key) throws IllegalArgumentException, RuntimeException {
    if (containsKey(key)) {
      key.trim();
      flushed.add(key);
      String output = "";

      // was line altered
      boolean altered = false;
      // going through the lines of the file
      for (String i : raw) {
        // checking if the entry key is on the flushed list, i.e. if the word before '=' matches a key from the list
        for (String j : flushed) {
          if (i.startsWith(j) && !i.startsWith("#") && !i.equals("")) {
            // if a match is found, assemble the key and the new value
            String split = i.split("=")[0];
            output += split+"="+get(split);
            altered = true;
            break;
          }
        }
        if (!altered) {
          output += i;
        }
        altered = false;
        output+="\n";
      }
      file.writeString(output, false);
    } else {
      throw new IllegalArgumentException("No '"+key+"' property found.");
    }
  }

  /**
   * Saves properties to the file.
   */
  public void flushAll() throws IllegalArgumentException, RuntimeException {
    flushed.clear();
    String output = "";
    // going through the lines of the file
    for (String i : raw) {
      if (!i.startsWith("#") && !i.equals("")) {
        // if a match is found, assembling the key and the new value
        String split = i.split("=")[0];
        output += split+"="+get(split);
      } else {
         output += i;
      }
      output+="\n";
    }
    file.writeString(output, false);
  }

  @Override public String toString() {
    return super.toString().replace(", ", "\n").replaceAll("\\{|\\}", "");
  }
}

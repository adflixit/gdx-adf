/*
 * Copyright 2018 Adflixit
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

import static adflixit.shared.Util.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Used to pack data into a readable and editable format, such as JSON.
 */
public class DataPacker {
	public static final JSONParser	parser		= new JSONParser();
	private JSONObject		read		= new JSONObject();
	private JSONObject		write		= new JSONObject();
	private boolean			isValid		= true;

	public void read(String data) {
		read.clear();
		try {
			read = (JSONObject)parser.parse(data);
		} catch (ParseException e) {
			e.printStackTrace();
			isValid = false;
		}
	}

	public boolean isValid() {
		return isValid;
	}

	public String write() {
		return write.toJSONString();
	}

	public void clear() {
		write.clear();
	}

	public Object get(String key) {
		return read.get(key);
	}

	public int getInt(String key) {
		return ((Long)get(key)).intValue();
	}

	public long getLong(String key) {
		return ((Long)get(key)).longValue();
	}

	public float getFloat(String key) {
		return ((Double)get(key)).floatValue();
	}

	public double getDouble(String key) {
		return ((Double)get(key)).doubleValue();
	}

	public boolean getBool(String key) {
		return ((Boolean)get(key)).booleanValue();
	}

	public String getString(String key) {
		return (String)read.get(key);
	}

	public JSONObject getObject(String key) {
		return (JSONObject)get(key);
	}

	public JSONArray getArray(String key) {
		return (JSONArray)read.get(key);
	}

	public void put(String key, Object value) {
		write.put(key, value);
	}

	public void putInt(String key, int value) {
		put(key, wrap(value));
	}

	public void putLong(String key, long value) {
		put(key, wrap(value));
	}

	public void putFloat(String key, float value) {
		put(key, wrap(value));
	}

	public void putDouble(String key, double value) {
		put(key, wrap(value));
	}

	public void putBool(String key, boolean value) {
		put(key, wrap(value));
	}

	public void putString(String key, String value) {
		put(key, value);
	}
}

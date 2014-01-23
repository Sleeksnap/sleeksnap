/**
 * Sleeksnap, the open source cross-platform screenshot uploader
 * Copyright (C) 2012 Nikki <nikki@nikkii.us>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sleeksnap.uploaders.settings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * A more customizable settings system with support for encrypting passwords by
 * a user-supplied master password
 * 
 * @author Nikki
 * 
 */
public class UploaderSettings {

	/**
	 * The backing JSONObject
	 */
	private JSONObject settings;

	/**
	 * Construct a blank settings instance
	 */
	public UploaderSettings() {
		settings = new JSONObject();
	}

	/**
	 * Construct a settings instance from an existing object
	 * 
	 * @param object
	 *            The object to construct from
	 */
	public UploaderSettings(JSONObject object) {
		this.settings = object;
	}

	public JSONObject getBaseObject() {
		return settings;
	}

	public int getInt(String key) {
		return settings.getInt(key);
	}

	public int getInt(String key, int defaultValue) {
		return settings.getInt(key, defaultValue);
	}

	public String getString(String key) {
		return settings.getString(key);
	}

	public String getString(String key, String defaultValue) {
		
		if (!settings.has(key)) {
			return defaultValue;
		}

		return settings.getString(key);
	}

	public boolean has(String name) {
		return settings.has(name);
	}

	public void load(File file) throws IOException {
		FileInputStream input = new FileInputStream(file);
		try {
			this.settings = new JSONObject(new JSONTokener(input));
		} finally {
			input.close();
		}
	}

	public void remove(String string) {
		settings.remove(string);
	}

	public void set(String string, Object value) {
		settings.put(string, value);
	}

	/**
	 * Set a password value (Encrypted by a user's password if saved to a file)
	 * 
	 * @param key
	 *            The password key
	 * @param value
	 *            The password
	 */
	public void setPassword(String key, Object value) {
		set(key, value.toString());
	}
	
	public String getPassword(String key) {
		if(settings.has(key)) {
			JSONObject obj = settings.getJSONObject(key);
			if(obj.getString("type").equals("password")) {
				obj.getString("encrypted");
			}
		}
		return null;
	}

	/**
	 * Write the JSON to an OutputStream (Using write to an OutputStreamWriter does not work for some reason)
	 * @param out
	 * 			The output stream to write to
	 * @throws IOException
	 * 			If an error occurs while writing
	 */
	public void saveTo(OutputStream out) throws IOException {
		try {
			out.write(settings.toString(4).getBytes());
		} catch(JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Save the settings to a specific file
	 * @param file
	 * 			The file to save to
	 * @throws IOException
	 * 			If an error occurs
	 */
	public void save(File file) throws IOException {
		FileOutputStream output = new FileOutputStream(file);
		try {
			saveTo(output);
		} finally {
			output.close();
		}
	}

	public void setBaseObject(JSONObject object) {
		this.settings = object;
	}

	public Object get(String name) {
		return settings.get(name);
	}

	public JSONObject getJSONObject(String string) {
		return settings.getJSONObject(string);
	}
	
	public String toString() {
		return settings.toString();
	}

	public boolean isEmpty(String string) {
		return settings.getString(string, "").equals("");
	}
}

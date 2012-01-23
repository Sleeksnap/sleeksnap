/**
 * Sleeksnap, the open source cross-platform screenshot uploader
 * Copyright (C) 2012 Nicole Schuiteman
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
package org.sleeksnap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

public class Configuration {
	/**
	 * The YAML Instance
	 */
	private static final Yaml yaml = new Yaml();
	
	/**
	 * The file which we loaded from
	 */
	private File file;
	
	/**
	 * The configuration map
	 */
	private Map<String, Object> config;
	
	/**
	 * Empty constructor
	 */
	public Configuration() {
		
	}
	
	/**
	 * Get a generic object, auto casting
	 * @param key
	 * 			The key
	 * @return
	 * 			The result
	 */
	@SuppressWarnings("unchecked")
	public <T> T get(String key) {
		return (T) config.get(key);
	}
	
	/**
	 * Get a boolean
	 * @param key
	 * 			The key
	 * @return
	 * 			The result
	 */
	public boolean getBoolean(String key) {
		if(config.containsKey(key)) {
			return (Boolean) config.get(key);
		}
		return false;
	}
	
	/**
	 * Get an integer
	 * @param key
	 * 			The key
	 * @return
	 * 			The integer
	 */
	public int getInteger(String key) {
		return (Integer) config.get(key);
	}
	
	/**
	 * Get a map which is automatically cast to the class specified
	 * @param key
	 * 			The key
	 * @return
	 * 			The map attached to the specified key
	 */
	@SuppressWarnings("unchecked")
	public <K, V> Map<K, V> getMap(String key) {
		return (Map<K, V>) config.get(key);
	}
	
	/**
	 * Get an object
	 * @param key
	 * 			The key
	 * @return
	 * 			The object
	 */
	public Object getObject(String key) {
		return config.get(key);
	}
	
	/**
	 * Get a string result
	 * @param key
	 * 			The key
	 * @return
	 */
	public String getString(String key) {
		return (String) config.get(key);
	}
	
	/**
	 * Load the configuration from a file
	 * @param file
	 * 			The file
	 * @throws IOException
	 * 			If an error occurred while loading
	 */
	@SuppressWarnings("unchecked")
	public void load(File file) throws IOException {
		this.file = file;
		this.config = (Map<String, Object>) yaml.load(new FileInputStream(file));
	}
	
	/**
	 * Load from a string
	 * @param contents
	 * 			The YAML Contents
	 */
	@SuppressWarnings("unchecked")
	public void load(String contents) {
		this.config = (Map<String, Object>) yaml.load(contents);
	}
	
	/**
	 * Put a value
	 * @param key
	 * 			The key
	 * @param value
	 * 			The value
	 */
	public void put(String key, Object value) {
		if(config == null) {
			System.out.println("CONFIG NULL");
		}
		if(key == null || value == null)
			System.out.println("Key or value null");
		config.put(key, value);
	}
	
	/**
	 * Save to the original file
	 * @throws IOException
	 * 				If an error occurred
	 */
	public void save() throws IOException {
		if(file == null) {
			throw new IOException("File not set! Cannot save to file");
		}
		FileWriter writer = new FileWriter(file);
		try {
			yaml.dump(config, writer);
		} finally {
			writer.close();
		}
	}

	/**
	 * Check whether the configuration map contains a string
	 * @param string
	 * 			The string
	 * @return
	 * 			Whether it contains the string or not
	 */
	public boolean contains(String string) {
		return config.containsKey(string);
	}
}

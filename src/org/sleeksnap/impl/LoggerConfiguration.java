package org.sleeksnap.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;

import org.sleeksnap.util.Util;

/**
 * A utility class to generate a Properties object containing logging information
 * It only supports basic handlers and levels currently.
 * 
 * @author Nikki
 *
 */
public class LoggerConfiguration {
	/**
	 * The list of normal handlers
	 */
	private List<String> handlers = new ArrayList<String>();
	
	/**
	 * The map of handlers to map to Loggers
	 */
	private Map<String, List<String>> namedHandlers = new HashMap<String, List<String>>();
	
	/**
	 * The map of names -> levels
	 */
	private Map<String, Level> levels = new HashMap<String, Level>();
	
	/**
	 * Add a handler to this logging configuration
	 * 
	 * @param handlerClass
	 * 			The handler class
	 */
	public void addHandler(Class<? extends Handler> handlerClass) {
		handlers.add(handlerClass.getName());
	}
	
	/**
	 * Add a handler to a named logger
	 * @param name
	 * 			The logger name
	 * @param handlerClass
	 * 			The handler class
	 */
	public void addHandler(String name, Class<? extends Handler> handlerClass) {
		List<String> list = namedHandlers.get(name);
		if(list == null) {
			namedHandlers.put(name, list = new ArrayList<String>());
		}
		list.add(handlerClass.getName());
	}

	/**
	 * Add a list of global handlers
	 * @param classes
	 * 			The handlers to add
	 */
	public void addHandlers(Class<? extends Handler>... classes) {
		for(Class<? extends Handler> handler : classes) {
			addHandler(handler);
		}
	}
	
	/**
	 * Add a list of handlers to a named logger
	 * @param name
	 * 			The logger name
	 * @param classes
	 * 			The handler classes
	 */
	public void addHandlers(String name, Class<? extends Handler>... classes) {
		List<String> list = namedHandlers.get(name);
		if(list == null) {
			namedHandlers.put(name, list = new ArrayList<String>());
		}
		for(Class<? extends Handler> handler : classes) {
			list.add(handler.getName());
		}
	}
	
	/**
	 * Set a logger's level
	 * @param name
	 * 			The logger name, or empty for global
	 * @param level
	 * 			The level to set
	 */
	public void setLevel(String name, Level level) {
		levels.put(name, level);
	}

	/**
	 * Sets the global logger's level
	 * @param level
	 * 			The level to set
	 */
	public void setLevel(Level level) {
		levels.put("", level);
	}
	
	/**
	 * Converts this configuration into an InputStream (by going to java.util.Properties, then writing that to a ByteArrayInputStream
	 * @return
	 * 			The InputStream object
	 * @throws IOException
	 * 			If an error occurred while writing
	 */
	public InputStream toInputStream() throws IOException {
		Properties props = toProperties();
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		props.store(os, "");
		return new ByteArrayInputStream(os.toByteArray());
	}

	/**
	 * Convert this configuration to a Properties object
	* @return
	* 			The Properties object
	 */
	private Properties toProperties() {
		Properties properties = new Properties();
		if(!handlers.isEmpty()) {
			properties.put("handlers", Util.implodeList(handlers, ","));
		}
		if(!namedHandlers.isEmpty()) {
			for(Entry<String, List<String>> e : namedHandlers.entrySet()) {
				properties.put(e.getKey() + ".handlers",  Util.implodeList(e.getValue(), ","));
			}
		}
		if(!levels.isEmpty()) {
			for(Entry<String, Level> level : levels.entrySet()) {
				properties.put(level.getKey() + ".level", level.getValue().getName());
			}
		}
		return properties;
	}
	
	/**
	 * Apply this logging configuration directly to the Java logger
	 * @throws IOException
	 * 			If an error occurred generating and applying
	 */
	public void apply() throws IOException {
		LogManager.getLogManager().readConfiguration(toInputStream());
	}
}

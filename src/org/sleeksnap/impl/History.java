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
package org.sleeksnap.impl;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

/**
 * A basic history manager loading and saving to YAML
 * 
 * @author Nikki
 * 
 */
public class History {
	
	/**
	 * The History Gson instance. It is different because we need a different Date serializer to keep the old history files working.
	 */
	private Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new DateSerializer()).create();

	/**
	 * The history storage class
	 */
	private List<HistoryEntry> history = new LinkedList<HistoryEntry>();

	/**
	 * The file to load/save to
	 */
	private File file;

	/**
	 * Initialize a new instance with the specified file
	 * 
	 * @param file
	 *            The file to use for storage
	 */
	public History(File file) {
		this.file = file;
	}

	/**
	 * Add an entry, forcing a save
	 * 
	 * @param entry
	 *            The entry
	 * @throws IOException
	 *             Thrown if an error occurred while saving, notify that local
	 *             history is disabled?
	 * @throws JSONException 
	 */
	public void addEntry(HistoryEntry entry) throws IOException {
		addEntry(entry, true);
	}

	/**
	 * Add an entry
	 * 
	 * @param entry
	 *            The entry
	 * @param save
	 *            True if a save is required
	 * @throws IOException
	 *             Thrown if an error occurred while saving, notify that local
	 *             history is disabled?
	 * @throws JSONException 
	 */
	public void addEntry(HistoryEntry entry, boolean save) throws IOException {
		synchronized (history) {
			history.add(entry);
			if (save) {
				save();
			}
		}
	}

	/**
	 * Get the history list
	 * 
	 * @return The list
	 */
	public List<HistoryEntry> getHistory() {
		return history;
	}

	/**
	 * Load the history from the specified file
	 * 
	 * @throws IOException
	 *             If an error occurs reading the file
	 */
	public void load() throws IOException {
		Reader reader = new FileReader(file);
		try {
			history = gson.fromJson(reader, new TypeToken<LinkedList<HistoryEntry>>() { }.getType());
		} finally {
			reader.close();
		}
	}

	/**
	 * Save the history to a file
	 * 
	 * @throws IOException
	 *             If an error occurs saving the file
	 */
	private void save() throws IOException {
		Writer writer = new FileWriter(file);
		try {
			gson.toJson(history, writer);
		} finally {
			writer.close();
		}
	}
	
	/**
	 * A temporary serialization class to work with the old HistoryEntry JSON Files
	 * 
	 * @author Nikki
	 *
	 */
	public class DateSerializer implements JsonSerializer<Date>, JsonDeserializer<Date> {

		@Override
		public Date deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
			return new Date(element.getAsLong());
		}

		@Override
		public JsonElement serialize(Date date, Type type, JsonSerializationContext context) {
			return new JsonPrimitive(date.getTime());
		}
		
	}
}

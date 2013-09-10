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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.sleeksnap.util.StreamUtils;

/**
 * A basic history manager loading and saving to YAML
 * 
 * @author Nikki
 * 
 */
public class History {

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
	 * @throws JSONException 
	 */
	public void load() throws IOException {
		InputStream input = new FileInputStream(file);
		try {
			history = new LinkedList<HistoryEntry>();
			JSONArray array = new JSONArray(StreamUtils.readContents(input));
			for(int i = 0; i < array.length(); i++) {
				history.add(new HistoryEntry(array.getJSONObject(i)));
			}
		} finally {
			input.close();
		}
	}

	/**
	 * Save the history to a file
	 * 
	 * @throws IOException
	 *             If an error occurs saving the file
	 * @throws JSONException 
	 */
	private void save() throws IOException {
		OutputStream output = new FileOutputStream(file);
		try {
			JSONArray array = new JSONArray();
			for(HistoryEntry e : history) {
				array.put(e.toJSONObject());
			}
			output.write(array.toString().getBytes());
		} finally {
			output.close();
		}
	}
}

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
package org.sleeksnap.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.yaml.snakeyaml.Yaml;

/**
 * A basic history manager loading and saving to YAML
 * 
 * @author Nikki
 *
 */
public class History {
	
	/**
	 * The static YAML instance
	 */
	private static Yaml yaml = new Yaml();
	
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
	 * @param file
	 * 			The file to use for storage
	 */
	public History(File file) {
		this.file = file;
	}
	
	/**
	 * Add an entry, forcing a save
	 * @param entry
	 * 			The entry
	 */
	public void addEntry(HistoryEntry entry) {
		addEntry(entry, true);
	}
	
	/**
	 * Add an entry
	 * @param entry
	 * 			The entry
	 * @param save
	 * 			True if a save is required
	 */
	public void addEntry(HistoryEntry entry, boolean save) {
		synchronized(history) {
			history.add(entry);
			if(save) {
				try {
					save();
				} catch (IOException e) {
				}
			}
		}
	}
	

	/**
	 * Get the history list
	 * @return
	 * 		The list
	 */
	public List<HistoryEntry> getHistory() {
		return history;
	}
	
	/**
	 * Load the history from the specified file
	 * @throws IOException
	 * 			If an error occurs reading the file
	 */
	@SuppressWarnings("unchecked")
	public void load() throws IOException {
		FileInputStream input = new FileInputStream(file);
		try {
			history = (List<HistoryEntry>) yaml.load(input);
		} finally {
			input.close();
		}
	}
	
	/**
	 * Save the history to a file
	 * @throws IOException
	 * 			If an error occurs saving the file
	 */
	private void save() throws IOException {
		FileWriter writer = new FileWriter(file);
		try {
			yaml.dump(history, writer);
		} finally {
			writer.close();
		}
	}
}

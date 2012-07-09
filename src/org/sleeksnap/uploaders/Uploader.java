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
package org.sleeksnap.uploaders;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * A basic uploader
 * 
 * @author Nikki
 * 
 * @param <T>
 *            The upload type
 */
@Settings(required = {}, optional = {})
public abstract class Uploader<T> {

	/**
	 * The properties instance
	 */
	protected Properties settings = new Properties();

	/**
	 * Get the uploader name
	 * 
	 * @return The uploader name
	 */
	public abstract String getName();

	/**
	 * Get the object type this class can upload
	 * 
	 * @return
	 */
	public abstract Class<?> getUploadType();

	/**
	 * Upload the specified object
	 * 
	 * @param t
	 *            The object
	 * @return The URL or Location
	 * @throws Exception
	 */
	public abstract String upload(T t) throws Exception;

	/**
	 * Load the settings from the specified file
	 * 
	 * @param file
	 *            The file to load from
	 * @throws IOException
	 *             If an error occurred while loading
	 */
	public void loadSettings(File file) throws IOException {
		FileInputStream input = new FileInputStream(file);
		try {
			settings.loadFromXML(input);
		} finally {
			input.close();
		}
	}

	/**
	 * Save the settings to the specified file
	 * 
	 * @param file
	 *            The file to save to
	 * @throws IOException
	 *             If an error occurred while saving
	 */
	public void saveSettings(File file) throws IOException {
		FileOutputStream out = new FileOutputStream(file);
		try {
			settings.storeToXML(out, "Uploader settings for "
					+ getClass().getName());
		} finally {
			out.close();
		}
	}

	/**
	 * Set this uploader's settings
	 * 
	 * @param settings
	 *            The Properties object containing the settings
	 */
	public void setSettings(Properties settings) {
		this.settings = settings;
	}

	/**
	 * Get this uploader's properties
	 * 
	 * @return The properties
	 */
	public Properties getSettings() {
		return settings;
	}
}
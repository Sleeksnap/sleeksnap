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

import org.sleeksnap.upload.Upload;
import org.sleeksnap.uploaders.settings.UploaderSettings;

/**
 * A basic uploader
 * 
 * @author Nikki
 * 
 * @param <T>
 *            The upload type
 */
public abstract class Uploader<T extends Upload> {

	/**
	 * The properties instance
	 */
	protected UploaderSettings settings = new UploaderSettings();
	
	/**
	 * The parent uploader, used if this is a sub uploader of another generic uploader
	 */
	protected Uploader<?> parent;
	
	public Uploader() {
		
	}
	
	public Uploader(Uploader<?> parent) {
		this.parent = parent;
	}

	/**
	 * Get the uploader name
	 * 
	 * @return
	 * 		The uploader name
	 */
	public abstract String getName();

	/**
	 * Upload the specified object
	 * 
	 * @param t
	 *      The object
	 * @return
	 * 		The URL or Location
	 * @throws Exception
	 */
	public abstract String upload(T t) throws Exception;

	/**
	 * Load the settings from the specified file
	 * 
	 * @param file
	 * 		The file to load from
	 * @throws IOException
	 *             If an error occurred while loading
	 */
	public void loadSettings(File file) throws IOException {
		FileInputStream input = new FileInputStream(file);
		try {
			settings.load(file);
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
			settings.saveTo(out);
		} finally {
			out.close();
		}
	}
	
	/**
	 * Can be overridden by the uploader to validate the settings.
	 * 
	 * If invalid, either throw an UploaderConfigurationException or display your own message.
	 * 
	 * @param newSettings
	 * 			The settings object
	 * @return
	 * 			true if valid, false if invalid.
	 */
	public boolean validateSettings(UploaderSettings newSettings) throws UploaderConfigurationException {
		return true;
	}
	
	/**
	 * Can be overidden to get a call when it is activated/set as default (On Load or User Settings)
	 * By default, if this is a sub uploader for a Generic uploader, it will call the parent's onActivation method
	 */
	public void onActivation() {
		if(parent != null) {
			parent.onActivation();
		}
	}

	/**
	 * Set this uploader's settings
	 * 
	 * @param settings
	 *            The Properties object containing the settings
	 */
	public void setSettings(UploaderSettings settings) {
		this.settings = settings;
	}

	/**
	 * Get this uploader's properties
	 * 
	 * @return The properties
	 */
	public UploaderSettings getSettings() {
		return settings;
	}
	
	/**
	 * Set this uploader's parent
	 */
	public void setParentUploader(Uploader<?> parent) {
		this.parent = parent;
	}
	
	public Uploader<?> getParentUploader() {
		return parent;
	}

	public boolean hasParent() {
		return parent != null;
	}
	
	/**
	 * Check if this class directly has settings
	 * @return
	 * 		True if this class has settings
	 */
	public boolean hasDirectSettings() {
		return getClass().isAnnotationPresent(Settings.class);
	}
	
	/**
	 * Check if this uploader (or generic uploader parent) has settings
	 * @return
	 * 		True if this uploader has settings
	 */
	public boolean hasSettings() {
		return hasDirectSettings() || parent != null && parent.hasSettings();
	}

	/**
	 * Get the Settings annotation from an uploader
	 * 
	 * @return The settings, or null if it doesn't have any
	 */
	public Settings getSettingsAnnotation() {
		Settings settings = getClass().getAnnotation(Settings.class);
		
		if (settings == null && parent != null) {
			settings = parent.getSettingsAnnotation();
		}
		return settings;
	}
}
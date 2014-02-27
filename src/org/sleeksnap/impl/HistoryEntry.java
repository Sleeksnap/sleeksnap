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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A simple class to wrap a History entry
 * 
 * @author Nikki
 * 
 */
public class HistoryEntry {

	/**
	 * The date format used for the toString function
	 */
	private static DateFormat format = new SimpleDateFormat(
			"E, MMM d yyyy hh:mm:ss a");

	/**
	 * The uploaded file URL
	 */
	private String url;

	/**
	 * The name of the uploader used
	 */
	private String uploader;

	/**
	 * The date/time uploaded
	 */
	private Date date;

	/**
	 * A blank constructor... used for serialization beans
	 */
	public HistoryEntry() {

	}

	/**
	 * Constructs a new HistoryEntry object with the specified URL and uploader,
	 * and a new date object
	 * 
	 * @param url
	 *            The upload url
	 * @param uploader
	 *            The uploader name
	 */
	public HistoryEntry(String url, String uploader) {
		this(url, uploader, new Date());
	}

	/**
	 * Constructs a new HistoryEntry object with the specified URL, uploader and
	 * date
	 * 
	 * @param url
	 *            The URL
	 * @param uploader
	 *            The uploader name
	 * @param date
	 *            The date that the entry was made
	 */
	public HistoryEntry(String url, String uploader, Date date) {
		this.url = url;
		this.uploader = uploader;
		this.date = date;
	}

	/**
	 * Get the upload date
	 * 
	 * @return The date
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * Get the uploader name
	 * 
	 * @return The uploader name
	 */
	public String getUploader() {
		return uploader;
	}

	/**
	 * Get the uploaded file URL
	 * 
	 * @return The URL
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * Set the date
	 * 
	 * @param date
	 *            The date object to set it to
	 */
	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * Set the uploader name
	 * 
	 * @param uploader
	 *            The uploader name
	 */
	public void setUploader(String uploader) {
		this.uploader = uploader;
	}

	/**
	 * Set the URL
	 * 
	 * @param url
	 *            The URL
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String toString() {
		return format.format(date) + " - " + url;
	}
}
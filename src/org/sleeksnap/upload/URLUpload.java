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
package org.sleeksnap.upload;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * While technically not an Upload, it is mostly used for URL Shorteners
 * 
 * @author Nikki
 *
 */
public class URLUpload implements Upload {
	
	/**
	 * The URL of this upload
	 */
	private URL url;
	
	public URLUpload(URL url) {
		this.url = url;
	}

	public URLUpload(String url) throws MalformedURLException {
		this.url = new URL(url);
	}

	@Override
	public InputStream asInputStream() {
		throw new IOException("URLs cannot be transformed into InputStreams!");
	}
	
	/**
	 * Set the URL
	 * @param url
	 * 			The URL to set it to
	 */
	public void setURL(URL url) {
		this.url = url;
	}

	/**
	 * Get the URL
	 * @return
	 * 		The URL
	 */
	public URL getURL() {
		return url;
	}
	
	@Override
	public String toString() {
		return url.toString();
	}
}

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
package org.sleeksnap.uploaders.url;

import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.sleeksnap.uploaders.Uploader;
import org.sleeksnap.util.StreamUtils;

/**
 * A URL Shortener for http://goo.gl
 * 
 * @author Nikki
 *
 */
public class GoogleShortener extends Uploader<URL> {

	/**
	 * The ID Pattern
	 */
	private static final Pattern ID_PATTERN = Pattern
			.compile("\"id\"\\:\\s*\"(.*?)\"");

	/**
	 * The page url
	 */
	private static final String PAGE_URL = "https://www.googleapis.com/urlshortener/v1/url";

	
	@Override
	public String getName() {
		return "Goo.gl";
	}

	@Override
	public Class<?> getUploadType() {
		return URL.class;
	}

	@Override
	public String upload(URL url) throws Exception {
		URLConnection connection = new URL(PAGE_URL).openConnection();
		connection.setDoOutput(true);
		connection.setRequestProperty("Content-type", "application/json");
		OutputStreamWriter writer = new OutputStreamWriter(
				connection.getOutputStream());
		writer.write("{\"longUrl\": \"" + url + "\"}");
		writer.flush();
		writer.close();

		String contents = StreamUtils.readContents(connection.getInputStream());
		Matcher matcher = ID_PATTERN.matcher(contents);
		if (matcher.find()) {
			return matcher.group(1);
		} else {
			throw new Exception("Unable to find short url");
		}
	}
}

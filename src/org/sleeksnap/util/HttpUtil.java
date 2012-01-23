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
package org.sleeksnap.util;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * A simple HTTP Utility which assists with POST/GET methods
 * 
 * @author Nikki
 *
 */
public class HttpUtil {
	
	/**
	 * Attempt to encode the string silenty
	 * @param string
	 * 			The string
	 * @return
	 * 			The encoded string
	 */
	public static String encode(String string) {
		try {
			return URLEncoder.encode(string, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			//Ignored
		}
		return string;
	}
	
	/**
	 * Alias for <code>executeGet(URL url)</code>
	 * @param url
	 * 			The URL
	 * @return
	 * 			The response
	 * @throws IOException
	 * 			If an error occurred
	 */
	public static String executeGet(String url) throws IOException {
		return executeGet(new URL(url));
	}

	/**
	 * Execute a GET request
	 * @param url
	 * 			The URL
	 * @return
	 * 			The response
	 * @throws IOException
	 * 			If an error occurred
	 */
	public static String executeGet(URL url) throws IOException {
		return StreamUtils.readContents(url.openStream());
	}
	
	/**
	 * Alias for <code>executePost(URL url, String data)</code>, constructs the url
	 * @param url
	 * 			The URL
	 * @param data
	 * 			The data
	 * @return
	 * 			The response
	 * @throws IOException
	 * 			If an error occurred
	 */
	public static String executePost(String url, String data) throws IOException {
		return executePost(new URL(url), data);
	}
	
	/**
	 * Execute a POST request
	 * @param url
	 * 			The URL
	 * @param data
	 * 			The data
	 * @return
	 * 			The response
	 * @throws IOException
	 * 			If an error occurred
	 */
	public static String executePost(URL url, String data)
			throws IOException {
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestProperty("User-Agent", Util.getHttpUserAgent());
		connection.setDoOutput(true);
		try {
			OutputStreamWriter writer = new OutputStreamWriter(
					connection.getOutputStream());
			writer.write(data);
			writer.flush();
			writer.close();
	
			return StreamUtils.readContents(connection.getInputStream());
		} finally {
			connection.disconnect();
		}
	}
	
	/**
	 * Alias for <code>executePostWithLocation(URL url, String data)</code>, constructs the url
	 * @param url
	 * 			The URL
	 * @param data
	 * 			The data
	 * @return
	 * 			The response
	 * @throws IOException
	 * 			If an error occurred
	 */
	public static String executePostWithLocation(String url, String data) throws IOException {
		return executePostWithLocation(new URL(url), data);
	}

	/**
	 * POST to a URL, then get the Location header for the result
	 * @param url
	 * 			The url
	 * @param data
	 * 			The data
	 * @return
	 * 			The result
	 * @throws IOException
	 * 			If an error occurred
	 */
	public static String executePostWithLocation(URL url, String data) throws IOException {
		if(HttpURLConnection.getFollowRedirects()) {
			HttpURLConnection.setFollowRedirects(false);
		}
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setDoOutput(true);
		try {
			OutputStreamWriter writer = new OutputStreamWriter(
					connection.getOutputStream());
			writer.write(data);
			writer.flush();
			writer.close();
			
			return connection.getHeaderField("Location");
		} finally {
			connection.disconnect();
		}
	}
}

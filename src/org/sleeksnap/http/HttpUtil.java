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
package org.sleeksnap.http;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.sleeksnap.util.StreamUtils;
import org.sleeksnap.util.Util;


/**
 * A simple HTTP Utility which assists with POST/GET methods
 * 
 * @author Nikki
 * 
 */
public class HttpUtil {

	/**
	 * Attempt to encode the string silenty
	 * 
	 * @param string
	 *            The string
	 * @return The encoded string
	 */
	public static String encode(String string) {
		try {
			return URLEncoder.encode(string, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// Ignored
		}
		return string;
	}

	/**
	 * Alias for <code>executeGet(URL url)</code>
	 * 
	 * @param url
	 *            The URL
	 * @return The response
	 * @throws IOException
	 *             If an error occurred
	 */
	public static String executeGet(String url) throws IOException {
		return executeGet(new URL(url));
	}

	/**
	 * Execute a GET request
	 * 
	 * @param url
	 *            The URL
	 * @return The response
	 * @throws IOException
	 *             If an error occurred
	 */
	public static String executeGet(URL url) throws IOException {
		return StreamUtils.readContents(url.openStream());
	}

	/**
	 * Alias for <code>executePost(URL url, String data)</code>, constructs the
	 * url
	 * 
	 * @param url
	 *            The URL
	 * @param data
	 *            The data
	 * @return The response
	 * @throws IOException
	 *             If an error occurred
	 */
	public static String executePost(String url, String data)
			throws IOException {
		return executePost(new URL(url), data);
	}
	
	/**
	 * POST to the specified URL with the specified map of values.
	 * 
	 * @param url
	 *            The URL
	 * @param values
	 *            The values to implode
	 * @return The HTTP response
	 * @throws IOException
	 *             If an error occurred while connecting/receiving the data
	 */
	public static String executePost(String url, PostData data)
			throws IOException {
		return executePost(url, data.toPostString());
	}
	
	/**
	 * POST to the specified URL with the specified map of values.
	 * 
	 * @param url
	 *            The URL
	 * @param values
	 *            The values to implode
	 * @return The HTTP response
	 * @throws IOException
	 *             If an error occurred while connecting/receiving the data
	 */
	public static String executePost(URL url, PostData data)
			throws IOException {
		return executePost(url, data.toPostString());
	}
	
	/**
	 * Execute a POST request
	 * 
	 * @param url
	 *            The URL
	 * @param data
	 *            The data
	 * @return The response
	 * @throws IOException
	 *             If an error occurred
	 */
	public static String executePost(URL url, String data) throws IOException {
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
	 * Alias for <code>executePostWithLocation(URL url, String data)</code>,
	 * constructs the url and request data
	 * 
	 * @param url
	 *            The URL
	 * @param data
	 *            The data
	 * @return The response
	 * @throws IOException
	 *             If an error occurred
	 */
	public static String executePostForLocation(String url, PostData data) throws IOException {
		return executePostForLocation(url, data.toPostString());
	}

	/**
	 * Alias for <code>executePostWithLocation(URL url, String data)</code>,
	 * constructs the url
	 * 
	 * @param url
	 *            The URL
	 * @param data
	 *            The data
	 * @return The response
	 * @throws IOException
	 *             If an error occurred
	 */
	public static String executePostForLocation(String url, String data)
			throws IOException {
		return executePostForLocation(new URL(url), data);
	}

	/**
	 * POST to a URL, then get the Location header for the result
	 * 
	 * @param url
	 *            The url
	 * @param data
	 *            The data
	 * @return The result
	 * @throws IOException
	 *             If an error occurred
	 */
	public static String executePostForLocation(URL url, String data)
			throws IOException {
		if (HttpURLConnection.getFollowRedirects()) {
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

			String location = connection.getHeaderField("Location");
			if(location == null) {
				throw new IOException("No location header found");
			}
			return location;
		} finally {
			connection.disconnect();
		}
	}
	
	/**
	 * Implode a map of key -> value pairs to a URL safe string
	 * 
	 * @param values
	 *            The values to implode
	 * @return The imploded string
	 * @throws IOException
	 *             If an error occurred while encoding any values. 
	 */
	public static String implode(Map<String, Object> values) throws IOException {
		StringBuilder builder = new StringBuilder();
		Iterator<Entry<String, Object>> iterator = values.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, Object> entry = iterator.next();
			builder.append(entry.getKey());

			if (entry.getValue() != null) {
				builder.append("=").append(URLEncoder.encode(entry.getValue().toString(), "UTF-8"));
			}
			if (iterator.hasNext())
				builder.append("&");
		}
		return builder.toString();
	}

	/**
	 * Parse an http query string
	 * @param string
	 * 			The string to parse
	 * @return
	 * 			The parsed string in a map.
	 */
	public static Map<String, Object> parseQueryString(String string) {
		Map<String, Object> values = new HashMap<String, Object>();
		String[] split = string.split("&");

		for(String s : split) {
			if(s.indexOf('=') != -1) {
				values.put(s.substring(0, s.indexOf('=')), s.substring(s.indexOf('=')+1));
			} else {
				values.put(s, null);
			}
		}

		return values;
	}
}

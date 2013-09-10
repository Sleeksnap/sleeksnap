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
package org.sleeksnap.uploaders.url;

import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONObject;
import org.sleeksnap.upload.URLUpload;
import org.sleeksnap.uploaders.UploadException;
import org.sleeksnap.uploaders.Uploader;
import org.sleeksnap.util.StreamUtils;

/**
 * A URL Shortener for http://goo.gl
 * 
 * @author Nikki
 * 
 */
public class GoogleShortener extends Uploader<URLUpload> {

	/**
	 * The page url
	 */
	private static final String PAGE_URL = "https://www.googleapis.com/urlshortener/v1/url";

	@Override
	public String getName() {
		return "Goo.gl";
	}

	@Override
	public String upload(URLUpload url) throws Exception {
		//Sanity check, otherwise google's api returns a 400
		if(url.toString().matches("http://goo.gl/[a-zA-Z0-9]{1,10}")) {
			return url.toString();
		}
		
		URLConnection connection = new URL(PAGE_URL).openConnection();
		connection.setDoOutput(true);
		connection.setRequestProperty("Content-type", "application/json");
		
		JSONObject out = new JSONObject();
		out.put("longUrl", url.getURL());
		
		OutputStreamWriter writer = new OutputStreamWriter(
				connection.getOutputStream());
		writer.write(out.toString());
		writer.flush();
		writer.close();

		String contents = StreamUtils.readContents(connection.getInputStream());
		
		JSONObject resp = new JSONObject(contents);
		if(resp.has("id")) {
			return resp.getString("id");
		} else {
			throw new UploadException("Unable to find short url");
		}
	}
}

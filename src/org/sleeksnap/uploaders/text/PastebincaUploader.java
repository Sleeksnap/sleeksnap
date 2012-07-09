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
package org.sleeksnap.uploaders.text;

import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

import org.sleeksnap.uploaders.Uploader;
import org.sleeksnap.util.HttpUtil;
import org.sleeksnap.util.StreamUtils;

/**
 * A text uploader for http://pastebin.ca
 * 
 * @author Nikki
 * 
 */
public class PastebincaUploader extends Uploader<String> {

	/**
	 * Basic variables, such as the API Key and URL
	 */
	private static final String PASTEBINCA_URL = "http://pastebin.ca/";
	private static final String PASTEBINCA_SCRIPTURL = PASTEBINCA_URL
			+ "quiet-paste.php";
	private static final String PASTEBINCA_KEY = "cjONz2tQBu4kZxDcugEVAdkSELcD77No";

	@Override
	public String getName() {
		return "Pastebin.ca";
	}

	@Override
	public Class<?> getUploadType() {
		return String.class;
	}

	@Override
	public String upload(String string) throws Exception {
		URL url = new URL(PASTEBINCA_SCRIPTURL);
		String data = "api=" + PASTEBINCA_KEY + "&content="
				+ HttpUtil.encode(string) + "&s=true&type=1&expiry=Never&name=";
		URLConnection connection = url.openConnection();
		connection.setDoOutput(true);
		OutputStreamWriter writer = new OutputStreamWriter(
				connection.getOutputStream());
		try {
			/**
			 * Write the image data and api key
			 */
			writer.write(data);
			writer.flush();
			writer.close();

			String resp = StreamUtils.readContents(connection.getInputStream());

			return PASTEBINCA_URL + resp.substring(resp.indexOf(':') + 1);
		} finally {
			writer.close();
		}
	}
}

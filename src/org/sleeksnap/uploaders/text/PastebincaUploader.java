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

import org.sleeksnap.uploaders.Uploader;
import org.sleeksnap.util.HttpUtil;

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
	public String upload(String string) throws Exception {
		String data = "api=" + PASTEBINCA_KEY + "&content="
				+ HttpUtil.encode(string) + "&s=true&type=1&expiry=Never&name=";
		
		String resp = HttpUtil.executePost(PASTEBINCA_SCRIPTURL, data);

		return PASTEBINCA_URL + resp.substring(resp.indexOf(':') + 1);
	}
}

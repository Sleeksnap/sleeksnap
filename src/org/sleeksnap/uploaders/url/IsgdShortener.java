/**
 * Sleeksnap, the open source cross-platform screenshot uploader
 * Copyright (C) 2014 Nikki <nikki@nikkii.us>
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

import org.sleeksnap.http.HttpUtil;
import org.sleeksnap.http.RequestData;
import org.sleeksnap.upload.URLUpload;
import org.sleeksnap.uploaders.UploadException;
import org.sleeksnap.uploaders.Uploader;

/**
 * A URL Shortener for is.gd
 * 
 * @author Nikki
 * 
 */
public class IsgdShortener extends Uploader<URLUpload> {

	/**
	 * The page URL Format
	 */
	private static final String PAGE_URL = "http://is.gd/api.php";

	@Override
	public String upload(URLUpload url) throws Exception {
		RequestData data = new RequestData();
		
		data.put("longurl", url.getURL());
		
		String contents = HttpUtil.executeGet(PAGE_URL, data);
		
		if(contents.startsWith("http")) {
			return contents;
		}
		
		throw new UploadException("Unexpected response from server.");
	}

	@Override
	public String getName() {
		return "is.gd";
	}
}

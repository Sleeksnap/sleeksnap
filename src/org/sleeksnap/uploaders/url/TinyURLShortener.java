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

import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.sleeksnap.uploaders.UploadException;
import org.sleeksnap.uploaders.Uploader;
import org.sleeksnap.util.HttpUtil;

/**
 * A URL Shortener for TinyURL
 * 
 * @author Nikki
 * 
 */
public class TinyURLShortener extends Uploader<URL> {

	/**
	 * The pattern to find the shortened url
	 */
	private static final Pattern urlPattern = Pattern
			.compile("<blockquote><b>(.*?)</b>");

	/**
	 * The page URL Format
	 */
	private static final String PAGE_URL = "http://tinyurl.com/create.php?url=%s";

	@Override
	public String getName() {
		return "Tinyurl";
	}

	@Override
	public String upload(URL t) throws Exception {
		String contents = HttpUtil.executeGet(String.format(PAGE_URL,
				HttpUtil.encode(t.toString())));
		Matcher matcher = urlPattern.matcher(contents);
		if (matcher.find()) {
			return matcher.group(1);
		} else {
			throw new UploadException("Cannot find the short url");
		}
	}
}

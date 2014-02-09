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
package org.sleeksnap.uploaders.text;

import org.sleeksnap.http.HttpUtil;
import org.sleeksnap.http.PostData;
import org.sleeksnap.http.ResponseType;
import org.sleeksnap.upload.TextUpload;
import org.sleeksnap.uploaders.Uploader;

/**
 * An uploader for the Paste2 pastebin.
 * 
 * @author Nikki
 *
 */
public class Paste2Uploader extends Uploader<TextUpload> {
	
	private static final String APIURL = "http://paste2.org/new-paste";

	@Override
	public String upload(TextUpload t) throws Exception {
		PostData data = new PostData();
		
		data.put("code", t.getText())
			.put("description", "")
			.put("lang", "text")
			.put("parent", "");
		
		return HttpUtil.executePost(APIURL, data, ResponseType.REDIRECT_URL);
	}

	@Override
	public String getName() {
		return "Paste2";
	}
}

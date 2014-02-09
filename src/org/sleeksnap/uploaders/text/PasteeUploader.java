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
import org.sleeksnap.upload.TextUpload;
import org.sleeksnap.uploaders.Settings;
import org.sleeksnap.uploaders.Uploader;

@Settings(required = {}, optional = { "apikey" })
public class PasteeUploader extends Uploader<TextUpload> {

	@Override
	public String getName() {
		return "Paste.ee";
	}

	@Override
	public String upload(TextUpload upload) throws Exception {
		PostData data = new PostData();
		
		data.put("key", settings.getStringBlankDefault("apikey", "public"))
			.put("language", "plain")
			.put("format", "simple")
			.put("paste", upload.getText());
		
		return HttpUtil.executePost("http://paste.ee/api", data);
	}
}

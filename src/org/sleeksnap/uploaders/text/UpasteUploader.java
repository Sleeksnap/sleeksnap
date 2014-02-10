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

import java.util.Map;

import org.sleeksnap.http.HttpUtil;
import org.sleeksnap.http.RequestData;
import org.sleeksnap.upload.TextUpload;
import org.sleeksnap.uploaders.Settings;
import org.sleeksnap.uploaders.UploadException;
import org.sleeksnap.uploaders.Uploader;
import org.sleeksnap.util.Util;
import org.sleeksnap.util.Utils.FormatUtil;

/**
 * An uploader for the Upaste.me pastebin.
 * 
 * @author Nikki
 *
 */
@Settings(required = {}, optional = { "private_token", "name", "privacy|combobox[Public,Private]", "expiration|combobox[No expiration,5 minutes,15 minutes,30 minutes,1 hour,6 hours,12 hours,1 day,3 days,5 days,10 days,15 days,1 month,3 months,6 months]" })
public class UpasteUploader extends Uploader<TextUpload> {
	
	private static final String APIURL = "http://upaste.me/api";
	
	private static final String APIKEY = "91f4d686e87b6ad41755954c4bb1aa96";

	@Override
	public String upload(TextUpload t) throws Exception {
		RequestData data = new RequestData();
		
		data.put("api_key", settings.getStringBlankDefault("private_token", APIKEY))
			.put("paste", t.getText())
			.put("name", settings.getString("name", ""))
			.put("privacy", settings.getString("privacy", "Public").equals("Private") ? 1 : 0)
			.put("expire", FormatUtil.formattedTimeToMinutes(settings.getString("expiration", "0")));
		
		String res = HttpUtil.executePost(APIURL, data);
		
		Map<String, String> m = Util.parseKeyValues(res, ": ", "\n");
		
		if(m.get("Status").equals("failure")) {
			throw new UploadException("Upload failed due to unknown reason.");
		}
		
		return m.get("Link");
	}

	@Override
	public String getName() {
		return "uPaste.me";
	}
}

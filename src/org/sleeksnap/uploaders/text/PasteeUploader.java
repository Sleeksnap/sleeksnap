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
import org.sleeksnap.http.RequestData;
import org.sleeksnap.upload.TextUpload;
import org.sleeksnap.uploaders.Settings;
import org.sleeksnap.uploaders.Uploader;
import org.sleeksnap.util.Utils.FormatUtil;

@Settings(required = {}, optional = { "apikey", "description", "expiration|combobox[No expiration,5 minutes,15 minutes,30 minutes,1 hour,6 hours,12 hours,1 day,3 days,5 days,10 days,15 days,1 month]", "expire_views|numspinner[default=0,min=0]"  })
public class PasteeUploader extends Uploader<TextUpload> {
	
	private static final String APIKEY = "public";

	@Override
	public String getName() {
		return "Paste.ee";
	}

	@Override
	public String upload(TextUpload upload) throws Exception {
		RequestData data = new RequestData();
		
		data.put("key", settings.getStringBlankDefault("apikey", APIKEY))
			.put("language", "plain")
			.put("format", "simple")
			.put("paste", upload.getText());
		
		int expireViews = settings.getInt("expire_views", 0);
		
		if(expireViews > 0) {
			data.put("expire", "views;" + expireViews);
		} else {
			data.put("expire", FormatUtil.formattedTimeToMinutes(settings.getString("expiration", "0")));
		}
		
		return HttpUtil.executePost("http://paste.ee/api", data);
	}
}

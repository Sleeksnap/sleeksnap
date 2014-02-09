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
import org.sleeksnap.uploaders.Settings;
import org.sleeksnap.uploaders.Uploader;
import org.sleeksnap.util.Utils.FormatUtil;

/**
 * An uploader for slexy.org
 * 
 * @author Nikki
 *
 */
@Settings(required = {}, optional = { "author", "description", "visibility|combobox[Public,Private]", "line_numbers|checkbox[true]", "expiration|combobox[No expiration,5 minutes,15 minutes,30 minutes,1 hour,6 hours,12 hours,1 day,3 days,5 days,10 days,15 days,1 month,3 months,6 months]" })
public class SlexyUploader extends Uploader<TextUpload> {
	
	private static final String APIURL = "http://slexy.org/index.php/submit";

	@Override
	public String upload(TextUpload t) throws Exception {
		PostData data = new PostData();
		data.put("raw_paste", t.getText())
			.put("author", settings.getString("author", ""))
			.put("comment", "")
			.put("desc", settings.getString("description", ""))
			.put("expire", FormatUtil.formattedTimeToMinutes(settings.getString("expiration", "0")))
			.put("language", "text")
			.put("linenumbers", settings.getBoolean("line_numbers", true))
			.put("permissions", settings.getString("visibility").equals("Private") ? 1 : 0)
			.put("submit", "Submit Paste")
			.put("tabbing", "true")
			.put("tabtype", "real");
		
		return HttpUtil.executePost(APIURL, data, ResponseType.REDIRECT_URL);
	}

	@Override
	public String getName() {
		return "Slexy.org";
	}
}

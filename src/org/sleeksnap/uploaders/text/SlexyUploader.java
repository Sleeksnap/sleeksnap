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
import org.sleeksnap.http.ResponseType;
import org.sleeksnap.upload.TextUpload;
import org.sleeksnap.uploaders.Uploader;
import org.sleeksnap.uploaders.settings.Setting;
import org.sleeksnap.uploaders.settings.SettingsClass;
import org.sleeksnap.uploaders.settings.types.ComboBoxSettingType;
import org.sleeksnap.util.Utils.FormatUtil;

/**
 * An uploader for slexy.org
 * 
 * @author Nikki
 *
 */
@SettingsClass(SlexyUploader.SlexySettings.class)
public class SlexyUploader extends Uploader<TextUpload> {
	
	/**
	 * The Slexy submit url
	 */
	private static final String APIURL = "http://slexy.org/index.php/submit";
	
	/**
	 * The settings object used for this uploader
	 */
	private SlexySettings settings;
	
	/**
	 * Construct this uploader with the loaded settings
	 * @param settings
	 * 			The settings object
	 */
	public SlexyUploader(SlexySettings settings) {
		this.settings = settings;
	}

	@Override
	public String upload(TextUpload t) throws Exception {
		RequestData data = new RequestData();
		
		data.put("raw_paste", t.getText())
			.put("author", settings.author != null ? settings.author : "")
			.put("comment", "")
			.put("desc", settings.description != null ? settings.description : "")
			.put("expire", FormatUtil.formattedTimeToSeconds(settings.expiration != null && !settings.expiration.isEmpty() ? settings.expiration : "0"))
			.put("language", "text")
			.put("linenumbers", settings.line_numbers)
			.put("permissions", settings.privacy == GenericPastePrivacy.Public ? 1 : 0)
			.put("submit", "Submit Paste")
			.put("tabbing", "true")
			.put("tabtype", "real");
		
		return HttpUtil.executePost(APIURL, data, ResponseType.REDIRECT_URL);
	}

	@Override
	public String getName() {
		return "Slexy.org";
	}
	
	public static class SlexySettings {
		//@Settings(required = {}, optional = { "author", "description", "visibility|combobox[Public,Private]", "line_numbers|checkbox[true]", "expiration|combobox[No expiration,5 minutes,15 minutes,30 minutes,1 hour,6 hours,12 hours,1 day,3 days,5 days,10 days,15 days,1 month,3 months,6 months]" })

		@Setting(name = "Author", description = "Paste Author", optional = true)
		public String author;
		
		@Setting(name = "Description", description = "Paste Description", optional = true)
		public String description;
		
		@Setting(name = "Privacy", description = "Paste Privacy", optional = true)
		public GenericPastePrivacy privacy = GenericPastePrivacy.Public;
		
		@Setting(name = "Line Numbers", description = "Show Line Numbers", defaults = "true", optional = true)
		public boolean line_numbers;
		
		@Setting(name = "Expiration", description = "Paste Expiration", type = ComboBoxSettingType.class, defaults = { "No expiration", "5 minutes", "15 minutes", "30 minutes", "1 hour", "6 hours", "12 hours", "1 day", "3 days", "5 days", "10 days", "15 days", "1 month", "3 months", "6 months" }, optional = true)
		public String expiration;
	}
}

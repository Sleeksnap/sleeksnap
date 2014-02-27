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
import org.sleeksnap.uploaders.UploadException;
import org.sleeksnap.uploaders.Uploader;
import org.sleeksnap.uploaders.settings.Setting;
import org.sleeksnap.uploaders.settings.SettingsClass;
import org.sleeksnap.uploaders.settings.types.ComboBoxSettingType;
import org.sleeksnap.util.Util;
import org.sleeksnap.util.Utils.FormatUtil;

/**
 * An uploader for the Upaste.me pastebin.
 * 
 * @author Nikki
 *
 */
@SettingsClass(UpasteUploader.UpasteSettings.class)
public class UpasteUploader extends Uploader<TextUpload> {
	
	/**
	 * The API URL
	 */
	private static final String APIURL = "http://upaste.me/api";
	
	/**
	 * The API Key
	 */
	private static final String APIKEY = "91f4d686e87b6ad41755954c4bb1aa96";

	/**
	 * The settings object used for this uploader
	 */
	private UpasteSettings settings;
	
	/**
	 * Construct this uploader with the loaded settings
	 * @param settings
	 * 			The settings object
	 */
	public UpasteUploader(UpasteSettings settings) {
		this.settings = settings;
	}

	@Override
	public String upload(TextUpload t) throws Exception {
		RequestData data = new RequestData();
		
		data.put("api_key", settings.private_token != null && !settings.private_token.isEmpty() ? settings.private_token : APIKEY)
			.put("paste", t.getText())
			.put("name", settings.name != null ? settings.name : "")
			.put("privacy", settings.privacy == GenericPastePrivacy.Private ? 1 : 0)
			.put("expire", FormatUtil.formattedTimeToMinutes(settings.expiration != null && !settings.expiration.isEmpty() ? settings.expiration : "0"));
		
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
	
	public static class UpasteSettings {
		@Setting(name = "Private Token", description = "Upaste Private Token", optional = true)
		public String private_token;
		
		@Setting(name = "Paste Name", description = "Paste Name", optional = true)
		public String name;
		
		@Setting(name = "Privacy", description = "Paste Privacy", optional = true)
		public GenericPastePrivacy privacy;
		
		@Setting(name = "Expiration", description = "Paste Expiration", type = ComboBoxSettingType.class, defaults = { "No expiration", "5 minutes", "15 minutes", "30 minutes", "1 hour", "6 hours", "12 hours", "1 day", "3 days", "5 days", "10 days", "15 days", "1 month", "3 months", "6 months" })
		public String expiration;
	}
}

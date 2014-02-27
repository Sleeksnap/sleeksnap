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
import org.sleeksnap.impl.APICredentials;
import org.sleeksnap.upload.TextUpload;
import org.sleeksnap.uploaders.Uploader;
import org.sleeksnap.uploaders.settings.Setting;
import org.sleeksnap.uploaders.settings.SettingsClass;
import org.sleeksnap.uploaders.settings.types.ComboBoxSettingType;

/**
 * A text uploader for http://pastebin.ca
 * 
 * @author Nikki
 * 
 */
@SettingsClass(PastebincaUploader.PastebincaSettings.class)
public class PastebincaUploader extends Uploader<TextUpload> {

	/**
	 * Basic variables, such as the API Key and URL
	 */
	private static final String PASTEBINCA_URL = "http://pastebin.ca/";
	private static final String PASTEBINCA_SCRIPTURL = PASTEBINCA_URL
			+ "quiet-paste.php";
	
	private static final String PASTEBINCA_KEY = APICredentials.getCredential(PastebincaUploader.class, "key");
	
	/**
	 * The settings object used for this uploader
	 */
	private PastebincaSettings settings;
	
	/**
	 * Construct this uploader with the loaded settings
	 * @param settings
	 * 			The settings object
	 */
	public PastebincaUploader(PastebincaSettings settings) {
		this.settings = settings;
	}

	@Override
	public String getName() {
		return "Pastebin.ca";
	}

	@Override
	public String upload(TextUpload text) throws Exception {		
		RequestData data = new RequestData();
		
		data.put("api", PASTEBINCA_KEY)
			.put("content", text.getText())
			.put("s", true)
			.put("type", "1")
			.put("expiry", settings.expiration)
			.put("name", "");
		
		String resp = HttpUtil.executePost(PASTEBINCA_SCRIPTURL, data);

		return PASTEBINCA_URL + resp.substring(resp.indexOf(':') + 1);
	}
	
	public static class PastebincaSettings {
		
		@Setting(name = "Paste Expiration", description = "Time until paste expires", type = ComboBoxSettingType.class, defaults = { "Never", "5 minutes", "10 minutes", "15 minutes", "30 minutes", "45 minutes", "1 hour", "2 hours", "4 hours", "8 hours", "12 hours", "1 day", "2 days", "3 days", "1 week", "2 weeks", "3 weeks", "1 month", "2 months", "3 months", "4 months", "5 months", "6 months", "1 year" })
		public String expiration;
	
	}
}

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

/**
 * An uploader for Pastie.org
 * 
 * @author Nikki
 * 
 */
@SettingsClass(PastieUploader.PastieSettings.class)
public class PastieUploader extends Uploader<TextUpload> {

	/**
	 * The submission url
	 */
	private static final String PASTIE_URL = "http://pastie.org/pastes";
	
	/**
	 * The settings object used for this uploader
	 */
	private PastieSettings settings;
	
	/**
	 * Construct this uploader with the loaded settings
	 * @param settings
	 * 			The settings object
	 */
	public PastieUploader(PastieSettings settings) {
		this.settings = settings;
	}

	@Override
	public String getName() {
		return "Pastie.org";
	}

	@Override
	public String upload(TextUpload t) throws Exception {
		RequestData data = new RequestData();
		
		data.put("paste[parser]", "plain_text")
			.put("paste[body]", t.getText())
			.put("paste[authorization]", "burger")
			.put("paste[restricted]", settings.privacy == PastiePrivacy.Private ? 1 : 0);
		
		return HttpUtil.executePost(PASTIE_URL, data, ResponseType.REDIRECT_URL);
	}
	
	public static class PastieSettings {
		@Setting(name = "Privacy", description = "Paste Privacy")
		public PastiePrivacy privacy = PastiePrivacy.Public;
	}
	
	private enum PastiePrivacy {
		Public, Private
	}
}

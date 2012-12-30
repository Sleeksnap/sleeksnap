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
package org.sleeksnap.uploaders.text;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.sleeksnap.uploaders.Settings;
import org.sleeksnap.uploaders.UploadException;
import org.sleeksnap.uploaders.Uploader;
import org.sleeksnap.uploaders.UploaderConfigurationException;
import org.sleeksnap.util.HttpUtil;

/**
 * A text uploader for Pastebin.com (New API)
 * 
 * @author Nikki
 * 
 */
@Settings(required = { "paste_exposure|combobox[Public,Unlisted,Private]" }, optional = { "username", "password|password" })
public class PastebinUploader extends Uploader<String> {

	/**
	 * Sleeksnap's API key
	 */
	private static final String API_KEY = "c61444e938ad8215390a77e2d64adcfe";

	/**
	 * API Options
	 */
	private static final String API_OPTION_PASTE = "paste";

	/**
	 * The URL of the API page
	 */
	private static final String API_URL = "http://pastebin.com/api/api_post.php";
	
	/**
	 * The URL of the API Auth page
	 */
	private static final String API_AUTH_URL = "http://pastebin.com/api/api_login.php";

	@Override
	public String getName() {
		return "Pastebin.com";
	}

	@Override
	public String upload(String contents) throws Exception {
		Map<String, Object> req = new HashMap<String, Object>();
		req.put("api_dev_key", API_KEY);
		req.put("api_option", API_OPTION_PASTE);
		req.put("api_paste_code", contents);
		// User signed in through API
		if (settings.containsKey("apikey")) {
			req.put("api_user_key", settings.getProperty("apikey"));
		}
		// Paste exposure is set.
		if (settings.containsKey("paste_exposure")) {
			PastebinExposure exp = PastebinExposure.valueOf(settings
					.getProperty("paste_exposure"));
			if (exp != null) {
				if (exp == PastebinExposure.Private
						&& (!settings.containsKey("apikey") || settings
								.getProperty("apikey").equals(""))) {
					throw new UploaderConfigurationException(
							"Pastebin.com only supports private pastes while logged in!");
				} else {
					req.put("api_paste_private", exp.ordinal());
				}
			}
		}
		// Execute it and let the user know if something is wrong with it.
		String resp = HttpUtil.executePost(API_URL, req);
		if (resp.startsWith("Bad")) {
			throw new UploadException(resp.substring(resp.indexOf(',') + 2));
		}
		return resp;
	}

	@Override
	public boolean validateSettings(Properties settings)
			throws UploaderConfigurationException {
		if (settings.containsKey("username")
				&& settings.containsKey("password")) {
			String username = settings.getProperty("username"), password = settings
					.getProperty("password");
			if (!username.equals("") && !password.equals("")) {
				// Validate the username and password, then get us a key.
				Map<String, Object> req = new HashMap<String, Object>();
				req.put("api_dev_key", API_KEY);
				req.put("api_user_name", username);
				req.put("api_user_password", password);
				try {
					String resp = HttpUtil.executePost(API_AUTH_URL, req);
					if (resp.startsWith("Bad")) {
						throw new UploaderConfigurationException(
								resp.substring(resp.indexOf(',') + 2));
					} else {
						settings.put("apikey", resp);
					}
				} catch (IOException e) {
					throw new UploaderConfigurationException(
							"Unable to verify username and password");
				}
			}
		}
		//We will not store the user's password in plain text!
		settings.remove("password");
		return true;
	}

	/**
	 * Pastebin exposure settings.
	 * 
	 * Documented as: We have 3 valid values available which you can use with
	 * the 'api_paste_private' parameter: 0 = Public 1 = Unlisted 2 = Private
	 * (only allowed in combination with api_user_key, as you have to be logged
	 * into your account to access the paste)
	 * 
	 * @author Nikki
	 * 
	 */
	private enum PastebinExposure {
		Public, Unlisted, Private
	}
}

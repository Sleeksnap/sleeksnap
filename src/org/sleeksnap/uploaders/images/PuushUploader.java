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
package org.sleeksnap.uploaders.images;

import java.io.IOException;

import org.sleeksnap.http.HttpUtil;
import org.sleeksnap.http.MultipartPostMethod;
import org.sleeksnap.http.MultipartPostMethod.MultipartFile;
import org.sleeksnap.http.RequestData;
import org.sleeksnap.upload.ImageUpload;
import org.sleeksnap.uploaders.Settings;
import org.sleeksnap.uploaders.UploadException;
import org.sleeksnap.uploaders.Uploader;
import org.sleeksnap.uploaders.UploaderConfigurationException;
import org.sleeksnap.uploaders.settings.UploaderSettings;
import org.sleeksnap.util.Utils.DateUtil;

/**
 * An uploader for puush.me
 * 
 * @author Nikki
 *
 */
@Settings(required = { "email", "password|password" }, optional = { })
public class PuushUploader extends Uploader<ImageUpload> {

	/**
	 * The auth page URL
	 */
	private static final String API_AUTH_URL = "http://puush.me/api/auth";
	
	/**
	 * The upload URL
	 */
	private static final String API_UPLOAD_URL = "http://puush.me/api/up";

	@Override
	public String getName() {
		return "Puu.sh";
	}

	@Override
	public String upload(ImageUpload image) throws Exception {
		if (!settings.has("apikey")) {
			throw new UploaderConfigurationException("API Key is not set! Please configure this uploader's settings.");
		}

		MultipartPostMethod post = new MultipartPostMethod(API_UPLOAD_URL);

		post.setParameter("k", settings.getString("apikey"));

		post.setParameter("z", "sleeksnap");

		post.setParameter("f",
				new MultipartFile("Sleeksnap-" + DateUtil.getCurrentDate()
						+ ".png", image.asInputStream()));

		post.execute();

		String[] fields = post.getResponse().split(",");

		if (fields.length == 1) {
			throw new UploadException("Puu.sh returned the wrong data!");
		}
		return fields[1];
	}
	
	@Override
	public boolean validateSettings(UploaderSettings properties) throws UploaderConfigurationException {
		if (!properties.has("email") || !properties.has("password")) {
			throw new UploaderConfigurationException("Username or password not set, please reconfigure puush's uploader!");
		}
		try {
			RequestData data = new RequestData();
			
			data.put("e", properties.getString("email"));
			data.put("p", properties.getString("password"));
			
			String resp = HttpUtil.executePost(API_AUTH_URL, data).trim();
			if(resp.startsWith("-1")) {
				throw new UploaderConfigurationException("Invalid login, please try again.");
			}
			
			String[] s = resp.split(",");
			
			if(s[1].length() > 0) {
				properties.set("apikey", s[1]);
			}
		} catch (IOException e) {
			throw new UploaderConfigurationException("Unable to validate auth due to unexpected error");
		}
		return true;
	}
}
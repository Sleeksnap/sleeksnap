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

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;
import org.sleeksnap.http.RequestData;
import org.sleeksnap.upload.ImageUpload;
import org.sleeksnap.uploaders.Settings;
import org.sleeksnap.uploaders.UploadException;
import org.sleeksnap.uploaders.Uploader;
import org.sleeksnap.uploaders.images.imgur.ImgurAuthentication;
import org.sleeksnap.uploaders.images.imgur.ImgurOAuthSettingType;
import org.sleeksnap.uploaders.settings.ParametersDialog;
import org.sleeksnap.util.StreamUtils;
import org.sleeksnap.util.Utils.ImageUtil;

/**
 * An uploader to upload images to imgur.com The included API Key is for use by
 * Sleeksnap ONLY, If you would like a key you may register one at imgur's
 * website
 * 
 * @author Nikki
 * 
 */
@Settings(required = {}, optional = { "account|imguroauth" })
public class ImgurUploader extends Uploader<ImageUpload> {

	public static final String CLIENT_ID = "b1793cd0a2c3844";
	public static final String CLIENT_SECRET = "e4027881760afb6bb0e5da5e224827963089c727";

	static {
		ParametersDialog.registerSettingType("imguroauth", new ImgurOAuthSettingType());
	}

	private ImgurAuthentication auth = new ImgurAuthentication(this);

	@Override
	public String getName() {
		return "Imgur";
	}

	@Override
	public String upload(ImageUpload image) throws Exception {
		// The API URL
		URL url = new URL("https://api.imgur.com/3/image.json");

		// Encode the image using our utility class
		RequestData req = new RequestData();
		req.put("image", ImageUtil.toBase64(image.getImage()));

		// Open a connection to the API and add our Client ID
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("POST");
		auth.addToConnection(connection);
		connection.setDoOutput(true);

		try {
			/**
			 * Write the image data and api key
			 */
			OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
			writer.write(req.toURLEncodedString());
			writer.flush();
			writer.close();
	
			String res = StreamUtils.readContents(connection.getInputStream());
			
			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				try {
	
					/**
					 * Parse the URL from the response
					 */
					JSONObject object = new JSONObject(res);
	
					JSONObject data = object.getJSONObject("data");
	
					if (!object.getBoolean("success")) {
						JSONObject error = data.getJSONObject("error");
						throw new UploadException(error.getString("message"));
					}
	
					return data.getString("link");
				} catch (JSONException e) {
					throw new UploadException("Malformed JSON Response");
				}
			} else {
				throw new UploadException("Imgur API returned HTTP Response " + connection.getResponseCode());
			}
		} finally {
			connection.disconnect();
		}
	}
}

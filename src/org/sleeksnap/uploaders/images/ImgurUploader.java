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

import org.json.JSONObject;
import org.sleeksnap.http.HttpUtil;
import org.sleeksnap.http.PostData;
import org.sleeksnap.upload.ImageUpload;
import org.sleeksnap.uploaders.UploadException;
import org.sleeksnap.uploaders.Uploader;
import org.sleeksnap.util.StreamUtils;
import org.sleeksnap.util.Utils.ImageUtil;

/**
 * An uploader to upload images to imgur.com
 * The included API Key is for use by Sleeksnap ONLY, If you would like a key you may register one at imgur's website
 * 
 * @author Nikki
 * 
 */
public class ImgurUploader extends Uploader<ImageUpload> {
	
	private static final String API_ID = "b1793cd0a2c3844";

	@Override
	public String getName() {
		return "Imgur";
	}

	@Override
	public String upload(ImageUpload image) throws Exception {
		// The API URL
		URL url = new URL("https://api.imgur.com/3/image.json");
		
		// Encode the image using our utility class
		PostData req = new PostData();
		req.put("image", ImageUtil.toBase64(image.getImage()));
		
		// Open a connection to the API and add our Client ID
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("POST");
		connection.addRequestProperty("Authorization", "Client-ID " + API_ID);
		connection.setDoOutput(true);
		
		/**
		 * Write the image data and api key
		 */
		OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
		writer.write(HttpUtil.implode(req));
		writer.flush();
		writer.close();
		
		/**
		 * Parse the URL from the response
		 */
		JSONObject object = new JSONObject(StreamUtils.readContents(connection.getInputStream()));
		
		JSONObject data = object.getJSONObject("data");

		if(!object.getBoolean("success")) {
			JSONObject error = data.getJSONObject("error");
			throw new UploadException(error.getString("message"));
		}
		
		return data.getString("link");
	}
}

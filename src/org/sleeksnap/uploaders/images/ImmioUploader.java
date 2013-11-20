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

import org.json.JSONObject;
import org.sleeksnap.Constants.Application;
import org.sleeksnap.http.MultipartPostMethod;
import org.sleeksnap.http.MultipartPostMethod.MultipartFile;
import org.sleeksnap.upload.ImageUpload;
import org.sleeksnap.uploaders.UploadException;
import org.sleeksnap.uploaders.Uploader;
import org.sleeksnap.util.Utils.FileUtils;

/**
 * An image uploader for http://imm.io
 * 
 * @author Nikki
 *
 */
public class ImmioUploader extends Uploader<ImageUpload> {
	
	private static final String API_URL = "http://imm.io/store/";

	@Override
	public String getName() {
		return "Imm.io";
	}

	@Override
	public String upload(ImageUpload image) throws Exception {
		MultipartPostMethod post = new MultipartPostMethod(API_URL);
		post.setParameter("image", new MultipartFile(FileUtils.generateFileName("png"), image.asInputStream()));
		post.setParameter("meta", new JSONObject().put("referer", new JSONObject().put("name", Application.NAME).put("url", Application.URL)).toString());
		post.execute();
		//Read the response as JSON
		JSONObject object = new JSONObject(post.getResponse());
		if(object.getBoolean("success")) {
			JSONObject payload = object.getJSONObject("payload");
			return payload.getString("uri");
		} else {
			throw new UploadException(object.getString("payload"));
		}
	}
}

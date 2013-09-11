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

import java.util.HashMap;
import java.util.Map;

import org.sleeksnap.http.MultipartPostMethod;
import org.sleeksnap.http.MultipartPostMethod.MultipartFile;
import org.sleeksnap.upload.ImageUpload;
import org.sleeksnap.uploaders.UploadException;
import org.sleeksnap.uploaders.Uploader;
import org.sleeksnap.util.Utils.DateUtil;

/**
 * An uploader for http://imagebin.ca
 * 
 * @author Nikki
 *
 */
public class ImagebinUploader extends Uploader<ImageUpload> {

	private static final String UPLOAD_URL = "http://imagebin.ca/upload.php";
	
	@Override
	public String upload(ImageUpload image) throws Exception {
		MultipartPostMethod m = new MultipartPostMethod(UPLOAD_URL);
		m.setParameter("file", new MultipartFile("Sleeksnap-" + DateUtil.getCurrentDate()
				+ ".png", image.asInputStream()));
		m.execute();
		String resp = m.getResponse();
		//Parsing it is not needed, but it's a good idea to make it easy to use.
		Map<String, String> res = new HashMap<String, String>();
		//The format is simple key:value, with url being a key, status will be 'error' or 'fail' on an error.
		String[] lines = resp.split("\n");
		for(String s : lines) {
			int idx = s.indexOf(':');
			if(idx != -1) {
				res.put(s.substring(0, idx), s.substring(idx+1));
			}
		}
		if(!res.containsKey("url")) {
			throw new UploadException(res.get("status"));
		}
		return res.get("url");
	}

	@Override
	public String getName() {
		return "Imagebin";
	}

}

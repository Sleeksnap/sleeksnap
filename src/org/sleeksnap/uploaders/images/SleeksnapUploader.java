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

import java.net.URLEncoder;

import org.sleeksnap.http.HttpUtil;
import org.sleeksnap.upload.ImageUpload;
import org.sleeksnap.uploaders.UploadException;
import org.sleeksnap.uploaders.Uploader;
import org.sleeksnap.util.Utils.ImageUtil;

/**
 * -- NOTE -- This currently does not work/is disabled due to hosting.
 * 
 * The default uploader for Sleeksnap images
 * Note: This uploader is meant to be used for Sleeksnap images only!
 * 
 * @author Nikki
 *
 */
public class SleeksnapUploader extends Uploader<ImageUpload> {

	@Override
	public String upload(ImageUpload image) throws Exception {
		String resp = HttpUtil.executePost("http://sleeksnap.com/upload", "image="
				+ URLEncoder.encode(ImageUtil.toBase64(image.getImage()), "UTF-8"));
		if(!resp.substring(0, 4).equals("http")) {
			throw new UploadException(resp);
		}
		return resp;
	}

	@Override
	public String getName() {
		return "Sleeksnap";
	}
}

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
package org.sleeksnap.upload;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import org.sleeksnap.util.Utils.ImageUtil;

/**
 * An Image based Upload
 * 
 * @author Nikki
 *
 */
public class ImageUpload implements Upload {
	
	/**
	 * The BufferedImage we are uploading
	 */
	private BufferedImage image;
	
	public ImageUpload(BufferedImage image) {
		this.image = image;
	}

	@Override
	public InputStream asInputStream() {
		try {
			return ImageUtil.toInputStream(image);
		} catch (IOException e) {
			return null;
		}
	}
	
	/**
	 * Covert this image into a Base64 string
	 * @return
	 * 			The image in base64
	 * @throws IOException
	 * 			If an error occurred while writing/reading into base64
	 */
	public String toBase64() throws IOException {
		return ImageUtil.toBase64(image);
	}

	/**
	 * Set this upload's image
	 * @param image
	 * 			The image to set
	 */
	public void setImage(BufferedImage image) {
		this.image = image;
	}

	/**
	 * Get this upload's image
	 * @return
	 * 		The image of the upload
	 */
	public BufferedImage getImage() {
		return image;
	}
}

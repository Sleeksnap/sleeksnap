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
package org.sleeksnap.filter;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.sleeksnap.upload.ImageUpload;
import org.sleeksnap.util.Util;

/**
 * A filter to attach your own watermark.
 * 
 * @author Nikki
 *
 */
public class WatermarkFilter implements UploadFilter<ImageUpload> {
	
	/**
	 * The watermark file, if it exists it will be applied if the image is large enough to have it not get in the way.
	 */
	private static final File watermarkFile = new File(Util.getWorkingDirectory(), "watermark.png");

	@Override
	public ImageUpload filter(ImageUpload object) {
		if(watermarkFile.exists()) {
			BufferedImage image = object.getImage();
			try {
				BufferedImage watermark = ImageIO.read(watermarkFile);
				if(image.getWidth() >= watermark.getWidth() && image.getHeight() >= watermark.getHeight()) {
					Graphics g = image.getGraphics();
					g.drawImage(watermark, image.getWidth()-watermark.getWidth(), image.getHeight()-watermark.getHeight(), watermark.getWidth(), watermark.getHeight(), null);
					g.dispose();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return object;
	}
}

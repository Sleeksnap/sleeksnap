package org.sleeksnap.filter;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.sleeksnap.util.Util;

/**
 * A filter to attach your own watermark.
 * 
 * @author Nikki
 *
 */
public class WatermarkFilter implements UploadFilter<BufferedImage> {
	
	/**
	 * The watermark file, if it exists it will be applied if the image is large enough to have it not get in the way.
	 */
	private static final File watermarkFile = new File(Util.getWorkingDirectory(), "watermark.png");

	@Override
	public BufferedImage filter(BufferedImage object) {
		if(watermarkFile.exists()) {
			try {
				BufferedImage watermark = ImageIO.read(watermarkFile);
				if(object.getWidth() >= watermark.getWidth() && object.getHeight() >= watermark.getHeight()) {
					Graphics g = object.getGraphics();
					g.drawImage(watermark, object.getWidth()-watermark.getWidth(), object.getHeight()-watermark.getHeight(), watermark.getWidth(), watermark.getHeight(), null);
					g.dispose();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return object;
	}

	@Override
	public Class<?> getType() {
		return BufferedImage.class;
	}
}

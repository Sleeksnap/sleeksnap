package org.sleeksnap.uploaders.images;

import java.awt.image.BufferedImage;
import java.net.URLEncoder;

import org.sleeksnap.uploaders.UploadException;
import org.sleeksnap.uploaders.Uploader;
import org.sleeksnap.util.HttpUtil;
import org.sleeksnap.util.Utils.ImageUtil;

/**
 * The default uploader for Sleeksnap images
 * Note: This uploader is meant to be used for Sleeksnap images only!
 * 
 * @author Nikki
 *
 */
public class SleeksnapUploader extends Uploader<BufferedImage> {

	@Override
	public String upload(BufferedImage image) throws Exception {
		String resp = HttpUtil.executePost("http://sleeksnap.com/upload", "image="
				+ URLEncoder.encode(ImageUtil.toBase64(image), "UTF-8"));
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

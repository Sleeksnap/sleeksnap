package org.sleeksnap.uploaders.images;
import java.awt.image.BufferedImage;
import java.net.URLEncoder;

import org.sleeksnap.uploaders.Uploader;
import org.sleeksnap.util.HttpUtil;
import org.sleeksnap.util.Utils.ImageUtil;

public class KsnpUploader extends Uploader<BufferedImage> {
	
	@Override
	public String upload(BufferedImage image) throws Exception {
		return HttpUtil.executePost("http://ksnp.co/upload", "image="
					+ URLEncoder.encode(ImageUtil.toBase64(image), "UTF-8"));
	}

	@Override
	public String getName() {
		return "Sleeksnap";
	}

	@Override
	public Class<?> getUploadType() {
		return BufferedImage.class;
	}
}

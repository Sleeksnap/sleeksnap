package org.sleeksnap.uploaders.images;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.Properties;

import org.sleeksnap.uploaders.Settings;
import org.sleeksnap.uploaders.UploadException;
import org.sleeksnap.uploaders.Uploader;
import org.sleeksnap.uploaders.UploaderConfigurationException;
import org.sleeksnap.util.MultipartPostMethod;
import org.sleeksnap.util.MultipartPostMethod.FileUpload;
import org.sleeksnap.util.Utils.DateUtil;
import org.sleeksnap.util.Utils.ImageUtil;

@Settings(required = { "apikey" }, optional = {})
public class PuushUploader extends Uploader<BufferedImage> {

	private static final String API_UPLOAD_URL = "http://puush.me/api/up";

	@Override
	public String getName() {
		return "Puu.sh";
	}

	@Override
	public Class<?> getUploadType() {
		return BufferedImage.class;
	}

	@Override
	public String upload(BufferedImage t) throws Exception {
		if (!settings.containsKey("apikey")) {
			throw new UploaderConfigurationException("API Key is not set!");
		}

		MultipartPostMethod post = new MultipartPostMethod(API_UPLOAD_URL);

		InputStream input = ImageUtil.toInputStream(t);

		post.setParameter("k", settings.getProperty("apikey"));

		post.setParameter("z", "sleeksnap");

		post.setParameter("f",
				new FileUpload("Sleeksnap-" + DateUtil.getCurrentDate()
						+ ".png", input));

		post.execute();

		String[] fields = post.getResponse().split(",");

		if (fields.length == 1) {
			throw new UploadException("Puu.sh returned the wrong data!");
		}
		return fields[1];
	}
	
	@Override
	public boolean validateSettings(Properties properties) throws UploaderConfigurationException {
		if (!properties.containsKey("apikey")) {
			throw new UploaderConfigurationException("API Key is not set!");
		}
		//TODO Validate the API key with an 'auth' login?
		return true;
	}
}
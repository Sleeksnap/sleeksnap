package org.sleeksnap.uploaders.images;

import java.awt.image.BufferedImage;

import org.json.JSONObject;
import org.sleeksnap.uploaders.UploadException;
import org.sleeksnap.uploaders.Uploader;
import org.sleeksnap.util.MultipartPostMethod;
import org.sleeksnap.util.MultipartPostMethod.FileUpload;
import org.sleeksnap.util.Utils.FileUtils;
import org.sleeksnap.util.Utils.ImageUtil;

/**
 * An image uploader for http://imm.io
 * 
 * @author Nikki
 *
 */
public class ImmioUploader extends Uploader<BufferedImage> {
	
	private static final String API_URL = "http://imm.io/store/";

	@Override
	public String getName() {
		return "Imm.io";
	}

	@Override
	public String upload(BufferedImage image) throws Exception {
		MultipartPostMethod post = new MultipartPostMethod(API_URL);
		post.setParameter("image", new FileUpload(FileUtils.generateFileName("png"), ImageUtil.toInputStream(image)));
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

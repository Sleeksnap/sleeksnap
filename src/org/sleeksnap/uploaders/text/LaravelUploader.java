package org.sleeksnap.uploaders.text;

import org.sleeksnap.http.HttpUtil;
import org.sleeksnap.http.RequestData;
import org.sleeksnap.http.ResponseType;
import org.sleeksnap.upload.TextUpload;
import org.sleeksnap.uploaders.Uploader;

/**
 * An uploader for Laravel's Paste Bucket
 * 
 * @author Nikki
 *
 */
public class LaravelUploader extends Uploader<TextUpload> {

	private static final String LARAVEL_URL = "http://laravel.io/bin";

	@Override
	public String upload(TextUpload t) throws Exception {
		RequestData data = new RequestData();
		data.put("code", t.getText());
		return HttpUtil.executePost(LARAVEL_URL, data, ResponseType.REDIRECT_URL);
	}

	@Override
	public String getName() {
		return "Laravel";
	}

}

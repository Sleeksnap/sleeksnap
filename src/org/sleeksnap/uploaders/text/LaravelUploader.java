package org.sleeksnap.uploaders.text;

import org.sleeksnap.http.HttpUtil;
import org.sleeksnap.upload.TextUpload;
import org.sleeksnap.uploaders.Uploader;

/**
 * An uploader for Laravel's Paste Bucket
 * 
 * @deprecated Laravel removed their paste bucket due to terms of use problems.
 * 
 * @author Nikki
 *
 */
@Deprecated
public class LaravelUploader extends Uploader<TextUpload> {

	private static final String LARAVEL_URL = "http://paste.laravel.com/";

	@Override
	public String upload(TextUpload t) throws Exception {
		return HttpUtil.executePostForLocation(LARAVEL_URL, "paste=" + HttpUtil.encode(t.getText()));
	}

	@Override
	public String getName() {
		return "Laravel";
	}

}

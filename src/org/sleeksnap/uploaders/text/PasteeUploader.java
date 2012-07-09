package org.sleeksnap.uploaders.text;

import org.sleeksnap.uploaders.Settings;
import org.sleeksnap.uploaders.Uploader;
import org.sleeksnap.util.HttpUtil;

@Settings(required = {}, optional = { "apikey" })
public class PasteeUploader extends Uploader<String> {

	@Override
	public String getName() {
		return "Paste.ee";
	}

	@Override
	public Class<?> getUploadType() {
		return String.class;
	}

	@Override
	public String upload(String t) throws Exception {
		return HttpUtil.executePost(
				"http://paste.ee/api",
				"key=" + settings.getProperty("apikey", "public")
						+ "&language=plain&format=simple&paste="
						+ HttpUtil.encode(t));
	}
}

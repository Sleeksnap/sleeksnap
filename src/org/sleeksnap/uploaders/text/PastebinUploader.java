package org.sleeksnap.uploaders.text;
import java.io.IOException;

import org.sleeksnap.uploaders.Uploader;
import org.sleeksnap.util.HttpUtil;

/**
 * A text uploader for Pastebin.com
 * 
 * @author Nikki
 *
 */
public class PastebinUploader extends Uploader<String> {
	
	/**
	 * The URL of the API page
	 */
	private static final String URL = "http://pastebin.com/api_public.php";

	@Override
	public String getName() {
		return "Pastebin.com";
	}

	@Override
	public Class<?> getUploadType() {
		return String.class;
	}

	@Override
	public String upload(String contents) throws IOException {
		return HttpUtil.executePost(URL, "paste_code="+HttpUtil.encode(contents));
	}
}

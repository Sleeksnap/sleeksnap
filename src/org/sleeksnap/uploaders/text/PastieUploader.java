package org.sleeksnap.uploaders.text;

import org.sleeksnap.uploaders.Uploader;
import org.sleeksnap.util.HttpUtil;

/**
 * An uploader for Pastie.org
 * 
 * @author Nikki
 *
 */
public class PastieUploader extends Uploader<String> {
	
	private static final String PASTEBIN_URL = "http://pastie.org/pastes";

	@Override
	public String getName() {
		return "Pastie.org";
	}

	@Override
	public Class<?> getUploadType() {
		return String.class;
	}

	@Override
	public String upload(String t) throws Exception {
		String url = HttpUtil.executePostWithLocation(PASTEBIN_URL, "paste[parser]=plain_text&paste[body]="+HttpUtil.encode(t)+"&paste[authorization]=burger&paste[restricted]=0");
		if(url == null) {
			throw new Exception("Failed to paste!");
		}
		return url;
	}
}

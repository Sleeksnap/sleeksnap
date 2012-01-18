package org.sleeksnap.uploaders.url;

import java.net.URL;

import org.sleeksnap.uploaders.Uploader;
import org.sleeksnap.util.HttpUtil;

/**
 * A url shortener for http://turl.ca
 * 
 * @author Nikki
 *
 */
public class TUrlShortener extends Uploader<URL> {
	
	/**
	 * The base URL
	 */
	private static final String TURL_BASE = "http://turl.ca/";

	@Override
	public String getName() {
		return "TUrl.ca";
	}

	@Override
	public Class<?> getUploadType() {
		return URL.class;
	}

	@Override
	public String upload(URL url) throws Exception {
		String resp = HttpUtil.executeGet(TURL_BASE+"api.php?url="+url);
		if(resp.contains("ERROR")) {
			throw new Exception("An error was reported from the URL Shortening service");
		}
		return TURL_BASE + resp.substring(resp.indexOf(':')+1);
	}
}

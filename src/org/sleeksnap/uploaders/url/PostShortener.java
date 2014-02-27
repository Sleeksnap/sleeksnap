package org.sleeksnap.uploaders.url;

import org.sleeksnap.http.HttpUtil;
import org.sleeksnap.http.RequestData;
import org.sleeksnap.impl.APICredentials;
import org.sleeksnap.upload.URLUpload;
import org.sleeksnap.uploaders.UploadException;
import org.sleeksnap.uploaders.Uploader;

/**
 * This is a URL Shortner for po.st, po.st can be used to track the stats of
 * your link. The included API Key is for use by Sleeksnap ONLY, If you would
 * like a key you may register one at po.st's website
 * 
 * @author Andrew Sampson - http://github.com/codeusa
 * 
 */
public class PostShortener extends Uploader<URLUpload> {

	/**
	 * The API KEY and URL
	 */
	private static final String API_KEY = APICredentials.getCredential(PostShortener.class, "key");
	
	private static final String API_URL = "http://po.st/api/shorten";

	@Override
	public String getName() {
		return "po.st";
	}

	@Override
	public String upload(final URLUpload url) throws Exception {
		RequestData data = new RequestData();
		
		data.put("longUrl", url.getURL())
			.put("apiKey", API_KEY)
			.put("format", "txt");

		final String shortURL = HttpUtil.executeGet(API_URL, data);
		
		if (shortURL.startsWith("http")) {
			return shortURL;
		}

		throw new UploadException("Unexpected response from server.");
	}

}
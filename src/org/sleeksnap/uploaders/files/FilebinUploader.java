package org.sleeksnap.uploaders.files;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.sleeksnap.uploaders.Settings;
import org.sleeksnap.uploaders.UploadException;
import org.sleeksnap.uploaders.Uploader;
import org.sleeksnap.util.MultipartPostMethod;

/**
 * An uploader for http://filebin.ca
 * http://filebin.ca/tools.php
 * 
 * @author Nikki
 *
 */
@Settings(required = {}, optional = { "api_key" })
public class FilebinUploader extends Uploader<File> {
	
	/**
	 * The Filebin API URL
	 */
	private static final String API_URL = "http://filebin.ca/upload.php";

	@Override
	public String getName() {
		return "Filebin.ca";
	}

	@Override
	public String upload(File file) throws Exception {
		MultipartPostMethod post = new MultipartPostMethod(API_URL);
		post.setParameter("file", file);
		if(settings.containsKey("api_key")) {
			post.setParameter("key", settings.getProperty("api_key"));
		}
		post.execute();
		String resp = post.getResponse();
		//Parsing it is not needed, but it's a good idea to make it easy to use.
		Map<String, String> res = new HashMap<String, String>();
		//The format is simple key:value, with url being a key, status will be 'error' or 'fail' on an error.
		String[] lines = resp.split("\n");
		for(String s : lines) {
			int idx = s.indexOf(':');
			if(idx != -1) {
				res.put(s.substring(0, idx), s.substring(idx+1));
			}
		}
		if(!res.containsKey("url")) {
			throw new UploadException(res.get("status"));
		}
		return res.get("url");
	}

}

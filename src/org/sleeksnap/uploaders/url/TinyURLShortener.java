package org.sleeksnap.uploaders.url;

import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.sleeksnap.uploaders.Uploader;
import org.sleeksnap.util.HttpUtil;

/**
 * A URL Shortener for TinyURL
 * 
 * @author Nikki
 *
 */
public class TinyURLShortener extends Uploader<URL> {
	
	/**
	 * The pattern to find the shortened url
	 */
	private static final Pattern urlPattern = Pattern.compile("<blockquote><b>(.*?)</b>");
	
	/**
	 * The page URL Format
	 */
	private static final String PAGE_URL = "http://tinyurl.com/create.php?url=%s";

	@Override
	public String getName() {
		return "Tinyurl";
	}

	@Override
	public Class<?> getUploadType() {
		return URL.class;
	}

	@Override
	public String upload(URL t) throws Exception {
		String contents = HttpUtil.executeGet(String.format(PAGE_URL, HttpUtil.encode(t.toString())));
		Matcher matcher = urlPattern.matcher(contents);
		if(matcher.find()) {
			return matcher.group(1);
		} else {
			throw new Exception("Cannot find the short url");
		}
	}
}

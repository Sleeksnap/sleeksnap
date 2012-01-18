package org.sleeksnap.uploaders.url;

import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.sleeksnap.uploaders.Uploader;
import org.sleeksnap.util.StreamUtils;

/**
 * A URL Shortener for http://goo.gl
 * 
 * @author Nikki
 *
 */
public class GoogleShortener extends Uploader<URL> {

	/**
	 * The ID Pattern
	 */
	private static final Pattern ID_PATTERN = Pattern
			.compile("\"id\"\\:\\s*\"(.*?)\"");

	/**
	 * The page url
	 */
	private static final String PAGE_URL = "https://www.googleapis.com/urlshortener/v1/url";

	
	@Override
	public String getName() {
		return "Goo.gl";
	}

	@Override
	public Class<?> getUploadType() {
		return URL.class;
	}

	@Override
	public String upload(URL url) throws Exception {
		URLConnection connection = new URL(PAGE_URL).openConnection();
		connection.setDoOutput(true);
		connection.setRequestProperty("Content-type", "application/json");
		OutputStreamWriter writer = new OutputStreamWriter(
				connection.getOutputStream());
		writer.write("{\"longUrl\": \"" + url + "\"}");
		writer.flush();
		writer.close();

		String contents = StreamUtils.readContents(connection.getInputStream());
		Matcher matcher = ID_PATTERN.matcher(contents);
		if (matcher.find()) {
			return matcher.group(1);
		} else {
			throw new Exception("Unable to find short url");
		}
	}
}

package org.sleeksnap.uploaders.text;

import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

import org.sleeksnap.uploaders.Uploader;
import org.sleeksnap.util.HttpUtil;
import org.sleeksnap.util.StreamUtils;

/**
 * A text uploader for http://pastebin.ca
 * 
 * @author Nikki
 *
 */
public class PastebincaUploader extends Uploader<String> {
	
	private static final String PASTEBINCA_URL = "http://pastebin.ca/";
	private static final String PASTEBINCA_SCRIPTURL = PASTEBINCA_URL+"quiet-paste.php";
	private static final String PASTEBINCA_KEY = "cjONz2tQBu4kZxDcugEVAdkSELcD77No";

	@Override
	public String getName() {
		return "Pastebin.ca";
	}

	@Override
	public Class<?> getUploadType() {
		return String.class;
	}

	@Override
	public String upload(String string) throws Exception {
		URL url = new URL(PASTEBINCA_SCRIPTURL);
		String data = "api="+PASTEBINCA_KEY+"&content="+HttpUtil.encode(string)+"&s=true&type=1&expiry=Never&name=";
		URLConnection connection = url.openConnection();
		connection.setDoOutput(true);
		OutputStreamWriter writer = new OutputStreamWriter(
				connection.getOutputStream());
		try {
			/**
			 * Write the image data and api key
			 */
			writer.write(data);
			writer.flush();
			writer.close();
			
			String resp = StreamUtils.readContents(connection.getInputStream());
			
			return PASTEBINCA_URL+resp.substring(resp.indexOf(':')+1);
		} finally {
			writer.close();
		}
	}
}

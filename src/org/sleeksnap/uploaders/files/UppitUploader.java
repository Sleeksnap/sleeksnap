package org.sleeksnap.uploaders.files;

import java.io.File;
import java.io.FileInputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.sleeksnap.uploaders.Uploader;
import org.sleeksnap.util.HttpUtil;
import org.sleeksnap.util.MultipartPostMethod;

/**
 * An uploader for http://uppit.com
 * @author Nikki
 *
 */
public class UppitUploader extends Uploader<File> {
	
	private static Pattern urlPattern = Pattern.compile("action=\"(.*?)\"");
	private static Pattern paramPattern = Pattern.compile("<input type=\"hidden\" name=\"(.*?)\" value=\"(.*?)\">");
	private static Pattern finalPattern = Pattern.compile("<textarea .*?>(.*?)\\s*</textarea>");

	@Override
	public String getName() {
		return "Uppit.com";
	}

	@Override
	public Class<?> getUploadType() {
		return File.class;
	}

	@Override
	public String upload(File t) throws Exception {
		String contents = HttpUtil.executeGet("http://uppit.com/");
		Matcher matcher = urlPattern.matcher(contents);
		if(matcher.find()) {
			MultipartPostMethod method = new MultipartPostMethod(matcher.group(1));
			matcher = paramPattern.matcher(contents);
			while(matcher.find()) {
				method.setParameter(matcher.group(1), matcher.group(2));
			}
			method.setParameter("tos", "1");
			method.setParameter("file_1", new MultipartPostMethod.FileUpload(t.getName(), new FileInputStream(t)));
			
			method.execute();
			try {
				matcher = finalPattern.matcher(method.getResponse());
				
				if(matcher.find()) {
					return matcher.group(1);
				}
			} finally {
				method.close();
			}
			throw new Exception("Unable to find url!");
		}
		return null;
	}
}

/**
 * Sleeksnap, the open source cross-platform screenshot uploader
 * Copyright (C) 2012 Nikki <nikki@nikkii.us>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sleeksnap.uploaders.files;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.sleeksnap.uploaders.Uploader;
import org.sleeksnap.util.HttpUtil;
import org.sleeksnap.util.MultipartPostMethod;
import org.sleeksnap.util.MultipartPostMethod.FileUpload;

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
			method.setParameter("file_1", FileUpload.create(t));
			
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

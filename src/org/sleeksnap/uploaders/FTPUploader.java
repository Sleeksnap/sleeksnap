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
package org.sleeksnap.uploaders;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.sleeksnap.util.SimpleFTP;
import org.sleeksnap.util.Utils.DateUtil;
import org.sleeksnap.util.Utils.ImageUtil;

/**
 * A generic uploader for FTP servers Also serves as an example for the Settings
 * annotation and GenericUploaders
 * 
 * @author Nikki
 * 
 */
@Settings(required = { "hostname", "username", "password", "baseurl" }, optional = {
		"port", "remotedir" })
public class FTPUploader extends GenericUploader {

	/**
	 * The date format used when uploading
	 */

	/**
	 * The uploader array
	 */
	private Uploader<?>[] uploaders = new Uploader<?>[] {
			new FTPImageUploader(), new FTPTextUploader(),
			new FTPFileUploader() };

	/**
	 * Upload a file to the FTP server
	 * 
	 * @param fileName
	 *            The filename
	 * @param input
	 *            The input stream
	 * @return The final URL
	 * @throws IOException
	 *             If an error occurred
	 */
	public String ftpUpload(String fileName, InputStream input)
			throws IOException, UploaderConfigurationException {
		if (!settings.containsKey("hostname")
				|| !settings.containsKey("username")
				|| !settings.containsKey("password")
				|| !settings.containsKey("baseurl")) {
			throw new UploaderConfigurationException(
					"Missing hostname, username, password or baseurl!");
		}
		SimpleFTP ftp = new SimpleFTP();
		ftp.connect(settings.getProperty("hostname"),
				Integer.parseInt(settings.getProperty("port", "21")));
		if (settings.containsKey("remotedir")) {
			ftp.cwd(settings.getProperty("remotedir"));
		}
		try {
			ftp.stor(input, fileName);
		} finally {
			ftp.disconnect();
			input.close();
		}
		return String.format(settings.getProperty("baseurl", "%s"), fileName);
	}

	/**
	 * An uploader to deal with Image uploads
	 * 
	 * @author Nikki
	 * 
	 */
	public class FTPImageUploader extends Uploader<BufferedImage> {

		@Override
		public String getName() {
			return FTPUploader.this.getName();
		}

		@Override
		public Class<?> getUploadType() {
			return BufferedImage.class;
		}

		@Override
		public String upload(BufferedImage t) throws Exception {
			return ftpUpload(generateFileName(t), ImageUtil.toInputStream(t));
		}
	}

	/**
	 * An uploader to deal with Text uploads
	 * 
	 * @author Nikki
	 * 
	 */
	public class FTPTextUploader extends Uploader<String> {

		@Override
		public String getName() {
			return FTPUploader.this.getName();
		}

		@Override
		public Class<?> getUploadType() {
			return String.class;
		}

		@Override
		public String upload(String t) throws Exception {
			return ftpUpload(generateFileName(t),
					new ByteArrayInputStream(t.getBytes()));
		}
	}

	/**
	 * An uploader to deal with File uploads
	 * 
	 * @author Nikki
	 * 
	 */
	public class FTPFileUploader extends Uploader<File> {

		@Override
		public String getName() {
			return FTPUploader.this.getName();
		}

		@Override
		public Class<?> getUploadType() {
			return File.class;
		}

		@Override
		public String upload(File t) throws Exception {
			return ftpUpload(generateFileName(t), new FileInputStream(t));
		}
	}

	/**
	 * Generate a file name from a date object formatted for filenames, plus the
	 * filename if applicable
	 * 
	 * @param object
	 *            The object to be uploaded
	 * @return The filename
	 */
	public String generateFileName(Object object) {
		String name = DateUtil.getCurrentDate();
		if (object instanceof BufferedImage) {
			name += ".png";
		} else if (object instanceof String) {
			name += ".txt";
		} else if (object instanceof File) {
			name += ((File) object).getName();
		} else {
			name += ".file";
		}
		return name;
	}

	@Override
	public Uploader<?>[] getSubUploaders() {
		return uploaders;
	}

	@Override
	public String getName() {
		return "FTP Server";
	}
}

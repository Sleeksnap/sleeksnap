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
package org.sleeksnap.uploaders.generic;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.sleeksnap.upload.FileUpload;
import org.sleeksnap.upload.ImageUpload;
import org.sleeksnap.upload.TextUpload;
import org.sleeksnap.uploaders.Settings;
import org.sleeksnap.uploaders.Uploader;
import org.sleeksnap.uploaders.UploaderConfigurationException;
import org.sleeksnap.util.SimpleFTP;
import org.sleeksnap.util.Utils.DateUtil;

/**
 * A generic uploader for FTP servers Also serves as an example for the Settings
 * annotation and GenericUploaders
 * 
 * @author Nikki
 * 
 */
@Settings(required = { "hostname", "username", "password|password", "baseurl" }, optional = {
		"port|numspinner[default=21,min=0,max=65535]", "remotedir" })
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
		if (!settings.has("hostname") && !settings.isEmpty("hostname")
				|| !settings.has("username") && !settings.isEmpty("username")
				|| !settings.has("password") // Password can be empty.
				|| !settings.has("baseurl") && !settings.isEmpty("baseurl")) {
			throw new UploaderConfigurationException(
					"Missing hostname, username, password or baseurl!");
		}
		SimpleFTP ftp = new SimpleFTP();
		
		try {
			ftp.connect(settings.getString("hostname"), settings.getInt("port", 21), settings.getString("username"), settings.getString("password"));
		} catch (IOException e) {
			e.printStackTrace();
			throw new UploaderConfigurationException("Unable to connect to FTP server, please check your username and password.");
		}
		
		if (settings.has("remotedir") && !settings.isEmpty("remotedir")) {
			if(!ftp.cwd(settings.getString("remotedir"))) {
				throw new UploaderConfigurationException("Unable to change FTP directory.");
			}
		}
		
		try {
			ftp.stor(input, fileName);
		} finally {
			ftp.disconnect();
			input.close();
		}
		String baseUrl = settings.getString("baseurl", "%s");
		if(!baseUrl.contains("%s")) {
			baseUrl = baseUrl + "%s";
		}
		return String.format(baseUrl, fileName);
	}

	/**
	 * An uploader to deal with Image uploads
	 * 
	 * @author Nikki
	 * 
	 */
	public class FTPImageUploader extends Uploader<ImageUpload> {

		@Override
		public String getName() {
			return FTPUploader.this.getName();
		}

		@Override
		public String upload(ImageUpload t) throws Exception {
			return ftpUpload(generateFileName(t), t.asInputStream());
		}
	}

	/**
	 * An uploader to deal with Text uploads
	 * 
	 * @author Nikki
	 * 
	 */
	public class FTPTextUploader extends Uploader<TextUpload> {

		@Override
		public String getName() {
			return FTPUploader.this.getName();
		}

		@Override
		public String upload(TextUpload t) throws Exception {
			return ftpUpload(generateFileName(t), t.asInputStream());
		}
	}

	/**
	 * An uploader to deal with File uploads
	 * 
	 * @author Nikki
	 * 
	 */
	public class FTPFileUploader extends Uploader<FileUpload> {

		@Override
		public String getName() {
			return FTPUploader.this.getName();
		}

		@Override
		public String upload(FileUpload t) throws Exception {
			return ftpUpload(generateFileName(t), t.asInputStream());
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
		if (object instanceof ImageUpload) {
			name += ".png";
		} else if (object instanceof TextUpload) {
			name += ".txt";
		} else if (object instanceof FileUpload) {
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

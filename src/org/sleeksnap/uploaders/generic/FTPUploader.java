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
import org.sleeksnap.uploaders.Uploader;
import org.sleeksnap.uploaders.UploaderConfigurationException;
import org.sleeksnap.uploaders.settings.Password;
import org.sleeksnap.uploaders.settings.Setting;
import org.sleeksnap.uploaders.settings.SettingsClass;
import org.sleeksnap.util.SimpleFTP;
import org.sleeksnap.util.Utils.DateUtil;

/**
 * A generic uploader for FTP servers Also serves as an example for the Settings
 * annotation and GenericUploaders
 * 
 * @author Nikki
 * 
 */
@SettingsClass(FTPUploader.FTPUploaderSettings.class)
public class FTPUploader extends GenericUploader {
	
	private Uploader<?>[] uploaders = new Uploader<?>[] { new FTPImageUploader(), new FTPTextUploader(), new FTPFileUploader() };

	/**
	 * The settings object used for this uploader
	 */
	private FTPUploaderSettings settings;
	
	/**
	 * Construct this uploader with the loaded settings
	 * @param settings
	 * 			The settings object
	 */
	public FTPUploader(FTPUploaderSettings settings) {
		this.settings = settings;
	}

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
		if (settings.hostname == null || settings.hostname.isEmpty()
				|| settings.username == null || settings.username.isEmpty()
				|| settings.password == null // Password can be empty.
				|| settings.baseurl == null) {
			throw new UploaderConfigurationException(
					"Missing hostname, username, password or baseurl!");
		}
		SimpleFTP ftp = new SimpleFTP();
		
		try {
			ftp.connect(settings.hostname, settings.port, settings.username, settings.password.toString());
		} catch (IOException e) {
			e.printStackTrace();
			throw new UploaderConfigurationException("Unable to connect to FTP server, please check your username and password.");
		}
		
		if (settings.remotedir != null && !settings.remotedir.isEmpty()) {
			if(!ftp.cwd(settings.remotedir)) {
				throw new UploaderConfigurationException("Unable to change FTP directory.");
			}
		}
		
		try {
			ftp.stor(input, fileName);
		} finally {
			ftp.disconnect();
			input.close();
		}
		String baseUrl = settings.baseurl != null ? settings.baseurl : "%s";
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

	public static class FTPUploaderSettings {
		
		@Setting(name = "Host", description = "FTP Server Host")
		public String hostname;

		@Setting(name = "Username", description = "FTP Server Username")
		public String username;

		@Setting(name = "Password", description = "FTP Server Password")
		public Password password;
		
		@Setting(name = "Base URL", description = "Webserver base url to use")
		public String baseurl;
		
		@Setting(name = "Port", description = "FTP Server Port", optional = true, defaults = { "min=1", "max=65535", "default=21" })
		public int port = 21;
		
		@Setting(name = "Remote Directory", description = "Remote Directory to upload to", optional = true)
		public String remotedir;
	}
}

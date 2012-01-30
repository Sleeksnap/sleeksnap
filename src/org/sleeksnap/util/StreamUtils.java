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
package org.sleeksnap.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Basic file/stream utilities
 * 
 * @author Nikki
 *
 */
public class StreamUtils {

	/**
	 * Copy a file from an InputStream to a temporary file
	 * @param in
	 * 			The input stream
	 * @param fileNamePrefix
	 * 			The name prefix
	 * @param extension
	 * 			The extension
	 * @return
	 * 			The newly created file object
	 * @throws IOException
	 * 			If an error occurred when writing
	 * @throws FileNotFoundException
	 * 			If we could not find the new file
	 */
	public static File getStreamAsTempFile(InputStream in,
			final String fileNamePrefix, final String extension)
			throws IOException, FileNotFoundException {
		File libraryFile = File.createTempFile(fileNamePrefix, extension);
		libraryFile.deleteOnExit();
		writeStreamToFile(in, libraryFile);

		return libraryFile;
	}

	/**
	 * Get a stream as a temporary file...
	 * @param in
	 * 			The input stream
	 * @param fileNamePrefix
	 * 			The file name
	 * @return
	 * 			???
	 */
	public static File getStreamAsTempFileOrCry(InputStream in,
			String fileNamePrefix) {
		try {
			return getStreamAsTempFile(in, fileNamePrefix, null);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static String readContents(InputStream inputStream) throws IOException {
		StringBuilder contents = new StringBuilder();
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		try {
			String line;
			while((line = reader.readLine()) != null) {
				contents.append(line).append("\n");
			}
		} finally {
			reader.close();
		}
		return contents.toString();
	}
	
	/**
	 * Write a stream to a file
	 * @param inputStream
	 * 				The stream
	 * @param targetFile
	 * 				The file
	 * @throws FileNotFoundException
	 * 				If the file was not found
	 * @throws IOException
	 * 				If an error occurred writing
	 */
	public static void writeStreamToFile(InputStream inputStream, File targetFile)
			throws FileNotFoundException, IOException {
		FileOutputStream out = new FileOutputStream(targetFile);
		int count;
		byte[] buffer = new byte[1024];
		while (0 < (count = inputStream.read(buffer))) {
			out.write(buffer, 0, count);
		}
	}
}
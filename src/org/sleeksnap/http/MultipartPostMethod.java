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
package org.sleeksnap.http;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

/**
 * Used to execute a Multipart POST request to a URL
 * 
 * @author Nikki
 * 
 */
public class MultipartPostMethod {

	/**
	 * The URL of this method
	 */
	private URL url;

	/**
	 * The connection, valid only after execute()
	 */
	private HttpURLConnection connection;

	/**
	 * The Parameters, can use File or any other type which supports toString()
	 */
	private Map<String, Object> parameters = new HashMap<String, Object>();

	/**
	 * Construct a new instance
	 * 
	 * @param url
	 *            The URL
	 */
	public MultipartPostMethod(String url) throws IOException {
		this.url = new URL(url);
	}

	/**
	 * Construct a new instance
	 * 
	 * @param url
	 *            The URL
	 */
	public MultipartPostMethod(URL url) {
		this.url = url;
	}

	/**
	 * Set a parameter
	 * 
	 * @param key
	 *            The key
	 * @param value
	 *            The value
	 */
	public void setParameter(String key, Object value) {
		parameters.put(key, value);
	}

	/**
	 * Execute the request, I will be explaining this
	 * 
	 * @throws IOException
	 *             If a problem occurs when writing/opening the connection
	 */
	public void execute() throws IOException {
		connection = (HttpURLConnection) url.openConnection();
		// The separator string
		String boundary = "---------------------------" + randomString()
				+ randomString() + randomString();
		// Set our content type to multipart and define our separator boundary
		connection.setRequestProperty("Content-Type",
				"multipart/form-data; boundary=" + boundary);
		boundary = "--" + boundary;
		// Let the connection output data
		connection.setDoOutput(true);
		// Get the output stream and open a writer to it
		OutputStream os = connection.getOutputStream();
		OutputStreamWriter writer = new OutputStreamWriter(os);
		// Loop through the parameters
		for (Entry<String, Object> entry : parameters.entrySet()) {
			// Write the boundary (separator)
			writer.write(boundary + "\r\n");
			// Write the content name
			writer.write("Content-Disposition: form-data; name=\""
					+ entry.getKey() + "\"");
			// Get the content value
			Object object = entry.getValue();
			if (object instanceof MultipartFile || object instanceof File) { // If it is an upload (We
												// simulate this if we have an
												// input/file name), use this to
												// write it
				MultipartFile file = object instanceof File ? MultipartFile.create((File) object) : (MultipartFile) object;
				// Write the file name with the content name
				writer.write("; filename=\"" + file.getName() + "\"");
				writer.write("\r\n");
				// Get the mime type
				String type = URLConnection.guessContentTypeFromName(file
						.getName());
				if (type == null) {
					type = "application/octet-stream";
				}
				// Write the mime type
				writer.write("Content-Type: " + type);
				writer.write("\r\n\r\n");
				// Flush data because we will be writing to it from a different
				// stream
				writer.flush();
				// Open the stream and copy the data into the output
				InputStream input = file.getStream();
				try {
					byte[] buffer = new byte[1024];
					while (true) {
						int read = input.read(buffer, 0, buffer.length);
						if (read == -1) {
							break;
						}
						os.write(buffer, 0, read);
					}
				} finally {
					os.flush();
					input.close();
				}
			} else {
				// Write a newline before the content
				writer.write("\r\n\r\n");
				// Write the data
				writer.write(entry.getValue().toString());
			}
			// Write a final newline
			writer.write("\r\n");
		}

		// Set the final boundary
		boundary = boundary + "--";
		// Write a boundary to let the server know the previous content area is
		// finished
		writer.write(boundary);
		// Write a final newline
		writer.write("\r\n");

		// Flush and close the output
		writer.flush();
		writer.close();
	}

	/**
	 * Read the response
	 * 
	 * @return The response
	 * @throws IOException
	 *             If an error occurs while opening the page
	 */
	public String getResponse() throws IOException {
		StringBuilder contents = new StringBuilder();
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				connection.getInputStream()));
		try {
			String line;
			while ((line = reader.readLine()) != null) {
				contents.append(line).append("\n");
			}
		} finally {
			reader.close();
		}
		return contents.toString();
	}

	/**
	 * Get the input stream
	 * 
	 * @return The input stream
	 * @throws IOException
	 *             If an error occurs while opening
	 */
	public InputStream getInputStream() throws IOException {
		return connection.getInputStream();
	}

	/**
	 * Close this connection
	 */
	public void close() {
		connection.disconnect();
	}

	@Override
	public void finalize() {
		close();
	}

	/**
	 * The random number generator
	 */
	private static Random random = new Random();

	/**
	 * Generate a random string
	 * 
	 * @return	A (semi-random) string built from a random long... this can be redone a lot better, but it's simple enough and works for now.
	 */
	protected static String randomString() {
		return Long.toString(random.nextLong(), 36);
	}

	/**
	 * A simple wrapper to simulate a File, except we can push a name and the
	 * data from memory
	 * 
	 * @author Nikki
	 * 
	 */
	public static class MultipartFile {
		/**
		 * The upload filename
		 */
		private String name;

		/**
		 * The upload data
		 */
		private InputStream stream;

		/**
		 * Construct a new "File Upload" instance
		 * 
		 * @param name
		 *            The name
		 * @param stream
		 *            The input stream which contains the data
		 */
		public MultipartFile(String name, InputStream stream) {
			this.name = name;
			this.stream = stream;
		}

		/**
		 * Get the file name
		 * 
		 * @return The name
		 */
		public String getName() {
			return name;
		}

		/**
		 * Get the file data
		 * 
		 * @return An input stream which contains the data
		 */
		public InputStream getStream() {
			return stream;
		}

		/**
		 * Create a FileUpload from an existing file
		 * 
		 * @param file
		 *            The file
		 * @return The wrapper with file name/input stream
		 * @throws IOException
		 *             If a problem occured while opening the file
		 */
		public static MultipartFile create(File file) throws IOException {
			return new MultipartFile(file.getName(), new FileInputStream(file));
		}
	}
}
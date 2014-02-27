package org.sleeksnap.impl;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.sleeksnap.util.ClassTypeAdapter;
import org.sleeksnap.util.Util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

/**
 * API Credential Storage class
 * 
 * @author Nikki
 *
 */
public class APICredentials {
	
	/**
	 * The logger is only used once if it cannot load.
	 */
	private static final Logger logger = Logger.getLogger(APICredentials.class.getName());
	
	/**
	 * Our Gson instance
	 */
	private static final Gson gson = new GsonBuilder().registerTypeAdapter(Class.class, new ClassTypeAdapter()).create();
	
	/**
	 * The credential map
	 */
	private static Map<Class<?>, Map<String, String>> credentials;
	
	/**
	 * Load the credential file
	 * @throws IOException
	 * 			If an error occurred finding the file
	 */
	public static void load() throws IOException {
		URL url = Util.getResourceByName("/apicredentials.json");
		if (url == null) {
			throw new IOException("Unable to find credentials file!");
		}
		Reader reader = new InputStreamReader(url.openStream());
		try {
			credentials = gson.fromJson(reader, new TypeToken<Map<Class<?>, Map<String, String>>>() {}.getType());
		} finally {
			reader.close();
		}
	}
	
	/**
	 * Get a credential
	 * @param uploader
	 * 			The uploader class
	 * @param credential
	 * 			The credential name
	 * @return
	 * 			The value
	 */
	public static String getCredential(Class<?> uploader, String credential) {
		if (credentials == null) {
			try {
				load();
			} catch (IOException e) {
				logger.log(Level.SEVERE, "Failed to load api credentials, some features may not work.", e);
			}
		}
		Map<String, String> sub = credentials.get(uploader);
		if (sub == null) {
			return null;
		}
		return sub.get(credential);
	}
}

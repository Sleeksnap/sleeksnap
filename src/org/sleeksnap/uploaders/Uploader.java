package org.sleeksnap.uploaders;

import java.util.Properties;

/**
 * A basic uploader
 * 
 * @author Nikki
 *
 * @param <T>
 * 			The upload type
 */
@Settings(required={},optional={})
public abstract class Uploader<T> {
	
	/**
	 * The properties instance
	 */
	protected Properties properties = new Properties();

	/**
	 * Get the uploader name
	 * @return
	 * 		The uploader name
	 */
	public abstract String getName();

	/**
	 * Get the object type this class can upload
	 * @return
	 */
	public abstract Class<?> getUploadType();

	/**
	 * Upload the specified object
	 * @param t
	 * 			The object
	 * @return
	 * 			The URL or Location
	 * @throws Exception 
	 */
	public abstract String upload(T t) throws Exception;
	
	/**
	 * Set this uploader's settings
	 * @param settings
	 * 			The Properties object containing the settings
	 */
	public void setProperties(Properties settings) {
		this.properties = settings;
	}

	/**
	 * Get this uploader's properties
	 * @return
	 * 		The properties
	 */
	public Properties getProperties() {
		return properties;
	}
}
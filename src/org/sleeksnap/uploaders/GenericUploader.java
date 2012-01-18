package org.sleeksnap.uploaders;


/**
 * A basic uploader
 * 
 * @author Nikki
 *
 * @param <T>
 * 			The upload type
 */
public abstract class GenericUploader extends Uploader<Object> {
	
	/**
	 * Get the uploaders that this uploader can use
	 * @return
	 * 		The uploaders
	 */
	public abstract Uploader<?>[] getSubUploaders();

	
	@Override
	public Class<?> getUploadType() {
		return Object.class;
	}

	@Override
	public String upload(Object t) throws Exception {
		return null;
	}
}
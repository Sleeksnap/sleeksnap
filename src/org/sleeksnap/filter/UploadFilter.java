package org.sleeksnap.filter;

/**
 * An interface representing an upload filter, used to modify images, text or files before uploading
 * @author Nikki
 *
 * @param <T>
 */
public interface UploadFilter<T> {

	/**
	 * Apply this filter to the object
	 * @param object
	 * 			The object to apply to
	 * @return
	 * 			The modified object, or original object.
	 */
	public T filter(T object);
	
}

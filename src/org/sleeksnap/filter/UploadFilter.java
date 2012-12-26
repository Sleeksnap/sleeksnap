package org.sleeksnap.filter;

public interface UploadFilter<T> {

	public T filter(T object);
	
	public Class<?> getType();
	
}

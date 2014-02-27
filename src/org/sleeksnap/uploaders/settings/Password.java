package org.sleeksnap.uploaders.settings;

public class Password {
	private String value;
	
	public Password(String value) {
		this.value = value;
	}
	
	public boolean isEmpty() {
		return value.isEmpty();
	}
	
	@Override
	public String toString() {
		return value;
	}
}

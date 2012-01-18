package org.sleeksnap.uploaders;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Settings {

	String[] required();
	
	String[] optional();
	
}

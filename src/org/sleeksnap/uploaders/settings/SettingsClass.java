package org.sleeksnap.uploaders.settings;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Defines the class to use for settings. Gson will deserialized to the specified class, then the object will be passed by constructor.
 * 
 * @author Nikki
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface SettingsClass {
	public Class<?> value();
}

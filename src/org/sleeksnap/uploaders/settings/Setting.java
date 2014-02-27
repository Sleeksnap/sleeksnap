package org.sleeksnap.uploaders.settings;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.sleeksnap.uploaders.settings.types.AutoDetectSettingType;

/**
 * Defines a Setting, this is only used in the {@link ParametersDialog}
 * 
 * @author Nikki
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Setting {
	public String name();
	
	public String description() default "";
	
	public boolean optional() default false;
	
	public Class<? extends UploaderSettingType> type() default AutoDetectSettingType.class;
	
	public String[] defaults() default { };
}

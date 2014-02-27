package org.sleeksnap.uploaders.settings.types;

import javax.swing.JComponent;

import org.sleeksnap.uploaders.settings.UploaderSettingType;

/**
 * A placeholder for auto detected setting types
 * 
 * @author Nikki
 *
 */
public class AutoDetectSettingType implements UploaderSettingType {

	@Override
	public JComponent constructComponent(String[] defaults) {
		return null;
	}

	@Override
	public void setValue(JComponent component, Object value) {
	}

	@Override
	public Object getValue(JComponent component) {
		return null;
	}

}

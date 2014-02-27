/**
 * Sleeksnap, the open source cross-platform screenshot uploader
 * Copyright (C) 2012 Nikki <nikki@nikkii.us>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sleeksnap.uploaders.settings.types;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;

import org.sleeksnap.uploaders.settings.UploaderSettingType;

/**
 * A basic setting type for Enums which are populated into a JComboBox
 * 
 * @author Nikki
 *
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class EnumComboBoxSettingType implements UploaderSettingType {
	
	private Class<? extends Enum> enumType;

	public EnumComboBoxSettingType(Class<? extends Enum> enumType) {
		this.enumType = enumType;
	}

	@Override
	public JComponent constructComponent(String[] defaults) {
		JComboBox box = new JComboBox();
		if(defaults.length > 0) {
			box.setModel(new DefaultComboBoxModel(defaults));
		}
		return box;
	}

	@Override
	public void setValue(JComponent component, Object value) {
		((JComboBox) component).setSelectedItem(value);
	}

	@Override
	public Object getValue(JComponent component) {
		String selected = (String) ((JComboBox) component).getSelectedItem();
		for (Enum o : enumType.getEnumConstants()) {
			if (o.name().equals(selected)) {
				return o;
			}
		}
		return null;
	}

}

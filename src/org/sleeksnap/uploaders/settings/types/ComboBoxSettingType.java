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
 * A basic setting type for JComboBoxes
 * 
 * @author Nikki
 *
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class ComboBoxSettingType implements UploaderSettingType {

	@Override
	public JComponent constructComponent(String defaultValue) {
		JComboBox box = new JComboBox();
		if(!defaultValue.equals("")) {
			String[] split = defaultValue.split(",");
			for(int i = 0; i < split.length; i++) {
				split[i] = split[i].trim();
			}
			box.setModel(new DefaultComboBoxModel(split));
		}
		return box;
	}

	@Override
	public void setValue(JComponent component, Object value) {
		((JComboBox) component).setSelectedItem(value);
	}

	@Override
	public Object getValue(JComponent component) {
		return ((JComboBox) component).getSelectedItem();
	}

}

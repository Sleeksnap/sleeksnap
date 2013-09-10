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
package org.sleeksnap.uploaders.settings;

import javax.swing.JComponent;

/**
 * A wrapper to contain the component and setting type of an uploader setting
 * 
 * @author Nikki
 *
 */
public class UploaderSetting {
	/**
	 * The component attached to this setting
	 */
	private JComponent component;
	
	/**
	 * The setting type to use when getting the value
	 */
	private UploaderSettingType type;
	
	public UploaderSetting(JComponent component, UploaderSettingType type) {
		this.component = component;
		this.type = type;
	}

	/**
	 * Get this setting's component
	 * @return
	 * 		The component
	 */
	public JComponent getComponent() {
		return component;
	}

	/**
	 * Get this setting's type
	 * @return
	 * 		The setting type
	 */
	public UploaderSettingType getType() {
		return type;
	}
}
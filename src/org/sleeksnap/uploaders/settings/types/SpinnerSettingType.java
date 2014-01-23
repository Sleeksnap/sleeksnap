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

import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import org.sleeksnap.uploaders.settings.UploaderSettingType;
import org.sleeksnap.util.Util;

/**
 * A simple wrapper for a setting type which allows only numeric values
 * 
 * Data names: default, min, max, step
 * 
 * @author Nikki
 *
 */
public class SpinnerSettingType implements UploaderSettingType {

	@Override
	public JComponent constructComponent(String data) {
		SpinnerNumberModel model = new SpinnerNumberModel();
		Map<String, String> m = Util.parseDataList(data);
		if(m.containsKey("default")) {
			model.setValue(Integer.parseInt(m.get("default")));
		}
		if(m.containsKey("min")) {
			model.setMinimum(Integer.parseInt(m.get("min")));
		}
		if(m.containsKey("max")) {
			model.setMaximum(Integer.parseInt(m.get("max")));
		}
		if(m.containsKey("step")) {
			model.setStepSize(Integer.parseInt(m.get("step")));
		}
		return new JSpinner(model);
	}

	@Override
	public void setValue(JComponent component, Object value) {
		if(!value.toString().isEmpty()) {
			((JSpinner) component).setValue(Integer.parseInt(value.toString()));
		}
	}

	@Override
	public Object getValue(JComponent component) {
		return ((JSpinner) component).getValue();
	}

}

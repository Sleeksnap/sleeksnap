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

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.LayoutStyle;

import org.sleeksnap.uploaders.Uploader;
import org.sleeksnap.uploaders.settings.types.AutoDetectSettingType;
import org.sleeksnap.uploaders.settings.types.CheckBoxSettingType;
import org.sleeksnap.uploaders.settings.types.EnumComboBoxSettingType;
import org.sleeksnap.uploaders.settings.types.NumberSpinnerSettingType;
import org.sleeksnap.uploaders.settings.types.TextSettingType;

/**
 * The dialog which shows the simulation's parameters before it is ran.
 * 
 * @author Graham Edgecombe
 * @author Nikki
 */
public class ParametersDialog extends JDialog {

	/**
	 * The serial version unique id for this class.
	 */
	private static final long serialVersionUID = 42399074877274640L;

	/**
	 * The labels that correspond with the parameters.
	 */
	private final JLabel[] labels;

	/**
	 * The input components that correspond with the parameters.
	 */
	private final UploaderSetting[] components;

	/**
	 * The OK button.
	 */
	private final JButton btnOk = new JButton("OK");

	/**
	 * The cancel button.
	 */
	private final JButton btnCancel = new JButton("Cancel");

	/**
	 * The settings array length
	 */
	private int length;

	/**
	 * The action listener
	 */
	private ActionListener actionListener;
	
	private Field[] requiredSettings;
	
	private Field[] optionalSettings;

	/**
	 * The map of name => field
	 */
	private Map<Field, UploaderSetting> fieldMap = new HashMap<Field, UploaderSetting>();

	/**
	 * The uploader name
	 */
	private Uploader<?> uploader;

	/**
	 * Creates a new parameters dialog with the specified parent and array of
	 * parameters.
	 * 
	 * @param parent
	 * 			The frame which opened this dialog
	 * @param uploader
	 * 			The uploader for this settings dialog
	 * @param settings
	 * 			The settings annotation
	 * @throws Exception 
	 */
	public ParametersDialog(JFrame parent, Uploader<?> uploader, Class<?> settingClass) throws Exception {
		super(parent);
		this.uploader = uploader;
		
		List<Field> required = new ArrayList<Field>();
		List<Field> optional = new ArrayList<Field>();
		
		for (Field field : settingClass.getFields()) {
			Setting setting = field.getAnnotation(Setting.class);
			
			if (setting != null) {
				if (setting.optional()) {
					optional.add(field);
				} else {
					required.add(field);
				}
			}
		}
		
		requiredSettings = required.toArray(new Field[required.size()]);
		optionalSettings = optional.toArray(new Field[optional.size()]);
		
		length = requiredSettings.length + optionalSettings.length;
		// Check the length for labels
		if (requiredSettings.length != 0) {
			length++;
		}
		if (optionalSettings.length != 0) {
			length++;
		}
		this.labels = new JLabel[length];
		this.components = createComponentArray(settingClass);

		initComponents();
	}

	/**
	 * Creates the component array from the parameters array.
	 * @param settingClass 
	 * 
	 * @return The component array.
	 * @throws Exception 
	 */
	private UploaderSetting[] createComponentArray(Class<?> settingClass) throws Exception {
		UploaderSetting[] components = new UploaderSetting[length];

		int i = 0;
		if (requiredSettings.length != 0) {
			labels[i] = new JLabel("Required settings");
			components[i] = new UploaderSetting(new JLabel(), null);
			i++;

			for (Field field : requiredSettings) {
				initializeSetting(components, i, field, field.getAnnotation(Setting.class));
				i++;
			}
		}

		if (optionalSettings.length != 0) {
			labels[i] = new JLabel("Optional settings");
			components[i] = new UploaderSetting(new JLabel(), null);
			i++;

			for (Field field : optionalSettings) {
				initializeSetting(components, i, field, field.getAnnotation(Setting.class));
				i++;
			}
		}

		return components;
	}
	
	@SuppressWarnings("serial")
	private static Map<Class<?>, UploaderSettingType> settingTypes = new HashMap<Class<?>, UploaderSettingType>() {{
		put(String.class, new TextSettingType());
		put(boolean.class, new CheckBoxSettingType());
		put(int.class, new NumberSpinnerSettingType());
	}};
	
	private static Map<Class<?>, UploaderSettingType> cachedTypes = new HashMap<Class<?>, UploaderSettingType>();
	
	/**
	 * Build the setting component/label
	 * @param components
	 * 			The component array to push the setting into
	 * @param index
	 * 			The current setting index
	 * @param name
	 * 			The setting name
	 * @param properties
	 * 			The current properties object
	 */
	@SuppressWarnings("unchecked")
	public void initializeSetting(UploaderSetting[] components, int index, Field field, Setting setting) throws Exception {
		Class<?> type = setting.type();
		
		Class<?> fieldType = field.getType();
		
		UploaderSettingType settingType = null;
		
		String[] defaults = setting.defaults();
		
		if (type == AutoDetectSettingType.class) {
			// Get the setting type class from the map
			
			if (fieldType == Enum.class || fieldType.getSuperclass() == Enum.class) {
				settingType = new EnumComboBoxSettingType((Class<? extends Enum<?>>) field.getType());
				// Set field data to enum value
				List<String> list = new ArrayList<String>();
				for(Object o : fieldType.getEnumConstants()) {
					list.add(o.toString());
				}
				defaults = list.toArray(new String[list.size()]);
			} else if (settingTypes.containsKey(fieldType)) {
				settingType = settingTypes.get(field.getType());
			} else if (fieldType.getSuperclass() != null && settingTypes.containsKey(fieldType.getSuperclass())) {
				settingType = settingTypes.get(fieldType.getSuperclass());
			} else {
				throw new Exception("Unable to auto detect setting type for " + field.getType());
			}
		} else {
			// Other type
			if (cachedTypes.containsKey(type)) {
				settingType = cachedTypes.get(type);
			} else {
				cachedTypes.put(type, settingType = (UploaderSettingType) type.newInstance());
			}
		}
		
		JComponent component = settingType.constructComponent(defaults);
		
		if (!setting.description().isEmpty()) {
			component.setToolTipText(setting.description());
		}
		
		labels[index] = new JLabel(setting.name() + ": ");
		components[index] = new UploaderSetting(component, settingType);
		
		try {
			Object value = field.get(uploader.getSettingsInstance());
			if (value != null) {
				settingType.setValue(component, field.get(uploader.getSettingsInstance()));
			}
		} catch (Exception e) {
			// Unable to get value
		}
		
		component.setMinimumSize(new Dimension(200, 0));
		
		fieldMap.put(field, components[index]);
	}

	/**
	 * Initialises the components.
	 */
	private void initComponents() {
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setTitle(uploader.getName() + " Settings");
		setResizable(false);

		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);

		GroupLayout.SequentialGroup horizontalGroup = layout
				.createSequentialGroup();
		GroupLayout.SequentialGroup verticalGroup = layout
				.createSequentialGroup().addContainerGap();

		GroupLayout.ParallelGroup horizontalLabels = layout
				.createParallelGroup(GroupLayout.Alignment.TRAILING);
		GroupLayout.ParallelGroup horizontalComponents = layout
				.createParallelGroup(GroupLayout.Alignment.LEADING, false);

		for (int i = 0; i < components.length; i++) {
			boolean last = i == (components.length - 1);
			JComponent component = components[i].getComponent();
			JLabel label = labels[i];

			horizontalLabels.addComponent(label);

			horizontalComponents.addGroup(layout.createSequentialGroup()
					.addComponent(component,
							javax.swing.GroupLayout.DEFAULT_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE,
							Short.MAX_VALUE));

			if (last) {
				verticalGroup
						.addGroup(layout
								.createParallelGroup(
										javax.swing.GroupLayout.Alignment.LEADING)
								.addGroup(
										layout.createSequentialGroup()
												.addGroup(
														layout.createParallelGroup()
																.addComponent(
																		component,
																		javax.swing.GroupLayout.PREFERRED_SIZE,
																		javax.swing.GroupLayout.DEFAULT_SIZE,
																		javax.swing.GroupLayout.PREFERRED_SIZE))
												.addPreferredGap(
														LayoutStyle.ComponentPlacement.RELATED)
												.addGroup(
														layout.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
																.addComponent(
																		btnCancel)
																.addComponent(
																		btnOk)))
								.addComponent(label));
				verticalGroup.addContainerGap();
			} else {
				verticalGroup.addGroup(layout
						.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(component).addComponent(label));
				verticalGroup
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED);
			}
		}

		horizontalGroup
				.addGroup(horizontalLabels)
				.addPreferredGap(
						javax.swing.LayoutStyle.ComponentPlacement.RELATED)
				.addGroup(horizontalComponents);

		layout.setHorizontalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						layout.createSequentialGroup()
								.addContainerGap()
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.LEADING)
												.addGroup(
														javax.swing.GroupLayout.Alignment.TRAILING,
														layout.createSequentialGroup()
																.addComponent(
																		btnCancel,
																		javax.swing.GroupLayout.PREFERRED_SIZE,
																		80,
																		javax.swing.GroupLayout.PREFERRED_SIZE)
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addComponent(
																		btnOk,
																		javax.swing.GroupLayout.PREFERRED_SIZE,
																		80,
																		javax.swing.GroupLayout.PREFERRED_SIZE))
												.addGroup(horizontalGroup))
								.addContainerGap(
										javax.swing.GroupLayout.DEFAULT_SIZE,
										Short.MAX_VALUE)));

		layout.setVerticalGroup(layout.createParallelGroup(
				GroupLayout.Alignment.LEADING).addGroup(verticalGroup));

		btnOk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				btnOkActionPerformed(e);
			}
		});

		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				btnCancelActionPerformed(e);
			}
		});

		pack();
	}

	/**
	 * A listener for the OK button.
	 * 
	 * @param evt
	 *            The event.
	 */
	private void btnOkActionPerformed(ActionEvent evt) {
		if (!validateProperties()) {
			JOptionPane.showMessageDialog(this,
					"Some required settings are empty, please fill them out",
					"Fields missing", JOptionPane.ERROR_MESSAGE);
			return;
		}
		// everything is valid, let the caller know, then let them close this.
		actionListener.actionPerformed(evt);
	}
	
	/**
	 * Close this window
	 */
	public void closeWindow() {
		dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
	}

	/**
	 * A listener for the cancel button.
	 * 
	 * @param evt
	 *            The event.
	 */
	private void btnCancelActionPerformed(ActionEvent evt) {
		dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
	}

	/**
	 * Validate the settings (verify they aren't empty, more validation later)
	 * @return
	 * 		true if all settings are good
	 */
	public boolean validateProperties() {
		for (Field field : requiredSettings) {
			Object object = getComponentValue(fieldMap.get(field));
			
			if (object == null || object.toString().isEmpty()) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Export this dialog's settings to the settings class
	 * @param uploaderSettings 
	 * @return
	 * 		The new UploaderSettings object
	 */
	public void exportToSettingInstance() throws Exception {
		for(Entry<Field, UploaderSetting> entry : fieldMap.entrySet()) {
			Field field = entry.getKey();
			UploaderSetting component = entry.getValue();
			
			Object value = getComponentValue(component);
			
			field.set(uploader.getSettingsInstance(), value);
		}
	}
	
	/**
	 * Get the component's value based on type
	 * @param component
	 * 			The component to get the value from
	 * @return
	 * 			The component value
	 */
	public Object getComponentValue(UploaderSetting component) {
		if(component == null || component.getType() == null) {
			return null;
		}
		return component.getType().getValue(component.getComponent());
	}

	/**
	 * Set the "OK" action
	 * @param actionListener
	 * 			The aciton listener
	 */
	public void setOkAction(ActionListener actionListener) {
		this.actionListener = actionListener;
	}
	
}
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
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;

import org.sleeksnap.uploaders.Settings;
import org.sleeksnap.uploaders.Uploader;
import org.sleeksnap.uploaders.settings.types.CheckBoxSettingType;
import org.sleeksnap.uploaders.settings.types.ComboBoxSettingType;
import org.sleeksnap.uploaders.settings.types.PasswordSettingType;
import org.sleeksnap.uploaders.settings.types.SpinnerSettingType;
import org.sleeksnap.uploaders.settings.types.TextSettingType;
import org.sleeksnap.util.Util;
import org.sleeksnap.util.Utils.ClassUtil;

/**
 * The dialog which shows the simulation's parameters before it is ran.
 * 
 * @author Graham Edgecombe
 * @author Nikki
 */
@Settings(required = { "Username", "Password" }, optional = { "Optional 1",
		"Optional 2" })
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
	 * The settings instance
	 */
	private Settings settings;

	/**
	 * The settings array length
	 */
	private int length;

	/**
	 * The action listener
	 */
	private ActionListener actionListener;

	/**
	 * The map of name => field
	 */
	private Map<String, UploaderSetting> fieldMap = new HashMap<String, UploaderSetting>();

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
	 */
	public ParametersDialog(JFrame parent, Uploader<?> uploader,
			Settings settings) {
		super(parent);
		this.uploader = uploader;
		this.settings = settings;
		int requiredLength = settings.required().length;
		int optionalLength = settings.optional().length;
		length = requiredLength + optionalLength;
		// Check the length for labels
		if (requiredLength != 0) {
			length++;
		}
		if (optionalLength != 0) {
			length++;
		}
		this.labels = new JLabel[length];
		this.components = createComponentArray();

		initComponents();
	}

	/**
	 * Creates the component array from the parameters array.
	 * 
	 * @return The component array.
	 */
	private UploaderSetting[] createComponentArray() {
		UploaderSetting[] components = new UploaderSetting[length];

		UploaderSettings properties = uploader.getSettings();

		int i = 0;
		if (settings.required().length != 0) {
			labels[i] = new JLabel("Required settings");
			components[i] = new UploaderSetting(new JLabel(), null);
			i++;

			for (String s : settings.required()) {
				initializeSetting(components, i, s, properties);
				i++;
			}
		}

		if (settings.optional().length != 0) {
			labels[i] = new JLabel("Optional settings");
			components[i] = new UploaderSetting(new JLabel(), null);
			i++;

			for (String s : settings.optional()) {
				initializeSetting(components, i, s, properties);
				i++;
			}
		}

		return components;
	}
	
	@SuppressWarnings("serial")
	private static Map<String, UploaderSettingType> settingTypes = new HashMap<String, UploaderSettingType>() {{
		put("text", new TextSettingType());
		put("password", new PasswordSettingType());
		put("combobox", new ComboBoxSettingType());
		put("checkbox", new CheckBoxSettingType());
		put("spinner", new SpinnerSettingType());
	}};
	
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
	public void initializeSetting(UploaderSetting[] components, int index, String name, UploaderSettings properties) {
		JComponent component = null;
		UploaderSettingType settingType = settingTypes.get("text");
		if(name.contains("|")) {
			//Could be a different type.
			String type = name.substring(name.indexOf('|')+1);
			String data = "";
			
			name = name.substring(0, name.indexOf('|'));

			//Data for Combo box or default setting
			if(type.indexOf('[') != -1 && type.indexOf(']') != -1) {
				int firstIdx = type.indexOf('[');
				data = type.substring(firstIdx+1, type.indexOf(']', firstIdx));
				type = type.substring(0, firstIdx);
			}
			
			//Parse out the setting type
			UploaderSettingType newType = settingTypes.get(type);
			if(newType != null) {
				settingType = newType;
				
				component = newType.constructComponent(data);
			} else {
				//Unknown type, just create a text component
				component = settingType.constructComponent(data);
			}
		} else {
			//Or revert to a simple text field
			component = new JTextField();
		}
		
		labels[index] = new JLabel(settingName(name) + ": ");
		components[index] = new UploaderSetting(component, settingType);
		
		if (properties.has(name)) {
			settingType.setValue(component, properties.get(name));
		}
		
		component.setMinimumSize(new Dimension(200, 0));
		
		fieldMap.put(name, components[index]);
	}

	/**
	 * Initialises the components.
	 */
	private void initComponents() {
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setTitle(ClassUtil.formatName(uploader.getClass()) + " Settings");
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
		for (String s : settings.required()) {
			if(s.indexOf('|') != -1) {
				s = s.substring(0, s.indexOf('|'));
			}
			Object object = getComponentValue(fieldMap.get(s));
			if (object == null || object.toString().equals("")) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Export this dialog's settings to an UploaderSettings object
	 * @param uploaderSettings 
	 * @return
	 * 		The new UploaderSettings object
	 */
	public UploaderSettings toSettings(UploaderSettings uploaderSettings) {
		UploaderSettings settings = new UploaderSettings();
		for(Entry<String, UploaderSetting> entry : fieldMap.entrySet()) {
			String s = entry.getKey();
			if(s.indexOf('|') != -1) {
				s = s.substring(0, s.indexOf('|'));
			}
			UploaderSetting component = entry.getValue();
			Object value = getComponentValue(component);
			if(component.getType() instanceof PasswordSettingType) {
				settings.setPassword(s, value);
			} else {
				settings.set(s, value);
			}
		}
		// Copy the original non-existant settings in
		return settings;
	}
	
	/**
	 * Export this dialog's settings into an existing UploaderSettings object
	 * This will eventually be a replacement for toSettings
	 * 
	 * @param settings
	 * 			The object to export into
	 */
	public void exportTo(UploaderSettings settings) {
		for(Entry<String, UploaderSetting> entry : fieldMap.entrySet()) {
			UploaderSetting component = entry.getValue();
			Object value = getComponentValue(component);
			if(component.getType() instanceof PasswordSettingType) {
				settings.setPassword(entry.getKey(), value);
			} else {
				settings.set(entry.getKey(), value);
			}
		}
	}
	
	/**
	 * Format the setting name for display
	 * @param key
	 * 			The setting key
	 * @return
	 * 			The formatted setting name (Words will be capitalized and _ replaced with space)
	 */
	public String settingName(String key) {
		return Util.ucwords(key.replace('_', ' '));
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
	
	public enum SettingType {
		TEXT(JTextField.class), PASSWORD(JPasswordField.class), CHECKBOX(JCheckBox.class), COMBOBOX(JComboBox.class);
		
		private Class<?> cl;
		
		private SettingType(Class<?> cl) {
			this.cl = cl;
		}
		
		public JComponent createComponent() {
			try {
				return (JComponent) cl.newInstance();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			}
			return null;
		}
	}

	public static void registerSettingType(String string, UploaderSettingType uploaderSettingType) {
		settingTypes.put(string, uploaderSettingType);
	}
}
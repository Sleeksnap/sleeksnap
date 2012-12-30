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
package org.sleeksnap.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.swing.DefaultComboBoxModel;
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
	private final JComponent[] components;

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
	private Map<String, JComponent> fieldMap = new HashMap<String, JComponent>();

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
	private JComponent[] createComponentArray() {
		JComponent[] components = new JComponent[length];

		Properties properties = uploader.getSettings();

		int i = 0;
		if (settings.required().length != 0) {
			labels[i] = new JLabel("Required settings");
			components[i] = new JLabel();
			i++;

			for (String s : settings.required()) {
				initializeSetting(components, i, s, properties);
				i++;
			}
		}

		if (settings.optional().length != 0) {
			labels[i] = new JLabel("Optional settings");
			components[i] = new JLabel();
			i++;

			for (String s : settings.optional()) {
				initializeSetting(components, i, s, properties);
				i++;
			}
		}

		return components;
	}
	
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
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void initializeSetting(JComponent[] components, int index, String name, Properties properties) {
		JComponent component = null;
		SettingType settingType = SettingType.TEXT;
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
			SettingType newType = SettingType.valueOf(type.toUpperCase());
			if(newType != null) {
				settingType = newType;
				component = newType.createComponent();
				
				//Populate the default settings or combo items
				if(!data.equals("")) {
					switch(settingType) {
					case TEXT:
						((JTextField) component).setText(data);
						break;
					case PASSWORD:
						((JPasswordField) component).setText(data);
						break;
					case CHECKBOX:
						((JCheckBox) component).setSelected(Boolean.valueOf(data));
						break;
					case COMBOBOX:
						String[] split = data.split(",");
						for(int i = 0; i < split.length; i++) {
							split[i] = split[i].trim();
						}
						((JComboBox) component).setModel(new DefaultComboBoxModel(split));
						break;
					}
				}
			} else {
				//Unknown type, just create a text component
				component = settingType.createComponent();
			}
		} else {
			//Or revert to a simple text field
			component = new JTextField();
		}
		
		labels[index] = new JLabel(settingName(name) + ": ");
		components[index] = component;
		
		if (properties.containsKey(name)) {
			switch(settingType) {
			case TEXT:
				((JTextField) component).setText(properties.getProperty(name));
				break;
			case PASSWORD:
				((JPasswordField) component).setText(properties.getProperty(name));
				break;
			case CHECKBOX:
				((JCheckBox) component).setSelected(Boolean.valueOf(properties.getProperty(name)));
				break;
			case COMBOBOX:
				((JComboBox) component).setSelectedItem(properties.getProperty(name));
				break;
			}
		}
		
		component.setMinimumSize(new Dimension(200, 0));
		
		fieldMap.put(name, component);
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
			JComponent component = components[i];
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
			Object object = getComponentValue(fieldMap.get(s));
			if (object == null || object.toString().equals("")) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Convert the fields into a properties object
	 * @return
	 * 		The properties object
	 */
	public Properties toProperties() {
		Properties props = new Properties();
		for (Entry<String, JComponent> entry : fieldMap.entrySet()) {
			props.put(entry.getKey(), getComponentValue(entry.getValue()).toString());
		}
		return props;
	}
	
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
	@SuppressWarnings("rawtypes")
	public Object getComponentValue(JComponent component) {
		if(component instanceof JTextField) {
			return ((JTextField) component).getText();
		} else if(component instanceof JPasswordField) {
			return new String(((JPasswordField) component).getPassword());
		} else if(component instanceof JCheckBox) {
			return ((JCheckBox) component).isSelected() ? true : false;
		} else if(component instanceof JComboBox) {
			return ((JComboBox) component).getSelectedItem();
		}
		return null;
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
				return (JComponent) this.cl.newInstance();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			}
			return null;
		}
	}
}
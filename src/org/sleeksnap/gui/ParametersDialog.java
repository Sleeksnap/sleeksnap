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

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;

import org.sleeksnap.uploaders.Settings;
import org.sleeksnap.uploaders.Uploader;
import org.sleeksnap.util.Utils.ClassUtil;

/**
 * The dialog which shows the simulation's parameters before it is ran.
 * 
 * @author Graham Edgecombe
 * @author Nikki
 */
@Settings(required = {"Username", "Password"}, optional = {"Optional 1", "Optional 2"})
public class ParametersDialog extends JFrame {

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
	private Map<String, JTextField> fieldMap = new HashMap<String, JTextField>();

	/**
	 * The uploader name
	 */
	private Uploader<?> uploader;

	/**
	 * Creates a new parameters dialog with the specified parent and array of
	 * parameters.
	 * 
	 * @param name
	 *            The name of the uploader
	 * @param 
	 *            The simulation.
	 */
	public ParametersDialog(Uploader<?> uploader, Settings settings) {
		this.uploader = uploader;
		this.settings = settings;
		int requiredLength = settings.required().length;
		int optionalLength = settings.optional().length;
		length = requiredLength + optionalLength;
		//Check the length for labels
		if(requiredLength != 0) {
			length++;
		}
		if(optionalLength != 0) {
			length++;
		}
		this.labels = new JLabel[length]; // TODO refactor
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
		if(settings.required().length != 0) {
			labels[i] = new JLabel("Required settings");
			components[i] = new JLabel();
			i++;
			
			for (String s : settings.required()) {
				labels[i] = new JLabel(s + ": ");
	
				JTextField component = new JTextField();
				components[i] = component;
				if(properties.containsKey(s)) {
					component.setText(properties.getProperty(s));
				}
				component.setMinimumSize(new Dimension(200, 0)); // TODO better way?
				
				fieldMap.put(s, component);
				
				i++;
			}
		}
		
		if(settings.optional().length != 0) {
			labels[i] = new JLabel("Optional settings");
			components[i] = new JLabel();
			i++;
			
			for(String s : settings.optional()) {
				labels[i] = new JLabel(s + ": ");
	
				JTextField component = new JTextField();
				components[i] = component;
				if(properties.containsKey(s)) {
					component.setText(properties.getProperty(s));
				}
				component.setMinimumSize(new Dimension(200, 0)); // TODO better way?
				
				fieldMap.put(s, component);
				
				i++;
			}
		}

		return components;
	}
	
	/**
	 * Initialises the components.
	 */
	private void initComponents() {
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setTitle(ClassUtil.formatName(uploader.getClass())+" Settings");
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
		if(!validateProperties()) {
			JOptionPane.showMessageDialog(this, "Some required settings are empty, please fill them out", "Fields missing", JOptionPane.ERROR_MESSAGE);
			return;
		}
		// everything is valid, let the caller know, then go ahead and close the window
		actionListener.actionPerformed(evt);
		
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
	
	public boolean validateProperties() {
		for(String s : settings.required()) {
			if(fieldMap.get(s).getText().equals("")) {
				return false;
			}
		}
		return true;
	}
	
	public Properties toProperties() {
		Properties props = new Properties();
		for(Entry<String, JTextField> entry : fieldMap.entrySet()) {
			props.put(entry.getKey(), entry.getValue().getText());
		}
		return props;
	}

	public void setOkAction(ActionListener actionListener) {
		this.actionListener = actionListener;
	}
}
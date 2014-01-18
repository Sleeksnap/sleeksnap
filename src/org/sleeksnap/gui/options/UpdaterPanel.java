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
package org.sleeksnap.gui.options;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import org.sleeksnap.gui.OptionPanel;
import org.sleeksnap.updater.Updater;
import org.sleeksnap.updater.UpdaterMode;
import org.sleeksnap.updater.UpdaterReleaseType;

/**
 * An OptionSubPanel for the Updater settings
 * 
 * @author Nikki
 * 
 */
@SuppressWarnings({ "unchecked", "rawtypes", "serial" })
public class UpdaterPanel extends OptionSubPanel {

	private JLabel updaterSettingLabel;
	private JLabel updaterModeLabel;
	private JLabel releaseTypeLabel;
	private JComboBox updaterMode;
	private JComboBox releaseType;
	private JButton checkButton;
	private OptionPanel optionPanel;

	public UpdaterPanel(OptionPanel optionPanel) {
		this.optionPanel = optionPanel;
	}

	@Override
	public void initComponents() {
		updaterSettingLabel = new JLabel();
		updaterModeLabel = new JLabel();
		releaseTypeLabel = new JLabel();
		updaterMode = new JComboBox();
		releaseType = new JComboBox();
		checkButton = new JButton();
		
		updaterSettingLabel.setText("Updater Settings");

		updaterModeLabel.setText("Updater Mode:");
		
		releaseTypeLabel.setText("Release Type: ");

		updaterMode.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Automatic", "Prompt", "Manual" }));

		releaseType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Recommended", "Development" }));
		
		UpdaterMode mode = optionPanel.getSnapper().getConfiguration().getEnumValue("updateMode", UpdaterMode.class);
		if(mode != null) {
			updaterMode.setSelectedIndex(mode.ordinal());
		}
		
		UpdaterReleaseType type = optionPanel.getSnapper().getConfiguration().getEnumValue("updateReleaseType", UpdaterReleaseType.class);
		if(mode != null) {
			releaseType.setSelectedIndex(type.ordinal());
		}
		
		updaterMode.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED) {
					// Set config to this.
					optionPanel.getSnapper().getConfiguration().put("updateMode", updaterMode.getSelectedIndex());
					try {
						optionPanel.getSnapper().getConfiguration().save();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		
		releaseType.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED) {
					// Set config to this.
					optionPanel.getSnapper().getConfiguration().put("updateReleaseType", releaseType.getSelectedIndex());
					try {
						optionPanel.getSnapper().getConfiguration().save();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		});

		checkButton.setText("Check for Updates");
		
		checkButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Updater updater = new Updater();
				
				checkButton.setEnabled(false);
				
				UpdaterReleaseType type = optionPanel.getSnapper().getConfiguration().getEnumValue("updateReleaseType", UpdaterReleaseType.class);
				if(type == null) {
					type = UpdaterReleaseType.RECOMMENDED;
				}
				
				if(!updater.checkUpdate(type, true)) {
					JOptionPane.showMessageDialog(UpdaterPanel.this, "No new updates available.", "Sleeksnap Update", JOptionPane.INFORMATION_MESSAGE);
					checkButton.setEnabled(true);
				}
			}
		});
		
		javax.swing.GroupLayout updaterPanelLayout = new javax.swing.GroupLayout(this);
		this.setLayout(updaterPanelLayout);
		updaterPanelLayout.setHorizontalGroup(
				updaterPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
			.addGroup(updaterPanelLayout.createSequentialGroup()
				.addContainerGap()
				.addGroup(updaterPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
					.addComponent(updaterSettingLabel)
					.addGroup(updaterPanelLayout.createSequentialGroup()
						.addGap(10, 10, 10)
						.addGroup(updaterPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
							.addComponent(releaseTypeLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addComponent(updaterModeLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
						.addGroup(updaterPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
							.addComponent(releaseType, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addComponent(updaterMode, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
						.addGap(272, 272, 272))
					.addGroup(updaterPanelLayout.createSequentialGroup()
						.addComponent(checkButton, javax.swing.GroupLayout.DEFAULT_SIZE, 143, Short.MAX_VALUE)
						.addGap(316, 316, 316))))
		);
		updaterPanelLayout.setVerticalGroup(
			updaterPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
			.addGroup(updaterPanelLayout.createSequentialGroup()
				.addContainerGap()
				.addComponent(updaterSettingLabel)
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
				.addGroup(updaterPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
					.addComponent(updaterModeLabel)
					.addComponent(updaterMode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
				.addGroup(updaterPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
					.addComponent(releaseTypeLabel)
					.addComponent(releaseType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
				.addComponent(checkButton)
				.addContainerGap(344, Short.MAX_VALUE))
		);
	}

}

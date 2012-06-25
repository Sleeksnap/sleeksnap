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
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import org.sleeksnap.Constants;
import org.sleeksnap.gui.OptionPanel;
import org.sleeksnap.util.Util;
import org.sleeksnap.util.WinRegistry;
import org.sleeksnap.util.Utils.FileUtils;

import com.sun.jna.Platform;

/**
 * An OptionSubPanel for the main info panel
 * 
 * @author Nikki
 *
 */
@SuppressWarnings({"serial"})
public class InfoPanel extends OptionSubPanel {
	
    private JLabel versionLabel;
	private JLabel logoLabel;
	
	private JCheckBox startOnStartup;
	private JButton saveAllButton;

	private OptionPanel parent;
	
	public InfoPanel(OptionPanel parent) {
		this.parent = parent;
	}
	
	@Override
	public void initComponents() {
        logoLabel = new JLabel();
        versionLabel = new JLabel();
        startOnStartup = new javax.swing.JCheckBox();
        saveAllButton = new javax.swing.JButton();
        
        
		this.setPreferredSize(new java.awt.Dimension(300, 442));

		logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
		logoLabel.setIcon(new ImageIcon(Util.getResourceByName(Constants.Resources.LOGO_PATH))); // NOI18N

		versionLabel.setText("Version "+Constants.Application.VERSION);

        startOnStartup.setText("Start Sleeksnap on startup (Windows only)");

        saveAllButton.setText("Save all");
        
        saveAllButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//Save startup options here... Windows we can add it to the registry, Linux.... dunno
				boolean start = startOnStartup.isSelected();
				if(start) {
					//Overwrite the old key, in case it's a new version.
					if(Platform.isWindows()) {
						try {
							WinRegistry.writeStringValue(WinRegistry.HKEY_CURRENT_USER, WinRegistry.RUN_PATH, Constants.Application.NAME, FileUtils.getJarPath(OptionPanel.class));
						} catch (Exception e1) {
							//TODO problem, we couldn't add it!
						}
					}
				} else {
					if(Platform.isWindows()) {
						try {
							WinRegistry.deleteValue(WinRegistry.HKEY_CURRENT_USER, WinRegistry.RUN_PATH, Constants.Application.NAME);
						} catch (Exception e1) {
							//Doesn't exist, no problem.
						}
					}
				}
				parent.getConfiguration().put("startOnStartup", start);
				try {
					parent.getConfiguration().save();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				parent.saveAll();
				//Save everything else
			}
        });
        
		javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(this);
        this.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(versionLabel)
                    .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(startOnStartup)
                        .addComponent(logoLabel)))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                .addContainerGap(352, Short.MAX_VALUE)
                .addComponent(saveAllButton)
                .addContainerGap())
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(logoLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(versionLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(startOnStartup)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 270, Short.MAX_VALUE)
                .addComponent(saveAllButton)
                .addContainerGap())
        );
	}
	
	public void doneBuilding() {
		if(parent.getConfiguration().contains("startOnStartup")) {
			startOnStartup.setSelected(parent.getConfiguration().getBoolean("startOnStartup"));
		}
	}
}

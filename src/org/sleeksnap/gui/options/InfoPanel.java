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

import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.LayoutStyle;
import javax.swing.SwingConstants;

import org.sleeksnap.Constants;
import org.sleeksnap.gui.OptionPanel;
import org.sleeksnap.updater.Updater;
import org.sleeksnap.updater.Updater.VerificationMode;
import org.sleeksnap.util.Util;
import org.sleeksnap.util.Utils.FileUtils;

import com.sun.jna.Platform;

/**
 * An OptionSubPanel for the main info panel
 * 
 * @author Nikki
 * 
 */
@SuppressWarnings({ "serial" })
public class InfoPanel extends OptionSubPanel {

	private JLabel versionLabel;
	private JLabel logoLabel;

	private JCheckBox startOnStartup;
	private JCheckBox compressImages;
	private JButton saveAllButton;

	private OptionPanel parent;
	private JCheckBox showIconCheckbox;

	public InfoPanel(OptionPanel parent) {
		this.parent = parent;
	}

	@Override
	public void initComponents() {
		logoLabel = new JLabel();
		versionLabel = new JLabel();
		startOnStartup = new JCheckBox();
		compressImages = new JCheckBox();
		showIconCheckbox = new JCheckBox();
		saveAllButton = new JButton();

		this.setPreferredSize(new java.awt.Dimension(300, 442));

		logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
		logoLabel.setIcon(new ImageIcon(Util
				.getResourceByName(Constants.Resources.LOGO_PATH)));

		versionLabel.setText("Version " + Constants.Version.getVersionString());

		startOnStartup.setText("Start Sleeksnap on startup (Windows and Linux only)");
		compressImages.setText("Compress images with pngout/pngcrush (Requires binaries)");
		showIconCheckbox.setText("Show icon in system tray");
		
		startOnStartup.setEnabled(Platform.isWindows() || Platform.isX11());
		
		showIconCheckbox.setSelected(true);

		saveAllButton.setText("Save all");

		saveAllButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean start = startOnStartup.isSelected();
				if (start) {
					// Overwrite the old key, in case it's a new version.
					if (Platform.isWindows() || Platform.isX11()) {
						try {
							Updater.verifyAutostart(FileUtils.getJarFile(OptionPanel.class), VerificationMode.INSERT);
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
				} else {
					if (Platform.isWindows() || Platform.isX11()) {
						try {
							Updater.verifyAutostart(FileUtils.getJarFile(OptionPanel.class), VerificationMode.REMOVE);
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
				}
				parent.getConfiguration().put("startOnStartup", start);
				parent.getConfiguration().put("compressImages", compressImages.isSelected());
				parent.getConfiguration().put("showIcon", showIconCheckbox.isSelected());
				try {
					parent.getConfiguration().save();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				parent.saveAll();
				// Save everything else
			}
		});

		GroupLayout mainPanelLayout = new GroupLayout(
				this);
		this.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
                mainPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(mainPanelLayout.createSequentialGroup()
                    .addGroup(mainPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(mainPanelLayout.createSequentialGroup()
                            .addContainerGap()
                            .addGroup(mainPanelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                .addComponent(versionLabel)
                                .addGroup(mainPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                    .addComponent(startOnStartup)
                                    .addComponent(logoLabel)
                                    .addComponent(compressImages)
                                    .addComponent(showIconCheckbox))))
                        .addGroup(GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                            .addContainerGap(368, Short.MAX_VALUE)
                            .addComponent(saveAllButton)))
                    .addContainerGap())
            );
            mainPanelLayout.setVerticalGroup(
                mainPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(mainPanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(logoLabel)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(versionLabel)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(startOnStartup)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(compressImages)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(showIconCheckbox)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 224, Short.MAX_VALUE)
                    .addComponent(saveAllButton)
                    .addContainerGap())
            );
	}

	public void doneBuilding() {
		if (parent.getConfiguration().contains("startOnStartup")) {
			startOnStartup.setSelected(parent.getConfiguration().getBoolean(
					"startOnStartup"));
		}
		if (parent.getConfiguration().contains("compressImages")) {
			compressImages.setSelected(parent.getConfiguration().getBoolean("compressImages"));
		}
	}
}

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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.swing.GroupLayout;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.sleeksnap.Configuration;
import org.sleeksnap.ScreenSnapper;
import org.sleeksnap.gui.options.HistoryPanel;
import org.sleeksnap.gui.options.HotkeyPanel;
import org.sleeksnap.gui.options.InfoPanel;
import org.sleeksnap.gui.options.LogPanel;
import org.sleeksnap.gui.options.UploaderPanel;
import org.sleeksnap.impl.History;
import org.sleeksnap.util.StreamUtils;
import org.sleeksnap.util.Util;

/**
 * 
 * @author Nikki
 */
@SuppressWarnings({ "serial" })
public class OptionPanel extends JPanel {

	private ScreenSnapper snapper;

	private JTabbedPane jTabbedPane1;

	private int previousTab = 0;

	private InfoPanel infoPanel;

	private UploaderPanel uploaderPanel;

	private HotkeyPanel hotkeyPanel;

	private HistoryPanel historyPanel;

	private LogPanel logPanel;

	public OptionPanel(ScreenSnapper snapper) {
		this.snapper = snapper;
		initComponents();
	}

	public void doneBuilding() {
		infoPanel.doneBuilding();
		uploaderPanel.doneBuilding();
		hotkeyPanel.doneBuilding();
		historyPanel.doneBuilding();
		logPanel.doneBuilding();
	}

	private void initComponents() {

		jTabbedPane1 = new JTabbedPane();

		// New panels
		infoPanel = new InfoPanel(this);
		infoPanel.initComponents();

		uploaderPanel = new UploaderPanel(this);
		uploaderPanel.initComponents();

		hotkeyPanel = new HotkeyPanel(this);
		hotkeyPanel.initComponents();

		historyPanel = new HistoryPanel(this);
		historyPanel.initComponents();

		logPanel = new LogPanel(this);
		logPanel.initComponents();

		setMinimumSize(new java.awt.Dimension(500, 300));

		jTabbedPane1.setTabPlacement(JTabbedPane.LEFT);
		jTabbedPane1.setCursor(new java.awt.Cursor(
				java.awt.Cursor.DEFAULT_CURSOR));

		jTabbedPane1.addTab("Main", infoPanel);

		jTabbedPane1.addTab("Uploaders", uploaderPanel);

		jTabbedPane1.addTab("Hotkeys", hotkeyPanel);

		jTabbedPane1.addTab("History", historyPanel);

		jTabbedPane1.addTab("Log", logPanel);

		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(
				GroupLayout.Alignment.LEADING).addComponent(jTabbedPane1,
				GroupLayout.PREFERRED_SIZE, 500, GroupLayout.PREFERRED_SIZE));
		layout.setVerticalGroup(layout.createParallelGroup(
				GroupLayout.Alignment.LEADING).addComponent(jTabbedPane1,
				GroupLayout.PREFERRED_SIZE, 470, GroupLayout.PREFERRED_SIZE));

		jTabbedPane1.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				int index = jTabbedPane1.getSelectedIndex();
				if (index == 4) {
					// Load the log
					File file = new File(Util.getWorkingDirectory(), "log.txt");
					try {
						String contents = StreamUtils
								.readContents(new FileInputStream(file));
						logPanel.setContents(contents);
					} catch (IOException ex) {
						ex.printStackTrace();
					}
				}
				if (previousTab == 2) {
					// Restart our input manager if we disabled it
					if (!snapper.getKeyManager().hasKeysBound()) {
						snapper.getKeyManager().initializeInput();
					}
				}
				previousTab = index;
			}
		});

		// Do any loading/initializing
		hotkeyPanel.loadCurrentHotkeys();
	}

	public void setHistory(History history) {
		historyPanel.setHistory(history);
	}

	public UploaderPanel getUploaderPanel() {
		return uploaderPanel;
	}

	public ScreenSnapper getSnapper() {
		return snapper;
	}

	public Configuration getConfiguration() {
		return snapper.getConfiguration();
	}

	public void saveAll() {
		hotkeyPanel.savePreferences();
		uploaderPanel.savePreferences();
	}
}

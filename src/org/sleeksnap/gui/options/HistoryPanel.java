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

import java.awt.Desktop;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.sleeksnap.gui.OptionPanel;
import org.sleeksnap.impl.History;
import org.sleeksnap.impl.HistoryEntry;
import org.sleeksnap.util.Util;
import org.sleeksnap.util.Utils.ClipboardUtil;

/**
 * An OptionSubPanel for History elements
 * 
 * @author Nikki
 * 
 */
@SuppressWarnings({ "serial", "unchecked", "rawtypes", "unused" })
public class HistoryPanel extends OptionSubPanel {

	private OptionPanel parent;

	private JList historyList;

	private JButton historySelect;
	private JButton historyOpen;
	private JButton historyCopy;

	private JTextField linkField;

	private JLabel linkLabel;
	private JScrollPane historyScroll;

	public HistoryPanel(OptionPanel parent) {

	}

	@Override
	public void initComponents() {
		historyScroll = new JScrollPane();
		historyList = new JList();
		linkLabel = new JLabel();
		linkField = new JTextField();
		historyOpen = new JButton();
		historyCopy = new JButton();
		historySelect = new JButton();

		historyList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		historyList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					Object value = historyList.getSelectedValue();
					if (value != null) {
						HistoryEntry entry = (HistoryEntry) value;
						linkField.setText(entry.getUrl());
					}
				}
			}
		});

		historyScroll.setViewportView(historyList);

		linkLabel.setText("Link:");

		linkField.setEditable(false);

		historyOpen.setText("Open");
		historyOpen.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				historyOpenActionPerformed(evt);
			}
		});

		historyCopy.setText("Copy");
		historyCopy.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				historyCopyActionPerformed(evt);
			}
		});

		historySelect.setText("Select");
		historySelect.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				historySelectActionPerformed(evt);
			}
		});

		GroupLayout historyPanelLayout = new GroupLayout(this);
		this.setLayout(historyPanelLayout);
		historyPanelLayout
				.setHorizontalGroup(historyPanelLayout
						.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addGroup(
								historyPanelLayout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												historyPanelLayout
														.createParallelGroup(
																GroupLayout.Alignment.LEADING)
														.addComponent(
																historyScroll,
																GroupLayout.DEFAULT_SIZE,
																411,
																Short.MAX_VALUE)
														.addGroup(
																historyPanelLayout
																		.createParallelGroup(
																				GroupLayout.Alignment.LEADING,
																				false)
																		.addGroup(
																				historyPanelLayout
																						.createSequentialGroup()
																						.addComponent(
																								historyOpen)
																						.addPreferredGap(
																								LayoutStyle.ComponentPlacement.RELATED,
																								GroupLayout.DEFAULT_SIZE,
																								Short.MAX_VALUE)
																						.addComponent(
																								historySelect)
																						.addPreferredGap(
																								LayoutStyle.ComponentPlacement.RELATED)
																						.addComponent(
																								historyCopy))
																		.addComponent(
																				linkField,
																				GroupLayout.PREFERRED_SIZE,
																				188,
																				GroupLayout.PREFERRED_SIZE)
																		.addComponent(
																				linkLabel)))
										.addContainerGap()));
		historyPanelLayout
				.setVerticalGroup(historyPanelLayout
						.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addGroup(
								historyPanelLayout
										.createSequentialGroup()
										.addContainerGap()
										.addComponent(linkLabel)
										.addPreferredGap(
												LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(linkField,
												GroupLayout.PREFERRED_SIZE,
												GroupLayout.DEFAULT_SIZE,
												GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(
												LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(
												historyPanelLayout
														.createParallelGroup(
																GroupLayout.Alignment.BASELINE)
														.addComponent(
																historyOpen)
														.addComponent(
																historyCopy)
														.addComponent(
																historySelect))
										.addPreferredGap(
												LayoutStyle.ComponentPlacement.UNRELATED)
										.addComponent(historyScroll,
												GroupLayout.DEFAULT_SIZE, 363,
												Short.MAX_VALUE)
										.addContainerGap()));
	}

	private void historyCopyActionPerformed(java.awt.event.ActionEvent evt) {
		String text = linkField.getText();
		if (!text.equals("")) {
			ClipboardUtil.setClipboard(text);
		}
	}

	private void historyOpenActionPerformed(java.awt.event.ActionEvent evt) {
		String text = linkField.getText();
		if (!text.equals("")) {
			try {
				Util.openURL(new URL(text));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void historySelectActionPerformed(java.awt.event.ActionEvent evt) {
		String text = linkField.getText();
		if (!text.equals("")) {
			linkField.select(0, text.length());
		}
	}

	public void setHistory(History history) {
		DefaultListModel model = new DefaultListModel();
		List<HistoryEntry> list = history.getHistory();
		for (int i = list.size() - 1; i >= 0; i--) {
			model.addElement(list.get(i));
		}
		historyList.setModel(model);
	}
}

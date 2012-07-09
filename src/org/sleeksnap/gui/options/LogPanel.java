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

import javax.swing.GroupLayout;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.sleeksnap.gui.OptionPanel;

/**
 * An OptionSubPanel for the Log panel
 * 
 * @author Nikki
 * 
 */
@SuppressWarnings({ "serial", "unused" })
public class LogPanel extends OptionSubPanel {

	private OptionPanel parent;

	private JScrollPane logScroll;
	private JTextArea logArea;

	public LogPanel(OptionPanel parent) {
		this.parent = parent;
	}

	@Override
	public void initComponents() {
		logScroll = new JScrollPane();
		logArea = new JTextArea();

		logArea.setColumns(20);
		logArea.setRows(5);
		logArea.setEditable(false);

		logScroll.setViewportView(logArea);

		GroupLayout logPanelLayout = new GroupLayout(this);
		this.setLayout(logPanelLayout);
		logPanelLayout.setHorizontalGroup(logPanelLayout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addComponent(
				logScroll, javax.swing.GroupLayout.Alignment.TRAILING,
				javax.swing.GroupLayout.DEFAULT_SIZE, 431, Short.MAX_VALUE));
		logPanelLayout.setVerticalGroup(logPanelLayout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addComponent(
				logScroll, javax.swing.GroupLayout.DEFAULT_SIZE, 465,
				Short.MAX_VALUE));
	}

	public void setContents(String contents) {
		logArea.setText(contents);
		logArea.setCaretPosition(contents.length());
	}

}

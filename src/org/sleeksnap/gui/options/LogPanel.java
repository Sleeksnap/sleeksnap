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

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.sleeksnap.gui.OptionPanel;
import org.sleeksnap.upload.TextUpload;
import org.sleeksnap.util.Utils.ClipboardUtil;
import org.sleeksnap.util.logging.LogPanelHandler;

/**
 * An OptionSubPanel for the Log panel
 * 
 * @author Nikki
 * 
 */
@SuppressWarnings({ "serial" })
public class LogPanel extends OptionSubPanel {

	private OptionPanel parent;

	private JScrollPane logScroll;
	private JTextArea logArea;
	
	private JButton copyLogButton;
	private JButton uploadLogButton;
	private JButton clearLogButton;

	public LogPanel(OptionPanel parent) {
		this.parent = parent;
	}

	@Override
	public void initComponents() {
		logScroll = new JScrollPane();
		logArea = new JTextArea();
		
		copyLogButton = new JButton();
		uploadLogButton = new JButton();
		clearLogButton = new JButton();

		logArea.setColumns(20);
		logArea.setRows(5);
		logArea.setEditable(false);

		logScroll.setViewportView(logArea);
		
		copyLogButton.setText("Copy Log");
		
		copyLogButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ClipboardUtil.setClipboard(logArea.getText());
			}
        });

        uploadLogButton.setText("Upload Log");
        
        uploadLogButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				parent.getSnapper().upload(new TextUpload(logArea.getText()));
			}
        });

        clearLogButton.setText("Clear Log");
        
        clearLogButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				logArea.setText("");
			}
        });

		GroupLayout logPanelLayout = new GroupLayout(this);
		this.setLayout(logPanelLayout);
		logPanelLayout.setHorizontalGroup(
	            logPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addComponent(logScroll)
	            .addGroup(logPanelLayout.createSequentialGroup()
	                .addComponent(copyLogButton, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
	                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 29, Short.MAX_VALUE)
	                .addComponent(clearLogButton, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
	                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 29, Short.MAX_VALUE)
	                .addComponent(uploadLogButton, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
	        );
	        logPanelLayout.setVerticalGroup(
	            logPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addGroup(logPanelLayout.createSequentialGroup()
	                .addComponent(logScroll, javax.swing.GroupLayout.PREFERRED_SIZE, 429, javax.swing.GroupLayout.PREFERRED_SIZE)
	                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
	                .addGroup(logPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
	                    .addComponent(uploadLogButton)
	                    .addComponent(copyLogButton)
	                    .addComponent(clearLogButton)))
	        );
	}

	public void setContents(String contents) {
		logArea.setText(contents);
		logArea.setCaretPosition(contents.length());
	}
	
	@Override
	public void doneBuilding() {
		LogPanelHandler.bindTo(this);
	}

	public void appendLog(String formattedLine) {
		logArea.append(formattedLine);
		logArea.setCaretPosition(logArea.getText().length());
	}

}

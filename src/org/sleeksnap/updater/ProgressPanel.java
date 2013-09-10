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
package org.sleeksnap.updater;

import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;

import org.sleeksnap.updater.Downloader.DownloadListener;
import org.sleeksnap.util.Utils.FormatUtil;

/**
 * A JPanel which monitors the progress of a download
 * 
 * @author Nikki
 *
 */
@SuppressWarnings("serial")
public class ProgressPanel extends JPanel implements DownloadListener {

	/**
	 * The label to represent download progress
	 */
    private JLabel progressLabel;

    /**
     * The ProgressBar to display percentage
     */
    private JProgressBar progressBar;
    
    /**
     * Construct a new panel and initialize it.
     */
    public ProgressPanel() {
        initComponents();
    }
    
    /**
     * Initialize the panel components
     */
    private void initComponents() {
        progressBar = new JProgressBar();
        progressLabel = new JLabel();

        progressLabel.setHorizontalAlignment(SwingConstants.CENTER);
        progressLabel.setText("Progress");
        
        progressBar.setStringPainted(true);

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(progressBar, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 384, Short.MAX_VALUE)
                    .addComponent(progressLabel, GroupLayout.DEFAULT_SIZE, 384, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(progressLabel)
                .addGap(14, 14, 14)
                .addComponent(progressBar, GroupLayout.PREFERRED_SIZE, 29, GroupLayout.PREFERRED_SIZE)
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }

	@Override
	public void downloadStarted(Downloader downloader, long fileSize) {
		//Set the initial values
		progressUpdated(downloader, 0, 0);
	}

	@Override
	public void progressUpdated(Downloader downloader, int percent, long bytes) {
		String text = FormatUtil.humanReadableByteCount(bytes, false)+"/"+ FormatUtil.humanReadableByteCount(downloader.getFileSize(), false);
		if(downloader.getStartTime() != 0) {
			//Update speed
			int elapsed = (int) ((System.currentTimeMillis() - downloader.getStartTime()) / 1000);
			if(elapsed > 0) {
				text += " (" + FormatUtil.humanReadableByteCount(downloader.getFileSize() / elapsed, false) + "/s)";
			}
		}
		//Set values
		progressLabel.setText(text);
		progressBar.setValue(percent);
	}

	@Override
	public void downloadFinished(Downloader downloader) {
		progressLabel.setText("Finished! Downloaded " + FormatUtil.humanReadableByteCount(downloader.getFileSize(), false) + " in " + FormatUtil.timeElapsed((System.currentTimeMillis() - downloader.getStartTime()), false));
	}
}
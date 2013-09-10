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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;
import java.util.List;

/**
 * A simple wrapper to provide percentage updates and other asynchronous functions for downloading files from URLs
 * 
 * @author Nikki
 *
 */
public class Downloader implements Runnable {

	/**
	 * The list of download listeners to inform for status updates
	 */
	private List<DownloadListener> listeners = new LinkedList<DownloadListener>();
	
	/**
	 * The URL to download from
	 */
	private URL url;
	
	/**
	 * The OutputStream to write to, can be any valid output stream.
	 */
	private OutputStream output;
	
	/**
	 * File size
	 */
	private long length = 0;
	
	/**
	 * Count of amount downloaded so far
	 */
	private long downloaded = 0;
	
	/**
	 * Current percentage
	 */
	private int percent = 0;
	
	/**
	 * Download start time
	 */
	private long startTime = 0;
	
	/**
	 * Construct a new Downloader
	 * @param url
	 * 			The URL to download from
	 * @param output
	 * 			The output stream to write to
	 */
	public Downloader(URL url, OutputStream output) {
		this.url = url;
		this.output = output;
	}
	
	/**
	 * Download the file.
	 */
	@Override
	public void run() {
		try {
			URLConnection connection = url.openConnection();
			
			length = connection.getContentLengthLong();
			
			InputStream input = connection.getInputStream();
			
			byte[] buffer = new byte[1024];
			
			int lastPercent = 0;
			
			downloadStarted(length);
			
			startTime = System.currentTimeMillis();
			
			try {
				while(true) {
					int read = input.read(buffer, 0, buffer.length);
					if (read < 0) {
						break;
					}
					downloaded += read;
					
					percent = (int) (((double) downloaded / (double) length) * 100);
					if (percent > lastPercent) {
						progressEvent(percent, downloaded);
						lastPercent = percent;
					}
					output.write(buffer, 0, read);
				}
			} finally {
				input.close();
				output.close();
			}
			
			downloadFinished();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Add a listener to the list
	 * @param listener
	 * 			The listener to add
	 */
	public void addListener(DownloadListener listener) {
		listeners.add(listener);
	}
	
	/**
	 * Start a new thread to download
	 */
	public void start() {
		new Thread(this).start();
	}
	
	/**
	 * Get the download's full size
	 * @return
	 * 		The size of the download as reported by the content length header
	 */
	public long getFileSize() {
		return length;
	}
	
	/**
	 * Get the amount of bytes downloaded so far
	 * @return
	 * 		The byte count downloaded
	 */
	public long getDownloaded() {
		return downloaded;
	}
	
	/**
	 * Get the current download percentage
	 * @return
	 * 		The percentage
	 */
	public int getPercentage() {
		return percent;
	}
	
	/**
	 * Get the download start time
	 * @return
	 * 		The download's start time
	 */
	public long getStartTime() {
		return startTime;
	}
	
	/**
	 * Notify the listeners for the download starting
	 * @param fileSize
	 * 			The file size parsed off the header
	 */
	private void downloadStarted(long fileSize) {
		for(DownloadListener listener : listeners) {
			listener.downloadStarted(this, fileSize);
		}
	}
	
	/**
	 * Called whenever percent > lastPercent (Like 80 -> 81)
	 * @param percent
	 * 			The current percentage
	 * @param bytes
	 * 			The amount of bytes downloaded since start
	 */
	private void progressEvent(int percent, long bytes) {
		for(DownloadListener listener : listeners) {
			listener.progressUpdated(this, percent, bytes);
		}
	}
	
	/**
	 * Called when the download finishes.
	 */
	private void downloadFinished() {
		for(DownloadListener listener : listeners) {
			listener.downloadFinished(this);
		}
	}
	
	/**
	 * A listener to provide download updates
	 * 
	 * @author Nikki
	 *
	 */
	public interface DownloadListener {

		/**
		 * Called after the filesize is found and the download is about to start
		 * @param fileSize
		 * 			The file size
		 */
		public void downloadStarted(Downloader downloader, long fileSize);

		/**
		 * Called whenever the percentage increases (Like 81% -> 82%)
		 * @param percent
		 * 			The current percentage, also available through Downloader.getPercentage();
		 * @param bytes
		 * 			The current amount of bytes downloaded, also available through Downloader.getDownloaded();
		 */
		public void progressUpdated(Downloader downloader, int percent, long bytes);
		
		/**
		 * Called when a download finishes
		 * @param downloader
		 * 			The downloader
		 */
		public void downloadFinished(Downloader downloader);
	}
	
	public static class DownloadAdapter implements DownloadListener {

		@Override
		public void downloadStarted(Downloader downloader, long fileSize) {
		}

		@Override
		public void progressUpdated(Downloader downloader, int percent, long bytes) {
		}

		@Override
		public void downloadFinished(Downloader downloader) {
		}
		
	}
}

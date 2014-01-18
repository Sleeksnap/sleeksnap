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

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.json.JSONException;
import org.json.JSONObject;
import org.sleeksnap.Constants.Application;
import org.sleeksnap.Constants.Version;
import org.sleeksnap.Launcher;
import org.sleeksnap.http.HttpUtil;
import org.sleeksnap.updater.Downloader.DownloadAdapter;
import org.sleeksnap.util.DesktopEntryBuilder;
import org.sleeksnap.util.Util;
import org.sleeksnap.util.Utils.FileUtils;
import org.sleeksnap.util.WinRegistry;

import com.sun.jna.Platform;

/**
 * An automatic Updater for Sleeksnap
 * 
 * Well documented to explain how it works and that it is not malicious.
 * 
 * @author Nikki
 *
 */
public class Updater {
	
	private static final Logger logger = Logger.getLogger(Updater.class.getName());
	
	/**
	 * The binary directory to use.
	 */
	private File binDirectory;
	
	/**
	 * Construct a new updater
	 */
	public Updater() {
		binDirectory = new File(Util.getWorkingDirectory(), "bin");
		binDirectory.mkdirs();
	}

	/**
	 * Check for updates
	 */
	public boolean checkUpdate(UpdaterReleaseType type, boolean prompt) {
		//Check for an update.
		try {
			logger.info("Checking for updates...");
			
			String data = HttpUtil.executeGet(Application.UPDATE_URL + type.getFeedPath());
			
			JSONObject obj = new JSONObject(data);
			
			//Compare versions
			String ver = obj.getString("version");
			
			String[] s = ver.split("\\.");
			int major = Integer.parseInt(s[0]), minor = Integer.parseInt(s[1]), patch = Integer.parseInt(s[2]);
			if(major > Version.MAJOR || major == Version.MAJOR && minor > Version.MINOR || major == Version.MAJOR && minor == Version.MINOR && patch > Version.PATCH) {
				logger.info("A new version is available. Current version: " + Version.getVersionString() + ", new version: " + major + "." + minor + "." + patch);
				if(prompt) {
					StringBuilder message = new StringBuilder();
					message.append("There is a new version of ").append(Application.NAME).append(" available!").append("\n");
					message.append("Your version: ").append(Version.getVersionString()).append("\n");
					message.append("Latest version: ").append(ver).append("\n");
					message.append("Click OK to download it, or Cancel to be prompted the next time you start ").append(Application.NAME);
					
					int choice = JOptionPane.showOptionDialog(null, message, "Update Available", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, new String[] {"OK", "Cancel"}, "OK");
					if(choice == 0) {
						logger.info("User confirmed the update.");
						
						applyUpdate(ver, new URL(obj.getString("file")));
						
						return true;
					} else {
						logger.info("User declined the update.");
					}
				} else {
					logger.info("Automatically applying update...");
					
					applyUpdate(ver, new URL(obj.getString("file")));
					
					return true;
				}
			} else {
				logger.info("No updates available.");
			}
		} catch (JSONException e) {
			logger.severe("Unable to check for update due to web service error.");
		} catch (IOException e) {
			//Unable to update
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Apply an update from the specified URL
	 * 
	 * @param version
	 * 			The version which we are downloading
	 * @param url
	 * 			The URL to download from
	 * @throws IOException
	 * 			If an error occurred while downloading
	 */
	public void applyUpdate(String version, URL url) throws IOException {
		// Construct the new path
		File out = new File(binDirectory, "Sleeksnap-v" + version + ".jar");
		
		logger.info("Creating new file...");
		// Create the new file
		out.createNewFile();
		
		logger.info("Downloading " + url + "...");
		// Download the new file
		download(url, out);
	}
	
	/**
	 * Download the specified file with a ProgressPanel to show progress.
	 * @param url
	 * 			The URL to download from
	 * @param file
	 * 			The file to download to
	 * @throws IOException
	 * 			If a problem occurred while starting the download
	 */
	public void download(URL url, final File file) throws IOException {
		JFrame frame = new JFrame("Sleeksnap Update");
		ProgressPanel panel = new ProgressPanel();
		frame.add(panel);
		frame.pack();
		frame.setResizable(false);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		try {
			frame.setIconImage(ImageIO.read(Util
					.getResourceByName("/icon32x32.png")));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		Util.centerFrame(frame);
		
		Downloader downloader = new Downloader(url, new FileOutputStream(file));
		downloader.addListener(panel);
		downloader.addListener(new DownloadAdapter() {
			public void downloadFinished(Downloader downloader) {
				updateFinished(file);
			}
		});
		downloader.start();
	}
	
	/**
	 * Called when the update is finished to re-launch Sleeksnap
	 * 
	 * @param file	The newly downloaded jar file
	 */
	public void updateFinished(File file) {
		logger.info("Checking autostart...");
		verifyAutostart(file, VerificationMode.VERIFY);
		
		if(Platform.isWindows()) {
			logger.info("Checking start menu icon...");
			try {
				WindowsUpdater.checkStartMenu(file);
			} catch (Exception e) {
				logger.log(Level.WARNING, "Unable to update start menu icon", e);
			}
		}
		
		logger.info("Launching new file...");
		try {
			Launcher.launch(file, Launcher.class.getName());
			
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * If on windows or linux, verify that the registry entry is intact and pointing to the correct version.
	 * @param file
	 * 			The file to verify against
	 * @param mode
	 * 			The mode to use, REMOVE will remove the entry, VERIFY will update it, and INSERT will insert if it does not exist.
	 */
	public static void verifyAutostart(File file, VerificationMode mode) {
		if(Platform.isWindows()) {
			try {
				String current = WinRegistry.readString(WinRegistry.HKEY_CURRENT_USER, WinRegistry.RUN_PATH, Application.NAME);
				if(mode == VerificationMode.REMOVE) {
					WinRegistry.deleteValue(WinRegistry.HKEY_CURRENT_USER, WinRegistry.RUN_PATH, Application.NAME);
				} else if(mode == VerificationMode.INSERT && current == null || current != null && !current.equals(file.getAbsolutePath())) {
					WinRegistry.writeStringValue(WinRegistry.HKEY_CURRENT_USER, WinRegistry.RUN_PATH, Application.NAME, file.getAbsolutePath());
				}
			} catch (Exception e) {
				//Ignore it.
			}
		} else if(Platform.isX11()) {
			File autostartDir = new File(System.getenv("XDG_CONFIG_HOME") != null ? System.getenv("XDG_CONFIG_HOME") : System.getProperty("user.home") + "/.config/autostart");
			if(autostartDir.exists()) {
				File autostart = new File(autostartDir, Application.NAME.toLowerCase() + ".desktop");
				
				if(mode == VerificationMode.REMOVE) {
					autostart.delete();
				} else {
					String contents = new DesktopEntryBuilder()
						.addEntry("Type", "Application")
						.addEntry("Categories", "Accessories")
						.addEntry("Name", Application.NAME)
						.addEntry("Comment", "Sleeksnap - Java Screenshot Program")
						.addEntry("Terminal", false)
						.addEntry("Exec", FileUtils.getJavaExecutable().getAbsoluteFile() + " -jar \"" + file.getAbsolutePath() + "\"")
					.build();
					
					try {
						FileWriter writer = new FileWriter(autostart);
						try {
							writer.write(contents);
						} finally {
							writer.close();
						}
					} catch(IOException e) {
					}
				}
			}
		}
	}
	
	public enum VerificationMode {
		INSERT, VERIFY, REMOVE
	}
}

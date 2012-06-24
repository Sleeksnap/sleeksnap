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
package org.sleeksnap;

import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import org.sleeksnap.Constants.Application;
import org.sleeksnap.Constants.Resources;
import org.sleeksnap.gui.OptionPanel;
import org.sleeksnap.gui.ParametersDialog;
import org.sleeksnap.gui.SelectionWindow;
import org.sleeksnap.impl.History;
import org.sleeksnap.impl.HistoryEntry;
import org.sleeksnap.impl.HotkeyManager;
import org.sleeksnap.impl.LoggingManager;
import org.sleeksnap.uploaders.FTPUploader;
import org.sleeksnap.uploaders.GenericUploader;
import org.sleeksnap.uploaders.Settings;
import org.sleeksnap.uploaders.Uploader;
import org.sleeksnap.uploaders.files.UppitUploader;
import org.sleeksnap.uploaders.images.ImgurUploader;
import org.sleeksnap.uploaders.images.KsnpUploader;
import org.sleeksnap.uploaders.text.PastebinUploader;
import org.sleeksnap.uploaders.text.PastebincaUploader;
import org.sleeksnap.uploaders.text.PasteeUploader;
import org.sleeksnap.uploaders.text.PastieUploader;
import org.sleeksnap.uploaders.url.GoogleShortener;
import org.sleeksnap.uploaders.url.TUrlShortener;
import org.sleeksnap.uploaders.url.TinyURLShortener;
import org.sleeksnap.util.MultipartPostMethod.FileUpload;
import org.sleeksnap.util.ScreenshotUtil;
import org.sleeksnap.util.StreamUtils;
import org.sleeksnap.util.Util;
import org.sleeksnap.util.Utils.ClipboardUtil;
import org.sleeksnap.util.Utils.ClipboardUtil.ClipboardException;
import org.sleeksnap.util.Utils.DateUtil;
import org.sleeksnap.util.Utils.DisplayUtil;
import org.sleeksnap.util.Utils.FileUtils;
import org.sleeksnap.util.active.WindowUtilProvider;

import tray.SystemTrayAdapter;
import tray.SystemTrayProvider;
import tray.TrayIconAdapter;

import com.sun.jna.Platform;

/**
 * The main Uploader Utility class
 * 
 * @author Nikki
 * 
 */
public class ScreenSnapper {

	/**
	 * A basic class which lets us execute a custom action based on the
	 * ScreenshotAction class
	 * 
	 * @author Nikki
	 * 
	 */
	@SuppressWarnings("serial")
	private class ActionMenuItem extends MenuItem implements ActionListener {
		/**
		 * The action id
		 */
		private int action;

		public ActionMenuItem(String s, int action) {
			super(s);
			this.action = action;
			addActionListener(this);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			hotkey(action);
		}
	}

	/**
	 * A wrapper for action ids
	 * 
	 * @author Nikki
	 * 
	 */
	public static class ScreenshotAction {
		private static final int CROP = 1;
		private static final int FULL = 2;
		private static final int CLIPBOARD = 3;
		public static final int ACTIVE = 4;
	}

	/**
	 * A basic hack for class associations -> names
	 */
	private static HashMap<Class<?>, String> names = new HashMap<Class<?>, String>();

	/**
	 * Load the names and set the useragent
	 */
	static {
		System.setProperty("http.agent", Util.getHttpUserAgent());
		
		names.put(BufferedImage.class, "Images");
		names.put(String.class, "Text");
		names.put(URL.class, "Urls");
		names.put(FileUpload.class, "Files");
	}

	private static final Logger logger = Logger.getLogger(ScreenSnapper.class.getName());

	public static void main(String[] args) {
		//Parse arguments, could be the directory, which can be set to "." for the current directory, or "./bla" to change
		HashMap<String, Object> map = Util.parseArguments(args);
		if(map.containsKey("dir")) {
			File file = new File(map.get("dir").toString());
			if(!file.exists()) {
				file.mkdirs();
			}
			Util.setWorkingDirectory(file);
		}
		//Initialize
		new ScreenSnapper();
	}
	/**
	 * A map which contains uploader classes -> a list of available uploaders
	 */
	private HashMap<Class<?>, Map<String, Uploader<?>>> uploaders = new HashMap<Class<?>, Map<String, Uploader<?>>>();

	/**
	 * A map which contains the current uploader settings
	 */
	private HashMap<Class<?>, Uploader<?>> uploaderAssociations = new HashMap<Class<?>, Uploader<?>>();

	/**
	 * The basic service...
	 */
	private ExecutorService serv = Executors.newSingleThreadExecutor();

	/**
	 * The ExecutorService used to upload
	 */
	private ExecutorService uploadService = Executors.newSingleThreadExecutor();

	/**
	 * The tray icon, used from a github repository which adds native support to
	 * allow linux transparency
	 * 
	 * @see tray.TrayIconAdapter
	 */
	private TrayIconAdapter icon;

	/**
	 * The configuration instance
	 */
	private Configuration configuration = new Configuration();

	/**
	 * The selection window instance
	 */
	private SelectionWindow window = null;

	/**
	 * Defines whether the options panel is open
	 */
	private boolean optionsOpen;
	
	/**
	 * The hotkey manager instance
	 */
	private HotkeyManager keyManager;
	
	/**
	 * The history instance
	 */
	private History history;

	public ScreenSnapper() {
		File local = Util.getWorkingDirectory();
		if (!local.exists()) {
			local.mkdirs();
		}
		//Then start
		LoggingManager.configure();
		logger.info("Loading uploaders...");
		try {
			loadUploaders();
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Failed to load uploaders!", e);
		}
		logger.info("Loading settings...");
		try {
			loadSettings();
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Failed to load settings!", e);
		}
		logger.info("Loading history...");
		history = new History(new File(local, "history.yml"));
		try {
			history.load();
		} catch (IOException e) {
			logger.log(Level.WARNING, "Failed to load history", e);
		}
		logger.info("Registering keys...");
		keyManager = new HotkeyManager(this);
		keyManager.initializeInput();
		logger.info("Opening tray icon...");
		initializeTray();
		logger.info("Ready.");
	}

	/**
	 * Perform a capture of the active window
	 */
	public void active() {
		try {
			upload(ScreenshotUtil.capture(WindowUtilProvider.getWindowUtil().getActiveWindow().getBounds()));
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Unable to take the active window screenshot", e);
			showException(e);
		}
	}

	/**
	 * Clear the screenshot selection window
	 */
	public void clearWindow() {
		window = null;
	}

	/**
	 * Upload content from the clipboard
	 */
	public void clipboard() {
		try {
			Object clipboard = ClipboardUtil.getClipboardContents();
			if (clipboard == null) {
				icon.displayMessage("Invalid clipboard contents",
						"Could not upload clipboard contents, invalid type",
						TrayIcon.MessageType.WARNING);
				return;
			}
			if (clipboard instanceof BufferedImage) {
				upload((BufferedImage) clipboard);
			} else if (clipboard instanceof File) {
				// TODO we could also trigger a file uploader for other files...
				File file = (File) clipboard;
				String mime = FileUtils.getMimeType(file.getAbsolutePath());
				// A better way to upload images, it'll check the mime type!
				if (mime.startsWith("image")) {
					upload(ImageIO.read(file));
				} else if (mime.startsWith("text")
						&& configuration.getBoolean("plainTextUpload")) {
					upload(FileUtils.readFile(file));
				} else {
					upload(file);
				}
			} else if (clipboard instanceof String) {
				String string = clipboard.toString();
				if (string
						.matches("((mailto\\:|(news|(ht|f)tp(s?))\\://){1}\\S+)")) {
					upload(new URL(clipboard.toString()));
				} else {
					upload(string);
				}
			}
		} catch (ClipboardException e) {
			logger.log(Level.SEVERE, "Unable to get clipboard contents", e);
			showException(e);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Perform a screenshot crop action
	 */
	public void crop() {
		if (window != null) {
			return;
		}
		window = new SelectionWindow(this, DisplayUtil.getRealScreenSize());
		window.pack();
		window.setAlwaysOnTop(true);
		window.setVisible(true);
	}

	/**
	 * Perform a full screenshot action
	 */
	public void full() {
		upload(ScreenshotUtil.capture(DisplayUtil.getRealScreenSize()));
	}

	/**
	 * Get the configuration file
	 * 
	 * @return The configuration file
	 */
	public Configuration getConfiguration() {
		return configuration;
	}

	public HotkeyManager getKeyManager() {
		return keyManager;
	}

	public File getSettingsFile(Class<?> uploader) {
		String name = uploader.getName();
		if(name.contains("$")) {
			name = name.substring(0, name.indexOf('$'));
		}
		File directory = new File(Util.getWorkingDirectory(), "config");
		if (!directory.exists()) {
			directory.mkdirs();
		}
		return new File(directory, name + ".xml");
	}

	/**
	 * Get the tray icon instance
	 * @return
	 * 		The instance of the Tray Icon
	 */
	public TrayIconAdapter getTrayIcon() {
		return icon;
	}

	/**
	 * Get the default uploader associations
	 * 
	 * @return The uploader associations
	 */
	public Map<Class<?>, Uploader<?>> getUploaderAssociations() {
		return uploaderAssociations;
	}

	/**
	 * Get the uploader which is mapped to the class type
	 * 
	 * @param cl
	 *            The type
	 * @return The uploader
	 */
	public Uploader<?> getUploaderFor(Class<?> cl) {
		return uploaderAssociations.get(cl);
	}

	/**
	 * Check and perform hotkeys, uses a separate service to not tie up the
	 * uploader
	 * 
	 * @param ident
	 *            The key id
	 */
	public void hotkey(final int ident) {
		serv.execute(new Runnable() {
			public void run() {
				switch (ident) {
				case ScreenshotAction.CROP:
					crop();
					break;
				case ScreenshotAction.FULL:
					full();
					break;
				case ScreenshotAction.CLIPBOARD:
					clipboard();
					break;
				case ScreenshotAction.ACTIVE:
					active();
					break;
				}
			}
		});
	}

	/**
	 * Initialize the tray menu
	 */
	private void initializeTray() {
		// Add uploaders from the list we loaded earlier
		PopupMenu tray = new PopupMenu();
		// Add the action menu
		Menu actions = new Menu("Actions");
		actions.add(new ActionMenuItem("Crop", ScreenshotAction.CROP));
		actions.add(new ActionMenuItem("Full", ScreenshotAction.FULL));
		actions.add(new ActionMenuItem("Clipboard", ScreenshotAction.CLIPBOARD));
		if(Platform.isWindows() || Platform.isLinux()) {
			actions.add(new ActionMenuItem("Active", ScreenshotAction.ACTIVE));
		}
		tray.add(actions);
		MenuItem settings = new MenuItem("Options");
		settings.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!openSettings()) {
					icon.displayMessage(
							"Error",
							"Could not open settings, is there another window open?",
							TrayIcon.MessageType.ERROR);
				}
			}
		});
		tray.add(settings);
		MenuItem exit = new MenuItem("Exit");
		exit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				shutdown();
			}

		});
		tray.add(exit);
		SystemTrayAdapter adapter = SystemTrayProvider.getSystemTray();
		icon = adapter.createAndAddTrayIcon(Util.getResourceByName(Resources.ICON_PATH),
				Application.NAME + " v" + Application.VERSION, tray);
		icon.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!openSettings()) {
					icon.displayMessage(
							"Error",
							"Could not open settings, is there another window open?",
							TrayIcon.MessageType.ERROR);
				}
			}
		});
	}
	
	/**
	 * Clean up and shut down
	 */
	private void shutdown() {
		uploadService.shutdown();
		keyManager.cleanupInput();
		System.exit(0);
	}

	/**
	 * Load settings
	 */
	private void loadSettings() throws Exception {
		File configFile = new File(Util.getWorkingDirectory(),
				Application.NAME.toLowerCase() + ".conf");
		if (!configFile.exists()) {
			String name = "/" + Application.NAME.toLowerCase()
					+ (Platform.isMac() ? "_mac" : "") + ".conf";
			StreamUtils.writeStreamToFile(getClass().getResourceAsStream(name),
					configFile);
		}
		configuration.load(configFile);
		if (configuration.contains("uploaders")) {
			Map<String, String> uploadConfig = configuration
					.getMap("uploaders");
			for (Entry<String, String> entry : uploadConfig.entrySet()) {
				Class<?> clType = Class.forName(entry.getKey());
				if (clType != null) {
					setDefaultUploader(clType, entry.getValue());
				}
			}
		}
	}

	/**
	 * Load the uploaders from inside the jar file and the regular file
	 * 
	 * @throws Exception
	 *             If an error occurred
	 */
	public void loadUploaders() throws Exception {
		// Register the default uploaders
		
		// Generic uploaders
		registerUploader(new FTPUploader());
		// Image Uploaders
		registerUploader(new ImgurUploader());
		registerUploader(new KsnpUploader());
		// Text uploaders
		registerUploader(new PasteeUploader());
		registerUploader(new PastebinUploader());
		registerUploader(new PastebincaUploader());
		registerUploader(new PastieUploader());
		// URL Shorteners
		registerUploader(new GoogleShortener());
		registerUploader(new TinyURLShortener());
		registerUploader(new TUrlShortener());
		// File uploaders
		registerUploader(new UppitUploader());
		
		// Load custom uploaders
		File dir = new File(Util.getWorkingDirectory(), "uploaders");
		if (!dir.exists()) {
			dir.mkdirs();
		}
		ClassLoader loader = new URLClassLoader(
				new URL[] { dir.toURI().toURL() });
		for (File f : dir.listFiles()) {
			String name = f.getName();
			if (name.endsWith(".class") && !name.contains("$")) {
				try {
					Class<?> c = loader.loadClass(f.getName().replaceAll(
							".class", ""));
					Uploader<?> uploader = (Uploader<?>) c.newInstance();
					if (uploader == null)
						throw new Exception();
					File uploaderSettings = new File(
							Util.getWorkingDirectory(), "config/"
									+ name.substring(0, name.lastIndexOf('.'))
									+ ".xml");
					if (uploaderSettings.exists()) {
						FileInputStream input = new FileInputStream(
								uploaderSettings);
						try {
							uploader.getProperties().loadFromXML(input);
						} finally {
							input.close();
						}
					}
					registerUploader(uploader);
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null,
							"An exception occured when loading " + name + " : "
									+ e + ", it could be outdated.",
							"Could not load uploader : " + name,
							JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}

	/**
	 * Open the settings panel
	 */
	public boolean openSettings() {
		if (optionsOpen) {
			return false;
		}
		if (!optionsOpen) {
			optionsOpen = true;
		}
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		JFrame frame = new JFrame("Sleeksnap Settings");
		
		OptionPanel panel = new OptionPanel(this);
		panel.setImageUploaders(uploaders.get(BufferedImage.class).values());
		panel.setTextUploaders(uploaders.get(String.class).values());
		panel.setURLUploaders(uploaders.get(URL.class).values());
		panel.setFileUploaders(uploaders.get(File.class).values());
		panel.setHistory(history);
		panel.doneBuilding();
		
		frame.add(panel);
		frame.pack();
		frame.setVisible(true);
		try {
			frame.setIconImage(ImageIO.read(Util.getResourceByName("/icon32x32.png")));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosed(WindowEvent e) {
				optionsOpen = false;
			}
		});
		return true;
	}

	/**
	 * Register an uploader
	 * 
	 * @param type
	 *            The type
	 * @param uploader
	 *            The uploader
	 */
	public void registerUploader(Uploader<?> uploader) {
		if (uploader instanceof GenericUploader) {
			GenericUploader u = (GenericUploader) uploader;
			for (Uploader<?> up : u.getSubUploaders()) {
				registerUploader(up);
			}
			return;
		}
		Class<?> type = uploader.getUploadType();
		if (!uploaders.containsKey(type)) {
			uploaders.put(type, new HashMap<String, Uploader<?>>());
		}
		uploaders.get(type).put(uploader.getClass().getName(), uploader);
	}

	/**
	 * Set a default type's uploader, checking if the settings are valid first
	 * 
	 * @param type
	 *            The class type
	 * @param name
	 *            The uploader name
	 */
	public void setDefaultUploader(final Class<?> type, String name) {
		if (uploaders.containsKey(type)) {
			Map<String, Uploader<?>> map = uploaders.get(type);
			if (map.containsKey(name)) {
				setDefaultUploader(map.get(name));
			} else {
				throw new RuntimeException("Invalid uploader " + name
						+ "! Possible choices: " + map.values());
			}
		} else {
			throw new RuntimeException("No uploaders set for " + type.getName());
		}
	}

	/**
	 * Set a default uploader
	 * 
	 * @param type
	 * @param uploader
	 */
	public void setDefaultUploader(final Uploader<?> uploader) {
		final Class<?> type = uploader.getUploadType();

		Settings settings = uploader.getClass().getAnnotation(Settings.class);
		Class<?> enclosing = uploader.getClass().getEnclosingClass();
		if(settings == null && enclosing != null) {
			settings = enclosing.getAnnotation(Settings.class);
		}
		if (settings != null) {
			final File file = getSettingsFile(uploader.getClass());
			if (!file.exists()) {
				final ParametersDialog dialog = new ParametersDialog(settings);
				dialog.setOkAction(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						uploader.setProperties(dialog.toProperties());
						uploaderAssociations.put(type, uploader);
						// Finally, save the settings
						try {
							FileOutputStream out = new FileOutputStream(file);
							try {
								uploader.getProperties()
										.storeToXML(
												out,
												"Uploader settings for "
														+ uploader.getClass()
																.getName());
							} finally {
								out.close();
							}
						} catch (Exception ex) {
							JOptionPane.showMessageDialog(null,
									"Save failed! Caused by: " + ex,
									"Save failed", JOptionPane.ERROR_MESSAGE);
						}
					}
				});
				dialog.setVisible(true);
			} else {
				uploaderAssociations.put(type, uploader);
			}
		} else {
			uploaderAssociations.put(type, uploader);
		}
	}
	
	private void showException(Exception e) {
		icon.displayMessage("An error occurred",
				"An error occurred while performing the selected action, cause: "
						+ e, MessageType.ERROR);
	}

	/**
	 * Upload an object
	 * 
	 * @param object
	 *            The object
	 */
	public void upload(final Object object) {
		if (!uploaderAssociations.containsKey(object.getClass())) {
			icon.displayMessage("No uploader available",
					"There is no uploader available for "
							+ object.getClass().getName() + "!",
					TrayIcon.MessageType.ERROR);
			return;
		}
		uploadService.execute(new Runnable() {
			@SuppressWarnings({ "rawtypes", "unchecked" })
			public void run() {
				Uploader uploader = uploaderAssociations.get(object.getClass());
				if (uploader != null) {
					try {
						String url = uploader.upload(uploader.getUploadType().cast(object));
						if (url != null) {
							if (configuration.getBoolean("shortenurls")) {
								Uploader shortener = uploaderAssociations
										.get(URL.class);
								if (shortener != null) {
									url = shortener.upload(new URL(url));
								}
							}
							if (object instanceof BufferedImage) {
								if(configuration.getBoolean("savelocal")) {
									FileOutputStream output = new FileOutputStream(getLocalFile(DateUtil.getCurrentDate()+".png"));
									try {
										ImageIO.write(((BufferedImage) object), "png", output);
									} finally {
										output.close();
									}
								}
							}
							url = url.trim();
							ClipboardUtil.setClipboard(url);
							history.addEntry(new HistoryEntry(url, uploader.getName()));
							icon.displayMessage("Upload complete",
									"Uploaded to " + url,
									TrayIcon.MessageType.INFO);
							logger.info("Upload completed, url: "+url);
							if(object instanceof BufferedImage) {
								((BufferedImage)object).flush();
							}
						} else {
							icon.displayMessage("Upload failed",
									"The upload failed to execute due to an unknown error",
									TrayIcon.MessageType.ERROR);
							logger.severe("Upload failed to execute due to an unknown error");
						}
					} catch (Exception e) {
						e.printStackTrace();
						icon.displayMessage("Upload failed",
								"The upload failed to execute: " + e,
								TrayIcon.MessageType.ERROR);
						logger.log(Level.SEVERE, "Upload failed to execute", e);
					}
				}
			}
		});
	}
	
	/**
	 * Get the local file for image archiving
	 * @param fileName
	 * 			The file name
	 * @return
	 * 			The constructed File object
	 */
	public File getLocalFile(String fileName) {
		File dir = new File(Util.getWorkingDirectory(), "images");
		if(!dir.exists()) {
			dir.mkdirs();
		}
		return new File(dir, fileName);
	}

	public boolean hasUploaderFor(Class<? extends Object> class1) {
		return uploaderAssociations.containsKey(class1);
	}
}

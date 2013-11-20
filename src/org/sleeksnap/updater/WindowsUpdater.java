package org.sleeksnap.updater;

import java.io.File;
import java.io.FileWriter;

import org.sleeksnap.util.WinRegistry;

/**
 * A utility class only used when Sleeksnap is installed by the NSIS installer
 * 
 * @author Nikki
 *
 */
public class WindowsUpdater {
	/**
	 * If on Windows and this was installed via an installer, then we need to
	 * update the shortcut file. This is no easy task, as there is no real
	 * 'easy' way to do it except via vbscript and exec.
	 * 
	 * The registry entry also isn't always where we need it, so we need to scan
	 * LOCAL_MACHINE AND CURRENT_USER
	 * 
	 * @param file
	 *            The file to update the path to
	 */
	public static void checkStartMenu(File file) throws Exception {
		String menuGroup = findStartMenuGroup();
		if (menuGroup != null) {
			String folder = WinRegistry.readString(WinRegistry.HKEY_CURRENT_USER, "Software\\Microsoft\\Windows\\CurrentVersion\\Explorer\\Shell Folders", "Programs");
			File folderPath = new File(folder + File.separatorChar + menuGroup);
			if (folderPath != null) {
				File shortcutFile = new File(folderPath, "Sleeksnap.lnk");

				if (shortcutFile.exists()) {
					String scriptContents = "Set sh = CreateObject(\"WScript.Shell\")\n";
					scriptContents += "Set shortcut = sh.CreateShortcut(\"" + shortcutFile.getAbsolutePath() + "\")\n";
					scriptContents += "shortcut.TargetPath = \"" + file.getAbsolutePath() + "\"\n";
					scriptContents += "shortcut.Save\n";

					File tmpScript = File.createTempFile("setshortcut", ".vbs");

					FileWriter writer = new FileWriter(tmpScript);
					try {
						writer.write(scriptContents);
					} finally {
						writer.close();
					}

					try {
						Runtime.getRuntime().exec(new String[] { "cscript", tmpScript.getAbsolutePath() });
					} finally {
						if (!tmpScript.delete())
							tmpScript.deleteOnExit();
					}
				}
			}
		}
	}

	/**
	 * Scan the registry for the start menu group
	 * 
	 * @return The menu group name
	 * @throws Exception
	 */
	private static String findStartMenuGroup() throws Exception {
		String[] locations = new String[] { "Software\\Sleeksnap", "Software\\Wow6432Node\\Sleeksnap" };

		String menuGroup = null;
		for (String s : locations) {
			menuGroup = WinRegistry.readString(WinRegistry.HKEY_CURRENT_USER, s, "StartMenuGroup");
			if (menuGroup != null) {
				break;
			}
			menuGroup = WinRegistry.readString(WinRegistry.HKEY_LOCAL_MACHINE, s, "StartMenuGroup");
			if (menuGroup != null) {
				break;
			}
		}

		return menuGroup;
	}
}

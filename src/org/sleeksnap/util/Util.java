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
package org.sleeksnap.util;

import java.io.File;
import java.net.URL;
import java.util.HashMap;

import org.sleeksnap.Constants;
import org.sleeksnap.Constants.Application;

/**
 * A basic utility class
 * 
 * @author Nikki
 * 
 */
public class Util {
	/**
	 * An enum containing operating system types
	 * 
	 * @author Nikki
	 * 
	 */
	public static enum OperatingSystem {
		LINUX, SOLARIS, WINDOWS, MAC, UNKNOWN
	}

	/**
	 * The cached operating system
	 */
	public static final OperatingSystem SYSTEM = getPlatform();

	/**
	 * The saved working directory
	 */
	private static File workDir;

	/**
	 * A method to get the unix time...
	 * @return
	 */
	public static long currentTimeSeconds() {
		return (System.currentTimeMillis() / 1000);
	}

	/**
	 * Get the last part of a class name
	 * 
	 * @param key
	 *            The full name
	 * @return The class name formatted
	 */
	public static String formatClassName(Class<?> key) {
		return key.getName().substring(key.getName().lastIndexOf('.') + 1);
	}

	/**
	 * Get the computer's user agent
	 * 
	 */
	public static String getHttpUserAgent() {
		return Application.NAME + " v" + Application.VERSION;
	}

	/**
	 * Get the current platform
	 * 
	 * @return The current platform
	 */
	public static OperatingSystem getPlatform() {
		String osName = System.getProperty("os.name").toLowerCase();
		if (osName.contains("win"))
			return OperatingSystem.WINDOWS;
		if (osName.contains("mac"))
			return OperatingSystem.MAC;
		if (osName.contains("solaris"))
			return OperatingSystem.SOLARIS;
		if (osName.contains("sunos"))
			return OperatingSystem.SOLARIS;
		if (osName.contains("linux"))
			return OperatingSystem.LINUX;
		if (osName.contains("unix"))
			return OperatingSystem.LINUX;
		return OperatingSystem.UNKNOWN;
	}

	/**
	 * Get a resource, allows us to run it from source or jar
	 * 
	 * @param name
	 *            The resource name
	 * @return The URL of the resource, either in-jar or on the filesystem
	 */
	public static URL getResourceByName(String name) {
		if (Utils.class.getResource(name) != null) {
			return Utils.class.getResource(name);
		} else {
			File file = new File("resources" + name);
			if (file.exists()) {
				try {
					return file.toURI().toURL();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				System.out.println("File does not exist : "
						+ file.getAbsolutePath());
			}
		}
		return null;
	}

	/**
	 * Get the system architecture
	 * 
	 * @return The system architecture integer
	 */
	public static int getSystemArch() {
		String archs = System.getProperty("os.arch");
		return Integer.parseInt(archs.substring(archs.length() - 2));
	}

	/**
	 * Get the current working directory
	 * 
	 * @return The working directory for the application
	 */
	public static File getWorkingDirectory() {
		if (workDir == null)
			workDir = getWorkingDirectory(Constants.Application.NAME
					.toLowerCase());
		return workDir;
	}

	/**
	 * Get the AppData directory
	 * 
	 * @param applicationName
	 *            The application name
	 * @return The working directory
	 */
	public static File getWorkingDirectory(String applicationName) {
		String userHome = System.getProperty("user.home", ".");
		File workingDirectory;
		switch (getPlatform()) {
		case LINUX:
		case SOLARIS:
			File config = new File(userHome, ".config");
			if(config.exists()) {
				workingDirectory = new File(config, applicationName + '/');
				workingDirectory.mkdirs();
			} else {
				workingDirectory = new File(userHome, '.' + applicationName + '/');
			}
			break;
		case WINDOWS:
			String applicationData = System.getenv("APPDATA");
			if (applicationData != null)
				workingDirectory = new File(applicationData, "."
						+ applicationName + '/');
			else
				workingDirectory = new File(userHome,
						'.' + applicationName + '/');
			break;
		case MAC:
			workingDirectory = new File(userHome,
					"Library/Application Support/" + applicationName);
			break;
		default:
			workingDirectory = new File(userHome, applicationName + '/');
			break;
		}
		if ((!workingDirectory.exists()) && (!workingDirectory.mkdirs()))
			throw new RuntimeException(
					"The working directory could not be created: "
							+ workingDirectory);
		return workingDirectory;
	}

	/**
	 * Parse commandline arguments
	 * 
	 * @param args
	 *            The arg array from the main method, or manual args
	 * @return The map containing the args
	 */
	public static HashMap<String, Object> parseArguments(String[] args) {
		HashMap<String, Object> arguments = new HashMap<String, Object>();
		for (String s : args) {
			if (s.startsWith("--")) {
				s = s.substring(2);
			}
			int eqIdx = s.indexOf('=');
			String key = s.substring(0, eqIdx != -1 ? eqIdx : s.length());
			Object value = true;
			if (eqIdx != -1) {
				value = s.substring(s.indexOf('=') + 1);
			}
			arguments.put(key, value);
		}
		return arguments;
	}

	/**
	 * Set the working directory, used when Sleeksnap is self-contained (Uses a
	 * directory which you can have on a flash drive)
	 * 
	 * @param workingDirectory
	 *            The directory
	 */
	public static void setWorkingDirectory(File workingDirectory) {
		workDir = workingDirectory;
	}

	/**
	 * Trim the string's start/finish of the specified character
	 * 
	 * @param str
	 *            The string
	 * @param ch
	 *            The character
	 * @return The trimmed string
	 */
	public static String trim(String str, final char ch) {
		if ((str == null) || str.isEmpty())
			return str;
		else if (str.length() == 1)
			return str.charAt(0) == ch ? "" : str;
		try {
			if (str.charAt(0) == ch)
				str = str.substring(1);
			final int l = str.length() - 1;
			if (str.charAt(l) == ch)
				str = str.substring(0, l);
			return str;
		} catch (final Exception e) {
			return str;
		}
	}

	/**
	 * Make each word in a string uppercase
	 * 
	 * @param string
	 *            The string to parse
	 * @return The formatted string
	 */
	public static String ucwords(String string) {
		StringBuilder out = new StringBuilder();
		String[] split = string.split(" ");
		for (int i = 0; i < split.length; i++) {
			String str = split[i];
			out.append(Character.toUpperCase(str.charAt(0)));
			if (str.length() > 1) {
				out.append(str.substring(1).toLowerCase());
			}
			if (i < (split.length - 1)) {
				out.append(" ");
			}
		}
		return out.toString();
	}
}

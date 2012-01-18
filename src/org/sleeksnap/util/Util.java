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
	 * Get the last part of a class name
	 * @param key
	 * 			The full name
	 * @return
	 * 			The class name formatted
	 */
	public static String formatClassName(Class<?> key) {
		return key.getName().substring(key.getName().lastIndexOf('.') + 1);
	}

	/**
	 * Get the computer's user agent
	 * 
	 */
	public static String getHttpUserAgent() {
		return Application.NAME+" v"+Application.VERSION;
	}

	/**
	 * Get the current platform
	 * @return
	 * 		The current platform
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
	 * Get the system architecture
	 * @return
	 * 		The system architecture integer
	 */
	public static int getSystemArch() {
		String archs = System.getProperty("os.arch");
		return Integer.parseInt(archs.substring(archs.length() - 2));
	}

	/**
	 * Get the current working directory
	 * @return
	 * 			The working directory for the application
	 */
	public static File getWorkingDirectory() {
		if (workDir == null)
			workDir = getWorkingDirectory(Constants.Application.NAME.toLowerCase());
		return workDir;
	}

	/**
	 * Get the AppData directory
	 * @param applicationName
	 * 			The application name
	 * @return
	 * 			The working directory
	 */
	public static File getWorkingDirectory(String applicationName) {
		String userHome = System.getProperty("user.home", ".");
		File workingDirectory;
		switch (getPlatform()) {
		case LINUX:
		case SOLARIS:
			workingDirectory = new File(userHome, '.' + applicationName + '/');
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
	
	public static HashMap<Object, Object> parseJSON(String json) {
		HashMap<Object, Object> map = new HashMap<Object, Object>();
		String[] split = json.split(",");
		for(int i = 0; i < split.length; i++) {
			String line = trim(split[i], '{');
			line = trim(line, '}');
			line = line.trim();
			if(line.contains(":")) {
				String key = trim(line.substring(0, line.indexOf(':')), '"');
				System.out.println("Key:"+line.substring(0, line.indexOf(':')));
				String value = line.substring(line.indexOf(':')+1).trim();
				if(value.charAt(0) == '"') {
					value = value.substring(1, value.length()-1);
					map.put(key, value);
				} else {
					Class<?> cast = Integer.class;
					if(value.equals("true") || value.equals("false")) {
						cast = Boolean.class;
					}
					map.put(key, cast.cast(value));
				}
			}
		}
		return map;
	}
	
	/**
	 * Trim the string's start/finish of the specified character
	 * @param str
	 * 			The string
	 * @param ch
	 * 			The character
	 * @return
	 * 			The trimmed string
	 */
	public static String trim(String str, final char ch) {
		if ((str == null) || str.isEmpty())
			return str;
		else if (str.length() == 1)
			return str.charAt(0) == ch ? "" : str;
		try {
			if(str.charAt(0) == ch)
				str = str.substring(1);
			final int l = str.length() - 1;
			if (str.charAt(l) == ch)
				str = str.substring(0, l);
			return str;
		} catch (final Exception e) {
			return str;
		}
	}
	
	public static String ucwords(String string) {
		StringBuilder out = new StringBuilder();
		String[] split = string.split(" ");
		for(int i = 0; i < split.length; i++) {
			String str = split[i];
			out.append(Character.toUpperCase(str.charAt(0)));
			if(str.length() > 1) {
				out.append(str.substring(1).toLowerCase());
			}
			if(i < (split.length-1)) {
				out.append(" ");
			}
		}
		return out.toString();
	}
	
	public static URL getResourceByName(String name) {
		if(Utils.class.getResource(name) != null) {
			return Utils.class.getResource(name);
		} else {
			File file = new File("resources"+name);
			if(file.exists()) {
				try {
					return file.toURI().toURL();
				} catch(Exception e) {
					e.printStackTrace();
				}
			} else {
				System.out.println("File does not exist : "+file.getAbsolutePath());
			}
		}
		return null;
	}
	
	public static long currentTimeSeconds() {
		return (System.currentTimeMillis()/1000);
	}
}

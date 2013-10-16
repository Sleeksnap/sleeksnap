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

/**
 * Contains constant names for the configuration file and misc settings
 * 
 * @author Nikki
 * 
 */
public class Constants {
	public static class Application {
		public static final String NAME = "Sleeksnap";
		public static final String UPDATE_URL = "http://sleeksnap.com/build/latest.json";
	}

	public static class Configuration {
		public static final String FILE_NAME = Application.NAME.toLowerCase()
				+ ".conf";
	}

	public static class Resources {
		public static final String LOGO_PATH = "/logo.png";
		public static final String ICON_PATH = "/icon.png";
	}
	
	public static class Version {
		public static final int MAJOR = 1;
		public static final int MINOR = 3;
		public static final int PATCH = 6;
		
		private static String versionString = null;
		
		public static String getVersionString() {
			if(versionString == null) {
				versionString = MAJOR + "." + MINOR + "." + PATCH;
			}
			return  versionString;
		}
	}
}

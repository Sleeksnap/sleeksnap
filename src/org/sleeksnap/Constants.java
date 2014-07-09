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

import java.awt.Image;
import java.awt.Toolkit;
import java.net.URL;

import org.sleeksnap.util.Util;

/**
 * Contains constant names for the configuration file and misc settings
 * 
 * @author Nikki
 * 
 */
public class Constants {
	public static class Application {
		public static final String NAME = "Sleeksnap";
		public static final String URL = "http://sleeksnap.com";
		public static final String UPDATE_URL = "http://sleeksnap.com/build/";
	}

	public static class Configuration {
		public static final String FILE_NAME = Application.NAME.toLowerCase()
				+ ".conf";

		public static final int DEFAULT_MAX_RETRIES = 3;
		
		public static final String DEFAULT_LANGUAGE = "english";
	}

	public static class Resources {
		public static final String LOGO_PATH = "/logo.png";
		public static final String ICON_PATH = "/icon.png";
		public static final String ICON_BUSY_PATH = "/icon-busy.png";
		
		public static final URL ICON = Util.getResourceByName(Resources.ICON_PATH);
		public static final URL ICON_BUSY = Util.getResourceByName(Resources.ICON_BUSY_PATH);
		
		public static final Image ICON_IMAGE = Toolkit.getDefaultToolkit().getImage(ICON);
		public static final Image ICON_BUSY_IMAGE = Toolkit.getDefaultToolkit().getImage(ICON_BUSY);
	}
	
	public static class Version {
		public static final int MAJOR = 1;
		public static final int MINOR = 4;
		public static final int PATCH = 6;
		
		private static String versionString = null;
		
		public static String getVersionString() {
			if(versionString == null) {
				// Construct string normally
				versionString = MAJOR + "." + MINOR + "." + PATCH;
			}
			return  versionString;
		}
	}
}

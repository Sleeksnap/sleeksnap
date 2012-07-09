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
package org.sleeksnap.util.active;

import org.sleeksnap.util.active.linux.GnomeWindowUtil;
import org.sleeksnap.util.active.linux.XPropWindowUtil;

import com.sun.jna.Platform;

/**
 * A simple class which contains a cached WindowUtil and a method to set it/get
 * it
 * 
 * @author Nikki
 * 
 */
public class WindowUtilProvider {
	/**
	 * The WindowUtil currently in use
	 */
	private static WindowUtil cachedUtil = null;

	/**
	 * Get the current WindowUtil
	 * 
	 * @return The WindowUtil for the OS
	 */
	public static WindowUtil getWindowUtil() {
		if (cachedUtil == null) {
			if (Platform.isWindows()) {
				cachedUtil = new Win32WindowUtil();
			} else if (Platform.isLinux()) {
				if (GnomeWindowUtil.isValid()) {
					cachedUtil = new GnomeWindowUtil();
				} else {
					cachedUtil = new XPropWindowUtil();
				}
			}
		}
		return cachedUtil;
	}
}

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

/**
 * An interface which declares methods to get the active window
 * 
 * @author Nikki
 *
 */
public interface WindowUtil {
	
	/**
	 * Get the active window
	 * @return
	 * 			The active desktop window
	 * @throws Exception
	 * 			If we failed to get the window
	 */
	public ActiveWindow getActiveWindow() throws Exception;
}

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
package org.sleeksnap.impl;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;

import org.sleeksnap.util.logging.FileLogHandler;
import org.sleeksnap.util.logging.LogPanelHandler;

/**
 * A class to load the logging properties of this application, allows the use of
 * file logging for the Log panel
 * 
 * @author Nikki
 * 
 */
public class LoggingManager {
	
	/**
	 * Load the configuration from a byte array
	 */
	@SuppressWarnings("unchecked")
	public static void configure() {
		try {
			LoggerConfiguration config = new LoggerConfiguration();
			config.addHandlers(ConsoleHandler.class, FileLogHandler.class, LogPanelHandler.class);
			config.setLevel(Level.INFO);
			config.apply();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

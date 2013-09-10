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
package org.sleeksnap.util.logging;

import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

import org.sleeksnap.gui.options.LogPanel;

/**
 * A simple java logging handler to log to the Log Panel in the settings gui when it is open.
 * 
 * @author Nikki
 * 
 */
public class LogPanelHandler extends java.util.logging.Handler {

	private static LogPanel logPanel;

	/**
	 * A simple file logger!
	 */
	public LogPanelHandler() {
	}

	@Override
	public void publish(LogRecord record) {
		if(logPanel == null) {
			return;
		}
		if (getFormatter() == null)
			setFormatter(new SimpleFormatter());
		
		if (!isLoggable(record))
			return;
		
		logPanel.appendLog(getFormatter().format(record));
	}

	@Override
	public void flush() {
	}

	@Override
	public void close() throws SecurityException {
	}

	/**
	 * Bind this logging handler to a LogPanel instance to update the log screen
	 * @param logPanel
	 * 			The panel to bind
	 */
	public static void bindTo(LogPanel logPanel) {
		LogPanelHandler.logPanel = logPanel;
	}

	/**
	 * Unbind the LogPanel instance (Always done when the settings gui is closed)
	 */
	public static void unbind() {
		LogPanelHandler.logPanel = null;
	}
}
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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

/**
 * A simple java logging handler to log to a BufferedWriter, the file is retained after application shutdown for debugging purposes
 * 
 * @author Nikki
 *
 */
public class FileLogHandler extends java.util.logging.Handler {

	/**
	 * The buffered writer for this logger
	 */
	private BufferedWriter writer;

	/**
	 * A simple file logger!
	 */
	public FileLogHandler() {
		configure();
	}

	/**
	 * Configure the logger
	 */
	private void configure() {
		File file = new File(Util.getWorkingDirectory(), "log.txt");
		try {
			writer = new BufferedWriter(new FileWriter(file));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void publish(LogRecord record) {
		if (getFormatter() == null)
			setFormatter(new SimpleFormatter());
		if (!isLoggable(record)) {
			return;
		}
		try {
			writer.write(getFormatter().format(record));
		} catch (IOException e) {
			e.printStackTrace();
		}
		flush();
	}

	@Override
	public void flush() {
		try {
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void close() throws SecurityException {
		try {
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
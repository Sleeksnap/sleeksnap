package org.sleeksnap.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

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
		file.deleteOnExit();
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
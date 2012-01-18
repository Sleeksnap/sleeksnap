package org.sleeksnap.impl;

import java.util.logging.LogManager;

import org.sleeksnap.util.Util;

public class LoggingManager {
	public static void configure() {
		try {
			LogManager.getLogManager().readConfiguration(Util.getResourceByName("/logging.props").openStream());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

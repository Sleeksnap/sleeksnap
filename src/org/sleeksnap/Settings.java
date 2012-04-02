package org.sleeksnap;

/**
 * Contains constant names for the configuration file and misc settings
 * 
 * @author Nikki
 *
 */
public class Settings {
	public static class Application {
		public static final String NAME = "Sleeksnap";
		public static final double VERSION = 1.0;
	}

	public static class Configuration {
		public static final String FILE_NAME = Application.NAME.toLowerCase()+".conf";
	}
	
	public static class Resources {
		public static final String LOGO_PATH = "/logo.png";
		public static final String ICON_PATH = "/icon.png";
	}
}

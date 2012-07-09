package tray;

import tray.java.JavaTrayAdapter;
import tray.linux.LinuxNativeTrayAdapter;

public class SystemTrayProvider {

	public static SystemTrayAdapter getSystemTray() {
		String osName = System.getProperty("os.name").toLowerCase();
		if (osName.startsWith("linux")) {
			try {
				return new LinuxNativeTrayAdapter();
			} catch (Exception e) {

			}
		}
		return new JavaTrayAdapter();
	}
}

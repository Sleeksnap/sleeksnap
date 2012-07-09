package tray.java;

import java.awt.AWTException;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.net.URL;

import tray.SystemTrayAdapter;
import tray.TrayIconAdapter;

public class JavaTrayAdapter implements SystemTrayAdapter {

	SystemTray systemTray = SystemTray.getSystemTray();

	@Override
	public TrayIconAdapter createAndAddTrayIcon(URL imageURL, String tooltip,
			PopupMenu popup) {
		JavaIconAdapter javaIconAdapter = new JavaIconAdapter(imageURL,
				tooltip, popup);
		add(javaIconAdapter);
		return javaIconAdapter;
	}

	private void add(TrayIconAdapter trayIcon) {
		JavaIconAdapter adapter = (JavaIconAdapter) trayIcon;
		try {
			systemTray.add(adapter.getTrayIcon());
		} catch (AWTException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void remove(TrayIconAdapter trayIcon) {
		JavaIconAdapter javaIconAdapter = (JavaIconAdapter) trayIcon;
		systemTray.remove(javaIconAdapter.getTrayIcon());
	}
}

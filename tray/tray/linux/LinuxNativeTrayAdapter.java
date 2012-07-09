package tray.linux;

import java.awt.PopupMenu;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

import tray.SystemTrayAdapter;
import tray.TrayIconAdapter;

public class LinuxNativeTrayAdapter implements SystemTrayAdapter {
	private static NativeLinuxTray nativeTray;
	static Map<Integer, LinuxTrayIconAdapter> trayIconInstances = new LinkedHashMap<Integer, LinuxTrayIconAdapter>();

	public LinuxNativeTrayAdapter() {
		loadNativeLibraryIfNotAlreadyLoaded();
	}

	private void loadNativeLibraryIfNotAlreadyLoaded() {
		if (nativeTray == null) {
			nativeTray = new NativeLinuxTray();
		}
	}

	@Override
	public TrayIconAdapter createAndAddTrayIcon(URL imageURL, String tooltip,
			PopupMenu popup) {
		LinuxTrayIconAdapter linuxTrayIconAdapter = new LinuxTrayIconAdapter(
				nativeTray, imageURL, tooltip, popup);
		trayIconInstances.put(linuxTrayIconAdapter.getNativeId(),
				linuxTrayIconAdapter);
		return linuxTrayIconAdapter;
	}

	public static LinuxTrayIconAdapter getLinuxTrayIconAdapter(int nativeId) {
		return trayIconInstances.get(nativeId);
	}

	@Override
	public void remove(TrayIconAdapter trayIcon) {
		LinuxTrayIconAdapter linuxTrayIconAdapter = (LinuxTrayIconAdapter) trayIcon;
		trayIconInstances.remove(linuxTrayIconAdapter.getNativeId());
		linuxTrayIconAdapter.removeMe();
	}
}
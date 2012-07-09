package tray;

import java.awt.PopupMenu;
import java.net.URL;

public interface SystemTrayAdapter {

	TrayIconAdapter createAndAddTrayIcon(URL imageUrl, String tooltip,
			PopupMenu popup);

	void remove(TrayIconAdapter trayIcon);

}

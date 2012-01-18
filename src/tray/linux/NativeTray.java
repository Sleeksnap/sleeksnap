package tray.linux;

import java.awt.TrayIcon.MessageType;

public interface NativeTray {

	int nativeCreateTrayIcon(String file, String tooltip);

	void nativeAddMenuItem(int nativeId, int menuItemIndex, String label);

	void nativeDisplayMessage(int nativeId, String title, String caption,
			MessageType info);

	void nativeSetAutosize(int nativeId, boolean autosize);

	void nativeRemoveMe(int nativeId);

	void nativeSetImage(int nativeId, String file);
}

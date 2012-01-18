package tray.linux;

import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.sleeksnap.util.StreamUtils;

import tray.TrayIconAdapter;

public class LinuxTrayIconAdapter implements TrayIconAdapter,
		TrayMethodsThatAreInvokedByTheNativeCounterpart {

	private final NativeTray nativeTray;
	private final PopupMenu popup;
	private final String tooltip;
	private int nativeId;
	private List<ActionListener> actionListeners = new ArrayList<ActionListener>();

	public LinuxTrayIconAdapter(NativeTray nativeTray, URL imageURL,
			String tooltip, PopupMenu popup) {
		this.nativeTray = nativeTray;
		this.tooltip = tooltip;
		this.popup = popup;
		setupNativeTrayIcon(imageURL);
	}

	public int getNativeId() {
		return nativeId;
	}

	@Override
	public void displayMessage(String title, String text, MessageType info) {
		nativeTray.nativeDisplayMessage(nativeId, title, text, info);
	}

	@Override
	public void setImageAutoSize(boolean autosize) {
		nativeTray.nativeSetAutosize(nativeId, autosize);
	}

	@Override
	public void setImage(URL imageUrl) {
		final URL existingFileUrl = makeSureUrlPointsToExistingFile(imageUrl);
		nativeTray.nativeSetImage(nativeId, existingFileUrl.getFile());
	}

	@Override
	public void addActionListener(ActionListener actionListener) {
		actionListeners.add(actionListener);
	}

	@Override
	public void fireActionActivated() {
		ActionEvent e = new ActionEvent(this, 0, "activate");
		for (ActionListener anActionListener : this.actionListeners) {
			anActionListener.actionPerformed(e);
		}
	}

	@Override
	public void fireMenuAction(int menuItemIndex) {
		MenuItem item = this.popup.getItem(menuItemIndex);
		ActionListener[] actionListeners = item.getActionListeners();
		ActionEvent e = new ActionEvent(item, 0, item.getActionCommand());
		for (ActionListener actionListener : actionListeners) {
			actionListener.actionPerformed(e);
		}
	}

	private URL makeSureUrlPointsToExistingFile(URL imageURL) {
		File imageFile = new File(imageURL.getFile());
		if (imageFile.exists())
			return imageURL;
		try {
			imageFile = StreamUtils.getStreamAsTempFileOrCry(
					imageURL.openStream(), "trayIconRehydratedImage");
			URL extractedImageFileUrl = imageFile.toURI().toURL();
			return extractedImageFileUrl;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void setupNativeTrayIcon(URL imageURL) {
		final URL existingFileUrl = makeSureUrlPointsToExistingFile(imageURL);
		nativeId = nativeTray.nativeCreateTrayIcon(existingFileUrl.getFile(),
				tooltip);
		populateNativeMenuListeners(popup);
	}

	private void populateNativeMenuListeners(PopupMenu popup) {
		int i;
		for (i = 0; i < popup.getItemCount(); i++) {
			MenuItem item = popup.getItem(i);
			nativeTray.nativeAddMenuItem(nativeId, i, item.getLabel());
		}

	}

	public void removeMe() {
		nativeTray.nativeRemoveMe(nativeId);
	}
}
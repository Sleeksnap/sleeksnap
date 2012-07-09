package tray.linux;

import java.awt.Point;
import java.awt.TrayIcon.MessageType;

import org.sleeksnap.util.JniUtils;

import tray.balloon.Balloon;

class NativeLinuxTray implements NativeTray {

	public NativeLinuxTray() {
		String libraryName = String.format("linuxtray_%s", getArchSuffix());
		JniUtils.loadLibrary(libraryName);
		nativeInit0();
	}

	private String getArchSuffix() {
		if (System.getProperty("os.arch").contains("64"))
			return "x64";
		return "x86";
	}

	@Override
	public int nativeCreateTrayIcon(String file, String tooltip) {
		return nativeCreateTrayIcon0(file, tooltip);
	}

	@Override
	public void nativeAddMenuItem(int nativeId, int menuItemIndex,
			String caption) {
		nativeAddMenuItem0(nativeId, menuItemIndex, caption);
	}

	public void nativeSetAutosize(int nativeId, boolean autosize) {
		// gtk is always autosize
	}

	@Override
	public void nativeDisplayMessage(int nativeId, final String caption,
			final String text, final MessageType messageType) {
		Point loc = nativeGetIconLocation0(nativeId);
		final Balloon balloon = new Balloon();
		balloon.setBounds(loc.x + 12, loc.y + 12, 1, 1);
		balloon.display(caption, text, messageType);
	}

	@Override
	public void nativeSetImage(int nativeId, String file) {
		this.nativeSetImage0(nativeId, file);
	}

	@Override
	public void nativeRemoveMe(int nativeId) {
		this.nativeRemoveMe0(nativeId);
	}

	private native void nativeInit0();

	private native int nativeCreateTrayIcon0(String file, String tooltip);

	private native void nativeRemoveMe0(int nativeId);

	private native void nativeAddMenuItem0(int nativeId, int menuItemIndex,
			String caption);

	private native void nativeSetImage0(int nativeId, String imageFileName);

	private native Point nativeGetIconLocation0(int nativeId);

}

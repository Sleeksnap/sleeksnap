package org.sleeksnap.util;

import java.awt.Rectangle;

import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import com.sun.jna.NativeLong;
import com.sun.jna.win32.W32APIOptions;

/**
 * A utility using JNA to get the active foreground window on Windows
 * 
 * @author Nikki
 * 
 */
public class Win32WindowUtil {
	static {
		Native.register(NativeLibrary.getInstance("user32",
				W32APIOptions.DEFAULT_OPTIONS));
	}

	/**
	 * Native function for GetForegroundWindow()
	 * 
	 * @return The NativeLong pointer of the window
	 */
	private static native NativeLong GetForegroundWindow();

	/**
	 * Get the window rectangle
	 * 
	 * @param pointer
	 *            The pointer from GetForegroundWindow()
	 * @param rect
	 *            The corner positions of the window
	 * @return ?
	 */
	private static native boolean GetWindowRect(NativeLong pointer, int[] rect);

	/**
	 * Get the window title
	 * 
	 * @param nativeLong
	 *            The pointer to the window
	 * @param nativeString
	 *            The byte array to store the result in
	 * @param nMaxCount
	 *            The max length count
	 * @return ?
	 */
	private static native int GetWindowTextA(NativeLong nativeLong,
			byte[] nativeString, int nMaxCount);

	/**
	 * Get the active window
	 * 
	 * @return A new ActiveWindow object with the name and window information
	 * @throws Exception
	 *             If a problem occurred finding the window
	 */
	public static ActiveWindow getActiveWindow() throws Exception {
		// Get the foreground window as a NativeLong pointer (Doesn't need the
		// jna platform binary)
		NativeLong pointer = GetForegroundWindow();
		// If it's null, throw an exception
		if (pointer == null) {
			throw new Exception("Unable to find active window");
		}
		// Initialize a new array for window title
		byte[] nameBytes = new byte[1024];
		// Perform the native GetWindowText function
		GetWindowTextA(pointer, nameBytes, nameBytes.length);
		// Get the window size (indexes: 0 = left, 1 = top, 2 = right, 3 =
		// bottom)
		int[] rect = new int[4];
		GetWindowRect(pointer, rect);
		// Check if fullscreen (We should shave off part of the sides if it is)
		if (rect[0] < 0) {
			rect[2] = rect[2] + rect[0];
			rect[0] = 0;
		}
		if (rect[1] < 0) {
			rect[3] = rect[3] + rect[1];
			rect[1] = 0;
		}
		// Return the window
		return new ActiveWindow(Native.toString(nameBytes), new Rectangle(
				rect[0], rect[1], rect[2] - rect[0], rect[3] - rect[1]));
	}

	public static class ActiveWindow {
		public String getName() {
			return name;
		}

		public Rectangle getBounds() {
			return bounds;
		}

		private String name;

		private Rectangle bounds;

		public ActiveWindow(String name, Rectangle bounds) {
			this.name = name;
			this.bounds = bounds;
		}
	}
}

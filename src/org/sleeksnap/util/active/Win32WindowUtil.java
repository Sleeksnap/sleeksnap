/**
 * Sleeksnap, the open source cross-platform screenshot uploader
 * Copyright (C) 2012 Nikki <nikki@nikkii.us>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sleeksnap.util.active;

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
public class Win32WindowUtil implements WindowUtil {
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
	@Override
	public ActiveWindow getActiveWindow() throws Exception {
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
}

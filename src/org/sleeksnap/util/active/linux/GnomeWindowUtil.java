/**
 * Sleeksnap, the open source cross-platform screenshot uploader
 * Copyright (C) 2012 Nicole Schuiteman
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
package org.sleeksnap.util.active.linux;

import org.sleeksnap.util.active.ActiveWindow;
import org.sleeksnap.util.active.WindowUtil;
import org.sleeksnap.util.active.linux.Gtk.GdkRectangle;

import com.sun.jna.Native;
import com.sun.jna.NativeLong;

/**
 * Gets the active window by using Gtk/Gdk functions
 * This method does not return a window title, but is still preferred over XPropWindowUtil since it will get the window location including the title bar
 * 
 * @author Nikki
 *
 */
public class GnomeWindowUtil implements WindowUtil {
	
	private static Gtk gtk;

	@Override
	public ActiveWindow getActiveWindow() throws Exception {
		if(gtk == null) {
			throw new Exception("Gtk library not loaded!");
		}
		gtk.gtk_init(null, null);
		//Display and window are pointers...
		NativeLong display = gtk.gdk_screen_get_default();
		if(display == null) {
			throw new Exception("Unable to find the default screen");
		}
		//Try to get the active window
		NativeLong window = gtk.gdk_screen_get_active_window(display);
		//If not, try to get the window under the cursor
		if(window == null) {
			window = gtk.gdk_window_at_pointer(null, null);
		}
		//If we didn't find a window, throw an exception
		if(window == null) {
			throw new Exception("Unable to get the active window!");
		}
		//Get the frame bounds as a GdkRectangle, why not a structure since we'd get it in an int[] the other way
		GdkRectangle rect = new GdkRectangle();
		gtk.gdk_window_get_frame_extents(window, rect);
		//We won't know the name, but it's not implemented in anything so no big deal
		return new ActiveWindow(null, rect.toRectangle());
	}

	/**
	 * Check whether this windowutil will be valid
	 * @return
	 * 		True if the GTK library was loaded
	 */
	public static boolean isValid() {
		try {
			gtk = (Gtk) Native.loadLibrary("gtk-x11-2.0", Gtk.class);
			return true;
		} catch(Exception e) {
			//We return false later
		}
		return false;
	}
}

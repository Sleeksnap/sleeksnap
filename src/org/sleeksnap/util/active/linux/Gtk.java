
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
 */package org.sleeksnap.util.active.linux;

import java.awt.Rectangle;

import com.sun.jna.Library;
import com.sun.jna.NativeLong;
import com.sun.jna.Structure;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

/**
 * A wrapper for the Gtk library
 * Note: When using,  you MUST call gtk_init(null, null)
 * 
 * @author Nikki
 *
 */
public interface Gtk extends Library {
	
	/**
	 * Initialize the GTK instance
	 * @param argc
	 * 			Argument count
	 * @param argv
	 * 			Arguments
	 */
	void gtk_init(IntByReference argc, PointerByReference argv);
	
	
	//The following are gdk methods
	
	/**
	 * A rectangle to represent the GdkWindow bounds
	 * @author Nikki
	 */
	public static class GdkRectangle extends Structure {
        public int x;
        public int y;
        public int width;
        public int height;
        
        public Rectangle toRectangle() {
        	Rectangle out = new Rectangle(x, y, width, height);
    		if(out.x < 0) {
    			out.width = out.width + out.x;
    			out.x = 0;
    		}
    		if(out.y < 0) {
    			out.height = out.height + out.y;
    			out.y = 0;
    		}
    		return out;
        }
    }
    
	/**
	 * Gets a pointer to the default screen object
	 * @return
	 * 		The pointer in the JNA NativeLong format
	 */
    public NativeLong gdk_screen_get_default();
    
    /**
     * Gets the active window's pointer
     * @param display
     * 			The default screen, found by gdk_screen_get_default()
     * @return
     * 		The pointer in the JNA NativeLong format
     */
    public NativeLong gdk_screen_get_active_window(NativeLong display);
    
    /**
     * Get the frame bounds
     * @param window
     * 			The window pointer
     * @param extents
     * 			The rectangle to write the bounds to
     */
    public void gdk_window_get_frame_extents(NativeLong window, GdkRectangle extents);

    /**
     * Get the gdk window that the cursor is in
     * @param object
     * @param object2
     * @return
     */
	public NativeLong gdk_window_at_pointer(IntByReference winx, IntByReference winy);

	/**
	 * Get the toplevel window ancestor
	 * @param window
	 * 			The NativeLong pointer of the window parent
	 * @return
	 * 			The NativeLong pointer of the top level window
	 */
	public NativeLong gdk_window_get_toplevel(NativeLong window);
}

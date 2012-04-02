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
package org.sleeksnap.util;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;

import org.sleeksnap.util.Utils.DisplayUtil;

/**
 * A basic screenshot utility
 * 
 * @author Nikki
 *
 */
public class ScreenshotUtil {
	
	/**
	 * The robot instance
	 */
	private static Robot robot;

	/**
	 * Initialize it..
	 */
	static {
		try {
			robot = new Robot();
		} catch (AWTException e) {
			
		}
	}

	/**
	 * Capture a simple screenshot
	 * @return
	 * 		The screenshot
	 */
	public static BufferedImage capture() {
		return capture(DisplayUtil.getRealScreenSize());
	}

	/**
	 * Capture a screenshot
	 * @param d
	 * 			The dimensions of the area, starts at 0,0
	 * @return
	 * 			The captured image
	 */
	public static BufferedImage capture(Dimension d) {
		return capture(new Rectangle(0, 0, d.width, d.height));
	}
	
	/**
	 * Capture a regular screenshot, a static method for robot.createScreenCapture
	 * @param rectangle
	 * 			Rect to capture in screen coordinates
	 * @return
	 * 			The captured image
	 */
	public static BufferedImage capture(Rectangle rectangle) {
		return robot.createScreenCapture(rectangle);
	}
}

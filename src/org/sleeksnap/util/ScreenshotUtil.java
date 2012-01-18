package org.sleeksnap.util;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

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
			e.printStackTrace();
		}
	}

	/**
	 * Capture a simple screenshot
	 * @return
	 * 		The screenshot
	 */
	public static BufferedImage capture() {
		return capture(Toolkit.getDefaultToolkit().getScreenSize());
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

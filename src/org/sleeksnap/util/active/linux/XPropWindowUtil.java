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
package org.sleeksnap.util.active.linux;

import java.awt.Rectangle;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.sleeksnap.util.StreamUtils;
import org.sleeksnap.util.active.ActiveWindow;
import org.sleeksnap.util.active.WindowUtil;

/**
 * A WindowUtil that uses xprop/xwininfo to get the active window bounds. Note:
 * This does not include the title bar, which is why GnomeWindowUtil is
 * preferred... However, GnomeWindowUtil does not have the actual window title,
 * however it is currently not used.
 * 
 * @author Nikki
 * 
 */
public class XPropWindowUtil implements WindowUtil {

	/**
	 * The pattern to match width/height variables
	 */
	private static Pattern pattern = Pattern
			.compile("Width:\\s*(\\d+)\\s*Height:\\s*(\\d+)");

	/**
	 * The pattern to match the window location (Does not include title bar)
	 */
	private static Pattern locationPattern = Pattern
			.compile("Absolute upper-left X:\\s*(\\d+)\\s*Absolute upper-left Y:\\s*(\\d+)");

	/**
	 * The pattern to match the window name
	 */
	private static Pattern namePattern = Pattern
			.compile("xwininfo\\: Window id\\: .*? \"(.*?)\"");

	/**
	 * The pattern to match the window id from the xprop -root command
	 */
	private static Pattern windowid = Pattern
			.compile("_NET_ACTIVE_WINDOW\\(WINDOW\\): window id # (.*)");

	@Override
	public ActiveWindow getActiveWindow() throws Exception {
		String id = runProcess("xprop -root");
		Matcher matcher = windowid.matcher(id);
		if (!matcher.find()) {
			throw new Exception("Xprop did not supply the information needed!");
		}
		System.out.println("Window id: " + matcher.group(1));
		id = matcher.group(1);
		String resp = runProcess("xwininfo -id " + id);
		System.out.println(resp);
		matcher = namePattern.matcher(resp);
		if (!matcher.find()) {
			throw new Exception("XWinInfo did not provide a name!");
		}
		String name = matcher.group(1);
		matcher = locationPattern.matcher(resp);
		if (!matcher.find()) {
			throw new Exception("XWinInfo did not provide location info!");
		}
		int x = Integer.parseInt(matcher.group(1));
		int y = Integer.parseInt(matcher.group(2));
		matcher = pattern.matcher(resp);
		if (!matcher.find()) {
			throw new Exception("XWinInfo did not provide size info!");
		}
		int width = Integer.parseInt(matcher.group(1));
		int height = Integer.parseInt(matcher.group(2));
		// Return a new ActiveWindow object
		return new ActiveWindow(name, new Rectangle(x, y, width, height));
	}

	/**
	 * Execute a command and get the response
	 * 
	 * @param cmdline
	 *            The command to be run
	 * @return The output of the comman
	 * @throws IOException
	 *             If an error occurred while reading from the process
	 * @throws InterruptedException
	 *             If the process is interrupted while waiting
	 */
	public static String runProcess(String cmdline) throws IOException,
			InterruptedException {
		Process p = Runtime.getRuntime().exec(cmdline);
		p.waitFor();
		try {
			return StreamUtils.readContents(p.getInputStream());
		} finally {
			p.destroy();
		}
	}
}

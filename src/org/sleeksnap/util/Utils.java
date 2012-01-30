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

import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.PixelGrabber;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.apache.commons.codec.binary.Base64;
import org.sleeksnap.uploaders.Uploader;

public class Utils {
	
	
	public static class FormatUtil {
		public static String formatLowercaseName(String name) {
			name = Character.toUpperCase(name.charAt(0)) + name.substring(1);
			name = name.replace('_', ' ');
			return name;
		}
	}
	/**
	 * A basic class to use clipboard related functions
	 * 
	 * @author Nikki
	 * 
	 */
	public static class ClipboardUtil {
		@SuppressWarnings("serial")
		public static class ClipboardException extends Exception {
			public ClipboardException(Exception e) {
				super(e);
			}
		}

		/**
		 * The clipboard instance
		 */
		private static Clipboard clipboard = Toolkit.getDefaultToolkit()
				.getSystemClipboard();

		@SuppressWarnings("unchecked")
		/**
		 * Get the clipboard contents
		 * @return
		 * 			Either an Image, String or File
		 * @throws ClipboardException
		 * 			If an error occurred
		 */
		public static Object getClipboardContents() throws ClipboardException {
			Transferable contents = clipboard.getContents(null);
			if (contents.isDataFlavorSupported(DataFlavor.imageFlavor)) {
				// An image is on the clipboard!
				try {
					// Get the image from the clipboard
					Image image = (Image) contents
							.getTransferData(DataFlavor.imageFlavor);
					if (image != null) {
						return image;
					}
				} catch (Exception e) {
					throw new ClipboardException(e);
				}
			} else if (contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
				// Text is on the clipboard!
				try {
					// Get the string from the clipboard
					String string = (String) contents
							.getTransferData(DataFlavor.stringFlavor);
					if (string != null) {
						return string;
					}
				} catch (Exception e) {
					throw new ClipboardException(e);
				}
			} else if (contents
					.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
				// Files are on the clipboard
				try {
					// We only will use the first file...
					List<File> list = (List<File>) contents
							.getTransferData(DataFlavor.javaFileListFlavor);
					if (list.size() > 0) {
						return list.get(0);
					}
				} catch (Exception e) {
					throw new ClipboardException(e);
				}
			}
			return null;
		}

		/**
		 * Set the clipboard contents to a string
		 * 
		 * @param string
		 *            The contents
		 */
		public static void setClipboard(String string) {
			clipboard.setContents(new StringSelection(string), null);
		}
	}

	/**
	 * A class containing simple file utilities
	 * 
	 * @author Nikki
	 * 
	 */
	public static class FileUtils {
		
		/**
		 * Get the mime type for a file
		 * @param fileUrl
		 * 			The file url (Can use file.toURI().toURL())
		 * @return
		 * 			The mime type
		 */
		public static String getMimeType(String fileUrl) {
			FileNameMap fileNameMap = URLConnection.getFileNameMap();
			String type = fileNameMap.getContentTypeFor(fileUrl);

			return type;
		}
		
		/**
		 * Read a file
		 * @param file
		 * 			The file
		 * @return
		 * 			The contents
		 * @throws IOException
		 * 			If an error occurred
		 */
		public static String readFile(File file) throws IOException {
			StringBuilder contents = new StringBuilder();
			BufferedReader reader = new BufferedReader(new FileReader(file));
			try {
				while(true) {
					String line = reader.readLine();
					if(line == null) {
						break;
					}
					contents.append(line).append("\n");
				}
			} finally {
				reader.close();
			}
			return contents.toString();
		}
		
		/**
		 * Get the jar path
		 * @param cl
		 * 			The class to get the path from
		 * @return
		 * 			The path
		 * @throws Exception
		 * 			If the path is invalid
		 */
		public static String getJarPath(Class<?> cl) throws Exception {
			return new File(cl.getProtectionDomain().getCodeSource().getLocation().toURI()).getAbsolutePath();
		}
	}

	/**
	 * A basic class to hold Image related functions
	 * 
	 * @author Nikki
	 * 
	 */
	public static class ImageUtil {

		/**
		 * Check whether an image has alpha pixels
		 * 
		 * @param image
		 *            The image
		 * @return True, if the image has alpha pixels
		 */
		public static boolean hasAlpha(Image image) {
			// If buffered image, the color model is readily available
			if (image instanceof BufferedImage) {
				BufferedImage bimage = (BufferedImage) image;
				return bimage.getColorModel().hasAlpha();
			}

			// Use a pixel grabber to retrieve the image's color model;
			// grabbing a single pixel is usually sufficient
			PixelGrabber pg = new PixelGrabber(image, 0, 0, 1, 1, false);
			try {
				pg.grabPixels();
			} catch (InterruptedException e) {
			}

			// Get the image's color model
			ColorModel cm = pg.getColorModel();
			if(cm == null) {
				return false;
			}
			return cm.hasAlpha();
		}

		/**
		 * Convert an image into a Base64 string
		 * 
		 * @param image
		 *            The image
		 * @return The base64 encoded image data
		 * @throws IOException
		 *             If an error occurred
		 */
		public static String toBase64(BufferedImage image) throws IOException {
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			ImageIO.write(image, "PNG", output);
			return Base64.encodeBase64String(output.toByteArray());
		}

		/**
		 * Convert a regular image to a buffered image
		 * 
		 * @param image
		 *            The image
		 * @return The buffered image
		 */
		public static BufferedImage toBufferedImage(Image image) {
			if (image instanceof BufferedImage) {
				return (BufferedImage) image;
			}

			// This code ensures that all the pixels in the image are loaded
			image = new ImageIcon(image).getImage();

			// Determine if the image has transparent pixels; for this method's
			// implementation, see Determining If an Image Has Transparent
			// Pixels
			boolean hasAlpha = hasAlpha(image);

			// Create a buffered image with a format that's compatible with the
			// screen
			BufferedImage bimage = null;
			GraphicsEnvironment ge = GraphicsEnvironment
					.getLocalGraphicsEnvironment();
			try {
				// Determine the type of transparency of the new buffered image
				int transparency = Transparency.OPAQUE;
				if (hasAlpha) {
					transparency = Transparency.BITMASK;
				}

				// Create the buffered image
				GraphicsDevice gs = ge.getDefaultScreenDevice();
				GraphicsConfiguration gc = gs.getDefaultConfiguration();
				bimage = gc.createCompatibleImage(image.getWidth(null),
						image.getHeight(null), transparency);
			} catch (HeadlessException e) {
				// The system does not have a screen
			}

			if (bimage == null) {
				// Create a buffered image using the default color model
				int type = BufferedImage.TYPE_INT_RGB;
				if (hasAlpha) {
					type = BufferedImage.TYPE_INT_ARGB;
				}
				bimage = new BufferedImage(image.getWidth(null),
						image.getHeight(null), type);
			}

			// Copy image to buffered image
			Graphics g = bimage.createGraphics();

			// Paint the image onto the buffered image
			g.drawImage(image, 0, 0, null);
			g.dispose();

			return bimage;
		}

		/**
		 * Get an image as a ByteArrayInputStream
		 * @param image
		 * 			The image
		 * @return
		 * 			The inputstream
		 * @throws IOException
		 * 			If an error occurred while writing or reading
		 */
		public static InputStream toInputStream(BufferedImage image) throws IOException {
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			ImageIO.write(image, "PNG", output);
			return new ByteArrayInputStream(output.toByteArray());
		}
	}
	
	/**
	 * A class used for sorting
	 * 
	 * @author Nikki
	 *
	 */
	public static class SortingUtil {
		public static List<Uploader<?>> sortUploaders(Collection<Uploader<?>> uploaders) {
			List<Uploader<?>> list = new LinkedList<Uploader<?>>(uploaders);
			Collections.sort(list, new Comparator<Uploader<?>>() {
				@Override
				public int compare(Uploader<?> o1, Uploader<?> o2) {
					return o1.getName().compareTo(o2.getName());
				}
			});
			return list;
		}
	}
	
	/**
	 * Contains display methods, will use more later...
	 * @author Nikki
	 *
	 */
	public static class DisplayUtil {

		/**
		 * Get the real screen size, multiple screens..
		 * @return
		 * 		The screen size
		 */
		public static Rectangle getRealScreenSize() {
			GraphicsEnvironment ge = GraphicsEnvironment
					.getLocalGraphicsEnvironment();
			GraphicsDevice[] screens = ge.getScreenDevices();

			Rectangle allScreenBounds = new Rectangle();
			for (GraphicsDevice screen : screens) {
				Rectangle screenBounds = screen.getDefaultConfiguration()
						.getBounds();

				allScreenBounds.width += screenBounds.width;
				allScreenBounds.height = Math.max(allScreenBounds.height,
						screenBounds.height);
			}
			return allScreenBounds;
		}
	}
	
	/**
	 * A utility class used for formatting Dates
	 * 
	 * @author Nikki
	 *
	 */
	public static class DateUtil {
		
		/**
		 * The date format
		 */
		private static SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy_HH-mm-ss");
		
		/**
		 * Gets a filename friendly date
		 * @return
		 * 		The formatted date
		 */
		public static String getCurrentDate() {
			return dateFormat.format(new Date());
		}
	}
}

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
package org.sleeksnap.gui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;

import javax.swing.JWindow;
import javax.swing.event.MouseInputAdapter;

import org.sleeksnap.ScreenSnapper;
import org.sleeksnap.upload.ImageUpload;
import org.sleeksnap.util.ScreenshotUtil;
import org.sleeksnap.util.Utils.ImageUtil;

@SuppressWarnings("serial")
/**
 * A <code>javax.swing.JWindow</code> which allows us to push an image onto it for selection
 * 
 * @author Nikki
 *
 */
public class SelectionWindow extends JWindow {

	/**
	 * A <code>javax.swing.event.MouseInputAdapter</code> which listens for
	 * selections
	 * 
	 * @author Nikki
	 * 
	 */
	private class ScreenSelectionListener extends MouseInputAdapter {
		public void mouseDragged(MouseEvent e) {
			updateSize(e);
		}

		public void mousePressed(MouseEvent e) {
			int x = e.getX();
			int y = e.getY();
			if (e.getButton() == MouseEvent.BUTTON3) {
				e.consume();
				close();
				return;
			}
			currentRect = new Rectangle(x, y, 0, 0);
			updateDrawableRect(getWidth(), getHeight());
			repaint();
		}

		public void mouseReleased(MouseEvent e) {
			// Cancel button
			if (e.getButton() == MouseEvent.BUTTON3) {
				e.consume();
				close();
				return;
			}
			updateSize(e);
			try {
				capture();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		/*
		 * Update the size of the current rectangle and call repaint. Because
		 * currentRect always has the same origin, translate it if the width or
		 * height is negative.
		 * 
		 * For efficiency (though that isn't an issue for this program), specify
		 * the painting region using arguments to the repaint() call.
		 */
		void updateSize(MouseEvent e) {
			currentRect.setSize(e.getX() - currentRect.x, e.getY()
					- currentRect.y);
			updateDrawableRect(getWidth(), getHeight());
			repaint();
		}
	}

	private Graphics g;
	private BufferedImage image;
	private Rectangle area;
	private Rectangle currentRect;
	private Rectangle rectToDraw = null;

	private ScreenSnapper snapper;

	private Image buffer;

	public SelectionWindow(ScreenSnapper snapper, Rectangle area) {
		this.snapper = snapper;
		this.area = area;
		this.image = ScreenshotUtil.capture(area);
		this.setPreferredSize(new Dimension(area.width, area.height));
		this.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
		this.setBounds(area);

		ScreenSelectionListener listener = new ScreenSelectionListener();
		addMouseListener(listener);
		addMouseMotionListener(listener);
	}

	public void capture() {
		Image sub = createImage(new FilteredImageSource(image.getSource(),
				new CropImageFilter(rectToDraw.x, rectToDraw.y,
						rectToDraw.width, rectToDraw.height)));
		if (sub == null) {
			throw new RuntimeException("Unable to crop!");
		}
		snapper.upload(new ImageUpload(ImageUtil.toBufferedImage(sub)));
		sub.flush();
		sub = null;
		close();
	}

	public void close() {
		dispose();
		snapper.clearWindow();
	}
	
	private static Color rectColor = new Color(0, 0, 0, 50);

	public void paint() {
		g.clearRect(area.x, area.y, area.width, area.height);
		g.drawImage(image, 0, 0, null);

		if (currentRect != null) {
			g.setColor(rectColor);
			g.drawRect(rectToDraw.x, rectToDraw.y, rectToDraw.width,
					rectToDraw.height);
			g.fillRect(rectToDraw.x, rectToDraw.y, rectToDraw.width,
					rectToDraw.height);
		}
	}

	@Override
	public void paint(Graphics gr) {
		if (buffer == null) {
			this.buffer = createImage(area.width, area.height);
			this.g = buffer.getGraphics();
		}
		// paint to the buffer
		paint();
		// draw the buffer
		gr.drawImage(buffer, 0, 0, this);
	}

	private void updateDrawableRect(int compWidth, int compHeight) {
		int x = currentRect.x;
		int y = currentRect.y;
		int width = currentRect.width;
		int height = currentRect.height;

		// Make the width and height positive, if necessary.
		if (width < 0) {
			width = 0 - width;
			x = x - width + 1;
			if (x < 0) {
				width += x;
				x = 0;
			}
		}
		if (height < 0) {
			height = 0 - height;
			y = y - height + 1;
			if (y < 0) {
				height += y;
				y = 0;
			}
		}

		// The rectangle shouldn't extend past the drawing area.
		if ((x + width) > compWidth) {
			width = compWidth - x;
		}
		if ((y + height) > compHeight) {
			height = compHeight - y;
		}

		// Update rectToDraw after saving old value.
		if (rectToDraw != null) {
			rectToDraw.setBounds(x, y, width, height);
		} else {
			rectToDraw = new Rectangle(x, y, width, height);
		}
	}
}

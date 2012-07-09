package tray.balloon;

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

import javax.swing.SwingUtilities;

@SuppressWarnings("serial")
public class IconCanvas extends Canvas {
	volatile Image image;
	IconObserver observer;
	int width, height;
	int curW, curH;

	IconCanvas(int width, int height) {
		this.width = curW = width;
		this.height = curH = height;
	}

	// Invoke on EDT.
	public void updateImage(Image image) {
		this.image = image;
		if (observer == null) {
			observer = new IconObserver();
		}
		repaintImage(true);
	}

	// Invoke on EDT.
	protected void repaintImage(boolean doClear) {
		Graphics g = getGraphics();
		if (g != null) {
			try {
				if (isVisible()) {
					if (doClear) {
						update(g);
					} else {
						paint(g);
					}
				}
			} finally {
				g.dispose();
			}
		}
	}

	// Invoke on EDT.
	public void paint(Graphics g) {
		if (g != null && curW > 0 && curH > 0) {
			BufferedImage bufImage = new BufferedImage(curW, curH,
					BufferedImage.TYPE_INT_ARGB);
			Graphics2D gr = bufImage.createGraphics();
			if (gr != null) {
				try {
					gr.setColor(getBackground());
					gr.fillRect(0, 0, curW, curH);
					gr.drawImage(image, 0, 0, curW, curH, observer);
					gr.dispose();

					g.drawImage(bufImage, 0, 0, curW, curH, null);
				} finally {
					gr.dispose();
				}
			}
		}
	}

	class IconObserver implements ImageObserver {
		public boolean imageUpdate(final Image image, final int flags, int x,
				int y, int width, int height) {
			if (image != IconCanvas.this.image || // if the image has been
													// changed
					!IconCanvas.this.isVisible()) {
				return false;
			}
			if ((flags & (ImageObserver.FRAMEBITS | ImageObserver.ALLBITS
					| ImageObserver.WIDTH | ImageObserver.HEIGHT)) != 0) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						repaintImage(false);
					}
				});
			}
			return (flags & ImageObserver.ALLBITS) == 0;
		}
	}
}
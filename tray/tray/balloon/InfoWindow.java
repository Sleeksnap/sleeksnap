package tray.balloon;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;

import javax.swing.SwingUtilities;

@SuppressWarnings("serial")
public class InfoWindow extends Window {
	Container container;
	Closer closer;

	InfoWindow(Frame parent, Color borderColor) {
		super(parent);
		container = new Container() {
			public Insets getInsets() {
				return new Insets(1, 1, 1, 1);
			}
		};
		setLayout(new BorderLayout());
		setBackground(borderColor);
		add(container, BorderLayout.CENTER);
		container.setLayout(new BorderLayout());

		closer = new Closer();
	}

	public Component add(Component c) {
		container.add(c, BorderLayout.CENTER);
		return c;
	}

	void setCloser(Runnable action, int time) {
		closer.set(action, time);
	}

	// Must be executed on EDT.
	@SuppressWarnings("deprecation")
	protected void show(Point corner, int indent) {
		pack();

		Dimension size = getSize();
		// TODO: When 6356322 is fixed we should get screen bounds in
		// this way: eframe.getGraphicsConfiguration().getBounds().
		Dimension scrSize = Toolkit.getDefaultToolkit().getScreenSize();

		if (corner.x < scrSize.width / 2 && corner.y < scrSize.height / 2) { // 1st
																				// square
			setLocation(corner.x + indent, corner.y + indent);

		} else if (corner.x >= scrSize.width / 2
				&& corner.y < scrSize.height / 2) { // 2nd square
			setLocation(corner.x - indent - size.width, corner.y + indent);

		} else if (corner.x < scrSize.width / 2
				&& corner.y >= scrSize.height / 2) { // 3rd square
			setLocation(corner.x + indent, corner.y - indent - size.height);

		} else if (corner.x >= scrSize.width / 2
				&& corner.y >= scrSize.height / 2) { // 4th square
			setLocation(corner.x - indent - size.width, corner.y - indent
					- size.height);
		}

		InfoWindow.super.show();
		InfoWindow.this.closer.schedule();
	}

	public void hide() {
		closer.close();
	}

	class Closer implements Runnable {
		Runnable action;
		int time;

		public void run() {
			doClose();
		}

		void set(Runnable action, int time) {
			this.action = action;
			this.time = time;
		}

		void schedule() {
		}

		void close() {
			doClose();
		}

		// WARNING: this method may be executed on Toolkit thread.
		private void doClose() {
			SwingUtilities.invokeLater(new Runnable() {
				@SuppressWarnings("deprecation")
				public void run() {
					InfoWindow.super.hide();
					invalidate();
					if (action != null) {
						action.run();
					}
				}
			});
		}
	}
}
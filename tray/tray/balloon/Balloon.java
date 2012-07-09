package tray.balloon;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Label;
import java.awt.Panel;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.BreakIterator;

import javax.swing.SwingUtilities;

@SuppressWarnings("serial")
public class Balloon extends InfoWindow {
	final static int BALLOON_SHOW_TIME = 10000;
	final static int BALLOON_TEXT_MAX_LENGTH = 256;
	final static int BALLOON_WORD_LINE_MAX_LENGTH = 16;
	final static int BALLOON_WORD_LINE_MAX_COUNT = 4;
	final static int BALLOON_ICON_WIDTH = 32;
	final static int BALLOON_ICON_HEIGHT = 32;
	final static int BALLOON_TRAY_ICON_INDENT = 0;
	final static Color BALLOON_CAPTION_BACKGROUND_COLOR = new Color(200, 200,
			255);
	final static Font BALLOON_CAPTION_FONT = new Font(Font.DIALOG, Font.BOLD,
			12);

	Panel mainPanel = new Panel();
	Panel captionPanel = new Panel();
	Label captionLabel = new Label("");
	Button closeButton = new Button("X");
	Panel textPanel = new Panel();
	IconCanvas iconCanvas = new IconCanvas(BALLOON_ICON_WIDTH,
			BALLOON_ICON_HEIGHT);
	Label[] lineLabels = new Label[BALLOON_WORD_LINE_MAX_COUNT];
	ActionPerformer ap = new ActionPerformer();

	Image iconImage;
	Image errorImage;
	Image warnImage;
	Image infoImage;
	boolean gtkImagesLoaded;

	public Balloon() {
		super(null, new Color(90, 80, 190));

		setCloser(new Runnable() {
			public void run() {
				if (textPanel != null) {
					textPanel.removeAll();
					textPanel.setSize(0, 0);
					iconCanvas.setSize(0, 0);
				}
			}
		}, BALLOON_SHOW_TIME);

		add(mainPanel);

		captionLabel.setFont(BALLOON_CAPTION_FONT);
		captionLabel.addMouseListener(ap);

		captionPanel.setLayout(new BorderLayout());
		captionPanel.add(captionLabel, BorderLayout.WEST);
		captionPanel.add(closeButton, BorderLayout.EAST);
		captionPanel.setBackground(BALLOON_CAPTION_BACKGROUND_COLOR);
		captionPanel.addMouseListener(ap);

		closeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				hide();
			}
		});

		mainPanel.setLayout(new BorderLayout());
		mainPanel.setBackground(Color.white);
		mainPanel.add(captionPanel, BorderLayout.NORTH);
		mainPanel.add(textPanel, BorderLayout.CENTER);
		mainPanel.add(iconCanvas, BorderLayout.WEST);

		for (int i = 0; i < BALLOON_WORD_LINE_MAX_COUNT; i++) {
			lineLabels[i] = new Label();
			lineLabels[i].addMouseListener(ap);
			lineLabels[i].setBackground(Color.white);
		}

	}

	public void display(String caption, String text, MessageType messageType) {
		if (!gtkImagesLoaded) {
			loadGtkImages();
		}
		captionLabel.setText(caption);

		renderText(text);
		setMessageIconBasedOnMessageType(messageType);
		renderBalloon();
	}

	private void setMessageIconBasedOnMessageType(MessageType messageType) {
		switch (messageType) {
		case ERROR:
			iconImage = errorImage;
			break;
		case INFO:
			iconImage = infoImage;
			break;
		case WARNING:
			iconImage = warnImage;
			break;
		default:
			iconImage = null;
		}

		if (iconImage != null) {
			Dimension tpSize = textPanel.getSize();
			iconCanvas.setSize(BALLOON_ICON_WIDTH,
					(BALLOON_ICON_HEIGHT > tpSize.height ? BALLOON_ICON_HEIGHT
							: tpSize.height));
		}
	}

	private void renderBalloon() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				Rectangle bounds = getBounds();
				Point parLoc = new Point(bounds.x, bounds.y);
				Dimension parSize = new Dimension(bounds.height, bounds.width);
				show(new Point(parLoc.x + parSize.width / 2, parLoc.y
						+ parSize.height / 2), BALLOON_TRAY_ICON_INDENT);
				if (iconImage != null) {
					iconCanvas.updateImage(iconImage);
				}
			}
		});
	}

	private void renderText(String text) {
		BreakIterator iter = BreakIterator.getWordInstance();
		if (text != null) {
			iter.setText(text);
			int start = iter.first(), end;
			int nLines = 0;

			do {
				end = iter.next();

				if (end == BreakIterator.DONE
						|| text.substring(start, end).length() >= 50) {
					lineLabels[nLines].setText(text.substring(start,
							end == BreakIterator.DONE ? iter.last() : end));
					textPanel.add(lineLabels[nLines++]);
					start = end;
				}
				if (nLines == BALLOON_WORD_LINE_MAX_COUNT) {
					if (end != BreakIterator.DONE) {
						lineLabels[nLines - 1].setText(new String(
								lineLabels[nLines - 1].getText() + " ..."));
					}
					break;
				}
			} while (end != BreakIterator.DONE);

			textPanel.setLayout(new GridLayout(nLines, 1));
		}
	}

	public void dispose() {
		super.dispose();
	}

	void loadGtkImages() {
		if (!gtkImagesLoaded) {
			errorImage = (Image) Toolkit.getDefaultToolkit()
					.getDesktopProperty("gtk.icon.gtk-dialog-error.6.rtl");
			warnImage = (Image) Toolkit.getDefaultToolkit().getDesktopProperty(
					"gtk.icon.gtk-dialog-warning.6.rtl");
			infoImage = (Image) Toolkit.getDefaultToolkit().getDesktopProperty(
					"gtk.icon.gtk-dialog-info.6.rtl");
			gtkImagesLoaded = true;
		}
	}

	class ActionPerformer extends MouseAdapter {
		public void mouseClicked(MouseEvent e) {
			// hide the balloon by any click
			hide();
		}
	}

	class Message {
		String caption, text;
		MessageType messageType;

		Message(String caption, String text, MessageType messageType2) {
			this.caption = caption;
			this.text = text;
			this.messageType = messageType2;
		}
	}
}
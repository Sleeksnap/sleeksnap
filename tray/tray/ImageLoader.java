package tray;

import java.awt.Image;
import java.awt.Toolkit;
import java.net.URL;

import javax.swing.ImageIcon;

public class ImageLoader {
	public static Image load(URL imageURL) {
		return retrieveImageAndEnsureItIsFullyLoaded(imageURL);
	}

	private static Image retrieveImageAndEnsureItIsFullyLoaded(URL imageURL) {
		Image image = Toolkit.getDefaultToolkit().getImage(imageURL);
		Image fullyLoadedImage = new ImageIcon(image).getImage();
		return fullyLoadedImage;
	}

	public static int getSizeForCurrentOS() {
		String osName = System.getProperty("os.name").toLowerCase();
		if (osName.contains("windows")) {
			return 16;
		}
		return 24;
	}
}

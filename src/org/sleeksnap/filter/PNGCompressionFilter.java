package org.sleeksnap.filter;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.imageio.ImageIO;

public class PNGCompressionFilter implements UploadFilter<BufferedImage> {

	private static File pngCrush = new File("tools/pngcrush.exe");
	
	private static File pngOut = new File("tools/pngout.exe");
	
	@Override
	public BufferedImage filter(BufferedImage object) {
		if(pngCrush.exists()) {
			System.out.println("Crushing image...");
			try {
				File file = new File("tools/sleeksnapcompress.png");
				ImageIO.write(object, "JPG", new FileOutputStream(file));

				/*File outFile = new File("tools/sleeksnapcompress_out.png");
				String[] opts = new String[3];
				opts[0] = strPad(pngCrush.getAbsolutePath(), '"');
				opts[1] = strPad(file.getAbsolutePath(), '"');
				opts[2] = strPad(outFile.getAbsolutePath(), '"');
				
				Process p = Runtime.getRuntime().exec(opts);
				BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
				String line;
				while((line = reader.readLine()) != null) {
					System.out.println(line);
				}
				
				System.out.println("Original size: "+file.length()+", Crushed size: "+outFile.length());
				*/
				try {
					//Finally, read the new file.
					return ImageIO.read(file);
				} finally {
					file.delete();
					//outFile.delete();
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return object;
	}
	
	@Override
	public Class<?> getType() {
		return BufferedImage.class;
	}

	private static String strPad(String string, char pad) {
		return pad + string + pad;
	}
}

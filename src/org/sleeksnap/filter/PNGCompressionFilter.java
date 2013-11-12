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
package org.sleeksnap.filter;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import org.sleeksnap.ScreenSnapper;
import org.sleeksnap.upload.ImageUpload;
import org.sleeksnap.util.Util;

import com.sun.jna.Platform;

/**
 * An Experimental upload filter for compressing images through pngcrush or pngout
 * Warning: May make uploads slower than they already are! Crushing big images takes time.
 * 
 * @author Nikki
 *
 */
public class PNGCompressionFilter implements UploadFilter<ImageUpload> {
	
	/**
	 * Logger object
	 */
	private static final Logger logger = Logger.getLogger(PNGCompressionFilter.class.getName());

	/**
	 * Location of the pngcrush program
	 * pngcrush is faster than pngout.
	 * Download: http://pmt.sourceforge.net/pngcrush/
	 */
	private static File pngCrushWindows = new File(Util.getWorkingDirectory(), "tools/pngcrush.exe"), pngCrushUnix = new File(Util.getWorkingDirectory(), "tools/pngcrush");
	
	/**
	 * Location of the pngout program
	 * pngout is slower than pngcrush, but has higher compression ratios.
	 * Download: http://advsys.net/ken/util/pngout.exe
	 */
	private static File pngOutWindows = new File(Util.getWorkingDirectory(), "tools/pngout.exe"), pngOutUnix = new File(Util.getWorkingDirectory(), "tools/pngcrush");
	
	private ScreenSnapper parent;
	
	public PNGCompressionFilter(ScreenSnapper parent) {
		this.parent = parent;
	}
	
	@Override
	public ImageUpload filter(ImageUpload object) throws FilterException {
		if(parent.getConfiguration().getBoolean("compressImages")) {
			File pngOut = null;
			File pngCrush = null;
			if(Platform.isWindows()) {
				pngCrush = pngCrushWindows;
				pngOut = pngOutWindows;
			} else {
				pngCrush = pngCrushUnix;
				pngOut = pngOutUnix;
			}
			if(pngOut.exists() || pngCrush.exists()) {
				try {
					//TODO verify it is a png image.
					File input = new File(Util.getWorkingDirectory(), "sleeksnap_original.png");
					File output = new File(Util.getWorkingDirectory(), "sleeksnap_compressed.png");
					
					ImageIO.write(object.getImage(), "png", input);
					
					String[] opts = new String[3];
					opts[0] = strPad((pngOut.exists() ? pngOut : pngCrush).getAbsolutePath(), '"');
					opts[1] = strPad(input.getAbsolutePath(), '"');
					opts[2] = strPad(output.getAbsolutePath(), '"');
					
					logger.info("Compressing image with "+(pngOut.exists() ? "pngout" : "pngcrush") +"...");
					
					Process p = Runtime.getRuntime().exec(opts);
					BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
					
					while(reader.readLine() != null) {
						//Nothing.
					}
					
					logger.info("Compressed image, original size: "+input.length()+", compressed size: "+output.length());
					
					object.setImage(ImageIO.read(output));
					
					try {
						//Finally, read the new file.
						return object;
					} finally {
						input.delete();
						output.delete();
					}
				} catch (IOException e) {
					throw new FilterException(e, "Unable to compress image");
				}
			}
		}
		return object;
	}

	private static String strPad(String string, char pad) {
		return pad + string + pad;
	}
}

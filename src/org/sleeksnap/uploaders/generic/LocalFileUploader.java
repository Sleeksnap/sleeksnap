package org.sleeksnap.uploaders.generic;

import java.io.File;
import java.io.FileWriter;

import javax.imageio.ImageIO;

import org.sleeksnap.upload.FileUpload;
import org.sleeksnap.upload.ImageUpload;
import org.sleeksnap.upload.TextUpload;
import org.sleeksnap.upload.Upload;
import org.sleeksnap.uploaders.Uploader;
import org.sleeksnap.util.Util;
import org.sleeksnap.util.Utils.FileUtils;

/**
 * An uploader to save images and text to local files (Working Directory/local/<images/text>)
 * 
 * @author Nikki
 *
 */
public class LocalFileUploader extends GenericUploader {
	
	/**
	 * Local base directory
	 */
	private static final File LOCAL_BASE_DIR = new File(Util.getWorkingDirectory(), "local");
	
	/**
	 * Local image directory
	 */
	private static final File LOCAL_IMAGE_DIR = new File(LOCAL_BASE_DIR, "images");
	
	/**
	 * Local text directory
	 */
	private static final File LOCAL_TEXT_DIR = new File(LOCAL_BASE_DIR, "text");
	
	/**
	 * List of sub uploaders
	 */
	private final Uploader<?>[] uploaders = new Uploader<?>[] { new ImageLocalFileUploader(), new TextLocalFileUploader() };

	/**
	 * An ImageUploader using ImageIO.write(image, format, file) to save an image
	 * 
	 * @author Nikki
	 *
	 */
	public class ImageLocalFileUploader extends Uploader<ImageUpload> {

		@Override
		public String getName() {
			return LocalFileUploader.this.getName();
		}

		@Override
		public String upload(ImageUpload upload) throws Exception {
			checkDirectory(upload);
			File file = new File(LOCAL_IMAGE_DIR, FileUtils.generateFileName(upload));
			ImageIO.write(upload.getImage(), "png", file);
			return file.toURI().toURL().toString();
		}
	}

	/**
	 * A text file uploader using FileWriter to save text files to disk
	 * 
	 * @author Nikki
	 */
	public class TextLocalFileUploader extends Uploader<TextUpload> {

		@Override
		public String getName() {
			return LocalFileUploader.this.getName();
		}

		@Override
		public String upload(TextUpload upload) throws Exception {
			checkDirectory(upload);
			File file = new File(LOCAL_TEXT_DIR, FileUtils.generateFileName(upload));
			FileWriter writer = new FileWriter(file);
			try {
				writer.write(upload.getText());
			} finally {
				writer.close();
			}
			return file.toURI().toURL().toString();
		}
	}
	
	/**
	 * Check and create any directories needed
	 * @param upload
	 * 			The upload to check for the type
	 */
	protected void checkDirectory(Upload upload) {
		if(!LOCAL_BASE_DIR.exists()) {
			LOCAL_BASE_DIR.mkdirs();
		}
		if(upload instanceof ImageUpload && !LOCAL_IMAGE_DIR.exists()) {
			LOCAL_IMAGE_DIR.mkdirs();
		} else if (upload instanceof TextUpload && !LOCAL_TEXT_DIR.exists()) {
			LOCAL_TEXT_DIR.mkdirs();
		}
	}

	@Override
	public Uploader<?>[] getSubUploaders() {
		return uploaders;
	}

	@Override
	public String getName() {
		return "Local File";
	}

}

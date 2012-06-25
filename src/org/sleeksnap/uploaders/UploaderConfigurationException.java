package org.sleeksnap.uploaders;

/**
 * An Exception which should be thrown if an uploader is not configured correctly
 * 
 * @author Nikki
 *
 */
public class UploaderConfigurationException extends UploadException {

	private static final long serialVersionUID = 2574424530734464288L;

	public UploaderConfigurationException(String string) {
		super(string);
	}
}

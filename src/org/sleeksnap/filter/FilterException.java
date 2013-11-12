package org.sleeksnap.filter;

/**
 * An Exception which can be thrown when a Filter experiences an error that should stop the upload
 * 
 * @author Nikki
 *
 */
@SuppressWarnings("serial")
public class FilterException extends Exception {

	/**
	 * The separate error message, this is NOT the same as e.getMessage()
	 */
	private String errorMessage;

	public FilterException(Exception e) {
		super(e);
	}
	
	public FilterException(Exception e, String errorMessage) {
		super(e);
		this.errorMessage = errorMessage;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}

}

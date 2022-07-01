package com.cognizant.mailorderpharmacy.exception;

/**Service class*/
@SuppressWarnings("serial")
public class UnauthorizedException extends RuntimeException {
	/**
	 * @param message
	 */
	public UnauthorizedException(String message) {
		super(message);
	}

}

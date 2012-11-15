package org.sevensource.commons.email.html;

public class EmailException extends RuntimeException {

	private static final long serialVersionUID = 5899688973194276929L;

	public EmailException() {
		super();
	}
	
	public EmailException(String message) {
		super(message);
	}
	
	public EmailException(Throwable cause) {
		super(cause);
	}
	
	public EmailException(String message, Throwable cause) {
		super(message, cause);
	}
	
}

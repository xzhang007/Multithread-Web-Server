package server.exception;

//@SuppressWarnings("serial")
public class BadRequest extends Exception {
	
	private static final long serialVersionUID = 1L;
	
	public BadRequest() {
	}
		
	public BadRequest(String message) {
		super(message);
	}
}
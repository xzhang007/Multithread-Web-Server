package server.exception;

//@SuppressWarnings("serial")
public class ServerConfigNotFoundException extends Exception {
	
	private static final long serialVersionUID = 1L;
	
	public ServerConfigNotFoundException() {
		super("Server Config Not Found");
	}
		
}